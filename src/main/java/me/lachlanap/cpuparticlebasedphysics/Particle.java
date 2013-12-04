package me.lachlanap.cpuparticlebasedphysics;

import me.lachlanap.lct.Constant;

/**
 *
 * @author lachlan
 */
public class Particle implements Cloneable {

    @Constant(name = "Spring Tension", constraints = "-1,50")
    public static float K = 50;
    @Constant(name = "Spring Damping", constraints = "-10,100")
    public static float D = 0.1f;
    @Constant(name = "Radius", constraints = "1, 50")
    public static float RADIUS = 50;

    public float x, y;
    public float px, py;
    public Body body;

    public Particle() {
    }

    public Particle(float x, float y) {
        this.x = x;
        this.y = y;
        this.px = x;
        this.py = y;
    }

    public Particle(float x, float y, Body b) {
        this.x = x;
        this.y = y;
        this.px = x;
        this.py = y;
        this.body = b;
    }

    public Particle(float x, float y, float px, float py) {
        this.x = x;
        this.y = y;
        this.px = px;
        this.py = py;
    }

    public void update(float dt, float forcesX, float forcesY) {
        float vx = x - px;
        float vy = y - py;

        vx *= .99f;
        vy *= .99f;

        float nextX = x + vx + (forcesX) * dt;
        float nextY = y + vy + (9.81f + forcesY) * dt;

        px = x;
        py = y;

        x = nextX;
        y = nextY;
    }

    public float dist2(Particle o) {
        return (x - o.x) * (x - o.x)
               + (y - o.y) * (y - o.y);
    }

    @Override
    public Particle clone() {
        return new Particle(x, y, px, py);
    }
}
