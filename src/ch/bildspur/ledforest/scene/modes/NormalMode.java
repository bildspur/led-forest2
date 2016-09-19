package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.Scene;
import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

/**
 * Created by cansik on 18/09/16.
 */
public class NormalMode extends SceneMode
{

    public NormalMode(RenderSketch sketch, SceneManager sceneManager)
    {
        super(sketch, sceneManager);
    }

    public void init()
    {
        PApplet.println("switching to normal mode");

        sceneManager.getActiveColorScene().init();
        sceneManager.getActivePatternScene().init();
    }

    public void update()
    {
        super.update();

        if (sketch.getLeapMotion().isLeapMotionHandAvailable())
        {
            sceneManager.changeMode(new TutorialMode(sketch, sceneManager));
        }

        Scene cs = sceneManager.getActiveColorScene();
        cs.update();

        if (!cs.isUnique())
        {
            Scene ps = sceneManager.getActivePatternScene();
            ps.update();
        }

        if (sketch.frameCount % sceneManager.getColorCycle() == 0)
            sceneManager.nextColorScene();

        if (sketch.frameCount % sceneManager.getPatternCycle() == 0)
            sceneManager.nextPatternScene();
    }

    public void close()
    {
        sceneManager.getActiveColorScene().dispose();
        sceneManager.getActivePatternScene().dispose();
    }
}
