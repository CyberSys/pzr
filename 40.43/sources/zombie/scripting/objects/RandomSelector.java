package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;


public class RandomSelector extends Script {
	public String name;
	public ArrayList scriptsToCall = new ArrayList();

	public void Load(String string, String[] stringArray) {
		this.name = new String(string);
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			this.DoLine(new String(stringArray[int1].trim()));
		}
	}

	private void DoLine(String string) {
		if (!string.isEmpty()) {
			this.scriptsToCall.add(string);
		}
	}

	public String Process() {
		int int1 = Rand.Next(this.scriptsToCall.size());
		if (((String)this.scriptsToCall.get(int1)).contains(".")) {
			ScriptManager.instance.PlayScript((String)this.scriptsToCall.get(int1));
		} else {
			ScriptManager.instance.PlayScript(this.module.name + "." + (String)this.scriptsToCall.get(int1));
		}

		return (String)this.scriptsToCall.get(int1);
	}

	public Script.ScriptInstance Process(Script.ScriptInstance scriptInstance) {
		int int1 = Rand.Next(this.scriptsToCall.size());
		return ((String)this.scriptsToCall.get(int1)).contains(".") ? ScriptManager.instance.PlayScript((String)this.scriptsToCall.get(int1), scriptInstance) : ScriptManager.instance.PlayScript(this.module.name + "." + (String)this.scriptsToCall.get(int1), scriptInstance);
	}
}
