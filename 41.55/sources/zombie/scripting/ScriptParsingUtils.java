package zombie.scripting;

import java.util.Stack;


public final class ScriptParsingUtils {

	public static String[] SplitExceptInbetween(String string, String string2, String string3) {
		Stack stack = new Stack();
		boolean boolean1 = false;
		int int1;
		while (string.contains(string2)) {
			int int2;
			if (!boolean1) {
				int int3 = string.indexOf(string2);
				int1 = string.indexOf(string3);
				String[] stringArray;
				if (int1 == -1) {
					stringArray = string.split(string2);
					for (int2 = 0; int2 < stringArray.length; ++int2) {
						stack.add(stringArray[int2].trim());
					}

					stringArray = new String[stack.size()];
					for (int2 = 0; int2 < stack.size(); ++int2) {
						stringArray[int2] = (String)stack.get(int2);
					}

					return stringArray;
				}

				if (int3 == -1) {
					stringArray = new String[stack.size()];
					if (!string.trim().isEmpty()) {
						stack.add(string.trim());
					}

					for (int2 = 0; int2 < stack.size(); ++int2) {
						stringArray[int2] = (String)stack.get(int2);
					}

					return stringArray;
				}

				if (int3 < int1) {
					stack.add(string.substring(0, int3));
					string = string.substring(int3 + 1);
				} else {
					boolean1 = true;
				}
			} else {
				string.indexOf(string3);
				string.indexOf(string3);
				int int4 = string.indexOf(string3, string.indexOf(string3) + 1);
				int2 = string.indexOf(string2, int4 + 1);
				if (int2 == -1) {
					break;
				}

				String string4 = string.substring(0, int2).trim();
				if (!string4.isEmpty()) {
					stack.add(string4);
				}

				string = string.substring(int2 + 1);
				boolean1 = false;
			}
		}

		if (!string.trim().isEmpty()) {
			stack.add(string.trim());
		}

		String[] stringArray2 = new String[stack.size()];
		for (int1 = 0; int1 < stack.size(); ++int1) {
			stringArray2[int1] = (String)stack.get(int1);
		}

		return stringArray2;
	}

	public static String[] SplitExceptInbetween(String string, String string2, String string3, String string4) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		int int1 = 0;
		int int2 = 0;
		int int3 = 0;
		int int4 = 0;
		Stack stack = new Stack();
		if (string.indexOf(string3, int2) == -1) {
			return string.split(string2);
		} else {
			do {
				int2 = string.indexOf(string3, int2 + 1);
				int3 = string.indexOf(string4, int3 + 1);
				int4 = string.indexOf(string2, int4 + 1);
				if (int4 == -1) {
					stack.add(string.trim());
					string = "";
				} else if ((int4 < int2 || int2 == -1 && int4 != -1) && int1 == 0) {
					stack.add(string.substring(0, int4));
					string = string.substring(int4 + 1);
					int2 = 0;
					int3 = 0;
					int4 = 0;
				} else if ((int3 >= int2 || int3 == -1) && int2 != -1) {
					if (int2 != -1 && int3 == -1) {
						int3 = int2;
						++int1;
					} else if (int2 != -1 && int3 != -1 && int2 < int3 && (int2 > int4 || int3 < int4)) {
						stack.add(string.substring(0, int4));
						string = string.substring(int4 + 1);
						int2 = 0;
						int3 = 0;
						int4 = 0;
					}
				} else {
					int2 = int3;
					--int1;
					if (int1 == 0) {
						stack.add(string.substring(0, int3 + 1));
						string = string.substring(int3 + 1);
						int2 = 0;
						int3 = 0;
						int4 = 0;
					}
				}
			}	 while (string.trim().length() > 0);

			if (!string.trim().isEmpty()) {
				stack.add(string.trim());
			}

			String[] stringArray = new String[stack.size()];
			for (int int5 = 0; int5 < stack.size(); ++int5) {
				stringArray[int5] = ((String)stack.get(int5)).trim();
			}

			return stringArray;
		}
	}
}
