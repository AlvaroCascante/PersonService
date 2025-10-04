package com.quetoquenana.personservice.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "persons_profiles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PersonProfile extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_person", unique = true)
    private Person person;

    @Column(name = "birthday")
    @JsonView(Person.PersonDetail.class)
    private LocalDate birthday;

    @Column(name = "gender")
    @JsonView(Person.PersonDetail.class)
    private String gender;

    @Column(name = "nationality")
    @JsonView(Person.PersonDetail.class)
    private String nationality;

    @Column(name = "marital_status")
    @JsonView(Person.PersonDetail.class)
    private String maritalStatus;

    @Column(name = "occupation")
    @JsonView(Person.PersonDetail.class)
    private String occupation;

    @Column(name = "profile_picture_url")
    @JsonView(Person.PersonDetail.class)
    private String profilePictureUrl;
}
