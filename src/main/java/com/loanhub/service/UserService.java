package com.loanhub.service;

import com.loanhub.dto.UserDto;
import com.loanhub.entity.User;
import com.loanhub.exception.AppException;
import com.loanhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found"));
        return UserDto.fromEntity(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found"));
        return UserDto.fromEntity(user);
    }

    public UserDto updateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found"));

        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("phone")) user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("dob")) user.setDob((String) updates.get("dob"));
        if (updates.containsKey("panCard")) user.setPanCard((String) updates.get("panCard"));
        if (updates.containsKey("aadhaarCard")) user.setAadhaarCard((String) updates.get("aadhaarCard"));
        if (updates.containsKey("annualIncome")) user.setAnnualIncome((String) updates.get("annualIncome"));
        if (updates.containsKey("education")) user.setEducation((String) updates.get("education"));
        if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
        if (updates.containsKey("profileImageUrl")) user.setProfileImageUrl((String) updates.get("profileImageUrl"));
        if (updates.containsKey("kycStatus")) {
            try {
                user.setKycStatus(User.KycStatus.valueOf((String) updates.get("kycStatus")));
            } catch (IllegalArgumentException ignored) {}
        }
        if (updates.containsKey("status")) {
            try {
                user.setStatus(User.Status.valueOf((String) updates.get("status")));
            } catch (IllegalArgumentException ignored) {}
        }
        if (updates.containsKey("password")) {
            String newPassword = (String) updates.get("password");
            if (newPassword != null && newPassword.length() >= 6) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
        }

        return UserDto.fromEntity(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException("User not found");
        }
        userRepository.deleteById(id);
    }

    public List<UserDto> getUsersByRole(String role) {
        try {
            User.Role r = User.Role.valueOf(role);
            return userRepository.findByRole(r).stream()
                    .map(UserDto::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException("Invalid role: " + role);
        }
    }
}
