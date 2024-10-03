package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByType(Branch.BranchType type);
}