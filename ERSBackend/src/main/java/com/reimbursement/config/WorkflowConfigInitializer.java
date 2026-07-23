package com.reimbursement.config;

import com.reimbursement.service.WorkflowConfigService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Ensures the default workflow_config row exists in every non-test environment
 * (including production, where demo user seeding is disabled).
 */
@Component
@Profile("!test")
public class WorkflowConfigInitializer implements CommandLineRunner {

    private final WorkflowConfigService workflowConfigService;

    public WorkflowConfigInitializer(WorkflowConfigService workflowConfigService) {
        this.workflowConfigService = workflowConfigService;
    }

    @Override
    public void run(String... args) {
        workflowConfigService.ensureDefaultConfig();
    }
}
