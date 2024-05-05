package zombie.characters;

import zombie.inventory.InventoryItem;


public interface ILuaGameCharacterHealth {

	void setSleepingTabletEffect(float float1);

	float getSleepingTabletEffect();

	float getFatigueMod();

	boolean Eat(InventoryItem inventoryItem, float float1);

	boolean Eat(InventoryItem inventoryItem);

	float getTemperature();

	void setTemperature(float float1);

	float getReduceInfectionPower();

	void setReduceInfectionPower(float float1);

	int getLastHourSleeped();

	void setLastHourSleeped(int int1);

	void setTimeOfSleep(float float1);
}
