package zombie.core.logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;


public final class LoggerManager {
	private static boolean s_isInitialized = false;
	private static final HashMap s_loggers = new HashMap();

	public static synchronized ZLogger getLogger(String string) {
		if (!s_loggers.containsKey(string)) {
			createLogger(string, false);
		}

		return (ZLogger)s_loggers.get(string);
	}

	public static synchronized void init() {
		if (!s_isInitialized) {
			DebugLog.General.debugln("Initializing...");
			s_isInitialized = true;
			backupOldLogFiles();
		}
	}

	private static void backupOldLogFiles() {
		try {
			File file = new File(getLogsDir());
			String[] stringArray = file.list();
			if (stringArray == null || stringArray.length == 0) {
				return;
			}

			Calendar calendar = getLogFileLastModifiedTime(stringArray[0]);
			String string = "logs_";
			if (calendar.get(5) < 9) {
				string = string + "0" + calendar.get(5);
			} else {
				string = string + calendar.get(5);
			}

			if (calendar.get(2) < 9) {
				string = string + "-0" + (calendar.get(2) + 1);
			} else {
				string = string + "-" + (calendar.get(2) + 1);
			}

			String string2 = getLogsDir();
			File file2 = new File(string2 + File.separator + string);
			if (!file2.exists()) {
				file2.mkdir();
			}

			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				string = stringArray[int1];
				string2 = getLogsDir();
				File file3 = new File(string2 + File.separator + string);
				if (file3.isFile()) {
					String string3 = file2.getAbsolutePath();
					file3.renameTo(new File(string3 + File.separator + file3.getName()));
					file3.delete();
				}
			}
		} catch (Exception exception) {
			DebugLog.General.error("Exception thrown trying to initialize LoggerManager, trying to copy old log files.");
			DebugLog.General.error("Exception: ");
			DebugLog.General.error(exception);
			exception.printStackTrace();
		}
	}

	private static Calendar getLogFileLastModifiedTime(String string) {
		String string2 = getLogsDir();
		File file = new File(string2 + File.separator + string);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(file.lastModified());
		return calendar;
	}

	public static synchronized void createLogger(String string, boolean boolean1) {
		init();
		s_loggers.put(string, new ZLogger(string, boolean1));
	}

	public static String getLogsDir() {
		String string = ZomboidFileSystem.instance.getCacheDirSub("Logs");
		ZomboidFileSystem.ensureFolderExists(string);
		File file = new File(string);
		return file.getAbsolutePath();
	}

	public static String getPlayerCoords(IsoPlayer player) {
		int int1 = (int)player.getX();
		return "(" + int1 + "," + (int)player.getY() + "," + (int)player.getZ() + ")";
	}
}
