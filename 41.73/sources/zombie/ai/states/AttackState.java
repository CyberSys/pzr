package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoMovingObject;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;


public final class AttackState extends State {
	private static final AttackState s_instance = new AttackState();
	private static final String frontStr = "FRONT";
	private static final String backStr = "BEHIND";
	private static final String rightStr = "LEFT";
	private static final String leftStr = "RIGHT";

	public static AttackState instance() {
		return s_instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.clear();
		hashMap.put(0, Boolean.FALSE);
		gameCharacter.setVariable("AttackOutcome", "start");
		gameCharacter.clearVariable("AttackDidDamage");
		gameCharacter.clearVariable("ZombieBiteDone");
	}

	public void execute(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoZombie zombie = (IsoZombie)gameCharacter;
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
		if (gameCharacter2 == null || !"Chainsaw".equals(gameCharacter2.getVariableString("ZombieHitReaction"))) {
			String string = gameCharacter.getVariableString("AttackOutcome");
			if ("success".equals(string) && gameCharacter.getVariableBoolean("bAttack") && gameCharacter.isVariable("targethitreaction", "EndDeath")) {
				string = "enddeath";
				gameCharacter.setVariable("AttackOutcome", string);
			}

			if ("success".equals(string) && !gameCharacter.getVariableBoolean("bAttack") && !gameCharacter.getVariableBoolean("AttackDidDamage") && gameCharacter.getVariableString("ZombieBiteDone") == null) {
				gameCharacter.setVariable("AttackOutcome", "interrupted");
			}

			if (gameCharacter2 == null || gameCharacter2.isDead()) {
				zombie.setTargetSeenTime(10.0F);
			}

			if (gameCharacter2 != null && hashMap.get(0) == Boolean.FALSE && !"started".equals(string) && !StringUtils.isNullOrEmpty(gameCharacter.getVariableString("PlayerHitReaction"))) {
				hashMap.put(0, Boolean.TRUE);
				gameCharacter2.testDefense(zombie);
			}

			zombie.setShootable(true);
			if (zombie.target != null && !zombie.bCrawling) {
				if (!"fail".equals(string) && !"interrupted".equals(string)) {
					zombie.faceThisObject(zombie.target);
				}

				zombie.setOnFloor(false);
			}

			boolean boolean1 = zombie.speedType == 1;
			if (zombie.target != null && boolean1 && ("start".equals(string) || "success".equals(string))) {
				IsoGameCharacter gameCharacter3 = (IsoGameCharacter)zombie.target;
				float float1 = gameCharacter3.getSlowFactor();
				if (gameCharacter3.getSlowFactor() <= 0.0F) {
					gameCharacter3.setSlowTimer(30.0F);
				}

				gameCharacter3.setSlowTimer(gameCharacter3.getSlowTimer() + GameTime.instance.getMultiplier());
				if (gameCharacter3.getSlowTimer() > 60.0F) {
					gameCharacter3.setSlowTimer(60.0F);
				}

				gameCharacter3.setSlowFactor(gameCharacter3.getSlowFactor() + 0.03F);
				if (gameCharacter3.getSlowFactor() >= 0.5F) {
					gameCharacter3.setSlowFactor(0.5F);
				}

				if (GameServer.bServer && float1 != gameCharacter3.getSlowFactor()) {
					GameServer.sendSlowFactor(gameCharacter3);
				}
			}

			if (zombie.target != null) {
				zombie.target.setTimeSinceZombieAttack(0);
				zombie.target.setLastTargettedBy(zombie);
			}

			if (!zombie.bCrawling) {
				zombie.setVariable("AttackType", "bite");
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		gameCharacter.clearVariable("AttackOutcome");
		gameCharacter.clearVariable("AttackType");
		gameCharacter.clearVariable("PlayerHitReaction");
		gameCharacter.setStateMachineLocked(false);
		if (zombie.target != null && zombie.target.isOnFloor()) {
			zombie.setEatBodyTarget(zombie.target, true);
			zombie.setTarget((IsoMovingObject)null);
		}

		zombie.AllowRepathDelay = 0.0F;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
			if (zombie.getVariableBoolean("bAttack")) {
				zombie.setVariable("AttackOutcome", "success");
			} else {
				zombie.setVariable("AttackOutcome", "fail");
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && !zombie.isNoTeeth()) {
			IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
			if (gameCharacter2 == null) {
				return;
			}

			gameCharacter2.setHitFromBehind(zombie.isBehind(gameCharacter2));
			String string = gameCharacter2.testDotSide(zombie);
			boolean boolean1 = string.equals("FRONT");
			if (boolean1 && !gameCharacter2.isAimAtFloor() && !StringUtils.isNullOrEmpty(gameCharacter2.getVariableString("AttackType"))) {
				return;
			}

			if ("KnifeDeath".equals(gameCharacter2.getVariableString("ZombieHitReaction"))) {
				int int1 = gameCharacter2.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1;
				int int2 = Math.max(0, 9 - int1 * 2);
				if (Rand.NextBool(int2)) {
					return;
				}
			}

			this.triggerPlayerReaction(gameCharacter.getVariableString("PlayerHitReaction"), gameCharacter);
			Vector2 vector2 = zombie.getHitDir();
			vector2.x = zombie.getX();
			vector2.y = zombie.getY();
			vector2.x -= gameCharacter2.getX();
			vector2.y -= gameCharacter2.getY();
			vector2.normalize();
			if (GameClient.bClient && !zombie.isRemoteZombie()) {
				GameClient.sendHitCharacter(zombie, gameCharacter2, (HandWeapon)null, 0.0F, false, 1.0F, false, false, false);
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
			gameCharacter.setVariable("EatingStarted", true);
			((IsoZombie)gameCharacter).setEatBodyTarget(((IsoZombie)gameCharacter).target, true);
			((IsoZombie)gameCharacter).setTarget((IsoMovingObject)null);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetState")) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Attack);
		}
	}

	public boolean isAttacking(IsoGameCharacter gameCharacter) {
		return true;
	}

	private void triggerPlayerReaction(String string, IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
		if (gameCharacter2 != null) {
			if (!(zombie.DistTo(gameCharacter2) > 1.0F) || zombie.bCrawling) {
				if (!zombie.isFakeDead() && !zombie.bCrawling || !(zombie.DistTo(gameCharacter2) > 1.3F)) {
					if ((!gameCharacter2.isDead() || gameCharacter2.getHitReaction().equals("EndDeath")) && !gameCharacter2.isOnFloor()) {
						if (!gameCharacter2.isDead()) {
							gameCharacter2.setHitFromBehind(zombie.isBehind(gameCharacter2));
							String string2 = gameCharacter2.testDotSide(zombie);
							boolean boolean1 = string2.equals("FRONT");
							boolean boolean2 = string2.equals("BEHIND");
							if (string2.equals("RIGHT")) {
								string = string + "LEFT";
							}

							if (string2.equals("LEFT")) {
								string = string + "RIGHT";
							}

							if (!((IsoPlayer)gameCharacter2).bDoShove || !boolean1 || gameCharacter2.isAimAtFloor()) {
								if (!((IsoPlayer)gameCharacter2).bDoShove || boolean1 || boolean2 || Rand.Next(100) <= 75) {
									if (!(Math.abs(zombie.z - gameCharacter2.z) >= 0.2F)) {
										LosUtil.TestResults testResults = LosUtil.lineClear(zombie.getCell(), (int)zombie.getX(), (int)zombie.getY(), (int)zombie.getZ(), (int)gameCharacter2.getX(), (int)gameCharacter2.getY(), (int)gameCharacter2.getZ(), false);
										if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
											if (!gameCharacter2.getSquare().isSomethingTo(zombie.getCurrentSquare())) {
												gameCharacter2.setAttackedBy(zombie);
												boolean boolean3 = false;
												if (!GameClient.bClient && !GameServer.bServer || GameClient.bClient && !zombie.isRemoteZombie()) {
													boolean3 = gameCharacter2.getBodyDamage().AddRandomDamageFromZombie(zombie, string);
												}

												gameCharacter.setVariable("AttackDidDamage", boolean3);
												gameCharacter2.getBodyDamage().Update();
												if (gameCharacter2.isDead()) {
													gameCharacter2.setHealth(0.0F);
													zombie.setEatBodyTarget(gameCharacter2, true);
													zombie.setTarget((IsoMovingObject)null);
												} else if (gameCharacter2.isAsleep()) {
													if (GameServer.bServer) {
														gameCharacter2.sendObjectChange("wakeUp");
													} else {
														gameCharacter2.forceAwake();
													}
												}
											}
										}
									}
								}
							}
						}
					} else {
						zombie.setEatBodyTarget(gameCharacter2, true);
					}
				}
			}
		}
	}
}
