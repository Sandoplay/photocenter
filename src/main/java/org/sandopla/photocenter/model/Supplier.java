package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.util.HashSet;

@Data
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String email;

    private String address;

    // Додаємо спеціалізації постачальника
    @ElementCollection(targetClass = ProductCategory.class)
    @CollectionTable(
            name = "supplier_specializations",
            joinColumns = @JoinColumn(name = "supplier_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ProductCategory> specializations = new HashSet<>();

    @Column(name = "is_active")
    private boolean active = true;

    // Додаткові поля для аналітики
    @Column(name = "rating")
    private Integer rating; // Рейтинг постачальника (1-5)

    @Column(name = "notes")
    private String notes; // Додаткові примітки
}