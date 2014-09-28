package com.gaugestructures.last_ditch;

import com.gaugestructures.last_ditch.components.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Manager {
    private boolean paused = false;
    private Yaml yaml = new Yaml();
    private Random rnd = new Random();
    private HashSet<String> entities = new HashSet<String>();
    private HashMap<Class<?>, HashMap<String, Component>> componentStores = new HashMap<Class<?>, HashMap<String, Component>>();


    public final String createEntity() {
        String entity = java.util.UUID.randomUUID().toString();

        entities.add(entity);

        return entity;
    }

    public final <T extends Component> T addComp(String entity, T comp) {
        HashMap<String, Component> store = componentStores.get(comp.getClass());

        if (store == null) {
            store = new HashMap<String, Component>();
            componentStores.put(comp.getClass(), store);
        }

        if (store.containsKey(entity)) {
            return null;
        } else {
            store.put(entity, comp);
            return comp;
        }
    }

    public final <T extends Component> T removeComp(String entity, Class<?> comp_type) {
        HashMap<String, Component> store = componentStores.get(comp_type);

        if (store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if (comp != null) {
                store.remove(entity, comp);
                return comp;
            }
        }
        return null;
    }

    public final <T extends Component> T removeComp(String entity, T comp) {
        HashMap<String, Component> store = componentStores.get(comp.getClass());

        if (store != null) {
            store.remove(entity, comp);
            return comp;
        }
        return null;
    }

    public final <T extends Component> T comp(String entity, Class<T> compClass) {
        HashMap<String, Component> store = componentStores.get(compClass);

        if (store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if (comp != null) return comp;
        }

        return null;
    }

    public final <T extends Component> boolean hasComp(String entity, Class<T> compClass) {
        HashMap<String, Component> store = componentStores.get(compClass);

        if(store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if(comp != null && compClass == comp.getClass()) {
                return true;
            }
        }

        return false;
    }

    public final Set<String> entitiesWith(Class<?> compClass) {
        HashMap<String, Component> store = componentStores.get(compClass);

        if (store != null) {
            return store.keySet();
        } else {
            return new HashSet<String>();
        }
    }

    public final Set<String> entitiesWithAll(Class<?>... compClasses) {
        HashSet<String> foundEntities = new HashSet<String>(entities);

        for (Class<?> compClass : compClasses) {
            foundEntities.retainAll(entitiesWith(compClass));
        }

        return foundEntities;
    }

    public void setPaused(boolean pause) {
        this.paused = pause;
    }

    public boolean isPaused() {
        return paused;
    }

    public int randInt(int start, int end) {
        long range = (long)end - (long)start + 1;
        long fraction = (long)(range * rnd.nextDouble());

        return (int)(fraction + start);
    }

    public float randFloat(float start, float end) {
        long range = (long)end - (long)start + 1;
        float scaled = rnd.nextFloat() * range;

        return scaled + start;
    }

    public Map<String, Object> getData(String dataType) {
        try {
            InputStream input = new FileInputStream(new File(String.format("../src/com/gaugestructures/last_ditch/cfg/%s.yml", dataType)));

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>)yaml.load(input);

            return data;
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}

