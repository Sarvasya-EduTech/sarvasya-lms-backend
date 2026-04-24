package com.sarvasya.sarvasya_lms_backend.repository.user;

import com.sarvasya.sarvasya_lms_backend.model.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.common.Role;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    List<User> findByClassId(UUID classId);
}








