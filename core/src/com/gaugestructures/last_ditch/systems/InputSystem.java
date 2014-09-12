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
    private boolean ctrl = false, shift = false;

    public InputSystem(Manager mgr, String player) {
        this.player = player;
        this.mgr = mgr;
    }

    @Override
    public boolean keyDown(int keycode) {
        VelocityComp vel_comp;

        switch (keycode) {
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.W:
            case Keys.UP:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.set_spd(C.PLAYER_SPD);
                break;
            case Keys.S:
            case Keys.DOWN:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.set_spd(-C.PLAYER_SPD * 0.5f);
                break;
            case Keys.A:
            case Keys.RIGHT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.set_ang_spd(C.PLAYER_ROT_SPD);
                break;
            case Keys.D:
            case Keys.LEFT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.set_ang_spd(-C.PLAYER_ROT_SPD);
                break;
            case Keys.CONTROL_LEFT:
            case Keys.CONTROL_RIGHT:
                ctrl = true;
                break;
            case Keys.SHIFT_LEFT:
            case Keys.SHIFT_RIGHT:
                shift = true;
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
                vel_comp.set_spd(0);
                break;
            case Keys.A:
            case Keys.D:
            case Keys.LEFT:
            case Keys.RIGHT:
                vel_comp = mgr.comp(player, VelocityComp.class);
                vel_comp.set_ang_spd(0);
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
