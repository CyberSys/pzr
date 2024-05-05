package zombie.sandbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class CustomSandboxOptions {
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;
	public static final CustomSandboxOptions instance = new CustomSandboxOptions();
	private final ArrayList m_options = new ArrayList();

	public void init() {
		ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string = (String)arrayList.get(int1);
			ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
			if (mod != null) {
				String string2 = mod.getDir();
				File file = new File(string2 + File.separator + "media" + File.separator + "sandbox-options.txt");
				if (file.exists() && !file.isDirectory()) {
					this.readFile(file.getAbsolutePath());
				}
			}
		}
	}

	public static void Reset() {
		instance.m_options.clear();
	}

	public void initInstance(SandboxOptions sandboxOptions) {
		for (int int1 = 0; int1 < this.m_options.size(); ++int1) {
			CustomSandboxOption customSandboxOption = (CustomSandboxOption)this.m_options.get(int1);
			sandboxOptions.newCustomOption(customSandboxOption);
		}
	}

	private boolean readFile(String string) {
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

					this.parse(stringBuilder.toString());
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

	private void parse(String string) {
		string = ScriptParser.stripComments(string);
		ScriptParser.Block block = ScriptParser.parse(string);
		int int1 = -1;
		ScriptParser.Value value = block.getValue("VERSION");
		if (value != null) {
			int1 = PZMath.tryParseInt(value.getValue(), -1);
		}

		if (int1 >= 1 && int1 <= 1) {
			Iterator iterator = block.children.iterator();
			while (iterator.hasNext()) {
				ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
				if (!block2.type.equalsIgnoreCase("option")) {
					throw new RuntimeException("unknown block type \"" + block2.type + "\"");
				}

				CustomSandboxOption customSandboxOption = this.parseOption(block2);
				if (customSandboxOption == null) {
					DebugLog.General.warn("failed to parse custom sandbox option \"%s\"", block2.id);
				} else {
					this.m_options.add(customSandboxOption);
				}
			}
		} else {
			throw new RuntimeException("invalid or missing VERSION");
		}
	}

	private CustomSandboxOption parseOption(ScriptParser.Block block) {
		if (StringUtils.isNullOrWhitespace(block.id)) {
			DebugLog.General.warn("missing or empty option id");
			return null;
		} else {
			ScriptParser.Value value = block.getValue("type");
			if (value != null && !StringUtils.isNullOrWhitespace(value.getValue())) {
				String string = value.getValue().trim();
				byte byte1 = -1;
				switch (string.hashCode()) {
				case -1325958191: 
					if (string.equals("double")) {
						byte1 = 1;
					}

					break;
				
				case -891985903: 
					if (string.equals("string")) {
						byte1 = 4;
					}

					break;
				
				case 3118337: 
					if (string.equals("enum")) {
						byte1 = 2;
					}

					break;
				
				case 64711720: 
					if (string.equals("boolean")) {
						byte1 = 0;
					}

					break;
				
				case 1958052158: 
					if (string.equals("integer")) {
						byte1 = 3;
					}

				
				}

				switch (byte1) {
				case 0: 
					return CustomBooleanSandboxOption.parse(block);
				
				case 1: 
					return CustomDoubleSandboxOption.parse(block);
				
				case 2: 
					return CustomEnumSandboxOption.parse(block);
				
				case 3: 
					return CustomIntegerSandboxOption.parse(block);
				
				case 4: 
					return CustomStringSandboxOption.parse(block);
				
				default: 
					DebugLog.General.warn("unknown option type \"%s\"", value.getValue().trim());
					return null;
				
				}
			} else {
				DebugLog.General.warn("missing or empty value \"type\"");
				return null;
			}
		}
	}
}
