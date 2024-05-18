package zombie;

import gnu.trove.map.hash.THashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import zombie.core.Core;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class ZomboidFileSystem {
	public static ZomboidFileSystem instance = new ZomboidFileSystem();
	ArrayList loadList = new ArrayList();
	Map modDirList = new HashMap();
	private ArrayList modFolders;
	private ArrayList modFoldersOrder;
	public HashMap ActiveFileMap = new HashMap();
	THashMap RelativeMap = new THashMap();
	public boolean IgnoreActiveFileMap = false;
	public File base;
	public URI baseURI;
	public ArrayList mods = new ArrayList();
	private HashSet LoadedPacks = new HashSet();

	public void searchFolders(File file) {
		if (!GameServer.bServer) {
			Thread.yield();
			Core.getInstance().DoFrameReady();
		}

		if (file.isDirectory()) {
			String string = file.getAbsolutePath().replace("\\", "/").replace("./", "");
			if (string.contains("media/maps/")) {
				this.loadList.add(string);
			}

			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				this.searchFolders(new File(file.getAbsolutePath() + File.separator + stringArray[int1]));
			}
		} else {
			this.loadList.add(file.getAbsolutePath().replace("\\", "/").replace("./", ""));
		}
	}

	public String getString(String string) {
		if (this.IgnoreActiveFileMap) {
			return string;
		} else {
			if (this.RelativeMap.containsKey(string)) {
				string = (String)this.RelativeMap.get(string);
			} else {
				String string2 = string;
				string = this.getRelativeFile(string);
				this.RelativeMap.put(string2, string);
			}

			return this.ActiveFileMap.containsKey(string) ? (String)this.ActiveFileMap.get(string) : string;
		}
	}

	public String getAbsolutePath(String string) {
		return this.ActiveFileMap.containsKey(string) ? (String)this.ActiveFileMap.get(string) : null;
	}

	public void init() {
		this.base = (new File("./")).getAbsoluteFile();
		this.baseURI = this.base.toURI();
		File file = (new File("./media/")).getAbsoluteFile();
		this.searchFolders(file);
		for (int int1 = 0; int1 < this.loadList.size(); ++int1) {
			String string = this.getRelativeFile((String)this.loadList.get(int1));
			this.ActiveFileMap.put(string, (new File((String)this.loadList.get(int1))).getAbsolutePath());
		}

		this.loadList.clear();
	}

	public void Reset() {
		this.loadList.clear();
		this.ActiveFileMap.clear();
		this.modDirList.clear();
		this.mods.clear();
		this.modFolders = null;
	}

	public void resetModFolders() {
		this.modFolders = null;
	}

	public void getInstalledItemModsFolders(ArrayList arrayList) {
		if (SteamUtils.isSteamModeEnabled()) {
			String[] stringArray = SteamWorkshop.instance.GetInstalledItemFolders();
			if (stringArray != null) {
				String[] stringArray2 = stringArray;
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string = stringArray2[int2];
					File file = new File(string + File.separator + "mods");
					if (file.exists()) {
						arrayList.add(file.getAbsolutePath());
					}
				}
			}
		}
	}

	public void getStagedItemModsFolders(ArrayList arrayList) {
		if (SteamUtils.isSteamModeEnabled()) {
			ArrayList arrayList2 = SteamWorkshop.instance.getStageFolders();
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				File file = new File((String)arrayList2.get(int1) + File.separator + "Contents" + File.separator + "mods");
				if (file.exists()) {
					arrayList.add(file.getAbsolutePath());
				}
			}
		}
	}

	private void getAllModFoldersAux(String string, List list) {
		Filter filter = new Filter(){
    
    public boolean accept(Path string) throws IOException {
        return Files.isDirectory(string, new LinkOption[0]) && Files.exists(string.resolve("mod.info"), new LinkOption[0]);
    }
};
		Path path = FileSystems.getDefault().getPath(string);
		if (Files.exists(path, new LinkOption[0])) {
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(path, filter);
				Throwable throwable = null;
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						if (path2.getFileName().toString().toLowerCase().equals("examplemod")) {
							System.out.println("MOD: refusing to list " + path2.getFileName());
						} else {
							list.add(path2.toAbsolutePath().toString());
						}
					}
				} catch (Throwable throwable2) {
					throwable = throwable2;
					throw throwable2;
				} finally {
					if (directoryStream != null) {
						if (throwable != null) {
							try {
								directoryStream.close();
							} catch (Throwable throwable3) {
								throwable.addSuppressed(throwable3);
							}
						} else {
							directoryStream.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void setModFoldersOrder(String string) {
		this.modFoldersOrder = new ArrayList(Arrays.asList(string.split(",")));
	}

	public void getAllModFolders(List list) {
		if (this.modFolders == null) {
			this.modFolders = new ArrayList();
			if (this.modFoldersOrder == null) {
				this.setModFoldersOrder("workshop,steam,mods");
			}

			ArrayList arrayList = new ArrayList();
			int int1;
			for (int1 = 0; int1 < this.modFoldersOrder.size(); ++int1) {
				String string = (String)this.modFoldersOrder.get(int1);
				if ("workshop".equals(string)) {
					this.getStagedItemModsFolders(arrayList);
				}

				if ("steam".equals(string)) {
					this.getInstalledItemModsFolders(arrayList);
				}

				if ("mods".equals(string)) {
					arrayList.add(Core.getMyDocumentFolder() + File.separator + "mods");
				}
			}

			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				this.getAllModFoldersAux((String)arrayList.get(int1), this.modFolders);
			}
		}

		list.clear();
		list.addAll(this.modFolders);
	}

	public ArrayList getWorkshopItemMods(long long1) {
		ArrayList arrayList = new ArrayList();
		if (!SteamUtils.isSteamModeEnabled()) {
			return arrayList;
		} else {
			String string = SteamWorkshop.instance.GetItemInstallFolder(long1);
			if (string == null) {
				return arrayList;
			} else {
				File file = new File(string + File.separator + "mods");
				if (file.exists() && file.isDirectory()) {
					File[] fileArray = file.listFiles();
					ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
					File[] fileArray2 = fileArray;
					int int1 = fileArray.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						File file2 = fileArray2[int2];
						if (file2.isDirectory()) {
							ChooseGameInfo.Mod mod = chooseGameInfo.readModInfo(file2.getAbsolutePath());
							if (mod != null) {
								arrayList.add(mod);
							}
						}
					}

					return arrayList;
				} else {
					return arrayList;
				}
			}
		}
	}

	public String searchForModInfo(File file, String string) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string2 = this.searchForModInfo(new File(file.getAbsolutePath() + File.separator + stringArray[int1]), string);
				if (string2 != null) {
					return string2;
				}
			}
		} else if (file.getAbsolutePath().endsWith("mod.info")) {
			ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
			ChooseGameInfo.Mod mod = chooseGameInfo.readModInfo(file.getAbsoluteFile().getParent());
			if (mod == null) {
				return null;
			}

			if (mod.getId() != null && !mod.getId().isEmpty()) {
				this.modDirList.put(string, mod.getDir());
			}

			if (mod.getId().equals(string)) {
				return mod.getDir();
			}
		}

		return null;
	}

	public void loadMod(String string) {
		if (this.getModDir(string) != null) {
			System.out.println("MOD: loading " + string);
			File file = new File(this.getModDir(string));
			URI uRI = file.toURI();
			this.loadList.clear();
			this.searchFolders(file);
			for (int int1 = 0; int1 < this.loadList.size(); ++int1) {
				String string2 = this.getRelativeFile(uRI, (String)this.loadList.get(int1));
				if (this.ActiveFileMap.containsKey(string2) && !string2.endsWith("mod.info") && !string2.endsWith("poster.png")) {
					System.out.println("MOD: mod \"" + string + "\" overrides " + string2);
				}

				this.ActiveFileMap.put(string2, (new File((String)this.loadList.get(int1))).getAbsolutePath());
			}

			this.loadList.clear();
		}
	}

	private ArrayList readLoadedTxt() {
		ArrayList arrayList = new ArrayList();
		String string = Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "loaded.txt";
		if (!(new File(string)).exists()) {
			return arrayList;
		} else {
			try {
				FileReader fileReader = new FileReader(string);
				Throwable throwable = null;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					Throwable throwable2 = null;
					try {
						for (String string2 = bufferedReader.readLine(); string2 != null; string2 = bufferedReader.readLine()) {
							string2 = string2.trim();
							if (!string2.isEmpty()) {
								arrayList.add(string2);
							}
						}
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					} finally {
						if (bufferedReader != null) {
							if (throwable2 != null) {
								try {
									bufferedReader.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								bufferedReader.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (fileReader != null) {
						if (throwable != null) {
							try {
								fileReader.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							fileReader.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return arrayList;
		}
	}

	public void loadMods() {
		Core.getInstance();
		if (Core.OptionModsEnabled) {
			if (GameClient.bClient) {
				this.loadTranslationMods();
				this.loadMods(GameClient.instance.ServerMods);
			} else {
				this.base = (new File("./")).getAbsoluteFile();
				try {
					ArrayList arrayList = this.readLoadedTxt();
					this.loadMods(arrayList);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	private boolean isTranslationMod(String string) {
		ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
		ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
		if (mod == null) {
			return false;
		} else {
			boolean boolean1 = false;
			File file = new File(mod.getDir());
			URI uRI = file.toURI();
			this.loadList.clear();
			this.searchFolders(file);
			for (int int1 = 0; int1 < this.loadList.size(); ++int1) {
				String string2 = this.getRelativeFile(uRI, (String)this.loadList.get(int1));
				if (string2.endsWith(".lua")) {
					return false;
				}

				if (string2.startsWith("media/maps/")) {
					return false;
				}

				if (string2.startsWith("media/scripts/")) {
					return false;
				}

				if (string2.startsWith("media/lua/")) {
					if (!string2.startsWith("media/lua/shared/Translate/")) {
						return false;
					}

					boolean1 = true;
				}
			}

			this.loadList.clear();
			return boolean1;
		}
	}

	private void loadTranslationMods() {
		if (GameClient.bClient) {
			ArrayList arrayList = this.readLoadedTxt();
			ArrayList arrayList2 = new ArrayList();
			if (this.loadModsAux(arrayList, arrayList2) == null) {
				Iterator iterator = arrayList2.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					if (this.isTranslationMod(string)) {
						DebugLog.log("MOD: loading translation mod \"" + string + "\"");
						this.loadMod(string);
					}
				}
			}
		}
	}

	private String loadModAndRequired(String string, ArrayList arrayList) {
		if (string.isEmpty()) {
			return null;
		} else if (string.toLowerCase().equals("examplemod")) {
			DebugLog.log("MOD: refusing to load " + string);
			return null;
		} else if (arrayList.contains(string)) {
			return null;
		} else {
			ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
			ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
			if (mod == null) {
				if (GameServer.bServer) {
					GameServer.ServerMods.remove(string);
				}

				DebugLog.log("MOD: required mod \"" + string + "\" not found");
				return string;
			} else {
				if (mod.getRequire() != null) {
					String string2 = this.loadModsAux(mod.getRequire(), arrayList);
					if (string2 != null) {
						return string2;
					}
				}

				arrayList.add(string);
				return null;
			}
		}
	}

	public String loadModsAux(ArrayList arrayList, ArrayList arrayList2) {
		Iterator iterator = arrayList.iterator();
		String string;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			String string2 = (String)iterator.next();
			string = this.loadModAndRequired(string2, arrayList2);
		} while (string == null);

		return string;
	}

	public void loadMods(ArrayList arrayList) {
		this.mods.clear();
		Iterator iterator = arrayList.iterator();
		String string;
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			this.loadModAndRequired(string, this.mods);
		}

		iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			this.loadMod(string);
		}
	}

	public ArrayList getModIDs() {
		return this.mods;
	}

	public String getModDir(String string) {
		return (String)this.modDirList.get(string);
	}

	public String getRelativeFile(String string) {
		return this.getRelativeFile(this.baseURI, string);
	}

	public String getRelativeFile(URI uRI, String string) {
		URI uRI2 = (new File(string)).toURI();
		URI uRI3 = uRI.relativize(uRI2);
		if (!uRI3.equals(uRI2)) {
			return string.endsWith("/") ? uRI3.getPath() + "/" : uRI3.getPath();
		} else {
			return string;
		}
	}

	public void saveModsFile() {
		this.base = (new File("./")).getAbsoluteFile();
		try {
			File file = new File(Core.getMyDocumentFolder() + File.separator + "mods");
			if (!file.exists()) {
				file.mkdir();
			}

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "loaded.txt")));
			String string = "";
			for (int int1 = 0; int1 < this.mods.size(); ++int1) {
				bufferedWriter.write((String)this.mods.get(int1));
				bufferedWriter.newLine();
			}

			bufferedWriter.close();
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void loadModPackFiles() {
		ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getPacks().iterator();
					while (iterator2.hasNext()) {
						String string2 = (String)iterator2.next();
						String string3 = this.getRelativeFile("media/texturepacks/" + string2 + ".pack");
						if (!this.ActiveFileMap.containsKey(string3)) {
							System.out.println("MOD: pack file \"" + string2 + "\" needed by " + string + " not found");
						} else if (!this.LoadedPacks.contains(string2)) {
							GameWindow.LoadTexturePack(string2);
							this.LoadedPacks.add(string2);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void loadModTileDefs() {
		HashSet hashSet = new HashSet();
		ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getTileDefs().iterator();
					while (iterator2.hasNext()) {
						ChooseGameInfo.TileDef tileDef = (ChooseGameInfo.TileDef)iterator2.next();
						if (hashSet.contains(tileDef.fileNumber)) {
							System.out.println("MOD: ERROR tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
						} else {
							String string2 = tileDef.name;
							String string3 = this.getRelativeFile("media/" + string2 + ".tiles");
							if (!this.ActiveFileMap.containsKey(string3)) {
								System.out.println("MOD: tiledef file \"" + tileDef.name + "\" needed by " + string + " not found");
							} else {
								string2 = (String)this.ActiveFileMap.get(string3);
								IsoWorld.instance.LoadTileDefinitions(IsoWorld.instance.spriteManager, string2, tileDef.fileNumber);
							}
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void loadModTileDefPropertyStrings() {
		HashSet hashSet = new HashSet();
		ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = chooseGameInfo.getModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getTileDefs().iterator();
					while (iterator2.hasNext()) {
						ChooseGameInfo.TileDef tileDef = (ChooseGameInfo.TileDef)iterator2.next();
						if (hashSet.contains(tileDef.fileNumber)) {
							System.out.println("MOD: ERROR tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
						} else {
							String string2 = tileDef.name;
							String string3 = this.getRelativeFile("media/" + string2 + ".tiles");
							if (!this.ActiveFileMap.containsKey(string3)) {
								System.out.println("MOD: tiledef file \"" + tileDef.name + "\" needed by " + string + " not found");
							} else {
								string2 = (String)this.ActiveFileMap.get(string3);
								IsoWorld.instance.LoadTileDefinitionsPropertyStrings(IsoWorld.instance.spriteManager, string2, tileDef.fileNumber);
							}
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
}
