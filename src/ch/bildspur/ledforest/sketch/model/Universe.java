package ch.bildspur.ledforest.sketch.model;

import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;

import java.awt.*;
import java.util.List;

/**
 * Created by cansik on 25.10.16.
 */
public class Universe {
    int id;
    List<Tube> tubes;
    byte[] dmxData;

    public Universe(int id, List<Tube> tubes) {
        this.id = id;
        this.tubes = tubes;
        this.dmxData = new byte[512];
    }

    public void stageDmx(float luminosity, float trace) {
        byte[] data = new byte[dmxData.length];

        for (Tube tube : tubes) {
            for (LED led : tube.getLeds()) {

                Color c = new Color(led.getColor().getRGBColor());

                // red
                data[led.getAddress()] = calculateValue(c.getRed(), dmxData[led.getAddress()], luminosity, trace);

                // green
                data[led.getAddress() + 1] = calculateValue(c.getGreen(), dmxData[led.getAddress() + 1], luminosity, trace);

                // blue
                data[led.getAddress() + 2] = calculateValue(c.getBlue(), dmxData[led.getAddress() + 2], luminosity, trace);
            }
        }

        dmxData = data;
    }

    private byte calculateValue(float value, int last, float luminosity, float trace) {
        return (byte) ((value * luminosity) % 256);
    }

    public int getId() {
        return id;
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public byte[] getDmxData() {
        return dmxData;
    }
}
