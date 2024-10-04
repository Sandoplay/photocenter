package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Manufacturer;
import org.sandopla.photocenter.repository.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ManufacturerService(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Manufacturer getManufacturerById(Long id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found with id: " + id));
    }

    public Manufacturer createManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    public Manufacturer updateManufacturer(Long id, Manufacturer manufacturerDetails) {
        Manufacturer manufacturer = getManufacturerById(id);
        manufacturer.setName(manufacturerDetails.getName());
        manufacturer.setCountry(manufacturerDetails.getCountry());
        manufacturer.setContactInfo(manufacturerDetails.getContactInfo());
        return manufacturerRepository.save(manufacturer);
    }

    public void deleteManufacturer(Long id) {
        manufacturerRepository.deleteById(id);
    }
}
