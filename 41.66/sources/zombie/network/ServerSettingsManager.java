package zombie.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;


public class ServerSettingsManager {
	public static final ServerSettingsManager instance = new ServerSettingsManager();
	protected ArrayList settings = new ArrayList();
	protected ArrayList suffixes = new ArrayList();

	public String getSettingsFolder() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		return string + File.separator + "Server";
	}

	public String getNameInSettingsFolder(String string) {
		String string2 = this.getSettingsFolder();
		return string2 + File.separator + string;
	}

	public void readAllSettings() {
		this.settings.clear();
		File file = new File(this.getSettingsFolder());
		if (!file.exists()) {
			file.mkdirs();
		} else {
			Filter filter = new Filter(){
				
				public boolean accept(Path file) throws IOException {
					String filter = file.getFileName().toString();
					return !Files.isDirectory(file, new LinkOption[0]) && filter.endsWith(".ini") && !filter.endsWith("_zombies.ini") && ServerSettingsManager.this.isValidName(filter.replace(".ini", ""));
				}
			};

			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath(), filter);
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path = (Path)iterator.next();
						ServerSettings serverSettings = new ServerSettings(path.getFileName().toString().replace(".ini", ""));
						this.settings.add(serverSettings);
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
				ExceptionLogger.logException(exception);
			}
		}
	}

	public int getSettingsCount() {
		return this.settings.size();
	}

	public ServerSettings getSettingsByIndex(int int1) {
		return int1 >= 0 && int1 < this.settings.size() ? (ServerSettings)this.settings.get(int1) : null;
	}

	public boolean isValidName(String string) {
		if (string != null && !string.isEmpty()) {
			if (!string.contains("/") && !string.contains("\\") && !string.contains(":") && !string.contains(";") && !string.contains("\"") && !string.contains(".")) {
				return !string.contains("_zombies");
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean anyFilesExist(String string) {
		this.getSuffixes();
		for (int int1 = 0; int1 < this.suffixes.size(); ++int1) {
			String string2 = this.getSettingsFolder();
			File file = new File(string2 + File.separator + string + (String)this.suffixes.get(int1));
			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	public boolean isValidNewName(String string) {
		if (!this.isValidName(string)) {
			return false;
		} else {
			return !this.anyFilesExist(string);
		}
	}

	public ArrayList getSuffixes() {
		if (this.suffixes.isEmpty()) {
			this.suffixes.add(".ini");
			this.suffixes.add("_SandboxVars.lua");
			this.suffixes.add("_spawnpoints.lua");
			this.suffixes.add("_spawnregions.lua");
			this.suffixes.add("_zombies.ini");
		}

		return this.suffixes;
	}
}
