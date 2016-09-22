package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.*;
import com.leapmotion.leap.processing.LeapMotion;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;


/**
 * Created by cansik on 18/09/16.
 */
public class LeapMotionController extends BaseController {
    public void init(RenderSketch sketch)
    {
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

    public Frame getFrame()
    {
        synchronized (lock) {
            return frame;
        }
    }

    public PVector getInteractionBox() {
        return interactionBox;
    }

    public void setupLeapMotion()
    {
        leapMotion = new LeapMotion(sketch);

        for(int i = 0; i < 10; i++)
        {
            handTargets.add(new PVector());
            handCurrents.add(new PVector());
        }
    }

    public void onFrame(final Controller controller)
    {
        synchronized (lock) {
            frame = controller.frame();
        }
    }

    public PVector normPVector(Vector v)
    {
        Vector np = frame.interactionBox().normalizePoint(v, true);
        return new PVector(np.getX(), np.getY(), np.getZ());
    }

    public PVector intBoxVector(Vector v)
    {
        PVector nv = normPVector(v);
        PVector ibv = new PVector(interactionBox.x * nv.x,
                interactionBox.y * nv.y,
                interactionBox.z * nv.z);

        PVector hbox = PVector.mult(interactionBox, 0.5f);
        return new PVector(ibv.x - hbox.x, -1 * (ibv.y - hbox.y), ibv.z - hbox.z);
    }

    public void visualizeLeapMotion()
    {
        // Interaction box visualisation
        g.pushMatrix();
        g.stroke(255);
        g.noFill();
        g.strokeWeight(2);
        g.box(interactionBox.x, interactionBox.y, interactionBox.z);
        g.popMatrix();

        // Create Targets
  /*
  if(lastHandsCount != frame.hands().count())
  {
      lastHandsCount = frame.hands().count();

  }
  */

        // Hand Visualisation
        if (isLeapMotionHandAvailable())
        {
            int hIndex = 0;
            for (Hand h : frame.hands())
            {
                //Hand h = frame.hands().get(0);
                Vector v = h.palmPosition();
                PVector ibv = intBoxVector(v);

                PVector handCurrent = handCurrents.get(hIndex);
                PVector handTarget = handTargets.get(hIndex);

                // easing
                handTarget = ibv;
                handCurrent.add(PVector.sub(handTarget, handCurrent).mult(handEasing));

                g.pushMatrix();
                g.translate(handCurrent.x, handCurrent.y, handCurrent.z);
                g.stroke(255, 100);
                g.noFill();
                g.sphere(15);
                g.popMatrix();

                hIndex++;
            }
        }
    }

    public boolean isLeapMotionHandAvailable()
    {
        return (frame != null && frame.hands().count() > 0);
    }
}
