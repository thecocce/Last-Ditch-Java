package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.Map;

public class InventorySystem extends GameSystem {
    private Manager mgr;
    private String player;
    private TextureAtlas atlas;

    private ArrayList<ImageButton> inv_slots = new ArrayList<ImageButton>();
    private boolean update_slots = true;
    private Map<String, Object> item_data;

    public InventorySystem(Manager mgr, String player, TextureAtlas atlas) {
        this.mgr = mgr;
        this.player = player;
        this.atlas = atlas;

        item_data = mgr.get_data("items");
    }

    public void update() {
        if(update_slots) {
            update_slots = false;

            InventoryComp inv_comp = mgr.comp(player, InventoryComp.class);

            for(int i = 0; i < inv_slots.size(); i++) {
                TypeComp type_comp = mgr.comp(inv_comp.get_item(i), TypeComp.class);

                if(type_comp != null) {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(inv_slots.get(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(atlas.findRegion(String.format("items/%s", type_comp.get_type())));

                    inv_slots.get(i).setStyle(style);
                } else {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(inv_slots.get(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(atlas.findRegion("environ/empty"));

                    inv_slots.get(i).setStyle(style);
                }
            }
        }
    }

    public void destroy_item(String item) {
        InfoComp info_comp = mgr.comp(item, InfoComp.class);
        ItemComp item_comp = mgr.comp(item, ItemComp.class);

        item_comp.set_usable(false);
        item_comp.set_condition(0);
        info_comp.set_desc(
            "This item is junk. It can only be used " +
            "as scrap at this point."
        );
    }

    public String create_item(String type, float x, float y) {
        String item = mgr.create_entity();

        @SuppressWarnings("unchecked")
        Map<String, Object> type_data = (Map<String, Object>)item_data.get(type);

        PositionComp pos_comp = new PositionComp(x, y);
        RotationComp rot_comp = new RotationComp(0);
        TypeComp type_comp = new TypeComp(type);
        InfoComp info_comp = new InfoComp((String)type_data.get("name"));
        info_comp.set_desc((String)type_data.get("desc"));

        mgr.add_comp(item, pos_comp);
        mgr.add_comp(item, rot_comp);
        mgr.add_comp(item, type_comp);
        mgr.add_comp(item, info_comp);

        float quality = mgr.rand_float(0.2f, 0.5f);
        float condition = mgr.rand_float(0.1f, 0.4f);

        ItemComp item_comp = new ItemComp(quality, condition);
        item_comp.set_base_value(((Double)type_data.get("base_value")).floatValue());
        item_comp.set_usable((Boolean)type_data.get("usable"));

        mgr.add_comp(item, item_comp);

        //Equippable types

        RenderComp render_comp = new RenderComp("");
        render_comp.set_region_name(String.format("items/%s", type));
        render_comp.set_region(atlas.findRegion(render_comp.get_region_name()));

        mgr.add_comp(item, render_comp);

        SizeComp size_comp = new SizeComp(render_comp.get_width() * C.WTB, render_comp.get_height() * C.WTB);

        mgr.add_comp(item, size_comp);

        return item;
    }
}
