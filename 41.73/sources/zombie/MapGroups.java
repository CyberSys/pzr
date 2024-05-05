package zombie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.modding.ActiveMods;


public final class MapGroups {
	private final ArrayList groups = new ArrayList();
	private final ArrayList realDirectories = new ArrayList();

	private static ArrayList getVanillaMapDirectories(boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		File file = ZomboidFileSystem.instance.getMediaFile("maps");
		String[] stringArray = file.list();
		if (stringArray != null) {
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string = stringArray[int1];
				if (string.equalsIgnoreCase("challengemaps")) {
					if (boolean1) {
						try {
							DirectoryStream directoryStream = Files.newDirectoryStream(Paths.get(file.getPath(), string), (boolean1x)->{
								return Files.isDirectory(boolean1x, new LinkOption[0]) && Files.exists(boolean1x.resolve("map.info"), new LinkOption[0]);
							});

							try {
								Iterator iterator = directoryStream.iterator();
								while (iterator.hasNext()) {
									Path path = (Path)iterator.next();
									arrayList.add(string + "/" + path.getFileName().toString());
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
						}
					}
				} else {
					arrayList.add(string);
				}
			}
		}

		return arrayList;
	}

	public static String addMissingVanillaDirectories(String string) {
		ArrayList arrayList = getVanillaMapDirectories(false);
		boolean boolean1 = false;
		String[] stringArray = string.split(";");
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		int int2;
		String string2;
		for (int2 = 0; int2 < int1; ++int2) {
			string2 = stringArray2[int2];
			string2 = string2.trim();
			if (!string2.isEmpty() && arrayList.contains(string2)) {
				boolean1 = true;
				break;
			}
		}

		if (!boolean1) {
			return string;
		} else {
			ArrayList arrayList2 = new ArrayList();
			String[] stringArray3 = stringArray;
			int2 = stringArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				String string3 = stringArray3[int3];
				string3 = string3.trim();
				if (!string3.isEmpty()) {
					arrayList2.add(string3);
				}
			}

			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string4 = (String)iterator.next();
				if (!arrayList2.contains(string4)) {
					arrayList2.add(string4);
				}
			}

			String string5 = "";
			for (Iterator iterator2 = arrayList2.iterator(); iterator2.hasNext(); string5 = string5 + string2) {
				string2 = (String)iterator2.next();
				if (!string5.isEmpty()) {
					string5 = string5 + ";";
				}
			}

			return string5;
		}
	}

	public void createGroups() {
		this.createGroups(ActiveMods.getById("currentGame"), true);
	}

	public void createGroups(ActiveMods activeMods, boolean boolean1) {
		this.createGroups(activeMods, boolean1, false);
	}

	public void createGroups(ActiveMods activeMods, boolean boolean1, boolean boolean2) {
		this.groups.clear();
		this.realDirectories.clear();
		Iterator iterator = activeMods.getMods().iterator();
		while (true) {
			ChooseGameInfo.Mod mod;
			String[] stringArray;
			do {
				File file;
				do {
					do {
						String string;
						if (!iterator.hasNext()) {
							if (boolean1) {
								ArrayList arrayList = getVanillaMapDirectories(boolean2);
								string = ZomboidFileSystem.instance.getMediaPath("maps");
								Iterator iterator2 = arrayList.iterator();
								while (iterator2.hasNext()) {
									String string2 = (String)iterator2.next();
									this.handleMapDirectory(string2, string + File.separator + string2);
								}
							}

							iterator = this.realDirectories.iterator();
							while (iterator.hasNext()) {
								MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)iterator.next();
								ArrayList arrayList2 = new ArrayList();
								this.getDirsRecursively(mapDirectory, arrayList2);
								MapGroups.MapGroup mapGroup = this.findGroupWithAnyOfTheseDirectories(arrayList2);
								if (mapGroup == null) {
									mapGroup = new MapGroups.MapGroup();
									this.groups.add(mapGroup);
								}

								Iterator iterator3 = arrayList2.iterator();
								while (iterator3.hasNext()) {
									MapGroups.MapDirectory mapDirectory2 = (MapGroups.MapDirectory)iterator3.next();
									if (!mapGroup.hasDirectory(mapDirectory2.name)) {
										mapGroup.addDirectory(mapDirectory2);
									}
								}
							}

							iterator = this.groups.iterator();
							MapGroups.MapGroup mapGroup2;
							while (iterator.hasNext()) {
								mapGroup2 = (MapGroups.MapGroup)iterator.next();
								mapGroup2.setPriority();
							}

							iterator = this.groups.iterator();
							while (iterator.hasNext()) {
								mapGroup2 = (MapGroups.MapGroup)iterator.next();
								mapGroup2.setOrder(activeMods);
							}

							if (Core.bDebug) {
								int int1 = 1;
								for (Iterator iterator4 = this.groups.iterator(); iterator4.hasNext(); ++int1) {
									MapGroups.MapGroup mapGroup3 = (MapGroups.MapGroup)iterator4.next();
									DebugLog.log("MapGroup " + int1 + "/" + this.groups.size());
									Iterator iterator5 = mapGroup3.directories.iterator();
									while (iterator5.hasNext()) {
										MapGroups.MapDirectory mapDirectory3 = (MapGroups.MapDirectory)iterator5.next();
										DebugLog.log("  " + mapDirectory3.name);
									}
								}

								DebugLog.log("-----");
							}

							return;
						}

						string = (String)iterator.next();
						mod = ChooseGameInfo.getAvailableModDetails(string);
					}			 while (mod == null);

					file = new File(mod.getDir() + "/media/maps/");
				}		 while (!file.exists());

				stringArray = file.list();
			}	 while (stringArray == null);

			for (int int2 = 0; int2 < stringArray.length; ++int2) {
				String string3 = stringArray[int2];
				if (string3.equalsIgnoreCase("challengemaps")) {
					if (boolean2) {
					}
				} else {
					String string4 = mod.getDir();
					this.handleMapDirectory(string3, string4 + "/media/maps/" + string3);
				}
			}
		}
	}

	private void getDirsRecursively(MapGroups.MapDirectory mapDirectory, ArrayList arrayList) {
		if (!arrayList.contains(mapDirectory)) {
			arrayList.add(mapDirectory);
			Iterator iterator = mapDirectory.lotDirs.iterator();
			while (true) {
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					Iterator iterator2 = this.realDirectories.iterator();
					while (iterator2.hasNext()) {
						MapGroups.MapDirectory mapDirectory2 = (MapGroups.MapDirectory)iterator2.next();
						if (mapDirectory2.name.equals(string)) {
							this.getDirsRecursively(mapDirectory2, arrayList);
							break;
						}
					}
				}

				return;
			}
		}
	}

	public int getNumberOfGroups() {
		return this.groups.size();
	}

	public ArrayList getMapDirectoriesInGroup(int int1) {
		if (int1 >= 0 && int1 < this.groups.size()) {
			ArrayList arrayList = new ArrayList();
			Iterator iterator = ((MapGroups.MapGroup)this.groups.get(int1)).directories.iterator();
			while (iterator.hasNext()) {
				MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)iterator.next();
				arrayList.add(mapDirectory.name);
			}

			return arrayList;
		} else {
			throw new RuntimeException("invalid MapGroups index " + int1);
		}
	}

	public void setWorld(int int1) {
		ArrayList arrayList = this.getMapDirectoriesInGroup(int1);
		String string = "";
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			string = string + (String)arrayList.get(int2);
			if (int2 < arrayList.size() - 1) {
				string = string + ";";
			}
		}

		IsoWorld.instance.setMap(string);
	}

	private void handleMapDirectory(String string, String string2) {
		ArrayList arrayList = this.getLotDirectories(string2);
		if (arrayList != null) {
			MapGroups.MapDirectory mapDirectory = new MapGroups.MapDirectory(string, string2, arrayList);
			this.realDirectories.add(mapDirectory);
		}
	}

	private ArrayList getLotDirectories(String string) {
		File file = new File(string + "/map.info");
		if (!file.exists()) {
			return null;
		} else {
			ArrayList arrayList = new ArrayList();
			try {
				FileReader fileReader = new FileReader(file.getAbsolutePath());
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String string2;
					try {
						while ((string2 = bufferedReader.readLine()) != null) {
							string2 = string2.trim();
							if (string2.startsWith("lots=")) {
								arrayList.add(string2.replace("lots=", "").trim());
							}
						}
					} catch (Throwable throwable) {
						try {
							bufferedReader.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedReader.close();
				} catch (Throwable throwable3) {
					try {
						fileReader.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileReader.close();
				return arrayList;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return null;
			}
		}
	}

	private MapGroups.MapGroup findGroupWithAnyOfTheseDirectories(ArrayList arrayList) {
		Iterator iterator = this.groups.iterator();
		MapGroups.MapGroup mapGroup;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			mapGroup = (MapGroups.MapGroup)iterator.next();
		} while (!mapGroup.hasAnyOfTheseDirectories(arrayList));

		return mapGroup;
	}

	public ArrayList getAllMapsInOrder() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.groups.iterator();
		while (iterator.hasNext()) {
			MapGroups.MapGroup mapGroup = (MapGroups.MapGroup)iterator.next();
			Iterator iterator2 = mapGroup.directories.iterator();
			while (iterator2.hasNext()) {
				MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)iterator2.next();
				arrayList.add(mapDirectory.name);
			}
		}

		return arrayList;
	}

	public boolean checkMapConflicts() {
		boolean boolean1 = false;
		MapGroups.MapGroup mapGroup;
		for (Iterator iterator = this.groups.iterator(); iterator.hasNext(); boolean1 |= mapGroup.checkMapConflicts()) {
			mapGroup = (MapGroups.MapGroup)iterator.next();
		}

		return boolean1;
	}

	public ArrayList getMapConflicts(String string) {
		Iterator iterator = this.groups.iterator();
		MapGroups.MapDirectory mapDirectory;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			MapGroups.MapGroup mapGroup = (MapGroups.MapGroup)iterator.next();
			mapDirectory = mapGroup.getDirectoryByName(string);
		} while (mapDirectory == null);

		ArrayList arrayList = new ArrayList();
		arrayList.addAll(mapDirectory.conflicts);
		return arrayList;
	}

	private class MapDirectory {
		String name;
		String path;
		ArrayList lotDirs = new ArrayList();
		ArrayList conflicts = new ArrayList();

		public MapDirectory(String string, String string2) {
			this.name = string;
			this.path = string2;
		}

		public MapDirectory(String string, String string2, ArrayList arrayList) {
			this.name = string;
			this.path = string2;
			this.lotDirs.addAll(arrayList);
		}

		public void getLotHeaders(ArrayList arrayList) {
			File file = new File(this.path);
			if (file.isDirectory()) {
				String[] stringArray = file.list();
				if (stringArray != null) {
					for (int int1 = 0; int1 < stringArray.length; ++int1) {
						if (stringArray[int1].endsWith(".lotheader")) {
							arrayList.add(stringArray[int1]);
						}
					}
				}
			}
		}
	}

	private class MapGroup {
		private LinkedList directories = new LinkedList();

		void addDirectory(String string, String string2) {
			assert !this.hasDirectory(string);
			MapGroups.MapDirectory mapDirectory = MapGroups.this.new MapDirectory(string, string2);
			this.directories.add(mapDirectory);
		}

		void addDirectory(String string, String string2, ArrayList arrayList) {
			assert !this.hasDirectory(string);
			MapGroups.MapDirectory mapDirectory = MapGroups.this.new MapDirectory(string, string2, arrayList);
			this.directories.add(mapDirectory);
		}

		void addDirectory(MapGroups.MapDirectory mapDirectory) {
			assert !this.hasDirectory(mapDirectory.name);
			this.directories.add(mapDirectory);
		}

		MapGroups.MapDirectory getDirectoryByName(String string) {
			Iterator iterator = this.directories.iterator();
			MapGroups.MapDirectory mapDirectory;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				mapDirectory = (MapGroups.MapDirectory)iterator.next();
			} while (!mapDirectory.name.equals(string));

			return mapDirectory;
		}

		boolean hasDirectory(String string) {
			return this.getDirectoryByName(string) != null;
		}

		boolean hasAnyOfTheseDirectories(ArrayList arrayList) {
			Iterator iterator = arrayList.iterator();
			MapGroups.MapDirectory mapDirectory;
			do {
				if (!iterator.hasNext()) {
					return false;
				}

				mapDirectory = (MapGroups.MapDirectory)iterator.next();
			} while (!this.directories.contains(mapDirectory));

			return true;
		}

		boolean isReferencedByOtherMaps(MapGroups.MapDirectory mapDirectory) {
			Iterator iterator = this.directories.iterator();
			MapGroups.MapDirectory mapDirectory2;
			do {
				if (!iterator.hasNext()) {
					return false;
				}

				mapDirectory2 = (MapGroups.MapDirectory)iterator.next();
			} while (mapDirectory == mapDirectory2 || !mapDirectory2.lotDirs.contains(mapDirectory.name));

			return true;
		}

		void getDirsRecursively(MapGroups.MapDirectory mapDirectory, ArrayList arrayList) {
			if (!arrayList.contains(mapDirectory.name)) {
				arrayList.add(mapDirectory.name);
				Iterator iterator = mapDirectory.lotDirs.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					MapGroups.MapDirectory mapDirectory2 = this.getDirectoryByName(string);
					if (mapDirectory2 != null) {
						this.getDirsRecursively(mapDirectory2, arrayList);
					}
				}
			}
		}

		void setPriority() {
			ArrayList arrayList = new ArrayList(this.directories);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)iterator.next();
				if (!this.isReferencedByOtherMaps(mapDirectory)) {
					ArrayList arrayList2 = new ArrayList();
					this.getDirsRecursively(mapDirectory, arrayList2);
					this.setPriority(arrayList2);
				}
			}
		}

		void setPriority(List list) {
			ArrayList arrayList = new ArrayList(list.size());
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				if (this.hasDirectory(string)) {
					arrayList.add(this.getDirectoryByName(string));
				}
			}

			for (int int1 = 0; int1 < this.directories.size(); ++int1) {
				MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)this.directories.get(int1);
				if (list.contains(mapDirectory.name)) {
					this.directories.set(int1, (MapGroups.MapDirectory)arrayList.remove(0));
				}
			}
		}

		void setOrder(ActiveMods activeMods) {
			if (!activeMods.getMapOrder().isEmpty()) {
				this.setPriority(activeMods.getMapOrder());
			}
		}

		boolean checkMapConflicts() {
			HashMap hashMap = new HashMap();
			ArrayList arrayList = new ArrayList();
			Iterator iterator = this.directories.iterator();
			while (iterator.hasNext()) {
				MapGroups.MapDirectory mapDirectory = (MapGroups.MapDirectory)iterator.next();
				mapDirectory.conflicts.clear();
				arrayList.clear();
				mapDirectory.getLotHeaders(arrayList);
				String string;
				for (Iterator iterator2 = arrayList.iterator(); iterator2.hasNext(); ((ArrayList)hashMap.get(string)).add(mapDirectory.name)) {
					string = (String)iterator2.next();
					if (!hashMap.containsKey(string)) {
						hashMap.put(string, new ArrayList());
					}
				}
			}

			boolean boolean1 = false;
			Iterator iterator3 = hashMap.keySet().iterator();
			while (true) {
				String string2;
				ArrayList arrayList2;
				do {
					if (!iterator3.hasNext()) {
						return boolean1;
					}

					string2 = (String)iterator3.next();
					arrayList2 = (ArrayList)hashMap.get(string2);
				}	 while (arrayList2.size() <= 1);

				for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
					MapGroups.MapDirectory mapDirectory2 = this.getDirectoryByName((String)arrayList2.get(int1));
					for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
						if (int1 != int2) {
							String string3 = Translator.getText("UI_MapConflict", mapDirectory2.name, arrayList2.get(int2), string2);
							mapDirectory2.conflicts.add(string3);
							boolean1 = true;
						}
					}
				}
			}
		}
	}
}
