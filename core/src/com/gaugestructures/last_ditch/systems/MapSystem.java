package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.PositionComp;
import com.gaugestructures.last_ditch.components.Room;
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


}
