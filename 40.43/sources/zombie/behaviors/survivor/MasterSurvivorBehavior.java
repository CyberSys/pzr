package zombie.behaviors.survivor;

import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.Stats;
import zombie.iso.IsoCamera;


public class MasterSurvivorBehavior extends Behavior {
	IsoSurvivor survivor;
	public Behavior toProcess = null;
	int sinceLastChanged = 0;
	int sinceLastChangedMax = 120;
	FleeBehaviour flee;
	AttackBehavior attack;
	SatisfyIdleBehavior idle;
	ObeyOrders orders;
	int timeTillProcessChange = 120;
	int timeTillPathSpeedChange = 120;
	public static float FleeMultiplier = 0.05F;
	public static float AttackMultiplier = 15.0F;

	public MasterSurvivorBehavior(IsoSurvivor survivor) {
		this.survivor = survivor;
		this.flee = new FleeBehaviour();
		this.attack = new AttackBehavior();
		this.idle = new SatisfyIdleBehavior();
		this.orders = new ObeyOrders(survivor);
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		gameCharacter.setPath((Path)null);
		boolean boolean1;
		if (gameCharacter == IsoCamera.CamCharacter) {
			boolean1 = false;
		}

		if (this.toProcess instanceof ObeyOrders && gameCharacter.getDangerLevels() > 5.0F) {
			this.timeTillProcessChange = -1;
		}

		Behavior.BehaviorResult behaviorResult;
		if (gameCharacter.getPersonalNeed() != null && gameCharacter.getPersonalNeed().isCritical() && !(this.toProcess instanceof FleeBehaviour)) {
			if (!gameCharacter.getPersonalNeed().bInit) {
				gameCharacter.getPersonalNeed().initOrder();
				gameCharacter.getPersonalNeed().bInit = true;
			}

			behaviorResult = gameCharacter.getPersonalNeed().process();
			if (!gameCharacter.getPersonalNeed().complete()) {
				return behaviorResult;
			}

			gameCharacter.getPersonalNeeds().pop();
			gameCharacter.setPersonalNeed((Order)null);
		}

		this.flee.update();
		this.attack.update();
		this.idle.update();
		this.orders.update();
		--this.timeTillProcessChange;
		--this.timeTillPathSpeedChange;
		if (gameCharacter.getStats().endurance < gameCharacter.getStats().endurancewarn && gameCharacter.getStats().endurancelast >= gameCharacter.getStats().endurancewarn) {
			this.timeTillPathSpeedChange = -1;
		}

		if (gameCharacter.getStats().endurance < gameCharacter.getStats().endurancedanger && gameCharacter.getStats().endurancelast >= gameCharacter.getStats().endurancedanger) {
			this.timeTillPathSpeedChange = -1;
		}

		if (this.timeTillPathSpeedChange <= 0) {
			if (gameCharacter == IsoCamera.CamCharacter) {
				boolean1 = false;
			}

			this.timeTillPathSpeedChange = 100;
			if (this.toProcess != null) {
				gameCharacter.setPathSpeed(this.toProcess.getPathSpeed());
			} else {
				gameCharacter.setPathSpeed(0.05F);
			}

			if (gameCharacter.getStats().endurance < gameCharacter.getStats().endurancewarn) {
				gameCharacter.setPathSpeed(0.05F);
				this.timeTillPathSpeedChange = 200;
			}

			if (gameCharacter.getStats().endurance < gameCharacter.getStats().endurancedanger) {
				gameCharacter.setPathSpeed(0.04F);
				this.timeTillPathSpeedChange = 200;
			}
		}

		Stats stats;
		if (gameCharacter.getPathSpeed() > 0.06F) {
			stats = gameCharacter.getStats();
			stats.endurance -= 0.005F;
		}

		if (gameCharacter.getPathSpeed() <= 0.06F) {
			stats = gameCharacter.getStats();
			stats.endurance += 5.0E-4F;
		}

		if (gameCharacter.getPathSpeed() <= 0.04F) {
			stats = gameCharacter.getStats();
			stats.endurance += 0.001F;
		}

		if (this.attack == this.toProcess && ((IsoSurvivor)gameCharacter).getVeryCloseEnemyList().size() > 3) {
			this.timeTillProcessChange = -1;
		}

		if (this.orders == this.toProcess && ((IsoSurvivor)gameCharacter).getVeryCloseEnemyList().size() > 3) {
			this.timeTillProcessChange = -1;
		}

		if (this.timeTillProcessChange <= 0 || this.toProcess == null) {
			if (gameCharacter == IsoCamera.CamCharacter) {
				boolean1 = false;
			}

			float float1 = -100000.0F;
			if (gameCharacter.getOrder() != null) {
				float1 = gameCharacter.getOrder().getPriority(gameCharacter) * 5.0F;
			}

			if (!((IsoSurvivor)gameCharacter).getVeryCloseEnemyList().isEmpty()) {
				this.timeTillProcessChange = -1;
				float1 = -10000.0F;
			}

			float float2 = 0.0F;
			float2 = this.attack.getPriority(gameCharacter);
			float float3 = this.flee.getPriority(gameCharacter);
			float float4 = this.idle.getPriority(gameCharacter);
			if (gameCharacter.getThreatLevel() > 0 && !gameCharacter.getLocalRelevantEnemyList().isEmpty() && gameCharacter.getCurrentSquare().getRoom() != null && float2 > 0.0F) {
				float2 += 1000000.0F;
			}

			if (gameCharacter.getThreatLevel() < 10 && gameCharacter.getVeryCloseEnemyList().size() > 0 && float2 > 0.0F) {
				float2 += 1000000.0F;
			}

			if (float4 > float2 && float4 > float1 && float4 > float3) {
				if (this.toProcess != this.idle) {
					this.idle.onSwitch();
				}

				this.toProcess = this.idle;
				this.timeTillProcessChange = 90;
			} else {
				this.idle.reset();
			}

			if (float2 > float3 && float2 > float4 && float2 > float1) {
				if (this.toProcess != this.attack) {
					this.attack.onSwitch();
				}

				this.toProcess = this.attack;
				this.timeTillProcessChange = 100;
			}

			if (float3 > float2 && float3 > float4 && float3 > float1) {
				if (this.toProcess != this.flee) {
					this.flee.onSwitch();
				}

				this.toProcess = this.flee;
				this.timeTillProcessChange = 620;
			}

			if (float1 > float2 && float1 > float4 && float1 > float3) {
				if (this.toProcess != this.orders) {
					this.orders.onSwitch();
				}

				this.toProcess = this.orders;
				this.timeTillProcessChange = 220;
			}
		}

		if (this.toProcess != null) {
			behaviorResult = this.toProcess.process((DecisionPath)null, gameCharacter);
			this.toProcess.last = behaviorResult;
			return behaviorResult;
		} else {
			return Behavior.BehaviorResult.Succeeded;
		}
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}

	public int renderDebug(int int1) {
		boolean boolean1 = true;
		int1 += 20;
		if (this.toProcess != null) {
		}

		return int1;
	}
}
