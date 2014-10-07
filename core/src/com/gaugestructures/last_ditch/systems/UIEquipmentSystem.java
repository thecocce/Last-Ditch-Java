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

public class UIEquipmentSystem extends GameSystem {
    private boolean active = false, toggle = false;

    private Manager mgr;
    private Table table;
    private Label desc;
    private EquipmentSystem equipment;
    private InventorySystem inventory;
    private ArrayList<String> lHeadItems, rHeadItems, lArmItems, rArmItems, torsoItems, beltItems;
    private ArrayList<String> lHandItems, rHandItems,  lLegItems, rLegItems, lFootItems, rFootItems;
    private Array<String> lHeadList, rHeadList, lArmList, rArmList, torsoList, beltList;
    private Array<String> lHandList, rHandList,  lLegList, rLegList, lFootList, rFootList;
    private SelectBox<String> lHeadBox, rHeadBox, lArmBox, rArmBox, torsoBox, beltBox;
    private SelectBox<String> lHandBox, rHandBox, lLegBox, rLegBox, lFootBox, rFootBox;

    public UIEquipmentSystem(Manager mgr, EquipmentSystem equipment, InventorySystem inventory) {
        this.mgr = mgr;
        this.equipment = equipment;
        this.inventory = inventory;

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

    public void setupEquipmentLists() {
        lHeadItems = new ArrayList<String>();
        rHeadItems = new ArrayList<String>();
        lArmItems = new ArrayList<String>();
        rArmItems = new ArrayList<String>();
        torsoItems = new ArrayList<String>();
        lHandItems = new ArrayList<String>();
        rHandItems = new ArrayList<String>();
        beltItems = new ArrayList<String>();
        lLegItems = new ArrayList<String>();
        rLegItems = new ArrayList<String>();
        lFootItems = new ArrayList<String>();
        rFootItems = new ArrayList<String>();

        lHeadList = new Array<String>();
        rHeadList = new Array<String>();
        lArmList = new Array<String>();
        rArmList = new Array<String>();
        torsoList = new Array<String>();
        lHandList = new Array<String>();
        rHandList = new Array<String>();
        beltList = new Array<String>();
        lLegList = new Array<String>();
        rLegList = new Array<String>();
        lFootList = new Array<String>();
        rFootList = new Array<String>();

        lHeadList.add("none");
        rHeadList.add("none");
        lArmList.add("none");
        rArmList.add("none");
        torsoList.add("none");
        lHandList.add("none");
        rHandList.add("none");
        beltList.add("none");
        lLegList.add("none");
        rLegList.add("none");
        lFootList.add("none");
        rFootList.add("none");

        updateEquipmentLists();

        lHeadBox.setItems(lHeadList);
        rHeadBox.setItems(lHeadList);
        lArmBox.setItems(lArmList);
        rArmBox.setItems(rArmList);
        torsoBox.setItems(torsoList);
        beltBox.setItems(beltList);
        lHandBox.setItems(lHandList);
        rHandBox.setItems(rHandList);
        lLegBox.setItems(lLegList);
        rLegBox.setItems(rLegList);
        lFootBox.setItems(lFootList);
        rFootBox.setItems(rFootList);
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

        if(slot.equals("lHead")) {
            item = lHeadItems.get(index - 1);
        } else if (slot.equals("rHead")) {
            item = rHeadItems.get(index - 1);
        } else if (slot.equals("lHand")) {
            item = lHandItems.get(index - 1);
        } else if (slot.equals("rHand")) {
            item = rHandItems.get(index - 1);
        } else if (slot.equals("lArm")) {
            item = lArmItems.get(index - 1);
        } else if (slot.equals("rArm")) {
            item = rArmItems.get(index - 1);
        } else if (slot.equals("torso")) {
            item = torsoItems.get(index - 1);
        } else if (slot.equals("belt")) {
            item = beltItems.get(index - 1);
        } else if (slot.equals("lLeg")) {
            item = lLegItems.get(index - 1);
        } else if (slot.equals("rLeg")) {
            item = rLegItems.get(index - 1);
        } else if (slot.equals("lFoot")) {
            item = lFootItems.get(index - 1);
        } else if (slot.equals("rFoot")) {
            item = rFootItems.get(index - 1);
        }

        equipment.equip(mgr.getPlayer(), slot, item);
        inventory.removeItem(invComp, item);
    }

    public void updateEquipmentLists() {
        lHeadList.clear();
        rHeadList.clear();
        lArmList.clear();
        rArmList.clear();
        torsoList.clear();
        lHandList.clear();
        rHandList.clear();
        beltList.clear();
        lLegList.clear();
        rLegList.clear();
        lFootList.clear();
        rFootList.clear();

        lHeadItems.clear();
        rHeadItems.clear();
        lArmItems.clear();
        rArmItems.clear();
        torsoItems.clear();
        lHandItems.clear();
        rHandItems.clear();
        beltItems.clear();
        lLegItems.clear();
        rLegItems.clear();
        lFootItems.clear();
        rFootItems.clear();

        lHeadList.add("none");
        rHeadList.add("none");
        lArmList.add("none");
        rArmList.add("none");
        torsoList.add("none");
        lHandList.add("none");
        rHandList.add("none");
        beltList.add("none");
        lLegList.add("none");
        rLegList.add("none");
        lFootList.add("none");
        rFootList.add("none");

        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

        for(String item : invComp.getItems()) {
            EquippableComp equippableComp = mgr.comp(item, EquippableComp.class);

            if(equippableComp != null) {
                List<String> types = equippableComp.getTypes();
                String name = mgr.comp(item, InfoComp.class).getName();

                if(types.contains("head")) {
                    lHeadItems.add(item);
                    rHeadItems.add(item);

                    if (lHeadList.contains(name, false)) {
                        lHeadList.add(name + " ");
                    } else {
                        lHeadList.add(name);
                    }

                    if (rHeadList.contains(name, false)) {
                        rHeadList.add(name + " ");
                    } else {
                        rHeadList.add(name);
                    }
                } else if(types.contains("arm")) {
                    lArmItems.add(item);
                    rArmItems.add(item);

                    if (lArmList.contains(name, false)) {
                        lArmList.add(name + " ");
                    } else {
                        lArmList.add(name);
                    }

                    if (rArmList.contains(name, false)) {
                        rArmList.add(name + " ");
                    } else {
                        rArmList.add(name);
                    }
                } else if(types.contains("torso")) {
                    torsoItems.add(item);

                    if (torsoList.contains(name, false)) {
                        torsoList.add(name + " ");
                    } else {
                        torsoList.add(name);
                    }
                } else if(types.contains("hand")) {
                    lHandItems.add(item);
                    rHandItems.add(item);

                    if (lHandList.contains(name, false)) {
                        lHandList.add(name + " ");
                    } else {
                        lHandList.add(name);
                    }

                    if (rHandList.contains(name, false)) {
                        rHandList.add(name + " ");
                    } else {
                        rHandList.add(name);
                    }
                } else if(types.contains("belt")) {
                    beltItems.add(item);

                    if (beltList.contains(name, false)) {
                        beltList.add(name + " ");
                    } else {
                        beltList.add(name);
                    }
                } else if(types.contains("leg")) {
                    lLegItems.add(item);
                    rLegItems.add(item);

                    if (lLegList.contains(name, false)) {
                        lLegList.add(name + " ");
                    } else {
                        lLegList.add(name);
                    }

                    if (rLegList.contains(name, false)) {
                        rLegList.add(name + " ");
                    } else {
                        rLegList.add(name);
                    }
                } else if(types.contains("foot")) {
                    lFootItems.add(item);
                    rFootItems.add(item);

                    if (lFootList.contains(name, false)) {
                        lFootList.add(name + " ");
                    } else {
                        lFootList.add(name);
                    }

                    if (rFootList.contains(name, false)) {
                        rFootList.add(name + " ");
                    } else {
                        rFootList.add(name);
                    }
                }
            }
        }

        InfoComp infoComp;
        EquipmentComp equipComp = mgr.comp(mgr.getPlayer(), EquipmentComp.class);

        if (equipComp.getRHead() != null) {
            rHeadItems.add(equipComp.getRHead());
            infoComp = mgr.comp(equipComp.getRHead(), InfoComp.class);

            if (rHeadList.contains(infoComp.getName(), false)) {
                rHeadList.add(infoComp.getName() + " ");
            } else {
                rHeadList.add(infoComp.getName());
            }
        }

        if (equipComp.getLHead() != null) {
            lHeadItems.add(equipComp.getLHead());
            infoComp = mgr.comp(equipComp.getLHead(), InfoComp.class);

            if (lHeadList.contains(infoComp.getName(), false)) {
                lHeadList.add(infoComp.getName() + " ");
            } else {
                lHeadList.add(infoComp.getName());
            }
        }

        if (equipComp.getLHand() != null) {
            lHandItems.add(equipComp.getLHand());
            infoComp = mgr.comp(equipComp.getLHand(), InfoComp.class);

            if (lHandList.contains(infoComp.getName(), false)) {
                lHandList.add(infoComp.getName() + " ");
            } else {
                lHandList.add(infoComp.getName());
            }
        }

        if (equipComp.getRHand() != null) {
            rHandItems.add(equipComp.getRHand());
            infoComp = mgr.comp(equipComp.getRHand(), InfoComp.class);

            if (rHandList.contains(infoComp.getName(), false)) {
                rHandList.add(infoComp.getName() + " ");
            } else {
                rHandList.add(infoComp.getName());
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
            lArmItems.add(equipComp.getLArm());
            infoComp = mgr.comp(equipComp.getLArm(), InfoComp.class);

            if (lArmList.contains(infoComp.getName(), false)) {
                lArmList.add(infoComp.getName() + " ");
            } else {
                lArmList.add(infoComp.getName());
            }
        }

        if (equipComp.getRArm() != null) {
            rArmItems.add(equipComp.getRArm());
            infoComp = mgr.comp(equipComp.getRArm(), InfoComp.class);

            if (rArmList.contains(infoComp.getName(), false)) {
                rArmList.add(infoComp.getName() + " ");
            } else {
                rArmList.add(infoComp.getName());
            }
        }

        if (equipComp.getLLeg() != null) {
            lLegItems.add(equipComp.getLLeg());
            infoComp = mgr.comp(equipComp.getLLeg(), InfoComp.class);

            if (lLegList.contains(infoComp.getName(), false)) {
                lLegList.add(infoComp.getName() + " ");
            } else {
                lLegList.add(infoComp.getName());
            }
        }

        if (equipComp.getRLeg() != null) {
            rLegItems.add(equipComp.getRLeg());
            infoComp = mgr.comp(equipComp.getRLeg(), InfoComp.class);

            if (rLegList.contains(infoComp.getName(), false)) {
                rLegList.add(infoComp.getName() + " ");
            } else {
                rLegList.add(infoComp.getName());
            }
        }

        if (equipComp.getLFoot() != null) {
            lFootItems.add(equipComp.getLFoot());
            infoComp = mgr.comp(equipComp.getLFoot(), InfoComp.class);

            if (lFootList.contains(infoComp.getName(), false)) {
                lFootList.add(infoComp.getName() + " ");
            } else {
                lFootList.add(infoComp.getName());
            }
        }

        if (equipComp.getRFoot() != null) {
            rFootItems.add(equipComp.getRFoot());
            infoComp = mgr.comp(equipComp.getRFoot(), InfoComp.class);

            if (rFootList.contains(infoComp.getName(), false)) {
                rFootList.add(infoComp.getName() + " ");
            } else {
                rFootList.add(infoComp.getName());
            }
        }

        lHeadBox.setItems(lHeadList);
        rHeadBox.setItems(rHeadList);
        lArmBox.setItems(lArmList);
        rArmBox.setItems(rArmList);
        torsoBox.setItems(torsoList);
        beltBox.setItems(beltList);
        lHandBox.setItems(lHandList);
        rHandBox.setItems(rHandList);
        lLegBox.setItems(lLegList);
        rLegBox.setItems(rLegList);
        lFootBox.setItems(lFootList);
        rFootBox.setItems(rFootList);
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
