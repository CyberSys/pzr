package zombie.util;

import java.util.HashMap;


public class SharedStrings {
	private HashMap strings = new HashMap();

	public String get(String string) {
		String string2 = (String)this.strings.get(string);
		if (string2 == null) {
			this.strings.put(string, string);
			string2 = string;
		}

		return string2;
	}

	public void clear() {
		this.strings.clear();
	}
}
