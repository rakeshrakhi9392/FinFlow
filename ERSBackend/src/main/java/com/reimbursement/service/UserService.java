package com.reimbursement.service;

import com.reimbursement.dto.request.LoginRequest;
import com.reimbursement.dto.request.RegistrationRequest;
import com.reimbursement.dto.response.UserLoginResponse;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.User;
import com.reimbursement.exception.AuthenticationException;
import com.reimbursement.exception.BadRequestException;
import com.reimbursement.exception.ResourceNotFoundException;
import com.reimbursement.mapper.UserMapper;
import com.reimbursement.repository.DepartmentRepository;
import com.reimbursement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DepartmentRepository departmentRepository;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public UserLoginResponse registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        User user = userMapper.toEntity(request);
        user.setDepartment(resolveDepartment(request.getDepartmentId()));
        User saved = userRepository.save(user);
        return userMapper.toLoginResponse(saved);
    }

    public UserLoginResponse loginUser(LoginRequest request) {
        return userRepository.findByUsernameAndPassword(request.getUsername(), request.getPassword())
                .map(userMapper::toLoginResponse)
                .orElseThrow(() -> new AuthenticationException("Login Failed!"));
    }

    @Transactional
    public void deleteUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User with id " + userId + " does not exist"));
        userRepository.delete(user);
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId != null) {
            return departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + departmentId));
        }
        return departmentRepository.findByCode("ENG")
                .orElseThrow(() -> new ResourceNotFoundException("Default department ENG is not seeded"));
    }
}
