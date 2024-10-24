/**
 *
 */
package com.amnex.agristack.dao;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 *
 * 8 Feb 2023 6:38:21 pm
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarmlandOwnershipDto {

	private Long farmLandOwnerShipId;

	private Long farmerRegistryId;

	private Long farmlandPlotRegistryId;

	private String ownerNoAsPerRor;

	private String ownerNamePerRor;

	private String ownerCleanedUpName;

	private Integer ownershipShareExtent;

	private String ownerIdentifierNamePerRor;

	private Integer ownerIdentifierTypePerRor;

	private Double extentAssignedArea;

	private Double extentTotalArea;

	//	private Long utUnitTypeMasterId;

	private Date updatedDate;

	private Date synchronisationDate;

	private Long plotStatusMasterId;

	private Integer farmerNameMatchScoreRor;

	private Integer mainOwnerNoAsPerRor;

	private Integer ownerType;

	private Integer ownershipShareType;
	
	//private OwnerShareType ownerShareTypeDetails;
	
	private Double farmLandOwnerNameMatchScore;

}
