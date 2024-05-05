package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.network.GameServer;


public final class ZombieEatBodyState extends State {
	private static final ZombieEatBodyState _instance = new ZombieEatBodyState();

	public static ZombieEatBodyState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		zombie.setStateEventDelayTimer(Rand.Next(1800.0F, 3600.0F));
		zombie.setVariable("onknees", Rand.Next(3) != 0);
		if (zombie.getEatBodyTarget() instanceof IsoDeadBody) {
			IsoDeadBody deadBody = (IsoDeadBody)zombie.eatBodyTarget;
			if (!zombie.isEatingOther(deadBody)) {
				HashMap hashMap = gameCharacter.getStateMachineParams(this);
				hashMap.put(0, deadBody);
				deadBody.getEatingZombies().add(zombie);
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		IsoMovingObject movingObject = zombie.getEatBodyTarget();
		if (zombie.getStateEventDelayTimer() <= 0.0F) {
			zombie.setEatBodyTarget((IsoMovingObject)null, false);
		} else if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Eating);
		}

		zombie.TimeSinceSeenFlesh = 0.0F;
		if (movingObject != null) {
			zombie.faceThisObject(movingObject);
		}

		if (Rand.Next(Rand.AdjustForFramerate(450)) == 0) {
			zombie.getCurrentSquare().getChunk().addBloodSplat(zombie.x + Rand.Next(-0.5F, 0.5F), zombie.y + Rand.Next(-0.5F, 0.5F), zombie.z, Rand.Next(8));
			if (Rand.Next(6) == 0) {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.B, zombie.getCell(), zombie.getX(), zombie.getY(), zombie.getZ() + 0.3F, Rand.Next(-0.2F, 0.2F) * 1.5F, Rand.Next(-0.2F, 0.2F) * 1.5F);
			} else {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, zombie.getCell(), zombie.getX(), zombie.getY(), zombie.getZ() + 0.3F, Rand.Next(-0.2F, 0.2F) * 1.5F, Rand.Next(-0.2F, 0.2F) * 1.5F);
			}

			if (Rand.Next(4) == 0) {
				zombie.addBlood((BloodBodyPartType)null, true, false, false);
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (hashMap.get(0) instanceof IsoDeadBody) {
			((IsoDeadBody)hashMap.get(0)).getEatingZombies().remove(zombie);
		}

		if (zombie.parameterZombieState.isState(ParameterZombieState.State.Eating)) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Idle);
		}
	}
}
