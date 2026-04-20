package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Boolean existsByEmail(String email);
    long countByRole(com.sarvasya.sarvasya_lms_backend.model.Role role);
    java.util.List<User> findByClassId(UUID classId);
}
