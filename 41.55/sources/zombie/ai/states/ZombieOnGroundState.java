package zombie.ai.states;

import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.model.Model;
import zombie.inventory.types.HandWeapon;
import zombie.iso.Vector3;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.DeadBodyPacket;


public final class ZombieOnGroundState extends State {
	private static final ZombieOnGroundState _instance = new ZombieOnGroundState();
	static Vector3 tempVector = new Vector3();
	static Vector3 tempVectorBonePos = new Vector3();

	public static ZombieOnGroundState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		gameCharacter.setCollidable(false);
		if (!gameCharacter.isDead()) {
			gameCharacter.setOnFloor(true);
		}

		if (!gameCharacter.isDead() && !zombie.isFakeDead()) {
			if (!zombie.isBecomeCrawler()) {
				if (!"Tutorial".equals(Core.GameMode)) {
					if ((GameServer.bServer || GameClient.bClient) && zombie.networkAI.reanimateTimer > 0.0F) {
						gameCharacter.setReanimateTimer(zombie.networkAI.reanimateTimer);
					} else {
						gameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
					}
				}
			}
		} else {
			becomeCorpse(zombie);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (!gameCharacter.isDead() && !zombie.isFakeDead()) {
			if (zombie.isBecomeCrawler()) {
				if (!zombie.isBeingSteppedOn() && !zombie.isUnderVehicle()) {
					zombie.bCrawling = true;
					zombie.setCanWalk(false);
					zombie.setReanimate(true);
					zombie.setBecomeCrawler(false);
				}
			} else {
				if (gameCharacter.hasAnimationPlayer()) {
					gameCharacter.getAnimationPlayer().setTargetToAngle();
				}

				gameCharacter.setReanimateTimer(gameCharacter.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
				if (gameCharacter.getReanimateTimer() <= 2.0F && gameCharacter.isBeingSteppedOn() && zombie.getReanimatedPlayer() == null) {
					gameCharacter.setReanimateTimer((float)(Rand.Next(60) + 30));
				}
			}
		} else {
			becomeCorpse(zombie);
		}
	}

	private static void becomeCorpse(IsoZombie zombie) {
		if (GameClient.bClient) {
			if (zombie.networkAI.deadZombie == null) {
				if ((zombie.lastPlayerHit & ~DeadBodyPacket.DIED_UNDER_VEHICLE) == IsoPlayer.getInstance().getOnlineID()) {
					GameClient.sendDeadZombie(zombie);
				}
			} else if (zombie.networkAI.deadZombie.isServer) {
				new IsoDeadBody(zombie);
			}
		} else {
			if (!zombie.isOnDeathDone()) {
				zombie.setOnDeathDone(true);
				zombie.DoZombieInventory();
				LuaEventManager.triggerEvent("OnZombieDead", zombie);
				zombie.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
			}

			if (GameServer.bServer) {
				GameServer.sendDeadZombie(zombie);
			}

			IsoDeadBody deadBody = new IsoDeadBody(zombie);
			if (!GameClient.bClient && zombie.isFakeDead()) {
				deadBody.setCrawling(true);
			}
		}
	}

	public static boolean isCharacterStandingOnOther(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2) {
		AnimationPlayer animationPlayer = gameCharacter2.getAnimationPlayer();
		int int1 = DoCollisionBoneCheck(gameCharacter, gameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_Spine", -1), 0.32F);
		if (int1 == -1) {
			int1 = DoCollisionBoneCheck(gameCharacter, gameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_L_Calf", -1), 0.18F);
		}

		if (int1 == -1) {
			int1 = DoCollisionBoneCheck(gameCharacter, gameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_R_Calf", -1), 0.18F);
		}

		if (int1 == -1) {
			int1 = DoCollisionBoneCheck(gameCharacter, gameCharacter2, animationPlayer.getSkinningBoneIndex("Bip01_Head", -1), 0.28F);
		}

		return int1 > -1;
	}

	private static int DoCollisionBoneCheck(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, int int1, float float1) {
		float float2 = 0.3F;
		Model.BoneToWorldCoords(gameCharacter2, int1, tempVectorBonePos);
		for (int int2 = 1; int2 <= 10; ++int2) {
			float float3 = (float)int2 / 10.0F;
			tempVector.x = gameCharacter.x;
			tempVector.y = gameCharacter.y;
			tempVector.z = gameCharacter.z;
			Vector3 vector3 = tempVector;
			vector3.x += gameCharacter.getForwardDirection().x * float2 * float3;
			vector3 = tempVector;
			vector3.y += gameCharacter.getForwardDirection().y * float2 * float3;
			tempVector.x = tempVectorBonePos.x - tempVector.x;
			tempVector.y = tempVectorBonePos.y - tempVector.y;
			tempVector.z = 0.0F;
			boolean boolean1 = tempVector.getLength() < float1;
			if (boolean1) {
				return int1;
			}
		}

		return -1;
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}
}
