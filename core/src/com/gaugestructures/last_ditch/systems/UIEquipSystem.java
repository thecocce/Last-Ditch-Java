package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.EquipmentComp;
import com.gaugestructures.last_ditch.components.EquippableComp;
import com.gaugestructures.last_ditch.components.InfoComp;
import com.gaugestructures.last_ditch.components.InventoryComp;

import java.util.ArrayList;

public class UIEquipSystem extends GameSystem {
    private boolean active = false, toggle = false;
    private Manager mgr;
    private Window window;
    private Table table;
    private EquipmentSystem equipment;
    private InventorySystem inventory;
    private Label desc;
    private ArrayList<String> headItems, armItems, torsoItems, handItems, beltItems, legItems, footItems;
    private SelectBox lHeadBox, rHeadBox, lArmBox, lHandBox, rHandBox, rArmBox, torsoBox, beltBox, lLegBox, rLegBox, lFootBox, rFootBox;

    public UIEquipSystem(Manager mgr, EquipmentSystem equipment, InventorySystem inventory, Window window) {
        this.mgr = mgr;
        this.window = window;
        this.inventory = inventory;
        this.equipment = equipment;

        setup();

        if(1 == 0) {
            table.setDebug(true);
        }
    }

    private void setup() {
        table = new Table();
        table.setPosition(0, 44);
        table.setSize(250, 290);

        lHeadBox = new SelectBox(mgr.getSkin(), "equipment");
        lHeadBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lHead", thisBox.getSelectedIndex());
            }
        });

        rHeadBox = new SelectBox(mgr.getSkin(), "equipment");
        rHeadBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rHead", thisBox.getSelectedIndex());
            }
        });

        lArmBox = new SelectBox(mgr.getSkin(), "equipment");
        lArmBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lArm", thisBox.getSelectedIndex());
            }
        });

        rArmBox = new SelectBox(mgr.getSkin(), "equipment");
        rArmBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rArm", thisBox.getSelectedIndex());
            }
        });

        torsoBox = new SelectBox(mgr.getSkin(), "equipment");
        torsoBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("torso", thisBox.getSelectedIndex());
            }
        });

        beltBox = new SelectBox(mgr.getSkin(), "equipment");
        beltBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("belt", thisBox.getSelectedIndex());
            }
        });

        lHandBox = new SelectBox(mgr.getSkin(), "equipment");
        lHandBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lHand", thisBox.getSelectedIndex());
            }
        });

        rHandBox = new SelectBox(mgr.getSkin(), "equipment");
        rHandBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rHand", thisBox.getSelectedIndex());
            }
        });

        lLegBox = new SelectBox(mgr.getSkin(), "equipment");
        lLegBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lLeg", thisBox.getSelectedIndex());
            }
        });

        rLegBox = new SelectBox(mgr.getSkin(), "equipment");
        rLegBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rLeg", thisBox.getSelectedIndex());
            }
        });

        lFootBox = new SelectBox(mgr.getSkin(), "equipment");
        lFootBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lFoot", thisBox.getSelectedIndex());
            }
        });

        rFootBox = new SelectBox(mgr.getSkin(), "equipment");
        rFootBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rFoot", thisBox.getSelectedIndex());
            }
        });

        desc = new Label("Description time!", mgr.getSkin(), "equipment");
        desc.setWrap(true);

        int equipBoxSize = 120;

        table.add(lHeadBox).width(equipBoxSize).padTop(8).padRight(0);
        table.add(rHeadBox).width(equipBoxSize).padTop(8).row();
        table.add(lArmBox).width(equipBoxSize).padRight(0);
        table.add(rArmBox).width(equipBoxSize).row();
        table.add(torsoBox).width(equipBoxSize).colspan(2).row();
        table.add(lHandBox).width(equipBoxSize).padRight(0);
        table.add(rHandBox).width(equipBoxSize).row();
        table.add(beltBox).width(equipBoxSize).colspan(2).row();
        table.add(lLegBox).width(equipBoxSize).padRight(0);
        table.add(rLegBox).width(equipBoxSize).row();
        table.add(lFootBox).width(equipBoxSize).padRight(0);
        table.add(rFootBox).width(equipBoxSize).row();

        table.add(desc).padTop(6).colspan(3).width(240);
    }

    private void setEquipment(String slot, int index) {
        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

        if(index == 0) {
            String item = equipment.dequip(mgr.getPlayer(), slot);

            if(!item.equals("")) {
                inventory.addItem(invComp, item);
                return;
            }
        }

        if(slot.equals("lHead") || slot.equals("rHead")) {
            equipment.equip(mgr.getPlayer(), slot, headItems.get(index - 1));
            inventory.removeItem(invComp, headItems.get(index - 1));
        } else if(slot.equals("lHand") || slot.equals("rHand")) {
            equipment.equip(mgr.getPlayer(), slot, handItems.get(index - 1));
            inventory.removeItem(invComp, handItems.get(index - 1));
        } else if(slot.equals("lArm") || slot.equals("rArm")) {
            equipment.equip(mgr.getPlayer(), slot, armItems.get(index - 1));
            inventory.removeItem(invComp, armItems.get(index - 1));
        } else if(slot.equals("torso")) {
            equipment.equip(mgr.getPlayer(), slot, torsoItems.get(index - 1));
            inventory.removeItem(invComp, torsoItems.get(index - 1));
        } else if(slot.equals("belt")) {
            equipment.equip(mgr.getPlayer(), slot, beltItems.get(index - 1));
            inventory.removeItem(invComp, beltItems.get(index - 1));
        } else if(slot.equals("lLeg") || slot.equals("rLeg")) {
            equipment.equip(mgr.getPlayer(), slot, legItems.get(index - 1));
            inventory.removeItem(invComp, legItems.get(index - 1));
        } else if(slot.equals("lFoot") || slot.equals("rFoot")) {
            equipment.equip(mgr.getPlayer(), slot, footItems.get(index - 1));
            inventory.removeItem(invComp, footItems.get(index - 1));
        }
    }

    public void setupEquipmentLists() {
        headItems = new ArrayList<String>();
        armItems = new ArrayList<String>();
        torsoItems = new ArrayList<String>();
        handItems = new ArrayList<String>();
        beltItems = new ArrayList<String>();
        legItems = new ArrayList<String>();
        footItems = new ArrayList<String>();

        Array<String> headList = new Array<String>();
        Array<String> armList = new Array<String>();
        Array<String> torsoList = new Array<String>();
        Array<String> handList = new Array<String>();
        Array<String> beltList = new Array<String>();
        Array<String> legList = new Array<String>();
        Array<String> footList = new Array<String>();

        headList.add("none");
        armList.add("none");
        torsoList.add("none");
        handList.add("none");
        beltList.add("none");
        legList.add("none");
        footList.add("none");

        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

        for(String item : invComp.getItems()) {
            EquippableComp equippableComp = mgr.comp(item, EquippableComp.class);

            if(equippableComp != null) {
                ArrayList<String> types = equippableComp.getTypes();
                InfoComp infoComp = mgr.comp(item, InfoComp.class);

                if(types.contains("lHead") || types.contains("rHead")) {
                    headItems.add(item);
                    headList.add(infoComp.getName());
                } else if(types.contains("lArm") || types.contains("rArm")) {
                    armItems.add(item);
                    armList.add(infoComp.getName());
                } else if(types.contains("torso")) {
                    torsoItems.add(item);
                    torsoList.add(infoComp.getName());
                } else if(types.contains("lHand") || types.contains("rHand")) {
                    handItems.add(item);
                    handList.add(infoComp.getName());
                } else if(types.contains("belt")) {
                    beltItems.add(item);
                    beltList.add(infoComp.getName());
                } else if(types.contains("lLeg") || types.contains("rLeg")) {
                    legItems.add(item);
                    legList.add(infoComp.getName());
                } else if(types.contains("lFoot") || types.contains("rFoot")) {
                    footItems.add(item);
                    footList.add(infoComp.getName());
                }
            }
        }

        InfoComp infoComp;
        EquipmentComp equipComp = mgr.comp(mgr.getPlayer(), EquipmentComp.class);

        if(equipComp.getRHead() != null) {
            infoComp = mgr.comp(equipComp.getRHead(), InfoComp.class);
            headList.add(infoComp.getName());
            headItems.add(equipComp.getRHead());
        }

        if(equipComp.getLHead() != null) {
            infoComp = mgr.comp(equipComp.getLHead(), InfoComp.class);
            headList.add(infoComp.getName());
            headItems.add(equipComp.getLHead());
        }

        if(equipComp.getLHand() != null) {
            infoComp = mgr.comp(equipComp.getLHand(), InfoComp.class);
            handList.add(infoComp.getName());
            handItems.add(equipComp.getLHand());
        }

        if(equipComp.getRHand() != null) {
            infoComp = mgr.comp(equipComp.getRHand(), InfoComp.class);
            handList.add(infoComp.getName());
            handItems.add(equipComp.getRHand());
        }

        if(equipComp.getTorso() != null) {
            infoComp = mgr.comp(equipComp.getTorso(), InfoComp.class);
            torsoList.add(infoComp.getName());
            torsoItems.add(equipComp.getTorso());
        }

        if(equipComp.getBelt() != null) {
            infoComp = mgr.comp(equipComp.getBelt(), InfoComp.class);
            beltList.add(infoComp.getName());
            beltItems.add(equipComp.getBelt());
        }

        if(equipComp.getLArm() != null) {
            infoComp = mgr.comp(equipComp.getLArm(), InfoComp.class);
            armList.add(infoComp.getName());
            armItems.add(equipComp.getLArm());
        }

        if(equipComp.getRArm() != null) {
            infoComp = mgr.comp(equipComp.getRArm(), InfoComp.class);
            armList.add(infoComp.getName());
            armItems.add(equipComp.getRArm());
        }

        if(equipComp.getLLeg() != null) {
            infoComp = mgr.comp(equipComp.getLLeg(), InfoComp.class);
            legList.add(infoComp.getName());
            legItems.add(equipComp.getLLeg());
        }

        if(equipComp.getRLeg() != null) {
            infoComp = mgr.comp(equipComp.getRLeg(), InfoComp.class);
            legList.add(infoComp.getName());
            legItems.add(equipComp.getRLeg());
        }

        if(equipComp.getLFoot() != null) {
            infoComp = mgr.comp(equipComp.getLFoot(), InfoComp.class);
            footList.add(infoComp.getName());
            footItems.add(equipComp.getLFoot());
        }

        if(equipComp.getRFoot() != null) {
            infoComp = mgr.comp(equipComp.getRFoot(), InfoComp.class);
            footList.add(infoComp.getName());
            footItems.add(equipComp.getRFoot());
        }

        lHeadBox.setItems(headList);
        rHeadBox.setItems(headList);
        lArmBox.setItems(armList);
        rArmBox.setItems(armList);
        torsoBox.setItems(torsoList);
        beltBox.setItems(beltList);
        lHandBox.setItems(handList);
        rHandBox.setItems(handList);
        lLegBox.setItems(legList);
        rLegBox.setItems(legList);
        lFootBox.setItems(footList);
        rFootBox.setItems(footList);
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
