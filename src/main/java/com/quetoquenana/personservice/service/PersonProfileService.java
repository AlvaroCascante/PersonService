package com.quetoquenana.personservice.service;

import com.quetoquenana.personservice.model.PersonProfile;

import java.util.UUID;

public interface PersonProfileService {
    PersonProfile addProfileToPerson(UUID idPerson, PersonProfile personProfile);
    PersonProfile updateProfile(UUID idProfile, PersonProfile personProfile);
}

