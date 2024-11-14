//package org.sandopla.photocenter.service;
//
//import org.sandopla.photocenter.model.Delivery;
//import org.sandopla.photocenter.repository.DeliveryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class DeliveryService {
//
//    private final DeliveryRepository deliveryRepository;
//
//    @Autowired
//    public DeliveryService(DeliveryRepository deliveryRepository) {
//        this.deliveryRepository = deliveryRepository;
//    }
//
//    public List<Delivery> getAllDeliveries() {
//        return deliveryRepository.findAll();
//    }
//
//    public Delivery getDeliveryById(Long id) {
//        return deliveryRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
//    }
//
//    public Delivery createDelivery(Delivery delivery) {
//        return deliveryRepository.save(delivery);
//    }
//
//    public Delivery updateDelivery(Long id, Delivery deliveryDetails) {
//        Delivery delivery = getDeliveryById(id);
//        delivery.setSupplier(deliveryDetails.getSupplier());
//        delivery.setOrderDate(deliveryDetails.getOrderDate());
//        delivery.setDeliveryDate(deliveryDetails.getDeliveryDate());
//        delivery.setStatus(deliveryDetails.getStatus());
//        return deliveryRepository.save(delivery);
//    }
//
//    public void deleteDelivery(Long id) {
//        deliveryRepository.deleteById(id);
//    }
//}
