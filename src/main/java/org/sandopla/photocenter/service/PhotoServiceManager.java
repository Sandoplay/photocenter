package org.sandopla.photocenter.service;

import org.sandopla.photocenter.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoServiceManager {
    private final ServiceRepository serviceRepository;

    @Autowired
    public PhotoServiceManager(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<org.sandopla.photocenter.model.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public org.sandopla.photocenter.model.Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public org.sandopla.photocenter.model.Service createService(org.sandopla.photocenter.model.Service service) {
        return serviceRepository.save(service);
    }

    public org.sandopla.photocenter.model.Service updateService(Long id,
                                                                org.sandopla.photocenter.model.Service serviceDetails) {
        org.sandopla.photocenter.model.Service service = getServiceById(id);
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setBasePrice(serviceDetails.getBasePrice());
        service.setUrgentPriceMultiplier(serviceDetails.getUrgentPriceMultiplier());
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}