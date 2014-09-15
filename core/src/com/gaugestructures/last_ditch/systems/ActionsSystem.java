package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;

public class ActionsSystem extends GameSystem {
    private Manager mgr;
    private String player;

    public ActionsSystem(Manager mgr, String player) {
        this.mgr = mgr;
        this.player = player;
    }

    public void clearCurStation() {

    }
}
