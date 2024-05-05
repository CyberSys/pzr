package zombie.characters;

import zombie.ai.states.ZombieTurnAlerted;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class NetworkZombieVariables {

	public static int getInt(IsoZombie zombie, short short1) {
		switch (short1) {
		case 0: 
			return (int)(zombie.Health * 1000.0F);
		
		case 1: 
			if (zombie.target == null) {
				return -1;
			}

			return ((IAnimatable)zombie.target).getOnlineID();
		
		case 2: 
			return (int)(zombie.speedMod * 1000.0F);
		
		case 3: 
			return (int)zombie.TimeSinceSeenFlesh;
		
		case 4: 
			Float Float1 = (Float)zombie.getStateMachineParams(ZombieTurnAlerted.instance()).get(ZombieTurnAlerted.PARAM_TARGET_ANGLE);
			if (Float1 == null) {
				return 0;
			}

			return Float1.intValue();
		
		default: 
			return 0;
		
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
				IsoPlayer player = (IsoPlayer)GameClient.IDToPlayerMap.get((short)int1);
				if (GameServer.bServer) {
					player = (IsoPlayer)GameServer.IDToPlayerMap.get((short)int1);
				}

				if (player != zombie.target) {
					zombie.setTargetSeenTime(0.0F);
					zombie.target = player;
				}
			}

			break;
		
		case 2: 
			zombie.speedMod = (float)int1 / 1000.0F;
			break;
		
		case 3: 
			zombie.TimeSinceSeenFlesh = (float)int1;
			break;
		
		case 4: 
			zombie.getStateMachineParams(ZombieTurnAlerted.instance()).put(ZombieTurnAlerted.PARAM_TARGET_ANGLE, (float)int1);
		
		}
	}

	public static short getBooleanVariables(IsoZombie zombie) {
		byte byte1 = 0;
		short short1 = (short)(byte1 | (zombie.isFakeDead() ? 1 : 0));
		short1 = (short)(short1 | (zombie.bLunger ? 2 : 0));
		short1 = (short)(short1 | (zombie.bRunning ? 4 : 0));
		short1 = (short)(short1 | (zombie.isCrawling() ? 8 : 0));
		short1 = (short)(short1 | (zombie.isSitAgainstWall() ? 16 : 0));
		short1 = (short)(short1 | (zombie.isReanimatedPlayer() ? 32 : 0));
		short1 = (short)(short1 | (zombie.isOnFire() ? 64 : 0));
		short1 = (short)(short1 | (zombie.isUseless() ? 128 : 0));
		short1 = (short)(short1 | (zombie.isOnFloor() ? 256 : 0));
		return short1;
	}

	public static void setBooleanVariables(IsoZombie zombie, short short1) {
		zombie.setFakeDead((short1 & 1) != 0);
		zombie.bLunger = (short1 & 2) != 0;
		zombie.bRunning = (short1 & 4) != 0;
		zombie.setCrawler((short1 & 8) != 0);
		zombie.setSitAgainstWall((short1 & 16) != 0);
		zombie.setReanimatedPlayer((short1 & 32) != 0);
		if ((short1 & 64) != 0) {
			zombie.SetOnFire();
		} else {
			zombie.StopBurning();
		}

		zombie.setUseless((short1 & 128) != 0);
		if (zombie.isReanimatedPlayer()) {
			zombie.setOnFloor((short1 & 256) != 0);
		}
	}

	public static class VariablesInt {
		public static final short health = 0;
		public static final short target = 1;
		public static final short speedMod = 2;
		public static final short timeSinceSeenFlesh = 3;
		public static final short smParamTargetAngle = 4;
		public static final short MAX = 5;
	}
}
