package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.audio.InfinityAudioPlayer;
import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.Frame;
import ddf.minim.Minim;

import java.util.HashMap;

/**
 * Created by cansik on 14.12.16.
 */
public class AudioFXController extends BaseController {
    private final String dataFolder = "audio/";

    private boolean enabled = false;

    private Minim minim;

    private HashMap<Integer, InfinityAudioPlayer> handPlayers;

    public void init(RenderSketch sketch) {
        super.init(sketch);

        minim = new Minim(sketch);
    }

    void updateHandSounds() {
        Frame frame = sketch.getLeapMotion().getFrame();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}