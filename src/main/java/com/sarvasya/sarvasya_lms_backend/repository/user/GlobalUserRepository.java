package com.sarvasya.sarvasya_lms_backend.repository.user;

import com.sarvasya.sarvasya_lms_backend.model.user.GlobalUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalUserRepository extends JpaRepository<GlobalUser, UUID> {
    Optional<GlobalUser> findByEmail(String email);
}








