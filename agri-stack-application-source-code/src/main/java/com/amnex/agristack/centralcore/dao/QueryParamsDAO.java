package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class QueryParamsDAO {

    String farmer_id;
    Long state_lgd_code;
    String season;
    String year;
    Long village_lgd_code;
    String khasra_number;
    String unique_number;
    
}
