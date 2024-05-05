package zombie.scripting.objects;


public class BaseScriptObject {
	public ScriptModule module = null;

	public void Load(String string, String[] stringArray) {
	}

	public ScriptModule getModule() {
		return this.module;
	}
}
