package zombie.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.LuaClosure;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;


public class SpawnRegions {

	private SpawnRegions.Region parseRegionTable(KahluaTable kahluaTable) {
		Object object = kahluaTable.rawget("name");
		Object object2 = kahluaTable.rawget("file");
		Object object3 = kahluaTable.rawget("serverfile");
		SpawnRegions.Region region;
		if (object instanceof String && object2 instanceof String) {
			region = new SpawnRegions.Region();
			region.name = (String)object;
			region.file = (String)object2;
			return region;
		} else if (object instanceof String && object3 instanceof String) {
			region = new SpawnRegions.Region();
			region.name = (String)object;
			region.serverfile = (String)object3;
			return region;
		} else {
			return null;
		}
	}

	private ArrayList parseProfessionsTable(KahluaTable kahluaTable) {
		ArrayList arrayList = null;
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getKey();
			Object object2 = kahluaTableIterator.getValue();
			if (object instanceof String && object2 instanceof KahluaTable) {
				ArrayList arrayList2 = this.parsePointsTable((KahluaTable)object2);
				if (arrayList2 != null) {
					SpawnRegions.Profession profession = new SpawnRegions.Profession();
					profession.name = (String)object;
					profession.points = arrayList2;
					if (arrayList == null) {
						arrayList = new ArrayList();
					}

					arrayList.add(profession);
				}
			}
		}

		return arrayList;
	}

	private ArrayList parsePointsTable(KahluaTable kahluaTable) {
		ArrayList arrayList = null;
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getValue();
			if (object instanceof KahluaTable) {
				SpawnRegions.Point point = this.parsePointTable((KahluaTable)object);
				if (point != null) {
					if (arrayList == null) {
						arrayList = new ArrayList();
					}

					arrayList.add(point);
				}
			}
		}

		return arrayList;
	}

	private SpawnRegions.Point parsePointTable(KahluaTable kahluaTable) {
		Object object = kahluaTable.rawget("worldX");
		Object object2 = kahluaTable.rawget("worldY");
		Object object3 = kahluaTable.rawget("posX");
		Object object4 = kahluaTable.rawget("posY");
		Object object5 = kahluaTable.rawget("posZ");
		if (object instanceof Double && object2 instanceof Double && object3 instanceof Double && object4 instanceof Double) {
			SpawnRegions.Point point = new SpawnRegions.Point();
			point.worldX = ((Double)object).intValue();
			point.worldY = ((Double)object2).intValue();
			point.posX = ((Double)object3).intValue();
			point.posY = ((Double)object4).intValue();
			point.posZ = object5 instanceof Double ? ((Double)object5).intValue() : 0;
			return point;
		} else {
			return null;
		}
	}

	public ArrayList loadRegionsFile(String string) {
		File file = new File(string);
		if (!file.exists()) {
			return null;
		} else {
			try {
				LuaManager.env.rawset("SpawnRegions", (Object)null);
				LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
				LuaManager.RunLua(file.getAbsolutePath());
				Object object = LuaManager.env.rawget("SpawnRegions");
				if (object instanceof LuaClosure) {
					Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, object);
					if (objectArray.length > 1 && objectArray[1] instanceof KahluaTable) {
						ArrayList arrayList = new ArrayList();
						KahluaTableIterator kahluaTableIterator = ((KahluaTable)objectArray[1]).iterator();
						while (kahluaTableIterator.advance()) {
							Object object2 = kahluaTableIterator.getValue();
							if (object2 instanceof KahluaTable) {
								SpawnRegions.Region region = this.parseRegionTable((KahluaTable)object2);
								if (region != null) {
									arrayList.add(region);
								}
							}
						}

						return arrayList;
					}
				}

				return null;
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}
	}

	private String fmtKey(String string) {
		if (string.contains("\\")) {
			string = string.replace("\\", "\\\\");
		}

		if (string.contains("\"")) {
			string = string.replace("\"", "\\\"");
		}

		if (string.contains(" ") || string.contains("\\")) {
			string = "\"" + string + "\"";
		}

		return string.startsWith("\"") ? "[" + string + "]" : string;
	}

	private String fmtValue(String string) {
		if (string.contains("\\")) {
			string = string.replace("\\", "\\\\");
		}

		if (string.contains("\"")) {
			string = string.replace("\"", "\\\"");
		}

		return "\"" + string + "\"";
	}

	public boolean saveRegionsFile(String string, ArrayList arrayList) {
		File file = new File(string);
		DebugLog.log("writing " + string);
		try {
			FileWriter fileWriter = new FileWriter(file);
			boolean boolean1;
			try {
				String string2 = System.lineSeparator();
				fileWriter.write("function SpawnRegions()" + string2);
				fileWriter.write("\treturn {" + string2);
				Iterator iterator = arrayList.iterator();
				while (true) {
					while (iterator.hasNext()) {
						SpawnRegions.Region region = (SpawnRegions.Region)iterator.next();
						String string3;
						if (region.file != null) {
							string3 = this.fmtValue(region.name);
							fileWriter.write("\t\t{ name = " + string3 + ", file = " + this.fmtValue(region.file) + " }," + string2);
						} else if (region.serverfile != null) {
							string3 = this.fmtValue(region.name);
							fileWriter.write("\t\t{ name = " + string3 + ", serverfile = " + this.fmtValue(region.serverfile) + " }," + string2);
						} else if (region.professions != null) {
							string3 = this.fmtValue(region.name);
							fileWriter.write("\t\t{ name = " + string3 + "," + string2);
							fileWriter.write("\t\t\tpoints = {" + string2);
							Iterator iterator2 = region.professions.iterator();
							while (iterator2.hasNext()) {
								SpawnRegions.Profession profession = (SpawnRegions.Profession)iterator2.next();
								string3 = this.fmtKey(profession.name);
								fileWriter.write("\t\t\t\t" + string3 + " = {" + string2);
								Iterator iterator3 = profession.points.iterator();
								while (iterator3.hasNext()) {
									SpawnRegions.Point point = (SpawnRegions.Point)iterator3.next();
									fileWriter.write("\t\t\t\t\t{ worldX = " + point.worldX + ", worldY = " + point.worldY + ", posX = " + point.posX + ", posY = " + point.posY + ", posZ = " + point.posZ + " }," + string2);
								}

								fileWriter.write("\t\t\t\t}," + string2);
							}

							fileWriter.write("\t\t\t}" + string2);
							fileWriter.write("\t\t}," + string2);
						}
					}

					fileWriter.write("\t}" + string2);
					fileWriter.write("end" + System.lineSeparator());
					boolean1 = true;
					break;
				}
			} catch (Throwable throwable) {
				try {
					fileWriter.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			fileWriter.close();
			return boolean1;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public ArrayList loadPointsFile(String string) {
		File file = new File(string);
		if (!file.exists()) {
			return null;
		} else {
			try {
				LuaManager.env.rawset("SpawnPoints", (Object)null);
				LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
				LuaManager.RunLua(file.getAbsolutePath());
				Object object = LuaManager.env.rawget("SpawnPoints");
				if (object instanceof LuaClosure) {
					Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, object);
					if (objectArray.length > 1 && objectArray[1] instanceof KahluaTable) {
						ArrayList arrayList = this.parseProfessionsTable((KahluaTable)objectArray[1]);
						return arrayList;
					}
				}

				return null;
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
		}
	}

	public boolean savePointsFile(String string, ArrayList arrayList) {
		File file = new File(string);
		DebugLog.log("writing " + string);
		try {
			FileWriter fileWriter = new FileWriter(file);
			boolean boolean1;
			try {
				String string2 = System.lineSeparator();
				fileWriter.write("function SpawnPoints()" + string2);
				fileWriter.write("\treturn {" + string2);
				Iterator iterator = arrayList.iterator();
				while (true) {
					if (!iterator.hasNext()) {
						fileWriter.write("\t}" + string2);
						fileWriter.write("end" + System.lineSeparator());
						boolean1 = true;
						break;
					}

					SpawnRegions.Profession profession = (SpawnRegions.Profession)iterator.next();
					String string3 = this.fmtKey(profession.name);
					fileWriter.write("\t\t" + string3 + " = {" + string2);
					Iterator iterator2 = profession.points.iterator();
					while (iterator2.hasNext()) {
						SpawnRegions.Point point = (SpawnRegions.Point)iterator2.next();
						fileWriter.write("\t\t\t{ worldX = " + point.worldX + ", worldY = " + point.worldY + ", posX = " + point.posX + ", posY = " + point.posY + ", posZ = " + point.posZ + " }," + string2);
					}

					fileWriter.write("\t\t}," + string2);
				}
			} catch (Throwable throwable) {
				try {
					fileWriter.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			fileWriter.close();
			return boolean1;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public KahluaTable loadPointsTable(String string) {
		ArrayList arrayList = this.loadPointsFile(string);
		if (arrayList == null) {
			return null;
		} else {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				SpawnRegions.Profession profession = (SpawnRegions.Profession)arrayList.get(int1);
				KahluaTable kahluaTable2 = LuaManager.platform.newTable();
				for (int int2 = 0; int2 < profession.points.size(); ++int2) {
					SpawnRegions.Point point = (SpawnRegions.Point)profession.points.get(int2);
					KahluaTable kahluaTable3 = LuaManager.platform.newTable();
					kahluaTable3.rawset("worldX", (double)point.worldX);
					kahluaTable3.rawset("worldY", (double)point.worldY);
					kahluaTable3.rawset("posX", (double)point.posX);
					kahluaTable3.rawset("posY", (double)point.posY);
					kahluaTable3.rawset("posZ", (double)point.posZ);
					kahluaTable2.rawset(int2 + 1, kahluaTable3);
				}

				kahluaTable.rawset(profession.name, kahluaTable2);
			}

			return kahluaTable;
		}
	}

	public boolean savePointsTable(String string, KahluaTable kahluaTable) {
		ArrayList arrayList = this.parseProfessionsTable(kahluaTable);
		return arrayList != null ? this.savePointsFile(string, arrayList) : false;
	}

	public ArrayList getDefaultServerRegions() {
		ArrayList arrayList = new ArrayList();
		Filter filter = new Filter(){
    
    public boolean accept(Path arrayList) throws IOException {
        return Files.isDirectory(arrayList, new LinkOption[0]) && Files.exists(arrayList.resolve("spawnpoints.lua"), new LinkOption[0]);
    }
};
		String string = ZomboidFileSystem.instance.getMediaPath("maps");
		Path path = FileSystems.getDefault().getPath(string);
		if (!Files.exists(path, new LinkOption[0])) {
			return arrayList;
		} else {
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(path, filter);
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						SpawnRegions.Region region = new SpawnRegions.Region();
						region.name = path2.getFileName().toString();
						region.file = "media/maps/" + region.name + "/spawnpoints.lua";
						arrayList.add(region);
					}
				} catch (Throwable throwable) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}
					}

					throw throwable;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return arrayList;
		}
	}

	public ArrayList getDefaultServerPoints() {
		ArrayList arrayList = new ArrayList();
		SpawnRegions.Profession profession = new SpawnRegions.Profession();
		profession.name = "unemployed";
		profession.points = new ArrayList();
		arrayList.add(profession);
		SpawnRegions.Point point = new SpawnRegions.Point();
		point.worldX = 40;
		point.worldY = 22;
		point.posX = 67;
		point.posY = 201;
		point.posZ = 0;
		profession.points.add(point);
		return arrayList;
	}

	public static class Region {
		public String name;
		public String file;
		public String serverfile;
		public ArrayList professions;
	}

	public static class Profession {
		public String name;
		public ArrayList points;
	}

	public static class Point {
		public int worldX;
		public int worldY;
		public int posX;
		public int posY;
		public int posZ;
	}
}
