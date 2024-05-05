package zombie.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import zombie.debug.DebugLog;


public final class ConfigFile {
	protected ArrayList options;
	protected int version;

	private void fileError(String string, int int1, String string2) {
		DebugLog.log(string + ":" + int1 + " " + string2);
	}

	public boolean read(String string) {
		this.options = new ArrayList();
		this.version = 0;
		File file = new File(string);
		if (!file.exists()) {
			return false;
		} else {
			DebugLog.log("reading " + string);
			try {
				FileReader fileReader = new FileReader(file);
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					try {
						int int1 = 0;
						while (true) {
							String string2 = bufferedReader.readLine();
							if (string2 == null) {
								break;
							}

							++int1;
							string2 = string2.trim();
							if (!string2.isEmpty() && !string2.startsWith("#")) {
								if (!string2.contains("=")) {
									this.fileError(string, int1, string2);
								} else {
									String[] stringArray = string2.split("=");
									if ("Version".equals(stringArray[0])) {
										try {
											this.version = Integer.parseInt(stringArray[1]);
										} catch (NumberFormatException numberFormatException) {
											this.fileError(string, int1, "expected version number, got \"" + stringArray[1] + "\"");
										}
									} else {
										StringConfigOption stringConfigOption = new StringConfigOption(stringArray[0], stringArray.length > 1 ? stringArray[1] : "", -1);
										this.options.add(stringConfigOption);
									}
								}
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
				return true;
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}
	}

	public boolean write(String string, int int1, ArrayList arrayList) {
		File file = new File(string);
		DebugLog.log("writing " + string);
		try {
			FileWriter fileWriter = new FileWriter(file, false);
			try {
				if (int1 != 0) {
					fileWriter.write("Version=" + int1 + System.lineSeparator());
				}

				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					ConfigOption configOption = (ConfigOption)arrayList.get(int2);
					String string2 = configOption.getTooltip();
					if (string2 != null) {
						string2 = string2.replaceAll("\n", System.lineSeparator() + "# ");
						fileWriter.write("# " + string2 + System.lineSeparator());
					}

					String string3 = configOption.getName();
					fileWriter.write(string3 + "=" + configOption.getValueAsString() + (int2 < arrayList.size() - 1 ? System.lineSeparator() + System.lineSeparator() : ""));
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
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public ArrayList getOptions() {
		return this.options;
	}

	public int getVersion() {
		return this.version;
	}
}
