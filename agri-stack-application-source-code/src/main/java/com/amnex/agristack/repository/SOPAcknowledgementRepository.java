package com.amnex.agristack.repository;

import com.amnex.agristack.entity.SOPAcknowledgement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SOPAcknowledgementRepository extends JpaRepository<SOPAcknowledgement, Long>{
    List<SOPAcknowledgement> findByReferenceIdOrderByIdDesc(String referenceId);

    List<SOPAcknowledgement> findByReferenceIdAndSopTypeOrderByIdDesc(String apiCallReferenceId, String toString);

    List<SOPAcknowledgement> findByReferenceIdAndStartSerialNumberAndEndSerialNumberOrderByIdDesc(String referenceId, Long recordIdStart, Long recordIdEnd);

    List<SOPAcknowledgement> findByReferenceIdAndStartingNumberAndEndingNumberOrderByIdDesc(String referenceId, Long valueOf, Long valueOf1);
}
