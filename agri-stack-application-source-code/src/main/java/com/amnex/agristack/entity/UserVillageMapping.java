package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the mapping of users to villages.
 * It stores information such as the mapping ID, user details, and village LGD code.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVillageMapping extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private UserMaster userMaster;

	@ManyToOne
	@JoinColumn(name = "village_lgd_code", referencedColumnName = "village_lgd_code")
	private VillageLgdMaster villageLgdMaster;

}
