package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author krupali.jogi
 *
 * Table containing crop category details
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class CropCategoryMaster extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crop_category_id")
	private Long cropCategoryId;
	@Column(name = "crop_category_name", columnDefinition = "VARCHAR(25)")
	private String cropCategoryName;
	@Column(name = "description", columnDefinition = "VARCHAR(250)")
	private String description;
}
