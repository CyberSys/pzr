package zombie.network.packets;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkStrikeAI;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;
import zombie.world.WorldDictionary;

public class HitPacket extends NetworkStrikeAI.Strike implements INetworkPacket {
   public short wielderId;
   public short targetId;
   public String typeAsString;
   public String targetType;

   public void parse(ByteBuffer var1) {
      this.wielderId = var1.getShort();
      this.objType = var1.get();
      this.targetId = var1.getShort();
      this.dead = var1.get() == 1;
      if (this.objType == 1) {
         this.angle = var1.getFloat();
         this.hitReaction = GameWindow.ReadString(var1);
      } else {
         this.angle = 0.0F;
         this.hitReaction = null;
      }

      this.doShove = var1.get() == 1;
      this.damageSplit = var1.getFloat();
      this.bIgnoreDamage = var1.get() == 1;
      this.bCloseKilled = var1.get() == 1;
      this.isCrit = var1.get() == 1;
      this.rangeDel = var1.getFloat();
      this.tx = var1.getFloat();
      this.ty = var1.getFloat();
      this.tz = var1.getFloat();
      this.ohit = var1.getFloat();
      this.ohitx = var1.getFloat();
      this.ohity = var1.getFloat();
      this.charge = var1.getFloat();
      this.aiming = var1.getFloat();
      this.zombieHitReaction = GameWindow.ReadString(var1);
      this.zombieFlags = var1.getShort();
      this.helmetFall = var1.get() == 1;
      this.jawStabAttach = var1.get() == 1;
      this.isAimAtFloor = var1.get() == 1;
      this.attackType = GameWindow.ReadString(var1);
      if (GameServer.bServer) {
         this.player = (IsoPlayer)GameServer.IDToPlayerMap.get(Integer.valueOf(this.wielderId));
         if (this.objType == 1) {
            this.zom = ServerMap.instance.ZombieMap.get(this.targetId);
            this.vehicle = null;
            this.targetType = "Zombie";
         } else if (this.objType == 2) {
            this.zom = (IsoGameCharacter)GameServer.IDToPlayerMap.get(Integer.valueOf(this.targetId));
            this.vehicle = null;
            this.targetType = "Player";
         } else if (this.objType == 3) {
            this.zom = null;
            this.vehicle = VehicleManager.instance.getVehicleByID(this.targetId);
            this.targetType = "Vehicle";
         } else {
            this.zom = null;
            this.vehicle = null;
            this.targetType = "None";
         }
      } else if (GameClient.bClient) {
         this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(Integer.valueOf(this.wielderId));
         if (this.objType == 1) {
            this.zom = (IsoGameCharacter)GameClient.IDToZombieMap.get(this.targetId);
            this.vehicle = null;
            this.targetType = "Zombie";
         } else if (this.objType == 2) {
            this.zom = (IsoGameCharacter)GameClient.IDToPlayerMap.get(Integer.valueOf(this.targetId));
            this.vehicle = null;
            this.targetType = "Player";
         } else if (this.objType == 3) {
            this.zom = null;
            this.vehicle = VehicleManager.instance.getVehicleByID(this.targetId);
            this.targetType = "Vehicle";
         } else {
            this.zom = null;
            this.vehicle = null;
            this.targetType = "None";
         }
      } else {
         this.player = null;
         this.zom = null;
         this.vehicle = null;
      }

      boolean var2 = var1.get() == 1;
      if (var2) {
         short var3 = var1.getShort();
         byte var4 = var1.get();
         this.typeAsString = WorldDictionary.getItemTypeFromID(var3);
         if (this.player != null) {
            this.item = this.player.getPrimaryHandItem();
            if (this.item == null || this.item.getRegistry_id() != var3) {
               this.item = InventoryItemFactory.CreateItem(var3);
            }

            if (this.item != null) {
               try {
                  this.item.load(var1, 184);
               } catch (BufferUnderflowException | IOException var6) {
                  DebugLog.Multiplayer.error("HandWeapon could not be load");
                  var6.printStackTrace();
                  this.item = InventoryItemFactory.CreateItem("Base.BareHands");
               }
            }
         }
      } else {
         this.item = InventoryItemFactory.CreateItem("Base.BareHands");
      }

      this.lifeTime = System.currentTimeMillis() + Math.min(3000L, (long)(IsoUtils.DistanceTo(this.player.x, this.player.y, this.tx, this.ty) * 1000.0F));
      DebugLog.log(DebugType.Multiplayer, "HitPacket receive: " + this.getDescription());
   }

   public void write(ByteBufferWriter var1) {
      var1.putShort(this.wielderId);
      var1.putByte(this.objType);
      var1.putShort(this.targetId);
      var1.putBoolean(this.dead);
      if (this.objType == 1) {
         var1.putFloat(this.angle);
         var1.putUTF(this.hitReaction);
      }

      var1.putBoolean(this.doShove);
      var1.putFloat(this.damageSplit);
      var1.putBoolean(this.bIgnoreDamage);
      var1.putBoolean(this.bCloseKilled);
      var1.putBoolean(this.isCrit);
      var1.putFloat(this.rangeDel);
      var1.putFloat(this.tx);
      var1.putFloat(this.ty);
      var1.putFloat(this.tz);
      var1.putFloat(this.ohit);
      var1.putFloat(this.ohitx);
      var1.putFloat(this.ohity);
      var1.putFloat(this.charge);
      var1.putFloat(this.aiming);
      var1.putUTF(this.zombieHitReaction);
      var1.putShort(this.zombieFlags);
      var1.putBoolean(this.helmetFall);
      var1.putBoolean(this.jawStabAttach);
      var1.putBoolean(this.isAimAtFloor);
      var1.putUTF(this.attackType);
      if (this.item == null) {
         var1.putByte((byte)0);
      } else {
         var1.putByte((byte)1);

         try {
            this.item.save(var1.bb, false);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      DebugLog.log(DebugType.Multiplayer, "HitPacket send: " + this.getDescription());
   }

   public int getPacketSizeBytes() {
      return 0;
   }

   public boolean check() {
      if (this.player == null) {
         DebugLog.log(DebugType.Multiplayer, "HitPacket.check failed, wilder not found");
         return false;
      } else if (this.item == null) {
         DebugLog.log(DebugType.Multiplayer, "HitPacket.check failed, weapon not found");
         return false;
      } else {
         if (this.objType != 2 && this.objType != 1) {
            if (this.objType == 3 && this.vehicle == null) {
               DebugLog.log(DebugType.Multiplayer, "HitPacket.check failed, vehicle not found");
               return false;
            }
         } else if (this.zom == null) {
            DebugLog.log(DebugType.Multiplayer, "HitPacket.check failed, target not found");
            return false;
         }

         return true;
      }
   }

   public String getDescription() {
      String var1 = String.format("Player (%d) hit %s (%d) with %s: dmg=%f reactions=( %s, %s ), dead=%b", this.wielderId, this.targetType, this.targetId, this.item.getFullType(), this.bIgnoreDamage ? 0.0F : this.damageSplit, this.zombieHitReaction, this.hitReaction, this.dead);
      if (this.zom != null) {
         if (this.zom instanceof IsoZombie) {
            var1 = var1 + ", health=" + this.zom.getHealth();
         } else if (this.zom instanceof IsoPlayer) {
            var1 = var1 + ", health=" + this.zom.getBodyDamage().getHealth();
         }

         var1 = var1 + ", states=( " + this.zom.getPreviousActionContextStateName() + " > " + this.zom.getCurrentActionContextStateName() + " )";
      }

      return var1;
   }

   public void set(IsoPlayer var1, IsoMovingObject var2, HandWeapon var3, float var4, boolean var5, float var6, boolean var7, boolean var8, Boolean var9) {
      this.player = var1;
      this.wielderId = (short)var1.OnlineID;
      this.doShove = var1.bDoShove;
      this.damageSplit = var4;
      this.bIgnoreDamage = var5;
      this.isCrit = var9 == null ? var1.isCrit : var9;
      this.rangeDel = var6;
      this.charge = var1.useChargeDelta;
      this.aiming = (float)var1.getPerkLevel(PerkFactory.Perks.Aiming);
      this.zombieHitReaction = var1.getVariableString("ZombieHitReaction");
      this.zombieFlags = 0;
      this.helmetFall = var7;
      this.jawStabAttach = var8;
      this.isAimAtFloor = var1.isAimAtFloor();
      this.item = var3;
      if (var2 == null) {
         this.targetType = "None";
         this.objType = 4;
         this.targetId = 0;
         this.dead = false;
         this.bCloseKilled = false;
         IsoGridSquare var10 = var1.getAttackTargetSquare();
         if (var10 != null) {
            this.tx = (float)var10.getX();
            this.ty = (float)var10.getY();
            this.tz = (float)var10.getZ();
         } else {
            this.tx = 0.0F;
            this.ty = 0.0F;
            this.tz = 0.0F;
         }

         this.ohit = 0.0F;
         this.ohitx = var1.getForwardDirection().x;
         this.ohity = var1.getForwardDirection().y;
      } else {
         if (var2 instanceof IsoZombie) {
            this.targetType = "Zombie";
            this.zom = (IsoZombie)var2;
            this.objType = 1;
            this.targetId = ((IsoZombie)var2).OnlineID;
            this.dead = ((IsoZombie)var2).isDead();
            this.angle = ((IsoZombie)var2).getAnimAngleRadians();
            this.hitReaction = ((IsoZombie)var2).getHitReaction();
         } else if (var2 instanceof IsoPlayer) {
            this.targetType = "Player";
            this.zom = (IsoPlayer)var2;
            this.objType = 2;
            this.targetId = (short)((IsoPlayer)var2).OnlineID;
            this.dead = ((IsoPlayer)var2).isDead();
         } else if (var2 instanceof BaseVehicle) {
            this.targetType = "Vehicle";
            this.objType = 3;
            this.targetId = ((BaseVehicle)var2).VehicleID;
            this.dead = false;
         }

         this.bCloseKilled = var2.isCloseKilled();
         this.tx = var2.getX();
         this.ty = var2.getY();
         this.tz = var2.getZ();
         this.ohit = var2.getHitForce();
         this.ohitx = var2.getHitDir().x;
         this.ohity = var2.getHitDir().y;
         this.attackType = var1.getAttackType();
         if (var2 instanceof IsoZombie) {
            short var11 = (short)(((IsoZombie)var2).bKnockedDown ? 1 : 0);
            var11 |= (short)(((IsoZombie)var2).isFakeDead() ? 2 : 0);
            var11 |= (short)(((IsoZombie)var2).isHitFromBehind() ? 4 : 0);
            var11 |= (short)(((IsoZombie)var2).bStaggerBack ? 8 : 0);
            var11 |= (short)(((IsoZombie)var2).getVariableBoolean("bKnifeDeath") ? 16 : 0);
            var11 |= (short)(((IsoZombie)var2).isFallOnFront() ? 32 : 0);
            this.zombieFlags = var11;
         }
      }

   }

   public static class HitVehicle implements INetworkPacket {
      public static final short MAX_DELAY_MS = 650;
      public int wielderID;
      public int targetID;
      public byte targetType;
      public float speed;
      public float dot;
      public float hitDirX;
      public float hitDirY;
      public short flags;
      public float health;
      public float angle;
      public float x;
      public float y;
      public float z;
      public float reanimateTimer;
      public float timestamp;
      public IsoGameCharacter wielder;
      public IsoGameCharacter target;
      public BaseVehicle vehicle;
      public int vehicleID;

      public void parse(ByteBuffer var1) {
         this.wielderID = var1.getInt();
         this.targetID = var1.getInt();
         this.targetType = var1.get();
         this.speed = var1.getFloat();
         this.dot = var1.getFloat();
         this.hitDirX = var1.getFloat();
         this.hitDirY = var1.getFloat();
         this.flags = var1.getShort();
         this.health = var1.getFloat();
         this.angle = var1.getFloat();
         this.x = var1.getFloat();
         this.y = var1.getFloat();
         this.z = var1.getFloat();
         this.reanimateTimer = var1.getFloat();
         this.timestamp = var1.getFloat();
         if (GameServer.bServer) {
            this.wielder = (IsoGameCharacter)GameServer.IDToPlayerMap.get(this.wielderID);
            if (this.wielder != null) {
               this.vehicle = this.wielder.getVehicle();
               if (this.vehicle != null) {
                  this.vehicleID = this.vehicle.getId();
               }
            }

            if (this.targetType == 1) {
               this.target = ServerMap.instance.ZombieMap.get((short)this.targetID);
            } else if (this.targetType == 2) {
               this.target = (IsoGameCharacter)GameServer.IDToPlayerMap.get(this.targetID);
            } else {
               this.target = null;
            }
         } else if (GameClient.bClient) {
            this.wielder = (IsoGameCharacter)GameClient.IDToPlayerMap.get(this.wielderID);
            if (this.wielder != null) {
               this.vehicle = this.wielder.getVehicle();
               if (this.vehicle != null) {
                  this.vehicleID = this.vehicle.getId();
               }
            }

            if (this.targetType == 1) {
               this.target = (IsoGameCharacter)GameClient.IDToZombieMap.get((short)this.targetID);
            } else if (this.targetType == 2) {
               this.target = (IsoGameCharacter)GameClient.IDToPlayerMap.get(this.targetID);
            } else {
               this.target = null;
            }
         } else {
            this.wielder = null;
            this.target = null;
            this.vehicle = null;
         }

      }

      public void write(ByteBufferWriter var1) {
         var1.putInt(this.wielderID);
         var1.putInt(this.targetID);
         var1.putByte(this.targetType);
         var1.putFloat(this.speed);
         var1.putFloat(this.dot);
         var1.putFloat(this.hitDirX);
         var1.putFloat(this.hitDirY);
         var1.putShort(this.flags);
         var1.putFloat(this.health);
         var1.putFloat(this.angle);
         var1.putFloat(this.x);
         var1.putFloat(this.y);
         var1.putFloat(this.z);
         var1.putFloat(this.reanimateTimer);
         var1.putFloat(this.timestamp);
      }

      public void set(IsoGameCharacter var1, IsoGameCharacter var2, BaseVehicle var3, float var4, float var5, float var6, float var7) {
         this.wielderID = var1.getOnlineID();
         this.targetID = var2.getOnlineID();
         this.speed = var4;
         this.dot = var5;
         this.hitDirX = var6;
         this.hitDirY = var7;
         this.flags = 0;
         this.health = var2.getHealth();
         this.angle = var2.getDirectionAngle();
         this.x = var2.x;
         this.y = var2.y;
         this.z = var2.z;
         this.flags = (short)(this.flags | (var2.isDead() ? 1 : 0));
         if (var2 instanceof IsoZombie) {
            IsoZombie var8 = (IsoZombie)var2;
            this.targetType = 1;
            this.flags = (short)(this.flags | (var8.bStaggerBack ? 2 : 0));
            this.flags = (short)(this.flags | (var8.bKnockedDown ? 4 : 0));
            this.flags = (short)(this.flags | (var8.isBecomeCrawler() ? 8 : 0));
            this.flags = (short)(this.flags | (var8.isHitFromBehind() ? 16 : 0));
            this.flags = (short)(this.flags | (var8.isFakeDead() ? 32 : 0));
            this.flags = (short)(this.flags | ("Floor".equals(var8.getHitReaction()) ? 64 : 0));
            this.timestamp = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime());
            this.reanimateTimer = Math.max(var8.networkAI.reanimateTimer, 0.0F);
         } else if (var2 instanceof IsoPlayer) {
            this.targetType = 2;
         }

         this.wielder = var1;
         this.target = var2;
         this.vehicle = var3;
         this.vehicleID = var3.getId();
      }

      public int getPacketSizeBytes() {
         return 0;
      }

      public String getDescription() {
         String var1 = String.format("Player (%d) hit character (%d) by vehicle %d", this.wielderID, this.targetID, this.vehicleID);
         if (this.target != null) {
            if (this.target instanceof IsoZombie) {
               var1 = var1 + ", health=" + this.target.getHealth() + " (" + this.health + ")";
               var1 = var1 + ", rt=" + ((IsoZombie)this.target).networkAI.reanimateTimer;
            } else if (this.target instanceof IsoPlayer) {
               var1 = var1 + ", health=" + this.target.getBodyDamage().getHealth() + " (" + this.health + ")";
            }

            var1 = var1 + ", states=( " + this.target.getPreviousActionContextStateName() + " > " + this.target.getCurrentActionContextStateName() + " )";
         }

         return var1;
      }

      public boolean check() {
         if (this.wielder == null) {
            DebugLog.log(DebugType.Multiplayer, String.format("HitVehicle.check failed, wilder %d not found", this.wielderID));
            return false;
         } else if (this.target == null) {
            DebugLog.log(DebugType.Multiplayer, String.format("HitVehicle.check failed, target %d not found", this.targetID));
            return false;
         } else if (this.vehicle == null) {
            DebugLog.log(DebugType.Multiplayer, String.format("HitVehicle.check failed, vehicle %d not found", this.vehicleID));
            return false;
         } else {
            return true;
         }
      }

      public boolean isTimeout() {
         boolean var1 = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp > 650.0F;
         if (var1) {
            DebugLog.log(DebugType.Multiplayer, "HitVehicle timeout: " + this.getDescription());
         }

         return var1;
      }

      public void process() {
         if (this.targetType == 1) {
            IsoZombie var1 = (IsoZombie)this.target;
            var1.setX(this.x);
            var1.setY(this.y);
            var1.setZ(this.z);
            var1.ensureOnTile();
            var1.setDirectionAngle(this.angle);
            var1.setAttackedBy(this.vehicle.getDriver());
            var1.getHitDir().set(this.hitDirX, this.hitDirY);
            var1.setHitForce(this.speed * 0.15F);
            var1.setTarget(this.wielder);
            var1.bStaggerBack = (this.flags & 2) != 0;
            var1.bKnockedDown = (this.flags & 4) != 0;
            var1.setBecomeCrawler((this.flags & 8) != 0);
            var1.setHitFromBehind((this.flags & 16) != 0);
            var1.setFakeDead((this.flags & 32) != 0);
            if ((this.flags & 64) != 0) {
               var1.setHitReaction("Floor");
            }

            if ((GameServer.bServer || GameClient.bClient) && var1.isDead()) {
               var1.lastPlayerHit = this.wielderID | DeadBodyPacket.DIED_UNDER_VEHICLE;
            }

            var1.setHealth(this.health);
            float var2 = ((float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp) / 30.0F;
            var1.networkAI.reanimateTimer = Math.max(this.reanimateTimer - var2, 0.0F);
            if (GameServer.bServer) {
               this.vehicle.hitCharacter(var1);
            } else if (GameClient.bClient) {
               var1.addBlood(this.speed);
               if (var1.isProne() && var1.emitter != null && !var1.emitter.isPlaying(var1.getHurtSound())) {
                  var1.playHurtSound();
               }
            }
         }

         DebugLog.log(DebugType.Multiplayer, "HitVehicle.process: " + this.getDescription());
      }
   }
}
