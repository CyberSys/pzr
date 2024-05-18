package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoCurtain;


public class CloseCurtains extends Order {
	IsoCurtain door = null;
	IsoGameCharacter chr;

	public CloseCurtains(IsoGameCharacter gameCharacter, IsoCurtain curtain) {
		super(gameCharacter);
		this.door = curtain;
		this.chr = gameCharacter;
	}

	public boolean complete() {
		if (this.door == null) {
			return true;
		} else {
			return !this.door.open;
		}
	}

	public Behavior.BehaviorResult process() {
		if (this.door != null && this.door.open) {
			this.door.ToggleDoor(this.chr);
		}

		return Behavior.BehaviorResult.Succeeded;
	}

	public void update() {
	}
}
