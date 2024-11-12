package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Role;
import org.sandopla.photocenter.repository.ClientRepository;
import org.sandopla.photocenter.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientRepository clientRepository,
                         BranchRepository branchRepository,
                         PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with username: " + username));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public List<Client> getAllAdmins() {
        return clientRepository.findByRole(Role.ADMIN);
    }

    @Transactional
    public Client updateAdmin(Client admin) {
        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Can only update administrators");
        }
        return clientRepository.save(admin);
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public Client createClient(Client client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);
        client.setName(clientDetails.getName());
        client.setPhoneNumber(clientDetails.getPhoneNumber());
        client.setEmail(clientDetails.getEmail());
        client.setType(clientDetails.getType());
        // Не оновлюємо пароль тут для безпеки
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Transactional
    public Client registerNewClient(Client client) {
        if (clientRepository.findByUsername(client.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));

        // Встановлення ролі за замовчуванням, якщо вона не встановлена
        if (client.getRole() == null || client.getAuthorities().isEmpty()) {
            client.setRole(Role.USER); // Змінено з "USER" на Role.USER
        }

        return clientRepository.save(client);
    }

    @Transactional
    public Client createAdmin(Client client, Branch branch) {
        if (clientRepository.findByUsername(client.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Role.ADMIN);
        client.setBranch(branch);

        return clientRepository.save(client);
    }

    @Transactional
    public List<Client> getAdminsByBranch(Branch branch) {
        return clientRepository.findByRoleAndBranch(Role.ADMIN, branch);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void assignAdminToBranch(Long adminId, Long branchId) {
        Client admin = getClientById(adminId);
        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Client is not an admin");
        }
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        admin.setBranch(branch);
        clientRepository.save(admin);
    }
}
