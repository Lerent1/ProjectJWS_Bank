package org.example.projectjws_bank.model.dto.request;

import lombok.Data;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Khong duoc de trong")
    private String fullName;

    @NotBlank(message = "Khong duoc de trong")
    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "Khong duoc de trong")
    @Size(min = 4, max = 20, message = "Username tu 4-20 ky tu")
    private String username;

    @NotBlank(message = "Khong duoc de trong")
    @Size(min = 6, message = "Password toi thieu 6 ky tu")
    private String password;

    @NotBlank(message = "Khong duoc de trong")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "So dien thoai khong hop le")
    private String phone;

    @NotBlank(message = "Khong duoc de trong")
    @Pattern(regexp = "^[0-9]{4,6}$", message = "PIN phai la 4-6 so")
    private String pinCode;
}
