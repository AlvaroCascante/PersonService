package com.quetoquenana.template.repository;

import com.quetoquenana.template.model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExecutionRepository extends JpaRepository<Execution, UUID> {
}
