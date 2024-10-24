package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Entity class representing reasons stored in the system.
 * Each instance corresponds to a specific reason with its unique identifier
 * and a description of the reason.
 */

@Entity
@Data
public class ReasonMaster extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;
}
