package com.amnex.agristack.centralcore.dao;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class ResponseMessageDAO {
    private String ackStatus;
    private Timestamp timestamp;
    private String correlationId;
    private ErrorDAO error;
}
