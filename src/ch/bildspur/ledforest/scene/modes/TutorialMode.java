package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.scene.TutorialScene;
import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PApplet;

/**
 * Created by cansik on 18/09/16.
 */
public class TutorialMode extends SceneMode
{
    float fadeTime = 0.5f;
    int beginFade;
    TutorialScene tutScene;

    public TutorialMode(RenderSketch sketch, SceneManager sceneManager)
    {
        super(sketch, sceneManager);
    }

    public void init()
    {
        PApplet.println("switching to tutorial mode");
        sceneManager.getTutorialScene().init();
        beginFade = sketch.secondsToFrames(fadeTime);

        tutScene = ((TutorialScene)sceneManager.getTutorialScene());
    }

    public void update()
    {
        super.update();
        sceneManager.getTutorialScene().update();

        // setting color
        sketch.getColors().setColorB(0, sketch.secondsToEasing(0.5f));
        sketch.getColors().setColorS(100, sketch.secondsToEasing(0.5f));
        sketch.getColors().setColorH(0, sketch.secondsToEasing(0.5f));

        // do fadeout
        if (sceneManager.getLeapMotionTransitionTime() - timer < beginFade && !tutScene.fade.isRunning())
        {
            PApplet.println("start fading..");
            tutScene.fade.reverse();
            tutScene.fade.start();
        }

        if (timer > sceneManager.getLeapMotionTransitionTime())
            sceneManager.changeMode(new LeapMode(sketch, sceneManager));
    }

    public void close()
    {
        sceneManager.getTutorialScene().dispose();
    }
}