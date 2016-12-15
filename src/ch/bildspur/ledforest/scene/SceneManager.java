package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.scene.modes.NormalMode;
import ch.bildspur.ledforest.scene.modes.SceneMode;
import ch.bildspur.ledforest.sketch.RenderSketch;

import java.util.ArrayList;

/**
 * Created by cansik on 18/09/16.
 */
public class SceneManager extends Scene {
    ArrayList<Scene> colorScenes;
    ArrayList<Scene> patternScenes;

    int transitionTime = 2;

    int currentColorScene = 0;
    int currentPatternScene = 0;

    int colorCycle = sketch.secondsToFrames(5 * 60);
    int patternCycle = sketch.secondsToFrames(3 * 60);

    int leapMotionIdleTime = sketch.secondsToFrames(5);
    int leapMotionIdleTimer = leapMotionIdleTime;
    int leapMotionTransitionTime = sketch.secondsToFrames(transitionTime);

    int sceneTimer = 0;

    boolean transitionMode = false;
    boolean normalMode = true;

    boolean running = true;

    Scene leapMotionScene = new LeapMotionScene(sketch);
    Scene tutorialScene = new TutorialScene(sketch);

    SceneMode currentMode;

    public SceneManager(RenderSketch sketch) {
        super(sketch);
        colorScenes = new ArrayList<>();
        patternScenes = new ArrayList<>();

        currentMode = new NormalMode(sketch, this);
    }

    public void init() {
        currentMode.init();
    }

    public void changeMode(SceneMode newMode) {
        currentMode.close();
        currentMode = newMode;
        currentMode.init();
    }

    public void nextColorScene() {
        getActiveColorScene().dispose();
        currentColorScene = (currentColorScene + 1) % colorScenes.size();
        getActiveColorScene().init();
    }

    public void nextPatternScene() {
        getActivePatternScene().dispose();
        currentPatternScene = (currentPatternScene + 1) % patternScenes.size();
        getActivePatternScene().init();
    }

    public Scene getActiveColorScene() {
        if (normalMode)
            return colorScenes.get(currentColorScene);
        else
            return leapMotionScene;
    }

    public Scene getActivePatternScene() {
        if (normalMode)
            return patternScenes.get(currentPatternScene);
        else
            return leapMotionScene;
    }

    public void update() {
        if (!running)
            return;

        currentMode.update();
    }

    public ArrayList<Scene> getColorScenes() {
        return colorScenes;
    }

    public ArrayList<Scene> getPatternScenes() {
        return patternScenes;
    }

    public boolean isTransitionMode() {
        return transitionMode;
    }

    public void setTransitionMode(boolean transitionMode) {
        this.transitionMode = transitionMode;
    }

    public boolean isNormalMode() {
        return normalMode;
    }

    public void setNormalMode(boolean normalMode) {
        this.normalMode = normalMode;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setColorScenes(ArrayList<Scene> colorScenes) {
        this.colorScenes = colorScenes;
    }

    public int getColorCycle() {
        return colorCycle;
    }

    public void setColorCycle(int colorCycle) {
        this.colorCycle = colorCycle;
    }

    public int getPatternCycle() {
        return patternCycle;
    }

    public void setPatternCycle(int patternCycle) {
        this.patternCycle = patternCycle;
    }

    public Scene getLeapMotionScene() {
        return leapMotionScene;
    }

    public Scene getTutorialScene() {
        return tutorialScene;
    }

    public SceneMode getCurrentMode() {
        return currentMode;
    }

    public int getTransitionTime() {
        return transitionTime;
    }

    public int getCurrentColorScene() {
        return currentColorScene;
    }

    public int getCurrentPatternScene() {
        return currentPatternScene;
    }

    public int getLeapMotionIdleTime() {
        return leapMotionIdleTime;
    }

    public int getLeapMotionIdleTimer() {
        return leapMotionIdleTimer;
    }

    public int getLeapMotionTransitionTime() {
        return leapMotionTransitionTime;
    }

    public int getSceneTimer() {
        return sceneTimer;
    }
}