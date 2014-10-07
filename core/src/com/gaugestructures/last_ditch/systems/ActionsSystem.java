package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.StationComp;

import java.util.ArrayList;

public class ActionsSystem extends GameSystem {
    private String curStation;
    private ArrayList<String> curActions = new ArrayList<String>();

    private Manager mgr;

    public ActionsSystem(Manager mgr) {
        this.mgr = mgr;
    }

    public void setCurStation(String station) {
        curStation = station;
    }

    public String getCurStation() {
        return curStation;
    }
}
