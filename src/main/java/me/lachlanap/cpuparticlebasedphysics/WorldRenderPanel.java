package me.lachlanap.cpuparticlebasedphysics;

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

import static me.lachlanap.cpuparticlebasedphysics.Main.FPS;
import static me.lachlanap.cpuparticlebasedphysics.Main.STEPS;

/**
 *
 * @author lachlan
 */
class WorldRenderPanel extends JPanel {

    private final World world;

    private final List<Vector2> drawing = new ArrayList<>();

    private int xShift = 0;
    private int yShift = 0;
    private boolean isDrawing;

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

        if (!isDrawing)
            updateWorld();
        paintWorld((Graphics2D) g);

        long now = System.nanoTime();

        fps = 1f / (float) ((now - lastTime) / 1000000000.0);

        lastTime = now;
    }

    private void updateWorld() {
        float timestep = 1f / FPS;
        for (int i = 0; i < STEPS; i++)
            world.step(timestep);
    }

    private void paintWorld(Graphics2D g) {
        super.paintComponent(g);

        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, Particle.RADIUS, Particle.RADIUS);

        synchronized (world) {

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
        }


        g.setColor(Color.GREEN);

        for (Vector2 v : drawing) {
            circle.x = xShift + v.x - Particle.RADIUS / 2;
            circle.y = yShift + v.y - Particle.RADIUS / 2;
            g.fill(circle);
        }


        g.setColor(Color.BLACK);
        g.drawLine(0, yShift + (int) world.getFloor(), getWidth(), yShift + (int) world.getFloor());

        g.drawString("FPS: " + fps, 10, 22);
    }

    class MouseAdapterImpl extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                isDrawing = true;

                Vector2 vec = new Vector2(e.getX() - xShift, e.getY() - yShift);
                vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;

                if (!drawing.contains(vec)) {
                    drawing.add(vec);
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            synchronized (world) {
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
                        drawing.clear();
                        isDrawing = false;
                    } else if (e.getButton() == MouseEvent.BUTTON2) {
                        if (drawing.isEmpty())
                            return;

                        for (Vector2 v : drawing) {
                            world.addBody(BodyFactory.makeBody(Arrays.asList(new Particle[]{new Particle(v)})));
                        }

                        drawing.clear();
                        isDrawing = false;
                    }
                }
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isDrawing) {
                Vector2 vec = new Vector2(e.getX() - xShift, e.getY() - yShift);
                vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;

                if (!drawing.contains(vec)) {
                    drawing.add(vec);
                }
            }
        }
    }

}
