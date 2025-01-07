package org.server.devices;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    private final HashMap<String, Device> deviceIPMap;
    private final TreeMap<Long, Device> deviceIDMap;

    @Autowired
    public DeviceMapper() {
        deviceIPMap = new HashMap<>();
        deviceIDMap = new TreeMap<>();
    }

    @PostConstruct
    public void init() {
        loadDevicesFromJson();
    }

    private void loadDevicesFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/devices.json")) {
            List<Camera> devices = objectMapper.readValue(inputStream, new TypeReference<List<Camera>>() {});
            for (Camera device : devices) {
                addDeviceByIP(device.AssociatedIP, device);
            }
            System.out.println("Devices successfully loaded: " + deviceIPMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load devices from JSON", e);
        }
    }

    public void addDeviceByIP(String ipv4, Device device) {
        deviceIPMap.put(ipv4, device);
        try{
            Long highestKey = deviceIDMap.lastKey();
            deviceIDMap.put(highestKey+1, device);
            device.id = highestKey+1;
        }
        catch(NoSuchElementException e)
        {
            deviceIDMap.put(0L, device);
        }
    }

    public List<Device> getAllDevices() {
        return List.copyOf(deviceIDMap.values());
    }


    public Device getDeviceByIP(String ipv4) {
        return deviceIPMap.get(ipv4);
    }

    public Device getDeviceByID(Long id) {
        return deviceIDMap.get(id);
    }

}
