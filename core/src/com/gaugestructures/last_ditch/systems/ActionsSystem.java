package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.StationComp;

public class ActionsSystem extends GameSystem {
    private Manager mgr;
    private String curStation;

    public ActionsSystem(Manager mgr) {
        this.mgr = mgr;
    }

    public void clearCurStation() {

    }

    public String getCurStation() {
        return curStation;
    }
}
