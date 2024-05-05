package zombie.characters;

import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;
import zombie.vehicles.PathFindBehavior2;


public class NetworkZombieMind {
	private final IsoZombie zombie;
	private byte pfbType = 0;
	private float pfbTargetX;
	private float pfbTargetY;
	private float pfbTargetZ;
	private boolean pfbIsCanceled = false;
	private boolean shouldRestorePFBTarget = false;
	private IsoPlayer pfbTargetCharacter = null;

	public NetworkZombieMind(IsoZombie zombie) {
		this.zombie = zombie;
	}

	public void set(ZombiePacket zombiePacket) {
		PathFindBehavior2 pathFindBehavior2 = this.zombie.getPathFindBehavior2();
		if (!pathFindBehavior2.getIsCancelled() && !pathFindBehavior2.isGoalNone() && !pathFindBehavior2.bStopping && this.zombie.realState != null && !NetworkVariables.ZombieState.Idle.equals(this.zombie.realState)) {
			if (pathFindBehavior2.isGoalCharacter()) {
				IsoGameCharacter gameCharacter = pathFindBehavior2.getTargetChar();
				if (gameCharacter instanceof IsoPlayer) {
					zombiePacket.pfbType = 1;
					zombiePacket.pfbTarget = gameCharacter.getOnlineID();
				} else {
					zombiePacket.pfbType = 0;
					DebugLog.Multiplayer.error("NetworkZombieMind: goal character is not set");
				}
			} else if (pathFindBehavior2.isGoalLocation()) {
				zombiePacket.pfbType = 2;
				zombiePacket.pfbTargetX = pathFindBehavior2.getTargetX();
				zombiePacket.pfbTargetY = pathFindBehavior2.getTargetY();
				zombiePacket.pfbTargetZ = (byte)((int)pathFindBehavior2.getTargetZ());
			} else if (pathFindBehavior2.isGoalSound()) {
				zombiePacket.pfbType = 3;
				zombiePacket.pfbTargetX = pathFindBehavior2.getTargetX();
				zombiePacket.pfbTargetY = pathFindBehavior2.getTargetY();
				zombiePacket.pfbTargetZ = (byte)((int)pathFindBehavior2.getTargetZ());
			}
		} else {
			zombiePacket.pfbType = 0;
		}
	}

	public void parse(ZombiePacket zombiePacket) {
		this.pfbIsCanceled = zombiePacket.pfbType == 0;
		if (!this.pfbIsCanceled) {
			this.pfbType = zombiePacket.pfbType;
			if (this.pfbType == 1) {
				if (GameServer.bServer) {
					this.pfbTargetCharacter = (IsoPlayer)GameServer.IDToPlayerMap.get(zombiePacket.pfbTarget);
				} else if (GameClient.bClient) {
					this.pfbTargetCharacter = (IsoPlayer)GameClient.IDToPlayerMap.get(zombiePacket.pfbTarget);
				}
			} else if (this.pfbType > 1) {
				this.pfbTargetX = zombiePacket.pfbTargetX;
				this.pfbTargetY = zombiePacket.pfbTargetY;
				this.pfbTargetZ = (float)zombiePacket.pfbTargetZ;
			}
		}
	}

	public void restorePFBTarget() {
		this.shouldRestorePFBTarget = true;
	}

	public void zombieIdleUpdate() {
		if (this.shouldRestorePFBTarget) {
			this.doRestorePFBTarget();
			this.shouldRestorePFBTarget = false;
		}
	}

	public void doRestorePFBTarget() {
		if (!this.pfbIsCanceled) {
			if (this.pfbType == 1 && this.pfbTargetCharacter != null) {
				this.zombie.pathToCharacter(this.pfbTargetCharacter);
				this.zombie.spotted(this.pfbTargetCharacter, true);
			} else if (this.pfbType == 2) {
				this.zombie.pathToLocationF(this.pfbTargetX, this.pfbTargetY, this.pfbTargetZ);
			} else if (this.pfbType == 3) {
				this.zombie.pathToSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
				this.zombie.alerted = false;
				this.zombie.setLastHeardSound((int)this.pfbTargetX, (int)this.pfbTargetY, (int)this.pfbTargetZ);
				this.zombie.AllowRepathDelay = 120.0F;
				this.zombie.timeSinceRespondToSound = 0.0F;
			}
		}
	}
}
