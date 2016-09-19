package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import peasy.PeasyCam;

/**
 * Created by cansik on 18/09/16.
 */
public class PeasyController extends BaseController {
    public void init(RenderSketch sketch)
    {
        super.init(sketch);
    }
    PeasyCam cam;

    public void setupPeasy()
    {
        sketch.smooth();
        cam = new PeasyCam(sketch, 100, 0, 0, 200);
        //cam.rotateX(radians(-20));
        cam.lookAt(0, 0, 0, 200, 5000);
        cam.setMinimumDistance(50);
        cam.setMaximumDistance(500);
    }

    public PeasyCam getCam() {
        return cam;
    }
}
