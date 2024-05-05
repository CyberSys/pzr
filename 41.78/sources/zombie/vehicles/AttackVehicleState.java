package zombie.vehicles;

import fmod.fmod.FMODSoundEmitter;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.ai.State;
import zombie.ai.states.ZombieIdleState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoMovingObject;
import zombie.network.GameServer;


public final class AttackVehicleState extends State {
	private static final AttackVehicleState _instance = new AttackVehicleState();
	private BaseSoundEmitter emitter;
	private final Vector3f worldPos = new Vector3f();

	public static AttackVehicleState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
	}

	public void execute(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (zombie.target instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
			if (gameCharacter2.isDead()) {
				if (gameCharacter2.getLeaveBodyTimedown() > 3600.0F) {
					zombie.changeState(ZombieIdleState.instance());
					zombie.setTarget((IsoMovingObject)null);
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
					Vector3f vector3f = baseVehicle.chooseBestAttackPosition(gameCharacter2, gameCharacter, this.worldPos);
					if (vector3f == null) {
						if (zombie.AllowRepathDelay <= 0.0F) {
							gameCharacter.pathToCharacter(gameCharacter2);
							zombie.AllowRepathDelay = 6.25F;
						}
					} else if (vector3f != null && (Math.abs(vector3f.x - gameCharacter.x) > 0.1F || Math.abs(vector3f.y - gameCharacter.y) > 0.1F)) {
						if (!(Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 0.8F) || !baseVehicle.isCharacterAdjacentTo(gameCharacter) && !(baseVehicle.DistToSquared(gameCharacter) < 16.0F)) {
							if (zombie.AllowRepathDelay <= 0.0F) {
								gameCharacter.pathToCharacter(gameCharacter2);
								zombie.AllowRepathDelay = 6.25F;
							}
						}
					} else {
						gameCharacter.faceThisObject(gameCharacter2);
					}
				}
			}
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		IsoZombie zombie = (IsoZombie)gameCharacter;
		if (zombie.target instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
			BaseVehicle baseVehicle = gameCharacter2.getVehicle();
			if (baseVehicle != null) {
				if (!gameCharacter2.isDead()) {
					if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck")) {
						gameCharacter2.getBodyDamage().AddRandomDamageFromZombie(zombie, (String)null);
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
					} else if (animEvent.m_EventName.equalsIgnoreCase("ThumpFrame")) {
						VehicleWindow vehicleWindow = null;
						VehiclePart vehiclePart = null;
						int int1 = baseVehicle.getSeat(gameCharacter2);
						String string = baseVehicle.getPassengerArea(int1);
						if (baseVehicle.isInArea(string, gameCharacter)) {
							VehiclePart vehiclePart2 = baseVehicle.getPassengerDoor(int1);
							if (vehiclePart2 != null && vehiclePart2.getDoor() != null && vehiclePart2.getInventoryItem() != null && !vehiclePart2.getDoor().isOpen()) {
								vehicleWindow = vehiclePart2.findWindow();
								if (vehicleWindow != null && !vehicleWindow.isHittable()) {
									vehicleWindow = null;
								}

								if (vehicleWindow == null) {
									vehiclePart = vehiclePart2;
								}
							}
						} else {
							vehiclePart = baseVehicle.getNearestBodyworkPart(gameCharacter);
							if (vehiclePart != null) {
								vehicleWindow = vehiclePart.getWindow();
								if (vehicleWindow == null) {
									vehicleWindow = vehiclePart.findWindow();
								}

								if (vehicleWindow != null && !vehicleWindow.isHittable()) {
									vehicleWindow = null;
								}

								if (vehicleWindow != null) {
									vehiclePart = null;
								}
							}
						}

						if (vehicleWindow != null) {
							vehicleWindow.damage(zombie.strength);
							baseVehicle.setBloodIntensity(vehicleWindow.part.getId(), baseVehicle.getBloodIntensity(vehicleWindow.part.getId()) + 0.025F);
							if (!GameServer.bServer) {
								zombie.setVehicleHitLocation(baseVehicle);
								gameCharacter.getEmitter().playSound("ZombieThumpVehicleWindow", baseVehicle);
							}

							zombie.setThumpFlag(3);
						} else {
							if (!GameServer.bServer) {
								zombie.setVehicleHitLocation(baseVehicle);
								gameCharacter.getEmitter().playSound("ZombieThumpVehicle", baseVehicle);
							}

							zombie.setThumpFlag(1);
						}

						baseVehicle.setAddThumpWorldSound(true);
						if (vehiclePart != null && vehiclePart.getWindow() == null && vehiclePart.getCondition() > 0) {
							vehiclePart.setCondition(vehiclePart.getCondition() - zombie.strength);
							vehiclePart.doInventoryItemStats(vehiclePart.getInventoryItem(), 0);
							baseVehicle.transmitPartCondition(vehiclePart);
						}

						if (gameCharacter2.isAsleep()) {
							if (GameServer.bServer) {
								gameCharacter2.sendObjectChange("wakeUp");
								gameCharacter2.setAsleep(false);
							} else {
								gameCharacter2.forceAwake();
							}
						}
					}
				}
			}
		}
	}

	public boolean isAttacking(IsoGameCharacter gameCharacter) {
		return true;
	}

	public boolean isPassengerExposed(IsoGameCharacter gameCharacter) {
		if (!(gameCharacter instanceof IsoZombie)) {
			return false;
		} else {
			IsoZombie zombie = (IsoZombie)gameCharacter;
			if (!(zombie.target instanceof IsoGameCharacter)) {
				return false;
			} else {
				IsoGameCharacter gameCharacter2 = (IsoGameCharacter)zombie.target;
				BaseVehicle baseVehicle = gameCharacter2.getVehicle();
				if (baseVehicle == null) {
					return false;
				} else {
					boolean boolean1 = false;
					VehicleWindow vehicleWindow = null;
					int int1 = baseVehicle.getSeat(gameCharacter2);
					String string = baseVehicle.getPassengerArea(int1);
					VehiclePart vehiclePart = null;
					if (baseVehicle.isInArea(string, gameCharacter)) {
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

					return boolean1;
				}
			}
		}
	}
}
