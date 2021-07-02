package com.salat.acloud.services;

import com.salat.acloud.entities.Client;
import com.salat.acloud.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public boolean isClientValid(Long clientId, String clientName, String clientSecret) {
        Optional<Client> client = clientRepository.findClientByClientIdAndAndClientNameAndClientSecret(clientId, clientName, clientSecret);
        return client.isPresent();
    }
}
