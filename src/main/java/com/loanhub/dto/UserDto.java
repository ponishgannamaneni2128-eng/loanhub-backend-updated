package com.loanhub.dto;

import com.loanhub.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String kycStatus;
    private String dob;
    private String panCard;
    private String aadhaarCard;
    private String annualIncome;
    private String education;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        dto.setKycStatus(user.getKycStatus().name());
        dto.setDob(user.getDob());
        dto.setPanCard(user.getPanCard());
        dto.setAadhaarCard(user.getAadhaarCard());
        dto.setAnnualIncome(user.getAnnualIncome());
        dto.setEducation(user.getEducation());
        dto.setBio(user.getBio());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
