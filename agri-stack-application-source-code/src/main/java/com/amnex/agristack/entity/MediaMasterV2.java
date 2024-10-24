package com.amnex.agristack.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

/**
 * Entity class representing media details.
 * Each instance corresponds to a specific media with its unique identifier, type, URL, and activity code.
 */

@Entity
@Table(name = "media_master_v2")
public class MediaMasterV2 {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String mediaId;
    private String mediaType;
    private String mediaUrl;
    private Integer activityCode;

    private Timestamp inserted_at;

    private Long userId;  // Maps to BIGINT NOT NULL

    public MediaMasterV2() {
        super();
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Integer getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(Integer activityCode) {
        this.activityCode = activityCode;
    }

    public Timestamp getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(Timestamp inserted_at) {
        this.inserted_at = inserted_at;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
