package me.lachlanap.cpuparticlebasedphysics;

import javax.swing.JFrame;
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
        frame.getContentPane().add(new WorldRenderPanel(world));

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
