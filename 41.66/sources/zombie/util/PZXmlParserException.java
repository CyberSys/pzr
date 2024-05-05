package zombie.util;


public final class PZXmlParserException extends Exception {

	public PZXmlParserException() {
	}

	public PZXmlParserException(String string) {
		super(string);
	}

	public PZXmlParserException(String string, Throwable throwable) {
		super(string, throwable);
	}

	public PZXmlParserException(Throwable throwable) {
		super(throwable);
	}

	public String toString() {
		String string = super.toString();
		String string2 = string;
		Throwable throwable = this.getCause();
		if (throwable != null) {
			string2 = string + System.lineSeparator() + "  Caused by:" + System.lineSeparator() + "	" + throwable.toString();
		}

		return string2;
	}
}
