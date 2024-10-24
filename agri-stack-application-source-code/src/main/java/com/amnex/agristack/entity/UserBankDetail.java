package com.amnex.agristack.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 * Entity class representing user bank details with user allocation.
 * Each user's bank information includes bank name, branch code, IFSC code, and account number.
 * It also contains associations with user master and bank master entities.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
@NoArgsConstructor
public class UserBankDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userBankDetailId;

	@Column(columnDefinition = "VARCHAR(150)")
	private String userBankName;

	@Column(columnDefinition = "VARCHAR(25)")
	private String userBranchCode;

	@Column(columnDefinition = "VARCHAR(25)")
	private String userIfscCode;

	@Column(columnDefinition = "VARCHAR(18)")
	private String userBankAccountNumber;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userId")
	private UserMaster userId;

	@OneToOne
	@JoinColumn(name = "bank_id", referencedColumnName = "bank_id")
	private BankMaster bankId;

	@Transient
	private Long userBankId;
	public UserBankDetail(String userBankName, String userBranchCode, String userIfscCode, String userBankAccountNumber, Long bankId) {
		this.userBankName = userBankName;
		this.userBranchCode = userBranchCode;
		this.userIfscCode = userIfscCode;
		this.userBankAccountNumber  = userBankAccountNumber;
		this.userBankId = bankId;

	}
	
	public UserBankDetail(Long userBankDetailId,String userBankName, String userBranchCode, String userIfscCode, String userBankAccountNumber,Long userBankId) {
		this.userBankDetailId = userBankDetailId;
		this.userBankName = userBankName;
		this.userBranchCode = userBranchCode;
		this.userIfscCode = userIfscCode;
		this.userBankAccountNumber  = userBankAccountNumber;
		this.userBankId=userBankId;

	}

}
