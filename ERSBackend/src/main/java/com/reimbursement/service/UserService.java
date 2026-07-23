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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       DepartmentRepository departmentRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserLoginResponse registerUser(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDepartment(resolveDepartment(request.getDepartmentId()));
        User saved = userRepository.save(user);
        return userMapper.toLoginResponse(saved);
    }

    public UserLoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Login Failed!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Login Failed!");
        }
        return userMapper.toLoginResponse(user);
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
