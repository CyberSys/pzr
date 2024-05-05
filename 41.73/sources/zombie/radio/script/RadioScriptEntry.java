package zombie.radio.script;


public final class RadioScriptEntry {
	private int chanceMin;
	private int chanceMax;
	private String scriptName;
	private int Delay;

	public RadioScriptEntry(String string, int int1) {
		this(string, int1, 0, 100);
	}

	public RadioScriptEntry(String string, int int1, int int2, int int3) {
		this.chanceMin = 0;
		this.chanceMax = 100;
		this.scriptName = "";
		this.Delay = 0;
		this.scriptName = string;
		this.setChanceMin(int2);
		this.setChanceMax(int3);
		this.setDelay(int1);
	}

	public void setChanceMin(int int1) {
		this.chanceMin = int1 < 0 ? 0 : (int1 > 100 ? 100 : int1);
	}

	public int getChanceMin() {
		return this.chanceMin;
	}

	public void setChanceMax(int int1) {
		this.chanceMax = int1 < 0 ? 0 : (int1 > 100 ? 100 : int1);
	}

	public int getChanceMax() {
		return this.chanceMax;
	}

	public String getScriptName() {
		return this.scriptName;
	}

	public void setScriptName(String string) {
		this.scriptName = string;
	}

	public int getDelay() {
		return this.Delay;
	}

	public void setDelay(int int1) {
		this.Delay = int1 >= 0 ? int1 : 0;
	}
}
