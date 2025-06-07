package org.server.devices;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.server.StreamProvider;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    private final HashMap<String, Device> deviceIPMap;
    private final TreeMap<Long, Device> deviceIDMap;

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Value("${filepath.devices}")
    private String devicesConfigFilepath;
    @Autowired
    private StreamProvider streamProvider;

    //    @Autowired
    public DeviceMapper() {
        deviceIPMap = new HashMap<>();
        deviceIDMap = new TreeMap<>();
    }

    @PostConstruct
    public void init() {
//        loadDevicesFromJson();
//        addMockedCameras();
    }

    public void loadDevicesFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new FileInputStream(devicesConfigFilepath)) {
            List<Camera> devices = objectMapper.readValue(inputStream, new TypeReference<List<Camera>>() {});
            for (Camera device : devices) {
                autowireCapableBeanFactory.autowireBean(device);
                addDeviceByIP(device.AssociatedIP, device);
            }
            System.out.println("Devices successfully loaded: " + deviceIPMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load devices from JSON", e);
        }
    }

    public void addMockedCameras() {
        MockCamera camera = new MockCamera(15L, "src/main/resources/video/tiktok.mp4", "192.0.0.4", "00:00:00:00:00" +
                ":00");
        autowireCapableBeanFactory.autowireBean(camera);
        addDeviceByIP(camera.AssociatedIP, camera);
        camera.startMockStreaming(30);
    }

    public void addDeviceByIP(String ipv4, Device device) {
        deviceIPMap.put(ipv4, device);
        try{
            Long highestKey = deviceIDMap.lastKey();
            deviceIDMap.put(highestKey+1, device);
            device.id = highestKey+1;
            //saveDevicesToJSON();
        }
        catch(NoSuchElementException e)
        {
            deviceIDMap.put(0L, device);
        }

        if (device instanceof Camera camera && camera.isRecordingMode()) {
            System.out.println("Camera is recording");
            this.streamProvider.getLastFrame(device.id);
        }

    }



    public Map<Long, Device> getAllDevices() {
        return this.deviceIDMap;
    }

    public Long[] getAllCamerasIDs() {return deviceIDMap.entrySet().stream()
            .filter(entry -> entry.getValue().whatAmI() == 1)
            .map(Map.Entry::getKey)
            .toArray(Long[]::new);}


    public Device getDeviceByIP(String ipv4) {
        return deviceIPMap.get(ipv4);
    }

    public Device getDeviceByID(Long id) {
        return deviceIDMap.get(id);
    }

    public void updateCameraById(Long id, Camera camera) {
        deviceIDMap.put(id, camera);
        saveDevicesToJSON();
    }

    public void deleteCameraById(Long id) {
        deviceIDMap.remove(id);
        saveDevicesToJSON();
    }

    private void saveDevicesToJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        String absolutePath = new File("src/main/resources/devices.json").getAbsolutePath();
        try {
            List<Device> values = List.copyOf(this.deviceIDMap.values());
            File file = new File(absolutePath);
            objectMapper.writeValue(file, values);

            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
