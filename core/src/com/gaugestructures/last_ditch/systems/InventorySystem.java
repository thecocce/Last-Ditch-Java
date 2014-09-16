package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class InventorySystem extends GameSystem {
    private Manager mgr;
    private String player;
    private TextureAtlas atlas;

    private ArrayList<ImageButton> inv_slots = new ArrayList<ImageButton>();
    private boolean updateSlots = true;
    private Map<String, Object> item_data;
    private UIEquipSystem uiEquipment;
    private UIActionsSystem uiActions;

    public InventorySystem(Manager mgr, String player, TextureAtlas atlas) {
        this.mgr = mgr;
        this.player = player;
        this.atlas = atlas;

        item_data = mgr.getData("items");
    }

    public void setUIEquipmentSystem(UIEquipSystem uiEquipment) {
        this.uiEquipment = uiEquipment;
    }

    public String addItem(InventoryComp invComp, String item) {
        for(int i = 0; i < invComp.getSize(); i++) {
            if(invComp.getItem(i) == null) {
                updateSlots = true;
                invComp.setItem(i, item);
                ItemComp itemComp = mgr.comp(item, ItemComp.class);
                invComp.setWeight(invComp.getWeight() + itemComp.getWeight());
                uiEquipment.setupEquipmentLists();

                return item;
            }
        }

        return null;
    }

    public void setUIActions(UIActionsSystem uiActions) {
        this.uiActions = uiActions;
    }

    public void update() {
        if(updateSlots) {
            updateSlots = false;

            InventoryComp inv_comp = mgr.comp(player, InventoryComp.class);

            for(int i = 0; i < inv_slots.size(); i++) {
                TypeComp type_comp = mgr.comp(inv_comp.getItem(i), TypeComp.class);

                if(type_comp != null) {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(inv_slots.get(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(atlas.findRegion(String.format("items/%s", type_comp.getType())));

                    inv_slots.get(i).setStyle(style);
                } else {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(inv_slots.get(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(atlas.findRegion("environ/empty"));

                    inv_slots.get(i).setStyle(style);
                }
            }
        }
    }

    public String removeItem(InventoryComp invComp, String item) {
        int index = Arrays.asList(invComp.getItems()).indexOf(item);

        if(index != -1) {
            updateSlots = true;
            invComp.setItem(index, null);
            ItemComp itemComp = mgr.comp(item, ItemComp.class);
            invComp.setWeight(invComp.getWeight() - itemComp.getWeight());
            uiEquipment.setupEquipmentLists();

            return item;
        }
        return null;
    }

    public void destroyItem(String item) {
        InfoComp info_comp = mgr.comp(item, InfoComp.class);
        ItemComp item_comp = mgr.comp(item, ItemComp.class);

        item_comp.setUsable(false);
        item_comp.setCondition(0);
        info_comp.setDesc(
                "This item is junk. It can only be used " +
                        "as scrap at this point."
        );
    }

    public void useItem(String entity, String item, String type) {
        updateSlots = true;
        NeedsComp needsComp = mgr.comp(entity, NeedsComp.class);

        if(type.equals("rations1")) {
            type = "rations1_empty";
            needsComp.setHunger(Math.min(1f, needsComp.getHunger() + 0.1f));
        } else if(type.equals("canteen1_water")) {
            type = "canteen1_empty";
            needsComp.setThirst(Math.min(1f, needsComp.getThirst() + 0.1f));
        } else if(type.equals("canister1_empty")) {
            type = "canister1_empty";
            needsComp.setThirst(Math.min(1f, needsComp.getThirst() + 0.3f));
        }

        TypeComp typeComp = mgr.comp(item, TypeComp.class);
        InfoComp infoComp = mgr.comp(item, InfoComp.class);
        ItemComp itemComp = mgr.comp(item, ItemComp.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>)item_data.get(type);

        typeComp.setType(type);
        infoComp.setName((String) typeData.get("name"));
        infoComp.setDesc((String) typeData.get("desc"));
        itemComp.setUsable((Boolean) typeData.get("usable"));
        itemComp.setWeight(((Double) typeData.get("weight")).floatValue());
        itemComp.setBaseValue(((Double) typeData.get("base_value")).floatValue());
        itemComp.setCondition(itemComp.getCondition() - itemComp.getDecayRate());

        if(itemComp.getCondition() <= 0) {
            destroyItem(item);
        }

        uiActions.updateCraftingInfo();
    }

    public String create_item(String type, float x, float y) {
        String item = mgr.createEntity();

        @SuppressWarnings("unchecked")
        Map<String, Object> type_data = (Map<String, Object>)item_data.get(type);

        mgr.addComp(item, new PositionComp(x, y));
        mgr.addComp(item, new RotationComp(0));
        mgr.addComp(item, new TypeComp(type));

        InfoComp info_comp = mgr.addComp(item, new InfoComp((String) type_data.get("name")));
        info_comp.setDesc((String) type_data.get("desc"));

        float quality = mgr.randFloat(0.2f, 0.5f);
        float condition = mgr.randFloat(0.1f, 0.4f);

        ItemComp item_comp = mgr.addComp(item, new ItemComp(quality, condition));
        item_comp.setBaseValue(((Double) type_data.get("baseValue")).floatValue());
        item_comp.setUsable((Boolean) type_data.get("usable"));

        //Equippable types

        RenderComp render_comp = mgr.addComp(item, new RenderComp(""));
        render_comp.setRegionName(String.format("items/%s", type));
        render_comp.setRegion(atlas.findRegion(render_comp.getRegionName()));

        mgr.addComp(item, new SizeComp(render_comp.getW() * C.WTB, render_comp.getH() * C.WTB));

        return item;
    }
}
