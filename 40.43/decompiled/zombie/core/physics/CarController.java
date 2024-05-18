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
   private double sentEngineSpeed = -1.0D;
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

   public CarController(BaseVehicle var1) {
      this.vehicleObject = var1;
      this.script = var1.getScript();
      this.engineStartingFromKeyboard = false;
      Bullet.addVehicle(var1.VehicleID, var1.x, var1.y, var1.z, var1.savedRot.x, var1.savedRot.y, var1.savedRot.z, var1.savedRot.w, this.script.getFullName());
      Bullet.setVehicleStatic(var1.VehicleID, var1.netPlayerAuthorization == 4);
   }

   public CarController.GearInfo findGear(float var1) {
      for(int var2 = 0; var2 < gears.length; ++var2) {
         if (var1 >= (float)gears[var2].minSpeed && var1 < (float)gears[var2].maxSpeed) {
            return gears[var2];
         }
      }

      return null;
   }

   public void accelerator(boolean var1) {
      this.acceleratorOn = var1;
   }

   public void brake(boolean var1) {
      this.brakeOn = var1;
   }

   public CarController.ClientControls getClientControls() {
      return this.clientControls;
   }

   public void setClientControls(CarController.ClientControls var1) {
      this.clientControls = var1;
   }

   public void update() {
      this.speed = this.vehicleObject.getCurrentSpeedKmHour();
      boolean var1 = this.vehicleObject.getDriver().HasTrait("SpeedDemon");
      boolean var2 = this.vehicleObject.getDriver().HasTrait("SundayDriver");
      float var3 = 0.0F;
      Vector3f var4 = this.vehicleObject.getLinearVelocity(this.tempVec3f_2);
      var4.y = 0.0F;
      if ((double)var4.length() > 0.5D) {
         var4.normalize();
         Vector3f var5 = this.tempVec3f;
         this.vehicleObject.getForwardVector(var5);
         var3 = var4.dot(var5);
      }

      float var14 = 1.0F;
      if (GameClient.bClient) {
         float var6 = this.vehicleObject.jniSpeed / Math.min(120.0F, (float)ServerOptions.instance.SpeedLimit.getValue());
         var6 *= var6;
         var14 = GameTime.getInstance().Lerp(1.0F, BaseVehicle.getFakeSpeedModifier(), var6);
      }

      int var17 = this.vehicleObject.getScript().gearRatioCount;
      float var7 = this.vehicleObject.getCurrentSpeedKmHour() * var14;
      float var8 = 0.0F;
      if (this.vehicleObject.transmissionNumber == TransmissionNumber.R) {
         var8 = this.vehicleObject.getScript().gearRatio[0];
      } else if (this.vehicleObject.transmissionNumber != TransmissionNumber.N) {
         var8 = this.vehicleObject.getScript().gearRatio[this.vehicleObject.transmissionNumber.getIndex()];
      }

      this.isGas = false;
      this.isGasR = false;
      this.isBreak = false;
      if (this.clientControls.forward) {
         if (var3 < 0.0F) {
            this.isBreak = true;
         }

         if (var3 >= 0.0F) {
            this.isGas = true;
         }

         this.isGasR = false;
      }

      if (this.clientControls.backward) {
         if (var3 > 0.0F) {
            this.isBreak = true;
         }

         if (var3 <= 0.0F) {
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

      float var9 = this.vehicleObject.throttle;
      if (!this.isGas && !this.isGasR) {
         var9 -= GameTime.getInstance().getMultiplier() / 30.0F;
      } else {
         var9 += GameTime.getInstance().getMultiplier() / 30.0F;
      }

      if (var9 < 0.0F) {
         var9 = 0.0F;
      }

      if (var9 > 1.0F) {
         var9 = 1.0F;
      }

      this.vehicleObject.throttle = var9;
      float var10 = GameTime.getInstance().getMultiplier() / 0.8F;
      if (this.isBreak || this.isGas || this.isGasR) {
         UIManager.speedControls.SetCurrentGameSpeed(1);
      }

      BaseVehicle var10000;
      if (!this.isGas && !this.isBreak && !this.isGasR) {
         if (this.vehicleObject.engineSpeed > (double)this.vehicleObject.getScript().getEngineIdleSpeed()) {
            var10000 = this.vehicleObject;
            var10000.engineSpeed -= (double)Rand.Next(10, 30);
         } else {
            var10000 = this.vehicleObject;
            var10000.engineSpeed += (double)Rand.Next(20);
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
         if (this.vehicleObject.engineSpeed > 1000.0D) {
            this.BrakingForce = 15.0F;
         } else {
            this.BrakingForce = 10.0F;
         }
      }

      float var11;
      if (!this.isGas && !this.isBreak && this.isGasR) {
         this.vehicleObject.transmissionNumber = TransmissionNumber.R;
         var11 = 3000.0F * var7 / 30.0F;
         var10000 = this.vehicleObject;
         var10000.engineSpeed -= Math.min(0.5D * (this.vehicleObject.engineSpeed - (double)var11), 100.0D);
         if (var1) {
            var10000 = this.vehicleObject;
            var10000.engineSpeed -= Math.min(0.06D * (this.vehicleObject.engineSpeed - 7000.0D), (double)(30.0F - var7));
         } else {
            var10000 = this.vehicleObject;
            var10000.engineSpeed -= Math.min(0.02D * (this.vehicleObject.engineSpeed - 7000.0D), (double)(30.0F - var7));
         }

         this.EngineForce = (float)((double)(-1.0F * (float)this.vehicleObject.getEnginePower()) * (0.75D + this.vehicleObject.engineSpeed / 24000.0D));
         if (this.vehicleObject.engineSpeed > 6000.0D) {
            this.EngineForce = (float)((double)this.EngineForce * ((7000.0D - this.vehicleObject.engineSpeed) / 1000.0D));
         }

         if (var2) {
            this.EngineForce *= 0.7F;
            if (var7 < -5.0F) {
               this.EngineForce *= (15.0F + var7) / 10.0F;
            }
         }

         if (var7 < -30.0F) {
            this.EngineForce *= (40.0F + var7) / 10.0F;
         }

         this.BrakingForce = 0.0F;
      }

      if (this.isGas && !this.isBreak && !this.isGasR) {
         var11 = 0.0F;
         if (this.vehicleObject.transmissionNumber == TransmissionNumber.N) {
            this.vehicleObject.transmissionNumber = TransmissionNumber.Speed1;
            boolean var12 = false;

            while(true) {
               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
                  var11 = 3000.0F * var7 / 30.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
                  var11 = 3000.0F * var7 / 40.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
                  var11 = 3000.0F * var7 / 60.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
                  var11 = 3000.0F * var7 / 85.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
                  var11 = 3000.0F * var7 / 105.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
                  var11 = 3000.0F * var7 / 130.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
                  var11 = 3000.0F * var7 / 160.0F;
               }

               if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
                  var11 = 3000.0F * var7 / 200.0F;
               }

               if (var1) {
                  if (var11 > 6000.0F) {
                     this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(var17));
                     var12 = true;
                  }
               } else if (var11 > 3000.0F) {
                  this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(var17));
                  var12 = true;
               }

               if (!var12 || this.vehicleObject.transmissionNumber.getIndex() >= var17) {
                  break;
               }

               var12 = false;
            }
         }

         if (var1) {
            if (this.vehicleObject.engineSpeed > 6000.0D && this.vehicleObject.transmissionChangeTime.Check()) {
               this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(var17));
            }
         } else if (this.vehicleObject.engineSpeed > 3000.0D && this.vehicleObject.transmissionChangeTime.Check()) {
            this.vehicleObject.changeTransmission(this.vehicleObject.transmissionNumber.getNext(var17));
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed1) {
            var11 = 3000.0F * var7 / 30.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed2) {
            var11 = 3000.0F * var7 / 40.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed3) {
            var11 = 3000.0F * var7 / 60.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed4) {
            var11 = 3000.0F * var7 / 85.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed5) {
            var11 = 3000.0F * var7 / 105.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed6) {
            var11 = 3000.0F * var7 / 130.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed7) {
            var11 = 3000.0F * var7 / 160.0F;
         }

         if (this.vehicleObject.transmissionNumber == TransmissionNumber.Speed8) {
            var11 = 3000.0F * var7 / 200.0F;
         }

         var10000 = this.vehicleObject;
         var10000.engineSpeed -= Math.min(0.5D * (this.vehicleObject.engineSpeed - (double)var11), 100.0D);
         if (var1) {
            if (var7 < 50.0F) {
               var10000 = this.vehicleObject;
               var10000.engineSpeed -= Math.min(0.06D * (this.vehicleObject.engineSpeed - 7000.0D), (double)(30.0F - var7));
            }
         } else if (var7 < 30.0F) {
            var10000 = this.vehicleObject;
            var10000.engineSpeed -= Math.min(0.02D * (this.vehicleObject.engineSpeed - 7000.0D), (double)(30.0F - var7));
         }

         this.EngineForce = (float)((double)this.vehicleObject.getEnginePower() * (0.5D + this.vehicleObject.engineSpeed / 24000.0D));
         this.EngineForce -= this.EngineForce * (var7 / 200.0F);
         if (this.vehicleObject.engineSpeed > 6000.0D) {
            this.EngineForce = (float)((double)this.EngineForce * ((7000.0D - this.vehicleObject.engineSpeed) / 1000.0D));
         }

         if (var2) {
            this.EngineForce *= 0.6F;
            if (var7 > 20.0F) {
               this.EngineForce *= (40.0F - var7) / 20.0F;
            }
         }

         if (var1) {
            if (var7 > this.vehicleObject.getMaxSpeed() * 1.15F) {
               this.EngineForce *= (this.vehicleObject.getMaxSpeed() * 1.15F + 20.0F - var7) / 20.0F;
            }
         } else if (var7 > this.vehicleObject.getMaxSpeed()) {
            this.EngineForce *= (this.vehicleObject.getMaxSpeed() + 20.0F - var7) / 20.0F;
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
            var10000 = this.vehicleObject;
            var10000.engineSpeed -= (double)Rand.Next(10, 30);
         } else {
            var10000 = this.vehicleObject;
            var10000.engineSpeed += (double)Rand.Next(20);
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

      if (this.vehicleObject.isEngineRunning() && (var7 < 1.0F && this.EngineForce > this.vehicleObject.getScript().getEngineIdleSpeed() * 2.0F || var7 > -0.5F && this.EngineForce < this.vehicleObject.getScript().getEngineIdleSpeed() * -2.0F)) {
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
         var11 = 1.0F - this.speed / this.vehicleObject.getMaxSpeed();
         if (var11 < 0.1F) {
            var11 = 0.1F;
         }

         this.VehicleSteering -= (this.clientControls.steering + this.VehicleSteering) * 0.06F * var10 * var11;
      } else if ((double)Math.abs(this.VehicleSteering) <= 0.04D) {
         this.VehicleSteering = 0.0F;
      } else if (this.VehicleSteering > 0.0F) {
         this.VehicleSteering -= 0.04F * var10;
      } else {
         this.VehicleSteering += 0.04F * var10;
      }

      if (this.VehicleSteering > this.script.getSteeringClamp(this.speed)) {
         this.VehicleSteering = this.script.getSteeringClamp(this.speed);
      } else if (this.VehicleSteering < -this.script.getSteeringClamp(this.speed)) {
         this.VehicleSteering = -this.script.getSteeringClamp(this.speed);
      }

      CarController.BulletVariables var18 = bulletVariables.set(this.vehicleObject, this.EngineForce, this.BrakingForce, this.VehicleSteering);
      this.checkTire(var18);
      this.EngineForce = var18.engineForce;
      this.BrakingForce = var18.brakingForce;
      this.VehicleSteering = var18.vehicleSteering;
      int var13;
      if (this.vehicleObject.isDoingOffroad()) {
         var13 = this.vehicleObject.getTransmissionNumber();
         if (var13 <= 0) {
            var13 = 1;
         }

         this.EngineForce = (float)((double)this.EngineForce / ((double)var13 * 1.5D));
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
               Boolean var15 = this.vehicleObject.getDriver().getInventory().haveThisKeyId(this.vehicleObject.getKeyId()) != null ? Boolean.TRUE : Boolean.FALSE;
               GameClient.instance.sendClientCommandV((IsoPlayer)this.vehicleObject.getDriver(), "vehicle", "startEngine", "haveKey", var15);
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

      for(var13 = 0; var13 < this.vehicleObject.getScript().getWheelCount(); ++var13) {
      }

      if (GameClient.bClient) {
         double var16 = this.vehicleObject.isEngineRunning() ? this.vehicleObject.engineSpeed : 0.0D;
         if (!this.isGas && !this.isBreak && !this.isGasR && var16 >= 950.0D && var16 <= 1050.0D) {
            var16 = 1000.0D;
         }

         if (this.sendEvery.Check() && (this.sentEngineSpeed == -1.0D || Math.abs(this.sentEngineSpeed - var16) > 10.0D || this.sentEngineSpeed != 0.0D != (var16 != 0.0D))) {
            VehicleManager.instance.sendEngineSound(this.vehicleObject, (float)var16, this.vehicleObject.throttle);
            this.sentEngineSpeed = var16;
         }
      }

   }

   private boolean delayCommandWhileDrunk(boolean var1) {
      this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
      if ((float)Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
         this.drunkDelayCommandTimer = 0.0F;
         return true;
      } else {
         return false;
      }
   }

   private float delayCommandWhileDrunk(float var1) {
      this.drunkDelayCommandTimer += GameTime.getInstance().getMultiplier();
      if ((float)Rand.AdjustForFramerate(4 * this.vehicleObject.getDriver().getMoodles().getMoodleLevel(MoodleType.Drunk)) < this.drunkDelayCommandTimer) {
         this.drunkDelayCommandTimer = 0.0F;
         return var1;
      } else {
         return 0.0F;
      }
   }

   private void checkTire(CarController.BulletVariables var1) {
      if (this.vehicleObject.getPartById("TireFrontLeft") == null || this.vehicleObject.getPartById("TireFrontLeft").getInventoryItem() == null) {
         var1.brakingForce = (float)((double)var1.brakingForce / 1.2D);
         var1.engineForce = (float)((double)var1.engineForce / 1.2D);
      }

      if (this.vehicleObject.getPartById("TireFrontRight") == null || this.vehicleObject.getPartById("TireFrontRight").getInventoryItem() == null) {
         var1.brakingForce = (float)((double)var1.brakingForce / 1.2D);
         var1.engineForce = (float)((double)var1.engineForce / 1.2D);
      }

      if (this.vehicleObject.getPartById("TireRearLeft") == null || this.vehicleObject.getPartById("TireRearLeft").getInventoryItem() == null) {
         var1.brakingForce = (float)((double)var1.brakingForce / 1.3D);
         var1.engineForce = (float)((double)var1.engineForce / 1.3D);
      }

      if (this.vehicleObject.getPartById("TireRearRight") == null || this.vehicleObject.getPartById("TireRearRight").getInventoryItem() == null) {
         var1.brakingForce = (float)((double)var1.brakingForce / 1.3D);
         var1.engineForce = (float)((double)var1.engineForce / 1.3D);
      }

   }

   public void updateControls() {
      float var7;
      if (!GameServer.bServer) {
         boolean var17;
         boolean var18;
         boolean var20;
         boolean var23;
         if (this.vehicleObject.isKeyboardControlled()) {
            boolean var15 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"));
            var18 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"));
            var17 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"));
            var20 = GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"));
            var23 = Keyboard.isKeyDown(57);
            this.clientControls.steering = 0.0F;
            if (var15) {
               --this.clientControls.steering;
            }

            if (var18) {
               ++this.clientControls.steering;
            }

            this.clientControls.forward = var17;
            this.clientControls.backward = var20;
            this.clientControls.brake = var23;
            if (this.clientControls.brake) {
               this.clientControls.wasUsingParkingBrakes = true;
            }
         }

         int var16 = this.vehicleObject.getJoypad();
         if (var16 != -1) {
            var18 = JoypadManager.instance.isLeftPressed(var16);
            var17 = JoypadManager.instance.isRightPressed(var16);
            var20 = JoypadManager.instance.isRTPressed(var16);
            var23 = JoypadManager.instance.isLTPressed(var16);
            boolean var22 = JoypadManager.instance.isBPressed(var16);
            var7 = JoypadManager.instance.getMovementAxisX(var16);
            this.clientControls.steering = var7;
            if (var18) {
               this.clientControls.steering = -1.0F;
            }

            if (var17) {
               this.clientControls.steering = 1.0F;
            }

            this.clientControls.forward = var20;
            this.clientControls.backward = var23;
            this.clientControls.brake = var22;
         }

      } else {
         float var1 = 1.0F;
         float var2 = 0.0F;
         Vector3f var3 = this.tempVec3f_2;
         this.vehicleObject.getLinearVelocity(var3);
         var3.y = 0.0F;
         if ((double)var3.length() > 0.5D) {
            var3.normalize();
            Vector3f var4 = this.tempVec3f;
            this.vehicleObject.getForwardVector(var4);
            var2 = var3.dot(var4);
         }

         int var19 = this.vehicleObject.getJoypad();
         if (var19 == -1) {
            this.EngineForce = 0.0F;
            this.BrakingForce = 0.0F;
            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Forward"))) {
               if (var2 <= 0.0F) {
                  this.BrakingForce = this.vehicleObject.getBrakingForce() * var1;
               }

               this.EngineForce = var2 >= 0.0F ? (float)this.vehicleObject.getEnginePower() * var1 : 0.0F;
            }

            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Backward"))) {
               this.BrakingForce = var2 >= 0.0F ? this.vehicleObject.getBrakingForce() * var1 : 0.0F;
               this.EngineForce = var2 <= 0.0F ? (float)(-this.vehicleObject.getEnginePower()) * var1 : 0.0F;
            }

            if (Keyboard.isKeyDown(57)) {
               this.BrakingForce = this.vehicleObject.getBrakingForce() * var1;
               this.EngineForce = 0.0F;
            }

            if (this.EngineForce != 0.0F && !this.vehicleObject.isEngineRunning() && this.vehicleObject.isEngineWorking()) {
               LuaEventManager.triggerEvent("OnUseVehicle", this.vehicleObject.getCharacter(0), this.vehicleObject, true);
            }

            this.accelerator(this.EngineForce != 0.0F);
            float var21 = 0.0F;
            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Left"))) {
               var21 = -1.0F;
            }

            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Right"))) {
               var21 = 1.0F;
            }

            if (Math.abs(var21) > 0.2F) {
               this.VehicleSteering -= var21 * 0.04F * var1;
               if (this.VehicleSteering > this.script.getSteeringClamp(this.speed)) {
                  this.VehicleSteering = this.script.getSteeringClamp(this.speed);
               } else if (this.VehicleSteering < -this.script.getSteeringClamp(this.speed)) {
                  this.VehicleSteering = -this.script.getSteeringClamp(this.speed);
               }
            } else if (this.VehicleSteering > 0.0F) {
               this.VehicleSteering -= this.script.getSteeringIncrement() * 6.0F * var1;
               if (this.VehicleSteering < 0.0F) {
                  this.VehicleSteering = 0.0F;
               }
            } else if (this.VehicleSteering < 0.0F) {
               this.VehicleSteering += this.script.getSteeringIncrement() * 6.0F * var1;
               if (this.VehicleSteering > 0.0F) {
                  this.VehicleSteering = 0.0F;
               }
            }

         } else {
            Vector2 var5 = this.tempVec2;
            var5.x = 0.0F;
            var5.y = 0.0F;
            float var6 = JoypadManager.instance.getMovementAxisX(var19);
            var7 = JoypadManager.instance.getMovementAxisY(var19);
            float var8 = JoypadManager.instance.getDeadZone(var19, 0);
            IsoGameCharacter var9 = this.vehicleObject.getCharacter(0);
            this.EngineForce = 0.0F;
            this.BrakingForce = 0.0F;
            float var10 = 0.0F;
            float var11 = 0.0F;
            if (Math.abs(var7) > var8 || Math.abs(var6) > var8) {
               var5.x = var6;
               var5.y = var7;
               var5.normalize();
               var5.rotate(-0.7853982F);
               Vector3f var12 = this.tempVec3f;
               this.vehicleObject.getForwardVector(var12);
               var11 = Vector2.dot(var5.x, var5.y, var12.x, var12.z);
               var10 = (float)((Math.atan2((double)var5.y, (double)var5.x) - Math.atan2((double)var12.z, (double)var12.x)) * 57.29577951308232D);
               if (var10 < 0.0F) {
                  var10 += 360.0F;
               }

               if (var2 >= 0.0F) {
                  if (var10 < 180.0F) {
                     if (var10 > 90.0F) {
                        var11 = 0.0F;
                     }

                     var10 = 90.0F;
                  } else {
                     if (var10 < 270.0F) {
                        var11 = 0.0F;
                     }

                     var10 = 270.0F;
                  }
               } else if (var10 >= 180.0F) {
                  if (var10 > 270.0F) {
                     var11 = 0.0F;
                  }

                  var10 = 270.0F;
               } else {
                  if (var10 < 90.0F) {
                     var11 = 0.0F;
                  }

                  var10 = 90.0F;
               }
            }

            if (JoypadManager.instance.isRTPressed(var19)) {
               if (var2 <= 0.0F) {
                  this.BrakingForce = this.vehicleObject.getBrakingForce() * var1;
               }

               this.EngineForce = var2 >= 0.0F ? (float)this.vehicleObject.getEnginePower() * var1 : 0.0F;
            }

            if (JoypadManager.instance.isLTPressed(var19)) {
               this.BrakingForce = var2 >= 0.0F ? this.vehicleObject.getBrakingForce() * var1 : 0.0F;
               this.EngineForce = var2 <= 0.0F ? (float)(-this.vehicleObject.getEnginePower()) * var1 : 0.0F;
            }

            if (JoypadManager.instance.isBPressed(var19)) {
               this.EngineForce = 0.0F;
               this.BrakingForce = this.vehicleObject.getBrakingForce() * var1;
            }

            if (this.EngineForce != 0.0F && !this.vehicleObject.isEngineRunning() && this.vehicleObject.isEngineWorking()) {
               LuaEventManager.triggerEvent("OnUseVehicle", this.vehicleObject.getCharacter(0), this.vehicleObject, true);
            }

            this.accelerator(this.EngineForce != 0.0F);
            float var24 = 0.0F;
            if (var10 > 0.0F && var10 < 180.0F) {
               var24 = GameTime.instance.Lerp(0.0F, 1.0F, var10 / 180.0F);
            } else if (var10 >= 180.0F) {
               var24 = -GameTime.instance.Lerp(0.0F, 1.0F, (var10 - 180.0F) / 180.0F);
            }

            if (Math.abs(var24) > 0.1F) {
               this.VehicleSteering -= var24 * 0.04F * var1;
               float var13 = this.script.getSteeringClamp(this.speed) * (1.0F - Math.abs(var11));
               float var14 = Math.abs(this.vehicleObject.getCurrentSpeedKmHour());
               if (var14 < 20.0F) {
                  var13 *= Math.min(20.0F / var14, 2.0F);
               }

               if (this.VehicleSteering > var13) {
                  this.VehicleSteering = var13;
               } else if (this.VehicleSteering < -var13) {
                  this.VehicleSteering = -var13;
               }
            } else if (this.VehicleSteering > 0.0F) {
               this.VehicleSteering -= this.script.getSteeringIncrement() * 6.0F * var1;
               if (this.VehicleSteering < 0.0F) {
                  this.VehicleSteering = 0.0F;
               }
            } else if (this.VehicleSteering < 0.0F) {
               this.VehicleSteering += this.script.getSteeringIncrement() * 6.0F * var1;
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
         Vector3f var1 = this.tempVec3f;
         this.vehicleObject.getForwardVector(var1);
         Transform var2 = this.tempXfrm;
         this.vehicleObject.getWorldTransform(var2);
         PolygonalMap2.VehiclePoly var3 = this.vehicleObject.getPoly();
         LineDrawer.addLine(var3.x1, var3.y1, 0.0F, var3.x2, var3.y2, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
         LineDrawer.addLine(var3.x2, var3.y2, 0.0F, var3.x3, var3.y3, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
         LineDrawer.addLine(var3.x3, var3.y3, 0.0F, var3.x4, var3.y4, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
         LineDrawer.addLine(var3.x4, var3.y4, 0.0F, var3.x1, var3.y1, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
         this.drawRect(var1, var2.origin.x, var2.origin.z, this.script.getExtents().x, this.script.getExtents().z / 2.0F);
         _UNIT_Y.set(0.0F, 1.0F, 0.0F);

         float var5;
         float var6;
         int var8;
         for(var8 = 0; var8 < this.vehicleObject.getScript().getWheelCount(); ++var8) {
            VehicleScript.Wheel var4 = this.script.getWheel(var8);
            this.tempVec3f.set(var4.offset.x, var4.offset.y, var4.offset.z);
            this.vehicleObject.getWorldPos(this.tempVec3f, this.tempVec3f);
            var5 = this.tempVec3f.x;
            var6 = this.tempVec3f.y;
            this.vehicleObject.getWheelForwardVector(var8, this.tempVec3f);
            LineDrawer.addLine(var5, var6, 0.0F, var5 + this.tempVec3f.x, var6 + this.tempVec3f.z, 0.0F, 1.0F, 1.0F, 1.0F, (String)null, true);
            this.drawRect(this.tempVec3f, var5 - WorldSimulation.instance.offsetX, var6 - WorldSimulation.instance.offsetY, var4.width, var4.radius);
         }

         if (this.vehicleObject.collideX != -1.0F) {
            this.vehicleObject.getForwardVector(var1);
            this.drawCircle(this.vehicleObject.collideX, this.vehicleObject.collideY, 0.3F);
            this.vehicleObject.collideX = -1.0F;
            this.vehicleObject.collideY = -1.0F;
         }

         var8 = this.vehicleObject.getJoypad();
         if (var8 != -1) {
            float var9 = JoypadManager.instance.getMovementAxisX(var8);
            var5 = JoypadManager.instance.getMovementAxisY(var8);
            var6 = JoypadManager.instance.getDeadZone(var8, 0);
            if (Math.abs(var5) > var6 || Math.abs(var9) > var6) {
               Vector2 var7 = this.tempVec2.set(var9, var5);
               var7.setLength(4.0F);
               var7.rotate(-0.7853982F);
               LineDrawer.addLine(this.vehicleObject.getX(), this.vehicleObject.getY(), this.vehicleObject.z, this.vehicleObject.getX() + var7.x, this.vehicleObject.getY() + var7.y, this.vehicleObject.z, 1.0F, 1.0F, 1.0F, (String)null, true);
            }
         }

      }
   }

   public void drawRect(Vector3f var1, float var2, float var3, float var4, float var5) {
      this.drawRect(var1, var2, var3, var4, var5, 1.0F, 1.0F, 1.0F);
   }

   public void drawRect(Vector3f var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      Vector3f var9 = this.tempVec3f_3;
      var1.cross(_UNIT_Y, var9);
      float var10 = 1.0F;
      var1.x *= var10 * var5;
      var1.z *= var10 * var5;
      var9.x *= var10 * var4;
      var9.z *= var10 * var4;
      float var11 = var2 + var1.x;
      float var12 = var3 + var1.z;
      float var13 = var2 - var1.x;
      float var14 = var3 - var1.z;
      float var15 = var11 - var9.x / 2.0F;
      float var16 = var11 + var9.x / 2.0F;
      float var17 = var13 - var9.x / 2.0F;
      float var18 = var13 + var9.x / 2.0F;
      float var19 = var14 - var9.z / 2.0F;
      float var20 = var14 + var9.z / 2.0F;
      float var21 = var12 - var9.z / 2.0F;
      float var22 = var12 + var9.z / 2.0F;
      var15 += WorldSimulation.instance.offsetX;
      var21 += WorldSimulation.instance.offsetY;
      var16 += WorldSimulation.instance.offsetX;
      var22 += WorldSimulation.instance.offsetY;
      var17 += WorldSimulation.instance.offsetX;
      var19 += WorldSimulation.instance.offsetY;
      var18 += WorldSimulation.instance.offsetX;
      var20 += WorldSimulation.instance.offsetY;
      LineDrawer.addLine(var15, var21, 0.0F, var16, var22, 0.0F, var6, var7, var8, (String)null, true);
      LineDrawer.addLine(var15, var21, 0.0F, var17, var19, 0.0F, var6, var7, var8, (String)null, true);
      LineDrawer.addLine(var16, var22, 0.0F, var18, var20, 0.0F, var6, var7, var8, (String)null, true);
      LineDrawer.addLine(var17, var19, 0.0F, var18, var20, 0.0F, var6, var7, var8, (String)null, true);
   }

   public void drawCircle(float var1, float var2, float var3) {
      this.drawCircle(var1, var2, var3, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawCircle(float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      double var8 = (double)var1 + (double)var3 * Math.cos(Math.toRadians(0.0D));
      double var10 = (double)var2 + (double)var3 * Math.sin(Math.toRadians(0.0D));

      for(int var12 = 1; var12 <= 16; ++var12) {
         double var13 = (double)var1 + (double)var3 * Math.cos(Math.toRadians((double)(var12 * 360 / 16)));
         double var15 = (double)var2 + (double)var3 * Math.sin(Math.toRadians((double)(var12 * 360 / 16)));
         LineDrawer.addLine((float)var8, (float)var10, 0.0F, (float)var13, (float)var15, 0.0F, var4, var5, var6, (String)null, true);
         var8 = var13;
         var10 = var15;
      }

   }

   static {
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

      GearInfo(int var1, int var2, float var3) {
         this.minSpeed = var1;
         this.maxSpeed = var2;
         this.minRPM = var3;
      }
   }

   public static class BulletVariables {
      float engineForce;
      float brakingForce;
      float vehicleSteering;
      BaseVehicle vehicle;

      CarController.BulletVariables set(BaseVehicle var1, float var2, float var3, float var4) {
         this.vehicle = var1;
         this.engineForce = var2;
         this.brakingForce = var3;
         this.vehicleSteering = var4;
         return this;
      }
   }
}
