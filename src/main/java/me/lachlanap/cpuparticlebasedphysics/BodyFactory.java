package me.lachlanap.cpuparticlebasedphysics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lachlan
 */
public class BodyFactory {

    public static Body makeBox(int side, float x, float y) {
        Body b = new Body();

        float shiftX = -side * Particle.RADIUS / 2f + Particle.RADIUS / 4f;
        float shiftY = -side * Particle.RADIUS / 2f + Particle.RADIUS / 4f;

        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                Particle p = new Particle(i * Particle.RADIUS + shiftX,
                                          j * Particle.RADIUS + shiftY);
                p.body = b;
                b.getParticles().add(p);
            }
        }

        b.pos.set(x, y);
        b.recalculate();

        return b;
    }

    /**
     * Creates a body with the particles in World position.
     */
    public static Body makeBody(List<Particle> particlesToUse) {
        List<Particle> particles = new ArrayList<>(particlesToUse);
        Body body = new Body(particles);

        for (Particle p : particles) {
            p.body = body;
        }

        body.recalculate();
        return body;
    }

    /**
     * Creates a body with the particles based off an existing body.
     */
    public static Body makeBody(List<Particle> particlesToUse, Body b) {
        List<Particle> particles = new ArrayList<>(particlesToUse);
        Body body = new Body(particles);

        for (Particle p : particles) {
            p.pos.set(b.convert(p.pos));
            p.body = body;
        }

        body.recalculate();
        return body;
    }
}
