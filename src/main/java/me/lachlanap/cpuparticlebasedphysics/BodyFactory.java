package me.lachlanap.cpuparticlebasedphysics;

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
}
