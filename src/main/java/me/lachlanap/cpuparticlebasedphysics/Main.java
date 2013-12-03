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
        LCTFrame lCTFrame = new LCTFrame(manager);
        lCTFrame.setVisible(true);

        final List<Particle> particles = new ArrayList<>();

        particles.add(new Particle(30, 30));

        final JFrame frame = new JFrame("CPU Particle-Based Physics");
        frame.getContentPane().add(new JPanel() {
            {
                addMouseListener(new MouseAdapterImpl(particles));
                addMouseMotionListener(new MouseAdapterImpl(particles));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, RADIUS, RADIUS);
                g.setColor(Color.RED);

                synchronized (particles) {
                    for (Particle p : particles) {
                        circle.x = p.x - RADIUS / 2;
                        circle.y = p.y - RADIUS / 2;

                        ((Graphics2D) g).fill(circle);
                    }
                }

                g.drawLine(0, FLOOR, getWidth(), FLOOR);
            }

            class MouseAdapterImpl extends MouseAdapter {

                private final List<Particle> particles;

                public MouseAdapterImpl(
                        List<Particle> particles) {
                    this.particles = particles;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    synchronized (particles) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            particles.clear();
                            particles.add(new Particle(30, 30));
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            particles.add(new Particle(e.getX(), e.getY()));
                        }
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    synchronized (particles) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            particles.add(new Particle(e.getX(), e.getY()));
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    synchronized (particles) {
                        particles.add(new Particle(e.getX(), e.getY()));
                    }
                }
            }
        });

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        while (true) {
            frame.repaint();
            if (FPS <= 0) {
                Thread.sleep(100);
                continue;
            }

            final float TIMESTEP = 1f / FPS;
            synchronized (particles) {
                List<Particle> oldSet = new ArrayList<>(particles);
                particles.clear();

                for (Particle p : oldSet) {
                    boolean keep = true;
                    Particle newP = p.clone();

                    if (p.y < RADIUS) {
                        newP.y = RADIUS;

                        if (p.py > RADIUS)
                            newP.py = -(p.py - RADIUS) + RADIUS;
                        //else
                        //    newP.py = 0;
                    } else if (p.y > FLOOR) {
                        newP.y = FLOOR - ((float) Math.random() * 0.1f);

                        if (p.py < FLOOR)
                            newP.py = -(p.py - FLOOR) + FLOOR;
                        //else
                        //    newP.py = FLOOR;
                    }

                    if (p.x < RADIUS) {
                        newP.x = RADIUS;

                        if (p.px > RADIUS)
                            newP.px = -(p.px - RADIUS) + RADIUS;
                        //else
                        //    newP.px = RADIUS;
                    } else if (p.x > frame.getWidth()) {
                        newP.x = frame.getWidth();

                        if (p.px > 0)
                            newP.px = -(p.px - frame.getWidth()) + frame.getWidth();
                        //else
                        //    newP.px = frame.getWidth();
                    }

                    float tforceX = 0;
                    float tforceY = 0;
                    for (Particle o : oldSet) {
                        if (p == o)
                            continue;

                        float dist2 = p.dist2(o);
                        if (dist2 > RADIUS * RADIUS)
                            continue;
                        else if (dist2 <= 0.3f) {
                            tforceX += (Math.random() - .5f) * 0.0166f;
                            tforceY += (Math.random() - .5f) * 0.0166f;
                            keep = false;
                            System.out.println("Death");
                            continue;
                        }

                        float rx = p.x - o.x;
                        float ry = p.y - o.y;

                        float vx = (p.x - p.px) - (o.x - o.px);
                        float vy = (p.y - p.py) - (o.y - o.py);

                        float rabs = (float) Math.abs(Math.sqrt(dist2));

                        float forceX = -K * (D - rabs) * (rx / rabs) - D * vx;
                        float forceY = -K * (D - rabs) * (ry / rabs) - D * vy;

                        tforceX += forceX;
                        tforceY += forceY;
                    }

                    tforceX *= D;
                    tforceY *= D;

                    newP.update(0.0166f, tforceX, tforceY);
                    if (keep)
                        particles.add(newP);
                }
            }

            Thread.sleep((int) (TIMESTEP * 1000));
        }
    }
    @Constant(name = "FPS", constraints = "0,100")
    public static int FPS = 12;
    public static final int FLOOR = 350;
    @Constant(name = "K", constraints = "-1,50")
    public static float K = 1;
    @Constant(name = "D", constraints = "-10,100")
    public static float D = -0.1f;
    @Constant(name = "Radius", constraints = "1, 50")
    public static float RADIUS = 50;
}
