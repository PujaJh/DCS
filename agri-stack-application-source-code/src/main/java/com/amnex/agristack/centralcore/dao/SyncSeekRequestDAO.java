package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class SyncSeekRequestDAO {

    public String mapper_id;
    public String farmer_id;
    public Long state_lgd_code;

}
