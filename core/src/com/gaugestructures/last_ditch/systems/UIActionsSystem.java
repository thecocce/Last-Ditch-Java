package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.InfoComp;

import java.util.ArrayList;
import java.util.Map;

public class UIActionsSystem extends GameSystem {
    private boolean active = false;
    private Skin skin;
    private Manager mgr;
    private CraftingSystem crafting;
    private Window window;
    private ScrollPane scrollpane;
    private SplitPane split;
    private List<String> craftingList, objectList;
    private Label craftingNameLabel, craftablesLabel, stationIdentifierLabel, stationLabel;
    private Table table, actionsListTable, craftingInfoTable, objectInfoTable;
    private Button craftablesLeftArrowButton, craftablesRightArrowButton;
    private TextButton craftingButton, objectButton;
    private ArrayList<Label> reqsAndIngsLabelList = new ArrayList<Label>();

    private int curIndex = 0;
    private boolean recipeCheck = false;
    private String focus = "crafting", prevSelection;
    private int numOfCraftables = 0, totNumOfCraftables = 0;

    public UIActionsSystem(Manager mgr, CraftingSystem crafting, Skin skin, Window window) {
        this.mgr = mgr;
        this.skin = skin;
        this.crafting = crafting;
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
        craftingButton = new TextButton("Crafting", skin, "actionsButton");
        craftingButton.setChecked(true);

        craftingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                switchFocus("crafting");
            }
        });

        objectButton = new TextButton("Object", skin, "actionsButton");

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

        craftingNameLabel = new Label("Name:", skin, "actionsTitle");
        craftablesLeftArrowButton = new Button(skin, "actionsLeftArrowButton");

        craftablesLeftArrowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeNumOfCraftables(-1);
            }
        });

        craftablesLabel = new Label("", skin, "actions");
        craftablesLabel.setAlignment(Align.center);
        craftablesRightArrowButton = new Button(skin, "actionsRightArrowButton");

        craftablesRightArrowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeNumOfCraftables(1);
            }
        });

        stationIdentifierLabel = new Label("  Station: ", skin, "actions");
        stationLabel = new Label("", skin, "actions");
        stationLabel.setAlignment(Align.left);

        craftingInfoTable.add(craftingNameLabel).width(190).padLeft(8).align(Align.left);
        craftingInfoTable.add(craftablesLeftArrowButton).width(16).padRight(0);
        craftingInfoTable.add(craftablesRightArrowButton).width(26).align(Align.center).row();
        craftingInfoTable.add(stationIdentifierLabel).width(50).padLeft(8).align(Align.left).colspan(2);
        craftingInfoTable.add(stationLabel).width(208).padLeft(-153).align(Align.left).colspan(2).row();

        reqsAndIngsLabelList = new ArrayList<Label>();

        for(int i = 0; i < 10; i++) {
            Label newReq = new Label("", skin, "actions");
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
        craftingList = new List<String>(skin, "actions");
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
        objectList = new List<String>(skin, "actions");
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
        scrollpane = new ScrollPane(craftingList, skin, "actions");
        scrollpane.setOverscroll(false, false);
        scrollpane.setFadeScrollBars(false);
        scrollpane.setFlickScroll(false);

        actionsListTable.add(scrollpane).width(264).height(202).padTop(6).colspan(2);

        split = new SplitPane(actionsListTable, craftingInfoTable, false, skin, "actionsSplitPane");

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

    }

    private void updateObjectInfo() {

    }

    public Table getTable() {
        return table;
    }


}
