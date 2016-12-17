package ch.bildspur.ledforest.scene;

/**
 * Created by cansik on 18/09/16.
 */

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Movie;

import java.util.Arrays;

public class ExtendedVideoScene extends Scene {
    private final boolean createMask = false;
    private final String dataFolder = "data/video";

    String[] videoFiles;
    int activeVideoIndex = -1;

    Movie activeVideo;

    boolean isFixtureSpaceSet = false;

    PVector scale = new PVector(1, 1);

    int pixelWidth = 4;
    int pixelHeight = 4;

    public ExtendedVideoScene(RenderSketch sketch) {
        super(sketch);
    }

    public boolean isUnique() {
        return true;
    }

    public String getName() {
        return "Extended Video Scene (" + videoFiles[activeVideoIndex] + ")";
    }

    public void init() {
        videoFiles = readVideoFiles();

        if (videoFiles.length > 0) {
            activeVideoIndex = (activeVideoIndex + 1) % videoFiles.length;
            activeVideo = new Movie(sketch, sketch.sketchPath(dataFolder + "/" + videoFiles[activeVideoIndex]));
            activeVideo.loop();

            isFixtureSpaceSet = false;
        } else {
            activeVideo = null;
        }
    }

    public void dispose() {
        if (activeVideo != null) {
            activeVideo.stop();
            activeVideo.dispose();
        }
    }

    public void update() {
        if (sketch.frameCount <= 1)
            return;

        if (activeVideo == null)
            return;

        PGraphics video;
        if (createMask) {
            video = sketch.createGraphics(activeVideo.width, activeVideo.height);
            video.beginDraw();

            //activeVideo.loadPixels();
            //video.image(activeVideo, 0, 0);
        }

        // set scale
        PVector intBox = sketch.getLeapMotion().getInteractionBox();
        scale = new PVector(intBox.x / activeVideo.width, intBox.z / activeVideo.height);

        // update colors
        for (Rod rod : sketch.getVisualizer().getRods()) {
            // position on video
            PVector videoPos = new PVector(
                    (rod.getPosition2d().x + (intBox.x / 2f)) / scale.x,
                    (rod.getPosition2d().y + (intBox.y / 2f)) / scale.y);

            for (int i = 0; i < rod.getTube().getLeds().size(); i++) {
                float inverted = rod.isInverted() ? -1 : 1;
                LED led = rod.getTube().getLeds().get(i);

                int color = getAverage(activeVideo,
                        (int) videoPos.x,
                        (int) (videoPos.y + (pixelHeight * i * inverted)),
                        pixelWidth,
                        pixelHeight);

                led.getColor().setColor(color);
                //led.getColor().fade(color, 0.05f);

                if (createMask) {
                    video.noFill();
                    video.stroke(0, 255, 0);
                    video.rect(
                            (int) videoPos.x,
                            (int) (videoPos.y + (pixelHeight * i * inverted)),
                            pixelWidth,
                            pixelHeight);
                }
            }
        }

        if (createMask) {
            video.endDraw();
            video.save("mask.png");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    int getAverage(PImage img, int x, int y, int w, int h) {
        // hsb mode
        int hue = 0;
        int sat = 0;
        int bright = 0;

        int count = w * h;

        for (int u = x; u < x + w; u++) {
            for (int v = y; v < y + h; v++) {
                int c = img.get(u, v);
                hue += sketch.g.hue(c);
                sat += sketch.g.saturation(c);
                bright += sketch.g.brightness(c);
            }
        }

        return sketch.g.color(hue / count, sat / count, bright / count);
    }

    String[] readVideoFiles() {
        java.io.File folder = new java.io.File(sketch.sketchPath(dataFolder));
        String[] files = folder.list((dir, name) -> name.matches(".*(?:mp4|mov)"));

        Arrays.sort(files);
        return files;
    }
}