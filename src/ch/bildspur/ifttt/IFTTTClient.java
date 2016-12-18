package ch.bildspur.ifttt;

import com.goebl.david.Webb;
import processing.core.PApplet;

/**
 * Created by cansik on 18.12.16.
 */
public class IFTTTClient {
    private static Webb webb = Webb.create();
    private static String IFTTT_URL = "https://maker.ifttt.com/trigger/ledforest/with/key/u4UidDDEGpwpLIruemMyf";

    public static void sendStatus(String event, String status, String error) {
        try {
            new Thread(() -> webb.post(IFTTT_URL)
                    .param("value1", event)
                    .param("value2", status)
                    .param("value3", error)
                    .ensureSuccess()
                    .asVoid()).start();
        } catch (Exception ex) {
            PApplet.println("could not send status!");
        }
    }
}
