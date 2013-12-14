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
import javax.swing.JFrame;
import javax.swing.JPanel;
import me.lachlanap.lct.Constant;
import me.lachlanap.lct.LCTManager;
import me.lachlanap.lct.gui.LCTFrame;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        LCTManager manager = new LCTManager();
        manager.register(Main.class);
        manager.register(Particle.class);
        LCTFrame lCTFrame = new LCTFrame(manager);
        lCTFrame.setVisible(true);

        final World world = new World();

        final JFrame frame = new JFrame("CPU Particle-Based Physics");
        frame.getContentPane().add(new JPanel() {
            private boolean isDrawing;
            private final List<Vector2> drawing = new ArrayList<>();

            {
                MouseAdapterImpl impl = new MouseAdapterImpl();
                addMouseListener(impl);
                addMouseMotionListener(impl);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Ellipse2D.Float circle = new Ellipse2D.Float(0, 0,
                                                             Particle.RADIUS, Particle.RADIUS);
                g.setColor(Color.RED);

                synchronized (world) {
                    for (Body b : world.getBodies()) {
                        g.setColor(Color.RED);
                        for (Particle p : b.getParticles()) {
                            circle.x = X_SHIFT + b.convertX(p.pos) - Particle.RADIUS / 2;
                            circle.y = Y_SHIFT + b.convertY(p.pos) - Particle.RADIUS / 2;

                            ((Graphics2D) g).fill(circle);
                        }

                        g.setColor(Color.BLUE);
                        g.drawLine(X_SHIFT + (int) b.pos.x, Y_SHIFT + (int) b.pos.y - 5,
                                   X_SHIFT + (int) b.pos.x, Y_SHIFT + (int) b.pos.y + 5);
                        g.drawLine(X_SHIFT + (int) b.pos.x - 5, Y_SHIFT + (int) b.pos.y,
                                   X_SHIFT + (int) b.pos.x + 5, Y_SHIFT + (int) b.pos.y);
                    }
                }

                g.setColor(Color.GREEN);
                for (Vector2 v : drawing) {
                    circle.x = X_SHIFT + v.x - Particle.RADIUS / 2;
                    circle.y = Y_SHIFT + v.y - Particle.RADIUS / 2;

                    ((Graphics2D) g).fill(circle);
                }

                g.setColor(Color.BLACK);
                g.drawLine(0, Y_SHIFT + (int) world.getFloor(), getWidth(), Y_SHIFT + (int) world.getFloor());
            }

            class MouseAdapterImpl extends MouseAdapter {

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
                        isDrawing = true;

                        Vector2 vec = new Vector2(e.getX() - X_SHIFT, e.getY() - Y_SHIFT);
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
                                    world.addBody(BodyFactory.makeBody(
                                            Arrays.asList(new Particle[]{new Particle(v)})));
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
                        Vector2 vec = new Vector2(e.getX() - X_SHIFT, e.getY() - Y_SHIFT);
                        vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                        vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;

                        if (!drawing.contains(vec)) {
                            drawing.add(vec);
                        }
                    }
                }
            }
        });

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        while (true) {
            if (FPS <= 0) {
                Thread.sleep(100);
                continue;
            }

            final float TIMESTEP = 1f / FPS;
            synchronized (world) {
                for (int i = 0; i < STEPS; i++)
                    world.step(TIMESTEP);
            }

            X_SHIFT = frame.getWidth() / 2;
            Y_SHIFT = frame.getHeight() / 2 - (int) world.getFloor() / 2;

            frame.repaint();
            Thread.sleep((int) (TIMESTEP * 1000));
        }
    }

    public static int X_SHIFT = 0;
    public static int Y_SHIFT = 0;

    @Constant(name = "FPS", constraints = "0,100")
    public static int FPS = 50;

    @Constant(name = "Steps", constraints = "1,100")
    public static int STEPS = 1;
}
