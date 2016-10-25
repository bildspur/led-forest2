package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.sketch.model.Universe;
import ch.bildspur.ledforest.ui.visualisation.Tube;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cansik on 25.10.16.
 */
public class ArtNetController extends BaseController {
    float luminosity = 1f;
    float trace = 1f;

    List<Universe> universes;

    public void init(RenderSketch sketch) {
        super.init(sketch);
    }

    public void initUniverses() {
        universes = new ArrayList<>();
        Map<Integer, List<Tube>> tubes = sketch.getTubesByUniverse();

        for (int id : tubes.keySet())
            universes.add(new Universe(id, tubes.get(id)));
    }

    public void sendDmx() {
        for (Universe universe : universes) {
            universe.stageDmx(luminosity, trace);

            // todo: send out dmx data
        }
    }
}
