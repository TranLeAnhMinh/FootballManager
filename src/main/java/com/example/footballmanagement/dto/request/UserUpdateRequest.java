package com.example.footballmanagement.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    
    @NotBlank(message= "Full name cannot be blank")
    @Size(max = 255, message= "Full name must not exceed 255 characters")
    private String fullname;

    @Pattern(regexp = "^(\\+?\\d{9,15})?$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;
}
