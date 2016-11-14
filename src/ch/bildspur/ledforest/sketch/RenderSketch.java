package ch.bildspur.ledforest.sketch;

import ch.bildspur.ledforest.scene.*;
import ch.bildspur.ledforest.sketch.controller.*;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import ch.bildspur.ledforest.ui.visualisation.TubeVisualizer;
import com.leapmotion.leap.Controller;
import controlP5.ControlEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;
import processing.video.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cansik on 16/08/16.
 */
public class RenderSketch extends PApplet {

    public final static String CONFIG_NAME = "config.json";

    public final static int OUTPUT_WIDTH = 640;
    public final static int OUTPUT_HEIGHT = 480;

    public final static int FRAME_RATE = 50;

    ArrayList<Tube> tubes = new ArrayList<>();
    TubeVisualizer visualizer;

    SceneManager sceneManager;

    int drawMode = 3;

    int defaultFrameRate = FRAME_RATE;

    boolean showLogo = true;
    boolean showInfo = false;
    boolean mappingMode = false;

    volatile boolean configLoaded = false;

    int markedLEDTube = -1;

    PImage logo;

    VideoScene videoScene = new VideoScene(this);

    SyphonController syphon = new SyphonController();
    PeasyController peasy = new PeasyController();
    OscController osc = new OscController();
    LeapMotionController leapMotion = new LeapMotionController();
    ColorController colors = new ColorController();
    RodEditController rodEditView = new RodEditController();
    ConfigurationController config = new ConfigurationController();
    ArtNetController artnet = new ArtNetController();
    DebugController debug = new DebugController();

    public void settings() {
        size(OUTPUT_WIDTH, OUTPUT_HEIGHT, P3D);
        //fullScreen(P3D, 1);
        PJOGL.profile = 1;
    }

    public void setup() {
        surface.setTitle("LED Forest 2");

        syphon.init(this);
        peasy.init(this);
        osc.init(this);
        leapMotion.init(this);
        colors.init(this);
        config.init(this);
        artnet.init(this);
        debug.init(this);

        leapMotion.setupLeapMotion();
        peasy.setupPeasy();

        // load logo
        logo = loadImage(sketchPath("images/logotext.png"));
        logo.resize(width / 6, 0);

        // settings
        ellipseMode(CENTER);
        frameRate(defaultFrameRate);
        colorMode(HSB, 360, 100, 100);

        // setup tubes
        visualizer = new TubeVisualizer(this);

        // setup edit mode
        rodEditView.init(this);

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

        // after config loaded
        config.addListener((e) -> {
            artnet.initUniverses();

            if (syphon.isEnabled())
                syphon.setupSyphon();

            if (artnet.isEnabled())
                artnet.setupArtNet();

            if (osc.isEnabled())
                osc.setupOSC();

            configLoaded = true;
        });
        config.loadAsync(CONFIG_NAME);
    }

    public void draw() {
        background(0);

        // show loading screen while waiting
        if (!configLoaded) {
            showLoadingScreen();
            return;
        }

        updateLEDs();

        // calculate syphon ouput
        if (syphon.isEnabled()) {
            PGraphics output2d = visualizer.render2d();
            syphon.sendImageToSyphon(output2d);
        }

        // output dmx
        if (artnet.isEnabled()) {
            artnet.sendDmx();
        }

        // show ui
        switch (drawMode) {
            case 1:
                peasy.getCam().beginHUD();
                rodEditView.render();
                peasy.getCam().endHUD();
                break;
            case 2:
                peasy.getCam().beginHUD();
                image(visualizer.render2d(), 0, 0);
                peasy.getCam().endHUD();
                break;
            case 3:
                visualizer.render3d();
                leapMotion.visualizeLeapMotion();
                break;
        }

        // show logo
        if (showLogo) {
            peasy.getCam().beginHUD();
            image(logo, (width / 2) - (logo.width / 2), height - logo.height - 10);
            peasy.getCam().endHUD();
        }

        sceneManager.update();

        // update osc app
        if (osc.isEnabled() && frameCount % secondsToFrames(1) == 0)
            osc.updateOSCApp();

        // hud
        if (showInfo)
            debug.showInfo();
    }

    void showLoadingScreen() {
        peasy.getCam().beginHUD();
        textAlign(CENTER, CENTER);

        textSize(24);
        text("LED Forest 2", width / 2, height / 2 - 20);

        textSize(18);
        text("loading...", width / 2, height / 2 + 20);

        image(logo, (width / 2) - (logo.width / 2), height - logo.height - 10);
        peasy.getCam().endHUD();
    }

    void updateLEDs() {
        for (Tube t : tubes) {
            for (LED l : t.getLeds()) {
                l.getColor().update();
            }
        }
    }

    public void addRod(Rod r) {
        tubes.add(r.getTube());
        visualizer.getRods().add(r);
    }

    public void removeRod(Rod r) {
        visualizer.getRods().remove(r);
        tubes.remove(r.getTube());
    }

    public void onFrame(final Controller controller) {
        leapMotion.onFrame(controller);
    }

    public void keyPressed() {
        // Don't listen to keys in edit mode.
        if (drawMode == 1)
            return;

        switch (key) {
            case '1':
                sceneManager.setRunning(false);
                getPeasy().getCam().setActive(false);
                drawMode = 1;
                rodEditView.updateRodList();
                break;

            case '2':
                drawMode = 2;
                break;

            case '3':
                drawMode = 3;
                break;

            case ' ':
                int c = color(random(0, 360), random(0, 100), random(0, 100));
                for (int i = 0; i < tubes.get(0).getLeds().size(); i++) {
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
                for (int j = 0; j < tubes.size(); j++) {
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
                for (int j = 0; j < tubes.size(); j++) {
                    tubes.get(j).getLeds().get(0).getColor().fade(color(255), secondsToEasing(0.5f));
                }
                break;

            default:
                println("Key: " + key);
        }
    }

    public void markTube(int tubeId, int c) {
        for (int i = 0; i < tubes.get(0).getLeds().size(); i++) {
            tubes.get(tubeId).getLeds().get(i).getColor().fade(c, secondsToEasing(0.3f));
        }
    }

    public void mousePressed() {
        if (drawMode == 1)
            rodEditView.mousePressed();
    }

    public void mouseDragged() {
        if (drawMode == 1)
            rodEditView.mouseDragged();
    }

    public void mouseReleased() {
        if (drawMode == 1)
            rodEditView.mouseReleased();
    }

    public Map<Integer, List<Tube>> getTubesByUniverse() {
        return tubes.stream().collect(Collectors.groupingBy(Tube::getUniverse));
    }

    public void controlEvent(ControlEvent e) {
        rodEditView.controlEvent(e);
    }

    public float secondsToEasing(float seconds) {
        return 1.0f / (seconds * defaultFrameRate);
    }

    public int secondsToFrames(float seconds) {
        return (int) (seconds * defaultFrameRate);
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

    public ConfigurationController getConfig() {
        return config;
    }

    public RodEditController getRodEditView() {
        return rodEditView;
    }

    public ArtNetController getArtNet() {
        return artnet;
    }

    public DebugController getDebug() {
        return debug;
    }
}

