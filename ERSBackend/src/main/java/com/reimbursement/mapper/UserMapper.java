package com.reimbursement.mapper;

import com.reimbursement.constant.AppConstants;
import com.reimbursement.dto.request.RegistrationRequest;
import com.reimbursement.dto.response.UserLoginResponse;
import com.reimbursement.entity.User;
import com.reimbursement.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(resolveFirstName(request.getFirstName()));
        user.setLastName(resolveLastName(request.getLastName()));
        user.setRole(resolveRole(request.getRole()));
        user.setEmail(request.getEmail());
        return user;
    }

    public UserLoginResponse toLoginResponse(User user) {
        return new UserLoginResponse(user.getUserId(), user.getUsername(), user.getRole());
    }

    private String resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return AppConstants.DEFAULT_ROLE;
        }
        return UserRole.fromValue(role).getValue();
    }

    private String resolveFirstName(String firstName) {
        return (firstName == null || firstName.isBlank()) ? AppConstants.DEFAULT_FIRST_NAME : firstName;
    }

    private String resolveLastName(String lastName) {
        return (lastName == null || lastName.isBlank()) ? AppConstants.DEFAULT_LAST_NAME : lastName;
    }
}
