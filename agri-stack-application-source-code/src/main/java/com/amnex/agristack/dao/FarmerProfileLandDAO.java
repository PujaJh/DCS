package com.amnex.agristack.dao;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FarmerProfileLandDAO {
	public FarmerDetailDAO farmerDetails;
	public ArrayList<LandDetailDAO> landDetails;

}
