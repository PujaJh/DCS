package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class SearchCriteriaDAO {

    String query_type;
    String reg_type;
    PaginationCentralDAO pagination;
    List<SortDAO> sort;
    QueryDAO query;
//    List<Object> consent;
    Object consent;
}
