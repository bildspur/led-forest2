package ch.bildspur.ledforest.scene.interaction;

import ch.bildspur.ledforest.ui.visualisation.LED;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import com.leapmotion.leap.Hand;

/**
 * Created by cansik on 13.11.16.
 */
public class InteractionData {
    final Hand hand;
    final Tube tube;
    final LED led;
    final float distance;

    public InteractionData(Tube tube, LED led, Hand hand, float distance) {
        this.hand = hand;
        this.led = led;
        this.tube = tube;
        this.distance = distance;
    }
}
