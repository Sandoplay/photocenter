package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.branch WHERE c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.branch WHERE c.id = :id")
    Optional<Client> findById(@Param("id") Long id);

    List<Client> findByRoleAndBranch(Role role, Branch branch);
}