package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entity class representing a year in the system.
 * It stores information such as the year ID, year value, start year, end year,
 * and a flag indicating whether it is the current year.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class YearMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String yearValue;
    @Column
    private String startYear;
    @Column
    private String endYear;
    
    @Column
    private Boolean isCurrentYear;

    public YearMaster(String yearValue, String startYear, String endYear, Boolean isCurrentYear) {
        this.yearValue = yearValue;
        this.startYear = startYear;
        this.endYear = endYear;
        this.isCurrentYear = isCurrentYear;
    }

        public YearMaster(Long id, String yearValue, String startYear, String endYear, Boolean isCurrentYear) {
        this.id = id;
        this.yearValue = yearValue;
        this.startYear = startYear;
        this.endYear = endYear;
        this.isCurrentYear = isCurrentYear;
    }
}

