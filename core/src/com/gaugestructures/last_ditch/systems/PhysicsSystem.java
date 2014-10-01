package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PhysicsSystem extends GameSystem {
    private Manager mgr;
    private String player;
    private MapSystem map;
    private Body playerBody;
    private List<List<Body>> tileBodies = new ArrayList<List<Body>>();
    private Vector2 gravity = new Vector2(0, 0);
    private World world = new World(gravity, false);
    private PositionComp focus;

    public PhysicsSystem(Manager mgr, String player, MapSystem map) {
        this.mgr = mgr;
        this.player = player;
        this.map = map;

        focus = mgr.comp(player, PositionComp.class);

        for(int i = 0; i < map.getNumOfChunks(); i++) {
            tileBodies.add(i, new ArrayList<Body>());
        }

        generatePlayerBody();
        generateTileBodies();
        generateDoorBodies();

        updateStationBodies();
    }

    private void generatePlayerBody() {
        PositionComp posComp = mgr.comp(player, PositionComp.class);
        AnimationComp animComp = mgr.comp(player, AnimationComp.class);
        CollisionComp colComp = mgr.comp(player, CollisionComp.class);

        float w = animComp.getW() * C.WTB;
        float h = animComp.getH() * C.WTB;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 20.0f;
        bodyDef.position.set(posComp.getX() + w/2, posComp.getY() + h/2);

        CircleShape shape = new CircleShape();
        shape.setRadius(w/2 - 0.01f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.2f;
        fixtureDef.density = 1f;

        fixtureDef.filter.categoryBits = C.BIT_PLAYER;

        colComp.setBody(world.createBody(bodyDef));
        colComp.getBody().createFixture(fixtureDef);
        colComp.getBody().setFixedRotation(true);
        colComp.getBody().setUserData(player);

        playerBody = colComp.getBody();
    }

    private void generateTileBodies() {
        for (int i = 0; i < map.getNumOfChunks(); i++) {
            int chunkX = map.getChunkX(i);
            int chunkY = map.getChunkY(i);

            for (int x = chunkX; x < C.CHUNK_SIZE + chunkX; x++) {
                for (int y = chunkY; y < C.CHUNK_SIZE + chunkY; y++) {
                    if (map.isSolid(x, y)) {
                        BodyDef bodyDef = new BodyDef();
                        bodyDef.position.set(x + 0.5f, y + 0.5f);

                        PolygonShape shape = new PolygonShape();
                        shape.setAsBox(0.5f, 0.5f);

                        FixtureDef fixtureDef = new FixtureDef();
                        fixtureDef.shape = shape;

                        if (map.hasSight(x, y)) {
                            fixtureDef.filter.categoryBits = C.BIT_WINDOW;
                        } else {
                            fixtureDef.filter.categoryBits = C.BIT_WALL;
                        }

                        Body body = world.createBody(bodyDef);
                        body.createFixture(fixtureDef);
                        body.setUserData(new Vector2(x, y));

                        tileBodies.get(i).add(body);
                    }
                }
            }
        }
    }

    public void updateTileBodies() {
        for (int chunk : map.getNewChunks()) {
            for (Body body : tileBodies.get(chunk)) {
                body.setActive(true);
            }
        }

        for (int chunk: map.getOldChunks()) {
            for (Body body : tileBodies.get(chunk)) {
                body.setActive(false);
            }
        }
    }

    private void generateDoorBodies() {
        for (int i = 0; i < map.getNumOfChunks(); i++) {
            for (String door : map.getDoors().get(i)) {
                DoorComp doorComp = mgr.comp(door, DoorComp.class);

                if (!doorComp.isOpen()) {
                    PositionComp posComp = mgr.comp(door, PositionComp.class);
                    RotationComp rotComp = mgr.comp(door, RotationComp.class);
                    SizeComp sizeComp = mgr.comp(door, SizeComp.class);
                    CollisionComp colComp = mgr.comp(door, CollisionComp.class);

                    Body body = createBody(
                        posComp.getX(), posComp.getY(),
                        sizeComp.getW(), sizeComp.getH(),
                        false, rotComp.getAng());

                    colComp.setBody(body);
                }
            }
        }
    }

    public void updateDoorBodies() {
        for (int chunk : map.getNewChunks()) {
            for (String door : map.getDoors().get(chunk)) {
                DoorComp doorComp = mgr.comp(door, DoorComp.class);

                if (!doorComp.isOpen()) {
                    CollisionComp colComp = mgr.comp(door, CollisionComp.class);

                    colComp.getBody().setActive(true);
                }
            }
        }

        for (int chunk : map.getOldChunks()) {
            for (String door : map.getDoors().get(chunk)) {
                DoorComp doorComp = mgr.comp(door, DoorComp.class);

                if (!doorComp.isOpen()) {
                    CollisionComp colComp = mgr.comp(door, CollisionComp.class);

                    colComp.getBody().setActive(false);
                }
            }
        }
    }

    public Body createBody(float x, float y, float w, float h, boolean sight, float ang) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2, h / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        if (sight) {
            fixtureDef.filter.categoryBits = C.BIT_WINDOW;
        } else {
            fixtureDef.filter.categoryBits = C.BIT_WALL;
        }

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setTransform(x, y, (float)(ang * Math.PI / 180));

        shape.dispose();

        return body;
    }

    private void updateStationBodies() {

    }

    public void update() {
        Set<String> entities = mgr.entitiesWith(VelocityComp.class);

        for(String entity : entities) {
            PositionComp posComp = mgr.comp(entity, PositionComp.class);
            VelocityComp velComp = mgr.comp(entity, VelocityComp.class);
            RotationComp rotComp = mgr.comp(entity, RotationComp.class);
            CollisionComp colComp = mgr.comp(entity, CollisionComp.class);

            posComp.setPrevX(posComp.getX());
            posComp.setPrevY(posComp.getY());

            if(velComp.getSpd() != 0) {
                Vector2 velVec = new Vector2(
                    velComp.getSpd() * rotComp.getX(),
                    velComp.getSpd() * rotComp.getY());

                colComp.getBody().applyLinearImpulse(
                    velVec, colComp.getBody().getWorldCenter(), true);
            }

            rotComp.setPrevAng(rotComp.getAng());

            if(velComp.getAngSpd() != 0) {
                rotComp.rotate(velComp.getAngSpd());
            }
        }

        world.step(C.BOX_STEP, C.BOX_VEL_ITER, C.BOX_POS_ITER);

        for(String entity : entities) {
            PositionComp posComp = mgr.comp(entity, PositionComp.class);
            CollisionComp colComp = mgr.comp(entity, CollisionComp.class);

            posComp.setX(colComp.getBody().getPosition().x);
            posComp.setY(colComp.getBody().getPosition().y);
        }
    }

    public void render(SpriteBatch batch) {

    }

    public void removeBody(Body body) {
        world.destroyBody(body);
    }

    public World getWorld() {
        return world;
    }

    public Body getPlayerBody() {
        return playerBody;
    }
}
