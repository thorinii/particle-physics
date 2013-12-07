package me.lachlanap.cpuparticlebasedphysics;

import com.badlogic.gdx.math.Vector2;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
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

        final List<Body> bodies = new ArrayList<>();

        final JFrame frame = new JFrame("CPU Particle-Based Physics");
        frame.getContentPane().add(new JPanel() {
            {
                MouseAdapterImpl impl = new MouseAdapterImpl(bodies);
                addMouseListener(impl);
                addMouseMotionListener(impl);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Ellipse2D.Float circle = new Ellipse2D.Float(0, 0,
                                                             Particle.RADIUS, Particle.RADIUS);
                g.setColor(Color.RED);

                synchronized (bodies) {
                    for (Body b : bodies) {
                        for (Particle p : b.getParticles()) {
                            circle.x = X_SHIFT + b.convertX(p.pos) - Particle.RADIUS / 2;
                            circle.y = Y_SHIFT + b.convertY(p.pos) - Particle.RADIUS / 2;

                            ((Graphics2D) g).fill(circle);
                        }
                    }
                }

                g.drawLine(0, Y_SHIFT + FLOOR, getWidth(), Y_SHIFT + FLOOR);
                g.drawLine(0, Y_SHIFT, getWidth(), Y_SHIFT);
                g.drawLine(X_SHIFT + WALL, 0, X_SHIFT + WALL, getHeight());
                g.drawLine(X_SHIFT, 0, X_SHIFT, getHeight());
            }

            class MouseAdapterImpl extends MouseAdapter {

                private final List<Body> bodies;

                private boolean isDrawing;
                private final List<Vector2> drawing = new ArrayList<>();

                public MouseAdapterImpl(
                        List<Body> bodies) {
                    this.bodies = bodies;
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON2)
                        isDrawing = true;
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    synchronized (bodies) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            bodies.clear();
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT,
                                                           e.getX() - X_SHIFT,
                                                           e.getY() - Y_SHIFT));
                        } else {
                            if (drawing.size() <= 1)
                                return;

                            Vector2 centre = new Vector2();
                            for (Vector2 v : drawing) {
                                centre.add(v);
                            }
                            centre.div(drawing.size());

                            Body body = new Body();
                            body.pos.set(centre);

                            List<Particle> particles = new ArrayList<>();
                            for (Vector2 v : drawing) {
                                particles.add(new Particle(v.sub(centre), body));
                            }
                            body.getParticles().addAll(particles);

                            bodies.add(body);

                            drawing.clear();
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    System.out.println("M");
                    synchronized (bodies) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT,
                                                           e.getX() - X_SHIFT,
                                                           e.getY() - Y_SHIFT));
                        } else if (e.getButton() == MouseEvent.BUTTON2) {
                            Vector2 vec = new Vector2(e.getX() - X_SHIFT, e.getY() - Y_SHIFT);
                            vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                            vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;

                            if (!drawing.contains(vec))
                                drawing.add(vec);
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    System.out.println("D");
                    if (e.getButton() == MouseEvent.BUTTON1)
                        synchronized (bodies) {
                            bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT,
                                                           e.getX() - X_SHIFT,
                                                           e.getY() - Y_SHIFT));
                        }

                    if (isDrawing) {
                        Vector2 vec = new Vector2(e.getX() - X_SHIFT, e.getY() - Y_SHIFT);
                        vec.x = (int) (vec.x / Particle.RADIUS) * Particle.RADIUS;
                        vec.y = (int) (vec.y / Particle.RADIUS) * Particle.RADIUS;

                        if (!drawing.contains(vec)) {
                            drawing.add(vec);
                            System.out.println("D2");
                        }
                        System.out.println("D");
                    }
                }
            }
        });

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        PhysicsSimulator simulator = new PhysicsSimulator();

        while (true) {
            if (FPS <= 0) {
                Thread.sleep(100);
                continue;
            }

            final float TIMESTEP = 1f / FPS;
            synchronized (bodies) {
                for (int i = 0; i < STEPS; i++)
                    simulator.simulate(bodies, TIMESTEP);
            }

            X_SHIFT = frame.getWidth() / 2 - WALL / 2;
            Y_SHIFT = frame.getHeight() / 2 - FLOOR / 2;

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


    public static int FLOOR = 350;
    public static int WALL = 350;

    @Constant(name = "Size of Object", constraints = "1,100")
    public static int SIZE_OF_OBJECT = 1;
}
