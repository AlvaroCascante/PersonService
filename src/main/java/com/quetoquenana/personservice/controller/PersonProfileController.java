package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.model.PersonProfile;
import com.quetoquenana.personservice.service.PersonProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
public class PersonProfileController {

    private final PersonProfileService personProfileService;

    @PostMapping("/{idPerson}/profile")
    @JsonView(Person.PersonDetail.class)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')") // ADMIN, AUDITOR and USER roles can access
    public ResponseEntity<PersonProfile> addProfile(
            @PathVariable UUID idPerson,
            @RequestBody PersonProfile personProfile) {
        log.info("POST /api/persons/{}/profile called with payload: {}", idPerson, personProfile);
        PersonProfile createdProfile = personProfileService.addProfileToPerson(idPerson, personProfile);
        return ResponseEntity.ok(createdProfile);
    }

    @PutMapping("/profile/{idPersonProfile}")
    @JsonView(Person.PersonDetail.class)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')") // ADMIN, AUDITOR and USER roles can access
    public ResponseEntity<PersonProfile> updateProfile(
            @PathVariable UUID idPersonProfile,
            @RequestBody PersonProfile personProfile) {
        log.info("PUT /api/persons/profile/{} called with payload: {}", idPersonProfile, personProfile);
        PersonProfile updatedProfile = personProfileService.updateProfile(idPersonProfile, personProfile);
        return ResponseEntity.ok(updatedProfile);
    }
}
