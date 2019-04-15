import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class TrackCreater implements  Runnable {

    private Display display;
    private BufferedImage image;

    public TrackCreater() {
        display = new Display();

        // Create the background image
        image = new BufferedImage(Config.windowSize.width, Config.windowSize.height, BufferedImage.TYPE_INT_RGB);
        int[] colorData = new int[Config.windowSize.width * Config.windowSize.height];
        Arrays.fill(colorData, 0xffffff);
        image.setRGB(0, 0, Config.windowSize.width, Config.windowSize.height, colorData, 0, 0);

        Setup();

        // Start the main thread
        new Thread(this).start();
    }

    private void Setup() {
        /*
        // Attempt to load the track
        Track t;
        t = TrackLoader.Load("default");
        if (t != null) {
            track = new TrackController(Config.windowSize.width, Config.windowSize.height, t);
            track.generateFull();
            isCreatingTrack = false;
        } else {

            // Make the track placer!
            tp = new TrackPlacer(Config.windowSize.width, Config.windowSize.height);
            isCreatingTrack = true;
            display.addKeyListener(tp);
            display.addMouseListener(tp);
            display.canvas.addMouseListener(tp);
        }
        */
    }

    @Override
    public void run() {
        // Get the time since our last update
        long pastUpdateTime = System.nanoTime();
        double delayTime = updateDelayTime(Config.updates_per_second);

        while (true) {

            // If the time since out last update is greater than the time per update, ... update
            if (System.nanoTime() - pastUpdateTime > delayTime) {
                pastUpdateTime += delayTime;
                // Here is where all update code will be located
                //TODO
                /*
                if (isCreatingTrack) {
                    TrackPlacer.PlacingPhase p = tp.getPhase();
                    if (p == TrackPlacer.PlacingPhase.COMPLETE) {
                        getTrackFromPlacer();
                    } else {
                        tp.render();
                    }
                } else {
                    car.update();
                    //car2.update();
                    testCarCollision(car.getCar());
                }
                */
            }

            draw();
        }
    }

    private double updateDelayTime(int _updates_per_second) {
        return Math.pow(10, 9) / _updates_per_second;
    }

    private void draw() {
        BufferStrategy b = display.canvas.getBufferStrategy();
        if (b == null) {
            display.canvas.createBufferStrategy(2);
            b = display.canvas.getBufferStrategy();
        }

        Graphics g = b.getDrawGraphics();
        /*
        if (isCreatingTrack) {

            tp.draw(g);

        } else {

            // Draw background
            //g.drawImage(image, 0, 0, Config.windowSize.width, Config.windowSize.height, null);

            // Draw track
            // TODO draw track
            track.draw(g);

            // Draw car
            car.draw(g);
            //car2.draw(g);

        }
        */
        g.dispose();
        b.show();
    }

}
