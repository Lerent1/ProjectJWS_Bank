package org.example.projectjws_bank.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private String username;

    private String phone;

    private Boolean enabled;

    private Boolean isKyc;

    private String role;

    //
    private String accountNumber;
}
