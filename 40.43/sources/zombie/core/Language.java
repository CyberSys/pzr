package zombie.core;



public enum Language {

	EN,
	FR,
	DE,
	ES,
	NO,
	RU,
	PL,
	IT,
	NL,
	AF,
	CS,
	DA,
	PT,
	TR,
	HU,
	KO,
	JP,
	CH,
	CN,
	AR,
	PTBR,
	TH,
	EE,
	index,
	text,
	charset;

	private Language(int int1, String string, String string2) {
		this.index = int1;
		this.text = string;
		this.charset = string2;
	}
	public int index() {
		return this.index;
	}
	public String text() {
		return this.text;
	}
	public String charset() {
		return this.charset;
	}
	public static Language fromIndex(int int1) {
		return ((Language[])Language.class.getEnumConstants())[int1];
	}
	public static Language FromString(String string) {
		try {
			return valueOf(string);
		} catch (Exception exception) {
			return EN;
		}
	}
}
