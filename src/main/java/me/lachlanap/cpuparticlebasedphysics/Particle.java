package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import me.lachlanap.lct.Constant;

/**
 *
 * @author lachlan
 */
public class Particle implements Cloneable {

    @Constant(name = "Spring Tension", constraints = "0,100")
    public static float K = 50;
    @Constant(name = "Spring Damping", constraints = "0,2")
    public static float DAMPING = 0.99f;
    @Constant(name = "Radius", constraints = "1, 50")
    public static float RADIUS = 10;

    public final Vector2 pos = new Vector2();
    public final Vector2 vel = new Vector2();
    public Body body;

    public Particle() {
    }

    public Particle(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    public Particle(float x, float y, Body b) {
        pos.x = x;
        pos.y = y;
        this.body = b;
    }

    public Particle(Vector2 position) {
        pos.set(position);
        body = null;
    }


    public Particle(Vector2 position, Body b) {
        pos.set(position);
        this.body = b;
    }

    public Particle(Particle p) {
        pos.set(p.pos);
        vel.set(p.vel);
    }

    public Particle setVelocity(Vector2 v) {
        vel.set(v);
        return this;
    }

    public float dist2(Particle o) {
        return pos.dst2(o.pos);
    }
}
