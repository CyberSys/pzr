package zombie.characters;

import zombie.ai.states.FishingState;
import zombie.iso.Vector2;


public class NetworkPlayerVariables {
	static Vector2 deferredMovement = new Vector2();

	public static int getBooleanVariables(IsoPlayer player) {
		byte byte1 = 0;
		int int1 = byte1 | (player.isSneaking() ? 1 : 0);
		int1 |= player.isOnFire() ? 2 : 0;
		int1 |= player.isAsleep() ? 4 : 0;
		int1 |= FishingState.instance().equals(player.getCurrentState()) ? 8 : 0;
		int1 |= player.isRunning() ? 16 : 0;
		int1 |= player.isSprinting() ? 32 : 0;
		int1 |= player.isAiming() ? 64 : 0;
		int1 |= player.isCharging ? 128 : 0;
		int1 |= player.isChargingLT ? 256 : 0;
		int1 |= player.bDoShove ? 512 : 0;
		player.getDeferredMovement(deferredMovement);
		int1 |= deferredMovement.getLength() > 0.0F ? 1024 : 0;
		int1 |= player.isOnFloor() ? 2048 : 0;
		int1 |= player.isGhostMode() ? 4096 : 0;
		int1 |= player.isSitOnGround() ? 8192 : 0;
		int1 |= player.isNoClip() ? 16384 : 0;
		int1 |= "fall".equals(player.getVariableString("ClimbFenceOutcome")) ? 262144 : 0;
		return int1;
	}

	public static void setBooleanVariables(IsoPlayer player, int int1) {
		player.setSneaking((int1 & 1) != 0);
		if ((int1 & 2) != 0) {
			player.SetOnFire();
		} else {
			player.StopBurning();
		}

		player.setAsleep((int1 & 4) != 0);
		boolean boolean1 = (int1 & 8) != 0;
		if (FishingState.instance().equals(player.getCurrentState()) && !boolean1) {
			player.SetVariable("FishingFinished", "true");
		}

		player.setRunning((int1 & 16) != 0);
		player.setSprinting((int1 & 32) != 0);
		player.setIsAiming((int1 & 64) != 0);
		player.isCharging = (int1 & 128) != 0;
		player.isChargingLT = (int1 & 256) != 0;
		if (!player.bDoShove && (int1 & 512) != 0) {
			player.setDoShove((int1 & 512) != 0);
		}

		player.networkAI.moving = (int1 & 1024) != 0;
		player.setOnFloor((int1 & 2048) != 0);
		player.setSitOnGround((int1 & 8192) != 0);
		player.networkAI.climbFenceOutcomeFall = (int1 & 262144) != 0;
	}
}
