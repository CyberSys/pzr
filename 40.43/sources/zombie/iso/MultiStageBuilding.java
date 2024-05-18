package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoThumpable;


public class MultiStageBuilding {
	public static ArrayList stages = new ArrayList();

	public static ArrayList getStages(IsoGameCharacter gameCharacter, IsoObject object, boolean boolean1) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < stages.size(); ++int1) {
			MultiStageBuilding.Stage stage = (MultiStageBuilding.Stage)stages.get(int1);
			if (stage.canBeDone(gameCharacter, object, boolean1) && !arrayList.contains(stage)) {
				arrayList.add(stage);
			}
		}

		return arrayList;
	}

	public static void addStage(MultiStageBuilding.Stage stage) {
		for (int int1 = 0; int1 < stages.size(); ++int1) {
			if (((MultiStageBuilding.Stage)stages.get(int1)).ID.equals(stage.ID)) {
				return;
			}
		}

		stages.add(stage);
	}

	public class Stage {
		public String name;
		public ArrayList previousStage = new ArrayList();
		public String recipeName;
		public String sprite;
		public String northSprite;
		public int timeNeeded;
		public int bonusHealth;
		public HashMap xp = new HashMap();
		public HashMap perks = new HashMap();
		public HashMap items = new HashMap();
		public ArrayList itemsToKeep = new ArrayList();
		public String knownRecipe;
		public String thumpSound;
		public String wallType;
		public boolean canBePlastered;
		public String craftingSound;
		public String ID;
		public boolean canBarricade = false;

		public String getName() {
			return this.name;
		}

		public String getDisplayName() {
			return Translator.getMultiStageBuild(this.recipeName);
		}

		public String getSprite() {
			return this.sprite;
		}

		public String getThumpSound() {
			return this.thumpSound;
		}

		public String getRecipeName() {
			return this.recipeName;
		}

		public String getKnownRecipe() {
			return this.knownRecipe;
		}

		public int getTimeNeeded(IsoGameCharacter gameCharacter) {
			int int1 = this.timeNeeded;
			Entry entry;
			for (Iterator iterator = this.xp.entrySet().iterator(); iterator.hasNext(); int1 -= gameCharacter.getPerkLevel(PerkFactory.Perks.FromString((String)entry.getKey())) * 10) {
				entry = (Entry)iterator.next();
			}

			return int1;
		}

		public ArrayList getItemsToKeep() {
			return this.itemsToKeep;
		}

		public ArrayList getPreviousStages() {
			return this.previousStage;
		}

		public String getCraftingSound() {
			return this.craftingSound;
		}

		public KahluaTable getItemsLua() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			Iterator iterator = this.items.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				kahluaTable.rawset(entry.getKey(), ((Integer)entry.getValue()).toString());
			}

			return kahluaTable;
		}

		public KahluaTable getPerksLua() {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			Iterator iterator = this.perks.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				kahluaTable.rawset(PerkFactory.Perks.FromString((String)entry.getKey()), ((Integer)entry.getValue()).toString());
			}

			return kahluaTable;
		}

		public void doStage(IsoGameCharacter gameCharacter, IsoThumpable thumpable, boolean boolean1) {
			int int1 = thumpable.getHealth();
			int int2 = thumpable.getMaxHealth();
			String string = this.sprite;
			if (thumpable.north) {
				string = this.northSprite;
			}

			IsoThumpable thumpable2 = new IsoThumpable(IsoWorld.instance.getCell(), thumpable.square, string, thumpable.north, thumpable.getTable());
			thumpable2.setCanBePlastered(this.canBePlastered);
			if ("doorframe".equals(this.wallType)) {
				thumpable2.setIsDoorFrame(true);
				thumpable2.setCanPassThrough(true);
			}

			int int3 = this.bonusHealth;
			switch (SandboxOptions.instance.ConstructionBonusPoints.getValue()) {
			case 1: 
				int3 = (int)((double)int3 * 0.5);
				break;
			
			case 2: 
				int3 = (int)((double)int3 * 0.7);
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				int3 = (int)((double)int3 * 1.3);
				break;
			
			case 5: 
				int3 = (int)((double)int3 * 1.5);
			
			}
			Iterator iterator = this.perks.keySet().iterator();
			byte byte1 = 40;
			switch (SandboxOptions.instance.ConstructionBonusPoints.getValue()) {
			case 1: 
				byte1 = 5;
				break;
			
			case 2: 
				byte1 = 20;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				byte1 = 55;
				break;
			
			case 5: 
				byte1 = 70;
			
			}
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				int3 += (gameCharacter.getPerkLevel(PerkFactory.Perks.FromString(string2)) - (Integer)this.perks.get(string2)) * byte1;
			}

			thumpable2.setMaxHealth(int2 + int3);
			thumpable2.setHealth(int1 + int3);
			thumpable2.setName(this.name);
			thumpable2.setThumpSound(this.getThumpSound());
			thumpable2.setCanBarricade(this.canBarricade);
			thumpable2.setModData(thumpable.getModData());
			if (this.wallType != null) {
				thumpable2.getModData().rawset("wallType", this.wallType);
			}

			if (boolean1) {
				ItemContainer itemContainer = gameCharacter.getInventory();
				Iterator iterator2 = this.items.keySet().iterator();
				while (iterator2.hasNext()) {
					String string3 = (String)iterator2.next();
					for (int int4 = 0; int4 < (Integer)this.items.get(string3); ++int4) {
						InventoryItem inventoryItem = itemContainer.getItemFromType(string3);
						if (inventoryItem != null) {
							inventoryItem.Use();
						}
					}
				}
			}

			Iterator iterator3 = this.xp.keySet().iterator();
			while (iterator3.hasNext()) {
				String string4 = (String)iterator3.next();
				gameCharacter.getXp().AddXP(PerkFactory.Perks.FromString(string4), (float)(Integer)this.xp.get(string4));
			}

			int int5 = thumpable.getSquare().transmitRemoveItemFromSquare(thumpable);
			thumpable2.getSquare().AddSpecialObject(thumpable2, int5);
			thumpable2.getSquare().RecalcAllWithNeighbours(true);
			thumpable2.transmitCompleteItemToServer();
		}

		public boolean canBeDone(IsoGameCharacter gameCharacter, IsoObject object, boolean boolean1) {
			ItemContainer itemContainer = gameCharacter.getInventory();
			boolean boolean2 = false;
			for (int int1 = 0; int1 < this.previousStage.size(); ++int1) {
				if (((String)this.previousStage.get(int1)).equalsIgnoreCase(object.getName())) {
					boolean2 = true;
					break;
				}
			}

			return boolean2;
		}

		public void Load(String string, String[] stringArray) {
			this.recipeName = string;
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				if (!stringArray[int1].trim().isEmpty() && stringArray[int1].contains(":")) {
					String[] stringArray2 = stringArray[int1].split(":");
					String string2 = stringArray2[0].trim();
					String string3 = stringArray2[1].trim();
					if (string2.equalsIgnoreCase("Name")) {
						this.name = string3.trim();
					}

					if (string2.equalsIgnoreCase("TimeNeeded")) {
						this.timeNeeded = Integer.parseInt(string3.trim());
					}

					if (string2.equalsIgnoreCase("BonusHealth")) {
						this.bonusHealth = Integer.parseInt(string3.trim());
					}

					if (string2.equalsIgnoreCase("Sprite")) {
						this.sprite = string3.trim();
					}

					if (string2.equalsIgnoreCase("NorthSprite")) {
						this.northSprite = string3.trim();
					}

					if (string2.equalsIgnoreCase("KnownRecipe")) {
						this.knownRecipe = string3.trim();
					}

					if (string2.equalsIgnoreCase("ThumpSound")) {
						this.thumpSound = string3.trim();
					}

					if (string2.equalsIgnoreCase("WallType")) {
						this.wallType = string3.trim();
					}

					if (string2.equalsIgnoreCase("CraftingSound")) {
						this.craftingSound = string3.trim();
					}

					if (string2.equalsIgnoreCase("ID")) {
						this.ID = string3.trim();
					}

					if (string2.equalsIgnoreCase("CanBePlastered")) {
						this.canBePlastered = Boolean.parseBoolean(string3.trim());
					}

					if (string2.equalsIgnoreCase("CanBarricade")) {
						this.canBarricade = Boolean.parseBoolean(string3.trim());
					}

					String[] stringArray3;
					int int2;
					String[] stringArray4;
					if (string2.equalsIgnoreCase("XP")) {
						stringArray3 = string3.split(";");
						for (int2 = 0; int2 < stringArray3.length; ++int2) {
							stringArray4 = stringArray3[int2].split("=");
							this.xp.put(stringArray4[0], Integer.parseInt(stringArray4[1]));
						}
					}

					if (string2.equalsIgnoreCase("PreviousStage")) {
						stringArray3 = string3.split(";");
						for (int2 = 0; int2 < stringArray3.length; ++int2) {
							this.previousStage.add(stringArray3[int2]);
						}
					}

					if (string2.equalsIgnoreCase("SkillRequired")) {
						stringArray3 = string3.split(";");
						for (int2 = 0; int2 < stringArray3.length; ++int2) {
							stringArray4 = stringArray3[int2].split("=");
							this.perks.put(stringArray4[0], Integer.parseInt(stringArray4[1]));
						}
					}

					if (string2.equalsIgnoreCase("ItemsRequired")) {
						stringArray3 = string3.split(";");
						for (int2 = 0; int2 < stringArray3.length; ++int2) {
							stringArray4 = stringArray3[int2].split("=");
							this.items.put(stringArray4[0], Integer.parseInt(stringArray4[1]));
						}
					}

					if (string2.equalsIgnoreCase("ItemsToKeep")) {
						stringArray3 = string3.split(";");
						for (int2 = 0; int2 < stringArray3.length; ++int2) {
							this.itemsToKeep.add(stringArray3[int2]);
						}
					}
				}
			}
		}
	}
}
