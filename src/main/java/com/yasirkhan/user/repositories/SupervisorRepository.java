package com.yasirkhan.user.repositories;

import com.yasirkhan.user.models.entities.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, UUID> {
}
