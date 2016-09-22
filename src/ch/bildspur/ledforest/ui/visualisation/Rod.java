package ch.bildspur.ledforest.ui.visualisation;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.NORMAL;
import static processing.core.PConstants.QUAD_STRIP;
import static processing.core.PConstants.TWO_PI;

/**
 * Created by cansik on 18/09/16.
 */
public class Rod
{
    String name;

    float rodWidth = 1;
    float rodLength;
    float ledLength = 2;
    int rodDetail = 5;

    PGraphics g;

    PVector position;
    protected ArrayList<PShape> shapes;

    Tube tube;

    public Rod(PGraphics g, Tube tube, PVector position)
    {
        this.g = g;

        this.tube = tube;
        this.position = position;

        initShapes();
    }

    public void initShapes()
    {
        shapes = new ArrayList<>();
        rodLength = ledLength * tube.leds.size();

        for (int i = 0; i < tube.leds.size(); i++)
        {
            PShape sh = createRod(rodWidth, ledLength, rodDetail);
            sh.disableStyle();
            shapes.add(sh);
        }

        name = "Rod " + tube.getLeds().size();
    }

    public void render()
    {
        for (int i = 0; i < shapes.size(); i++)
        {
            PShape sh = shapes.get(i);

            g.pushMatrix();
            g.translate(position.x, position.y + (i * ledLength), position.z);
            g.noStroke();
            g.fill(tube.leds.get(i).getColor().getColor());

            sh.disableStyle();
            g.shape(sh);
            g.popMatrix();
        }
    }

    PShape createRod(float r, float h, int detail) {
        g.textureMode(NORMAL);
        PShape sh = g.createShape();
        sh.beginShape(QUAD_STRIP);
        for (int i = 0; i <= detail; i++) {
            float angle = TWO_PI / detail;
            float x = (float)Math.sin(i * angle);
            float z = (float)Math.cos(i * angle);
            float u = (float)i / detail;
            sh.normal(x, 0, z);
            sh.vertex(x * r, -h/2, z * r, u, 0);
            sh.vertex(x * r, +h/2, z * r, u, 1);
        }
        sh.endShape();
        return sh;
    }

    @Override
    public String toString()
    {
        return name + " (" + tube.getLeds().size() + " LED)";
    }

    public float getRodWidth() {
        return rodWidth;
    }

    public float getRodLength() {
        return rodLength;
    }

    public int getRodDetail() {
        return rodDetail;
    }

    public float getLedLength() {
        return ledLength;
    }

    public ArrayList<PShape> getShapes() {
        return shapes;
    }

    public PVector getPosition() {
        return position;
    }

    public Tube getTube() {
        return tube;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}