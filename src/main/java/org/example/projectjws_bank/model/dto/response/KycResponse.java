package org.example.projectjws_bank.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KycResponse {
    private Integer id;
    private String documentUrl;
    private String documentType;
    private String status;
}
