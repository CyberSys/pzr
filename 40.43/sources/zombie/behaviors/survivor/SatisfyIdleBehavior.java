package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class SatisfyIdleBehavior extends Behavior {
	public boolean Started = false;
	boolean OtherRoom = false;
	PathFindBehavior pathFind = new PathFindBehavior("Idle");
	IsoGridSquare sq = null;
	int timeout = 0;

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		Behavior.BehaviorResult behaviorResult = Behavior.BehaviorResult.Working;
		return behaviorResult;
	}

	public void reset() {
		this.Started = false;
		this.sq = null;
		this.pathFind.reset();
	}

	public boolean valid() {
		return true;
	}

	private boolean InDistanceOfPlayer(IsoGameCharacter gameCharacter, int int1, int int2) {
		if (gameCharacter.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare() == null) {
			return true;
		} else if (gameCharacter.getDescriptor().getGroup().Leader == gameCharacter.getDescriptor()) {
			return true;
		} else if (gameCharacter.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() != null && gameCharacter.getCurrentSquare().getRoom() == null) {
			return false;
		} else if (gameCharacter.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() == null && gameCharacter.getCurrentSquare().getRoom() != null) {
			return false;
		} else if (gameCharacter.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom() != null && gameCharacter.getCurrentSquare().getRoom() != null && gameCharacter.getCurrentSquare().getRoom().building == gameCharacter.getDescriptor().getGroup().Leader.getInstance().getCurrentSquare().getRoom().building && gameCharacter.getThreatLevel() == 0) {
			return true;
		} else {
			return IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)((int)gameCharacter.getDescriptor().getGroup().Leader.getInstance().getX()), (float)((int)gameCharacter.getDescriptor().getGroup().Leader.getInstance().getY())) < gameCharacter.getPersonality().getPlayerDistanceComfort();
		}
	}

	public float getPriority(IsoGameCharacter gameCharacter) {
		float float1 = 1.0F;
		if (gameCharacter.getThreatLevel() > 0) {
			float1 -= 1000000.0F;
		}

		if (gameCharacter.getTimeSinceZombieAttack() < 30) {
			float1 = -1000000.0F;
		}

		return float1;
	}

	public int renderDebug(int int1) {
		byte byte1 = 50;
		TextManager.instance.DrawString(UIFont.Small, (double)byte1, (double)int1, "SatisfyIdleBehaviour", 1.0, 1.0, 1.0, 1.0);
		int1 += 30;
		return int1;
	}
}
