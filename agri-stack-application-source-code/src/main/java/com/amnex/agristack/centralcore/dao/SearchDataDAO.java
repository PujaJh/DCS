package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class SearchDataDAO {
    public String regRecord_type;
    public RegRecordsDAO reg_records;
}
