package me.lachlanap.cpuparticlebasedphysics;

/**
 *
 * @author lachlan
 */
public class Particle {

    public float x, y;
    public float px, py;

    public Particle() {
    }

    public Particle(int x, int y) {
        this.x = x;
        this.y = y;
        this.px = x;
        this.py = y;
    }

    public void update(float dt) {
        float vx = x - px;
        float vy = y - py;

        float nextX = x + vx;
        float nextY = y + vy + (9.81f) * dt;

        px = x;
        py = y;

        x = nextX;
        y = nextY;
    }
}
