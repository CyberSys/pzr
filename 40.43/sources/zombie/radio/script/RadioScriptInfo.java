package zombie.radio.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class RadioScriptInfo {
	private Map onStartSetters = new HashMap();
	private List exitOptions = new ArrayList();

	public RadioScriptEntry getNextScript() {
		RadioScriptEntry radioScriptEntry = null;
		Iterator iterator = this.exitOptions.iterator();
		while (iterator.hasNext()) {
			ExitOptionOld exitOptionOld = (ExitOptionOld)iterator.next();
			if (exitOptionOld != null) {
				radioScriptEntry = exitOptionOld.evaluate();
				if (radioScriptEntry != null) {
					break;
				}
			}
		}

		return radioScriptEntry;
	}

	public void addExitOption(ExitOptionOld exitOptionOld) {
		if (exitOptionOld != null) {
			this.exitOptions.add(exitOptionOld);
		}
	}
}
