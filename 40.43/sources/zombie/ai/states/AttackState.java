package zombie.ai.states;

import fmod.fmod.FMODSoundEmitter;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.LosUtil;
import zombie.network.GameServer;


public class AttackState extends State {
	static AttackState _instance = new AttackState();
	private BaseSoundEmitter emitter;

	public static AttackState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(true);
		if (gameCharacter instanceof IsoZombie) {
			IsoZombie zombie = (IsoZombie)gameCharacter;
			if (!zombie.bCrawling) {
				zombie.PlayAnim("ZombieBite");
				zombie.def.setFrameSpeedPerFrame(0.2F);
				zombie.def.Frame = 4.0F;
			} else {
				zombie.PlayAnim("Zombie_CrawlLunge");
				zombie.def.setFrameSpeedPerFrame(Rand.Next(0.12F, 0.17F));
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoZombie) {
			IsoZombie zombie = (IsoZombie)gameCharacter;
			zombie.setShootable(true);
			if (zombie.target != null) {
				if (zombie.target instanceof IsoGameCharacter && ((IsoGameCharacter)zombie.target).getVehicle() != null) {
					zombie.setDefaultState();
					return;
				}

				if (zombie.target instanceof IsoPlayer && ((IsoPlayer)zombie.target).GhostMode) {
					zombie.setDefaultState();
					return;
				}

				if (!zombie.bCrawling) {
					zombie.setDir(IsoDirections.fromAngle(zombie.vectorToTarget));
					zombie.setOnFloor(false);
				} else if (zombie.dir != IsoDirections.fromAngle(zombie.vectorToTarget)) {
					gameCharacter.StateMachineParams.clear();
					gameCharacter.StateMachineParams.put(0, IsoDirections.fromAngle(zombie.vectorToTarget));
					gameCharacter.getStateMachine().Lock = false;
					gameCharacter.getStateMachine().changeState(CrawlingZombieTurnState.instance());
				}

				if (Math.abs(zombie.z - zombie.target.z) >= 0.2F) {
					zombie.target = null;
					zombie.Wander();
					return;
				}
			}

			float float1 = zombie.vectorToTarget.getLength();
			float float2 = zombie.bCrawling ? 1.4F : 0.9F;
			if (float1 > float2) {
				zombie.getStateMachine().Lock = false;
				zombie.Wander();
				zombie.Lunge();
				return;
			}

			if (zombie.target == null) {
				zombie.Wander();
				return;
			}

			IsoGameCharacter gameCharacter2;
			if (zombie.target != null && (zombie.def.Frame > 7.0F || ((IsoGameCharacter)zombie.target).getSlowFactor() > 0.0F)) {
				gameCharacter2 = (IsoGameCharacter)zombie.target;
				float float3 = gameCharacter2.getSlowFactor();
				if (gameCharacter2.getSlowFactor() <= 0.0F) {
					gameCharacter2.setSlowTimer(30.0F);
				}

				gameCharacter2.setSlowTimer(gameCharacter2.getSlowTimer() + GameTime.instance.getMultiplier());
				if (gameCharacter2.getSlowTimer() > 90.0F) {
					gameCharacter2.setSlowTimer(60.0F);
				}

				gameCharacter2.setSlowFactor(gameCharacter2.getSlowFactor() + 0.03F);
				if (gameCharacter2.getSlowFactor() >= 0.5F) {
					gameCharacter2.setSlowFactor(0.5F);
				}

				if (GameServer.bServer && float3 != gameCharacter2.getSlowFactor()) {
					GameServer.sendSlowFactor(gameCharacter2);
				}
			}

			zombie.target.setTimeSinceZombieAttack(0);
			zombie.target.setLastTargettedBy(zombie);
			--zombie.AttackAnimTime;
			if (!zombie.bCrawling) {
				zombie.PlayAnim("ZombieBite");
				zombie.def.setFrameSpeedPerFrame(0.2F);
			} else {
				zombie.PlayAnim("Zombie_CrawlLunge");
				zombie.def.setFrameSpeedPerFrame(Rand.Next(0.12F, 0.17F));
			}

			if (SandboxOptions.instance.Lore.Speed.getValue() == 1 || zombie.speedType == 1) {
				zombie.def.setFrameSpeedPerFrame(0.4F);
			}

			if (zombie.inactive) {
				zombie.def.setFrameSpeedPerFrame(0.08F);
			}

			gameCharacter2 = (IsoGameCharacter)zombie.target;
			if (gameCharacter2 != null && gameCharacter2.isDead()) {
				if (gameCharacter2.getLeaveBodyTimedown() > 3600.0F) {
					zombie.getStateMachine().changeState(ZombieStandState.instance());
					zombie.target = null;
				} else {
					gameCharacter2.setLeaveBodyTimedown(gameCharacter2.getLeaveBodyTimedown() + GameTime.getInstance().getMultiplier() / 1.6F);
					if (!GameServer.bServer && !Core.SoundDisabled && Rand.Next(Rand.AdjustForFramerate(15)) == 0) {
						if (this.emitter == null) {
							this.emitter = new FMODSoundEmitter();
						}

						String string = zombie.isFemale() ? "FemaleZombieEating" : "MaleZombieEating";
						if (!this.emitter.isPlaying(string)) {
							this.emitter.playSound(string);
						}
					}
				}

				zombie.TimeSinceSeenFlesh = 0.0F;
				return;
			}

			boolean boolean1 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
			if (!boolean1) {
				boolean boolean2 = zombie.bCrawling && zombie.def.Frame >= 10.0F && zombie.def.Frame <= 16.0F || !zombie.bCrawling && zombie.def.Frame >= 15.0F && zombie.def.Frame <= 21.0F;
				if (!boolean2) {
					zombie.HurtPlayerTimer = 0;
					return;
				}

				if (zombie.HurtPlayerTimer == 1) {
					return;
				}
			}

			if (Rand.Next(Rand.AdjustForFramerate(3)) == 0 && zombie.target != null && Math.abs(zombie.z - zombie.target.z) < 0.2F) {
				LosUtil.TestResults testResults = LosUtil.lineClear(zombie.getCell(), (int)zombie.getX(), (int)zombie.getY(), (int)zombie.getZ(), (int)zombie.target.getX(), (int)zombie.target.getY(), (int)zombie.target.getZ(), false);
				if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor && !zombie.target.getSquare().isSomethingTo(zombie.getCurrentSquare())) {
					gameCharacter2.getBodyDamage().AddRandomDamageFromZombie(zombie);
					gameCharacter2.getBodyDamage().Update();
					if (gameCharacter2.isDead()) {
						if (gameCharacter2.isFemale()) {
							zombie.getEmitter().playVocals("FemaleBeingEatenDeath");
						} else {
							zombie.getEmitter().playVocals("MaleBeingEatenDeath");
						}

						gameCharacter2.setHealth(0.0F);
					} else if (gameCharacter2.isAsleep()) {
						if (GameServer.bServer) {
							gameCharacter2.sendObjectChange("wakeUp");
						} else {
							gameCharacter2.forceAwake();
						}
					}

					zombie.HurtPlayerTimer = 1;
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
		gameCharacter.getStateMachine().Lock = false;
	}
}
