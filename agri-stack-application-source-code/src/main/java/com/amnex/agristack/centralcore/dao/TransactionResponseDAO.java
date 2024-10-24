package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TransactionResponseDAO {
	public String signature;
	public HeaderDAO header;
	public MessageDAO message;

	public String referenceId;
}