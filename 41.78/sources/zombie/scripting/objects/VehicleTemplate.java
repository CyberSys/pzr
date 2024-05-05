package zombie.scripting.objects;

import zombie.scripting.ScriptManager;


public final class VehicleTemplate extends BaseScriptObject {
	public String name;
	public String body;
	public VehicleScript script;

	public VehicleTemplate(ScriptModule scriptModule, String string, String string2) {
		ScriptManager scriptManager = ScriptManager.instance;
		if (!scriptManager.scriptsWithVehicleTemplates.contains(scriptManager.currentFileName)) {
			scriptManager.scriptsWithVehicleTemplates.add(scriptManager.currentFileName);
		}

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
