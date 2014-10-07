package com.gaugestructures.last_ditch.components;

import java.util.HashMap;
import java.util.Map;

public class IngredientsComp extends GameComponent {
    private Map<String, Float> ingredients = new HashMap<String, Float>();

    public IngredientsComp(Map<String, Object> ings) {
        for(Map.Entry<String, Object> entry : ings.entrySet()) {
            Double amt = (Double)entry.getValue();
            ingredients.put(entry.getKey(), amt.floatValue());
        }
    }

    public Map<String, Float> getIngredients() {
        return ingredients;
    }
}
