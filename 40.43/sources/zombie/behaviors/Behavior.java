package zombie.behaviors;

import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;


public abstract class Behavior {
	public Behavior.BehaviorResult last;
	protected final ArrayList childNodes;

	public Behavior() {
		this.last = Behavior.BehaviorResult.Working;
		this.childNodes = new ArrayList(3);
	}

	public float getPathSpeed() {
		return 0.06F;
	}

	public int renderDebug(int int1) {
		return int1;
	}

	public void update() {
	}

	public void onSwitch() {
	}

	public abstract Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter);

	public abstract void reset();

	public abstract boolean valid();

	public void addChild(Behavior behavior) {
		this.childNodes.add(behavior);
	}

	public Behavior.BehaviorResult processChild(DecisionPath decisionPath, IsoGameCharacter gameCharacter, int int1) {
		if (!((Behavior)this.childNodes.get(int1)).valid()) {
			return Behavior.BehaviorResult.Failed;
		} else {
			decisionPath.DecisionPath.push(this);
			Behavior.BehaviorResult behaviorResult = ((Behavior)this.childNodes.get(int1)).process(decisionPath, gameCharacter);
			decisionPath.DecisionPath.pop();
			return behaviorResult;
		}
	}
	public static enum BehaviorResult {

		Failed,
		Working,
		Succeeded;
	}
}
