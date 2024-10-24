package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class O16OutputDAO {
    public Object farmerdetails;
    public List<Object> plotdetails;
    public List<O16CropDetailsDAO> cropsowndetails;
}
