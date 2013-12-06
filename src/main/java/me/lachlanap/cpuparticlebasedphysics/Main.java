package me.lachlanap.cpuparticlebasedphysics;

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
                addMouseListener(new MouseAdapterImpl(bodies));
                addMouseMotionListener(new MouseAdapterImpl(bodies));
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
                            circle.x = b.convertX(p.pos) - Particle.RADIUS / 2;
                            circle.y = b.convertY(p.pos) - Particle.RADIUS / 2;

                            ((Graphics2D) g).fill(circle);
                        }
                    }
                }

                g.drawLine(0, FLOOR, getWidth(), FLOOR);
                g.drawLine(WALL, 0, WALL, getHeight());
            }

            class MouseAdapterImpl extends MouseAdapter {

                private final List<Body> bodies;

                public MouseAdapterImpl(
                        List<Body> bodies) {
                    this.bodies = bodies;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    synchronized (bodies) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            bodies.clear();
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT, e.getX(), e.getY()));
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    synchronized (bodies) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT, e.getX(), e.getY()));
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    synchronized (bodies) {
                        bodies.add(BodyFactory.makeBox(SIZE_OF_OBJECT, e.getX(), e.getY()));
                    }
                }
            }
        });

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        PhysicsSimulator simulator = new PhysicsSimulator();

        while (true) {
            frame.repaint();
            if (FPS <= 0) {
                Thread.sleep(100);
                continue;
            }

            final float TIMESTEP = 1f / FPS;
            synchronized (bodies) {
                simulator.simulate(bodies, TIMESTEP);
            }

            Thread.sleep((int) (TIMESTEP * 1000));
        }
    }

    @Constant(name = "FPS", constraints = "0,100")
    public static int FPS = 50;
    public static int FLOOR = 350;
    public static int WALL = 350;

    @Constant(name = "Size of Object", constraints = "1,100")
    public static int SIZE_OF_OBJECT = 1;
}
