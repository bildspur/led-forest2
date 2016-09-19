package ch.bildspur.ledforest.scene;

/**
 * Created by cansik on 18/09/16.
 */
import ch.bildspur.ledforest.sketch.RenderSketch;
import processing.core.PImage;
import processing.video.*; //<>//
import java.util.Arrays;

public class VideoScene extends Scene
{
    String[] videoFiles;
    int activeVideoIndex = -1;

    float wspace2d = 20;
    float hspace2d = 2;

    float hoffset = 100;
    float woffset = 100;

    float width2d = 15;
    float height2d = 15;

    Movie activeVideo;

    boolean isFixtureSpaceSet = false;

    public VideoScene(RenderSketch sketch) {
        super(sketch);
    }

    public boolean isUnique()
    {
        return true;
    }

    public String getName()
    {
        return "Video Scene ("+ videoFiles[activeVideoIndex]+")";
    }

    public void init()
    {
        videoFiles = readVideoFiles();

        if (videoFiles.length > 0)
        {
            activeVideoIndex = (activeVideoIndex + 1) % videoFiles.length;
            activeVideo = new Movie(sketch, videoFiles[activeVideoIndex]);
            activeVideo.loop();

            isFixtureSpaceSet = false;
        } else
        {
            activeVideo = null;
        }
    }

    public void dispose()
    {
        if (activeVideo != null)
            activeVideo.stop();
    }

    public void update()
    {
        if (sketch.frameCount <= 1)
            return;

    /*
    if(frameCount % secondsToFrames(0.3) == 0)
      return;
    */

        if (activeVideo == null)
            return;

        if (!isFixtureSpaceSet)
            setFixtureSpace(sketch.getTubes().size(), sketch.getTubes().get(0).getLeds().size(), activeVideo.width, activeVideo.height);

    /*
    if(drawMode == 3)
    {
       cam.beginHUD();
       image(activeVideo, 0, 0);
       cam.endHUD();
    }
    */

        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                setColorForLED(activeVideo, j, i);
            }
        }
    }

    void setFixtureSpace(int tubeCount, int ledCount, float w, float h)
    {
        wspace2d = (w - (tubeCount * width2d + (woffset * 2))) / tubeCount;
        hspace2d = (h - (ledCount * height2d + (hoffset * 2))) / ledCount;

        isFixtureSpaceSet = true;
    }

    void setColorForLED(PImage videoFrame, int tubeIndex, int ledIndex) {
        // set window
        int x = (int) (tubeIndex * (wspace2d + width2d) + woffset);
        int y = (int) (ledIndex * (hspace2d + height2d) + hoffset);
        int w = (int) (x + width2d);
        int h = (int) (y + height2d);

    /*
    if(drawMode == 3)
    {
       cam.beginHUD();
       noFill();
       stroke(120, 100, 100);
       rect(x, y, w-x, h-y);
       cam.endHUD();
    }
    */

        int c = getAverage(videoFrame, x, y, w, h);
        sketch.getTubes().get(tubeIndex).getLeds().get(ledIndex).getColor().fade(c, 0.5f);
    }

    int getAverage(PImage img, int x, int y, int w, int h)
    {
        // hsb mode
        int hue = 0;
        int sat = 0;
        int bright = 0;

        int count = (w-x) * (h-y);

        for (int u = x; u < w; u++)
        {
            for (int v = y; v < h; v++)
            {
                int c = img.get(u, v);
                hue += sketch.g.hue(c);
                sat += sketch.g.saturation(c);
                bright += sketch.g.brightness(c);
            }
        }

        return sketch.g.color(hue / count, sat / count, bright / count);
    }

    String[] readVideoFiles()
    {
        java.io.File folder = new java.io.File(sketch.sketchPath("data"));
        String[] files = folder.list((dir, name) -> name.matches(".*mov"));

        Arrays.sort(files);
        return files;
    }
}