package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillTestSystem extends GameSystem {
    private boolean scoreFlip = true;
    private boolean active = false, testing = false;
    private Manager mgr;
    private ActionsSystem actions;
    private CraftingSystem crafting;
    private InventorySystem inventory;
    private UISystem ui;
    private UIActionsSystem uiActions;
    private int numOfItemsToCraft = 0, numOfHits = 4;
    private float scoreRange = 0.12f, scoreMinDist = 0.1f, finalScore = 0;
    private float scoreLabelHeightLimit = 0;
    private float r = 42.3f, R = 110.2f, d = 22.9f;
    private float theta = 0, dTheta = 0.01f;
    private float x = 0, y = 0, x1 = 0, y1 = 0, x2 = 0, y2 = 0;
    private ArrayList<Float> hits = new ArrayList<Float>();
    private ArrayList<Float> scores = new ArrayList<Float>();
    private ArrayList<Label> scoreLabels = new ArrayList<Label>();
    private ParticleEffect effect1 = new ParticleEffect();
    private ParticleEffect effect2 = new ParticleEffect();
    private PositionComp focus;

    public SkillTestSystem(Manager mgr, ActionsSystem actions, CraftingSystem crafting, InventorySystem inventory, UISystem ui) {
        this.mgr = mgr;
        this.actions = actions;
        this.crafting = crafting;
        this.inventory = inventory;
        this.ui = ui;
        this.uiActions = ui.getActions();
        this.uiActions.setSkillTestSystem(this);

        focus = mgr.comp(mgr.getPlayer(), PositionComp.class);

        effect1.load(
            Gdx.files.internal("fx/trails5.p"),
            Gdx.files.internal("gfx/ui"));

        effect2.load(
            Gdx.files.internal("fx/trails6.p"),
            Gdx.files.internal("gfx/ui"));

        calcHits();
    }

    private void calcHits() {
        x = 0;
        theta = 0;
        int count = 0;
        float px = 0;

        float prevHit = -(scoreMinDist + 0.1f);

        hits = new ArrayList<Float>();

        while (count < numOfHits + 2) {
            px = x;
            x = calcX(theta);

            if ((x < 0 && px >= 0) || (x > 0 && px <= 0)) {
                if (theta - prevHit > scoreMinDist) {
                    count += 1;
                    prevHit = theta;
                    hits.add(theta);
                }
            }

            theta += dTheta;
        }

        hits.remove(0);
    }

    private float calcX(float theta) {
        float dr = R - r;
        double cx = dr * Math.pow(Math.sin(theta), 2) -
            d * Math.pow(Math.sin(theta * dr / r), 2);

        return (float)cx;
    }

    private float calcY(float theta) {
        float dr = R - r;
        double cy = Math.pow(Math.cos(theta), 2) +
            d * Math.pow(Math.cos(theta * dr / r), 2);

        return (float)cy;
    }

    public void score() {
        if (testing) {
            boolean goal = false;
            float lowestDiff = 1000;

            for (float hit : hits) {
                float dif = Math.abs(theta - hit);

                if (dif < lowestDiff)
                    lowestDiff = dif;

                if (dif < scoreRange) {
                    goal = true;

                    float score = (scoreRange - dif) * (float)Math.pow(scoreRange, -1);
                    score = Math.max(-1, score);

                    scores.add(score);

                    break;
                }
            }

            if (!goal) {
                float score = (scoreRange - lowestDiff) * (float)Math.pow(scoreRange, -1);
                score = Math.max(-1, score);
                scores.add(score);
            }

            scoreFlip = !scoreFlip;
            float dx = scoreFlip ? 60 : -60;

            Label scoreLabel = new Label(
                String.format("%.2f", scores.get(scores.size() - 1)),
                mgr.getSkin(), "skillsScore");

            scoreLabel.setPosition(
                C.BTW * focus.getX() + dx,
                C.BTW * focus.getY() + 80);

            scoreLabels.add(scoreLabel);
        }
    }

    public void finalizeScore() {
        testing = false;

        numOfItemsToCraft -= 1;

        finalScore = 0;

        for (float score : scores) {
            finalScore += score;
        }
        finalScore /= hits.size() - 1;

        InventoryComp invComp = mgr.comp(mgr.getPlayer(), InventoryComp.class);
        StationComp stationComp = mgr.comp(actions.getCurStation(), StationComp.class);

        TypeComp recipeTypeComp = mgr.comp(crafting.getCurRecipe(), TypeComp.class);
        IngredientsComp ingsComp = mgr.comp(crafting.getCurRecipe(), IngredientsComp.class);

        ArrayList<Float> ingQualities = new ArrayList<Float>();
        ArrayList<Float> ingConditions = new ArrayList<Float>();

        ResourcesComp stationResComp = mgr.comp(actions.getCurStation(), ResourcesComp.class);

        for (Map.Entry<String, Float> entry : ingsComp.getIngredients().entrySet()) {
            String ingType = entry.getKey();
            float ingAmt = entry.getValue();

            if (ingType.equals("water") || ingType.equals("energy") || ingType.equals("fuel")) {
                stationResComp.changeAmount(ingType, -ingAmt);
            } else {
                List<String> removedItems;
                removedItems = inventory.removeItemsByType(
                    invComp, ingType, (int)ingAmt);

                for (String item : removedItems) {
                    ItemComp itemComp = mgr.comp(item, ItemComp.class);

                    ingQualities.add(itemComp.getQuality());
                    ingConditions.add(itemComp.getCondition());
                }
            }
        }

        float avgQuality = 0;

        for (float quality : ingQualities) {
            avgQuality += quality;
        }

        avgQuality /= ingQualities.size();

        float avgCondition = 0;

        for (float condition : ingConditions) {
            avgCondition += condition;
        }

        avgCondition /= ingConditions.size();

        String recipeType = recipeTypeComp.getType();

        if (recipeType.equals("water")) {
            stationResComp.changeAmount("water", finalScore);
        } else if (recipeType.equals("energy1") || recipeType.equals("energy2")) {
            stationResComp.changeAmount("energy", finalScore);
        } else {
            String item = inventory.createInvItem(recipeType);

            ItemComp itemComp = mgr.comp(item, ItemComp.class);
            itemComp.setQuality(avgQuality * 1.3f * finalScore);
            itemComp.setCondition(avgCondition);

            inventory.addItem(invComp, item);
        }
    }

    public void activate() {
        reset();

        active = true;
        testing = true;
        numOfItemsToCraft = uiActions.getNumOfCraftables();
    }

    public void deactivate() {
        reset();

        active = false;
        testing = false;
    }

    public void reset() {
        theta = 0;

        effect1.reset();
        effect2.reset();

        scores = new ArrayList<Float>();
        scoreLabels = new ArrayList<Label>();
        scoreLabelHeightLimit = C.BTW * focus.getY() + 260;

        calcHits();

        theta = 0;
    }

    public void update() {
        if (active) {
            if (testing) {
                if (theta < hits.get(hits.size() - 1)) {
                    effect1.update(C.BOX_STEP);
                    effect2.update(C.BOX_STEP);

                    x = C.BTW * focus.getX();
                    y = C.BTW * focus.getY();

                    x1 = calcX(theta);
                    y1 = calcY(theta);
                    x2 = -x1;
                    y2 = y1;

                    theta += dTheta;

                    effect1.setPosition(x + x1, y + y1 + 140);
                    effect2.setPosition(x + x2, y + y2 + 140);
                } else {
                    finalizeScore();

                    Label finalScoreLabel = new Label(
                        String.format("Final: %.2f", finalScore),
                        mgr.getSkin(), "skillsScore");

                    finalScoreLabel.setPosition(
                        C.BTW * focus.getX(),
                        C.BTW * focus.getY() + 160);

                    scoreLabels.add(finalScoreLabel);
                }
            } else {
                if (scoreLabels.isEmpty()) {
                    if (numOfItemsToCraft > 0) {
                        reset();
                        testing = true;
                    } else {
                        active = false;
                        mgr.setPaused(false);
                        mgr.getTime().setActive(true);

                        ui.deactivate();
                    }
                }
            }

            ArrayList<Label> labelsToRemove = new ArrayList<Label>();

            for (Label label : scoreLabels) {
                if (label.getY() > scoreLabelHeightLimit) {
                    labelsToRemove.add(label);
                    continue;
                }

                if (label.getY() > scoreLabelHeightLimit - 20) {
                    label.setColor(
                        label.getColor().r,
                        label.getColor().g,
                        label.getColor().b,
                        label.getColor().a - 0.05f);
                }

                label.setPosition(label.getX(), label.getY() + 1);
            }

            scoreLabels.removeAll(labelsToRemove);
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.begin();

            if (testing) {
                effect1.draw(batch);
                effect2.draw(batch);
            }

            for (Label label : scoreLabels) {
                label.draw(batch, 1);
            }

            batch.end();
        }
    }

    public boolean isActive() {
        return active;
    }
}
