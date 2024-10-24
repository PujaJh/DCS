package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "nic_owner_log")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class NicOwnerLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "village_lgd_code")
	private Long villageLgdCode;
	@Column
	private Boolean isUploaded;
	
	@Column
	private Boolean isActive;

}
