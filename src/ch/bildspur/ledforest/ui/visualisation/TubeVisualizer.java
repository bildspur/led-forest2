package ch.bildspur.ledforest.ui.visualisation;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by cansik on 18/09/16.
 */
public class TubeVisualizer
{
    public int rodColumnCount = 3;
    public int rodRowCount = 5;

    // 3d vars
    float rodSpaceWidthDistance = 40;
    float rodSpaceDepthDistance = 50;
    float rodHeight = -40;

    ArrayList<Rod> rods;

    // 2d vars
    float wspace2d = 20;
    float hspace2d = 2;
    float width2d = 15;
    float height2d = 15;
    float hoffset = 50;
    float woffset = 50;

    PGraphics output2d;

    PApplet sketch;

    public TubeVisualizer(PApplet sketch)
    {
        this.sketch = sketch;
        output2d = sketch.createGraphics(640, 480, PConstants.P2D);
        rods = new ArrayList<>();
    }

    public void render3d()
    {
        for (Rod r : rods)
        {
            r.render();
        }
    }

    public PGraphics render2d()
    {
        PGraphics p = output2d;

        p.beginDraw();
        p.background(0);
        for (int i = 0; i < rods.size(); i++)
        {
            Rod rod = rods.get(i);
            Tube t = rod.tube;

            p.noStroke();
            p.fill(255);
            p.textSize(12);
            p.text(rod.getName(),
                    i * (wspace2d + width2d) + woffset,
                    (hspace2d + height2d) + hoffset - (1.5f * height2d));

            // draw leds
            for (int j = 0; j < t.leds.size(); j++)
            {
                LED l = t.leds.get(t.leds.size() - 1 - j);
                p.fill(l.getColor().getColor());
                p.rect(i * (wspace2d + width2d) + woffset,
                        j * (hspace2d + height2d) + hoffset,
                        width2d, height2d);
            }
        }
        p.endDraw();

        return p;
    }

    public void initRods(ArrayList<Tube> tubes)
    {
        rods = new ArrayList<>();

        float deltaX = (rodSpaceWidthDistance * rodRowCount) / 2f - (rodSpaceWidthDistance / 2);
        float deltaZ = (rodSpaceDepthDistance * rodColumnCount) / 2f - (rodSpaceDepthDistance / 2);

        for (int z = 0; z < rodColumnCount; z++)
        {
            for (int x = 0; x < rodRowCount; x++)
            {
                PVector pos = new PVector(x * rodSpaceWidthDistance - deltaX, rodHeight, z * rodSpaceDepthDistance - deltaZ);
                rods.add(new Rod(sketch.g, tubes.get((z * rodRowCount) + x), pos));
            }
        }
    }

    public ArrayList<Rod> getRods() {
        return rods;
    }
}