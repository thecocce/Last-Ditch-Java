package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.InventoryComp;
import com.gaugestructures.last_ditch.components.NeedsComp;

import java.util.ArrayList;

public class UIBaseSystem extends GameSystem {
    private boolean active = true, noExit = false;
    private Manager mgr;
    private Stage stage;
    private TimeSystem time;
    private Table tableInfo, tableNeeds, tableSlots;
    private Label timeLabel, dateLabel, moneyLabel, weightLabel;
    private ImageButton selection;
    private ImageButton hungerBar, thirstBar, energyBar, sanityBar;
    private ArrayList<ImageButton> slots = new ArrayList<ImageButton>();

    public UIBaseSystem(Manager mgr, Stage stage, TimeSystem time) {
        this.mgr = mgr;
        this.time = time;
        this.stage = stage;

        setup();

        if (1 == 0) {
            tableInfo.debug();
            tableNeeds.debug();
            tableSlots.debug();
        }
    }

    private void setup() {
        int w = 62, h = 42;

        tableInfo = new Table(mgr.getSkin());
        tableInfo.setBounds(C.WIDTH - w, C.HEIGHT - h, w, h);

        timeLabel = new Label("", mgr.getSkin(), "baseUI");
        dateLabel = new Label("", mgr.getSkin(), "baseUI");
        moneyLabel = new Label("", mgr.getSkin(), "baseUI");
        weightLabel = new Label("", mgr.getSkin(), "baseUI");

        moneyLabel.setAlignment(Align.right);
        moneyLabel.setColor(0.75f, 0.82f, 0.7f, 1f);
        weightLabel.setAlignment(Align.right);
        weightLabel.setColor(0.75f, 0.75f, 0.89f, 1f);

        tableInfo.add(dateLabel).align(Align.right).height(11).row();
        tableInfo.add(timeLabel).align(Align.right).height(11).row();
        tableInfo.add(weightLabel).align(Align.right).height(11).row();
        tableInfo.add(moneyLabel).align(Align.right).height(11);

        tableNeeds = new Table(mgr.getSkin());
        tableNeeds.setBounds(-3, C.HEIGHT - 29, 106, 30);

        hungerBar = new ImageButton(mgr.getSkin(), "statusBars");
        thirstBar = new ImageButton(mgr.getSkin(), "statusBars");
        energyBar = new ImageButton(mgr.getSkin(), "statusBars");
        sanityBar = new ImageButton(mgr.getSkin(), "statusBars");

        hungerBar.setColor(0.94f, 0.35f, 0.34f, 1f);
        thirstBar.setColor(0.07f, 0.86f, 0.86f, 1f);
        energyBar.setColor(0.98f, 0.98f, 0.04f, 1f);
        sanityBar.setColor(0.77f, 0.1f, 0.87f, 1f);

        tableNeeds.add(hungerBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(thirstBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(energyBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(sanityBar).width(106).padTop(0).height(7).row();

        tableSlots = new Table(mgr.getSkin());
        tableSlots.setBounds(0, 0, C.WIDTH, 32);
        tableSlots.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                exitWindow();
            }
        });

        for (int i = 1; i <= C.BASE_SLOTS; i++) {
            final ImageButton slot = new ImageButton(mgr.getSkin(), "baseSlot");

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
                }
            });

            if (i < 9) {
                tableSlots.add(slot).align(Align.left);
            } else if (i == 9) {
                tableSlots.add(slot).align(Align.right).padLeft(286);
            } else {
                tableSlots.add(slot).align(Align.right);
            }

            stage.addActor(tableInfo);
            stage.addActor(tableNeeds);
            stage.addActor(tableSlots);
        }

    }

    private void enterSlot(ImageButton slot) {
        if (selection != null) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
            style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/baseSlot"));
            selection.setStyle(style);
        }

        selection = slot;

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
        style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/baseSelection"));
        selection.setStyle(style);
    }

    private void exitWindow() {
        if (noExit) {
            noExit = false;
        } else {
            if (selection != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
                style.up = new TextureRegionDrawable(mgr.getAtlas().findRegion("ui/baseSlot"));
                selection.setStyle(style);

                selection = null;
            }
        }
    }

    public void update() {
        if (active) {
            NeedsComp needsComp = mgr.comp(mgr.getPlayer(), NeedsComp.class);
            InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);

            timeLabel.setText(time.getTime());
            dateLabel.setText(time.getDate());
            moneyLabel.setText(String.format("$%.2f", invComp.getMoney()));
            weightLabel.setText(String.format("%.2fkg", invComp.getWeight()));

            hungerBar.setWidth((int)(needsComp.getHunger() * 100 + 4));
            thirstBar.setWidth((int)(needsComp.getThirst() * 100 + 4));
            energyBar.setWidth((int)(needsComp.getEnergy() * 100 + 4));
            sanityBar.setWidth((int)(needsComp.getSanity() * 100 + 4));
        }
    }

    public void toggleActive() {
        active = !active;

        if(active) {
            stage.addActor(tableInfo);
            stage.addActor(tableNeeds);
            stage.addActor(tableSlots);
        } else {
            tableInfo.remove();
            tableNeeds.remove();
            tableSlots.remove();
        }
    }

    public void activate() {
        active = true;

        stage.addActor(tableInfo);
        stage.addActor(tableNeeds);
        stage.addActor(tableSlots);
    }

    public void deactivate() {
        active = false;

        tableInfo.remove();
        tableNeeds.remove();
        tableSlots.remove();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isNoExit() {
        return noExit;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNoExit(boolean noExit) {
        this.noExit = noExit;
    }
}
