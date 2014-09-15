package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gaugestructures.last_ditch.Manager;

public class UIBaseSystem extends GameSystem {
    private boolean active = false;
    private Manager mgr;
    private Stage stage;

    public UIBaseSystem(Manager mgr, Stage stage) {
        this.mgr = mgr;
        this.stage = stage;
    }

    public void toggleActive() {
        active = !active;

        if(active) {

        } else {

        }
    }
}
