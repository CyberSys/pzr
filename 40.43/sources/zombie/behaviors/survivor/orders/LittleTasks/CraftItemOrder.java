package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.Drainable;
import zombie.scripting.objects.Recipe;


public class CraftItemOrder extends Order {
	IsoGameCharacter chr;
	Recipe rec;

	public CraftItemOrder(IsoGameCharacter gameCharacter, Recipe recipe) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.rec = recipe;
	}

	public Behavior.BehaviorResult process() {
		return Behavior.BehaviorResult.Succeeded;
	}

	public void initOrder() {
		this.PerformMakeItem();
	}

	public boolean complete() {
		return true;
	}

	public void update() {
	}

	void DoDrainOnItem(InventoryItem inventoryItem, String string) {
		if (RecipeManager.DoesWipeUseDelta(inventoryItem.getType(), string)) {
			((Drainable)inventoryItem).setUsedDelta(0.0F);
		}

		if (RecipeManager.DoesUseItemUp(inventoryItem.getType(), this.rec)) {
			float float1 = RecipeManager.UseAmount(inventoryItem.getType(), this.rec, this.chr);
			for (int int1 = 0; (float)int1 < float1; ++int1) {
				inventoryItem.Use(true);
			}
		}
	}

	void PerformMakeItem() {
		Object object = null;
		this.character.getInventory().AddItem(this.rec.module.name + "." + this.rec.Result.type);
	}
}
