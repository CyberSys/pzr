package se.krka.kahlua.integration.expose;


public class MethodParameter {
	private final String name;
	private final String type;
	private final String description;

	public MethodParameter(String string, String string2, String string3) {
		this.name = string;
		this.type = string2;
		this.description = string3;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getDescription() {
		return this.description;
	}
}
