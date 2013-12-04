package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.lachlanap.cpuparticlebasedphysics.Main.FLOOR;
import static me.lachlanap.cpuparticlebasedphysics.Main.WALL;
import static me.lachlanap.cpuparticlebasedphysics.Particle.*;

/**
 *
 * @author lachlan
 */
public class PhysicsSimulator {

    private final List<Particle> tmp = new ArrayList<>();
    private final Map<Body, Vector2> bodyForces = new HashMap<>();

    public void simulate(List<Body> bodies, float dt) {
        for (Body b : bodies) {
            for (Particle p : b.getParticles()) {
                tmp.add(new Particle(b.convertX(p.x, p.y),
                                     b.convertY(p.x, p.y),
                                     b));
            }

            bodyForces.put(b, new Vector2(0, 0));
        }

        for (Particle p : tmp)
            processParticle(p, dt);

        for (Body b : bodies) {
            Vector2 bodyForce = bodyForces.get(b);

            b.vx += (bodyForce.x) * dt;
            b.vy += (9.81f + bodyForce.y) * dt;
            b.x = b.x + b.vx * dt;
            b.y = b.y + b.vy * dt;
        }

        tmp.clear();
        bodyForces.clear();
    }

    private void processParticle(Particle p, float dt) {
        float tforceX = 0;
        float tforceY = 0;

        if (p.x < 0) {
            tforceX += K;
        } else if (p.x > WALL) {
            tforceX -= K;
        }
        if (p.y > FLOOR) {
            tforceY -= K;
        }

        for (Particle o : tmp) {
            if (p == o)
                continue;

            float dist2 = p.dist2(o);
            if (dist2 > RADIUS * RADIUS)
                continue;

            float rx = p.x - o.x;
            float ry = p.y - o.y;

            float vx = (p.x - p.px) - (o.x - o.px);
            float vy = (p.y - p.py) - (o.y - o.py);

            float rabs = (float) Math.abs(Math.sqrt(dist2));

            float forceX = -K * (D - rabs) * (rx / rabs) - D * vx;
            float forceY = -K * (D - rabs) * (ry / rabs) - D * vy;

            if (forceX == Float.NaN)
                throw new IllegalStateException("X Force on Particle " + p + " exerted by " + o + " is NaN");
            if (forceY == Float.NaN)
                throw new IllegalStateException("Y Force on Particle " + p + " exerted by " + o + " is NaN");

            tforceX += forceX;
            tforceY += forceY;
        }

        Vector2 bodyForce = bodyForces.get(p.body);
        if (bodyForce == null)
            throw new NullPointerException("Could not find body forces vector for " + p.body);
        bodyForce.x += tforceX;
        bodyForce.y += tforceY;
    }

    /* private void processBody(Body body, float dt) {
     * float bodyForceX = 0;
     * float bodyForceY = 0;
     *
     * for (Particle p : body.getParticles()) {
     * Particle newP = p.clone();
     *
     * newP.x = body.convertX(newP.x, newP.y);
     * newP.y = body.convertY(newP.x, newP.y);
     *
     * float tforceX = 0;
     * float tforceY = 0;
     *
     * if (newP.y < RADIUS) {
     * //tforceY += K;
     * //newP.y = RADIUS;
     *
     * //if (p.py > RADIUS)
     * // newP.py = -(p.py - RADIUS) + RADIUS;
     * //else
     * // newP.py = 0;
     * } else if (newP.y > FLOOR) {
     * tforceY -= K;
     * newP.y = FLOOR - ((float) Math.random() * 0.1f);
     *
     * if (newP.py < FLOOR)
     * newP.py = -(newP.py - FLOOR) + FLOOR;
     * //else
     * // newP.py = FLOOR;
     * }
     *
     * if (newP.x < RADIUS) {
     * newP.x = RADIUS;
     *
     * if (newP.px > RADIUS)
     * newP.px = -(newP.px - RADIUS) + RADIUS;
     * //else
     * // newP.px = RADIUS;
     * } else if (newP.x > WALL) {
     * newP.x = WALL;
     *
     * if (newP.px > 0)
     * newP.px = -(newP.px - WALL) + WALL;
     * //else
     * // newP.px = frame.getWidth();
     * }
     * for (Particle o : body.getParticles()) {
     * if (p == o)
     * continue;
     *
     * float dist2 = newP.dist2(o);
     * if (dist2 > RADIUS * RADIUS)
     * continue;
     *
     * float rx = newP.x - o.x;
     * float ry = newP.y - o.y;
     *
     * float vx = (newP.x - newP.px) - (o.x - o.px);
     * float vy = (newP.y - newP.py) - (o.y - o.py);
     *
     * float rabs = (float) Math.abs(Math.sqrt(dist2));
     *
     * float forceX = -K * (D - rabs) * (rx / rabs) - D * vx;
     * float forceY = -K * (D - rabs) * (ry / rabs) - D * vy;
     *
     * tforceX += forceX;
     * tforceY += forceY;
     * }
     *
     * //tforceX *= D;
     * //tforceY *= D;
     *
     * newP.update(0.0166f, tforceX, tforceY);
     *
     * bodyForceX += tforceX;
     * bodyForceY += tforceY;
     * }
     *
     * body.vx += (bodyForceX) * dt;
     * body.vy += (9.81f + bodyForceY) * dt;
     * body.x = body.x + body.vx * dt;
     * body.y = body.y + body.vy * dt;
     * } */
}
