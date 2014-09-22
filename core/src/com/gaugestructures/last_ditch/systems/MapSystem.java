package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.util.*;

public class MapSystem extends GameSystem {
    private Manager mgr;
    private String player;
    private Yaml yaml = new Yaml();
    private PositionComp focus;
    private TextureAtlas atlas;
    private Room master;
    private Random rnd = new Random();
    private int start_x, start_y, end_x, end_y;
    private int iterations = 120;
    private int num_of_rooms = 200;
    private int num_of_items = 3200;
    private InventorySystem inventory;
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private ArrayList<String> items = new ArrayList<String>();
    private ArrayList<String> doors = new ArrayList<String>();
    private ArrayList<String> stations = new ArrayList<String>();
    private boolean[][] solid = new boolean[C.MAP_WIDTH][C.MAP_HEIGHT];
    private boolean[][] sight = new boolean[C.MAP_WIDTH][C.MAP_HEIGHT];
    private float[][] rot = new float[C.MAP_WIDTH][C.MAP_HEIGHT];
    private RenderSystem render;
    private PhysicsSystem physics;
    private TextureRegion[][] tiles = new TextureRegion[C.MAP_WIDTH][C.MAP_HEIGHT];
    private OrthographicCamera cam = new OrthographicCamera();
    private int width = C.MAP_WIDTH, height = C.MAP_HEIGHT;

    public MapSystem(Manager mgr, String player, TextureAtlas atlas, InventorySystem inventory) {
        this.mgr = mgr;
        this.atlas = atlas;
        this.player = player;
        this.inventory = inventory;

        for(TextureRegion[] row : tiles)
            Arrays.fill(row, atlas.findRegion("environ/floor1"));

        cam.setToOrtho(false, C.WIDTH, C.HEIGHT);
        focus = mgr.comp(player, PositionComp.class);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    solid[x][y] = true;
                    sight[x][y] = true;
                    tiles[x][y] = atlas.findRegion("environ/empty");
                }
            }
        }

        generate_rooms();
        generate_items();
        generate_doors();
        generate_stations();

        update();
    }

    public void setPhysicsSystem(PhysicsSystem physics) {
        this.physics = physics;
    }

    public boolean useDoorAt(String entity, int screenX, int screenY) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        float x = posComp.getX() + C.WTB * (screenX - C.WIDTH / 2);
        float y = posComp.getY() - C.WTB * (screenY - C.HEIGHT / 2);

        String door = getDoor(x, y);

        if(door == null)
            return false;

        DoorComp doorComp = mgr.comp(door, DoorComp.class);

        if(doorComp.isLocked())
            return false;

        doorComp.setOpen(doorComp.isOpen());
        updateDoor(door, doorComp.isOpen());

        return true;
    }

    public boolean useDoor(String entity) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        String door = getNearDoor(posComp.getX(), posComp.getY());

        if(door == null)
            return false;

        DoorComp doorComp = mgr.comp(door, DoorComp.class);

        if(doorComp.isLocked())
            return false;

        doorComp.setOpen(!doorComp.isOpen());

        updateDoor(door, doorComp.isOpen());

        return true;
    }

    private void updateDoor(String door, boolean open) {
        PositionComp posComp = mgr.comp(door, PositionComp.class);
        RotationComp rotComp = mgr.comp(door, RotationComp.class);
        SizeComp sizeComp = mgr.comp(door, SizeComp.class);
        CollisionComp colComp = mgr.comp(door, CollisionComp.class);

        if(open) {
            mgr.removeComp(door, RenderComp.class);
            physics.removeBody(colComp.getBody());
        } else {
            TypeComp typeComp = mgr.comp(door, TypeComp.class);

            physics.createBody(
                posComp.getX(), posComp.getY(),
                sizeComp.getW(), sizeComp.getH(),
                false, rotComp.getAng());

            RenderComp renderComp = new RenderComp(
                typeComp.getType(),
                atlas.findRegion(
                    String.format("environ/%s", typeComp.getType())));

            mgr.addComp(door, renderComp);
        }
    }

    private String getDoor(float x, float y) {
        for(String entity : render.getNearbyEntities()) {
            if(doors.contains(entity)) {
                PositionComp posComp = mgr.comp(entity, PositionComp.class);
                RenderComp renderComp = mgr.comp(entity, RenderComp.class);
                SizeComp sizeComp = mgr.comp(entity, SizeComp.class);
                RotationComp rotComp = mgr.comp(entity, RotationComp.class);

                double c = Math.cos(-rotComp.getAng() * Math.PI / 180);
                double s = Math.sin(-rotComp.getAng() * Math.PI / 180);
                double rotX = posComp.getX() + c * (x - posComp.getX()) - s * (y - posComp.getY());
                double rotY = posComp.getY() + s * (x - posComp.getX()) + c * (y - posComp.getY());

                double left = posComp.getX() - sizeComp.getW();
                double right = posComp.getX() + sizeComp.getW();
                double top = posComp.getY() - sizeComp.getH();
                double bottom = posComp.getY() + sizeComp.getH();

                if(left <= rotX && rotX <= right && top <= rotY && rotY <= bottom) {
                    return entity;
                }
            }
        }
        return null;
    }

    private String getNearDoor(float x, float y) {
        for(String entity : render.getNearbyEntities()) {
            if(doors.contains(entity)) {
                PositionComp posComp = mgr.comp(entity, PositionComp.class);
                double distSqr = Math.pow((posComp.getX() - x), 2) + Math.pow((posComp.getY() - y), 2);

                if(distSqr < 2.6)
                    return entity;
            }
        }
        return null;
    }

    public void removeItem(String item) {
        mgr.removeComp(item, PositionComp.class);
        mgr.removeComp(item, RenderComp.class);

        items.remove(item);
        inventory.setUpdateSlots(true);
    }

    public void setRender(RenderSystem render) {
        this.render = render;
    }

    public void generate_rooms() {
        int x = 0, y = 0;
        master = new Room(10, 10, C.MAP_WIDTH - 10, C.MAP_HEIGHT - 10);

        for (int i = 0; i < num_of_rooms; i++) {
            x = mgr.randInt(master.getX1(), master.getX2() - 1);
            y = mgr.randInt(master.getY1(), master.getY2() - 1);

            rooms.add(new Room(x, y, 1, 1));
        }

        for (int i = 0; i < iterations; i++) {
            for (Room room : rooms) {
                expand(room);
            }
        }

        ArrayList<Room> degenerate_rooms = new ArrayList<Room>();

        for (Room room : rooms) {
            if (room.getW() < 5 || room.getH() < 5) {
                degenerate_rooms.add(room);
            } else {
                for(x = room.getX1(); x < room.getX2(); x++) {
                    for(y = room.getY1(); y < room.getY2(); y++) {
                        if(x == room.getX1() || x == room.getX2() - 1) {
                            solid[x][y] = true;
                            sight[x][y] = false;
                            rot[x][y] = 0;
                            tiles[x][y] = atlas.findRegion("environ/wall1");
                        } else if(y == room.getY1() || y == room.getY2() - 1) {
                            solid[x][y] = true;
                            sight[x][y] = false;
                            rot[x][y] = 0;
                            tiles[x][y] = atlas.findRegion("environ/wall1");
                        } else {
                            solid[x][y] = false;
                            sight[x][y] = true;
                            rot[x][y] = 0;
                            tiles[x][y] = atlas.findRegion("environ/floor2");
                        }
                    }
                }
            }
        }

        rooms.removeAll(degenerate_rooms);
    }

    public void generate_items() {
        Map<String, Object> item_data = mgr.getData("items");

        @SuppressWarnings("unchecked")
        List<String> item_list = (List<String>) item_data.get("item_list");

        float x = 0, y = 0;
        for(int i = 0; i < num_of_items; i++) {
            do {
                x = mgr.randFloat(10f, width - 10f);
                y = mgr.randFloat(10f, height - 10f);
            } while(is_solid((int)x, (int)y));

            String choice = item_list.get(rnd.nextInt(item_list.size()));
            String item = inventory.create_item(choice, x, y);

            items.add(item);
        }
    }

    public void generate_doors() {
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

            solid[x][y] = false;
            sight[x][y] = true;
            rot[x][y] = 0;
            tiles[x][y] = atlas.findRegion("environ/floor2");

            solid[x+1][y] = false;
            sight[x+1][y] = true;
            rot[x+1][y] = 0;
            tiles[x+1][y] = atlas.findRegion("environ/floor2");

            String door = mgr.createEntity();

            RenderComp renderComp = new RenderComp(
                "environ/door1",
                atlas.findRegion("environ/door1"));

            float w = renderComp.getW() * C.WTB;
            float h = renderComp.getH() * C.WTB;

            mgr.addComp(door, renderComp);
            mgr.addComp(door, new PositionComp(x + w/2, y + h/2));
            mgr.addComp(door, new SizeComp(w, h));
            mgr.addComp(door, new RotationComp(doorRot));
            mgr.addComp(door, new CollisionComp());
            mgr.addComp(door, new TypeComp("door1"));
            mgr.addComp(door, new DoorComp());

            doors.add(door);

            y = mgr.randInt(room.getY1() + 1, room.getY2() - 3);

            if (mgr.randInt(0, 1) == 0) {
                x = room.getX1();
                doorRot = 90;
            } else {
                x = room.getX2() - 1;
                doorRot = -90;
            }

            solid[x][y] = false;
            sight[x][y] = true;
            rot[x][y] = 0;
            tiles[x][y] = atlas.findRegion("environ/floor2");

            solid[x][y+1] = false;
            sight[x][y+1] = true;
            rot[x][y+1] = 0;
            tiles[x][y+1] = atlas.findRegion("environ/floor2");

            door = mgr.createEntity();

            renderComp = new RenderComp(
                "environ/door1",
                atlas.findRegion("environ/door1"));

            w = renderComp.getW() * C.WTB;
            h = renderComp.getH() * C.WTB;

            mgr.addComp(door, renderComp);

            mgr.addComp(door, renderComp);
            mgr.addComp(door, new PositionComp(x + h/2, y + w/2));
            mgr.addComp(door, new SizeComp(w, h));
            mgr.addComp(door, new RotationComp(doorRot));
            mgr.addComp(door, new CollisionComp());
            mgr.addComp(door, new TypeComp("door1"));
            mgr.addComp(door, new DoorComp());

            doors.add(door);
        }
    }

    public void generate_stations() {

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
        for(String entity : render.getNearbyEntities()) {
            PositionComp posComp = mgr.comp(entity, PositionComp.class);
            double distSqr = Math.pow((posComp.getX() - x), 2) + Math.pow((posComp.getY() - y), 2);

            if(distSqr < 1.4) {
                if(items.contains(entity)) {
                    RotationComp rotComp = mgr.comp(entity, RotationComp.class);
                    RenderComp renderComp = mgr.comp(entity, RenderComp.class);

                    double c = Math.cos(-rotComp.getAng() * Math.PI / 180);
                    double s = Math.sin(-rotComp.getAng() * Math.PI / 180);
                    double rotX = posComp.getX() + c * (x - posComp.getX()) - s * (y - posComp.getY());
                    double rotY = posComp.getY() + s * (x - posComp.getX()) + c * (y - posComp.getY());

                    double left = posComp.getX() - renderComp.getW() * C.WTB / 2;
                    double right = posComp.getX() + renderComp.getW() * C.WTB / 2;
                    double top = posComp.getY() - renderComp.getH() * C.WTB / 2;
                    double bottom = posComp.getY() + renderComp.getH() * C.WTB / 2;

                    if(left <= rotX && rotX <= right && top <= rotY && rotY <= bottom) {
                        return entity;
                    }
                }
            }

        }
        return null;
    }

    public boolean intersects(Room r1, Room r2) {
        return !(r1.getX2() < r2.getX1() || r2.getX2() < r1.getX1() ||
                 r1.getY2() < r2.getY1() || r2.getY2() < r1.getY1());
    }

    public void update() {
        start_x = (int)Math.max(focus.getX() - 13, 0);
        start_y = (int)Math.max(focus.getY() - 10, 0);
        end_x = (int)Math.min(focus.getX() + 13, C.MAP_WIDTH - 1);
        end_y = (int)Math.min(focus.getY() + 10, C.MAP_HEIGHT - 1);

        cam.position.set(focus.getX() * C.BTW, focus.getY() * C.BTW, 0);
        cam.update();
    }

    public void render(SpriteBatch batch) {
        for(int x = start_x; x <= end_x; x++) {
            for(int y = start_y; y <= end_y; y++) {
                batch.draw(
                    tiles[x][y],
                    x * C.BTW, y * C.BTW,
                    C.BTW / 2, C.BTW / 2,
                    C.BTW, C.BTW,
                    1, 1,
                    rot[x][y]
                );
            }
        }
    }

    public boolean is_solid(int x, int y) {
        return solid[x][y];
    }

    public boolean has_sight(int x, int y) {
        return sight[x][y];
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public ArrayList<String> getDoors() {
        return doors;
    }
}
