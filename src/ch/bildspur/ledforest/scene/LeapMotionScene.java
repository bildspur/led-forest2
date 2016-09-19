package ch.bildspur.ledforest.scene;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import processing.core.PVector;

import static processing.core.PConstants.PI;

/**
 * Created by cansik on 18/09/16.
 */
public class LeapMotionScene extends Scene
{
    float fadeSpeed = sketch.secondsToEasing(0.5f);


    float fadeSpeedH = sketch.secondsToEasing(0.1f);
    float fadeSpeedS = sketch.secondsToEasing(0.1f);
    float fadeSpeedBIn = sketch.secondsToEasing(0.3f);
    float fadeSpeedBOut = sketch.secondsToEasing(0.1f);


    float interactionRadius = 50;

    int hue = 0;

    public LeapMotionScene(RenderSketch sketch) {
        super(sketch);
    }

    public String getName()
    {
        return "LeapMotion Scene";
    }

    public void init()
    {
        sketch.getColors().setColorB(0, fadeSpeed);
        sketch.getColors().setColorS(0, fadeSpeed);
        sketch.getColors().setColorH(0, fadeSpeedH);
    }

    public void update()
    {
        PVector[] palms = new PVector[sketch.getLeapMotion().getFrame().hands().count()];

        // update palms
        for (int i = 0; i < palms.length; i++)
        {
            Hand h = sketch.getLeapMotion().getFrame().hands().get(i);
            Vector v = h.palmPosition();
            PVector palmPosition = sketch.getLeapMotion().intBoxVector(v);
            palms[i] = palmPosition;
        }

        // interactionRadius = map(palmPosition.y * -1, 0, interactionBox.y / 2, 50, 300);

        // strength affects saturation
        //setColorS(h.grabStrength() * 100, secondsToEasing(0.5));

        //setColorH(hue, secondsToEasing(0.5));

        for (int j = 0; j < sketch.getTubes().size(); j++)
        {
            Tube t =  sketch.getTubes().get(j);
            for (int i = 0; i < t.getLeds().size(); i++)
            {
                LED led = t.getLeds().get(i);

                boolean isOn = false;
                float minDistance = Float.MAX_VALUE;
                int nearestHandIndex = 0;

                // check if a palm is nearby
                for (int p = 0; p < palms.length; p++)
                {
                    float distance = ledPosition(j, (t.getLeds().size() - 1 - i)).dist(palms[p]);
                    if (distance < interactionRadius)
                    {
                        if (minDistance > distance)
                        {
                            minDistance = distance;
                            nearestHandIndex = p;
                        }

                        isOn = true;
                    }
                }

                if (isOn)
                {
                    Hand h = sketch.getLeapMotion().getFrame().hands().get(nearestHandIndex);

                    led.getColor().fadeH(getHueByHand(h), fadeSpeedH);
                    led.getColor().fadeS(100 - (h.grabStrength() * 100), fadeSpeedS);
                    led.getColor().fadeB(100, fadeSpeedBIn);
                } else
                {
          /*
          led.c.fadeH(getHueByHand(h), fadeSpeedH);
          led.c.fadeS(100 - (h.grabStrength() * 100), fadeSpeedS);
          */
                    //led.c.fadeS(0, fadeSpeedS);
                    led.getColor().fadeB(0, fadeSpeedBOut);
                }
            }
        }

        if (sketch.frameCount % sketch.secondsToFrames(1) == 0)
            hue = (hue + 1) % 360;
    }

    float getHueByHand(Hand h)
    {
        float roll = Math.abs(h.palmNormal().roll());
        return sketch.map(roll, 0, PI, 0, 360);
    }

    public PVector ledPosition(int rodIndex, int ledIndex)
    {
        Rod r = sketch.getVisualizer().getRods().get(rodIndex);
        float ledLength = r.getLedLength();
        return new PVector(r.p.x, r.p.y + ((r.getShapes().size() - ledIndex) * ledLength), r.p.z);
    }
}