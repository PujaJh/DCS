/**
 * 
 */
package com.amnex.agristack.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.amnex.agristack.entity.CropRegistry;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
public class CropSequenceMappingDAO {
	
	
	private Long cropSequenceMappingId;

	private Long stateLgdCode;
	private Long districtCode;
	private Long subDistrictCode;
	private List<Long> subDistrictCodes;

	private Long seasonId;

	private Long cropId;

	private Integer sequenceNumber;
	
	private Timestamp createdOn;

	private String createdBy;

	private String createdIp;
	private String type;
	
	List<CropSequenceDAO> cropSequenceList;

}
