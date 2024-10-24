/**
 *
 */
package com.amnex.agristack.service;

import java.util.regex.Pattern;

/**
 * @author kinnari.soni
 *
 * 5 Apr 2023 6:58:53 pm
 */
public class ValidationService {

	public static String EMAIL_REGEX = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

	public static String LL_REGEX = "^[^s].([^\\x00-\\x0019\\x0021-\\x7F]+s)*[^\\x00-\\x0019\\x0021-\\x7F]+$";

	public static String ALPHA_REGEX = "^[a-zA-Z]*$";

	public static String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9]*$";
	public static String NUMERIC_REGEX = "^[0-9]*$";


	public static int AGE_LIMIT = 130;

	/**
	 * Checks if the given string contains only alphabetic characters.
	 *
	 * @param str The string to be checked.
	 * @return true if the string contains only alphabetic characters, false otherwise.
	 */
	public static boolean isStringOnlyAlphabet(String str)
	{
		return ((!str.equals(""))
				&& (str != null)
				&& (str.matches(ALPHA_REGEX)));
	}

	/**
	 * Validates a local language string based on the LL_REGEX pattern.
	 *
	 * @param ll The local language string to be validated.
	 * @return true if the local language string is valid, false otherwise.
	 */
	public static boolean localLangValidator(String ll) {
		return
				Pattern.compile(LL_REGEX).matcher(ll).matches();
	}

	/**
	 * Validates an email address based on the EMAIL_REGEX pattern.
	 *
	 * @param email The email address to be validated.
	 * @return true if the email address is valid, false otherwise.
	 */
	public static boolean emailValidator(String email) {
		return
				Pattern.compile(EMAIL_REGEX).matcher(email).matches();
	}

	/**
	 * Checks if the given string contains only alphanumeric characters.
	 *
	 * @param str The string to be checked.
	 * @return true if the string contains only alphanumeric characters, false otherwise.
	 */
	public static boolean isAlphaNumeric(String str) {
		return ((!str.equals(""))
				&& (str != null)
				&& (str.matches(ALPHA_NUMERIC_REGEX)));

	}
}
