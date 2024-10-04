package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    private ClientType type;

    public enum ClientType {
        AMATEUR, REGULAR, PROFESSIONAL
    }
}