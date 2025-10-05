package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quetoquenana.personservice.exception.DuplicateRecordException;
import com.quetoquenana.personservice.exception.InactiveRecordException;
import com.quetoquenana.personservice.exception.RecordNotFoundException;
import com.quetoquenana.personservice.model.PersonProfile;
import com.quetoquenana.personservice.service.PersonProfileService;
import com.quetoquenana.personservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PersonProfileControllerTest {
    @Mock
    private PersonProfileService personProfileService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PersonProfileController personProfileController;

    private PersonProfile personProfile;
    private UUID personId;
    private UUID profileId;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        personId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        personProfile = PersonProfile.builder()
                .id(profileId)
                .gender("M")
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testAddProfile_PersonNotFound() {
        when(personProfileService.addProfileToPerson(any(), any())).thenThrow(new RecordNotFoundException());
        assertThrows(RecordNotFoundException.class, () -> personProfileController.addProfile(personId, personProfile));
    }

    @Test
    void testAddProfile_PersonInactive() {
        when(personProfileService.addProfileToPerson(any(), any())).thenThrow(new InactiveRecordException());
        assertThrows(InactiveRecordException.class, () -> personProfileController.addProfile(personId, personProfile));
    }

    @Test
    void testAddProfile_ProfileAlreadyExists() {
        when(personProfileService.addProfileToPerson(any(), any())).thenThrow(new DuplicateRecordException());
        assertThrows(DuplicateRecordException.class, () -> personProfileController.addProfile(personId, personProfile));
    }

    @Test
    void testAddProfile_Success() {
        when(personProfileService.addProfileToPerson(any(), any())).thenReturn(personProfile);
        assertDoesNotThrow(() -> personProfileController.addProfile(personId, personProfile));
    }

    @Test
    void testUpdateProfile_ProfileNotFound() {
        when(personProfileService.updateProfile(any(), any())).thenThrow(new RecordNotFoundException());
        assertThrows(RecordNotFoundException.class, () -> personProfileController.updateProfile(profileId, personProfile));
    }

    @Test
    void testUpdateProfile_PersonInactive() {
        when(personProfileService.updateProfile(any(), any())).thenThrow(new InactiveRecordException());
        assertThrows(InactiveRecordException.class, () -> personProfileController.updateProfile(profileId, personProfile));
    }

    @Test
    void testUpdateProfile_Success() {
        when(personProfileService.updateProfile(any(), any())).thenReturn(personProfile);
        assertDoesNotThrow(() -> personProfileController.updateProfile(profileId, personProfile));
    }
}
