package zombie.core.logger;

import java.io.File;
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
import zombie.GameWindow;
import zombie.core.Core;
import zombie.network.MD5Checksum;


public class ZipLogs {
	static ArrayList filePaths = new ArrayList();

	public static void addZipFile(boolean boolean1) {
		FileSystem fileSystem = null;
		try {
			String string = GameWindow.getCacheDir() + File.separator + "logs.zip";
			String string2 = (new File(string)).toURI().toString();
			URI uRI = URI.create("jar:" + string2);
			Path path = FileSystems.getDefault().getPath(string).toAbsolutePath();
			HashMap hashMap = new HashMap();
			hashMap.put("create", String.valueOf(Files.notExists(path, new LinkOption[0])));
			try {
				fileSystem = FileSystems.newFileSystem(uRI, hashMap);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}

			long long1 = getMD5FromZip(fileSystem, "/meta/console.txt.md5");
			long long2 = getMD5FromZip(fileSystem, "/meta/coop-console.txt.md5");
			long long3 = getMD5FromZip(fileSystem, "/meta/server-console.txt.md5");
			addLogToZip(fileSystem, "console", "console.txt", long1);
			addLogToZip(fileSystem, "coop-console", "coop-console.txt", long2);
			addLogToZip(fileSystem, "server-console", "server-console.txt", long3);
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
			if (boolean1) {
				addSaveToZip(fileSystem, "/save/map_t.bin", "map_t.bin");
				addSaveToZip(fileSystem, "/save/map_ver.bin", "map_ver.bin");
				addSaveToZip(fileSystem, "/save/map.bin", "map.bin");
				addSaveToZip(fileSystem, "/save/map_sand.bin", "map_sand.bin");
				addSaveToZip(fileSystem, "/save/reanimated.bin", "reanimated.bin");
				addSaveToZip(fileSystem, "/save/zombies.ini", "zombies.ini");
				addSaveToZip(fileSystem, "/save/descriptors.bin", "descriptors.bin");
				addSaveToZip(fileSystem, "/save/map_p.bin", "map_p.bin");
				addSaveToZip(fileSystem, "/save/map_meta.bin", "map_meta.bin");
				addSaveToZip(fileSystem, "/save/map_zone.bin", "map_zone.bin");
				addSaveToZip(fileSystem, "/save/serverid.dat", "serverid.dat");
				addSaveToZip(fileSystem, "/save/thumb.png", "thumb.png");
			}

			try {
				fileSystem.close();
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		} catch (Exception exception) {
			if (fileSystem != null) {
				try {
					fileSystem.close();
				} catch (IOException ioException3) {
					ioException3.printStackTrace();
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

	private static void addToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			Files.createDirectories(path.getParent());
			Path path2 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + string2).toAbsolutePath();
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
			Path path2 = FileSystems.getDefault().getPath(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + string2).toAbsolutePath();
			Files.deleteIfExists(path);
			if (Files.exists(path2, new LinkOption[0])) {
				Files.copy(path2, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void addDirToZip(FileSystem fileSystem, String string, String string2) {
		try {
			Path path = fileSystem.getPath(string);
			deleteDirectory(fileSystem, path);
			Files.createDirectories(path);
			Path path2 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + string2).toAbsolutePath();
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
			Path path2 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + string2).toAbsolutePath();
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
			Path path2 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + string2).toAbsolutePath();
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
			ioException.printStackTrace();
		}
	}

	private static void addLogToZip(FileSystem fileSystem, String string, String string2, long long1) {
		long long2;
		try {
			long2 = MD5Checksum.createChecksum(GameWindow.getCacheDir() + File.separator + string2);
		} catch (Exception exception) {
			long2 = 0L;
		}

		File file = new File(GameWindow.getCacheDir() + File.separator + string2);
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
				path2 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + string2).toAbsolutePath();
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
}
