package com.yasirkhan.user.repositories;

import com.yasirkhan.user.models.entities.UsersProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UsersProfile, UUID> {

    @Query(
            "SELECT p FROM UsersProfile p " +
            "LEFT JOIN FETCH p.driver " +
            "LEFT JOIN FETCH p.supervisor " +
            "WHERE p.id = :id"
    )
    Optional<UsersProfile> findProfileWithDetails(UUID id);

    @Query(
            "SELECT p FROM UsersProfile p " +
            "LEFT JOIN FETCH p.driver " +
            "LEFT JOIN FETCH p.supervisor "
    )
    Optional<List<UsersProfile>> findAllProfileWithDetails();
}
