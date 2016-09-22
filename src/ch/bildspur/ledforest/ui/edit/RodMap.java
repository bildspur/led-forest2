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

    boolean mouseDown = false;
    RodHandle currentHandle = null;
    PVector mouseDelta = null;

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
            h.moveTo(inTransform2d(h.getRod().getPosition2d()));
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
                PApplet.map(v.y, 0f - (intBox.z / 2f), intBox.z / 2f, 0, height));
    }

    public PVector outTransform2d(PVector v)
    {
        PVector intBox = sketch.getLeapMotion().getInteractionBox();
        return new PVector(
                PApplet.map(v.x, 0, width, 0f - (intBox.x / 2f), intBox.x / 2f),
                PApplet.map(v.y, 0, height, 0f - (intBox.z / 2f), intBox.z / 2f));
    }

    public ArrayList<RodHandle> getHandles() {
        return handles;
    }

    public void mousePressed(PVector mouse) {
        PApplet.println("mouse pressed: " + mouse.toString());
        if (!mouseDown)
        {
            PVector m = new PVector(mouse.x, mouse.y);
            for (RodHandle h : handles)
            {
                if (h.isInside(m))
                {
                    currentHandle = h;
                    h.grabbed = true;
                    mouseDelta = PVector.sub(h.position, m);
                    mouseDown = true;
                    return;
                }
            }
        }
    }

    public void mouseDragged(PVector mouse)
    {
        if (mouseDown)
        {
            if (currentHandle.fixed)
                return;

            PVector m = new PVector(mouse.x, mouse.y);
            moveHandleToPosition(currentHandle, PVector.add(mouseDelta, m));
        }
    }

    public void mouseReleased(PVector mouse) {
        if (mouseDown)
        {
            mouseDown = false;
            //currentHandle.grabbed = false;
            currentHandle = null;
        }
    }

    void moveHandleToPosition(RodHandle h, PVector p)
    {
        h.getRod().setPosition2d(outTransform2d(p));
    }

    public RodHandle getCurrentHandle() {
        return currentHandle;
    }

    public void setCurrentHandle(RodHandle currentHandle) {
        this.currentHandle = currentHandle;
    }
}
