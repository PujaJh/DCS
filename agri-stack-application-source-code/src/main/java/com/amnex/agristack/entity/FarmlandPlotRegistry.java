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
import javax.persistence.Transient;

import com.amnex.agristack.dao.FarmLandPlotRegistryDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 *
 */

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(value = { "parcelGeometry" })
@NoArgsConstructor
public class FarmlandPlotRegistry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fpr_farmland_plot_registry_id")
    private Long farmlandPlotRegistryId;

    @Column(name = "fpr_land_parcel_id", columnDefinition = "VARCHAR(15)")
    private String landParcelId;

    @Column(name = "fpr_parcel_geometry", columnDefinition = "GEOMETRY")
    private Geometry parcelGeometry;

    @Column(name = "fpr_land_parcel_ulpin", columnDefinition = "VARCHAR(14)")
    private String landParcelUlpin;

    // @Column(name = "fpr_plot_geometry")
    @Column(name = "fpr_plot_geometry", columnDefinition = "GEOMETRY")

    private Geometry plotGeometry;

    //	@Column(name = "fpr_plot_area", columnDefinition = "DECIMAL(8,5)")
    @Column(name = "fpr_plot_area", columnDefinition = "DOUBLE PRECISION")
    private Double plotArea;

    @ManyToOne
    @JoinColumn(name = "fpr_ut_unit_type_master_id", referencedColumnName = "ut_unit_type_master_id")
    private StateUnitTypeMaster utUnitTypeMasterId;

    @ManyToOne
    @JoinColumn(name = "fpr_ps_plot_status_master_id", referencedColumnName = "ps_plot_status_master_id")
    private PlotStatusMaster psPlotStatusId;

    // @Column(name = "fpr_village_lgd_code")
    @ManyToOne
    @JoinColumn(name = "fpr_village_lgd_code", referencedColumnName = "village_lgd_code")
    private VillageLgdMaster villageLgdMaster;

    @Column(name = "fpr_synchronisation_date")
    private Date synchronisationDate;

    @Column(name = "fpr_update_date")
    private Date updateDate;

    @ManyToOne
    @JoinColumn(name = "fpr_lt_location_type_master_id", referencedColumnName = "lt_location_type_master_id")
    private LocationTypeMaster locationTypeMasterId;

    @Column(name = "fpr_ulb_code")
    private Integer ulbCode;

    @Column(name = "fpr_ward_code")
    private Integer wardCode;

    ////// SYSTEM GENERATED CODE WITH STATE CODE EX: KAXXXXXXXX
    @Column(name = "fpr_farmland_id", columnDefinition = "VARCHAR(50)")
    private String farmlandId;

    @Column(name = "fpr_circle_code")
    private Integer circleCode;

    @Column(name = "fpr_mauja_code")
    private Integer maujaCode;

    @Column(name = "fpr_land_type", columnDefinition = "VARCHAR(15)")
    private String landType;

    @Column(name = "fpr_survey_number")
    private String surveyNumber;

    @Column(name = "fpr_sub_survey_number")
    private String subSurveyNumber;

    @Column(name = "fpr_unique_code")
    private String uniqueCode;

    @Column(name = "fpr_land_usage_type")
    private String landUsageType;

    @Column(name = "fpr_ownership_type")
    private String landOwnershipType;

//    @Column(name = "season_name")
//    private String seasonName;

    @Column(name = "season_id")
    private Long seasonId;

    @Column(name = "year")
    private Integer year;

    @Transient
    //@Column(name = "season")
    private SowingSeason season;

    @Transient
    private Boolean isNAPlot;
    @Transient
    private String parcelGeoms;

    public FarmlandPlotRegistry(FarmLandPlotRegistryDto farmLandPlotRegistryDto) {

        if (farmLandPlotRegistryDto.getFarmLandPlotRegisterId() != null) {
            this.farmlandPlotRegistryId = farmLandPlotRegistryDto.getFarmLandPlotRegisterId();
        }
        if (farmLandPlotRegistryDto.getLocationTypeMasterId() != null) {
            this.locationTypeMasterId = farmLandPlotRegistryDto.getLocationTypeMasterId();
        }
        if (farmLandPlotRegistryDto.getMaujaCode() != null) {
            this.maujaCode = farmLandPlotRegistryDto.getMaujaCode();
        }
        // if (farmLandPlotRegistryDto.getUtUnitTypeMasterId() != null) {
        // this.utUnitTypeMasterId = farmLandPlotRegistryDto.getUtUnitTypeMasterId();
        // }
        if (farmLandPlotRegistryDto.getLandParcelId() != null) {
            this.landParcelId = farmLandPlotRegistryDto.getLandParcelId();
        }
        if (farmLandPlotRegistryDto.getParcelGeometry() != null) {
            // this.parcelGeometry = farmLandPlotRegistryDto.getParcelGeometry();
        }
        if (farmLandPlotRegistryDto.getPlotGeometry() != null) {
            // this.plotGeometry = farmLandPlotRegistryDto.getPlotGeometry();
        }

        if (farmLandPlotRegistryDto.getLandParcelUlpin() != null) {
            this.landParcelUlpin = farmLandPlotRegistryDto.getLandParcelUlpin();
        }

        if (farmLandPlotRegistryDto.getPlotArea() != null) {
            this.plotArea = farmLandPlotRegistryDto.getPlotArea();
        }
        // if (farmLandPlotRegistryDto.getUtUnitTypeMasterId() != null) {
        // this.utUnitTypeMasterId = farmLandPlotRegistryDto.getUtUnitTypeMasterId();
        // }
        if (farmLandPlotRegistryDto.getPsPlotStatusId() != null) {
            this.psPlotStatusId = farmLandPlotRegistryDto.getPsPlotStatusId();
        }
        if (farmLandPlotRegistryDto.getVillage() != null) {
            this.villageLgdMaster = farmLandPlotRegistryDto.getVillage();
        }
        /*
         * if (farmLandPlotRegistryDto.getSynchronisationDate() != null) {
         * this.synchronisationDate = farmLandPlotRegistryDto.getSynchronisationDate();
         * } if (farmLandPlotRegistryDto.getUpdateDate() != null) { this.updateDate =
         * farmLandPlotRegistryDto.getUpdateDate(); }
         */
        if (farmLandPlotRegistryDto.getLocationTypeMasterId() != null) {
            this.locationTypeMasterId = farmLandPlotRegistryDto.getLocationTypeMasterId();
        }
        if (farmLandPlotRegistryDto.getUlbCode() != null) {
            this.ulbCode = farmLandPlotRegistryDto.getUlbCode();
        }
        if (farmLandPlotRegistryDto.getWardCode() != null) {
            this.wardCode = farmLandPlotRegistryDto.getWardCode();
        }
        if (farmLandPlotRegistryDto.getFarmlandId() != null) {
            this.farmlandId = farmLandPlotRegistryDto.getFarmlandId();
        }
        if (farmLandPlotRegistryDto.getCircleCode() != null) {
            this.circleCode = farmLandPlotRegistryDto.getCircleCode();
        }
        if (farmLandPlotRegistryDto.getMaujaCode() != null) {
            this.maujaCode = farmLandPlotRegistryDto.getMaujaCode();
        }
        if (farmLandPlotRegistryDto.getLandType() != null) {
            this.landType = farmLandPlotRegistryDto.getLandType();
        }

        if (farmLandPlotRegistryDto.getSurveyNumber() != null) {
            this.surveyNumber = farmLandPlotRegistryDto.getSurveyNumber();
        }

//        if (farmLandPlotRegistryDto.getSeasonName() != null) {
//            this.seasonName = farmLandPlotRegistryDto.getSeasonName();
//        }

        if(farmLandPlotRegistryDto.getYear() != null)
        {
            this.year = farmLandPlotRegistryDto.getYear();
        }
        if(farmLandPlotRegistryDto.getSeasonId() != null){
            this.seasonId = farmLandPlotRegistryDto.getSeasonId();
        }

    }

}
