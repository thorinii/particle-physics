package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.lachlanap.lct.Constant;

import static me.lachlanap.cpuparticlebasedphysics.Main.FLOOR;
import static me.lachlanap.cpuparticlebasedphysics.Main.WALL;
import static me.lachlanap.cpuparticlebasedphysics.Particle.*;

/**
 *
 * @author lachlan
 */
public class PhysicsSimulator {

    @Constant(name = "Spring Damping", constraints = "0.9,1")
    public static final float SPRING_DAMPING = 0.99f;

    private final List<Particle> tmp = new ArrayList<>();
    private final Map<Body, Vector2> bodyForces = new HashMap<>();
    private final Map<Body, Vector2> bodyTorques = new HashMap<>();

    public void simulate(List<Body> bodies, float dt) {
        for (Body b : bodies) {
            for (Particle p : b.getParticles()) {
                tmp.add(new Particle(b.convertX(p.pos),
                                     b.convertY(p.pos),
                                     b));
            }

            bodyForces.put(b, new Vector2(0, 0));
            bodyTorques.put(b, new Vector2(0, -1));
        }

        for (Particle p : tmp)
            processParticle(p, p.pos.cpy().sub(p.body.x, p.body.y), dt);

        for (Body b : bodies) {
            Vector2 bodyForce = bodyForces.get(b);
            float torque = bodyTorques.get(b).x;

            b.vx += (bodyForce.x / b.getMass()) * dt;
            b.vy += (9.81f + bodyForce.y / b.getMass()) * dt;

            b.x = b.x + b.vx * dt;
            b.y = b.y + b.vy * dt;


            b.va += (torque / b.getMass()) * dt;
            b.va *= .999f;

            b.a += b.va * dt;
        }

        tmp.clear();
        bodyForces.clear();
    }

    private void processParticle(Particle p, Vector2 particleDisplacement, float dt) {
        Vector2 totalForce = new Vector2();
        float tforceX = 0;
        float tforceY = 0;

        if (p.pos.x < 0) {
            tforceX += K;
        } else if (p.pos.x > WALL) {
            tforceX -= K;
        }
        if (p.pos.y < 0) {
            tforceY += K;
        } else if (p.pos.y > FLOOR) {
            tforceY -= K;
        }

        for (Particle o : tmp) {
            if (p == o)
                continue;

            float dist2 = p.dist2(o);
            if (dist2 > RADIUS * RADIUS)
                continue;

            Vector2 distance = p.pos.cpy().sub(o.pos);
            Vector2 relVel = p.velocity().sub(o.velocity());

            float rabs = (float) Math.abs(Math.sqrt(dist2));

            Vector2 springForce;
            Vector2 force = distance.cpy().scl(-K * (D - rabs) / rabs).add(relVel.cpy().scl(D));
            totalForce.add(force);
        }

        Vector2 bodyForce = bodyForces.get(p.body);
        if (bodyForce == null)
            throw new NullPointerException("Could not find body forces vector for " + p.body);

        bodyForce.add(totalForce);
        bodyForce.x += tforceX;
        bodyForce.y += tforceY;

        bodyTorques.get(p.body).x += totalForce.cpy().scl(0.001f).crs(particleDisplacement);
    }
}
