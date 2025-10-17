package com.example.demo.repository;

import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"role", "role.permissions"})
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    @EntityGraph(attributePaths = {"role", "role.permissions"})
    Optional<UserEntity> findByUserId(UUID userId);

    Optional<UserEntity> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
