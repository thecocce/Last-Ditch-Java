package com.gaugestructures.last_ditch.components;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class StationComp extends Component {
    private String name, type;

    public StationComp(String type) {
        this.type = type;

        Yaml yaml = new Yaml();

        try {
            InputStream input = new FileInputStream(
                new File(String.format("../src/com/gaugestructures/last_ditch/cfg/stations.yml")));

            @SuppressWarnings("unchecked")
            Map<String, Object> stationsData = (Map<String, Object>)yaml.load(input);

            @SuppressWarnings("unchecked")
            Map<String, Object> stationData = (Map<String, Object>)stationsData.get(type);

            name = (String)stationData.get("name");
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
