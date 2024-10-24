package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SchemeMaster extends BaseEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "scheme_id", columnDefinition="VARCHAR(100)")
    private String schemeId;
    @Column(name = "subscheme_id", columnDefinition="VARCHAR(100)")
    private String subschemeId;
    @Column(name = "scheme_component_id", columnDefinition="VARCHAR(100)")
    private String schemeComponentId;
    @Column(name = "scheme_subcomponent_id", columnDefinition="VARCHAR(100)")
    private String schemeSubcomponentId;
    @Column(name = "date_of_scheme_starting", columnDefinition="Date")
    private Date dateOfSchemeStarting;
    @Column(name = "benefit_type", columnDefinition="VARCHAR(2)")
    private String benefitType;
    @Column(name = "benefit_by_state_or_centre_both", columnDefinition="VARCHAR(2)")
    private String benefitByStateOrCentreBoth;
    @Column(name = "benefit_value", columnDefinition="numeric(10,2)")
    private Double benefitValue;
    @Column(name = "total_benefit_value_planned", columnDefinition="numeric(9,2)")
    private Double totalBenefitValuePlanned;
    @Column(name = "scheme_status", columnDefinition="CHAR(1)")
    private Double schemeStatus;
    @Column(name = "total_benefit_value_disbursed", columnDefinition="numeric(9,2)")
    private String totalBenefitValueDisbursed;
    @Column(name = "scheme_closure_date", columnDefinition="TIMESTAMP")
    private Calendar schemeClosureDate;
    @Column(name = "scheme_name", columnDefinition="VARCHAR(100)")
    private String schemeName;
}
