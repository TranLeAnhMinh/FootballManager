package com.example.footballmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.footballmanagement.entity.User;
import com.example.footballmanagement.entity.enums.UserRole;
import com.example.footballmanagement.entity.enums.UserStatus;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone); 
     @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatus(UUID id, UserStatus status);

    List<User> findAllByStatus(UserStatus status);
    @Modifying
@Query("UPDATE User u SET u.role = :role WHERE u.id = :id")
int updateRole(UUID id, UserRole role);

boolean existsByIdAndRole(UUID id, UserRole role);

List<User> findAllByRole(UserRole role);
@Query("""
SELECT u FROM User u
WHERE (:role IS NULL OR u.role = :role)
AND (:status IS NULL OR u.status = :status)
AND (COALESCE(:name, '') = '' OR LOWER(u.fullName) LIKE CONCAT('%', LOWER(:name), '%'))
AND (COALESCE(:email, '') = '' OR LOWER(u.email) LIKE CONCAT('%', LOWER(:email), '%'))
""")
Page<User> searchUsers(
        UserRole role,
        UserStatus status,
        String name,
        String email,
        Pageable pageable
);


}
