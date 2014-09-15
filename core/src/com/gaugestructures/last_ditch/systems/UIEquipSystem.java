package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gaugestructures.last_ditch.Manager;

public class UIEquipSystem extends GameSystem {
    private boolean active = false;
    private Manager mgr;
    private Window window;
    private Table table;

    public UIEquipSystem(Manager mgr, Window window) {
        this.mgr = mgr;
        this.window = window;

        table = new Table();
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public Table getTable() {
        return table;
    }
}
