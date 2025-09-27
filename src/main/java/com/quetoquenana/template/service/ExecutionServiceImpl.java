package com.quetoquenana.template.service;

import com.quetoquenana.template.model.Execution;
import com.quetoquenana.template.repository.ExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {
    private final ExecutionRepository executionRepository;

    @Value("${app.version:0.0.1}")
    private String appVersion;

    @Value("${spring.profiles.active:dev}")
    private String environment;

    /**
     * Save a new execution record. Called by StartupExecutionRecorder.
     */
    public void saveExecutionOnStartup() {
        Execution execution = new Execution();
        execution.setId(UUID.randomUUID());
        execution.setExecutedAt(LocalDateTime.now());
        try {
            execution.setServerName(InetAddress.getLocalHost().getHostName());
            execution.setIpAddress(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            execution.setServerName("unknown");
            execution.setIpAddress("unknown");
        }
        execution.setAppVersion(appVersion);
        execution.setEnvironment(environment);
        executionRepository.save(execution);
    }

    @Override
    public List<Execution> getAllExecutions() {
        return executionRepository.findAll();
    }

    @Override
    public Optional<Execution> getExecutionById(UUID id) {
        return executionRepository.findById(id);
    }

    @Override
    public Page<Execution> getExecutionsPage(int page, int size) {
        return executionRepository.findAll(PageRequest.of(page, size));
    }
}
