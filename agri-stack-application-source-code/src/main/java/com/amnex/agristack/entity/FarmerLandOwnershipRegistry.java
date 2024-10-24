/**
 *
 */
package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 *
 */

@Entity
//@Data
//@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class FarmerLandOwnershipRegistry extends BaseEntity {


	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "flor_farmer_land_ownership_registry_id")
	private Long farmerLandOwnershipRegistryId;

	@ManyToOne
	@JsonBackReference
	//@JsonManagedReference
	@JoinColumn(name = "flor_fr_farmer_registry_id", referencedColumnName = "fr_farmer_registry_id")
	private FarmerRegistry farmerRegistryId;

	@ManyToOne
	@JoinColumn(name = "flor_fpr_farmland_plot_registry_id", referencedColumnName = "fpr_farmland_plot_registry_id")
	private FarmlandPlotRegistry farmlandPlotRegistryId;

	@Column(name = "flor_owner_no_as_per_ror", columnDefinition = "VARCHAR(30)")
	private String ownerNoAsPerRor;

	@Column(name = "flor_owner_name_per_ror", columnDefinition = "VARCHAR(100)")
	private String ownerNamePerRor;

	@Column(name = "flor_owner_cleaned_up_name", columnDefinition = "VARCHAR(100)")
	private String ownerCleanedUpName;

	@Column(name = "flor_ownership_share_extent")
	private Integer ownershipShareExtent;

	@Column(name = "flor_owner_identifier_name_per_ror", columnDefinition = "VARCHAR(100)")
	private String ownerIdentifierNamePerRor;

	@Column(name = "flor_owner_identifier_type_per_ror")
	private Integer ownerIdentifierTypePerRor;

//	@Column(name = "flor_extent_assigned_area", columnDefinition = "DECIMAL(8,5)")
	@Column(name = "flor_extent_assigned_area", columnDefinition = "DOUBLE PRECISION")
	private Double extentAssignedArea;

//	@Column(name = "flor_extent_total_area", columnDefinition = "DECIMAL(8,5)")
	@Column(name = "flor_extent_total_area", columnDefinition = "DOUBLE PRECISION")
	private Double extentTotalArea;

	/*@ManyToOne
	@JoinColumn(name = "flor_ut_unit_type_master_id", referencedColumnName = "ut_unit_type_master_id")
	private StateUnitTypeMaster utUnitTypeMasterId;*/

	@Column(name = "flor_updated_date")
	private Date updatedDate;

	@Column(name = "flor_synchronisation_date")
	private Date synchronisationDate;

	@ManyToOne
	@JoinColumn(name = "flor_ps_plot_status_master_id", referencedColumnName = "ps_plot_status_master_id")
	private PlotStatusMaster plotStatusMasterId;

	@Column(name = "flor_farmer_name_match_score_ror")
	private Integer farmerNameMatchScoreRor;

	@Column(name = "flor_main_owner_no_as_per_ror")
	private Integer mainOwnerNoAsPerRor;

	@Column(name = "flor_owner_type")
	private Integer ownerType;

	@Column(name = "flor_ownership_share_type")
	private Integer ownershipShareType;
	
	@Column(name = "flor_main_owner_name", columnDefinition = "VARCHAR(100)")
	private String mainOwnerName;




	@Column(name = "owner_unique_code", columnDefinition = "VARCHAR(100)")
	private String ownerUniqueCode;

	public Long getFarmerLandOwnershipRegistryId() {
		return farmerLandOwnershipRegistryId;
	}

	public void setFarmerLandOwnershipRegistryId(Long farmerLandOwnershipRegistryId) {
		this.farmerLandOwnershipRegistryId = farmerLandOwnershipRegistryId;
	}

	public FarmerRegistry getFarmerRegistryId() {
		return null;
	}

	public void setFarmerRegistryId(FarmerRegistry farmerRegistryId) {
		this.farmerRegistryId = farmerRegistryId;
	}

	public FarmlandPlotRegistry getFarmlandPlotRegistryId() {
		return farmlandPlotRegistryId;
	}

	public void setFarmlandPlotRegistryId(FarmlandPlotRegistry farmlandPlotRegistryId) {
		this.farmlandPlotRegistryId = farmlandPlotRegistryId;
	}

	public String getOwnerNoAsPerRor() {
		return ownerNoAsPerRor;
	}

	public void setOwnerNoAsPerRor(String ownerNoAsPerRor) {
		this.ownerNoAsPerRor = ownerNoAsPerRor;
	}

	public String getOwnerNamePerRor() {
		return ownerNamePerRor;
	}

	public void setOwnerNamePerRor(String ownerNamePerRor) {
		this.ownerNamePerRor = ownerNamePerRor;
	}

	public String getOwnerCleanedUpName() {
		return ownerCleanedUpName;
	}

	public void setOwnerCleanedUpName(String ownerCleanedUpName) {
		this.ownerCleanedUpName = ownerCleanedUpName;
	}

	public Integer getOwnershipShareExtent() {
		return ownershipShareExtent;
	}

	public void setOwnershipShareExtent(Integer ownershipShareExtent) {
		this.ownershipShareExtent = ownershipShareExtent;
	}

	public String getOwnerIdentifierNamePerRor() {
		return ownerIdentifierNamePerRor;
	}

	public void setOwnerIdentifierNamePerRor(String ownerIdentifierNamePerRor) {
		this.ownerIdentifierNamePerRor = ownerIdentifierNamePerRor;
	}

	public Integer getOwnerIdentifierTypePerRor() {
		return ownerIdentifierTypePerRor;
	}

	public void setOwnerIdentifierTypePerRor(Integer ownerIdentifierTypePerRor) {
		this.ownerIdentifierTypePerRor = ownerIdentifierTypePerRor;
	}

	public Double getExtentAssignedArea() {
		return extentAssignedArea;
	}

	public void setExtentAssignedArea(Double extentAssignedArea) {
		this.extentAssignedArea = extentAssignedArea;
	}

	public Double getExtentTotalArea() {
		return extentTotalArea;
	}

	public void setExtentTotalArea(Double extentTotalArea) {
		this.extentTotalArea = extentTotalArea;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getSynchronisationDate() {
		return synchronisationDate;
	}

	public void setSynchronisationDate(Date synchronisationDate) {
		this.synchronisationDate = synchronisationDate;
	}

	public PlotStatusMaster getPlotStatusMasterId() {
		return plotStatusMasterId;
	}

	public void setPlotStatusMasterId(PlotStatusMaster plotStatusMasterId) {
		this.plotStatusMasterId = plotStatusMasterId;
	}

	public Integer getFarmerNameMatchScoreRor() {
		return farmerNameMatchScoreRor;
	}

	public void setFarmerNameMatchScoreRor(Integer farmerNameMatchScoreRor) {
		this.farmerNameMatchScoreRor = farmerNameMatchScoreRor;
	}

	public Integer getMainOwnerNoAsPerRor() {
		return mainOwnerNoAsPerRor;
	}

	public void setMainOwnerNoAsPerRor(Integer mainOwnerNoAsPerRor) {
		this.mainOwnerNoAsPerRor = mainOwnerNoAsPerRor;
	}

	public Integer getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(Integer ownerType) {
		this.ownerType = ownerType;
	}

	public Integer getOwnershipShareType() {
		return ownershipShareType;
	}

	public void setOwnershipShareType(Integer ownershipShareType) {
		this.ownershipShareType = ownershipShareType;
	}

	public String getMainOwnerName() {
		return mainOwnerName;
	}

	public void setMainOwnerName(String mainOwnerName) {
		this.mainOwnerName = mainOwnerName;
	}
	public String getOwnerUniqueCode() {
		return ownerUniqueCode;
	}

	public void setOwnerUniqueCode(String ownerUniqueCode) {
		this.ownerUniqueCode = ownerUniqueCode;
	}
}
