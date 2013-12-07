package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class Body {

    private final List<Particle> particles;

    public final Vector2 pos = new Vector2();
    public final Vector2 vel = new Vector2();
    public float a;
    public float va;

    public Body() {
        this.particles = new ArrayList<>();
    }

    public Body(List<Particle> particles) {
        this.particles = new ArrayList<>(particles);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public float getA() {
        return a;
    }

    public float getMass() {
        return particles.size();
    }

    public Vector2 convert(Vector2 in) {
        float ca = (float) Math.cos(a);
        float sa = (float) Math.sin(a);

        float convX = in.x * ca - in.y * sa + pos.x;
        float convY = in.x * sa + in.y * ca + pos.y;
        return new Vector2(convX, convY);
    }

    public float convertX(Vector2 in) {
        return in.x * (float) Math.cos(a) - in.y * (float) Math.sin(a) + pos.x;
    }

    public float convertY(Vector2 in) {
        return in.x * (float) Math.sin(a) + in.y * (float) Math.cos(a) + pos.y;
    }

}
