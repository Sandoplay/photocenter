package org.sandopla.photocenter.controller;

import lombok.Data;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.service.BranchService;
import org.sandopla.photocenter.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('OWNER')")
public class AdminApiController {

    private final ClientService clientService;
    private final BranchService branchService;

    @Autowired
    public AdminApiController(ClientService clientService, BranchService branchService) {
        this.clientService = clientService;
        this.branchService = branchService;
    }

    @PostMapping
    public ResponseEntity<Client> createAdmin(@RequestBody AdminCreateRequest request) {
        Branch branch = branchService.getBranchById(request.getBranch().getId());

        Client admin = new Client();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());

        Client createdAdmin = clientService.createAdmin(admin, branch);
        return ResponseEntity.ok(createdAdmin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateAdmin(@PathVariable Long id, @RequestBody AdminUpdateRequest request) {
        Branch branch = branchService.getBranchById(request.getBranch().getId());

        Client admin = clientService.getClientById(id);
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setUsername(request.getUsername());
        admin.setBranch(branch);

        Client updatedAdmin = clientService.updateAdmin(admin);
        return ResponseEntity.ok(updatedAdmin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAdmin(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
}

@Data
class AdminCreateRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private Branch branch;
}

@Data
class AdminUpdateRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String username;
    private Branch branch;
}
