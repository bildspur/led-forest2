package ch.bildspur.ledforest.ui;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Created by cansik on 18/09/16.
 */
public class FadeColor
{
    /* Importent: This is for HSB only! */
    final float hmax = 360;

    PVector current = new PVector();
    PVector target = new PVector();
    PVector easingVector = new PVector();

    PGraphics g;

    public FadeColor(PGraphics g)
    {
        this.g = g;
    }

    public FadeColor(PGraphics g, int c)
    {
        this(g);
        current = colorToVector(c);
        target = colorToVector(c);
    }

    public void update()
    {
        PVector delta = target.copy().sub(current);

        // Hue => 360° (can ease to both sides)
        float otherDelta = hmax - Math.abs(delta.x);

        if (Math.abs(otherDelta) < Math.abs(delta.x))
        {
            delta.x = otherDelta * (delta.x < 0 ? 1 : -1);
        }

        // no matrix multiplication possible
        current.x = (current.x + delta.x * easingVector.x) % hmax;
        current.x = current.x < 0 ? 360 + current.x : current.x;

        current.y += delta.y * easingVector.y;
        current.z += delta.z * easingVector.z;
    }

    public void setColor(int c)
    {
        current = colorToVector(c);
        target = colorToVector(c);
    }

    public void fade(int t, float easing)
    {
        easingVector = new PVector(easing, easing, easing);
        this.target = colorToVector(t);
    }

    public void fadeH(float h, float easing)
    {
        easingVector.x = easing;
        target.x = h;
    }

    public void fadeS(float s, float easing)
    {
        easingVector.y = easing;
        target.y = s;
    }

    public void fadeB(float b, float easing)
    {
        easingVector.z = easing;
        target.z = b;
    }

    public int getColor()
    {
        return vectorToColor(current);
    }

    private PVector colorToVector(int c)
    {
        return new PVector(g.hue(c), g.saturation(c), g.brightness(c));
    }

    private int vectorToColor(PVector v)
    {
        return g.color(Math.round(v.x), Math.round(v.y), Math.round(v.z));
    }
}