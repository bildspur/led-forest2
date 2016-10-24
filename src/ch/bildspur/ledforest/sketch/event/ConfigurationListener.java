package ch.bildspur.ledforest.sketch.event;

import ch.bildspur.ledforest.sketch.controller.ConfigurationController;

/**
 * Created by cansik on 25.10.16.
 */
public interface ConfigurationListener {
    void configurationLoaded(ConfigurationController configurationController);
}
