package zombie.core;


public final class Language {
	private final int index;
	private final String name;
	private final String text;
	private final String charset;
	private final String base;
	private final boolean azerty;

	Language(int int1, String string, String string2, String string3, String string4, boolean boolean1) {
		this.index = int1;
		this.name = string;
		this.text = string2;
		this.charset = string3;
		this.base = string4;
		this.azerty = boolean1;
	}

	public int index() {
		return this.index;
	}

	public String name() {
		return this.name;
	}

	public String text() {
		return this.text;
	}

	public String charset() {
		return this.charset;
	}

	public String base() {
		return this.base;
	}

	public boolean isAzerty() {
		return this.azerty;
	}

	public String toString() {
		return this.name;
	}

	public static Language fromIndex(int int1) {
		return Languages.instance.getByIndex(int1);
	}

	public static Language FromString(String string) {
		Language language = Languages.instance.getByName(string);
		if (language == null) {
			language = Languages.instance.getDefaultLanguage();
		}

		return language;
	}
}
