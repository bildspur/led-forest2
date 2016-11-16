package ch.bildspur.ledforest;

import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

/**
 * Created by cansik on 16/08/16.
 */
public class Main {
    public static void main(String... args) {
        RenderSketch sketch = new RenderSketch();

        sketch.setFullscreenEnabled(isFullscreen(args));
        sketch.setFullscreenDisplay(getFullScreenDisplay(args));

        PApplet.runSketch(new String[]{"ch.bildspur.ledforest.sketch.RenderSketch "}, sketch);
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
