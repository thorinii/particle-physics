package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.lachlanap.cpuparticlebasedphysics.physics.PhysicsSimulator;

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

    public void cutAlong(Vector2 from, Vector2 to) {
        List<Particle> leftParticles = new ArrayList<>();
        List<Particle> rightParticles = new ArrayList<>();

        List<Body> newBodies = new ArrayList<>();

        for (Iterator<Body> it = bodies.iterator(); it.hasNext();) {
            Body b = it.next();

            leftParticles.clear();
            rightParticles.clear();

            for (Particle p : b.getParticles()) {
                if (isLeft(from, to, b.convert(p.pos)))
                    leftParticles.add(p);
                else
                    rightParticles.add(p);
            }

            if (leftParticles.isEmpty() || rightParticles.isEmpty()) {
                System.out.println("Skipping");
                continue;
            }

            Body left = BodyFactory.makeBody(leftParticles, b);
            Body right = BodyFactory.makeBody(rightParticles, b);

            left.vel.set(b.vel);
            right.vel.set(b.vel);

            newBodies.add(left);
            newBodies.add(right);

            it.remove();
        }

        bodies.addAll(newBodies);
    }

    private boolean isLeft(Vector2 a, Vector2 b, Vector2 check) {
        return (b.x - a.x) * (check.y - a.y)
               > (b.y - a.y) * (check.x - a.x);
    }


    public void step(float dt) {
        simulator.simulate(bodies, dt);
    }
}
