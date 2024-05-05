package zombie.world.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import zombie.world.ItemInfo;


public class Log {

	public static class ModIDChangedItem extends Log.BaseItemLog {
		protected final String oldModID;
		protected final String newModID;

		public ModIDChangedItem(ItemInfo itemInfo, String string, String string2) {
			super(itemInfo);
			this.oldModID = string;
			this.newModID = string2;
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"modchange_item\", oldModID = \"" + this.oldModID + "\", " + this.getItemString() + " }" + System.lineSeparator());
		}
	}

	public static class RemovedItem extends Log.BaseItemLog {
		protected final boolean isScriptMissing;

		public RemovedItem(ItemInfo itemInfo, boolean boolean1) {
			super(itemInfo);
			this.isScriptMissing = boolean1;
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"removed_item\", scriptMissing = " + this.isScriptMissing + ", " + this.getItemString() + " }" + System.lineSeparator());
		}
	}

	public static class ObsoleteItem extends Log.BaseItemLog {

		public ObsoleteItem(ItemInfo itemInfo) {
			super(itemInfo);
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"obsolete_item\", " + this.getItemString() + " }" + System.lineSeparator());
		}
	}

	public static class ReinstateItem extends Log.BaseItemLog {

		public ReinstateItem(ItemInfo itemInfo) {
			super(itemInfo);
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"reinstate_item\", " + this.getItemString() + " }" + System.lineSeparator());
		}
	}

	public static class RegisterItem extends Log.BaseItemLog {

		public RegisterItem(ItemInfo itemInfo) {
			super(itemInfo);
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"reg_item\", " + this.getItemString() + " }" + System.lineSeparator());
		}
	}

	public abstract static class BaseItemLog extends Log.BaseLog {
		protected final ItemInfo itemInfo;

		public BaseItemLog(ItemInfo itemInfo) {
			this.itemInfo = itemInfo;
		}

		abstract void saveAsText(FileWriter fileWriter, String string) throws IOException;

		protected String getItemString() {
			String string = this.itemInfo.getFullType();
			return "fulltype = \"" + string + "\", registeryID = " + this.itemInfo.getRegistryID() + ", existsVanilla = " + this.itemInfo.isExistsAsVanilla() + ", isModded = " + this.itemInfo.isModded() + ", modID = \"" + this.itemInfo.getModID() + "\", obsolete = " + this.itemInfo.isObsolete() + ", removed = " + this.itemInfo.isRemoved() + ", isLoaded = " + this.itemInfo.isLoaded();
		}
	}

	public static class RegisterObject extends Log.BaseLog {
		protected final String objectName;
		protected final int ID;

		public RegisterObject(String string, int int1) {
			this.objectName = string;
			this.ID = int1;
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{ type = \"reg_obj\", id = " + this.ID + ", obj = \"" + this.objectName + "\" }" + System.lineSeparator());
		}
	}

	public static class Comment extends Log.BaseLog {
		protected String txt;

		public Comment(String string) {
			this.ignoreSaveCheck = true;
			this.txt = string;
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "-- " + this.txt + System.lineSeparator());
		}
	}

	public static class Info extends Log.BaseLog {
		protected final List mods;
		protected final String timeStamp;
		protected final String saveWorld;
		protected final int worldVersion;
		public boolean HasErrored = false;

		public Info(String string, String string2, int int1, List list) {
			this.ignoreSaveCheck = true;
			this.timeStamp = string;
			this.saveWorld = string2;
			this.worldVersion = int1;
			this.mods = list;
		}

		public void saveAsText(FileWriter fileWriter, String string) throws IOException {
			fileWriter.write(string + "{" + System.lineSeparator());
			fileWriter.write(string + "\ttype = \"info\"," + System.lineSeparator());
			fileWriter.write(string + "\ttimeStamp = \"" + this.timeStamp + "\"," + System.lineSeparator());
			fileWriter.write(string + "\tsaveWorld = \"" + this.saveWorld + "\"," + System.lineSeparator());
			fileWriter.write(string + "\tworldVersion = " + this.worldVersion + "," + System.lineSeparator());
			fileWriter.write(string + "\thasErrored = " + this.HasErrored + "," + System.lineSeparator());
			fileWriter.write(string + "\titemMods = {" + System.lineSeparator());
			for (int int1 = 0; int1 < this.mods.size(); ++int1) {
				fileWriter.write(string + "\t\t\"" + (String)this.mods.get(int1) + "\"," + System.lineSeparator());
			}

			fileWriter.write(string + "\t}," + System.lineSeparator());
			fileWriter.write(string + "}," + System.lineSeparator());
		}
	}

	public abstract static class BaseLog {
		protected boolean ignoreSaveCheck = false;

		public boolean isIgnoreSaveCheck() {
			return this.ignoreSaveCheck;
		}

		abstract void saveAsText(FileWriter fileWriter, String string) throws IOException;
	}
}
