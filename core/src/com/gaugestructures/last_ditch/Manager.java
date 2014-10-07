package com.gaugestructures.last_ditch;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gaugestructures.last_ditch.components.GameComponent;
import com.gaugestructures.last_ditch.systems.TimeSystem;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Manager {
    private boolean paused = false;

    private Skin skin;
    private String player;
    private TimeSystem time;
    private TextureAtlas atlas;
    private Yaml yaml = new Yaml();
    private Random rnd = new Random();
    private HashSet<String> entities = new HashSet<String>();
    private HashMap<Class<?>, HashMap<String, GameComponent>> componentStores = new HashMap<Class<?>, HashMap<String, GameComponent>>();

    public Manager(TextureAtlas atlas, Skin skin) {
        this.atlas = atlas;
        this.skin = skin;
    }

    public final void createPlayer() {
        player = createEntity();
    }

    public final String createEntity() {
        String entity = java.util.UUID.randomUUID().toString();

        entities.add(entity);

        return entity;
    }

    public final <T extends GameComponent> T addComp(String entity, T comp) {
        HashMap<String, GameComponent> store = componentStores.get(comp.getClass());

        if (store == null) {
            store = new HashMap<String, GameComponent>();
            componentStores.put(comp.getClass(), store);
        }

        if (store.containsKey(entity)) {
            return null;
        } else {
            store.put(entity, comp);
            return comp;
        }
    }

    public final <T extends GameComponent> T removeComp(String entity, Class<?> comp_type) {
        HashMap<String, GameComponent> store = componentStores.get(comp_type);

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

    public final <T extends GameComponent> T removeComp(String entity, T comp) {
        HashMap<String, GameComponent> store = componentStores.get(comp.getClass());

        if (store != null) {
            store.remove(entity, comp);
            return comp;
        }
        return null;
    }

    public final <T extends GameComponent> T comp(String entity, Class<T> compClass) {
        HashMap<String, GameComponent> store = componentStores.get(compClass);

        if (store != null) {
            @SuppressWarnings("unchecked")
            T comp = (T)store.get(entity);

            if (comp != null) return comp;
        }

        return null;
    }

    public final <T extends GameComponent> boolean hasComp(String entity, Class<T> compClass) {
        HashMap<String, GameComponent> store = componentStores.get(compClass);

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
        HashMap<String, GameComponent> store = componentStores.get(compClass);

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
            InputStream input = new FileInputStream(
                new File(String.format("../src/com/gaugestructures/last_ditch/cfg/%s.yml", dataType)));

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>)yaml.load(input);

            return data;
        } catch(FileNotFoundException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public final void setTime(TimeSystem time) {
        this.time = time;
    }

    public TimeSystem getTime() { return time; }

    public void setPaused(boolean pause) {
        this.paused = pause;
    }

    public boolean isPaused() {
        return paused;
    }

    public String getPlayer() {
        return player;
    }

    public Skin getSkin() {
        return skin;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }
}

