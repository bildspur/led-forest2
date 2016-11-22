package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.Hand;
import processing.core.PApplet;

import static processing.core.PConstants.PI;

/**
 * Created by cansik on 11.11.16.
 */
public class GlowInteraction implements TubeInteraction {
    float fadeSpeed;


    float fadeSpeedH;
    float fadeSpeedS;
    float fadeSpeedBIn;
    float fadeSpeedBOut;

    RenderSketch sketch;

    @Override
    public void init(RenderSketch sketch) {
        this.sketch = sketch;

        fadeSpeed = sketch.secondsToEasing(0.5f);

        fadeSpeedH = sketch.secondsToEasing(0.1f);
        fadeSpeedS = sketch.secondsToEasing(0.1f);
        fadeSpeedBIn = sketch.secondsToEasing(0.3f);
        fadeSpeedBOut = sketch.secondsToEasing(0.1f);
    }

    @Override
    public void ledOn(InteractionData data) {
        if (data.hand == null)
            return;

        data.led.getColor().fadeH(getHueByHand(data.hand), fadeSpeedH);
        data.led.getColor().fadeS(100 - (data.hand.grabStrength() * 100), fadeSpeedS);
        data.led.getColor().fadeB(100, fadeSpeedBIn);
    }

    @Override
    public void ledOff(InteractionData data) {
        data.led.getColor().fadeB(0, fadeSpeedBOut);
    }

    public static float getHueByHand(Hand h) {
        float roll = Math.abs(h.palmNormal().roll());
        return PApplet.map(roll, 0, PI, 0, 360);
    }
}
