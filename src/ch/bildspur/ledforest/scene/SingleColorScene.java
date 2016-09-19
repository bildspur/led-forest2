package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;

/**
 * Created by cansik on 18/09/16.
 */
public class SingleColorScene extends Scene
{
    int h = 0;
    float fadeSpeed = sketch.secondsToEasing(5);

    public SingleColorScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "Single Color Scene";
    }

    public void update()
    {
        // do something every second
        if (sketch.frameCount % sketch.secondsToFrames(1) != 0)
            return;

        // iterate over every led
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            Tube t =  sketch.getTubes().get(j);
            for (int i = 0; i < t.getLeds().size(); i++)
            {
                LED led = t.getLeds().get(i);
                led.getColor().fadeH(h, fadeSpeed);
                led.getColor().fadeS(100, fadeSpeed);
            }
        }

        // update colorwheel
        h = h > 360 ? 0 : h + 1;
    }
}