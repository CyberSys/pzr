package zombie.scripting.objects;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.MultiStageBuilding;
import zombie.scripting.IScriptObjectStore;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParsingUtils;


public class ScriptModule extends BaseScriptObject implements IScriptObjectStore {
	public Stack ValidMaps = new Stack();
	public Stack ExitPoints = new Stack();
	public String name;
	public String value;
	public HashMap WaypointMap = new HashMap();
	public HashMap RoomMap = new HashMap();
	public Stack RoomList = new Stack();
	public HashMap DoorMap = new HashMap();
	public HashMap ItemMap = new HashMap();
	public final HashMap GameSoundMap = new HashMap();
	public final ArrayList GameSoundList = new ArrayList();
	public HashMap VehicleMap = new HashMap();
	public HashMap VehicleTemplateMap = new HashMap();
	public HashMap ScriptMap = new HashMap();
	public HashMap CharacterMap = new HashMap();
	public ArrayList RecipeMap = new ArrayList();
	public HashMap RecipesWithDotInName = new HashMap();
	public ArrayList EvolvedRecipeMap = new ArrayList();
	public ArrayList UniqueRecipeMap = new ArrayList();
	public HashMap FixingMap = new HashMap();
	public HashMap InventoryMap = new HashMap();
	public HashMap ActivatableMap = new HashMap();
	public HashMap TalkerMap = new HashMap();
	public HashMap ScriptContainerMap = new HashMap();
	public HashMap ConditionMap = new HashMap();
	public HashMap FlagMap = new HashMap();
	public HashMap ZoneMap = new HashMap();
	public Stack ZoneList = new Stack();
	public HashMap RandomSelectorMap = new HashMap();
	public Stack ContainerDistributions = new Stack();
	public Stack FloorDistributions = new Stack();
	public Stack ShelfDistributions = new Stack();
	public Stack Imports = new Stack();
	public boolean disabled = false;
	public HashMap LanguageMap = new HashMap();

	public boolean ValidMapCheck(String string) {
		return this.ValidMaps.isEmpty() ? true : this.ValidMaps.contains(string);
	}

	public void Load(String string, String string2) {
		this.name = string;
		this.value = string2.trim();
		ScriptManager.instance.CurrentLoadingModule = this;
		this.ParseScriptPP(this.value);
		this.ParseScript(this.value);
		this.value = "";
	}

	private void CreateFromTokenPP(String string) {
		string = string.trim();
		String[] stringArray;
		String string2;
		if (string.indexOf("zone") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("zone", "");
			string2 = string2.trim();
			Zone zone = new Zone();
			this.ZoneMap.put(string2, zone);
			this.ZoneList.add(zone);
		} else if (string.indexOf("waypoint") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("waypoint", "");
			string2 = string2.trim();
			Waypoint waypoint = new Waypoint();
			this.WaypointMap.put(string2, waypoint);
		} else if (string.indexOf("room") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("room", "");
			string2 = string2.trim();
			Room room = new Room();
			this.RoomMap.put(string2, room);
			this.RoomList.add(room);
		} else if (string.indexOf("character") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("character", "");
			string2 = string2.trim();
			ScriptCharacter scriptCharacter = new ScriptCharacter();
			this.CharacterMap.put(string2, scriptCharacter);
		} else if (string.indexOf("item") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("item", "");
			string2 = string2.trim();
			Item item = new Item();
			this.ItemMap.put(string2, item);
		} else if (string.indexOf("door") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("door", "");
			string2 = string2.trim();
			Door door = new Door();
			this.DoorMap.put(string2, door);
		} else if (string.indexOf("activatable") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("activatable", "");
			string2 = string2.trim();
			ScriptActivatable scriptActivatable = new ScriptActivatable();
			this.ActivatableMap.put(string2, scriptActivatable);
		} else if (string.indexOf("talker") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("talker", "");
			string2 = string2.trim();
			ScriptTalker scriptTalker = new ScriptTalker();
			this.TalkerMap.put(string2, scriptTalker);
		} else {
			String string3;
			String[] stringArray2;
			if (string.indexOf("language") == 0) {
				stringArray = string.split("[{}]");
				stringArray2 = ScriptParsingUtils.SplitExceptInbetween(stringArray[1], ",", "\"");
				string3 = stringArray[0];
				string3 = string3.replace("language", "");
				string3 = new String(string3.trim());
				LanguageDefinition languageDefinition = new LanguageDefinition();
				languageDefinition.Load(string3, stringArray2);
				this.LanguageMap.put(string3, languageDefinition);
			} else if (string.indexOf("container ") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("container", "");
				string2 = new String(string2.trim());
				ScriptContainer scriptContainer = new ScriptContainer();
				this.ScriptContainerMap.put(string2, scriptContainer);
			} else if (string.indexOf("questcondition") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("questcondition", "");
				string2 = string2.trim();
				QuestTaskCondition questTaskCondition = new QuestTaskCondition();
				this.ConditionMap.put(string2, questTaskCondition);
			} else if (string.indexOf("recipe") != 0) {
				if (string.indexOf("randomselector") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("randomselector", "");
					string2 = string2.trim();
					RandomSelector randomSelector = new RandomSelector();
					this.RandomSelectorMap.put(string2, randomSelector);
				} else if (string.indexOf("inventory") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("inventory", "");
					string2 = string2.trim();
					Inventory inventory = new Inventory();
					this.InventoryMap.put(string2, inventory);
				} else if (string.indexOf("scriptflag") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("scriptflag", "");
					string2 = string2.trim();
					ScriptFlag scriptFlag = new ScriptFlag();
					this.FlagMap.put(string2, scriptFlag);
				} else {
					Script script;
					int int1;
					if (string.indexOf("instancescript") == 0) {
						int1 = string.indexOf("{");
						stringArray2 = new String[]{string.substring(0, int1).trim(), string.substring(int1 + 1)};
						stringArray2[1] = stringArray2[1].substring(0, stringArray2[1].length() - 1);
						string3 = stringArray2[0];
						string3 = string3.replace("instancescript", "");
						string3 = string3.trim();
						script = new Script();
						script.Instancable = true;
						this.ScriptMap.put(string3, script);
					} else if (string.indexOf("script") == 0) {
						int1 = string.indexOf("{");
						stringArray2 = new String[]{string.substring(0, int1).trim(), string.substring(int1 + 1)};
						stringArray2[1] = stringArray2[1].substring(0, stringArray2[1].length() - 1);
						string3 = stringArray2[0];
						string3 = string3.replace("script", "");
						string3 = string3.trim();
						script = new Script();
						this.ScriptMap.put(string3, script);
					} else if (string.indexOf("sound") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("sound", "");
						string2 = string2.trim();
						GameSoundScript gameSoundScript;
						if (this.GameSoundMap.containsKey(string2)) {
							gameSoundScript = (GameSoundScript)this.GameSoundMap.get(string2);
							gameSoundScript.reset();
						} else {
							gameSoundScript = new GameSoundScript();
							this.GameSoundMap.put(string2, gameSoundScript);
							this.GameSoundList.add(gameSoundScript);
						}
					} else if (string.indexOf("vehicle") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("vehicle", "");
						string2 = string2.trim();
						VehicleScript vehicleScript = new VehicleScript();
						this.VehicleMap.put(string2, vehicleScript);
					} else if (string.indexOf("template") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("template", "");
						String[] stringArray3 = string2.trim().split("\\s+");
						if (stringArray3.length == 2) {
							String string4 = stringArray3[0].trim();
							String string5 = stringArray3[1].trim();
							if ("vehicle".equals(string4)) {
								VehicleTemplate vehicleTemplate = new VehicleTemplate(this, string5, string);
								vehicleTemplate.module = this;
								this.VehicleTemplateMap.put(string5, vehicleTemplate);
							}
						}
					}
				}
			}
		}
	}

	private void CreateFromToken(String string) {
		string = string.trim();
		String[] stringArray;
		String string2;
		String[] stringArray2;
		if (string.indexOf("zone") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("zone", "");
			string2 = string2.trim();
			stringArray2 = stringArray[1].split(",");
			Zone zone = (Zone)this.ZoneMap.get(string2);
			zone.module = this;
			zone.Load(string2, stringArray2);
		} else if (string.indexOf("waypoint") == 0) {
			stringArray = string.split("[{}]");
			string2 = stringArray[0];
			string2 = string2.replace("waypoint", "");
			string2 = string2.trim();
			stringArray2 = stringArray[1].split(",");
			Waypoint waypoint = (Waypoint)this.WaypointMap.get(string2);
			waypoint.module = this;
			waypoint.Load(string2, stringArray2);
		} else {
			int int1;
			if (string.indexOf("imports") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("waypoint", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				for (int1 = 0; int1 < stringArray2.length; ++int1) {
					if (stringArray2[int1].trim().length() > 0) {
						String string3 = stringArray2[int1].trim();
						if (string3.equals(this.getName())) {
							DebugLog.log("ERROR: module \"" + this.getName() + "\" imports itself");
						} else {
							this.Imports.add(string3);
						}
					}
				}
			} else if (string.indexOf("validmaps") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("validmaps", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				for (int1 = 0; int1 < stringArray2.length; ++int1) {
					if (stringArray2[int1].trim().length() > 0) {
						this.ValidMaps.add(stringArray2[int1].trim());
					}
				}
			} else if (string.indexOf("cellexit") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("validmaps", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				ScriptModule.Exit exit = new ScriptModule.Exit();
				exit.fromX1 = Integer.parseInt(stringArray2[0].trim());
				exit.fromY1 = Integer.parseInt(stringArray2[1].trim());
				exit.fromZ1 = Integer.parseInt(stringArray2[2].trim());
				exit.fromX2 = Integer.parseInt(stringArray2[3].trim());
				exit.fromY2 = Integer.parseInt(stringArray2[4].trim());
				exit.fromZ2 = Integer.parseInt(stringArray2[5].trim());
				exit.map = stringArray2[6].trim();
				exit.toX1 = Integer.parseInt(stringArray2[7].trim());
				exit.toY1 = Integer.parseInt(stringArray2[8].trim());
				exit.toZ1 = Integer.parseInt(stringArray2[9].trim());
				exit.toX2 = Integer.parseInt(stringArray2[10].trim());
				exit.toY2 = Integer.parseInt(stringArray2[11].trim());
				exit.toZ2 = Integer.parseInt(stringArray2[12].trim());
				this.ExitPoints.add(exit);
			} else if (string.indexOf("room") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("room", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				Room room = (Room)this.RoomMap.get(string2);
				room.module = this;
				room.Load(string2, stringArray2);
			} else if (string.indexOf("character") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("character", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				ScriptCharacter scriptCharacter = (ScriptCharacter)this.CharacterMap.get(string2);
				scriptCharacter.module = this;
				scriptCharacter.Load(string2, stringArray2);
			} else if (string.indexOf("talker") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("talker", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				ScriptTalker scriptTalker = (ScriptTalker)this.TalkerMap.get(string2);
				scriptTalker.module = this;
				scriptTalker.Load(string2, stringArray2);
			} else if (string.indexOf("activatable") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("activatable", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				ScriptActivatable scriptActivatable = (ScriptActivatable)this.ActivatableMap.get(string2);
				scriptActivatable.module = this;
				scriptActivatable.Load(string2, stringArray2);
			} else if (string.indexOf("container ") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("container ", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				ScriptContainer scriptContainer = (ScriptContainer)this.ScriptContainerMap.get(string2);
				scriptContainer.module = this;
				scriptContainer.Load(string2, stringArray2);
			} else if (string.indexOf("questcondition") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("questcondition", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				QuestTaskCondition questTaskCondition = (QuestTaskCondition)this.ConditionMap.get(string2);
				questTaskCondition.module = this;
				questTaskCondition.Load(string2, stringArray2);
			} else if (string.indexOf("door") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("door", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				Door door = (Door)this.DoorMap.get(string2);
				door.module = this;
				door.Load(string2, stringArray2);
			} else if (string.indexOf("item") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("item", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				Item item = (Item)this.ItemMap.get(string2);
				item.module = this;
				try {
					item.Load(string2, stringArray2);
				} catch (Exception exception) {
					DebugLog.log((Object)exception);
				}
			} else if (string.indexOf("randomselector") == 0) {
				stringArray = string.split("[{}]");
				string2 = stringArray[0];
				string2 = string2.replace("randomselector", "");
				string2 = string2.trim();
				stringArray2 = stringArray[1].split(",");
				RandomSelector randomSelector = (RandomSelector)this.RandomSelectorMap.get(string2);
				randomSelector.module = this;
				randomSelector.Load(string2, stringArray2);
			} else {
				Trigger trigger;
				if (string.indexOf("trigger") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("trigger", "");
					string2 = string2.trim();
					stringArray2 = ScriptParsingUtils.SplitExceptInbetween(stringArray[1], ",", "(", ")");
					trigger = new Trigger();
					string2 = string2.toLowerCase();
					trigger.module = this;
					trigger.Load(string2, stringArray2);
					if (ScriptManager.instance.TriggerMap.containsKey(string2)) {
						((Stack)ScriptManager.instance.TriggerMap.get(string2)).add(trigger);
					} else {
						ScriptManager.instance.TriggerMap.put(string2, new Stack());
						((Stack)ScriptManager.instance.TriggerMap.get(string2)).add(trigger);
						ScriptManager.instance.CustomTriggerLastRan.put(trigger.name, 0);
					}
				} else if (string.indexOf("customtrigger") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("customtrigger", "");
					string2 = string2.trim();
					stringArray2 = ScriptParsingUtils.SplitExceptInbetween(stringArray[1], ",", "(", ")");
					trigger = new Trigger();
					string2 = string2.toLowerCase();
					trigger.module = this;
					trigger.Load(string2, stringArray2);
					trigger.Locked = true;
					if (ScriptManager.instance.CustomTriggerMap.containsKey(string2)) {
						((Stack)ScriptManager.instance.CustomTriggerMap.get(string2)).add(trigger);
					} else {
						ScriptManager.instance.CustomTriggerMap.put(string2, new Stack());
						((Stack)ScriptManager.instance.CustomTriggerMap.get(string2)).add(trigger);
						ScriptManager.instance.CustomTriggerLastRan.put(trigger.name, 0);
					}
				} else if (string.indexOf("inventory") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("inventory", "");
					string2 = string2.trim();
					stringArray2 = null;
					if (stringArray.length > 1) {
						stringArray2 = stringArray[1].split(",");
					} else {
						stringArray2 = new String[1];
					}

					Inventory inventory = (Inventory)this.InventoryMap.get(string2);
					inventory.module = this;
					inventory.Load(string2, stringArray2);
				} else if (string.indexOf("scriptflag") == 0) {
					stringArray = string.split("[{}]");
					string2 = stringArray[0];
					string2 = string2.replace("scriptflag", "");
					string2 = string2.trim();
					stringArray2 = stringArray[1].split(",");
					ScriptFlag scriptFlag = (ScriptFlag)this.FlagMap.get(string2);
					scriptFlag.module = this;
					scriptFlag.Load(string2, stringArray2);
				} else {
					int int2;
					String string4;
					Script script;
					String[] stringArray3;
					if (string.indexOf("script") == 0) {
						int2 = string.indexOf("{");
						stringArray3 = new String[]{string.substring(0, int2).trim(), string.substring(int2 + 1)};
						stringArray3[1] = stringArray3[1].substring(0, stringArray3[1].length() - 1);
						string4 = stringArray3[0];
						string4 = string4.replace("script", "");
						string4 = string4.trim();
						script = (Script)this.ScriptMap.get(string4);
						script.module = this;
						try {
							script.DoScriptParsing(string4, stringArray3[1]);
						} catch (Exception exception2) {
							DebugLog.log((Object)exception2);
						}
					} else if (string.indexOf("instancescript") == 0) {
						int2 = string.indexOf("{");
						stringArray3 = new String[]{string.substring(0, int2).trim(), string.substring(int2 + 1)};
						stringArray3[1] = stringArray3[1].substring(0, stringArray3[1].length() - 1);
						string4 = stringArray3[0];
						string4 = string4.replace("instancescript", "");
						string4 = string4.trim();
						script = (Script)this.ScriptMap.get(string4);
						script.module = this;
						try {
							script.DoScriptParsing(string4, stringArray3[1]);
						} catch (Exception exception3) {
							DebugLog.log((Object)exception3);
						}
					} else if (string.indexOf("recipe") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("recipe", "");
						string2 = string2.trim();
						stringArray2 = stringArray[1].split(",");
						Recipe recipe = new Recipe();
						this.RecipeMap.add(recipe);
						if (string2.contains(".")) {
							this.RecipesWithDotInName.put(string2, recipe);
						}

						recipe.module = this;
						recipe.Load(string2, stringArray2);
					} else if (string.indexOf("uniquerecipe") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("uniquerecipe", "");
						string2 = string2.trim();
						stringArray2 = stringArray[1].split(",");
						UniqueRecipe uniqueRecipe = new UniqueRecipe(string2);
						this.UniqueRecipeMap.add(uniqueRecipe);
						uniqueRecipe.module = this;
						uniqueRecipe.Load(string2, stringArray2);
					} else if (string.indexOf("evolvedrecipe") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("evolvedrecipe", "");
						string2 = string2.trim();
						stringArray2 = stringArray[1].split(",");
						boolean boolean1 = false;
						Iterator iterator = this.EvolvedRecipeMap.iterator();
						while (iterator.hasNext()) {
							EvolvedRecipe evolvedRecipe = (EvolvedRecipe)iterator.next();
							if (evolvedRecipe.name.equals(string2)) {
								evolvedRecipe.Load(string2, stringArray2);
								evolvedRecipe.module = this;
								boolean1 = true;
							}
						}

						if (!boolean1) {
							EvolvedRecipe evolvedRecipe2 = new EvolvedRecipe(string2);
							this.EvolvedRecipeMap.add(evolvedRecipe2);
							evolvedRecipe2.module = this;
							evolvedRecipe2.Load(string2, stringArray2);
						}
					} else if (string.indexOf("fixing") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("fixing", "");
						string2 = string2.trim();
						stringArray2 = stringArray[1].split(",");
						Fixing fixing = new Fixing();
						fixing.module = this;
						this.FixingMap.put(string2, fixing);
						fixing.Load(string2, stringArray2);
					} else if (string.indexOf("multistagebuild") == 0) {
						stringArray = string.split("[{}]");
						string2 = stringArray[0];
						string2 = string2.replace("multistagebuild", "");
						string2 = string2.trim();
						stringArray2 = stringArray[1].split(",");
						MultiStageBuilding.Stage stage = new MultiStageBuilding().new Stage();
						stage.Load(string2, stringArray2);
						MultiStageBuilding.addStage(stage);
					} else {
						String[] stringArray4;
						if (string.indexOf("containeritemdistribution") == 0) {
							int2 = string.indexOf("{");
							stringArray3 = new String[]{string.substring(0, int2).trim(), string.substring(int2 + 1)};
							stringArray3[1] = stringArray3[1].substring(0, stringArray3[1].length() - 1);
							string4 = stringArray3[0];
							string4 = string4.replace("containeritemdistribution", "");
							string4 = string4.trim();
							stringArray4 = stringArray3[1].split(",");
							ContainerDistribution containerDistribution = new ContainerDistribution();
							containerDistribution.module = this;
							containerDistribution.Load(string4, stringArray4);
							this.ContainerDistributions.add(containerDistribution);
						} else if (string.indexOf("flooritemdistribution") == 0) {
							int2 = string.indexOf("{");
							stringArray3 = new String[]{string.substring(0, int2).trim(), string.substring(int2 + 1)};
							stringArray3[1] = stringArray3[1].substring(0, stringArray3[1].length() - 1);
							string4 = stringArray3[0];
							string4 = string4.replace("flooritemdistribution", "");
							string4 = string4.trim();
							stringArray4 = stringArray3[1].split(",");
							FloorDistribution floorDistribution = new FloorDistribution();
							floorDistribution.module = this;
							floorDistribution.Load(string4, stringArray4);
							this.FloorDistributions.add(floorDistribution);
						} else if (string.indexOf("shelfitemdistribution") == 0) {
							int2 = string.indexOf("{");
							stringArray3 = new String[]{string.substring(0, int2).trim(), string.substring(int2 + 1)};
							stringArray3[1] = stringArray3[1].substring(0, stringArray3[1].length() - 1);
							string4 = stringArray3[0];
							string4 = string4.replace("shelfitemdistribution", "");
							string4 = string4.trim();
							stringArray4 = stringArray3[1].split(",");
							ShelfDistribution shelfDistribution = new ShelfDistribution();
							shelfDistribution.module = this;
							shelfDistribution.Load(string4, stringArray4);
							this.ShelfDistributions.add(shelfDistribution);
						} else if (string.indexOf("sound") == 0) {
							stringArray = string.split("[{}]");
							string2 = stringArray[0];
							string2 = string2.replace("sound", "");
							string2 = string2.trim();
							GameSoundScript gameSoundScript = (GameSoundScript)this.GameSoundMap.get(string2);
							gameSoundScript.module = this;
							try {
								gameSoundScript.Load(string2, string);
							} catch (Throwable throwable) {
								ExceptionLogger.logException(throwable);
							}
						} else if (string.indexOf("vehicle") == 0) {
							stringArray = string.split("[{}]");
							string2 = stringArray[0];
							string2 = string2.replace("vehicle", "");
							string2 = string2.trim();
							VehicleScript vehicleScript = (VehicleScript)this.VehicleMap.get(string2);
							vehicleScript.module = this;
							try {
								vehicleScript.Load(string2, string);
								vehicleScript.Loaded();
							} catch (Exception exception4) {
								ExceptionLogger.logException(exception4);
							}
						}
					}
				}
			}
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

	public void PlayScript(String string) {
		if (string.contains(".")) {
			ScriptManager.instance.PlayScript(string);
		} else {
			if (this.ScriptMap.containsKey(string)) {
				Script script = (Script)this.ScriptMap.get(string);
				Script.ScriptInstance scriptInstance = new Script.ScriptInstance();
				scriptInstance.theScript = script;
				scriptInstance.ID = this.name;
				ScriptManager.instance.PlayingScripts.add(scriptInstance);
				scriptInstance.begin();
			} else if (this.RandomSelectorMap.containsKey(string)) {
				((RandomSelector)this.RandomSelectorMap.get(string)).Process();
			} else {
				for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
					Script script2 = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getScript(this.name);
					if (script2 != null) {
						ScriptManager.instance.getModule((String)this.Imports.get(int1)).PlayScript(script2.name);
						return;
					}
				}

				JOptionPane.showMessageDialog((Component)null, "Module: " + this.name + " cannot find script: " + string, "Error", 0);
			}
		}
	}

	public Script.ScriptInstance PlayScript(String string, Script.ScriptInstance scriptInstance) {
		if (string.contains(".")) {
			return ScriptManager.instance.PlayScript(string, scriptInstance);
		} else if (this.ScriptMap.containsKey(string)) {
			Script script = (Script)this.ScriptMap.get(string);
			Script.ScriptInstance scriptInstance2 = new Script.ScriptInstance();
			scriptInstance2.theScript = script;
			scriptInstance2.ID = this.name;
			scriptInstance2.CopyAliases(scriptInstance);
			ScriptManager.instance.PlayingScripts.add(scriptInstance2);
			scriptInstance2.begin();
			return scriptInstance2;
		} else {
			if (this.RandomSelectorMap.containsKey(string)) {
				((RandomSelector)this.RandomSelectorMap.get(string)).Process(scriptInstance);
			} else {
				for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
					Script script2 = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getScript(this.name);
					if (script2 != null) {
						return ScriptManager.instance.getModule((String)this.Imports.get(int1)).PlayScript(script2.name, scriptInstance);
					}
				}

				JOptionPane.showMessageDialog((Component)null, "Module: " + this.name + " cannot find script: " + string, "Error", 0);
			}

			return null;
		}
	}

	public Script.ScriptInstance PlayScript(Script.ScriptInstance scriptInstance) {
		ScriptManager.instance.PlayingScripts.add(scriptInstance);
		scriptInstance.begin();
		return scriptInstance;
	}

	public Inventory getInventory(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getInventory(string);
		} else if (!this.InventoryMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				Inventory inventory = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getInventory(string);
				if (inventory != null) {
					return inventory;
				}
			}

			return null;
		} else {
			return (Inventory)this.InventoryMap.get(string);
		}
	}

	public ScriptCharacter getCharacter(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getCharacter(string);
		} else if (!this.CharacterMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptCharacter scriptCharacter = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getCharacter(string);
				if (scriptCharacter != null) {
					return scriptCharacter;
				}
			}

			return null;
		} else {
			return (ScriptCharacter)this.CharacterMap.get(string);
		}
	}

	public IsoGameCharacter getCharacterActual(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getCharacterActual(string);
		} else if (!this.CharacterMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptCharacter scriptCharacter = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getCharacter(string);
				if (scriptCharacter != null) {
					return scriptCharacter.Actual;
				}
			}

			return null;
		} else {
			return ((ScriptCharacter)this.CharacterMap.get(string)).Actual;
		}
	}

	public int getFlagIntValue(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getFlagIntValue(string);
		} else if (!this.FlagMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptFlag scriptFlag = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getFlag(string);
				if (scriptFlag != null) {
					return Integer.parseInt(scriptFlag.value);
				}
			}

			return 0;
		} else {
			return Integer.parseInt(((ScriptFlag)this.FlagMap.get(string)).value);
		}
	}

	public String getFlagValue(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getFlagValue(string);
		} else if (!this.FlagMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptFlag scriptFlag = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getFlag(string);
				if (scriptFlag != null) {
					return scriptFlag.value;
				}
			}

			return null;
		} else {
			return ((ScriptFlag)this.FlagMap.get(string)).value;
		}
	}

	public Waypoint getWaypoint(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getWaypoint(string);
		} else if (!this.WaypointMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				Waypoint waypoint = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getWaypoint(string);
				if (waypoint != null) {
					return waypoint;
				}
			}

			return null;
		} else {
			return (Waypoint)this.WaypointMap.get(string);
		}
	}

	public ScriptContainer getScriptContainer(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getScriptContainer(string);
		} else if (!this.ScriptContainerMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptContainer scriptContainer = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getScriptContainer(string);
				if (scriptContainer != null) {
					return scriptContainer;
				}
			}

			return null;
		} else {
			return (ScriptContainer)this.ScriptContainerMap.get(string);
		}
	}

	public Room getRoom(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getRoom(string);
		} else if (!this.RoomMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				Room room = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getRoom(string);
				if (room != null) {
					return room;
				}
			}

			return null;
		} else {
			return (Room)this.RoomMap.get(string);
		}
	}

	public ScriptActivatable getActivatable(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getActivatable(string);
		} else if (!this.ActivatableMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptActivatable scriptActivatable = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getActivatable(string);
				if (scriptActivatable != null) {
					return scriptActivatable;
				}
			}

			return null;
		} else {
			return (ScriptActivatable)this.ActivatableMap.get(string);
		}
	}

	public ScriptTalker getTalker(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getTalker(string);
		} else if (!this.TalkerMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptTalker scriptTalker = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getTalker(string);
				if (scriptTalker != null) {
					return scriptTalker;
				}
			}

			return null;
		} else {
			return (ScriptTalker)this.TalkerMap.get(string);
		}
	}

	public LanguageDefinition getLanguageDef(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getLanguageDef(string);
		} else if (!this.LanguageMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				LanguageDefinition languageDefinition = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getLanguageDef(string);
				if (languageDefinition != null) {
					return languageDefinition;
				}
			}

			return null;
		} else {
			return (LanguageDefinition)this.LanguageMap.get(string);
		}
	}

	public String getLanguage(String string) {
		if (!string.contains("@")) {
			return string;
		} else {
			string = string.substring(1);
			if (string.contains(".")) {
				return ScriptManager.instance.getLanguage(string);
			} else {
				String[] stringArray = string.split("-");
				LanguageDefinition languageDefinition = this.getLanguageDef(stringArray[0]);
				String string2 = languageDefinition.get(Integer.parseInt(stringArray[1]));
				string2 = string2.substring(1);
				string2 = string2.substring(0, string2.length() - 1);
				return string2;
			}
		}
	}

	public ScriptFlag getFlag(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getFlag(string);
		} else if (!this.FlagMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				ScriptFlag scriptFlag = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getFlag(string);
				if (scriptFlag != null) {
					return scriptFlag;
				}
			}

			return null;
		} else {
			return (ScriptFlag)this.FlagMap.get(string);
		}
	}

	public Zone getZone(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getZone(string);
		} else if (!this.ZoneMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				Zone zone = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getZone(string);
				if (zone != null) {
					return zone;
				}
			}

			return null;
		} else {
			return (Zone)this.ZoneMap.get(string);
		}
	}

	public QuestTaskCondition getQuestCondition(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getQuestCondition(string);
		} else if (!this.ConditionMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				QuestTaskCondition questTaskCondition = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getQuestCondition(string);
				if (questTaskCondition != null) {
					return questTaskCondition;
				}
			}

			return null;
		} else {
			return (QuestTaskCondition)this.ConditionMap.get(string);
		}
	}

	public Item getItem(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getItem(string);
		} else if (!this.ItemMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				Item item = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getItem(string);
				if (item != null) {
					return item;
				}
			}

			return null;
		} else {
			return (Item)this.ItemMap.get(string);
		}
	}

	public Recipe getRecipe(String string) {
		if (string.contains(".") && !this.RecipesWithDotInName.containsKey(string)) {
			return ScriptManager.instance.getRecipe(string);
		} else {
			int int1;
			Recipe recipe;
			for (int1 = 0; int1 < this.RecipeMap.size(); ++int1) {
				recipe = (Recipe)this.RecipeMap.get(int1);
				if (recipe.getOriginalname().equals(string)) {
					return recipe;
				}
			}

			for (int1 = 0; int1 < this.Imports.size(); ++int1) {
				if (ScriptManager.instance.getModule((String)this.Imports.get(int1)) != null) {
					recipe = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getRecipe(string);
					if (recipe != null) {
						return recipe;
					}
				}
			}

			return null;
		}
	}

	public VehicleScript getVehicle(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getVehicle(string);
		} else if (!this.VehicleMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				VehicleScript vehicleScript = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getVehicle(string);
				if (vehicleScript != null) {
					return vehicleScript;
				}
			}

			return null;
		} else {
			return (VehicleScript)this.VehicleMap.get(string);
		}
	}

	public VehicleTemplate getVehicleTemplate(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getVehicleTemplate(string);
		} else if (!this.VehicleTemplateMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				VehicleTemplate vehicleTemplate = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getVehicleTemplate(string);
				if (vehicleTemplate != null) {
					return vehicleTemplate;
				}
			}

			return null;
		} else {
			return (VehicleTemplate)this.VehicleTemplateMap.get(string);
		}
	}

	private boolean ContainsRecipe(String string) {
		for (int int1 = 0; int1 < this.RecipeMap.size(); ++int1) {
			Recipe recipe = (Recipe)this.RecipeMap.get(int1);
			if (recipe.getOriginalname().equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean CheckExitPoints() {
		return false;
	}

	public Script getScript(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getScript(string);
		} else {
			if (this.RandomSelectorMap.containsKey(string)) {
				string = (String)((RandomSelector)this.RandomSelectorMap.get(string)).scriptsToCall.get(Rand.Next(((RandomSelector)this.RandomSelectorMap.get(string)).scriptsToCall.size()));
			}

			if (!this.ScriptMap.containsKey(string)) {
				for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
					Script script = ScriptManager.instance.getModule((String)this.Imports.get(int1)).getScript(string);
					if (script != null) {
						return script;
					}
				}

				return null;
			} else {
				return (Script)this.ScriptMap.get(string);
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public static class Exit {
		public int fromX1;
		public int fromY1;
		public int fromZ1;
		public int toX1;
		public int toY1;
		public int toZ1;
		public int fromX2;
		public int fromY2;
		public int fromZ2;
		public int toX2;
		public int toY2;
		public int toZ2;
		public String map;
	}
}
