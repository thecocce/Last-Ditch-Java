package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.InfoComp;
import com.gaugestructures.last_ditch.components.InventoryComp;
import com.gaugestructures.last_ditch.components.ItemComp;
import com.gaugestructures.last_ditch.components.TypeComp;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class UIInventorySystem extends GameSystem {
    private boolean active = false, noExit = false;
    private String player;

    private Manager mgr;
    private Skin skin;
    private Table table;
    private ActionsSystem actions;
    private InventorySystem inventory;
    private UISystem ui;
    private ImageButton selection, prevSelection;
    private ArrayList<ImageButton> slots;
    private Label itemName, itemDesc, itemValue, itemWeight, itemQualityDur;

    public UIInventorySystem(Manager mgr, ActionsSystem actions, InventorySystem inventory, UISystem ui) {
        this.mgr = mgr;
        this.actions = actions;
        this.inventory = inventory;
        this.ui = ui;

        skin = mgr.getSkin();
        player = mgr.getPlayer();

        setup();

        if (1 == 0) {
            table.debug();
        }
    }

    private void setup() {
        itemName = new Label("", skin, "inventory");
        itemDesc = new Label("", skin, "inventory");
        itemValue = new Label("", skin, "inventory");
        itemWeight = new Label("", skin, "inventory");
        itemQualityDur = new Label("", skin, "inventory");

        itemValue.setColor(0.75f, 0.82f, 0.7f, 1f);
        itemWeight.setColor(0.75f, 0.75f, 0.89f, 1f);
        itemQualityDur.setColor(0.8f, 0.8f, 0.8f, 1f);

        itemDesc.setAlignment(Align.top | Align.left);
        itemDesc.setWrap(true);

        table = new Table();
        table.setPosition(262, 2);
        table.setSize(276, 236);

        table.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                exitTable();
            }
        });

        table.add(itemName).colspan(4).align(Align.left).padTop(4).height(12);
        table.add(itemValue).colspan(4).align(Align.right).padTop(4).height(14).row();
        table.add(itemWeight).colspan(4).align(Align.left).height(14).padTop(1);
        table.add(itemQualityDur).colspan(4).align(Align.right).padTop(1).height(12).row();
        table.add(itemDesc).colspan(8).width(256).height(62).row();

        slots = new ArrayList<ImageButton>();

        for(int i = 1; i <= C.INVENTORY_SLOTS; i++) {
            final ImageButton slot = new ImageButton(skin, "invSlot");

            slots.add(slot);

            slot.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    enterSlot(slot);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == 0) {
                        setActiveAction();
                    } else if (button == 1) {

                    }

                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    noExit = true;
                }
            });

            if(i % 8 == 0) {
                table.add(slot).pad(0).row();
            } else {
                table.add(slot).pad(0);
            }
        }
    }

    private void setActiveAction() {
        if (selection == null) {
            ui.setHoverAction(null);
            return;
        }

        int index = slots.indexOf(selection);
        InventoryComp invComp = mgr.comp(player, InventoryComp.class);

        if (index != -1) {
            String item = invComp.getItem(index);
            ItemComp itemComp = mgr.comp(item, ItemComp.class);

            if (itemComp != null && itemComp.isUsable()) {
                ui.setHoverAction(actions.createAction(item));
            }
        }
    }

    private boolean useItem() {
        if (selection == null)
            return false;

        int index = slots.indexOf(selection);
        InventoryComp invComp = mgr.comp(player, InventoryComp.class);

        if (index != -1) {
            String item = invComp.getItem(index);
            ItemComp itemComp = mgr.comp(item, ItemComp.class);

            if (itemComp != null && itemComp.isUsable()) {
                TypeComp typeComp = mgr.comp(item, TypeComp.class);
                InfoComp infoComp = mgr.comp(item, InfoComp.class);

                inventory.useItem(player, item, typeComp.getType());

                setItemName(infoComp.getName());
                setItemDesc(infoComp.getDesc());
                setItemQualAndCond(itemComp.getQuality(), itemComp.getCondition());
                setItemValue(itemComp.getValue());
                setItemWeight(itemComp.getWeight());

                return true;
            }
        }

        return false;
    }

    private void enterSlot(ImageButton slot) {
        int index = getSlotIndex(slot);
        InventoryComp invComp = mgr.comp(player, InventoryComp.class);
        ItemComp itemComp = mgr.comp(invComp.getItem(index), ItemComp.class);

        if(selection != null) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());

            if (itemComp != null && itemComp.isEquipped()) {
                style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSlotEquipped"));
            } else {
                style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSlot"));
            }

            selection.setStyle(style);
        }

        selection = slot;

        index = getSlotIndex(slot);
        itemComp = mgr.comp(invComp.getItem(index), ItemComp.class);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());

        if (itemComp != null && itemComp.isEquipped()) {
            style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSelectionEquipped"));
        } else {
            style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSelection"));
        }

        selection.setStyle(style);
    }

    private void exitTable() {
        if (noExit) {
            noExit = false;
        } else {
            if (selection != null) {
                int index = getSlotIndex(selection);
                InventoryComp invComp = mgr.comp(player, InventoryComp.class);
                ItemComp itemComp = mgr.comp(invComp.getItem(index), ItemComp.class);

                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());

                if (itemComp != null && itemComp.isEquipped()) {
                    style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSlotEquipped"));
                } else {
                    style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/invSlot"));
                }

                selection.setStyle(style);

                selection = null;
            }
        }
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void resetInfo() {
        setItemName("");
        setItemQualAndCond(-1, -1);
        setItemValue(-1);
        setItemWeight(-1);
        setItemDesc("");
    }

    public void update() {
        if(active) {
            if(selection != prevSelection) {
                if(selection != null) {
                    InventoryComp invComp = mgr.comp(player, InventoryComp.class);
                    int index = slots.indexOf(selection);

                    if (index != -1) {
                        String item = invComp.getItem(index);

                        if (item != null && !item.equals("")) {
                            ItemComp itemComp = mgr.comp(item, ItemComp.class);
                            InfoComp infoComp = mgr.comp(item, InfoComp.class);

                            setItemName(infoComp.getName());
                            setItemQualAndCond(itemComp.getQuality(), itemComp.getCondition());
                            setItemValue(itemComp.getValue());
                            setItemWeight(itemComp.getWeight());
                            setItemDesc(infoComp.getDesc());
                        } else {
                            resetInfo();
                        }
                    }
                } else {
                    resetInfo();
                }
            }
            prevSelection = selection;
        }
    }

    private void setItemName(String name) {
        if (name.equals("")) {
            itemName.setText("");
        } else {
            itemName.setText(StringUtils.capitalize(name));
        }
    }

    private void setItemDesc(String desc) {
        if (desc.equals("")) {
            itemDesc.setText("");
        } else {
            itemDesc.setText("  " + desc);
        }
    }

    private void setItemQualAndCond(float quality, float condition) {
        if(quality == -1 && condition == -1) {
            itemQualityDur.setText("");
        } else {
            itemQualityDur.setText(String.format("Q-%d C-%d", (int)(quality * 100), (int)(condition * 100)));
        }
    }

    private void setItemValue(float value) {
        if(value == -1) {
            itemValue.setText("");
        } else {
            itemValue.setText(String.format("$%.2f", value));
        }
    }

    private void setItemWeight(float weight) {
        if(weight == -1) {
            itemWeight.setText("");
        } else {
            itemWeight.setText(String.format("%.2fkg", weight));
        }
    }

    public void setPrevSelection(ImageButton prevSelection) {
        this.prevSelection = prevSelection;
    }

    public Table getTable() {
        return table;
    }

    public ImageButton getSlot(int i) {
        return slots.get(i);
    }

    public int getSlotIndex(ImageButton slot) {
        return slots.indexOf(slot);
    }

    public ImageButton getSelection() {
        return selection;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNoExit(boolean noExit) {
        this.noExit = noExit;
    }
}
