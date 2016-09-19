package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;

/**
 * Created by cansik on 18/09/16.
 */
public class FloatingPatternScene extends Scene
{
    float fadeSpeed = sketch.secondsToEasing(0.1f);

    float[][] heightMap;
    float[][] velMap;
    float gravity=0.1f;
    int[] sel=null;
    int zeroHeight = 15;

    public FloatingPatternScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "Floating Pattern Scene";
    }

    public void init()
    {
        heightMap = new float[6][6];
        velMap = new float[6][6];
    }


    public void update()
    {
        //if (frameCount % secondsToFrames(0.1) != 0)
        //  return;

        updateVelocityAndHeight();

        if(sketch.random(0, 1) > 0.99)
        {
            heightMap[(int)sketch.random(1, 3)][(int)sketch.random(1, 3)] = sketch.random(1, 10);
        }

        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            float h = heightMap[j / 4][j%4];
            for (int i = 0; i < sketch.getTubes().get(j).getLeds().size(); i++)
            {
                if (i < h + zeroHeight)
                {
                    sketch.getTubes().get(j).getLeds().get(i).getColor().fadeB(100, fadeSpeed);
                } else
                {
                    sketch.getTubes().get(j).getLeds().get(i).getColor().fadeB(0, fadeSpeed);
                }
            }
        }
    }


    void updateVelocityAndHeight() {
        int y, x;
        for (y = 0; y < 4; y++) {
            for (x = 0; x < 4; x++) {
                if (sel != null && sel[0] == x && sel[1] == y) continue;
                float h = heightMap[x][y];
                //float v = velMap[x][y];
                int x1 = 0;
                int y1 = 0;

                float f = 0;
                for (y1 = -1; y1 < 2; y1++) {
                    for (x1 = -1; x1 < 2; x1++) {
                        if (x + x1 > 0 && x + x1 < 19 && y + y1 > 0 && y + y1 < 19 && (x1 != 0 || y1 != 0)) {
                            f += heightMap[x + x1][y + y1];
                        }
                    }
                }
                f /= 6;
                f = (f - h) / 8;

                velMap[x][y] -= f;
                velMap[x][y] *= 0.95; //0.95;

                heightMap[x][y] -= velMap[x][y];
            }
        }
    }
}