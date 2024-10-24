package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class ConsentAttributesDAO {
	ConsentMainDAO main;
	Boolean consent_required;
}
