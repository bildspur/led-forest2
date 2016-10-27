package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import codeanticode.syphon.SyphonServer;
import processing.core.PGraphics;

/**
 * Created by cansik on 18/09/16.
 */
public class SyphonController extends BaseController {
    boolean enabled = false;

    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    SyphonServer syphon;

    public void setupSyphon() {
        syphon = new SyphonServer(sketch, "LED Forest 2");
    }

    public void sendScreenToSyphon() {
        sketch.loadPixels();
        syphon.sendScreen();
    }

    public void sendImageToSyphon(PGraphics p) {
        syphon.sendImage(p);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
