package com.example.bazaar.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String billingAddress;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
