package com.quetoquenana.template.service;

import com.quetoquenana.template.model.Execution;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ExecutionService {
    List<Execution> getAllExecutions();
    Optional<Execution> getExecutionById(UUID id);
    void saveExecutionOnStartup();
    Page<Execution> getExecutionsPage(int page, int size);
}
