package com.quetoquenana.personservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.model.PersonProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestEntityFactory {

    public static final String DEFAULT_ID_NUMBER = "ID123456";
    public static final String DEFAULT_USER = "testUser";
    public static final String ROLE_ADMIN = "ADMIN";

    public static Person createPerson(
    ) {
        return createPerson(LocalDateTime.now(), DEFAULT_USER);
    }

    public static Person createPerson(
            LocalDateTime createdAt,
            String createdBy
    ) {
        return createPerson(createdAt, createdBy, true);
    }

    public static Person createPerson(
            LocalDateTime createdAt,
            String createdBy,
            Boolean isActive
    ) {
        Person person = Person.builder()
                .name("John")
                .lastname("White")
                .idNumber(DEFAULT_ID_NUMBER)
                .isActive(isActive)
                .build();
        person.setCreatedAt(createdAt);
        person.setCreatedBy(createdBy);
        return person;
    }

    public static String createPersonPayload(ObjectMapper objectMapper, boolean isActive) {
        return createPersonPayload(objectMapper, DEFAULT_ID_NUMBER, isActive);
    }

    public static String createPersonPayload(ObjectMapper objectMapper) {
        return createPersonPayload(objectMapper, DEFAULT_ID_NUMBER, true);
    }

    public static String createPersonPayload(ObjectMapper objectMapper, String idNumber, boolean isActive) {
        Person personPayload = Person.builder()
                .name("John")
                .lastname("Doe")
                .idNumber(idNumber)
                .isActive(isActive)
                .build();
        try {
            return objectMapper.writeValueAsString(personPayload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PersonProfile createPersonProfile(Person person) {
        PersonProfile profile = PersonProfile.builder()
                .person(person)
                .birthday(LocalDate.of(1990, 1, 1))
                .gender("M")
                .nationality("TestNationality")
                .maritalStatus("Single")
                .occupation("Engineer")
                .profilePictureUrl("https://example.com/profile.jpg")
                .build();
        profile.setCreatedAt(LocalDateTime.now());
        profile.setCreatedBy(DEFAULT_USER);
        return profile;
    }

    public static String createPersonProfilePayload(ObjectMapper objectMapper) {
        PersonProfile profilePayload = PersonProfile.builder()
                .birthday(LocalDate.of(1990, 1, 1))
                .gender("M")
                .nationality("TestNationality")
                .maritalStatus("Single")
                .occupation("Engineer")
                .profilePictureUrl("https://example.com/profile.jpg")
                .build();
        try {
            return objectMapper.writeValueAsString(profilePayload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
