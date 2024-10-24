package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Entity class representing an activity in the system.
 * It stores information such as the activity ID, code, and name.
 */
@Entity
@Data
public class ActivityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private Integer code;
    private String activityName;
}
