package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.CraftItemOrder;
import zombie.behaviors.survivor.orders.LittleTasks.TakeItemFromContainer;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;


public class ObtainItem extends OrderSequence {
	Stack items = new Stack();
	public float priority = 1.0F;

	public ObtainItem(IsoGameCharacter gameCharacter, Stack stack, int int1) {
		super(gameCharacter);
		this.items.addAll(stack);
		this.priority = (float)int1;
	}

	public ObtainItem(IsoGameCharacter gameCharacter, String string, int int1) {
		super(gameCharacter);
		this.items.add(string);
		this.priority = (float)int1;
	}

	public void initOrder() {
		int int1;
		for (int1 = 0; int1 < this.items.size(); ++int1) {
			String string = (String)this.items.get(int1);
			if (this.character.getInventory().contains(string)) {
				return;
			}
		}

		if (this.character.getCurrentSquare().getRoom() == null || this.character.getCurrentSquare().getRoom().building == null || this.character.getCurrentSquare().getRoom().building == this.character.getDescriptor().getGroup().Safehouse || this.character.getCurrentSquare().getRoom().building == null || !this.CheckBuildingForItems(this.character.getCurrentSquare().getRoom().building)) {
			if (this.character.getDescriptor().getGroup().Safehouse == null || !this.CheckBuildingForItems(this.character.getDescriptor().getGroup().Safehouse)) {
				if (!IsoWorld.instance.CurrentCell.getBuildingList().isEmpty()) {
					IsoBuilding building = (IsoBuilding)IsoWorld.instance.CurrentCell.getBuildingList().get(Rand.Next(IsoWorld.instance.CurrentCell.getBuildingList().size()));
					this.CheckBuildingForItems(building);
				}

				if (this.Orders.isEmpty()) {
					boolean boolean1 = false;
					int int2 = Rand.Next(this.items.size());
					Stack stack = ScriptManager.instance.getAllRecipesFor((String)this.items.get(int2));
					if (stack.size() > 0) {
						Recipe recipe = (Recipe)stack.get(Rand.Next(stack.size()));
						for (int int3 = 0; int3 < recipe.Source.size(); ++int3) {
						}

						this.Orders.add(new CraftItemOrder(this.character, recipe));
					}
				}

				if (this.Orders.isEmpty()) {
					for (int1 = 0; int1 < this.items.size(); ++int1) {
						this.character.getDescriptor().getGroup().AddNeed((String)this.items.get(int1), (int)this.priority);
					}
				}
			}
		}
	}

	public boolean ActedThisFrame() {
		if (this.Orders.isEmpty()) {
			return false;
		} else {
			return this.Orders.get(this.ID) instanceof GotoNextTo || this.Orders.get(this.ID) instanceof GotoBuildingOrder;
		}
	}

	private boolean CheckBuildingForItems(IsoBuilding building) {
		int int1 = this.Orders.size();
		for (int int2 = 0; int2 < building.container.size(); ++int2) {
			for (int int3 = 0; int3 < this.items.size(); ++int3) {
				String string = (String)this.items.get(int3);
				if (((ItemContainer)building.container.get(int2)).contains(string)) {
					ItemContainer itemContainer = (ItemContainer)building.container.get(int2);
					this.Orders.add(new GotoNextTo(this.character, itemContainer.parent.square.getX(), itemContainer.parent.square.getY(), itemContainer.parent.square.getZ()));
					this.Orders.add(new TakeItemFromContainer(this.character, itemContainer, string));
					if (this.character.getCurrentSquare().getRoom() == null || this.character.getCurrentSquare().getRoom().building != building) {
						this.Orders.insertElementAt(new GotoBuildingOrder(this.character, building), int1);
					}

					return true;
				}
			}
		}

		return false;
	}
}
