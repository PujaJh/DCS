package com.amnex.agristack.service;

import com.amnex.agristack.repository.DummyLandRecordREpo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;


@Service
public class DummyLandRecordREpoService {

    @Autowired
	private EntityManager entityManager;

  
        public String getWKTFromGeoJson1(String plotgeometry, int projectionCode, int destCRS) {
            String queryString = "SELECT st_astext(st_transform(st_setsrid(st_geomfromGeojson(:plotgeometry), :projectionCode), :destCRS))";
        //     javax.persistence.Query query =  entityManager.createNativeQuery(queryString);
        //     query.setParameter("plotgeometry", plotgeometry);
        //     query.setParameter("projectionCode", projectionCode);
        //     query.setParameter("destCRS", destCRS);
    
        //     // Print the exact query with values
        //     System.out.println("Executing query: " + queryString);
        //     System.out.println("With parameters: plotgeometry=" + plotgeometry + ", projectionCode=" + projectionCode + ", destCRS=" + destCRS);
        //    // System.out.println("query "+(String) ((javax.persistence.Query) query).getSingleResult());
        //   //  Object result = ((javax.persistence.Query) query).getSingleResult();
        //     //System.out.println(" result "+result);
    

        // List<?> result1 = query.getResultList();

        // for (Object obj : result1) {
        //     System.out.println(obj);
        // }

        String geoJson = "{\"type\": \"MultiPolygon\", \"coordinates\": [[[[" +
        "76.63614467612219983, 9.26483003500575464], " +
        "[76.63603916908655833, 9.2648581141214521], " +
        "[76.63588253129643135, 9.26499420182978994], " +
        "[76.63589589518802825, 9.26516629472095943], " +
        "[76.6362909793844409, 9.26509417242615108], " +
        "[76.63629143585586689, 9.26496192966661702], " +
        "[76.63626972399563897, 9.26476372737465859], " +
        "[76.63614467612219983, 9.26483003500575464]]]]}";

    String queryStr = "SELECT st_astext(st_transform(st_setsrid(st_geomfromGeojson(:plotgeometry), :projectionCode), :destCRS))";

    HashMap<String, Object> params = new HashMap<>();
    params.put("plotgeometry", geoJson);
    params.put("projectionCode", 4326);
    params.put("destCRS", 4326);

    System.out.println("Executing query: " + queryStr);
    System.out.println("With parameters: " + params);

    javax.persistence.Query nativeQuery = entityManager.createNativeQuery(queryStr);
    nativeQuery.setParameter("plotgeometry", params.get("plotgeometry"));
    nativeQuery.setParameter("projectionCode", params.get("projectionCode"));
    nativeQuery.setParameter("destCRS", params.get("destCRS"));

    List<String> result = nativeQuery.getResultList();
    String result1 = "";

    for (String obj : result) {
        System.out.println("obj" +obj);
        result1 = obj;
    }
           // return (String) ((javax.persistence.Query) query).getSingleResult();
           return result1;
        }
       
    
}
