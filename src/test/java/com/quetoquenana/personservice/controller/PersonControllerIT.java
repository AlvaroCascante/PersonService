package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.repository.PersonRepository;
import com.quetoquenana.personservice.util.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static com.quetoquenana.personservice.util.TestEntityFactory.DEFAULT_USER;
import static com.quetoquenana.personservice.util.TestEntityFactory.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(SpringExtension.class)
class PersonControllerIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/persons";

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER, roles = {ROLE_ADMIN})
    void testCreatePerson_SetsAuditableFields() throws Exception {
        String json = TestEntityFactory.createPersonPayload(objectMapper);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        Person savedPerson = personRepository.findByIdNumber(TestEntityFactory.DEFAULT_ID_NUMBER).orElse(null);
        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getCreatedBy()).isEqualTo("integrationUser");
        assertThat(savedPerson.getCreatedAt()).isNotNull();
        assertThat(savedPerson.isActive()).isTrue();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER, roles = {ROLE_ADMIN})
    void testCreatePerson_ReactivatesInactiveRecord() throws Exception {
        // Create inactive person
        Person inactivePerson = TestEntityFactory.createPerson(
                LocalDateTime.now().minusDays(2),
                "oldUser",
                false
        );
        personRepository.save(inactivePerson);
        personRepository.flush();

        // Try to create again with same idNumber
        String json = TestEntityFactory.createPersonPayload(objectMapper);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        Person reactivatedPerson = personRepository.findByIdNumber(TestEntityFactory.DEFAULT_ID_NUMBER).orElse(null);
        assertThat(reactivatedPerson).isNotNull();
        assertThat(reactivatedPerson.isActive()).isTrue();
        assertThat(reactivatedPerson.getUpdatedBy()).isEqualTo("integrationUser");
        assertThat(reactivatedPerson.getUpdatedAt()).isNotNull();
        assertThat(reactivatedPerson.getCreatedBy()).isEqualTo("oldUser");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER, roles = {ROLE_ADMIN})
    void testUpdatePerson_SetsAuditableFields() throws Exception {
        Person person = TestEntityFactory.createPerson(LocalDateTime.now().minusDays(1), "creator");
        personRepository.save(person);
        personRepository.flush();
        person = personRepository.findByIdNumber(TestEntityFactory.DEFAULT_ID_NUMBER).orElseThrow();

        String json = TestEntityFactory.createPersonPayload(objectMapper);

        mockMvc.perform(put(BASE_URL + "/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        Person updatedPerson = personRepository.findById(person.getId()).orElse(null);
        assertThat(updatedPerson).isNotNull();
        assertThat(updatedPerson.isActive()).isTrue();
    }

    @Test
    @WithMockUser(username = DEFAULT_USER, roles = {ROLE_ADMIN})
    void testUpdatePerson_NoUpdateStatus() throws Exception {
        Person person = TestEntityFactory.createPerson(LocalDateTime.now().minusDays(1), "creator");
        personRepository.save(person);
        personRepository.flush();
        person = personRepository.findByIdNumber(TestEntityFactory.DEFAULT_ID_NUMBER).orElseThrow();

        String json = TestEntityFactory.createPersonPayload(objectMapper, false);

        mockMvc.perform(put(BASE_URL + "/" + person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Person updatedPerson = personRepository.findById(person.getId()).orElse(null);
        assertThat(updatedPerson).isNotNull();
        assertThat(updatedPerson.getUpdatedBy()).isEqualTo(DEFAULT_USER);
        assertThat(updatedPerson.getUpdatedAt()).isNotNull();
        assertThat(updatedPerson.getCreatedBy()).isEqualTo("creator");
    }

    @Test
    @WithMockUser(username = DEFAULT_USER, roles = {ROLE_ADMIN})
    void testDeletePerson_SetsAuditableFields() throws Exception {
        Person person = TestEntityFactory.createPerson(LocalDateTime.now().minusDays(1), "creator");
        personRepository.save(person);
        personRepository.flush();
        person = personRepository.findByIdNumber(TestEntityFactory.DEFAULT_ID_NUMBER).orElseThrow();

        mockMvc.perform(delete(BASE_URL + "/" + person.getId()))
                .andExpect(status().isNoContent());

        Person deletedPerson = personRepository.findById(person.getId()).orElse(null);
        assertThat(deletedPerson).isNotNull();
        assertThat(deletedPerson.isActive()).isFalse();
        assertThat(deletedPerson.getUpdatedBy()).isEqualTo("integrationUser");
        assertThat(deletedPerson.getUpdatedAt()).isNotNull();
        assertThat(deletedPerson.getCreatedBy()).isEqualTo("creator");
    }
}
