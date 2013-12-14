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

/**
 *
 * @author lachlan
 */
class WorldRenderPanel extends JPanel {

    private final World world;

    private final List<Vector2> drawing = new ArrayList<>();
    private boolean isDrawing;

    public WorldRenderPanel(World world) {
        this.world = world;

        MouseAdapterImpl impl = new MouseAdapterImpl();
        addMouseListener(impl);
        addMouseMotionListener(impl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, Particle.RADIUS, Particle.RADIUS);
        g.setColor(Color.RED);
        synchronized (world) {
            for (Body b : world.getBodies()) {
                g.setColor(Color.RED);
                for (Particle p : b.getParticles()) {
                    circle.x = Main.X_SHIFT + b.convertX(p.pos) - Particle.RADIUS / 2;
                    circle.y = Main.Y_SHIFT + b.convertY(p.pos) - Particle.RADIUS / 2;
                    ((Graphics2D) g).fill(circle);
                }
                g.setColor(Color.BLUE);
                g.drawLine(Main.X_SHIFT + (int) b.pos.x, Main.Y_SHIFT + (int) b.pos.y - 5, Main.X_SHIFT + (int) b.pos.x,
                           Main.Y_SHIFT + (int) b.pos.y + 5);
                g.drawLine(Main.X_SHIFT + (int) b.pos.x - 5, Main.Y_SHIFT + (int) b.pos.y,
                           Main.X_SHIFT + (int) b.pos.x + 5, Main.Y_SHIFT + (int) b.pos.y);
            }
        }
        g.setColor(Color.GREEN);
        for (Vector2 v : drawing) {
            circle.x = Main.X_SHIFT + v.x - Particle.RADIUS / 2;
            circle.y = Main.Y_SHIFT + v.y - Particle.RADIUS / 2;
            ((Graphics2D) g).fill(circle);
        }
        g.setColor(Color.BLACK);
        g.drawLine(0, Main.Y_SHIFT + (int) world.getFloor(), getWidth(), Main.Y_SHIFT + (int) world.getFloor());
    }

    class MouseAdapterImpl extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                isDrawing = true;
                Vector2 vec = new Vector2(e.getX() - Main.X_SHIFT, e.getY() - Main.Y_SHIFT);
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
                Vector2 vec = new Vector2(e.getX() - Main.X_SHIFT, e.getY() - Main.Y_SHIFT);
                vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;
                if (!drawing.contains(vec)) {
                    drawing.add(vec);
                }
            }
        }
    }

}
