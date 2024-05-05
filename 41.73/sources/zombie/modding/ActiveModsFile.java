package zombie.modding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class ActiveModsFile {
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;

	public boolean write(String string, ActiveMods activeMods) {
		if (Core.getInstance().isNoSave()) {
			return false;
		} else {
			File file = new File(string);
			try {
				FileWriter fileWriter = new FileWriter(file);
				try {
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					try {
						String string2 = this.toString(activeMods);
						bufferedWriter.write(string2);
					} catch (Throwable throwable) {
						try {
							bufferedWriter.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedWriter.close();
				} catch (Throwable throwable3) {
					try {
						fileWriter.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileWriter.close();
				return true;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return false;
			}
		}
	}

	private String toString(ActiveMods activeMods) {
		ScriptParser.Block block = new ScriptParser.Block();
		block.setValue("VERSION", String.valueOf(1));
		ScriptParser.Block block2 = block.addBlock("mods", (String)null);
		ArrayList arrayList = activeMods.getMods();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			block2.addValue("mod", (String)arrayList.get(int1));
		}

		ScriptParser.Block block3 = block.addBlock("maps", (String)null);
		ArrayList arrayList2 = activeMods.getMapOrder();
		for (int int2 = 0; int2 < arrayList2.size(); ++int2) {
			block3.addValue("map", (String)arrayList2.get(int2));
		}

		StringBuilder stringBuilder = new StringBuilder();
		String string = System.lineSeparator();
		block.prettyPrintElements(0, stringBuilder, string);
		return stringBuilder.toString();
	}

	public boolean read(String string, ActiveMods activeMods) {
		activeMods.clear();
		try {
			FileReader fileReader = new FileReader(string);
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					StringBuilder stringBuilder = new StringBuilder();
					for (String string2 = bufferedReader.readLine(); string2 != null; string2 = bufferedReader.readLine()) {
						stringBuilder.append(string2);
					}

					this.fromString(stringBuilder.toString(), activeMods);
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
		} catch (FileNotFoundException fileNotFoundException) {
			return false;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		}
	}

	private void fromString(String string, ActiveMods activeMods) {
		string = ScriptParser.stripComments(string);
		ScriptParser.Block block = ScriptParser.parse(string);
		int int1 = -1;
		ScriptParser.Value value = block.getValue("VERSION");
		if (value != null) {
			int1 = PZMath.tryParseInt(value.getValue(), -1);
		}

		if (int1 >= 1 && int1 <= 1) {
			ScriptParser.Block block2 = block.getBlock("mods", (String)null);
			String string2;
			if (block2 != null) {
				Iterator iterator = block2.values.iterator();
				while (iterator.hasNext()) {
					ScriptParser.Value value2 = (ScriptParser.Value)iterator.next();
					String string3 = value2.getKey().trim();
					if (string3.equalsIgnoreCase("mod")) {
						string2 = value2.getValue().trim();
						if (!StringUtils.isNullOrWhitespace(string2)) {
							activeMods.getMods().add(string2);
						}
					}
				}
			}

			ScriptParser.Block block3 = block.getBlock("maps", (String)null);
			if (block3 != null) {
				Iterator iterator2 = block3.values.iterator();
				while (iterator2.hasNext()) {
					ScriptParser.Value value3 = (ScriptParser.Value)iterator2.next();
					string2 = value3.getKey().trim();
					if (string2.equalsIgnoreCase("map")) {
						String string4 = value3.getValue().trim();
						if (!StringUtils.isNullOrWhitespace(string4)) {
							activeMods.getMapOrder().add(string4);
						}
					}
				}
			}
		}
	}
}
