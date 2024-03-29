package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventorySystem extends GameSystem {
    private boolean updateSlots = true;

    private Manager mgr;
    private TextureAtlas atlas;
    private MapSystem map;
    private UISystem ui;
    private UIActionsSystem uiActions;
    private UIInventorySystem uiInventory;
    private UIEquipmentSystem uiEquipment;
    private Map<String, Object> itemData;
    private ArrayList<ImageButton> invSlots = new ArrayList<ImageButton>();

    public InventorySystem(Manager mgr) {
        this.mgr = mgr;
        atlas = mgr.getAtlas();

        itemData = mgr.getData("items");
    }

    public String createItem(String type, float x, float y) {
        String item = mgr.createEntity();

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>) itemData.get(type);

        mgr.addComp(item, new PositionComp(x, y));
        mgr.addComp(item, new TypeComp(type));

        InfoComp infoComp = mgr.addComp(item, new InfoComp((String) typeData.get("name")));
        infoComp.setDesc((String)typeData.get("desc"));

        float quality = mgr.randFloat(0.2f, 0.5f);
        float condition = mgr.randFloat(0.1f, 0.4f);

        ItemComp itemComp = mgr.addComp(item, new ItemComp(quality, condition));
        itemComp.setBaseValue(((Double)typeData.get("baseValue")).floatValue());
        itemComp.setUsable((Boolean)typeData.get("usable"));
        itemComp.setWeight(((Double)typeData.get("weight")).floatValue());

        @SuppressWarnings("unchecked")
        List<String> equippableData = (List<String>)typeData.get("equippable");

        if (equippableData != null) {
            mgr.addComp(item, new EquippableComp(equippableData));
        }

        RenderComp renderComp = mgr.addComp(item, new RenderComp(""));
        renderComp.setRegionName(String.format("items/%s", type));
        renderComp.setRegion(atlas.findRegion(renderComp.getRegionName()));

        mgr.addComp(item, new SizeComp(renderComp.getW() * C.WTB, renderComp.getH() * C.WTB));

        return item;
    }

    public String createInvItem(String type) {
        updateSlots = true;

        String item = mgr.createEntity();
        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>) itemData.get(type);

        mgr.addComp(item, new RotationComp(0));
        mgr.addComp(item, new TypeComp(type));
        InfoComp infoComp = mgr.addComp(
            item, new InfoComp((String)typeData.get("name")));
        infoComp.setDesc((String)typeData.get("desc"));

        float quality = mgr.randFloat(0.2f, 0.5f);
        float condition = mgr.randFloat(0.1f, 0.4f);

        ItemComp itemComp = mgr.addComp(item, new ItemComp(quality, condition));

        Double baseValue = (Double)typeData.get("baseValue");
        itemComp.setBaseValue(baseValue.floatValue());
        itemComp.setUsable((Boolean)typeData.get("usable"));
        itemComp.setWeight(((Double)typeData.get("weight")).floatValue());

        if(typeData.get("equippable") != null) {
            @SuppressWarnings("unchecked")
            ArrayList<String> equippableData = (ArrayList<String>)typeData.get("equippable");

            mgr.addComp(item, new EquippableComp(equippableData));
        }

        RenderComp renderComp = new RenderComp("");
        renderComp.setRegion(atlas.findRegion(String.format("items/%s", type)));

        mgr.addComp(item, new SizeComp(renderComp.getW() * C.WTB, renderComp.getH() * C.WTB));

        return item;
    }

    public String addItem(InventoryComp invComp, String item) {
        for(int i = 0; i < invComp.getSize(); i++) {
            if(invComp.getItem(i) == null) {
                updateSlots = true;
                invComp.setItem(i, item);
                ItemComp itemComp = mgr.comp(item, ItemComp.class);
                invComp.setWeight(invComp.getWeight() + itemComp.getWeight());
                uiEquipment.updateEquipmentLists();

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
                uiEquipment.updateEquipmentLists();

                return item;
            }
        }

        return null;
    }

    public String removeItem(InventoryComp invComp, String item) {
        int index = invComp.getItems().indexOf(item);

        if (index != -1) {
            updateSlots = true;
            invComp.setItem(index, null);
            ItemComp itemComp = mgr.comp(item, ItemComp.class);
            invComp.setWeight(invComp.getWeight() - itemComp.getWeight());
            uiEquipment.updateEquipmentLists();

            return item;
        }

        return null;
    }

    public List<String> removeItemsByType(InventoryComp invComp, String type, int amt) {
        List<String> itemsToRemove = new ArrayList<String>();

        for (String item : invComp.getItems()) {
            if (item == null)
                continue;

            TypeComp typeComp = mgr.comp(item, TypeComp.class);

            if (type.equals(typeComp.getType())) {
                amt -= 1;
                itemsToRemove.add(item);

                if (amt == 0)
                    break;
            }
        }

        if (amt > 0) {
            return null;
        } else {
            for (String item : itemsToRemove) {
                int index = invComp.getItems().indexOf(item);
                invComp.setItem(index, null);
            }

            updateSlots = true;

            return itemsToRemove;
        }
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
            type = "rations1Empty";
            needsComp.setHunger(Math.min(1f, needsComp.getHunger() + 0.1f));
        } else if(type.equals("canteen1Water")) {
            type = "canteen1Empty";
            needsComp.setThirst(Math.min(1f, needsComp.getThirst() + 0.1f));
        } else if(type.equals("canister1Water")) {
            type = "canister1Empty";
            needsComp.setThirst(Math.min(1f, needsComp.getThirst() + 0.3f));
        }

        TypeComp typeComp = mgr.comp(item, TypeComp.class);
        InfoComp infoComp = mgr.comp(item, InfoComp.class);
        ItemComp itemComp = mgr.comp(item, ItemComp.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>)itemData.get(type);

        typeComp.setType(type);
        infoComp.setName((String)typeData.get("name"));
        infoComp.setDesc((String)typeData.get("desc"));
        itemComp.setUsable((Boolean)typeData.get("usable"));
        itemComp.setWeight(((Double)typeData.get("weight")).floatValue());
        itemComp.setBaseValue(((Double)typeData.get("baseValue")).floatValue());
        itemComp.setCondition(itemComp.getCondition() - itemComp.getDecayRate());

        if(itemComp.getCondition() <= 0)
            destroyItem(item);

        uiActions.updateCraftingInfo();
    }

    public boolean pickupItem(String entity) {
        PositionComp posComp = mgr.comp(entity, PositionComp.class);
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        String item = map.getNearItem(posComp.getX(), posComp.getY());

        if (item != null) {
            if (addItem(invComp, item) != null) {
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
            if(addItem(invComp, item) != null) {
                map.removeItem(item);
                uiInventory.setPrevSelection(null);

                return true;
            }
        }

        return false;
    }

    public int itemCount(String entity, String type) {
        int count = 0;
        InventoryComp invComp = mgr.comp(entity, InventoryComp.class);

        for (String item : invComp.getItems()) {
            TypeComp typeComp = mgr.comp(item, TypeComp.class);

            if (typeComp != null && typeComp.getType().equals(type)) {
                count += 1;
            }
        }

        return count;
    }

    public void update() {
        if(updateSlots) {
            updateSlots = false;

            InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

            for(int i = 0; i < C.INVENTORY_SLOTS; i++) {
                String item = invComp.getItem(i);
                ItemComp itemComp = mgr.comp(item, ItemComp.class);
                TypeComp typeComp = mgr.comp(item, TypeComp.class);

                if(typeComp != null) {
                    ImageButton slot = uiInventory.getSlot(i);
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiInventory.getSlot(i).getStyle());

                    if (itemComp.isEquipped()) {
                        if (slot == uiInventory.getSelection()) {
                            style.up = new TextureRegionDrawable(
                                atlas.findRegion("ui/invSelectionEquipped"));
                        } else {
                            style.up = new TextureRegionDrawable(
                                atlas.findRegion("ui/invSlotEquipped"));
                        }
                    } else {
                        if (slot == uiInventory.getSelection()) {
                            style.up = new TextureRegionDrawable(
                                atlas.findRegion("ui/invSelection"));
                        } else {
                            style.up = new TextureRegionDrawable(
                                atlas.findRegion("ui/invSlot"));
                        }
                    }
                    style.imageUp = new TextureRegionDrawable(
                        atlas.findRegion(String.format("items/%s", typeComp.getType())));

                    slot.setStyle(style);
                } else {
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(uiInventory.getSlot(i).getStyle());
                    style.imageUp = new TextureRegionDrawable(
                        atlas.findRegion("environ/empty"));

                    uiInventory.getSlot(i).setStyle(style);
                }
            }
        }
    }

    public void setMap(MapSystem map) {
        this.map = map;
    }

    public void setUISystem(UISystem ui) {
        this.ui = ui;
        uiActions = ui.getActions();
        uiInventory = ui.getInventory();
        uiEquipment = ui.getEquipment();
    }

    public void setUpdateSlots(boolean updateSlots) {
        this.updateSlots = updateSlots;
    }
}
