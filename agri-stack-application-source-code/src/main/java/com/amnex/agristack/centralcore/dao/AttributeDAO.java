package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class AttributeDAO {
//    List<FarmerRegAttributes> farmerRegAttributes;
//
//    List<CropRegAttributesDAO> cropRegAttributes;



    List<List<String>> farmerRegAttributes;

    List<List<String>> cropRegAttributes;

}
