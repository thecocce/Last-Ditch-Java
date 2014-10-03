package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ArraySelection;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UIActionsSystem extends GameSystem {
    private boolean active = false;
    private int curIndex;
    private Manager mgr;
    private CraftingSystem crafting;
    private Window window;
    private ScrollPane scrollpane;
    private SplitPane split;
    private ActionsSystem actions;
    private InventorySystem inventory;
    private List<String> craftingList, objectList;
    private Label craftingNameLabel, craftablesLabel, stationIdentifierLabel, stationLabel;
    private Table table, actionsListTable, craftingInfoTable, objectInfoTable;
    private Button craftablesLeftArrowButton, craftablesRightArrowButton;
    private TextButton craftingButton, objectButton;
    private ArrayList<Label> reqsAndIngsLabelList = new ArrayList<Label>();

    private boolean recipeCheck = false;
    private String focus = "crafting", prevSelection;
    private int numOfCraftables = 0, totNumOfCraftables = 0;

    public UIActionsSystem(Manager mgr, ActionsSystem actions, CraftingSystem crafting, InventorySystem inventory, Window window) {
        this.mgr = mgr;
        this.actions = actions;
        this.crafting = crafting;
        this.inventory = inventory;
        this.window = window;

        table = new Table();
        table.setPosition(128, 342);
        table.setSize(548, 254);

        actionsListTable = new Table();
        actionsListTable.align(Align.left | Align.top);

        setupMainButtons();
        setupCraftingInfo();
        setupObjectInfo();
        setupCraftingList();
        setupObjectList();
        setupScrollpane();

        if(1 == 0) {
            table.setDebug(true);
            actionsListTable.setDebug(true);
            craftingInfoTable.setDebug(true);
            objectInfoTable.setDebug(true);
        }
    }

    public void setupMainButtons() {
        craftingButton = new TextButton("Crafting", mgr.getSkin(), "actionsButton");
        craftingButton.setChecked(true);

        craftingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("crafting");
            }
        });

        objectButton = new TextButton("Object", mgr.getSkin(), "actionsButton");

        objectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("object");
            }
        });

        actionsListTable.add(craftingButton).height(15).padRight(9).padTop(3);
        actionsListTable.add(objectButton).height(15).padRight(130).padTop(3).row();
    }

    public void setupCraftingInfo() {
        craftingInfoTable = new Table();
        craftingInfoTable.align(Align.left | Align.top);

        craftingNameLabel = new Label("Name:", mgr.getSkin(), "actionsTitle");
        craftablesLeftArrowButton = new Button(mgr.getSkin(), "actionsLeftArrowButton");

        craftablesLeftArrowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeNumOfCraftables(-1);
            }
        });

        craftablesLabel = new Label("", mgr.getSkin(), "actions");
        craftablesLabel.setAlignment(Align.center);
        craftablesRightArrowButton = new Button(mgr.getSkin(), "actionsRightArrowButton");

        craftablesRightArrowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeNumOfCraftables(1);
            }
        });

        stationIdentifierLabel = new Label("  Station: ", mgr.getSkin(), "actions");
        stationLabel = new Label("", mgr.getSkin(), "actions");
        stationLabel.setAlignment(Align.left);

        craftingInfoTable.add(craftingNameLabel).width(190).padLeft(8).align(Align.left);
        craftingInfoTable.add(craftablesLeftArrowButton).width(16).padRight(0);
        craftingInfoTable.add(craftablesRightArrowButton).width(26).align(Align.center).row();
        craftingInfoTable.add(stationIdentifierLabel).width(50).padLeft(8).align(Align.left).colspan(2);
        craftingInfoTable.add(stationLabel).width(208).padLeft(-153).align(Align.left).colspan(2).row();

        reqsAndIngsLabelList = new ArrayList<Label>();

        for(int i = 0; i < 10; i++) {
            Label newReq = new Label("", mgr.getSkin(), "actions");
            newReq.setColor(Color.GRAY);

            reqsAndIngsLabelList.add(newReq);
            craftingInfoTable.add(newReq).padLeft(8).align(Align.left).colspan(4).row();
        }
    }

    public void setupObjectInfo() {
        objectInfoTable = new Table();
        objectInfoTable.align(Align.left | Align.top);
    }

    public void setupCraftingList() {
        craftingList = new List<String>(mgr.getSkin(), "actions");
        craftingList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listItemChanged();
            }
        });

        Array<String> craftingItems = new Array<String>();

        for(Map.Entry<String, String> entry : crafting.getRecipes().entrySet()) {
            InfoComp infoComp = mgr.comp(entry.getValue(), InfoComp.class);
            craftingItems.add(infoComp.getName());
        }

        craftingList.setItems(craftingItems);
    }

    private void listItemChanged() {
        if(focus.equals("crafting")) {
            updateCraftingInfo();

            if(totNumOfCraftables > 0) {
                numOfCraftables = totNumOfCraftables;
            }

            String curSelection = craftingList.getSelection().first();

            if(recipeCheck && curSelection.equals(prevSelection)) {
                deactivate();
                // Skill test activate
            }

            prevSelection = curSelection;
        } else if(focus.equals("object")) {
            updateObjectInfo();
        }
    }

    public void setupObjectList() {
        objectList = new List<String>(mgr.getSkin(), "actions");
        objectList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listItemChanged();
            }
        });

        Array<String> objectItems = new Array<String>();
        objectItems.add("fire");
        objectItems.add("pick lock");

        objectList.setItems(objectItems);
    }

    public void setupScrollpane() {
        scrollpane = new ScrollPane(craftingList, mgr.getSkin(), "actions");
        scrollpane.setOverscroll(false, false);
        scrollpane.setFadeScrollBars(false);
        scrollpane.setFlickScroll(false);

        actionsListTable.add(scrollpane).width(264).height(202).padTop(6).colspan(2);

        split = new SplitPane(actionsListTable, craftingInfoTable, false, mgr.getSkin(), "actionsSplitPane");

        table.add(split).width(540).height(239).padTop(10);

        switchFocus("crafting");
    }

    private void switchFocus(String focus) {
        this.focus = focus;

        if(focus.equals("crafting")) {
            craftingButton.setChecked(true);
            objectButton.setChecked(false);
            scrollpane.setWidget(craftingList);
            split.setSecondWidget(craftingInfoTable);
        } else if(focus.equals("object")) {
            craftingButton.setChecked(false);
            objectButton.setChecked(true);
            scrollpane.setWidget(objectList);
            split.setSecondWidget(objectInfoTable);
        }
    }

    private void changeNumOfCraftables(int amt) {
        if(totNumOfCraftables > 0) {
            numOfCraftables += amt;

            if(numOfCraftables < 1) {
                numOfCraftables = totNumOfCraftables;
            } else if(numOfCraftables > totNumOfCraftables) {
                numOfCraftables = 1;
            }
        } else {
            numOfCraftables = 0;
        }

        updateCraftingInfo();
    }

    public void setCurAction(ArraySelection<String> selectionList) {
        for(Map.Entry<String, String> entry : crafting.getRecipes().entrySet()) {
            String recipeType = entry.getKey();
            String recipe = entry.getValue();

            InfoComp infoComp = mgr.comp(recipe, InfoComp.class);

            if (selectionList.contains(infoComp.getName())) {
                IngredientsComp ingsComp = mgr.comp(recipe, IngredientsComp.class);
                RequirementsComp reqsComp = mgr.comp(recipe, RequirementsComp.class);
                StationComp stationComp = mgr.comp(recipe, StationComp.class);

                curIndex = 0;
                recipeCheck = true;
                totNumOfCraftables = 1000;
                crafting.setCurRecipe(recipe);

                setName(infoComp.getName());
                setStation(stationComp);
                setReqs(reqsComp.getRequirements());
                setIngs(ingsComp.getIngredients());

                setNumOfCraftables();
            }
        }
    }

    public void setRecipeActive() {
        if (recipeCheck) {
            setNameHighlight(true);
        } else {
            setNameHighlight(false);
        }
    }

    public void setName(String name) {
        craftingNameLabel.setText(StringUtils.capitalize(name));
    }

    public void setNameHighlight(boolean highlight) {
        if (highlight) {
            craftingNameLabel.setColor(Color.WHITE);
        } else {
            craftingNameLabel.setColor(Color.GRAY);
        }
    }

    public void setStationHighlight(boolean highlight) {
        if (highlight) {
            stationLabel.setColor(Color.WHITE);
        } else {
            stationLabel.setColor(Color.GRAY);
        }
    }

    public void setNumOfCraftables() {
        craftablesLabel.setText(String.format(
            "%d / %d", numOfCraftables, totNumOfCraftables));
    }

    public void setStation(StationComp newStationComp) {
        if (actions.getCurStation() != null) {
            StationComp curStationComp = mgr.comp(actions.getCurStation(), StationComp.class);

            if (curStationComp != null && curStationComp.getType().equals(newStationComp.getType())) {

                Gdx.app.log("highlight", "");
                setStationHighlight(true);
            } else {
                recipeCheck = false;
                numOfCraftables = 0;
                totNumOfCraftables = 0;
                setStationHighlight(false);
            }
        } else {
            recipeCheck = false;
            numOfCraftables = 0;
            totNumOfCraftables = 0;
            setStationHighlight(false);
        }
        stationLabel.setText(WordUtils.capitalize(newStationComp.getName()));
    }

    public void setReqs(HashMap<String, Float> reqs) {
        SkillsComp skillsComp = mgr.comp(mgr.getPlayer(), SkillsComp.class);

        Map<String, Object> skillsData = mgr.getData("skills");

        reqsAndIngsLabelList.get(curIndex).setText("  Skills:");
        reqsAndIngsLabelList.get(curIndex).setColor(Color.WHITE);

        curIndex += 1;

        for(Map.Entry<String, Float> entry : reqs.entrySet()) {
            String req = entry.getKey();

            @SuppressWarnings("unchecked")
            Map<String, Object> skillData = (Map<String, Object>)skillsData.get(req);

            String skillName = (String)skillData.get("name");
            int displayLvl = (int)(entry.getValue() * 100);
            int skillLvl = (int)(skillsComp.getLevel(req) * 100);

            if (skillLvl < displayLvl) {
                recipeCheck = false;
                numOfCraftables = 0;
                totNumOfCraftables = 0;
                reqsAndIngsLabelList.get(curIndex).setColor(Color.GRAY);
            } else {
                reqsAndIngsLabelList.get(curIndex).setColor(Color.WHITE);
            }

            String txt = String.format("    - %s %d / %d", skillName, skillLvl, displayLvl);
            reqsAndIngsLabelList.get(curIndex).setText(txt);

            curIndex += 1;
        }
    }

    public void setIngs(HashMap<String, Float> ings) {
        int craftables = 0;

        reqsAndIngsLabelList.get(curIndex).setText("  Ingredients:");
        reqsAndIngsLabelList.get(curIndex).setColor(Color.WHITE);

        curIndex += 1;

        Map<String, Object> itemsData = mgr.getData("items");
        Map<String, Object> resourcesData = mgr.getData("resources");

        for (Map.Entry<String, Float> entry: ings.entrySet()) {
            String ing = entry.getKey();
            float amt = entry.getValue();

            @SuppressWarnings("unchecked")
            Map<String, Object> resData = (Map<String, Object>)resourcesData.get(ing);

            @SuppressWarnings("unchecked")
            Map<String, Object> itemData = (Map<String, Object>)itemsData.get(ing);

            if (resData != null) {
                String resName = (String)resData.get("name");

                if (actions.getCurStation() != null) {
                    ResourcesComp resComp = mgr.comp(actions.getCurStation(), ResourcesComp.class);

                    float resAmt = resComp.getAmount(ing);

                    if (resAmt < amt) {
                        craftables = 0;
                        recipeCheck = false;
                        reqsAndIngsLabelList.get(curIndex).setColor(Color.GRAY);
                    } else {
                        craftables = (int)(resAmt / amt);
                        reqsAndIngsLabelList.get(curIndex).setColor(Color.WHITE);
                    }

                    if (craftables < totNumOfCraftables) {
                        totNumOfCraftables = craftables;
                    }

                    String txt = String.format(
                        "    - %s %.1f / %.1f", resName, resAmt, amt);

                    reqsAndIngsLabelList.get(curIndex).setText(txt);
                } else {
                    String txt = String.format(
                        "    - %s 0.0 / %.1f", resName, amt);

                    reqsAndIngsLabelList.get(curIndex).setText(txt);
                    reqsAndIngsLabelList.get(curIndex).setColor(Color.GRAY);
                }
            } else if (itemData != null) {
                String itemName = (String)itemData.get("name");
                int itemAmt = inventory.itemCount(mgr.getPlayer(), ing);

                if (itemAmt < amt) {
                    craftables = 0;
                    recipeCheck = false;
                    reqsAndIngsLabelList.get(curIndex).setColor(Color.GRAY);
                } else {
                    craftables = (int)(itemAmt / amt);
                    reqsAndIngsLabelList.get(curIndex).setColor(Color.WHITE);
                }

                if (craftables < totNumOfCraftables) {
                    totNumOfCraftables = craftables;
                }

                String txt = String.format(
                    "    - %s %d / %d", itemName, itemAmt, (int)amt);

                reqsAndIngsLabelList.get(curIndex).setText(txt);
            }

            if (totNumOfCraftables > 0 && numOfCraftables == 0) {
                numOfCraftables = totNumOfCraftables;
            }

            if (numOfCraftables > totNumOfCraftables) {
                numOfCraftables = totNumOfCraftables;
            }

            curIndex += 1;
        }

        while (curIndex < 10) {
            reqsAndIngsLabelList.get(curIndex).setText("");
            curIndex += 1;
        }
    }

    public void update() {

    }

    public void activate() {
        active = true;

        updateCraftingInfo();

        if(totNumOfCraftables > 0) {
            numOfCraftables = totNumOfCraftables;
        }
    }

    public void deactivate() {
        active = false;
    }

    public void updateCraftingInfo() {
        setStationHighlight(false);
        ArraySelection<String> itemSelection = craftingList.getSelection();
        setCurAction(itemSelection);
        setRecipeActive();
    }

    private void updateObjectInfo() {

    }

    public Table getTable() {
        return table;
    }


}
