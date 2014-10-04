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
    private Box2DDebugRenderer debug;

    private Manager mgr;
    private TimeSystem time;
    private ActionsSystem actions;
    private CraftingSystem crafting;
    private InventorySystem inventory;
    private EquipmentSystem equipment;
    private StatusSystem status;

    private UISystem ui;
    private InputSystem input;
    private MapSystem map;
    private RenderSystem render;
    private PhysicsSystem physics;
    private LightingSystem lighting;

	@Override
	public void create () {
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("gfx/graphics.atlas"));
        skin = new Skin(Gdx.files.internal("com/gaugestructures/last_ditch/cfg/uiskin.json"), atlas);

        mgr = new Manager(atlas, skin);
        mgr.createPlayer();

        setupPlayer(mgr.getPlayer());

        time = new TimeSystem();
        actions = new ActionsSystem(mgr);
        crafting = new CraftingSystem(mgr);
        inventory = new InventorySystem(mgr);
        equipment = new EquipmentSystem(mgr);
        status = new StatusSystem(mgr, time);

        ui = new UISystem(mgr, time, actions, equipment, inventory, crafting);
        inventory.setUIActions(ui.getActions());
        inventory.setUIEquipmentSystem(ui.getEquipment());
        inventory.setUIInventorySystem(ui.getInventory());
        equipment.setUIStatusSystem(ui.getStatus());
        map = new MapSystem(mgr, time, actions, inventory, ui, ui.getActions(), ui.getEquipment(), ui.getInventory());
        inventory.setMap(map);
        input = new InputSystem(mgr, map, ui, inventory, actions, ui.getInventory(), time);
        render = new RenderSystem(mgr, map);
        physics = new PhysicsSystem(mgr, map);
        map.setPhysicsSystem(physics);
        lighting = new LightingSystem(map.getCam(), physics);

        debug = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(new InputMultiplexer(ui.getStage(), input));
	}

    public void update() {
        timer += Gdx.graphics.getDeltaTime();
        int n = (int) Math.floor(timer / C.BOX_STEP);
        if (n > 0) timer -= n * C.BOX_STEP;
        int steps = Math.min(n, C.MAX_STEPS);

        while (steps > 0) {
            actions.update();
            crafting.update();
            inventory.update();
            equipment.update();
            status.update();

            if (!mgr.isPaused()) {
                time.update();
                render.update();
                physics.update();
            }
            steps--;
        }
        map.update();
        ui.update();
    }

	@Override
	public void render() {
        update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(map.getCam().combined);

        batch.begin();

        map.render(batch);
        render.render(batch);

        batch.end();

        lighting.render();
        ui.render();
	}

    private void setupPlayer(String player) {
        mgr.addComp(player, new PositionComp(12, 12));
        mgr.addComp(player, new RotationComp(0));
        mgr.addComp(player, new TypeComp("player"));
        mgr.addComp(player, new NeedsComp());
        mgr.addComp(player, new InventoryComp(C.INVENTORY_SLOTS));
        mgr.addComp(player, new VelocityComp(0, 0, C.PLAYER_SPD, C.PLAYER_ROT_SPD));
        mgr.addComp(player, new SkillsComp());
        mgr.addComp(player, new AttributesComp());
        mgr.addComp(player, new StatsComp());
        mgr.addComp(player, new CollisionComp());
        mgr.addComp(player, new EquipmentComp());

        AnimationComp animComp = new AnimationComp(0.1f);
        animComp.addAnimation(
            "female1/idle",
            "female1/idle1");
        animComp.addAnimation(
            "female1/walk",
            "female1/walk1",
            "female1/walk2",
            "female1/walk1",
            "female1/idle1",
            "female1/walk1-f",
            "female1/walk2-f",
            "female1/walk1-f");
        mgr.addComp(player, animComp);
        mgr.addComp(player, new InfoComp("Kadijah"));
    }
}
