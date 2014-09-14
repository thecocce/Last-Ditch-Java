package com.gaugestructures.last_ditch;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gaugestructures.last_ditch.components.*;
import com.gaugestructures.last_ditch.systems.*;

public class LastDitch extends ApplicationAdapter {
    private float timer = 0;

    private Skin skin;
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private String player;
    private Box2DDebugRenderer debug;

    private Manager mgr = new Manager();
    private TimeSystem time;
    private InputSystem input;
    private InventorySystem inventory;
    private MapSystem map;
    private RenderSystem render;
    private PhysicsSystem physics;
    private LightingSystem lighting;

	@Override
	public void create () {
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("gfx/graphics.atlas"));
        skin = new Skin(Gdx.files.internal("com/gaugestructures/last_ditch/cfg/uiskin.json"), atlas);

        player = mgr.create_entity();

        setup_player(player);

        time = new TimeSystem();
        input = new InputSystem(mgr, player);
        inventory = new InventorySystem(mgr, player, atlas);
        map = new MapSystem(mgr, player, atlas, inventory);
        render = new RenderSystem(mgr, player, atlas);
        physics = new PhysicsSystem(mgr, player, map);
        lighting = new LightingSystem(map.get_cam(), physics);

        debug = new Box2DDebugRenderer();

        InputMultiplexer multiplexer = new InputMultiplexer(input);

        Gdx.input.setInputProcessor(multiplexer);
	}

    public void update() {
        timer += Gdx.graphics.getDeltaTime();
        int n = (int) Math.floor(timer / C.BOX_STEP);
        if (n > 0) timer -= n * C.BOX_STEP;

        int steps = Math.min(n, C.MAX_STEPS);

        while (steps > 0) {
            inventory.update();

            if (!mgr.is_paused()) {
                time.update();
                render.update();
                physics.update();
            }

            steps--;
        }

        map.update();
    }

	@Override
	public void render() {
        update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(map.get_cam().combined);

        batch.begin();

        map.render(batch);
        render.render(batch);

        batch.end();

        lighting.render();
	}

    private void setup_player(String player) {
        mgr.add_comp(player, new PositionComp(12, 12));
        mgr.add_comp(player, new RotationComp(0));
        mgr.add_comp(player, new TypeComp("player"));
        mgr.add_comp(player, new InventoryComp(C.INVENTORY_SLOTS));
        mgr.add_comp(player, new VelocityComp(0, 0, C.PLAYER_SPD, C.PLAYER_ROT_SPD));
        mgr.add_comp(player, new CollisionComp());

        AnimationComp anim_comp = new AnimationComp(0.1f);
        anim_comp.add_animation("female1/idle", "female1/idle1");
        anim_comp.add_animation(
            "female1/walk",
            "female1/walk1",
            "female1/walk2",
            "female1/walk1",
            "female1/idle1",
            "female1/walk1-f",
            "female1/walk2-f",
            "female1/walk1-f");
        mgr.add_comp(player, anim_comp);
        mgr.add_comp(player, new InfoComp("Kadijah"));
    }
}
