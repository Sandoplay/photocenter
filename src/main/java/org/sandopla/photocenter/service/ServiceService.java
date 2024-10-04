package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Service;
import org.sandopla.photocenter.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    public Service updateService(Long id, Service serviceDetails) {
        Service service = getServiceById(id);
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
