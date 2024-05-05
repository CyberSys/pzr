package zombie.profanity;

import java.util.regex.Matcher;


public class Phonizer {
	private String name;
	private String regex;

	public Phonizer(String string, String string2) {
		this.name = string;
		this.regex = string2;
	}

	public String getName() {
		return this.name;
	}

	public String getRegex() {
		return this.regex;
	}

	public void execute(Matcher matcher, StringBuffer stringBuffer) {
		if (matcher.group(this.name) != null) {
			matcher.appendReplacement(stringBuffer, "${" + this.name + "}");
		}
	}
}
