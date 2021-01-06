package com.salat.acloud.repositories;

import com.salat.acloud.entities.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
}
