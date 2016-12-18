package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.Scene;
import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.Animation;
import processing.core.PApplet;
import processing.core.PConstants;

import static ch.bildspur.ledforest.util.ImageUtil.centerImageAdjusted;

/**
 * Created by cansik on 18/09/16.
 */
public class NormalMode extends SceneMode {

    public NormalMode(RenderSketch sketch, SceneManager sceneManager) {
        super(sketch, sceneManager);
    }

    private Animation fade;
    private boolean showWelcomeScreen = true;

    public void init() {
        PApplet.println("switching to normal mode");

        fade = new Animation(0.2f, 0, 255);
        fade.start();

        sceneManager.getActiveColorScene().init();
        sceneManager.getActivePatternScene().init();
    }

    public void update() {
        super.update();
        fade.update();

        if (sketch.getLeapMotion().isLeapMotionHandAvailable()) {
            sceneManager.changeMode(new TutorialMode(sketch, sceneManager));
        }

        Scene cs = sceneManager.getActiveColorScene();
        cs.update();

        if (!cs.isUnique()) {
            Scene ps = sceneManager.getActivePatternScene();
            ps.update();
        }

        if (sketch.frameCount % sceneManager.getColorCycle() == 0)
            sceneManager.nextColorScene();

        if (sketch.frameCount % sceneManager.getPatternCycle() == 0)
            sceneManager.nextPatternScene();

        // show welcome screen
        if (showWelcomeScreen) {
            sketch.getPeasy().getCam().beginHUD();
            sketch.tint(255, fade.getValue());
            centerImageAdjusted(sketch.g, sketch.getWelcomeImage());
            sketch.blendMode(PConstants.SCREEN);
            sketch.tint(255, fade.getValue());
            centerImageAdjusted(sketch.g, sketch.getHandAnimation());
            sketch.blendMode(PConstants.BLEND);
            sketch.noTint();
            sketch.getPeasy().getCam().endHUD();
        }
    }

    public void close() {
        PApplet.println("start fading..");
        fade.reverse();
        fade.start();

        sceneManager.getActiveColorScene().dispose();
        sceneManager.getActivePatternScene().dispose();
    }
}
