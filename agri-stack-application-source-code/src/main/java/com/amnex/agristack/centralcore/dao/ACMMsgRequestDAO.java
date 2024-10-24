package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class ACMMsgRequestDAO {
    public String requestId;
    public String farmerId;
    public String farmerMobileNo;
    public String farmerName;
    public String aiuName;
    public String aipName;
    public String purpose;
    public String acmUrl;
    public String acmRequestId;
    public String acmName;
}

