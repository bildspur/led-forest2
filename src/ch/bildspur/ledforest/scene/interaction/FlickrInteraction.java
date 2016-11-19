package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.sketch.RenderSketch;

import java.util.Random;

/**
 * Created by cansik on 11.11.16.
 */
public class FlickrInteraction implements TubeInteraction {
    RenderSketch sketch;

    double lightUpValue = 0.90;

    float fadeTime;
    float fadeSpeedS;

    Random rand;

    @Override
    public void init(RenderSketch sketch) {
        this.sketch = sketch;
        this.rand = new Random();
        this.fadeTime = sketch.secondsToEasing(0.5f);
        fadeSpeedS = sketch.secondsToEasing(0.1f);
    }

    @Override
    public void ledOn(InteractionData data) {
        if (data.led.getColor().isFading())
            return;

        //this.fadeTime = PApplet.map(data.hand.palmNormal().roll(), -0.5f, -3.14f, 0.5f, 1f);

        data.led.getColor().fadeS(100 - (data.hand.grabStrength() * 100), fadeSpeedS);
        data.led.getColor().fadeH(GlowInteraction.getHueByHand(data.hand), fadeSpeedS);

        if (rand.nextFloat() >= lightUpValue) {
            data.led.getColor().fadeB(100, sketch.secondsToEasing(fadeTime));
        } else
            data.led.getColor().fadeB(0, sketch.secondsToEasing(fadeTime));
    }

    @Override
    public void ledOff(InteractionData data) {
        data.led.getColor().fadeB(0, sketch.secondsToEasing(fadeTime));
    }
}
