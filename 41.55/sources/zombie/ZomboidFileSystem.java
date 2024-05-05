package zombie;

import gnu.trove.map.hash.THashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
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
import java.nio.file.Paths;
import java.nio.file.DirectoryStream.Filter;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;


public final class ZomboidFileSystem {
	public static final ZomboidFileSystem instance = new ZomboidFileSystem();
	private final ArrayList loadList = new ArrayList();
	private final Map modIdToDir = new HashMap();
	private final Map modDirToMod = new HashMap();
	private ArrayList modFolders;
	private ArrayList modFoldersOrder;
	public final HashMap ActiveFileMap = new HashMap();
	public File base;
	public URI baseURI;
	private File workdir;
	private URI workdirURI;
	private File localWorkdir;
	private File anims;
	private URI animsURI;
	private File animsX;
	private URI animsXURI;
	private File animSets;
	private URI animSetsURI;
	private File actiongroups;
	private URI actiongroupsURI;
	private File cacheDir;
	private final THashMap RelativeMap = new THashMap();
	public boolean IgnoreActiveFileMap = false;
	private final ArrayList mods = new ArrayList();
	private final HashSet LoadedPacks = new HashSet();
	private FileGuidTable m_fileGuidTable = null;
	private boolean m_fileGuidTableWatcherActive = false;
	private final PredicatedFileWatcher m_modFileWatcher = new PredicatedFileWatcher(this::isModFile, this::onModFileChanged);
	private final HashSet m_watchedModFolders = new HashSet();
	private long m_modsChangedTime = 0L;

	private ZomboidFileSystem() {
	}

	public void init() throws IOException {
		this.base = (new File("./")).getAbsoluteFile().getCanonicalFile();
		this.baseURI = this.base.toURI();
		this.workdir = (new File(this.base, "media")).getAbsoluteFile().getCanonicalFile();
		this.workdirURI = this.workdir.toURI();
		this.localWorkdir = this.base.toPath().relativize(this.workdir.toPath()).toFile();
		this.anims = new File(this.workdir, "anims");
		this.animsURI = this.anims.toURI();
		this.animsX = new File(this.workdir, "anims_X");
		this.animsXURI = this.animsX.toURI();
		this.animSets = new File(this.workdir, "AnimSets");
		this.animSetsURI = this.animSets.toURI();
		this.actiongroups = new File(this.workdir, "actiongroups");
		this.actiongroupsURI = this.actiongroups.toURI();
		this.searchFolders(this.workdir);
		for (int int1 = 0; int1 < this.loadList.size(); ++int1) {
			String string = this.getRelativeFile((String)this.loadList.get(int1));
			File file = (new File((String)this.loadList.get(int1))).getAbsoluteFile();
			String string2 = file.getAbsolutePath();
			if (file.isDirectory()) {
				string2 = string2 + File.separator;
			}

			this.ActiveFileMap.put(string.toLowerCase(Locale.ENGLISH), string2);
		}

		this.loadList.clear();
	}

	public String getGameModeCacheDir() {
		if (Core.GameMode == null) {
			Core.GameMode = "Sandbox";
		}

		String string = this.getSaveDir();
		return string + File.separator + Core.GameMode + File.separator;
	}

	public String getFileNameInCurrentSave(String string) {
		String string2 = this.getGameModeCacheDir();
		return string2 + File.separator + Core.GameSaveWorld + File.separator + string;
	}

	public String getFileNameInCurrentSave(String string, String string2) {
		return this.getFileNameInCurrentSave(string + File.separator + string2);
	}

	public String getFileNameInCurrentSave(String string, String string2, String string3) {
		return this.getFileNameInCurrentSave(string + File.separator + string2 + File.separator + string3);
	}

	public File getFileInCurrentSave(String string) {
		return new File(this.getFileNameInCurrentSave(string));
	}

	public File getFileInCurrentSave(String string, String string2) {
		return new File(this.getFileNameInCurrentSave(string, string2));
	}

	public File getFileInCurrentSave(String string, String string2, String string3) {
		return new File(this.getFileNameInCurrentSave(string, string2, string3));
	}

	public String getSaveDir() {
		String string = this.getCacheDirSub("Saves");
		ensureFolderExists(string);
		return string;
	}

	public String getSaveDirSub(String string) {
		String string2 = this.getSaveDir();
		return string2 + File.separator + string;
	}

	public String getScreenshotDir() {
		String string = this.getCacheDirSub("Screenshots");
		ensureFolderExists(string);
		return string;
	}

	public String getScreenshotDirSub(String string) {
		String string2 = this.getScreenshotDir();
		return string2 + File.separator + string;
	}

	public void setCacheDir(String string) {
		string = string.replace("/", File.separator);
		this.cacheDir = (new File(string)).getAbsoluteFile();
		ensureFolderExists(this.cacheDir);
	}

	public String getCacheDir() {
		if (this.cacheDir == null) {
			String string = System.getProperty("deployment.user.cachedir");
			if (string == null || System.getProperty("os.name").startsWith("Win")) {
				string = System.getProperty("user.home");
			}

			String string2 = string + File.separator + "Zomboid";
			this.setCacheDir(string2);
		}

		return this.cacheDir.getPath();
	}

	public String getCacheDirSub(String string) {
		String string2 = this.getCacheDir();
		return string2 + File.separator + string;
	}

	public String getMessagingDir() {
		String string = this.getCacheDirSub("messaging");
		ensureFolderExists(string);
		return string;
	}

	public String getMessagingDirSub(String string) {
		String string2 = this.getMessagingDir();
		return string2 + File.separator + string;
	}

	public File getMediaRootFile() {
		assert this.workdir != null;
		return this.workdir;
	}

	public String getMediaRootPath() {
		return this.workdir.getPath();
	}

	public File getMediaFile(String string) {
		assert this.workdir != null;
		return new File(this.workdir, string);
	}

	public String getMediaPath(String string) {
		return this.getMediaFile(string).getPath();
	}

	public String getAbsoluteWorkDir() {
		return this.workdir.getPath();
	}

	public String getLocalWorkDir() {
		return this.localWorkdir.getPath();
	}

	public String getLocalWorkDirSub(String string) {
		String string2 = this.getLocalWorkDir();
		return string2 + File.separator + string;
	}

	public String getAnimSetsPath() {
		return this.animSets.getPath();
	}

	public String getActionGroupsPath() {
		return this.actiongroups.getPath();
	}

	public static boolean ensureFolderExists(String string) {
		return ensureFolderExists((new File(string)).getAbsoluteFile());
	}

	public static boolean ensureFolderExists(File file) {
		return file.exists() || file.mkdirs();
	}

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
				String string2 = file.getAbsolutePath();
				this.searchFolders(new File(string2 + File.separator + stringArray[int1]));
			}
		} else {
			this.loadList.add(file.getAbsolutePath().replace("\\", "/").replace("./", ""));
		}
	}

	public Object[] getAllPathsContaining(String string) {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.ActiveFileMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (((String)entry.getKey()).contains(string)) {
				arrayList.add((String)entry.getValue());
			}
		}

		return arrayList.toArray();
	}

	public Object[] getAllPathsContaining(String string, String string2) {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.ActiveFileMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (((String)entry.getKey()).contains(string) && ((String)entry.getKey()).contains(string2)) {
				arrayList.add((String)entry.getValue());
			}
		}

		return arrayList.toArray();
	}

	public synchronized String getString(String string) {
		if (this.IgnoreActiveFileMap) {
			return string;
		} else {
			String string2 = string.toLowerCase(Locale.ENGLISH);
			String string3 = (String)this.RelativeMap.get(string2);
			String string4;
			if (string3 != null) {
				string2 = string3;
			} else {
				string4 = string2;
				string2 = this.getRelativeFile(string);
				string2 = string2.toLowerCase(Locale.ENGLISH);
				this.RelativeMap.put(string4, string2);
			}

			string4 = (String)this.ActiveFileMap.get(string2);
			return string4 != null ? string4 : string;
		}
	}

	public String getAbsolutePath(String string) {
		String string2 = string.toLowerCase(Locale.ENGLISH);
		return (String)this.ActiveFileMap.get(string2);
	}

	public void Reset() {
		this.loadList.clear();
		this.ActiveFileMap.clear();
		this.modIdToDir.clear();
		this.modDirToMod.clear();
		this.mods.clear();
		this.modFolders = null;
		ActiveMods.Reset();
		if (this.m_fileGuidTable != null) {
			this.m_fileGuidTable.clear();
			this.m_fileGuidTable = null;
		}
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
				String string = (String)arrayList2.get(int1);
				File file = new File(string + File.separator + "Contents" + File.separator + "mods");
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
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						if (path2.getFileName().toString().toLowerCase().equals("examplemod")) {
							DebugLog.Mod.println("refusing to list " + path2.getFileName());
						} else {
							String string2 = path2.toAbsolutePath().toString();
							if (!this.m_watchedModFolders.contains(string2)) {
								this.m_watchedModFolders.add(string2);
								DebugFileWatcher.instance.addDirectory(string2);
								Path path3 = path2.resolve("media");
								if (Files.exists(path3, new LinkOption[0])) {
									DebugFileWatcher.instance.addDirectoryRecurse(path3.toAbsolutePath().toString());
								}
							}

							list.add(string2);
						}
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
			String string;
			for (int1 = 0; int1 < this.modFoldersOrder.size(); ++int1) {
				string = (String)this.modFoldersOrder.get(int1);
				if ("workshop".equals(string)) {
					this.getStagedItemModsFolders(arrayList);
				}

				if ("steam".equals(string)) {
					this.getInstalledItemModsFolders(arrayList);
				}

				if ("mods".equals(string)) {
					String string2 = Core.getMyDocumentFolder();
					arrayList.add(string2 + File.separator + "mods");
				}
			}

			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				string = (String)arrayList.get(int1);
				if (!this.m_watchedModFolders.contains(string)) {
					this.m_watchedModFolders.add(string);
					DebugFileWatcher.instance.addDirectory(string);
				}

				this.getAllModFoldersAux(string, this.modFolders);
			}

			DebugFileWatcher.instance.add(this.m_modFileWatcher);
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
					File[] fileArray2 = fileArray;
					int int1 = fileArray.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						File file2 = fileArray2[int2];
						if (file2.isDirectory()) {
							ChooseGameInfo.Mod mod = ChooseGameInfo.readModInfo(file2.getAbsolutePath());
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

	public ChooseGameInfo.Mod searchForModInfo(File file, String string, ArrayList arrayList) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			if (stringArray == null) {
				return null;
			}

			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string2 = file.getAbsolutePath();
				File file2 = new File(string2 + File.separator + stringArray[int1]);
				ChooseGameInfo.Mod mod = this.searchForModInfo(file2, string, arrayList);
				if (mod != null) {
					return mod;
				}
			}
		} else if (file.getAbsolutePath().endsWith("mod.info")) {
			ChooseGameInfo.Mod mod2 = ChooseGameInfo.readModInfo(file.getAbsoluteFile().getParent());
			if (mod2 == null) {
				return null;
			}

			if (!StringUtils.isNullOrWhitespace(mod2.getId())) {
				this.modIdToDir.put(mod2.getId(), mod2.getDir());
				arrayList.add(mod2);
			}

			if (mod2.getId().equals(string)) {
				return mod2;
			}
		}

		return null;
	}

	public void loadMod(String string) {
		if (this.getModDir(string) != null) {
			DebugLog.Mod.println("loading " + string);
			File file = new File(this.getModDir(string));
			URI uRI = file.toURI();
			this.loadList.clear();
			this.searchFolders(file);
			for (int int1 = 0; int1 < this.loadList.size(); ++int1) {
				String string2 = this.getRelativeFile(uRI, (String)this.loadList.get(int1));
				string2 = string2.toLowerCase(Locale.ENGLISH);
				if (this.ActiveFileMap.containsKey(string2) && !string2.endsWith("mod.info") && !string2.endsWith("poster.png")) {
					DebugLog.Mod.println("mod \"" + string + "\" overrides " + string2);
				}

				this.ActiveFileMap.put(string2, (new File((String)this.loadList.get(int1))).getAbsolutePath());
			}

			this.loadList.clear();
		}
	}

	private ArrayList readLoadedDotTxt() {
		String string = Core.getMyDocumentFolder();
		String string2 = string + File.separator + "mods" + File.separator + "loaded.txt";
		File file = new File(string2);
		if (!file.exists()) {
			return null;
		} else {
			ArrayList arrayList = new ArrayList();
			try {
				FileReader fileReader = new FileReader(string2);
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					try {
						for (String string3 = bufferedReader.readLine(); string3 != null; string3 = bufferedReader.readLine()) {
							string3 = string3.trim();
							if (!string3.isEmpty()) {
								arrayList.add(string3);
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
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				arrayList = null;
			}

			try {
				file.delete();
			} catch (Exception exception2) {
				ExceptionLogger.logException(exception2);
			}

			return arrayList;
		}
	}

	private ActiveMods readDefaultModsTxt() {
		ActiveMods activeMods = ActiveMods.getById("default");
		ArrayList arrayList = this.readLoadedDotTxt();
		if (arrayList != null) {
			activeMods.getMods().addAll(arrayList);
			this.saveModsFile();
		}

		activeMods.clear();
		String string = Core.getMyDocumentFolder();
		String string2 = string + File.separator + "mods" + File.separator + "default.txt";
		try {
			ActiveModsFile activeModsFile = new ActiveModsFile();
			if (activeModsFile.read(string2, activeMods)) {
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		return activeMods;
	}

	public void loadMods(String string) {
		if (Core.OptionModsEnabled) {
			if (GameClient.bClient) {
				ArrayList arrayList = new ArrayList();
				this.loadTranslationMods(arrayList);
				arrayList.addAll(GameClient.instance.ServerMods);
				this.loadMods(arrayList);
			} else {
				ActiveMods activeMods = ActiveMods.getById(string);
				if (!"default".equalsIgnoreCase(string)) {
					ActiveMods.setLoadedMods(activeMods);
					this.loadMods(activeMods.getMods());
				} else {
					try {
						activeMods = this.readDefaultModsTxt();
						activeMods.checkMissingMods();
						activeMods.checkMissingMaps();
						ActiveMods.setLoadedMods(activeMods);
						this.loadMods(activeMods.getMods());
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				}
			}
		}
	}

	private boolean isTranslationMod(String string) {
		ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
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

	private void loadTranslationMods(ArrayList arrayList) {
		if (GameClient.bClient) {
			ActiveMods activeMods = this.readDefaultModsTxt();
			ArrayList arrayList2 = new ArrayList();
			if (this.loadModsAux(activeMods.getMods(), arrayList2) == null) {
				Iterator iterator = arrayList2.iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					if (this.isTranslationMod(string)) {
						DebugLog.Mod.println("loading translation mod \"" + string + "\"");
						if (!arrayList.contains(string)) {
							arrayList.add(string);
						}
					}
				}
			}
		}
	}

	private String loadModAndRequired(String string, ArrayList arrayList) {
		if (string.isEmpty()) {
			return null;
		} else if (string.toLowerCase().equals("examplemod")) {
			DebugLog.Mod.warn("refusing to load " + string);
			return null;
		} else if (arrayList.contains(string)) {
			return null;
		} else {
			ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
			if (mod == null) {
				if (GameServer.bServer) {
					GameServer.ServerMods.remove(string);
				}

				DebugLog.Mod.warn("required mod \"" + string + "\" not found");
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
		return (String)this.modIdToDir.get(string);
	}

	public ChooseGameInfo.Mod getModInfoForDir(String string) {
		ChooseGameInfo.Mod mod = (ChooseGameInfo.Mod)this.modDirToMod.get(string);
		if (mod == null) {
			mod = new ChooseGameInfo.Mod(string);
			this.modDirToMod.put(string, mod);
		}

		return mod;
	}

	public String getRelativeFile(File file) {
		return this.getRelativeFile(this.baseURI, file.getAbsolutePath());
	}

	public String getRelativeFile(String string) {
		return this.getRelativeFile(this.baseURI, string);
	}

	public String getRelativeFile(URI uRI, File file) {
		return this.getRelativeFile(uRI, file.getAbsolutePath());
	}

	public String getRelativeFile(URI uRI, String string) {
		URI uRI2 = (new File(string)).getAbsoluteFile().toURI();
		URI uRI3 = uRI.relativize(uRI2);
		if (uRI3.equals(uRI2)) {
			return string;
		} else {
			String string2 = uRI3.getPath();
			if (string.endsWith("/") && !string2.endsWith("/")) {
				string2 = string2 + "/";
			}

			return string2;
		}
	}

	public String getAnimName(URI uRI, File file) {
		String string = this.getRelativeFile(uRI, file);
		String string2 = string.toLowerCase(Locale.ENGLISH);
		int int1 = string2.lastIndexOf(46);
		if (int1 > -1) {
			string2 = string2.substring(0, int1);
		}

		if (string2.startsWith("anims/")) {
			string2 = string2.substring("anims/".length());
		} else if (string2.startsWith("anims_x/")) {
			string2 = string2.substring("anims_x/".length());
		}

		return string2;
	}

	public String resolveRelativePath(String string, String string2) {
		Path path = Paths.get(string);
		Path path2 = path.getParent();
		Path path3 = path2.resolve(string2);
		String string3 = path3.toString();
		string3 = this.getRelativeFile(string3);
		return string3;
	}

	public void saveModsFile() {
		try {
			String string = Core.getMyDocumentFolder();
			ensureFolderExists(string + File.separator + "mods");
			string = Core.getMyDocumentFolder();
			String string2 = string + File.separator + "mods" + File.separator + "default.txt";
			ActiveModsFile activeModsFile = new ActiveModsFile();
			activeModsFile.write(string2, ActiveMods.getById("default"));
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void loadModPackFiles() {
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getPacks().iterator();
					while (iterator2.hasNext()) {
						ChooseGameInfo.PackFile packFile = (ChooseGameInfo.PackFile)iterator2.next();
						String string2 = this.getRelativeFile("media/texturepacks/" + packFile.name + ".pack");
						string2 = string2.toLowerCase(Locale.ENGLISH);
						if (!this.ActiveFileMap.containsKey(string2)) {
							DebugLog.Mod.warn("pack file \"" + packFile.name + "\" needed by " + string + " not found");
						} else {
							String string3 = instance.getString("media/texturepacks/" + packFile.name + ".pack");
							if (!this.LoadedPacks.contains(string3)) {
								GameWindow.LoadTexturePack(packFile.name, packFile.flags, string);
								this.LoadedPacks.add(string3);
							}
						}
					}
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		GameWindow.setTexturePackLookup();
	}

	public void loadModTileDefs() {
		HashSet hashSet = new HashSet();
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getTileDefs().iterator();
					while (iterator2.hasNext()) {
						ChooseGameInfo.TileDef tileDef = (ChooseGameInfo.TileDef)iterator2.next();
						if (hashSet.contains(tileDef.fileNumber)) {
							DebugLog.Mod.error("tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
						} else {
							String string2 = tileDef.name;
							String string3 = this.getRelativeFile("media/" + string2 + ".tiles");
							string3 = string3.toLowerCase(Locale.ENGLISH);
							if (!this.ActiveFileMap.containsKey(string3)) {
								DebugLog.Mod.error("tiledef file \"" + tileDef.name + "\" needed by " + string + " not found");
							} else {
								string2 = (String)this.ActiveFileMap.get(string3);
								IsoWorld.instance.LoadTileDefinitions(IsoSpriteManager.instance, string2, tileDef.fileNumber);
								hashSet.add(tileDef.fileNumber);
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
		Iterator iterator = this.mods.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
				if (mod != null) {
					Iterator iterator2 = mod.getTileDefs().iterator();
					while (iterator2.hasNext()) {
						ChooseGameInfo.TileDef tileDef = (ChooseGameInfo.TileDef)iterator2.next();
						if (hashSet.contains(tileDef.fileNumber)) {
							DebugLog.Mod.error("tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
						} else {
							String string2 = tileDef.name;
							String string3 = this.getRelativeFile("media/" + string2 + ".tiles");
							string3 = string3.toLowerCase(Locale.ENGLISH);
							if (!this.ActiveFileMap.containsKey(string3)) {
								DebugLog.Mod.error("tiledef file \"" + tileDef.name + "\" needed by " + string + " not found");
							} else {
								string2 = (String)this.ActiveFileMap.get(string3);
								IsoWorld.instance.LoadTileDefinitionsPropertyStrings(IsoSpriteManager.instance, string2, tileDef.fileNumber);
								hashSet.add(tileDef.fileNumber);
							}
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void loadFileGuidTable() {
		File file = instance.getMediaFile("fileGuidTable.xml");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{FileGuidTable.class});
				Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
				this.m_fileGuidTable = (FileGuidTable)unmarshaller.unmarshal(fileInputStream);
				this.m_fileGuidTable.setModID("game");
			} catch (Throwable throwable) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			fileInputStream.close();
		} catch (IOException | JAXBException error) {
			System.err.println("Failed to load file Guid table.");
			ExceptionLogger.logException(error);
			return;
		}

		try {
			JAXBContext jAXBContext2 = JAXBContext.newInstance(new Class[]{FileGuidTable.class});
			Unmarshaller unmarshaller2 = jAXBContext2.createUnmarshaller();
			Iterator iterator = this.getModIDs().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
				if (mod != null) {
					try {
						String string2 = this.getModDir(string);
						FileInputStream fileInputStream2 = new FileInputStream(string2 + "/media/fileGuidTable.xml");
						try {
							FileGuidTable fileGuidTable = (FileGuidTable)unmarshaller2.unmarshal(fileInputStream2);
							fileGuidTable.setModID(string);
							this.m_fileGuidTable.mergeFrom(fileGuidTable);
						} catch (Throwable throwable3) {
							try {
								fileInputStream2.close();
							} catch (Throwable throwable4) {
								throwable3.addSuppressed(throwable4);
							}

							throw throwable3;
						}

						fileInputStream2.close();
					} catch (FileNotFoundException fileNotFoundException) {
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				}
			}
		} catch (Exception exception2) {
			ExceptionLogger.logException(exception2);
		}

		this.m_fileGuidTable.loaded();
		if (!this.m_fileGuidTableWatcherActive) {
			DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/fileGuidTable.xml", (filex)->{
				this.loadFileGuidTable();
			}));

			this.m_fileGuidTableWatcherActive = true;
		}
	}

	public FileGuidTable getFileGuidTable() {
		if (this.m_fileGuidTable == null) {
			this.loadFileGuidTable();
		}

		return this.m_fileGuidTable;
	}

	public String getFilePathFromGuid(String string) {
		FileGuidTable fileGuidTable = this.getFileGuidTable();
		return fileGuidTable != null ? fileGuidTable.getFilePathFromGuid(string) : null;
	}

	public String getGuidFromFilePath(String string) {
		FileGuidTable fileGuidTable = this.getFileGuidTable();
		return fileGuidTable != null ? fileGuidTable.getGuidFromFilePath(string) : null;
	}

	public String resolveFileOrGUID(String string) {
		String string2 = string;
		String string3 = this.getFilePathFromGuid(string);
		if (string3 != null) {
			string2 = string3;
		}

		String string4 = string2.toLowerCase(Locale.ENGLISH);
		return this.ActiveFileMap.containsKey(string4) ? (String)this.ActiveFileMap.get(string4) : string2;
	}

	public boolean isValidFilePathGuid(String string) {
		return this.getFilePathFromGuid(string) != null;
	}

	public static File[] listAllDirectories(String string, FileFilter fileFilter, boolean boolean1) {
		File file = (new File(string)).getAbsoluteFile();
		return listAllDirectories(file, fileFilter, boolean1);
	}

	public static File[] listAllDirectories(File file, FileFilter fileFilter, boolean boolean1) {
		if (!file.isDirectory()) {
			return new File[0];
		} else {
			ArrayList arrayList = new ArrayList();
			listAllDirectoriesInternal(file, fileFilter, boolean1, arrayList);
			return (File[])arrayList.toArray(new File[0]);
		}
	}

	private static void listAllDirectoriesInternal(File file, FileFilter fileFilter, boolean boolean1, ArrayList arrayList) {
		File[] fileArray = file.listFiles();
		if (fileArray != null) {
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray2[int2];
				if (!file2.isFile() && file2.isDirectory()) {
					if (fileFilter.accept(file2)) {
						arrayList.add(file2);
					}

					if (boolean1) {
						listAllFilesInternal(file2, fileFilter, true, arrayList);
					}
				}
			}
		}
	}

	public static File[] listAllFiles(String string, FileFilter fileFilter, boolean boolean1) {
		File file = (new File(string)).getAbsoluteFile();
		return listAllFiles(file, fileFilter, boolean1);
	}

	public static File[] listAllFiles(File file, FileFilter fileFilter, boolean boolean1) {
		if (!file.isDirectory()) {
			return new File[0];
		} else {
			ArrayList arrayList = new ArrayList();
			listAllFilesInternal(file, fileFilter, boolean1, arrayList);
			return (File[])arrayList.toArray(new File[0]);
		}
	}

	private static void listAllFilesInternal(File file, FileFilter fileFilter, boolean boolean1, ArrayList arrayList) {
		File[] fileArray = file.listFiles();
		if (fileArray != null) {
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray2[int2];
				if (file2.isFile()) {
					if (fileFilter.accept(file2)) {
						arrayList.add(file2);
					}
				} else if (file2.isDirectory() && boolean1) {
					listAllFilesInternal(file2, fileFilter, true, arrayList);
				}
			}
		}
	}

	public void walkGameAndModFiles(String string, boolean boolean1, ZomboidFileSystem.IWalkFilesVisitor iWalkFilesVisitor) {
		this.walkGameAndModFilesInternal(this.base, string, boolean1, iWalkFilesVisitor);
		ArrayList arrayList = this.getModIDs();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = this.getModDir((String)arrayList.get(int1));
			if (string2 != null) {
				this.walkGameAndModFilesInternal(new File(string2), string, boolean1, iWalkFilesVisitor);
			}
		}
	}

	private void walkGameAndModFilesInternal(File file, String string, boolean boolean1, ZomboidFileSystem.IWalkFilesVisitor iWalkFilesVisitor) {
		File file2 = new File(file, string);
		if (file2.isDirectory()) {
			File[] fileArray = file2.listFiles();
			if (fileArray != null) {
				File[] fileArray2 = fileArray;
				int int1 = fileArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					File file3 = fileArray2[int2];
					iWalkFilesVisitor.visit(file3, string);
					if (boolean1 && file3.isDirectory()) {
						this.walkGameAndModFilesInternal(file, string + "/" + file3.getName(), true, iWalkFilesVisitor);
					}
				}
			}
		}
	}

	public String[] resolveAllDirectories(String string, FileFilter fileFilter, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		this.walkGameAndModFiles(string, boolean1, (fileFilterx,boolean1x)->{
			if (fileFilterx.isDirectory() && fileFilter.accept(fileFilterx)) {
				String arrayListx = boolean1x + "/" + fileFilterx.getName();
				if (!arrayList.contains(arrayListx)) {
					arrayList.add(arrayListx);
				}
			}
		});
		return (String[])arrayList.toArray(new String[0]);
	}

	public String[] resolveAllFiles(String string, FileFilter fileFilter, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		this.walkGameAndModFiles(string, boolean1, (fileFilterx,boolean1x)->{
			if (fileFilterx.isFile() && fileFilter.accept(fileFilterx)) {
				String arrayListx = boolean1x + "/" + fileFilterx.getName();
				if (!arrayList.contains(arrayListx)) {
					arrayList.add(arrayListx);
				}
			}
		});
		return (String[])arrayList.toArray(new String[0]);
	}

	public String normalizeFolderPath(String string) {
		string = string.toLowerCase(Locale.ENGLISH).replace('\\', '/');
		string = string + "/";
		string = string.replace("///", "/").replace("//", "/");
		return string;
	}

	public static String processFilePath(String string, char char1) {
		if (char1 != '\\') {
			string = string.replace('\\', char1);
		}

		if (char1 != '/') {
			string = string.replace('/', char1);
		}

		return string;
	}

	public boolean tryDeleteFile(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return false;
		} else {
			try {
				return this.deleteFile(string);
			} catch (AccessControlException | IOException error) {
				ExceptionLogger.logException(error, String.format("Failed to delete file: \"%s\"", string), DebugLog.FileIO, LogSeverity.General);
				return false;
			}
		}
	}

	public boolean deleteFile(String string) throws IOException {
		File file = (new File(string)).getAbsoluteFile();
		if (!file.isFile()) {
			throw new FileNotFoundException(String.format("File path not found: \"%s\"", string));
		} else if (file.delete()) {
			DebugLog.FileIO.debugln("File deleted successfully: \"%s\"", string);
			return true;
		} else {
			DebugLog.FileIO.debugln("Failed to delete file: \"%s\"", string);
			return false;
		}
	}

	public void update() {
		if (this.m_modsChangedTime != 0L) {
			long long1 = System.currentTimeMillis();
			if (this.m_modsChangedTime <= long1) {
				this.m_modsChangedTime = 0L;
				this.modFolders = null;
				this.modIdToDir.clear();
				this.modDirToMod.clear();
				ChooseGameInfo.Reset();
				Iterator iterator = this.getModIDs().iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					ChooseGameInfo.getModDetails(string);
				}

				LuaEventManager.triggerEvent("OnModsModified");
			}
		}
	}

	private boolean isModFile(String string) {
		if (this.m_modsChangedTime > 0L) {
			return false;
		} else if (this.modFolders == null) {
			return false;
		} else {
			string = string.toLowerCase().replace('\\', '/');
			if (string.endsWith("/mods/default.txt")) {
				return false;
			} else {
				for (int int1 = 0; int1 < this.modFolders.size(); ++int1) {
					String string2 = ((String)this.modFolders.get(int1)).toLowerCase().replace('\\', '/');
					if (string.startsWith(string2)) {
						return true;
					}
				}

				return false;
			}
		}
	}

	private void onModFileChanged(String string) {
		this.m_modsChangedTime = System.currentTimeMillis() + 2000L;
	}

	public void cleanMultiplayerSaves() {
		DebugLog.FileIO.println("Start cleaning save fs");
		String string = this.getSaveDir();
		String string2 = string + File.separator + "Multiplayer" + File.separator;
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdir();
		}

		try {
			File[] fileArray = file.listFiles();
			File[] fileArray2 = fileArray;
			int int1 = fileArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				File file2 = fileArray2[int2];
				DebugLog.FileIO.println("Checking " + file2.getAbsoluteFile() + " dir");
				if (file2.isDirectory()) {
					String string3 = file2.toString();
					File file3 = new File(string3 + File.separator + "map.bin");
					if (file3.exists()) {
						DebugLog.FileIO.println("Processing " + file2.getAbsoluteFile() + " dir");
						try {
							Stream stream = Files.walk(file2.toPath());
							stream.forEach((var0)->{
								if (var0.getFileName().toString().matches("map_\\d+_\\d+.bin")) {
									DebugLog.FileIO.println("Delete " + var0.getFileName().toString());
									var0.toFile().delete();
								}
							});
						} catch (IOException ioException) {
							throw new RuntimeException(ioException);
						}
					}
				}
			}
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
		}
	}

	public void resetDefaultModsForNewRelease(String string) {
		ensureFolderExists(this.getCacheDirSub("mods"));
		String string2 = this.getCacheDirSub("mods");
		String string3 = string2 + File.separator + "reset-mods-" + string + ".txt";
		File file = new File(string3);
		if (!file.exists()) {
			try {
				FileWriter fileWriter = new FileWriter(file);
				try {
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					try {
						String string4 = "If this file does not exist, default.txt will be reset to empty (no mods active).";
						bufferedWriter.write(string4);
					} catch (Throwable throwable) {
						try {
							bufferedWriter.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedWriter.close();
				} catch (Throwable throwable3) {
					try {
						fileWriter.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileWriter.close();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return;
			}

			ActiveMods activeMods = ActiveMods.getById("default");
			activeMods.clear();
			this.saveModsFile();
		}
	}

	public interface IWalkFilesVisitor {

		void visit(File file, String string);
	}
}
