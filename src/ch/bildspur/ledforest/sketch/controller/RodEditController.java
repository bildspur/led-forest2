package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import ch.bildspur.ledforest.ui.edit.RodHandle;
import ch.bildspur.ledforest.ui.edit.RodMap;
import ch.bildspur.ledforest.ui.visualisation.Rod;
import ch.bildspur.ledforest.ui.visualisation.Tube;
import ch.bildspur.ledforest.util.Tuple;
import controlP5.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by cansik on 22.09.16.
 */
public class RodEditController extends BaseController {

    Rod selectedRod = null;
    ArrayList<Rod> rods;

    ControlP5 cp5;
    DropdownList rodList;

    Textfield nameField;
    Textfield xAxisField;
    Textfield yAxisField;
    Textfield zAxisField;
    Textfield ledCountField;

    Toggle invertedToggle;

    RodMap rodMap;
    PVector rodMapPosition;

    @Override
    public void init(RenderSketch sketch)
    {
        super.init(sketch);

        rods = sketch.getVisualizer().getRods();

        cp5 = new ControlP5(sketch);
        cp5.setAutoDraw(false);

        initUI();
        updateRodList();
        rodMap.refreshHandles(true);
    }

    public void render()
    {
        // draw rod map
        sketch.image(rodMap.render(), rodMapPosition.x, rodMapPosition.y);

        // draw border
        sketch.noFill();
        sketch.stroke(255);
        sketch.rect(rodMapPosition.x, rodMapPosition.y, rodMap.getWidth(), rodMap.getHeight());

        cp5.draw();
    }

    public void controlEvent(ControlEvent e) {
    }

    public void updateRodList()
    {
        updateRodList(false);
    }

    public void updateRodList(boolean reselectItem)
    {
        int selectedItem = (int)rodList.getValue();

        rodList.setItems(new ArrayList<>());
        for(Rod r : rods)
        {
            rodList.addItem(r.toString(), r);
        }

        if(reselectItem) {
            rodList.setValue(selectedItem);
            rodList.setCaptionLabel(selectedRod.toString());
        }

        rodMap.refreshHandles();
    }

    void updateSelectedRod()
    {
        if(selectedRod == null)
            return;

        nameField.setText(selectedRod.getName());
        xAxisField.setText(Float.toString(selectedRod.getPosition().x));
        yAxisField.setText(Float.toString(selectedRod.getPosition().y));
        zAxisField.setText(Float.toString(selectedRod.getPosition().z));
        ledCountField.setText(Integer.toString(selectedRod.getTube().getLeds().size()));
        invertedToggle.setValue(selectedRod.isInverted());

        // select handler in map
        for(RodHandle h : rodMap.getHandles())
        {
            if(h.getRod().equals(selectedRod)) {
                rodMap.setCurrentHandle(h);
                h.setGrabbed(true);
            }
            else
            {
                h.setGrabbed(false);
            }
        }
    }

    void clearTextfileds()
    {
        nameField.setText("");
        xAxisField.setText("0.0");
        yAxisField.setText("0.0");
        zAxisField.setText("0.0");
        ledCountField.setText("0");
        invertedToggle.setValue(false);
    }

    void initUI() {
        int topControlWidth = 200;

        int editControlHeight = 30;
        int editControlWidth = 50;

        cp5.addLabel("LED Forest 2 - Rod Edit")
                .setPosition(10, 10)
                .setFont(sketch.createFont("Arial", 10));

        // list controls

        cp5.addButton("Add")
                .setValue(0)
                .setPosition(topControlWidth, 10)
                .setSize(50, 10)
                .onClick((e) -> {
                    sketch.addRod(new Rod(sketch.g, new Tube(0, 1, sketch.g), new PVector(0, 0, 0)));
                    selectedRod = null;
                    clearTextfileds();
                    updateRodList();
                });

        cp5.addButton("Remove")
                .setValue(0)
                .setPosition(topControlWidth + 60, 10)
                .setSize(50, 10)
                .onClick((e) -> {
                    if (selectedRod != null) {
                        sketch.removeRod(selectedRod);

                        selectedRod = null;
                        clearTextfileds();
                    }
                    updateRodList();
                });

        rodList = cp5.addDropdownList("rodList")
                .setPosition(topControlWidth + 120, 10)
                .setSize(200, 150)
                .setOpen(false)
                .onChange((e) -> {
                    selectedRod = rods.get((int)e.getController().getValue());
                    updateSelectedRod();
                });

        cp5.addButton("Exit")
                .setValue(0)
                .setPosition(sketch.width - 40, 10)
                .setSize(30, 10)
                .onClick((e) -> {
                    sketch.getPeasy().getCam().setActive(true);
                    sketch.setDrawMode(3);
                });

        cp5.addButton("Save")
                .setValue(0)
                .setPosition(sketch.width - 80, 10)
                .setSize(30, 10)
                .onClick((e) -> {
                    sketch.getConfig().save(RenderSketch.CONFIG_NAME);
                    PApplet.println("config saved!");
                });

        // edit controls
        nameField = cp5.addTextfield("Name")
                .setPosition(editControlWidth, editControlHeight)
                .setSize(100, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if(selectedRod != null) {
                        selectedRod.setName(nameField.getText());
                        updateRodList(true);
                    }
                });

        xAxisField = cp5.addTextfield("X-Axis")
                .setPosition(editControlWidth + 110, editControlHeight)
                .setSize(60, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if(selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(xAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if(result.getFirst())
                            selectedRod.getPosition().set(result.getSecond(), pos.y, pos.z);
                    }
                });

        yAxisField = cp5.addTextfield("Y-Axis")
                .setPosition(editControlWidth + 180, editControlHeight)
                .setSize(60, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if(selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(yAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if(result.getFirst())
                            selectedRod.getPosition().set(pos.x, result.getSecond(), pos.z);
                    }
                });

        zAxisField = cp5.addTextfield("Z-Axis")
                .setPosition(editControlWidth + 250, editControlHeight)
                .setSize(60, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if(selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(zAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if(result.getFirst())
                            selectedRod.getPosition().set(pos.x, pos.y, result.getSecond());
                    }
                });

        ledCountField = cp5.addTextfield("LED Count")
                .setPosition(editControlWidth + 320, editControlHeight)
                .setSize(60, 15)
                .setAutoClear(false)
                .setInputFilter(ControlP5.INTEGER)
                .onChange((e) -> {
                    if(selectedRod != null) {
                        int count = Integer.parseInt(ledCountField.getText());

                        selectedRod.getTube().initLED(count, sketch.g);
                        selectedRod.initShapes();
                    }
                });

        invertedToggle = cp5.addToggle("Inverted")
                .setPosition(editControlWidth + 390, editControlHeight)
                .setSize(60, 15)
                .setMode(ControlP5.DEFAULT)
                .onChange((e) -> {
                    if(selectedRod != null)
                        selectedRod.setInverted(invertedToggle.getBooleanValue());
                });

        rodMap = new RodMap(sketch, 540, 350);
        rodMapPosition = new PVector(50, 75);

        rodList.bringToFront();
    }

    Tuple<Boolean, Float> tryParseFloat(String text)
    {
        boolean parsed = false;
        float value = 0.0f;

        try
        {
            value = Float.parseFloat(text);
            parsed = true;
        }
        catch (Exception ex)
        {
        }

        return new Tuple<>(parsed, value);
    }

    boolean isOverMap(PVector v)
    {
        return v.x >= rodMapPosition.x && v.x < rodMapPosition.x + rodMap.getWidth()
                && v.y >= rodMapPosition.y && v.y < rodMapPosition.y + rodMap.getHeight();
    }

    public RodMap getRodMap() {
        return rodMap;
    }

    public void mousePressed() {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if(isOverMap(mouse)) {
            rodMap.mousePressed(PVector.sub(mouse, rodMapPosition));

            if (rodMap.getCurrentHandle() != null) {
                selectedRod = rodMap.getCurrentHandle().getRod();
                updateSelectedRod();
            }
        }

    }

    public void mouseDragged()
    {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if(isOverMap(mouse)) {
            rodMap.mouseDragged(PVector.sub(mouse, rodMapPosition));

            if(selectedRod != null
                    && rodMap.getCurrentHandle() != null
                    && selectedRod.equals(rodMap.getCurrentHandle().getRod()))
                updateSelectedRod();
        }
    }

    public void mouseReleased() {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if(isOverMap(mouse))
            rodMap.mouseReleased(PVector.sub(mouse, rodMapPosition));
    }
}
