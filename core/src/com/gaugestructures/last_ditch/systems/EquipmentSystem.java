package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.AttributesComp;
import com.gaugestructures.last_ditch.components.EquipmentComp;
import com.gaugestructures.last_ditch.components.StatsComp;
import com.gaugestructures.last_ditch.components.TypeComp;

import java.util.Map;

public class EquipmentSystem extends GameSystem {
    private Manager mgr;
    private UIStatusSystem uiStatus;

    public EquipmentSystem(Manager mgr, String player) {
        this.mgr = mgr;
    }

    public void setUIStatusSystem(UIStatusSystem uiStatus) {
        this.uiStatus = uiStatus;
    }

    public void equip(String entity, String slot, String item) {
        dequip(entity, slot);

        EquipmentComp equipComp = mgr.comp(item, EquipmentComp.class);
        equipComp.setSlot(slot, item);

        Map<String, Object> itemData = mgr.getData("items");
        TypeComp typeComp = mgr.comp(item, TypeComp.class);
        String type = typeComp.getType();

        StatsComp statsComp = mgr.comp(entity, StatsComp.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> typeData = (Map<String, Object>)itemData.get(type);

        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>)typeData.get("stats");
        if(statsData != null) {
            Double dmg = (Double)statsData.get("dmg");
            if(dmg != null) {
                statsComp.setDmg(dmg.floatValue());
            }

            Double armor = (Double)statsData.get("armor");
            if(armor != null) {
                statsComp.setArmor(armor.floatValue());
            }

            uiStatus.updateStatsList();
        }

        AttributesComp attrComp = mgr.comp(entity, AttributesComp.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> attributesData = (Map<String, Object>)typeData.get("attributes");
        if(attributesData != null) {
            for(Map.Entry<String, Object> entry : attributesData.entrySet()) {
                Double runningMod = attrComp.getModifiers().get(entry.getKey()) + (Double)entry.getValue();
                attrComp.getModifiers().put(entry.getKey(), runningMod.floatValue());
            }
            uiStatus.updateAttributesList();
        }
    }

    public String dequip(String entity, String slot) {
        EquipmentComp equipComp = mgr.comp(entity, EquipmentComp.class);

        String item = equipComp.getSlot(slot);
        equipComp.setSlot(slot, null);

        if(!item.equals("")) {
            Map<String, Object> itemData = mgr.getData("items");
            String type = mgr.comp(item, TypeComp.class).getType();

            StatsComp statsComp = mgr.comp(entity, StatsComp.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> typeData = (Map<String, Object>)itemData.get(type);

            @SuppressWarnings("unchecked")
            Map<String, Object> statsData = (Map<String, Object>)typeData.get("stats");

            if(statsData != null) {
                Double dmg = (Double)statsData.get("dmg");
                if(dmg != null) {
                    statsComp.setDmg(0);
                }

                Double armor = (Double)statsData.get("armor");
                if(armor != null) {
                    statsComp.setArmor(0);
                }

                uiStatus.updateStatsList();
            }

            AttributesComp attrComp = mgr.comp(entity, AttributesComp.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> attributesData = (Map<String, Object>)typeData.get("attributes");
            if(attributesData != null) {
                for(Map.Entry<String, Object> entry : attributesData.entrySet()) {
                    Double runningMod = attrComp.getModifiers().get(entry.getKey()) - (Double)entry.getValue();
                    attrComp.getModifiers().put(entry.getKey(), runningMod.floatValue());
                }
                uiStatus.updateAttributesList();
            }
        }
        return item;
    }

    public void update() {

    }
}
