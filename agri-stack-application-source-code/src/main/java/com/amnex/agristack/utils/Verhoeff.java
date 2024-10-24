package com.amnex.agristack.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @see <a href="http://en.wikipedia.org/wiki/Verhoeff_algorithm">More Info</a>
 * @see <a href="http://en.wikipedia.org/wiki/Dihedral_group">Dihedral Group</a>
 * @see <a href="http://mathworld.wolfram.com/DihedralGroupD5.html">Dihedral
 *      Group Order 10</a>
 * @author Colm Rice
 */
public class Verhoeff {

	// The multiplication table
	static int[][] d = new int[][] {
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
		{ 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
		{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 },
		{ 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
		{ 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
		{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
		{ 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 },
		{ 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
		{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 },
		{ 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 }
	};

	// The permutation table
	static int[][] p = new int[][] {
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
		{ 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
		{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 },
		{ 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
		{ 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
		{ 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
		{ 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 },
		{ 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 }
	};

	// The inverse table
	static int[] inv = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

	/*
	 * For a given number generates a Verhoeff digit
	 *
	 */
	public static String generateVerhoeff(String num) {

		int c = 0;
		int[] myArray = stringToReversedIntArray(num);

		for (int i = 0; i < myArray.length; i++) {
			c = d[c][p[((i + 1) % 8)][myArray[i]]];
		}

		return Integer.toString(inv[c]);
	}

	/*
	 * Validates that an entered number is Verhoeff compliant.
	 * NB: Make sure the check digit is the last one.
	 */
	public static boolean validateVerhoeff(String num) {

		int c = 0;
		int[] myArray = stringToReversedIntArray(num);

		for (int i = 0; i < myArray.length; i++) {
			c = d[c][p[(i % 8)][myArray[i]]];
		}

		return (c == 0);
	}

	/*
	 * Converts a string to a reversed integer array.
	 */
	private static int[] stringToReversedIntArray(String num) {

		int[] myArray = new int[num.length()];

		for (int i = 0; i < num.length(); i++) {
			myArray[i] = Integer.parseInt(num.substring(i, i + 1));
		}

		myArray = reverse(myArray);

		return myArray;

	}

	/*
	 * Reverses an int array
	 */
	private static int[] reverse(int[] myArray) {
		int[] reversed = new int[myArray.length];

		for (int i = 0; i < myArray.length; i++) {
			reversed[i] = myArray[myArray.length - (i + 1)];
		}

		return reversed;
	}

	public static Integer generateLastFiveSequenceNumbers() {
		Random randomNumber = new Random();
		int result = 0;
		for (int i = 0; i < 10; i++) {
			result = result * 10 + (randomNumber.nextInt(9) + 1);
		}
		return result;
	}

	private static final long LIMIT = 10000L;
	private static long last = 0;

	public static long getID() {
		// 10 digits.
		long id = System.currentTimeMillis() / LIMIT;
		if (id <= last) {
			id = (last + 1) % LIMIT;
		}
		return last = id;
	}

	public static String getFarmerUniqueIdWithChecksum() {
		Long id = generate10DigitNumber();
		// Long[] ary = new Long[] { 987654321L, 987654322L,  987654324L};
		// Long id = ary[new Random().nextInt(ary.length)];
		// System.out.println(id);
		String verhoff = generateVerhoeff(String.valueOf(id));
		String finalVerhoff = id + verhoff;
		return finalVerhoff;
	}

	public static Long generate10DigitNumber() {
		long timeSeed = System.nanoTime();
		double randSeed = Math.random() * 1000;
		long midSeed = (long) (timeSeed * randSeed);
		String s = midSeed + "";
		String subStr = s.substring(0, 10);
		Long finalSeed = Long.parseLong(subStr);
		return finalSeed;
	}

	public static String getFarmLandUniqueIdWithChecksum() {
		Long id = generate9DigitNumber();
		// Long[] ary = new Long[] { 987654321L, 987654322L,  987654324L};
		// Long id = ary[new Random().nextInt(ary.length)];
		// System.out.println(id);
		String verhoff = generateVerhoeff(String.valueOf(id));
		String finalVerhoff = id + verhoff;
		return finalVerhoff;
	}

	public static Long generate9DigitNumber() {
		long timeSeed = System.nanoTime();
		double randSeed = Math.random() * 1000;
		long midSeed = (long) (timeSeed * randSeed);
		String s = midSeed + "";
		String subStr = s.substring(0, 9);
		Long finalSeed = Long.parseLong(subStr);
		return finalSeed;
	}
	
	public static boolean validateAadhaarNumber(String UID)
    {

        int c = 0;
        Integer[] myArray = StringToReversedIntArray(UID);

        for (int i = 0; i <= myArray.length - 1; i++)
        {
           // c = d[c, p[(i % 8), myArray[i]]];
			c = d[c][p[(i % 8)][myArray[i].intValue()]];

        }

        return c==0;

    }
	/// <summary>
    /// Converts a string to a reversed integer array.
    /// </summary>
    /// <param name="num"></param>
    /// <returns>Reversed integer array</returns>
    private static Integer[] StringToReversedIntArray(String num)
    {
        Integer[] myArray = new Integer[num.length()];

        for (int i = 0; i < num.length(); i++)
        {
            myArray[i] = Integer.parseInt(num.substring(i, i+1));
        }
        List<Integer>integerList=Arrays.asList(myArray);
        Collections.reverse(integerList);
       
       

        return  integerList.toArray(myArray);

    }

	public static String getFarmLandUniqueIdWithChecksum_v3() {
        String uniqueId = generateUniqueIdentifier();
        String verhoff = generateVerhoeff_v3(uniqueId);
        String finalVerhoff = uniqueId + verhoff;
        return finalVerhoff;
    }
	public static String generateVerhoeff_v3(String num) {
        int c = 0;
        int[] myArray = stringToReversedIntArray(num);

        for (int i = 0; i < myArray.length; i++) {
            c = d[c][p[i % 8][myArray[i]]];
        }

        return Integer.toString(inv[c]);
    }

	public static String generateUniqueIdentifier() {
        // Generate a UUID and take a substring of the digits to form a 9-digit number
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").replaceAll("[^0-9]", "");
        // Ensure the string is at least 9 characters long
        if (uuid.length() < 9) {
            uuid = String.format("%-9s", uuid).replace(' ', '0'); // Pad with zeros if necessary
        }
        String subStr = uuid.substring(0, 9);
        return subStr;
    }
}