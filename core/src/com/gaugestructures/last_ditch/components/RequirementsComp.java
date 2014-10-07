package com.gaugestructures.last_ditch.components;

import java.util.HashMap;
import java.util.Map;

public class RequirementsComp extends GameComponent {
    private Map<String, Float> requirements = new HashMap<String, Float>();

    public RequirementsComp(Map<String, Object> reqs) {
        for(Map.Entry<String, Object> entry : reqs.entrySet()) {
            Double lvl = (Double)entry.getValue();
            requirements.put(entry.getKey(), lvl.floatValue());
        }
    }

    public Map<String, Float> getRequirements() {
        return requirements;
    }
}
