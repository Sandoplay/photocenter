package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Service;
import org.sandopla.photocenter.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final PhotoService photoService;

    @Autowired
    public ServiceController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public List<Service> getAllServices() {
        return photoService.getAllServices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Service service = photoService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service newService = photoService.createService(service);
        return ResponseEntity.ok(newService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
        Service updatedService = photoService.updateService(id, serviceDetails);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        photoService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}
