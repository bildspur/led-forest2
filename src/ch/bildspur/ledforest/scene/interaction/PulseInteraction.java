package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 11.11.16.
 */
public class PulseInteraction implements TubeInteraction {
    RenderSketch sketch;

    @Override
    public void init(RenderSketch sketch) {
        this.sketch = sketch;
    }

    @Override
    public void ledOn(InteractionData data) {

    }

    @Override
    public void ledOff(InteractionData data) {

    }
}
