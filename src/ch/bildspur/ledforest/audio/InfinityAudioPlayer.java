package ch.bildspur.ledforest.audio;

import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

/**
 * Created by cansik on 14.12.16.
 */
class InfinityAudioPlayer implements AudioListener {
    private Minim minim;
    private AudioPlayer player;

    private int loopStart;
    private int loopEnd;

    private boolean stopping = false;

    public InfinityAudioPlayer(Minim minim) {
        this.minim = minim;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void loadFile(String fileName, int bufferSize) {
        player = minim.loadFile(fileName, bufferSize);
        player.addListener(this);
        setLoop(0, player.length());
    }

    public void setLoop(int start, int end) {
        loopStart = start;
        loopEnd = end;
    }

    public void play() {
        player.play(0);
    }

    public void stop() {
        stopping = true;
    }

    public void fastStop() {
        player.pause();
    }

    void update() {
        if (!player.isPlaying())
            return;

        if (!stopping && player.position() >= loopEnd)
            player.play(loopStart);


        if (stopping && player.position() < loopStart) {
            player.skip(loopEnd);
        }
    }

    public AudioPlayer getPlayer() {
        return this.player;
    }

    public synchronized void samples(float[] samp) {
        update();
    }

    public synchronized void samples(float[] sampL, float[] sampR) {
        update();
    }
}
