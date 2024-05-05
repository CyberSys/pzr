package zombie.core.backup;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


public class ZipBackup {
	private static final int compressionMethod = 0;
	static ParallelScatterZipCreator scatterZipCreator = null;
	private static long lastBackupTime = 0L;

	public static void onStartup() {
		lastBackupTime = System.currentTimeMillis();
		if (ServerOptions.getInstance().BackupsOnStart.getValue()) {
			makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.startup);
		}
	}

	public static void onVersion() {
		if (ServerOptions.getInstance().BackupsOnVersionChange.getValue()) {
			String string = ZomboidFileSystem.instance.getCacheDir();
			String string2 = string + File.separator + "backups" + File.separator + "last_server_version.txt";
			String string3 = getStringFromZip(string2);
			String string4 = Core.getInstance().getGameVersion().toString();
			if (!string4.equals(string3)) {
				putTextFile(string2, string4);
				makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.version);
			}
		}
	}

	public static void onPeriod() {
		int int1 = ServerOptions.getInstance().BackupsPeriod.getValue();
		if (int1 > 0) {
			if (System.currentTimeMillis() - lastBackupTime > (long)(int1 * '')) {
				lastBackupTime = System.currentTimeMillis();
				makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.period);
			}
		}
	}

	public static void makeBackupFile(String string, ZipBackup.BackupTypes backupTypes) {
		String string2 = ZomboidFileSystem.instance.getCacheDir();
		String string3 = string2 + File.separator + "backups" + File.separator + backupTypes.name();
		long long1 = System.currentTimeMillis();
		DebugLog.log("Start making backup to: " + string3);
		scatterZipCreator = new ParallelScatterZipCreator();
		CoopSlave.status("UI_ServerStatus_CreateBackup");
		FileOutputStream fileOutputStream = null;
		ZipArchiveOutputStream zipArchiveOutputStream = null;
		try {
			File file = new File(string3);
			if (!file.exists()) {
				file.mkdirs();
			}

			rotateBackupFile(backupTypes);
			String string4 = string3 + File.separator + "backup_1.zip";
			try {
				Files.deleteIfExists(Paths.get(string4));
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			File file2 = new File(string4);
			file2.delete();
			fileOutputStream = new FileOutputStream(file2);
			zipArchiveOutputStream = new ZipArchiveOutputStream(fileOutputStream);
			zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
			zipArchiveOutputStream.setMethod(0);
			zipArchiveOutputStream.setLevel(0);
			zipTextFile("readme.txt", getBackupReadme(string));
			zipArchiveOutputStream.setComment(getBackupReadme(string));
			zipFile("options.ini", "options.ini");
			zipFile("popman-options.ini", "popman-options.ini");
			zipFile("latestSave.ini", "latestSave.ini");
			zipFile("debug-options.ini", "debug-options.ini");
			zipFile("sounds.ini", "sounds.ini");
			zipFile("gamepadBinding.config", "gamepadBinding.config");
			zipDir("mods", "mods");
			zipDir("Lua", "Lua");
			zipDir("db", "db");
			zipDir("Server", "Server");
			synchronized (IsoChunk.WriteLock) {
				zipDir("Saves" + File.separator + "Multiplayer" + File.separator + string, "Saves" + File.separator + "Multiplayer" + File.separator + string);
				try {
					scatterZipCreator.writeTo(zipArchiveOutputStream);
					DebugLog.log(scatterZipCreator.getStatisticsMessage().toString());
					zipArchiveOutputStream.close();
					fileOutputStream.close();
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ioException3) {
					ioException3.printStackTrace();
				}
			}
		}

		DebugLog.log("Backup made in " + (System.currentTimeMillis() - long1) + " ms");
	}

	private static void rotateBackupFile(ZipBackup.BackupTypes backupTypes) {
		int int1 = ServerOptions.getInstance().BackupsCount.getValue() - 1;
		if (int1 > 0) {
			Path path = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + "backups" + File.separator + backupTypes + File.separator + "backup_" + (int1 + 1) + ".zip");
			try {
				Files.deleteIfExists(path);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			for (int int2 = int1; int2 > 0; --int2) {
				Path path2 = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + "backups" + File.separator + backupTypes + File.separator + "backup_" + int2 + ".zip");
				Path path3 = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + "backups" + File.separator + backupTypes + File.separator + "backup_" + (int2 + 1) + ".zip");
				try {
					Files.move(path2, path3);
				} catch (Exception exception) {
				}
			}
		}
	}

	private static String getBackupReadme(String string) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		simpleDateFormat.format(date);
		int int1 = getWorldVersion(string);
		String string2 = "";
		if (int1 == -2) {
			string2 = "World isn\'t exist";
		} else if (int1 == -1) {
			string2 = "World version cannot be determined";
		} else {
			string2 = String.valueOf(int1);
		}

		String string3 = simpleDateFormat.format(date);
		return "Backup time: " + string3 + "\nServerName: " + string + "\nCurrent server version:" + Core.getInstance().getGameVersion() + "\nCurrent world version:195\nWorld version in this backup is:" + string2;
	}

	private static int getWorldVersion(String string) {
		String string2 = ZomboidFileSystem.instance.getSaveDir();
		File file = new File(string2 + File.separator + "Multiplayer" + File.separator + string + File.separator + "map_t.bin");
		if (file.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				int int1;
				label64: {
					byte byte1;
					try {
						DataInputStream dataInputStream = new DataInputStream(fileInputStream);
						label60: {
							try {
								byte byte2 = dataInputStream.readByte();
								byte byte3 = dataInputStream.readByte();
								byte byte4 = dataInputStream.readByte();
								byte byte5 = dataInputStream.readByte();
								if (byte2 != 71 || byte3 != 77 || byte4 != 84 || byte5 != 77) {
									byte1 = -1;
									break label60;
								}

								int int2 = dataInputStream.readInt();
								int1 = int2;
							} catch (Throwable throwable) {
								try {
									dataInputStream.close();
								} catch (Throwable throwable2) {
									throwable.addSuppressed(throwable2);
								}

								throw throwable;
							}

							dataInputStream.close();
							break label64;
						}

						dataInputStream.close();
					} catch (Throwable throwable3) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}

						throw throwable3;
					}

					fileInputStream.close();
					return byte1;
				}

				fileInputStream.close();
				return int1;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return -2;
	}

	private static void putTextFile(String string, String string2) {
		try {
			Path path = Paths.get(string);
			Files.createDirectories(path.getParent());
			try {
				Files.delete(path);
			} catch (Exception exception) {
			}

			Files.write(path, string2.getBytes(), new OpenOption[0]);
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}
	}

	private static String getStringFromZip(String string) {
		String string2 = null;
		try {
			Path path = Paths.get(string);
			if (Files.exists(path, new LinkOption[0])) {
				List list = Files.readAllLines(path);
				string2 = (String)list.get(0);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return string2;
	}

	private static void zipTextFile(String string, String string2) {
		InputStreamSupplier inputStreamSupplier = ()->{
    ByteArrayInputStream string2x = new ByteArrayInputStream(string2.getBytes(StandardCharsets.UTF_8));
    return string2x;
};
		ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string);
		zipArchiveEntry.setMethod(0);
		scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
	}

	private static void zipFile(String string, String string2) {
		Path path = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2);
		if (Files.exists(path, new LinkOption[0])) {
			InputStreamSupplier inputStreamSupplier = ()->{
				InputStream string2 = null;
				try {
					string2 = Files.newInputStream(path);
				} catch (IOException inputStreamSupplier) {
					inputStreamSupplier.printStackTrace();
				}

				return string2;
			};

			ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string);
			zipArchiveEntry.setMethod(0);
			scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
		}
	}

	private static void zipDir(String string, String string2) {
		Path path = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2);
		if (Files.exists(path, new LinkOption[0])) {
			try {
				String string3 = ZomboidFileSystem.instance.getCacheDir();
				File file = new File(string3 + File.separator + string2);
				if (file.isDirectory()) {
					Iterator iterator = Arrays.asList(file.listFiles()).iterator();
					int int1 = file.getAbsolutePath().length() + 1;
					while (iterator.hasNext()) {
						File file2 = (File)iterator.next();
						if (!file2.isDirectory()) {
							String string4 = file2.getAbsolutePath().substring(int1);
							InputStreamSupplier inputStreamSupplier = ()->{
								InputStream string2 = null;
								try {
									string2 = Files.newInputStream(file2.toPath());
								} catch (IOException file) {
									file.printStackTrace();
								}

								return string2;
							};

							ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string + File.separator + string4);
							zipArchiveEntry.setMethod(0);
							scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	private static enum BackupTypes {

		period,
		startup,
		version;

		private static ZipBackup.BackupTypes[] $values() {
			return new ZipBackup.BackupTypes[]{period, startup, version};
		}
	}
}
