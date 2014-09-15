package com.gaugestructures.last_ditch.components;

import java.util.HashMap;
import java.util.Map;

public class RequirementsComp extends Component {
    private HashMap<String, Float> requirements = new HashMap<String, Float>();

    public RequirementsComp(HashMap<String, Object> reqHash) {
        for(Map.Entry<String, Object> entry : reqHash.entrySet()) {
            Double lvl = (Double)entry.getValue();
            requirements.put(entry.getKey(), lvl.floatValue());
        }
    }

    public HashMap<String, Float> getRequirements() {
        return requirements;
    }
}
