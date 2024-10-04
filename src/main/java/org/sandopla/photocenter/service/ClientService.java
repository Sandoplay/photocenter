package org.sandopla.photocenter.service;

import jakarta.annotation.PostConstruct;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    public void initTestData() {
        if (clientRepository.count() == 0) {
            Client client = new Client();
            client.setName("Test Client");
            client.setPhoneNumber("1234567890");
            client.setEmail("test@example.com");
            client.setType(Client.ClientType.AMATEUR); // Змінено на AMATEUR
            clientRepository.save(client);
        }
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);
        client.setName(clientDetails.getName());
        client.setPhoneNumber(clientDetails.getPhoneNumber());
        client.setEmail(clientDetails.getEmail());
        client.setType(clientDetails.getType());
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
