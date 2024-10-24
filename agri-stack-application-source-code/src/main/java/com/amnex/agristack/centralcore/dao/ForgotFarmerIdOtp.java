package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class ForgotFarmerIdOtp {
    public String farmerId;
    public String otp;
    public String data;
}
