package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gaugestructures.last_ditch.Manager;

public class UISystem extends GameSystem {
    private boolean active = false, toggle = false;
    private Manager mgr;
    private TextureAtlas atlas;
    private Skin skin;
    private Stage stage;
    private Window window;
    private String focus;
    private Actor focusActor;
    private Table buttonTable;

    private String player;

    private UIBaseSystem base;
    private UIActionsSystem uiActions;
    private UIInventorySystem uiInventory;
    private UIEquipSystem uiEquipment;
    private UIStatusSystem uiStatus;

    private TextButton actionsButton, inventoryButton, equipmentButton, statusButton;

    public UISystem(Manager mgr, EquipmentSystem equipment, InventorySystem inventory, String player, TextureAtlas atlas, Skin skin, CraftingSystem crafting) {
        this.mgr = mgr;
        this.player = player;
        this.atlas = atlas;
        this.skin = skin;

        stage = new Stage();

        window = new Window("", skin, "window1");
        window.setPosition(24, 24);
        window.setSize(750, 490);
        window.setMovable(false);
        window.padTop(9);

        base = new UIBaseSystem(mgr, stage);
        uiActions = new UIActionsSystem(mgr, crafting, skin, window);
        uiInventory = new UIInventorySystem(mgr, inventory, player, atlas, skin, window);
        uiEquipment = new UIEquipSystem(mgr, equipment, inventory, player, skin, window);
        uiStatus = new UIStatusSystem(mgr, window);

        setupButtons();
        setupInitialState();

        if(1 == 0) {
            window.setDebug(true);
        }
    }

    private void setupButtons() {
        actionsButton = new TextButton("Actions", skin, "actionsButton");
        inventoryButton = new TextButton("Inventory", skin, "actionsButton");
        equipmentButton = new TextButton("Equipment", skin, "actionsButton");
        statusButton = new TextButton("Status", skin, "actionsButton");

        actionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("actions");
            }
        });

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("inventory");
            }
        });

        equipmentButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("equipment");
            }
        });

        statusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("status");
            }
        });

        buttonTable = new Table();
        buttonTable.add(actionsButton).width(100).height(16).padRight(60);
        buttonTable.add(inventoryButton).width(100).height(16).padRight(60);
        buttonTable.add(equipmentButton).width(100).height(16).padRight(60);
        buttonTable.add(statusButton).width(100).height(16).padRight(60);

        window.add(buttonTable).width(700).row();
    }

    private void setupInitialState() {
        focus = "actions";
        focusActor = uiActions.getTable();

        window.add(focusActor).width(680).height(432);

        uiActions.activate();
        window.getCell(focusActor).setActor(uiActions.getTable());

        actionsButton.setChecked(true);
    }

    private void switchFocus(String focus) {
        if(focus.equals("actions")) {
            uiActions.activate();
            window.getCell(focusActor).setActor(uiActions.getTable());

            actionsButton.setChecked(true);
            inventoryButton.setChecked(false);
            equipmentButton.setChecked(false);
            statusButton.setChecked(false);

            focusActor = uiActions.getTable();
        } else if(focus.equals("inventory")) {
            uiInventory.activate();
            window.getCell(focusActor).setActor(uiInventory.getTable());

            actionsButton.setChecked(false);
            inventoryButton.setChecked(true);
            equipmentButton.setChecked(false);
            statusButton.setChecked(false);

            focusActor = uiInventory.getTable();
        } else if(focus.equals("equipment")) {
            uiEquipment.activate();
            window.getCell(focusActor).setActor(uiEquipment.getTable());

            actionsButton.setChecked(false);
            inventoryButton.setChecked(false);
            equipmentButton.setChecked(true);
            statusButton.setChecked(false);

            focusActor = uiEquipment.getTable();
        } else if(focus.equals("status")) {
            uiStatus.activate();
            window.getCell(focusActor).setActor(uiStatus.getTable());

            actionsButton.setChecked(false);
            inventoryButton.setChecked(false);
            equipmentButton.setChecked(false);
            statusButton.setChecked(true);

            focusActor = uiStatus.getTable();
        }
    }

    public void update() {
        base.update();
        uiActions.update();
        uiInventory.update();
        uiEquipment.update();
        uiStatus.update();
    }

    public void activate(String focus) {
        active = true;
        this.focus = focus;
        switchFocus(focus);
        stage.addActor(window);
    }

    public void deactivate() {
        active = false;

        uiActions.deactivate();
        uiInventory.deactivate();
        uiEquipment.deactivate();
        uiStatus.deactivate();

        window.remove();
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public UIBaseSystem getBase() {
        return base;
    }

    public UIActionsSystem getActions() {
        return uiActions;
    }

    public UIInventorySystem getInventory() {
        return uiInventory;
    }

    public UIEquipSystem getEquipment() {
        return uiEquipment;
    }

    public UIStatusSystem getStatus() {
        return uiStatus;
    }

    public String getFocus() {
        return focus;
    }

    public Stage getStage() {
        return stage;
    }


}
