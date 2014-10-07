package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.PositionComp;
import com.gaugestructures.last_ditch.components.RenderComp;

public class UISystem extends GameSystem {
    private boolean active = false;
    private float iconX, iconY;

    private Manager mgr;
    private String uiFocus, activeAction;
    private PositionComp focus;
    private Stage stage;
    private Window window;
    private Actor focusActor;
    private Table buttonTable;
    private Image activeIcon;
    private TextButton actionsButton, equipmentButton, inventoryButton, statusButton;

    private UIBaseSystem uiBase;
    private UIActionsSystem uiActions;
    private UIEquipmentSystem uiEquipment;
    private UIInventorySystem uiInventory;
    private UIStatusSystem uiStatus;

    public UISystem(Manager mgr, ActionsSystem actions, CraftingSystem crafting, EquipmentSystem equipment, InventorySystem inventory) {
        this.mgr = mgr;

        stage = new Stage();
        focus = mgr.comp(mgr.getPlayer(), PositionComp.class);

        window = new Window("", mgr.getSkin(), "window1");
        window.setPosition(24, 54);
        window.setSize(750, 490);
        window.setMovable(false);
        window.padTop(9);

        uiBase = new UIBaseSystem(mgr, stage, this);
        uiActions = new UIActionsSystem(mgr, actions, crafting, inventory);
        uiInventory = new UIInventorySystem(mgr, inventory, this);
        uiEquipment = new UIEquipmentSystem(mgr, equipment, inventory);
        uiStatus = new UIStatusSystem(mgr);

        uiActions.setUISystem(this);

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
        uiFocus = "actions";
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

    public Image getActiveIcon() {
        return activeIcon;
    }

    public void setActiveAction(String action) {
        if (action == null) {
            activeAction = null;
            activeIcon = null;
        } else {
            activeAction = action;

            TextureRegion actionRegion = mgr.getAtlas().findRegion(String.format("items/%s", action));
            activeIcon = new Image(actionRegion);
        }
    }

    public void setIconPosition(float screenX, float screenY) {
        iconX = screenX + C.BTW * focus.getX() - C.WIDTH / 2 - 14;
        iconY = C.HEIGHT - screenY + C.BTW * focus.getY() - C.HEIGHT / 2 - 14;
    }

    public void activate(String uiFocus) {
        active = true;
        this.uiFocus = uiFocus;
        switchFocus(uiFocus);
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

    public void update() {
        uiBase.update();
        uiActions.update();
        uiInventory.update();
        uiEquipment.update();
        uiStatus.update();

        if (activeIcon != null) {
            activeIcon.setPosition(iconX, iconY);
        }
    }

    public void render(SpriteBatch batch) {
        stage.act();
        stage.draw();

        if (activeIcon != null) {
            batch.begin();
            activeIcon.draw(batch, 1);
            batch.end();
        }
    }

    public boolean isActive() {
        return active;
    }

    public UIBaseSystem getBase() {
        return uiBase;
    }

    public UIActionsSystem getActions() {
        return uiActions;
    }

    public UIInventorySystem getInventory() {
        return uiInventory;
    }

    public UIEquipmentSystem getEquipment() {
        return uiEquipment;
    }

    public UIStatusSystem getStatus() {
        return uiStatus;
    }

    public String getUiFocus() {
        return uiFocus;
    }

    public Stage getStage() {
        return stage;
    }
}
