package com.quetoquenana.personservice.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping
    @JsonView(Person.PersonList.class)
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/page")
    @JsonView(Person.PersonList.class)
    public Page<Person> getPersonsPage(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return personService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @JsonView(Person.PersonDetail.class)
    public ResponseEntity<Person> getPersonById(@PathVariable UUID id) {
        Optional<Person> person = personService.findById(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @JsonView(Person.PersonDetail.class)
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person created = personService.save(person);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @JsonView(Person.PersonDetail.class)
    public ResponseEntity<Person> updatePerson(@PathVariable UUID id, @RequestBody Person person) {
        if (personService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        person.setId(id);
        Person updated = personService.save(person);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        if (personService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

