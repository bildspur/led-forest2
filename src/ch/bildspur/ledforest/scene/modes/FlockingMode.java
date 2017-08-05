package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.scene.modes.flocking.FlockLight;
import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by cansik on 09.02.17.
 */
public class FlockingMode extends SceneMode {
    boolean isFlocking;

    final boolean visualize = true;

    float minimumLightDistance = 10;

    float sphereDistance = 150;
    float minSphereDistance = 50;
    float maxSphereDistance = 75;
    float maxDistanceToHand = 200;

    float minSpeed = 0.005f;
    float maxSpeed = 0.05f;

    float colorEasing = 0.95f; //sketch.secondsToEasing(0.1f);

    public FlockingMode(RenderSketch sketch, SceneManager sceneManager) {
        super(sketch, sceneManager);
    }

    ArrayList<FlockLight> lights = new ArrayList<>();

    public void init() {
        PApplet.println("switching to magnet mode");

        isFlocking = true;
        prepareFlocking();
    }

    public void setSpeed(float speed) {
        for (FlockLight light : lights) {
            light.setSpeed(speed);
        }
    }

    public void update() {
        super.update();

        updateColorsInScene();

        // vis
        if (visualize) {
            for (FlockLight light : lights) {
                sketch.pushMatrix();
                sketch.translate(light.getCurrentPosition().x, light.getCurrentPosition().y, light.getCurrentPosition().z);
                sketch.stroke(120, 100, 100);
                sketch.strokeWeight(1);
                sketch.noFill();
                sketch.g.box(5);
                sketch.popMatrix();
            }
        }

        for (FlockLight light : lights) {
            light.update();
        }

        if (sketch.getLeapMotion().isLeapMotionHandAvailable()) {
            PVector firstHandPalm = getPalms()[0];

            // set things cause of palms
            Hand h = sketch.getLeapMotion().getFrame().hands().get(0);

            sphereDistance = (1 - h.grabStrength()) * maxSphereDistance + minSphereDistance;
            minimumLightDistance = h.grabStrength() * 30 + 10;

            for (FlockLight light : lights) {

                if (!light.isMoving() || light.getCurrentPosition().dist(firstHandPalm) > maxDistanceToHand) {
                    PVector sphereDistribution = PVector.random3D();
                    sphereDistribution.mult(sphereDistance);
                    light.moveTo(PVector.add(firstHandPalm, sphereDistribution));
                }
            }
        }

        if (!sketch.getLeapMotion().isLeapMotionHandAvailable()) {
            boolean isStill = true;

            for (FlockLight light : lights) {
                light.moveToOrigin();

                if (light.isMoving())
                    isStill = false;
            }

            if (isStill) {
                sceneManager.setCurrentMode(sceneManager.getNormalMode());
            }
        }
    }

    public void close() {

    }

    private PVector[] getPalms() {
        PVector[] palms = new PVector[sketch.getLeapMotion().getFrame().hands().count()];

        // update palms
        for (int i = 0; i < palms.length; i++) {
            Hand h = sketch.getLeapMotion().getFrame().hands().get(i);
            Vector v = h.palmPosition();
            PVector palmPosition = sketch.getLeapMotion().intBoxVector(v);
            palms[i] = palmPosition;
        }

        return palms;
    }

    private void updateColorsInScene() {
        // update colors if they are in distance
        for (Rod r : sketch.getVisualizer().getRods()) {
            Tube t = r.getTube();
            for (int i = 0; i < t.getLeds().size(); i++) {
                LED led = t.getLeds().get(i);
                PVector pos = r.getLEDPosition(i);

                PVector colorVector = new PVector();
                int lightCount = 0;

                // loop over every flocklight
                for (FlockLight light : lights) {
                    float distance = light.getCurrentPosition().dist(pos);

                    if (distance <= minimumLightDistance) {
                        colorVector.add(light.getColor().getRawColor());
                        lightCount++;
                    }
                }

                // average vector
                colorVector.x = colorVector.x / (float) lightCount;
                colorVector.y = colorVector.y / (float) lightCount;
                colorVector.z = colorVector.z / (float) lightCount;

                led.getColor().fade(vectorToColor(colorVector), colorEasing);
            }
        }
    }

    private int vectorToColor(PVector v) {
        return sketch.g.color(Math.round(v.x), Math.round(v.y), Math.round(v.z));
    }

    private void prepareFlocking() {
        lights.clear();

        for (Rod r : sketch.getVisualizer().getRods()) {
            Tube t = r.getTube();
            for (int i = 0; i < t.getLeds().size(); i++) {
                FlockLight light = new FlockLight(r.getLEDPosition(i));

                // init with positive random
                light.setEasingVector(new PVector(
                        sketch.random(minSpeed, maxSpeed),
                        sketch.random(minSpeed, maxSpeed),
                        sketch.random(minSpeed, maxSpeed)));

                light.setColor(t.getLeds().get(i).getColor().copy());
                lights.add(light);
            }
        }
    }
}
