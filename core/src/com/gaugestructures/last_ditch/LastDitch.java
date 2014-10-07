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
    private AISystem ai;
    private MapSystem map;
    private SkillTestSystem skillTest;
    private RenderSystem render;
    private PhysicsSystem physics;
    private LightingSystem lighting;

	@Override
	public void create () {
        batch = new SpriteBatch();
        debug = new Box2DDebugRenderer();
        atlas = new TextureAtlas(Gdx.files.internal("gfx/graphics.atlas"));
        skin = new Skin(Gdx.files.internal("com/gaugestructures/last_ditch/cfg/uiskin.json"), atlas);

        time = new TimeSystem();

        mgr = new Manager(atlas, skin);
        mgr.setTime(time);
        mgr.createPlayer();

        setupPlayer(mgr.getPlayer());
        setupDrones();

        actions = new ActionsSystem(mgr);
        crafting = new CraftingSystem(mgr);
        equipment = new EquipmentSystem(mgr);
        inventory = new InventorySystem(mgr);
        status = new StatusSystem(mgr);

        ui = new UISystem(mgr, actions, crafting, equipment, inventory);
        ai = new AISystem(mgr);
        map = new MapSystem(mgr, actions, inventory, ui);
        skillTest = new SkillTestSystem(mgr, actions, crafting, inventory, ui);
        input = new InputSystem(mgr, map, actions, inventory, skillTest, ui);
        render = new RenderSystem(mgr, map);
        physics = new PhysicsSystem(mgr, map);
        lighting = new LightingSystem(map.getCam(), physics);

        inventory.setUISystem(ui);
        inventory.setMap(map);
        equipment.setUISystem(ui);
        map.setPhysicsSystem(physics);

        givePlayerBasics();
        createTestingArea();

        Gdx.input.setInputProcessor(new InputMultiplexer(ui.getStage(), input));
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

    private void setupDrones() {
        String drone1 = mgr.createEntity();

        mgr.addComp(drone1, new PositionComp(6, 6));
        mgr.addComp(drone1, new VelocityComp(0, 0, 0.8f, 1f));
        mgr.addComp(drone1, new RotationComp(0));
        mgr.addComp(drone1, new CollisionComp());
        mgr.addComp(drone1, new EquipmentComp());
        mgr.addComp(drone1, new AIComp("wander"));
        AnimationComp animComp = mgr.addComp(drone1, new AnimationComp(0.3f));
        animComp.addAnimation(
            "drone1/idle",
            "drone1/idle1",
            "drone1/idle2");

        String drone2 = mgr.createEntity();

        mgr.addComp(drone2, new PositionComp(6, 40));
        mgr.addComp(drone2, new VelocityComp(0, 0, 1f, 1f));
        mgr.addComp(drone2, new RotationComp(0));
        mgr.addComp(drone2, new CollisionComp());
        mgr.addComp(drone2, new EquipmentComp());
        mgr.addComp(drone2, new AIComp("wander"));
        animComp = mgr.addComp(drone2, new AnimationComp(0.3f));
        animComp.addAnimation(
            "drone1/idle",
            "drone1/idle1",
            "drone1/idle2");
    }

    private void givePlayerBasics() {
        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

        inventory.addItemByType(invComp, "canister1Waste");
        inventory.addItemByType(invComp, "batteryEmpty");
        inventory.addItemByType(invComp, "canteen1Empty");
        inventory.addItemByType(invComp, "canister1Empty");
        inventory.addItemByType(invComp, "overgrowth1");
        inventory.addItemByType(invComp, "overgrowth1");
        inventory.addItemByType(invComp, "handgun1");
        inventory.addItemByType(invComp, "headset");
        inventory.addItemByType(invComp, "flakLight");

        ui.getEquipment().updateEquipmentLists();
    }

    private void createTestingArea() {
        map.createStation("chargingStation", 3, 3);
        map.createStation("purificationStation", 3, 9);
        map.createStation("incinerator", 9, 3);
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
            skillTest.update();

            if (!mgr.isPaused()) {
                time.update();
                ai.update();
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
        ui.render(batch);
        skillTest.render(batch);
    }
}