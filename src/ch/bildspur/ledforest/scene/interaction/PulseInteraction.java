package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 11.11.16.
 */
public class PulseInteraction implements TubeInteraction {
    RenderSketch sketch;

    float pulseValue = 50;
    float pulseIncrement = 0.05f;

    float fadeTime;
    float fadeSpeedS;

    @Override
    public void init(RenderSketch sketch) {
        this.sketch = sketch;

        fadeTime = sketch.secondsToEasing(0.5f);
        fadeSpeedS = sketch.secondsToEasing(0.1f);
    }

    @Override
    public void ledOn(InteractionData data) {
        data.led.getColor().fadeH(GlowInteraction.getHueByHand(data.hand), fadeSpeedS);
        data.led.getColor().fadeS(100 - (data.hand.grabStrength() * 100), fadeSpeedS);

        data.led.getColor().fadeB(pulseValue, sketch.secondsToEasing(fadeTime));

        // increment
        pulseValue += pulseIncrement;

        if (pulseValue > 100 || pulseValue < 0)
            pulseIncrement *= -1;
    }

    @Override
    public void ledOff(InteractionData data) {
        data.led.getColor().fadeB(0, sketch.secondsToEasing(fadeTime));
    }
}
