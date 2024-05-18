package zombie.core.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.input.Keyboard;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.utils.OnceEvery;
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
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.TransmissionNumber;
import zombie.vehicles.VehicleManager;


public class CarController {
	public BaseVehicle vehicleObject;
	public float clientForce = 0.0F;
	public float EngineForce = 0.0F;
	public float BrakingForce = 0.0F;
	private float VehicleSteering = 0.0F;
	boolean isGas = false;
	boolean isGasR = false;
	boolean isBreak = false;
	private VehicleScript script;
	private final OnceEvery sendEvery = new OnceEvery(0.1F);
	private double sentEngineSpeed = -1.0;
	public boolean isEnable = false;
	private Transform tempXfrm = new Transform();
	private Vector2 tempVec2 = new Vector2();
	private Vector3f tempVec3f = new Vector3f();
	private Vector3f tempVec3f_2 = new Vector3f();
	private Vector3f tempVec3f_3 = new Vector3f();
	private static final Vector3f _UNIT_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	public boolean acceleratorOn = false;
	public boolean brakeOn = false;
	public float speed = 0.0F;
	public static CarController.GearInfo[] gears = new CarController.GearInfo[3];
	public CarController.ClientControls clientControls = new CarController.ClientControls();
	private boolean engineStartingFromKeyboard;
	private static final CarController.BulletVariables bulletVariables;
	float drunkDelayCommandTimer = 0.0F;
	boolean wasBreaking = false;
	boolean wasGas = false;
	boolean wasGasR = false;
	boolean wasSteering = false;
	private static final Matrix4f tempMatrix4f;
	private static final Vector4f tempVector4f;

	public CarController(BaseVehicle baseVehicle) {
		this.vehicleObject = baseVehicle;
		this.script = baseVehicle.getScript();
		this.engineStartingFromKeyboard = false;
		Bullet.addVehicle(baseVehicle.VehicleID, baseVehicle.x, baseVehicle.y, baseVehicle.z, baseVehicle.savedRot.x, baseVehicle.savedRot.y, baseVehicle.savedRot.z, baseVehicle.savedRot.w, this.script.getFullName());
		Bullet.setVehicleStatic(baseVehicle.VehicleID, baseVehicle.netPlayerAuthorization == 4);
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

	public void setClientControls(CarController.ClientControls clientControls) {
		this.clientControls = clientControls;
	}

	public void update() {
		this.speed = this.vehicleObject.getCurrentSpeedKmHour();
		boolean boolean1 = this.vehicleObject.getDriver().HasTrait("SpeedDemon");
		boolean boolean2 = this.vehicleObject.getDriver().HasTrait("SundayDriver");
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
		if (GameClient.bClient) {
			float float3 = this.vehicleObject.jniSpeed / Math.min(120.0F, (float)ServerOptions.instance.SpeedLimit.getValue());
			float3 *= float3;
			float2 = GameTime.getInstance().Lerp(1.0F, BaseVehicle.getFakeSpeedModifier(), float3);
		}

		int int1 = this.vehicleObject.getScript().gearRatioCount;
		float float4 = this.vehicleObject.getCurrentSpeedKmHour() * float2;
		float float5 = 0.0F;
		if (this.vehicleObject.transmissionNumber == TransmissionNumber.R) {
			float5 = this.vehicleObject.getScript().gearRatio[0];
		} else if (this.vehicleObject.transmissionNumber != TransmissionNumber.N) {
			float5 = this.vehicleObject.getScript().gearRatio[this.vehicleObject.transmissionNumber.getIndex()];
		}

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

		if (this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk) > 1 && this.vehicleObject.engineState != BaseVehicle.engineStateTypes.Idle) {
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

		this.wasBreaking = this.isBreak;
		this.wasGas = this.isGas;
		this.wasGasR = this.isGasR;
		this.wasSteering = this.clientControls.steering != 0.0F;
		if (this.vehicleObject.isInvalidChunkAround()) {
			this.isBreak = true;
			this.isGas = false;
			this.isGasR = false;
		}

		float float6 = this.vehicleObject.throttle;
		if (!this.isGas && !this.isGasR) {
			float6 -= GameTime.getInstance().getMultiplier() / 30.0F;
		} else {
			float6 += GameTime.getInstance().getMultiplier() / 30.0F;
		}

		if (float6 < 0.0F) {
			float6 = 0.0F;
		}

		if (float6 > 1.0F) {
			float6 = 1.0F;
		}

		this.vehicleObject.throttle = float6;
		float float7 = GameTime.getInstance().getMultiplier() / 0.8F;
		if (this.isBreak || this.isGas || this.isGasR) {
			UIManager.speedControls.SetCurrentGameSpeed(1);
		}

		BaseVehicle baseVehicle;
		if (!this.isGas && !this.isBreak && !this.isGasR) {
			if (this.vehicleObject.engineSpeed > (double)this.vehicleObject.getScript().getEngineIdleSpeed()) {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= (double)Rand.Next(10, 30);
			} else {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed += (double)Rand.Next(20);
			}

			this.vehicleObject.transmissionNumber = TransmissionNumber.N;
			if (this.EngineForce > 0.0F) {
				this.EngineForce -= 30.0F;
				this.EngineForce = Math.max(0.0F, this.EngineForce);
			} else {
				this.EngineForce += 30.0F;
				this.EngineForce = Math.min(0.0F, this.EngineForce);
			}

			this.EngineForce = 0.0F;
			if (this.vehicleObject.engineSpeed > 1000.0) {
				this.BrakingForce = 15.0F;
			} else {
				this.BrakingForce = 10.0F;
			}
		}

		float float8;
		if (!this.isGas && !this.isBreak && this.isGasR) {
			this.vehicleObject.transmissionNumber = TransmissionNumber.R;
			float8 = 3000.0F * float4 / 30.0F;
			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - (double)float8), 100.0);
			if (boolean1) {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float4));
			} else {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float4));
			}

			this.EngineForce = (float)((double)(-1.0F * (float)this.vehicleObject.getEnginePower()) * (0.75 + this.vehicleObject.engineSpeed / 24000.0));
			if (this.vehicleObject.engineSpeed > 6000.0) {
				this.EngineForce = (float)((double)this.EngineForce * ((7000.0 - this.vehicleObject.engineSpeed) / 1000.0));
			}

			if (boolean2) {
				this.EngineForce *= 0.7F;
				if (float4 < -5.0F) {
					this.EngineForce *= (15.0F + float4) / 10.0F;
				}
			}

			if (float4 < -30.0F) {
				this.EngineForce *= (40.0F + float4) / 10.0F;
			}

			this.BrakingForce = 0.0F;
		}

		if (this.isGas && !this.isBreak && !this.isGasR) {
			float8 = 0.0F;
			if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
				this.vehicleObject.transmissionNumber = TransmissionNumber.Speed1;
				boolean boolean3 = false;
				while (true) {
					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
						float8 = 3000.0F * float4 / 30.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
						float8 = 3000.0F * float4 / 40.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
						float8 = 3000.0F * float4 / 60.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
						float8 = 3000.0F * float4 / 85.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
						float8 = 3000.0F * float4 / 105.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
						float8 = 3000.0F * float4 / 130.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
						float8 = 3000.0F * float4 / 160.0F;
					}

					if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
						float8 = 3000.0F * float4 / 200.0F;
					}

					if (boolean1) {
						if (float8 > 6000.0F) {
							this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(int1));
							boolean3 = true;
						}
					} else if (float8 > 3000.0F) {
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
				float8 = 3000.0F * float4 / 30.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
				float8 = 3000.0F * float4 / 40.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
				float8 = 3000.0F * float4 / 60.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
				float8 = 3000.0F * float4 / 85.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
				float8 = 3000.0F * float4 / 105.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
				float8 = 3000.0F * float4 / 130.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
				float8 = 3000.0F * float4 / 160.0F;
			}

			if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
				float8 = 3000.0F * float4 / 200.0F;
			}

			baseVehicle = this.vehicleObject;
			baseVehicle.engineSpeed -= Math.min(0.5 * (this.vehicleObject.engineSpeed - (double)float8), 100.0);
			if (boolean1) {
				if (float4 < 50.0F) {
					baseVehicle = this.vehicleObject;
					baseVehicle.engineSpeed -= Math.min(0.06 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float4));
				}
			} else if (float4 < 30.0F) {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= Math.min(0.02 * (this.vehicleObject.engineSpeed - 7000.0), (double)(30.0F - float4));
			}

			this.EngineForce = (float)((double)this.vehicleObject.getEnginePower() * (0.5 + this.vehicleObject.engineSpeed / 24000.0));
			this.EngineForce -= this.EngineForce * (float4 / 200.0F);
			if (this.vehicleObject.engineSpeed > 6000.0) {
				this.EngineForce = (float)((double)this.EngineForce * ((7000.0 - this.vehicleObject.engineSpeed) / 1000.0));
			}

			if (boolean2) {
				this.EngineForce *= 0.6F;
				if (float4 > 20.0F) {
					this.EngineForce *= (40.0F - float4) / 20.0F;
				}
			}

			if (boolean1) {
				if (float4 > this.vehicleObject.getMaxSpeed() * 1.15F) {
					this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15F + 20.0F - float4) / 20.0F;
				}
			} else if (float4 > this.vehicleObject.getMaxSpeed()) {
				this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0F - float4) / 20.0F;
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

		if (this.isBreak) {
			if (this.vehicleObject.engineSpeed > (double)this.vehicleObject.getScript().getEngineIdleSpeed()) {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed -= (double)Rand.Next(10, 30);
			} else {
				baseVehicle = this.vehicleObject;
				baseVehicle.engineSpeed += (double)Rand.Next(20);
			}

			this.vehicleObject.transmissionNumber = TransmissionNumber.N;
			this.EngineForce = 0.0F;
			this.BrakingForce = this.vehicleObject.getBrakingForce();
			if (this.clientControls.brake) {
				this.BrakingForce *= 13.0F;
			}

			if (!this.vehicleObject.getStoplightsOn()) {
				if (!GameClient.bClient && !GameServer.bServer) {
					this.vehicleObject.setStoplightsOn(true);
				} else if (GameClient.bClient) {
					GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.TRUE);
				}
			}
		} else if (this.vehicleObject.getStoplightsOn()) {
			if (!GameClient.bClient && !GameServer.bServer) {
				this.vehicleObject.setStoplightsOn(false);
			} else if (GameClient.bClient) {
				GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "setStoplightsOn", "on", Boolean.FALSE);
			}
		}

		if (this.vehicleObject.isEngineRunning() && (float4 < 1.0F && this.EngineForce > this.vehicleObject.getScript().getEngineIdleSpeed() * 2.0F || float4 > -0.5F && this.EngineForce < this.vehicleObject.getScript().getEngineIdleSpeed() * -2.0F)) {
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

		if (Math.abs(this.clientControls.steering) > 0.1F) {
			float8 = 1.0F - this.speed / this.vehicleObject.getMaxSpeed();
			if (float8 < 0.1F) {
				float8 = 0.1F;
			}

			this.VehicleSteering -= (this.clientControls.steering + this.VehicleSteering) * 0.06F * float7 * float8;
		} else if ((double)Math.abs(this.VehicleSteering) <= 0.04) {
			this.VehicleSteering = 0.0F;
		} else if (this.VehicleSteering > 0.0F) {
			this.VehicleSteering -= 0.04F * float7;
		} else {
			this.VehicleSteering += 0.04F * float7;
		}

		if (this.VehicleSteering > this.script.getSteeringClamp(this.speed)) {
			this.VehicleSteering = this.script.getSteeringClamp(this.speed);
		} else if (this.VehicleSteering < -this.script.getSteeringClamp(this.speed)) {
			this.VehicleSteering = -this.script.getSteeringClamp(this.speed);
		}

		CarController.BulletVariables bulletVariables = bulletVariables.set(this.vehicleObject, this.EngineForce, this.BrakingForce, this.VehicleSteering);
		this.checkTire(bulletVariables);
		this.EngineForce = bulletVariables.engineForce;
		this.BrakingForce = bulletVariables.brakingForce;
		this.VehicleSteering = bulletVariables.vehicleSteering;
		int int2;
		if (this.vehicleObject.isDoingOffroad()) {
			int2 = this.vehicleObject.getTransmissionNumber();
			if (int2 <= 0) {
				int2 = 1;
			}

			this.EngineForce = (float)((double)this.EngineForce / ((double)int2 * 1.5));
		}

		this.vehicleObject.setCurrentSteering(this.VehicleSteering);
		this.vehicleObject.setBraking(this.isBreak);
		if (!GameServer.bServer) {
			if (Math.abs(this.EngineForce) > 0.01F && !this.isEnable) {
				Bullet.setVehicleActive(this.vehicleObject.VehicleID, true);
				this.isEnable = true;
			}

			if (this.isEnable && Math.abs(this.EngineForce) < 0.01F && this.vehicleObject.jniSpeed < 0.01F) {
				Bullet.setVehicleActive(this.vehicleObject.VehicleID, false);
				this.isEnable = false;
			}

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
			this.park();
		}

		for (int2 = 0; int2 < this.vehicleObject.getScript().getWheelCount(); ++int2) {
		}

		if (GameClient.bClient) {
			double double1 = this.vehicleObject.isEngineRunning() ? this.vehicleObject.engineSpeed : 0.0;
			if (!this.isGas && !this.isBreak && !this.isGasR && double1 >= 950.0 && double1 <= 1050.0) {
				double1 = 1000.0;
			}

			if (this.sendEvery.Check() && (this.sentEngineSpeed == -1.0 || Math.abs(this.sentEngineSpeed - double1) > 10.0 || this.sentEngineSpeed != 0.0 != (double1 != 0.0))) {
				VehicleManager.instance.sendEngineSound(this.vehicleObject, (float)double1, this.vehicleObject.throttle);
				this.sentEngineSpeed = double1;
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
		float float1;
		if (!GameServer.bServer) {
			boolean boolean1;
			boolean boolean2;
			boolean boolean3;
			boolean boolean4;
			if (this.vehicleObject.isKeyboardControlled()) {
				boolean boolean5 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"));
				boolean2 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"));
				boolean1 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"));
				boolean3 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"));
				boolean4 = Keyboard.isKeyDown(57);
				this.clientControls.steering = 0.0F;
				if (boolean5) {
					--this.clientControls.steering;
				}

				if (boolean2) {
					++this.clientControls.steering;
				}

				this.clientControls.forward = boolean1;
				this.clientControls.backward = boolean3;
				this.clientControls.brake = boolean4;
				if (this.clientControls.brake) {
					this.clientControls.wasUsingParkingBrakes = true;
				}
			}

			int int1 = this.vehicleObject.getJoypad();
			if (int1 != -1) {
				boolean2 = JoypadManager.instance.isLeftPressed(int1);
				boolean1 = JoypadManager.instance.isRightPressed(int1);
				boolean3 = JoypadManager.instance.isRTPressed(int1);
				boolean4 = JoypadManager.instance.isLTPressed(int1);
				boolean boolean6 = JoypadManager.instance.isBPressed(int1);
				float1 = JoypadManager.instance.getMovementAxisX(int1);
				this.clientControls.steering = float1;
				if (boolean2) {
					this.clientControls.steering = -1.0F;
				}

				if (boolean1) {
					this.clientControls.steering = 1.0F;
				}

				this.clientControls.forward = boolean3;
				this.clientControls.backward = boolean4;
				this.clientControls.brake = boolean6;
			}
		} else {
			float float2 = 1.0F;
			float float3 = 0.0F;
			Vector3f vector3f = this.tempVec3f_2;
			this.vehicleObject.getLinearVelocity(vector3f);
			vector3f.y = 0.0F;
			if ((double)vector3f.length() > 0.5) {
				vector3f.normalize();
				Vector3f vector3f2 = this.tempVec3f;
				this.vehicleObject.getForwardVector(vector3f2);
				float3 = vector3f.dot(vector3f2);
			}

			int int2 = this.vehicleObject.getJoypad();
			if (int2 == -1) {
				this.EngineForce = 0.0F;
				this.BrakingForce = 0.0F;
				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"))) {
					if (float3 <= 0.0F) {
						this.BrakingForce = this.vehicleObject.getBrakingForce() * float2;
					}

					this.EngineForce = float3 >= 0.0F ? (float)this.vehicleObject.getEnginePower() * float2 : 0.0F;
				}

				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"))) {
					this.BrakingForce = float3 >= 0.0F ? this.vehicleObject.getBrakingForce() * float2 : 0.0F;
					this.EngineForce = float3 <= 0.0F ? (float)(-this.vehicleObject.getEnginePower()) * float2 : 0.0F;
				}

				if (Keyboard.isKeyDown(57)) {
					this.BrakingForce = this.vehicleObject.getBrakingForce() * float2;
					this.EngineForce = 0.0F;
				}

				if (this.EngineForce != 0.0F && !this.vehicleObject.isEngineRunning() && this.vehicleObject.isEngineWorking()) {
					LuaEventManager.triggerEvent("OnUseVehicle", this.vehicleObject.getCharacter(0), this.vehicleObject, true);
				}

				this.accelerator(this.EngineForce != 0.0F);
				float float4 = 0.0F;
				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"))) {
					float4 = -1.0F;
				}

				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"))) {
					float4 = 1.0F;
				}

				if (Math.abs(float4) > 0.2F) {
					this.VehicleSteering -= float4 * 0.04F * float2;
					if (this.VehicleSteering > this.script.getSteeringClamp(this.speed)) {
						this.VehicleSteering = this.script.getSteeringClamp(this.speed);
					} else if (this.VehicleSteering < -this.script.getSteeringClamp(this.speed)) {
						this.VehicleSteering = -this.script.getSteeringClamp(this.speed);
					}
				} else if (this.VehicleSteering > 0.0F) {
					this.VehicleSteering -= this.script.getSteeringIncrement() * 6.0F * float2;
					if (this.VehicleSteering < 0.0F) {
						this.VehicleSteering = 0.0F;
					}
				} else if (this.VehicleSteering < 0.0F) {
					this.VehicleSteering += this.script.getSteeringIncrement() * 6.0F * float2;
					if (this.VehicleSteering > 0.0F) {
						this.VehicleSteering = 0.0F;
					}
				}
			} else {
				Vector2 vector2 = this.tempVec2;
				vector2.x = 0.0F;
				vector2.y = 0.0F;
				float float5 = JoypadManager.instance.getMovementAxisX(int2);
				float1 = JoypadManager.instance.getMovementAxisY(int2);
				float float6 = JoypadManager.instance.getDeadZone(int2, 0);
				IsoGameCharacter gameCharacter = this.vehicleObject.getCharacter(0);
				this.EngineForce = 0.0F;
				this.BrakingForce = 0.0F;
				float float7 = 0.0F;
				float float8 = 0.0F;
				if (Math.abs(float1) > float6 || Math.abs(float5) > float6) {
					vector2.x = float5;
					vector2.y = float1;
					vector2.normalize();
					vector2.rotate(-0.7853982F);
					Vector3f vector3f3 = this.tempVec3f;
					this.vehicleObject.getForwardVector(vector3f3);
					float8 = Vector2.dot(vector2.x, vector2.y, vector3f3.x, vector3f3.z);
					float7 = (float)((Math.atan2((double)vector2.y, (double)vector2.x) - Math.atan2((double)vector3f3.z, (double)vector3f3.x)) * 57.29577951308232);
					if (float7 < 0.0F) {
						float7 += 360.0F;
					}

					if (float3 >= 0.0F) {
						if (float7 < 180.0F) {
							if (float7 > 90.0F) {
								float8 = 0.0F;
							}

							float7 = 90.0F;
						} else {
							if (float7 < 270.0F) {
								float8 = 0.0F;
							}

							float7 = 270.0F;
						}
					} else if (float7 >= 180.0F) {
						if (float7 > 270.0F) {
							float8 = 0.0F;
						}

						float7 = 270.0F;
					} else {
						if (float7 < 90.0F) {
							float8 = 0.0F;
						}

						float7 = 90.0F;
					}
				}

				if (JoypadManager.instance.isRTPressed(int2)) {
					if (float3 <= 0.0F) {
						this.BrakingForce = this.vehicleObject.getBrakingForce() * float2;
					}

					this.EngineForce = float3 >= 0.0F ? (float)this.vehicleObject.getEnginePower() * float2 : 0.0F;
				}

				if (JoypadManager.instance.isLTPressed(int2)) {
					this.BrakingForce = float3 >= 0.0F ? this.vehicleObject.getBrakingForce() * float2 : 0.0F;
					this.EngineForce = float3 <= 0.0F ? (float)(-this.vehicleObject.getEnginePower()) * float2 : 0.0F;
				}

				if (JoypadManager.instance.isBPressed(int2)) {
					this.EngineForce = 0.0F;
					this.BrakingForce = this.vehicleObject.getBrakingForce() * float2;
				}

				if (this.EngineForce != 0.0F && !this.vehicleObject.isEngineRunning() && this.vehicleObject.isEngineWorking()) {
					LuaEventManager.triggerEvent("OnUseVehicle", this.vehicleObject.getCharacter(0), this.vehicleObject, true);
				}

				this.accelerator(this.EngineForce != 0.0F);
				float float9 = 0.0F;
				if (float7 > 0.0F && float7 < 180.0F) {
					float9 = GameTime.instance.Lerp(0.0F, 1.0F, float7 / 180.0F);
				} else if (float7 >= 180.0F) {
					float9 = -GameTime.instance.Lerp(0.0F, 1.0F, (float7 - 180.0F) / 180.0F);
				}

				if (Math.abs(float9) > 0.1F) {
					this.VehicleSteering -= float9 * 0.04F * float2;
					float float10 = this.script.getSteeringClamp(this.speed) * (1.0F - Math.abs(float8));
					float float11 = Math.abs(this.vehicleObject.getCurrentSpeedKmHour());
					if (float11 < 20.0F) {
						float10 *= Math.min(20.0F / float11, 2.0F);
					}

					if (this.VehicleSteering > float10) {
						this.VehicleSteering = float10;
					} else if (this.VehicleSteering < -float10) {
						this.VehicleSteering = -float10;
					}
				} else if (this.VehicleSteering > 0.0F) {
					this.VehicleSteering -= this.script.getSteeringIncrement() * 6.0F * float2;
					if (this.VehicleSteering < 0.0F) {
						this.VehicleSteering = 0.0F;
					}
				} else if (this.VehicleSteering < 0.0F) {
					this.VehicleSteering += this.script.getSteeringIncrement() * 6.0F * float2;
					if (this.VehicleSteering > 0.0F) {
						this.VehicleSteering = 0.0F;
					}
				}
			}
		}
	}

	public void render() {
	}

	public void park() {
		if (this.vehicleObject.getScript().getWheelCount() == 4) {
			Bullet.controlVehicle(this.vehicleObject.VehicleID, 0.0F, this.vehicleObject.getBrakingForce(), 0.0F);
		}
	}

	public void debug() {
		if (Core.bDebug && DebugOptions.instance.VehicleRenderOutline.getValue()) {
			Vector3f vector3f = this.tempVec3f;
			this.vehicleObject.getForwardVector(vector3f);
			Transform transform = this.tempXfrm;
			this.vehicleObject.getWorldTransform(transform);
			PolygonalMap2.VehiclePoly vehiclePoly = this.vehicleObject.getPoly();
			LineDrawer.addLine(vehiclePoly.x1, vehiclePoly.y1, 0.0F, vehiclePoly.x2, vehiclePoly.y2, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x2, vehiclePoly.y2, 0.0F, vehiclePoly.x3, vehiclePoly.y3, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x3, vehiclePoly.y3, 0.0F, vehiclePoly.x4, vehiclePoly.y4, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			LineDrawer.addLine(vehiclePoly.x4, vehiclePoly.y4, 0.0F, vehiclePoly.x1, vehiclePoly.y1, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
			this.drawRect(vector3f, transform.origin.x, transform.origin.z, this.script.getExtents().x, this.script.getExtents().z / 2.0F);
			_UNIT_Y.set(0.0F, 1.0F, 0.0F);
			float float1;
			float float2;
			int int1;
			for (int1 = 0; int1 < this.vehicleObject.getScript().getWheelCount(); ++int1) {
				VehicleScript.Wheel wheel = this.script.getWheel(int1);
				this.tempVec3f.set(wheel.offset.x, wheel.offset.y, wheel.offset.z);
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
			if (int1 != -1) {
				float float3 = JoypadManager.instance.getMovementAxisX(int1);
				float1 = JoypadManager.instance.getMovementAxisY(int1);
				float2 = JoypadManager.instance.getDeadZone(int1, 0);
				if (Math.abs(float1) > float2 || Math.abs(float3) > float2) {
					Vector2 vector2 = this.tempVec2.set(float3, float1);
					vector2.setLength(4.0F);
					vector2.rotate(-0.7853982F);
					LineDrawer.addLine(this.vehicleObject.getX(), this.vehicleObject.getY(), this.vehicleObject.z, this.vehicleObject.getX() + vector2.x, this.vehicleObject.getY() + vector2.y, this.vehicleObject.z, 1.0F, 1.0F, 1.0F, (String)null, true);
				}
			}
		}
	}

	public void drawRect(Vector3f vector3f, float float1, float float2, float float3, float float4) {
		this.drawRect(vector3f, float1, float2, float3, float4, 1.0F, 1.0F, 1.0F);
	}

	public void drawRect(Vector3f vector3f, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		Vector3f vector3f2 = this.tempVec3f_3;
		vector3f.cross(_UNIT_Y, vector3f2);
		float float8 = 1.0F;
		vector3f.x *= float8 * float4;
		vector3f.z *= float8 * float4;
		vector3f2.x *= float8 * float3;
		vector3f2.z *= float8 * float3;
		float float9 = float1 + vector3f.x;
		float float10 = float2 + vector3f.z;
		float float11 = float1 - vector3f.x;
		float float12 = float2 - vector3f.z;
		float float13 = float9 - vector3f2.x / 2.0F;
		float float14 = float9 + vector3f2.x / 2.0F;
		float float15 = float11 - vector3f2.x / 2.0F;
		float float16 = float11 + vector3f2.x / 2.0F;
		float float17 = float12 - vector3f2.z / 2.0F;
		float float18 = float12 + vector3f2.z / 2.0F;
		float float19 = float10 - vector3f2.z / 2.0F;
		float float20 = float10 + vector3f2.z / 2.0F;
		float13 += WorldSimulation.instance.offsetX;
		float19 += WorldSimulation.instance.offsetY;
		float14 += WorldSimulation.instance.offsetX;
		float20 += WorldSimulation.instance.offsetY;
		float15 += WorldSimulation.instance.offsetX;
		float17 += WorldSimulation.instance.offsetY;
		float16 += WorldSimulation.instance.offsetX;
		float18 += WorldSimulation.instance.offsetY;
		LineDrawer.addLine(float13, float19, 0.0F, float14, float20, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float13, float19, 0.0F, float15, float17, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float14, float20, 0.0F, float16, float18, 0.0F, float5, float6, float7, (String)null, true);
		LineDrawer.addLine(float15, float17, 0.0F, float16, float18, 0.0F, float5, float6, float7, (String)null, true);
	}

	public void drawCircle(float float1, float float2, float float3) {
		this.drawCircle(float1, float2, float3, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void drawCircle(float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
		double double1 = (double)float1 + (double)float3 * Math.cos(Math.toRadians(0.0));
		double double2 = (double)float2 + (double)float3 * Math.sin(Math.toRadians(0.0));
		for (int int1 = 1; int1 <= 16; ++int1) {
			double double3 = (double)float1 + (double)float3 * Math.cos(Math.toRadians((double)(int1 * 360 / 16)));
			double double4 = (double)float2 + (double)float3 * Math.sin(Math.toRadians((double)(int1 * 360 / 16)));
			LineDrawer.addLine((float)double1, (float)double2, 0.0F, (float)double3, (float)double4, 0.0F, float4, float5, float6, (String)null, true);
			double1 = double3;
			double2 = double4;
		}
	}

	static  {
		gears[0] = new CarController.GearInfo(0, 25, 0.0F);
		gears[1] = new CarController.GearInfo(25, 50, 0.5F);
		gears[2] = new CarController.GearInfo(50, 1000, 0.5F);
		bulletVariables = new CarController.BulletVariables();
		tempMatrix4f = new Matrix4f();
		tempVector4f = new Vector4f();
	}

	public class ClientControls {
		public float steering;
		public boolean forward;
		public boolean backward;
		public boolean brake;
		public boolean wasUsingParkingBrakes;
	}

	public static class GearInfo {
		int minSpeed;
		int maxSpeed;
		float minRPM;

		GearInfo(int int1, int int2, float float1) {
			this.minSpeed = int1;
			this.maxSpeed = int2;
			this.minRPM = float1;
		}
	}

	public static class BulletVariables {
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
