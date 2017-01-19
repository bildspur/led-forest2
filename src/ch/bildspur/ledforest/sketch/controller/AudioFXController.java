package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.audio.InfinityAudioPlayer;
import ch.bildspur.ledforest.sketch.RenderSketch;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import ddf.minim.Minim;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by cansik on 14.12.16.
 */
public class AudioFXController extends BaseController {
    private final int PLAYER_BUFFER_SIZE = 4;

    private final String dataFolder = "data/audio/";

    private final String trumpetAudio = "trumpet.wav";

    private final String rassleAudio = "rassle.wav";

    private boolean enabled = false;

    private Minim minim;

    private HashMap<Integer, InfinityAudioPlayer> handPlayers = new HashMap<>();

    private InfinityAudioPlayer[] playerBuffer = new InfinityAudioPlayer[PLAYER_BUFFER_SIZE];

    private int playerBufferIndex = -1;

    private InfinityAudioPlayer trumpetPlayer;

    private long handCount = 0;

    public void init(RenderSketch sketch) {
        super.init(sketch);

        if (!enabled)
            return;

        minim = new Minim(sketch);

        initBuffer();

        trumpetPlayer = new InfinityAudioPlayer(minim);
        trumpetPlayer.loadFile(sketch.sketchPath(dataFolder + trumpetAudio), 4096);
        trumpetPlayer.getPlayer().loop();
    }

    public void initBuffer() {
        for (int i = 0; i < playerBuffer.length; i++) {
            InfinityAudioPlayer player = new InfinityAudioPlayer(minim);
            player.loadFile(sketch.sketchPath(dataFolder + rassleAudio), 4096);
            player.setLoop(2000, 15000);
            playerBuffer[i] = player;
        }
    }

    public void updateHandSounds() {
        if (!sketch.getLeapMotion().isLeapConnected())
            return;

        Frame frame = sketch.getLeapMotion().getFrame();

        // loop through hands and check play state
        for (Hand h : frame.hands()) {
            if (!h.isValid())
                return;

            InfinityAudioPlayer player;
            if (!handPlayers.containsKey(h.id())) {
                // create new player
                playerBufferIndex = (playerBufferIndex + 1) % PLAYER_BUFFER_SIZE;
                player = playerBuffer[playerBufferIndex];
                player.getPlayer().setGain(0f);
                player.play();
                handPlayers.put(h.id(), player);
                handCount++;
            }
            player = handPlayers.get(h.id());

            // set pan
            player.getPlayer().setBalance(PApplet.map(
                    sketch.getLeapMotion().intBoxVector(h.palmPosition()).x,
                    -sketch.getLeapMotion().interactionBox.x / 2f,
                    sketch.getLeapMotion().interactionBox.x / 2f, -1, 1)
            );
        }

        // loop through map and delete old ones
        for (int handIndex : new HashSet<>(handPlayers.keySet())) {
            InfinityAudioPlayer player = handPlayers.get(handIndex);

            // stop player
            if (!frame.hand(handIndex).isValid()) {
                player.stop();
            }

            if (player.isStopping()) {
                player.getPlayer().setGain(PApplet.max(-80, player.getPlayer().getGain() - 0.3f));
            }

            // remove player
            if (!player.getPlayer().isPlaying() || player.getPlayer().getGain() <= -80) {
                player.getPlayer().pause();
                handPlayers.remove(handIndex);
            }
        }

        sketch.getDebug().addMessage("Hands: " + handPlayers.size());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void stop() {
        if (!isEnabled())
            return;

        // clear buffer
        for (int i = 0; i < playerBuffer.length; i++) {
            playerBuffer[i].getPlayer().pause();
            playerBuffer[i].getPlayer().close();
        }

        trumpetPlayer.getPlayer().pause();
        trumpetPlayer.getPlayer().close();
    }

    public long getHandCount() {
        return handCount;
    }
}