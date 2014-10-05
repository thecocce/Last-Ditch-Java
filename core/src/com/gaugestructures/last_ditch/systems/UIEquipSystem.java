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
import java.util.List;

public class UIEquipSystem extends GameSystem {
    private boolean active = false, toggle = false;
    private Manager mgr;
    private Window window;
    private Table table;
    private EquipmentSystem equipment;
    private InventorySystem inventory;
    private Label desc;
    private ArrayList<String> headItems, armItems, torsoItems, handItems, beltItems, legItems, footItems;
    private Array<String> headList, armList, torsoList, handList, beltList, legList, footList;
    private SelectBox<String> lHeadBox, rHeadBox, lArmBox, lHandBox, rHandBox, rArmBox, torsoBox, beltBox, lLegBox, rLegBox, lFootBox, rFootBox;

    public UIEquipSystem(Manager mgr, EquipmentSystem equipment, InventorySystem inventory, Window window) {
        this.mgr = mgr;
        this.window = window;
        this.inventory = inventory;
        this.equipment = equipment;

        setup();
        setupEquipmentLists();

        if(1 == 0) {
            table.setDebug(true);
        }
    }

    private void setup() {
        table = new Table();
        table.setPosition(0, 44);
        table.setSize(250, 290);

        lHeadBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        lHeadBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lHead", thisBox.getSelectedIndex());
            }
        });

        rHeadBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        rHeadBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rHead", thisBox.getSelectedIndex());
            }
        });

        lArmBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        lArmBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lArm", thisBox.getSelectedIndex());
            }
        });

        rArmBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        rArmBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rArm", thisBox.getSelectedIndex());
            }
        });

        torsoBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        torsoBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("torso", thisBox.getSelectedIndex());
            }
        });

        beltBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        beltBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("belt", thisBox.getSelectedIndex());
            }
        });

        lHandBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        lHandBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lHand", thisBox.getSelectedIndex());
            }
        });

        rHandBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        rHandBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rHand", thisBox.getSelectedIndex());
            }
        });

        lLegBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        lLegBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lLeg", thisBox.getSelectedIndex());
            }
        });

        rLegBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        rLegBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("rLeg", thisBox.getSelectedIndex());
            }
        });

        lFootBox = new SelectBox<String>(mgr.getSkin(), "equipment");
        lFootBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SelectBox thisBox = (SelectBox)actor;
                setEquipment("lFoot", thisBox.getSelectedIndex());
            }
        });

        rFootBox = new SelectBox<String>(mgr.getSkin(), "equipment");
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
        String item = "";
        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

        if(index == 0) {
            item = equipment.dequip(mgr.getPlayer(), slot);

            if(item != null && !item.equals("")) {
                inventory.addItem(invComp, item);
            }
            return;
        }

        if(slot.equals("lHead") || slot.equals("rHead")) {
            item = headItems.get(index - 1);
        } else if(slot.equals("lHand") || slot.equals("rHand")) {
            item = handItems.get(index - 1);
        } else if(slot.equals("lArm") || slot.equals("rArm")) {
            item = armItems.get(index - 1);
        } else if(slot.equals("torso")) {
            item = torsoItems.get(index - 1);
        } else if(slot.equals("belt")) {
            item = headItems.get(index - 1);
        } else if(slot.equals("lLeg") || slot.equals("rLeg")) {
            item = headItems.get(index - 1);
        } else if(slot.equals("lFoot") || slot.equals("rFoot")) {
            item = headItems.get(index - 1);
        }
        equipment.equip(mgr.getPlayer(), slot, item);
        inventory.removeItem(invComp, item);
    }

    public void setupEquipmentLists() {
        headItems = new ArrayList<String>();
        armItems = new ArrayList<String>();
        torsoItems = new ArrayList<String>();
        handItems = new ArrayList<String>();
        beltItems = new ArrayList<String>();
        legItems = new ArrayList<String>();
        footItems = new ArrayList<String>();

        headList = new Array<String>();
        armList = new Array<String>();
        torsoList = new Array<String>();
        handList = new Array<String>();
        beltList = new Array<String>();
        legList = new Array<String>();
        footList = new Array<String>();

        headList.add("none");
        armList.add("none");
        torsoList.add("none");
        handList.add("none");
        beltList.add("none");
        legList.add("none");
        footList.add("none");

        updateEquipmentLists();

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

    public void updateEquipmentLists() {
        headList.clear();
        armList.clear();
        torsoList.clear();
        handList.clear();
        beltList.clear();
        legList.clear();
        footList.clear();

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
                List<String> types = equippableComp.getTypes();
                InfoComp infoComp = mgr.comp(item, InfoComp.class);
                Array<String> equipList;

                if(types.contains("lHead") || types.contains("rHead")) {
                    headItems.add(item);
                    equipList = headList;
                } else if(types.contains("lArm") || types.contains("rArm")) {
                    armItems.add(item);
                    equipList = armList;
                } else if(types.contains("torso")) {
                    torsoItems.add(item);
                    equipList = torsoList;
                } else if(types.contains("lHand") || types.contains("rHand")) {
                    handItems.add(item);
                    equipList = handList;
                } else if(types.contains("belt")) {
                    beltItems.add(item);
                    equipList = beltList;
                } else if(types.contains("lLeg") || types.contains("rLeg")) {
                    legItems.add(item);
                    equipList = legList;
                } else if(types.contains("lFoot") || types.contains("rFoot")) {
                    footItems.add(item);
                    equipList = footList;
                } else {
                    equipList = new Array<String>();
                }

                if (equipList.contains(infoComp.getName(), false)) {
                    equipList.add(infoComp.getName() + " ");
                } else {
                    equipList.add(infoComp.getName());
                }

            }
        }

        InfoComp infoComp;
        EquipmentComp equipComp = mgr.comp(mgr.getPlayer(), EquipmentComp.class);

        if (equipComp.getRHead() != null) {
            headItems.add(equipComp.getRHead());
            infoComp = mgr.comp(equipComp.getRHead(), InfoComp.class);

            if (headList.contains(infoComp.getName(), false)) {
                headList.add(infoComp.getName() + " ");
            } else {
                headList.add(infoComp.getName());
            }
        }

        if (equipComp.getLHead() != null) {
            headItems.add(equipComp.getLHead());
            infoComp = mgr.comp(equipComp.getLHead(), InfoComp.class);

            if (headList.contains(infoComp.getName(), false)) {
                headList.add(infoComp.getName() + " ");
            } else {
                headList.add(infoComp.getName());
            }
        }

        if (equipComp.getLHand() != null) {
            handItems.add(equipComp.getLHand());
            infoComp = mgr.comp(equipComp.getLHand(), InfoComp.class);

            if (handList.contains(infoComp.getName(), false)) {
                handList.add(infoComp.getName() + " ");
            } else {
                handList.add(infoComp.getName());
            }
        }

        if (equipComp.getRHand() != null) {
            handItems.add(equipComp.getRHand());
            infoComp = mgr.comp(equipComp.getRHand(), InfoComp.class);

            if (handList.contains(infoComp.getName(), false)) {
                handList.add(infoComp.getName() + " ");
            } else {
                handList.add(infoComp.getName());
            }
        }

        if (equipComp.getTorso() != null) {
            torsoItems.add(equipComp.getTorso());
            infoComp = mgr.comp(equipComp.getTorso(), InfoComp.class);

            if (torsoList.contains(infoComp.getName(), false)) {
                torsoList.add(infoComp.getName() + " ");
            } else {
                torsoList.add(infoComp.getName());
            }
        }

        if (equipComp.getBelt() != null) {
            beltItems.add(equipComp.getBelt());
            infoComp = mgr.comp(equipComp.getBelt(), InfoComp.class);

            if (beltList.contains(infoComp.getName(), false)) {
                beltList.add(infoComp.getName() + " ");
            } else {
                beltList.add(infoComp.getName());
            }
        }

        if (equipComp.getLArm() != null) {
            armItems.add(equipComp.getLArm());
            infoComp = mgr.comp(equipComp.getLArm(), InfoComp.class);

            if (armList.contains(infoComp.getName(), false)) {
                armList.add(infoComp.getName() + " ");
            } else {
                armList.add(infoComp.getName());
            }
        }

        if (equipComp.getRArm() != null) {
            armItems.add(equipComp.getRArm());
            infoComp = mgr.comp(equipComp.getRArm(), InfoComp.class);

            if (armList.contains(infoComp.getName(), false)) {
                armList.add(infoComp.getName() + " ");
            } else {
                armList.add(infoComp.getName());
            }
        }

        if (equipComp.getLLeg() != null) {
            legItems.add(equipComp.getLLeg());
            infoComp = mgr.comp(equipComp.getLLeg(), InfoComp.class);

            if (legList.contains(infoComp.getName(), false)) {
                legList.add(infoComp.getName() + " ");
            } else {
                legList.add(infoComp.getName());
            }
        }

        if (equipComp.getRLeg() != null) {
            legItems.add(equipComp.getRLeg());
            infoComp = mgr.comp(equipComp.getRLeg(), InfoComp.class);

            if (legList.contains(infoComp.getName(), false)) {
                legList.add(infoComp.getName() + " ");
            } else {
                legList.add(infoComp.getName());
            }
        }

        if (equipComp.getLFoot() != null) {
            footItems.add(equipComp.getLFoot());
            infoComp = mgr.comp(equipComp.getLFoot(), InfoComp.class);

            if (footList.contains(infoComp.getName(), false)) {
                footList.add(infoComp.getName() + " ");
            } else {
                footList.add(infoComp.getName());
            }
        }

        if (equipComp.getRFoot() != null) {
            footItems.add(equipComp.getRFoot());
            infoComp = mgr.comp(equipComp.getRFoot(), InfoComp.class);

            if (footList.contains(infoComp.getName(), false)) {
                footList.add(infoComp.getName() + " ");
            } else {
                footList.add(infoComp.getName());
            }
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
