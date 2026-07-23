package com.reimbursement.service;

import com.reimbursement.dto.request.UpdateWorkflowConfigRequest;
import com.reimbursement.dto.response.WorkflowConfigResponse;
import com.reimbursement.entity.WorkflowConfig;
import com.reimbursement.repository.WorkflowConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkflowConfigService {

    private final WorkflowConfigRepository workflowConfigRepository;

    public WorkflowConfigService(WorkflowConfigRepository workflowConfigRepository) {
        this.workflowConfigRepository = workflowConfigRepository;
    }

    public WorkflowConfig getActiveConfig() {
        return workflowConfigRepository.findByConfigKey(WorkflowConfig.DEFAULT_KEY)
                .orElseGet(this::defaultConfig);
    }

    public WorkflowConfigResponse getActiveConfigResponse() {
        return toResponse(getActiveConfig());
    }

    @Transactional
    public WorkflowConfigResponse updateConfig(UpdateWorkflowConfigRequest request) {
        WorkflowConfig config = workflowConfigRepository.findByConfigKey(WorkflowConfig.DEFAULT_KEY)
                .orElseGet(() -> {
                    WorkflowConfig created = defaultConfig();
                    return workflowConfigRepository.save(created);
                });

        if (request.getSeniorApprovalAmountThreshold() != null) {
            config.setSeniorApprovalAmountThreshold(request.getSeniorApprovalAmountThreshold());
        }
        if (request.getEscalateOnBudgetExceed() != null) {
            config.setEscalateOnBudgetExceed(request.getEscalateOnBudgetExceed());
        }
        if (request.getSeniorManagerStageEnabled() != null) {
            config.setSeniorManagerStageEnabled(request.getSeniorManagerStageEnabled());
        }
        if (request.getFinanceStageEnabled() != null) {
            config.setFinanceStageEnabled(request.getFinanceStageEnabled());
        }
        if (request.getDescription() != null) {
            config.setDescription(request.getDescription());
        }
        return toResponse(workflowConfigRepository.save(config));
    }

    @Transactional
    public WorkflowConfig ensureDefaultConfig() {
        return workflowConfigRepository.findByConfigKey(WorkflowConfig.DEFAULT_KEY)
                .orElseGet(() -> workflowConfigRepository.save(defaultConfig()));
    }

    private WorkflowConfig defaultConfig() {
        WorkflowConfig config = new WorkflowConfig();
        config.setConfigKey(WorkflowConfig.DEFAULT_KEY);
        config.setSeniorApprovalAmountThreshold(5000.0);
        config.setEscalateOnBudgetExceed(true);
        config.setSeniorManagerStageEnabled(true);
        config.setFinanceStageEnabled(true);
        config.setDescription("Enterprise reimbursement approval workflow");
        return config;
    }

    private WorkflowConfigResponse toResponse(WorkflowConfig config) {
        WorkflowConfigResponse response = new WorkflowConfigResponse();
        response.setId(config.getId());
        response.setConfigKey(config.getConfigKey());
        response.setSeniorApprovalAmountThreshold(config.getSeniorApprovalAmountThreshold());
        response.setEscalateOnBudgetExceed(config.isEscalateOnBudgetExceed());
        response.setSeniorManagerStageEnabled(config.isSeniorManagerStageEnabled());
        response.setFinanceStageEnabled(config.isFinanceStageEnabled());
        response.setDescription(config.getDescription());
        return response;
    }
}
