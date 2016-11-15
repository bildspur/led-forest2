package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 18/09/16.
 */
public abstract class BaseController {
    RenderSketch sketch;

    public void init(RenderSketch sketch) {
        this.sketch = sketch;
    }

    public void stop() {
    }
}
