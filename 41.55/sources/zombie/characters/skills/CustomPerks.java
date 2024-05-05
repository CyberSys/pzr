package zombie.characters.skills;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class CustomPerks {
	private static final int VERSION1 = 1;
	private static final int VERSION = 1;
	public static final CustomPerks instance = new CustomPerks();
	private final ArrayList m_perks = new ArrayList();

	public void init() {
		ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string = (String)arrayList.get(int1);
			ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
			if (mod != null) {
				String string2 = mod.getDir();
				File file = new File(string2 + File.separator + "media" + File.separator + "perks.txt");
				if (file.exists() && !file.isDirectory()) {
					this.readFile(file.getAbsolutePath());
				}
			}
		}

		Iterator iterator = this.m_perks.iterator();
		while (true) {
			CustomPerk customPerk;
			PerkFactory.Perk perk;
			do {
				if (!iterator.hasNext()) {
					iterator = this.m_perks.iterator();
					while (iterator.hasNext()) {
						customPerk = (CustomPerk)iterator.next();
						perk = PerkFactory.Perks.FromString(customPerk.m_id);
						PerkFactory.Perk perk2 = PerkFactory.Perks.FromString(customPerk.m_parent);
						if (perk2 == null || perk2 == PerkFactory.Perks.None || perk2 == PerkFactory.Perks.MAX) {
							perk2 = PerkFactory.Perks.None;
						}

						int[] intArray = customPerk.m_xp;
						PerkFactory.AddPerk(perk, customPerk.m_translation, perk2, intArray[0], intArray[1], intArray[2], intArray[3], intArray[4], intArray[5], intArray[6], intArray[7], intArray[8], intArray[9], customPerk.m_bPassive);
					}

					return;
				}

				customPerk = (CustomPerk)iterator.next();
				perk = PerkFactory.Perks.FromString(customPerk.m_id);
			}	 while (perk != null && perk != PerkFactory.Perks.None && perk != PerkFactory.Perks.MAX);

			perk = new PerkFactory.Perk(customPerk.m_id);
			perk.setCustom();
		}
	}

	public void initLua() {
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("Perks");
		Iterator iterator = this.m_perks.iterator();
		while (iterator.hasNext()) {
			CustomPerk customPerk = (CustomPerk)iterator.next();
			PerkFactory.Perk perk = PerkFactory.Perks.FromString(customPerk.m_id);
			kahluaTable.rawset(perk.getId(), perk);
		}
	}

	public static void Reset() {
		instance.m_perks.clear();
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
				if (!block2.type.equalsIgnoreCase("perk")) {
					throw new RuntimeException("unknown block type \"" + block2.type + "\"");
				}

				CustomPerk customPerk = this.parsePerk(block2);
				if (customPerk == null) {
					DebugLog.General.warn("failed to parse custom perk \"%s\"", block2.id);
				} else {
					this.m_perks.add(customPerk);
				}
			}
		} else {
			throw new RuntimeException("invalid or missing VERSION");
		}
	}

	private CustomPerk parsePerk(ScriptParser.Block block) {
		if (StringUtils.isNullOrWhitespace(block.id)) {
			DebugLog.General.warn("missing or empty perk id");
			return null;
		} else {
			CustomPerk customPerk = new CustomPerk(block.id);
			ScriptParser.Value value = block.getValue("parent");
			if (value != null && !StringUtils.isNullOrWhitespace(value.getValue())) {
				customPerk.m_parent = value.getValue().trim();
			}

			ScriptParser.Value value2 = block.getValue("translation");
			if (value2 != null) {
				customPerk.m_translation = StringUtils.discardNullOrWhitespace(value2.getValue().trim());
			}

			if (StringUtils.isNullOrWhitespace(customPerk.m_translation)) {
				customPerk.m_translation = customPerk.m_id;
			}

			ScriptParser.Value value3 = block.getValue("passive");
			if (value3 != null) {
				customPerk.m_bPassive = StringUtils.tryParseBoolean(value3.getValue().trim());
			}

			for (int int1 = 1; int1 <= 10; ++int1) {
				ScriptParser.Value value4 = block.getValue("xp" + int1);
				if (value4 != null) {
					int int2 = PZMath.tryParseInt(value4.getValue().trim(), -1);
					if (int2 > 0) {
						customPerk.m_xp[int1 - 1] = int2;
					}
				}
			}

			return customPerk;
		}
	}
}
