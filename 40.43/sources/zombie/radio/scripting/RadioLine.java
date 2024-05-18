package zombie.radio.scripting;


public class RadioLine {
	private float r;
	private float g;
	private float b;
	private String text;
	private String effects;

	public RadioLine(String string, float float1, float float2, float float3) {
		this(string, float1, float2, float3, (String)null);
	}

	public RadioLine(String string, float float1, float float2, float float3, String string2) {
		this.r = 1.0F;
		this.g = 1.0F;
		this.b = 1.0F;
		this.text = "<!text missing!>";
		this.effects = "";
		this.text = string != null ? string : this.text;
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.effects = string2 != null ? string2 : this.effects;
	}

	public float getR() {
		return this.r;
	}

	public float getG() {
		return this.g;
	}

	public float getB() {
		return this.b;
	}

	public String getText() {
		return this.text;
	}

	public String getEffectsString() {
		return this.effects;
	}
}
