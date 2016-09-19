package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Created by cansik on 18/09/16.
 */
public class ColorController extends BaseController {
    PGraphics g;

    public void init(RenderSketch sketch)
    {
       super.init(sketch);
        g = sketch.g;
    }

    public void setColor(int c, float fadeTime)
    {
        PApplet.println("Setting Color: " + g.hue(c) + ", " + g.saturation(c) + ", " + g.brightness(c));
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                sketch.getTubes().get(j).getLeds().get(i).getColor().fade(c, fadeTime);
            }
        }
    }

    public void setColorH(float c, float fadeTime)
    {
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                sketch.getTubes().get(j).getLeds().get(i).getColor().fadeH(c, fadeTime);
            }
        }
    }

    public void setColorS(float c, float fadeTime)
    {
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                sketch.getTubes().get(j).getLeds().get(i).getColor().fadeS(c, fadeTime);
            }
        }
    }

    public void setColorB(float c, float fadeTime)
    {
        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                sketch.getTubes().get(j).getLeds().get(i).getColor().fadeB(c, fadeTime);
            }
        }
    }


    public void setRandomColor(float fadeTime)
    {
        int c = g.color(sketch.random(0, 360), sketch.random(0, 100), sketch.random(0, 100));
        setColor(c, fadeTime);
    }

    public void setColorToWhite()
    {
        sketch.getSceneManager().setRunning(sketch.getSceneManager().isRunning());
        setColor(g.color(100, 0, 100), sketch.secondsToEasing(1));
    }
}
