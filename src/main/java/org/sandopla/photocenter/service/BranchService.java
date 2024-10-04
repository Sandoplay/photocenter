package org.sandopla.photocenter.service;

import jakarta.annotation.PostConstruct;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @PostConstruct
    public void initTestData() {
        if (branchRepository.count() == 0) {
            Branch branch = new Branch();
            branch.setName("Main Office");
            branch.setAddress("123 Main St");
            branch.setWorkplaceCount(10);
            branch.setType(Branch.BranchType.MAIN_OFFICE);
            branchRepository.save(branch);
        }
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Branch updateBranch(Long id, Branch branchDetails) {
        Branch branch = getBranchById(id);
        branch.setName(branchDetails.getName());
        branch.setAddress(branchDetails.getAddress());
        branch.setWorkplaceCount(branchDetails.getWorkplaceCount());
        branch.setType(branchDetails.getType());
        branch.setParentBranch(branchDetails.getParentBranch());
        return branchRepository.save(branch);
    }

    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }
}
