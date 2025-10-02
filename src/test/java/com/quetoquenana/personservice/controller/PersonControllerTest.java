package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quetoquenana.personservice.model.ApiResponse;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PersonControllerTest {
    @Mock
    private PersonService personService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PersonController personController;

    private Person person;
    private UUID personId;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
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

        Field field = PersonController.class.getDeclaredField("messageSource");
        field.setAccessible(true);
        field.set(personController, messageSource);
        // Mock getMessage for error.not.found
        when(messageSource.getMessage(eq("error.not.found"), any(), any())).thenReturn("Resource not found.");
    }

    @Test
    void testGetAllPersons_ReturnsList() throws Exception {
        List<Person> persons = Collections.singletonList(person);
        when(personService.findAll()).thenReturn(persons);
        ResponseEntity<ApiResponse> response = personController.getAllPersons();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(0, apiResponse.getErrorCode());
        List<?> data = (List<?>) apiResponse.getData();
        assertEquals(1, data.size());
        String json = objectMapper.writerWithView(Person.PersonList.class).writeValueAsString(data);
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
        ResponseEntity<ApiResponse> response = personController.getPersonById(personId, Locale.getDefault());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(0, apiResponse.getErrorCode());
        Person data = (Person) apiResponse.getData();
        assertEquals(person, data);
        String json = objectMapper.writerWithView(Person.PersonDetail.class).writeValueAsString(data);
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
        ResponseEntity<ApiResponse> response = personController.getPersonById(personId, Locale.getDefault());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Resource not found.", apiResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getErrorCode());
        assertNull(apiResponse.getData());
    }

    @Test
    void testGetPersonsPage_ReturnsPage() throws Exception {
        Page<Person> page = new PageImpl<>(Collections.singletonList(person), PageRequest.of(0, 10), 1);
        when(personService.findAll(any())).thenReturn(page);
        ResponseEntity<ApiResponse> response = personController.getPersonsPage(0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(0, apiResponse.getErrorCode());
        Page<?> data = (Page<?>) apiResponse.getData();
        assertEquals(1, data.getTotalElements());
        String json = objectMapper.writerWithView(Person.PersonList.class).writeValueAsString(((Page<?>) apiResponse.getData()).getContent());
        assertTrue(json.contains("id"));
        assertTrue(json.contains("name"));
        assertTrue(json.contains("lastname"));
    }

    @Test
    void testCreatePerson_ReturnsCreated() {
        when(personService.save(any(Person.class))).thenReturn(person);
        when(messageSource.getMessage(eq("person.created"), any(), any())).thenReturn("Person created successfully");
        ResponseEntity<ApiResponse> response = personController.createPerson(person, Locale.getDefault());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(0, apiResponse.getErrorCode());
        Person data = (Person) apiResponse.getData();
        assertEquals(person, data);
    }

    @Test
    void testUpdatePerson_ReturnsUpdated() {
        when(personService.findById(personId)).thenReturn(Optional.of(person));
        when(personService.save(any(Person.class))).thenReturn(person);
        when(messageSource.getMessage(eq("person.updated"), any(), any())).thenReturn("Person updated successfully");
        ResponseEntity<ApiResponse> response = personController.updatePerson(personId, person, Locale.getDefault());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(0, apiResponse.getErrorCode());
        Person data = (Person) apiResponse.getData();
        assertEquals(person, data);
    }

    @Test
    void testUpdatePerson_NotFound() {
        when(personService.findById(personId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("person.not.found"), any(), any())).thenReturn("Person not found");
        ResponseEntity<ApiResponse> response = personController.updatePerson(personId, person, Locale.getDefault());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Person not found", apiResponse.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), apiResponse.getErrorCode());
        assertNull(apiResponse.getData());
    }
}
