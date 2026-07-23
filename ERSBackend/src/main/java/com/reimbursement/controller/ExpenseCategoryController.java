package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.response.ExpenseCategoryResponse;
import com.reimbursement.repository.ExpenseCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_CATEGORIES)
@CrossOrigin(origins = { AppConstants.CORS_ORIGIN_LOCAL }, allowedHeaders = "*", allowCredentials = "true")
public class ExpenseCategoryController {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryController(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseCategoryResponse>> listCategories() {
        List<ExpenseCategoryResponse> response = expenseCategoryRepository.findAll().stream()
                .map(c -> new ExpenseCategoryResponse(c.getId(), c.getName(), c.getCode(), c.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
