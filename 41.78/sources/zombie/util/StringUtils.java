package zombie.util;

import java.util.function.BiFunction;


public class StringUtils {
	public static final String s_emptyString = "";
	public static final char UTF8_BOM = '\ufeff';

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static boolean isNullOrWhitespace(String string) {
		return isNullOrEmpty(string) || isWhitespace(string);
	}

	private static boolean isWhitespace(String string) {
		int int1 = string.length();
		if (int1 <= 0) {
			return false;
		} else {
			int int2 = 0;
			int int3 = int1 / 2;
			for (int int4 = int1 - 1; int2 <= int3; --int4) {
				if (!Character.isWhitespace(string.charAt(int2)) || !Character.isWhitespace(string.charAt(int4))) {
					return false;
				}

				++int2;
			}

			return true;
		}
	}

	public static String discardNullOrWhitespace(String string) {
		return isNullOrWhitespace(string) ? null : string;
	}

	public static String trimPrefix(String string, String string2) {
		return string.startsWith(string2) ? string.substring(string2.length()) : string;
	}

	public static String trimSuffix(String string, String string2) {
		return string.endsWith(string2) ? string.substring(0, string.length() - string2.length()) : string;
	}

	public static boolean equals(String string, String string2) {
		if (string == string2) {
			return true;
		} else {
			return string != null && string.equals(string2);
		}
	}

	public static boolean startsWithIgnoreCase(String string, String string2) {
		return string.regionMatches(true, 0, string2, 0, string2.length());
	}

	public static boolean endsWithIgnoreCase(String string, String string2) {
		int int1 = string2.length();
		return string.regionMatches(true, string.length() - int1, string2, 0, int1);
	}

	public static boolean containsIgnoreCase(String string, String string2) {
		for (int int1 = string.length() - string2.length(); int1 >= 0; --int1) {
			if (string.regionMatches(true, int1, string2, 0, string2.length())) {
				return true;
			}
		}

		return false;
	}

	public static boolean equalsIgnoreCase(String string, String string2) {
		if (string == string2) {
			return true;
		} else {
			return string != null && string.equalsIgnoreCase(string2);
		}
	}

	public static boolean tryParseBoolean(String string) {
		if (isNullOrWhitespace(string)) {
			return false;
		} else {
			String string2 = string.trim();
			return string2.equalsIgnoreCase("true") || string2.equals("1") || string2.equals("1.0");
		}
	}

	public static boolean isBoolean(String string) {
		String string2 = string.trim();
		if (!string2.equalsIgnoreCase("true") && !string2.equals("1") && !string2.equals("1.0")) {
			return string2.equalsIgnoreCase("false") || string2.equals("0") || string2.equals("0.0");
		} else {
			return true;
		}
	}

	public static boolean contains(String[] stringArray, String string, BiFunction biFunction) {
		return indexOf(stringArray, string, biFunction) > -1;
	}

	public static int indexOf(String[] stringArray, String string, BiFunction biFunction) {
		int int1 = -1;
		for (int int2 = 0; int2 < stringArray.length; ++int2) {
			if ((Boolean)biFunction.apply(stringArray[int2], string)) {
				int1 = int2;
				break;
			}
		}

		return int1;
	}

	public static String indent(String string) {
		return indent(string, "", "\t");
	}

	private static String indent(String string, String string2, String string3) {
		String string4 = System.lineSeparator();
		return indent(string, string4, string2, string3);
	}

	private static String indent(String string, String string2, String string3, String string4) {
		if (isNullOrEmpty(string)) {
			return string;
		} else {
			int int1 = string.length();
			StringBuilder stringBuilder = new StringBuilder(int1);
			StringBuilder stringBuilder2 = new StringBuilder(int1);
			int int2 = 0;
			for (int int3 = 0; int3 < int1; ++int3) {
				char char1 = string.charAt(int3);
				switch (char1) {
				case '\n': 
					stringBuilder.append(stringBuilder2);
					stringBuilder.append(string2);
					stringBuilder2.setLength(0);
					++int2;
				
				case '\r': 
					break;
				
				default: 
					if (stringBuilder2.length() == 0) {
						if (int2 == 0) {
							stringBuilder2.append(string3);
						} else {
							stringBuilder2.append(string4);
						}
					}

					stringBuilder2.append(char1);
				
				}
			}

			stringBuilder.append(stringBuilder2);
			stringBuilder2.setLength(0);
			return stringBuilder.toString();
		}
	}

	public static String leftJustify(String string, int int1) {
		if (string == null) {
			return leftJustify("", int1);
		} else {
			int int2 = string.length();
			if (int2 >= int1) {
				return string;
			} else {
				int int3 = int1 - int2;
				char[] charArray = new char[int3];
				for (int int4 = 0; int4 < int3; ++int4) {
					charArray[int4] = ' ';
				}

				String string2 = new String(charArray);
				return string + string2;
			}
		}
	}

	public static String moduleDotType(String string, String string2) {
		if (string2 == null) {
			return null;
		} else {
			return string2.contains(".") ? string2 : string + "." + string2;
		}
	}

	public static String stripBOM(String string) {
		return string != null && string.length() > 0 && string.charAt(0) == '﻿' ? string.substring(1) : string;
	}

	public static boolean containsDoubleDot(String string) {
		if (isNullOrEmpty(string)) {
			return false;
		} else {
			return string.contains("..") || string.contains("\u0000.\u0000.");
		}
	}
}
