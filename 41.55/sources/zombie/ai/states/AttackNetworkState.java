package zombie.ai.states;

import java.util.HashMap;
import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.iso.LosUtil;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;


public class AttackNetworkState extends State {
	private static final AttackNetworkState s_instance = new AttackNetworkState();
	private static final String frontStr = "FRONT";
	private static final String backStr = "BEHIND";
	private static final String rightStr = "LEFT";
	private static final String leftStr = "RIGHT";

	public static AttackNetworkState instance() {
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
		gameCharacter.setIgnoreMovementForDirection(true);
		zombie.setTargetSeenTime(1.0F);
		if (!zombie.bCrawling) {
			zombie.setVariable("AttackType", "bite");
		}

		if (!zombie.attackNetworkEvents.isEmpty()) {
			zombie.currentAttackNetworkEvent = (IsoZombie.AttackNetworkEvent)zombie.attackNetworkEvents.pop();
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
		if (gameCharacter2 == null || !"Chainsaw".equals(gameCharacter2.getVariableString("ZombieHitReaction"))) {
			String string = gameCharacter.getVariableString("AttackOutcome");
			if ("success".equals(string) && !gameCharacter.getVariableBoolean("bAttack") && (gameCharacter2 == null || !gameCharacter2.isGodMod()) && !gameCharacter.getVariableBoolean("AttackDidDamage") && gameCharacter.getVariableString("ZombieBiteDone") != "true") {
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
		if (zombie.currentAttackNetworkEvent != null) {
			zombie.currentAttackNetworkEvent = null;
		}

		gameCharacter.clearVariable("AttackOutcome");
		gameCharacter.clearVariable("AttackType");
		gameCharacter.clearVariable("PlayerHitReaction");
		gameCharacter.setIgnoreMovementForDirection(false);
		gameCharacter.setStateMachineLocked(false);
		if (zombie.target != null && zombie.target.isOnFloor()) {
			zombie.setEatBodyTarget(zombie.target, true);
			zombie.setTarget((IsoMovingObject)null);
		}

		zombie.AllowRepathDelay = 0.0F;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		IsoGameCharacter gameCharacter2;
		if (!zombie.networkAI.isLocalControl()) {
			if (zombie.currentAttackNetworkEvent != null) {
				if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
					switch (zombie.currentAttackNetworkEvent.outcome) {
					case 1: 
						zombie.setVariable("AttackOutcome", "success");
						break;
					
					case 2: 
						zombie.setVariable("AttackOutcome", "fail");
						break;
					
					case 3: 
						zombie.setVariable("AttackOutcome", "interrupted");
						break;
					
					default: 
						zombie.setVariable("AttackOutcome", "fail");
					
					}
				}

				if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
					if (zombie.target == null) {
						return;
					}

					gameCharacter2 = (IsoGameCharacter)zombie.target;
					gameCharacter2.setAttackingZombie(zombie);
					gameCharacter2.setHitReaction(zombie.currentAttackNetworkEvent.targetHitReaction);
					this.triggerPlayerReaction(zombie.currentAttackNetworkEvent.targetHitReaction, gameCharacter);
				}

				if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
					gameCharacter.setVariable("EatingStarted", true);
					((IsoZombie)gameCharacter).setEatBodyTarget(((IsoZombie)gameCharacter).target, true);
					((IsoZombie)gameCharacter).setTarget((IsoMovingObject)null);
				}
			}
		} else {
			if (animEvent.m_EventName.equalsIgnoreCase("SetAttackOutcome")) {
				if (zombie.getVariableBoolean("bAttack")) {
					zombie.setVariable("AttackOutcome", "success");
				} else {
					zombie.setVariable("AttackOutcome", "fail");
				}
			}

			if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
				gameCharacter2 = (IsoGameCharacter)zombie.target;
				if (gameCharacter2 == null) {
					return;
				}

				gameCharacter2.setHitFromBehind(zombie.isBehind(gameCharacter2));
				String string = gameCharacter2.testDotSide(zombie);
				boolean boolean1 = string.equals("FRONT");
				if (boolean1 && !StringUtils.isNullOrEmpty(gameCharacter2.getVariableString("AttackType"))) {
					return;
				}

				if (gameCharacter2 != null && "KnifeDeath".equals(gameCharacter2.getVariableString("ZombieHitReaction"))) {
					int int1 = gameCharacter2.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1;
					int int2 = Math.max(0, 9 - int1 * 2);
					if (Rand.NextBool(int2)) {
						return;
					}
				}

				this.triggerPlayerReaction(gameCharacter.getVariableString("PlayerHitReaction"), gameCharacter);
				if (zombie.networkAI.isLocalControl()) {
					GameClient.sendZombieAttackTarget(zombie, (IsoPlayer)gameCharacter2, zombie.getVariableString("AttackOutcome"), gameCharacter2.getHitReaction());
				}
			}

			if (animEvent.m_EventName.equalsIgnoreCase("EatBody")) {
				gameCharacter.setVariable("EatingStarted", true);
				((IsoZombie)gameCharacter).setEatBodyTarget(((IsoZombie)gameCharacter).target, true);
				((IsoZombie)gameCharacter).setTarget((IsoMovingObject)null);
			}
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

						if (!((IsoPlayer)gameCharacter2).bDoShove || !boolean1) {
							if (!((IsoPlayer)gameCharacter2).bDoShove || boolean1 || boolean2 || Rand.Next(100) <= 75) {
								if (!(Math.abs(zombie.z - gameCharacter2.z) >= 0.2F)) {
									LosUtil.TestResults testResults = LosUtil.lineClear(zombie.getCell(), (int)zombie.getX(), (int)zombie.getY(), (int)zombie.getZ(), (int)gameCharacter2.getX(), (int)gameCharacter2.getY(), (int)gameCharacter2.getZ(), false);
									if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
										if (!gameCharacter2.getSquare().isSomethingTo(zombie.getCurrentSquare())) {
											gameCharacter2.setAttackingZombie(zombie);
											boolean boolean3 = gameCharacter2.getBodyDamage().AddRandomDamageFromZombie(zombie, string);
											gameCharacter.setVariable("AttackDidDamage", boolean3);
											gameCharacter2.getBodyDamage().Update();
											if (gameCharacter2.isDead()) {
												if (gameCharacter2.isFemale()) {
													zombie.getEmitter().playVocals("FemaleBeingEatenDeath");
												} else {
													zombie.getEmitter().playVocals("MaleBeingEatenDeath");
												}

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

											gameCharacter2.reportEvent("washit");
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
