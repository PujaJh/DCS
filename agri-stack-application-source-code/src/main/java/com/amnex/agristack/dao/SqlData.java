package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class SqlData {

	int position;
	String value;
	String type;

	public SqlData() {
	}

	public SqlData(int position, String value, String type) {

		this.position = position;
		this.value = value;
		this.type = type;
	}

}
