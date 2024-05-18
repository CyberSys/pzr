package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SystemDisabler;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.ui.UIManager;


public class IsoCurtain extends IsoObject {
	public boolean Barricaded = false;
	public Integer BarricideMaxStrength = 0;
	public Integer BarricideStrength = 0;
	public Integer Health = 1000;
	public boolean Locked = false;
	public Integer MaxHealth = 1000;
	public Integer PushedMaxStrength = 0;
	public Integer PushedStrength = 0;
	IsoSprite closedSprite;
	public boolean north = false;
	public boolean open = false;
	IsoSprite openSprite;
	private boolean destroyed = false;

	public void removeSheet(IsoGameCharacter gameCharacter) {
		this.square.transmitRemoveItemFromSquare(this);
		if (GameServer.bServer) {
			gameCharacter.sendObjectChange("addItemOfType", new Object[]{"type", "Base.Sheet"});
		} else {
			gameCharacter.getInventory().AddItem("Base.Sheet");
		}

		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			LosUtil.cachecleared[int1] = true;
		}

		GameTime.instance.lightSourceUpdate = 100.0F;
		IsoGridSquare.setRecalcLightTime(-1);
	}

	public IsoCurtain(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1, boolean boolean2) {
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		if (boolean2) {
			this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)sprite, 4);
			this.closedSprite = sprite;
		} else {
			this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)sprite, -4);
			this.openSprite = sprite;
		}

		this.open = true;
		this.sprite = this.openSprite;
		this.square = square;
		this.north = boolean1;
		this.DirtySlice();
	}

	public IsoCurtain(IsoCell cell, IsoGridSquare square, String string, boolean boolean1) {
		this.OutlineOnMouseover = true;
		this.PushedMaxStrength = this.PushedStrength = 2500;
		this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, -4);
		this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 0);
		this.open = true;
		this.sprite = this.openSprite;
		this.square = square;
		this.north = boolean1;
		this.DirtySlice();
	}

	public IsoCurtain(IsoCell cell) {
		super(cell);
	}

	public String getObjectName() {
		return "Curtain";
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else if (this.getType() == IsoObjectType.curtainS) {
			return vector2.set(this.getX() + 0.5F, this.getY() + 1.0F);
		} else if (this.getType() == IsoObjectType.curtainE) {
			return vector2.set(this.getX() + 1.0F, this.getY() + 0.5F);
		} else {
			return this.north ? vector2.set(this.getX() + 0.5F, this.getY()) : vector2.set(this.getX(), this.getY() + 0.5F);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.open = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		this.Health = byteBuffer.getInt();
		this.BarricideStrength = byteBuffer.getInt();
		if (this.open) {
			this.closedSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
			this.openSprite = this.sprite;
		} else {
			this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, byteBuffer.getInt());
			this.closedSprite = this.sprite;
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.open ? 1 : 0));
		byteBuffer.put((byte)(this.north ? 1 : 0));
		byteBuffer.putInt(this.Health);
		byteBuffer.putInt(this.BarricideStrength);
		if (this.open) {
			byteBuffer.putInt(this.closedSprite.ID);
		} else {
			byteBuffer.putInt(this.openSprite.ID);
		}
	}

	public boolean getNorth() {
		return this.north;
	}

	public boolean IsOpen() {
		return this.open;
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.0F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
			if (UIManager.getDragInventory() != null) {
			}

			if (this.Barricaded) {
				return false;
			} else {
				this.ToggleDoor(IsoPlayer.getInstance());
				return true;
			}
		} else {
			return false;
		}
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		if (square2.getZ() != square.getZ()) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square && (this.getType() == IsoObjectType.curtainW || this.getType() == IsoObjectType.curtainN) || square != this.square && (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS)) {
				if (this.north && square2.getY() < square.getY() && !this.open) {
					return IsoObject.VisionResult.Blocked;
				}

				if (!this.north && square2.getX() < square.getX() && !this.open) {
					return IsoObject.VisionResult.Blocked;
				}
			} else {
				if (this.north && square2.getY() > square.getY() && !this.open) {
					return IsoObject.VisionResult.Blocked;
				}

				if (!this.north && square2.getX() > square.getX() && !this.open) {
					return IsoObject.VisionResult.Blocked;
				}
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void ToggleDoor(IsoGameCharacter gameCharacter) {
		if (!this.Barricaded) {
			this.DirtySlice();
			if (!this.Locked || gameCharacter == null || gameCharacter.getCurrentSquare().getRoom() != null || this.open) {
				this.open = !this.open;
				this.sprite = this.closedSprite;
				if (this.open) {
					this.sprite = this.openSprite;
				}

				this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null);
			}
		}
	}

	public void ToggleDoorSilent() {
		if (!this.Barricaded) {
			this.DirtySlice();
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				LosUtil.cachecleared[int1] = true;
			}

			GameTime.instance.lightSourceUpdate = 100.0F;
			IsoGridSquare.setRecalcLightTime(-1);
			this.open = !this.open;
			this.sprite = this.closedSprite;
			if (this.open) {
				this.sprite = this.openSprite;
			}

			this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null);
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo) {
		super.render(float1, float2, float3, colorInfo, true);
	}

	public void syncIsoObjectSend(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.square.getX());
		byteBufferWriter.putInt(this.square.getY());
		byteBufferWriter.putInt(this.square.getZ());
		byte byte1 = (byte)this.square.getObjects().indexOf(this);
		byteBufferWriter.putByte(byte1);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)(this.open ? 1 : 0));
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		this.syncIsoObject(boolean1, byte1, udpConnection);
	}

	public void syncIsoObject(boolean boolean1, byte byte1, UdpConnection udpConnection) {
		if (this.square == null) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
		} else if (this.getObjectIndex() == -1) {
			System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
				this.square.clientModify();
			} else if (boolean1) {
				if (byte1 == 1) {
					this.open = true;
					this.sprite = this.openSprite;
				} else {
					this.open = false;
					this.sprite = this.closedSprite;
				}

				if (GameServer.bServer) {
					Iterator iterator = GameServer.udpEngine.connections.iterator();
					while (iterator.hasNext()) {
						UdpConnection udpConnection2 = (UdpConnection)iterator.next();
						if (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
							ByteBufferWriter byteBufferWriter2 = udpConnection2.startPacket();
							PacketTypes.doPacket((short)12, byteBufferWriter2);
							this.syncIsoObjectSend(byteBufferWriter2);
							udpConnection2.endPacketImmediate();
						}
					}

					this.square.revisionUp();
				}
			}

			this.square.RecalcProperties();
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				LosUtil.cachecleared[int1] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
			if (this.square != null) {
				this.square.RecalcProperties();
			}
		}
	}
}
