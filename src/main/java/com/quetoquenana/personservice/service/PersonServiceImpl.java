package com.quetoquenana.personservice.service;

import com.quetoquenana.personservice.exception.ImmutableFieldModificationException;
import com.quetoquenana.personservice.model.Person;
import com.quetoquenana.personservice.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

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
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person update(Person person, Person newPerson) {
        if (!person.getIdNumber().equals(newPerson.getIdNumber())) {
            throw new ImmutableFieldModificationException("person.id.number.immutable");
        }
        // Only update allowed fields
        person.setName(newPerson.getName());
        person.setLastname(newPerson.getLastname());
        person.setBirthday(newPerson.getBirthday());
        person.setGender(newPerson.getGender());
        // Do NOT update idNumber
        return personRepository.save(person);
    }

    @Override
    public void deleteById(UUID id) {
        personRepository.deleteById(id);
    }

    @Override
    public Optional<Person> findByIdNumber(String idNumber) {
        return personRepository.findByIdNumber(idNumber);
    }
}
