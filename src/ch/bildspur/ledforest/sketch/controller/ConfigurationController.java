package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Created by cansik on 22.09.16.
 */
public class ConfigurationController extends BaseController {

    String CONFIG_DIR;

    @Override
    public void init(RenderSketch sketch) {
        super.init(sketch);

        CONFIG_DIR = sketch.sketchPath("config/");
    }

    public void load(String fileName)
    {
        JSONObject root = sketch.loadJSONObject(CONFIG_DIR + fileName);
        JSONArray rods = root.getJSONArray("rods");

        for(int i = 0; i < rods.size(); i++)
        {
            JSONObject rodJSON = rods.getJSONObject(i);
            sketch.addRod(loadRod(rodJSON));
        }

        System.out.println(rods.size() + " rods loaded!");
    }

    public Thread loadAsync(String fileName)
    {
        Thread t = new Thread(() -> {
            load(fileName);
        });
        t.start();
        return t;
    }

    public void save(String fileName)
    {
        JSONObject root = new JSONObject();
        JSONArray clips = new JSONArray();

        for(Rod r : sketch.getVisualizer().getRods())
            clips.append(getRodJSON(r));
        root.setJSONArray("rods", clips);

        // write file
        sketch.saveJSONObject(root, CONFIG_DIR + fileName);
    }

    private Rod loadRod(JSONObject json)
    {
        JSONObject tube = json.getJSONObject("tube");
        JSONObject pos = json.getJSONObject("position");

        Rod r = new Rod(sketch.g,
                new Tube(tube.getInt("universe"),
                        tube.getInt("ledCount"),
                        sketch.g),
                    new PVector(
                            pos.getFloat("x"),
                            pos.getFloat("y"),
                            pos.getFloat("z")
                    ));

        r.setName(json.getString("name"));
        r.setInverted(json.getBoolean("inverted"));

        return r;
    }

    private JSONObject getRodJSON(Rod rod)
    {
        JSONObject json = new JSONObject();
        JSONObject tube = new JSONObject();
        JSONObject pos = new JSONObject();

        tube.setInt("universe", rod.getTube().getUniverse());
        tube.setInt("ledCount", rod.getTube().getLeds().size());

        pos.setFloat("x", rod.getPosition().x);
        pos.setFloat("y", rod.getPosition().y);
        pos.setFloat("z", rod.getPosition().z);

        json.setJSONObject("tube", tube);
        json.setJSONObject("position", pos);

        json.setString("name", rod.getName());
        json.setBoolean("inverted", rod.isInverted());

        return json;
    }
}
