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
            private boolean isDrawing;
            private final List<Vector2> drawing = new ArrayList<>();

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

                g.setColor(Color.GREEN);
                for (Vector2 v : drawing) {
                    circle.x = X_SHIFT + v.x - Particle.RADIUS / 2;
                    circle.y = Y_SHIFT + v.y - Particle.RADIUS / 2;

                    ((Graphics2D) g).fill(circle);
                }

                g.setColor(Color.BLACK);
                g.drawLine(0, Y_SHIFT + FLOOR, getWidth(), Y_SHIFT + FLOOR);
            }

            class MouseAdapterImpl extends MouseAdapter {

                private final List<Body> bodies;

                public MouseAdapterImpl(List<Body> bodies) {
                    this.bodies = bodies;
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1)
                        isDrawing = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    synchronized (bodies) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            bodies.clear();
                        } else if (isDrawing) {
                            if (drawing.isEmpty())
                                return;

                            List<Particle> particles = new ArrayList<>();
                            for (Vector2 v : drawing) {
                                particles.add(new Particle(v));
                            }

                            bodies.add(BodyFactory.makeBody(particles));
                            drawing.clear();
                            isDrawing = false;
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
