package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonControllerTest {
    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private Person person;
    private UUID personId;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        personId = UUID.randomUUID();
        person = Person.builder()
                .id(personId)
                .name("John")
                .lastname("Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .gender("male")
                .idNumber("ID123456")
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetAllPersons_ReturnsList() throws Exception {
        List<Person> persons = Collections.singletonList(person);
        when(personService.findAll()).thenReturn(persons);
        List<Person> result = personController.getAllPersons();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(person, result.getFirst());
        String json = objectMapper.writerWithView(Person.PersonList.class).writeValueAsString(result);
        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("lastname"));
        assertTrue(json.contains("idNumber"));
        assertFalse(json.contains("birthday"));
        assertFalse(json.contains("gender"));
    }

    @Test
    void testGetPersonById_Found() throws Exception {
        when(personService.findById(personId)).thenReturn(Optional.of(person));
        ResponseEntity<Person> response = personController.getPersonById(personId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(person, response.getBody());
        String json = objectMapper.writerWithView(Person.PersonDetail.class).writeValueAsString(response.getBody());
        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("lastname"));
        assertTrue(json.contains("birthday"));
        assertTrue(json.contains("gender"));
        assertTrue(json.contains("idNumber"));
    }

    @Test
    void testGetPersonById_NotFound() {
        when(personService.findById(personId)).thenReturn(Optional.empty());
        ResponseEntity<Person> response = personController.getPersonById(personId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetPersonsPage_ReturnsPage() throws Exception {
        Page<Person> page = new PageImpl<>(Collections.singletonList(person), PageRequest.of(0, 10), 1);
        when(personService.findAll(any())).thenReturn(page);
        Page<Person> result = personController.getPersonsPage(0, 10);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        String json = objectMapper.writerWithView(Person.PersonList.class).writeValueAsString(result.getContent());
        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("lastname"));
    }

    @Test
    void testCreatePerson_ReturnsCreated() {
        when(personService.save(any(Person.class))).thenReturn(person);
        ResponseEntity<Person> response = personController.createPerson(person);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(person, response.getBody());
    }
}
