package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 18/09/16.
 */
public abstract class Scene
{
    RenderSketch sketch;

    public Scene(RenderSketch sketch)
    {
        this.sketch = sketch;
    }

    public boolean isUnique()
    {
        return false;
    }

    public String getName()
    {
        return "Unnamed Scene";
    }

    // will be called when the scene gets enabled
    public void init()
    {
    }

    // will be called every frame
    public void update()
    {
    }

    // will be called when the scene gets stopped
    public void dispose()
    {
    }
}
