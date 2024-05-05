package zombie.radio.devices;


public final class PresetEntry {
	public String name = "New preset";
	public int frequency = 93200;

	public PresetEntry() {
	}

	public PresetEntry(String string, int int1) {
		this.name = string;
		this.frequency = int1;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int int1) {
		this.frequency = int1;
	}
}
