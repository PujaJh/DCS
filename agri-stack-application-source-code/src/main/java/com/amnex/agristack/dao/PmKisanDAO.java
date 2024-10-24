package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PmKisanDAO {
	public String Rsponce;
	public String Message;
	public Object ReasonfornotReceivingBenefit;
	public Object LandStatus;
	public Object eKYCStatus;
	public Object EligibleStatus;
	public Object ReasonforMarkEligible;
	public Object PendingRemarks;
}
