package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.physics.box2d.Body;

public class CollisionComp extends Component {
    private Body body;

    public CollisionComp() {

    }

    public CollisionComp(Body body) {
        this.body = body;
    }

    public Body get_body() {
        return body;
    }

    public void set_body(Body body) {
        this.body = body;
    }
}
