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

	public void parse(ByteBuffer byteBuffer) {
		this.wielderId = byteBuffer.getShort();
		this.objType = byteBuffer.get();
		this.targetId = byteBuffer.getShort();
		this.dead = byteBuffer.get() == 1;
		if (this.objType == 1) {
			this.angle = byteBuffer.getFloat();
			this.hitReaction = GameWindow.ReadString(byteBuffer);
		} else {
			this.angle = 0.0F;
			this.hitReaction = null;
		}

		this.doShove = byteBuffer.get() == 1;
		this.damageSplit = byteBuffer.getFloat();
		this.bIgnoreDamage = byteBuffer.get() == 1;
		this.bCloseKilled = byteBuffer.get() == 1;
		this.isCrit = byteBuffer.get() == 1;
		this.rangeDel = byteBuffer.getFloat();
		this.tx = byteBuffer.getFloat();
		this.ty = byteBuffer.getFloat();
		this.tz = byteBuffer.getFloat();
		this.ohit = byteBuffer.getFloat();
		this.ohitx = byteBuffer.getFloat();
		this.ohity = byteBuffer.getFloat();
		this.charge = byteBuffer.getFloat();
		this.aiming = byteBuffer.getFloat();
		this.zombieHitReaction = GameWindow.ReadString(byteBuffer);
		this.zombieFlags = byteBuffer.getShort();
		this.helmetFall = byteBuffer.get() == 1;
		this.jawStabAttach = byteBuffer.get() == 1;
		this.isAimAtFloor = byteBuffer.get() == 1;
		this.attackType = GameWindow.ReadString(byteBuffer);
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

		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			short short1 = byteBuffer.getShort();
			byte byte1 = byteBuffer.get();
			this.typeAsString = WorldDictionary.getItemTypeFromID(short1);
			if (this.player != null) {
				this.item = this.player.getPrimaryHandItem();
				if (this.item == null || this.item.getRegistry_id() != short1) {
					this.item = InventoryItemFactory.CreateItem(short1);
				}

				if (this.item != null) {
					try {
						this.item.load(byteBuffer, 184);
					} catch (BufferUnderflowException | IOException error) {
						DebugLog.Multiplayer.error("HandWeapon could not be load");
						error.printStackTrace();
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

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.wielderId);
		byteBufferWriter.putByte(this.objType);
		byteBufferWriter.putShort(this.targetId);
		byteBufferWriter.putBoolean(this.dead);
		if (this.objType == 1) {
			byteBufferWriter.putFloat(this.angle);
			byteBufferWriter.putUTF(this.hitReaction);
		}

		byteBufferWriter.putBoolean(this.doShove);
		byteBufferWriter.putFloat(this.damageSplit);
		byteBufferWriter.putBoolean(this.bIgnoreDamage);
		byteBufferWriter.putBoolean(this.bCloseKilled);
		byteBufferWriter.putBoolean(this.isCrit);
		byteBufferWriter.putFloat(this.rangeDel);
		byteBufferWriter.putFloat(this.tx);
		byteBufferWriter.putFloat(this.ty);
		byteBufferWriter.putFloat(this.tz);
		byteBufferWriter.putFloat(this.ohit);
		byteBufferWriter.putFloat(this.ohitx);
		byteBufferWriter.putFloat(this.ohity);
		byteBufferWriter.putFloat(this.charge);
		byteBufferWriter.putFloat(this.aiming);
		byteBufferWriter.putUTF(this.zombieHitReaction);
		byteBufferWriter.putShort(this.zombieFlags);
		byteBufferWriter.putBoolean(this.helmetFall);
		byteBufferWriter.putBoolean(this.jawStabAttach);
		byteBufferWriter.putBoolean(this.isAimAtFloor);
		byteBufferWriter.putUTF(this.attackType);
		if (this.item == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			try {
				this.item.save(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
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
		String string = String.format("Player (%d) hit %s (%d) with %s: dmg=%f reactions=( %s, %s ), dead=%b", this.wielderId, this.targetType, this.targetId, this.item.getFullType(), this.bIgnoreDamage ? 0.0F : this.damageSplit, this.zombieHitReaction, this.hitReaction, this.dead);
		if (this.zom != null) {
			if (this.zom instanceof IsoZombie) {
				string = string + ", health=" + this.zom.getHealth();
			} else if (this.zom instanceof IsoPlayer) {
				string = string + ", health=" + this.zom.getBodyDamage().getHealth();
			}

			string = string + ", states=( " + this.zom.getPreviousActionContextStateName() + " > " + this.zom.getCurrentActionContextStateName() + " )";
		}

		return string;
	}

	public void set(IsoPlayer player, IsoMovingObject movingObject, HandWeapon handWeapon, float float1, boolean boolean1, float float2, boolean boolean2, boolean boolean3, Boolean Boolean1) {
		this.player = player;
		this.wielderId = (short)player.OnlineID;
		this.doShove = player.bDoShove;
		this.damageSplit = float1;
		this.bIgnoreDamage = boolean1;
		this.isCrit = Boolean1 == null ? player.isCrit : Boolean1;
		this.rangeDel = float2;
		this.charge = player.useChargeDelta;
		this.aiming = (float)player.getPerkLevel(PerkFactory.Perks.Aiming);
		this.zombieHitReaction = player.getVariableString("ZombieHitReaction");
		this.zombieFlags = 0;
		this.helmetFall = boolean2;
		this.jawStabAttach = boolean3;
		this.isAimAtFloor = player.isAimAtFloor();
		this.item = handWeapon;
		if (movingObject == null) {
			this.targetType = "None";
			this.objType = 4;
			this.targetId = 0;
			this.dead = false;
			this.bCloseKilled = false;
			IsoGridSquare square = player.getAttackTargetSquare();
			if (square != null) {
				this.tx = (float)square.getX();
				this.ty = (float)square.getY();
				this.tz = (float)square.getZ();
			} else {
				this.tx = 0.0F;
				this.ty = 0.0F;
				this.tz = 0.0F;
			}

			this.ohit = 0.0F;
			this.ohitx = player.getForwardDirection().x;
			this.ohity = player.getForwardDirection().y;
		} else {
			if (movingObject instanceof IsoZombie) {
				this.targetType = "Zombie";
				this.zom = (IsoZombie)movingObject;
				this.objType = 1;
				this.targetId = ((IsoZombie)movingObject).OnlineID;
				this.dead = ((IsoZombie)movingObject).isDead();
				this.angle = ((IsoZombie)movingObject).getAnimAngleRadians();
				this.hitReaction = ((IsoZombie)movingObject).getHitReaction();
			} else if (movingObject instanceof IsoPlayer) {
				this.targetType = "Player";
				this.zom = (IsoPlayer)movingObject;
				this.objType = 2;
				this.targetId = (short)((IsoPlayer)movingObject).OnlineID;
				this.dead = ((IsoPlayer)movingObject).isDead();
			} else if (movingObject instanceof BaseVehicle) {
				this.targetType = "Vehicle";
				this.objType = 3;
				this.targetId = ((BaseVehicle)movingObject).VehicleID;
				this.dead = false;
			}

			this.bCloseKilled = movingObject.isCloseKilled();
			this.tx = movingObject.getX();
			this.ty = movingObject.getY();
			this.tz = movingObject.getZ();
			this.ohit = movingObject.getHitForce();
			this.ohitx = movingObject.getHitDir().x;
			this.ohity = movingObject.getHitDir().y;
			this.attackType = player.getAttackType();
			if (movingObject instanceof IsoZombie) {
				short short1 = (short)(((IsoZombie)movingObject).bKnockedDown ? 1 : 0);
				short1 |= (short)(((IsoZombie)movingObject).isFakeDead() ? 2 : 0);
				short1 |= (short)(((IsoZombie)movingObject).isHitFromBehind() ? 4 : 0);
				short1 |= (short)(((IsoZombie)movingObject).bStaggerBack ? 8 : 0);
				short1 |= (short)(((IsoZombie)movingObject).getVariableBoolean("bKnifeDeath") ? 16 : 0);
				short1 |= (short)(((IsoZombie)movingObject).isFallOnFront() ? 32 : 0);
				this.zombieFlags = short1;
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

		public void parse(ByteBuffer byteBuffer) {
			this.wielderID = byteBuffer.getInt();
			this.targetID = byteBuffer.getInt();
			this.targetType = byteBuffer.get();
			this.speed = byteBuffer.getFloat();
			this.dot = byteBuffer.getFloat();
			this.hitDirX = byteBuffer.getFloat();
			this.hitDirY = byteBuffer.getFloat();
			this.flags = byteBuffer.getShort();
			this.health = byteBuffer.getFloat();
			this.angle = byteBuffer.getFloat();
			this.x = byteBuffer.getFloat();
			this.y = byteBuffer.getFloat();
			this.z = byteBuffer.getFloat();
			this.reanimateTimer = byteBuffer.getFloat();
			this.timestamp = byteBuffer.getFloat();
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

		public void write(ByteBufferWriter byteBufferWriter) {
			byteBufferWriter.putInt(this.wielderID);
			byteBufferWriter.putInt(this.targetID);
			byteBufferWriter.putByte(this.targetType);
			byteBufferWriter.putFloat(this.speed);
			byteBufferWriter.putFloat(this.dot);
			byteBufferWriter.putFloat(this.hitDirX);
			byteBufferWriter.putFloat(this.hitDirY);
			byteBufferWriter.putShort(this.flags);
			byteBufferWriter.putFloat(this.health);
			byteBufferWriter.putFloat(this.angle);
			byteBufferWriter.putFloat(this.x);
			byteBufferWriter.putFloat(this.y);
			byteBufferWriter.putFloat(this.z);
			byteBufferWriter.putFloat(this.reanimateTimer);
			byteBufferWriter.putFloat(this.timestamp);
		}

		public void set(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BaseVehicle baseVehicle, float float1, float float2, float float3, float float4) {
			this.wielderID = gameCharacter.getOnlineID();
			this.targetID = gameCharacter2.getOnlineID();
			this.speed = float1;
			this.dot = float2;
			this.hitDirX = float3;
			this.hitDirY = float4;
			this.flags = 0;
			this.health = gameCharacter2.getHealth();
			this.angle = gameCharacter2.getDirectionAngle();
			this.x = gameCharacter2.x;
			this.y = gameCharacter2.y;
			this.z = gameCharacter2.z;
			this.flags = (short)(this.flags | (gameCharacter2.isDead() ? 1 : 0));
			if (gameCharacter2 instanceof IsoZombie) {
				IsoZombie zombie = (IsoZombie)gameCharacter2;
				this.targetType = 1;
				this.flags = (short)(this.flags | (zombie.bStaggerBack ? 2 : 0));
				this.flags = (short)(this.flags | (zombie.bKnockedDown ? 4 : 0));
				this.flags = (short)(this.flags | (zombie.isBecomeCrawler() ? 8 : 0));
				this.flags = (short)(this.flags | (zombie.isHitFromBehind() ? 16 : 0));
				this.flags = (short)(this.flags | (zombie.isFakeDead() ? 32 : 0));
				this.flags = (short)(this.flags | ("Floor".equals(zombie.getHitReaction()) ? 64 : 0));
				this.timestamp = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime());
				this.reanimateTimer = Math.max(zombie.networkAI.reanimateTimer, 0.0F);
			} else if (gameCharacter2 instanceof IsoPlayer) {
				this.targetType = 2;
			}

			this.wielder = gameCharacter;
			this.target = gameCharacter2;
			this.vehicle = baseVehicle;
			this.vehicleID = baseVehicle.getId();
		}

		public int getPacketSizeBytes() {
			return 0;
		}

		public String getDescription() {
			String string = String.format("Player (%d) hit character (%d) by vehicle %d", this.wielderID, this.targetID, this.vehicleID);
			if (this.target != null) {
				if (this.target instanceof IsoZombie) {
					string = string + ", health=" + this.target.getHealth() + " (" + this.health + ")";
					string = string + ", rt=" + ((IsoZombie)this.target).networkAI.reanimateTimer;
				} else if (this.target instanceof IsoPlayer) {
					string = string + ", health=" + this.target.getBodyDamage().getHealth() + " (" + this.health + ")";
				}

				string = string + ", states=( " + this.target.getPreviousActionContextStateName() + " > " + this.target.getCurrentActionContextStateName() + " )";
			}

			return string;
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
			boolean boolean1 = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp > 650.0F;
			if (boolean1) {
				DebugLog.log(DebugType.Multiplayer, "HitVehicle timeout: " + this.getDescription());
			}

			return boolean1;
		}

		public void process() {
			if (this.targetType == 1) {
				IsoZombie zombie = (IsoZombie)this.target;
				zombie.setX(this.x);
				zombie.setY(this.y);
				zombie.setZ(this.z);
				zombie.ensureOnTile();
				zombie.setDirectionAngle(this.angle);
				zombie.setAttackedBy(this.vehicle.getDriver());
				zombie.getHitDir().set(this.hitDirX, this.hitDirY);
				zombie.setHitForce(this.speed * 0.15F);
				zombie.setTarget(this.wielder);
				zombie.bStaggerBack = (this.flags & 2) != 0;
				zombie.bKnockedDown = (this.flags & 4) != 0;
				zombie.setBecomeCrawler((this.flags & 8) != 0);
				zombie.setHitFromBehind((this.flags & 16) != 0);
				zombie.setFakeDead((this.flags & 32) != 0);
				if ((this.flags & 64) != 0) {
					zombie.setHitReaction("Floor");
				}

				if ((GameServer.bServer || GameClient.bClient) && zombie.isDead()) {
					zombie.lastPlayerHit = this.wielderID | DeadBodyPacket.DIED_UNDER_VEHICLE;
				}

				zombie.setHealth(this.health);
				float float1 = ((float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp) / 30.0F;
				zombie.networkAI.reanimateTimer = Math.max(this.reanimateTimer - float1, 0.0F);
				if (GameServer.bServer) {
					this.vehicle.hitCharacter(zombie);
				} else if (GameClient.bClient) {
					zombie.addBlood(this.speed);
					if (zombie.isProne() && zombie.emitter != null && !zombie.emitter.isPlaying(zombie.getHurtSound())) {
						zombie.playHurtSound();
					}
				}
			}

			DebugLog.log(DebugType.Multiplayer, "HitVehicle.process: " + this.getDescription());
		}
	}
}
