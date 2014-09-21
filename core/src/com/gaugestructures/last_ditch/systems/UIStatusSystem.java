package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gaugestructures.last_ditch.Manager;

public class UIStatusSystem extends GameSystem {
    private boolean active = false;
    private Manager mgr;
    private Window window;
    private Table table;

    public UIStatusSystem(Manager mgr, Window window) {
        this.mgr = mgr;
        this.window = window;

        table = new Table();
    }

    public void updateStatsList() {

    }

    public void updateAttributesList() {

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