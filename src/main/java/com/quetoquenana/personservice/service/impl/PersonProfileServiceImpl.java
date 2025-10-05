package com.quetoquenana.personservice.service.impl;

import com.quetoquenana.personservice.exception.DuplicateRecordException;
import com.quetoquenana.personservice.exception.InactiveRecordException;
import com.quetoquenana.personservice.exception.RecordNotFoundException;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.model.PersonProfile;
import com.quetoquenana.personservice.repository.PersonProfileRepository;
import com.quetoquenana.personservice.repository.PersonRepository;
import com.quetoquenana.personservice.service.PersonProfileService;
import com.quetoquenana.personservice.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PersonProfileServiceImpl implements PersonProfileService {

    private final PersonRepository personRepository;
    private final PersonProfileRepository personProfileRepository;
    private final UserService userService;

    public PersonProfileServiceImpl(PersonRepository personRepository, PersonProfileRepository personProfileRepository, UserService userService) {
        this.personRepository = personRepository;
        this.personProfileRepository = personProfileRepository;
        this.userService = userService;
    }

    @Override
    public PersonProfile addProfileToPerson(UUID idPerson, PersonProfile personProfile) {
        Person person = personRepository.findById(idPerson)
            .map(it -> {
                if (!it.isActive()) {
                    throw new InactiveRecordException("person.inactive");
                }
                if (it.getPersonProfile() != null) {
                    throw new DuplicateRecordException("person.profile.already.exists");
                }
                return it;
            })
            .orElseThrow(() -> new RecordNotFoundException("Person not found"));
        personProfile.setPerson(person);
        personProfile.setCreatedAt(LocalDateTime.now());
        personProfile.setCreatedBy(userService.getCurrentUsername());
        return personProfileRepository.save(personProfile);
    }

    @Override
    public PersonProfile updateProfile(UUID idProfile, PersonProfile personProfile) {
        PersonProfile existingProfile = personProfileRepository.findById(idProfile)
            .map(it -> {
                if (!it.getPerson().isActive()) {
                    throw new InactiveRecordException("person.inactive");
                }
                return it;
            })
            .orElseThrow(() -> new RecordNotFoundException("Profile not found"));
        existingProfile.setBirthday(personProfile.getBirthday());
        existingProfile.setGender(personProfile.getGender());
        existingProfile.setNationality(personProfile.getNationality());
        existingProfile.setMaritalStatus(personProfile.getMaritalStatus());
        existingProfile.setOccupation(personProfile.getOccupation());
        existingProfile.setProfilePictureUrl(personProfile.getProfilePictureUrl());
        existingProfile.setUpdatedAt(LocalDateTime.now());
        existingProfile.setUpdatedBy(userService.getCurrentUsername());
        return personProfileRepository.save(existingProfile);
    }
}
