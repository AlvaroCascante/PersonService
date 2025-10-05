package com.quetoquenana.personservice.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "persons")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Person extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JsonView(PersonList.class)
    private UUID id;

    @Column(name = "id_number", nullable = false, unique = true)
    @JsonView(PersonList.class)
    private String idNumber;

    @Column(name = "name", nullable = false)
    @JsonView(PersonList.class)
    private String name;

    @Column(name = "lastname", nullable = false)
    @JsonView(PersonList.class)
    private String lastname;

    @Column(name = "is_active", nullable = false)
    @JsonView(PersonDetail.class)
    private boolean isActive;

    @OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
    @JsonView(PersonDetail.class)
    private PersonProfile personProfile;

    // JSON Views to control serialization responses
    public static class PersonList extends ApiBaseResponseView.Always {}
    public static class PersonDetail extends Person.PersonList {}
}
