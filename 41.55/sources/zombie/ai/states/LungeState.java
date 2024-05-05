package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.gameStates.IngameState;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.util.Type;


public final class LungeState extends State {
	private static final LungeState _instance = new LungeState();
	private final Vector2 temp = new Vector2();
	private static final Integer PARAM_TICK_COUNT = 0;

	public static LungeState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (System.currentTimeMillis() - zombie.LungeSoundTime > 5000L) {
			String string = "MaleZombieAttack";
			if (zombie.isFemale()) {
				string = "FemaleZombieAttack";
			}

			if (GameServer.bServer) {
				GameServer.sendZombieSound(IsoZombie.ZombieSound.Lunge, zombie);
			}

			zombie.LungeSoundTime = System.currentTimeMillis();
		}

		zombie.LungeTimer = 180.0F;
		hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setOnFloor(false);
		gameCharacter.setShootable(true);
		if (zombie.bLunger) {
			zombie.walkVariantUse = "ZombieWalk3";
		}

		zombie.LungeTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(zombie.getTarget(), IsoPlayer.class);
		if (player != null && player.isGhostMode()) {
			zombie.LungeTimer = 0.0F;
		}

		if (zombie.LungeTimer < 0.0F) {
			zombie.LungeTimer = 0.0F;
		}

		if (zombie.LungeTimer <= 0.0F) {
			zombie.AllowRepathDelay = 0.0F;
		}

		this.temp.x = zombie.vectorToTarget.x;
		this.temp.y = zombie.vectorToTarget.y;
		zombie.getZombieLungeSpeed();
		this.temp.normalize();
		zombie.setForwardDirection(this.temp);
		zombie.DirectionFromVector(this.temp);
		zombie.getVectorFromDirection(zombie.getForwardDirection());
		zombie.setForwardDirection(this.temp);
		boolean boolean1 = false;
		if (zombie.NetRemoteState != 4) {
			zombie.NetRemoteState = 4;
			boolean1 = true;
		}

		if (GameServer.bServer && boolean1) {
			GameServer.sendZombie((IsoZombie)gameCharacter);
		}

		if (!zombie.isTargetLocationKnown() && zombie.LastTargetSeenX != -1 && !gameCharacter.getPathFindBehavior2().isTargetLocation((float)zombie.LastTargetSeenX + 0.5F, (float)zombie.LastTargetSeenY + 0.5F, (float)zombie.LastTargetSeenZ)) {
			zombie.LungeTimer = 0.0F;
			gameCharacter.pathToLocation(zombie.LastTargetSeenX, zombie.LastTargetSeenY, zombie.LastTargetSeenZ);
		}

		long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
		if (IngameState.instance.numberTicks - long1 == 2L) {
			((IsoZombie)gameCharacter).parameterZombieState.setState(ParameterZombieState.State.LockTarget);
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public boolean isMoving(IsoGameCharacter gameCharacter) {
		return true;
	}
}
