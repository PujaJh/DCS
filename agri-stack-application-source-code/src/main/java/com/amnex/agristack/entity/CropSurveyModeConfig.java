package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CropSurveyModeConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private Long villageLgdCode;

    @Column
    private Long subDistrictLgdCode;

    @Column
    private Long districtLgdCode;

    @Column
    private Long stateLgdCode;

    @OneToOne
    @JoinColumn(name = "mode", referencedColumnName = "id")
    private CropSurveyModeMaster mode;
}
