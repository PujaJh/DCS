package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PaginationCentralDAO {
    Long page_size;
    Long page_number;
    Long total_count;
}
