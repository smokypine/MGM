package com.example.MultiGreenMaster.api;

import com.example.MultiGreenMaster.device.ScFrm;
import com.example.MultiGreenMaster.device.UdpCon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/udp")
public class UdpAPI {

    @Autowired
    private UdpCon udpCon;

    @GetMapping("/sensor")
    public Map<String, Object> getSensorData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] data = udpCon.start("175.123.202.85", 20920);
            response.put("sensorData", data);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/rotater")
    public Map<String, Object> getRotaterData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] data = udpCon.actuator("175.123.202.85", 20920, ScFrm.ROTATER);
            response.put("rotaterData", data);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/sprinkler")
    public Map<String, Object> getSprinklerData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] data = udpCon.actuator("175.123.202.85", 20920, ScFrm.SPRINKLER);
            response.put("sprinklerData", data);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/led")
    public Map<String, Object> getLedData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String[] data = udpCon.actuator("175.123.202.85", 20920, ScFrm.LED);
            response.put("ledData", data);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/screenshot")
    public Map<String, Object> getScreenshot() {
        Map<String, Object> response = new HashMap<>();
        try {
            udpCon.receiveAndProcessData();
            String screenshot = udpCon.getLatestScreenshotBase64();
            response.put("screenshot", screenshot);
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        return response;
    }

}
