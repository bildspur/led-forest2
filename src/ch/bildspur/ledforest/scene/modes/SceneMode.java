package ch.bildspur.ledforest.scene.modes;

import ch.bildspur.ledforest.scene.SceneManager;
import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 18/09/16.
 */
public abstract class SceneMode
{
    SceneManager sceneManager;
    RenderSketch sketch;
    int timer = 0;

    public SceneMode(RenderSketch sketch, SceneManager sceneManager)
    {
        this.sceneManager = sceneManager;
        this.sketch = sketch;
    }

    public void init() {
    }

    public void update() {
        timer++;
    }

    public void close() {
    }
}