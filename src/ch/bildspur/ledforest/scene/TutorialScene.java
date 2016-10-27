package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.Animation;
import processing.core.PImage;

import static ch.bildspur.ledforest.util.ImageUtil.centerImageAdjusted;

/**
 * Created by cansik on 18/09/16.
 */
public class TutorialScene extends Scene {
    PImage tutorialImage;

    public Animation fade;

    public TutorialScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName() {
        return "Tutorial Scene";
    }

    public void init() {
        tutorialImage = sketch.loadImage("images/tutorial.png");

        fade = new Animation(0.5f, 0, 255);
        fade.start();
    }

    public void update() {
        fade.update();

        sketch.getPeasy().getCam().beginHUD();
        sketch.tint(255, fade.getValue());
        centerImageAdjusted(sketch.g, tutorialImage);
        sketch.noTint();
        sketch.getPeasy().getCam().endHUD();
    }
}
