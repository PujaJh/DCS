package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignedPlotOwnerRepsonseDTO {
    @JsonProperty("ownerId")
    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    int ownerId;

    @JsonProperty("ownerName")
    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    String ownerName;

    Integer ownerType;
    String ownerTypeName;
    String mainOwnerId;
    String mainOwner;


    @JsonProperty("ownerType")
    public Integer getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(Integer ownerType) {
        this.ownerType = ownerType;
    }

    @JsonProperty("ownerTypeName")
    public String getOwnerTypeName() {
        return ownerTypeName;
    }

    public void setOwnerTypeName(String ownerTypeName) {
        this.ownerTypeName = ownerTypeName;
    }

    @JsonProperty("mainOwnerId")
    public String getMainOwnerId() {
        return mainOwnerId;
    }

    public void setMainOwnerId(String mainOwnerId) {
        this.mainOwnerId = mainOwnerId;
    }

    @JsonProperty("mainOwner")
    public String getMainOwner() {
        return mainOwner;
    }

    public void setMainOwner(String mainOwner) {
        this.mainOwner = mainOwner;
    }

}