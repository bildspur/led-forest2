package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 18/09/16.
 */
public class WhiteColorScene extends Scene
{
    float fadeSpeed = sketch.secondsToEasing(0.8f);

    public WhiteColorScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "White Color Scene";
    }


    public void update()
    {
        if (sketch.frameCount % sketch.secondsToFrames(1) != 0)
            return;

        sketch.getColors().setColorS(0, fadeSpeed);
    }
}
