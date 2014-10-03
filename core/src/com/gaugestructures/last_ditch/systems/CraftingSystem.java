package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.HashMap;
import java.util.Map;

public class CraftingSystem extends GameSystem {
    private Manager mgr;
    private boolean active = false;
    private String curRecipe;
    private Map<String, Object> recipeData;
    private HashMap<String, String> recipes = new HashMap<String, String>();

    public CraftingSystem(Manager mgr) {
        this.mgr = mgr;

        recipeData = mgr.getData("recipes");

        for(Map.Entry<String, Object> entry : recipeData.entrySet()) {
            if(!entry.getKey().equals("recipeList")) {
                String recipe = mgr.createEntity();

                @SuppressWarnings("unchecked")
                Map<String, Object> recipeInfo = (Map<String, Object>)entry.getValue();

                InfoComp infoComp = mgr.addComp(recipe, new InfoComp(""));
                infoComp.setName((String)recipeInfo.get("name"));
                infoComp.setDesc((String)recipeInfo.get("desc"));

                mgr.addComp(recipe, new TypeComp(entry.getKey()));
                mgr.addComp(recipe, new StationComp((String)recipeInfo.get("station")));

                @SuppressWarnings("unchecked")
                IngredientsComp ingComp = new IngredientsComp((HashMap<String, Object>)recipeInfo.get("ingredients"));
                mgr.addComp(recipe, ingComp);

                @SuppressWarnings("unchecked")
                RequirementsComp reqComp = new RequirementsComp((HashMap<String, Object>)recipeInfo.get("requirements"));
                mgr.addComp(recipe, reqComp);

                recipes.put(entry.getKey(), recipe);
            }
        }
    }

    public void update() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCurRecipe() {
        return curRecipe;
    }

    public void setCurRecipe(String curRecipe) {
        this.curRecipe = curRecipe;
    }

    public HashMap<String, String> getRecipes() {
        return recipes;
    }
}
