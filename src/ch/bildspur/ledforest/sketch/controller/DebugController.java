package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cansik on 01.11.16.
 */
public class DebugController extends BaseController {
    final float textSize = 12;
    final List<String> messages = new ArrayList<>();

    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public void showInfo() {
        if (sketch.getDrawMode() == 3)
            showRodInfo();

        sketch.getPeasy().getCam().beginHUD();
        sketch.fill(255);
        sketch.textAlign(PConstants.LEFT, PConstants.BOTTOM);
        sketch.textSize(textSize);
        sketch.text("Draw Mode: " + sketch.getDrawMode() + "D", 5, 15);
        sketch.text("FPS: " + PApplet.round(sketch.frameRate), 5, 30);
        sketch.text("Color Scene: " + sketch.getSceneManager().getActiveColorScene().getName(), 150, 15);
        sketch.text("Pattern Scene: " + sketch.getSceneManager().getActivePatternScene().getName(), 150, 30);


        // show messages
        float h = sketch.height * (3f / 4f);
        float w = 100f;

        for (int i = 0; i < messages.size(); i++) {
            sketch.text(messages.get(i), w, h + (i * textSize + 2));
        }

        sketch.getPeasy().getCam().endHUD();
        messages.clear();
    }

    void showRodInfo() {
        // draw stub info
        sketch.fill(255);
        sketch.textSize(textSize);

        for (int i = 0; i < sketch.getVisualizer().getRods().size(); i++) {
            sketch.pushMatrix();
            Rod r = sketch.getVisualizer().getRods().get(i);
            sketch.translate(r.getPosition().x, 0, r.getPosition().z);
            sketch.text(r.getTube().getUniverse() + "|" + i, 0, 0);
            sketch.popMatrix();
        }
    }
}
