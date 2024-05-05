package zombie.radio.globals;

import java.util.HashMap;
import java.util.Map;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public final class RadioGlobalsManager {
	private final Map globals = new HashMap();
	private final RadioGlobalInt bufferInt = new RadioGlobalInt("bufferInt", 0);
	private final RadioGlobalString bufferString = new RadioGlobalString("bufferString", "");
	private final RadioGlobalBool bufferBoolean = new RadioGlobalBool("bufferBoolean", false);
	private final RadioGlobalFloat bufferFloat = new RadioGlobalFloat("bufferFloat", 0.0F);
	private static RadioGlobalsManager instance;

	public static RadioGlobalsManager getInstance() {
		if (instance == null) {
			instance = new RadioGlobalsManager();
		}

		return instance;
	}

	private RadioGlobalsManager() {
	}

	public void reset() {
		instance = null;
	}

	public boolean exists(String string) {
		return this.globals.containsKey(string);
	}

	public RadioGlobalType getType(String string) {
		return this.globals.containsKey(string) ? ((RadioGlobal)this.globals.get(string)).getType() : RadioGlobalType.Invalid;
	}

	public String getString(String string) {
		RadioGlobal radioGlobal = this.getGlobal(string);
		return radioGlobal != null ? radioGlobal.getString() : null;
	}

	public boolean addGlobal(String string, RadioGlobal radioGlobal) {
		if (!this.exists(string) && radioGlobal != null) {
			this.globals.put(string, radioGlobal);
			return true;
		} else {
			DebugLog.log(DebugType.Radio, "Error adding global: " + string + " to globals (already exists or global==null)");
			return false;
		}
	}

	public boolean addGlobalString(String string, String string2) {
		return this.addGlobal(string, new RadioGlobalString(string, string2));
	}

	public boolean addGlobalBool(String string, boolean boolean1) {
		return this.addGlobal(string, new RadioGlobalBool(string, boolean1));
	}

	public boolean addGlobalInt(String string, int int1) {
		return this.addGlobal(string, new RadioGlobalInt(string, int1));
	}

	public boolean addGlobalFloat(String string, float float1) {
		return this.addGlobal(string, new RadioGlobalFloat(string, float1));
	}

	public RadioGlobal getGlobal(String string) {
		return this.exists(string) ? (RadioGlobal)this.globals.get(string) : null;
	}

	public RadioGlobalString getGlobalString(String string) {
		RadioGlobal radioGlobal = this.getGlobal(string);
		return radioGlobal != null && radioGlobal instanceof RadioGlobalString ? (RadioGlobalString)radioGlobal : null;
	}

	public RadioGlobalInt getGlobalInt(String string) {
		RadioGlobal radioGlobal = this.getGlobal(string);
		return radioGlobal != null && radioGlobal instanceof RadioGlobalInt ? (RadioGlobalInt)radioGlobal : null;
	}

	public RadioGlobalFloat getGlobalFloat(String string) {
		RadioGlobal radioGlobal = this.getGlobal(string);
		return radioGlobal != null && radioGlobal instanceof RadioGlobalFloat ? (RadioGlobalFloat)radioGlobal : null;
	}

	public RadioGlobalBool getGlobalBool(String string) {
		RadioGlobal radioGlobal = this.getGlobal(string);
		return radioGlobal != null && radioGlobal instanceof RadioGlobalBool ? (RadioGlobalBool)radioGlobal : null;
	}

	public boolean setGlobal(String string, RadioGlobal radioGlobal, EditGlobalOps editGlobalOps) {
		RadioGlobal radioGlobal2 = this.getGlobal(string);
		return radioGlobal2 != null && radioGlobal != null ? radioGlobal2.setValue(radioGlobal, editGlobalOps) : false;
	}

	public boolean setGlobal(String string, String string2) {
		this.bufferString.setValue(string2);
		return this.setGlobal(string, this.bufferString, EditGlobalOps.set);
	}

	public boolean setGlobal(String string, int int1) {
		this.bufferInt.setValue(int1);
		return this.setGlobal(string, this.bufferInt, EditGlobalOps.set);
	}

	public boolean setGlobal(String string, float float1) {
		this.bufferFloat.setValue(float1);
		return this.setGlobal(string, this.bufferFloat, EditGlobalOps.set);
	}

	public boolean setGlobal(String string, boolean boolean1) {
		this.bufferBoolean.setValue(boolean1);
		return this.setGlobal(string, this.bufferBoolean, EditGlobalOps.set);
	}

	public CompareResult compare(RadioGlobal radioGlobal, RadioGlobal radioGlobal2, CompareMethod compareMethod) {
		return radioGlobal != null && radioGlobal2 != null && radioGlobal.getType().equals(radioGlobal2.getType()) ? radioGlobal.compare(radioGlobal2, compareMethod) : CompareResult.Invalid;
	}

	public CompareResult compare(String string, String string2, CompareMethod compareMethod) {
		return this.compare(this.getGlobal(string), this.getGlobal(string2), compareMethod);
	}
}
