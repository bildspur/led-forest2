package ch.bildspur.ifttt;

import ch.bildspur.ledforest.sketch.RenderSketch;
import com.goebl.david.Webb;
import processing.core.PApplet;

/**
 * Created by cansik on 18.12.16.
 */
public class IFTTTClient {
    private static boolean enabled = true;
    private static Webb webb = Webb.create();
    private static String IFTTT_URL = "https://maker.ifttt.com/trigger/installation/with/key/u4UidDDEGpwpLIruemMyf";

    public static void sendStatus(String event, String status, String error) {
        if (!enabled)
            return;

        try {
            new Thread(() -> webb.post(IFTTT_URL)
                    .param("value1", RenderSketch.APP_NAME + " - " + event)
                    .param("value2", status)
                    .param("value3", error)
                    .ensureSuccess()
                    .asVoid()).start();
        } catch (Exception ex) {
            PApplet.println("could not send status!");
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        IFTTTClient.enabled = enabled;
    }
}
