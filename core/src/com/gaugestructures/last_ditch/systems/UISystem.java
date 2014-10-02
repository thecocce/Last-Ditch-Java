package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gaugestructures.last_ditch.Manager;

public class UISystem extends GameSystem {
    private boolean active = false;
    private Manager mgr;
    private Stage stage;
    private Window window;
    private String focus;
    private Actor focusActor;
    private Table buttonTable;

    private UIBaseSystem uiBase;
    private UIActionsSystem uiActions;
    private UIInventorySystem uiInventory;
    private UIEquipSystem uiEquipment;
    private UIStatusSystem uiStatus;

    private TextButton actionsButton, inventoryButton, equipmentButton, statusButton;

    public UISystem(Manager mgr, TimeSystem time, EquipmentSystem equipment, InventorySystem inventory, CraftingSystem crafting) {
        this.mgr = mgr;

        stage = new Stage();

        window = new Window("", mgr.getSkin(), "window1");
        window.setPosition(24, 24);
        window.setSize(750, 490);
        window.setMovable(false);
        window.padTop(9);

        uiBase = new UIBaseSystem(mgr, stage, time);
        uiActions = new UIActionsSystem(mgr, crafting, window);
        uiInventory = new UIInventorySystem(mgr, inventory, window);
        uiEquipment = new UIEquipSystem(mgr, equipment, inventory, window);
        uiStatus = new UIStatusSystem(mgr, window);

        setupButtons();
        setupInitialState();

        if (0 == 1) {
            window.debug();
        }
    }

    private void setupButtons() {
        actionsButton = new TextButton("Actions", mgr.getSkin(), "actionsButton");
        inventoryButton = new TextButton("Inventory", mgr.getSkin(), "actionsButton");
        equipmentButton = new TextButton("Equipment", mgr.getSkin(), "actionsButton");
        statusButton = new TextButton("Status", mgr.getSkin(), "actionsButton");

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
        inventoryButton.setChecked(false);
        equipmentButton.setChecked(false);
        statusButton.setChecked(false);
    }

    private void switchFocus(String focus) {
        if (focus.equals("actions")) {
            uiActions.activate();
            window.getCell(focusActor).setActor(uiActions.getTable());

            actionsButton.setChecked(true);
            inventoryButton.setChecked(false);
            equipmentButton.setChecked(false);
            statusButton.setChecked(false);

            focusActor = uiActions.getTable();
        } else if (focus.equals("inventory")) {
            uiInventory.activate();
            window.getCell(focusActor).setActor(uiInventory.getTable());

            actionsButton.setChecked(false);
            inventoryButton.setChecked(true);
            equipmentButton.setChecked(false);
            statusButton.setChecked(false);

            focusActor = uiInventory.getTable();
        } else if (focus.equals("equipment")) {
            uiEquipment.activate();
            window.getCell(focusActor).setActor(uiEquipment.getTable());

            actionsButton.setChecked(false);
            inventoryButton.setChecked(false);
            equipmentButton.setChecked(true);
            statusButton.setChecked(false);

            focusActor = uiEquipment.getTable();
        } else if (focus.equals("status")) {
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
        uiBase.update();
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

    public UIBaseSystem getUiBase() {
        return uiBase;
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
