package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class FarmerGrievanceMediaMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long farmerGrievanceId;
    private String mediaId;

    @Transient
    MediaMaster mediaMaster;
}
