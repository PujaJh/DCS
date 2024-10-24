package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class TokenDAO {
    private String clientId;
    private String username;
    private String  password;
    private String grantType;
}
