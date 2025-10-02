package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.quetoquenana.personservice.model.ApiResponse;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.service.PersonService;
import com.quetoquenana.personservice.util.JsonViewPageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
public class PersonController {
    private final PersonService personService;
    private final MessageSource messageSource;

    @GetMapping
    @JsonView(Person.PersonList.class)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getAllPersons() {
        log.info("GET /api/persons called");
        List<Person> entities = personService.findAll();
        return ResponseEntity.ok(new ApiResponse(entities));
    }

    @GetMapping("/page")
    @JsonView(Person.PersonList.class)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getPersonsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/persons/page called with page={}, size={}", page, size);
        Page<Person> entities = personService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(new ApiResponse(new JsonViewPageUtil<>(entities, entities.getPageable())));
    }

    @GetMapping("/{id}")
    @JsonView(Person.PersonDetail.class)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getPersonById(@PathVariable UUID id, Locale locale) {
        log.info("GET /api/persons/{} called", id);
        return personService.findById(id)
                .map(entity -> ResponseEntity.ok(new ApiResponse(entity)))
                .orElseGet(() -> {
                    log.error("Person with id {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(messageSource.getMessage("error.not.found", null, locale), HttpStatus.NOT_FOUND.value()));
                });
    }

    @GetMapping("/idNumber/{idNumber}")
    @JsonView(Person.PersonDetail.class)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getPersonByIdNumber(@PathVariable String idNumber, Locale locale) {
        log.info("GET /api/persons/idNumber/{} called", idNumber);
        return personService.findByIdNumber(idNumber)
                .map(entity -> ResponseEntity.ok(new ApiResponse(entity)))
                .orElseGet(() -> {
                    log.error("Person with idNumber {} not found", idNumber);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(messageSource.getMessage("error.not.found", null, locale), HttpStatus.NOT_FOUND.value()));
                });
    }

    @PostMapping
    @JsonView(Person.PersonDetail.class)
    public ResponseEntity<ApiResponse> createPerson(@RequestBody Person person, Locale locale) {
        log.info("POST /api/persons called with payload: {}", person);
        Person entity = personService.save(person);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(entity));
    }

    @PutMapping("/{id}")
    @JsonView(Person.PersonDetail.class)
    public ResponseEntity<ApiResponse> updatePerson(@PathVariable UUID id, @RequestBody Person person, Locale locale) {
        log.info("PUT /api/persons/{} called", id);
        if (personService.findById(id).isEmpty()) {
            String message = messageSource.getMessage("person.not.found", null, locale);
            log.error("Person with id {} not found for update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(message, HttpStatus.NOT_FOUND.value()));
        }
        person.setId(id);
        Person updated = personService.save(person);
        return ResponseEntity.ok(new ApiResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        log.info("DELETE /api/persons/{} called", id);
        if (personService.findById(id).isEmpty()) {
            log.error("Person with id {} not found for delete", id);
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
