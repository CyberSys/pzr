package zombie.behaviors.general;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;


public class FollowBehaviour extends Behavior {
	public int thinkTime = 30;
	public int thinkTimeMax = 30;
	public boolean stayInside = false;
	PathFindBehavior pathFind = new PathFindBehavior("FollowBehaviour");
	IsoGameCharacter Target = null;
	InventoryItem weapon = null;
	int timeout = 180;

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		gameCharacter.setFollowingTarget(this.Target);
		boolean boolean1 = false;
		--this.timeout;
		if (this.timeout <= 0) {
		}

		if (this.Target != null && this.Target.getHealth() <= 0.0F) {
			this.Target = null;
			this.weapon = null;
			this.timeout = 180;
			return Behavior.BehaviorResult.Succeeded;
		} else {
			--this.thinkTime;
			if (this.Target == null && this.thinkTime <= 0) {
				this.Target = gameCharacter.getCurrentSquare().FindFriend(gameCharacter, gameCharacter.getPersonality().getHuntZombieRange(), gameCharacter.getEnemyList());
				if (this.Target != null && this.Target.getCurrentSquare() != null) {
					boolean1 = true;
				}

				if (Rand.Next(2) != 0) {
					gameCharacter.setPathSpeed(0.08F);
				} else {
					gameCharacter.setPathSpeed(0.05F);
				}

				this.thinkTime = this.thinkTimeMax;
				this.pathFind.sx = gameCharacter.getCurrentSquare().getX();
				this.pathFind.sy = gameCharacter.getCurrentSquare().getY();
				this.pathFind.sz = gameCharacter.getCurrentSquare().getZ();
			}

			if (this.Target == null) {
				this.weapon = null;
				this.timeout = 180;
				return Behavior.BehaviorResult.Succeeded;
			} else {
				IsoGridSquare square = gameCharacter.getCurrentSquare();
				IsoGridSquare square2 = this.Target.getCurrentSquare();
				if (square != null && square2 != null) {
					float float1 = IsoUtils.DistanceManhatten((float)square.getX(), (float)square.getY(), (float)square2.getX(), (float)square2.getY());
					if (square2.getZ() == square.getZ() && !(5.0F < float1)) {
						this.timeout = 180;
						return Behavior.BehaviorResult.Succeeded;
					}

					if (boolean1) {
						this.pathFind.tx = square2.getX();
						this.pathFind.ty = square2.getY();
						this.pathFind.tz = square2.getZ();
						boolean1 = false;
					}

					Behavior.BehaviorResult behaviorResult = this.pathFind.process(decisionPath, gameCharacter);
					if (behaviorResult == Behavior.BehaviorResult.Failed) {
						this.Target = null;
						this.weapon = null;
						this.thinkTime = this.thinkTimeMax;
						return behaviorResult;
					}

					if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
						this.Target = null;
						this.weapon = null;
						this.thinkTime = 0;
						return Behavior.BehaviorResult.Succeeded;
					}
				}

				return Behavior.BehaviorResult.Working;
			}
		}
	}

	public void reset() {
		this.Target = null;
		this.weapon = null;
		this.timeout = 180;
		this.pathFind.reset();
	}

	public boolean valid() {
		return true;
	}
}
