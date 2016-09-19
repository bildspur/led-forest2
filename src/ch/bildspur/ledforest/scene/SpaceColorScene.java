package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;

/**
 * Created by cansik on 18/09/16.
 */
public class SpaceColorScene extends Scene
{
    int h = 0;
    float fadeSpeed = sketch.secondsToEasing(0.8f);

    public SpaceColorScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "Space Color Scene";
    }
    public void init() {
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
                led.getColor().fadeS(100 - (4*i), fadeSpeed);
            }
        }

        // update colorwheel
        h = (h + 1) % 361;
    }
}