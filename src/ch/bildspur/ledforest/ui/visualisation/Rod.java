package ch.bildspur.ledforest.ui.visualisation;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.*;

/**
 * Created by cansik on 18/09/16.
 */
public class Rod {
    String name;

    float rodWidth = 1;
    float rodLength;
    float ledLength = 2;
    int rodDetail = 5;

    PGraphics g;

    PVector position;
    PVector rotation;
    PVector barrelDistortion = new PVector(1f, 1f, 1f);

    protected ArrayList<PShape> shapes;

    Tube tube;

    boolean inverted;

    public Rod(PGraphics g, Tube tube, PVector position) {
        this(g, tube, position, new PVector());
    }

    public Rod(PGraphics g, Tube tube, PVector position, PVector rotation) {
        this.g = g;

        this.tube = tube;
        this.position = position;
        this.rotation = rotation;

        inverted = false;

        name = "Rod " + tube.getLeds().size();

        initShapes();
    }

    public void initShapes() {
        shapes = new ArrayList<>();
        rodLength = ledLength * tube.leds.size();

        for (int i = 0; i < tube.leds.size(); i++) {
            PShape sh = createRod(rodWidth, ledLength, rodDetail);
            sh.disableStyle();
            shapes.add(sh);
        }
    }

    public void render(PGraphics p) {
        for (int i = 0; i < shapes.size(); i++) {
            PShape sh = shapes.get(i);

            p.pushMatrix();
            PVector pos = getPosition();

            p.translate(pos.x, pos.y, pos.z);

            p.rotateX(rotation.x);
            p.rotateY(rotation.y);
            p.rotateZ(rotation.z);

            p.translate(0, (inverted ? 1 : -1) * (i * ledLength), 0);

            p.noStroke();
            p.fill(tube.leds.get(i).getColor().getColor());

            sh.disableStyle();
            p.shape(sh);
            p.popMatrix();
        }
    }

    PShape createRod(float r, float h, int detail) {
        g.textureMode(NORMAL);
        PShape sh = g.createShape();
        sh.beginShape(QUAD_STRIP);
        for (int i = 0; i <= detail; i++) {
            float angle = TWO_PI / detail;
            float x = (float) Math.sin(i * angle);
            float z = (float) Math.cos(i * angle);
            float u = (float) i / detail;
            sh.normal(x, 0, z);
            sh.vertex(x * r, -h / 2, z * r, u, 0);
            sh.vertex(x * r, +h / 2, z * r, u, 1);
        }
        sh.endShape();
        return sh;
    }

    @Override
    public String toString() {
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
        return new PVector(
                position.x * barrelDistortion.x,
                position.y * barrelDistortion.y,
                position.z * barrelDistortion.z);
    }

    public PVector getLEDPosition(int index) {
        float ledLength = getLedLength();
        float ledYTranslate = ((getShapes().size() - index) * ledLength);

        if (isInverted())
            ledYTranslate *= -1;

        return new PVector(getPosition().x, getPosition().y - ledYTranslate, getPosition().z);
    }

    public PVector getRawPosition() {
        return position;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public PVector getPosition2d() {
        return new PVector(position.x * barrelDistortion.x, position.z * barrelDistortion.z);
    }

    public PVector getRawPosition2d() {
        return new PVector(position.x, position.z);
    }

    public void setPosition2d(PVector position) {
        this.position = new PVector(position.x, this.position.y, position.y);
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

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public PVector getBarrelDistortion() {
        return barrelDistortion;
    }

    public void setBarrelDistortion(PVector barrelDistortion) {
        this.barrelDistortion = barrelDistortion;
    }

    public PVector getRotation() {
        return rotation;
    }

    public void setRotation(PVector rotation) {
        this.rotation = rotation;
    }
}