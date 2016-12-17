package ch.bildspur.ledforest.audio;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

/**
 * Created by cansik on 14.12.16.
 */
public class InfinityAudioPlayer {
    private Minim minim;
    private AudioPlayer player;

    private boolean stopping = false;

    public InfinityAudioPlayer(Minim minim) {
        this.minim = minim;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void loadFile(String fileName, int bufferSize) {
        player = minim.loadFile(fileName, bufferSize);
    }

    public void setLoop(int start, int end) {
        player.setLoopPoints(start, end);
    }

    public void play() {
        stopping = false;
        player.loop();
        player.skip(0);
    }

    public void stop() {
        stopping = true;
        player.play();
    }

    public void fastStop() {
        player.pause();
    }

    public void close() {
        player.close();
    }

    public AudioPlayer getPlayer() {
        return this.player;
    }

    public boolean isStopping() {
        return stopping;
    }
}
