package org.server.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
class DeviceController {
    private final DeviceMapper mapper;

    @Autowired
    DeviceController(DeviceMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/")
    ResponseEntity<Map<Long, Device>> getDevices() {
        return ResponseEntity.ok(mapper.getAllDevices());
    }

    @GetMapping("/ip/{ipv4}")
    ResponseEntity<Device> getDeviceByIP(@PathVariable("ipv4") String ipv4) {
        Device device = mapper.getDeviceByIP(ipv4);
        return device!=null? ResponseEntity.ok(device): ResponseEntity.notFound().build();
    }

    @GetMapping("/id/{id}")
    ResponseEntity<Device> getDeviceByIP(@PathVariable("id") long id) {
        Device device = mapper.getDeviceByID(id);
        return device!=null? ResponseEntity.ok(device): ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    ResponseEntity<Device> addCamera(@RequestBody Camera device) {
        mapper.addDeviceByIP(device.AssociatedIP, device);
        return ResponseEntity.created(null).body(device);
    }

    @PutMapping("/edit/id/{id}")
    ResponseEntity<Device> editCamera(@PathVariable("id") long id, @RequestBody Camera camera) {
        Device existingDevice = mapper.getDeviceByID(id);
        if (existingDevice == null) {
            return ResponseEntity.notFound().build();
        }
        mapper.updateCameraById(id, camera);
        return ResponseEntity.ok(camera);
    }

    @DeleteMapping("/id/{id}")
    ResponseEntity<Void> deleteCamera(@PathVariable("id") long id) {
        Device existingDevice = mapper.getDeviceByID(id);
        if (existingDevice == null) {
            return ResponseEntity.notFound().build();
        }
        mapper.deleteCameraById(id);
        return ResponseEntity.noContent().build();
    }
}
