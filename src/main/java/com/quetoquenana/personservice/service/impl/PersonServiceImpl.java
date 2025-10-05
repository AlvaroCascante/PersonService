package com.quetoquenana.personservice.service.impl;

import com.quetoquenana.personservice.exception.DuplicateRecordException;
import com.quetoquenana.personservice.exception.ImmutableFieldModificationException;
import com.quetoquenana.personservice.exception.RecordNotFoundException;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.repository.PersonRepository;
import com.quetoquenana.personservice.service.PersonService;
import com.quetoquenana.personservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final UserService userService;

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    @Override
    public Optional<Person> findByIdNumber(String idNumber) { return personRepository.findByIdNumber(idNumber);}

    @Override
    public Person save(Person person) {
        String username = userService.getCurrentUsername();
        return personRepository.findByIdNumber(person.getIdNumber())
            .map(found -> {
                if (found.isActive()) {
                    throw new DuplicateRecordException("person.id.number.duplicate.active");
                } else {
                    found.setActive(true);
                    found.setUpdatedAt(LocalDateTime.now());
                    found.setUpdatedBy(username);
                    return personRepository.save(found);
                }
            })
            .orElseGet(() -> {
                person.setActive(true);
                person.setCreatedAt(LocalDateTime.now());
                person.setCreatedBy(username);
                return personRepository.save(person);
            });
    }

    @Override
    public Person update(UUID id, Person newPerson) {
        String username = userService.getCurrentUsername();
        return personRepository.findById(id)
            .map(existingPerson -> {
                if (!existingPerson.getIdNumber().equals(newPerson.getIdNumber())) {
                    throw new ImmutableFieldModificationException("person.id.number.immutable");
                }
                existingPerson.setName(newPerson.getName());
                existingPerson.setLastname(newPerson.getLastname());
                existingPerson.setUpdatedAt(LocalDateTime.now());
                existingPerson.setUpdatedBy(username);
                return personRepository.save(existingPerson);
            })
            .orElseThrow(RecordNotFoundException::new);
    }

    @Override
    public void deleteById(UUID id) {
        String username = userService.getCurrentUsername();
        personRepository.findById(id)
            .ifPresentOrElse(existingPerson -> {
                if (existingPerson.isActive()) {
                    existingPerson.setActive(false);
                    existingPerson.setUpdatedAt(LocalDateTime.now());
                    existingPerson.setUpdatedBy(username);
                    personRepository.save(existingPerson);
                }
            }, () -> {
                throw new RecordNotFoundException();
            });
    }
}
