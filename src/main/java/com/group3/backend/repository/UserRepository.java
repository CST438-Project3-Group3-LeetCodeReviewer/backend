package com.group3.backend.repository;

import com.group3.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing User entities.
 * Provides standard JPA operations for user profile and account data.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    
}
