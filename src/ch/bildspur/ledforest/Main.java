package ch.bildspur.ledforest;

import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

/**
 * Created by cansik on 16/08/16.
 */
public class Main {
    public static void main(String... args) {
        RenderSketch sketch = new RenderSketch();
        PApplet.runSketch(new String[]{"ch.bildspur.ledforest.sketch.RenderSketch "}, sketch);
    }
}
