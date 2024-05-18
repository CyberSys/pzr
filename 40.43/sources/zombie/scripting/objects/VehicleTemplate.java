package zombie.scripting.objects;


public class VehicleTemplate extends BaseScriptObject {
	public String name;
	public String body;
	public VehicleScript script;

	public VehicleTemplate(ScriptModule scriptModule, String string, String string2) {
		this.module = scriptModule;
		this.name = string;
		this.body = string2;
	}

	public VehicleScript getScript() {
		if (this.script == null) {
			this.script = new VehicleScript();
			this.script.module = this.getModule();
			this.script.Load(this.name, this.body);
		}

		return this.script;
	}
}
