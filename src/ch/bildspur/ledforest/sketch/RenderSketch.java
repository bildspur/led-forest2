package ch.bildspur.ledforest.sketch;

import processing.core.PApplet;
import processing.opengl.PJOGL;

/**
 * Created by cansik on 16/08/16.
 */
public class RenderSketch extends PApplet {

    final static int OUTPUT_WIDTH = 640;
    final static int OUTPUT_HEIGHT = 480;

    final static int FRAME_RATE = 30;

    public void settings(){
        size(OUTPUT_WIDTH, OUTPUT_HEIGHT, P2D);
        PJOGL.profile = 1;
    }

    public void setup()
    {
        frameRate(FRAME_RATE);
    }

    public void draw(){
        background(0);
    }

    public void keyPressed()
    {
        switch (key) {
            case 'h':

                break;
        }
    }
}

