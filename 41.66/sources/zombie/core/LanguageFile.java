package zombie.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class LanguageFile {
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;

	public boolean read(String string, LanguageFileData languageFileData) {
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

					this.fromString(stringBuilder.toString(), languageFileData);
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

	private void fromString(String string, LanguageFileData languageFileData) {
		string = ScriptParser.stripComments(string);
		ScriptParser.Block block = ScriptParser.parse(string);
		int int1 = -1;
		ScriptParser.Value value = block.getValue("VERSION");
		if (value != null) {
			int1 = PZMath.tryParseInt(value.getValue(), -1);
		}

		if (int1 >= 1 && int1 <= 1) {
			ScriptParser.Value value2 = block.getValue("text");
			if (value2 != null && !StringUtils.isNullOrWhitespace(value2.getValue())) {
				ScriptParser.Value value3 = block.getValue("charset");
				if (value3 != null && !StringUtils.isNullOrWhitespace(value3.getValue())) {
					languageFileData.text = value2.getValue().trim();
					languageFileData.charset = value3.getValue().trim();
					ScriptParser.Value value4 = block.getValue("base");
					if (value4 != null && !StringUtils.isNullOrWhitespace(value4.getValue())) {
						languageFileData.base = value4.getValue().trim();
					}

					ScriptParser.Value value5 = block.getValue("azerty");
					if (value5 != null) {
						languageFileData.azerty = StringUtils.tryParseBoolean(value5.getValue());
					}
				} else {
					throw new RuntimeException("missing or empty value \"charset\"");
				}
			} else {
				throw new RuntimeException("missing or empty value \"text\"");
			}
		} else {
			throw new RuntimeException("invalid or missing VERSION");
		}
	}
}
