package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.response.DepartmentResponse;
import com.reimbursement.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_DEPARTMENTS)
@CrossOrigin(origins = { AppConstants.CORS_ORIGIN_LOCAL }, allowedHeaders = "*", allowCredentials = "true")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> listDepartments() {
        List<DepartmentResponse> response = departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getName(), d.getCode()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
