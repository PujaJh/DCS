package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Entity class representing media details.
 * Each instance corresponds to a specific media with its unique identifier, type, URL, and activity code.
 */

@Entity
public class MediaMaster {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String mediaId;
    private String mediaType;
    private String mediaUrl;
    private Integer activityCode;

    public MediaMaster() {
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
}
