package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.response.DepartmentResponse;
import com.reimbursement.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_DEPARTMENTS)
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> listDepartments() {
        List<DepartmentResponse> response = departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getName(), d.getCode()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
