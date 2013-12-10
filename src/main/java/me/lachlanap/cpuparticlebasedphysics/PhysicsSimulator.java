package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.lachlanap.lct.Constant;

import static me.lachlanap.cpuparticlebasedphysics.Main.FLOOR;
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
                Vector2 vel = b.vel.cpy().add(-b.a * p.pos.y, b.a * p.pos.x);

                tmp.add(new Particle(b.convert(p.pos), b).setVelocity(vel));
            }

            bodyForces.put(b, new Vector2(0, 0));
            bodyTorques.put(b, new Vector2(0, -1));
        }

        for (Particle p : tmp)
            processParticle(p, p.pos.cpy().sub(p.body.pos.x, p.body.pos.y), dt);

        for (Body b : bodies) {
            Vector2 bodyForce = bodyForces.get(b);
            float torque = bodyTorques.get(b).x;


            Vector2 oldLinearVel = b.vel.cpy();

            b.vel.x += (bodyForce.x / b.getMass()) * dt;
            b.vel.y += (9.81f + bodyForce.y / b.getMass()) * dt;
            b.vel.scl(0.99f);

            b.pos.x = b.pos.x + (oldLinearVel.x + b.vel.x) * dt;
            b.pos.y = b.pos.y + (oldLinearVel.y + b.vel.y) * 0.5f * dt;


            float oldAngularVel = b.va;
            float angularAccel = torque / (0.5f * b.getMass() * b.getMaxRadius() * b.getMaxRadius());

            b.va += -angularAccel * dt;
            b.va *= .999f;

            b.a = b.a + (oldAngularVel + b.va) * 0.5f * dt;
        }

        tmp.clear();
        bodyForces.clear();
    }

    private void processParticle(Particle p, Vector2 particleDisplacement, float dt) {
        Vector2 totalForce = new Vector2();

//        if (p.pos.x < 0) {
//            float dist = Particle.RADIUS - p.pos.x;
//            totalForce.x += dist * -K * (Particle.RADIUS * 2 - dist) * (1 / dist) + -p.vel.x * DAMPING;
//        } else if (p.pos.x > WALL) {
//            float dist = WALL - p.pos.x;
//            totalForce.x += dist * -K * (Particle.RADIUS * 2 - dist) * (1 / dist) + -p.vel.x * DAMPING;
//        }
//        if (p.pos.y < 0) {
//            float dist = Particle.RADIUS - p.pos.y;
//            totalForce.y += dist * -K * (Particle.RADIUS * 2 - dist) * (1 / dist) + -p.vel.y * DAMPING;
//        } else
        if (p.pos.y > FLOOR) {
            float dist = FLOOR - p.pos.y;
            totalForce.y += dist * -K * (Particle.RADIUS * 2 - dist) * (1 / dist) + -p.vel.y * DAMPING;
        }

        for (Particle o : tmp) {
            if (p == o)
                continue;

            float dist2 = p.dist2(o);
            if (dist2 > RADIUS * RADIUS)
                continue;
            else if (dist2 < 0.01f) {
                totalForce.x += 1;
                totalForce.y += 1;
                continue;
            }

            Vector2 distance = o.pos.cpy().sub(p.pos);
            Vector2 relVel = o.vel.cpy().sub(p.vel);

            float rabs = distance.len();

            // Fspring = -K(distance)(Diameter - |distance|) / |distance|
            Vector2 springForce
                    = distance.cpy().scl(-K).scl(Particle.RADIUS * 2 - rabs).scl(1 / rabs);

            // Fdamping = D(relvel)
            Vector2 dampingForce = relVel.cpy().scl(DAMPING);

            Vector2 force = springForce.add(dampingForce);
            totalForce.add(force);
        }

        Vector2 bodyForce = bodyForces.get(p.body);
        if (bodyForce == null)
            throw new NullPointerException("Could not find body forces vector for " + p.body);

        bodyForce.add(totalForce);

        bodyTorques.get(p.body).x += totalForce.cpy().crs(particleDisplacement);
    }
}
