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
import java.util.Map;

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
    Textfield universeField;
    Textfield startAddressField;

    Toggle invertedToggle;

    Slider luminanceSlider;
    Slider responseSlider;
    Slider traceSlider;

    RodMap rodMap;
    PVector rodMapPosition;

    DropdownList gridList;
    Toggle gridOffsetToggle;

    Toggle markRodToggle;

    @Override
    public void init(RenderSketch sketch) {
        super.init(sketch);

        rods = sketch.getVisualizer().getRods();

        cp5 = new ControlP5(sketch);
        cp5.setAutoDraw(false);

        initUI();
        updateGridList();
        updateRodList();
        rodMap.refreshHandles(true);
    }

    public void render() {
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

    public void updateGridList() {
        // add grid values
        gridList.addItem("None", 0f);
        gridList.addItem("10 px", 10f);
        gridList.addItem("20 px", 20f);
        gridList.addItem("30 px", 30f);
        gridList.addItem("50 px", 50f);
    }

    public void updateRodList() {
        updateRodList(false);
    }

    public void updateRodList(boolean reselectItem) {
        int selectedItem = (int) rodList.getValue();

        rodList.setItems(new ArrayList<>());
        for (Rod r : rods) {
            rodList.addItem(r.toString(), r);
        }

        if (reselectItem) {
            rodList.setValue(selectedItem);
            rodList.setCaptionLabel(selectedRod.toString());
        }

        rodMap.refreshHandles();
    }

    public void setGridSize(float size) {
        rodMap.setGridSize(size);
        gridList.setCaptionLabel((int) size + " px");
    }

    public void setGridOffset(boolean value) {
        gridOffsetToggle.setState(value);
    }

    void updateSelectedRod() {
        if (selectedRod == null)
            return;

        rodList.setCaptionLabel(selectedRod.getName());
        nameField.setText(selectedRod.getName());
        xAxisField.setText(Float.toString(selectedRod.getPosition().x));
        yAxisField.setText(Float.toString(selectedRod.getPosition().y));
        zAxisField.setText(Float.toString(selectedRod.getPosition().z));
        ledCountField.setText(Integer.toString(selectedRod.getTube().getLeds().size()));
        invertedToggle.setState(selectedRod.isInverted());
        universeField.setText(Integer.toString(selectedRod.getTube().getUniverse()));
        startAddressField.setText(Integer.toString(selectedRod.getTube().getStartAddress()));

        // select handler in map
        for (RodHandle h : rodMap.getHandles()) {
            if (h.getRod().equals(selectedRod)) {
                rodMap.setCurrentHandle(h);
                h.setGrabbed(true);
            } else {
                h.setGrabbed(false);
            }
        }

        // mark rod
        if (markRodToggle.getState()) {
            selectedRod.getTube().getLeds().forEach((e) -> e.getColor().fade(sketch.g.color(0, 100, 100), sketch.secondsToEasing(0.25f)));
        }
    }

    void deselectRod() {
        if (selectedRod != null)
            selectedRod.getTube().getLeds().forEach((e) -> e.getColor().fade(sketch.g.color(0, 0, 50), sketch.secondsToEasing(0.25f)));

        selectedRod = null;
    }

    void clearTextfileds() {
        nameField.setText("");
        xAxisField.setText("0.0");
        yAxisField.setText("0.0");
        zAxisField.setText("0.0");
        ledCountField.setText("0");
        invertedToggle.setValue(false);
        universeField.setText("0");
        startAddressField.setText("0");
    }

    void initUI() {
        int topControlWidth = 200;

        int editControlHeight = 30;
        int editControlWidth = 50;

        cp5.addLabel("LED Forest 2 - " + RenderSketch.VERSION)
                .setPosition(10, 10);

        // list controls

        cp5.addButton("Copy")
                .setValue(0)
                .setPosition(topControlWidth, 10)
                .setSize(50, 10)
                .onClick((e) -> {
                    if (selectedRod != null) {
                        sketch.addRod(new Rod(sketch.g, new Tube(selectedRod.getTube().getUniverse(),
                                selectedRod.getTube().getLeds().size(),
                                selectedRod.getTube().getStartAddress(),
                                sketch.g),
                                PVector.add(selectedRod.getPosition(), new PVector(5, 0, 5))));
                        clearTextfileds();
                        updateRodList();
                    }
                });

        cp5.addButton("Add")
                .setValue(0)
                .setPosition(topControlWidth + 60, 10)
                .setSize(50, 10)
                .onClick((e) -> {
                    sketch.addRod(new Rod(sketch.g, new Tube(0, 1, 0, sketch.g), new PVector(0, 0, 0)));
                    deselectRod();
                    clearTextfileds();
                    updateRodList();
                });

        cp5.addButton("Remove")
                .setValue(0)
                .setPosition(topControlWidth + 120, 10)
                .setSize(50, 10)
                .onClick((e) -> {
                    if (selectedRod != null) {
                        sketch.removeRod(selectedRod);

                        deselectRod();
                        clearTextfileds();
                    }
                    updateRodList();
                });

        rodList = cp5.addDropdownList("rodList")
                .setPosition(topControlWidth + 180, 10)
                .setSize(160, 150)
                .setOpen(false)
                .onChange((e) -> {
                    deselectRod();
                    selectedRod = rods.get((int) e.getController().getValue());
                    updateSelectedRod();
                });

        cp5.addButton("Back")
                .setValue(0)
                .setPosition(sketch.width - 40, 10)
                .setSize(30, 10)
                .onClick((e) -> {
                    sketch.getArtNet().initUniverses();
                    sketch.getSceneManager().setRunning(true);
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
                .setSize(80, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        selectedRod.setName(nameField.getText());
                        updateRodList(true);
                    }
                });

        xAxisField = cp5.addTextfield("X-Axis")
                .setPosition(editControlWidth + 90, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(xAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if (result.getFirst())
                            selectedRod.getPosition().set(result.getSecond(), pos.y, pos.z);
                    }
                });

        yAxisField = cp5.addTextfield("Y-Axis")
                .setPosition(editControlWidth + 150, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(yAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if (result.getFirst())
                            selectedRod.getPosition().set(pos.x, result.getSecond(), pos.z);
                    }
                });

        zAxisField = cp5.addTextfield("Z-Axis")
                .setPosition(editControlWidth + 210, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        Tuple<Boolean, Float> result = tryParseFloat(zAxisField.getText());
                        PVector pos = selectedRod.getPosition();

                        if (result.getFirst())
                            selectedRod.getPosition().set(pos.x, pos.y, result.getSecond());
                    }
                });

        invertedToggle = cp5.addToggle("Inverted")
                .setPosition(editControlWidth + 270, editControlHeight)
                .setSize(50, 15)
                .setMode(ControlP5.DEFAULT)
                .onChange((e) -> {
                    if (selectedRod != null)
                        selectedRod.setInverted(invertedToggle.getState());
                });

        ledCountField = cp5.addTextfield("LED Count")
                .setPosition(editControlWidth + 330, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .setInputFilter(ControlP5.INTEGER)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        int count = Integer.parseInt(ledCountField.getText());

                        selectedRod.getTube().initLED(count, selectedRod.getTube().getStartAddress(), sketch.g);
                        selectedRod.initShapes();
                    }
                });

        universeField = cp5.addTextfield("Universe")
                .setPosition(editControlWidth + 390, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .setInputFilter(ControlP5.INTEGER)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        int universe = Integer.parseInt(universeField.getText());
                        selectedRod.getTube().setUniverse(universe);
                    }
                });

        startAddressField = cp5.addTextfield("Start Address")
                .setPosition(editControlWidth + 450, editControlHeight)
                .setSize(50, 15)
                .setAutoClear(false)
                .setInputFilter(ControlP5.INTEGER)
                .onChange((e) -> {
                    if (selectedRod != null) {
                        int startAddress = Integer.parseInt(startAddressField.getText());

                        selectedRod.getTube().initLED(selectedRod.getTube().getLeds().size(), startAddress, sketch.g);
                        selectedRod.initShapes();
                    }
                });

        rodMap = new RodMap(sketch, 540, 350);
        rodMapPosition = new PVector(50, 75);

        // sub rod map
        cp5.addLabel("Grid: ")
                .setPosition(rodMapPosition.x, rodMapPosition.y + rodMap.getHeight() + 10);

        gridList = cp5.addDropdownList("None")
                .setPosition(rodMapPosition.x + 30, rodMapPosition.y + rodMap.getHeight() + 10)
                .setSize(50, 45)
                .setOpen(false)
                .onChange((e) -> {
                    Map<String, Object> entry = gridList.getItem((int) e.getController().getValue());
                    setGridSize((float) entry.get("value"));
                });

        gridOffsetToggle = cp5.addToggle("Offset")
                .setPosition(rodMapPosition.x + 90, rodMapPosition.y + rodMap.getHeight() + 10)
                .setSize(30, 10)
                .setMode(ControlP5.DEFAULT)
                .onChange((e) -> {
                    rodMap.setGridOffset(gridOffsetToggle.getState());
                });

        // mark
        markRodToggle = cp5.addToggle("Mark Rod")
                .setPosition(sketch.width - 100, rodMapPosition.y + rodMap.getHeight() + 10)
                .setSize(30, 10)
                .setMode(ControlP5.DEFAULT)
                .onChange((e) -> {
                    if (markRodToggle.getState()) {
                        updateSelectedRod();
                    }
                });

        luminanceSlider = cp5.addSlider("lumi")
                .setPosition(sketch.width - 40, rodMapPosition.y)
                .setSize(15, 80)
                .setRange(0, 1)
                .setValue(sketch.getArtNet().getLuminosity())
                .onChange((e) -> {
                    sketch.getArtNet().setLuminosity(luminanceSlider.getValue());
                });

        responseSlider = cp5.addSlider("resp")
                .setPosition(sketch.width - 40, rodMapPosition.y + 110)
                .setSize(15, 80)
                .setRange(-0.9f, 0.9f)
                .setValue(sketch.getArtNet().getResponse())
                .onChange((e) -> {
                    sketch.getArtNet().setResponse(responseSlider.getValue());
                });

        traceSlider = cp5.addSlider("trace")
                .setPosition(sketch.width - 40, rodMapPosition.y + 220)
                .setSize(15, 80)
                .setRange(0, 1)
                .setValue(sketch.getArtNet().getTrace())
                .onChange((e) -> {
                    sketch.getArtNet().setTrace(traceSlider.getValue());
                });


        rodList.bringToFront();
    }

    Tuple<Boolean, Float> tryParseFloat(String text) {
        boolean parsed = false;
        float value = 0.0f;

        try {
            value = Float.parseFloat(text);
            parsed = true;
        } catch (Exception ex) {
        }

        return new Tuple<>(parsed, value);
    }

    boolean isOverMap(PVector v) {
        return v.x >= rodMapPosition.x && v.x < rodMapPosition.x + rodMap.getWidth()
                && v.y >= rodMapPosition.y && v.y < rodMapPosition.y + rodMap.getHeight();
    }

    public RodMap getRodMap() {
        return rodMap;
    }

    public void mousePressed() {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if (isOverMap(mouse)) {
            rodMap.mousePressed(PVector.sub(mouse, rodMapPosition));

            if (rodMap.getCurrentHandle() != null) {
                deselectRod();
                selectedRod = rodMap.getCurrentHandle().getRod();
                updateSelectedRod();
            }
        }

    }

    public void mouseDragged() {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if (isOverMap(mouse)) {
            rodMap.mouseDragged(PVector.sub(mouse, rodMapPosition));

            if (selectedRod != null
                    && rodMap.getCurrentHandle() != null
                    && selectedRod.equals(rodMap.getCurrentHandle().getRod()))
                updateSelectedRod();
        }
    }

    public void mouseReleased() {
        PVector mouse = new PVector(sketch.mouseX, sketch.mouseY);
        if (isOverMap(mouse))
            rodMap.mouseReleased(PVector.sub(mouse, rodMapPosition));
    }
}
