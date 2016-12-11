package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.processing.LeapMotion;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;


/**
 * Created by cansik on 18/09/16.
 */
public class LeapMotionController extends BaseController {
    public void init(RenderSketch sketch) {
        super.init(sketch);
        g = sketch.g;
    }

    LeapMotion leapMotion;

    PGraphics g;

    volatile Frame frame;
    private final Object lock = new Object();

    PVector interactionBox = new PVector(180, 180, 180);

    ArrayList<PVector> handTargets = new ArrayList<PVector>();
    ArrayList<PVector> handCurrents = new ArrayList<PVector>();

    int lastHandsCount = -1;

    float handEasing = 0.1f;

    public Frame getFrame() {
        synchronized (lock) {
            return frame;
        }
    }

    public PVector getInteractionBox() {
        return interactionBox;
    }

    public void setupLeapMotion() {
        leapMotion = new LeapMotion(sketch);

        for (int i = 0; i < 10; i++) {
            handTargets.add(new PVector());
            handCurrents.add(new PVector());
        }
    }

    public void onFrame(final Controller controller) {
        synchronized (lock) {
            frame = controller.frame();
        }
    }

    public PVector normPVector(Vector v) {
        Vector np = frame.interactionBox().normalizePoint(v, true);
        return new PVector(np.getX(), np.getY(), np.getZ());
    }

    public PVector intBoxVector(Vector v) {
        PVector nv = normPVector(v);
        PVector ibv = new PVector(interactionBox.x * nv.x,
                interactionBox.y * nv.y,
                interactionBox.z * nv.z);

        PVector hbox = PVector.mult(interactionBox, 0.5f);
        return new PVector(ibv.x - hbox.x, -1 * (ibv.y - hbox.y), ibv.z - hbox.z);
    }

    public void drawInteractionBox(PGraphics p) {
        // Interaction box visualisation
        p.beginDraw();
        p.pushMatrix();
        p.stroke(255);
        p.noFill();
        p.strokeWeight(2);
        p.box(interactionBox.x, interactionBox.y, interactionBox.z);
        p.popMatrix();
        p.endDraw();
    }

    public void drawHands(PGraphics p) {
        // Hand Visualisation
        p.beginDraw();
        if (isLeapMotionHandAvailable()) {
            int hIndex = 0;
            for (Hand h : frame.hands()) {
                Vector v = h.palmPosition();
                PVector ibv = intBoxVector(v);

                PVector handCurrent = handCurrents.get(hIndex);
                PVector handTarget = handTargets.get(hIndex);

                // easing
                handTarget = ibv;
                handCurrent.add(PVector.sub(handTarget, handCurrent).mult(handEasing));

                p.pushMatrix();
                p.translate(handCurrent.x, handCurrent.y, handCurrent.z);
                p.stroke(255, 100);
                p.noFill();
                p.sphere(15);
                //p.noStroke();
                //drawSphere(g, 3, p.color(255), 15, 3, 20);
                p.popMatrix();

                hIndex++;
            }
        }
        p.endDraw();
    }

    public boolean isLeapMotionHandAvailable() {
        return (frame != null && frame.hands().count() > 0);
    }

    void drawSphere(PGraphics g, float size, int c, int detail, float blurRadius, float maxOpac) {
        g.hint(DISABLE_DEPTH_TEST);
        float partSize = (blurRadius - 1f) / (float) detail;

        for (int i = detail - 1; i >= 0; i--) {
            float sphereSize = size * (1f + (i * partSize));
            float alphaValue = PApplet.map(i, 0, detail - 1, maxOpac, 10);
            g.fill(c, alphaValue);
            g.sphere(sphereSize);
        }
        g.hint(ENABLE_DEPTH_TEST);
    }
}
