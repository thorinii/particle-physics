package me.lachlanap.cpuparticlebasedphysics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class Body {

    private final List<Particle> particles;

    public float x, y;
    public float vx, vy;
    public float a;

    public Body() {
        this.particles = new ArrayList<>();
    }

    public Body(List<Particle> particles) {
        this.particles = new ArrayList<>(particles);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getA() {
        return a;
    }

    public float convertX(float inX, float inY) {
        return inX * (float) Math.cos(a) - inY * (float) Math.sin(a) + x;
    }

    public float convertY(float inX, float inY) {
        return inX * (float) Math.sin(a) + inY * (float) Math.cos(a) + y;
    }

}
