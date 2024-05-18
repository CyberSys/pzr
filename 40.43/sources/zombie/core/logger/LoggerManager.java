package zombie.core.logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;


public class LoggerManager {
	static HashMap _loggers = null;

	public static synchronized ZLogger getLogger(String string) {
		if (!_loggers.containsKey(string)) {
			createLogger(string);
		}

		return (ZLogger)_loggers.get(string);
	}

	public static void init() {
		if (_loggers == null) {
			_loggers = new HashMap();
			try {
				File file = new File(getLogsDir());
				String[] stringArray = file.list();
				if (stringArray == null) {
					return;
				}

				String string = "logs_";
				File file2 = null;
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					File file3;
					if (int1 == 0) {
						file3 = new File(getLogsDir() + File.separator + stringArray[int1]);
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(file3.lastModified());
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

						file2 = new File(getLogsDir() + File.separator + string);
						if (!file2.exists()) {
							file2.mkdir();
						}
					}

					if (file2 != null) {
						file3 = new File(getLogsDir() + File.separator + stringArray[int1]);
						if (!file3.isDirectory()) {
							file3.renameTo(new File(file2.getAbsolutePath() + File.separator + file3.getName()));
							file3.delete();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static synchronized void createLogger(String string) {
		_loggers.put(string, new ZLogger(string));
	}

	public static synchronized void createLogger(String string, boolean boolean1) {
		_loggers.put(string, new ZLogger(string, boolean1));
	}

	public static String getLogsDir() {
		String string = GameWindow.getCacheDir() + File.separator + "Logs";
		File file = new File(string);
		if (!file.exists()) {
			file.mkdir();
		}

		return file.getAbsolutePath();
	}

	public static String getPlayerCoords(IsoPlayer player) {
		return "(" + (int)player.getX() + "," + (int)player.getY() + "," + (int)player.getZ() + ")";
	}
}
