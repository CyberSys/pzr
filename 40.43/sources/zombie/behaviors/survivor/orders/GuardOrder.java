package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoGridSquare;
import zombie.iso.Vector2;


public class GuardOrder extends Order {
	public int Range = 6;
	public boolean StayInRoom = true;
	PathFindBehavior PathFind = new PathFindBehavior("Guard");
	IsoGridSquare GuardStand;
	IsoGridSquare GuardFace = null;
	Vector2 vec = new Vector2();

	public GuardOrder(IsoSurvivor survivor, IsoGridSquare square, IsoGridSquare square2) {
		super(survivor);
		this.GuardFace = square2;
		this.GuardStand = square;
		this.PathFind.sx = this.character.getCurrentSquare().getX();
		this.PathFind.sy = this.character.getCurrentSquare().getY();
		this.PathFind.sz = this.character.getCurrentSquare().getZ();
		this.PathFind.tx = square.getX();
		this.PathFind.ty = square.getY();
		this.PathFind.tz = square.getZ();
	}

	GuardOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
	}

	public Behavior.BehaviorResult process() {
		if (this.GuardFace == null) {
			return Behavior.BehaviorResult.Working;
		} else {
			if (this.character.getCurrentSquare() != this.GuardStand) {
				this.PathFind.tx = this.GuardStand.getX();
				this.PathFind.ty = this.GuardStand.getY();
				this.PathFind.tz = this.GuardStand.getZ();
				this.PathFind.process((DecisionPath)null, this.character);
			} else {
				this.vec.x = (float)this.GuardFace.getX() + 0.5F;
				this.vec.y = (float)this.GuardFace.getY() + 0.5F;
				Vector2 vector2 = this.vec;
				vector2.x -= this.character.getX();
				vector2 = this.vec;
				vector2.y -= this.character.getY();
				this.vec.normalize();
				this.character.DirectionFromVector(this.vec);
			}

			return Behavior.BehaviorResult.Working;
		}
	}

	public boolean complete() {
		return false;
	}

	public void update() {
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		return 200.0F;
	}
}
