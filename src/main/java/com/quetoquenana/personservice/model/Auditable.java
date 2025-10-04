package com.quetoquenana.personservice.model;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {
    @Column(name = "created_at", nullable = false)
    @JsonView(ApiBaseResponseView.Admin.class)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    @JsonView(ApiBaseResponseView.Admin.class)
    private String createdBy;

    @Column(name = "updated_at")
    @JsonView(ApiBaseResponseView.Admin.class)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    @JsonView(ApiBaseResponseView.Admin.class)
    private String updatedBy;
}

