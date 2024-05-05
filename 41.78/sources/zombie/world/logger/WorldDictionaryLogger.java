package zombie.world.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.network.GameClient;


public class WorldDictionaryLogger {
	private static final ArrayList _logItems = new ArrayList();

	public static void reset() {
		_logItems.clear();
	}

	public static void startLogging() {
		reset();
	}

	public static void log(Log.BaseLog baseLog) {
		if (!GameClient.bClient) {
			_logItems.add(baseLog);
		}
	}

	public static void log(String string) {
		log(string, true);
	}

	public static void log(String string, boolean boolean1) {
		if (!GameClient.bClient) {
			if (boolean1) {
				DebugLog.log("WorldDictionary: " + string);
			}

			_logItems.add(new Log.Comment(string));
		}
	}

	public static void saveLog(String string) throws IOException {
		if (!GameClient.bClient) {
			boolean boolean1 = false;
			Log.BaseLog baseLog;
			for (int int1 = 0; int1 < _logItems.size(); ++int1) {
				baseLog = (Log.BaseLog)_logItems.get(int1);
				if (!baseLog.isIgnoreSaveCheck()) {
					boolean1 = true;
					break;
				}
			}

			if (boolean1) {
				String string2 = ZomboidFileSystem.instance.getCurrentSaveDir();
				File file = new File(string2 + File.separator);
				if (file.exists() && file.isDirectory()) {
					String string3 = ZomboidFileSystem.instance.getFileNameInCurrentSave(string);
					File file2 = new File(string3);
					try {
						FileWriter fileWriter = new FileWriter(file2, true);
						try {
							fileWriter.write("log = log or {};" + System.lineSeparator());
							fileWriter.write("table.insert(log, {" + System.lineSeparator());
							int int2 = 0;
							while (true) {
								if (int2 >= _logItems.size()) {
									fileWriter.write("};" + System.lineSeparator());
									break;
								}

								baseLog = (Log.BaseLog)_logItems.get(int2);
								baseLog.saveAsText(fileWriter, "\t");
								++int2;
							}
						} catch (Throwable throwable) {
							try {
								fileWriter.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}

							throw throwable;
						}

						fileWriter.close();
					} catch (Exception exception) {
						exception.printStackTrace();
						throw new IOException("Error saving WorldDictionary log.");
					}
				}
			}
		}
	}
}
