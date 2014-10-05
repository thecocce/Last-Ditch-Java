package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.AttributesComp;
import com.gaugestructures.last_ditch.components.InfoComp;
import com.gaugestructures.last_ditch.components.StatsComp;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class UIStatusSystem extends GameSystem {
    private boolean active = false;
    private Manager mgr;
    private Window window;
    private Label nameLabel, occupationLabel;
    private Label dmgLabel, armorLabel;
    private Table table, tableMaleModel, tableFemaleModel, statsTable, skillsTable, attributesTable, addInfoTable;
    private Image empty;
    private Image maleLHead, maleRHead, maleLArm, maleRArm, maleLHand, maleRHand;
    private Image maleTorso, maleBelt, maleLLeg, maleRLeg, maleLFoot, maleRFoot;
    private Image femaleLHead, femaleRHead, femaleLArm, femaleRArm, femaleLHand, femaleRHand;
    private Image femaleTorso, femaleBelt, femaleLLeg, femaleRLeg, femaleLFoot, femaleRFoot;

    public UIStatusSystem(Manager mgr, Window window) {
        this.mgr = mgr;
        this.window = window;

        setupMain();
        setupModel();
        setupStats();
        setupAttributes();

        if (1 == 0) {
            table.debug();
            tableMaleModel.debug();
            tableFemaleModel.debug();
        }
    }

    private void setupMain() {
        table = new Table();
        table.setPosition(560, 44);
        table.setSize(250, 290);

        InfoComp infoComp = mgr.comp(mgr.getPlayer(), InfoComp.class);

        nameLabel = new Label(
            "Name: " + infoComp.getName(),
            mgr.getSkin(), "status");

        occupationLabel = new Label(
            "Occupation: " + infoComp.getOccupation(),
            mgr.getSkin(), "status");

        empty = new Image(mgr.getAtlas().findRegion("environ/empty"));

        table.add(nameLabel).width(246).height(14).padLeft(4).padTop(4).align(Align.left).colspan(4).row();
        table.add(occupationLabel).height(11).padLeft(4).padBottom(18).align(Align.left).colspan(4).row();
    }

    private void setupModel() {
        tableMaleModel = new Table(mgr.getSkin());

        maleLHead = new Image(mgr.getAtlas().findRegion("model/male/lHead"));
        maleRHead = new Image(mgr.getAtlas().findRegion("model/male/rHead"));
        maleLArm = new Image(mgr.getAtlas().findRegion("model/male/lArm"));
        maleTorso = new Image(mgr.getAtlas().findRegion("model/male/torso"));
        maleRArm = new Image(mgr.getAtlas().findRegion("model/male/rArm"));
        maleLHand = new Image(mgr.getAtlas().findRegion("model/male/lHand"));
        maleRHand = new Image(mgr.getAtlas().findRegion("model/male/rHand"));
        maleLLeg = new Image(mgr.getAtlas().findRegion("model/male/lLeg"));
        maleRLeg = new Image(mgr.getAtlas().findRegion("model/male/rLeg"));
        maleLFoot = new Image(mgr.getAtlas().findRegion("model/male/lFoot"));
        maleRFoot = new Image(mgr.getAtlas().findRegion("model/male/rFoot"));

        maleLHead.setColor(1f, 0.5f, 0.5f, 1f);
        maleRHead.setColor(1f, 0.5f, 0.5f, 1f);
        maleLArm.setColor(1f, 0.5f, 0.5f, 1f);
        maleTorso.setColor(1f, 0.5f, 0.5f, 1f);
        maleRArm.setColor(1f, 0.5f, 0.5f, 1f);
        maleLHand.setColor(1f, 0.5f, 0.5f, 1f);
        maleRHand.setColor(1f, 0.5f, 0.5f, 1f);
        maleLLeg.setColor(1f, 0.5f, 0.5f, 1f);
        maleRLeg.setColor(1f, 0.5f, 0.5f, 1f);
        maleLFoot.setColor(1f, 0.5f, 0.5f, 1f);
        maleRFoot.setColor(1f, 0.5f, 0.5f, 1f);

        tableMaleModel.add(empty).colspan(2);
        tableMaleModel.add(maleLHead).padLeft(57).colspan(1);
        tableMaleModel.add(maleRHead).padRight(55).colspan(1);
        tableMaleModel.add(empty).colspan(2).row();
        tableMaleModel.add(maleLHand).padRight(-4).padTop(-74).colspan(1);
        tableMaleModel.add(maleLArm).padRight(-52).padTop(-49).colspan(1);
        tableMaleModel.add(maleTorso).colspan(2);
        tableMaleModel.add(maleRArm).padLeft(-58).padTop(-48).colspan(1);
        tableMaleModel.add(maleRHand).padLeft(-18).padTop(-76).colspan(1).row();
        tableMaleModel.add(empty).colspan(2);
        tableMaleModel.add(maleLLeg).padRight(-26).colspan(1);
        tableMaleModel.add(maleRLeg).padLeft(-17).padTop(-2).colspan(1);
        tableMaleModel.add(empty).colspan(2).row();
        tableMaleModel.add(empty).colspan(2);
        tableMaleModel.add(maleLFoot).padRight(21).padTop(-9).colspan(1);
        tableMaleModel.add(maleRFoot).padLeft(30).padTop(-12).colspan(1);
        tableMaleModel.add(empty).colspan(2).row();

        tableFemaleModel = new Table(mgr.getSkin());

        femaleLHead = new Image(mgr.getAtlas().findRegion("model/female/lHead"));
        femaleRHead = new Image(mgr.getAtlas().findRegion("model/female/rHead"));
        femaleLArm = new Image(mgr.getAtlas().findRegion("model/female/lArm"));
        femaleTorso = new Image(mgr.getAtlas().findRegion("model/female/torso"));
        femaleRArm = new Image(mgr.getAtlas().findRegion("model/female/rArm"));
        femaleLHand = new Image(mgr.getAtlas().findRegion("model/female/lHand"));
        femaleRHand = new Image(mgr.getAtlas().findRegion("model/female/rHand"));
        femaleLLeg = new Image(mgr.getAtlas().findRegion("model/female/lLeg"));
        femaleRLeg = new Image(mgr.getAtlas().findRegion("model/female/rLeg"));
        femaleLFoot = new Image(mgr.getAtlas().findRegion("model/female/lFoot"));
        femaleRFoot = new Image(mgr.getAtlas().findRegion("model/female/rFoot"));

        femaleLHead.setColor(1f, 0.5f, 0.5f, 1f);
        femaleRHead.setColor(1f, 0.5f, 0.5f, 1f);
        femaleLArm.setColor(1f, 0.5f, 0.5f, 1f);
        femaleTorso.setColor(1f, 0.5f, 0.5f, 1f);
        femaleRArm.setColor(1f, 0.5f, 0.5f, 1f);
        femaleLHand.setColor(1f, 0.5f, 0.5f, 1f);
        femaleRHand.setColor(1f, 0.5f, 0.5f, 1f);
        femaleLLeg.setColor(1f, 0.5f, 0.5f, 1f);
        femaleRLeg.setColor(1f, 0.5f, 0.5f, 1f);
        femaleLFoot.setColor(1f, 0.5f, 0.5f, 1f);
        femaleRFoot.setColor(1f, 0.5f, 0.5f, 1f);

        tableFemaleModel.add(empty).colspan(2);
        tableFemaleModel.add(femaleLHead).padLeft(55).colspan(1);
        tableFemaleModel.add(femaleRHead).padRight(57).colspan(1);
        tableFemaleModel.add(empty).colspan(2).row();
        tableFemaleModel.add(femaleLHand).padRight(-8).padTop(-71).colspan(1);
        tableFemaleModel.add(femaleLArm).padRight(-56).padTop(-43).colspan(1);
        tableFemaleModel.add(femaleTorso).colspan(2);
        tableFemaleModel.add(femaleRArm).padLeft(-64).padTop(-45).colspan(1);
        tableFemaleModel.add(femaleRHand).padLeft(-21).padTop(-73).colspan(1).row();
        tableFemaleModel.add(empty).colspan(2);
        tableFemaleModel.add(femaleLLeg).padRight(-21).padTop(-3).colspan(1);
        tableFemaleModel.add(femaleRLeg).padLeft(-25).padTop(1).colspan(1);
        tableFemaleModel.add(empty).colspan(2).row();
        tableFemaleModel.add(empty).colspan(2);
        tableFemaleModel.add(femaleLFoot).padLeft(-23).padTop(-10).colspan(1);
        tableFemaleModel.add(femaleRFoot).padRight(-23).padTop(-9).colspan(1);
        tableFemaleModel.add(empty).colspan(2).row();

        table.add(tableMaleModel).width(180).height(150).align(Align.left).row();
//        table.add(tableFemaleModel).width(180).height(150).align(Align.left).row();
    }

    private void setupStats() {
        statsTable = new Table();

        dmgLabel = new Label("Dmg:", mgr.getSkin(), "status");
        armorLabel = new Label("Armor:", mgr.getSkin(), "status");

        statsTable.add(dmgLabel).row();
        statsTable.add(armorLabel);

        updateStatsList();

        table.add(statsTable).width(246).padTop(4).padLeft(4);
    }

    private void setupAttributes() {
        addInfoTable = new Table();
        attributesTable = new Table();
        skillsTable = new Table();

        updateAttributesList();

        addInfoTable.add(attributesTable);
        table.add(addInfoTable).width(100).padTop(4).padLeft(4);
    }


    public void updateStatsList() {
        StatsComp statsComp = mgr.comp(mgr.getPlayer(), StatsComp.class);

        dmgLabel.setText("Dmg: " + statsComp.getDmg());
        armorLabel.setText("Armor: " + statsComp.getArmor());
    }

    public void updateAttributesList() {
        attributesTable.clearChildren();

        AttributesComp attrComp = mgr.comp(mgr.getPlayer(), AttributesComp.class);

        Map<String, Object> attrData = mgr.getData("attributes");

        @SuppressWarnings("unchecked")
        List<String> attrList = (List<String>)attrData.get("attributeList");

        for (String attr : attrList) {
            float lvl = attrComp.getAttributes().get(attr) + attrComp.getModifiers().get(attr);

            Label attrLabel = new Label(
                String.format("%s - %.2f", StringUtils.capitalize(attr), lvl),
                mgr.getSkin(), "status");

            attributesTable.add(attrLabel).width(120).height(20).row();
        }


    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void toggleActive() {
        active = !active;
    }

    public Table getTable() {
        return table;
    }
}
