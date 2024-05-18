package zombie.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import zombie.debug.DebugLog;


public class ConfigFile {
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
				Throwable throwable = null;
				try {
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					Throwable throwable2 = null;
					try {
						int int1 = 0;
						while (true) {
							String string2 = bufferedReader.readLine();
							if (string2 == null) {
								return true;
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
										StringConfigOption stringConfigOption = new StringConfigOption(stringArray[0], stringArray.length > 1 ? stringArray[1] : "");
										this.options.add(stringConfigOption);
									}
								}
							}
						}
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					} finally {
						if (bufferedReader != null) {
							if (throwable2 != null) {
								try {
									bufferedReader.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								bufferedReader.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (fileReader != null) {
						if (throwable != null) {
							try {
								fileReader.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							fileReader.close();
						}
					}
				}
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
			Throwable throwable = null;
			try {
				if (int1 != 0) {
					fileWriter.write("Version=" + int1 + System.lineSeparator());
				}

				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					ConfigOption configOption = (ConfigOption)arrayList.get(int2);
					fileWriter.write(configOption.getName() + "=" + configOption.getValueAsString() + System.lineSeparator());
				}
			} catch (Throwable throwable2) {
				throwable = throwable2;
				throw throwable2;
			} finally {
				if (fileWriter != null) {
					if (throwable != null) {
						try {
							fileWriter.close();
						} catch (Throwable throwable3) {
							throwable.addSuppressed(throwable3);
						}
					} else {
						fileWriter.close();
					}
				}
			}

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
