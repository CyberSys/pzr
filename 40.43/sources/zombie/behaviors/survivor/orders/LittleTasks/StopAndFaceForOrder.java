package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.iso.Vector2;


public class StopAndFaceForOrder extends Order {
	IsoGameCharacter other;
	int ticks;
	int delayticks = 0;
	Vector2 vec = new Vector2();

	public StopAndFaceForOrder(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, int int1) {
		super(gameCharacter);
		this.other = gameCharacter2;
		this.ticks = int1;
		this.delayticks = Rand.Next(35) + 10;
	}

	public Behavior.BehaviorResult process() {
		if (this.delayticks > 0) {
			return this.processNext();
		} else {
			this.vec.x = this.other.getX();
			this.vec.y = this.other.getY();
			Vector2 vector2 = this.vec;
			vector2.x -= this.character.getX();
			vector2 = this.vec;
			vector2.y -= this.character.getY();
			this.vec.normalize();
			this.character.DirectionFromVector(this.vec);
			return Behavior.BehaviorResult.Succeeded;
		}
	}

	public boolean complete() {
		return this.ticks <= 0;
	}

	public void update() {
		if (this.delayticks <= 0) {
			--this.ticks;
		} else {
			--this.delayticks;
			this.updatenext();
		}
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		return this.delayticks <= 0 ? 100000.0F : -100000.0F;
	}
}
