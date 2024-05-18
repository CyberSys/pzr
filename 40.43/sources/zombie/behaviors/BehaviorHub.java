package zombie.behaviors;

import java.util.HashMap;
import java.util.Iterator;
import zombie.characters.IsoGameCharacter;


public class BehaviorHub extends Behavior {
	public HashMap TriggerMap = new HashMap();

	public BehaviorHub.BehaviorTrigger AddTrigger(String string, float float1, float float2, float float3, Behavior behavior) {
		BehaviorHub.BehaviorTrigger behaviorTrigger = new BehaviorHub.BehaviorTrigger();
		behaviorTrigger.Name = string;
		behaviorTrigger.Value = float1;
		behaviorTrigger.TriggerValue = float2;
		behaviorTrigger.TriggerBehavior = behavior;
		behaviorTrigger.Decay = float3;
		behaviorTrigger.LastValue = float1;
		this.TriggerMap.put(string, behaviorTrigger);
		return behaviorTrigger;
	}

	public void ChangeTriggerValue(String string, float float1) {
		((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string)).LastValue = ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string)).Value;
		BehaviorHub.BehaviorTrigger behaviorTrigger = (BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string);
		behaviorTrigger.Value += float1;
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		decisionPath.DecisionPath.push(this);
		Iterator iterator = this.TriggerMap.values().iterator();
		BehaviorHub.BehaviorTrigger behaviorTrigger;
		do {
			if (!iterator.hasNext()) {
				decisionPath.DecisionPath.pop();
				return Behavior.BehaviorResult.Succeeded;
			}

			behaviorTrigger = (BehaviorHub.BehaviorTrigger)iterator.next();
			if (behaviorTrigger.Value > 1.0F) {
				behaviorTrigger.Value = 1.0F;
			}

			behaviorTrigger.Value -= behaviorTrigger.Decay;
			if (behaviorTrigger.Value < 0.0F) {
				behaviorTrigger.Value = 0.0F;
			}
		} while (!(behaviorTrigger.Value >= behaviorTrigger.TriggerValue));

		if (behaviorTrigger.LastValue < behaviorTrigger.TriggerValue) {
			behaviorTrigger.TriggerBehavior.reset();
		}

		Behavior.BehaviorResult behaviorResult = behaviorTrigger.TriggerBehavior.process(decisionPath, gameCharacter);
		decisionPath.DecisionPath.pop();
		if (behaviorResult == Behavior.BehaviorResult.Failed) {
			return Behavior.BehaviorResult.Succeeded;
		} else if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
			return Behavior.BehaviorResult.Succeeded;
		} else {
			return Behavior.BehaviorResult.Working;
		}
	}

	public void reset() {
		Iterator iterator = this.TriggerMap.values().iterator();
		while (iterator.hasNext()) {
			BehaviorHub.BehaviorTrigger behaviorTrigger = (BehaviorHub.BehaviorTrigger)iterator.next();
			behaviorTrigger.TriggerBehavior.reset();
		}
	}

	public void SetTriggerValue(String string, float float1) {
		if (this.TriggerMap.containsKey(string)) {
			((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string)).LastValue = ((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string)).Value;
			((BehaviorHub.BehaviorTrigger)this.TriggerMap.get(string)).Value = float1;
		}
	}

	public boolean valid() {
		return true;
	}

	public class BehaviorTrigger {
		public float Decay;
		public String Name;
		public Behavior TriggerBehavior;
		public float TriggerValue;
		public float Value;
		private float LastValue;
	}
}
