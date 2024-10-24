package com.amnex.agristack.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class FarmerGrievance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long lpsmId;
    Long grievanceReasonId;
    String description;
    Long userId;
    Long status;
    Long resolvedBy;
    String refId;
    String rejectionRemark;

    @Transient
    List<FarmerGrievanceMediaMapping> farmerGrievanceMedias;
    @Transient
    String statusName;
    @Transient
    Boolean isApproved;
}
