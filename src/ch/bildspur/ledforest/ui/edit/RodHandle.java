package ch.bildspur.ledforest.ui.edit;

import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Created by cansik on 22.09.16.
 */
public class RodHandle {
    final float SIZE = 15f;
    final float STROKE_WEIGHT = 2f;

    PGraphics g;

    final int NORMAL_COLOR;
    final int GRAB_COLOR;

    final int INNER_COLOR;
    final int FIXED_COLOR;

    final int[] INNER_COLORS;

    final int TEXT_COLOR;

    final float EASING = 0.2f;

    PVector position;
    PVector target;

    boolean grabbed;
    boolean fixed;

    Rod rod;

    public RodHandle(Rod rod, PGraphics g) {
        this(0, 0, rod, g);
    }

    public RodHandle(float x, float y, Rod rod, PGraphics g) {
        this.rod = rod;
        this.g = g;

        position = new PVector(x, y);
        target = new PVector();
        grabbed = false;
        fixed = false;

        NORMAL_COLOR = g.parent.color(210, 18.4f, 29.8f);
        GRAB_COLOR = g.parent.color(14, 60, 100);
        INNER_COLOR = g.parent.color(202, 50.2f, 99.2f);
        FIXED_COLOR = g.parent.color(203, 83.2f, 77.3f);
        TEXT_COLOR = g.parent.color(0, 0, 100);

        INNER_COLORS = new int[]{
                g.parent.color(208, 100, 85),
                g.parent.color(127, 77, 80),
                g.parent.color(52, 100, 100),
                g.parent.color(28, 89, 100),
                g.parent.color(3, 79, 100),
                g.parent.color(314, 93, 94),
                g.parent.color(197, 50, 100),
                g.parent.color(146, 100, 100),
                g.parent.color(292, 94, 79),
                g.parent.color(0, 0, 87)
        };
    }

    public void update() {
        PVector delta = target.copy().sub(position);
        position.add(delta.mult(EASING));
    }

    public void moveTo(PVector v) {
        target = v;
    }

    public void render() {
        g.strokeWeight(STROKE_WEIGHT);
        g.stroke(grabbed ? GRAB_COLOR : NORMAL_COLOR);


        int innerColor = rod.getTube().getUniverse() < INNER_COLORS.length ?
                INNER_COLORS[rod.getTube().getUniverse()] : INNER_COLOR;
        g.fill(fixed ? FIXED_COLOR : innerColor);

        g.ellipse(position.x, position.y, SIZE, SIZE);

        Tube tube = rod.getTube();
        String text = rod.getName() +
                " (" + tube.getUniverse() +
                "." + tube.getStartAddress() +
                "-" + (tube.getEndAddress() + tube.LED_ADDRESS_SIZE) + ")";

        g.fill(TEXT_COLOR);
        g.textAlign(PConstants.CENTER, PConstants.CENTER);
        g.text(text, position.x, position.y - 15);
    }

    public boolean isInside(PVector p) {
        return (Math.abs(p.dist(position)) < SIZE);
    }

    public Rod getRod() {
        return rod;
    }

    public PVector getPosition() {
        return position;
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}