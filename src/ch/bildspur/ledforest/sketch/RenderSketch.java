package ch.bildspur.ledforest.sketch;

import ch.bildspur.ledforest.scene.*;
import ch.bildspur.ledforest.sketch.controller.*;
import ch.bildspur.ledforest.ui.FadeColor;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import ch.bildspur.ledforest.ui.visualisation.TubeVisualizer;
import com.leapmotion.leap.Controller;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;
import processing.video.Movie;

import java.util.ArrayList;

/**
 * Created by cansik on 16/08/16.
 */
public class RenderSketch extends PApplet {

    final static int OUTPUT_WIDTH = 640;
    final static int OUTPUT_HEIGHT = 480;

    final static int FRAME_RATE = 40;

    ArrayList<Tube> tubes;
    TubeVisualizer visualizer;

    SceneManager sceneManager;

    int drawMode = 3;

    int defaultFrameRate = FRAME_RATE;

    boolean showLogo = true;
    boolean showInfo = false;
    boolean mappingMode = false;

    int markedLEDTube = -1;

    PImage logo;

    VideoScene videoScene = new VideoScene(this);

    SyphonController syphon = new SyphonController();
    PeasyController peasy = new PeasyController();
    OscController osc = new OscController();
    LeapMotionController leapMotion = new LeapMotionController();
    ColorController colors = new ColorController();

    public void settings()
    {
        size(OUTPUT_WIDTH, OUTPUT_HEIGHT, P3D);
        //fullScreen(P3D, 1);
        PJOGL.profile = 1;
    }

    public void setup()
    {
        syphon.init(this);
        peasy.init(this);
        osc.init(this);
        leapMotion.init(this);
        colors.init(this);

        syphon.setupSyphon();
        leapMotion.setupLeapMotion();
        peasy.setupPeasy();
        osc.setupOSC();

        // load logo
        logo = loadImage(sketchPath("images/logotext.png"));
        logo.resize(width / 6, 0);

        // settings
        ellipseMode(CENTER);
        frameRate(defaultFrameRate);
        colorMode(HSB, 360, 100, 100);

        // setup tubes
        visualizer = new TubeVisualizer(this);
        createTubes(visualizer.rodColumnCount * visualizer.rodRowCount, 24);

        // mark some LEDS
        tubes.get(0).getLeds().get(0).setColor(new FadeColor(g, color(255, 0, 0)));
        tubes.get(1).getLeds().get(0).setColor(new FadeColor(g, color(0, 255, 0)));

        // visualizer
        visualizer.initRods(tubes);

        // scenes
        sceneManager = new SceneManager(this);

        // color scenes
        sceneManager.getColorScenes().add(new SpaceColorScene(this));
        sceneManager.getColorScenes().add(new SingleColorScene(this));
        sceneManager.getColorScenes().add(videoScene);
        sceneManager.getColorScenes().add(new WhiteColorScene(this));
        sceneManager.getColorScenes().add(new ExampleScene(this));
        sceneManager.getColorScenes().add(videoScene);
        sceneManager.getColorScenes().add(new HSVColorScene(this));

        // pattern scenes
        sceneManager.getPatternScenes().add(new FloatingPatternScene(this));
        sceneManager.getPatternScenes().add(new StarPatternScene(this));
        sceneManager.getPatternScenes().add(new FallingTraceScene(this));
        sceneManager.getPatternScenes().add(new WaveStarsPattern(this));

        sceneManager.init();
    }

    public void draw()
    {
        background(0);

        // scenes
        //sceneManager.update();

        updateLEDs();

        // calculate syphon ouput
        PGraphics output2d = visualizer.render2d();
        syphon.sendImageToSyphon(output2d);

        if (drawMode == 3)
        {
            visualizer.render3d();
            leapMotion.visualizeLeapMotion();
        } else
        {
            peasy.getCam().beginHUD();
            image(output2d, 0, 0);
            peasy.getCam().endHUD();
        }

        if (showLogo)
        {
            peasy.getCam().beginHUD();
            image(logo, (width / 2) - (logo.width / 2), height - logo.height - 10);
            peasy.getCam().endHUD();
        }

        sceneManager.update();

        // update osc app
        if (frameCount % secondsToFrames(1) == 0)
            osc.updateOSCApp();

        // hud
        if (showInfo)
        {
            if (drawMode == 3)
            {
                // draw stub info
                fill(255);
                textSize(12);

                for (int i = 0; i < visualizer.getRods().size(); i++)
                {
                    pushMatrix();
                    Rod r = visualizer.getRods().get(i);
                    translate(r.p.x, 0, r.p.z);
                    text(r.tube.getUniverse() + "|" + i, 0, 0);
                    popMatrix();
                }
            }

            peasy.getCam().beginHUD();
            fill(255);
            textSize(12);
            text("Draw Mode: " + drawMode + "D", 5, 15);
            text("FPS: " + round(frameRate), 5, 30);
            text("Color Scene: " + sceneManager.getActiveColorScene().getName(), 150, 15);
            text("Pattern Scene: " + sceneManager.getActivePatternScene().getName(), 150, 30);
            peasy.getCam().endHUD();
        }
    }

    void updateLEDs()
    {
        for (Tube t : tubes)
        {
            for (LED l : t.getLeds())
            {
                l.getColor().update();
            }
        }
    }

    void createTubes(int tubeCount, int ledCount)
    {
        tubes = new ArrayList<>();
        int address = 0;
        int universe = 0;

        for (int i = 0; i < tubeCount; i++)
        {
            Tube t = new Tube(universe);
            tubes.add(t);

            int colorShift = 0;

            for (int j = 0; j < ledCount; j++)
            {
                colorShift += 5;
                LED led = new LED(g, address, color(200 + colorShift, 100, 100));
                t.getLeds().add(led);

                address += 3;
            }

            // break alle 300 led's (showjockey standard)
            if (300 - ((ledCount * 3) + address)  <= 0)
            {
                universe++;
                address = 0;
            }
        }
    }

    public void onFrame(final Controller controller)
    {
        leapMotion.onFrame(controller);
    }

    public void keyPressed() {
        switch(key)
        {
            case '2':
                drawMode = 2;
                break;

            case '3':
                drawMode = 3;
                break;

            case ' ':
                int c = color(random(0, 360), random(0, 100), random(0, 100));
                for (int i = 0; i < tubes.get(0).getLeds().size(); i++)
                {
                    tubes.get(0).getLeds().get(i).getColor().fade(c, secondsToEasing(0.3f));
                }
                break;

            case 'c':
                sceneManager.nextColorScene();
                break;

            case 'p':
                sceneManager.nextPatternScene();
                break;

            case 'i':
                showInfo = !showInfo;
                break;

            case 'm':
                mappingMode = !mappingMode;
                sceneManager.setRunning(!sceneManager.isRunning());
                colors.setColor(color(100, 100, 100), 1);
                break;

            case 'b':
                for (int j = 0; j < tubes.size(); j++)
                {
                    markTube(j, color(0, 0, 0));
                }
                break;

            case 'n':
                if (markedLEDTube >= 0)
                    markTube(markedLEDTube, color(0, 0, 0));
                markedLEDTube = (markedLEDTube + 1) % tubes.size();
                println("marking tube nr.: " + markedLEDTube);
                markTube(markedLEDTube, color(0, 100, 100));
                break;

            case 'l':
                colors.setColorToWhite();
                break;

            case 'v':
                videoScene.dispose();
                videoScene.init();
                break;

            case 'z':
                // set color for led 0
                for (int j = 0; j < tubes.size(); j++)
                {
                    tubes.get(j).getLeds().get(0).getColor().fade(color(255), secondsToEasing(0.5f));
                }
                break;

            default:
                println("Key: " + key);
        }
    }

    public void markTube(int tubeId, int c)
    {
        for (int i = 0; i < tubes.get(0).getLeds().size(); i++)
        {
            tubes.get(tubeId).getLeds().get(i).getColor().fade(c, secondsToEasing(0.3f));
        }
    }

    public float secondsToEasing(float seconds)
    {
        return 1.0f / (seconds * defaultFrameRate);
    }

    public int secondsToFrames(float seconds)
    {
        return (int)(seconds * defaultFrameRate);
    }

    public void movieEvent(Movie m) {
        m.read();
    }

    public ArrayList<Tube> getTubes() {
        return tubes;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public int getDefaultFrameRate() {
        return defaultFrameRate;
    }

    public VideoScene getVideoScene() {
        return videoScene;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public boolean isShowLogo() {
        return showLogo;
    }

    public void setShowLogo(boolean showLogo) {
        this.showLogo = showLogo;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public boolean isMappingMode() {
        return mappingMode;
    }

    public void setMappingMode(boolean mappingMode) {
        this.mappingMode = mappingMode;
    }

    public int getMarkedLEDTube() {
        return markedLEDTube;
    }

    public void setMarkedLEDTube(int markedLEDTube) {
        this.markedLEDTube = markedLEDTube;
    }

    public PImage getLogo() {
        return logo;
    }

    public void setLogo(PImage logo) {
        this.logo = logo;
    }

    public SyphonController getSyphon() {
        return syphon;
    }

    public PeasyController getPeasy() {
        return peasy;
    }

    public OscController getOsc() {
        return osc;
    }

    public LeapMotionController getLeapMotion() {
        return leapMotion;
    }

    public ColorController getColors() {
        return colors;
    }

    public TubeVisualizer getVisualizer() {
        return visualizer;
    }
}

