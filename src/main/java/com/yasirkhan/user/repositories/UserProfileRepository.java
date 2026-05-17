package com.yasirkhan.user.repositories;

import com.yasirkhan.user.models.entities.UsersProfile;
import org.springframework.data.jpa.repository.EntityGraph;
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
    List<UsersProfile> findAllProfileWithDetails(); // Removed Optional

    @Query(
            "SELECT p FROM UsersProfile p " +
                    "LEFT JOIN FETCH p.driver d " +
                    "LEFT JOIN FETCH p.supervisor s " +
                    "WHERE d.id IS NOT NULL"
    )
    List<UsersProfile> findAllDrivers();

    @Query(
            "SELECT p FROM UsersProfile p " +
                    "LEFT JOIN FETCH p.driver d " +
                    "LEFT JOIN FETCH p.supervisor s " +
                    "WHERE s.id IS NOT NULL"
    )
    List<UsersProfile> findAllSupervisors();

    @Query(
            "SELECT p FROM UsersProfile p " +
                    "LEFT JOIN FETCH p.driver d " +
                    "LEFT JOIN FETCH p.supervisor s " +
                    "WHERE d.id IS NULL AND s.id IS NULL"
    )
    List<UsersProfile> findAllAdmins();
}
