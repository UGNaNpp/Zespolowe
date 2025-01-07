package org.server.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
