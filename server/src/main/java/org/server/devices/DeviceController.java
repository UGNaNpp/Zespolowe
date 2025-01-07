package org.server.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
class DeviceController {
    private final DeviceMapper mapper;

    @Autowired
    DeviceController(DeviceMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/")
    ResponseEntity<List<Device>> getDevices() {
        return ResponseEntity.ok(mapper.getAllDevices());
    }

    @GetMapping("/{ipv4}")
    ResponseEntity<Device> getDeviceByIP(@PathVariable("ipv4") String ipv4) {
        Device device = mapper.getDeviceByIP(ipv4);
        return device!=null? ResponseEntity.ok(device): ResponseEntity.notFound().build();
    }
}
