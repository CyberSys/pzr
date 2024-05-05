package zombie.util.util;


public class Display {
	private static final String displayChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#\u00a4%&/()=?\'@\u00a3${[]}+|^~*-_.:,;<>\\";

	public static String display(int int1) {
		return String.valueOf(int1);
	}

	static String hexChar(char char1) {
		String string = Integer.toHexString(char1);
		switch (string.length()) {
		case 1: 
			return "\\u000" + string;
		
		case 2: 
			return "\\u00" + string;
		
		case 3: 
			return "\\u0" + string;
		
		case 4: 
			return "\\u" + string;
		
		default: 
			throw new RuntimeException("Internal error");
		
		}
	}
}
