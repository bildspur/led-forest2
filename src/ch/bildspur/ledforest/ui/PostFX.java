package ch.bildspur.ledforest.ui;

/**
 * Created by cansik on 06.12.16.
 */

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.nio.file.Path;
import java.nio.file.Paths;

import static processing.core.PConstants.P2D;

public class PostFX {
    private final int PASS_NUMBER = 2;
    private final Path SHADER_PATH;

    private PApplet sketch;

    private int width;
    private int height;
    private int passIndex = -1;

    // shaders
    private PShader brightPassShader;
    private PShader blurShader;

    // frameBuffer
    private PGraphics[] passBuffers;

    public PostFX(PApplet sketch) {
        this.sketch = sketch;
        this.width = sketch.width;
        this.height = sketch.height;

        // constants
        SHADER_PATH = Paths.get(sketch.sketchPath(), "shader");

        // init temp pass buffer
        passBuffers = new PGraphics[PASS_NUMBER];
        for (int i = 0; i < passBuffers.length; i++) {
            passBuffers[i] = sketch.createGraphics(width, height, P2D);
            passBuffers[i].noSmooth();
        }

        // load shaders
        loadShaders();
    }

    private void loadShaders() {
        brightPassShader = sketch.loadShader(Paths.get(SHADER_PATH.toString(), "brightPassFrag.glsl").toString());
        blurShader = sketch.loadShader(Paths.get(SHADER_PATH.toString(), "blurFrag.glsl").toString());
    }

    private void increasePass() {
        passIndex = (passIndex + 1) % passBuffers.length;
    }

    private PGraphics getNextPass() {
        int nextIndex = (passIndex + 1) % passBuffers.length;
        return passBuffers[nextIndex];
    }

    private PGraphics getCurrentPass() {
        return passBuffers[passIndex];
    }

    private void clearPass(PGraphics pass) {
        // clear pass buffer
        pass.beginDraw();
        pass.background(0, 0);
        pass.resetShader();
        pass.endDraw();
    }

    public PostFX filter(PGraphics pg) {
        PGraphics pass = getNextPass();
        clearPass(pass);

        pass.beginDraw();
        pass.image(pg, 0, 0);
        pass.endDraw();

        increasePass();
        return this;
    }

    public PGraphics close() {
        return getCurrentPass();
    }

    public PostFX brightPass(float luminanceTreshold) {
        PGraphics pass = getNextPass();
        clearPass(pass);

        brightPassShader.set("brightPassThreshold", luminanceTreshold);

        pass.beginDraw();
        pass.shader(brightPassShader);
        pass.image(getCurrentPass(), 0, 0);
        pass.endDraw();

        increasePass();

        return this;
    }

    public PostFX blur(int blurSize, float sigma, boolean horizonatal) {
        PGraphics pass = getNextPass();
        clearPass(pass);

        blurShader.set("blurSize", blurSize);
        blurShader.set("sigma", sigma);
        blurShader.set("horizontalPass", horizonatal ? 1 : 0);

        pass.beginDraw();
        pass.shader(blurShader);
        pass.image(getCurrentPass(), 0, 0);
        pass.endDraw();

        increasePass();

        return this;
    }
}
