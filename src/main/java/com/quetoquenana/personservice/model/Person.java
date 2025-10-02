package com.quetoquenana.personservice.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView(PersonList.class)
    private UUID id;

    @Column(nullable = false, unique = true)
    @JsonView(PersonList.class)
    private String idNumber;

    @Column(nullable = false)
    @JsonView(PersonList.class)
    private String name;

    @Column(nullable = false)
    @JsonView(PersonList.class)
    private String lastname;

    @Column(nullable = false)
    @JsonView(PersonDetail.class)
    private LocalDate birthday;

    @Column(nullable = false)
    @JsonView(PersonDetail.class)
    private String gender;

    // JSON Views to control serialization responses
    public static class PersonList extends ApiBaseResponseView.Always {}
    public static class PersonDetail extends Person.PersonList {}
}
