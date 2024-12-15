package org.server.devices;

import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
    private HashMap<String, Device> deviceMap;

    public DeviceMapper() {
        deviceMap = new HashMap<>();
    }

    public void addDevice(String ipv4, Device device) {
        deviceMap.put(ipv4, device);
    }

    public Device getDevice(String ipv4) {
        return deviceMap.get(ipv4);
    }
}
