package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.objects.RainManager;


public final class ZombieIdleState extends State {
	private static final ZombieIdleState _instance = new ZombieIdleState();
	private static final Integer PARAM_TICK_COUNT = 0;

	public static ZombieIdleState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		((IsoZombie)gameCharacter).soundSourceTarget = null;
		((IsoZombie)gameCharacter).soundAttract = 0.0F;
		((IsoZombie)gameCharacter).movex = 0.0F;
		((IsoZombie)gameCharacter).movey = 0.0F;
		gameCharacter.setStateEventDelayTimer(this.pickRandomWanderInterval());
		if (IngameState.instance == null) {
			hashMap.put(PARAM_TICK_COUNT, 0L);
		} else {
			hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		zombie.NetRemoteState = 1;
		zombie.setRemoteMoveX(0.0F);
		zombie.setRemoteMoveY(0.0F);
		zombie.movex = 0.0F;
		zombie.movey = 0.0F;
		int int1;
		if (Core.bLastStand) {
			IsoPlayer player = null;
			float float1 = 1000000.0F;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null && IsoPlayer.players[int1].DistTo(gameCharacter) < float1 && !IsoPlayer.players[int1].isDead()) {
					float1 = IsoPlayer.players[int1].DistTo(gameCharacter);
					player = IsoPlayer.players[int1];
				}
			}

			if (player != null) {
				zombie.pathToCharacter(player);
			}
		} else {
			if (((IsoZombie)gameCharacter).bCrawling) {
				gameCharacter.setOnFloor(true);
			} else {
				gameCharacter.setOnFloor(false);
			}

			long long1 = (Long)hashMap.get(PARAM_TICK_COUNT);
			if (IngameState.instance.numberTicks - long1 == 2L) {
				((IsoZombie)gameCharacter).parameterZombieState.setState(ParameterZombieState.State.Idle);
			}

			if (!zombie.bIndoorZombie) {
				if (!zombie.isUseless()) {
					if (zombie.getStateEventDelayTimer() <= 0.0F) {
						gameCharacter.setStateEventDelayTimer(this.pickRandomWanderInterval());
						int1 = (int)gameCharacter.getX() + (Rand.Next(8) - 4);
						int int2 = (int)gameCharacter.getY() + (Rand.Next(8) - 4);
						if (gameCharacter.getCell().getGridSquare((double)int1, (double)int2, (double)gameCharacter.getZ()) != null && gameCharacter.getCell().getGridSquare((double)int1, (double)int2, (double)gameCharacter.getZ()).isFree(true)) {
							gameCharacter.pathToLocation(int1, int2, (int)gameCharacter.getZ());
							zombie.AllowRepathDelay = 200.0F;
						}
					}
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}

	private float pickRandomWanderInterval() {
		float float1 = (float)Rand.Next(400, 1000);
		if (!RainManager.isRaining()) {
			float1 *= 1.5F;
		}

		return float1;
	}
}
