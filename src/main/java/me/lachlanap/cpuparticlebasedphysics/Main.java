package me.lachlanap.cpuparticlebasedphysics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.Timer;
import me.lachlanap.cpuparticlebasedphysics.ui.WorldRenderPanel;
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

        JFrame frame = new JFrame("CPU Particle-Based Physics");
        final WorldRenderPanel wrp = new WorldRenderPanel(world);
        frame.getContentPane().add(wrp);

        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        final Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (FPS == 0)
                    return;
                wrp.repaint();

                int timestep = 1000 / Math.max(FPS, 1);
                timer.setDelay(timestep);
            }
        });
        timer.start();
    }

    @Constant(name = "FPS", constraints = "0,100")
    public static int FPS = 50;

    @Constant(name = "Steps", constraints = "1,100")
    public static int STEPS = 1;
}
