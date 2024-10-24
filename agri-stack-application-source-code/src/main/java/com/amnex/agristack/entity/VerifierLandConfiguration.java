/**
 * 
 */
package com.amnex.agristack.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 * 
 * Entity class representing the configuration of land assigned to verifiers.
 * It stores information such as the configuration ID, starting and ending years of the assignment,
 * associated season, village LGD code, state LGD code, verification configuration ID,
 * and the list of land plots assigned to the verifier.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class VerifierLandConfiguration extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long verifierLandConfigurationId;

	@Column
	private Integer startingYear;

	@Column
	private Integer endingYear;

	@OneToOne
	@JoinColumn(name = "season_id")
	private SowingSeason season;
	
//	private Long villageLgdCode;
	@OneToOne
	@JoinColumn(name = "village_lgd_code", referencedColumnName = "village_lgd_code")
	private VillageLgdMaster villageLgdCode;
	@Column(name = "state_lgd_code")
	private Long stateLgdCode;

	@OneToOne
	@JoinColumn(name = "configId")
	private SurveyVerificationConfigurationMaster configId;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "verifier_Land_plot_mapping", joinColumns = @JoinColumn(name = "verifier_Land_Configuration_Id"), inverseJoinColumns = @JoinColumn(name = "fpr_farmland_plot_registry_id"))
	private List<FarmlandPlotRegistry> landPlot;
	
	

	@ElementCollection
	@JoinTable(name = "verifier_Land_plots_mapping")
	private List<Long> landPlots;

}
