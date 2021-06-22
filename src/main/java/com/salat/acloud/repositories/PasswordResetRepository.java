package com.salat.acloud.repositories;

import com.salat.acloud.entities.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetRequest, String> {

    Optional<PasswordResetRequest> findById(String id);
}