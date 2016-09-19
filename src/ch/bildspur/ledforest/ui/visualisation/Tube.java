package ch.bildspur.ledforest.ui.visualisation;

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
        leds = new ArrayList<>();
        this.universe = universe;
    }

    public ArrayList<LED> getLeds() {
        return leds;
    }

    public int getUniverse() {
        return universe;
    }
}