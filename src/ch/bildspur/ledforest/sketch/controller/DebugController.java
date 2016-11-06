package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import processing.core.PApplet;

/**
 * Created by cansik on 01.11.16.
 */
public class DebugController extends BaseController {
    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    public void showInfo() {
        if (sketch.getDrawMode() == 3)
            showRodInfo();

        sketch.getPeasy().getCam().beginHUD();
        sketch.fill(255);
        sketch.textSize(12);
        sketch.text("Draw Mode: " + sketch.getDrawMode() + "D", 5, 15);
        sketch.text("FPS: " + PApplet.round(sketch.frameRate), 5, 30);
        sketch.text("Color Scene: " + sketch.getSceneManager().getActiveColorScene().getName(), 150, 15);
        sketch.text("Pattern Scene: " + sketch.getSceneManager().getActivePatternScene().getName(), 150, 30);
        sketch.getPeasy().getCam().endHUD();
    }

    void showRodInfo() {
        // draw stub info
        sketch.fill(255);
        sketch.textSize(12);

        for (int i = 0; i < sketch.getVisualizer().getRods().size(); i++) {
            sketch.pushMatrix();
            Rod r = sketch.getVisualizer().getRods().get(i);
            sketch.translate(r.getPosition().x, 0, r.getPosition().z);
            sketch.text(r.getTube().getUniverse() + "|" + i, 0, 0);
            sketch.popMatrix();
        }
    }
}
