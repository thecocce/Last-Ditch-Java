package com.gaugestructures.last_ditch.components;

import java.util.HashMap;
import java.util.Map;

public class IngredientsComp extends Component {
    private HashMap<String, Float> ingredients = new HashMap<String, Float>();

    public IngredientsComp(HashMap<String, Object> ingHash) {
        for(Map.Entry<String, Object> entry : ingHash.entrySet()) {
            Double amt = (Double)entry.getValue();
            ingredients.put(entry.getKey(), amt.floatValue());
        }
    }

    public HashMap<String, Float> getIngredients() {
        return ingredients;
    }
}
