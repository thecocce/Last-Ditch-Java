package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.InventoryComp;
import com.gaugestructures.last_ditch.components.NeedsComp;
import com.gaugestructures.last_ditch.components.RenderComp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UIBaseSystem extends GameSystem {
    private boolean active = true, noExit = false;

    private Manager mgr;
    private Skin skin;
    private TextureAtlas atlas;
    private TimeSystem time;
    private ActionsSystem actions;
    private UISystem ui;
    private Stage stage;
    private ImageButton selection;
    private ImageButton hungerBar, thirstBar, energyBar, sanityBar;
    private Table tableInfo, tableNeeds, tableSlots;
    private Label timeLabel, dateLabel, moneyLabel, weightLabel;
    private ArrayList<ImageButton> slots = new ArrayList<ImageButton>();
    private Map<ImageButton, String> baseActions = new HashMap<ImageButton, String>();

    public UIBaseSystem(Manager mgr, Stage stage, ActionsSystem actions, UISystem ui) {
        this.mgr = mgr;
        this.ui = ui;
        this.stage = stage;
        this.time = mgr.getTime();
        this.actions = actions;

        atlas = mgr.getAtlas();
        skin = mgr.getSkin();

        setup();

        if (1 == 0) {
            tableInfo.debug();
            tableNeeds.debug();
            tableSlots.debug();
        }
    }

    private void setup() {
        int w = 62, h = 42;

        tableInfo = new Table(skin);
        tableInfo.setBounds(C.WIDTH - w, C.HEIGHT - h, w, h);

        timeLabel = new Label("", skin, "baseUI");
        dateLabel = new Label("", skin, "baseUI");
        moneyLabel = new Label("", skin, "baseUI");
        weightLabel = new Label("", skin, "baseUI");

        moneyLabel.setAlignment(Align.right);
        moneyLabel.setColor(0.75f, 0.82f, 0.7f, 1f);
        weightLabel.setAlignment(Align.right);
        weightLabel.setColor(0.75f, 0.75f, 0.89f, 1f);

        tableInfo.add(dateLabel).align(Align.right).height(11).row();
        tableInfo.add(timeLabel).align(Align.right).height(11).row();
        tableInfo.add(weightLabel).align(Align.right).height(11).row();
        tableInfo.add(moneyLabel).align(Align.right).height(11);

        tableNeeds = new Table(skin);
        tableNeeds.setBounds(-3, C.HEIGHT - 29, 106, 30);

        hungerBar = new ImageButton(skin, "statusBars");
        thirstBar = new ImageButton(skin, "statusBars");
        energyBar = new ImageButton(skin, "statusBars");
        sanityBar = new ImageButton(skin, "statusBars");

        hungerBar.setColor(0.94f, 0.35f, 0.34f, 1f);
        thirstBar.setColor(0.07f, 0.86f, 0.86f, 1f);
        energyBar.setColor(0.98f, 0.98f, 0.04f, 1f);
        sanityBar.setColor(0.77f, 0.1f, 0.87f, 1f);

        tableNeeds.add(hungerBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(thirstBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(energyBar).width(106).padTop(0).height(7).row();
        tableNeeds.add(sanityBar).width(106).padTop(0).height(7).row();

        tableSlots = new Table(skin);
        tableSlots.setBounds(0, 0, C.WIDTH, 32);
        tableSlots.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                exitWindow();
            }
        });

        for (int i = 1; i <= C.BASE_SLOTS; i++) {
            final ImageButton slot = new ImageButton(skin, "baseSlot");

            slots.add(slot);

            slot.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    enterSlot(slot);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    noExit = true;

                    if (button == 0) {
                        String action = baseActions.get(slot);

                        if (action != null) {
                            actions.initiateAction(action);
                        } else if (ui.getActiveIcon() != null) {
                            setSlotAction(slot, ui.getActiveAction());
                        }
                    } else if (button == 1) {
                        setSlotAction(slot, null);
                    }
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
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

    private void setSlotAction(ImageButton slot, String action) {
        RenderComp renderComp = mgr.comp(action, RenderComp.class);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(slot.getStyle());

        if (action != null) {
            baseActions.put(slot, action);
            style.imageUp = new TextureRegionDrawable(renderComp.getRegion());
        } else {
            baseActions.remove(slot);
            style.imageUp = new TextureRegionDrawable(atlas.findRegion("environ/empty"));
        }

        slot.setStyle(style);

        ui.setHoverAction(null);
    }

    private void enterSlot(ImageButton slot) {
        if (selection != null) {
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
            style.up = new TextureRegionDrawable(atlas.findRegion("ui/baseSlot"));
            selection.setStyle(style);
        }

        selection = slot;

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
        style.up = new TextureRegionDrawable(atlas.findRegion("ui/baseSelection"));
        selection.setStyle(style);
    }

    private void exitWindow() {
        if (noExit) {
            noExit = false;
        } else {
            if (selection != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(selection.getStyle());
                style.up = new TextureRegionDrawable(atlas.findRegion("ui/baseSlot"));
                selection.setStyle(style);

                selection = null;
            }
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
