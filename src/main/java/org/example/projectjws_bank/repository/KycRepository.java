package org.example.projectjws_bank.repository;

import org.example.projectjws_bank.model.entity.KycProfile;
import org.example.projectjws_bank.model.entity.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<KycProfile,Long> {

    Page<KycProfile> findByStatus(KycStatus status, Pageable pageable);
}