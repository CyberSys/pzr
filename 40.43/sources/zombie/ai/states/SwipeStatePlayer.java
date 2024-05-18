package zombie.ai.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.State;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;


public class SwipeStatePlayer extends State {
	static SwipeStatePlayer _instance = new SwipeStatePlayer();
	static ArrayList HitList = new ArrayList();
	private static ArrayList HitList2 = new ArrayList();
	private static Vector2 tempVector2_1 = new Vector2();
	private static Vector2 tempVector2_2 = new Vector2();
	private ArrayList dotList = new ArrayList();
	private boolean bHitOnlyTree;
	static IsoPlayer testPlayer;
	private static SwipeStatePlayer.CustomComparator Comparator = new SwipeStatePlayer.CustomComparator();
	static Stack RemoveList = new Stack();

	public static SwipeStatePlayer instance() {
		return _instance;
	}

	public static void WeaponLowerCondition(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (handWeapon.getUses() > 1) {
			handWeapon.Use();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(handWeapon.getModule() + "." + handWeapon.getType());
			((HandWeapon)inventoryItem).setCondition(handWeapon.getCondition() - 1);
			handWeapon.getContainer().AddItem(inventoryItem);
			gameCharacter.setPrimaryHandItem(inventoryItem);
		} else {
			handWeapon.setCondition(handWeapon.getCondition() - 1);
		}
	}

	public void enter(IsoGameCharacter gameCharacter) {
		testPlayer = (IsoPlayer)gameCharacter;
		gameCharacter.AttackDelayLast = -10000.0F;
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		if (gameCharacter.getSprite().CurrentAnim.name.contains("Attack_") && gameCharacter.def.Frame < (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1) && gameCharacter.def.Frame != 0.0F) {
			gameCharacter.getStateMachine().RevertToPrevious();
		} else {
			LuaEventManager.triggerEvent("OnWeaponSwing", gameCharacter, handWeapon);
			if (LuaHookManager.TriggerHook("WeaponSwing", gameCharacter, handWeapon)) {
				gameCharacter.getStateMachine().RevertToPrevious();
			}

			gameCharacter.StopAllActionQueue();
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				IsoWorld.instance.CurrentCell.setDrag((KahluaTable)null, ((IsoPlayer)gameCharacter).PlayerIndex);
			}

			((IsoLivingCharacter)gameCharacter).bDoShove = false;
			((IsoLivingCharacter)gameCharacter).setAimAtFloor(false);
			((IsoLivingCharacter)gameCharacter).targetOnGround = null;
			boolean boolean1 = false;
			if (gameCharacter instanceof IsoPlayer && (handWeapon == ((IsoPlayer)gameCharacter).bareHands || gameCharacter.isForceShove())) {
				((IsoLivingCharacter)gameCharacter).bDoShove = true;
				((IsoLivingCharacter)gameCharacter).setAimAtFloor(false);
				boolean1 = false;
				if (gameCharacter.isForceShove()) {
					handWeapon = ((IsoLivingCharacter)gameCharacter).bareHands;
				}
			}

			float float1 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < gameCharacter.getCell().getObjectList().size(); ++int1) {
				IsoMovingObject movingObject = (IsoMovingObject)gameCharacter.getCell().getObjectList().get(int1);
				if (movingObject.isShootable() && movingObject instanceof IsoZombie && (double)Math.abs(movingObject.getZ() - gameCharacter.getZ()) < 0.5) {
					float float2 = IsoUtils.DistanceTo(movingObject.x, movingObject.y, gameCharacter.x, gameCharacter.y);
					float float3 = handWeapon.getMaxRange(gameCharacter);
					if (((IsoZombie)movingObject).bCrawling) {
						float3 = (float)((double)float3 + 0.5);
					}

					if (float2 < float1 && float2 < float3) {
						Vector2 vector2 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
						Vector2 vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
						vector22.x -= vector2.x;
						vector22.y -= vector2.y;
						Vector2 vector23 = gameCharacter.getAngle();
						vector22.normalize();
						vector23.normalize();
						float float4 = vector22.dot(vector23);
						if (float4 > ((IsoLivingCharacter)gameCharacter).bareHands.getMinAngle() && !movingObject.isOnFloor()) {
							float1 = float2;
						}

						if (float4 > handWeapon.getMinAngle() && gameCharacter.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ()) && !handWeapon.isDirectional()) {
							if (!movingObject.isOnFloor()) {
								((IsoLivingCharacter)gameCharacter).setAimAtFloor(false);
								((IsoLivingCharacter)gameCharacter).targetOnGround = null;
								boolean1 = true;
							} else if (!boolean1 && gameCharacter.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ()) && !handWeapon.isDirectional()) {
								((IsoLivingCharacter)gameCharacter).setAimAtFloor(true);
								((IsoLivingCharacter)gameCharacter).targetOnGround = (IsoGameCharacter)movingObject;
							}
						}
					}
				}
			}

			boolean boolean2 = false;
			float float5;
			if (float1 < handWeapon.getMinRange() && handWeapon.CloseKillMove == null) {
				((IsoLivingCharacter)gameCharacter).bDoShove = true;
				((IsoLivingCharacter)gameCharacter).setAimAtFloor(false);
				if (((IsoLivingCharacter)gameCharacter).bareHands.getSwingAnim() != null) {
					gameCharacter.PlayAnimUnlooped("Attack_" + ((IsoLivingCharacter)gameCharacter).bareHands.getSwingAnim());
					float5 = 0.016666668F * (float)gameCharacter.getSprite().CurrentAnim.Frames.size() / gameCharacter.getAttackDelayMax();
					gameCharacter.def.setFrameSpeedPerFrame(float5 * 2.0F);
					((IsoLivingCharacter)gameCharacter).useChargeDelta = 3.0F;
				}
			} else if (handWeapon.getSwingAnim() != null) {
				gameCharacter.def.Finished = false;
				if (((IsoLivingCharacter)gameCharacter).isAimAtFloor()) {
					if (((IsoLivingCharacter)gameCharacter).bDoShove) {
						gameCharacter.PlayAnimUnlooped("Attack_Floor_Stamp");
						float5 = 0.013888889F * (float)gameCharacter.getSprite().CurrentAnim.Frames.size() / gameCharacter.getAttackDelayMax();
						if (gameCharacter.isForceShove()) {
							float5 = 0.17F;
						}

						gameCharacter.def.setFrameSpeedPerFrame(float5 * 2.0F);
						gameCharacter.setAttackDelayUse(0.35F * (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1));
					} else {
						gameCharacter.PlayAnimUnlooped("Attack_Floor_" + handWeapon.getSwingAnim());
						float5 = 0.016666668F * (float)gameCharacter.getSprite().CurrentAnim.Frames.size() / gameCharacter.getAttackDelayMax();
						gameCharacter.def.setFrameSpeedPerFrame(float5 * 2.0F);
						gameCharacter.setAttackDelayUse(0.35F * (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1));
					}
				} else {
					if (handWeapon.CloseKillMove != null && float1 < handWeapon.getMinRange()) {
						gameCharacter.PlayAnimUnlooped("Attack_" + handWeapon.CloseKillMove);
						gameCharacter.setAttackDelayUse(0.4F * (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1));
						float5 = 0.016666668F * (float)gameCharacter.getSprite().CurrentAnim.Frames.size() / gameCharacter.getAttackDelayMax();
						gameCharacter.def.setFrameSpeedPerFrame(float5);
						boolean2 = true;
					} else {
						gameCharacter.setAttackDelayUse(0.3F * (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1));
						if (!gameCharacter.isForceShove()) {
							gameCharacter.PlayAnimUnlooped("Attack_" + handWeapon.getSwingAnim());
						} else if (gameCharacter.isForceShove() && (float1 < 1.4F || float1 == Float.MAX_VALUE && gameCharacter.getClickSound() == null)) {
							gameCharacter.PlayAnimUnlooped("Attack_" + handWeapon.getSwingAnim());
						}

						float5 = 0.016666668F * (float)gameCharacter.getSprite().CurrentAnim.Frames.size() / gameCharacter.getAttackDelayMax();
						if (gameCharacter.isForceShove()) {
							float5 = 0.12F;
						}

						gameCharacter.def.setFrameSpeedPerFrame(float5 * 2.0F);
					}

					if (handWeapon.isRanged()) {
						gameCharacter.setAttackDelayUse(0.05F * (float)(gameCharacter.getSprite().CurrentAnim.Frames.size() - 1));
					}
				}
			}

			gameCharacter.sprite.CurrentAnim.FinishUnloopedOnFrame = 0;
			if (!((IsoLivingCharacter)gameCharacter).bDoShove) {
				if (handWeapon.isRanged()) {
					float5 = GameTime.getInstance().getNight() * 0.8F;
					IsoWorld.instance.CurrentCell.getLamppostPositions().add(new IsoLightSource((int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 0.8F * float5, 0.8F * float5, 0.6F * float5, 18, 6));
				}

				if (handWeapon.getPhysicsObject() == null) {
					gameCharacter.getEmitter().playSound(handWeapon.getSwingSound());
					if (!boolean2) {
						WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), Math.max(10, handWeapon.getSoundRadius()), handWeapon.getSoundVolume());
					} else {
						WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), Math.max(10, handWeapon.getSoundRadius()) / 4, handWeapon.getSoundVolume() / 4);
					}
				}
			}

			if (gameCharacter.getAttackDelayUse() < 1.0F) {
				gameCharacter.setAttackDelayUse(1.0F);
			}

			if (GameClient.bClient && gameCharacter == IsoPlayer.instance) {
				GameClient.instance.sendPlayer((IsoPlayer)gameCharacter);
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.StopAllActionQueue();
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		if (((IsoLivingCharacter)gameCharacter).bDoShove || gameCharacter.isForceShove()) {
			handWeapon = ((IsoLivingCharacter)gameCharacter).bareHands;
		}

		if (handWeapon == null) {
			gameCharacter.PlayAnim("Idle");
			gameCharacter.getStateMachine().RevertToPrevious();
		} else {
			RemoveList.clear();
			if (handWeapon != null && gameCharacter.def.Frame >= gameCharacter.getAttackDelayUse() && gameCharacter.AttackDelayLast < gameCharacter.getAttackDelayUse()) {
				this.ConnectSwing(gameCharacter, handWeapon);
			}

			if (gameCharacter.def.Finished || gameCharacter.AttackDelayLast > gameCharacter.def.Frame) {
				this.changeWeapon(handWeapon, gameCharacter);
				gameCharacter.setAttackDelay(-1.0F);
				gameCharacter.getStateMachine().RevertToPrevious();
				gameCharacter.getStateMachine().Lock = false;
				((IsoPlayer)gameCharacter).NetRemoteState = 0;
				if (gameCharacter.sprite.CurrentAnim.name.contains("Shove") || gameCharacter.isForceShove()) {
					gameCharacter.PlayAnim("Idle");
				}

				gameCharacter.setForceShove(false);
				gameCharacter.setClickSound((String)null);
			}

			gameCharacter.AttackDelayLast = gameCharacter.def.Frame;
		}
	}

	public void ConnectSwing(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameServer.bServer) {
			DebugLog.log(DebugType.Network, "Player swing connects.");
		}

		int int1 = handWeapon.getMaxHitCount();
		if (handWeapon == ((IsoPlayer)gameCharacter).bareHands && !(((IsoPlayer)gameCharacter).getPrimaryHandItem() instanceof HandWeapon)) {
			int1 = 1;
		}

		if (handWeapon == ((IsoPlayer)gameCharacter).bareHands && ((IsoPlayer)gameCharacter).targetOnGround != null) {
			int1 = 1;
		}

		LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", gameCharacter, handWeapon);
		if (!PerkFactory.newMode) {
			gameCharacter.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
		}

		HitList.clear();
		if (handWeapon.getPhysicsObject() != null) {
			gameCharacter.Throw(handWeapon);
		}

		boolean boolean1 = false;
		int int2 = 0;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean2 = this.CheckObjectHit(gameCharacter, handWeapon);
		int int3;
		Vector2 vector2;
		Vector2 vector22;
		float float1;
		float float2;
		int int4;
		Vector2 vector23;
		if (int2 < int1) {
			IsoMovingObject movingObject;
			Faction faction;
			Faction faction2;
			if (((IsoLivingCharacter)gameCharacter).bDoShove) {
				if (!((IsoLivingCharacter)gameCharacter).isAimAtFloor()) {
					boolean1 = true;
				}

				int3 = 0;
				label749: while (true) {
					if (int3 >= gameCharacter.getCell().getObjectList().size()) {
						Collections.sort(HitList, Comparator);
						while (true) {
							if (HitList.size() <= int1) {
								break label749;
							}

							HitList.remove(HitList.size() - 1);
						}
					}

					movingObject = (IsoMovingObject)gameCharacter.getCell().getObjectList().get(int3);
					if (movingObject != gameCharacter && (!GameClient.bClient || !(movingObject instanceof IsoPlayer) || ((IsoPlayer)movingObject).accessLevel.equals("") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !gameCharacter.isSafety() || !((IsoGameCharacter)movingObject).isSafety())) && (GameClient.bClient || !(movingObject instanceof IsoPlayer) || IsoPlayer.getCoopPVP()) && (!(movingObject instanceof IsoGameCharacter) || !((IsoGameCharacter)movingObject).godMod) && (!GameClient.bClient || !(movingObject instanceof IsoPlayer) || NonPvpZone.getNonPvpZone((int)movingObject.getX(), (int)movingObject.getY()) == null) && (!GameClient.bClient || !(movingObject instanceof IsoPlayer) || !(gameCharacter instanceof IsoPlayer) || NonPvpZone.getNonPvpZone((int)gameCharacter.getX(), (int)gameCharacter.getY()) == null)) {
						label889: {
							if (GameClient.bClient && movingObject instanceof IsoPlayer && gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).factionPvp) {
								faction = Faction.getPlayerFaction((IsoPlayer)gameCharacter);
								faction2 = Faction.getPlayerFaction((IsoPlayer)movingObject);
								if (faction != null && faction2 != null && faction == faction2) {
									break label889;
								}
							}

							if ((movingObject == ((IsoLivingCharacter)gameCharacter).targetOnGround || movingObject.isShootable() && !movingObject.isOnFloor() && !((IsoLivingCharacter)gameCharacter).isAimAtFloor() || movingObject.isShootable() && movingObject.isOnFloor() && ((IsoLivingCharacter)gameCharacter).isAimAtFloor()) && (gameCharacter.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ()) && !handWeapon.isDirectional() || movingObject instanceof BaseVehicle && gameCharacter.DistToSquared((float)((int)movingObject.x), (float)((int)movingObject.y)) <= 16.0F)) {
								LosUtil.TestResults testResults = LosUtil.lineClear(gameCharacter.getCell(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), (int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ(), false);
								if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor && (movingObject.getCurrentSquare() == null || gameCharacter.getCurrentSquare() == null || movingObject.getCurrentSquare() == gameCharacter.getCurrentSquare() || !movingObject.getCurrentSquare().isWindowBlockedTo(gameCharacter.getCurrentSquare()))) {
									vector23 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
									vector2 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
									vector2.x -= vector23.x;
									vector2.y -= vector23.y;
									vector22 = gameCharacter.getAngle();
									gameCharacter.DirectionFromVector(vector22);
									vector2.normalize();
									float float3 = vector2.dot(vector22);
									if (!handWeapon.isRanged() && gameCharacter.getDescriptor() != null && !(movingObject instanceof IsoZombie) && (gameCharacter.getDescriptor().InGroupWith(movingObject) || gameCharacter instanceof IsoSurvivor && movingObject instanceof IsoGameCharacter && !gameCharacter.getEnemyList().contains((IsoGameCharacter)movingObject))) {
										RemoveList.add(movingObject);
									} else {
										if (float3 > 1.0F) {
											float3 = 1.0F;
										}

										if (float3 < -1.0F) {
											float3 = -1.0F;
										}

										if (float3 >= handWeapon.getMinAngle() && float3 <= handWeapon.getMaxAngle() || ((IsoLivingCharacter)gameCharacter).targetOnGround == movingObject) {
											HitList.add(movingObject);
											movingObject.setHitFromAngle(float3);
											++int2;
										}
									}
								}
							}
						}
					}

					++int3;
				}
			} else {
				for (int3 = 0; int3 < gameCharacter.getCell().getObjectList().size(); ++int3) {
					movingObject = (IsoMovingObject)gameCharacter.getCell().getObjectList().get(int3);
					if (movingObject != gameCharacter && (!GameClient.bClient || !(movingObject instanceof IsoPlayer) || ((IsoPlayer)movingObject).accessLevel.equals("") && ServerOptions.instance.PVP.getValue() && (!ServerOptions.instance.SafetySystem.getValue() || !gameCharacter.isSafety() || !((IsoGameCharacter)movingObject).isSafety())) && (GameClient.bClient || !(movingObject instanceof IsoPlayer) || IsoPlayer.getCoopPVP())) {
						if (movingObject instanceof IsoPlayer) {
							if (GameClient.bClient && movingObject instanceof IsoPlayer && NonPvpZone.getNonPvpZone((int)movingObject.getX(), (int)movingObject.getY()) != null || GameClient.bClient && movingObject instanceof IsoPlayer && gameCharacter instanceof IsoPlayer && NonPvpZone.getNonPvpZone((int)gameCharacter.getX(), (int)gameCharacter.getY()) != null) {
								continue;
							}

							if (GameClient.bClient && !handWeapon.isRanged() && movingObject instanceof IsoPlayer && gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).factionPvp) {
								faction = Faction.getPlayerFaction((IsoPlayer)gameCharacter);
								faction2 = Faction.getPlayerFaction((IsoPlayer)movingObject);
								if (faction != null && faction2 != null && faction == faction2) {
									continue;
								}
							}
						}

						if ((!(movingObject instanceof IsoGameCharacter) || !((IsoGameCharacter)movingObject).godMod) && (movingObject == ((IsoLivingCharacter)gameCharacter).targetOnGround || movingObject.isShootable() && !movingObject.isOnFloor() && !((IsoLivingCharacter)gameCharacter).isAimAtFloor() || movingObject.isShootable() && movingObject.isOnFloor() && ((IsoLivingCharacter)gameCharacter).isAimAtFloor())) {
							VehiclePart vehiclePart = null;
							if (movingObject instanceof BaseVehicle) {
								vehiclePart = ((BaseVehicle)movingObject).getNearestBodyworkPart(gameCharacter);
							}

							if (vehiclePart != null || gameCharacter.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ()) && !handWeapon.isDirectional()) {
								LosUtil.TestResults testResults2 = LosUtil.lineClear(gameCharacter.getCell(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), (int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ(), false);
								if (testResults2 != LosUtil.TestResults.Blocked && testResults2 != LosUtil.TestResults.ClearThroughClosedDoor) {
									vector2 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
									vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
									vector22.x -= vector2.x;
									vector22.y -= vector2.y;
									Vector2 vector24 = gameCharacter.getAngle();
									gameCharacter.DirectionFromVector(vector24);
									vector22.normalize();
									float1 = vector22.dot(vector24);
									if (float1 > 1.0F) {
										float1 = 1.0F;
									}

									if (float1 < -1.0F) {
										float1 = -1.0F;
									}

									float2 = handWeapon.getMinAngle();
									if (handWeapon.isRanged()) {
										float2 -= handWeapon.getAimingPerkMinAngleModifier() * (float)(gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2);
									}

									if (float1 >= float2 && float1 <= handWeapon.getMaxAngle()) {
										HitList.add(movingObject);
										movingObject.setHitFromAngle(float1);
										++int2;
									}
								}
							}
						}
					}
				}
			}

			Collections.sort(HitList, Comparator);
			HitList2.clear();
			if (handWeapon.isPiercingBullets()) {
				double double1 = 0.0;
				for (int4 = 0; int4 < HitList.size(); ++int4) {
					IsoMovingObject movingObject2 = (IsoMovingObject)HitList.get(int4);
					double double2 = (double)(gameCharacter.getX() - movingObject2.getX());
					double double3 = (double)(-(gameCharacter.getY() - movingObject2.getY()));
					double double4 = Math.atan2(double3, double2);
					if (double4 < 0.0) {
						double4 = Math.abs(double4);
					} else {
						double4 = 6.283185307179586 - double4;
					}

					if (int4 == 0) {
						double1 = Math.toDegrees(double4);
						HitList2.add(movingObject2);
					} else {
						double double5 = Math.toDegrees(double4);
						if (Math.abs(double1 - double5) < 1.0) {
							HitList2.add(movingObject2);
							break;
						}
					}
				}

				HitList.clear();
				HitList.addAll(HitList2);
			} else {
				while (HitList.size() > int1) {
					HitList.remove(HitList.size() - 1);
				}
			}
		}

		Stats stats;
		if (handWeapon.isUseEndurance()) {
			float float4 = 0.0F;
			if (handWeapon.isTwoHandWeapon() && (gameCharacter.getPrimaryHandItem() != handWeapon || gameCharacter.getSecondaryHandItem() != handWeapon)) {
				float4 = handWeapon.getWeight() / 1.5F / 10.0F;
			}

			float float5 = 0.0F;
			if (int2 <= 0 && !gameCharacter.isForceShove()) {
				float5 = (handWeapon.getWeight() * 0.28F * handWeapon.getFatigueMod(gameCharacter) * gameCharacter.getFatigueMod() * handWeapon.getEnduranceMod() * 0.3F + float4) * 0.04F;
				float float6 = 1.0F;
				if (gameCharacter.HasTrait("Asthmatic")) {
					float6 = 1.3F;
				}

				stats = gameCharacter.getStats();
				stats.endurance -= float5 * float6;
			}
		}

		if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)((IsoPlayer)gameCharacter)).isFakeAttack()) {
			HitList.clear();
			int2 = 0;
			((IsoPlayer)((IsoPlayer)gameCharacter)).setFakeAttack(false);
			((IsoPlayer)((IsoPlayer)gameCharacter)).getFakeAttackTarget().AttackObject(gameCharacter);
		}

		gameCharacter.setLastHitCount(HitList.size());
		if (!PerkFactory.newMode) {
			if (gameCharacter.getStats().endurance > gameCharacter.getStats().endurancewarn && !handWeapon.isRanged()) {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.Fitness, 1.0F);
			}

			if (!handWeapon.isRanged() && int2 > 1) {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.Strength, (float)(int2 / 2));
			}

			if (int2 > 0) {
				gameCharacter.getXp().AddXP(handWeapon, int2);
			}
		}

		if (!handWeapon.isMultipleHitConditionAffected()) {
			if (Rand.Next(handWeapon.getConditionLowerChance() + gameCharacter.getMaintenanceMod() * 2) == 0) {
				WeaponLowerCondition(handWeapon, gameCharacter);
			} else if (!handWeapon.isRanged() && !handWeapon.getName().contains("Bare Hands")) {
				if (gameCharacter.haveBladeWeapon()) {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
				} else {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
				}
			}

			boolean3 = true;
		}

		int3 = 1;
		this.dotList.clear();
		if (HitList.isEmpty() && gameCharacter.getClickSound() != null) {
			gameCharacter.getEmitter().playSound(gameCharacter.getClickSound());
			gameCharacter.setRecoilDelay(10.0F);
		}

		for (int int5 = 0; int5 < HitList.size(); ++int5) {
			IsoMovingObject movingObject3 = (IsoMovingObject)HitList.get(int5);
			vector23 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
			vector2 = tempVector2_2.set(movingObject3.getX(), movingObject3.getY());
			vector2.x -= vector23.x;
			vector2.y -= vector23.y;
			vector22 = tempVector2_1.set(gameCharacter.getAngle().getX(), gameCharacter.getAngle().getY());
			vector22.tangent();
			vector2.normalize();
			boolean boolean4 = true;
			float1 = vector22.dot(vector2);
			float float7;
			for (int int6 = 0; int6 < this.dotList.size(); ++int6) {
				float7 = (Float)this.dotList.get(int6);
				if ((double)Math.abs(float1 - float7) < 1.0E-4) {
					boolean4 = false;
				}
			}

			float2 = handWeapon.getMinDamage();
			float7 = handWeapon.getMaxDamage();
			if (!boolean4) {
				float2 /= 5.0F;
				float7 /= 5.0F;
			}

			if (int5 == 0) {
				this.dotList.add(float1);
			}

			int int7 = handWeapon.getHitChance();
			int7 = (int)((float)int7 + handWeapon.getAimingPerkHitChanceModifier() * (float)(gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2));
			float float8 = IsoUtils.DistanceTo(movingObject3.getX(), movingObject3.getY(), gameCharacter.getX(), gameCharacter.getY());
			if (handWeapon.getMinRangeRanged() > 0.0F) {
				if (float8 < handWeapon.getMinRangeRanged()) {
					int7 -= 50;
				}
			} else if ((double)float8 < 1.5 && handWeapon.isRanged()) {
				int7 += 15;
			}

			if (handWeapon.isRanged() && gameCharacter.getBeenMovingFor() > (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming))) {
				int7 = (int)((float)int7 - (gameCharacter.getBeenMovingFor() - (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming))));
			}

			if (gameCharacter.HasTrait("Marksman")) {
				int7 += 20;
			}

			float float9 = 0.0F;
			for (int int8 = BodyPartType.ToIndex(BodyPartType.Hand_L); int8 <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++int8) {
				float9 += ((BodyPart)gameCharacter.getBodyDamage().getBodyParts().get(int8)).getPain();
			}

			if (float9 > 0.0F) {
				int7 = (int)((float)int7 - float9 / 10.0F);
			}

			if (int7 <= 10) {
				int7 = 10;
			}

			if (int7 > 100 || !handWeapon.isRanged()) {
				int7 = 100;
			}

			boolean boolean5 = Rand.Next(100) <= int7;
			if (movingObject3 instanceof IsoZombie && ((IsoZombie)movingObject3).getStateMachine().getCurrent() == StaggerBackDieState.instance() && ((IsoZombie)movingObject3).getReanimPhase() == 1) {
				((IsoZombie)movingObject3).setReanimateTimer((float)(Rand.Next(60) + 30));
			}

			if (movingObject3 instanceof IsoZombie && ((IsoZombie)movingObject3).getStateMachine().getCurrent() == StaggerBackDieState.instance() && ((IsoZombie)movingObject3).getReanimPhase() == 2 && !((IsoZombie)movingObject3).isFakeDead()) {
				float float10 = 15.0F - movingObject3.def.Frame;
				if (float10 < 2.0F) {
					float10 = 2.0F;
				}

				((IsoZombie)movingObject3).setReanimPhase(1);
				((IsoZombie)movingObject3).PlayAnimUnlooped("ZombieDeath");
				movingObject3.def.Frame = float10;
				((IsoZombie)movingObject3).setReanimateTimer((float)(Rand.Next(60) + 30));
			}

			boolean boolean6 = false;
			if (!handWeapon.isTwoHandWeapon() || handWeapon.isTwoHandWeapon() && gameCharacter.getPrimaryHandItem() == handWeapon && gameCharacter.getSecondaryHandItem() == handWeapon) {
				boolean6 = true;
			}

			float float11 = float7 - float2;
			float float12;
			if (float11 == 0.0F) {
				float12 = float2 + 0.0F;
			} else {
				float12 = float2 + (float)Rand.Next((int)(float11 * 1000.0F)) / 1000.0F;
			}

			if (!handWeapon.isRanged()) {
				float12 *= handWeapon.getDamageMod(gameCharacter) * gameCharacter.getHittingMod();
			}

			if (!boolean6 && !handWeapon.isRanged() && float7 > float2) {
				float12 -= float2;
			}

			if (float9 > 0.0F) {
				float12 /= float9 / 10.0F;
			}

			if (gameCharacter.HasTrait("Underweight")) {
				float12 *= 0.8F;
			}

			if (gameCharacter.HasTrait("Very Underweight")) {
				float12 *= 0.6F;
			}

			if (gameCharacter.HasTrait("Emaciated")) {
				float12 *= 0.4F;
			}

			float float13 = float12 / ((float)int3 / 2.0F);
			if (gameCharacter.isAttackWasSuperAttack()) {
				float13 *= 10.0F;
			}

			switch (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
			case 0: 
			
			default: 
				break;
			
			case 1: 
				float13 *= 0.5F;
				break;
			
			case 2: 
				float13 *= 0.2F;
				break;
			
			case 3: 
				float13 *= 0.1F;
				break;
			
			case 4: 
				float13 *= 0.05F;
			
			}

			++int3;
			if (handWeapon.isUseEndurance() && handWeapon.isShareEndurance()) {
			}

			if (handWeapon.isMultipleHitConditionAffected()) {
				if (Rand.Next(handWeapon.getConditionLowerChance() + gameCharacter.getMaintenanceMod() * 2) == 0) {
					WeaponLowerCondition(handWeapon, gameCharacter);
				} else if (!handWeapon.getName().contains("Bare Hands")) {
					if (gameCharacter.haveBladeWeapon()) {
						gameCharacter.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
					} else {
						gameCharacter.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
					}
				}

				boolean3 = true;
			}

			Vector2 vector25 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
			Vector2 vector26 = tempVector2_2.set(movingObject3.getX(), movingObject3.getY());
			vector26.x -= vector25.x;
			vector26.y -= vector25.y;
			float float14 = vector26.getLength();
			float float15 = 1.0F;
			if (!handWeapon.isRangeFalloff()) {
				float15 = float14 / handWeapon.getMaxRange(gameCharacter);
			}

			if (float15 < 0.3F) {
				float15 = 1.0F;
			}

			if (handWeapon.getZombieHitSound() != null) {
			}

			if (handWeapon.isRanged() && gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) < 6 && gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 2) {
				float13 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.2F;
			}

			if (!handWeapon.isRanged() && gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
				float13 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.1F;
			}

			if (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) > 1) {
				float13 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) * 0.1F;
			}

			if (float13 < 0.0F) {
				float13 = 0.1F;
			}

			gameCharacter.knockbackAttackMod = 1.0F;
			if (handWeapon.CloseKillMove != null && float14 < handWeapon.getMinRange() && gameCharacter.sprite.CurrentAnim.name.contains(handWeapon.CloseKillMove)) {
				float15 *= 1000.0F;
				gameCharacter.knockbackAttackMod = 0.0F;
				WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 4, 4);
				movingObject3.setCloseKilled(true);
			} else {
				movingObject3.setCloseKilled(false);
				if (handWeapon.getImpactSound() != null && (!((IsoLivingCharacter)gameCharacter).bDoShove || gameCharacter.sprite.CurrentAnim.name.contains("Attack_Floor_Stamp"))) {
					gameCharacter.getEmitter().playSound(handWeapon.getImpactSound());
				}

				WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 8, 8);
				if (Rand.Next(3) == 0) {
					WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 10, 10);
				} else if (Rand.Next(7) == 0) {
					WorldSoundManager.instance.addSound(gameCharacter, (int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, 16, 16);
				}
			}

			if (GameClient.bClient) {
				if (boolean5) {
					ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
					PacketTypes.doPacket((short)26, byteBufferWriter);
					byteBufferWriter.putByte((byte)((IsoPlayer)gameCharacter).PlayerIndex);
					if (movingObject3 instanceof IsoZombie) {
						byteBufferWriter.putInt(1);
						byteBufferWriter.putShort(((IsoZombie)movingObject3).OnlineID);
					} else if (movingObject3 instanceof IsoPlayer) {
						byteBufferWriter.putInt(2);
						byteBufferWriter.putShort((short)((IsoPlayer)movingObject3).OnlineID);
						gameCharacter.setSafetyCooldown(gameCharacter.getSafetyCooldown() + (float)ServerOptions.instance.SafetyCooldownTimer.getValue());
					} else if (movingObject3 instanceof BaseVehicle) {
						byteBufferWriter.putInt(3);
						byteBufferWriter.putShort(((BaseVehicle)movingObject3).VehicleID);
					}

					byteBufferWriter.putUTF(handWeapon.getFullType());
					byteBufferWriter.putFloat(float13);
					byteBufferWriter.putBoolean(boolean1);
					byteBufferWriter.putBoolean(movingObject3.isCloseKilled());
					byteBufferWriter.putFloat(float15);
					byteBufferWriter.putFloat(movingObject3.getX());
					byteBufferWriter.putFloat(movingObject3.getY());
					byteBufferWriter.putFloat(movingObject3.getZ());
					byteBufferWriter.putFloat(gameCharacter.getX());
					byteBufferWriter.putFloat(gameCharacter.getY());
					byteBufferWriter.putFloat(gameCharacter.getZ());
					byteBufferWriter.putFloat(movingObject3.getHitForce());
					byteBufferWriter.putFloat(movingObject3.getHitDir().x);
					byteBufferWriter.putFloat(movingObject3.getHitDir().y);
					byteBufferWriter.putFloat(((IsoPlayer)gameCharacter).useChargeDelta);
					GameClient.connection.endPacket();
				}
			} else if (boolean5) {
				if (!(movingObject3 instanceof BaseVehicle) && movingObject3.getSquare() != null && gameCharacter.getSquare() != null && !movingObject3.getSquare().isWindowBlockedTo(gameCharacter.getSquare()) && !gameCharacter.getSquare().isWindowBlockedTo(movingObject3.getSquare())) {
					movingObject3.Hit(handWeapon, gameCharacter, float13, boolean1, float15);
					if (movingObject3 instanceof IsoGameCharacter) {
						if (((IsoGameCharacter)movingObject3).getHealth() <= 0.0F) {
							stats = gameCharacter.getStats();
							stats.stress -= 0.02F;
							if (gameCharacter instanceof IsoSurvivor) {
								((IsoSurvivor)gameCharacter).Killed((IsoGameCharacter)movingObject3);
							}
						} else if (!((IsoLivingCharacter)gameCharacter).bDoShove) {
							splash(movingObject3, handWeapon, gameCharacter);
						}
					}
				} else if (movingObject3 instanceof BaseVehicle) {
					BaseVehicle baseVehicle = (BaseVehicle)movingObject3;
					baseVehicle.getNearestBodyworkPart(gameCharacter);
					VehiclePart vehiclePart2 = baseVehicle.getNearestBodyworkPart(gameCharacter);
					if (vehiclePart2 != null) {
						VehicleWindow vehicleWindow = vehiclePart2.getWindow();
						for (int int9 = 0; int9 < vehiclePart2.getChildCount(); ++int9) {
							VehiclePart vehiclePart3 = vehiclePart2.getChild(int9);
							if (vehiclePart3.getWindow() != null) {
								vehicleWindow = vehiclePart3.getWindow();
								break;
							}
						}

						if (vehicleWindow != null) {
							vehicleWindow.damage((int)float13 * 10);
						} else {
							vehiclePart2.setCondition(vehiclePart2.getCondition() - (int)float13 * 10);
						}
					}
				}
			}
		}

		gameCharacter.AttackDelayLast = gameCharacter.def.Frame;
		if (int2 > 0 && Rand.Next(4) == 0 && !handWeapon.isRanged() && !handWeapon.getName().contains("Bare Hands")) {
			if (gameCharacter.haveBladeWeapon()) {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.BladeGuard, 1.0F);
			} else {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.BluntGuard, 1.0F);
			}
		}

		if (!boolean3 && boolean2) {
			boolean boolean7 = this.bHitOnlyTree && handWeapon.getScriptItem().Categories.contains("Axe");
			int4 = boolean7 ? 2 : 1;
			if (Rand.Next(handWeapon.getConditionLowerChance() * int4 + gameCharacter.getMaintenanceMod() * 2) == 0) {
				WeaponLowerCondition(handWeapon, gameCharacter);
			} else if (Rand.Next(2) == 0 && !handWeapon.getName().contains("Bare Hands")) {
				if (gameCharacter.haveBladeWeapon()) {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.BladeMaintenance, 1.0F);
				} else {
					gameCharacter.getXp().AddXP(PerkFactory.Perks.BluntMaintenance, 1.0F);
				}
			}
		}
	}

	private static void splash(IsoMovingObject movingObject, HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)movingObject;
		if (SandboxOptions.instance.ClothingDegradation.getValue() > 1) {
			float float1 = 0.01F;
			float float2 = 0.1F;
			if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
				float1 = 0.001F;
				float2 = 0.01F;
			}

			if (SandboxOptions.instance.ClothingDegradation.getValue() == 3) {
				float1 = 0.1F;
				float2 = 0.3F;
			}

			Clothing clothing;
			if (gameCharacter.getClothingItem_Torso() != null && gameCharacter.getClothingItem_Torso() instanceof Clothing) {
				clothing = (Clothing)gameCharacter.getClothingItem_Torso();
				clothing.bloodLevel += Rand.Next(float1, float2);
			}

			if (gameCharacter.getClothingItem_Legs() != null && gameCharacter.getClothingItem_Legs() instanceof Clothing) {
				clothing = (Clothing)gameCharacter.getClothingItem_Legs();
				clothing.bloodLevel += Rand.Next(float1, float2);
			}
		}

		if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
			int int1 = handWeapon.getSplatNumber();
			if (int1 < 1) {
				int1 = 1;
			}

			if (Core.bLastStand) {
				int1 *= 3;
			}

			switch (SandboxOptions.instance.BloodLevel.getValue()) {
			case 2: 
				int1 /= 2;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				int1 *= 2;
				break;
			
			case 5: 
				int1 *= 5;
			
			}

			for (int int2 = 0; int2 < int1; ++int2) {
				gameCharacter2.splatBlood(3, 0.3F);
			}
		}

		byte byte1 = 3;
		byte byte2 = 7;
		switch (SandboxOptions.instance.BloodLevel.getValue()) {
		case 1: 
			byte2 = 0;
			break;
		
		case 2: 
			byte2 = 4;
			byte1 = 5;
		
		case 3: 
		
		default: 
			break;
		
		case 4: 
			byte2 = 10;
			byte1 = 2;
			break;
		
		case 5: 
			byte2 = 15;
			byte1 = 0;
		
		}
		if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
			gameCharacter2.splatBloodFloorBig(0.3F);
		}

		float float3 = 0.6F;
		if (gameCharacter2 instanceof IsoZombie && ((IsoZombie)gameCharacter2).bCrawling || gameCharacter2.legsSprite != null && gameCharacter2.legsSprite.CurrentAnim != null && "ZombieDeath".equals(gameCharacter2.legsSprite.CurrentAnim.name)) {
			float3 = 0.3F;
		}

		for (int int3 = 0; int3 < byte2; ++int3) {
			if (Rand.Next(byte1) == 0) {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, gameCharacter2.getCell(), gameCharacter2.getX(), gameCharacter2.getY(), gameCharacter2.getZ() + float3, gameCharacter2.getHitDir().x * Rand.Next(1.5F, 5.0F), gameCharacter2.getHitDir().y * Rand.Next(1.5F, 5.0F));
			}
		}
	}

	private IsoMovingObject PickNPCTarget(IsoSurvivor survivor, HandWeapon handWeapon) {
		for (int int1 = 0; int1 < survivor.getLocalEnemyList().size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)survivor.getLocalEnemyList().get(int1);
			if ((movingObject.isShootable() && !movingObject.isOnFloor() && !survivor.isAimAtFloor() || movingObject.isShootable() && movingObject.isOnFloor() && survivor.isAimAtFloor()) && survivor.IsAttackRange(handWeapon, movingObject.getX(), movingObject.getY(), movingObject.getZ())) {
				Vector2 vector2 = new Vector2(survivor.getX(), survivor.getY());
				Vector2 vector22 = new Vector2(movingObject.getX(), movingObject.getY());
				vector22.x -= vector2.x;
				vector22.y -= vector2.y;
				Vector2 vector23 = survivor.getAngle();
				survivor.DirectionFromVector(vector23);
				vector22.normalize();
				float float1 = vector22.dot(vector23);
				if (float1 > 1.0F) {
					float1 = 1.0F;
				}

				if (float1 < -1.0F) {
					float1 = -1.0F;
				}

				if (float1 >= handWeapon.getMinAngle() && float1 <= handWeapon.getMaxAngle()) {
					return movingObject;
				}
			}
		}

		return null;
	}

	private boolean CheckObjectHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		boolean boolean1 = false;
		int int1 = 0;
		int int2 = 0;
		IsoDirections directions = IsoDirections.fromAngle(gameCharacter.getAngle());
		int int3 = 0;
		int int4 = 0;
		if (directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) {
			--int4;
		}

		if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
			++int4;
		}

		if (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) {
			--int3;
		}

		if (directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE) {
			++int3;
		}

		boolean boolean2 = false;
		IsoGridSquare square = gameCharacter.getCurrentSquare().getCell().getGridSquare(gameCharacter.getCurrentSquare().getX() + int3, gameCharacter.getCurrentSquare().getY() + int4, gameCharacter.getCurrentSquare().getZ());
		int int5;
		IsoObject object;
		if (square != null) {
			for (int5 = 0; int5 < square.getSpecialObjects().size(); ++int5) {
				object = (IsoObject)square.getSpecialObjects().get(int5);
				if (object instanceof IsoBarricade) {
					((IsoBarricade)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoWindow && !((IsoWindow)object).isSmashed()) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoThumpable) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object.getSpecialObjectIndex() == -1) {
					--int5;
				}
			}

			for (int5 = 0; int5 < square.getObjects().size(); ++int5) {
				object = (IsoObject)square.getObjects().get(int5);
				if (object instanceof IsoTree) {
					((IsoTree)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
					++int2;
				}

				if (object.getObjectIndex() == -1) {
					--int5;
				}
			}
		}

		if (directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) {
			for (int5 = 0; int5 < gameCharacter.getCurrentSquare().getSpecialObjects().size(); ++int5) {
				object = (IsoObject)gameCharacter.getCurrentSquare().getSpecialObjects().get(int5);
				if (object instanceof IsoDoor && ((IsoDoor)object).north && !((IsoDoor)object).open) {
					((IsoDoor)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoWindow && ((IsoWindow)object).north && !((IsoWindow)object).isSmashed()) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoThumpable && ((IsoThumpable)object).north) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object.getSpecialObjectIndex() == -1) {
					--int5;
				}
			}
		}

		IsoObject object2;
		int int6;
		IsoGridSquare square2;
		if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
			square2 = gameCharacter.getCell().getGridSquare(gameCharacter.getCurrentSquare().getX(), gameCharacter.getCurrentSquare().getY() + 1, gameCharacter.getCurrentSquare().getZ());
			if (square2 != null) {
				for (int6 = 0; int6 < square2.getSpecialObjects().size(); ++int6) {
					object2 = (IsoObject)square2.getSpecialObjects().get(int6);
					if (object2 instanceof IsoDoor && ((IsoDoor)object2).north) {
						((IsoDoor)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2 instanceof IsoWindow && ((IsoWindow)object2).north && !((IsoWindow)object2).isSmashed()) {
						((IsoWindow)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2 instanceof IsoThumpable && ((IsoThumpable)object2).north) {
						((IsoThumpable)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2.getSpecialObjectIndex() == -1) {
						--int6;
					}
				}
			}
		}

		if (directions == IsoDirections.SE || directions == IsoDirections.E || directions == IsoDirections.NE) {
			square2 = gameCharacter.getCell().getGridSquare(gameCharacter.getCurrentSquare().getX() + 1, gameCharacter.getCurrentSquare().getY(), gameCharacter.getCurrentSquare().getZ());
			if (square2 != null) {
				for (int6 = 0; int6 < square2.getSpecialObjects().size(); ++int6) {
					object2 = (IsoObject)square2.getSpecialObjects().get(int6);
					if (object2 instanceof IsoDoor && !((IsoDoor)object2).north) {
						((IsoDoor)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2 instanceof IsoWindow && !((IsoWindow)object2).north && !((IsoWindow)object2).isSmashed()) {
						((IsoWindow)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2 instanceof IsoThumpable && !((IsoThumpable)object2).north) {
						((IsoThumpable)object2).WeaponHit(gameCharacter, handWeapon);
						boolean1 = true;
						++int1;
					}

					if (object2.getSpecialObjectIndex() == -1) {
						--int6;
					}
				}
			}
		}

		if (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) {
			for (int5 = 0; int5 < gameCharacter.getCurrentSquare().getSpecialObjects().size(); ++int5) {
				object = (IsoObject)gameCharacter.getCurrentSquare().getSpecialObjects().get(int5);
				if (object instanceof IsoDoor && !((IsoDoor)object).north) {
					((IsoDoor)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoWindow && !((IsoWindow)object).north && !((IsoWindow)object).isSmashed()) {
					((IsoWindow)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object instanceof IsoThumpable && !((IsoThumpable)object).north) {
					((IsoThumpable)object).WeaponHit(gameCharacter, handWeapon);
					boolean1 = true;
					++int1;
				}

				if (object.getSpecialObjectIndex() == -1) {
					--int5;
				}
			}
		}

		this.bHitOnlyTree = boolean1 && int1 == int2;
		return boolean1;
	}

	public void changeWeapon(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (handWeapon != null && handWeapon.isUseSelf()) {
			gameCharacter.getInventory().setDrawDirty(true);
			Iterator iterator = gameCharacter.getInventory().getItems().iterator();
			while (iterator.hasNext()) {
				InventoryItem inventoryItem = (InventoryItem)iterator.next();
				if (inventoryItem != handWeapon && inventoryItem instanceof HandWeapon && inventoryItem.getType() == handWeapon.getType() && inventoryItem.getCondition() > 0) {
					if (gameCharacter.getPrimaryHandItem() == handWeapon && gameCharacter.getSecondaryHandItem() == handWeapon) {
						gameCharacter.setPrimaryHandItem(inventoryItem);
						gameCharacter.setSecondaryHandItem(inventoryItem);
					} else if (gameCharacter.getPrimaryHandItem() == handWeapon) {
						gameCharacter.setPrimaryHandItem(inventoryItem);
					} else if (gameCharacter.getSecondaryHandItem() == handWeapon) {
						gameCharacter.setSecondaryHandItem(inventoryItem);
					}

					return;
				}
			}
		}

		if (handWeapon == null || handWeapon.getCondition() <= 0 || handWeapon.isUseSelf()) {
			HandWeapon handWeapon2 = (HandWeapon)gameCharacter.getInventory().getBestWeapon(gameCharacter.getDescriptor());
			gameCharacter.setPrimaryHandItem((InventoryItem)null);
			if (gameCharacter.getSecondaryHandItem() == handWeapon) {
				gameCharacter.setSecondaryHandItem((InventoryItem)null);
			}

			if (handWeapon2 != null && handWeapon2 != gameCharacter.getPrimaryHandItem() && handWeapon2.getCondition() > 0) {
				gameCharacter.setPrimaryHandItem(handWeapon2);
				if (handWeapon2.isTwoHandWeapon() && gameCharacter.getSecondaryHandItem() == null) {
					gameCharacter.setSecondaryHandItem(handWeapon2);
				}
			}
		}
	}

	public static class CustomComparator implements Comparator {

		public int compare(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
			float float1 = movingObject.DistToProper(SwipeStatePlayer.testPlayer);
			float float2 = movingObject2.DistToProper(SwipeStatePlayer.testPlayer);
			if (float1 > float2) {
				return 1;
			} else {
				return float2 > float1 ? -1 : 0;
			}
		}
	}
}
