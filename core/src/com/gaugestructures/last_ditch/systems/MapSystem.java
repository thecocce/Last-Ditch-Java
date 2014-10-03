package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;
import com.google.common.collect.Sets;
import org.yaml.snakeyaml.Yaml;
import sun.swing.UIAction;

import java.util.*;

public class MapSystem extends GameSystem {
    private Manager mgr;
    private Yaml yaml = new Yaml();
    private PositionComp focus;
    private int prevChunk = -1;
    private Set<Integer> curChunks = new HashSet<Integer>();
    private Set<Integer> prevChunks = new HashSet<Integer>();
    private Room master;
    private Random rnd = new Random();
    private int startX, startY, endX, endY;
    private int iterations = 120;
    private int numOfRooms = 200;
    private int numOfItems = 600;
    private InventorySystem inventory;
    private UIActionsSystem uiActions;
    private UIInventorySystem uiInventory;
    private int numOfChunks = C.MAP_WIDTH * C.MAP_HEIGHT;
    private int width = C.MAP_WIDTH * C.CHUNK_SIZE, height = C.MAP_HEIGHT * C.CHUNK_SIZE;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private List<List<String>> items = new ArrayList<List<String>>(numOfChunks);
    private List<List<String>> doors = new ArrayList<List<String>>(numOfChunks);
    private List<List<String>> stations = new ArrayList<List<String>>(numOfChunks);
    private boolean[][][] solid = new boolean[numOfChunks][C.CHUNK_SIZE][C.CHUNK_SIZE];
    private boolean[][][] sight = new boolean[numOfChunks][C.CHUNK_SIZE][C.CHUNK_SIZE];
    private float[][][] rot = new float[numOfChunks][C.CHUNK_SIZE][C.CHUNK_SIZE];
    private TextureRegion[][][] tiles = new TextureRegion[numOfChunks][C.CHUNK_SIZE][C.CHUNK_SIZE];
    private PhysicsSystem physics;
    private OrthographicCamera cam = new OrthographicCamera();

    public MapSystem(Manager mgr, InventorySystem inventory, UIActionsSystem uiActions, UIInventorySystem uiInventory) {
        this.mgr = mgr;
        this.inventory = inventory;
        this.uiActions = uiActions;
        this.uiInventory = uiInventory;

        for(int i = 0; i < numOfChunks; i++) {
            items.add(i, new ArrayList<String>());
            doors.add(i, new ArrayList<String>());

            stations.add(i, new ArrayList<String>());

            for (TextureRegion[] row : tiles[i]) {
                Arrays.fill(row, mgr.getAtlas().findRegion("environ/floor1"));
            }
        }
        cam.setToOrtho(false, C.WIDTH, C.HEIGHT);
        focus = mgr.comp(mgr.getPlayer(), PositionComp.class);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    setSolid(x, y, true);
                    setSight(x, y, true);
                    setTile(x, y, "environ/empty");
                }
            }
        }

        generateRooms();
        generateItems();
        generateDoors();
        generateStations();

        startX = (int)Math.max(focus.getX() - 13, 0);
        startY = (int)Math.max(focus.getY() - 10, 0);
        endX = (int)Math.min(focus.getX() + 13, width - 1);
        endY = (int)Math.min(focus.getY() + 10, height - 1);

        cam.position.set(focus.getX() * C.BTW, focus.getY() * C.BTW, 0);
        cam.update();
    }

    public boolean useDoorAt(String entity, int screenX, int screenY) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);

        float x = posComp.getX() + C.WTB * (screenX - C.WIDTH / 2);
        float y = posComp.getY() - C.WTB * (screenY - C.HEIGHT / 2);

        String door = getDoor(x, y);

        if(door == null)
            return false;

        DoorComp doorComp = mgr.comp(door, DoorComp.class);

        if(doorComp.isLocked())
            return false;

        doorComp.setOpen(!doorComp.isOpen());
        updateDoor(door, doorComp.isOpen());

        return true;
    }

    public boolean useDoor(String entity) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);

        String door = getNearDoor(posComp.getX(), posComp.getY());

        if (door == null)
            return false;

        DoorComp doorComp = mgr.comp(door, DoorComp.class);

        if (doorComp.isLocked())
            return false;

        doorComp.setOpen(!doorComp.isOpen());
        updateDoor(door, doorComp.isOpen());

        return true;
    }

    private void updateDoor(String door, boolean open) {
        CollisionComp colComp = mgr.comp(door, CollisionComp.class);

        if (open) {
            mgr.removeComp(door, RenderComp.class);
            colComp.getBody().setActive(false);
        } else {
            TypeComp typeComp = mgr.comp(door, TypeComp.class);

            RenderComp renderComp = new RenderComp(
                typeComp.getType(),
                mgr.getAtlas().findRegion(
                    String.format("environ/%s", typeComp.getType())));

            mgr.addComp(door, renderComp);

            colComp.getBody().setActive(true);
        }
    }

    private String getDoor(float x, float y) {
        for (int cx = (int)focus.getX() - 1; cx <= focus.getX() + 1; cx++) {
            for (int cy = (int)focus.getY() - 1; cy <= focus.getY() + 1; cy++) {
                for (String door: doors.get(getChunk(cx, cy))) {
                    PositionComp posComp = mgr.comp(door, PositionComp.class);
                    SizeComp sizeComp = mgr.comp(door, SizeComp.class);
                    RotationComp rotComp = mgr.comp(door, RotationComp.class);

                    double c = Math.cos(-rotComp.getAng() * Math.PI / 180);
                    double s = Math.sin(-rotComp.getAng() * Math.PI / 180);
                    double rotX = posComp.getX() + c * (x - posComp.getX()) - s * (y - posComp.getY());
                    double rotY = posComp.getY() + s * (x - posComp.getX()) + c * (y - posComp.getY());

                    double left = posComp.getX() - sizeComp.getW();
                    double right = posComp.getX() + sizeComp.getW();
                    double top = posComp.getY() - sizeComp.getH();
                    double bottom = posComp.getY() + sizeComp.getH();

                    if(left <= rotX && rotX <= right && top <= rotY && rotY <= bottom) {
                        return door;
                    }
                }
            }
        }
        return null;
    }

    private String getNearDoor(float x, float y) {
        for (int cx = (int)focus.getX() - 1; cx <= focus.getX() + 1; cx++) {
            for (int cy = (int)focus.getY() - 1; cy <= focus.getY() + 1; cy++) {
                for (String door: doors.get(getChunk(cx, cy))) {
                    PositionComp posComp = mgr.comp(door, PositionComp.class);
                    double distSqr = Math.pow((posComp.getX() - x), 2) + Math.pow((posComp.getY() - y), 2);

                    if (distSqr < 2.6)
                        return door;
                }
            }
        }
        return null;
    }

    public boolean useStation(String entity) {
        return false;
    }

    public boolean dropItem(String entity) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        RotationComp rotComp = mgr.comp(entity, RotationComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        if (uiInventory.getSelection() != null) {
            int index = uiInventory.getSlotIndex(uiInventory.getSelection());

            String item = invComp.getItem(index);

            if (item != null) {
                TypeComp itemTypeComp = mgr.comp(item, TypeComp.class);

                PositionComp itemPosComp = new PositionComp(
                    posComp.getX() + rotComp.getX(),
                    posComp.getY() + rotComp.getY());

                RenderComp itemRenderComp = new RenderComp(
                    String.format("items/%s", itemTypeComp.getType()),
                    mgr.getAtlas().findRegion(String.format("items/%s", itemTypeComp.getType())));

                mgr.addComp(item, itemPosComp);
                mgr.addComp(item, itemRenderComp);

                items.get(getChunk(itemPosComp.getX(), itemPosComp.getY())).add(item);
                inventory.setUpdateSlots(true);

                ItemComp itemComp = mgr.comp(item, ItemComp.class);

                invComp.setWeight(invComp.getWeight() - itemComp.getWeight());
                inventory.removeItem(invComp, item);

                uiInventory.resetInfo();
                uiActions.updateCraftingInfo();

                return true;
            }
        }
        return false;
    }

    public void removeItem(String item) {
        PositionComp posComp = mgr.removeComp(item, PositionComp.class);
        mgr.removeComp(item, RenderComp.class);

        items.get(getChunk(posComp.getX(), posComp.getY())).remove(item);
        inventory.setUpdateSlots(true);
    }

    public void generateRooms() {
        int x = 0, y = 0;
        master = new Room(10, 10, width - 10, height - 10);

        for (int i = 0; i < numOfRooms; i++) {
            x = mgr.randInt(master.getX1(), master.getX2() - 1);
            y = mgr.randInt(master.getY1(), master.getY2() - 1);

            rooms.add(new Room(x, y, 1, 1));
        }

        for (int i = 0; i < iterations; i++) {
            for (Room room : rooms) {
                expand(room);
            }
        }

        ArrayList<Room> degenerateRooms = new ArrayList<Room>();

        for (Room room : rooms) {
            if (room.getW() < 5 || room.getH() < 5) {
                degenerateRooms.add(room);
            } else {
                for(x = room.getX1(); x < room.getX2(); x++) {
                    for(y = room.getY1(); y < room.getY2(); y++) {
                        if(x == room.getX1() || x == room.getX2() - 1) {
                            setSolid(x, y, true);
                            setSight(x, y, false);
                            setRot(x, y, 0);
                            setTile(x, y, "environ/wall1");
                        } else if(y == room.getY1() || y == room.getY2() - 1) {
                            setSolid(x, y, true);
                            setSight(x, y, false);
                            setRot(x, y, 0);
                            setTile(x, y, "environ/wall1");
                        } else {
                            setSolid(x, y, false);
                            setSight(x, y, true);
                            setRot(x, y, 0);
                            setTile(x, y, "environ/floor2");
                        }
                    }
                }
            }
        }
        rooms.removeAll(degenerateRooms);
    }

    public void generateItems() {
        Map<String, Object> itemData = mgr.getData("items");

        @SuppressWarnings("unchecked")
        List<String> itemList = (List<String>) itemData.get("itemList");

        float x = 0, y = 0;
        for(int i = 0; i < numOfItems; i++) {
            do {
                x = mgr.randFloat(10f, width - 10f);
                y = mgr.randFloat(10f, height - 10f);
            } while(isSolid((int) x, (int) y));

            String choice = itemList.get(rnd.nextInt(itemList.size()));
            String item = inventory.createItem(choice, x, y);

            items.get(getChunk(x, y)).add(item);
        }
    }

    public void generateDoors() {
        for(Room room : rooms) {
            int x = mgr.randInt(room.getX1() + 1, room.getX2() - 3);
            int y = 0;
            float doorRot;

            if (mgr.randInt(0, 1) == 0) {
                y = room.getY1();
                doorRot = 0;
            } else {
                y = room.getY2() - 1;
                doorRot = 180;
            }

            setSolid(x, y, false);
            setSight(x, y, true);
            setRot(x, y, 0);
            setTile(x, y, "environ/floor2");

            setSolid(x+1, y, false);
            setSight(x + 1, y, true);
            setRot(x+1, y, 0);
            setTile(x + 1, y, "environ/floor2");

            String door = mgr.createEntity();

            RenderComp renderComp = new RenderComp(
                "environ/door1",
                mgr.getAtlas().findRegion("environ/door1"));

            float w = renderComp.getW() * C.WTB;
            float h = renderComp.getH() * C.WTB;

            mgr.addComp(door, renderComp);
            mgr.addComp(door, new PositionComp(x + w/2, y + h/2));
            mgr.addComp(door, new SizeComp(w, h));
            mgr.addComp(door, new RotationComp(doorRot));
            mgr.addComp(door, new CollisionComp());
            mgr.addComp(door, new TypeComp("door1"));
            mgr.addComp(door, new DoorComp());

            doors.get(getChunk(x, y)).add(door);

            y = mgr.randInt(room.getY1() + 1, room.getY2() - 3);

            if (mgr.randInt(0, 1) == 0) {
                x = room.getX1();
                doorRot = 90;
            } else {
                x = room.getX2() - 1;
                doorRot = -90;
            }

            setSolid(x, y, false);
            setSight(x, y, true);
            setRot(x, y, 0);
            setTile(x, y, "environ/floor2");

            setSolid(x, y+1, false);
            setSight(x, y+1, true);
            setRot(x, y+1, 0);
            setTile(x, y+1, "environ/floor2");

            door = mgr.createEntity();

            renderComp = new RenderComp(
                "environ/door1",
                mgr.getAtlas().findRegion("environ/door1")
            );

            w = renderComp.getW() * C.WTB;
            h = renderComp.getH() * C.WTB;

            mgr.addComp(door, renderComp);
            mgr.addComp(door, new PositionComp(x + h/2, y + w/2));
            mgr.addComp(door, new SizeComp(w, h));
            mgr.addComp(door, new RotationComp(doorRot));
            mgr.addComp(door, new CollisionComp());
            mgr.addComp(door, new TypeComp("door1"));
            mgr.addComp(door, new DoorComp());

            doors.get(getChunk(x, y)).add(door);
        }
    }

    public void generateStations() {
        Map<String, Object> stationData = mgr.getData("stations");

        @SuppressWarnings("unchecked")
        List<String> stationList = (List<String>)stationData.get("stationList");

        for (Room room : rooms) {
            if (room.getX1() + 2 < room.getX2() - 4 && room.getY1() + 2 < room.getY2() - 4) {
                String station = mgr.createEntity();
                String stationType = stationList.get(mgr.randInt(0, stationList.size() - 1));

                float rot = mgr.randInt(0, 3) * 90;

                RenderComp renderComp = new RenderComp(
                    String.format("environ/%s", stationType),
                    mgr.getAtlas().findRegion(String.format("environ/%s", stationType)));

                float w = renderComp.getW() * C.WTB;
                float h = renderComp.getH() * C.WTB;
                float x = mgr.randInt(room.getX1() + 2, room.getX2() - 3);
                float y = mgr.randInt(room.getY1() + 2, room.getY2() - 3);

                if (rot == 0 || rot == 180) {
                    mgr.addComp(station, new PositionComp(x + w/2, y + h/2));
                } else {
                    mgr.addComp(station, new PositionComp(x + h/2, y + w/2));
                }

                mgr.addComp(station, renderComp);
                mgr.addComp(station, new SizeComp(w, h));
                mgr.addComp(station, new RotationComp(rot));
                mgr.addComp(station, new CollisionComp());
                mgr.addComp(station, new TypeComp("station"));
                mgr.addComp(station, new StationComp(stationType));

                ResourcesComp resourcesComp = mgr.addComp(station, new ResourcesComp());

                if (stationType.equals("purification_station")) {
                    resourcesComp.setWater(2);
                } else if (stationType.equals("charging_station")) {
                    resourcesComp.setEnergy(2);
                } else if (stationType.equals("incinerator")) {
                    resourcesComp.setEnergy(1);
                }

                stations.get(getChunk(x, y)).add(station);
            }
        }
    }

    public void expand(Room test_room) {
        int direction = rnd.nextInt(4);

        switch(direction) {
            case 0:
                if(test_room.getX1() - 2 > master.getX1()) {
                    test_room.setX1(test_room.getX1() - 2);
                }
                break;
            case 1:
                if(test_room.getY1() - 2 > master.getY1()) {
                    test_room.setY1(test_room.getY1() - 2);
                }
                break;
            case 2:
                if(test_room.getX2() + 3 < master.getX2()) {
                    test_room.setX2(test_room.getX2() + 2);
                }
                break;
            case 3:
                if(test_room.getY2() + 3 < master.getY2()) {
                    test_room.setY2(test_room.getY2() + 2);
                }
                break;
        }

        boolean check = false;

        for(Room room : rooms) {
            if(room == test_room) {
                continue;
            }

            if(intersects(room, test_room)) {
                check = true;
                break;
            }
        }

        int fix = check ? 2 : 1;

        switch(direction) {
            case 0:
                test_room.setX1(test_room.getX1() + fix);
                break;
            case 1:
                test_room.setY1(test_room.getY1() + fix);
                break;
            case 2:
                test_room.setX2(test_room.getX2() - fix);
                break;
            case 3:
                test_room.setY2(test_room.getY2() - fix);
                break;
        }
    }

    public String getItem(float x, float y) {
        for (int cx = (int)focus.getX() - C.CHUNK_SIZE; cx <= focus.getX() + C.CHUNK_SIZE; cx++) {
            for (int cy = (int)focus.getY() - C.CHUNK_SIZE; cy <= focus.getY() + C.CHUNK_SIZE; cy++) {
                int chunk = getChunk(cx, cy);

                if (chunk != -1) {
                    for (String item : items.get(chunk)) {
                        PositionComp posComp = mgr.comp(item, PositionComp.class);
                        double distSqr = Math.pow((posComp.getX() - x), 2) + Math.pow((posComp.getY() - y), 2);

                        if (distSqr < 1.4) {
                            RotationComp rotComp = mgr.comp(item, RotationComp.class);
                            RenderComp renderComp = mgr.comp(item, RenderComp.class);

                            double c = Math.cos(-rotComp.getAng() * Math.PI / 180);
                            double s = Math.sin(-rotComp.getAng() * Math.PI / 180);
                            double rotX = posComp.getX() + c * (x - posComp.getX()) - s * (y - posComp.getY());
                            double rotY = posComp.getY() + s * (x - posComp.getX()) + c * (y - posComp.getY());

                            double left = posComp.getX() - renderComp.getW() * C.WTB / 2;
                            double right = posComp.getX() + renderComp.getW() * C.WTB / 2;
                            double top = posComp.getY() - renderComp.getH() * C.WTB / 2;
                            double bottom = posComp.getY() + renderComp.getH() * C.WTB / 2;

                            if (left <= rotX && rotX <= right && top <= rotY && rotY <= bottom) {
                                return item;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getNearItem(float x, float y) {
        int chunk = getChunk(focus.getX(), focus.getY());

        for (String item : items.get(chunk)) {
            PositionComp posComp = mgr.comp(item, PositionComp.class);

            double distSqr = Math.pow((posComp.getX() - x), 2) + Math.pow((posComp.getY() - y), 2);

            if (distSqr < 1.4) {
                return item;
            }
        }
        return null;
    }

    public void update() {
        startX = (int)Math.max(focus.getX() - 13, 0);
        startY = (int)Math.max(focus.getY() - 10, 0);
        endX = (int)Math.min(focus.getX() + 13, width - 1);
        endY = (int)Math.min(focus.getY() + 10, height - 1);

        cam.position.set(focus.getX() * C.BTW, focus.getY() * C.BTW, 0);
        cam.update();

        int thisChunk = getChunk(focus.getX(), focus.getY());

        if(thisChunk != prevChunk) {
            updateChunks();
        }
        prevChunk = thisChunk;
    }

    public void render(SpriteBatch batch) {
        for(int x = startX; x <= endX; x++) {
            for(int y = startY; y <= endY; y++) {
                batch.draw(
                    getTile(x, y),
                    x * C.BTW, y * C.BTW,
                    C.BTW / 2, C.BTW / 2,
                    C.BTW, C.BTW,
                    1, 1,
                    getRot(x, y));
            }
        }
    }

    private void updateChunks() {
        curChunks.clear();
        for(int x = (int)focus.getX() - C.CHUNK_SIZE; x <= focus.getX() + C.CHUNK_SIZE; x += C.CHUNK_SIZE) {
            for (int y = (int)focus.getY() - C.CHUNK_SIZE; y <= focus.getY() + C.CHUNK_SIZE; y += C.CHUNK_SIZE) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    curChunks.add(getChunk(x, y));
                }
            }
        }

        physics.updateTileBodies();
        physics.updateDoorBodies();
        physics.updateStationBodies();

        prevChunks = new HashSet<Integer>(curChunks);
    }

    public Set<Integer> getCurChunks() {
        return curChunks;
    }

    public Set<Integer> getNewChunks() {
        return Sets.difference(curChunks, prevChunks);
    }

    public Set<Integer> getOldChunks() {
        return Sets.difference(prevChunks, curChunks);
    }

    public int getChunkX(int chunk) {

        return (chunk % C.MAP_WIDTH) * C.CHUNK_SIZE;
    }

    public int getChunkY(int chunk) {

        return (chunk / C.MAP_WIDTH) * C.CHUNK_SIZE;
    }

    public int getNumOfChunks() {
        return numOfChunks;
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public List<List<String>> getDoors() {
        return doors;
    }

    public List<List<String>> getItems() { return items; }

    public List<List<String>> getStations() { return stations; }

    public int getW() {
        return width;
    }

    public int getH() {
        return height;
    }

    public boolean intersects(Room r1, Room r2) {
        return !(r1.getX2() < r2.getX1() || r2.getX2() < r1.getX1() ||
            r1.getY2() < r2.getY1() || r2.getY2() < r1.getY1());
    }

    public int getChunk(int x, int y) {
        if (x < 0 || x > width || y < 0 || y > height) {
            return -1;
        } else {
            return x / C.CHUNK_SIZE + (y / C.CHUNK_SIZE) * C.MAP_WIDTH;
        }
    }

    public int getChunk(float x, float y) {
        return getChunk((int)x, (int)y);
    }

    public boolean setSolid(int x, int y, boolean solid) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            this.solid[chunk][chunkX][chunkY] = solid;
            return true;
        }
        return false;
    }

    public boolean isSolid(int x, int y) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            return solid[chunk][chunkX][chunkY];
        }
        return false;
    }

    public boolean setSight(int x, int y, boolean sight) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            this.sight[chunk][chunkX][chunkY] = sight;
            return true;
        }
        return false;
    }

    public boolean hasSight(int x, int y) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            return sight[chunk][chunkX][chunkY];

        }
        return false;
    }

    public boolean setRot(int x, int y, float ang) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            this.rot[chunk][chunkX][chunkY] = ang;
            return true;
        }
        return false;
    }

    public float getRot(int x, int y) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            return rot[chunk][chunkX][chunkY];
        }
        return 0;
    }

    public boolean setTile(int x, int y, String regionName) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            tiles[chunk][chunkX][chunkY] = mgr.getAtlas().findRegion(regionName);
            return true;
        }
        return false;
    }

    public TextureRegion getTile(int x, int y) {
        int chunk = getChunk(x, y);
        int chunkX = x % C.CHUNK_SIZE;
        int chunkY = y % C.CHUNK_SIZE;

        if (chunk != -1) {
            return tiles[chunk][chunkX][chunkY];
        }
        return null;
    }

    public PositionComp getFocus() {
        return focus;
    }

    public void setPhysicsSystem(PhysicsSystem physics) {
        this.physics = physics;
    }
}
