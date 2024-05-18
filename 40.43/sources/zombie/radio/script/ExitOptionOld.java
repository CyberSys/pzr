package zombie.radio.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.globals.CompareResult;


public class ExitOptionOld {
	private String parentScript;
	private String name;
	private ConditionContainer condition;
	private List scriptEntries = new ArrayList();

	public ExitOptionOld(String string, String string2) {
		this.parentScript = string != null ? string : "Noname";
		this.name = string2 != null ? string2 : "Noname";
	}

	public void setCondition(ConditionContainer conditionContainer) {
		this.condition = conditionContainer;
	}

	public void addScriptEntry(RadioScriptEntry radioScriptEntry) {
		if (radioScriptEntry != null) {
			this.scriptEntries.add(radioScriptEntry);
		} else {
			DebugLog.log(DebugType.Radio, "Error trying to add \'null\' scriptentry in script: " + this.parentScript + ", exitoption: " + this.name);
		}
	}

	public RadioScriptEntry evaluate() {
		CompareResult compareResult = CompareResult.True;
		if (this.condition != null) {
			compareResult = this.condition.Evaluate();
		}

		if (compareResult.equals(CompareResult.True)) {
			if (this.scriptEntries != null && this.scriptEntries.size() > 0) {
				int int1 = Rand.Next(100);
				Iterator iterator = this.scriptEntries.iterator();
				while (iterator.hasNext()) {
					RadioScriptEntry radioScriptEntry = (RadioScriptEntry)iterator.next();
					if (radioScriptEntry != null) {
						System.out.println("ScriptEntry " + radioScriptEntry.getScriptName());
						System.out.println("Chance: " + int1 + " Min: " + radioScriptEntry.getChanceMin() + " Max: " + radioScriptEntry.getChanceMax());
						if (int1 >= radioScriptEntry.getChanceMin() && int1 < radioScriptEntry.getChanceMax()) {
							return radioScriptEntry;
						}
					}
				}
			}
		} else if (compareResult.equals(CompareResult.Invalid)) {
			System.out.println("Error occured evaluating condition: " + this.parentScript + ", exitoption: " + this.name);
			DebugLog.log(DebugType.Radio, "Error occured evaluating condition: " + this.parentScript + ", exitoption: " + this.name);
		}

		return null;
	}
}
