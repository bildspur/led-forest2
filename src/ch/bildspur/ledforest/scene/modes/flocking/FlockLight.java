package ch.bildspur.ledforest.scene.modes.flocking;

import ch.bildspur.ledforest.ui.FadeColor;
import processing.core.PVector;

/**
 * Created by cansik on 09.02.17.
 */
public class FlockLight {
    private final float minimumDistance = 20f;

    PVector currentPosition = new PVector();
    PVector originPosition = new PVector();
    PVector targetPosition = new PVector();
    PVector easingVector = new PVector(0.05f, 0.05f, 0.05f);

    FadeColor color;

    public FlockLight(PVector currentPosition) {
        this.currentPosition = currentPosition;
        this.targetPosition = currentPosition.copy();
        this.originPosition = currentPosition.copy();
    }

    public void update() {
        PVector delta = targetPosition.copy().sub(currentPosition);
        currentPosition.add(new PVector(
                delta.x * easingVector.x,
                delta.y * easingVector.y,
                delta.z * easingVector.z));
    }

    public void setSpeed(float speed) {
        easingVector.x = speed;
        easingVector.y = speed;
        easingVector.z = speed;
    }

    public boolean isMoving() {
        return PVector.sub(targetPosition, currentPosition).mag() > minimumDistance;
    }

    public void moveTo(PVector target) {
        targetPosition = target.copy();
    }

    public void moveToOrigin() {
        targetPosition = originPosition;
    }

    public PVector getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PVector currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PVector getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(PVector targetPosition) {
        this.targetPosition = targetPosition;
    }

    public PVector getEasingVector() {
        return easingVector;
    }

    public void setEasingVector(PVector easingVector) {
        this.easingVector = easingVector;
    }

    public FadeColor getColor() {
        return color;
    }

    public void setColor(FadeColor color) {
        this.color = color;
    }
}
