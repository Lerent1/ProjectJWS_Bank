package org.example.projectjws_bank.model.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String fullName;

    private String phone;

    private Boolean enabled;
}
