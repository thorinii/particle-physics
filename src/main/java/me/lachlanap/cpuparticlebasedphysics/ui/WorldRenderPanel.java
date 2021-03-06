package me.lachlanap.cpuparticlebasedphysics.ui;

import com.badlogic.gdx.math.Vector2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import me.lachlanap.cpuparticlebasedphysics.Body;
import me.lachlanap.cpuparticlebasedphysics.BodyFactory;
import me.lachlanap.cpuparticlebasedphysics.Particle;
import me.lachlanap.cpuparticlebasedphysics.World;

import static me.lachlanap.cpuparticlebasedphysics.Main.FPS;
import static me.lachlanap.cpuparticlebasedphysics.Main.STEPS;

/**
 *
 * @author lachlan
 */
public class WorldRenderPanel extends JPanel {

    private final World world;

    private final List<Vector2> drawing = new ArrayList<>();
    private final Vector2 laserBase = new Vector2();
    private final Vector2 laserTarget = new Vector2();

    private int xShift = 0;
    private int yShift = 0;
    private boolean paused;
    private boolean isDrawing, isLaser;

    private long lastTime;
    private float fps;

    public WorldRenderPanel(World world) {
        this.world = world;

        MouseAdapterImpl impl = new MouseAdapterImpl();
        addMouseListener(impl);
        addMouseMotionListener(impl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        xShift = getWidth() / 2;
        yShift = getHeight() / 2 - (int) world.getFloor() / 2;

        if (!paused)
            updateWorld();
        paintWorld((Graphics2D) g);

        if (!paused) {
            long now = System.nanoTime();
            fps = 1f / (float) ((now - lastTime) / 1000000000.0);
            lastTime = now;
        }
    }

    private void updateWorld() {
        float timestep = 1f / FPS;
        for (int i = 0; i < STEPS; i++)
            world.step(timestep);
    }

    private void paintWorld(Graphics2D g) {
        super.paintComponent(g);

        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, Particle.RADIUS, Particle.RADIUS);
        for (Body b : world.getBodies()) {
            g.setColor(Color.RED);

            for (Particle p : b.getParticles()) {
                circle.x = xShift + b.convertX(p.pos) - Particle.RADIUS / 2;
                circle.y = yShift + b.convertY(p.pos) - Particle.RADIUS / 2;
                g.fill(circle);
            }


            g.setColor(Color.BLUE);

            g.drawLine(xShift + (int) b.pos.x, yShift + (int) b.pos.y - 5,
                       xShift + (int) b.pos.x, yShift + (int) b.pos.y + 5);
            g.drawLine(xShift + (int) b.pos.x - 5, yShift + (int) b.pos.y,
                       xShift + (int) b.pos.x + 5, yShift + (int) b.pos.y);
        }


        g.setColor(Color.GREEN);
        for (Vector2 v : drawing) {
            circle.x = xShift + v.x - Particle.RADIUS / 2;
            circle.y = yShift + v.y - Particle.RADIUS / 2;
            g.fill(circle);
        }

        g.setColor(Color.ORANGE);
        if (isLaser) {
            float m = (laserBase.y - laserTarget.y) / (laserBase.x - laserTarget.x);
            float b = -m * (laserBase.x + xShift) + (laserBase.y + yShift);

            int y_1 = (int) b;
            int y_2 = (int) (m * (getWidth()) + b);

            g.drawLine(0, y_1,
                       getWidth(), y_2);
        }


        g.setColor(Color.BLACK);
        g.drawLine(0, yShift + (int) world.getFloor(), getWidth(), yShift + (int) world.getFloor());

        g.drawString("FPS: " + fps, 10, 22);
    }

    class MouseAdapterImpl extends MouseAdapter {


        @Override
        public void mousePressed(MouseEvent e) {
            if (!e.isControlDown() && (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2)) {
                paused = true;
                isDrawing = true;

                Vector2 vec = intCoordsFromMouse(e);

                if (!drawing.contains(vec)) {
                    drawing.add(vec);
                }
            } else if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
                paused = true;
                isLaser = true;

                laserBase.set(coordsFromMouse(e));
                laserTarget.set(coordsFromMouse(e));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                world.reset();
            } else if (isDrawing) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (drawing.isEmpty())
                        return;

                    List<Particle> particles = new ArrayList<>();
                    for (Vector2 v : drawing) {
                        particles.add(new Particle(v));
                    }

                    world.addBody(BodyFactory.makeBody(particles));
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    for (Vector2 v : drawing)
                        world.addBody(BodyFactory.makeBody(Arrays.asList(new Particle[]{new Particle(v)})));
                }

                drawing.clear();
            } else if (isLaser) {
                world.cutAlong(laserBase, laserTarget);
            }

            paused = false;
            isDrawing = false;
            isLaser = false;
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawing) {
                Vector2 vec = intCoordsFromMouse(e);

                if (!drawing.contains(vec))
                    drawing.add(vec);
            } else if (isLaser) {
                laserTarget.set(coordsFromMouse(e));
            }

            repaint();
        }
    }

    private Vector2 intCoordsFromMouse(MouseEvent e) {
        Vector2 vec = new Vector2(e.getX() - xShift, e.getY() - yShift);
        vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
        vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;
        return vec;
    }

    private Vector2 coordsFromMouse(MouseEvent e) {
        Vector2 vec = new Vector2(e.getX() - xShift, e.getY() - yShift);
        return vec;
    }

}
