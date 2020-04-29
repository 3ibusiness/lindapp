package com.androidcorpo.lindapp.elipticurve;

import java.math.BigInteger;

public class BinaryConversions {

	// converts a character to its binary equivalent on 8 bits
	public static String CharactToBinary(char charact) {
		String t = "", a = DecToBinary(new BigInteger("" + (int) charact));

		if (a.length() < 8) {
			for (int i = 0; i < 8 - a.length(); i++)
				t += "0";
			a = t + a;
		}
		return a;
	}

	// converts a decimal number to its binary equivalent
	public static String DecToBinary(BigInteger x) {

		String temp = "", result = "";
		if (x.compareTo(new BigInteger("2")) < 0)
			return "" + x;
		BigInteger x2 = x;
		while (x2.compareTo(new BigInteger("2")) >= 0) {
			temp += x2.mod(new BigInteger("2"));
			x2 = x2.divide(new BigInteger("2"));
		}
		temp += x2.mod(new BigInteger("2"));

		for (int i = temp.length() - 1; i >= 0; i--)
			result += temp.charAt(i);

		return result;
	}

	// converts a binary number to its decimal equivalent
	public static BigInteger BinaryToDec(int[] BinNumb) {
		BigInteger decEquiv = new BigInteger("0");
		int j = 0;
		BigInteger po = new BigInteger("1");

		for (int i = BinNumb.length - 1; i >= 0; i--) {
			po = new BigInteger("1");
			if (i != BinNumb.length - 1) {

				for (int k = 1; k <= j; k++)
					po = po.multiply(new BigInteger("2"));

			}
			decEquiv = decEquiv.add(new BigInteger("" + BinNumb[i]).multiply(po));

			j++;

		}
		return decEquiv;

	}

	// converts a string to its binary equivalent
	public static String StringToBinary(String str) {
		String Result = "";
		for (int i = 0; i < str.length(); i++) {
			Result += CharactToBinary(str.charAt(i));
		}
		return Result;
	}

	public static String hexToBin(String s) {
		return new BigInteger(s, 16).toString(2);
	}

	public static String binToHex(String binaryStr) {

		int digitNumber = 1;
		int sum = 0;
		String binary = binaryStr;
		String result = "";
		for (int i = 0; i < binary.length(); i++) {
			if (digitNumber == 1)
				sum += Integer.parseInt(binary.charAt(i) + "") * 8;
			else if (digitNumber == 2)
				sum += Integer.parseInt(binary.charAt(i) + "") * 4;
			else if (digitNumber == 3)
				sum += Integer.parseInt(binary.charAt(i) + "") * 2;
			else if (digitNumber == 4 || i < binary.length() + 1) {
				sum += Integer.parseInt(binary.charAt(i) + "") * 1;
				digitNumber = 0;
				if (sum < 10)
					result += sum;
				else if (sum == 10)
					result += "A";
				else if (sum == 11)
					result += "B";
				else if (sum == 12)
					result += "C";
				else if (sum == 13)
					result += "D";
				else if (sum == 14)
					result += "E";
				else if (sum == 15)
					result += "F";
				sum = 0;
			}
			digitNumber++;
		}
		return result;
	}

	public static boolean isHexNumber (String hexString) {
     return  hexString.matches("[0-9A-F]+");
	}
}