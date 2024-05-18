package zombie.network;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;


public class ServerSettings {
	protected String name;
	protected ServerOptions serverOptions;
	protected SandboxOptions sandboxOptions;
	protected ArrayList spawnRegions;
	protected ArrayList spawnPoints;

	public ServerSettings(String string) {
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public void resetToDefault() {
		this.serverOptions = new ServerOptions();
		this.sandboxOptions = new SandboxOptions();
		this.spawnRegions = (new SpawnRegions()).getDefaultServerRegions();
		this.spawnPoints = null;
	}

	public boolean loadFiles() {
		this.serverOptions = new ServerOptions();
		this.serverOptions.loadServerTextFile(this.name);
		this.sandboxOptions = new SandboxOptions();
		this.sandboxOptions.loadServerLuaFile(this.name);
		this.sandboxOptions.loadServerZombiesFile(this.name);
		SpawnRegions spawnRegions = new SpawnRegions();
		this.spawnRegions = spawnRegions.loadRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnregions.lua"));
		if (this.spawnRegions == null) {
			this.spawnRegions = spawnRegions.getDefaultServerRegions();
		}

		this.spawnPoints = spawnRegions.loadPointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnpoints.lua"));
		return true;
	}

	public boolean saveFiles() {
		if (this.serverOptions == null) {
			return false;
		} else {
			this.serverOptions.saveServerTextFile(this.name);
			this.sandboxOptions.saveServerLuaFile(this.name);
			if (this.spawnRegions != null) {
				(new SpawnRegions()).saveRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnregions.lua"), this.spawnRegions);
			}

			if (this.spawnPoints != null) {
				(new SpawnRegions()).savePointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnpoints.lua"), this.spawnPoints);
			}

			this.tryDeleteFile(this.name + "_zombies.ini");
			return true;
		}
	}

	private boolean tryDeleteFile(String string) {
		try {
			File file = new File(ServerSettingsManager.instance.getNameInSettingsFolder(string));
			if (file.exists()) {
				DebugLog.log("deleting " + file.getAbsolutePath());
				file.delete();
			}

			return true;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		}
	}

	public boolean deleteFiles() {
		this.tryDeleteFile(this.name + ".ini");
		this.tryDeleteFile(this.name + "_SandboxVars.lua");
		this.tryDeleteFile(this.name + "_spawnregions.lua");
		this.tryDeleteFile(this.name + "_spawnpoints.lua");
		this.tryDeleteFile(this.name + "_zombies.ini");
		return true;
	}

	public boolean duplicateFiles(String string) {
		if (!ServerSettingsManager.instance.isValidNewName(string)) {
			return false;
		} else {
			ServerSettings serverSettings = new ServerSettings(this.name);
			serverSettings.loadFiles();
			if (serverSettings.spawnRegions != null) {
				Iterator iterator = serverSettings.spawnRegions.iterator();
				while (iterator.hasNext()) {
					SpawnRegions.Region region = (SpawnRegions.Region)iterator.next();
					if (region.serverfile != null && region.serverfile.equals(this.name + "_spawnpoints.lua")) {
						region.serverfile = string + "_spawnpoints.lua";
					}
				}
			}

			serverSettings.name = string;
			serverSettings.saveFiles();
			return true;
		}
	}

	public boolean rename(String string) {
		if (!ServerSettingsManager.instance.isValidNewName(string)) {
			return false;
		} else {
			this.loadFiles();
			this.deleteFiles();
			if (this.spawnRegions != null) {
				Iterator iterator = this.spawnRegions.iterator();
				while (iterator.hasNext()) {
					SpawnRegions.Region region = (SpawnRegions.Region)iterator.next();
					if (region.serverfile != null && region.serverfile.equals(this.name + "_spawnpoints.lua")) {
						region.serverfile = string + "_spawnpoints.lua";
					}
				}
			}

			this.name = string;
			this.saveFiles();
			return true;
		}
	}

	public ServerOptions getServerOptions() {
		return this.serverOptions;
	}

	public SandboxOptions getSandboxOptions() {
		return this.sandboxOptions;
	}

	public int getNumSpawnRegions() {
		return this.spawnRegions.size();
	}

	public String getSpawnRegionName(int int1) {
		return ((SpawnRegions.Region)this.spawnRegions.get(int1)).name;
	}

	public String getSpawnRegionFile(int int1) {
		SpawnRegions.Region region = (SpawnRegions.Region)this.spawnRegions.get(int1);
		return region.file != null ? region.file : region.serverfile;
	}

	public void clearSpawnRegions() {
		this.spawnRegions.clear();
	}

	public void addSpawnRegion(String string, String string2) {
		if (string != null && string2 != null) {
			SpawnRegions.Region region = new SpawnRegions.Region();
			region.name = string;
			if (string2.startsWith("media")) {
				region.file = string2;
			} else {
				region.serverfile = string2;
			}

			this.spawnRegions.add(region);
		} else {
			throw new NullPointerException();
		}
	}

	public void removeSpawnRegion(int int1) {
		this.spawnRegions.remove(int1);
	}

	public KahluaTable loadSpawnPointsFile(String string) {
		SpawnRegions spawnRegions = new SpawnRegions();
		return spawnRegions.loadPointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(string));
	}

	public boolean saveSpawnPointsFile(String string, KahluaTable kahluaTable) {
		SpawnRegions spawnRegions = new SpawnRegions();
		return spawnRegions.savePointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(string), kahluaTable);
	}
}
