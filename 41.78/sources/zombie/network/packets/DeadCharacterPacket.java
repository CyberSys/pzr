package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public abstract class DeadCharacterPacket implements INetworkPacket {
	public short id;
	protected float x;
	protected float y;
	protected float z;
	protected float angle;
	protected IsoDirections direction;
	protected byte characterFlags;
	protected IsoGameCharacter killer;
	protected IsoGameCharacter character;

	public void set(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
		this.id = gameCharacter.getOnlineID();
		this.killer = gameCharacter.getAttackedBy();
		this.x = gameCharacter.getX();
		this.y = gameCharacter.getY();
		this.z = gameCharacter.getZ();
		this.angle = gameCharacter.getAnimAngleRadians();
		this.direction = gameCharacter.getDir();
		this.characterFlags = (byte)(gameCharacter.isFallOnFront() ? 1 : 0);
	}

	public void process() {
		if (this.character != null) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
			if (this.character.getCurrentSquare() != square) {
				DebugLog.Multiplayer.warn(String.format("Corpse %s(%d) teleport: position (%f ; %f) => (%f ; %f)", this.character.getClass().getSimpleName(), this.id, this.character.x, this.character.y, this.x, this.y));
				this.character.setX(this.x);
				this.character.setY(this.y);
				this.character.setZ(this.z);
			}

			if (this.character.getAnimAngleRadians() - this.angle > 1.0E-4F) {
				DebugLog.Multiplayer.warn(String.format("Corpse %s(%d) teleport: direction (%f) => (%f)", this.character.getClass().getSimpleName(), this.id, this.character.getAnimAngleRadians(), this.angle));
				if (this.character.hasAnimationPlayer() && this.character.getAnimationPlayer().isReady() && !this.character.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
					this.character.getAnimationPlayer().setAngle(this.angle);
				} else {
					this.character.getForwardDirection().setDirection(this.angle);
				}
			}

			boolean boolean1 = (this.characterFlags & 1) != 0;
			if (boolean1 != this.character.isFallOnFront()) {
				DebugLog.Multiplayer.warn(String.format("Corpse %s(%d) teleport: pose (%s) => (%s)", this.character.getClass().getSimpleName(), this.id, this.character.isFallOnFront() ? "front" : "back", boolean1 ? "front" : "back"));
				this.character.setFallOnFront(boolean1);
			}

			this.character.setCurrent(square);
			this.character.dir = this.direction;
			this.character.setAttackedBy(this.killer);
			this.character.becomeCorpse();
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.angle = byteBuffer.getFloat();
		this.direction = IsoDirections.fromIndex(byteBuffer.get());
		this.characterFlags = byteBuffer.get();
		byte byte1 = byteBuffer.get();
		boolean boolean1 = true;
		Exception exception;
		short short1;
		if (GameServer.bServer) {
			switch (byte1) {
			case 0: 
				this.killer = null;
				break;
			
			case 1: 
				short1 = byteBuffer.getShort();
				this.killer = (IsoGameCharacter)ServerMap.instance.ZombieMap.get(short1);
				break;
			
			case 2: 
				short1 = byteBuffer.getShort();
				this.killer = (IsoGameCharacter)GameServer.IDToPlayerMap.get(short1);
				break;
			
			default: 
				exception = new Exception("killerIdType:" + byte1);
				exception.printStackTrace();
			
			}
		} else {
			switch (byte1) {
			case 0: 
				this.killer = null;
				break;
			
			case 1: 
				short1 = byteBuffer.getShort();
				this.killer = (IsoGameCharacter)GameClient.IDToZombieMap.get(short1);
				break;
			
			case 2: 
				short1 = byteBuffer.getShort();
				this.killer = (IsoGameCharacter)GameClient.IDToPlayerMap.get(short1);
				break;
			
			default: 
				exception = new Exception("killerIdType:" + byte1);
				exception.printStackTrace();
			
			}
		}
	}

	protected IsoDeadBody getDeadBody() {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
		if (square != null) {
			Iterator iterator = square.getStaticMovingObjects().iterator();
			while (iterator.hasNext()) {
				IsoMovingObject movingObject = (IsoMovingObject)iterator.next();
				if (movingObject instanceof IsoDeadBody && ((IsoDeadBody)movingObject).getOnlineID() == this.id) {
					return (IsoDeadBody)movingObject;
				}
			}
		}

		return null;
	}

	protected void parseDeadBodyInventory(IsoDeadBody deadBody, ByteBuffer byteBuffer) {
		String string = deadBody.readInventory(byteBuffer);
		deadBody.getContainer().setType(string);
	}

	protected void parseDeadBodyHumanVisuals(IsoDeadBody deadBody, ByteBuffer byteBuffer) {
		HumanVisual humanVisual = deadBody.getHumanVisual();
		if (humanVisual != null) {
			try {
				humanVisual.load(byteBuffer, IsoWorld.getWorldVersion());
			} catch (Exception exception) {
				DebugLog.Multiplayer.printException(exception, "Parse dead body HumanVisuals failed", LogSeverity.Error);
			}
		}
	}

	protected void parseCharacterInventory(ByteBuffer byteBuffer) {
		if (this.character != null) {
			if (this.character.getContainer() != null) {
				this.character.getContainer().clear();
			}

			if (this.character.getInventory() != null) {
				this.character.getInventory().clear();
			}

			if (this.character.getWornItems() != null) {
				this.character.getWornItems().clear();
			}

			if (this.character.getAttachedItems() != null) {
				this.character.getAttachedItems().clear();
			}

			this.character.getInventory().setSourceGrid(this.character.getCurrentSquare());
			String string = this.character.readInventory(byteBuffer);
			this.character.getInventory().setType(string);
			this.character.resetModelNextFrame();
		}
	}

	protected void writeCharacterInventory(ByteBufferWriter byteBufferWriter) {
		if (this.character != null) {
			this.character.writeInventory(byteBufferWriter.bb);
		}
	}

	protected void writeCharacterHumanVisuals(ByteBufferWriter byteBufferWriter) {
		if (this.character != null) {
			int int1 = byteBufferWriter.bb.position();
			byteBufferWriter.putByte((byte)1);
			try {
				byteBufferWriter.putBoolean(this.character.isFemale());
				this.character.getVisual().save(byteBufferWriter.bb);
			} catch (Exception exception) {
				byteBufferWriter.bb.put(int1, (byte)0);
				DebugLog.Multiplayer.printException(exception, "Write character HumanVisuals failed", LogSeverity.Error);
			}
		}
	}

	protected void parseCharacterHumanVisuals(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		if (this.character != null && byte1 != 0) {
			try {
				this.character.setFemale(byteBuffer.get() != 0);
				this.character.getVisual().load(byteBuffer, IsoWorld.getWorldVersion());
			} catch (Exception exception) {
				DebugLog.Multiplayer.printException(exception, "Parse character HumanVisuals failed", LogSeverity.Error);
			}
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putFloat(this.z);
		byteBufferWriter.putFloat(this.angle);
		byteBufferWriter.putByte((byte)this.direction.index());
		byteBufferWriter.putByte(this.characterFlags);
		if (this.killer == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			if (this.killer instanceof IsoZombie) {
				byteBufferWriter.putByte((byte)1);
			} else {
				byteBufferWriter.putByte((byte)2);
			}

			byteBufferWriter.putShort(this.killer.getOnlineID());
		}
	}

	public String getDescription() {
		String string = this.getDeathDescription() + "\n\t";
		if (this.character != null) {
			string = string + " isDead=" + this.character.isDead();
			string = string + " isOnDeathDone=" + this.character.isOnDeathDone();
			string = string + " isOnKillDone=" + this.character.isOnKillDone();
			string = string + " | health=" + this.character.getHealth();
			if (this.character.getBodyDamage() != null) {
				string = string + " | bodyDamage=" + this.character.getBodyDamage().getOverallBodyHealth();
			}

			string = string + " | states=( " + this.character.getPreviousActionContextStateName() + " > " + this.character.getCurrentActionContextStateName() + " )";
		}

		return string;
	}

	public String getDeathDescription() {
		String string = this.getClass().getSimpleName();
		return string + " id(" + this.id + ") | killer=" + (this.killer == null ? "Null" : this.killer.getClass().getSimpleName() + "(" + this.killer.getOnlineID() + ")") + " | pos=(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ";a=" + this.angle + ") | dir=" + this.direction.name() + " | isFallOnFront=" + ((this.characterFlags & 1) != 0);
	}

	public boolean isConsistent() {
		return this.character != null;
	}
}
