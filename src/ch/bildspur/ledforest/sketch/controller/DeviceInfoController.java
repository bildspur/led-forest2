package ch.bildspur.ledforest.sketch.controller;

import ch.bildspur.ledforest.sketch.RenderSketch;
import com.yoctopuce.YoctoAPI.*;

/**
 * Created by cansik on 15.11.16.
 */
public class DeviceInfoController extends BaseController {
    private final String CONNECTION_MODE = "usb";

    private YHumidity humiditySensor;
    private YTemperature temperatureSensor;
    private YPressure pressureSensor;

    @Override
    public void init(RenderSketch sketch) {
        super.init(sketch);

        try {
            YAPI.RegisterHub(CONNECTION_MODE);
        } catch (YAPI_Exception e)

        {
            System.out.println("Cannot contact VirtualHub on 127.0.0.1 (" + e.getLocalizedMessage() + ")");
            System.out.println("Ensure that the VirtualHub application is running");
        }

        // find sensors
        humiditySensor = YHumidity.FirstHumidity();
        temperatureSensor = YTemperature.FirstTemperature();
        pressureSensor = YPressure.FirstPressure();

        if (humiditySensor == null || temperatureSensor == null || pressureSensor == null) {
            System.out.println("No module connected (check USB cable)");
        }

    }

    public void close() {
        YAPI.FreeAPI();
    }

    public double getHumidity() {
        try {
            return humiditySensor.get_currentValue();
        } catch (YAPI_Exception | NullPointerException e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }

    public double getTemperature() {
        try {
            return temperatureSensor.get_currentValue();
        } catch (YAPI_Exception | NullPointerException e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }

    public double getPressure() {
        try {
            return pressureSensor.get_currentValue();
        } catch (YAPI_Exception | NullPointerException e) {
            e.printStackTrace();
        }

        return Double.NaN;
    }
}
