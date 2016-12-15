package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.audio.InfinityAudioPlayer;
import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import ddf.minim.Minim;

import java.util.HashMap;

/**
 * Created by cansik on 14.12.16.
 */
public class AudioFXController extends BaseController {
    private final String dataFolder = "data/audio/";

    private final String trumpetAudio = "trumpet.wav";

    private final String rassleAudio = "rassle.wav";

    private boolean enabled = false;

    private Minim minim;

    private HashMap<Integer, InfinityAudioPlayer> handPlayers = new HashMap<>();

    InfinityAudioPlayer trumpetPlayer;

    public void init(RenderSketch sketch) {
        super.init(sketch);

        if (!enabled)
            return;

        minim = new Minim(sketch);

        trumpetPlayer = new InfinityAudioPlayer(minim);
        trumpetPlayer.loadFile(sketch.sketchPath(dataFolder + trumpetAudio), 2048);
        trumpetPlayer.play();
    }

    public void updateHandSounds() {
        Frame frame = sketch.getLeapMotion().getFrame();

        // loop through hands and check play state
        for (Hand h : frame.hands()) {
            InfinityAudioPlayer player;
            if (!handPlayers.containsKey(h.id())) {
                // create new player
                player = new InfinityAudioPlayer(minim);
                player.loadFile(sketch.sketchPath(dataFolder + rassleAudio), 2048);
                player.setLoop(500, 1500);
                player.play();
                handPlayers.put(h.id(), player);
            }
            player = handPlayers.get(h.id());

            // set pan
            /*
            player.getPlayer().setPan(PApplet.map(
                    sketch.getLeapMotion().intBoxVector(h.palmPosition()).x,
                    0, sketch.getLeapMotion().interactionBox.x, -1, 1)
            );
            */
        }

        // loop through map and delete old ones
        for (int handIndex : handPlayers.keySet()) {
            if (!frame.hand(handIndex).isValid()) {
                // delete player
                InfinityAudioPlayer player = handPlayers.get(handIndex);
                player.stop();
                player.getPlayer().pause();
                player.getPlayer().close();

                handPlayers.remove(handIndex);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}