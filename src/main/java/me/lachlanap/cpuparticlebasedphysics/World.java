package me.lachlanap.cpuparticlebasedphysics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class World {

    private final List<Body> bodies;
    private final PhysicsSimulator simulator;

    private float floor = 350;

    public World() {
        bodies = new ArrayList<>();
        simulator = new PhysicsSimulator();
        simulator.setWorld(this);
    }

    public void addBody(Body b) {
        bodies.add(b);
    }

    public List<Body> getBodies() {
        return Collections.unmodifiableList(bodies);
    }

    public float getFloor() {
        return floor;
    }

    public void setFloor(float floor) {
        this.floor = floor;
    }


    public void reset() {
        bodies.clear();
    }


    public void step(float dt) {
        simulator.simulate(bodies, dt);
    }
}
