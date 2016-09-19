package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

/**
 * Created by cansik on 18/09/16.
 */
public class LeapMode extends SceneMode
{
    int leapMotionIdleTimer;

    public LeapMode(RenderSketch sketch, SceneManager sceneManager)
    {
        super(sketch, sceneManager);
    }

    public void init()
    {
        PApplet.println("switching to leapmotion mode");
        sceneManager.getLeapMotionScene().init();
        leapMotionIdleTimer = sceneManager.getLeapMotionIdleTime();
    }

    public void update()
    {
        super.update();
        sceneManager.getLeapMotionScene().update();

        if (sketch.getLeapMotion().isLeapMotionHandAvailable())
            leapMotionIdleTimer =  sceneManager.getLeapMotionIdleTime();

        if (leapMotionIdleTimer < 0)
            sceneManager.changeMode(new NormalMode(sketch, sceneManager));

        leapMotionIdleTimer--;
    }

    public void close()
    {
        sceneManager.getLeapMotionScene().dispose();
    }
}

