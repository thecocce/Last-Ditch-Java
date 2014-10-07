package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.VelocityComp;

public class InputSystem extends GameSystem implements InputProcessor {
    private boolean ctrl = false, shift = false;
    private String player;

    private Manager mgr;
    private TimeSystem time;
    private ActionsSystem actions;
    private InventorySystem inventory;
    private MapSystem map;
    private SkillTestSystem skillTest;
    private UISystem ui;
    private UIBaseSystem uiBase;
    private UIInventorySystem uiInventory;

    public InputSystem(Manager mgr, MapSystem map, ActionsSystem actions, InventorySystem inventory, SkillTestSystem skillTest, UISystem ui) {
        this.mgr = mgr;
        this.time = mgr.getTime();
        this.map = map;
        this.actions = actions;
        this.inventory = inventory;
        this.skillTest = skillTest;
        this.ui = ui;
        this.uiBase = ui.getBase();
        this.uiInventory = ui.getInventory();

        player = mgr.getPlayer();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            if (shift) {
                if (ui.isActive()) {

                } else {
                    if (inventory.pickupItemAt(player, screenX, screenY))
                        return true;

                    if (map.useDoorAt(player, screenX, screenY))
                        return true;

                    if (map.useStationAt(player, screenX, screenY))
                        return true;
                }
            } else {
                if (ui.isActive()) {

                } else if (skillTest.isActive()) {
                    skillTest.score();
                } else {
                    inventory.pickupItem(player);
                }
            }
        } else if (button == 1) {
            if (shift) {

            } else {
                if (uiBase.isActive()) {
                    uiBase.setNoExit(true);
                }

                if (ui.isActive()) {
                    uiInventory.setNoExit(true);

                    if (ui.getActiveIcon() == null) {
                        map.dropItem(player);
                    } else {
                        ui.setActiveAction(null);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        VelocityComp velComp;

        switch (keycode) {
            case Keys.TAB:
                if(ctrl) {
                    uiBase.toggleActive();
                } else {
                    velComp = mgr.comp(player, VelocityComp.class);
                    velComp.setSpd(0);
                    velComp.setAngSpd(0);

                    mgr.setPaused(!mgr.isPaused());
                    time.toggleActive();
                    actions.setCurStation(null);

                    if(ui.isActive()) {
                        ui.deactivate();
                        skillTest.deactivate();
                    } else {
                        ui.activate("inventory");
                    }
                }
                break;
            case Keys.E:
                if (shift) {
                    if (ui.isActive()) {

                    } else {

                    }
                } else {
                    if (ui.isActive()) {
                        mgr.setPaused(false);
                        time.setActive(true);
                        actions.setCurStation(null);
                        ui.deactivate();
                    } else {
                        if (map.useDoor(player))
                            return true;

                        if (map.useStation(player))
                            return true;
                    }
                }
                break;
            case Keys.W:
            case Keys.UP:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setSpd(C.PLAYER_SPD);
                break;
            case Keys.S:
            case Keys.DOWN:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setSpd(-C.PLAYER_SPD * 0.5f);
                break;
            case Keys.A:
            case Keys.LEFT:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setAngSpd(C.PLAYER_ROT_SPD);
                break;
            case Keys.D:
            case Keys.RIGHT:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setAngSpd(-C.PLAYER_ROT_SPD);
                break;
            case Keys.CONTROL_LEFT:
            case Keys.CONTROL_RIGHT:
                ctrl = true;
                break;
            case Keys.SHIFT_LEFT:
            case Keys.SHIFT_RIGHT:
                shift = true;
                break;
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        VelocityComp velComp;

        switch (keycode) {
            case Keys.W:
            case Keys.S:
            case Keys.UP:
            case Keys.DOWN:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setSpd(0);
                break;
            case Keys.A:
            case Keys.D:
            case Keys.LEFT:
            case Keys.RIGHT:
                velComp = mgr.comp(player, VelocityComp.class);
                velComp.setAngSpd(0);
                break;
            case Keys.CONTROL_LEFT:
            case Keys.CONTROL_RIGHT:
                ctrl = false;
                break;
            case Keys.SHIFT_LEFT:
            case Keys.SHIFT_RIGHT:
                shift = false;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (ui.getInventory().isActive()) {
            ui.setIconPosition(screenX, screenY);
        }

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}