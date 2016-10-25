package ch.bildspur.ledforest.sketch.controller;

import artnet4j.ArtNetNode;
import ch.bildspur.artnet.ArtNetClient;
import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.sketch.model.Universe;
import ch.bildspur.ledforest.ui.visualisation.Tube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cansik on 25.10.16.
 */
public class ArtNetController extends BaseController {
    float luminosity = 1f;
    float trace = 1f;

    List<Universe> universes;

    ArtNetClient artnet;

    Map<Integer, ArtNetNode> nodes;

    public void init(RenderSketch sketch) {
        super.init(sketch);

        nodes = new HashMap<>();

        artnet = new ArtNetClient();
        artnet.open();
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

            ArtNetNode node = nodes.get(universe.getId());
            artnet.send(node, universe.getId(), universe.getDmxData());
        }
    }

    public float getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(float luminosity) {
        this.luminosity = luminosity;
    }

    public float getTrace() {
        return trace;
    }

    public void setTrace(float trace) {
        this.trace = trace;
    }

    public List<Universe> getUniverses() {
        return universes;
    }

    public ArtNetClient getArtnet() {
        return artnet;
    }

    public Map<Integer, ArtNetNode> getNodes() {
        return nodes;
    }
}
