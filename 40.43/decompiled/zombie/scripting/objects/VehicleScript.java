package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.physics.Bullet;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;

public class VehicleScript extends BaseScriptObject {
   private String name;
   private ArrayList models = new ArrayList();
   private float mass = 800.0F;
   private Vector3f centerOfMassOffset = new Vector3f();
   private float engineForce = 3000.0F;
   private float engineIdleSpeed = 750.0F;
   private float steeringIncrement = 0.04F;
   private float steeringClamp = 0.4F;
   private float steeringClampMax = 0.9F;
   private float wheelFriction = 800.0F;
   private float stoppingMovementForce = 1.0F;
   private float suspensionStiffness = 20.0F;
   private float suspensionDamping = 2.3F;
   private float suspensionCompression = 4.4F;
   private float suspensionRestLength = 0.6F;
   private float maxSuspensionTravelCm = 500.0F;
   private float rollInfluence = 0.1F;
   private Vector3f extents = new Vector3f(0.75F, 0.5F, 2.0F);
   private Vector4f shadowOffset = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
   private Vector2f extentsOffset = new Vector2f(0.5F, 0.5F);
   private Vector3f physicsChassisShape = new Vector3f(0.75F, 0.5F, 1.0F);
   private ArrayList wheels = new ArrayList();
   private ArrayList passengers = new ArrayList();
   public float maxSpeed = 20.0F;
   private int frontEndHealth = 100;
   private int rearEndHealth = 100;
   private int storageCapacity = 100;
   private int engineLoudness = 100;
   private int engineQuality = 100;
   private int seats = 2;
   private int mechanicType;
   private int engineRepairLevel;
   private float playerDamageProtection;
   private float forcedHue = -1.0F;
   private float forcedSat = -1.0F;
   private float forcedVal = -1.0F;
   private String engineRPMType = "jeep";
   private float offroadEfficiency = 1.0F;
   public int gearRatioCount = 0;
   public float[] gearRatio = new float[9];
   private ArrayList skins = new ArrayList();
   private ArrayList areas = new ArrayList();
   private ArrayList parts = new ArrayList();
   private boolean hasSiren = false;
   private VehicleScript.LightBar lightbar = new VehicleScript.LightBar();
   private VehicleScript.Sounds sound = new VehicleScript.Sounds();
   public boolean textureMaskEnable = false;
   public String textureRust = null;
   public String textureMask = null;
   public String textureLights = null;
   public String textureDamage1Overlay = null;
   public String textureDamage1Shell = null;
   public String textureDamage2Overlay = null;
   public String textureDamage2Shell = null;
   public Texture[] textureDataSkins;
   public Texture textureDataRust;
   public Texture textureDataMask;
   public Texture textureDataLights;
   public Texture textureDataDamage1Overlay;
   public Texture textureDataDamage1Shell;
   public Texture textureDataDamage2Overlay;
   public Texture textureDataDamage2Shell;

   public VehicleScript() {
      this.gearRatioCount = 4;
      this.gearRatio[0] = 7.09F;
      this.gearRatio[1] = 6.44F;
      this.gearRatio[2] = 4.1F;
      this.gearRatio[3] = 2.29F;
      this.gearRatio[4] = 1.47F;
      this.gearRatio[5] = 1.0F;
   }

   public void Load(String var1, String var2) {
      this.name = var1;
      VehicleScript.Block var3 = new VehicleScript.Block();
      this.readBlock(var2, 0, var3);
      var3 = (VehicleScript.Block)var3.children.get(0);
      Iterator var4 = var3.elements.iterator();

      while(true) {
         while(var4.hasNext()) {
            VehicleScript.BlockElement var5 = (VehicleScript.BlockElement)var4.next();
            String var15;
            String var16;
            String[] var17;
            if (var5.asValue() != null) {
               String[] var13 = var5.asValue().string.split("=");
               var15 = var13[0].trim();
               var16 = var13[1].trim();
               if ("extents".equals(var15)) {
                  this.LoadVector3f(var16, this.extents);
               } else if ("shadowOffset".equals(var15)) {
                  this.LoadVector4f(var16, this.shadowOffset);
               } else if ("physicsChassisShape".equals(var15)) {
                  this.LoadVector3f(var16, this.physicsChassisShape);
               } else if ("extentsOffset".equals(var15)) {
                  this.LoadVector2f(var16, this.extentsOffset);
               } else if ("mass".equals(var15)) {
                  this.mass = Float.valueOf(var16);
               } else if ("offRoadEfficiency".equalsIgnoreCase(var15)) {
                  this.offroadEfficiency = Float.valueOf(var16);
               } else if ("centerOfMassOffset".equals(var15)) {
                  this.LoadVector3f(var16, this.centerOfMassOffset);
               } else if ("engineForce".equals(var15)) {
                  this.engineForce = Float.valueOf(var16);
               } else if ("engineIdleSpeed".equals(var15)) {
                  this.engineIdleSpeed = Float.valueOf(var16);
               } else if ("gearRatioCount".equals(var15)) {
                  this.gearRatioCount = Integer.valueOf(var16);
               } else if ("gearRatioR".equals(var15)) {
                  this.gearRatio[0] = Float.valueOf(var16);
               } else if ("gearRatio1".equals(var15)) {
                  this.gearRatio[1] = Float.valueOf(var16);
               } else if ("gearRatio2".equals(var15)) {
                  this.gearRatio[2] = Float.valueOf(var16);
               } else if ("gearRatio3".equals(var15)) {
                  this.gearRatio[3] = Float.valueOf(var16);
               } else if ("gearRatio4".equals(var15)) {
                  this.gearRatio[4] = Float.valueOf(var16);
               } else if ("gearRatio5".equals(var15)) {
                  this.gearRatio[5] = Float.valueOf(var16);
               } else if ("gearRatio6".equals(var15)) {
                  this.gearRatio[6] = Float.valueOf(var16);
               } else if ("gearRatio7".equals(var15)) {
                  this.gearRatio[7] = Float.valueOf(var16);
               } else if ("gearRatio8".equals(var15)) {
                  this.gearRatio[8] = Float.valueOf(var16);
               } else if ("textureMaskEnable".equals(var15)) {
                  this.textureMaskEnable = Boolean.valueOf(var16);
               } else if ("textureRust".equals(var15)) {
                  this.textureRust = var16;
               } else if ("textureMask".equals(var15)) {
                  this.textureMask = var16;
               } else if ("textureLights".equals(var15)) {
                  this.textureLights = var16;
               } else if ("textureDamage1Overlay".equals(var15)) {
                  this.textureDamage1Overlay = var16;
               } else if ("textureDamage1Shell".equals(var15)) {
                  this.textureDamage1Shell = var16;
               } else if ("textureDamage2Overlay".equals(var15)) {
                  this.textureDamage2Overlay = var16;
               } else if ("textureDamage2Shell".equals(var15)) {
                  this.textureDamage2Shell = var16;
               } else if ("rollInfluence".equals(var15)) {
                  this.rollInfluence = Float.valueOf(var16);
               } else if ("steeringIncrement".equals(var15)) {
                  this.steeringIncrement = Float.valueOf(var16);
               } else if ("steeringClamp".equals(var15)) {
                  this.steeringClamp = Float.valueOf(var16);
               } else if ("suspensionStiffness".equals(var15)) {
                  this.suspensionStiffness = Float.valueOf(var16);
               } else if ("suspensionDamping".equals(var15)) {
                  this.suspensionDamping = Float.valueOf(var16);
               } else if ("suspensionCompression".equals(var15)) {
                  this.suspensionCompression = Float.valueOf(var16);
               } else if ("suspensionRestLength".equals(var15)) {
                  this.suspensionRestLength = Float.valueOf(var16);
               } else if ("maxSuspensionTravelCm".equals(var15)) {
                  this.maxSuspensionTravelCm = Float.valueOf(var16);
               } else if ("wheelFriction".equals(var15)) {
                  this.wheelFriction = Float.valueOf(var16);
               } else if ("stoppingMovementForce".equals(var15)) {
                  this.stoppingMovementForce = Float.valueOf(var16);
               } else if ("maxSpeed".equals(var15)) {
                  this.maxSpeed = Float.valueOf(var16);
               } else if ("frontEndDurability".equals(var15)) {
                  this.frontEndHealth = Integer.valueOf(var16);
               } else if ("rearEndDurability".equals(var15)) {
                  this.rearEndHealth = Integer.valueOf(var16);
               } else if ("storageCapacity".equals(var15)) {
                  this.storageCapacity = Integer.valueOf(var16);
               } else if ("engineLoudness".equals(var15)) {
                  this.engineLoudness = Integer.valueOf(var16);
               } else if ("engineQuality".equals(var15)) {
                  this.engineQuality = Integer.valueOf(var16);
               } else if ("seats".equals(var15)) {
                  this.seats = Integer.valueOf(var16);
               } else if ("hasSiren".equals(var15)) {
                  this.hasSiren = Boolean.valueOf(var16);
               } else if ("mechanicType".equals(var15)) {
                  this.mechanicType = Integer.valueOf(var16);
               } else if ("forcedColor".equals(var15)) {
                  var17 = var16.split(" ");
                  this.setForcedHue(Float.parseFloat(var17[0]));
                  this.setForcedSat(Float.parseFloat(var17[1]));
                  this.setForcedVal(Float.parseFloat(var17[2]));
               } else if ("engineRPMType".equals(var15)) {
                  this.engineRPMType = var16.trim();
               } else if ("template".equals(var15)) {
                  this.LoadTemplate(var16);
               } else if ("template!".equals(var15)) {
                  VehicleTemplate var21 = ScriptManager.instance.getVehicleTemplate(var16);
                  if (var21 == null) {
                     DebugLog.log("ERROR: template \"" + var16 + "\" not found");
                  } else {
                     this.Load(var1, var21.body);
                  }
               } else if ("engineRepairLevel".equals(var15)) {
                  this.engineRepairLevel = Integer.valueOf(var16);
               } else if ("playerDamageProtection".equals(var15)) {
                  this.setPlayerDamageProtection(Float.valueOf(var16));
               }
            } else {
               VehicleScript.Block var6 = var5.asBlock();
               if ("area".equals(var6.type)) {
                  this.LoadArea(var6);
               } else if ("model".equals(var6.type)) {
                  this.LoadModel(var6, this.models);
               } else {
                  Iterator var8;
                  if ("part".equals(var6.type)) {
                     if (var6.id != null && var6.id.contains("*")) {
                        var15 = var6.id;
                        var8 = this.parts.iterator();

                        while(var8.hasNext()) {
                           VehicleScript.Part var19 = (VehicleScript.Part)var8.next();
                           if (this.globMatch(var15, var19.id)) {
                              var6.id = var19.id;
                              this.LoadPart(var6);
                           }
                        }
                     } else {
                        this.LoadPart(var6);
                     }
                  } else if ("passenger".equals(var6.type)) {
                     if (var6.id != null && var6.id.contains("*")) {
                        var15 = var6.id;
                        var8 = this.passengers.iterator();

                        while(var8.hasNext()) {
                           VehicleScript.Passenger var18 = (VehicleScript.Passenger)var8.next();
                           if (this.globMatch(var15, var18.id)) {
                              var6.id = var18.id;
                              this.LoadPassenger(var6);
                           }
                        }
                     } else {
                        this.LoadPassenger(var6);
                     }
                  } else {
                     String var11;
                     if (!"skin".equals(var6.type)) {
                        if ("wheel".equals(var6.type)) {
                           this.LoadWheel(var6);
                        } else {
                           Iterator var14;
                           String var20;
                           if ("lightbar".equals(var6.type)) {
                              for(var14 = var6.values.iterator(); var14.hasNext(); this.lightbar.enable = true) {
                                 var16 = (String)var14.next();
                                 var17 = var16.split("=");
                                 var20 = var17[0].trim();
                                 var11 = var17[1].trim();
                                 if ("soundSiren".equals(var20)) {
                                    this.lightbar.soundSiren0 = var11 + "Yelp";
                                    this.lightbar.soundSiren1 = var11 + "Wall";
                                    this.lightbar.soundSiren2 = var11 + "Alarm";
                                 }

                                 if ("soundSiren0".equals(var20)) {
                                    this.lightbar.soundSiren0 = var11;
                                 }

                                 if ("soundSiren1".equals(var20)) {
                                    this.lightbar.soundSiren1 = var11;
                                 }

                                 if ("soundSiren2".equals(var20)) {
                                    this.lightbar.soundSiren2 = var11;
                                 }
                              }
                           } else if ("sound".equals(var6.type)) {
                              var14 = var6.values.iterator();

                              while(var14.hasNext()) {
                                 var16 = (String)var14.next();
                                 var17 = var16.split("=");
                                 var20 = var17[0].trim();
                                 var11 = var17[1].trim();
                                 if ("horn".equals(var20)) {
                                    this.sound.horn = var11;
                                    this.sound.hornEnable = true;
                                 }

                                 if ("backSignal".equals(var20)) {
                                    this.sound.backSignal = var11;
                                    this.sound.backSignalEnable = true;
                                 }
                              }
                           }
                        }
                     } else {
                        VehicleScript.Skin var7 = new VehicleScript.Skin();
                        var8 = var6.values.iterator();

                        while(var8.hasNext()) {
                           String var9 = (String)var8.next();
                           String[] var10 = var9.split("=");
                           var11 = var10[0].trim();
                           String var12 = var10[1].trim();
                           if ("texture".equals(var11)) {
                              var7.texture = var12;
                           }
                        }

                        if (var7.texture != null && !var7.texture.isEmpty()) {
                           this.skins.add(var7);
                        }
                     }
                  }
               }
            }
         }

         return;
      }
   }

   public void Loaded() {
      int var1;
      int var3;
      for(var1 = 0; var1 < this.passengers.size(); ++var1) {
         VehicleScript.Passenger var2 = (VehicleScript.Passenger)this.passengers.get(var1);

         for(var3 = 0; var3 < var2.switchSeats.size(); ++var3) {
            VehicleScript.Passenger.SwitchSeat var4 = (VehicleScript.Passenger.SwitchSeat)var2.switchSeats.get(var3);
            var4.seat = this.getPassengerIndex(var4.id);

            assert var4.seat != -1;
         }
      }

      for(var1 = 0; var1 < this.parts.size(); ++var1) {
         VehicleScript.Part var6 = (VehicleScript.Part)this.parts.get(var1);
         if (var6.container != null && var6.container.seatID != null && !var6.container.seatID.isEmpty()) {
            var6.container.seat = this.getPassengerIndex(var6.container.seatID);

            assert var6.container.seat != -1;
         }

         if (var6.specificItem && var6.itemType != null) {
            for(var3 = 0; var3 < var6.itemType.size(); ++var3) {
               var6.itemType.set(var3, (String)var6.itemType.get(var3) + this.mechanicType);
            }
         }
      }

      float[] var5 = new float[100];
      byte var7 = 0;
      int var8 = var7 + 1;
      var5[var7] = this.getModelScale();
      var5[var8++] = this.extents.x;
      var5[var8++] = this.extents.y;
      var5[var8++] = this.extents.z;
      var5[var8++] = this.physicsChassisShape.x;
      var5[var8++] = this.physicsChassisShape.y;
      var5[var8++] = this.physicsChassisShape.z;
      var5[var8++] = this.mass;
      var5[var8++] = this.centerOfMassOffset.x;
      var5[var8++] = this.centerOfMassOffset.y;
      var5[var8++] = this.centerOfMassOffset.z;
      var5[var8++] = this.rollInfluence;
      var5[var8++] = this.suspensionStiffness;
      var5[var8++] = this.suspensionCompression;
      var5[var8++] = this.suspensionDamping;
      var5[var8++] = this.maxSuspensionTravelCm;
      var5[var8++] = this.suspensionRestLength;
      var5[var8++] = this.wheelFriction;
      var5[var8++] = this.stoppingMovementForce;
      var5[var8++] = (float)this.getWheelCount();

      for(var3 = 0; var3 < this.getWheelCount(); ++var3) {
         var5[var8++] = this.getWheel(var3).front ? 1.0F : 0.0F;
         var5[var8++] = this.getWheel(var3).offset.x;
         var5[var8++] = this.getWheel(var3).offset.y;
         var5[var8++] = this.getWheel(var3).offset.z;
         var5[var8++] = this.getWheel(var3).radius;
      }

      Bullet.defineVehicleScript(this.getFullName(), var5);
   }

   private void LoadVector2f(String var1, Vector2f var2) {
      String[] var3 = var1.split(" ");
      var2.set(Float.valueOf(var3[0]), Float.valueOf(var3[1]));
   }

   private void LoadVector3f(String var1, Vector3f var2) {
      String[] var3 = var1.split(" ");
      var2.set(Float.valueOf(var3[0]), Float.valueOf(var3[1]), Float.valueOf(var3[2]));
   }

   private void LoadVector4f(String var1, Vector4f var2) {
      String[] var3 = var1.split(" ");
      var2.set(Float.valueOf(var3[0]), Float.valueOf(var3[1]), Float.valueOf(var3[2]), Float.valueOf(var3[3]));
   }

   private void LoadVector2i(String var1, Vector2i var2) {
      String[] var3 = var1.split(" ");
      var2.set(Integer.valueOf(var3[0]), Integer.valueOf(var3[1]));
   }

   private VehicleScript.Model LoadModel(VehicleScript.Block var1, ArrayList var2) {
      VehicleScript.Model var3 = this.getModelById(var1.id, var2);
      if (var3 == null) {
         var3 = new VehicleScript.Model();
         var3.id = var1.id;
         var2.add(var3);
      }

      Iterator var4 = var1.values.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String[] var6 = var5.split("=");
         String var7 = var6[0].trim();
         String var8 = var6[1].trim();
         if ("file".equals(var7)) {
            var3.file = var8;
         } else if ("offset".equals(var7)) {
            this.LoadVector3f(var8, var3.offset);
         } else if ("rotate".equals(var7)) {
            this.LoadVector3f(var8, var3.rotate);
         } else if ("scale".equals(var7)) {
            var3.scale = Float.parseFloat(var8);
         }
      }

      return var3;
   }

   private VehicleScript.Wheel LoadWheel(VehicleScript.Block var1) {
      VehicleScript.Wheel var2 = this.getWheelById(var1.id);
      if (var2 == null) {
         var2 = new VehicleScript.Wheel();
         var2.id = var1.id;
         this.wheels.add(var2);
      }

      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         if ("model".equals(var6)) {
            var2.model = var7;
         } else if ("front".equals(var6)) {
            var2.front = Boolean.valueOf(var7);
         } else if ("offset".equals(var6)) {
            this.LoadVector3f(var7, var2.offset);
         } else if ("radius".equals(var6)) {
            var2.radius = Float.valueOf(var7);
         } else if ("width".equals(var6)) {
            var2.width = Float.valueOf(var7);
         }
      }

      return var2;
   }

   private VehicleScript.Passenger LoadPassenger(VehicleScript.Block var1) {
      VehicleScript.Passenger var2 = this.getPassengerById(var1.id);
      if (var2 == null) {
         var2 = new VehicleScript.Passenger();
         var2.id = var1.id;
         this.passengers.add(var2);
      }

      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         if ("area".equals(var6)) {
            var2.area = var7;
         } else if ("door".equals(var6)) {
            var2.door = var7;
         } else if ("door2".equals(var6)) {
            var2.door2 = var7;
         } else if ("hasRoof".equals(var6)) {
            var2.hasRoof = Boolean.parseBoolean(var7);
         }
      }

      var3 = var1.children.iterator();

      while(var3.hasNext()) {
         VehicleScript.Block var8 = (VehicleScript.Block)var3.next();
         if ("anim".equals(var8.type)) {
            this.LoadAnim(var8, var2.anims);
         } else if ("position".equals(var8.type)) {
            this.LoadPosition(var8, var2.positions);
         } else if ("switchSeat".equals(var8.type)) {
            this.LoadPassengerSwitchSeat(var8, var2);
         }
      }

      return var2;
   }

   private VehicleScript.Anim LoadAnim(VehicleScript.Block var1, ArrayList var2) {
      VehicleScript.Anim var3 = this.getAnimationById(var1.id, var2);
      if (var3 == null) {
         var3 = new VehicleScript.Anim();
         var3.id = var1.id;
         var2.add(var3);
      }

      Iterator var4 = var1.values.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String[] var6 = var5.split("=");
         String var7 = var6[0].trim();
         String var8 = var6[1].trim();
         if ("angle".equals(var7)) {
            this.LoadVector3f(var8, var3.angle);
         } else if ("anim".equals(var7)) {
            var3.anim = var8;
         } else if ("rate".equals(var7)) {
            var3.rate = Float.valueOf(var8);
         } else if ("offset".equals(var7)) {
            this.LoadVector3f(var8, var3.offset);
         } else if ("sound".equals(var7)) {
            var3.sound = var8;
         }
      }

      return var3;
   }

   private VehicleScript.Passenger.SwitchSeat LoadPassengerSwitchSeat(VehicleScript.Block var1, VehicleScript.Passenger var2) {
      VehicleScript.Passenger.SwitchSeat var3 = var2.getSwitchSeatById(var1.id);
      if (var1.isEmpty()) {
         if (var3 != null) {
            var2.switchSeats.remove(var3);
         }

         return null;
      } else {
         if (var3 == null) {
            var3 = new VehicleScript.Passenger.SwitchSeat();
            var3.id = var1.id;
            var2.switchSeats.add(var3);
         }

         Iterator var4 = var1.values.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            String[] var6 = var5.split("=");
            String var7 = var6[0].trim();
            String var8 = var6[1].trim();
            if ("anim".equals(var7)) {
               var3.anim = var8;
            } else if ("rate".equals(var7)) {
               var3.rate = Float.valueOf(var8);
            } else if ("sound".equals(var7)) {
               var3.sound = var8.isEmpty() ? null : var8;
            }
         }

         return var3;
      }
   }

   private VehicleScript.Area LoadArea(VehicleScript.Block var1) {
      VehicleScript.Area var2 = this.getAreaById(var1.id);
      if (var2 == null) {
         var2 = new VehicleScript.Area();
         var2.id = var1.id;
         this.areas.add(var2);
      }

      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         if ("xywh".equals(var6)) {
            String[] var8 = var7.split(" ");
            var2.x = Float.valueOf(var8[0]);
            var2.y = Float.valueOf(var8[1]);
            var2.w = Float.valueOf(var8[2]);
            var2.h = Float.valueOf(var8[3]);
         }
      }

      return var2;
   }

   private VehicleScript.Part LoadPart(VehicleScript.Block var1) {
      VehicleScript.Part var2 = this.getPartById(var1.id);
      if (var2 == null) {
         var2 = new VehicleScript.Part();
         var2.id = var1.id;
         this.parts.add(var2);
      }

      Iterator var3 = var1.values.iterator();

      while(true) {
         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            String[] var5 = var4.split("=");
            String var6 = var5[0].trim();
            String var7 = var5[1].trim();
            if ("area".equals(var6)) {
               var2.area = var7.isEmpty() ? null : var7;
            } else if ("itemType".equals(var6)) {
               var2.itemType = new ArrayList();
               String[] var8 = var7.split(";");
               String[] var9 = var8;
               int var10 = var8.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  String var12 = var9[var11];
                  var2.itemType.add(var12);
               }
            } else if ("parent".equals(var6)) {
               var2.parent = var7.isEmpty() ? null : var7;
            } else if ("mechanicRequireKey".equals(var6)) {
               var2.mechanicRequireKey = Boolean.parseBoolean(var7);
            } else if ("repairMechanic".equals(var6)) {
               var2.setRepairMechanic(Boolean.parseBoolean(var7));
            } else if ("wheel".equals(var6)) {
               var2.wheel = var7;
            } else if ("category".equals(var6)) {
               var2.category = var7;
            } else if ("specificItem".equals(var6)) {
               var2.specificItem = Boolean.parseBoolean(var7);
            } else if ("hasLightsRear".equals(var6)) {
               var2.hasLightsRear = Boolean.parseBoolean(var7);
            }
         }

         var3 = var1.children.iterator();

         while(var3.hasNext()) {
            VehicleScript.Block var13 = (VehicleScript.Block)var3.next();
            if ("anim".equals(var13.type)) {
               if (var2.anims == null) {
                  var2.anims = new ArrayList();
               }

               this.LoadAnim(var13, var2.anims);
            } else if ("container".equals(var13.type)) {
               var2.container = this.LoadContainer(var13, var2.container);
            } else if ("door".equals(var13.type)) {
               var2.door = this.LoadDoor(var13);
            } else if ("lua".equals(var13.type)) {
               var2.luaFunctions = this.LoadLuaFunctions(var13);
            } else if ("model".equals(var13.type)) {
               if (var2.models == null) {
                  var2.models = new ArrayList();
               }

               this.LoadModel(var13, var2.models);
            } else if ("table".equals(var13.type)) {
               Object var14 = var2.tables == null ? null : var2.tables.get(var13.id);
               KahluaTable var15 = this.LoadTable(var13, var14 instanceof KahluaTable ? (KahluaTable)var14 : null);
               if (var2.tables == null) {
                  var2.tables = new HashMap();
               }

               var2.tables.put(var13.id, var15);
            } else if ("window".equals(var13.type)) {
               var2.window = this.LoadWindow(var13);
            }
         }

         return var2;
      }
   }

   private VehicleScript.Door LoadDoor(VehicleScript.Block var1) {
      VehicleScript.Door var2 = new VehicleScript.Door();

      String[] var5;
      String var7;
      for(Iterator var3 = var1.values.iterator(); var3.hasNext(); var7 = var5[1].trim()) {
         String var4 = (String)var3.next();
         var5 = var4.split("=");
         String var6 = var5[0].trim();
      }

      return var2;
   }

   private VehicleScript.Window LoadWindow(VehicleScript.Block var1) {
      VehicleScript.Window var2 = new VehicleScript.Window();
      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         if ("openable".equals(var6)) {
            var2.openable = Boolean.valueOf(var7);
         }
      }

      return var2;
   }

   private VehicleScript.Container LoadContainer(VehicleScript.Block var1, VehicleScript.Container var2) {
      VehicleScript.Container var3 = var2 == null ? new VehicleScript.Container() : var2;
      Iterator var4 = var1.values.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String[] var6 = var5.split("=");
         String var7 = var6[0].trim();
         String var8 = var6[1].trim();
         if ("capacity".equals(var7)) {
            var3.capacity = Integer.valueOf(var8);
         } else if ("conditionAffectsCapacity".equals(var7)) {
            var3.conditionAffectsCapacity = Boolean.valueOf(var8);
         } else if ("contentType".equals(var7)) {
            var3.contentType = var8;
         } else if ("seat".equals(var7)) {
            var3.seatID = var8;
         } else if ("test".equals(var7)) {
            var3.luaTest = var8;
         }
      }

      return var3;
   }

   private HashMap LoadLuaFunctions(VehicleScript.Block var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.values.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String[] var5 = var4.split("=");
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         var2.put(var6, var7);
      }

      return var2;
   }

   private Object checkIntegerKey(Object var1) {
      if (!(var1 instanceof String)) {
         return var1;
      } else {
         String var2 = (String)var1;

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            if (!Character.isDigit(var2.charAt(var3))) {
               return var1;
            }
         }

         return Double.valueOf(var2);
      }
   }

   private KahluaTable LoadTable(VehicleScript.Block var1, KahluaTable var2) {
      KahluaTable var3 = var2 == null ? LuaManager.platform.newTable() : var2;

      Iterator var4;
      String var7;
      String var8;
      for(var4 = var1.values.iterator(); var4.hasNext(); var3.rawset(this.checkIntegerKey(var7), var8)) {
         String var5 = (String)var4.next();
         String[] var6 = var5.split("=");
         var7 = var6[0].trim();
         var8 = var6[1].trim();
         if (var8.isEmpty()) {
            var8 = null;
         }
      }

      var4 = var1.children.iterator();

      while(var4.hasNext()) {
         VehicleScript.Block var9 = (VehicleScript.Block)var4.next();
         Object var10 = var3.rawget(var9.type);
         KahluaTable var11 = this.LoadTable(var9, var10 instanceof KahluaTable ? (KahluaTable)var10 : null);
         var3.rawset(this.checkIntegerKey(var9.type), var11);
      }

      return var3;
   }

   private void LoadTemplate(String var1) {
      if (var1.contains("/")) {
         String[] var2 = var1.split("/");
         if (var2.length == 0 || var2.length > 3) {
            DebugLog.log("ERROR: template \"" + var1 + "\"");
            return;
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = var2[var3].trim();
            if (var2[var3].isEmpty()) {
               DebugLog.log("ERROR: template \"" + var1 + "\"");
               return;
            }
         }

         String var9 = var2[0];
         VehicleTemplate var4 = ScriptManager.instance.getVehicleTemplate(var9);
         if (var4 == null) {
            DebugLog.log("ERROR: template \"" + var1 + "\" not found");
            return;
         }

         VehicleScript var5 = var4.getScript();
         String var6 = var2[1];
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -944810854:
            if (var6.equals("passenger")) {
               var7 = 2;
            }
            break;
         case 3002509:
            if (var6.equals("area")) {
               var7 = 0;
            }
            break;
         case 3433459:
            if (var6.equals("part")) {
               var7 = 1;
            }
            break;
         case 113097563:
            if (var6.equals("wheel")) {
               var7 = 3;
            }
         }

         switch(var7) {
         case 0:
            if (var2.length == 2) {
               DebugLog.log("ERROR: template \"" + var1 + "\"");
               return;
            }

            this.copyAreasFrom(var5, var2[2]);
            break;
         case 1:
            if (var2.length == 2) {
               DebugLog.log("ERROR: template \"" + var1 + "\"");
               return;
            }

            this.copyPartsFrom(var5, var2[2]);
            break;
         case 2:
            if (var2.length == 2) {
               DebugLog.log("ERROR: template \"" + var1 + "\"");
               return;
            }

            this.copyPassengersFrom(var5, var2[2]);
            break;
         case 3:
            if (var2.length == 2) {
               DebugLog.log("ERROR: template \"" + var1 + "\"");
               return;
            }

            this.copyWheelsFrom(var5, var2[2]);
            break;
         default:
            DebugLog.log("ERROR: template \"" + var1 + "\"");
            return;
         }
      } else {
         String var8 = var1.trim();
         VehicleTemplate var10 = ScriptManager.instance.getVehicleTemplate(var8);
         if (var10 == null) {
            DebugLog.log("ERROR: template \"" + var1 + "\" not found");
            return;
         }

         VehicleScript var11 = var10.getScript();
         this.copyAreasFrom(var11, "*");
         this.copyPartsFrom(var11, "*");
         this.copyPassengersFrom(var11, "*");
         this.copyWheelsFrom(var11, "*");
      }

   }

   public void copyAreasFrom(VehicleScript var1, String var2) {
      if ("*".equals(var2)) {
         for(int var3 = 0; var3 < var1.getAreaCount(); ++var3) {
            VehicleScript.Area var4 = var1.getArea(var3);
            int var5 = this.getIndexOfAreaById(var4.id);
            if (var5 == -1) {
               this.areas.add(var4.makeCopy());
            } else {
               this.areas.set(var5, var4.makeCopy());
            }
         }
      } else {
         VehicleScript.Area var6 = var1.getAreaById(var2);
         if (var6 == null) {
            DebugLog.log("ERROR: area \"" + var2 + "\" not found");
            return;
         }

         int var7 = this.getIndexOfAreaById(var6.id);
         if (var7 == -1) {
            this.areas.add(var6.makeCopy());
         } else {
            this.areas.set(var7, var6.makeCopy());
         }
      }

   }

   public void copyPartsFrom(VehicleScript var1, String var2) {
      if ("*".equals(var2)) {
         for(int var3 = 0; var3 < var1.getPartCount(); ++var3) {
            VehicleScript.Part var4 = var1.getPart(var3);
            int var5 = this.getIndexOfPartById(var4.id);
            if (var5 == -1) {
               this.parts.add(var4.makeCopy());
            } else {
               this.parts.set(var5, var4.makeCopy());
            }
         }
      } else {
         VehicleScript.Part var6 = var1.getPartById(var2);
         if (var6 == null) {
            DebugLog.log("ERROR: part \"" + var2 + "\" not found");
            return;
         }

         int var7 = this.getIndexOfPartById(var6.id);
         if (var7 == -1) {
            this.parts.add(var6.makeCopy());
         } else {
            this.parts.set(var7, var6.makeCopy());
         }
      }

   }

   public void copyPassengersFrom(VehicleScript var1, String var2) {
      if ("*".equals(var2)) {
         for(int var3 = 0; var3 < var1.getPassengerCount(); ++var3) {
            VehicleScript.Passenger var4 = var1.getPassenger(var3);
            int var5 = this.getPassengerIndex(var4.id);
            if (var5 == -1) {
               this.passengers.add(var4.makeCopy());
            } else {
               this.passengers.set(var5, var4.makeCopy());
            }
         }
      } else {
         VehicleScript.Passenger var6 = var1.getPassengerById(var2);
         if (var6 == null) {
            DebugLog.log("ERROR: passenger \"" + var2 + "\" not found");
            return;
         }

         int var7 = this.getPassengerIndex(var6.id);
         if (var7 == -1) {
            this.passengers.add(var6.makeCopy());
         } else {
            this.passengers.set(var7, var6.makeCopy());
         }
      }

   }

   public void copyWheelsFrom(VehicleScript var1, String var2) {
      if ("*".equals(var2)) {
         for(int var3 = 0; var3 < var1.getWheelCount(); ++var3) {
            VehicleScript.Wheel var4 = var1.getWheel(var3);
            int var5 = this.getIndexOfWheelById(var4.id);
            if (var5 == -1) {
               this.wheels.add(var4.makeCopy());
            } else {
               this.wheels.set(var5, var4.makeCopy());
            }
         }
      } else {
         VehicleScript.Wheel var6 = var1.getWheelById(var2);
         if (var6 == null) {
            DebugLog.log("ERROR: wheel \"" + var2 + "\" not found");
            return;
         }

         int var7 = this.getIndexOfWheelById(var6.id);
         if (var7 == -1) {
            this.wheels.add(var6.makeCopy());
         } else {
            this.wheels.set(var7, var6.makeCopy());
         }
      }

   }

   private VehicleScript.Position LoadPosition(VehicleScript.Block var1, ArrayList var2) {
      VehicleScript.Position var3 = this.getPositionById(var1.id, var2);
      if (var1.isEmpty()) {
         if (var3 != null) {
            var2.remove(var3);
         }

         return null;
      } else {
         if (var3 == null) {
            var3 = new VehicleScript.Position();
            var3.id = var1.id;
            var2.add(var3);
         }

         Iterator var4 = var1.values.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            String[] var6 = var5.split("=");
            String var7 = var6[0].trim();
            String var8 = var6[1].trim();
            if ("rotate".equals(var7)) {
               this.LoadVector3f(var8, var3.rotate);
            } else if ("offset".equals(var7)) {
               this.LoadVector3f(var8, var3.offset);
            } else if ("area".equals(var7)) {
               var3.area = var8.isEmpty() ? null : var8;
            }
         }

         return var3;
      }
   }

   private int readBlock(String var1, int var2, VehicleScript.Block var3) {
      int var4;
      for(var4 = var2; var4 < var1.length(); ++var4) {
         if (var1.charAt(var4) == '{') {
            VehicleScript.Block var5 = new VehicleScript.Block();
            var3.children.add(var5);
            var3.elements.add(var5);
            String var6 = var1.substring(var2, var4).trim();
            String[] var7 = var6.split("\\s+");
            var5.type = var7[0];
            var5.id = var7.length > 1 ? var7[1] : null;
            var4 = this.readBlock(var1, var4 + 1, var5);
            var2 = var4;
         } else {
            if (var1.charAt(var4) == '}') {
               return var4 + 1;
            }

            if (var1.charAt(var4) == ',') {
               VehicleScript.Value var8 = new VehicleScript.Value();
               var8.string = var1.substring(var2, var4);
               var3.values.add(var8.string);
               var3.elements.add(var8);
               var2 = var4 + 1;
            }
         }
      }

      return var4;
   }

   public String getName() {
      return this.name;
   }

   public String getFullName() {
      return this.getModule().getName() + "." + this.getName();
   }

   public VehicleScript.Model getModel() {
      return this.models.isEmpty() ? null : (VehicleScript.Model)this.models.get(0);
   }

   public float getModelScale() {
      return this.getModel() == null ? 1.0F : this.getModel().scale;
   }

   public int getModelCount() {
      return this.models.size();
   }

   public VehicleScript.Model getModelByIndex(int var1) {
      return (VehicleScript.Model)this.models.get(var1);
   }

   public VehicleScript.Model getModelById(String var1, ArrayList var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         VehicleScript.Model var4 = (VehicleScript.Model)var2.get(var3);
         if (var4.id != null && var4.id.equals(var1)) {
            return var4;
         }
      }

      return null;
   }

   public VehicleScript.Model getModelById(String var1) {
      return this.getModelById(var1, this.models);
   }

   public VehicleScript.LightBar getLightbar() {
      return this.lightbar;
   }

   public VehicleScript.Sounds getSounds() {
      return this.sound;
   }

   public boolean getHasSiren() {
      return this.hasSiren;
   }

   public Vector3f getExtents() {
      return this.extents;
   }

   public Vector4f getShadowOffset() {
      return this.shadowOffset;
   }

   public Vector2f getExtentsOffset() {
      return this.extentsOffset;
   }

   public float getMass() {
      return this.mass;
   }

   public Vector3f getCenterOfMassOffset() {
      return this.centerOfMassOffset;
   }

   public float getEngineForce() {
      return this.engineForce;
   }

   public float getEngineIdleSpeed() {
      return this.engineIdleSpeed;
   }

   public int getEngineQuality() {
      return this.engineQuality;
   }

   public int getEngineLoudness() {
      return this.engineLoudness;
   }

   public float getRollInfluence() {
      return this.rollInfluence;
   }

   public float getSteeringIncrement() {
      return this.steeringIncrement;
   }

   public float getSteeringClamp(float var1) {
      var1 = Math.abs(var1);
      float var2 = var1 / this.maxSpeed;
      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      var2 = 1.0F - var2;
      return (this.steeringClampMax - this.steeringClamp) * var2 + this.steeringClamp;
   }

   public float getSuspensionStiffness() {
      return this.suspensionStiffness;
   }

   public float getSuspensionDamping() {
      return this.suspensionDamping;
   }

   public float getSuspensionCompression() {
      return this.suspensionCompression;
   }

   public float getSuspensionRestLength() {
      return this.suspensionRestLength;
   }

   public float getSuspensionTravel() {
      return this.maxSuspensionTravelCm;
   }

   public float getWheelFriction() {
      return this.wheelFriction;
   }

   public int getWheelCount() {
      return this.wheels.size();
   }

   public VehicleScript.Wheel getWheel(int var1) {
      return (VehicleScript.Wheel)this.wheels.get(var1);
   }

   public VehicleScript.Wheel getWheelById(String var1) {
      for(int var2 = 0; var2 < this.wheels.size(); ++var2) {
         VehicleScript.Wheel var3 = (VehicleScript.Wheel)this.wheels.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public int getIndexOfWheelById(String var1) {
      for(int var2 = 0; var2 < this.wheels.size(); ++var2) {
         VehicleScript.Wheel var3 = (VehicleScript.Wheel)this.wheels.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public int getPassengerCount() {
      return this.passengers.size();
   }

   public VehicleScript.Passenger getPassenger(int var1) {
      return (VehicleScript.Passenger)this.passengers.get(var1);
   }

   public VehicleScript.Passenger getPassengerById(String var1) {
      for(int var2 = 0; var2 < this.passengers.size(); ++var2) {
         VehicleScript.Passenger var3 = (VehicleScript.Passenger)this.passengers.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public int getPassengerIndex(String var1) {
      for(int var2 = 0; var2 < this.passengers.size(); ++var2) {
         VehicleScript.Passenger var3 = (VehicleScript.Passenger)this.passengers.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public int getFrontEndHealth() {
      return this.frontEndHealth;
   }

   public int getRearEndHealth() {
      return this.rearEndHealth;
   }

   public int getStorageCapacity() {
      return this.storageCapacity;
   }

   public int getSkinCount() {
      return this.skins.size();
   }

   public VehicleScript.Skin getSkin(int var1) {
      return (VehicleScript.Skin)this.skins.get(var1);
   }

   public int getAreaCount() {
      return this.areas.size();
   }

   public VehicleScript.Area getArea(int var1) {
      return (VehicleScript.Area)this.areas.get(var1);
   }

   public VehicleScript.Area getAreaById(String var1) {
      for(int var2 = 0; var2 < this.areas.size(); ++var2) {
         VehicleScript.Area var3 = (VehicleScript.Area)this.areas.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public int getIndexOfAreaById(String var1) {
      for(int var2 = 0; var2 < this.areas.size(); ++var2) {
         VehicleScript.Area var3 = (VehicleScript.Area)this.areas.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public int getPartCount() {
      return this.parts.size();
   }

   public VehicleScript.Part getPart(int var1) {
      return (VehicleScript.Part)this.parts.get(var1);
   }

   public VehicleScript.Part getPartById(String var1) {
      for(int var2 = 0; var2 < this.parts.size(); ++var2) {
         VehicleScript.Part var3 = (VehicleScript.Part)this.parts.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public int getIndexOfPartById(String var1) {
      for(int var2 = 0; var2 < this.parts.size(); ++var2) {
         VehicleScript.Part var3 = (VehicleScript.Part)this.parts.get(var2);
         if (var3.id != null && var3.id.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private VehicleScript.Anim getAnimationById(String var1, ArrayList var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         VehicleScript.Anim var4 = (VehicleScript.Anim)var2.get(var3);
         if (var4.id != null && var4.id.equals(var1)) {
            return var4;
         }
      }

      return null;
   }

   private VehicleScript.Position getPositionById(String var1, ArrayList var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         VehicleScript.Position var4 = (VehicleScript.Position)var2.get(var3);
         if (var4.id != null && var4.id.equals(var1)) {
            return var4;
         }
      }

      return null;
   }

   public boolean globMatch(String var1, String var2) {
      Pattern var3 = Pattern.compile(var1.replaceAll("\\*", ".*"));
      return var3.matcher(var2).matches();
   }

   public int getGearRatioCount() {
      return this.gearRatioCount;
   }

   public int getSeats() {
      return this.seats;
   }

   public void setSeats(int var1) {
      this.seats = var1;
   }

   public int getMechanicType() {
      return this.mechanicType;
   }

   public void setMechanicType(int var1) {
      this.mechanicType = var1;
   }

   public int getEngineRepairLevel() {
      return this.engineRepairLevel;
   }

   public int getHeadlightConfigLevel() {
      return 2;
   }

   public void setEngineRepairLevel(int var1) {
      this.engineRepairLevel = var1;
   }

   public float getPlayerDamageProtection() {
      return this.playerDamageProtection;
   }

   public void setPlayerDamageProtection(float var1) {
      this.playerDamageProtection = var1;
   }

   public float getForcedHue() {
      return this.forcedHue;
   }

   public void setForcedHue(float var1) {
      this.forcedHue = var1;
   }

   public float getForcedSat() {
      return this.forcedSat;
   }

   public void setForcedSat(float var1) {
      this.forcedSat = var1;
   }

   public float getForcedVal() {
      return this.forcedVal;
   }

   public void setForcedVal(float var1) {
      this.forcedVal = var1;
   }

   public String getEngineRPMType() {
      return this.engineRPMType;
   }

   public void setEngineRPMType(String var1) {
      this.engineRPMType = var1;
   }

   public float getOffroadEfficiency() {
      return this.offroadEfficiency;
   }

   public void setOffroadEfficiency(float var1) {
      this.offroadEfficiency = var1;
   }

   private static class Block implements VehicleScript.BlockElement {
      public String type;
      public String id;
      public ArrayList elements;
      public ArrayList values;
      public ArrayList children;

      private Block() {
         this.elements = new ArrayList();
         this.values = new ArrayList();
         this.children = new ArrayList();
      }

      public VehicleScript.Block asBlock() {
         return this;
      }

      public VehicleScript.Value asValue() {
         return null;
      }

      public boolean isEmpty() {
         return this.elements.isEmpty();
      }

      // $FF: synthetic method
      Block(Object var1) {
         this();
      }
   }

   private static class Value implements VehicleScript.BlockElement {
      String string;

      private Value() {
      }

      public VehicleScript.Block asBlock() {
         return null;
      }

      public VehicleScript.Value asValue() {
         return this;
      }

      // $FF: synthetic method
      Value(Object var1) {
         this();
      }
   }

   private interface BlockElement {
      VehicleScript.Block asBlock();

      VehicleScript.Value asValue();
   }

   public static class Position {
      public String id;
      public Vector3f offset = new Vector3f();
      public Vector3f rotate = new Vector3f();
      public String area = null;

      public Vector3f getOffset() {
         return this.offset;
      }

      public Vector3f getRotate() {
         return this.rotate;
      }

      VehicleScript.Position makeCopy() {
         VehicleScript.Position var1 = new VehicleScript.Position();
         var1.id = this.id;
         var1.offset.set((Vector3fc)this.offset);
         var1.rotate.set((Vector3fc)this.rotate);
         return var1;
      }
   }

   public static class Window {
      public boolean openable;

      VehicleScript.Window makeCopy() {
         VehicleScript.Window var1 = new VehicleScript.Window();
         var1.openable = this.openable;
         return var1;
      }
   }

   public static class Door {
      VehicleScript.Door makeCopy() {
         VehicleScript.Door var1 = new VehicleScript.Door();
         return var1;
      }
   }

   public static class Part {
      public String id = "Unknown";
      public String parent;
      public ArrayList itemType;
      public VehicleScript.Container container;
      public String area;
      public String wheel;
      public HashMap tables;
      public HashMap luaFunctions;
      public ArrayList models;
      public VehicleScript.Door door;
      public VehicleScript.Window window;
      public ArrayList anims;
      public String category;
      public boolean specificItem = true;
      public boolean mechanicRequireKey = false;
      public boolean repairMechanic = false;
      public boolean hasLightsRear = false;

      public boolean isMechanicRequireKey() {
         return this.mechanicRequireKey;
      }

      public void setMechanicRequireKey(boolean var1) {
         this.mechanicRequireKey = var1;
      }

      public boolean isRepairMechanic() {
         return this.repairMechanic;
      }

      public void setRepairMechanic(boolean var1) {
         this.repairMechanic = var1;
      }

      VehicleScript.Part makeCopy() {
         VehicleScript.Part var1 = new VehicleScript.Part();
         var1.id = this.id;
         var1.parent = this.parent;
         if (this.itemType != null) {
            var1.itemType = new ArrayList();
            var1.itemType.addAll(this.itemType);
         }

         if (this.container != null) {
            var1.container = this.container.makeCopy();
         }

         var1.area = this.area;
         var1.wheel = this.wheel;
         if (this.tables != null) {
            var1.tables = new HashMap();
            Iterator var2 = this.tables.entrySet().iterator();

            while(var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               KahluaTable var4 = LuaManager.copyTable((KahluaTable)var3.getValue());
               var1.tables.put(var3.getKey(), var4);
            }
         }

         if (this.luaFunctions != null) {
            var1.luaFunctions = new HashMap();
            var1.luaFunctions.putAll(this.luaFunctions);
         }

         int var5;
         if (this.models != null) {
            var1.models = new ArrayList();

            for(var5 = 0; var5 < this.models.size(); ++var5) {
               var1.models.add(((VehicleScript.Model)this.models.get(var5)).makeCopy());
            }
         }

         if (this.door != null) {
            var1.door = this.door.makeCopy();
         }

         if (this.window != null) {
            var1.window = this.window.makeCopy();
         }

         if (this.anims != null) {
            var1.anims = new ArrayList();

            for(var5 = 0; var5 < this.anims.size(); ++var5) {
               var1.anims.add(((VehicleScript.Anim)this.anims.get(var5)).makeCopy());
            }
         }

         var1.category = this.category;
         var1.specificItem = this.specificItem;
         var1.mechanicRequireKey = this.mechanicRequireKey;
         var1.repairMechanic = this.repairMechanic;
         var1.hasLightsRear = this.hasLightsRear;
         return var1;
      }
   }

   public static class Container {
      public int capacity;
      public int seat = -1;
      public String seatID;
      public String luaTest;
      public String contentType;
      public boolean conditionAffectsCapacity = false;

      VehicleScript.Container makeCopy() {
         VehicleScript.Container var1 = new VehicleScript.Container();
         var1.capacity = this.capacity;
         var1.seat = this.seat;
         var1.seatID = this.seatID;
         var1.luaTest = this.luaTest;
         var1.contentType = this.contentType;
         var1.conditionAffectsCapacity = this.conditionAffectsCapacity;
         return var1;
      }
   }

   public static class Area {
      public String id;
      public float x;
      public float y;
      public float w;
      public float h;

      public String getId() {
         return this.id;
      }

      public Double getX() {
         return BoxedStaticValues.toDouble((double)this.x);
      }

      public Double getY() {
         return BoxedStaticValues.toDouble((double)this.y);
      }

      public Double getW() {
         return BoxedStaticValues.toDouble((double)this.w);
      }

      public Double getH() {
         return BoxedStaticValues.toDouble((double)this.h);
      }

      public void setX(Double var1) {
         this.x = var1.floatValue();
      }

      public void setY(Double var1) {
         this.y = var1.floatValue();
      }

      public void setW(Double var1) {
         this.w = var1.floatValue();
      }

      public void setH(Double var1) {
         this.h = var1.floatValue();
      }

      private VehicleScript.Area makeCopy() {
         VehicleScript.Area var1 = new VehicleScript.Area();
         var1.id = this.id;
         var1.x = this.x;
         var1.y = this.y;
         var1.w = this.w;
         var1.h = this.h;
         return var1;
      }
   }

   public static class Skin {
      public String texture;
   }

   public static class Model {
      public String id;
      public String file;
      public float scale = 1.0F;
      public Vector3f offset = new Vector3f();
      public Vector3f rotate = new Vector3f();

      VehicleScript.Model makeCopy() {
         VehicleScript.Model var1 = new VehicleScript.Model();
         var1.id = this.id;
         var1.file = this.file;
         var1.scale = this.scale;
         var1.offset = new Vector3f(this.offset);
         var1.rotate = new Vector3f(this.rotate);
         return var1;
      }
   }

   public static class Passenger {
      public String id;
      public ArrayList anims = new ArrayList();
      public ArrayList switchSeats = new ArrayList();
      public boolean hasRoof = true;
      public String door;
      public String door2;
      public String area;
      public ArrayList positions = new ArrayList();

      public String getId() {
         return this.id;
      }

      public VehicleScript.Passenger makeCopy() {
         VehicleScript.Passenger var1 = new VehicleScript.Passenger();
         var1.id = this.id;

         int var2;
         for(var2 = 0; var2 < this.anims.size(); ++var2) {
            var1.anims.add(((VehicleScript.Anim)this.anims.get(var2)).makeCopy());
         }

         for(var2 = 0; var2 < this.switchSeats.size(); ++var2) {
            var1.switchSeats.add(((VehicleScript.Passenger.SwitchSeat)this.switchSeats.get(var2)).makeCopy());
         }

         var1.hasRoof = this.hasRoof;
         var1.door = this.door;
         var1.door2 = this.door2;
         var1.area = this.area;

         for(var2 = 0; var2 < this.positions.size(); ++var2) {
            var1.positions.add(((VehicleScript.Position)this.positions.get(var2)).makeCopy());
         }

         return var1;
      }

      public VehicleScript.Position getPositionById(String var1) {
         for(int var2 = 0; var2 < this.positions.size(); ++var2) {
            VehicleScript.Position var3 = (VehicleScript.Position)this.positions.get(var2);
            if (var3.id != null && var3.id.equals(var1)) {
               return var3;
            }
         }

         return null;
      }

      public VehicleScript.Passenger.SwitchSeat getSwitchSeatById(String var1) {
         for(int var2 = 0; var2 < this.switchSeats.size(); ++var2) {
            VehicleScript.Passenger.SwitchSeat var3 = (VehicleScript.Passenger.SwitchSeat)this.switchSeats.get(var2);
            if (var3.id != null && var3.id.equals(var1)) {
               return var3;
            }
         }

         return null;
      }

      public static class SwitchSeat {
         public String id;
         public int seat;
         public String anim;
         public float rate = 1.0F;
         public String sound;

         public VehicleScript.Passenger.SwitchSeat makeCopy() {
            VehicleScript.Passenger.SwitchSeat var1 = new VehicleScript.Passenger.SwitchSeat();
            var1.id = this.id;
            var1.seat = this.seat;
            var1.anim = this.anim;
            var1.rate = this.rate;
            var1.sound = this.sound;
            return var1;
         }
      }
   }

   public static class Anim {
      public String id;
      public String anim;
      public float rate = 1.0F;
      public Vector3f offset = new Vector3f();
      public Vector3f angle = new Vector3f();
      public String sound;

      VehicleScript.Anim makeCopy() {
         VehicleScript.Anim var1 = new VehicleScript.Anim();
         var1.id = this.id;
         var1.anim = this.anim;
         var1.rate = this.rate;
         var1.offset = new Vector3f(this.offset);
         var1.angle = new Vector3f(this.angle);
         var1.sound = this.sound;
         return var1;
      }
   }

   public static class Wheel {
      public String id;
      public String model;
      public boolean front;
      public Vector3f offset = new Vector3f();
      public float radius = 0.5F;
      public float width = 0.4F;

      public Vector3f getOffset() {
         return this.offset;
      }

      VehicleScript.Wheel makeCopy() {
         VehicleScript.Wheel var1 = new VehicleScript.Wheel();
         var1.id = this.id;
         var1.model = this.model;
         var1.front = this.front;
         var1.offset = new Vector3f(this.offset);
         var1.radius = this.radius;
         var1.width = this.width;
         return var1;
      }
   }

   public static class Sounds {
      public boolean hornEnable = false;
      public String horn = new String();
      public boolean backSignalEnable = false;
      public String backSignal = new String();
   }

   public static class LightBar {
      public boolean enable = false;
      public String soundSiren0 = new String();
      public String soundSiren1 = new String();
      public String soundSiren2 = new String();
   }
}
