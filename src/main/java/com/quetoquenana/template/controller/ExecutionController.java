package com.quetoquenana.template.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.quetoquenana.template.model.Execution;
import com.quetoquenana.template.model.ApiResponse;
import com.quetoquenana.template.model.ApiBaseResponseView;
import com.quetoquenana.template.util.JsonViewPageUtil;
import com.quetoquenana.template.service.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RestController
@JsonView(ApiBaseResponseView.Always.class)
@RequestMapping("/api/executions")
public class ExecutionController {
    private final ExecutionService executionService;

    @Autowired
    private MessageSource messageSource;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    /**
     * Get all executions
     * @return ResponseEntity with ApiResponse (list of executions)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getAllExecutions() {
        log.info("GET /api/executions called");
        List<Execution> executions = executionService.getAllExecutions();
        return ResponseEntity.ok(new ApiResponse(executions));
    }

    /**
     * Get execution by id
     * @param id UUID of execution
     * @return ResponseEntity with ApiResponse (execution or error)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse> getExecutionById(@PathVariable UUID id, Locale locale) {
        log.info("GET /api/executions/{} called", id);
        return executionService.getExecutionById(id)
            .map(execution -> ResponseEntity.ok(new ApiResponse(execution)))
            .orElseGet(() -> {
                log.error("Execution with id {} not found", id);
                return ResponseEntity.status(404)
                    .body(new ApiResponse(messageSource.getMessage("error.not.found", null, locale), 404));
            });
    }

    /**
     * Get executions with pagination
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @return ResponseEntity with ApiResponse (paginated executions)
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse> getExecutionsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/executions/page?page={}&size={} called", page, size);
        Page<Execution> executionsPage = executionService.getExecutionsPage(page, size);
        return ResponseEntity.ok(new ApiResponse(new JsonViewPageUtil<>(executionsPage, executionsPage.getPageable())));
    }
}