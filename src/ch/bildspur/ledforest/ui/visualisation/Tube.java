package ch.bildspur.ledforest.ui.visualisation;

import processing.core.PGraphics;

import java.util.ArrayList;

/**
 * Created by cansik on 18/09/16.
 */
public class Tube {
    public final int LED_ADDRESS_SIZE = 3;

    int universe;
    ArrayList<LED> leds;

    public Tube(int universe) {
        this(universe, 0, 0, null);
    }

    public Tube(int universe, int ledCount, int addressStart, PGraphics g) {
        this.universe = universe;
        initLED(ledCount, addressStart, g);
    }

    public void initLED(int ledCount, int addressStart, PGraphics g) {
        leds = new ArrayList<>();

        for (int i = 0; i < ledCount; i++)
            leds.add(new LED(g, addressStart + (i * LED_ADDRESS_SIZE), g.color(0, 100, 100)));
    }

    public ArrayList<LED> getLeds() {
        return leds;
    }

    public int getUniverse() {
        return universe;
    }

    public void setUniverse(int universe) {
        this.universe = universe;
    }

    public int getStartAddress() {
        return leds.size() > 0 ? leds.get(0).getAddress() : 0;
    }

    public int getEndAddress() {
        return leds.size() > 0 ? leds.get(leds.size() - 1).getAddress() : 0;
    }
}