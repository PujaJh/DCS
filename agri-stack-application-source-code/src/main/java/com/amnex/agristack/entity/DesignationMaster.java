package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
/**
 * @author krupali.jogi
 *
 * Table containing Designation details
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DesignationMaster extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id")
    private Long designationId;

    @Column(name = "designationName", columnDefinition = "VARCHAR(100)")
    private String designationName;

    public DesignationMaster(Long id, String name) {
        this.designationId = id;
        this.designationName = name;
    }
}
