package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Тут можна додати специфічні методи запитів, якщо потрібно
}
