package ch.bildspur.ledforest.ui.visualisation;

import processing.core.PGraphics;

import java.util.ArrayList;

/**
 * Created by cansik on 18/09/16.
 */
public class Tube
{
    int universe;
    ArrayList<LED> leds;

    public Tube(int universe)
    {
        this(universe, 0, null);
    }

    public Tube(int universe, int ledCount, PGraphics g)
    {
        this.universe = universe;
        initLED(ledCount, g);
    }

    public void initLED(int ledCount, PGraphics g)
    {
        leds = new ArrayList<>();

        for(int i = 0; i < ledCount; i++)
            leds.add(new LED(g, i, g.color(0, 100, 100)));
    }

    public ArrayList<LED> getLeds() {
        return leds;
    }

    public int getUniverse() {
        return universe;
    }
}