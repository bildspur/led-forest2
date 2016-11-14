package ch.bildspur.ledforest.sketch.controller;

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

            case "/forest/flash":
                msg.setAddrPattern("/forest/luminosity_mm");
                sketch.getArtNet().setLuminosity(msg.get(0).floatValue());
                break;

            case "/forest/response":
                sketch.getArtNet().setResponse(msg.get(0).floatValue());
                break;

            case "/forest/haze":
                break;

            case "/forest/nextcolor":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Color!");
                    sketch.getSceneManager().nextColorScene();
                }
                break;

            case "/forest/nextlight":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Light!");
                    sketch.getSceneManager().nextPatternScene();
                }
                break;

            case "/forest/nextvideo":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Next Video!");
                    sketch.getVideoScene().dispose();
                    sketch.getVideoScene().init();
                }
                break;

            case "/forest/light":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Light up!");
                    sketch.getColors().setColorToWhite();
                }
                break;

            case "/forest/black":
                if (msg.get(0).floatValue() > 0) {
                    PApplet.println("Blackout Light!");
                    sketch.getSceneManager().setRunning(sketch.getSceneManager().isRunning());
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

            case "/forest/normalmode":
                if (msg.get(0).floatValue() > 0) {
                    /*
                    sketch.getSceneManager().normalMode = true;
                    sketch.getSceneManager().transitionMode = false;
                    */
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

        sendMessage("/forest/info/color", sketch.getSceneManager().getActiveColorScene().getName());
        sendMessage("/forest/info/pattern", sketch.getSceneManager().getActivePatternScene().getName());
        sendMessage("/forest/info/fps", "FPS: " + Math.round(sketch.frameRate));
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
