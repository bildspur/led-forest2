package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 14.12.16.
 */
public class AudioFXController extends BaseController {
    boolean enabled = false;

    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}