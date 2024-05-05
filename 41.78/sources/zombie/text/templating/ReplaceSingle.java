package zombie.text.templating;


public class ReplaceSingle implements IReplace {
	private String value = "";

	public ReplaceSingle() {
	}

	public ReplaceSingle(String string) {
		this.value = string;
	}

	protected String getValue() {
		return this.value;
	}

	protected void setValue(String string) {
		this.value = string;
	}

	public String getString() {
		return this.value;
	}
}
