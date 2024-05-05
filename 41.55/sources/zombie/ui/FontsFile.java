package zombie.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class FontsFile {
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;

	public boolean read(String string, HashMap hashMap) {
		try {
			FileReader fileReader = new FileReader(string);
			boolean boolean1;
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					StringBuilder stringBuilder = new StringBuilder();
					for (String string2 = bufferedReader.readLine(); string2 != null; string2 = bufferedReader.readLine()) {
						stringBuilder.append(string2);
					}

					this.fromString(stringBuilder.toString(), hashMap);
					boolean1 = true;
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
			return boolean1;
		} catch (FileNotFoundException fileNotFoundException) {
			return false;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		}
	}

	private void fromString(String string, HashMap hashMap) {
		string = ScriptParser.stripComments(string);
		ScriptParser.Block block = ScriptParser.parse(string);
		int int1 = -1;
		ScriptParser.Value value = block.getValue("VERSION");
		if (value != null) {
			int1 = PZMath.tryParseInt(value.getValue(), -1);
		}

		if (int1 >= 1 && int1 <= 1) {
			Iterator iterator = block.children.iterator();
			while (true) {
				while (iterator.hasNext()) {
					ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
					if (!block2.type.equalsIgnoreCase("font")) {
						throw new RuntimeException("unknown block type \"" + block2.type + "\"");
					}

					if (StringUtils.isNullOrWhitespace(block2.id)) {
						DebugLog.General.warn("missing or empty font id");
					} else {
						ScriptParser.Value value2 = block2.getValue("fnt");
						ScriptParser.Value value3 = block2.getValue("img");
						if (value2 != null && !StringUtils.isNullOrWhitespace(value2.getValue())) {
							FontsFileFont fontsFileFont = new FontsFileFont();
							fontsFileFont.id = block2.id;
							fontsFileFont.fnt = value2.getValue().trim();
							if (value3 != null && !StringUtils.isNullOrWhitespace(value3.getValue())) {
								fontsFileFont.img = value3.getValue().trim();
							}

							hashMap.put(fontsFileFont.id, fontsFileFont);
						} else {
							DebugLog.General.warn("missing or empty value \"fnt\"");
						}
					}
				}

				return;
			}
		} else {
			throw new RuntimeException("invalid or missing VERSION");
		}
	}
}
