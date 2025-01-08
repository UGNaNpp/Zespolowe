package org.server.devices;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    private HashMap<String, Device> deviceIPMap;
    private TreeMap<Long, Device> deviceIDMap;


    public DeviceMapper() {
        deviceIPMap = new HashMap<>();
        deviceIDMap = new TreeMap<>();
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

    public Device getDeviceByIP(String ipv4) {
        return deviceIPMap.get(ipv4);
    }

    public Device getDeviceByID(Long id) {
        return deviceIDMap.get(id);
    }
}
