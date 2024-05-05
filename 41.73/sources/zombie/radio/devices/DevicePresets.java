package zombie.radio.devices;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaManager;


public final class DevicePresets implements Cloneable {
	protected int maxPresets = 10;
	protected ArrayList presets = new ArrayList();

	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public KahluaTable getPresetsLua() {
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		for (int int1 = 0; int1 < this.presets.size(); ++int1) {
			PresetEntry presetEntry = (PresetEntry)this.presets.get(int1);
			KahluaTable kahluaTable2 = LuaManager.platform.newTable();
			kahluaTable2.rawset("name", presetEntry.name);
			kahluaTable2.rawset("frequency", presetEntry.frequency);
			kahluaTable.rawset(int1, kahluaTable2);
		}

		return kahluaTable;
	}

	public ArrayList getPresets() {
		return this.presets;
	}

	public void setPresets(ArrayList arrayList) {
		this.presets = arrayList;
	}

	public int getMaxPresets() {
		return this.maxPresets;
	}

	public void setMaxPresets(int int1) {
		this.maxPresets = int1;
	}

	public void addPreset(String string, int int1) {
		if (this.presets.size() < this.maxPresets) {
			this.presets.add(new PresetEntry(string, int1));
		}
	}

	public void removePreset(int int1) {
		if (this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size()) {
			this.presets.remove(int1);
		}
	}

	public String getPresetName(int int1) {
		return this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size() ? ((PresetEntry)this.presets.get(int1)).name : "";
	}

	public int getPresetFreq(int int1) {
		return this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size() ? ((PresetEntry)this.presets.get(int1)).frequency : -1;
	}

	public void setPresetName(int int1, String string) {
		if (string == null) {
			string = "name-is-null";
		}

		if (this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size()) {
			PresetEntry presetEntry = (PresetEntry)this.presets.get(int1);
			presetEntry.name = string;
		}
	}

	public void setPresetFreq(int int1, int int2) {
		if (this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size()) {
			PresetEntry presetEntry = (PresetEntry)this.presets.get(int1);
			presetEntry.frequency = int2;
		}
	}

	public void setPreset(int int1, String string, int int2) {
		if (string == null) {
			string = "name-is-null";
		}

		if (this.presets.size() != 0 && int1 >= 0 && int1 < this.presets.size()) {
			PresetEntry presetEntry = (PresetEntry)this.presets.get(int1);
			presetEntry.name = string;
			presetEntry.frequency = int2;
		}
	}

	public void clearPresets() {
		this.presets.clear();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.putInt(this.maxPresets);
		byteBuffer.putInt(this.presets.size());
		for (int int1 = 0; int1 < this.presets.size(); ++int1) {
			PresetEntry presetEntry = (PresetEntry)this.presets.get(int1);
			GameWindow.WriteString(byteBuffer, presetEntry.name);
			byteBuffer.putInt(presetEntry.frequency);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		if (int1 >= 69) {
			this.clearPresets();
			this.maxPresets = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				String string = GameWindow.ReadString(byteBuffer);
				int int4 = byteBuffer.getInt();
				if (this.presets.size() < this.maxPresets) {
					this.presets.add(new PresetEntry(string, int4));
				}
			}
		}
	}
}
