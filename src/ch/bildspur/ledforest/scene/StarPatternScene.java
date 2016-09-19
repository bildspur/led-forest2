package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;

/**
 * Created by cansik on 18/09/16.
 */
public class StarPatternScene extends Scene
{
    float randomOnFactor = 0.95f;
    float randomOffFactor = 0.8f;

    public StarPatternScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "StarPattern Scene";
    }

    public void init()
    {
        sketch.getColors().setColorB(0, sketch.secondsToEasing(1));
    }

    public void update()
    {
        if (sketch.frameCount % sketch.secondsToFrames(0.5f) != 0)
            return;

        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            Tube t =  sketch.getTubes().get(j);
            for (int i = 0; i < t.getLeds().size(); i++)
            {
                LED led = t.getLeds().get(i);

                float ledBrightness = sketch.g.brightness(led.getColor().getColor());
                float fadeSpeed = 1; //random(secondsToEasing(10), secondsToEasing(1));

                if (ledBrightness > 10)
                {
                    //led is ON
                    if (sketch.random(0, 1) > randomOffFactor)
                    {
                        led.getColor().fadeB(0, sketch.secondsToEasing(fadeSpeed));
                    }
                } else
                {
                    //led is OFF
                    if (sketch.random(0, 1) > randomOnFactor)
                    {
                        led.getColor().fadeB(sketch.random(50, 100), sketch.secondsToEasing(fadeSpeed));
                    }
                }
            }
        }
    }
}