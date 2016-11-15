package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.scene.modes.NormalMode;
import ch.bildspur.ledforest.sketch.RenderSketch;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

/**
 * Created by cansik on 18/09/16.
 */
public class OscController extends BaseController {
    boolean enabled = false;

    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    // OSC server and client
    OscP5 osc;
    NetAddress apps;

    public void setupOSC() {
        //init osc with default ports
        osc = new OscP5(this, 9000);
        apps = new NetAddress("255.255.255.255", 8000);
    }

    public void oscEvent(OscMessage msg) {
        switch (msg.addrPattern()) {
            case "/forest/luminosity":
                sketch.getArtNet().setLuminosity(msg.get(0).floatValue());
                break;

            case "/forest/response":
                sketch.getArtNet().setResponse(msg.get(0).floatValue());
                break;

            case "/forest/trace":
                sketch.getArtNet().setTrace(msg.get(0).floatValue());
                break;

            case "/forest/resetLuminosity":
                if (msg.get(0).floatValue() > 0) {
                    sketch.getArtNet().setLuminosity(1);
                }
                break;

            case "/forest/resetResponse":
                if (msg.get(0).floatValue() > 0) {
                    sketch.getArtNet().setResponse(0);
                }
                break;

            case "/forest/resetTrace":
                if (msg.get(0).floatValue() > 0) {
                    sketch.getArtNet().setTrace(0);
                }
                break;

            case "/forest/nextColor":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Color!");
                    sketch.getSceneManager().nextColorScene();
                }
                break;

            case "/forest/nextPattern":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Pattern!");
                    sketch.getSceneManager().nextPatternScene();
                }
                break;

            case "/forest/nextVideo":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Video!");
                    sketch.getVideoScene().dispose();
                    sketch.getVideoScene().init();
                }
                break;

            case "/forest/light":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Light up!");
                    sketch.getSceneManager().setRunning(!sketch.getSceneManager().isRunning());
                    sketch.getColors().setColorToWhite();
                }
                break;

            case "/forest/black":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Blackout Light!");
                    sketch.getSceneManager().setRunning(!sketch.getSceneManager().isRunning());
                    sketch.getColors().setColor(sketch.g.color(100, 100, 0), sketch.secondsToEasing(1));
                }
                break;

            case "/forest/info":
                if (msg.get(0).floatValue() > 0) {
                    sketch.setShowInfo(!sketch.isShowInfo());
                }
                break;

            case "/forest/2d":
                if (msg.get(0).floatValue() > 0) {
                    sketch.setDrawMode(2);
                }
                break;

            case "/forest/3d":
                if (msg.get(0).floatValue() > 0) {
                    sketch.setDrawMode(3);
                }
                break;

            case "/forest/normalMode":
                if (msg.get(0).floatValue() > 0) {
                    sketch.getSceneManager().setRunning(true);
                    sketch.getSceneManager().changeMode(new NormalMode(sketch, sketch.getSceneManager()));
                }
                break;

            case "/forest/update":
                if (msg.get(0).floatValue() > 0) {
                    updateOSCApp();
                }
                break;
        }
    }

    public void updateOSCApp() {
        sendMessage("/forest/luminosity", sketch.getArtNet().getLuminosity());
        sendMessage("/forest/response", sketch.getArtNet().getResponse());
        sendMessage("/forest/trace", sketch.getArtNet().getTrace());

        sendMessage("/forest/info/color", sketch.getSceneManager().getActiveColorScene().getName());
        sendMessage("/forest/info/pattern", sketch.getSceneManager().getActivePatternScene().getName());
        sendMessage("/forest/info/fps", "FPS: " + Math.round(sketch.frameRate));

        sendMessage("/forest/info/sceneManager", "SCN: " + (sketch.getSceneManager().isRunning() ? "RUN" : "STOP"));
    }

    public void sendMessage(String address, float value) {
        OscMessage m = new OscMessage(address);
        m.add(value);
        osc.send(m, apps);
    }

    public void sendMessage(String address, String value) {
        OscMessage m = new OscMessage(address);
        m.add(value);
        osc.send(m, apps);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
