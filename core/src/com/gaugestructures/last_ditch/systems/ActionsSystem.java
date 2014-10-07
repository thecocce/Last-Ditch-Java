package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.ActionComp;
import com.gaugestructures.last_ditch.components.RenderComp;
import com.gaugestructures.last_ditch.components.StationComp;
import com.gaugestructures.last_ditch.components.TypeComp;

import java.util.ArrayList;
import java.util.Map;

public class ActionsSystem extends GameSystem {
    private String curStation;
    private ArrayList<String> curActions = new ArrayList<String>();

    private Manager mgr;
    private TextureAtlas atlas;

    public ActionsSystem(Manager mgr) {
        this.mgr = mgr;

        atlas = mgr.getAtlas();
    }

    public String createAction(String item) {
        String type = mgr.comp(item, TypeComp.class).getType();

        String action = mgr.createEntity();

        Map<String, Object> itemsData = mgr.getData("items");

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>)itemsData.get(type);

        mgr.addComp(action, new TypeComp(type));
        ActionComp actionComp = mgr.addComp(action, new ActionComp((String)typeData.get("actionType")));
        actionComp.setItem(item);

        RenderComp renderComp = new RenderComp(
            type,
            atlas.findRegion(String.format("items/%s", type)));

        mgr.addComp(action, renderComp);

        return action;
    }

    public void setCurStation(String station) {
        curStation = station;
    }

    public String getCurStation() {
        return curStation;
    }
}
