package com.example.bazaar.mapper;

import org.springframework.stereotype.Component;

import com.example.bazaar.dto.UserDto;
import com.example.bazaar.model.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .billingAddress(user.getBillingAddress())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setBillingAddress(dto.getBillingAddress());
        user.setRole(dto.getRole());
        user.setEnabled(dto.isEnabled());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
}
