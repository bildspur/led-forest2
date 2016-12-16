package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.scene.interaction.*;
import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import processing.core.PVector;

/**
 * Created by cansik on 18/09/16.
 */
public class LeapMotionScene extends Scene {
    float fadeSpeed = sketch.secondsToEasing(0.5f);


    float fadeSpeedH = sketch.secondsToEasing(0.1f);
    float fadeSpeedS = sketch.secondsToEasing(0.1f);
    float fadeSpeedBIn = sketch.secondsToEasing(0.3f);
    float fadeSpeedBOut = sketch.secondsToEasing(0.1f);


    float interactionRadius = 75;

    float glowOffset = 0.5f;
    int hue = 0;

    TubeInteraction currentInteraction;

    FlickrInteraction flickrInteraction = new FlickrInteraction();
    GlowInteraction glowInteraction = new GlowInteraction();
    PulseInteraction pulseInteraction = new PulseInteraction();

    public LeapMotionScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName() {
        return "LeapMotion Scene";
    }

    public void init() {
        sketch.getColors().setColorB(0, fadeSpeed);
        sketch.getColors().setColorS(0, fadeSpeed);
        sketch.getColors().setColorH(0, fadeSpeedH);

        flickrInteraction.init(sketch);
        glowInteraction.init(sketch);
        pulseInteraction.init(sketch);

        currentInteraction = glowInteraction;
    }

    public void update() {
        PVector[] palms = new PVector[sketch.getLeapMotion().getFrame().hands().count()];

        // update palms
        for (int i = 0; i < palms.length; i++) {
            Hand h = sketch.getLeapMotion().getFrame().hands().get(i);
            Vector v = h.palmPosition();
            PVector palmPosition = sketch.getLeapMotion().intBoxVector(v);
            palms[i] = palmPosition;
        }

        for (int j = 0; j < sketch.getTubes().size(); j++) {
            Tube t = sketch.getTubes().get(j);
            for (int i = 0; i < t.getLeds().size(); i++) {
                LED led = t.getLeds().get(i);

                boolean isOn = false;
                float minDistance = Float.MAX_VALUE;
                int nearestHandIndex = 0;

                // check if a palm is nearby
                for (int p = 0; p < palms.length; p++) {
                    PVector pos = ledPosition(j, (t.getLeds().size() - 1 - i));

                    float distance = pos.dist(palms[p]);
                    if (distance < interactionRadius) {
                        if (minDistance > distance) {
                            minDistance = distance;
                            nearestHandIndex = p;
                        }

                        isOn = true;
                    }
                }

                // 3.14 <- 0 -> -3.14;
                // GLOW - STAND - FLICKR

                Hand h = null;
                if (sketch.getLeapMotion().getFrame().hands().count() > 0)
                    h = sketch.getLeapMotion().getFrame().hands().get(nearestHandIndex);

                // set default interaction mode
                currentInteraction = glowInteraction;

                /*
                if (h != null) {
                    float roll = h.palmNormal().roll();

                    if (roll <= -glowOffset) {
                        currentInteraction = flickrInteraction;
                    } else if (roll >= glowOffset) {
                        currentInteraction = pulseInteraction;
                    }
                }
                */

                InteractionData interactionData = new InteractionData(t, led, h, minDistance);

                if (isOn) {
                    currentInteraction.ledOn(interactionData);
                } else {
                    currentInteraction.ledOff(interactionData);
                }
            }
        }

        sketch.getDebug().addMessage("Mode: " + currentInteraction.toString());

        if (sketch.frameCount % sketch.secondsToFrames(1) == 0)
            hue = (hue + 1) % 360;
    }

    public PVector ledPosition(int rodIndex, int ledIndex) {
        Rod r = sketch.getVisualizer().getRods().get(rodIndex);
        float ledLength = r.getLedLength();
        float ledYTranslate = ((r.getShapes().size() - ledIndex) * ledLength);

        if (r.isInverted())
            ledYTranslate *= -1;

        return new PVector(r.getPosition().x, r.getPosition().y - ledYTranslate, r.getPosition().z);
    }
}