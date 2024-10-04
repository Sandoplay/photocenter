package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String country;

    @Column(name = "contact_info")
    private String contactInfo;
}
