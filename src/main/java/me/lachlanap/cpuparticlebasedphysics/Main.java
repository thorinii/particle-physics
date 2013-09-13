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

public class Main {

    public static final int SIZE = 10;
    public static final int FLOOR = 350;

    public static void main(String[] args) throws InterruptedException {
        final List<Particle> particles = new ArrayList<>();

        particles.add(new Particle(30, 30));

        JFrame frame = new JFrame("CPU Particle-Based Physics");
        frame.getContentPane().add(new JPanel() {
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        particles.clear();
                        particles.add(new Particle(30, 30));
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Ellipse2D.Float circle = new Ellipse2D.Float(0, 0, SIZE, SIZE);
                g.setColor(Color.RED);

                for (Particle p : particles) {
                    circle.x = p.x - SIZE / 2;
                    circle.y = p.y - SIZE / 2;

                    ((Graphics2D) g).fill(circle);
                }

                g.drawLine(0, FLOOR, getWidth(), FLOOR);
            }
        });

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        final float TIMESTEP = 1 / 20f;
        while (true) {
            for (Particle p : particles) {
                p.update(TIMESTEP);

                if (p.y > FLOOR && p.py < FLOOR) {
                    p.py = -(p.py - FLOOR) + FLOOR + 10f * TIMESTEP;
                }
            }

            frame.repaint();
            Thread.sleep(50);
        }
    }
}
