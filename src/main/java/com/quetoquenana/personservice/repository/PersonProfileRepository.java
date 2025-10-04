package com.quetoquenana.personservice.repository;

import com.quetoquenana.personservice.model.PersonProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonProfileRepository extends JpaRepository<PersonProfile, UUID> {
}
