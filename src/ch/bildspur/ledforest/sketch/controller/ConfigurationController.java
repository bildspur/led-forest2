package ch.bildspur.ledforest.sketch.controller;

import artnet4j.ArtNetNode;
import ch.bildspur.ifttt.IFTTTClient;
import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.sketch.event.ConfigurationListener;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import ch.bildspur.ledforest.util.MapUtils;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by cansik on 22.09.16.
 */
public class ConfigurationController extends BaseController {

    List<ConfigurationListener> listener = new ArrayList<>();

    String CONFIG_DIR;

    @Override
    public void init(RenderSketch sketch) {
        super.init(sketch);

        CONFIG_DIR = sketch.sketchPath("config/");
    }

    public void addListener(ConfigurationListener l) {
        listener.add(l);
    }

    public void load(String fileName) {
        JSONObject root = sketch.loadJSONObject(CONFIG_DIR + fileName);

        // global
        JSONObject global = root.getJSONObject("global");
        sketch.getLeapMotion().setInteractionBox(vectorFromJSON(global.getJSONObject("interactionBox")));
        sketch.getVisualizer().setBarrelDistortion(vectorFromJSON(global.getJSONObject("barrelDistortion")));

        //ifttt
        IFTTTClient.setEnabled(root.getJSONObject("ifttt").getBoolean("enabled"));

        // rods
        JSONArray rods = root.getJSONArray("rods");

        for (int i = 0; i < rods.size(); i++) {
            JSONObject rodJSON = rods.getJSONObject(i);
            sketch.addRod(loadRod(rodJSON));
        }

        // editor
        JSONObject editor = root.getJSONObject("editor");
        sketch.getRodEditView().setGridSize(editor.getFloat("gridSize"));
        sketch.getRodEditView().setGridOffset(editor.getBoolean("gridOffset"));

        // dmx
        loadDmx(root.getJSONObject("dmx"));

        // syphon
        JSONObject syphon = root.getJSONObject("syphon");
        sketch.getSyphon().setEnabled(syphon.getBoolean("enabled"));

        // osc
        JSONObject osc = root.getJSONObject("osc");
        sketch.getOsc().setEnabled(osc.getBoolean("enabled"));
        sketch.getOsc().setZeroConfEnabled(osc.getBoolean("zeroConf"));

        // audio
        JSONObject audio = root.getJSONObject("audio");
        sketch.getAudioFX().setEnabled(audio.getBoolean("enabled"));

        notifyConfigListener();
        System.out.println(rods.size() + " rods loaded!");
    }

    public Thread loadAsync(String fileName) {
        Thread t = new Thread(() -> {
            load(fileName);
        });
        t.start();
        return t;
    }

    public void save(String fileName) {
        JSONObject root = new JSONObject();
        JSONArray clips = new JSONArray();

        // global
        JSONObject global = new JSONObject();
        global.setJSONObject("interactionBox", jsonFromVector(sketch.getLeapMotion().getInteractionBox()));
        global.setJSONObject("barrelDistortion", jsonFromVector(sketch.getVisualizer().getBarrelDistortion()));
        root.setJSONObject("global", global);

        // ifttt
        JSONObject ifttt = new JSONObject();
        ifttt.setBoolean("enabled", IFTTTClient.isEnabled());
        root.setJSONObject("ifttt", ifttt);

        (sketch.getVisualizer().getRods()).sort(Comparator.comparing(Rod::getName));
        for (Rod r : sketch.getVisualizer().getRods())
            clips.append(getRodJSON(r));
        root.setJSONArray("rods", clips);

        JSONObject editor = new JSONObject();
        editor.setFloat("gridSize", sketch.getRodEditView().getRodMap().getGridSize());
        editor.setBoolean("gridOffset", sketch.getRodEditView().getRodMap().isGridOffset());

        root.setJSONObject("editor", editor);
        root.setJSONObject("dmx", getDmx());

        // syphon
        JSONObject syphon = new JSONObject();
        syphon.setBoolean("enabled", sketch.getSyphon().isEnabled());
        root.setJSONObject("syphon", syphon);

        // osc
        JSONObject osc = new JSONObject();
        osc.setBoolean("enabled", sketch.getOsc().isEnabled());
        osc.setBoolean("zeroConf", sketch.getOsc().isZeroConfEnabled());
        root.setJSONObject("osc", osc);

        // audio
        JSONObject audio = new JSONObject();
        audio.setBoolean("enabled", sketch.getAudioFX().isEnabled());
        root.setJSONObject("audio", audio);

        // write file
        sketch.saveJSONObject(root, CONFIG_DIR + fileName);
    }

    private void loadDmx(JSONObject dmx) {
        ArtNetController artNetController = sketch.getArtNet();

        artNetController.setEnabled(dmx.getBoolean("enabled"));

        JSONObject parameter = dmx.getJSONObject("parameter");
        artNetController.setLuminosity(parameter.getFloat("luminosity"));
        artNetController.setResponse(parameter.getFloat("response"));
        artNetController.setTrace(parameter.getFloat("trace"));

        JSONArray nodeList = dmx.getJSONArray("nodes");
        for (int i = 0; i < nodeList.size(); i++) {
            JSONObject nodeObject = nodeList.getJSONObject(i);
            JSONArray universes = nodeObject.getJSONArray("universes");

            ArtNetNode node = artNetController.getArtnet().createNode(nodeObject.getString("address"));

            for (int j = 0; j < universes.size(); j++) {
                int universeId = universes.getInt(j);
                artNetController.getNodes().put(universeId, node);
            }
        }
    }

    private JSONObject getDmx() {
        JSONObject dmxObject = new JSONObject();
        JSONArray nodeList = new JSONArray();

        dmxObject.setBoolean("enabled", sketch.getArtNet().isEnabled());

        JSONObject parameter = new JSONObject();
        parameter.setFloat("luminosity", sketch.getArtNet().getLuminosity());
        parameter.setFloat("response", sketch.getArtNet().getResponse());
        parameter.setFloat("trace", sketch.getArtNet().getTrace());
        dmxObject.setJSONObject("parameter", parameter);

        Map<ArtNetNode, List<Integer>> nodes = MapUtils.flipMap(sketch.getArtNet().getNodes());

        // add nodes
        for (ArtNetNode n : nodes.keySet()) {
            JSONObject node = new JSONObject();
            node.setString("address", n.getIPAddress().getHostAddress());

            // add universe
            JSONArray universes = new JSONArray();
            nodes.get(n).forEach(universes::append);

            node.setJSONArray("universes", universes);
            nodeList.append(node);
        }

        dmxObject.setJSONArray("nodes", nodeList);
        return dmxObject;
    }

    private Rod loadRod(JSONObject json) {
        JSONObject tube = json.getJSONObject("tube");
        JSONObject pos = json.getJSONObject("position");
        JSONObject rotation = json.getJSONObject("rotation");

        Rod r = new Rod(sketch.g,
                new Tube(tube.getInt("universe"),
                        tube.getInt("ledCount"),
                        tube.getInt("address"),
                        sketch.g),
                vectorFromJSON(pos), vectorFromJSON(rotation));

        r.setName(json.getString("name"));
        r.setInverted(json.getBoolean("inverted"));

        return r;
    }

    private JSONObject getRodJSON(Rod rod) {
        JSONObject json = new JSONObject();
        JSONObject tube = new JSONObject();

        tube.setInt("universe", rod.getTube().getUniverse());
        tube.setInt("ledCount", rod.getTube().getLeds().size());
        tube.setInt("address", rod.getTube().getStartAddress());

        JSONObject pos = jsonFromVector(rod.getRawPosition());
        JSONObject rotation = jsonFromVector(rod.getRotation());

        json.setJSONObject("tube", tube);
        json.setJSONObject("position", pos);
        json.setJSONObject("rotation", rotation);

        json.setString("name", rod.getName());
        json.setBoolean("inverted", rod.isInverted());

        return json;
    }

    private void notifyConfigListener() {
        for (ConfigurationListener l : listener) {
            l.configurationLoaded(this);
        }
    }

    private PVector vectorFromJSON(JSONObject json) {
        return new PVector(
                json.getFloat("x"),
                json.getFloat("y"),
                json.getFloat("z")
        );
    }

    private JSONObject jsonFromVector(PVector vector) {
        JSONObject json = new JSONObject();
        json.setFloat("x", vector.x);
        json.setFloat("y", vector.y);
        json.setFloat("z", vector.z);
        return json;
    }
}
