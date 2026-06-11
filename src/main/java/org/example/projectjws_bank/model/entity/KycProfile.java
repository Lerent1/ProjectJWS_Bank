package org.example.projectjws_bank.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectjws_bank.model.entity.enums.KycStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String documentUrl;

    private String documentType;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
