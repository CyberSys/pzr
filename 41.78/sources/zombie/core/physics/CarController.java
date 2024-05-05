package zombie.core.physics;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.iso.IsoObject;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.EngineRPMData;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.TransmissionNumber;


public final class CarController {
	public final BaseVehicle vehicleObject;
	public float clientForce = 0.0F;
	public float EngineForce = 0.0F;
	public float BrakingForce = 0.0F;
	private float VehicleSteering = 0.0F;
	boolean isGas = false;
	boolean isGasR = false;
	boolean isBreak = false;
	private float atRestTimer = -1.0F;
	private float regulatorTimer = 0.0F;
	public boolean isEnable = false;
	private final Transform tempXfrm = new Transform();
	private final Vector2 tempVec2 = new Vector2();
	private final Vector3f tempVec3f = new Vector3f();
	private final Vector3f tempVec3f_2 = new Vector3f();
	private final Vector3f tempVec3f_3 = new Vector3f();
	private static final Vector3f _UNIT_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	public boolean acceleratorOn = false;
	public boolean brakeOn = false;
	public float speed = 0.0F;
	public static CarController.GearInfo[] gears = new CarController.GearInfo[3];
	public final CarController.ClientControls clientControls = new CarController.ClientControls();
	private boolean engineStartingFromKeyboard;
	private static final CarController.BulletVariables bulletVariables;
	float drunkDelayCommandTimer = 0.0F;
	boolean wasBreaking = false;
	boolean wasGas = false;
	boolean wasGasR = false;
	boolean wasSteering = false;

	public CarController(BaseVehicle baseVehicle) {
		this.vehicleObject = baseVehicle;
		this.engineStartingFromKeyboard = false;
		VehicleScript vehicleScript = baseVehicle.getScript();
		float float1 = baseVehicle.savedPhysicsZ;
		if (Float.isNaN(float1)) {
			float float2 = Math.max((float)((int)baseVehicle.z), 0.0F);
			if (vehicleScript.getWheelCount() > 0) {
				Vector3f vector3f = vehicleScript.getModelOffset();
				float2 += vector3f.y();
				float2 += vehicleScript.getWheel(0).getOffset().y() - vehicleScript.getWheel(0).radius;
			}

			float float3 = vehicleScript.getCenterOfMassOffset().y() - vehicleScript.getExtents().y() / 2.0F;
			float1 = 0.0F - Math.min(float2, float3);
			baseVehicle.jniTransform.origin.y = float1;
		}

		if (!GameServer.bServer) {
			Bullet.addVehicle(baseVehicle.VehicleID, baseVehicle.x, baseVehicle.y, float1, baseVehicle.savedRot.x, baseVehicle.savedRot.y, baseVehicle.savedRot.z, baseVehicle.savedRot.w, vehicleScript.getFullName());
			Bullet.setVehicleStatic(baseVehicle, baseVehicle.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote));
			DebugLog.Vehicle.debugln("Vehicle vid=%d type=%s has been added at (%f;%f;%f) auth=%s", baseVehicle.VehicleID, vehicleScript.getFullName(), baseVehicle.x, baseVehicle.y, float1, baseVehicle.getAuthorizationDescription());
		}
	}

	public CarController.GearInfo findGear(float float1) {
		for (int int1 = 0; int1 < gears.length; ++int1) {
			if (float1 >= (float)gears[int1].minSpeed && float1 < (float)gears[int1].maxSpeed) {
				return gears[int1];
			}
		}

		return null;
	}

	public void accelerator(boolean boolean1) {
		this.acceleratorOn = boolean1;
	}

	public void brake(boolean boolean1) {
		this.brakeOn = boolean1;
	}

	public CarController.ClientControls getClientControls() {
		return this.clientControls;
	}

	public void update() {
		if (this.vehicleObject.getVehicleTowedBy() == null) {
			VehicleScript vehicleScript = this.vehicleObject.getScript();
			this.speed = this.vehicleObject.getCurrentSpeedKmHour();
			boolean boolean1 = this.vehicleObject.getDriver() != null && this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk) > 1;
			float float1 = 0.0F;
			Vector3f vector3f = this.vehicleObject.getLinearVelocity(this.tempVec3f_2);
			vector3f.y = 0.0F;
			if ((double)vector3f.length() > 0.5) {
				vector3f.normalize();
				Vector3f vector3f2 = this.tempVec3f;
				this.vehicleObject.getForwardVector(vector3f2);
				float1 = vector3f.dot(vector3f2);
			}

			float float2 = 1.0F;
			float float3;
			if (GameClient.bClient) {
				float3 = this.vehicleObject.jniSpeed / Math.min(120.0F, (float)ServerOptions.instance.SpeedLimit.getValue());
				float3 *= float3;
				float2 = GameTime.getInstance().Lerp(1.0F, BaseVehicle.getFakeSpeedModifier(), float3);
			}

			float3 = this.vehicleObject.getCurrentSpeedKmHour() * float2;
			this.isGas = false;
			this.isGasR = false;
			this.isBreak = false;
			if (this.clientControls.forward) {
				if (float1 < 0.0F) {
					this.isBreak = true;
				}

				if (float1 >= 0.0F) {
					this.isGas = true;
				}

				this.isGasR = false;
			}

			if (this.clientControls.backward) {
				if (float1 > 0.0F) {
					this.isBreak = true;
				}

				if (float1 <= 0.0F) {
					this.isGasR = true;
				}

				this.isGas = false;
			}

			if (this.clientControls.brake) {
				this.isBreak = true;
				this.isGas = false;
				this.isGasR = false;
			}

			if (this.clientControls.forward && this.clientControls.backward) {
				this.isBreak = true;
				this.isGas = false;
				this.isGasR = false;
			}

			if (boolean1 && this.vehicleObject.engineState != BaseVehicle.engineStateTypes.Idle) {
				if (this.isBreak && !this.wasBreaking) {
					this.isBreak = this.delayCommandWhileDrunk(this.isBreak);
				}

				if (this.isGas && !this.wasGas) {
					this.isGas = this.delayCommandWhileDrunk(this.isGas);
				}

				if (this.isGasR && !this.wasGasR) {
					this.isGasR = this.delayCommandWhileDrunk(this.isGas);
				}

				if (this.clientControls.steering != 0.0F && !this.wasSteering) {
					this.clientControls.steering = this.delayCommandWhileDrunk(this.clientControls.steering);
				}
			}

			this.updateRegulator();
			this.wasBreaking = this.isBreak;
			this.wasGas = this.isGas;
			this.wasGasR = this.isGasR;
			this.wasSteering = this.clientControls.steering != 0.0F;
			if (!this.isGasR && this.vehicleObject.isInvalidChunkAhead()) {
				this.isBreak = true;
				this.isGas = false;
				this.isGasR = false;
			} else if (!this.isGas && this.vehicleObject.isInvalidChunkBehind()) {
				this.isBreak = true;
				this.isGas = false;
				this.isGasR = false;
			}

			if (this.clientControls.shift) {
				this.isGas = false;
				this.isBreak = false;
				this.isGasR = false;
				this.clientControls.wasUsingParkingBrakes = false;
			}

			float float4 = this.vehicleObject.throttle;
			if (!this.isGas && !this.isGasR) {
				float4 -= GameTime.getInstance().getMultiplier() / 30.0F;
			} else {
				float4 += GameTime.getInstance().getMultiplier() / 30.0F;
			}

			if (float4 < 0.0F) {
				float4 = 0.0F;
			}

			if (float4 > 1.0F) {
				float4 = 1.0F;
			}

			if (this.vehicleObject.isRegulator() && !this.isGas && !this.isGasR) {
				float4 = 0.5F;
				if (float3 < this.vehicleObject.getRegulatorSpeed()) {
					this.isGas = true;
				}
			}

			this.vehicleObject.throttle = float4;
			float float5 = GameTime.getInstance().getMultiplier() / 0.8F;
			CarController.ControlState controlState = CarController.ControlState.NoControl;
			if (this.isBreak) {
				controlState = CarController.ControlState.Braking;
			} else if (this.isGas && !this.isGasR) {
				controlState = CarController.ControlState.Forward;
			} else if (!this.isGas && this.isGasR) {
				controlState = CarController.ControlState.Reverse;
			}

			if (controlState != CarController.ControlState.NoControl) {
				UIManager.speedControls.SetCurrentGameSpeed(1);
			}

			if (controlState == CarController.ControlState.NoControl) {
				this.control_NoControl();
			}

			if (controlState == CarController.ControlState.Reverse) {
				this.control_Reverse(float3);
			}

			if (controlState == CarController.ControlState.Forward) {
				this.control_ForwardNew(float3);
			}

			this.updateBackSignal();
			if (controlState == CarController.ControlState.Braking) {
				this.control_Braking();
			}

			this.updateBrakeLights();
			BaseVehicle baseVehicle = this.vehicleObject.getVehicleTowedBy();
			if (baseVehicle != null && baseVehicle.getDriver() == null && this.vehicleObject.getDriver() != null && !GameClient.bClient) {
				this.vehicleObject.addPointConstraint((IsoPlayer)null, baseVehicle, this.vehicleObject.getTowAttachmentSelf(), baseVehicle.getTowAttachmentSelf());
			}

			this.updateRammingSound(float3);
			float float6;
			if (Math.abs(this.clientControls.steering) > 0.1F) {
				float6 = 1.0F - this.speed / this.vehicleObject.getMaxSpeed();
				if (float6 < 0.1F) {
					float6 = 0.1F;
				}

				this.VehicleSteering -= (this.clientControls.steering + this.VehicleSteering) * 0.06F * float5 * float6;
			} else if ((double)Math.abs(this.VehicleSteering) <= 0.04) {
				this.VehicleSteering = 0.0F;
			} else if (this.VehicleSteering > 0.0F) {
				this.VehicleSteering -= 0.04F * float5;
				this.VehicleSteering = Math.max(this.VehicleSteering, 0.0F);
			} else {
				this.VehicleSteering += 0.04F * float5;
				this.VehicleSteering = Math.min(this.VehicleSteering, 0.0F);
			}

			float6 = vehicleScript.getSteeringClamp(this.speed);
			this.VehicleSteering = PZMath.clamp(this.VehicleSteering, -float6, float6);
			CarController.BulletVariables bulletVariables = bulletVariables.set(this.vehicleObject, this.EngineForce, this.BrakingForce, this.VehicleSteering);
			this.checkTire(bulletVariables);
			this.EngineForce = bulletVariables.engineForce;
			this.BrakingForce = bulletVariables.brakingForce;
			this.VehicleSteering = bulletVariables.vehicleSteering;
			if (this.vehicleObject.isDoingOffroad()) {
				int int1 = this.vehicleObject.getTransmissionNumber();
				if (int1 <= 0) {
					int1 = 1;
				}

				this.EngineForce = (float)((double)this.EngineForce / ((double)int1 * 1.5));
			}

			this.vehicleObject.setCurrentSteering(this.VehicleSteering);
			this.vehicleObject.setBraking(this.isBreak);
			if (!GameServer.bServer) {
				this.checkShouldBeActive();
				Bullet.controlVehicle(this.vehicleObject.VehicleID, this.EngineForce, this.BrakingForce, this.VehicleSteering);
				if (this.EngineForce > 0.0F && this.vehicleObject.engineState == BaseVehicle.engineStateTypes.Idle && !this.engineStartingFromKeyboard) {
					this.engineStartingFromKeyboard = true;
					if (GameClient.bClient) {
						Boolean Boolean1 = this.vehicleObject.getDriver().getInventory().haveThisKeyId(this.vehicleObject.getKeyId()) != null ? Boolean.TRUE : Boolean.FALSE;
						GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "startEngine", "haveKey", Boolean1);
					} else {
						this.vehicleObject.tryStartEngine();
					}
				}

				if (this.engineStartingFromKeyboard && this.EngineForce == 0.0F) {
					this.engineStartingFromKeyboard = false;
				}
			}

			if (this.vehicleObject.engineState != BaseVehicle.engineStateTypes.Running) {
				this.acceleratorOn = false;
				if (!GameServer.bServer && this.vehicleObject.jniSpeed > 5.0F && this.vehicleObject.getScript().getWheelCount() > 0) {
					Bullet.controlVehicle(this.vehicleObject.VehicleID, 0.0F, this.BrakingForce, this.VehicleSteering);
				} else {
					this.park();
				}
			}
		}
	}

	public void updateTrailer() {
		BaseVehicle baseVehicle = this.vehicleObject.getVehicleTowedBy();
		if (baseVehicle != null) {
			if (GameServer.bServer) {
				if (baseVehicle.getDriver() == null && this.vehicleObject.getDriver() != null) {
					this.vehicleObject.addPointConstraint((IsoPlayer)null, baseVehicle, this.vehicleObject.getTowAttachmentSelf(), baseVehicle.getTowAttachmentSelf());
				}
			} else {
				this.speed = this.vehicleObject.getCurrentSpeedKmHour();
				this.isGas = false;
				this.isGasR = false;
				this.isBreak = false;
				this.wasGas = false;
				this.wasGasR = false;
				this.wasBreaking = false;
				this.vehicleObject.throttle = 0.0F;
				if (baseVehicle.getDriver() == null && this.vehicleObject.getDriver() != null && !GameClient.bClient) {
					this.vehicleObject.addPointConstraint((IsoPlayer)null, baseVehicle, this.vehicleObject.getTowAttachmentSelf(), baseVehicle.getTowAttachmentSelf());
				} else {
					this.checkShouldBeActive();
					this.EngineForce = 0.0F;
					this.BrakingForce = 0.0F;
					this.VehicleSteering = 0.0F;
					if (!this.vehicleObject.getScriptName().contains("Trailer")) {
						this.BrakingForce = 10.0F;
					}

					Bullet.controlVehicle(this.vehicleObject.VehicleID, this.EngineForce, this.BrakingForce, this.VehicleSteering);
				}
			}
		}
	}

	private void updateRegulator() {
		if (this.regulatorTimer > 0.0F) {
			this.regulatorTimer -= GameTime.getInstance().getMultiplier() / 1.6F;
		}

		if (this.clientControls.shift) {
			if (this.clientControls.forward && this.regulatorTimer <= 0.0F) {
				if (this.vehicleObject.getRegulatorSpeed() < this.vehicleObject.getMaxSpeed() + 20.0F && (!this.vehicleObject.isRegulator() && this.vehicleObject.getRegulatorSpeed() == 0.0F || this.vehicleObject.isRegulator())) {
					if (this.vehicleObject.getRegulatorSpeed() == 0.0F && this.vehicleObject.getCurrentSpeedForRegulator() != this.vehicleObject.getRegulatorSpeed()) {
						this.vehicleObject.setRegulatorSpeed(this.vehicleObject.getCurrentSpeedForRegulator());
					} else {
						this.vehicleObject.setRegulatorSpeed(this.vehicleObject.getRegulatorSpeed() + 5.0F);
					}
				}

				this.vehicleObject.setRegulator(true);
				this.regulatorTimer = 20.0F;
			} else if (this.clientControls.backward && this.regulatorTimer <= 0.0F) {
				this.regulatorTimer = 20.0F;
				if (this.vehicleObject.getRegulatorSpeed() >= 5.0F && (!this.vehicleObject.isRegulator() && this.vehicleObject.getRegulatorSpeed() == 0.0F || this.vehicleObject.isRegulator())) {
					this.vehicleObject.setRegulatorSpeed(this.vehicleObject.getRegulatorSpeed() - 5.0F);
				}

				this.vehicleObject.setRegulator(true);
				if (this.vehicleObject.getRegulatorSpeed() <= 0.0F) {
					this.vehicleObject.setRegulatorSpeed(0.0F);
					this.vehicleObject.setRegulator(false);
				}
			}
		} else if (this.isGasR || this.isBreak) {
			this.vehicleObject.setRegulator(false);
		}
	}

	public void control_NoControl() {
		float float1 = GameTime.getInstance().getMultiplier() / 0.8F;
		if (!this.vehicleObject.isEngineRunning()) {
			if (this.vehicleObject.engineSpeed > 0.0) {
				this.vehicleObject.engineSpeed = Math.max(this.vehicleObject.engineSpeed - (double)(50.0F * float1), 0.0);
			}
		} else {
			BaseVehicle baseVehicle;
			if (this.vehicleObject.engineSpeed > (double)this.vehicleObject.getScript().getEngineIdleSpeed()) {
				if (!this.vehicleObject.isRegulator()) {
					baseVehicle = this.vehicleObject;
					baseVehicle.engineSpeed -= (double)(20.0F * float1);
				}
			} else {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed += (double)(20.0F * float1);
			}
		}

		if (!this.vehicleObject.isRegulator()) {
			this.vehicleObject.transmissionNumber = TransmissionNumber.N;
		}

		this.EngineForce = 0.0F;
		if (this.vehicleObject.engineSpeed > 1000.0) {
			this.BrakingForce = 15.0F;
		} else {
			this.BrakingForce = 10.0F;
		}
	}

	private void control_Braking() {
		float float1 = GameTime.getInstance().getMultiplier() / 0.8F;
		BaseVehicle baseVehicle;
		if (this.vehicleObject.engineSpeed > (double)this.vehicleObject.getScript().getEngineIdleSpeed()) {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= (double)((float)Rand.Next(10, 30) * float1);
		} else {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed += (double)((float)Rand.Next(20) * float1);
		}

		this.vehicleObject.transmissionNumber = TransmissionNumber.N;
		this.EngineForce = 0.0F;
		this.BrakingForce = this.vehicleObject.getBrakingForce();
		if (this.clientControls.brake) {
			this.BrakingForce *= 13.0F;
		}
	}

	private void control_Forward(float float1) {
		float float2 = GameTime.getInstance().getMultiplier() / 0.8F;
		IsoGameCharacter gameCharacter = this.vehicleObject.getDriver();
		boolean boolean1 = gameCharacter != null && gameCharacter.Traits.SpeedDemon.isSet();
		boolean boolean2 = gameCharacter != null && gameCharacter.Traits.SundayDriver.isSet();
		int int1 = this.vehicleObject.getScript().gearRatioCount;
		float float3 = 0.0F;
		boolean boolean3;
		if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
			this.vehicleObject.transmissionNumber = TransmissionNumber.Speed1;
			boolean3 = false;
			while (true) {
				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
					float3 = 3000.0F * float1 / 30.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
					float3 = 3000.0F * float1 / 40.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
					float3 = 3000.0F * float1 / 60.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
					float3 = 3000.0F * float1 / 85.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
					float3 = 3000.0F * float1 / 105.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
					float3 = 3000.0F * float1 / 130.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
					float3 = 3000.0F * float1 / 160.0F;
				}

				if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
					float3 = 3000.0F * float1 / 200.0F;
				}

				if (boolean1) {
					if (float3 > 6000.0F) {
						this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(int1));
						boolean3 = true;
					}
				} else if (float3 > 3000.0F) {
					this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(int1));
					boolean3 = true;
				}

				if (!boolean3 || this.vehicleObject.transmissionNumber.getIndex() >= int1) {
					break;
				}

				boolean3 = false;
			}
		}

		if (boolean1) {
			if (this.vehicleObject.engineSpeed > 6000.0 && this.vehicleObject.transmissionChangeTime.Check()) {
				this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(int1));
			}
		} else if (this.vehicleObject.engineSpeed > 3000.0 && this.vehicleObject.transmissionChangeTime.Check()) {
			this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(int1));
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
			float3 = 3000.0F * float1 / 30.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
			float3 = 3000.0F * float1 / 40.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
			float3 = 3000.0F * float1 / 60.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
			float3 = 3000.0F * float1 / 85.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
			float3 = 3000.0F * float1 / 105.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
			float3 = 3000.0F * float1 / 130.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
			float3 = 3000.0F * float1 / 160.0F;
		}

		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
			float3 = 3000.0F * float1 / 200.0F;
		}

		BaseVehicle baseVehicle = this.vehicleObject;
		baseVehicle.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - (double)float3), 100.0) * (double)float2;
		if (boolean1) {
			if (float1 < 50.0F) {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float1)) * (double)float2;
			}
		} else if (float1 < 30.0F) {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float1)) * (double)float2;
		}

		this.EngineForce = (float)((double)this.vehicleObject.getEnginePower() * (0.5 + this.vehicleObject.engineSpeed / 24000.0));
		this.EngineForce -= this.EngineForce * (float1 / 200.0F);
		boolean3 = false;
		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1 && this.vehicleObject.getVehicleTowedBy() != null) {
			if (this.vehicleObject.getVehicleTowedBy().getScript().getPassengerCount() == 0 && this.vehicleObject.getVehicleTowedBy().getScript().getMass() > 200.0F) {
				boolean3 = true;
			}

			if (float1 < (float)(boolean3 ? 20 : 5)) {
				this.EngineForce *= Math.min(1.2F, this.vehicleObject.getVehicleTowedBy().getMass() / 500.0F);
				if (boolean3) {
					this.EngineForce *= 4.0F;
				}
			}
		}

		if (this.vehicleObject.engineSpeed > 6000.0) {
			this.EngineForce = (float)((double)this.EngineForce * ((7000.0 - this.vehicleObject.engineSpeed) / 1000.0));
		}

		if (boolean2) {
			this.EngineForce *= 0.6F;
			if (float1 > 20.0F) {
				this.EngineForce *= (40.0F - float1) / 20.0F;
			}
		}

		if (boolean1) {
			if (float1 > this.vehicleObject.getMaxSpeed() * 1.15F) {
				this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15F + 20.0F - float1) / 20.0F;
			}
		} else if (float1 > this.vehicleObject.getMaxSpeed()) {
			this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0F - float1) / 20.0F;
		}

		this.BrakingForce = 0.0F;
		if (this.clientControls.wasUsingParkingBrakes) {
			this.clientControls.wasUsingParkingBrakes = false;
			this.EngineForce *= 8.0F;
		}

		if (GameClient.bClient && (double)this.vehicleObject.jniSpeed >= ServerOptions.instance.SpeedLimit.getValue()) {
			this.EngineForce = 0.0F;
		}
	}

	private void control_ForwardNew(float float1) {
		float float2 = GameTime.getInstance().getMultiplier() / 0.8F;
		IsoGameCharacter gameCharacter = this.vehicleObject.getDriver();
		boolean boolean1 = gameCharacter != null && gameCharacter.Traits.SpeedDemon.isSet();
		boolean boolean2 = gameCharacter != null && gameCharacter.Traits.SundayDriver.isSet();
		int int1 = this.vehicleObject.getScript().gearRatioCount;
		float float3 = 0.0F;
		EngineRPMData[] engineRPMDataArray = this.vehicleObject.getVehicleEngineRPM().m_rpmData;
		float float4 = this.vehicleObject.getMaxSpeed() / (float)int1;
		float float5 = PZMath.clamp(float1, 0.0F, this.vehicleObject.getMaxSpeed());
		int int2 = (int)PZMath.floor(float5 / float4) + 1;
		int2 = PZMath.min(int2, int1);
		float3 = engineRPMDataArray[int2 - 1].gearChange;
		TransmissionNumber transmissionNumber = TransmissionNumber.Speed1;
		switch (int2) {
		case 1: 
			transmissionNumber = TransmissionNumber.Speed1;
			break;
		
		case 2: 
			transmissionNumber = TransmissionNumber.Speed2;
			break;
		
		case 3: 
			transmissionNumber = TransmissionNumber.Speed3;
			break;
		
		case 4: 
			transmissionNumber = TransmissionNumber.Speed4;
			break;
		
		case 5: 
			transmissionNumber = TransmissionNumber.Speed5;
			break;
		
		case 6: 
			transmissionNumber = TransmissionNumber.Speed6;
			break;
		
		case 7: 
			transmissionNumber = TransmissionNumber.Speed7;
			break;
		
		case 8: 
			transmissionNumber = TransmissionNumber.Speed8;
		
		}
		if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
			this.vehicleObject.transmissionNumber = transmissionNumber;
		} else if (this.vehicleObject.transmissionNumber.getIndex() - 1 >= 0 && this.vehicleObject.transmissionNumber.getIndex() < transmissionNumber.getIndex() && this.vehicleObject.getEngineSpeed() >= (double)engineRPMDataArray[this.vehicleObject.transmissionNumber.getIndex() - 1].gearChange && float1 >= float4 * (float)this.vehicleObject.transmissionNumber.getIndex()) {
			this.vehicleObject.transmissionNumber = transmissionNumber;
			this.vehicleObject.engineSpeed = (double)engineRPMDataArray[this.vehicleObject.transmissionNumber.getIndex() - 1].afterGearChange;
		}

		if (this.vehicleObject.transmissionNumber.getIndex() < int1 && this.vehicleObject.transmissionNumber.getIndex() - 1 >= 0) {
			this.vehicleObject.engineSpeed = Math.min(this.vehicleObject.engineSpeed, (double)(engineRPMDataArray[this.vehicleObject.transmissionNumber.getIndex() - 1].gearChange + 100.0F));
		}

		float float6;
		BaseVehicle baseVehicle;
		if (this.vehicleObject.engineSpeed > (double)float3) {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - (double)float3), 10.0) * (double)float2;
		} else {
			float float7;
			switch (this.vehicleObject.transmissionNumber) {
			case Speed1: 
				float7 = 10.0F;
				break;
			
			case Speed2: 
				float7 = 8.0F;
				break;
			
			case Speed3: 
				float7 = 7.0F;
				break;
			
			case Speed4: 
				float7 = 6.0F;
				break;
			
			case Speed5: 
				float7 = 5.0F;
				break;
			
			default: 
				float7 = 4.0F;
			
			}

			float6 = float7;
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed += (double)(float6 * float2);
		}

		float6 = (float)this.vehicleObject.getEnginePower();
		float6 = this.vehicleObject.getScript().getEngineForce();
		float float8;
		switch (this.vehicleObject.transmissionNumber) {
		case Speed1: 
			float8 = 1.5F;
			break;
		
		default: 
			float8 = 1.0F;
		
		}
		float6 *= float8;
		this.EngineForce = (float)((double)float6 * (0.30000001192092896 + this.vehicleObject.engineSpeed / 30000.0));
		this.EngineForce -= this.EngineForce * (float1 / 200.0F);
		boolean boolean3 = false;
		if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1 && this.vehicleObject.getVehicleTowedBy() != null) {
			if (this.vehicleObject.getVehicleTowedBy().getScript().getPassengerCount() == 0 && this.vehicleObject.getVehicleTowedBy().getScript().getMass() > 200.0F) {
				boolean3 = true;
			}

			if (float1 < (float)(boolean3 ? 20 : 5)) {
				this.EngineForce *= Math.min(1.2F, this.vehicleObject.getVehicleTowedBy().getMass() / 500.0F);
				if (boolean3) {
					this.EngineForce *= 4.0F;
				}
			}
		}

		if (this.vehicleObject.engineSpeed > 6000.0) {
			this.EngineForce = (float)((double)this.EngineForce * ((7000.0 - this.vehicleObject.engineSpeed) / 1000.0));
		}

		if (boolean2) {
			this.EngineForce *= 0.6F;
			if (float1 > 20.0F) {
				this.EngineForce *= (40.0F - float1) / 20.0F;
			}
		}

		if (boolean1) {
			if (float1 > this.vehicleObject.getMaxSpeed() * 1.15F) {
				this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15F + 20.0F - float1) / 20.0F;
			}
		} else if (float1 > this.vehicleObject.getMaxSpeed()) {
			this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0F - float1) / 20.0F;
		}

		this.BrakingForce = 0.0F;
		if (this.clientControls.wasUsingParkingBrakes) {
			this.clientControls.wasUsingParkingBrakes = false;
			this.EngineForce *= 8.0F;
		}

		if (GameClient.bClient && (double)this.vehicleObject.jniSpeed >= ServerOptions.instance.SpeedLimit.getValue()) {
			this.EngineForce = 0.0F;
		}
	}

	private void control_Reverse(float float1) {
		float float2 = GameTime.getInstance().getMultiplier() / 0.8F;
		float1 *= 1.5F;
		IsoGameCharacter gameCharacter = this.vehicleObject.getDriver();
		boolean boolean1 = gameCharacter != null && gameCharacter.Traits.SpeedDemon.isSet();
		boolean boolean2 = gameCharacter != null && gameCharacter.Traits.SundayDriver.isSet();
		this.vehicleObject.transmissionNumber = TransmissionNumber.R;
		float float3 = 1000.0F * float1 / 30.0F;
		BaseVehicle baseVehicle = this.vehicleObject;
		baseVehicle.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - (double)float3), 100.0) * (double)float2;
		if (boolean1) {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float1)) * (double)float2;
		} else {
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float1)) * (double)float2;
		}

		this.EngineForce = (float)((double)(-1.0F * (float)this.vehicleObject.getEnginePower()) * (0.75 + this.vehicleObject.engineSpeed / 24000.0));
		if (this.vehicleObject.engineSpeed > 6000.0) {
			this.EngineForce = (float)((double)this.EngineForce * ((7000.0 - this.vehicleObject.engineSpeed) / 1000.0));
		}

		if (boolean2) {
			this.EngineForce *= 0.7F;
			if (float1 < -5.0F) {
				this.EngineForce *= (15.0F + float1) / 10.0F;
			}
		}

		if (float1 < -30.0F) {
			this.EngineForce *= (40.0F + float1) / 10.0F;
		}

		this.BrakingForce = 0.0F;
	}

	private void updateRammingSound(float float1) {
		if (this.vehicleObject.isEngineRunning() && (float1 < 1.0F && this.EngineForce > this.vehicleObject.getScript().getEngineIdleSpeed() * 2.0F || float1 > -0.5F && this.EngineForce < this.vehicleObject.getScript().getEngineIdleSpeed() * -2.0F)) {
			if (this.vehicleObject.ramSound == 0L) {
				this.vehicleObject.ramSound = this.vehicleObject.playSoundImpl("VehicleSkid", (IsoObject)null);
				this.vehicleObject.ramSoundTime = System.currentTimeMillis() + 1000L + (long)Rand.Next(2000);
			}

			if (this.vehicleObject.ramSound != 0L && this.vehicleObject.ramSoundTime < System.currentTimeMillis()) {
				this.vehicleObject.stopSound(this.vehicleObject.ramSound);
				this.vehicleObject.ramSound = 0L;
			}
		} else if (this.vehicleObject.ramSound != 0L) {
			this.vehicleObject.stopSound(this.vehicleObject.ramSound);
			this.vehicleObject.ramSound = 0L;
		}
	}

	private void updateBackSignal() {
		if (this.isGasR && this.vehicleObject.isEngineRunning() && this.vehicleObject.hasBackSignal() && !this.vehicleObject.isBackSignalEmitting()) {
			if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "onBackSignal", "state", "start");
			} else {
				this.vehicleObject.onBackMoveSignalStart();
			}
		}

		if (!this.isGasR && this.vehicleObject.isBackSignalEmitting()) {
			if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "onBackSignal", "state", "stop");
			} else {
				this.vehicleObject.onBackMoveSignalStop();
			}
		}
	}

	private void updateBrakeLights() {
		if (this.isBreak) {
			if (this.vehicleObject.getStoplightsOn()) {
				return;
			}

			if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.TRUE);
			}

			if (!GameServer.bServer) {
				this.vehicleObject.setStoplightsOn(true);
			}
		} else {
			if (!this.vehicleObject.getStoplightsOn()) {
				return;
			}

			if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.FALSE);
			}

			if (!GameServer.bServer) {
				this.vehicleObject.setStoplightsOn(false);
			}
		}
	}

	private boolean delayCommandWhileDrunk(boolean boolean1) {
		this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
		if ((float)Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
			this.drunkDelayCommandTimer = 0.0F;
			return true;
		} else {
			return false;
		}
	}

	private float delayCommandWhileDrunk(float float1) {
		this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
		if ((float)Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
			this.drunkDelayCommandTimer = 0.0F;
			return float1;
		} else {
			return 0.0F;
		}
	}

	private void checkTire(CarController.BulletVariables bulletVariables) {
		if (this.vehicleObject.getPartById("TireFrontLeft") == null || this.vehicleObject.getPartById("TireFrontLeft").getInventoryItem() == null) {
			bulletVariables.brakingForce = (float)((double)bulletVariables.brakingForce / 1.2);
			bulletVariables.engineForce = (float)((double)bulletVariables.engineForce / 1.2);
		}

		if (this.vehicleObject.getPartById("TireFrontRight") == null || this.vehicleObject.getPartById("TireFrontRight").getInventoryItem() == null) {
			bulletVariables.brakingForce = (float)((double)bulletVariables.brakingForce / 1.2);
			bulletVariables.engineForce = (float)((double)bulletVariables.engineForce / 1.2);
		}

		if (this.vehicleObject.getPartById("TireRearLeft") == null || this.vehicleObject.getPartById("TireRearLeft").getInventoryItem() == null) {
			bulletVariables.brakingForce = (float)((double)bulletVariables.brakingForce / 1.3);
			bulletVariables.engineForce = (float)((double)bulletVariables.engineForce / 1.3);
		}

		if (this.vehicleObject.getPartById("TireRearRight") == null || this.vehicleObject.getPartById("TireRearRight").getInventoryItem() == null) {
			bulletVariables.brakingForce = (float)((double)bulletVariables.brakingForce / 1.3);
			bulletVariables.engineForce = (float)((double)bulletVariables.engineForce / 1.3);
		}
	}

	public void updateControls() {
		if (!GameServer.bServer) {
			boolean boolean1;
			boolean boolean2;
			boolean boolean3;
			boolean boolean4;
			boolean boolean5;
			if (this.vehicleObject.isKeyboardControlled()) {
				boolean boolean6 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"));
				boolean1 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"));
				boolean2 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"));
				boolean3 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"));
				boolean4 = GameKeyboard.isKeyDown(57);
				boolean5 = GameKeyboard.isKeyDown(42);
				this.clientControls.steering = 0.0F;
				if (boolean6) {
					--this.clientControls.steering;
				}

				if (boolean1) {
					++this.clientControls.steering;
				}

				this.clientControls.forward = boolean2;
				this.clientControls.backward = boolean3;
				this.clientControls.brake = boolean4;
				this.clientControls.shift = boolean5;
				if (this.clientControls.brake) {
					this.clientControls.wasUsingParkingBrakes = true;
				}
			}

			int int1 = this.vehicleObject.getJoypad();
			if (int1 != -1) {
				boolean1 = JoypadManager.instance.isLeftPressed(int1);
				boolean2 = JoypadManager.instance.isRightPressed(int1);
				boolean3 = JoypadManager.instance.isRTPressed(int1);
				boolean4 = JoypadManager.instance.isLTPressed(int1);
				boolean5 = JoypadManager.instance.isBPressed(int1);
				float float1 = JoypadManager.instance.getMovementAxisX(int1);
				this.clientControls.steering = float1;
				this.clientControls.forward = boolean3;
				this.clientControls.backward = boolean4;
				this.clientControls.brake = boolean5;
			}

			if (this.clientControls.forceBrake != 0L) {
				long long1 = System.currentTimeMillis() - this.clientControls.forceBrake;
				if (long1 > 0L && long1 < 1000L) {
					this.clientControls.brake = true;
					this.clientControls.shift = false;
				}
			}
		}
	}

	public void park() {
		if (!GameServer.bServer && this.vehicleObject.getScript().getWheelCount() > 0) {
			Bullet.controlVehicle(this.vehicleObject.VehicleID, 0.0F, this.vehicleObject.getBrakingForce(), 0.0F);
		}

		this.isGas = this.wasGas = false;
		this.isGasR = this.wasGasR = false;
		this.clientControls.reset();
		this.vehicleObject.transmissionNumber = TransmissionNumber.N;
		if (this.vehicleObject.getVehicleTowing() != null) {
			this.vehicleObject.getVehicleTowing().getController().park();
		}
	}

	protected boolean shouldBeActive() {
		if (this.vehicleObject.physicActiveCheck != -1L) {
			return true;
		} else {
			BaseVehicle baseVehicle = this.vehicleObject.getVehicleTowedBy();
			if (baseVehicle == null) {
				float float1 = this.vehicleObject.isEngineRunning() ? this.EngineForce : 0.0F;
				return Math.abs(float1) > 0.01F;
			} else {
				return baseVehicle.getController() == null ? false : baseVehicle.getController().shouldBeActive();
			}
		}
	}

	public void checkShouldBeActive() {
		if (this.shouldBeActive()) {
			if (!this.isEnable) {
				Bullet.setVehicleActive(this.vehicleObject.VehicleID, true);
				this.isEnable = true;
			}

			this.atRestTimer = 1.0F;
		} else if (this.isEnable && this.vehicleObject.isAtRest()) {
			if (this.atRestTimer > 0.0F) {
				this.atRestTimer -= GameTime.getInstance().getTimeDelta();
			}

			if (this.atRestTimer <= 0.0F) {
				Bullet.setVehicleActive(this.vehicleObject.VehicleID, false);
				this.isEnable = false;
			}
		}
	}

	public boolean isGasPedalPressed() {
		return this.isGas || this.isGasR;
	}

	public boolean isBrakePedalPressed() {
		return this.isBreak;
	}

	public void debug() {
		if (Core.bDebug && DebugOptions.instance.VehicleRenderOutline.getValue()) {
			VehicleScript vehicleScript = this.vehicleObject.getScript();
			Vector3f vector3f = this.tempVec3f;
			this.vehicleObject.getForwardVector(vector3f);
			Transform transform = this.tempXfrm;
			this.vehicleObject.getWorldTransform(transform);
			PolygonalMap2.VehiclePoly vehiclePoly = this.vehicleObject.getPoly();
			LineDrawer.addLine(vehiclePoly.x1, vehiclePoly.y1, 0.0F, vehiclePoly.x2, vehiclePoly.y2, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x2, vehiclePoly.y2, 0.0F, vehiclePoly.x3, vehiclePoly.y3, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x3, vehiclePoly.y3, 0.0F, vehiclePoly.x4, vehiclePoly.y4, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x4, vehiclePoly.y4, 0.0F, vehiclePoly.x1, vehiclePoly.y1, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			_UNIT_Y.set(0.0F, 1.0F, 0.0F);
			float float1;
			float float2;
			int int1;
			for (int1 = 0; int1 < this.vehicleObject.getScript().getWheelCount(); ++int1) {
				VehicleScript.Wheel wheel = vehicleScript.getWheel(int1);
				this.tempVec3f.set((Vector3fc)wheel.getOffset());
				if (vehicleScript.getModel() != null) {
					this.tempVec3f.add(vehicleScript.getModelOffset());
				}

				this.vehicleObject.getWorldPos(this.tempVec3f, this.tempVec3f);
				float1 = this.tempVec3f.x;
				float2 = this.tempVec3f.y;
				this.vehicleObject.getWheelForwardVector(int1, this.tempVec3f);
				LineDrawer.addLine(float1, float2, 0.0F, float1 + this.tempVec3f.x, float2 + this.tempVec3f.z, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
				this.drawRect(this.tempVec3f, float1 - WorldSimulation.instance.offsetX, float2 - WorldSimulation.instance.offsetY, wheel.width, wheel.radius);
			}

			if (this.vehicleObject.collideX != -1.0F) {
				this.vehicleObject.getForwardVector(vector3f);
				this.drawCircle(this.vehicleObject.collideX, this.vehicleObject.collideY, 0.3F);
				this.vehicleObject.collideX = -1.0F;
				this.vehicleObject.collideY = -1.0F;
			}

			int1 = this.vehicleObject.getJoypad();
			float float3;
			if (int1 != -1) {
				float3 = JoypadManager.instance.getMovementAxisX(int1);
				float1 = JoypadManager.instance.getMovementAxisY(int1);
				float2 = JoypadManager.instance.getDeadZone(int1, 0);
				if (Math.abs(float1) > float2 || Math.abs(float3) > float2) {
					Vector2 vector2 = this.tempVec2.set(float3, float1);
					vector2.setLength(4.0F);
					vector2.rotate(-0.7853982F);
					LineDrawer.addLine(this.vehicleObject.getX(), this.vehicleObject.getY(), this.vehicleObject.z, this.vehicleObject.getX() + vector2.x, this.vehicleObject.getY() + vector2.y, this.vehicleObject.z, 1.0F, 1.0F, 1.0F, (String)null, true);
				}
			}

			float3 = this.vehicleObject.x;
			float1 = this.vehicleObject.y;
			float2 = this.vehicleObject.z;
			LineDrawer.DrawIsoLine(float3 - 0.5F, float1, float2, float3 + 0.5F, float1, float2, 1.0F, 1.0F, 1.0F, 0.25F, 1);
			LineDrawer.DrawIsoLine(float3, float1 - 0.5F, float2, float3, float1 + 0.5F, float2, 1.0F, 1.0F, 1.0F, 0.25F, 1);
		}
	}

	public void drawRect(Vector3f vector3f, float float1, float float2, float float3, float float4) {
		this.drawRect(vector3f, float1, float2, float3, float4, 1.0F, 1.0F, 1.0F);
	}

	public void drawRect(Vector3f vector3f, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		float float8 = vector3f.x;
		float float9 = vector3f.y;
		float float10 = vector3f.z;
		Vector3f vector3f2 = this.tempVec3f_3;
		vector3f.cross(_UNIT_Y, vector3f2);
		float float11 = 1.0F;
		vector3f.x *= float11 * float4;
		vector3f.z *= float11 * float4;
		vector3f2.x *= float11 * float3;
		vector3f2.z *= float11 * float3;
		float float12 = float1 + vector3f.x;
		float float13 = float2 + vector3f.z;
		float float14 = float1 - vector3f.x;
		float float15 = float2 - vector3f.z;
		float float16 = float12 - vector3f2.x / 2.0F;
		float float17 = float12 + vector3f2.x / 2.0F;
		float float18 = float14 - vector3f2.x / 2.0F;
		float float19 = float14 + vector3f2.x / 2.0F;
		float float20 = float15 - vector3f2.z / 2.0F;
		float float21 = float15 + vector3f2.z / 2.0F;
		float float22 = float13 - vector3f2.z / 2.0F;
		float float23 = float13 + vector3f2.z / 2.0F;
		float16 += WorldSimulation.instance.offsetX;
		float22 += WorldSimulation.instance.offsetY;
		float17 += WorldSimulation.instance.offsetX;
		float23 += WorldSimulation.instance.offsetY;
		float18 += WorldSimulation.instance.offsetX;
		float20 += WorldSimulation.instance.offsetY;
		float19 += WorldSimulation.instance.offsetX;
		float21 += WorldSimulation.instance.offsetY;
		LineDrawer.addLine(float16, float22, 0.0F, float17, float23, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float16, float22, 0.0F, float18, float20, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float17, float23, 0.0F, float19, float21, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float18, float20, 0.0F, float19, float21, 0.0F, float5, float6, float7, (String)null, true);
		vector3f.set(float8, float9, float10);
	}

	public void drawCircle(float float1, float float2, float float3) {
		this.drawCircle(float1, float2, float3, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void drawCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		LineDrawer.DrawIsoCircle(float1, float2, 0.0F, float3, 16, float4, float5, float6, float7);
	}

	static  {
		gears[0] = new CarController.GearInfo(0, 25, 0.0F);
		gears[1] = new CarController.GearInfo(25, 50, 0.5F);
		gears[2] = new CarController.GearInfo(50, 1000, 0.5F);
		bulletVariables = new CarController.BulletVariables();
	}

	public static final class ClientControls {
		public float steering;
		public boolean forward;
		public boolean backward;
		public boolean brake;
		public boolean shift;
		public boolean wasUsingParkingBrakes;
		public long forceBrake = 0L;

		public void reset() {
			this.steering = 0.0F;
			this.forward = false;
			this.backward = false;
			this.brake = false;
			this.shift = false;
			this.wasUsingParkingBrakes = false;
			this.forceBrake = 0L;
		}
	}

	public static final class GearInfo {
		int minSpeed;
		int maxSpeed;
		float minRPM;

		GearInfo(int int1, int int2, float float1) {
			this.minSpeed = int1;
			this.maxSpeed = int2;
			this.minRPM = float1;
		}
	}

	static enum ControlState {

		NoControl,
		Braking,
		Forward,
		Reverse;

		private static CarController.ControlState[] $values() {
			return new CarController.ControlState[]{NoControl, Braking, Forward, Reverse};
		}
	}

	public static final class BulletVariables {
		float engineForce;
		float brakingForce;
		float vehicleSteering;
		BaseVehicle vehicle;

		CarController.BulletVariables set(BaseVehicle baseVehicle, float float1, float float2, float float3) {
			this.vehicle = baseVehicle;
			this.engineForce = float1;
			this.brakingForce = float2;
			this.vehicleSteering = float3;
			return this;
		}
	}
}
