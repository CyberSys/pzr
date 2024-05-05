package zombie.world;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import zombie.GameWindow;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.scripting.objects.Item;


public class ItemInfo {
	protected String itemName;
	protected String moduleName;
	protected String fullType;
	protected short registryID;
	protected boolean existsAsVanilla = false;
	protected boolean isModded = false;
	protected String modID;
	protected boolean obsolete = false;
	protected boolean removed = false;
	protected boolean isLoaded = false;
	protected List modOverrides;
	protected Item scriptItem;

	public String getFullType() {
		return this.fullType;
	}

	public short getRegistryID() {
		return this.registryID;
	}

	public boolean isExistsAsVanilla() {
		return this.existsAsVanilla;
	}

	public boolean isModded() {
		return this.isModded;
	}

	public String getModID() {
		return this.modID;
	}

	public boolean isObsolete() {
		return this.obsolete;
	}

	public boolean isRemoved() {
		return this.removed;
	}

	public boolean isLoaded() {
		return this.isLoaded;
	}

	public Item getScriptItem() {
		return this.scriptItem;
	}

	public List getModOverrides() {
		return this.modOverrides;
	}

	public ItemInfo copy() {
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.fullType = this.fullType;
		itemInfo.registryID = this.registryID;
		itemInfo.existsAsVanilla = this.existsAsVanilla;
		itemInfo.isModded = this.isModded;
		itemInfo.modID = this.modID;
		itemInfo.obsolete = this.obsolete;
		itemInfo.removed = this.removed;
		itemInfo.isLoaded = this.isLoaded;
		itemInfo.scriptItem = this.scriptItem;
		if (this.modOverrides != null) {
			itemInfo.modOverrides = new ArrayList();
			itemInfo.modOverrides.addAll(this.modOverrides);
		}

		return itemInfo;
	}

	public boolean isValid() {
		return !this.obsolete && !this.removed && this.isLoaded;
	}

	public void DebugPrint() {
		DebugLog.log(this.GetDebugString());
	}

	public String GetDebugString() {
		short short1 = this.registryID;
		String string = "=== Dictionary Item Debug Print ===\nregistryID = " + short1 + ",\nfulltype = \"" + this.fullType + "\",\nmodID = \"" + this.modID + "\",\nexistsAsVanilla = " + this.existsAsVanilla + ",\nisModded = " + this.isModded + ",\nobsolete = " + this.obsolete + ",\nremoved = " + this.removed + ",\nisModdedOverride = " + (this.modOverrides != null ? this.modOverrides.size() : 0) + ",\n";
		if (this.modOverrides != null) {
			string = string + "modOverrides = { ";
			if (this.existsAsVanilla) {
				string = string + "PZ-Vanilla, ";
			}

			for (int int1 = 0; int1 < this.modOverrides.size(); ++int1) {
				string = string + "\"" + (String)this.modOverrides.get(int1) + "\"";
				if (int1 < this.modOverrides.size() - 1) {
					string = string + ", ";
				}
			}

			string = string + " },\n";
		}

		string = "===================================\n";
		return string;
	}

	public String ToString() {
		short short1 = this.registryID;
		return "registryID = " + short1 + ",fulltype = \"" + this.fullType + "\",modID = \"" + this.modID + "\",existsAsVanilla = " + this.existsAsVanilla + ",isModded = " + this.isModded + ",obsolete = " + this.obsolete + ",removed = " + this.removed + ",modOverrides = " + (this.modOverrides != null ? this.modOverrides.size() : 0) + ",";
	}

	protected void saveAsText(FileWriter fileWriter, String string) throws IOException {
		fileWriter.write(string + "registryID = " + this.registryID + "," + System.lineSeparator());
		fileWriter.write(string + "fulltype = \"" + this.fullType + "\"," + System.lineSeparator());
		fileWriter.write(string + "modID = \"" + this.modID + "\"," + System.lineSeparator());
		fileWriter.write(string + "existsAsVanilla = " + this.existsAsVanilla + "," + System.lineSeparator());
		fileWriter.write(string + "isModded = " + this.isModded + "," + System.lineSeparator());
		fileWriter.write(string + "obsolete = " + this.obsolete + "," + System.lineSeparator());
		fileWriter.write(string + "removed = " + this.removed + "," + System.lineSeparator());
		if (this.modOverrides != null) {
			String string2 = "modOverrides = { ";
			for (int int1 = 0; int1 < this.modOverrides.size(); ++int1) {
				string2 = string2 + "\"" + (String)this.modOverrides.get(int1) + "\"";
				if (int1 < this.modOverrides.size() - 1) {
					string2 = string2 + ", ";
				}
			}

			string2 = string2 + " },";
			fileWriter.write(string + string2 + System.lineSeparator());
		}
	}

	protected void save(ByteBuffer byteBuffer, List list, List list2) {
		byteBuffer.putShort(this.registryID);
		if (list2.size() > 127) {
			byteBuffer.putShort((short)list2.indexOf(this.moduleName));
		} else {
			byteBuffer.put((byte)list2.indexOf(this.moduleName));
		}

		GameWindow.WriteString(byteBuffer, this.itemName);
		byte byte1 = 0;
		int int1 = byteBuffer.position();
		byteBuffer.put((byte)0);
		if (this.isModded) {
			byte1 = Bits.addFlags((byte)byte1, 1);
			if (list.size() > 127) {
				byteBuffer.putShort((short)list.indexOf(this.modID));
			} else {
				byteBuffer.put((byte)list.indexOf(this.modID));
			}
		}

		if (this.existsAsVanilla) {
			byte1 = Bits.addFlags((byte)byte1, 2);
		}

		if (this.obsolete) {
			byte1 = Bits.addFlags((byte)byte1, 4);
		}

		if (this.removed) {
			byte1 = Bits.addFlags((byte)byte1, 8);
		}

		int int2;
		if (this.modOverrides != null) {
			byte1 = Bits.addFlags((byte)byte1, 16);
			if (this.modOverrides.size() == 1) {
				if (list.size() > 127) {
					byteBuffer.putShort((short)list.indexOf(this.modOverrides.get(0)));
				} else {
					byteBuffer.put((byte)list.indexOf(this.modOverrides.get(0)));
				}
			} else {
				byte1 = Bits.addFlags((byte)byte1, 32);
				byteBuffer.put((byte)this.modOverrides.size());
				for (int2 = 0; int2 < this.modOverrides.size(); ++int2) {
					if (list.size() > 127) {
						byteBuffer.putShort((short)list.indexOf(this.modOverrides.get(int2)));
					} else {
						byteBuffer.put((byte)list.indexOf(this.modOverrides.get(int2)));
					}
				}
			}
		}

		int2 = byteBuffer.position();
		byteBuffer.position(int1);
		byteBuffer.put(byte1);
		byteBuffer.position(int2);
	}

	protected void load(ByteBuffer byteBuffer, int int1, List list, List list2) {
		this.registryID = byteBuffer.getShort();
		this.moduleName = (String)list2.get(list2.size() > 127 ? byteBuffer.getShort() : byteBuffer.get());
		this.itemName = GameWindow.ReadString(byteBuffer);
		this.fullType = this.moduleName + "." + this.itemName;
		byte byte1 = byteBuffer.get();
		if (Bits.hasFlags((byte)byte1, 1)) {
			this.modID = (String)list.get(list.size() > 127 ? byteBuffer.getShort() : byteBuffer.get());
			this.isModded = true;
		} else {
			this.modID = "pz-vanilla";
			this.isModded = false;
		}

		this.existsAsVanilla = Bits.hasFlags((byte)byte1, 2);
		this.obsolete = Bits.hasFlags((byte)byte1, 4);
		this.removed = Bits.hasFlags((byte)byte1, 8);
		if (Bits.hasFlags((byte)byte1, 16)) {
			if (this.modOverrides == null) {
				this.modOverrides = new ArrayList();
			}

			this.modOverrides.clear();
			if (!Bits.hasFlags((byte)byte1, 32)) {
				this.modOverrides.add((String)list.get(list.size() > 127 ? byteBuffer.getShort() : byteBuffer.get()));
			} else {
				byte byte2 = byteBuffer.get();
				for (int int2 = 0; int2 < byte2; ++int2) {
					this.modOverrides.add((String)list.get(list.size() > 127 ? byteBuffer.getShort() : byteBuffer.get()));
				}
			}
		}
	}
}
