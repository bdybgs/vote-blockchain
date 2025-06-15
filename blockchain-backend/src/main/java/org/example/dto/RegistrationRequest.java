package org.example.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotEmpty
    @Email
    String email;
    @NotEmpty
    private String username;
    @Size(min = 5, message = "Password must be more than 5 characters")
    @NotEmpty
    private String password;
}

