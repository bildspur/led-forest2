package ch.bildspur.ledforest.ui.edit;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.util.Tuple;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by cansik on 22.09.16.
 */
public class RodMap {
    public static final float SNAP_DISTANCE = 0.2f;

    RenderSketch sketch;
    PGraphics p;

    int width;
    int height;

    boolean mouseDown = false;
    RodHandle currentHandle = null;
    PVector mouseDelta = null;

    ArrayList<RodHandle> handles = new ArrayList<>();

    float gridSize = 0;
    PVector snapDistance;
    boolean gridOffset = false;

    public RodMap(RenderSketch sketch, int width, int height)
    {
        this.sketch = sketch;
        this.width = width;
        this.height = height;

        p = sketch.createGraphics(width, height, PConstants.P2D);
    }

    public void refreshHandles()
    {
        refreshHandles(false);
    }

    public void refreshHandles(boolean animate)
    {
        handles = new ArrayList<>();

        for(Rod r : sketch.getVisualizer().getRods()) {
            PVector pos;

            // intro animation
            if(animate)
                pos = new PVector(width/2f, height/2f);
            else
                pos = inTransform2d(r.getPosition2d());

            RodHandle h = new RodHandle(pos.x, pos.y, r, p);
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
        // draw initial grid
        p.stroke(100);
        p.strokeWeight(1f);
        p.line(width / 2f, 0, width / 2f, height);
        p.line(0, height / 2f, width, height / 2f);

        if(gridSize == 0)
            return;

        // draw snap grid
        int lineCount = (int)Math.ceil(PApplet.max((width / 2f / snapDistance.x), (int)(height / 2f / snapDistance.y)));

        p.stroke(150, 206, 180, 100);
        p.strokeWeight(1f);

        PVector offset = getGridOffsetValues();

        for(int i = 0; i < lineCount; i++)
        {
            // horizontal
            float xshift = (float)i * snapDistance.y + offset.x;

            // horizontal up
            p.line(0, height / 2f - xshift, width, height / 2f - xshift);

            // horizontal down
            p.line(0, height / 2f + xshift, width, height / 2f + xshift);

            // vertical
            float yshift = (float)i * snapDistance.x + offset.y;

            // vertical left
            p.line(width / 2f - yshift, 0, width / 2f - yshift, height);

            // vertical right
            p.line(width / 2f + yshift, 0, width / 2f + yshift, height);
        }
    }

    PVector getGridOffsetValues()
    {
        float offsetX = gridOffset ? snapDistance.x / 2f : 0f;
        float offsetY = gridOffset ? snapDistance.y / 2f : 0f;

        return new PVector(offsetX, offsetY);
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

            if(gridSize == 0)
                return;

            Tuple<PVector, PVector> snapInfos = getSnapInformation(m);
            PVector snapDistance = snapInfos.getFirst();
            PVector snapIndex = snapInfos.getSecond();

            if(snapDistance.x < SNAP_DISTANCE && snapDistance.y < SNAP_DISTANCE)
            {
                moveHandleToPosition(currentHandle,
                        inTransform2d(new PVector(gridSize * snapIndex.x, gridSize * snapIndex.y)));
            }
        }
    }

    public void mouseReleased(PVector mouse) {
        if (mouseDown)
        {
            mouseDown = false;
            currentHandle = null;
        }
    }

    public Tuple<PVector, PVector> getSnapInformation(PVector pos)
    {
        // check if pos is near snap position
        PVector posOut = outTransform2d(pos);

        // x
        double xrel = Math.abs(posOut.x / gridSize);
        float xdist = (float)((xrel) - Math.floor(xrel));
        xdist = (float)Math.min(Math.ceil(xdist) - xdist, xdist);

        // y
        double yrel = Math.abs(posOut.y / gridSize);
        float ydist = (float)((yrel) - Math.floor(yrel));
        ydist = (float)Math.min(Math.ceil(ydist) - ydist, ydist);

        // snap index
        int xindex = Math.round(posOut.x / gridSize);
        int yindex = Math.round(posOut.y / gridSize);

        return new Tuple<>(new PVector(xdist, ydist), new PVector(xindex, yindex));
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

    public float getGridSize() {
        return gridSize;
    }

    public void setGridSize(float gridSize) {
        this.gridSize = gridSize;

        PVector snapTrans = inTransform2d(new PVector(gridSize, gridSize));
        PVector zero = inTransform2d(new PVector(0, 0));
        snapDistance = snapTrans.sub(zero);
    }

    public PVector getSnapDistance() {
        return snapDistance;
    }

    public boolean isGridOffset() {
        return gridOffset;
    }

    public void setGridOffset(boolean gridOffset) {
        this.gridOffset = gridOffset;
    }
}
