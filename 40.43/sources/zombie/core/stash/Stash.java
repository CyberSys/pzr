package zombie.core.stash;

import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;


public class Stash {
	public String name;
	public String type;
	public String item;
	public String customName;
	public int buildingX;
	public int buildingY;
	public String spawnTable;
	public KahluaTableImpl annotations;
	public boolean spawnOnlyOnZed;
	public int minDayToSpawn = -1;
	public int maxDayToSpawn = -1;
	public int minTrapToSpawn = -1;
	public int maxTrapToSpawn = -1;
	public int zombies;
	public ArrayList containers;
	public int barricades;

	public Stash(String string) {
		this.name = string;
	}

	public void load(KahluaTableImpl kahluaTableImpl) {
		this.type = kahluaTableImpl.rawgetStr("type");
		this.item = kahluaTableImpl.rawgetStr("item");
		StashBuilding stashBuilding = new StashBuilding(this.name, kahluaTableImpl.rawgetInt("buildingX"), kahluaTableImpl.rawgetInt("buildingY"));
		StashSystem.possibleStashes.add(stashBuilding);
		this.buildingX = stashBuilding.buildingX;
		this.buildingY = stashBuilding.buildingY;
		this.spawnTable = kahluaTableImpl.rawgetStr("spawnTable");
		this.customName = Translator.getText(kahluaTableImpl.rawgetStr("customName"));
		this.zombies = kahluaTableImpl.rawgetInt("zombies");
		this.barricades = kahluaTableImpl.rawgetInt("barricades");
		this.spawnOnlyOnZed = kahluaTableImpl.rawgetBool("spawnOnlyOnZed");
		String string = kahluaTableImpl.rawgetStr("daysToSpawn");
		if (string != null) {
			String[] stringArray = string.split("-");
			if (stringArray.length == 2) {
				this.minDayToSpawn = Integer.parseInt(stringArray[0]);
				this.maxDayToSpawn = Integer.parseInt(stringArray[1]);
			} else {
				this.minDayToSpawn = Integer.parseInt(stringArray[0]);
			}
		}

		String string2 = kahluaTableImpl.rawgetStr("traps");
		if (string2 != null) {
			String[] stringArray2 = string2.split("-");
			if (stringArray2.length == 2) {
				this.minTrapToSpawn = Integer.parseInt(stringArray2[0]);
				this.maxTrapToSpawn = Integer.parseInt(stringArray2[1]);
			} else {
				this.minTrapToSpawn = Integer.parseInt(stringArray2[0]);
				this.maxTrapToSpawn = this.minTrapToSpawn;
			}
		}

		KahluaTable kahluaTable = (KahluaTable)kahluaTableImpl.rawget("containers");
		if (kahluaTable != null) {
			this.containers = new ArrayList();
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			while (kahluaTableIterator.advance()) {
				KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableIterator.getValue();
				StashContainer stashContainer = new StashContainer(kahluaTableImpl2.rawgetStr("room"), kahluaTableImpl2.rawgetStr("containerSprite"), kahluaTableImpl2.rawgetStr("containerType"));
				stashContainer.contX = kahluaTableImpl2.rawgetInt("contX");
				stashContainer.contY = kahluaTableImpl2.rawgetInt("contY");
				stashContainer.contZ = kahluaTableImpl2.rawgetInt("contZ");
				stashContainer.containerItem = kahluaTableImpl2.rawgetStr("containerItem");
				this.containers.add(stashContainer);
			}
		}

		if ("Map".equals(this.type)) {
			this.annotations = (KahluaTableImpl)kahluaTableImpl.rawget("annotations");
		}
	}

	public String getName() {
		return this.name;
	}

	public String getItem() {
		return this.item;
	}

	public int getBuildingX() {
		return this.buildingX;
	}

	public int getBuildingY() {
		return this.buildingY;
	}
}
