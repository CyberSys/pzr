package zombie.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoWorld;
import zombie.iso.MultiStageBuilding;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.scripting.objects.AnimationsMesh;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.SoundTimelineScript;
import zombie.scripting.objects.UniqueRecipe;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.VehicleTemplate;
import zombie.util.StringUtils;
import zombie.vehicles.VehicleEngineRPM;
import zombie.world.WorldDictionary;


public final class ScriptManager implements IScriptObjectStore {
	public static final ScriptManager instance = new ScriptManager();
	public String currentFileName;
	public final ArrayList scriptsWithVehicles = new ArrayList();
	public final ArrayList scriptsWithVehicleTemplates = new ArrayList();
	public final HashMap ModuleMap = new HashMap();
	public final ArrayList ModuleList = new ArrayList();
	private final HashMap FullTypeToItemMap = new HashMap();
	private final HashMap SoundTimelineMap = new HashMap();
	public ScriptModule CurrentLoadingModule = null;
	private final HashMap ModuleAliases = new HashMap();
	private final StringBuilder buf = new StringBuilder();
	private final HashMap CachedModules = new HashMap();
	private final ArrayList recipesTempList = new ArrayList();
	private final Stack evolvedRecipesTempList = new Stack();
	private final Stack uniqueRecipesTempList = new Stack();
	private final ArrayList itemTempList = new ArrayList();
	private final HashMap tagToItemMap = new HashMap();
	private final HashMap typeToItemMap = new HashMap();
	private final ArrayList animationsMeshTempList = new ArrayList();
	private final ArrayList mannequinScriptTempList = new ArrayList();
	private final ArrayList modelScriptTempList = new ArrayList();
	private final ArrayList vehicleScriptTempList = new ArrayList();
	private final HashMap clothingToItemMap = new HashMap();
	private final ArrayList visualDamagesList = new ArrayList();
	private static final String Base = "Base";
	private static final String Base_Module = "Base.";
	private String checksum = "";
	private HashMap tempFileToModMap;
	private static String currentLoadFileMod;
	private static String currentLoadFileAbsPath;
	public static final String VanillaID = "pz-vanilla";

	public void ParseScript(String string) {
		if (DebugLog.isEnabled(DebugType.Script)) {
			DebugLog.Script.debugln("Parsing...");
		}

		ArrayList arrayList = ScriptParser.parseTokens(string);
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = (String)arrayList.get(int1);
			this.CreateFromToken(string2);
		}
	}

	public void update() {
	}

	public void LoadFile(String string, boolean boolean1) throws FileNotFoundException {
		if (DebugLog.isEnabled(DebugType.Script)) {
			DebugLog.Script.debugln(string + (boolean1 ? " bLoadJar" : ""));
		}

		if (!GameServer.bServer) {
			Thread.yield();
			Core.getInstance().DoFrameReady();
		}

		if (string.contains(".tmx")) {
			IsoWorld.mapPath = string.substring(0, string.lastIndexOf("/"));
			IsoWorld.mapUseJar = boolean1;
			DebugLog.Script.debugln("  file is a .tmx (map) file. Set mapPath to " + IsoWorld.mapPath + (IsoWorld.mapUseJar ? " mapUseJar" : ""));
		} else if (!string.endsWith(".txt")) {
			DebugLog.Script.warn(" file is not a .txt (script) file: " + string);
		} else {
			InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string, !boolean1);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			this.buf.setLength(0);
			String string2 = null;
			String string3 = "";
			label135: {
				try {
					while (true) {
						if ((string2 = bufferedReader.readLine()) == null) {
							break label135;
						}

						this.buf.append(string2);
						this.buf.append('\n');
					}
				} catch (Exception exception) {
					DebugLog.Script.error("Exception thrown reading file " + string + "\n  " + exception);
				} finally {
					try {
						bufferedReader.close();
						inputStreamReader.close();
					} catch (Exception exception2) {
						DebugLog.Script.error("Exception thrown closing file " + string + "\n  " + exception2);
						exception2.printStackTrace(DebugLog.Script);
					}
				}

				return;
			}

			string3 = this.buf.toString();
			string3 = ScriptParser.stripComments(string3);
			this.currentFileName = string;
			this.ParseScript(string3);
			this.currentFileName = null;
		}
	}

	private void CreateFromToken(String string) {
		string = string.trim();
		if (string.indexOf("module") == 0) {
			int int1 = string.indexOf("{");
			int int2 = string.lastIndexOf("}");
			String[] stringArray = string.split("[{}]");
			String string2 = stringArray[0];
			string2 = string2.replace("module", "");
			string2 = string2.trim();
			String string3 = string.substring(int1 + 1, int2);
			ScriptModule scriptModule = (ScriptModule)this.ModuleMap.get(string2);
			if (scriptModule == null) {
				if (DebugLog.isEnabled(DebugType.Script)) {
					DebugLog.Script.debugln("Adding new module: " + string2);
				}

				scriptModule = new ScriptModule();
				this.ModuleMap.put(string2, scriptModule);
				this.ModuleList.add(scriptModule);
			}

			scriptModule.Load(string2, string3);
		}
	}

	public void searchFolders(URI uRI, File file, ArrayList arrayList) {
		if (file.isDirectory()) {
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				String string = file.getAbsolutePath();
				this.searchFolders(uRI, new File(string + File.separator + stringArray[int1]), arrayList);
			}
		} else if (file.getAbsolutePath().toLowerCase().endsWith(".txt")) {
			arrayList.add(ZomboidFileSystem.instance.getRelativeFile(uRI, file.getAbsolutePath()));
		}
	}

	public static String getItemName(String string) {
		int int1 = string.indexOf(46);
		return int1 == -1 ? string : string.substring(int1 + 1);
	}

	public ScriptModule getModule(String string) {
		return this.getModule(string, true);
	}

	public ScriptModule getModule(String string, boolean boolean1) {
		if (!string.trim().equals("Base") && !string.startsWith("Base.")) {
			if (this.CachedModules.containsKey(string)) {
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
							return boolean1 ? (ScriptModule)this.ModuleMap.get("Base") : null;
						}
					}
				}
			}
		} else {
			return (ScriptModule)this.ModuleMap.get("Base");
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

	public Item getItem(String string) {
		if (string.contains(".") && this.FullTypeToItemMap.containsKey(string)) {
			return (Item)this.FullTypeToItemMap.get(string);
		} else {
			ScriptModule scriptModule = this.getModule(string);
			return scriptModule == null ? null : scriptModule.getItem(getItemName(string));
		}
	}

	public Item FindItem(String string) {
		return this.FindItem(string, true);
	}

	public Item FindItem(String string, boolean boolean1) {
		if (string.contains(".") && this.FullTypeToItemMap.containsKey(string)) {
			return (Item)this.FullTypeToItemMap.get(string);
		} else {
			ScriptModule scriptModule = this.getModule(string, boolean1);
			if (scriptModule == null) {
				return null;
			} else {
				Item item = scriptModule.getItem(getItemName(string));
				if (item == null) {
					for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
						ScriptModule scriptModule2 = (ScriptModule)this.ModuleList.get(int1);
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

	public VehicleEngineRPM getVehicleEngineRPM(String string) {
		ScriptModule scriptModule = this.getModule(string);
		return scriptModule == null ? null : scriptModule.getVehicleEngineRPM(getItemName(string));
	}

	public void CheckExitPoints() {
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled && scriptModule.CheckExitPoints()) {
				return;
			}
		}
	}

	public ArrayList getAllItems() {
		if (this.itemTempList.isEmpty()) {
			for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
				ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
				if (!scriptModule.disabled) {
					Iterator iterator = scriptModule.ItemMap.values().iterator();
					while (iterator.hasNext()) {
						Item item = (Item)iterator.next();
						this.itemTempList.add(item);
					}
				}
			}
		}

		return this.itemTempList;
	}

	public ArrayList getItemsTag(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			throw new IllegalArgumentException("invalid tag \"" + string + "\"");
		} else {
			string = string.toLowerCase(Locale.ENGLISH);
			ArrayList arrayList = (ArrayList)this.tagToItemMap.get(string);
			if (arrayList != null) {
				return arrayList;
			} else {
				arrayList = new ArrayList();
				ArrayList arrayList2 = this.getAllItems();
				for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
					Item item = (Item)arrayList2.get(int1);
					for (int int2 = 0; int2 < item.Tags.size(); ++int2) {
						if (((String)item.Tags.get(int2)).equalsIgnoreCase(string)) {
							arrayList.add(item);
							break;
						}
					}
				}

				this.tagToItemMap.put(string, arrayList);
				return arrayList;
			}
		}
	}

	public ArrayList getItemsByType(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			throw new IllegalArgumentException("invalid type \"" + string + "\"");
		} else {
			ArrayList arrayList = (ArrayList)this.typeToItemMap.get(string);
			if (arrayList != null) {
				return arrayList;
			} else {
				arrayList = new ArrayList();
				for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
					ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
					if (!scriptModule.disabled) {
						Item item = (Item)this.FullTypeToItemMap.get(StringUtils.moduleDotType(scriptModule.name, string));
						if (item != null) {
							arrayList.add(item);
						}
					}
				}

				this.tagToItemMap.put(string, arrayList);
				return arrayList;
			}
		}
	}

	public List getAllFixing(List list) {
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				list.addAll(scriptModule.FixingMap.values());
			}
		}

		return list;
	}

	public ArrayList getAllRecipes() {
		this.recipesTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				for (int int2 = 0; int2 < scriptModule.RecipeMap.size(); ++int2) {
					Recipe recipe = (Recipe)scriptModule.RecipeMap.get(int2);
					this.recipesTempList.add(recipe);
				}
			}
		}

		return this.recipesTempList;
	}

	public Stack getAllEvolvedRecipes() {
		this.evolvedRecipesTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				for (int int2 = 0; int2 < scriptModule.EvolvedRecipeMap.size(); ++int2) {
					EvolvedRecipe evolvedRecipe = (EvolvedRecipe)scriptModule.EvolvedRecipeMap.get(int2);
					this.evolvedRecipesTempList.add(evolvedRecipe);
				}
			}
		}

		return this.evolvedRecipesTempList;
	}

	public Stack getAllUniqueRecipes() {
		this.uniqueRecipesTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				Iterator iterator = scriptModule.UniqueRecipeMap.iterator();
				while (iterator != null && iterator.hasNext()) {
					UniqueRecipe uniqueRecipe = (UniqueRecipe)iterator.next();
					this.uniqueRecipesTempList.add(uniqueRecipe);
				}
			}
		}

		return this.uniqueRecipesTempList;
	}

	public ArrayList getAllGameSounds() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				arrayList.addAll(scriptModule.GameSoundList);
			}
		}

		return arrayList;
	}

	public ArrayList getAllRuntimeAnimationScripts() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				arrayList.addAll(scriptModule.RuntimeAnimationScriptMap.values());
			}
		}

		return arrayList;
	}

	public AnimationsMesh getAnimationsMesh(String string) {
		ScriptModule scriptModule = this.getModule(string);
		if (scriptModule == null) {
			return null;
		} else {
			string = getItemName(string);
			return (AnimationsMesh)scriptModule.AnimationsMeshMap.get(string);
		}
	}

	public ArrayList getAllAnimationsMeshes() {
		this.animationsMeshTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				this.animationsMeshTempList.addAll(scriptModule.AnimationsMeshMap.values());
			}
		}

		return this.animationsMeshTempList;
	}

	public MannequinScript getMannequinScript(String string) {
		ScriptModule scriptModule = this.getModule(string);
		if (scriptModule == null) {
			return null;
		} else {
			string = getItemName(string);
			return (MannequinScript)scriptModule.MannequinScriptMap.get(string);
		}
	}

	public ArrayList getAllMannequinScripts() {
		this.mannequinScriptTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				this.mannequinScriptTempList.addAll(scriptModule.MannequinScriptMap.values());
			}
		}

		this.mannequinScriptTempList.sort((var0,int1x)->{
			return String.CASE_INSENSITIVE_ORDER.compare(var0.getName(), int1x.getName());
		});
		return this.mannequinScriptTempList;
	}

	public ModelScript getModelScript(String string) {
		ScriptModule scriptModule = this.getModule(string);
		if (scriptModule == null) {
			return null;
		} else {
			string = getItemName(string);
			return (ModelScript)scriptModule.ModelScriptMap.get(string);
		}
	}

	public ArrayList getAllModelScripts() {
		this.modelScriptTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				this.modelScriptTempList.addAll(scriptModule.ModelScriptMap.values());
			}
		}

		return this.modelScriptTempList;
	}

	public ArrayList getAllVehicleScripts() {
		this.vehicleScriptTempList.clear();
		for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
			if (!scriptModule.disabled) {
				this.vehicleScriptTempList.addAll(scriptModule.VehicleMap.values());
			}
		}

		return this.vehicleScriptTempList;
	}

	public SoundTimelineScript getSoundTimeline(String string) {
		if (this.SoundTimelineMap.isEmpty()) {
			for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
				ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int1);
				if (!scriptModule.disabled) {
					this.SoundTimelineMap.putAll(scriptModule.SoundTimelineMap);
				}
			}
		}

		return (SoundTimelineScript)this.SoundTimelineMap.get(string);
	}

	public void Reset() {
		Iterator iterator = this.ModuleList.iterator();
		while (iterator.hasNext()) {
			ScriptModule scriptModule = (ScriptModule)iterator.next();
			scriptModule.Reset();
		}

		this.ModuleMap.clear();
		this.ModuleList.clear();
		this.ModuleAliases.clear();
		this.CachedModules.clear();
		this.FullTypeToItemMap.clear();
		this.itemTempList.clear();
		this.tagToItemMap.clear();
		this.typeToItemMap.clear();
		this.clothingToItemMap.clear();
		this.scriptsWithVehicles.clear();
		this.scriptsWithVehicleTemplates.clear();
		this.SoundTimelineMap.clear();
	}

	public String getChecksum() {
		return this.checksum;
	}

	public static String getCurrentLoadFileMod() {
		return currentLoadFileMod;
	}

	public static String getCurrentLoadFileAbsPath() {
		return currentLoadFileAbsPath;
	}

	public void Load() {
		try {
			WorldDictionary.StartScriptLoading();
			this.tempFileToModMap = new HashMap();
			ArrayList arrayList = new ArrayList();
			this.searchFolders(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaFile("scripts"), arrayList);
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(string), "pz-vanilla");
			}

			ArrayList arrayList2 = new ArrayList();
			ArrayList arrayList3 = ZomboidFileSystem.instance.getModIDs();
			for (int int1 = 0; int1 < arrayList3.size(); ++int1) {
				String string2 = ZomboidFileSystem.instance.getModDir((String)arrayList3.get(int1));
				if (string2 != null) {
					URI uRI = (new File(string2)).toURI();
					int int2 = arrayList2.size();
					this.searchFolders(uRI, new File(string2 + File.separator + "media" + File.separator + "scripts"), arrayList2);
					if (((String)arrayList3.get(int1)).equals("pz-vanilla")) {
						throw new RuntimeException("Warning mod id is named pz-vanilla!");
					}

					for (int int3 = int2; int3 < arrayList2.size(); ++int3) {
						String string3 = (String)arrayList2.get(int3);
						this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(string3), (String)arrayList3.get(int1));
					}
				}
			}

			Comparator comparator = new Comparator(){
				
				public int compare(String arrayList, String iterator) {
					String string = (new File(arrayList)).getName();
					String int1 = (new File(iterator)).getName();
					if (string.startsWith("template_") && !int1.startsWith("template_")) {
						return -1;
					} else {
						return !string.startsWith("template_") && int1.startsWith("template_") ? 1 : arrayList.compareTo(iterator);
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
			Iterator iterator2 = arrayList.iterator();
			label76: while (true) {
				String string4;
				String string5;
				do {
					do {
						if (!iterator2.hasNext()) {
							if (GameClient.bClient || GameServer.bServer) {
								this.checksum = NetChecksum.checksummer.checksumToString();
								if (GameServer.bServer) {
									DebugLog.General.println("scriptChecksum: " + this.checksum);
								}
							}

							break label76;
						}

						string4 = (String)iterator2.next();
					}			 while (hashSet.contains(string4));

					hashSet.add(string4);
					string5 = ZomboidFileSystem.instance.getAbsolutePath(string4);
					currentLoadFileAbsPath = string5;
					currentLoadFileMod = (String)this.tempFileToModMap.get(string5);
					this.LoadFile(string4, false);
				}		 while (!GameClient.bClient && !GameServer.bServer);

				NetChecksum.checksummer.addFile(string4, string5);
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		this.buf.setLength(0);
		for (int int4 = 0; int4 < this.ModuleList.size(); ++int4) {
			ScriptModule scriptModule = (ScriptModule)this.ModuleList.get(int4);
			Iterator iterator3 = scriptModule.ItemMap.values().iterator();
			while (iterator3.hasNext()) {
				Item item = (Item)iterator3.next();
				this.FullTypeToItemMap.put(item.getFullName(), item);
			}
		}

		this.debugItems();
		this.resolveItemTypes();
		WorldDictionary.ScriptsLoaded();
		RecipeManager.Loaded();
		GameSounds.ScriptsLoaded();
		ModelScript.ScriptsLoaded();
		if (SoundManager.instance != null) {
			SoundManager.instance.debugScriptSounds();
		}

		Translator.debugItemEvolvedRecipeNames();
		Translator.debugItemNames();
		Translator.debugMultiStageBuildNames();
		Translator.debugRecipeNames();
		this.createClothingItemMap();
		this.createZedDmgMap();
	}

	private void debugItems() {
		ArrayList arrayList = instance.getAllItems();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			if (item.getType() == Item.Type.Drainable && item.getReplaceOnUse() != null) {
				DebugLog.Script.warn("%s ReplaceOnUse instead of ReplaceOnDeplete", item.getFullName());
			}

			if (item.getType() == Item.Type.Weapon && !item.HitSound.equals(item.hitFloorSound)) {
				boolean boolean1 = true;
			}

			if (!StringUtils.isNullOrEmpty(item.worldStaticModel)) {
				ModelScript modelScript = this.getModelScript(item.worldStaticModel);
				if (modelScript != null && modelScript.getAttachmentById("world") != null) {
					boolean boolean2 = true;
				}
			}
		}
	}

	public ArrayList getAllRecipesFor(String string) {
		ArrayList arrayList = this.getAllRecipes();
		ArrayList arrayList2 = new ArrayList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = ((Recipe)arrayList.get(int1)).Result.type;
			if (string2.contains(".")) {
				string2 = string2.substring(string2.indexOf(".") + 1);
			}

			if (string2.equals(string)) {
				arrayList2.add((Recipe)arrayList.get(int1));
			}
		}

		return arrayList2;
	}

	public String getItemTypeForClothingItem(String string) {
		return (String)this.clothingToItemMap.get(string);
	}

	public Item getItemForClothingItem(String string) {
		String string2 = this.getItemTypeForClothingItem(string);
		return string2 == null ? null : this.FindItem(string2);
	}

	private void createZedDmgMap() {
		this.visualDamagesList.clear();
		ScriptModule scriptModule = this.getModule("Base");
		Iterator iterator = scriptModule.ItemMap.values().iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			if (!StringUtils.isNullOrWhitespace(item.getBodyLocation()) && "ZedDmg".equals(item.getBodyLocation())) {
				this.visualDamagesList.add(item.getName());
			}
		}
	}

	public ArrayList getZedDmgMap() {
		return this.visualDamagesList;
	}

	private void createClothingItemMap() {
		Iterator iterator = this.getAllItems().iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			if (!StringUtils.isNullOrWhitespace(item.getClothingItem())) {
				if (DebugLog.isEnabled(DebugType.Script)) {
					DebugLog.Script.debugln("ClothingItem \"%s\" <---> Item \"%s\"", item.getClothingItem(), item.getFullName());
				}

				this.clothingToItemMap.put(item.getClothingItem(), item.getFullName());
			}
		}
	}

	private void resolveItemTypes() {
		Iterator iterator = this.getAllItems().iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			item.resolveItemTypes();
		}
	}

	public String resolveItemType(ScriptModule scriptModule, String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else if (string.contains(".")) {
			return string;
		} else {
			Item item = scriptModule.getItem(string);
			if (item != null) {
				return item.getFullName();
			} else {
				for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
					ScriptModule scriptModule2 = (ScriptModule)this.ModuleList.get(int1);
					if (!scriptModule2.disabled) {
						item = scriptModule2.getItem(string);
						if (item != null) {
							return item.getFullName();
						}
					}
				}

				return "???." + string;
			}
		}
	}

	public String resolveModelScript(ScriptModule scriptModule, String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else if (string.contains(".")) {
			return string;
		} else {
			ModelScript modelScript = scriptModule.getModelScript(string);
			if (modelScript != null) {
				return modelScript.getFullType();
			} else {
				for (int int1 = 0; int1 < this.ModuleList.size(); ++int1) {
					ScriptModule scriptModule2 = (ScriptModule)this.ModuleList.get(int1);
					if (scriptModule2 != scriptModule && !scriptModule2.disabled) {
						modelScript = scriptModule2.getModelScript(string);
						if (modelScript != null) {
							return modelScript.getFullType();
						}
					}
				}

				return "???." + string;
			}
		}
	}

	public Item getSpecificItem(String string) {
		if (!string.contains(".")) {
			DebugLog.log("ScriptManager.getSpecificItem requires a full type name, cannot find: " + string);
			if (Core.bDebug) {
				throw new RuntimeException("ScriptManager.getSpecificItem requires a full type name, cannot find: " + string);
			} else {
				return null;
			}
		} else if (this.FullTypeToItemMap.containsKey(string)) {
			return (Item)this.FullTypeToItemMap.get(string);
		} else {
			int int1 = string.indexOf(".");
			String string2 = string.substring(0, int1);
			String string3 = string.substring(int1 + 1);
			ScriptModule scriptModule = this.getModule(string2, false);
			return scriptModule == null ? null : scriptModule.getItem(string3);
		}
	}
}
