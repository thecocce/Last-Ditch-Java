package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.VelocityComp;

public class InputSystem extends GameSystem implements InputProcessor {
    private Manager mgr;
    private String player;
    private UISystem ui;
    private TimeSystem time;
    private ActionsSystem actions;
    private boolean ctrl = false, shift = false;

    public InputSystem(Manager mgr, String player, UISystem ui, ActionsSystem actions, TimeSystem time) {
        this.player = player;
        this.mgr = mgr;
        this.ui = ui;
        this.time = time;
        this.actions = actions;
    }

    @Override
    public boolean keyDown(int keycode) {
        VelocityComp vel_comp;

        switch (keycode) {
            case Keys.TAB:
                if(ctrl) {
                    ui.getBase().toggleActive();
                } else {
                    VelocityComp velComp = mgr.comp(player, VelocityComp.class);
                    velComp.setSpd(0);
                    velComp.setAngSpd(0);

                    mgr.setPaused(!mgr.isPaused());
                    time.toggleActive();
                    actions.clearCurStation();

                    if(ui.isActive()) {
                        ui.deactivate();

                    } else {
                        ui.activate(ui.getFocus());
                    }

                }
            case Keys.W:
            case Keys.UP:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setSpd(C.PLAYER_SPD);
                break;
            case Keys.S:
            case Keys.DOWN:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setSpd(-C.PLAYER_SPD * 0.5f);
                break;
            case Keys.A:
            case Keys.RIGHT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setAngSpd(C.PLAYER_ROT_SPD);
                break;
            case Keys.D:
            case Keys.LEFT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setAngSpd(-C.PLAYER_ROT_SPD);
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
        VelocityComp vel_comp;

        switch (keycode) {
            case Keys.W:
            case Keys.S:
            case Keys.UP:
            case Keys.DOWN:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setSpd(0);
                break;
            case Keys.A:
            case Keys.D:
            case Keys.LEFT:
            case Keys.RIGHT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.setAngSpd(0);
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
