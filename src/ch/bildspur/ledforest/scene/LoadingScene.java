package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

import static processing.core.PConstants.*;

/**
 * Created by cansik on 15.11.16.
 */
public class LoadingScene extends Scene {
    boolean debugInfo = false;

    public LoadingScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName() {
        return "Loading Scene";
    }

    float r = 0;

    public void init() {
    }

    public void update() {
        sketch.getPeasy().getCam().beginHUD();

        sketch.fill(255);
        sketch.textAlign(CENTER, CENTER);

        sketch.textSize(24);
        sketch.text("LED Forest 2", sketch.width / 2, sketch.height / 2 - 20);

        sketch.textSize(18);
        sketch.text("loading...", sketch.width / 2, sketch.height / 2 + 20);

        sketch.image(sketch.getLogo(),
                (sketch.width / 2) - (sketch.getLogo().width / 2),
                sketch.height - sketch.getLogo().height - 10);

        showLoadingCircle(sketch.width / 2, sketch.height / 2 - 10);

        if (debugInfo)
            showDebugInfo();

        sketch.getPeasy().getCam().endHUD();
    }

    private void showLoadingCircle(float x, float y) {
        sketch.ellipseMode(CENTER);
        sketch.noFill();
        sketch.stroke(255);

        sketch.strokeWeight(5);
        sketch.arc(x, y, 240, 240, PApplet.radians(0 + r), PApplet.radians(100 + r));

        sketch.strokeWeight(5);
        sketch.arc(x, y, 220, 220, PApplet.radians(0 + (4 * r)), PApplet.radians(80 + (4 * r)));

        sketch.strokeWeight(5);
        sketch.arc(x, y, 200, 200, PApplet.radians(0 + (8 * r)), PApplet.radians(50 + (8 * r)));

        sketch.ellipseMode(CORNER);
        r++;
    }

    private void showDebugInfo() {
        String[] msgs = sketch.getInterceptor().getAll();

        sketch.fill(90, 100, 100);
        sketch.textAlign(LEFT, BOTTOM);
        sketch.textSize(10);

        for (int i = 0; i < msgs.length; i++) {
            String msg = msgs[msgs.length - i - 1];
            if (msg != null)
                sketch.text(msg, 20, 20 + (12 * i));
        }
    }

    public boolean isDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(boolean debugInfo) {
        this.debugInfo = debugInfo;
    }
}
