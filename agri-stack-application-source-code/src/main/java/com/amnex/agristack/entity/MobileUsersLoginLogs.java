/**
 * 
 */
package com.amnex.agristack.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author majid.belim
 *
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class MobileUsersLoginLogs {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userLogsId;
	private Long userId;
	@CreationTimestamp
	private Timestamp createdOn;
	String imeiNumber;
	String flag;

}
