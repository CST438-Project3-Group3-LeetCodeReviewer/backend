package com.group3.backend.repository;

import com.group3.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * Repository interface for accessing and managing User entities.
 * Provides standard JPA operations for user profile and account data.
 */

public interface UserRepository extends JpaRepository<User, UUID> {
    
}
