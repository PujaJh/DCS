package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class QueryDAO {
    String query_name;
    String mapper_id;
//    List<QueryParamsDAO> query_params;
    Object query_params;
 }
