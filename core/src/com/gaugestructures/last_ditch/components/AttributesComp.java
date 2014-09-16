package com.gaugestructures.last_ditch.components;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesComp extends Component {
    private HashMap<String, Float> attributes = new HashMap<String, Float>();
    private HashMap<String, Float> modifiers = new HashMap<String, Float>();

    public AttributesComp() {
        try {
            Yaml yaml = new Yaml();

            InputStream input = new FileInputStream(new File(String.format("../src/com/gaugestructures/last_ditch/cfg/attributes.yml")));

            @SuppressWarnings("unchecked")
            Map<String, Object> attrData = (Map<String, Object>)yaml.load(input);

            @SuppressWarnings("unchecked")
            List<Object> attrList = (List<Object>)attrData.get("attributeList");

            for(Object attribute : attrList) {
                attributes.put((String)attribute, 0.1f);
                modifiers.put((String)attribute, 0f);
            }

        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public HashMap<String, Float> getAttributes() {
        return attributes;
    }

    public HashMap<String, Float> getModifiers() {
        return modifiers;
    }
}
