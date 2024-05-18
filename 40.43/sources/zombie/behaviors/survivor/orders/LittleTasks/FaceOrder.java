package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;


public class FaceOrder extends Order {
	IsoGameCharacter chr;
	IsoGameCharacter chr2;
	IsoDirections dir;
	Vector2 vec = new Vector2();
	boolean instantComplete = false;

	public FaceOrder(IsoGameCharacter gameCharacter, IsoDirections directions) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.dir = directions;
		this.instantComplete = true;
	}

	public FaceOrder(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.chr2 = gameCharacter2;
	}

	public Behavior.BehaviorResult process() {
		if (this.chr2 == null) {
			this.chr.dir = this.dir;
		} else {
			this.vec.x = this.chr2.getX();
			this.vec.y = this.chr2.getY();
			Vector2 vector2 = this.vec;
			vector2.x -= this.character.getX();
			vector2 = this.vec;
			vector2.y -= this.character.getY();
			this.vec.normalize();
			this.character.DirectionFromVector(this.vec);
		}

		return Behavior.BehaviorResult.Succeeded;
	}

	public boolean complete() {
		return this.instantComplete;
	}

	public void update() {
	}
}
