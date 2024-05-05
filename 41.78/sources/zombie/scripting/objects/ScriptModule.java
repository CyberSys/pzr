package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.debug.DebugLog;
import zombie.iso.MultiStageBuilding;
import zombie.scripting.IScriptObjectStore;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.vehicles.VehicleEngineRPM;


public final class ScriptModule extends BaseScriptObject implements IScriptObjectStore {
	public String name;
	public String value;
	public final HashMap ItemMap = new HashMap();
	public final HashMap GameSoundMap = new HashMap();
	public final ArrayList GameSoundList = new ArrayList();
	public final HashMap AnimationsMeshMap = new HashMap();
	public final HashMap MannequinScriptMap = new HashMap();
	public final TreeMap ModelScriptMap;
	public final HashMap RuntimeAnimationScriptMap;
	public final HashMap SoundTimelineMap;
	public final HashMap VehicleMap;
	public final HashMap VehicleTemplateMap;
	public final HashMap VehicleEngineRPMMap;
	public final ArrayList RecipeMap;
	public final HashMap RecipeByName;
	public final HashMap RecipesWithDotInName;
	public final ArrayList EvolvedRecipeMap;
	public final ArrayList UniqueRecipeMap;
	public final HashMap FixingMap;
	public final ArrayList Imports;
	public boolean disabled;

	public ScriptModule() {
		this.ModelScriptMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.RuntimeAnimationScriptMap = new HashMap();
		this.SoundTimelineMap = new HashMap();
		this.VehicleMap = new HashMap();
		this.VehicleTemplateMap = new HashMap();
		this.VehicleEngineRPMMap = new HashMap();
		this.RecipeMap = new ArrayList();
		this.RecipeByName = new HashMap();
		this.RecipesWithDotInName = new HashMap();
		this.EvolvedRecipeMap = new ArrayList();
		this.UniqueRecipeMap = new ArrayList();
		this.FixingMap = new HashMap();
		this.Imports = new ArrayList();
		this.disabled = false;
	}

	public void Load(String string, String string2) {
		this.name = string;
		this.value = string2.trim();
		ScriptManager.instance.CurrentLoadingModule = this;
		this.ParseScriptPP(this.value);
		this.ParseScript(this.value);
		this.value = "";
	}

	private String GetTokenType(String string) {
		int int1 = string.indexOf(123);
		if (int1 == -1) {
			return null;
		} else {
			String string2 = string.substring(0, int1).trim();
			int int2 = string2.indexOf(32);
			int int3 = string2.indexOf(9);
			if (int2 != -1 && int3 != -1) {
				return string2.substring(0, PZMath.min(int2, int3));
			} else if (int2 != -1) {
				return string2.substring(0, int2);
			} else {
				return int3 != -1 ? string2.substring(0, int3) : string2;
			}
		}
	}

	private void CreateFromTokenPP(String string) {
		string = string.trim();
		String string2 = this.GetTokenType(string);
		if (string2 != null) {
			String[] stringArray;
			String string3;
			if ("item".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("item", "");
				string3 = string3.trim();
				Item item = new Item();
				this.ItemMap.put(string3, item);
			} else if ("animationsMesh".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("animationsMesh", "");
				string3 = string3.trim();
				AnimationsMesh animationsMesh;
				if (this.AnimationsMeshMap.containsKey(string3)) {
					animationsMesh = (AnimationsMesh)this.AnimationsMeshMap.get(string3);
					animationsMesh.reset();
				} else {
					animationsMesh = new AnimationsMesh();
					this.AnimationsMeshMap.put(string3, animationsMesh);
				}
			} else if ("mannequin".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("mannequin", "");
				string3 = string3.trim();
				MannequinScript mannequinScript;
				if (this.MannequinScriptMap.containsKey(string3)) {
					mannequinScript = (MannequinScript)this.MannequinScriptMap.get(string3);
					mannequinScript.reset();
				} else {
					mannequinScript = new MannequinScript();
					this.MannequinScriptMap.put(string3, mannequinScript);
				}
			} else if ("model".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("model", "");
				string3 = string3.trim();
				ModelScript modelScript;
				if (this.ModelScriptMap.containsKey(string3)) {
					modelScript = (ModelScript)this.ModelScriptMap.get(string3);
					modelScript.reset();
				} else {
					modelScript = new ModelScript();
					this.ModelScriptMap.put(string3, modelScript);
				}
			} else if ("sound".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("sound", "");
				string3 = string3.trim();
				GameSoundScript gameSoundScript;
				if (this.GameSoundMap.containsKey(string3)) {
					gameSoundScript = (GameSoundScript)this.GameSoundMap.get(string3);
					gameSoundScript.reset();
				} else {
					gameSoundScript = new GameSoundScript();
					this.GameSoundMap.put(string3, gameSoundScript);
					this.GameSoundList.add(gameSoundScript);
				}
			} else if ("soundTimeline".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("soundTimeline", "");
				string3 = string3.trim();
				SoundTimelineScript soundTimelineScript;
				if (this.SoundTimelineMap.containsKey(string3)) {
					soundTimelineScript = (SoundTimelineScript)this.SoundTimelineMap.get(string3);
					soundTimelineScript.reset();
				} else {
					soundTimelineScript = new SoundTimelineScript();
					this.SoundTimelineMap.put(string3, soundTimelineScript);
				}
			} else if ("vehicle".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("vehicle", "");
				string3 = string3.trim();
				VehicleScript vehicleScript = new VehicleScript();
				this.VehicleMap.put(string3, vehicleScript);
			} else if ("template".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("template", "");
				String[] stringArray2 = string3.trim().split("\\s+");
				if (stringArray2.length == 2) {
					String string4 = stringArray2[0].trim();
					String string5 = stringArray2[1].trim();
					if ("vehicle".equals(string4)) {
						VehicleTemplate vehicleTemplate = new VehicleTemplate(this, string5, string);
						vehicleTemplate.module = this;
						this.VehicleTemplateMap.put(string5, vehicleTemplate);
					}
				}
			} else if ("animation".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("animation", "");
				string3 = string3.trim();
				RuntimeAnimationScript runtimeAnimationScript;
				if (this.RuntimeAnimationScriptMap.containsKey(string3)) {
					runtimeAnimationScript = (RuntimeAnimationScript)this.RuntimeAnimationScriptMap.get(string3);
					runtimeAnimationScript.reset();
				} else {
					runtimeAnimationScript = new RuntimeAnimationScript();
					this.RuntimeAnimationScriptMap.put(string3, runtimeAnimationScript);
				}
			} else if ("vehicleEngineRPM".equals(string2)) {
				stringArray = string.split("[{}]");
				string3 = stringArray[0];
				string3 = string3.replace("vehicleEngineRPM", "");
				string3 = string3.trim();
				VehicleEngineRPM vehicleEngineRPM;
				if (this.VehicleEngineRPMMap.containsKey(string3)) {
					vehicleEngineRPM = (VehicleEngineRPM)this.VehicleEngineRPMMap.get(string3);
					vehicleEngineRPM.reset();
				} else {
					vehicleEngineRPM = new VehicleEngineRPM();
					this.VehicleEngineRPMMap.put(string3, vehicleEngineRPM);
				}
			}
		}
	}

	private void CreateFromToken(String string) {
		string = string.trim();
		String string2 = this.GetTokenType(string);
		if (string2 != null) {
			String[] stringArray;
			if ("imports".equals(string2)) {
				stringArray = string.split("[{}]");
				String[] stringArray2 = stringArray[1].split(",");
				for (int int1 = 0; int1 < stringArray2.length; ++int1) {
					if (stringArray2[int1].trim().length() > 0) {
						String string3 = stringArray2[int1].trim();
						if (string3.equals(this.getName())) {
							DebugLog.log("ERROR: module \"" + this.getName() + "\" imports itself");
						} else {
							this.Imports.add(string3);
						}
					}
				}
			} else {
				String string4;
				String[] stringArray3;
				if ("item".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("item", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					Item item = (Item)this.ItemMap.get(string4);
					item.module = this;
					try {
						item.Load(string4, stringArray3);
					} catch (Exception exception) {
						DebugLog.log((Object)exception);
					}
				} else if ("recipe".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("recipe", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					Recipe recipe = new Recipe();
					this.RecipeMap.add(recipe);
					if (!this.RecipeByName.containsKey(string4)) {
						this.RecipeByName.put(string4, recipe);
					}

					if (string4.contains(".")) {
						this.RecipesWithDotInName.put(string4, recipe);
					}

					recipe.module = this;
					recipe.Load(string4, stringArray3);
				} else if ("uniquerecipe".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("uniquerecipe", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					UniqueRecipe uniqueRecipe = new UniqueRecipe(string4);
					this.UniqueRecipeMap.add(uniqueRecipe);
					uniqueRecipe.module = this;
					uniqueRecipe.Load(string4, stringArray3);
				} else if ("evolvedrecipe".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("evolvedrecipe", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					boolean boolean1 = false;
					Iterator iterator = this.EvolvedRecipeMap.iterator();
					while (iterator.hasNext()) {
						EvolvedRecipe evolvedRecipe = (EvolvedRecipe)iterator.next();
						if (evolvedRecipe.name.equals(string4)) {
							evolvedRecipe.Load(string4, stringArray3);
							evolvedRecipe.module = this;
							boolean1 = true;
						}
					}

					if (!boolean1) {
						EvolvedRecipe evolvedRecipe2 = new EvolvedRecipe(string4);
						this.EvolvedRecipeMap.add(evolvedRecipe2);
						evolvedRecipe2.module = this;
						evolvedRecipe2.Load(string4, stringArray3);
					}
				} else if ("fixing".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("fixing", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					Fixing fixing = new Fixing();
					fixing.module = this;
					this.FixingMap.put(string4, fixing);
					fixing.Load(string4, stringArray3);
				} else if ("animationsMesh".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("animationsMesh", "");
					string4 = string4.trim();
					AnimationsMesh animationsMesh = (AnimationsMesh)this.AnimationsMeshMap.get(string4);
					animationsMesh.module = this;
					try {
						animationsMesh.Load(string4, string);
					} catch (Throwable throwable) {
						ExceptionLogger.logException(throwable);
					}
				} else if ("mannequin".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("mannequin", "");
					string4 = string4.trim();
					MannequinScript mannequinScript = (MannequinScript)this.MannequinScriptMap.get(string4);
					mannequinScript.module = this;
					try {
						mannequinScript.Load(string4, string);
					} catch (Throwable throwable2) {
						ExceptionLogger.logException(throwable2);
					}
				} else if ("multistagebuild".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("multistagebuild", "");
					string4 = string4.trim();
					stringArray3 = stringArray[1].split(",");
					MultiStageBuilding.Stage stage = new MultiStageBuilding().new Stage();
					stage.Load(string4, stringArray3);
					MultiStageBuilding.addStage(stage);
				} else if ("model".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("model", "");
					string4 = string4.trim();
					ModelScript modelScript = (ModelScript)this.ModelScriptMap.get(string4);
					modelScript.module = this;
					try {
						modelScript.Load(string4, string);
					} catch (Throwable throwable3) {
						ExceptionLogger.logException(throwable3);
					}
				} else if ("sound".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("sound", "");
					string4 = string4.trim();
					GameSoundScript gameSoundScript = (GameSoundScript)this.GameSoundMap.get(string4);
					gameSoundScript.module = this;
					try {
						gameSoundScript.Load(string4, string);
					} catch (Throwable throwable4) {
						ExceptionLogger.logException(throwable4);
					}
				} else if ("soundTimeline".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("soundTimeline", "");
					string4 = string4.trim();
					SoundTimelineScript soundTimelineScript = (SoundTimelineScript)this.SoundTimelineMap.get(string4);
					soundTimelineScript.module = this;
					try {
						soundTimelineScript.Load(string4, string);
					} catch (Throwable throwable5) {
						ExceptionLogger.logException(throwable5);
					}
				} else if ("vehicle".equals(string2)) {
					stringArray = string.split("[{}]");
					string4 = stringArray[0];
					string4 = string4.replace("vehicle", "");
					string4 = string4.trim();
					VehicleScript vehicleScript = (VehicleScript)this.VehicleMap.get(string4);
					vehicleScript.module = this;
					try {
						vehicleScript.Load(string4, string);
						vehicleScript.Loaded();
					} catch (Exception exception2) {
						ExceptionLogger.logException(exception2);
					}
				} else if (!"template".equals(string2)) {
					if ("animation".equals(string2)) {
						stringArray = string.split("[{}]");
						string4 = stringArray[0];
						string4 = string4.replace("animation", "");
						string4 = string4.trim();
						RuntimeAnimationScript runtimeAnimationScript = (RuntimeAnimationScript)this.RuntimeAnimationScriptMap.get(string4);
						runtimeAnimationScript.module = this;
						try {
							runtimeAnimationScript.Load(string4, string);
						} catch (Throwable throwable6) {
							ExceptionLogger.logException(throwable6);
						}
					} else if ("vehicleEngineRPM".equals(string2)) {
						stringArray = string.split("[{}]");
						string4 = stringArray[0];
						string4 = string4.replace("vehicleEngineRPM", "");
						string4 = string4.trim();
						VehicleEngineRPM vehicleEngineRPM = (VehicleEngineRPM)this.VehicleEngineRPMMap.get(string4);
						vehicleEngineRPM.module = this;
						try {
							vehicleEngineRPM.Load(string4, string);
						} catch (Throwable throwable7) {
							this.VehicleEngineRPMMap.remove(string4);
							ExceptionLogger.logException(throwable7);
						}
					} else {
						DebugLog.Script.warn("unknown script object \"%s\"", string2);
					}
				}
			}
		}
	}

	public void ParseScript(String string) {
		ArrayList arrayList = ScriptParser.parseTokens(string);
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = (String)arrayList.get(int1);
			this.CreateFromToken(string2);
		}
	}

	public void ParseScriptPP(String string) {
		ArrayList arrayList = ScriptParser.parseTokens(string);
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			String string2 = (String)arrayList.get(int1);
			this.CreateFromTokenPP(string2);
		}
	}

	public Item getItem(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getItem(string);
		} else if (!this.ItemMap.containsKey(string)) {
			for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
				String string2 = (String)this.Imports.get(int1);
				ScriptModule scriptModule = ScriptManager.instance.getModule(string2);
				Item item = scriptModule.getItem(string);
				if (item != null) {
					return item;
				}
			}

			return null;
		} else {
			return (Item)this.ItemMap.get(string);
		}
	}

	public ModelScript getModelScript(String string) {
		if (string.contains(".")) {
			return ScriptManager.instance.getModelScript(string);
		} else {
			ModelScript modelScript = (ModelScript)this.ModelScriptMap.get(string);
			if (modelScript == null) {
				for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
					String string2 = (String)this.Imports.get(int1);
					ScriptModule scriptModule = ScriptManager.instance.getModule(string2);
					modelScript = scriptModule.getModelScript(string);
					if (modelScript != null) {
						return modelScript;
					}
				}

				return null;
			} else {
				return modelScript;
			}
		}
	}

	public Recipe getRecipe(String string) {
		if (string.contains(".") && !this.RecipesWithDotInName.containsKey(string)) {
			return ScriptManager.instance.getRecipe(string);
		} else {
			Recipe recipe = (Recipe)this.RecipeByName.get(string);
			if (recipe != null) {
				return recipe;
			} else {
				for (int int1 = 0; int1 < this.Imports.size(); ++int1) {
					ScriptModule scriptModule = ScriptManager.instance.getModule((String)this.Imports.get(int1));
					if (scriptModule != null) {
						recipe = scriptModule.getRecipe(string);
						if (recipe != null) {
							return recipe;
						}
					}
				}

				return null;
			}
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

	public VehicleEngineRPM getVehicleEngineRPM(String string) {
		return string.contains(".") ? ScriptManager.instance.getVehicleEngineRPM(string) : (VehicleEngineRPM)this.VehicleEngineRPMMap.get(string);
	}

	public boolean CheckExitPoints() {
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void Reset() {
		this.ItemMap.clear();
		this.GameSoundMap.clear();
		this.GameSoundList.clear();
		this.AnimationsMeshMap.clear();
		this.MannequinScriptMap.clear();
		this.ModelScriptMap.clear();
		this.RuntimeAnimationScriptMap.clear();
		this.SoundTimelineMap.clear();
		this.VehicleMap.clear();
		this.VehicleTemplateMap.clear();
		this.VehicleEngineRPMMap.clear();
		this.RecipeMap.clear();
		this.RecipeByName.clear();
		this.RecipesWithDotInName.clear();
		this.EvolvedRecipeMap.clear();
		this.UniqueRecipeMap.clear();
		this.FixingMap.clear();
		this.Imports.clear();
	}

	public Item getSpecificItem(String string) {
		return (Item)this.ItemMap.get(string);
	}
}
