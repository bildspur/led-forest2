package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import processing.core.PApplet;

/**
 * Created by cansik on 18/09/16.
 */
public class WaveStarsPattern extends Scene
{
    float fadeValue = sketch.secondsToEasing(0.8f);

    //vars
    int orbitRadius = 50; // 100 is max

    int waveStrengthHorizontal= 5;
    int waveStrengthVertical= 5;

    float speed = 1;

    boolean directionDown = false;

    public WaveStarsPattern(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "Wave Stars Scene";
    }

    public void init()
    {
        waveStrengthHorizontal = (int)sketch.random(0, 20);
        waveStrengthVertical = (int)sketch.random(0, 20);
        directionDown = ((int)sketch.random(0, 2) == 1);

        PApplet.println("vertical changed to " + waveStrengthVertical);
        PApplet.println("horizontal changed to " + waveStrengthHorizontal);
        PApplet.println("direction is down = " + directionDown);
    }

    public void update()
    {
        if (sketch.frameCount % sketch.secondsToFrames(0.1f) != 0)
            return;

        // change strength every 5 seconds
    /*
    if (frameCount % 300 == 0)
     {
     waveStrengthHorizontal = (waveStrengthHorizontal + 1) % 100;
     waveStrengthVertical = (waveStrengthVertical + 5) % 100;
     println("");
     println("vertical changed to " + waveStrengthVertical);
     println("horizontal changed to " + waveStrengthHorizontal);
     }
     */

        // iterate over every led
        for (int u = 0; u < sketch.getTubes().size(); u++)
        {
            Tube t =  sketch.getTubes().get(u);
            for (int v = 0; v < t.getLeds().size(); v++)
            {
                int ledIndex = v;

                if (directionDown)
                    ledIndex = (t.getLeds().size() - 1 - v);

                float strength = u * waveStrengthHorizontal + v * waveStrengthVertical;
                int finalSpeed = (int)(360 / speed);

                float angle = (float)((((sketch.frameCount + strength) % finalSpeed) * Math.PI) / 90);

                float x = (float)(orbitRadius * Math.cos(angle));
                float y = (float)(orbitRadius * Math.sin(angle));

                // set brightness by x value
                t.getLeds().get(ledIndex).getColor().fadeB((y + x) % 100, fadeValue);
            }
        }
    }
}
