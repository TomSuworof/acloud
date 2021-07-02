package com.salat.acloud.repositories;

import com.salat.acloud.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    public Optional<Client> findClientByClientIdAndAndClientNameAndClientSecret(Long clientId, String clientName, String clientSecret);
}
