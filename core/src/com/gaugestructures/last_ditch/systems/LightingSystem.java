package com.gaugestructures.last_ditch.systems;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gaugestructures.last_ditch.C;

public class LightingSystem extends GameSystem {
    private OrthographicCamera cam;
    private PhysicsSystem physics;
    private RayHandler handler;
    private PointLight centralLight;

    public LightingSystem(OrthographicCamera cam, PhysicsSystem physics) {
        this.cam = cam;
        this.physics = physics;

        handler = new RayHandler(physics.getWorld());

        RayHandler.isDiffuse = true;
        Light.setContactFilter(C.BIT_LIGHT, C.BIT_ZERO, C.BIT_WALL);

        centralLight = new PointLight(handler, 600);
        centralLight.setSoft(true);
        centralLight.setSoftnessLength(1.2f);
        centralLight.setColor(0.8f, 0.8f, 0.8f, 1f);
        centralLight.setDistance(1000f);
        centralLight.attachToBody(physics.getPlayerBody(), 0, 0);
    }

    public void render() {
        handler.setCombinedMatrix(cam.combined.scl(C.BTW));
        handler.updateAndRender();
    }
}
