package ch.bildspur.ledforest.ui.edit;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by cansik on 22.09.16.
 */
public class RodMap {

    RenderSketch sketch;
    PGraphics p;

    int width;
    int height;

    ArrayList<RodHandle> handles = new ArrayList<>();

    public RodMap(RenderSketch sketch, int width, int height)
    {
        this.sketch = sketch;
        this.width = width;
        this.height = height;

        p = sketch.createGraphics(width, height, PConstants.P2D);
    }

    public void refreshHandles()
    {
        handles = new ArrayList<>();

        for(Rod r : sketch.getVisualizer().getRods()) {
            RodHandle h = new RodHandle(width / 2f, height / 2f, r, p);
            h.moveTo(inTransform2d(r.getPosition()));
            handles.add(h);
        }
    }


    public PGraphics render()
    {
        p.beginDraw();
        p.background(0);

        drawHelpLines();

        // update handles and draw
        for(RodHandle h : handles) {
            h.moveTo(inTransform2d(h.getRod().getPosition()));
            h.update();
            h.render();
        }

        p.endDraw();
        return p;
    }

    void drawHelpLines()
    {
        // draw grid
        p.stroke(100);
        p.strokeWeight(1f);
        p.line(width / 2f, 0, width / 2f, height);
        p.line(0, height / 2f, width, height / 2f);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PVector inTransform2d(PVector v)
    {
        PVector intBox = sketch.getLeapMotion().getInteractionBox();
        return new PVector(
                PApplet.map(v.x, 0f - (intBox.x / 2f), intBox.x / 2f, 0, width),
                v.y,
                PApplet.map(v.z, 0f - (intBox.z / 2f), intBox.z / 2f, 0, height));
    }

    public PVector outTransform2d(PVector v)
    {
        PVector intBox = sketch.getLeapMotion().getInteractionBox();
        return new PVector(
                PApplet.map(v.x, 0, width, 0f - (intBox.x / 2f), intBox.x / 2f),
                v.y,
                PApplet.map(v.z, 0, height, 0f - (intBox.z / 2f), intBox.z / 2f));
    }

    public ArrayList<RodHandle> getHandles() {
        return handles;
    }
}
