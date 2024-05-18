package zombie.behaviors.survivor;

import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;


public class ObtainItemBehavior extends Behavior {
	public ItemType FindItem;
	ItemContainer container;
	boolean DoneFindItem;
	FindKnownItemBehavior findItem;
	boolean Found;
	boolean HaveLocation;
	boolean LocationIsInventory;
	PathFindBehavior pathFind;

	public ObtainItemBehavior() {
		this.FindItem = ItemType.None;
		this.DoneFindItem = false;
		this.findItem = new FindKnownItemBehavior();
		this.Found = false;
		this.HaveLocation = false;
		this.LocationIsInventory = false;
		this.pathFind = new PathFindBehavior("ObtainItem");
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		Behavior.BehaviorResult behaviorResult;
		if (!this.HaveLocation) {
			if (!this.DoneFindItem) {
				this.DoneFindItem = true;
				this.findItem.reset();
				this.findItem.FindItem = this.FindItem;
				behaviorResult = this.findItem.process(decisionPath, gameCharacter);
				if (behaviorResult == Behavior.BehaviorResult.Failed) {
					return Behavior.BehaviorResult.Working;
				}

				if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
					if (this.findItem.LocationIsInventory) {
						this.LocationIsInventory = true;
						this.Found = true;
						return Behavior.BehaviorResult.Succeeded;
					}

					this.LocationIsInventory = false;
					this.Found = true;
					this.container = this.findItem.container;
					this.pathFind.reset();
					this.pathFind.sx = (int)gameCharacter.getX();
					this.pathFind.sy = (int)gameCharacter.getY();
					this.pathFind.sz = (int)gameCharacter.getZ();
					this.pathFind.tx = this.container.SourceGrid.getX();
					this.pathFind.ty = this.container.SourceGrid.getY();
					this.pathFind.tz = this.container.SourceGrid.getZ();
					this.HaveLocation = true;
				}
			}
		} else {
			behaviorResult = this.pathFind.process(decisionPath, gameCharacter);
			if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
				InventoryItem inventoryItem = this.container.Remove(this.FindItem);
				this.pathFind.reset();
				gameCharacter.setPath((Path)null);
				if (inventoryItem != null) {
					gameCharacter.getInventory().AddItem(inventoryItem);
					return behaviorResult;
				}
			}

			if (behaviorResult == Behavior.BehaviorResult.Failed) {
				return behaviorResult;
			}
		}

		return Behavior.BehaviorResult.Working;
	}

	public void reset() {
		this.HaveLocation = false;
		this.findItem.reset();
	}

	public boolean valid() {
		return true;
	}
}
