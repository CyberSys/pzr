package zombie.iso.objects;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SystemDisabler;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;
import zombie.util.list.PZArrayList;


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
			this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (IsoSprite)sprite, 4);
			this.closedSprite = sprite;
		} else {
			this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (IsoSprite)sprite, -4);
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
		this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, -4);
		this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 0);
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

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.open = byteBuffer.get() == 1;
		this.north = byteBuffer.get() == 1;
		this.Health = byteBuffer.getInt();
		this.BarricideStrength = byteBuffer.getInt();
		if (this.open) {
			this.closedSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			this.openSprite = this.sprite;
		} else {
			this.openSprite = IsoSprite.getSprite(IsoSpriteManager.instance, byteBuffer.getInt());
			this.closedSprite = this.sprite;
		}

		if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
			GameClient.instance.objectSyncReq.putRequestLoad(this.square);
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
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
		return false;
	}

	public boolean canInteractWith(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.getCurrentSquare() != null) {
			IsoGridSquare square = gameCharacter.getCurrentSquare();
			return (this.isAdjacentToSquare(square) || square == this.getOppositeSquare()) && !this.getSquare().isBlockedTo(square);
		} else {
			return false;
		}
	}

	public IsoGridSquare getOppositeSquare() {
		if (this.getType() == IsoObjectType.curtainN) {
			return this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
		} else if (this.getType() == IsoObjectType.curtainS) {
			return this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() + 1.0F), (double)this.getZ());
		} else if (this.getType() == IsoObjectType.curtainW) {
			return this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
		} else {
			return this.getType() == IsoObjectType.curtainE ? this.getCell().getGridSquare((double)(this.getX() + 1.0F), (double)this.getY(), (double)this.getZ()) : null;
		}
	}

	public boolean isAdjacentToSquare(IsoGridSquare square, IsoGridSquare square2) {
		if (square != null && square2 != null) {
			if (this.getType() != IsoObjectType.curtainN && this.getType() != IsoObjectType.curtainS) {
				return square.x == square2.x && Math.abs(square.y - square2.y) <= 1;
			} else {
				return square.y == square2.y && Math.abs(square.x - square2.x) <= 1;
			}
		} else {
			return false;
		}
	}

	public boolean isAdjacentToSquare(IsoGridSquare square) {
		return this.isAdjacentToSquare(this.getSquare(), square);
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
					if (gameCharacter != null) {
						gameCharacter.playSound(this.getSoundPrefix() + "Open");
					}
				} else if (gameCharacter != null) {
					gameCharacter.playSound(this.getSoundPrefix() + "Close");
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

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoObject object = this.getObjectAttachedTo();
		if (object != null && this.getSquare().getTargetDarkMulti(int1) <= object.getSquare().getTargetDarkMulti(int1)) {
			colorInfo = object.getSquare().lighting[int1].lightInfo();
			this.setTargetAlpha(int1, object.getTargetAlpha(int1));
		}

		super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
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
			PrintStream printStream = System.out;
			String string = this.getClass().getSimpleName();
			printStream.println("ERROR: " + string + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
		} else {
			if (GameClient.bClient && !boolean1) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)12, byteBufferWriter);
				this.syncIsoObjectSend(byteBufferWriter);
				GameClient.connection.endPacketImmediate();
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
				}
			}

			this.square.RecalcProperties();
			this.square.RecalcAllWithNeighbours(true);
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

	public IsoObject getObjectAttachedTo() {
		int int1 = this.getObjectIndex();
		if (int1 == -1) {
			return null;
		} else {
			PZArrayList pZArrayList = this.getSquare().getObjects();
			if (this.getType() != IsoObjectType.curtainW && this.getType() != IsoObjectType.curtainN) {
				if (this.getType() == IsoObjectType.curtainE || this.getType() == IsoObjectType.curtainS) {
					IsoGridSquare square = this.getOppositeSquare();
					if (square != null) {
						boolean boolean1 = this.getType() == IsoObjectType.curtainS;
						pZArrayList = square.getObjects();
						for (int int2 = pZArrayList.size() - 1; int2 >= 0; --int2) {
							BarricadeAble barricadeAble = (BarricadeAble)Type.tryCastTo((IsoObject)pZArrayList.get(int2), BarricadeAble.class);
							if (barricadeAble != null && boolean1 == barricadeAble.getNorth()) {
								return (IsoObject)pZArrayList.get(int2);
							}
						}
					}
				}
			} else {
				boolean boolean2 = this.getType() == IsoObjectType.curtainN;
				for (int int3 = int1 - 1; int3 >= 0; --int3) {
					BarricadeAble barricadeAble2 = (BarricadeAble)Type.tryCastTo((IsoObject)pZArrayList.get(int3), BarricadeAble.class);
					if (barricadeAble2 != null && boolean2 == barricadeAble2.getNorth()) {
						return (IsoObject)pZArrayList.get(int3);
					}
				}
			}

			return null;
		}
	}

	public String getSoundPrefix() {
		if (this.closedSprite == null) {
			return "CurtainShort";
		} else {
			PropertyContainer propertyContainer = this.closedSprite.getProperties();
			return propertyContainer.Is("CurtainSound") ? "Curtain" + propertyContainer.Val("CurtainSound") : "CurtainShort";
		}
	}

	public static boolean isSheet(IsoObject object) {
		if (object instanceof IsoDoor) {
			object = ((IsoDoor)object).HasCurtains();
		}

		if (object instanceof IsoThumpable) {
			object = ((IsoThumpable)object).HasCurtains();
		}

		if (object instanceof IsoWindow) {
			object = ((IsoWindow)object).HasCurtains();
		}

		if (object != null && ((IsoObject)object).getSprite() != null) {
			IsoSprite sprite = ((IsoObject)object).getSprite();
			return sprite.getProperties().Is("CurtainSound") ? "Sheet".equals(sprite.getProperties().Val("CurtainSound")) : false;
		} else {
			return false;
		}
	}
}
