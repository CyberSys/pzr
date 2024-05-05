package zombie.config;


public class EnumConfigOption extends IntegerConfigOption {

	public EnumConfigOption(String string, int int1, int int2) {
		super(string, 1, int1, int2);
	}

	public String getType() {
		return "enum";
	}

	public int getNumValues() {
		return this.max;
	}
}
