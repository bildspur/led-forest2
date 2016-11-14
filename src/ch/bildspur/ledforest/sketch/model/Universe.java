package ch.bildspur.ledforest.sketch.model;

import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;

import java.awt.*;
import java.util.List;

/**
 * Created by cansik on 25.10.16.
 */
public class Universe {
    final int maxLuminance = 255;

    int id;
    List<Tube> tubes;
    byte[] dmxData;

    public Universe(int id, List<Tube> tubes) {
        this.id = id;
        this.tubes = tubes;
        this.dmxData = new byte[512];
    }

    public void stageDmx(float luminosity, float response, float trace) {
        byte[] data = new byte[dmxData.length];

        for (Tube tube : tubes) {
            for (LED led : tube.getLeds()) {

                Color c = new Color(led.getColor().getRGBColor());

                // red
                data[led.getAddress()] = calculateValue(c.getRed(),
                        dmxData[led.getAddress()] & 0xFF,
                        luminosity, response, trace);

                // green
                data[led.getAddress() + 1] = calculateValue(c.getGreen(),
                        dmxData[led.getAddress() + 1] & 0xFF,
                        luminosity, response, trace);

                // blue
                data[led.getAddress() + 2] = calculateValue(c.getBlue(),
                        dmxData[led.getAddress() + 2] & 0xFF,
                        luminosity, response, trace);
            }
        }

        dmxData = data;
    }

    private byte calculateValue(float value, int last, float luminosity, float response, float trace) {
        // normalize value
        float normValue = value / (float) maxLuminance;
        float normLast = last / (float) maxLuminance;

        // add response
        normValue = normalisedTunableSigmoid(normValue, response);

        // add luminance
        normValue *= luminosity;

        // add trace
        normValue = Math.min(1, (normLast * trace) + normValue);

        return (byte) (normValue * maxLuminance);
    }

    /**
     * Normalised tunable sigmoid function.
     *
     * @param x Normalized x value
     * @param k Normalized tune value
     * @return Normalised sigmoid result.
     */
    private float normalisedTunableSigmoid(float x, float k) {
        return (x - x * k) / (k - Math.abs(x) * 2 * k + 1);
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
