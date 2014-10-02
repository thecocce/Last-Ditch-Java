package com.gaugestructures.last_ditch.systems;

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

    private ArrayList<ImageButton> invSlots = new ArrayList<ImageButton>();
    private boolean updateSlots = true;
    private Map<String, Object> itemData;
    private MapSystem map;
    private UIEquipSystem uiEquipment;
    private UIActionsSystem uiActions;
    private UIInventorySystem uiInventory;

    public InventorySystem(Manager mgr) {
        this.mgr = mgr;

        itemData = mgr.getData("items");
    }

    public void setMap(MapSystem map) {
        this.map = map;
    }

    public void setUIInventorySystem(UIInventorySystem uiInventory) {
        this.uiInventory = uiInventory;
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

    public String addItemByType(InventoryComp invComp, String type) {
        String item = createInvItem(type);

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

    public String createInvItem(String type) {
        updateSlots = true;

        String item = mgr.createEntity();
        @SuppressWarnings("unchecked")
        Map<String, Object> type_data = (Map<String, Object>) itemData.get(type);

        RotationComp rotComp = mgr.addComp(item, new RotationComp(0));
        TypeComp typeComp = mgr.addComp(item, new TypeComp(type));
        InfoComp infoComp = mgr.addComp(item, new InfoComp((String)type_data.get("name")));
        infoComp.setDesc((String)type_data.get("desc"));

        float quality = mgr.randFloat(0.2f, 0.5f);
        float condition = mgr.randFloat(0.1f, 0.4f);

        ItemComp itemComp = mgr.addComp(item, new ItemComp(quality, condition));
        Double baseValue = (Double)type_data.get("baseValue");
        itemComp.setBaseValue(baseValue.floatValue());
        itemComp.setUsable((Boolean)type_data.get("usable"));

        if(type_data.get("equippable") != null) {
            @SuppressWarnings("unchecked")
            ArrayList<String> equippableData = (ArrayList<String>) type_data.get("equippable");

            mgr.addComp(item, new EquippableComp(equippableData));
        }

        RenderComp renderComp = new RenderComp("");
        renderComp.setRegion(mgr.getAtlas().findRegion(String.format("items/%s", type)));

        SizeComp sizeComp = mgr.addComp(item, new SizeComp(renderComp.getW() * C.WTB, renderComp.getH() * C.WTB));

        return item;
    }

    public void setUIActions(UIActionsSystem uiActions) {
        this.uiActions = uiActions;
    }

    public void update() {
        if(updateSlots) {
            updateSlots = false;

            InventoryComp invComp = mgr.comp(mgr.getPlayer()        , InventoryComp.class);

            for(int i = 0; i < C.INVENTORY_SLOTS; i++) {
                TypeComp typeComp = mgr.comp(invComp.getItem(i), TypeComp.class);

                if(typeComp != null) {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiInventory.getSlot(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(mgr.getAtlas().findRegion(String.format("items/%s", typeComp.getType())));

                    uiInventory.getSlot(i).setStyle(style);


                } else {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiInventory.getSlot(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(mgr.getAtlas().findRegion("environ/empty"));

                    uiInventory.getSlot(i).setStyle(style);
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
            "as scrap at this point.");
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
        Map<String, Object> typeData = (Map<String, Object>) itemData.get(type);

        typeComp.setType(type);
        infoComp.setName((String)typeData.get("name"));
        infoComp.setDesc((String)typeData.get("desc"));
        itemComp.setUsable((Boolean)typeData.get("usable"));
        itemComp.setWeight(((Double)typeData.get("weight")).floatValue());
        itemComp.setBaseValue(((Double)typeData.get("baseValue")).floatValue());
        itemComp.setCondition(itemComp.getCondition() - itemComp.getDecayRate());

        if(itemComp.getCondition() <= 0) {
            destroyItem(item);
        }

        uiActions.updateCraftingInfo();
    }

    public String createItem(String type, float x, float y) {
        String item = mgr.createEntity();

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>) itemData.get(type);

        mgr.addComp(item, new PositionComp(x, y));
        mgr.addComp(item, new RotationComp(0));
        mgr.addComp(item, new TypeComp(type));

        InfoComp infoComp = mgr.addComp(item, new InfoComp((String) typeData.get("name")));
        infoComp.setDesc((String) typeData.get("desc"));

        float quality = mgr.randFloat(0.2f, 0.5f);
        float condition = mgr.randFloat(0.1f, 0.4f);

        ItemComp itemComp = mgr.addComp(item, new ItemComp(quality, condition));
        itemComp.setBaseValue(((Double) typeData.get("baseValue")).floatValue());
        itemComp.setUsable((Boolean) typeData.get("usable"));

        //Equippable types

        RenderComp renderComp = mgr.addComp(item, new RenderComp(""));
        renderComp.setRegionName(String.format("items/%s", type));
        renderComp.setRegion(mgr.getAtlas().findRegion(renderComp.getRegionName()));

        mgr.addComp(item, new SizeComp(renderComp.getW() * C.WTB, renderComp.getH() * C.WTB));

        return item;
    }

    public boolean pickupItem(String entity) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        String item = map.getNearItem(posComp.getX(), posComp.getY());

        if (item != null) {
            TypeComp typeComp = mgr.comp(item, TypeComp.class);

            if (addItemByType(invComp, typeComp.getType()) != null) {
                ItemComp itemComp = mgr.comp(item, ItemComp.class);
                invComp.setWeight(invComp.getWeight() + itemComp.getWeight());

                map.removeItem(item);
                uiInventory.setPrevSelection(null);

                return true;
            }

        }
        return false;
    }

    public boolean pickupItemAt(String entity, int screenX, int screenY) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        float x = posComp.getX() + C.WTB * (screenX - C.WIDTH / 2);
        float y = posComp.getY() - C.WTB * (screenY - C.HEIGHT / 2);

        String item = map.getItem(x, y);

        if(item != null) {
            TypeComp type = mgr.comp(item, TypeComp.class);
            String newItem = addItemByType(invComp, type.getType());

            if(newItem != null) {
                ItemComp itemComp = mgr.comp(item, ItemComp.class);

                map.removeItem(item);
                uiInventory.setPrevSelection(null);

                return true;
            }
        }
        return false;
    }

    public void setUpdateSlots(boolean updateSlots) {
        this.updateSlots = updateSlots;
    }
}
