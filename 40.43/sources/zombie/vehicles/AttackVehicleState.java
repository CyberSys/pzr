package zombie.vehicles;

import fmod.fmod.FMODSoundEmitter;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.ai.states.ZombieStandState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.network.GameServer;


public class AttackVehicleState extends State {
	private static final AttackVehicleState _instance = new AttackVehicleState();
	private BaseSoundEmitter emitter;

	public static AttackVehicleState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		((IsoZombie)gameCharacter).thumpFrame = -1;
		gameCharacter.setIgnoreMovementForDirection(true);
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (!(zombie.target instanceof IsoGameCharacter)) {
			gameCharacter.setDefaultState();
		} else {
			IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
			if (gameCharacter2.isDead()) {
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
			} else {
				BaseVehicle baseVehicle = gameCharacter2.getVehicle();
				if (baseVehicle != null && baseVehicle.isCharacterAdjacentTo(gameCharacter)) {
					Vector3f vector3f = baseVehicle.chooseBestAttackPosition(gameCharacter2, gameCharacter);
					if (vector3f == null || !(Math.abs(vector3f.x - gameCharacter.x) > 0.1F) && !(Math.abs(vector3f.y - gameCharacter.y) > 0.1F)) {
						boolean boolean1 = false;
						VehicleWindow vehicleWindow = null;
						int int1 = baseVehicle.getSeat(gameCharacter2);
						String string2 = baseVehicle.getPassengerArea(int1);
						VehiclePart vehiclePart = null;
						if (baseVehicle.isInArea(string2, gameCharacter)) {
							vehiclePart = baseVehicle.getPassengerDoor(int1);
							if (vehiclePart != null && vehiclePart.getDoor() != null) {
								if (vehiclePart.getInventoryItem() != null && !vehiclePart.getDoor().isOpen()) {
									vehicleWindow = vehiclePart.findWindow();
									if (vehicleWindow != null) {
										if (!vehicleWindow.isHittable()) {
											vehicleWindow = null;
										}

										boolean1 = vehicleWindow == null;
									} else {
										boolean1 = false;
									}
								} else {
									boolean1 = true;
								}
							}
						} else {
							vehiclePart = baseVehicle.getNearestBodyworkPart(gameCharacter);
							if (vehiclePart != null) {
								vehicleWindow = vehiclePart.findWindow();
								if (vehicleWindow != null && !vehicleWindow.isHittable()) {
									vehicleWindow = null;
								}
							}
						}

						gameCharacter.setIgnoreMovementForDirection(false);
						gameCharacter.faceThisObject(gameCharacter2);
						gameCharacter.setIgnoreMovementForDirection(true);
						boolean boolean2 = GameServer.bServer && GameServer.bFastForward || !GameServer.bServer && IsoPlayer.allPlayersAsleep();
						if (boolean1) {
							zombie.PlayAnim("ZombieBite");
							zombie.def.setFrameSpeedPerFrame(0.2F);
							if (!boolean2) {
								boolean boolean3 = zombie.def.Frame >= 15.0F && zombie.def.Frame <= 21.0F;
								if (!boolean3) {
									zombie.HurtPlayerTimer = 0;
									return;
								}

								if (zombie.HurtPlayerTimer == 1) {
									return;
								}
							}

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
									gameCharacter2.setAsleep(false);
								} else {
									gameCharacter2.forceAwake();
								}
							}

							zombie.HurtPlayerTimer = 1;
						} else {
							gameCharacter.PlayAnim("ZombieDoor");
							gameCharacter.def.setFrameSpeedPerFrame(0.15F);
							int int2 = (int)gameCharacter.def.getFrame();
							if (vehicleWindow != null) {
								if (boolean2 || zombie.thumpFrame < 5 && int2 >= 5) {
									vehicleWindow.damage(zombie.strength);
									if (!GameServer.bServer) {
										gameCharacter.getEmitter().playSound("ZombieThumpWindow", baseVehicle);
									}

									zombie.thumpFlag = 3;
								}
							} else if (boolean2 || zombie.thumpFrame < 5 && int2 >= 5) {
								if (!GameServer.bServer) {
									gameCharacter.getEmitter().playSound("ZombieThumpVehicle", baseVehicle);
								}

								zombie.thumpFlag = 1;
							}

							if (vehiclePart != null && Rand.Next(100) < 5) {
								vehiclePart.setCondition(vehiclePart.getCondition() - zombie.strength);
							}

							zombie.thumpFrame = int2;
							if (gameCharacter2.isAsleep()) {
								if (GameServer.bServer) {
									gameCharacter2.sendObjectChange("wakeUp");
									gameCharacter2.setAsleep(false);
								} else {
									gameCharacter2.forceAwake();
								}
							}
						}
					} else if (!(Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 0.1F) || !baseVehicle.isCharacterAdjacentTo(gameCharacter) && !(baseVehicle.DistToSquared(gameCharacter) < 16.0F)) {
						if (zombie.AllowRepathDelay <= 0.0F) {
							gameCharacter.pathToCharacter(gameCharacter2);
							zombie.AllowRepathDelay = 6.25F;
						}
					}
				} else {
					gameCharacter.setDefaultState();
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setIgnoreMovementForDirection(false);
	}
}
