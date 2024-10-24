package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.DummyLandRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.vividsolutions.jts.geom.Geometry;

public interface DummyLandRecordREpo extends JpaRepository<DummyLandRecords, Long> {

	@Query("select df from DummyLandRecords df where df.surveyNumber=:surveyNumber and villageLgdCode=:villageLgdCode ")
	public List<DummyLandRecords>findBySurveyNumberAndVillageLgdCode(String surveyNumber,Long villageLgdCode);
	
	@Query("select df from DummyLandRecords df where df.surveyNumber=:surveyNumber and df.subSurveyNumber=:subSurveyNumber and villageLgdCode=:villageLgdCode")
	public List<DummyLandRecords>findBySurveyNumberAndSubSurveyNumberAndVillageLgdCodeLimit1(String surveyNumber,String subSurveyNumber,Long villageLgdCode);

	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.uploaded_plot_details where lgd_villag = :villageLgdCode", nativeQuery = true)
	public void deleteAllByVillageCode(Long villageLgdCode);

	@Query(value = "select "
			+ "st_astext("
			+ "st_transform( "
			+ "st_setsrid("
			+ "st_force2d(st_geomfromtext(:plotgeometry)) "
			+ ",:sourceSRS),:destCRS))",nativeQuery = true)
	public String getGeometryFromWKT(String plotgeometry, int sourceSRS, int destCRS);

	@Query(value = "select st_astext(st_transform(st_setsrid(st_geomfromGeojson(:plotgeometry),:projectionCode),:destCRS))", nativeQuery = true)
	public String getWKTFromGeoJson(String plotgeometry, int projectionCode, int destCRS);
	
	@Query(value = "select st_force2d(st_transform(st_setsrid(st_geomfromGeojson(:plotgeometry),:projectionCode),:destCRS))", nativeQuery = true)
	public Geometry getGeometryFromGeoJson(String plotgeometry, int projectionCode, int destCRS);
	
	@Query(value = "select st_astext(st_force2d(st_transform(st_setsrid(st_geomfromGeojson(:plotgeometry),:projectionCode),:destCRS)))", nativeQuery = true)
	public String getWKTStringFromGeoJson(String plotgeometry, int projectionCode, int destCRS);
	
	@Query(value = "select "
			+ "st_transform( "
			+ "st_setsrid("
			+ "st_force2d(st_geomfromtext(:plotgeometry)) "
			+ ",:sourceSRS),:destCRS)",nativeQuery = true)
	public Geometry getGeometryFromWKTV2(String plotgeometry, int sourceSRS, int destCRS);

	@Query("select df from DummyLandRecords df where df.surveyNumber=:surveyNumber and df.villageLgdCode=:villageLgdCode and df.uniqueCode=:uniqueCode ")
	public List<DummyLandRecords>findBySurveyNumberAndVillageLgdCodeAndUniqueCode(String surveyNumber,Long villageLgdCode,String uniqueCode);
	
}
