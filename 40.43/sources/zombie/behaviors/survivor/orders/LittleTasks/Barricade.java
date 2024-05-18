package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;


public class Barricade extends Order {
	IsoDoor door = null;
	IsoGameCharacter chr;
	int level = 2;

	public Barricade(IsoGameCharacter gameCharacter, IsoDoor door) {
		super(gameCharacter);
		this.door = door;
		this.chr = gameCharacter;
	}

	public boolean complete() {
		IsoBarricade barricade = this.door.getBarricadeForCharacter(this.chr);
		return barricade != null && barricade.getNumPlanks() >= this.level;
	}

	public Behavior.BehaviorResult process() {
		IsoBarricade barricade = this.door.getBarricadeForCharacter(this.chr);
		if (this.chr.getCharacterActions().isEmpty() && barricade != null && barricade.getNumPlanks() < this.level) {
		}

		return barricade != null && barricade.getNumPlanks() >= this.level ? Behavior.BehaviorResult.Succeeded : Behavior.BehaviorResult.Working;
	}

	public void update() {
	}
}
