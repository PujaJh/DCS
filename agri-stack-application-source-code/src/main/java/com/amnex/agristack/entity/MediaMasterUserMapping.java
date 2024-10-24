package com.amnex.agristack.entity;


import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "media_master_user_mapping", schema = "agri_stack")
public class MediaMasterUserMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Maps to SERIAL PRIMARY KEY

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Maps to BIGINT NOT NULL

    @Column(name="media_id")
    private String mediaID;

    @Column(name = "created_on", nullable = false, updatable = false)
    private Timestamp createdOn;  // Maps to TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;  // Maps to BOOLEAN DEFAULT true

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getMediaID() {
        return mediaID;
    }

    public void setMediaID(String mediaID) {
        this.mediaID = mediaID;
    }
}
