package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import codeanticode.syphon.SyphonClient;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.HashMap;

/**
 * Created by cansik on 19.11.16.
 */
public class SyphonInputScene extends Scene {
    SyphonClient client;
    PGraphics canvas;

    public SyphonInputScene(RenderSketch sketch) {
        super(sketch);
    }

    @Override
    public void init() {
        HashMap<String, String>[] servers = SyphonClient.listServers();
        PApplet.println("selecting syphon server " + servers[0].get("AppName"));
        client = new SyphonClient(sketch, servers[0].get("AppName"));
    }

    @Override
    public void update() {
        /*
        if (client.newFrame() && client != null) {
            canvas = client.getGraphics(canvas);

            float tubeWidth = canvas.width / (float) sketch.getTubes().size();

            for (int i = 0; i < sketch.getTubes().size(); i++) {
                setTubeColor(canvas, i, tubeWidth, sketch.getTubes().get(i));
            }
        }
        */
    }

    void setTubeColor(PGraphics img, int tubeIndex, float tubeWidth, Tube tube) {
        float tubePosition = tubeWidth * tubeIndex;
        float ledHeight = img.height / (float) tube.getLeds().size();

        for (int i = 0; i < tube.getLeds().size(); i++) {
            setLEDColor(img, tubeWidth, tubePosition, ledHeight, i, tube.getLeds().get(i));
        }
    }

    void setLEDColor(PGraphics img, float tubeWidth, float tubePosition, float ledHeight, int ledIndex, LED led) {
        int c = getAverage(img, (int) tubePosition, (int) (ledHeight * ledIndex), (int) tubeWidth, (int) ledHeight);
        led.getColor().fade(c, 0.5f);
    }

    int getAverage(PGraphics img, int x, int y, int w, int h) {
        // hsb mode
        int hue = 0;
        int sat = 0;
        int bright = 0;

        int count = w * h;

        for (int u = x; u < x + w; u++) {
            for (int v = y; v < y + h; v++) {
                int c = img.get(u, v);
                hue += sketch.g.hue(c);
                sat += sketch.g.saturation(c);
                bright += sketch.g.brightness(c);
            }
        }

        return sketch.g.color(hue / count, sat / count, bright / count);
    }

    public boolean isUnique() {
        return false;
    }

    public String getName() {
        return "Syphon Input Scene";
    }
}
