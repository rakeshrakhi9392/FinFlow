package com.reimbursement.mapper;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.RegistrationRequest;
import com.reimbursement.dto.response.UserLoginResponse;
import com.reimbursement.entity.User;
import com.reimbursement.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Maps registration fields except password (encoded in {@code UserService}) and role
     * (validated and applied in {@code UserService}).
     */
    public User toEntity(RegistrationRequest request, UserRole role) {
        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setFirstName(resolveFirstName(request.getFirstName()));
        user.setLastName(resolveLastName(request.getLastName()));
        user.setRole(role.getValue());
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }
        return user;
    }

    public UserLoginResponse toLoginResponse(User user) {
        return new UserLoginResponse(user.getUserId(), user.getUsername(), user.getRole());
    }

    private String resolveFirstName(String firstName) {
        return (firstName == null || firstName.isBlank()) ? AppConstants.DEFAULT_FIRST_NAME : firstName.trim();
    }

    private String resolveLastName(String lastName) {
        return (lastName == null || lastName.isBlank()) ? AppConstants.DEFAULT_LAST_NAME : lastName.trim();
    }
}
