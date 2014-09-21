package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

import java.util.ArrayList;

public class UIInventorySystem extends GameSystem {
    private boolean active = false, noExit = false;
    private ImageButton selection, prevSelection;
    private Manager mgr;
    private Window window;
    private Table table;
    private Skin skin;
    private String player;
    private TextureAtlas atlas;
    private InventorySystem inventory;
    private ArrayList<ImageButton> slots;

    private Label itemName, itemDesc, itemValue, itemWeight, itemQualityDur;

    public UIInventorySystem(Manager mgr, InventorySystem inventory, String player, TextureAtlas atlas, Skin skin, Window window) {
        this.mgr = mgr;
        this.inventory = inventory;
        this.atlas = atlas;
        this.skin = skin;
        this.player = player;
        this.window = window;

        setup();
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
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
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
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    noExit = true;
                    useItem();
                }
            });

            if(i % 8 == 0) {
                table.add(slot).pad(0).row();
            } else {
                table.add(slot).pad(0);
            }
        }
    }

    private void enterSlot(ImageButton slot) {
        if(selection != null) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
            style.up = new TextureRegionDrawable(atlas.findRegion("ui/invSlot"));
            selection.setStyle(style);
        }

        selection = slot;

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
        style.up = new TextureRegionDrawable(atlas.findRegion("ui/invSelection"));
        selection.setStyle(style);
    }

    private boolean useItem() {
        if(selection == null) return false;

        int index = slots.indexOf(selection);
        InventoryComp invComp = mgr.comp(player, InventoryComp.class);

        if(index != -1) {
            String item = invComp.getItem(index);
            ItemComp itemComp = mgr.comp(item, ItemComp.class);

            if(itemComp != null && itemComp.isUsable()) {
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

    private void setItemName(String name) {
        itemName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
    }

    private void setItemDesc(String desc) {
        itemDesc.setText(desc);
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
            itemValue.setText("");
        } else {
            itemValue.setText(String.format("$%.2f", weight));
        }
    }

    private void exitTable() {
        if(noExit) {
            noExit = false;
        } else {
            if(selection != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
                style.up = new TextureRegionDrawable(atlas.findRegion("ui/invSlot"));
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

    public Table getTable() {
        return table;
    }

    public void update() {
        if(active) {
            if(selection != prevSelection) {
                if(selection != null) {
                    InventoryComp invComp = mgr.comp(player, InventoryComp.class);
                    int index = slots.indexOf(selection);

                    if (index != -1) {
                        String item = invComp.getItem(index);

                        if (!item.equals("")) {
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
        }
        prevSelection = selection;
    }

    public ImageButton getPrevSelection() {
        return prevSelection;
    }

    public void setPrevSelection(ImageButton prevSelection) {
        this.prevSelection = prevSelection;
    }

    private void resetInfo() {
        setItemName("");
        setItemQualAndCond(-1, -1);
        setItemValue(-1);
        setItemWeight(-1);
        setItemDesc("");
    }
}
