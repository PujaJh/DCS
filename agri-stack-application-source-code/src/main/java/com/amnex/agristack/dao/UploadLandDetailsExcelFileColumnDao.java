package com.amnex.agristack.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadLandDetailsExcelFileColumnDao {

	private String columnName;
	private Integer indexValue;
}
