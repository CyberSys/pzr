package zombie.scripting.objects;


public class ScriptFlag extends BaseScriptObject {
	public String name;
	public String value;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.value = stringArray[0].trim();
	}

	public void SetValue(String string) {
		this.value = string;
	}

	public boolean IsValue(String string) {
		return this.value.equals(string);
	}
}
