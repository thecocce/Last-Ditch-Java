package com.gaugestructures.last_ditch;

public class C {
    public static final String TITLE = "Last Ditch";

    public static final float BTW = 32f, WTB = 1 / 32f;
    public static final int BOX_VEL_ITER = 6, BOX_POS_ITER = 2;
    public static final float BOX_STEP = 1 / 60.f;
    public static final int MAX_STEPS = 5;

    public static final int WIDTH = 800, HEIGHT = 600;
    public static final int CHUNK_SIZE = 32;
    public static final int MAP_WIDTH = 6, MAP_HEIGHT = 6;

    public static final int BASE_SLOTS = 16;
    public static final int INVENTORY_SLOTS = 32;
    public static final float PLAYER_SPD = 1.7f, PLAYER_ROT_SPD = 4.4f;

    public static final short BIT_ZERO = 0;
    public static final short BIT_PLAYER = 2;
    public static final short BIT_LIGHT = 4;
    public static final short BIT_WALL = 8;
    public static final short BIT_WINDOW = 16;
    public static final short BIT_ENTITY = 32;

}
