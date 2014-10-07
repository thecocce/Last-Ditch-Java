package com.gaugestructures.last_ditch.components;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillsComp extends GameComponent {
    private HashMap<String, Float> skills = new HashMap<String, Float>();

    public SkillsComp() {
        Yaml yaml = new Yaml();

        try {
            InputStream input = new FileInputStream(
                new File("../src/com/gaugestructures/last_ditch/cfg/skills.yml"));

            @SuppressWarnings("unchecked")
            Map<String, Object> skillData = (Map<String, Object>) yaml.load(input);

            @SuppressWarnings("unchecked")
            List<String> skillList = (List<String>)skillData.get("skillList");

            for (String skill : skillList) {
                skills.put(skill, 0.12f);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public float getLevel(String skill) {
        return skills.get(skill);
    }

    public void setLevel(String skill, float lvl) {
        skills.put(skill, lvl);
    }
}
