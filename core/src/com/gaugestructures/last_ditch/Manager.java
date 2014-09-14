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
    private HashMap<Class<?>, HashMap<String, Component>> component_stores = new HashMap<Class<?>, HashMap<String, Component>>();


    public final String create_entity() {
        String entity = java.util.UUID.randomUUID().toString();

        entities.add(entity);

        return entity;
    }

    public final <T extends Component> T add_comp(String entity, T comp) {
        HashMap<String, Component> store = component_stores.get(comp.getClass());

        if (store == null) {
            store = new HashMap<String, Component>();
            component_stores.put(comp.getClass(), store);
        }

        if (store.containsKey(entity)) {
            return null;
        } else {
            store.put(entity, comp);
            return comp;
        }
    }

    public final boolean remove_comp(String entity, Class<?> comp_type) {
        HashMap<String, Component> store = component_stores.get(comp_type);

        if (store != null) {
            Component comp = store.get(entity);

            if (comp != null) {
                return store.remove(entity, comp);
            }
        }

        return false;
    }

    public final boolean remove_comp(String entity, Component comp) {
        HashMap<String, Component> store = component_stores.get(comp.getClass());

        return store != null && store.remove(entity, comp);
    }

    public final <T extends Component> T comp(String entity, Class<T> comp_type) {
        HashMap<String, Component> store = component_stores.get(comp_type);

        if (store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if (comp != null) return comp;
        }

        return null;
    }

    public final <T extends Component> boolean has_comp(String entity, Class<T> comp_class) {
        HashMap<String, Component> store = component_stores.get(comp_class);

        if(store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if(comp != null && comp_class == comp.getClass()) {
                return true;
            }
        }

        return false;
    }

    public final Set<String> entities_with(Class<?> comp_class) {
        HashMap<String, Component> store = component_stores.get(comp_class);

        if (store != null) {
            return store.keySet();
        } else {
            return new HashSet<String>();
        }
    }

    public final Set<String> entities_with_all(Class<?>... comp_classes) {
        HashSet<String> found_entities = new HashSet<String>(entities);

        for (Class<?> comp_class : comp_classes) {
            found_entities.retainAll(entities_with(comp_class));
        }

        return found_entities;
    }

    public void set_paused(boolean pause) {
        this.paused = pause;
    }

    public boolean is_paused() {
        return paused;
    }

    public int rand_int(int start, int end) {
        long range = (long)end - (long)start + 1;
        long fraction = (long)(range * rnd.nextDouble());

        return (int)(fraction + start);
    }

    public float rand_float(float start, float end) {
        long range = (long)end - (long)start + 1;
        float scaled = rnd.nextFloat() * range;

        return scaled + start;
    }

    public Map<String, Object> get_data(String data_type) {
        try {
            InputStream input = new FileInputStream(new File(String.format("../src/com/gaugestructures/last_ditch/cfg/%s.yml", data_type)));

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>)yaml.load(input);

            return data;
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}

