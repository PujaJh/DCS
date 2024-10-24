package com.amnex.agristack.dao;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Data;

@Data
public class CadastralLinePointDTO {
	
	private Geometry lineStringGeom;
	
	private Geometry pointGeom;
	
	private Geometry perpPointGeom;
	
	private Double nearestDistance;
	
}
