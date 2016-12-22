package ch.bildspur.ledforest;

import ch.bildspur.ifttt.IFTTTClient;
import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

import java.io.IOException;

/**
 * Created by cansik on 16/08/16.
 */
public class Main {
    public static void main(String... args) {
        int restartCounter = 0;

        RenderSketch sketch = null;

        while (restartCounter < 5) {
            long frameCount = -1;
            sketch = new RenderSketch();

            sketch.setFullscreenEnabled(isFullscreen(args));
            sketch.setFullscreenDisplay(getFullScreenDisplay(args));

            PApplet.runSketch(new String[]{"ch.bildspur.ledforest.sketch.RenderSketch "}, sketch);

            while (frameCount < sketch.frameCount) {
                frameCount = sketch.frameCount;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // restart
            try {
                // send info
                String error = sketch.getInterceptor().toString("<br>");
                IFTTTClient.sendStatus("Restart", sketch.getApplicationState(), error);

                if (sketch.getOsc().isEnabled())
                    sketch.getOsc().stop();

                if (sketch.getAudioFX().isEnabled())
                    sketch.getAudioFX().stop();

                sketch.getSurface().stopThread();
                sketch.getSurface().setVisible(false);
                sketch.noLoop();
                sketch.dispose();
            } catch (Exception ex) {
                PApplet.println("Shutdown not nicely!");
            }

            restartCounter++;
        }

        // last thing if nothing else works
        IFTTTClient.sendStatus("Reboot", "", "");
        reboot();
    }

    private static void reboot() {
        Runtime runtime = Runtime.getRuntime();
        try {
            PApplet.println("rebooting!");
            String[] cmd = {"osascript", "-e", "tell app \"System Events\" to restart"};
            runtime.exec(cmd);
            Runtime.getRuntime().halt(100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFullscreen(String... args) {
        for (String arg : args)
            if (arg.equals("-fullscreen"))
                return true;
        return false;
    }

    private static int getFullScreenDisplay(String... args) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals("-fullscreen") && i < args.length - 1)
                return Integer.parseInt(args[i + 1]);
        return 0;
    }
}
