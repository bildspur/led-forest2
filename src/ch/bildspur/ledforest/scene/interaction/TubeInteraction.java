package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 11.11.16.
 */
public interface TubeInteraction {
    void init(RenderSketch sketch);

    void ledOn(InteractionData data);

    void ledOff(InteractionData data);
}
