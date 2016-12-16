package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.Animation;

import static ch.bildspur.ledforest.util.ImageUtil.centerImageAdjusted;

/**
 * Created by cansik on 18/09/16.
 */
public class TutorialScene extends Scene {
    public Animation fade;

    public TutorialScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName() {
        return "Tutorial Scene";
    }

    public void init() {
        fade = new Animation(0.5f, 255, 255);
        fade.start();
    }

    public void update() {
        fade.update();

        sketch.getPeasy().getCam().beginHUD();
        sketch.tint(255, fade.getValue());
        centerImageAdjusted(sketch.g, sketch.getWelcomeImage());
        sketch.noTint();
        sketch.getPeasy().getCam().endHUD();
    }
}
