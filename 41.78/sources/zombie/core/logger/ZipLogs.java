package zombie.core.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipError;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.MD5Checksum;


public final class ZipLogs {
	static ArrayList filePaths = new ArrayList();

	public static void addZipFile(boolean boolean1) {
		FileSystem fileSystem = null;
		try {
			String string = ZomboidFileSystem.instance.getCacheDir();
			String string2 = string + File.separator + "logs.zip";
			String string3 = (new File(string2)).toURI().toString();
			URI uRI = URI.create("jar:" + string3);
			Path path = FileSystems.getDefault().getPath(string2).toAbsolutePath();
			HashMap hashMap = new HashMap();
			hashMap.put("create", String.valueOf(Files.notExists(path, new LinkOption[0])));
			try {
				fileSystem = FileSystems.newFileSystem(uRI, hashMap);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			} catch (ZipError zipError) {
				zipError.printStackTrace();
				DebugLog.log("Deleting possibly-corrupt " + string2);
				try {
					Files.deleteIfExists(path);
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}

				return;
			}

			long long1 = getMD5FromZip(fileSystem, "/meta/console.txt.md5");
			long long2 = getMD5FromZip(fileSystem, "/meta/coop-console.txt.md5");
			long long3 = getMD5FromZip(fileSystem, "/meta/server-console.txt.md5");
			long long4 = getMD5FromZip(fileSystem, "/meta/DebugLog.txt.md5");
			addLogToZip(fileSystem, "console", "console.txt", long1);
			addLogToZip(fileSystem, "coop-console", "coop-console.txt", long2);
			addLogToZip(fileSystem, "server-console", "server-console.txt", long3);
			addDebugLogToZip(fileSystem, "debug-log", "DebugLog.txt", long4);
			addToZip(fileSystem, "/configs/options.ini", "options.ini");
			addToZip(fileSystem, "/configs/popman-options.ini", "popman-options.ini");
			addToZip(fileSystem, "/configs/latestSave.ini", "latestSave.ini");
			addToZip(fileSystem, "/configs/debug-options.ini", "debug-options.ini");
			addToZip(fileSystem, "/configs/sounds.ini", "sounds.ini");
			addToZip(fileSystem, "/addition/translationProblems.txt", "translationProblems.txt");
			addToZip(fileSystem, "/addition/gamepadBinding.config", "gamepadBinding.config");
			addFilelistToZip(fileSystem, "/addition/mods.txt", "mods");
			addDirToZipLua(fileSystem, "/lua", "Lua");
			addDirToZip(fileSystem, "/db", "db");
			addDirToZip(fileSystem, "/server", "Server");
			addDirToZip(fileSystem, "/statistic", "Statistic");
			if (!boolean1) {
				addSaveOldToZip(fileSystem, "/save_old/map_t.bin", "map_t.bin");
				addSaveOldToZip(fileSystem, "/save_old/map_ver.bin", "map_ver.bin");
				addSaveOldToZip(fileSystem, "/save_old/map.bin", "map.bin");
				addSaveOldToZip(fileSystem, "/save_old/map_sand.bin", "map_sand.bin");
				addSaveOldToZip(fileSystem, "/save_old/reanimated.bin", "reanimated.bin");
				addSaveOldToZip(fileSystem, "/save_old/zombies.ini", "zombies.ini");
				addSaveOldToZip(fileSystem, "/save_old/z_outfits.bin", "z_outfits.bin");
				addSaveOldToZip(fileSystem, "/save_old/map_p.bin", "map_p.bin");
				addSaveOldToZip(fileSystem, "/save_old/map_meta.bin", "map_meta.bin");
				addSaveOldToZip(fileSystem, "/save_old/map_zone.bin", "map_zone.bin");
				addSaveOldToZip(fileSystem, "/save_old/serverid.dat", "serverid.dat");
				addSaveOldToZip(fileSystem, "/save_old/thumb.png", "thumb.png");
				addSaveOldToZip(fileSystem, "/save_old/players.db", "players.db");
				addSaveOldToZip(fileSystem, "/save_old/players.db-journal", "players.db-journal");
				addSaveOldToZip(fileSystem, "/save_old/vehicles.db", "vehicles.db");
				addSaveOldToZip(fileSystem, "/save_old/vehicles.db-journal", "vehicles.db-journal");
				putTextFile(fileSystem, "/save_old/description.txt", getLastSaveDescription());
			} else {
				addSaveToZip(fileSystem, "/save/map_t.bin", "map_t.bin");
				addSaveToZip(fileSystem, "/save/map_ver.bin", "map_ver.bin");
				addSaveToZip(fileSystem, "/save/map.bin", "map.bin");
				addSaveToZip(fileSystem, "/save/map_sand.bin", "map_sand.bin");
				addSaveToZip(fileSystem, "/save/reanimated.bin", "reanimated.bin");
				addSaveToZip(fileSystem, "/save/zombies.ini", "zombies.ini");
				addSaveToZip(fileSystem, "/save/z_outfits.bin", "z_outfits.bin");
				addSaveToZip(fileSystem, "/save/map_p.bin", "map_p.bin");
				addSaveToZip(fileSystem, "/save/map_meta.bin", "map_meta.bin");
				addSaveToZip(fileSystem, "/save/map_zone.bin", "map_zone.bin");
				addSaveToZip(fileSystem, "/save/serverid.dat", "serverid.dat");
				addSaveToZip(fileSystem, "/save/thumb.png", "thumb.png");
				addSaveToZip(fileSystem, "/save/players.db", "players.db");
				addSaveToZip(fileSystem, "/save/players.db-journal", "players.db-journal");
				addSaveToZip(fileSystem, "/save/vehicles.db", "vehicles.db");
				addSaveToZip(fileSystem, "/save/vehicles.db-journal", "vehicles.db-journal");
				putTextFile(fileSystem, "/save/description.txt", getCurrentSaveDescription());
			}

			try {
				fileSystem.close();
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}
		} catch (Exception exception) {
			if (fileSystem != null) {
				try {
					fileSystem.close();
				} catch (IOException ioException4) {
					ioException4.printStackTrace();
				}
			}

			exception.printStackTrace();
		}
	}

	private static void copyToZip(Path path, Path path2, Path path3) throws IOException {
		Path path4 = path.resolve(path2.relativize(path3).toString());
		if (Files.isDirectory(path3, new LinkOption[0])) {
			Files.createDirectories(path4);
		} else {
			Files.copy(path3, path4);
		}
	}

	public static void addToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			Files.createDirectories(path.getParent());
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2).toAbsolutePath();
			Files.deleteIfExists(path);
			if (Files.exists(path2, new LinkOption[0])) {
				Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void addSaveToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			Files.createDirectories(path.getParent());
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getFileNameInCurrentSave(string2)).toAbsolutePath();
			Files.deleteIfExists(path);
			if (Files.exists(path2, new LinkOption[0])) {
				Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void addSaveOldToZip(FileSystem fileSystem, String string, String string2) {
		try {
			BufferedReader bufferedReader = null;
			try {
				String string3 = ZomboidFileSystem.instance.getCacheDir();
				bufferedReader = new BufferedReader(new FileReader(new File(string3 + File.separator + "latestSave.ini")));
			} catch (FileNotFoundException fileNotFoundException) {
				return;
			}

			String string4 = bufferedReader.readLine();
			String string5 = bufferedReader.readLine();
			bufferedReader.close();
			Path path = fileSystem.getPath(string);
			Files.createDirectories(path.getParent());
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getSaveDir() + File.separator + string5 + File.separator + string4 + File.separator + string2).toAbsolutePath();
			Files.deleteIfExists(path);
			if (Files.exists(path2, new LinkOption[0])) {
				Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static String getLastSaveDescription() {
		try {
			BufferedReader bufferedReader = null;
			try {
				String string = ZomboidFileSystem.instance.getCacheDir();
				bufferedReader = new BufferedReader(new FileReader(new File(string + File.separator + "latestSave.ini")));
			} catch (FileNotFoundException fileNotFoundException) {
				return "-";
			}

			String string2 = bufferedReader.readLine();
			String string3 = bufferedReader.readLine();
			bufferedReader.close();
			return "World: " + string2 + "\n\rGameMode:" + string3;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return "-";
		}
	}

	private static String getCurrentSaveDescription() {
		String string = "Sandbox";
		if (Core.GameMode != null) {
			string = Core.GameMode;
		}

		String string2 = "-";
		if (Core.GameSaveWorld != null) {
			string2 = Core.GameSaveWorld;
		}

		return "World: " + string2 + "\n\rGameMode:" + string;
	}

	public static void addDirToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			deleteDirectory(fileSystem, path);
			Files.createDirectories(path);
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2).toAbsolutePath();
			Stream stream = Files.walk(path2);
			stream.forEach((string2x)->{
				try {
					copyToZip(path, path2, string2x);
				} catch (IOException path2x) {
					throw new RuntimeException(path2x);
				}
			});
		} catch (IOException ioException) {
		}
	}

	private static void addDirToZipLua(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			deleteDirectory(fileSystem, path);
			Files.createDirectories(path);
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2).toAbsolutePath();
			Stream stream = Files.walk(path2);
			stream.forEach((string2x)->{
				try {
					if (!string2x.endsWith("ServerList.txt") && !string2x.endsWith("ServerListSteam.txt")) {
						copyToZip(path, path2, string2x);
					}
				} catch (IOException path2x) {
					throw new RuntimeException(path2x);
				}
			});
		} catch (IOException ioException) {
		}
	}

	private static void addFilelistToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			Path path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2).toAbsolutePath();
			Stream stream = Files.list(path2);
			String string3 = (String)stream.map(Path::getFileName).map(Path::toString).collect(Collectors.joining("; "));
			Files.deleteIfExists(path);
			Files.write(path, string3.getBytes(), new OpenOption[0]);
		} catch (IOException ioException) {
		}
	}

	static void deleteDirectory(FileSystem fileSystem, Path path) {
		filePaths.clear();
		getDirectoryFiles(path);
		Iterator iterator = filePaths.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			try {
				Files.delete(fileSystem.getPath(string));
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	static void getDirectoryFiles(Path path) {
		try {
			Stream stream = Files.walk(path);
			stream.forEach((streamx)->{
				if (!streamx.toString().equals(path.toString())) {
					if (Files.isDirectory(streamx, new LinkOption[0])) {
						getDirectoryFiles(streamx);
					} else if (!filePaths.contains(streamx.toString())) {
						filePaths.add(streamx.toString());
					}
				}
			});

			filePaths.add(path.toString());
		} catch (IOException ioException) {
		}
	}

	private static void addLogToZip(FileSystem fileSystem, String string, String string2, long long1) {
		long long2;
		try {
			String string3 = ZomboidFileSystem.instance.getCacheDir();
			long2 = MD5Checksum.createChecksum(string3 + File.separator + string2);
		} catch (Exception exception) {
			long2 = 0L;
		}

		String string4 = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string4 + File.separator + string2);
		if (file.exists() && !file.isDirectory() && long2 != long1) {
			Path path;
			try {
				path = fileSystem.getPath("/" + string + "/log_5.txt");
				Files.delete(path);
			} catch (Exception exception2) {
			}

			Path path2;
			Path path3;
			for (int int1 = 5; int1 > 0; --int1) {
				path2 = fileSystem.getPath("/" + string + "/log_" + int1 + ".txt");
				path3 = fileSystem.getPath("/" + string + "/log_" + (int1 + 1) + ".txt");
				try {
					Files.move(path2, path3);
				} catch (Exception exception3) {
				}
			}

			try {
				path = fileSystem.getPath("/" + string + "/log_1.txt");
				Files.createDirectories(path.getParent());
				path2 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string2).toAbsolutePath();
				Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
				path3 = fileSystem.getPath("/meta/" + string2 + ".md5");
				Files.createDirectories(path3.getParent());
				try {
					Files.delete(path3);
				} catch (Exception exception4) {
				}

				Files.write(path3, String.valueOf(long2).getBytes(), new OpenOption[0]);
			} catch (Exception exception5) {
				exception5.printStackTrace();
			}
		}
	}

	private static void addDebugLogToZip(FileSystem fileSystem, String string, String string2, long long1) {
		String string3 = null;
		File file = new File(LoggerManager.getLogsDir());
		String[] stringArray = file.list();
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			String string4 = stringArray[int1];
			if (string4.contains("DebugLog.txt")) {
				String string5 = LoggerManager.getLogsDir();
				string3 = string5 + File.separator + string4;
				break;
			}
		}

		if (string3 != null) {
			long long2;
			try {
				long2 = MD5Checksum.createChecksum(string3);
			} catch (Exception exception) {
				long2 = 0L;
			}

			File file2 = new File(string3);
			if (file2.exists() && !file2.isDirectory() && long2 != long1) {
				Path path;
				try {
					path = fileSystem.getPath("/" + string + "/log_5.txt");
					Files.delete(path);
				} catch (Exception exception2) {
				}

				Path path2;
				Path path3;
				for (int int2 = 5; int2 > 0; --int2) {
					path2 = fileSystem.getPath("/" + string + "/log_" + int2 + ".txt");
					path3 = fileSystem.getPath("/" + string + "/log_" + (int2 + 1) + ".txt");
					try {
						Files.move(path2, path3);
					} catch (Exception exception3) {
					}
				}

				try {
					path = fileSystem.getPath("/" + string + "/log_1.txt");
					Files.createDirectories(path.getParent());
					path2 = FileSystems.getDefault().getPath(string3).toAbsolutePath();
					Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
					path3 = fileSystem.getPath("/meta/" + string2 + ".md5");
					Files.createDirectories(path3.getParent());
					try {
						Files.delete(path3);
					} catch (Exception exception4) {
					}

					Files.write(path3, String.valueOf(long2).getBytes(), new OpenOption[0]);
				} catch (Exception exception5) {
					exception5.printStackTrace();
				}
			}
		}
	}

	private static long getMD5FromZip(FileSystem fileSystem, String string) {
		long long1 = 0L;
		try {
			Path path = fileSystem.getPath(string);
			if (Files.exists(path, new LinkOption[0])) {
				List list = Files.readAllLines(path);
				long1 = Long.parseLong((String)list.get(0));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return long1;
	}

	public static void putTextFile(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
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
}
