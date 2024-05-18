package zombie.behaviors.survivor;

import java.util.Iterator;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;


public class SatisfyFatigueBehavior extends Behavior {
	PathFindBehavior pathFind = new PathFindBehavior("Fatigue");

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		IsoGridSquare square = null;
		Behavior.BehaviorResult behaviorResult = Behavior.BehaviorResult.Failed;
		if (gameCharacter.getCurrentSquare().getRoom() != null) {
			if (gameCharacter.getCurrentSquare().getRoom().Beds.size() > 0) {
				square = (IsoGridSquare)gameCharacter.getCurrentSquare().getRoom().Beds.get(0);
				if (square.getMovingObjects().size() > 0 && square.getMovingObjects().get(0) != gameCharacter) {
					square = null;
				}
			} else {
				Iterator iterator = gameCharacter.getCurrentSquare().getRoom().building.Rooms.iterator();
				while (iterator.hasNext()) {
					IsoRoom room = (IsoRoom)iterator.next();
					if (room.Beds.size() > 0) {
						square = (IsoGridSquare)room.Beds.get(0);
						if (square.getMovingObjects().size() > 0 && square.getMovingObjects().get(0) != gameCharacter) {
							square = null;
						}
					}

					if (square != null) {
						break;
					}
				}
			}

			if (square != null) {
				if (!this.pathFind.running(gameCharacter)) {
					this.pathFind.sx = (int)gameCharacter.getX();
					this.pathFind.sy = (int)gameCharacter.getY();
					this.pathFind.sz = (int)gameCharacter.getZ();
				}

				this.pathFind.tx = square.getX();
				this.pathFind.ty = square.getY();
				this.pathFind.tz = square.getZ();
				behaviorResult = this.pathFind.process(decisionPath, gameCharacter);
				if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
					gameCharacter.setAsleep(true);
				}
			}
		}

		return behaviorResult;
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}
}
