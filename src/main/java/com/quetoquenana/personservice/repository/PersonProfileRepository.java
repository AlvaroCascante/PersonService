package com.quetoquenana.personservice.repository;

import com.quetoquenana.personservice.model.PersonProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonProfileRepository extends JpaRepository<PersonProfile, UUID> {
    Optional<PersonProfile> findByPersonId(UUID personId);
}
