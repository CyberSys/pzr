package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import se.krka.kahlua.vm.KahluaTable;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.Talker;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.physics.Transform;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.debug.LogSeverity;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.IsoObjectID;
import zombie.network.ServerGUI;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class IsoDeadBody extends IsoMovingObject implements Talker,IHumanVisual {
	public static final int MAX_ROT_STAGES = 3;
	private static final int VISUAL_TYPE_HUMAN = 0;
	private static final IsoObjectID Bodies = new IsoObjectID(IsoDeadBody.class);
	private static final ArrayList tempBodies = new ArrayList();
	private boolean bFemale;
	private boolean wasZombie;
	private boolean bFakeDead;
	private boolean bCrawling;
	private Color SpeakColor;
	private float SpeakTime;
	private int m_persistentOutfitID;
	private SurvivorDesc desc;
	private BaseVisual baseVisual;
	private WornItems wornItems;
	private AttachedItems attachedItems;
	private float deathTime;
	private float reanimateTime;
	private IsoPlayer player;
	private boolean fallOnFront;
	private boolean wasSkeleton;
	private InventoryItem primaryHandItem;
	private InventoryItem secondaryHandItem;
	private float m_angle;
	private int m_zombieRotStageAtDeath;
	private short onlineID;
	private short objectID;
	private static final ThreadLocal tempZombie = new ThreadLocal(){
    
    public IsoZombie initialValue() {
        return new IsoZombie((IsoCell)null);
    }
};
	private static ColorInfo inf = new ColorInfo();
	public DeadBodyAtlas.BodyTexture atlasTex;
	private static Texture DropShadow = null;
	private static final float HIT_TEST_WIDTH = 0.3F;
	private static final float HIT_TEST_HEIGHT = 0.9F;
	private static final Quaternionf _rotation = new Quaternionf();
	private static final Transform _transform = new Transform();
	private static final Vector3f _UNIT_Z = new Vector3f(0.0F, 0.0F, 1.0F);
	private static final Vector3f _tempVec3f_1 = new Vector3f();
	private static final Vector3f _tempVec3f_2 = new Vector3f();
	private float burnTimer;
	public boolean Speaking;
	public String sayLine;

	public static boolean isDead(short short1) {
		float float1 = (float)GameTime.getInstance().getWorldAgeHours();
		Iterator iterator = Bodies.iterator();
		IsoDeadBody deadBody;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			deadBody = (IsoDeadBody)iterator.next();
		} while (deadBody.onlineID != short1 || !(float1 - deadBody.deathTime < 0.1F));

		return true;
	}

	public String getObjectName() {
		return "DeadBody";
	}

	public IsoDeadBody(IsoGameCharacter gameCharacter) {
		this(gameCharacter, false);
	}

	public IsoDeadBody(IsoGameCharacter gameCharacter, boolean boolean1) {
		super(gameCharacter.getCell(), false);
		this.bFemale = false;
		this.wasZombie = false;
		this.bFakeDead = false;
		this.bCrawling = false;
		this.SpeakTime = 0.0F;
		this.baseVisual = null;
		this.deathTime = -1.0F;
		this.reanimateTime = -1.0F;
		this.fallOnFront = false;
		this.wasSkeleton = false;
		this.primaryHandItem = null;
		this.secondaryHandItem = null;
		this.m_zombieRotStageAtDeath = 1;
		this.onlineID = -1;
		this.objectID = -1;
		this.burnTimer = 0.0F;
		this.Speaking = false;
		this.sayLine = "";
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		this.setFallOnFront(gameCharacter.isFallOnFront());
		if (!GameClient.bClient && !GameServer.bServer && zombie != null && zombie.bCrawling) {
			if (!zombie.isReanimate()) {
				this.setFallOnFront(true);
			}

			this.bCrawling = true;
		}

		IsoGridSquare square = gameCharacter.getCurrentSquare();
		if (square != null) {
			if (gameCharacter.getZ() < 0.0F) {
				DebugLog.General.error("invalid z-coordinate %d,%d,%d", gameCharacter.x, gameCharacter.y, gameCharacter.z);
				gameCharacter.setZ(0.0F);
			}

			this.square = square;
			this.current = square;
			if (gameCharacter instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter).removeSaveFile();
			}

			square.getStaticMovingObjects().add(this);
			if (gameCharacter instanceof IsoSurvivor) {
				IsoWorld world = IsoWorld.instance;
				world.TotalSurvivorNights += ((IsoSurvivor)gameCharacter).nightsSurvived;
				++IsoWorld.instance.TotalSurvivorsDead;
				if (IsoWorld.instance.SurvivorSurvivalRecord < ((IsoSurvivor)gameCharacter).nightsSurvived) {
					IsoWorld.instance.SurvivorSurvivalRecord = ((IsoSurvivor)gameCharacter).nightsSurvived;
				}
			}

			this.bFemale = gameCharacter.isFemale();
			this.wasZombie = zombie != null;
			if (this.wasZombie) {
				this.bFakeDead = zombie.isFakeDead();
				this.wasSkeleton = zombie.isSkeleton();
			}

			this.dir = gameCharacter.dir;
			this.m_angle = gameCharacter.getAnimAngleRadians();
			this.Collidable = false;
			this.x = gameCharacter.getX();
			this.y = gameCharacter.getY();
			this.z = gameCharacter.getZ();
			this.nx = this.x;
			this.ny = this.y;
			this.offsetX = gameCharacter.offsetX;
			this.offsetY = gameCharacter.offsetY;
			this.solid = false;
			this.shootable = false;
			this.onlineID = gameCharacter.getOnlineID();
			this.OutlineOnMouseover = true;
			this.setContainer(gameCharacter.getInventory());
			this.setWornItems(gameCharacter.getWornItems());
			this.setAttachedItems(gameCharacter.getAttachedItems());
			if (gameCharacter instanceof IHumanVisual) {
				this.baseVisual = new HumanVisual(this);
				this.baseVisual.copyFrom(((IHumanVisual)gameCharacter).getHumanVisual());
				this.m_zombieRotStageAtDeath = this.getHumanVisual().zombieRotStage;
			}

			gameCharacter.setInventory(new ItemContainer());
			gameCharacter.clearWornItems();
			gameCharacter.clearAttachedItems();
			if (!this.container.bExplored) {
				this.container.setExplored(gameCharacter instanceof IsoPlayer || gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).isReanimatedPlayer());
			}

			boolean boolean2 = gameCharacter.isOnFire();
			if (gameCharacter instanceof IsoZombie) {
				this.m_persistentOutfitID = gameCharacter.getPersistentOutfitID();
				if (!boolean1 && !GameServer.bServer) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						IsoPlayer player = IsoPlayer.players[int1];
						if (player != null && player.ReanimatedCorpse == gameCharacter) {
							player.ReanimatedCorpse = null;
							player.ReanimatedCorpseID = -1;
						}
					}

					if (!GameClient.bClient && gameCharacter.emitter != null) {
						gameCharacter.emitter.tick();
					}
				}
			} else {
				if (gameCharacter instanceof IsoSurvivor) {
					this.getCell().getSurvivorList().remove(gameCharacter);
				}

				this.desc = new SurvivorDesc(gameCharacter.getDescriptor());
				if (gameCharacter instanceof IsoPlayer) {
					if (GameServer.bServer) {
						this.player = (IsoPlayer)gameCharacter;
					} else if (!GameClient.bClient && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
						this.player = (IsoPlayer)gameCharacter;
					}
				}
			}

			LuaManager.copyTable(this.getModData(), gameCharacter.getModData());
			gameCharacter.removeFromWorld();
			gameCharacter.removeFromSquare();
			this.sayLine = gameCharacter.getSayLine();
			this.SpeakColor = gameCharacter.getSpeakColour();
			this.SpeakTime = gameCharacter.getSpeakTime();
			this.Speaking = gameCharacter.isSpeaking();
			if (boolean2) {
				if (!GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
					IsoFireManager.StartFire(this.getCell(), this.getSquare(), true, 100, 500);
				}

				this.container.setExplored(true);
			}

			if (!boolean1 && !GameServer.bServer) {
				LuaEventManager.triggerEvent("OnContainerUpdate", this);
			}

			if (gameCharacter instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter).bDeathFinished = true;
			}

			this.deathTime = (float)GameTime.getInstance().getWorldAgeHours();
			this.setEatingZombies(gameCharacter.getEatingZombies());
			if (!this.wasZombie) {
				ArrayList arrayList = new ArrayList();
				int int2;
				for (int2 = -2; int2 < 2; ++int2) {
					for (int int3 = -2; int3 < 2; ++int3) {
						IsoGridSquare square2 = square.getCell().getGridSquare(square.x + int2, square.y + int3, square.z);
						if (square2 != null) {
							for (int int4 = 0; int4 < square2.getMovingObjects().size(); ++int4) {
								if (square2.getMovingObjects().get(int4) instanceof IsoZombie) {
									arrayList.add((IsoMovingObject)square2.getMovingObjects().get(int4));
								}
							}
						}
					}
				}

				for (int2 = 0; int2 < arrayList.size(); ++int2) {
					((IsoZombie)arrayList.get(int2)).pathToLocationF(this.getX() + Rand.Next(-0.3F, 0.3F), this.getY() + Rand.Next(-0.3F, 0.3F), this.getZ());
					((IsoZombie)arrayList.get(int2)).bodyToEat = this;
				}
			}

			if (!GameClient.bClient) {
				this.objectID = Bodies.allocateID();
			}

			Bodies.put(this.objectID, this);
			if (!GameServer.bServer) {
				FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
			}

			DebugLog.Death.noise("Corpse created %s", this.getDescription());
		}
	}

	public IsoDeadBody(IsoCell cell) {
		super(cell, false);
		this.bFemale = false;
		this.wasZombie = false;
		this.bFakeDead = false;
		this.bCrawling = false;
		this.SpeakTime = 0.0F;
		this.baseVisual = null;
		this.deathTime = -1.0F;
		this.reanimateTime = -1.0F;
		this.fallOnFront = false;
		this.wasSkeleton = false;
		this.primaryHandItem = null;
		this.secondaryHandItem = null;
		this.m_zombieRotStageAtDeath = 1;
		this.onlineID = -1;
		this.objectID = -1;
		this.burnTimer = 0.0F;
		this.Speaking = false;
		this.sayLine = "";
		this.SpeakColor = Color.white;
		this.solid = false;
		this.shootable = false;
		BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
		this.wornItems = new WornItems(bodyLocationGroup);
		AttachedLocationGroup attachedLocationGroup = AttachedLocations.getGroup("Human");
		this.attachedItems = new AttachedItems(attachedLocationGroup);
		DebugLog.Death.noise("Corpse created on cell %s", this.getDescription());
	}

	public BaseVisual getVisual() {
		return this.baseVisual;
	}

	public HumanVisual getHumanVisual() {
		return (HumanVisual)Type.tryCastTo(this.baseVisual, HumanVisual.class);
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		this.wornItems.getItemVisuals(itemVisuals);
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public boolean isZombie() {
		return this.wasZombie;
	}

	public boolean isCrawling() {
		return this.bCrawling;
	}

	public void setCrawling(boolean boolean1) {
		this.bCrawling = boolean1;
	}

	public boolean isFakeDead() {
		return this.bFakeDead;
	}

	public void setFakeDead(boolean boolean1) {
		if (!boolean1 || SandboxOptions.instance.Lore.DisableFakeDead.getValue() != 3) {
			this.bFakeDead = boolean1;
		}
	}

	public boolean isSkeleton() {
		return this.wasSkeleton;
	}

	public void setWornItems(WornItems wornItems) {
		this.wornItems = new WornItems(wornItems);
	}

	public WornItems getWornItems() {
		return this.wornItems;
	}

	public void setAttachedItems(AttachedItems attachedItems) {
		if (attachedItems != null) {
			this.attachedItems = new AttachedItems(attachedItems);
			for (int int1 = 0; int1 < this.attachedItems.size(); ++int1) {
				AttachedItem attachedItem = this.attachedItems.get(int1);
				InventoryItem inventoryItem = attachedItem.getItem();
				if (!this.container.contains(inventoryItem) && !GameClient.bClient && !GameServer.bServer) {
					inventoryItem.setContainer(this.container);
					this.container.getItems().add(inventoryItem);
				}
			}
		}
	}

	public AttachedItems getAttachedItems() {
		return this.attachedItems;
	}

	public InventoryItem getItem() {
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.CorpseMale");
		inventoryItem.storeInByteData(this);
		return inventoryItem;
	}

	private IsoSprite loadSprite(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		return null;
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.bFemale = byteBuffer.get() == 1;
		this.wasZombie = byteBuffer.get() == 1;
		if (int1 >= 192) {
			this.objectID = byteBuffer.getShort();
		} else {
			this.objectID = -1;
		}

		boolean boolean2 = byteBuffer.get() == 1;
		if (int1 >= 171) {
			this.m_persistentOutfitID = byteBuffer.getInt();
		}

		if (boolean2 && int1 < 171) {
			short short1 = byteBuffer.getShort();
		}

		if (byteBuffer.get() == 1) {
			this.desc = new SurvivorDesc(true);
			this.desc.load(byteBuffer, int1, (IsoGameCharacter)null);
		}

		if (int1 >= 190) {
			byte byte1 = byteBuffer.get();
			switch (byte1) {
			case 0: 
				this.baseVisual = new HumanVisual(this);
				this.baseVisual.load(byteBuffer, int1);
				break;
			
			default: 
				throw new IOException("invalid visualType for corpse");
			
			}
		} else {
			this.baseVisual = new HumanVisual(this);
			this.baseVisual.load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			int int2 = byteBuffer.getInt();
			try {
				this.setContainer(new ItemContainer());
				this.container.ID = int2;
				ArrayList arrayList = this.container.load(byteBuffer, int1);
				byte byte2 = byteBuffer.get();
				for (int int3 = 0; int3 < byte2; ++int3) {
					String string = GameWindow.ReadString(byteBuffer);
					short short2 = byteBuffer.getShort();
					if (short2 >= 0 && short2 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string) != null) {
						this.wornItems.setItem(string, (InventoryItem)arrayList.get(short2));
					}
				}

				byte byte3 = byteBuffer.get();
				for (int int4 = 0; int4 < byte3; ++int4) {
					String string2 = GameWindow.ReadString(byteBuffer);
					short short3 = byteBuffer.getShort();
					if (short3 >= 0 && short3 < arrayList.size() && this.attachedItems.getGroup().getLocation(string2) != null) {
						this.attachedItems.setItem(string2, (InventoryItem)arrayList.get(short3));
					}
				}
			} catch (Exception exception) {
				if (this.container != null) {
					DebugLog.log("Failed to stream in container ID: " + this.container.ID);
				}
			}
		}

		this.deathTime = byteBuffer.getFloat();
		this.reanimateTime = byteBuffer.getFloat();
		this.fallOnFront = byteBuffer.get() == 1;
		if (boolean2 && (GameClient.bClient || GameServer.bServer && ServerGUI.isCreated())) {
			this.checkClothing((InventoryItem)null);
		}

		this.wasSkeleton = byteBuffer.get() == 1;
		if (int1 >= 159) {
			this.m_angle = byteBuffer.getFloat();
		} else {
			this.m_angle = this.dir.toAngle();
		}

		if (int1 >= 166) {
			this.m_zombieRotStageAtDeath = byteBuffer.get() & 255;
		}

		if (int1 >= 168) {
			this.bCrawling = byteBuffer.get() == 1;
			this.bFakeDead = byteBuffer.get() == 1;
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.bFemale ? 1 : 0));
		byteBuffer.put((byte)(this.wasZombie ? 1 : 0));
		byteBuffer.putShort(this.objectID);
		if (!GameServer.bServer && !GameClient.bClient) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
		}

		byteBuffer.putInt(this.m_persistentOutfitID);
		if (this.desc != null) {
			byteBuffer.put((byte)1);
			this.desc.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.baseVisual instanceof HumanVisual) {
			byteBuffer.put((byte)0);
			this.baseVisual.save(byteBuffer);
			if (this.container != null) {
				byteBuffer.put((byte)1);
				byteBuffer.putInt(this.container.ID);
				ArrayList arrayList = this.container.save(byteBuffer);
				if (this.wornItems.size() > 127) {
					throw new RuntimeException("too many worn items");
				}

				byteBuffer.put((byte)this.wornItems.size());
				this.wornItems.forEach((boolean1x)->{
					GameWindow.WriteString(byteBuffer, boolean1x.getLocation());
					byteBuffer.putShort((short)arrayList.indexOf(boolean1x.getItem()));
				});

				if (this.attachedItems == null) {
					byteBuffer.put((byte)0);
				} else {
					if (this.attachedItems.size() > 127) {
						throw new RuntimeException("too many attached items");
					}

					byteBuffer.put((byte)this.attachedItems.size());
					this.attachedItems.forEach((boolean1x)->{
						GameWindow.WriteString(byteBuffer, boolean1x.getLocation());
						byteBuffer.putShort((short)arrayList.indexOf(boolean1x.getItem()));
					});
				}
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.putFloat(this.deathTime);
			byteBuffer.putFloat(this.reanimateTime);
			byteBuffer.put((byte)(this.fallOnFront ? 1 : 0));
			byteBuffer.put((byte)(this.isSkeleton() ? 1 : 0));
			byteBuffer.putFloat(this.m_angle);
			byteBuffer.put((byte)this.m_zombieRotStageAtDeath);
			byteBuffer.put((byte)(this.bCrawling ? 1 : 0));
			byteBuffer.put((byte)(this.bFakeDead ? 1 : 0));
		} else {
			throw new IllegalStateException("unhandled baseVisual class");
		}
	}

	public void softReset() {
		this.square.RemoveTileObject(this);
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("becomeSkeleton".equals(string)) {
			byteBuffer.putInt(this.getHumanVisual().getSkinTextureIndex());
		} else if ("zombieRotStage".equals(string)) {
			byteBuffer.putInt(this.getHumanVisual().zombieRotStage);
		} else {
			super.saveChange(string, kahluaTable, byteBuffer);
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("becomeSkeleton".equals(string)) {
			int int1 = byteBuffer.getInt();
			this.getHumanVisual().setBeardModel("");
			this.getHumanVisual().setHairModel("");
			this.getHumanVisual().setSkinTextureIndex(int1);
			this.wasSkeleton = true;
			this.getWornItems().clear();
			this.getAttachedItems().clear();
			this.getContainer().clear();
			this.atlasTex = null;
		} else if ("zombieRotStage".equals(string)) {
			this.getHumanVisual().zombieRotStage = byteBuffer.getInt();
			this.atlasTex = null;
		} else {
			super.loadChange(string, byteBuffer);
		}
	}

	public void renderlast() {
		if (this.Speaking) {
			float float1 = this.sx;
			float float2 = this.sy;
			float1 -= IsoCamera.getOffX();
			float2 -= IsoCamera.getOffY();
			float1 += 8.0F;
			float2 += 32.0F;
			if (this.sayLine != null) {
				TextManager.instance.DrawStringCentre(UIFont.Medium, (double)float1, (double)float2, this.sayLine, (double)this.SpeakColor.r, (double)this.SpeakColor.g, (double)this.SpeakColor.b, (double)this.SpeakColor.a);
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		boolean boolean3 = this.isHighlighted();
		float float4;
		float float5;
		if (ModelManager.instance.bDebugEnableModels && ModelManager.instance.isCreated()) {
			if (this.atlasTex == null) {
				this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
				DeadBodyAtlas.instance.render();
			}

			if (this.atlasTex != null) {
				if (IsoSprite.globalOffsetX == -1.0F) {
					IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
					IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
				}

				float4 = IsoUtils.XToScreen(float1, float2, float3, 0);
				float5 = IsoUtils.YToScreen(float1, float2, float3, 0);
				this.sx = float4;
				this.sy = float5;
				float4 = this.sx + IsoSprite.globalOffsetX;
				float5 = this.sy + IsoSprite.globalOffsetY;
				if (Core.TileScale == 1) {
				}

				if (boolean3) {
					inf.r = this.getHighlightColor().r;
					inf.g = this.getHighlightColor().g;
					inf.b = this.getHighlightColor().b;
					inf.a = this.getHighlightColor().a;
				} else {
					inf.r = colorInfo.r;
					inf.g = colorInfo.g;
					inf.b = colorInfo.b;
					inf.a = colorInfo.a;
				}

				colorInfo = inf;
				if (!boolean3 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
					this.getCurrentSquare().interpolateLight(colorInfo, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
				}

				if (GameServer.bServer && ServerGUI.isCreated()) {
					inf.set(1.0F, 1.0F, 1.0F, 1.0F);
				}

				this.atlasTex.render((float)((int)float4), (float)((int)float5), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
				if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
					LineDrawer.DrawIsoLine(float1 - 0.5F, float2, float3, float1 + 0.5F, float2, float3, 1.0F, 1.0F, 1.0F, 0.25F, 1);
					LineDrawer.DrawIsoLine(float1, float2 - 0.5F, float3, float1, float2 + 0.5F, float3, 1.0F, 1.0F, 1.0F, 0.25F, 1);
				}

				this.sx = float4;
				this.sy = float5;
				if (IsoObjectPicker.Instance.wasDirty) {
					this.renderObjectPicker(this.getX(), this.getY(), this.getZ(), colorInfo);
				}
			}
		}

		float float6;
		if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
			_rotation.setAngleAxis((double)this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
			_transform.setRotation(_rotation);
			_transform.origin.set(this.x, this.y, this.z);
			Vector3f vector3f = _tempVec3f_1;
			_transform.basis.getColumn(1, vector3f);
			Vector3f vector3f2 = _tempVec3f_2;
			vector3f.cross(_UNIT_Z, vector3f2);
			float6 = 0.3F;
			float float7 = 0.9F;
			vector3f.x *= float7;
			vector3f.y *= float7;
			vector3f2.x *= float6;
			vector3f2.y *= float6;
			float float8 = float1 + vector3f.x;
			float float9 = float2 + vector3f.y;
			float float10 = float1 - vector3f.x;
			float float11 = float2 - vector3f.y;
			float float12 = float8 - vector3f2.x;
			float float13 = float8 + vector3f2.x;
			float float14 = float10 - vector3f2.x;
			float float15 = float10 + vector3f2.x;
			float float16 = float11 - vector3f2.y;
			float float17 = float11 + vector3f2.y;
			float float18 = float9 - vector3f2.y;
			float float19 = float9 + vector3f2.y;
			float float20 = 1.0F;
			float float21 = 1.0F;
			float float22 = 1.0F;
			if (this.isMouseOver((float)Mouse.getX(), (float)Mouse.getY())) {
				float22 = 0.0F;
				float20 = 0.0F;
			}

			LineDrawer.addLine(float12, float18, 0.0F, float13, float19, 0.0F, float20, float21, float22, (String)null, true);
			LineDrawer.addLine(float12, float18, 0.0F, float14, float16, 0.0F, float20, float21, float22, (String)null, true);
			LineDrawer.addLine(float13, float19, 0.0F, float15, float17, 0.0F, float20, float21, float22, (String)null, true);
			LineDrawer.addLine(float14, float16, 0.0F, float15, float17, 0.0F, float20, float21, float22, (String)null, true);
		}

		if (this.isFakeDead() && DebugOptions.instance.ZombieRenderFakeDead.getValue()) {
			float4 = IsoUtils.XToScreen(float1, float2, float3, 0) + IsoSprite.globalOffsetX;
			float5 = IsoUtils.YToScreen(float1, float2, float3, 0) + IsoSprite.globalOffsetY - (float)(16 * Core.TileScale);
			float6 = this.getFakeDeadWakeupHours() - (float)GameTime.getInstance().getWorldAgeHours();
			float6 = Math.max(float6, 0.0F);
			TextManager.instance.DrawStringCentre(UIFont.Medium, (double)float4, (double)float5, String.format("FakeDead %.2f", float6), 1.0, 1.0, 1.0, 1.0);
		}

		if (Core.bDebug && DebugOptions.instance.MultiplayerShowZombieOwner.getValue()) {
			Color color = Colors.Yellow;
			float5 = IsoUtils.XToScreenExact(float1 + 0.4F, float2 + 0.4F, float3, 0);
			float6 = IsoUtils.YToScreenExact(float1 + 0.4F, float2 - 1.4F, float3, 0);
			TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)float5, (double)float6, this.objectID + " / " + this.onlineID + " / " + (this.isFemale() ? "F" : "M"), (double)color.r, (double)color.g, (double)color.b, (double)color.a);
			TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)float5, (double)(float6 + 10.0F), String.format("x=%09.3f ", float1) + String.format("y=%09.3f ", float2) + String.format("z=%d", (byte)((int)float3)), (double)color.r, (double)color.g, (double)color.b, (double)color.a);
		}
	}

	public void renderShadow() {
		_rotation.setAngleAxis((double)this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
		_transform.setRotation(_rotation);
		_transform.origin.set(this.x, this.y, this.z);
		Vector3f vector3f = _tempVec3f_1;
		_transform.basis.getColumn(1, vector3f);
		float float1 = 0.45F;
		float float2 = 1.4F;
		float float3 = 1.125F;
		int int1 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.square.lighting[int1].lightInfo();
		renderShadow(this.x, this.y, this.z, vector3f, float1, float2, float3, colorInfo, this.getAlpha(int1));
	}

	public static void renderShadow(float float1, float float2, float float3, Vector3f vector3f, float float4, float float5, float float6, ColorInfo colorInfo, float float7) {
		float float8 = float7 * ((colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F);
		float8 *= 0.66F;
		vector3f.normalize();
		Vector3f vector3f2 = _tempVec3f_2;
		vector3f.cross(_UNIT_Z, vector3f2);
		float4 = Math.max(0.65F, float4);
		float5 = Math.max(float5, 0.65F);
		float6 = Math.max(float6, 0.65F);
		vector3f2.x *= float4;
		vector3f2.y *= float4;
		float float9 = float1 + vector3f.x * float5;
		float float10 = float2 + vector3f.y * float5;
		float float11 = float1 - vector3f.x * float6;
		float float12 = float2 - vector3f.y * float6;
		float float13 = float9 - vector3f2.x;
		float float14 = float9 + vector3f2.x;
		float float15 = float11 - vector3f2.x;
		float float16 = float11 + vector3f2.x;
		float float17 = float12 - vector3f2.y;
		float float18 = float12 + vector3f2.y;
		float float19 = float10 - vector3f2.y;
		float float20 = float10 + vector3f2.y;
		float float21 = IsoUtils.XToScreenExact(float13, float19, float3, 0);
		float float22 = IsoUtils.YToScreenExact(float13, float19, float3, 0);
		float float23 = IsoUtils.XToScreenExact(float14, float20, float3, 0);
		float float24 = IsoUtils.YToScreenExact(float14, float20, float3, 0);
		float float25 = IsoUtils.XToScreenExact(float16, float18, float3, 0);
		float float26 = IsoUtils.YToScreenExact(float16, float18, float3, 0);
		float float27 = IsoUtils.XToScreenExact(float15, float17, float3, 0);
		float float28 = IsoUtils.YToScreenExact(float15, float17, float3, 0);
		if (DropShadow == null) {
			DropShadow = Texture.getSharedTexture("media/textures/NewShadow.png");
		}

		SpriteRenderer.instance.renderPoly(DropShadow, float21, float22, float23, float24, float25, float26, float27, float28, 0.0F, 0.0F, 0.0F, float8);
		if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue()) {
			LineDrawer.addLine(float13, float19, float3, float14, float20, float3, 1, 1, 1, (String)null);
			LineDrawer.addLine(float14, float20, float3, float16, float18, float3, 1, 1, 1, (String)null);
			LineDrawer.addLine(float16, float18, float3, float15, float17, float3, 1, 1, 1, (String)null);
			LineDrawer.addLine(float15, float17, float3, float13, float19, float3, 1, 1, 1, (String)null);
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.atlasTex != null) {
			this.atlasTex.renderObjectPicker(this.sx, this.sy, colorInfo, this.square, this);
		}
	}

	public boolean isMouseOver(float float1, float float2) {
		_rotation.setAngleAxis((double)this.m_angle + 1.5707963267948966, 0.0, 0.0, 1.0);
		_transform.setRotation(_rotation);
		_transform.origin.set(this.x, this.y, this.z);
		_transform.inverse();
		Vector3f vector3f = _tempVec3f_1.set(IsoUtils.XToIso(float1, float2, this.z), IsoUtils.YToIso(float1, float2, this.z), this.z);
		_transform.transform(vector3f);
		return vector3f.x >= -0.3F && vector3f.y >= -0.9F && vector3f.x < 0.3F && vector3f.y < 0.9F;
	}

	public void Burn() {
		if (!GameClient.bClient) {
			if (this.getSquare() != null && this.getSquare().getProperties().Is(IsoFlagType.burning)) {
				this.burnTimer += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
				if (this.burnTimer >= 10.0F) {
					boolean boolean1 = true;
					for (int int1 = 0; int1 < this.getSquare().getObjects().size(); ++int1) {
						IsoObject object = (IsoObject)this.getSquare().getObjects().get(int1);
						if (object.getName() != null && "burnedCorpse".equals(object.getName())) {
							boolean1 = false;
							break;
						}
					}

					if (boolean1) {
						IsoObject object2 = new IsoObject(this.getSquare(), "floors_burnt_01_" + Rand.Next(1, 3), "burnedCorpse");
						this.getSquare().getObjects().add(object2);
						object2.transmitCompleteItemToClients();
					}

					if (GameServer.bServer) {
						GameServer.sendRemoveCorpseFromMap(this);
					}

					this.getSquare().removeCorpse(this, true);
				}
			}
		}
	}

	public void setContainer(ItemContainer itemContainer) {
		super.setContainer(itemContainer);
		itemContainer.type = this.bFemale ? "inventoryfemale" : "inventorymale";
		itemContainer.Capacity = 8;
		itemContainer.SourceGrid = this.square;
	}

	public void checkClothing(InventoryItem inventoryItem) {
		int int1;
		InventoryItem inventoryItem2;
		for (int1 = 0; int1 < this.wornItems.size(); ++int1) {
			inventoryItem2 = this.wornItems.getItemByIndex(int1);
			if (this.container == null || this.container.getItems().indexOf(inventoryItem2) == -1) {
				this.wornItems.remove(inventoryItem2);
				this.atlasTex = null;
				--int1;
			}
		}

		if (inventoryItem == this.getPrimaryHandItem()) {
			this.setPrimaryHandItem((InventoryItem)null);
			this.atlasTex = null;
		}

		if (inventoryItem == this.getSecondaryHandItem()) {
			this.setSecondaryHandItem((InventoryItem)null);
			this.atlasTex = null;
		}

		for (int1 = 0; int1 < this.attachedItems.size(); ++int1) {
			inventoryItem2 = this.attachedItems.getItemByIndex(int1);
			if (this.container == null || this.container.getItems().indexOf(inventoryItem2) == -1) {
				this.attachedItems.remove(inventoryItem2);
				this.atlasTex = null;
				--int1;
			}
		}
	}

	public boolean IsSpeaking() {
		return this.Speaking;
	}

	public void Say(String string) {
		this.SpeakTime = (float)(string.length() * 4);
		if (this.SpeakTime < 60.0F) {
			this.SpeakTime = 60.0F;
		}

		this.sayLine = string;
		this.Speaking = true;
	}

	public String getSayLine() {
		return this.sayLine;
	}

	public String getTalkerType() {
		return "Talker";
	}

	public void addToWorld() {
		super.addToWorld();
		if (!GameServer.bServer) {
			FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
		}

		if (!GameClient.bClient && this.objectID == -1) {
			this.objectID = Bodies.allocateID();
		}

		Bodies.put(this.objectID, this);
		if (!GameClient.bClient) {
			if (this.reanimateTime > 0.0F) {
				this.getCell().addToStaticUpdaterObjectList(this);
				if (Core.bDebug) {
					DebugLog.log("reanimate: addToWorld reanimateTime=" + this.reanimateTime + this);
				}
			}

			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			if (this.deathTime < 0.0F) {
				this.deathTime = float1;
			}

			if (this.deathTime > float1) {
				this.deathTime = float1;
			}
		}
	}

	public void removeFromWorld() {
		if (!GameServer.bServer) {
			FliesSound.instance.corpseRemoved((int)this.getX(), (int)this.getY(), (int)this.getZ());
		}

		Bodies.remove(this.objectID);
		super.removeFromWorld();
	}

	public static void updateBodies() {
		if (!GameClient.bClient) {
			if (Core.bDebug) {
			}

			boolean boolean1 = false;
			float float1 = (float)SandboxOptions.instance.HoursForCorpseRemoval.getValue();
			if (!(float1 <= 0.0F)) {
				float float2 = float1 / 3.0F;
				float float3 = (float)GameTime.getInstance().getWorldAgeHours();
				tempBodies.clear();
				Bodies.getObjects(tempBodies);
				Iterator iterator = tempBodies.iterator();
				while (true) {
					IsoDeadBody deadBody;
					do {
						do {
							do {
								if (!iterator.hasNext()) {
									return;
								}

								deadBody = (IsoDeadBody)iterator.next();
							}					 while (deadBody.getHumanVisual() == null);

							if (deadBody.deathTime > float3) {
								deadBody.deathTime = float3;
								deadBody.getHumanVisual().zombieRotStage = deadBody.m_zombieRotStageAtDeath;
							}
						}				 while (deadBody.updateFakeDead());
					}			 while (!ServerOptions.instance.RemovePlayerCorpsesOnCorpseRemoval.getValue() && !deadBody.wasZombie);

					int int1 = deadBody.getHumanVisual().zombieRotStage;
					deadBody.updateRotting(float3, float2, boolean1);
					if (deadBody.isFakeDead()) {
					}

					int int2 = deadBody.getHumanVisual().zombieRotStage;
					float float4 = float3 - deadBody.deathTime;
					if (!(float4 < float1 + (deadBody.isSkeleton() ? float2 : 0.0F))) {
						if (boolean1) {
							int int3 = (int)(float4 / float2);
							DebugLog.General.debugln("%s REMOVE %d -> %d age=%.2f stages=%d", deadBody, int1, int2, float4, int3);
						}

						if (GameServer.bServer) {
							GameServer.sendRemoveCorpseFromMap(deadBody);
						}

						deadBody.removeFromWorld();
						deadBody.removeFromSquare();
					}
				}
			}
		}
	}

	private void updateRotting(float float1, float float2, boolean boolean1) {
		if (!this.isSkeleton()) {
			float float3 = float1 - this.deathTime;
			int int1 = (int)(float3 / float2);
			int int2 = this.m_zombieRotStageAtDeath + int1;
			if (int1 < 3) {
				int2 = PZMath.clamp(int2, 1, 3);
			}

			if (int2 <= 3 && int2 != this.getHumanVisual().zombieRotStage) {
				int int3 = int2 - this.getHumanVisual().zombieRotStage;
				if (boolean1) {
					DebugLog.General.debugln("%s zombieRotStage %d -> %d age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, int2, float3, int1);
				}

				this.getHumanVisual().zombieRotStage = int2;
				this.atlasTex = null;
				if (GameServer.bServer) {
					this.sendObjectChange("zombieRotStage");
				}

				if (Rand.Next(100) == 0 && this.wasZombie && SandboxOptions.instance.Lore.DisableFakeDead.getValue() == 2) {
					this.setFakeDead(true);
					if (Rand.Next(5) == 0) {
						this.setCrawling(true);
					}
				}

				String string = ClimateManager.getInstance().getSeasonName();
				if (int3 >= 1 && string != "Winter") {
					if (SandboxOptions.instance.MaggotSpawn.getValue() != 3) {
						byte byte1 = 5;
						if (string == "Summer") {
							byte1 = 3;
						}

						for (int int4 = 0; int4 < int3; ++int4) {
							InventoryItem inventoryItem;
							if (this.wasZombie) {
								if (Rand.Next(byte1) == 0) {
									inventoryItem = InventoryItemFactory.CreateItem("Maggots");
									if (inventoryItem != null && this.getContainer() != null) {
										this.getContainer().addItem(inventoryItem);
										if (inventoryItem instanceof Food) {
											((Food)inventoryItem).setPoisonPower(5);
										}
									}
								}

								if (Rand.Next(byte1 * 2) == 0 && SandboxOptions.instance.MaggotSpawn.getValue() != 2) {
									inventoryItem = InventoryItemFactory.CreateItem("Maggots");
									if (inventoryItem != null && this.getSquare() != null) {
										this.getSquare().AddWorldInventoryItem(inventoryItem, (float)(Rand.Next(10) / 10), (float)(Rand.Next(10) / 10), 0.0F);
										if (inventoryItem instanceof Food) {
											((Food)inventoryItem).setPoisonPower(5);
										}
									}
								}
							} else {
								if (Rand.Next(byte1) == 0) {
									inventoryItem = InventoryItemFactory.CreateItem("Maggots");
									if (inventoryItem != null && this.getContainer() != null) {
										this.getContainer().addItem(inventoryItem);
									}
								}

								if (Rand.Next(byte1 * 2) == 0 && SandboxOptions.instance.MaggotSpawn.getValue() != 2) {
									inventoryItem = InventoryItemFactory.CreateItem("Maggots");
									if (inventoryItem != null && this.getSquare() != null) {
										this.getSquare().AddWorldInventoryItem(inventoryItem, (float)(Rand.Next(10) / 10), (float)(Rand.Next(10) / 10), 0.0F);
									}
								}
							}
						}
					}
				}
			} else {
				if (int1 == 3 && Rand.NextBool(7)) {
					if (boolean1) {
						DebugLog.General.debugln("%s zombieRotStage %d -> x age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, float3, int1);
					}

					this.getHumanVisual().setBeardModel("");
					this.getHumanVisual().setHairModel("");
					this.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
					this.wasSkeleton = true;
					this.getWornItems().clear();
					this.getAttachedItems().clear();
					this.getContainer().clear();
					this.atlasTex = null;
					if (GameServer.bServer) {
						this.sendObjectChange("becomeSkeleton");
					}
				}
			}
		}
	}

	private boolean updateFakeDead() {
		if (!this.isFakeDead()) {
			return false;
		} else if (this.isSkeleton()) {
			return false;
		} else if ((double)this.getFakeDeadWakeupHours() > GameTime.getInstance().getWorldAgeHours()) {
			return false;
		} else if (!this.isPlayerNearby()) {
			return false;
		} else if (SandboxOptions.instance.Lore.DisableFakeDead.getValue() == 3) {
			return false;
		} else {
			this.reanimateNow();
			return true;
		}
	}

	private float getFakeDeadWakeupHours() {
		return this.deathTime + 0.5F;
	}

	private boolean isPlayerNearby() {
		if (GameServer.bServer) {
			for (int int1 = 0; int1 < GameServer.Players.size(); ++int1) {
				IsoPlayer player = (IsoPlayer)GameServer.Players.get(int1);
				boolean boolean1 = this.square != null && ServerLOS.instance.isCouldSee(player, this.square);
				if (this.isPlayerNearby(player, boolean1)) {
					return true;
				}
			}
		} else {
			IsoGridSquare square = this.getSquare();
			for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				IsoPlayer player2 = IsoPlayer.players[int2];
				boolean boolean2 = square != null && square.isCanSee(int2);
				if (this.isPlayerNearby(player2, boolean2)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isPlayerNearby(IsoPlayer player, boolean boolean1) {
		if (!boolean1) {
			return false;
		} else if (player != null && !player.isDead()) {
			if (!player.isGhostMode() && !player.isInvisible()) {
				if (player.getVehicle() != null) {
					return false;
				} else {
					float float1 = player.DistToSquared(this);
					return !(float1 < 4.0F) && !(float1 > 16.0F);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public float getReanimateTime() {
		return this.reanimateTime;
	}

	public void setReanimateTime(float float1) {
		this.reanimateTime = float1;
		if (!GameClient.bClient) {
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList();
			if (this.reanimateTime > 0.0F && !arrayList.contains(this)) {
				arrayList.add(this);
			} else if (this.reanimateTime <= 0.0F && arrayList.contains(this)) {
				arrayList.remove(this);
			}
		}
	}

	private float getReanimateDelay() {
		float float1 = 0.0F;
		float float2 = 0.0F;
		switch (SandboxOptions.instance.Lore.Reanimate.getValue()) {
		case 1: 
		
		default: 
			break;
		
		case 2: 
			float2 = 0.008333334F;
			break;
		
		case 3: 
			float2 = 0.016666668F;
			break;
		
		case 4: 
			float2 = 12.0F;
			break;
		
		case 5: 
			float1 = 48.0F;
			float2 = 72.0F;
			break;
		
		case 6: 
			float1 = 168.0F;
			float2 = 336.0F;
		
		}
		if (Core.bTutorial) {
			float2 = 0.25F;
		}

		return float1 == float2 ? float1 : Rand.Next(float1, float2);
	}

	public void reanimateLater() {
		this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours() + this.getReanimateDelay());
	}

	public void reanimateNow() {
		this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours());
	}

	public void update() {
		if (this.current == null) {
			this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
		}

		if (!GameClient.bClient) {
			if (this.reanimateTime > 0.0F) {
				float float1 = (float)GameTime.getInstance().getWorldAgeHours();
				if (this.reanimateTime <= float1) {
					this.reanimate();
				}
			}
		}
	}

	public void reanimate() {
		short short1 = -1;
		if (GameServer.bServer) {
			short1 = ServerMap.instance.getUniqueZombieId();
			if (short1 == -1) {
				return;
			}
		}

		SurvivorDesc survivorDesc = new SurvivorDesc();
		survivorDesc.setFemale(this.isFemale());
		IsoZombie zombie = new IsoZombie(IsoWorld.instance.CurrentCell, survivorDesc, -1);
		zombie.setPersistentOutfitID(this.m_persistentOutfitID);
		if (this.container == null) {
			this.container = new ItemContainer();
		}

		zombie.setInventory(this.container);
		this.container = null;
		zombie.getHumanVisual().copyFrom(this.getHumanVisual());
		zombie.getWornItems().copyFrom(this.wornItems);
		this.wornItems.clear();
		zombie.getAttachedItems().copyFrom(this.attachedItems);
		this.attachedItems.clear();
		zombie.setX(this.getX());
		zombie.setY(this.getY());
		zombie.setZ(this.getZ());
		zombie.setCurrent(this.getCurrentSquare());
		zombie.setMovingSquareNow();
		zombie.setDir(this.dir);
		LuaManager.copyTable(zombie.getModData(), this.getModData());
		zombie.getAnimationPlayer().setTargetAngle(this.m_angle);
		zombie.getAnimationPlayer().setAngleToTarget();
		zombie.setForwardDirection(Vector2.fromLengthDirection(1.0F, this.m_angle));
		zombie.setAlphaAndTarget(1.0F);
		Arrays.fill(zombie.IsVisibleToPlayer, true);
		zombie.setOnFloor(true);
		zombie.setCrawler(this.bCrawling);
		zombie.setCanWalk(!this.bCrawling);
		zombie.walkVariant = "ZombieWalk";
		zombie.DoZombieStats();
		zombie.setFallOnFront(this.isFallOnFront());
		if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
			zombie.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
			zombie.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
			zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
		}

		if (GameServer.bServer) {
			zombie.OnlineID = short1;
			ServerMap.instance.ZombieMap.put(zombie.OnlineID, zombie);
		}

		if (this.isFakeDead()) {
			zombie.setWasFakeDead(true);
		} else {
			zombie.setReanimatedPlayer(true);
			zombie.getDescriptor().setID(0);
			SharedDescriptors.createPlayerZombieDescriptor(zombie);
		}

		zombie.setReanimate(this.bCrawling);
		if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
			IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
		}

		if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie) && !IsoWorld.instance.CurrentCell.getAddList().contains(zombie)) {
			IsoWorld.instance.CurrentCell.getAddList().add(zombie);
		}

		if (GameServer.bServer) {
			if (this.player != null) {
				this.player.ReanimatedCorpse = zombie;
				this.player.ReanimatedCorpseID = zombie.OnlineID;
			}

			zombie.networkAI.reanimatedBodyID = this.objectID;
		}

		if (GameServer.bServer) {
			GameServer.sendRemoveCorpseFromMap(this);
		}

		this.removeFromWorld();
		this.removeFromSquare();
		LuaEventManager.triggerEvent("OnContainerUpdate");
		zombie.setReanimateTimer(0.0F);
		zombie.onWornItemsChanged();
		if (this.player != null) {
			if (GameServer.bServer) {
				GameServer.sendReanimatedZombieID(this.player, zombie);
			} else if (!GameClient.bClient && this.player.isLocalPlayer()) {
				this.player.ReanimatedCorpse = zombie;
			}

			this.player.setLeaveBodyTimedown(3601.0F);
		}

		zombie.actionContext.update();
		float float1 = GameTime.getInstance().FPSMultiplier;
		GameTime.getInstance().FPSMultiplier = 100.0F;
		try {
			zombie.advancedAnimator.update();
		} finally {
			GameTime.getInstance().FPSMultiplier = float1;
		}

		if (this.isFakeDead() && SoundManager.instance.isListenerInRange(this.x, this.y, 20.0F) && !GameServer.bServer) {
			zombie.parameterZombieState.setState(ParameterZombieState.State.Reanimate);
		}

		if (Core.bDebug) {
			DebugLog.Multiplayer.debugln("Reanimate: corpse=%d/%d zombie=%d delay=%f", this.getObjectID(), this.getOnlineID(), zombie.getOnlineID(), GameTime.getInstance().getWorldAgeHours() - (double)this.reanimateTime);
		}
	}

	public static void Reset() {
		Bodies.clear();
	}

	public void Collision(Vector2 vector2, IsoObject object) {
		if (object instanceof BaseVehicle) {
			BaseVehicle baseVehicle = (BaseVehicle)object;
			float float1 = 15.0F;
			Vector3f vector3f = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
			Vector3f vector3f2 = (Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc();
			baseVehicle.getLinearVelocity(vector3f);
			vector3f.y = 0.0F;
			vector3f2.set(baseVehicle.x - this.x, 0.0F, baseVehicle.z - this.z);
			vector3f2.normalize();
			vector3f.mul((Vector3fc)vector3f2);
			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f2);
			float float2 = vector3f.length();
			((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
			float2 = Math.min(float2, float1);
			if (float2 < 0.05F) {
				return;
			}

			if (Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 20.0F) {
				baseVehicle.doChrHitImpulse(this);
			}
		}
	}

	public boolean isFallOnFront() {
		return this.fallOnFront;
	}

	public void setFallOnFront(boolean boolean1) {
		this.fallOnFront = boolean1;
	}

	public InventoryItem getPrimaryHandItem() {
		return this.primaryHandItem;
	}

	public void setPrimaryHandItem(InventoryItem inventoryItem) {
		this.primaryHandItem = inventoryItem;
		this.updateContainerWithHandItems();
	}

	private void updateContainerWithHandItems() {
		if (this.getContainer() != null) {
			if (this.getPrimaryHandItem() != null) {
				this.getContainer().AddItem(this.getPrimaryHandItem());
			}

			if (this.getSecondaryHandItem() != null) {
				this.getContainer().AddItem(this.getSecondaryHandItem());
			}
		}
	}

	public InventoryItem getSecondaryHandItem() {
		return this.secondaryHandItem;
	}

	public void setSecondaryHandItem(InventoryItem inventoryItem) {
		this.secondaryHandItem = inventoryItem;
		this.updateContainerWithHandItems();
	}

	public float getAngle() {
		return this.m_angle;
	}

	public String getOutfitName() {
		return this.getHumanVisual().getOutfit() != null ? this.getHumanVisual().getOutfit().m_Name : null;
	}

	private String getDescription() {
		return String.format("object-id=%d online-id=%d bFakeDead=%b bCrawling=%b isFallOnFront=%b (x=%f,y=%f,z=%f;a=%f) outfit=%d", this.objectID, this.onlineID, this.bFakeDead, this.bCrawling, this.fallOnFront, this.x, this.y, this.z, this.m_angle, this.m_persistentOutfitID);
	}

	public String readInventory(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		if (this.getContainer() != null && this.getWornItems() != null && this.getAttachedItems() != null) {
			this.getContainer().clear();
			this.getWornItems().clear();
			this.getAttachedItems().clear();
			boolean boolean1 = byteBuffer.get() == 1;
			if (boolean1) {
				try {
					ArrayList arrayList = this.getContainer().load(byteBuffer, IsoWorld.getWorldVersion());
					this.getContainer().Capacity = 8;
					byte byte1 = byteBuffer.get();
					for (int int1 = 0; int1 < byte1; ++int1) {
						String string2 = GameWindow.ReadStringUTF(byteBuffer);
						short short1 = byteBuffer.getShort();
						if (short1 >= 0 && short1 < arrayList.size() && this.getWornItems().getBodyLocationGroup().getLocation(string2) != null) {
							this.getWornItems().setItem(string2, (InventoryItem)arrayList.get(short1));
						}
					}

					byte byte2 = byteBuffer.get();
					for (int int2 = 0; int2 < byte2; ++int2) {
						String string3 = GameWindow.ReadStringUTF(byteBuffer);
						short short2 = byteBuffer.getShort();
						if (short2 >= 0 && short2 < arrayList.size() && this.getAttachedItems().getGroup().getLocation(string3) != null) {
							this.getAttachedItems().setItem(string3, (InventoryItem)arrayList.get(short2));
						}
					}
				} catch (IOException ioException) {
					DebugLog.Multiplayer.printException(ioException, "ReadDeadBodyInventory error for dead body " + this.getOnlineID(), LogSeverity.Error);
				}
			}

			return string;
		} else {
			return string;
		}
	}

	public short getObjectID() {
		return this.objectID;
	}

	public void setObjectID(short short1) {
		this.objectID = short1;
	}

	public short getOnlineID() {
		return this.onlineID;
	}

	public void setOnlineID(short short1) {
		this.onlineID = short1;
	}

	public boolean isPlayer() {
		return this.player != null;
	}

	public static IsoDeadBody getDeadBody(short short1) {
		return (IsoDeadBody)Bodies.get(short1);
	}

	public static void addDeadBodyID(short short1, IsoDeadBody deadBody) {
		Bodies.put(short1, deadBody);
	}

	public static void removeDeadBody(short short1) {
		IsoDeadBody deadBody = (IsoDeadBody)Bodies.get(short1);
		if (deadBody != null) {
			Bodies.remove(short1);
			if (deadBody.getSquare() != null) {
				deadBody.getSquare().removeCorpse(deadBody, true);
			}
		}
	}
}
