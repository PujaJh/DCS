package com.amnex.agristack.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDAO {

	private String token;

	private Integer verificationCode;

	private String newPassword;

	private String confirmPassword;

	private Long userId;

	private String existingPassword;
}
