package ch.bildspur.ledforest.ui.visualisation;

import ch.bildspur.ledforest.ui.FadeColor;
import processing.core.PGraphics;

/**
 * Created by cansik on 18/09/16.
 */
public class LED
{
    int address;
    FadeColor color;

    public LED(PGraphics g, int address, int color)
    {
        this.address = address;
        this.color = new FadeColor(g, color);
    }

    public FadeColor getColor() {
        return color;
    }

    public void setColor(FadeColor color) {
        this.color = color;
    }
}
