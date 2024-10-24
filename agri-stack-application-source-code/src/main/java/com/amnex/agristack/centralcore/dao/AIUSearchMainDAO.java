package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class AIUSearchMainDAO {
	public Integer offset;
	public Integer limit;
	public AIUSearchFiltersDAO filters;

}
