package zombie.characters;

import zombie.ai.states.ZombieTurnAlerted;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.iso.IsoMovingObject;
import zombie.network.GameClient;


public class NetworkZombieVariables {

	public static int getInt(IsoZombie zombie, int int1) {
		switch (int1) {
		case 0: 
			return (int)(zombie.Health * 1000.0F);
		
		case 1: 
			if (zombie.target == null) {
				return -1;
			}

			return ((IAnimatable)zombie.target).getOnlineID();
		
		case 2: 
			return zombie.getHitHeadWhileOnFloor();
		
		case 3: 
			return zombie.thumpFlag;
		
		case 4: 
			if (!(zombie.eatBodyTarget instanceof IsoPlayer)) {
				return -1;
			}

			return ((IsoPlayer)zombie.eatBodyTarget).getOnlineID();
		
		case 5: 
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		case 10: 
		
		case 11: 
		
		case 12: 
		
		case 14: 
		
		case 15: 
		
		case 16: 
		
		default: 
			return 0;
		
		case 13: 
			return (int)zombie.TimeSinceSeenFlesh;
		
		case 17: 
			return (int)(zombie.LungeTimer * 1000.0F);
		
		case 18: 
			Float Float1 = (Float)zombie.getStateMachineParams(ZombieTurnAlerted.instance()).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE);
			return Float1 == null ? 0 : Float1.intValue();
		
		}
	}

	public static void setInt(IsoZombie zombie, short short1, int int1) {
		switch (short1) {
		case 0: 
			zombie.Health = (float)int1 / 1000.0F;
			break;
		
		case 1: 
			if (int1 == -1) {
				zombie.setTargetSeenTime(0.0F);
				zombie.target = null;
			} else {
				IsoPlayer player = (IsoPlayer)GameClient.IDToPlayerMap.get(int1);
				if (player != zombie.target) {
					zombie.setTargetSeenTime(0.0F);
					zombie.target = player;
				}
			}

			break;
		
		case 2: 
			zombie.setHitHeadWhileOnFloor(int1);
			break;
		
		case 3: 
			zombie.thumpFlag = int1;
			break;
		
		case 4: 
			if (int1 == -1) {
				zombie.eatBodyTarget = null;
			} else if (zombie.vectorToTarget.getLength() <= (zombie.bCrawling ? 1.4F : 0.72F)) {
				zombie.eatBodyTarget = (IsoMovingObject)GameClient.IDToPlayerMap.get(int1);
			}

		
		case 5: 
		
		case 6: 
		
		case 7: 
		
		case 8: 
		
		case 9: 
		
		case 10: 
		
		case 11: 
		
		case 12: 
		
		case 14: 
		
		case 15: 
		
		case 16: 
		
		default: 
			break;
		
		case 13: 
			zombie.TimeSinceSeenFlesh = (float)int1;
			break;
		
		case 17: 
			zombie.LungeTimer = (float)int1 / 1000.0F;
			break;
		
		case 18: 
			zombie.getStateMachineParams(ZombieTurnAlerted.instance()).put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, (float)int1);
		
		}
	}

	public static int getBooleanVariables(IsoZombie zombie) {
		byte byte1 = 0;
		int int1 = byte1 | (zombie.isFakeDead() ? 1 : 0);
		int1 |= zombie.bLunger ? 2 : 0;
		int1 |= zombie.bRunning ? 4 : 0;
		int1 |= zombie.bCrawling ? 8 : 0;
		int1 |= zombie.isSitAgainstWall() ? 16 : 0;
		int1 |= zombie.isOnDeathDone() ? 32 : 0;
		int1 |= zombie.isFallOnFront() ? 64 : 0;
		int1 |= zombie.isReanimatedPlayer() ? 128 : 0;
		int1 |= zombie.isOnFire() ? 256 : 0;
		int1 |= zombie.bStaggerBack ? 512 : 0;
		int1 |= zombie.bKnockedDown ? 1024 : 0;
		int1 |= zombie.isUseless() ? 2048 : 0;
		int1 |= zombie.alerted ? 4096 : 0;
		int1 |= zombie.isDead() ? 8192 : 0;
		int1 |= zombie.isOnFloor() ? 16384 : 0;
		return int1;
	}

	public static void setBooleanVariables(IsoZombie zombie, int int1) {
		zombie.setFakeDead((int1 & 1) != 0);
		zombie.bLunger = (int1 & 2) != 0;
		zombie.bRunning = (int1 & 4) != 0;
		zombie.bCrawling = (int1 & 8) != 0;
		zombie.setSitAgainstWall((int1 & 16) != 0);
		zombie.setOnDeathDone((int1 & 32) != 0);
		zombie.setReanimatedPlayer((int1 & 128) != 0);
		if ((int1 & 256) != 0) {
			zombie.SetOnFire();
		} else {
			zombie.StopBurning();
		}

		zombie.bStaggerBack = (int1 & 512) != 0;
		zombie.bKnockedDown = (int1 & 1024) != 0;
		zombie.setUseless((int1 & 2048) != 0);
		zombie.alerted = (int1 & 4096) != 0;
		if ((int1 & 8192) != 0) {
			zombie.setHealth(0.0F);
		}

		if (zombie.isReanimatedPlayer()) {
			zombie.setOnFloor((int1 & 16384) != 0);
		}
	}

	public static class VariablesInt {
		public static final byte helth = 0;
		public static final byte target = 1;
		public static final byte hitHeadWhileOnFloor = 2;
		public static final byte thumpFlag = 3;
		public static final byte eatBodyTarget = 4;
		public static final byte timeSinceSeenFlesh = 13;
		public static final byte lungeTimer = 17;
		public static final byte smParamTargetAngle = 18;
		public static final byte MAX = 19;
	}
}
