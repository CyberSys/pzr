package zombie.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameSounds;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.MultiStageBuilding;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.scripting.objects.ContainerDistribution;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.FloorDistribution;
import zombie.scripting.objects.Inventory;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.LanguageDefinition;
import zombie.scripting.objects.QuestTaskCondition;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.Room;
import zombie.scripting.objects.Script;
import zombie.scripting.objects.ScriptActivatable;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.ScriptContainer;
import zombie.scripting.objects.ScriptFlag;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.ScriptTalker;
import zombie.scripting.objects.ShelfDistribution;
import zombie.scripting.objects.Trigger;
import zombie.scripting.objects.UniqueRecipe;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.VehicleTemplate;
import zombie.scripting.objects.Waypoint;
import zombie.scripting.objects.Zone;


public class ScriptManager implements IScriptObjectStore {
	public static ScriptManager instance = new ScriptManager();
	public HashMap TriggerMap = new HashMap();
	public HashMap CustomTriggerMap = new HashMap();
	public HashMap CustomTriggerLastRan = new HashMap();
	public HashMap HookMap = new HashMap();
	public HashMap ModuleMap = new HashMap();
	public final HashMap FullTypeToItemMap = new HashMap();
	public Stack PlayingScripts = new Stack();
	public ScriptModule CurrentLoadingModule = null;
	public HashMap ModuleAliases = new HashMap();
	public boolean skipping = false;
	public HashMap MapMap = new HashMap();
	ArrayList toStop = new ArrayList();
	ArrayList toStopInstance = new ArrayList();
	StringBuffer buf = new StringBuffer();
	StringBuffer buf2 = new StringBuffer();
	HashMap CachedModules = new HashMap();
	Stack recipesTempList = new Stack();
	Stack evolvedRecipesTempList = new Stack();
	Stack uniqueRecipesTempList = new Stack();
	ArrayList fixingTempList = null;
	ArrayList itemTempList = null;
	final ArrayList vehicleScriptTempList = new ArrayList();
	Stack zoneTempList = new Stack();
	Stack conTempList = new Stack();
	Stack floorTempList = new Stack();
	Stack shelfTempList = new Stack();
	static StringBuilder builder = new StringBuilder();
	static String Base = "Base";
	private String checksum = "";

	public void AddOneTime(String string, String string2) {
		string = string.toLowerCase();
		Stack stack = null;
		if (this.HookMap.containsKey(string)) {
			stack = (Stack)this.HookMap.get(string);
		} else {
			stack = new Stack();
			this.HookMap.put(string, stack);
		}

		stack.add(string2);
	}

	public void FireHook(String string) {
		if (this.HookMap.containsKey(string)) {
			Iterator iterator = ((Stack)this.HookMap.get(string)).iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				this.PlayScript(string2);
			}

			((Stack)this.HookMap.get(string)).clear();
		}
	}

	@Deprecated
	public void ParseScriptPP(String string) {
		boolean boolean1 = false;
		Stack stack = new Stack();
		boolean boolean2 = false;
		boolean boolean3 = false;
		int int1;
		for (boolean boolean4 = false; !boolean1; string = string.substring(int1 + 1)) {
			int int2 = 0;
			int1 = 0;
			int int3 = 0;
			if (string.indexOf("}", int1 + 1) == -1) {
				boolean1 = true;
				break;
			}

			do {
				int1 = string.indexOf("{", int1 + 1);
				int3 = string.indexOf("}", int3 + 1);
				if ((int3 >= int1 || int3 == -1) && int1 != -1) {
					if (int1 != -1) {
						int3 = int1;
						++int2;
					}
				} else {
					int1 = int3;
					--int2;
				}
			}	 while (int2 > 0);

			stack.add(string.substring(0, int1 + 1));
		}

		if (string.trim().length() > 0) {
			stack.add(string.trim());
		}

		for (int int4 = 0; int4 < stack.size(); ++int4) {
			String string2 = (String)stack.get(int4);
			this.CreateFromTokenPP(string2);
		}
	}

	public void ParseScript(String string) {
		boolean boolean1 = false;
		Stack stack = new Stack();
		boolean boolean2 = false;
		boolean boolean3 = false;
		int int1;
		for (boolean boolean4 = false; !boolean1; string = string.substring(int1 + 1)) {
			int int2 = 0;
			int1 = 0;
			int int3 = 0;
			if (string.indexOf("}", int1 + 1) == -1) {
				boolean1 = true;
				break;
			}

			do {
				int1 = string.indexOf("{", int1 + 1);
				int3 = string.indexOf("}", int3 + 1);
				if ((int3 >= int1 || int3 == -1) && int1 != -1) {
					if (int1 != -1) {
						int3 = int1;
						++int2;
					}
				} else {
					int1 = int3;
					--int2;
				}
			}	 while (int2 > 0);

			stack.add(string.substring(0, int1 + 1));
		}

		if (string.trim().length() > 0) {
			stack.add(string.trim());
		}

		for (int int4 = 0; int4 < stack.size(); ++int4) {
			String string2 = (String)stack.get(int4);
			this.CreateFromToken(string2);
		}
	}

	public void StopScript(String string) {
		this.toStop.add(string);
	}

	public void PlayInstanceScript(String string, String string2, String string3, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (string != null && string.equals(((Script.ScriptInstance)this.PlayingScripts.get(int1)).ID)) {
				return;
			}
		}

		Script.ScriptInstance scriptInstance = new Script.ScriptInstance();
		scriptInstance.ID = string;
		Script script = this.getScript(string2);
		scriptInstance.theScript = script;
		scriptInstance.CharacterAliases.put(string3, gameCharacter);
		scriptInstance.CharacterAliasesR.put(gameCharacter, string3);
		gameCharacter.getActiveInInstances().add(scriptInstance);
		instance.PlayingScripts.add(scriptInstance);
		scriptInstance.begin();
	}

	public Script.ScriptInstance PlayInstanceScript(String string, String string2, KahluaTable kahluaTable) {
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		HashMap hashMap = new HashMap();
		while (kahluaTableIterator.advance()) {
			String string3 = (String)kahluaTableIterator.getKey();
			IsoGameCharacter gameCharacter = (IsoGameCharacter)kahluaTableIterator.getValue();
			hashMap.put(string3, gameCharacter);
		}

		return this.PlayInstanceScript(string, string2, hashMap);
	}

	public Script.ScriptInstance PlayInstanceScript(String string, String string2, KahluaTable kahluaTable, KahluaTable kahluaTable2) {
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		HashMap hashMap = new HashMap();
		while (kahluaTableIterator.advance()) {
			String string3 = (String)kahluaTableIterator.getKey();
			IsoGameCharacter gameCharacter = (IsoGameCharacter)kahluaTableIterator.getValue();
			hashMap.put(string3, gameCharacter);
		}

		HashMap hashMap2 = new HashMap();
		kahluaTableIterator = kahluaTable2.iterator();
		while (kahluaTableIterator.advance()) {
			String string4 = (String)kahluaTableIterator.getKey();
			String string5 = (String)kahluaTableIterator.getValue();
			hashMap2.put(string4, string5);
		}

		return this.PlayInstanceScript(string, string2, hashMap, hashMap2);
	}

	public Script.ScriptInstance PlayInstanceScript(String string, String string2, HashMap hashMap) {
		return this.PlayInstanceScript(string, string2, (HashMap)hashMap, (HashMap)null);
	}

	public Script.ScriptInstance PlayInstanceScript(String string, String string2, HashMap hashMap, HashMap hashMap2) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (string != null && string.equals(((Script.ScriptInstance)this.PlayingScripts.get(int1)).ID)) {
				return null;
			}
		}

		Script.ScriptInstance scriptInstance = new Script.ScriptInstance();
		scriptInstance.ID = string;
		Script script = this.getScript(string2);
		scriptInstance.theScript = script;
		Iterator iterator = hashMap.entrySet().iterator();
		while (iterator != null && iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			String string3 = (String)entry.getKey();
			IsoGameCharacter gameCharacter = (IsoGameCharacter)entry.getValue();
			scriptInstance.CharacterAliases.put(string3, gameCharacter);
			scriptInstance.CharacterAliasesR.put(gameCharacter, string3);
			gameCharacter.getActiveInInstances().add(scriptInstance);
		}

		Iterator iterator2 = hashMap2.entrySet().iterator();
		while (iterator2 != null && iterator2.hasNext()) {
			Entry entry2 = (Entry)iterator2.next();
			String string4 = (String)entry2.getKey();
			String string5 = (String)entry2.getValue();
			scriptInstance.addPair(string4, string5);
		}

		instance.PlayingScripts.add(scriptInstance);
		scriptInstance.begin();
		return scriptInstance;
	}

	public void PlayInstanceScript(String string, String string2, String string3, IsoGameCharacter gameCharacter, String string4, IsoGameCharacter gameCharacter2) {
		Script.ScriptInstance scriptInstance = new Script.ScriptInstance();
		scriptInstance.ID = string;
		Script script = this.getScript(string2);
		scriptInstance.theScript = script;
		scriptInstance.CharacterAliases.put(string3, gameCharacter);
		scriptInstance.CharacterAliasesR.put(gameCharacter, string3);
		scriptInstance.CharacterAliases.put(string4, gameCharacter2);
		scriptInstance.CharacterAliasesR.put(gameCharacter2, string4);
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (string != null && string.equals(((Script.ScriptInstance)this.PlayingScripts.get(int1)).ID)) {
				scriptInstance.CharactersAlreadyInScript = true;
			}
		}

		gameCharacter.getActiveInInstances().add(scriptInstance);
		gameCharacter2.getActiveInInstances().add(scriptInstance);
		instance.PlayingScripts.add(scriptInstance);
		scriptInstance.begin();
	}

	public void PlayInstanceScript(String string, String string2, String string3, IsoGameCharacter gameCharacter, String string4, IsoGameCharacter gameCharacter2, String string5, IsoGameCharacter gameCharacter3) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (string != null && string.equals(((Script.ScriptInstance)this.PlayingScripts.get(int1)).ID)) {
				return;
			}
		}

		Script.ScriptInstance scriptInstance = new Script.ScriptInstance();
		scriptInstance.ID = string;
		Script script = this.getScript(string2);
		scriptInstance.theScript = script;
		scriptInstance.CharacterAliases.put(string3, gameCharacter);
		scriptInstance.CharacterAliasesR.put(gameCharacter, string3);
		scriptInstance.CharacterAliases.put(string4, gameCharacter2);
		scriptInstance.CharacterAliasesR.put(gameCharacter2, string4);
		scriptInstance.CharacterAliases.put(string5, gameCharacter3);
		scriptInstance.CharacterAliasesR.put(gameCharacter3, string5);
		gameCharacter.getActiveInInstances().add(scriptInstance);
		gameCharacter2.getActiveInInstances().add(scriptInstance);
		gameCharacter3.getActiveInInstances().add(scriptInstance);
		instance.PlayingScripts.add(scriptInstance);
		scriptInstance.begin();
	}

	public void PlayScript(String string) {
		Script script = this.getScript(string);
		if (script != null) {
			for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
				if (((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript == script && !((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript.Instancable) {
					this.PlayingScripts.remove(int1);
					--int1;
				}
			}

			script.module.PlayScript(script.name);
		}
	}

	public Script.ScriptInstance PlayScript(String string, Script.ScriptInstance scriptInstance) {
		Script script = this.getScript(string);
		if (script != null) {
			for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
				if (((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript == script && !((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript.Instancable) {
					this.PlayingScripts.remove(int1);
					--int1;
				}
			}

			return script.module.PlayScript(script.name, scriptInstance);
		} else {
			return null;
		}
	}

	public void update() {
		assert this.toStopInstance.isEmpty();
		assert this.toStop.isEmpty();
		assert this.CustomTriggerMap.isEmpty();
		assert this.CustomTriggerLastRan.isEmpty();
		assert this.PlayingScripts.isEmpty();
	}

	public void LoadFile(String string, boolean boolean1) throws FileNotFoundException {
		if (!GameServer.bServer) {
			Thread.yield();
			Core.getInstance().DoFrameReady();
		}

		string = ZomboidFileSystem.instance.getString(string);
		if (string.contains(".tmx")) {
			IsoWorld.mapPath = string.substring(0, string.lastIndexOf("/"));
			IsoWorld.mapUseJar = boolean1;
		} else if (string.contains(".txt")) {
			DebugLog.log("script: loading " + string);
			InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(ZomboidFileSystem.instance.getString(string), !boolean1);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			this.buf.setLength(0);
			this.buf2.setLength(0);
			String string2 = null;
			String string3 = "";
			label170: {
				try {
					while (true) {
						if ((string2 = bufferedReader.readLine()) == null) {
							break label170;
						}

						if (string2 != null) {
							IsoGridSquare.Checksum += (long)string2.hashCode();
							this.buf.append(string2);
						}
					}
				} catch (Exception exception) {
				} finally {
					try {
						bufferedReader.close();
						inputStreamReader.close();
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}
				}

				return;
			}

			string3 = this.buf.toString();
			this.buf2.setLength(0);
			this.buf2.append(string3);
			int int1;
			for (int int2 = this.buf2.lastIndexOf("*/"); int2 != -1; int2 = this.buf2.lastIndexOf("*/", int1)) {
				int1 = this.buf2.lastIndexOf("/*", int2 - 1);
				if (int1 == -1) {
					break;
				}

				int int3;
				for (int int4 = this.buf2.lastIndexOf("*/", int2 - 1); int4 > int1; int4 = this.buf2.lastIndexOf("*/", int3 - 2)) {
					int3 = int1;
					this.buf2.substring(int1, int4 + 2);
					int1 = this.buf2.lastIndexOf("/*", int1 - 2);
					if (int1 == -1) {
						break;
					}
				}

				if (int1 == -1) {
					break;
				}

				this.buf2.substring(int1, int2 + 2);
				this.buf2.replace(int1, int2 + 2, "");
			}

			string3 = this.buf2.toString();
			this.ParseScript(string3);
		}
	}

	@Deprecated
	public void LoadFilePP(String string, boolean boolean1) throws FileNotFoundException, UnsupportedEncodingException {
		if (string.contains(".tmx")) {
			IsoWorld.mapPath = string.substring(0, string.lastIndexOf("/"));
			IsoWorld.mapUseJar = boolean1;
		} else if (string.contains(".txt")) {
			InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, !boolean1);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			this.buf.setLength(0);
			this.buf2.setLength(0);
			String string2 = null;
			String string3 = "";
			label127: {
				try {
					while (true) {
						if ((string2 = bufferedReader.readLine()) == null) {
							break label127;
						}

						if (string2 != null) {
							this.buf.append(string2);
						}
					}
				} catch (Exception exception) {
				} finally {
					try {
						bufferedReader.close();
						inputStreamReader.close();
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}
				}

				return;
			}

			string3 = this.buf.toString();
			try {
				while (string3.contains("*/")) {
					int int1 = string3.indexOf("/*");
					int int2 = string3.indexOf("*/");
					this.buf2.setLength(0);
					this.buf2.append(string3.substring(0, int1));
					this.buf2.append("\n");
					this.buf2.append(string3.substring(int2 + 2));
					string3 = this.buf2.toString();
				}
			} catch (Exception exception3) {
				Logger.getLogger(IsoWorld.class.getName()).log(Level.SEVERE, (String)null, exception3);
			}

			this.ParseScriptPP(string3);
		}
	}

	private void CreateFromToken(String string) {
		string = string.trim();
		IsoGridSquare.Checksum += (long)string.hashCode();
		if (string.indexOf("module") == 0) {
			int int1 = string.indexOf("{");
			int int2 = string.lastIndexOf("}");
			String[] stringArray = string.split("[{}]");
			String string2 = stringArray[0];
			string2 = string2.replace("module", "");
			string2 = string2.trim();
			String string3 = string.substring(int1 + 1, int2);
			if (!this.ModuleMap.containsKey(string2)) {
				this.ModuleMap.put(string2, new ScriptModule());
			}

			ScriptModule scriptModule = (ScriptModule)this.ModuleMap.get(string2);
			scriptModule.Load(string2, string3);
		}
	}

	private void CreateFromTokenPP(String string) {
		string = string.trim();
		IsoGridSquare.Checksum += (long)string.hashCode();
		if (string.indexOf("module") == 0) {
			String[] stringArray = string.split("[{}]");
			String string2 = stringArray[0];
			string2 = string2.replace("module", "");
			string2 = string2.trim();
			ScriptModule scriptModule = new ScriptModule();
			this.ModuleMap.put(string2, scriptModule);
		}
	}

	public void LoadStory(String string) throws IOException, URISyntaxException {
		try {
			Enumeration enumeration = GameWindow.class.getClassLoader().getResources("stories/" + string + "/");
			if (enumeration.hasMoreElements()) {
				URL url = (URL)enumeration.nextElement();
				File file = new File(url.toURI());
				String[] stringArray = file.list();
				String[] stringArray2 = stringArray;
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string2 = stringArray2[int2];
					this.LoadFile("stories/" + string + "/" + string2, true);
				}
			}
		} catch (IOException ioException) {
			Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, (String)null, ioException);
		}
	}

	public Stack getStoryList() throws IOException, URISyntaxException {
		Stack stack = new Stack();
		File file = new File("mods/stories/");
		String[] stringArray = file.list();
		stack.addAll(Arrays.asList(stringArray));
		file = new File("media/stories/");
		stringArray = file.list();
		stack.addAll(Arrays.asList(stringArray));
		return stack;
	}

	public void searchFolders(URI uRI, File file, ArrayList arrayList) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				this.searchFolders(uRI, new File(file.getAbsolutePath() + File.separator + stringArray[int1]), arrayList);
			}
		} else if (file.getAbsolutePath().toLowerCase().contains(".txt")) {
			arrayList.add(ZomboidFileSystem.instance.getRelativeFile(uRI, file.getAbsolutePath()));
		} else if (file.getAbsolutePath().toLowerCase().contains(".lot")) {
			String string = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1);
			string = string.substring(0, string.lastIndexOf("."));
			this.MapMap.put(string, file.getAbsolutePath());
		}
	}

	public static String getItemName(String string) {
		return !string.contains(".") ? string : string.split("\\.")[1];
	}

	public void FillInventory(IsoGameCharacter gameCharacter, ItemContainer itemContainer, String string) {
		Inventory inventory = null;
		String string2 = gameCharacter.getScriptModule();
		if (string.contains(".")) {
			inventory = this.getInventory(string);
			string2 = string.split("\\.")[0];
		} else {
			inventory = this.getInventory(gameCharacter.getScriptModule() + "." + string);
		}

		if (inventory != null) {
			for (int int1 = 0; int1 < inventory.Items.size(); ++int1) {
				if (((Inventory.Source)inventory.Items.get(int1)).type.trim().length() > 0) {
					int int2 = ((Inventory.Source)inventory.Items.get(int1)).count;
					for (int int3 = 0; int3 < int2; ++int3) {
						InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string2 + "." + ((Inventory.Source)inventory.Items.get(int1)).type);
						itemContainer.AddItem(inventoryItem);
					}
				}
			}
		}
	}

	public void Trigger(String string) {
		string = string.toLowerCase();
		this.FireHook(string);
		if (this.TriggerMap.containsKey(string)) {
			Stack stack = (Stack)this.TriggerMap.get(string);
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				if (!((Trigger)stack.get(int1)).module.disabled && !((Trigger)stack.get(int1)).Locked) {
					((Trigger)stack.get(int1)).TriggerParam = null;
					((Trigger)stack.get(int1)).TriggerParam2 = null;
					((Trigger)stack.get(int1)).TriggerParam3 = null;
					((Trigger)stack.get(int1)).Process();
				}
			}
		}
	}

	public void Trigger(String string, String string2) {
		string = string.toLowerCase();
		this.FireHook(string);
		if (this.TriggerMap.containsKey(string)) {
			Stack stack = (Stack)this.TriggerMap.get(string);
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				if (!((Trigger)stack.get(int1)).module.disabled && !((Trigger)stack.get(int1)).Locked) {
					((Trigger)stack.get(int1)).TriggerParam = string2;
					((Trigger)stack.get(int1)).Process();
				}
			}
		}
	}

	public void Trigger(String string, String string2, String string3) {
		string = string.toLowerCase();
		this.FireHook(string);
		if (this.TriggerMap.containsKey(string)) {
			Stack stack = (Stack)this.TriggerMap.get(string);
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				if (!((Trigger)stack.get(int1)).module.disabled && !((Trigger)stack.get(int1)).Locked) {
					((Trigger)stack.get(int1)).TriggerParam = string2;
					((Trigger)stack.get(int1)).TriggerParam2 = string3;
					((Trigger)stack.get(int1)).Process();
				}
			}
		}
	}

	public void Trigger(String string, String string2, String string3, String string4) {
		string = string.toLowerCase();
		this.FireHook(string);
		if (this.TriggerMap.containsKey(string)) {
			Stack stack = (Stack)this.TriggerMap.get(string);
			for (int int1 = 0; int1 < stack.size(); ++int1) {
				if (!((Trigger)stack.get(int1)).module.disabled && !((Trigger)stack.get(int1)).Locked) {
					((Trigger)stack.get(int1)).TriggerParam = string2;
					((Trigger)stack.get(int1)).TriggerParam2 = string3;
					((Trigger)stack.get(int1)).TriggerParam2 = string4;
					((Trigger)stack.get(int1)).Process();
				}
			}
		}
	}

	public boolean IsScriptPlaying(String string) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript.name.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean IsScriptPlaying(Script.ScriptInstance scriptInstance) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (this.PlayingScripts.get(int1) == scriptInstance) {
				return true;
			}
		}

		return false;
	}

	public void PauseScript(String string) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript.name.equals(string)) {
				((Script.ScriptInstance)this.PlayingScripts.get(int1)).Paused = true;
			}
		}
	}

	public void UnPauseScript(String string) {
		for (int int1 = 0; int1 < this.PlayingScripts.size(); ++int1) {
			if (((Script.ScriptInstance)this.PlayingScripts.get(int1)).theScript.name.equals(string)) {
				((Script.ScriptInstance)this.PlayingScripts.get(int1)).Paused = false;
			}
		}
	}

	public ScriptModule getModule(String string) {
		if (string.startsWith(Base)) {
			return (ScriptModule)this.ModuleMap.get(Base);
		} else if (this.CachedModules.containsKey(string)) {
			return (ScriptModule)this.CachedModules.get(string);
		} else {
			ScriptModule scriptModule = null;
			if (this.ModuleAliases.containsKey(string)) {
				string = (String)this.ModuleAliases.get(string);
			}

			if (this.CachedModules.containsKey(string)) {
				return (ScriptModule)this.CachedModules.get(string);
			} else {
				if (this.ModuleMap.containsKey(string)) {
					if (((ScriptModule)this.ModuleMap.get(string)).disabled) {
						scriptModule = null;
					} else {
						scriptModule = (ScriptModule)this.ModuleMap.get(string);
					}
				}

				if (scriptModule != null) {
					this.CachedModules.put(string, scriptModule);
					return scriptModule;
				} else {
					int int1 = string.indexOf(".");
					if (int1 != -1) {
						scriptModule = this.getModule(string.substring(0, int1));
					}

					if (scriptModule != null) {
						this.CachedModules.put(string, scriptModule);
						return scriptModule;
					} else {
						return (ScriptModule)this.ModuleMap.get(Base);
					}
				}
			}
		}
	}

	public ScriptModule getModuleNoDisableCheck(String string) {
		if (this.ModuleAliases.containsKey(string)) {
			string = (String)this.ModuleAliases.get(string);
		}

		if (this.ModuleMap.containsKey(string)) {
			return (ScriptModule)this.ModuleMap.get(string);
		} else {
			return string.indexOf(".") != -1 ? this.getModule(string.split("\\.")[0]) : null;
		}
	}

	public Inventory getInventory(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : (Inventory)scriptModule.InventoryMap.get(getItemName(string));
	}

	public ScriptCharacter getCharacter(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getCharacter(getItemName(string));
	}

	public ScriptCharacter FindCharacter(String string) {
		Iterator iterator = this.ModuleMap.values().iterator();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled && scriptModule.CharacterMap.containsKey(string)) {
				return scriptModule.getCharacter(string);
			}
		}

		return null;
	}

	public IsoGameCharacter getCharacterActual(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getCharacterActual(getItemName(string));
	}

	public int getFlagIntValue(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? 0 : scriptModule.getFlagIntValue(getItemName(string));
	}

	public String getFlagValue(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? "" : scriptModule.getFlagValue(getItemName(string));
	}

	public Waypoint getWaypoint(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getWaypoint(getItemName(string));
	}

	public ScriptContainer getScriptContainer(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getScriptContainer(getItemName(string));
	}

	public Room getRoom(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getRoom(getItemName(string));
	}

	public LanguageDefinition getLanguageDef(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getLanguageDef(getItemName(string));
	}

	public String getLanguage(String string) {
		if (!string.contains("@")) {
			return string;
		} else {
			String[] stringArray = string.split("-");
			LanguageDefinition languageDefinition = this.getLanguageDef(stringArray[0]);
			return languageDefinition.get(Integer.parseInt(stringArray[1]));
		}
	}

	public ScriptTalker getTalker(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getTalker(getItemName(string));
	}

	public ScriptActivatable getActivatable(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getActivatable(getItemName(string));
	}

	public ScriptFlag getFlag(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getFlag(getItemName(string));
	}

	public Zone getZone(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getZone(getItemName(string));
	}

	public QuestTaskCondition getQuestCondition(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getQuestCondition(getItemName(string));
	}

	public Item getItem(String string) {
		if (string.contains(".") && this.FullTypeToItemMap.containsKey(string)) {
			return (Item)this.FullTypeToItemMap.get(string);
		} else {
			ScriptModule scriptModule = this.getModule(string);
			return scriptModule == null ? null : scriptModule.getItem(getItemName(string));
		}
	}

	public Item FindItem(String string) {
		ScriptModule scriptModule = this.getModule(string);
		if (scriptModule == null) {
			return null;
		} else {
			Item item = scriptModule.getItem(getItemName(string));
			if (item == null) {
				Iterator iterator = this.ModuleMap.values().iterator();
				while (iterator != null && iterator.hasNext()) {
					ScriptModule scriptModule2 = (ScriptModule)iterator.next();
					if (!scriptModule2.disabled) {
						item = scriptModule.getItem(getItemName(string));
						if (item != null) {
							return item;
						}
					}
				}
			}

			return item;
		}
	}

	public boolean isDrainableItemType(String string) {
		Item item = this.FindItem(string);
		if (item != null) {
			return item.getType() == Item.Type.Drainable;
		} else {
			return false;
		}
	}

	public Recipe getRecipe(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getRecipe(getItemName(string));
	}

	public VehicleScript getVehicle(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getVehicle(getItemName(string));
	}

	public VehicleTemplate getVehicleTemplate(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getVehicleTemplate(getItemName(string));
	}

	public void CheckExitPoints() {
		Iterator iterator = this.ModuleMap.values().iterator();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled && scriptModule.CheckExitPoints()) {
				return;
			}
		}
	}

	public Script getScript(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getScript(getItemName(string));
	}

	public ArrayList getAllItems() {
		if (this.itemTempList == null) {
			this.itemTempList = new ArrayList();
			Iterator iterator = this.ModuleMap.values().iterator();
			while (true) {
				ScriptModule scriptModule;
				do {
					if (iterator == null || !iterator.hasNext()) {
						return this.itemTempList;
					}

					scriptModule = (ScriptModule)iterator.next();
				}		 while (scriptModule.disabled);

				Iterator iterator2 = scriptModule.ItemMap.values().iterator();
				while (iterator2.hasNext()) {
					Item item = (Item)iterator2.next();
					this.itemTempList.add(item);
				}
			}
		} else {
			return this.itemTempList;
		}
	}

	public ArrayList getAllFixing() {
		this.fixingTempList = new ArrayList();
		Iterator iterator = this.ModuleMap.values().iterator();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.FixingMap.values().iterator();
				while (iterator2.hasNext()) {
					Fixing fixing = (Fixing)iterator2.next();
					this.fixingTempList.add(fixing);
				}
			}
		}

		return this.fixingTempList;
	}

	public Stack getAllRecipes() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.recipesTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.RecipeMap.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					Recipe recipe = (Recipe)iterator2.next();
					this.recipesTempList.add(recipe);
				}
			}
		}

		return this.recipesTempList;
	}

	public Stack getAllEvolvedRecipes() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.evolvedRecipesTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.EvolvedRecipeMap.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					EvolvedRecipe evolvedRecipe = (EvolvedRecipe)iterator2.next();
					this.evolvedRecipesTempList.add(evolvedRecipe);
				}
			}
		}

		return this.evolvedRecipesTempList;
	}

	public Stack getAllUniqueRecipes() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.uniqueRecipesTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.UniqueRecipeMap.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					UniqueRecipe uniqueRecipe = (UniqueRecipe)iterator2.next();
					this.uniqueRecipesTempList.add(uniqueRecipe);
				}
			}
		}

		return this.uniqueRecipesTempList;
	}

	public Stack getAllZones() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.zoneTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.ZoneList.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					Zone zone = (Zone)iterator2.next();
					this.zoneTempList.add(zone);
				}
			}
		}

		return this.zoneTempList;
	}

	public Stack getAllContainerDistributions() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.conTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.ContainerDistributions.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					ContainerDistribution containerDistribution = (ContainerDistribution)iterator2.next();
					this.conTempList.add(containerDistribution);
				}
			}
		}

		return this.conTempList;
	}

	public Stack getAllShelfDistributions() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.shelfTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.ShelfDistributions.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					ShelfDistribution shelfDistribution = (ShelfDistribution)iterator2.next();
					this.shelfTempList.add(shelfDistribution);
				}
			}
		}

		return this.shelfTempList;
	}

	public Stack getAllFloorDistributions() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.floorTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.FloorDistributions.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					FloorDistribution floorDistribution = (FloorDistribution)iterator2.next();
					this.floorTempList.add(floorDistribution);
				}
			}
		}

		return this.floorTempList;
	}

	public ArrayList getAllGameSounds() {
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.ModuleMap.values().iterator();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				arrayList.addAll(scriptModule.GameSoundList);
			}
		}

		return arrayList;
	}

	public ArrayList getAllVehicleScripts() {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.vehicleScriptTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				this.vehicleScriptTempList.addAll(scriptModule.VehicleMap.values());
			}
		}

		return this.vehicleScriptTempList;
	}

	public Stack getZones(String string) {
		Iterator iterator = this.ModuleMap.values().iterator();
		this.zoneTempList.clear();
		while (iterator != null && iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			if (!scriptModule.disabled) {
				Iterator iterator2 = scriptModule.ZoneList.iterator();
				while (iterator2 != null && iterator2.hasNext()) {
					Zone zone = (Zone)iterator2.next();
					if (zone.name.equals(string)) {
						this.zoneTempList.add(zone);
					}
				}
			}
		}

		return this.zoneTempList;
	}

	public void AddZone(String string, String string2, Zone zone) {
		ScriptModule scriptModule = null;
		if (this.ModuleMap.containsKey(string)) {
			scriptModule = this.getModule(string);
		} else {
			scriptModule = new ScriptModule();
			scriptModule.name = string;
			this.ModuleMap.put(string, scriptModule);
		}

		scriptModule.ZoneMap.put(string2, zone);
		scriptModule.ZoneList.add(zone);
	}

	public void AddRoom(String string, String string2, Room room) {
		ScriptModule scriptModule = null;
		if (this.ModuleMap.containsKey(string)) {
			scriptModule = this.getModule(string);
		} else {
			scriptModule = new ScriptModule();
			scriptModule.name = string;
			this.ModuleMap.put(string, scriptModule);
		}

		scriptModule.RoomMap.put(string2, room);
		scriptModule.RoomList.add(room);
	}

	public void Reset() {
		this.ModuleMap.clear();
		this.ModuleAliases.clear();
		this.TriggerMap.clear();
		this.HookMap.clear();
		this.CustomTriggerMap.clear();
		this.CustomTriggerLastRan.clear();
		this.PlayingScripts.clear();
		this.CachedModules.clear();
		this.FullTypeToItemMap.clear();
	}

	public String getChecksum() {
		return this.checksum;
	}

	public void Load() {
		try {
			ArrayList arrayList = new ArrayList();
			this.searchFolders(ZomboidFileSystem.instance.baseURI, new File("media" + File.separator + "scripts"), arrayList);
			ArrayList arrayList2 = new ArrayList();
			ArrayList arrayList3 = ZomboidFileSystem.instance.getModIDs();
			for (int int1 = 0; int1 < arrayList3.size(); ++int1) {
				String string = ZomboidFileSystem.instance.getModDir((String)arrayList3.get(int1));
				if (string != null) {
					URI uRI = (new File(string)).toURI();
					this.searchFolders(uRI, new File(string + File.separator + "media" + File.separator + "scripts"), arrayList2);
				}
			}

			Comparator comparator = new Comparator(){
				
				public int compare(String arrayList, String arrayList2) {
					String arrayList3 = (new File(arrayList)).getName();
					String int1 = (new File(arrayList2)).getName();
					if (arrayList3.startsWith("template_") && !int1.startsWith("template_")) {
						return -1;
					} else {
						return !arrayList3.startsWith("template_") && int1.startsWith("template_") ? 1 : arrayList.compareTo(arrayList2);
					}
				}
			};

			Collections.sort(arrayList, comparator);
			Collections.sort(arrayList2, comparator);
			arrayList.addAll(arrayList2);
			if (GameClient.bClient || GameServer.bServer) {
				NetChecksum.checksummer.reset(true);
				NetChecksum.GroupOfFiles.initChecksum();
			}

			MultiStageBuilding.stages.clear();
			HashSet hashSet = new HashSet();
			Iterator iterator = arrayList.iterator();
			label66: while (true) {
				String string2;
				String string3;
				do {
					do {
						if (!iterator.hasNext()) {
							if (GameClient.bClient || GameServer.bServer) {
								this.checksum = NetChecksum.checksummer.checksumToString();
							}

							break label66;
						}

						string2 = (String)iterator.next();
					}			 while (hashSet.contains(string2));

					hashSet.add(string2);
					string3 = ZomboidFileSystem.instance.getAbsolutePath(string2);
					this.LoadFile(string3, false);
				}		 while (!GameClient.bClient && !GameServer.bServer);

				NetChecksum.checksummer.addFile(string2, string3);
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		this.buf = new StringBuffer();
		this.buf2 = new StringBuffer();
		Iterator iterator2 = this.ModuleMap.values().iterator();
		while (iterator2.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator2.next();
			Iterator iterator3 = scriptModule.ItemMap.values().iterator();
			while (iterator3.hasNext()) {
				Item item = (Item)iterator3.next();
				this.FullTypeToItemMap.put(item.getFullName(), item);
			}
		}

		RecipeManager.Loaded();
		GameSounds.ScriptsLoaded();
		if (SoundManager.instance != null) {
			SoundManager.instance.debugScriptSounds();
		}

		Translator.debugItemNames();
		Translator.debugMultiStageBuildNames();
		Translator.debugRecipeNames();
	}

	public String getRandomMap() {
		int int1 = Rand.Next(this.MapMap.keySet().size());
		Iterator iterator = this.MapMap.keySet().iterator();
		String string = "";
		for (int int2 = -1; iterator != null && iterator.hasNext() && int2 != int1; ++int2) {
			string = (String)iterator.next();
		}

		return string;
	}

	public Stack getAllRecipesFor(String string) {
		Stack stack = this.getAllRecipes();
		Stack stack2 = new Stack();
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			String string2 = ((Recipe)stack.get(int1)).Result.type;
			if (string2.contains(".")) {
				string2 = string2.substring(string2.indexOf(".") + 1);
			}

			if (string2.equals(string)) {
				stack2.add((Recipe)stack.get(int1));
			}
		}

		return stack2;
	}

	public void StopScript(Script.ScriptInstance scriptInstance) {
		this.toStopInstance.add(scriptInstance);
	}
}
