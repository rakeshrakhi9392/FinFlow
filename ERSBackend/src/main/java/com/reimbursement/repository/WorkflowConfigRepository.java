package com.reimbursement.repository;

import com.reimbursement.entity.WorkflowConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfig, Long> {

    Optional<WorkflowConfig> findByConfigKey(String configKey);
}
