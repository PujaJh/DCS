package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class MessageDAO {
    public String transaction_id;
    public String correlation_id;
    public String locale = "en";

    public PaginationCentralDAO pagination;
    public List<SearchResponseDAO> search_request;

    public List<SearchResponseDAO> search_response;

}
