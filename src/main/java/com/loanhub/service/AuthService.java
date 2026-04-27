package com.loanhub.service;

import com.loanhub.dto.AuthDto;
import com.loanhub.dto.UserDto;
import com.loanhub.entity.User;
import com.loanhub.exception.AppException;
import com.loanhub.repository.UserRepository;
import com.loanhub.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private PasswordEncoder passwordEncoder;

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Invalid email or password");
        }

        if (user.getStatus() == User.Status.suspended) {
            throw new AppException("Your account has been suspended. Please contact admin.");
        }

        if (user.getStatus() == User.Status.pending) {
            throw new AppException("Your account is pending approval.");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthDto.AuthResponse(token, UserDto.fromEntity(user));
    }

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email is already registered");
        }

        // Determine role - default to borrower if not provided or invalid
        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole() != null ? request.getRole() : "borrower");
        } catch (IllegalArgumentException e) {
            role = User.Role.borrower;
        }

        // Analysts go to pending status
        User.Status status = (role == User.Role.analyst) ? User.Status.pending : User.Status.active;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .status(status)
                .dob(request.getDob())
                .panCard(request.getPanCard())
                .aadhaarCard(request.getAadhaarCard())
                .annualIncome(request.getAnnualIncome())
                .education(request.getEducation())
                .build();

        user = userRepository.save(user);

        if (status == User.Status.pending) {
            throw new AppException("Registration successful! Your account is pending admin approval.");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new AuthDto.AuthResponse(token, UserDto.fromEntity(user));
    }
}
