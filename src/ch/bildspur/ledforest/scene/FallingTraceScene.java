package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Tube;

/**
 * Created by cansik on 18/09/16.
 */
public class FallingTraceScene extends Scene
{
    int layerCount = 24;
    int activeLayer = 23;
    int addValue = -1;

    int trace = 15;

    float fadeValue = sketch.secondsToEasing(0.1f);

    public FallingTraceScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "Falling Trace Scene";
    }

    public void update()
    {
        // do something every second
        if (sketch.frameCount % sketch.secondsToFrames(0.1f) != 0)
            return;

        for(int i = 0; i < trace; i++)
        {
            setLayer((activeLayer + i) % layerCount, sketch.g.color(255 - (255 / (trace - 1) * i)));
        }

        activeLayer += addValue;

        if(activeLayer < 0)
        {
            activeLayer = 23;
            addValue *= 1;
        }

        if(activeLayer >= layerCount)
        {
            activeLayer = 23;
        }
    }

    void setLayer(int layer, int c)
    {
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            Tube t =  sketch.getTubes().get(j);
            t.getLeds().get(layer).getColor().fadeB(sketch.g.brightness(c), fadeValue);
        }
    }
}