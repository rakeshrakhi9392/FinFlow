package com.reimbursement.controller;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.LoginRequest;
import com.reimbursement.dto.request.RegistrationRequest;
import com.reimbursement.dto.response.MessageResponse;
import com.reimbursement.dto.response.UserLoginResponse;
import com.reimbursement.security.SessionAuthService;
import com.reimbursement.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API_USERS)
public class UserController {

    private final UserService userService;
    private final SessionAuthService sessionAuthService;

    public UserController(UserService userService, SessionAuthService sessionAuthService) {
        this.userService = userService;
        this.sessionAuthService = sessionAuthService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        UserLoginResponse created = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User " + created.getUsername() + " was created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest request,
                                                       HttpSession session) {
        UserLoginResponse response = userService.loginUser(request);
        sessionAuthService.establishSession(session, response.getUserId(), response.getUsername(), response.getRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpSession session) {
        sessionAuthService.clearSession(session);
        return ResponseEntity.ok(new MessageResponse("Logged out"));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new MessageResponse("User " + userId + " was deleted successfully"));
    }
}
