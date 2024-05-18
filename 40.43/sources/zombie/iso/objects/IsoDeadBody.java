package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ReanimatePlayerState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.Talker;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;


public class IsoDeadBody extends IsoMovingObject implements Talker {
	public InventoryItem ClothingItem_Head;
	public InventoryItem ClothingItem_Torso;
	public InventoryItem ClothingItem_Hands;
	public InventoryItem ClothingItem_Legs;
	public InventoryItem ClothingItem_Feet;
	IsoSprite torsoSprite;
	public IsoSprite legsSprite;
	IsoSprite headSprite;
	IsoSprite shoeSprite;
	public IsoSprite topSprite;
	public IsoSprite bottomsSprite;
	public boolean wasZombie;
	public boolean bUseParts;
	private Color SpeakColor;
	private float SpeakTime;
	public ArrayList extraSprites;
	public int DescriptorID;
	private SurvivorDesc desc;
	private float deathTime;
	private long realWorldDeathTime;
	private float reanimateTime;
	private IsoPlayer player;
	private static final ThreadLocal tempZombie = new ThreadLocal(){
    
    public IsoZombie initialValue() {
        return new IsoZombie((IsoCell)null);
    }
};
	private Color tempColor;
	private static ColorInfo inf = new ColorInfo();
	public Texture atlasTex;
	private float burnTimer;
	public boolean Speaking;
	public String sayLine;
	private static ArrayList AllBodies = new ArrayList(256);

	public String getObjectName() {
		return "DeadBody";
	}

	public IsoDeadBody(IsoGameCharacter gameCharacter) {
		this(gameCharacter, false);
	}

	public IsoDeadBody(IsoGameCharacter gameCharacter, boolean boolean1) {
		super(gameCharacter.getCell(), false);
		this.wasZombie = false;
		this.bUseParts = false;
		this.SpeakTime = 0.0F;
		this.extraSprites = new ArrayList(0);
		this.deathTime = -1.0F;
		this.reanimateTime = -1.0F;
		this.tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
		this.burnTimer = 0.0F;
		this.Speaking = false;
		this.sayLine = "";
		IsoGridSquare square = gameCharacter.getCurrentSquare();
		if (square != null) {
			if (GameClient.bClient && !boolean1 && gameCharacter instanceof IsoZombie && !GameClient.instance.RecentlyDied.contains(((IsoZombie)gameCharacter).OnlineID)) {
				GameClient.instance.RecentlyDied.add(((IsoZombie)gameCharacter).OnlineID);
			}

			this.square = square;
			this.current = square;
			this.bUseParts = true;
			if (gameCharacter instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter).removeSaveFile();
			}

			square.getStaticMovingObjects().add(this);
			if (gameCharacter instanceof IsoSurvivor) {
				IsoWorld world = IsoWorld.instance;
				world.TotalSurvivorNights += ((IsoSurvivor)((IsoSurvivor)gameCharacter)).nightsSurvived;
				++IsoWorld.instance.TotalSurvivorsDead;
				if (IsoWorld.instance.SurvivorSurvivalRecord < ((IsoSurvivor)((IsoSurvivor)gameCharacter)).nightsSurvived) {
					IsoWorld.instance.SurvivorSurvivalRecord = ((IsoSurvivor)((IsoSurvivor)gameCharacter)).nightsSurvived;
				}
			}

			this.wasZombie = gameCharacter instanceof IsoZombie;
			if (this.wasZombie && ((IsoZombie)gameCharacter).bCrawling) {
				if (!"ZombieCrawl".equals(gameCharacter.legsSprite.CurrentAnim.name)) {
					gameCharacter.PlayAnim("ZombieCrawl");
					gameCharacter.def.Frame = 4.0F;
				}
			} else if (!"ZombieDeath".equals(gameCharacter.legsSprite.CurrentAnim.name)) {
				gameCharacter.PlayAnim("ZombieDeath");
				gameCharacter.def.Frame = (float)(gameCharacter.sprite.CurrentAnim.Frames.size() - 1);
			}

			if (!boolean1 && !gameCharacter.getScriptName().equals("none")) {
				ScriptManager.instance.Trigger("OnCharacterDeath", gameCharacter.getScriptName());
			}

			this.dir = gameCharacter.dir;
			this.Collidable = false;
			int int1;
			if (!GameServer.bServer || ServerGUI.isCreated()) {
				int int2 = (int)gameCharacter.def.Frame;
				if (int2 > gameCharacter.legsSprite.CurrentAnim.Frames.size() - 1) {
					int2 = gameCharacter.legsSprite.CurrentAnim.Frames.size() - 1;
				}

				if (gameCharacter.getTorsoSprite() != null) {
					this.torsoSprite = null;
					this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)gameCharacter.torsoSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), gameCharacter.torsoSprite.TintMod.toColor());
					this.torsoSprite.Animate = false;
				}

				this.legsSprite = null;
				this.legsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)gameCharacter.legsSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), gameCharacter.legsSprite.TintMod.toColor());
				this.legsSprite.TintMod.r = gameCharacter.legsSprite.TintMod.r;
				this.legsSprite.TintMod.g = gameCharacter.legsSprite.TintMod.g;
				this.legsSprite.TintMod.b = gameCharacter.legsSprite.TintMod.b;
				float float1 = this.modelLightAdjust(gameCharacter);
				ColorInfo colorInfo = this.legsSprite.TintMod;
				colorInfo.r *= float1;
				colorInfo = this.legsSprite.TintMod;
				colorInfo.g *= float1;
				colorInfo = this.legsSprite.TintMod;
				colorInfo.b *= float1;
				this.sprite = this.legsSprite;
				this.legsSprite.Animate = false;
				if (gameCharacter.getHeadSprite() != null) {
					this.headSprite = null;
					this.headSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)gameCharacter.headSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), gameCharacter.headSprite.TintMod.toColor());
					this.headSprite.Animate = false;
				}

				float float2;
				Texture texture;
				if (gameCharacter.getTopSprite() != null && gameCharacter.topSprite.CurrentAnim != null && gameCharacter.topSprite.CurrentAnim.Frames.size() > int2) {
					this.topSprite = null;
					texture = ((IsoDirectionFrame)gameCharacter.topSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
					if (texture != null && texture.getName() != null) {
						this.topSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(texture.getName(), gameCharacter.topSprite.TintMod.toColor());
						this.topSprite.TintMod.r = gameCharacter.getTopSprite().TintMod.r;
						this.topSprite.TintMod.g = gameCharacter.getTopSprite().TintMod.g;
						this.topSprite.TintMod.b = gameCharacter.getTopSprite().TintMod.b;
						float2 = this.modelLightAdjust(gameCharacter);
						colorInfo = this.topSprite.TintMod;
						colorInfo.r *= float2;
						colorInfo = this.topSprite.TintMod;
						colorInfo.g *= float2;
						colorInfo = this.topSprite.TintMod;
						colorInfo.b *= float2;
						this.topSprite.Animate = false;
					}
				}

				if (gameCharacter.getBottomsSprite() != null && gameCharacter.bottomsSprite.CurrentAnim != null && gameCharacter.bottomsSprite.CurrentAnim.Frames.size() > int2) {
					this.bottomsSprite = null;
					texture = ((IsoDirectionFrame)gameCharacter.bottomsSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
					if (texture != null && texture.getName() != null) {
						this.bottomsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(texture.getName(), gameCharacter.bottomsSprite.TintMod.toColor());
						this.bottomsSprite.TintMod.r = gameCharacter.getBottomsSprite().TintMod.r;
						this.bottomsSprite.TintMod.g = gameCharacter.getBottomsSprite().TintMod.g;
						this.bottomsSprite.TintMod.b = gameCharacter.getBottomsSprite().TintMod.b;
						float2 = this.modelLightAdjust(gameCharacter);
						colorInfo = this.bottomsSprite.TintMod;
						colorInfo.r *= float2;
						colorInfo = this.bottomsSprite.TintMod;
						colorInfo.g *= float2;
						colorInfo = this.bottomsSprite.TintMod;
						colorInfo.b *= float2;
						this.bottomsSprite.Animate = false;
					}
				}

				this.sprite = this.legsSprite;
				if (gameCharacter.getHairSprite() != null && gameCharacter.hairSprite.CurrentAnim != null && gameCharacter.hairSprite.CurrentAnim.Frames.size() > int2) {
					texture = null;
					Texture texture2 = ((IsoDirectionFrame)gameCharacter.hairSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
					if (texture2 != null && texture2.getName() != null) {
						IsoSprite sprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)gameCharacter.hairSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), gameCharacter.hairSprite.TintMod.toColor());
						sprite.TintMod.r = gameCharacter.getHairSprite().TintMod.r;
						sprite.TintMod.g = gameCharacter.getHairSprite().TintMod.g;
						sprite.TintMod.b = gameCharacter.getHairSprite().TintMod.b;
						float float3 = this.modelLightAdjust(gameCharacter);
						colorInfo = sprite.TintMod;
						colorInfo.r *= float3;
						colorInfo = sprite.TintMod;
						colorInfo.g *= float3;
						colorInfo = sprite.TintMod;
						colorInfo.b *= float3;
						sprite.Animate = false;
						this.extraSprites.add(sprite);
					}
				}

				for (int1 = 0; int1 < gameCharacter.getExtraSprites().size(); ++int1) {
					IsoSprite sprite2 = (IsoSprite)gameCharacter.getExtraSprites().get(int1);
					IsoSprite sprite3 = null;
					if (sprite2.CurrentAnim.Frames.size() > int2 && sprite2 != null && sprite2.CurrentAnim != null && ((IsoDirectionFrame)sprite2.CurrentAnim.Frames.get(int2)).getTexture(this.dir) != null) {
						sprite3 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)sprite2.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), sprite2.TintMod.toColor());
						sprite3.TintMod.r = sprite2.TintMod.r;
						sprite3.TintMod.g = sprite2.TintMod.g;
						sprite3.TintMod.b = sprite2.TintMod.b;
						if (gameCharacter.legsSprite.modelSlot != null) {
							colorInfo = sprite3.TintMod;
							colorInfo.r *= 0.5F;
							colorInfo = sprite3.TintMod;
							colorInfo.g *= 0.5F;
							colorInfo = sprite3.TintMod;
							colorInfo.b *= 0.5F;
						}

						sprite3.Animate = false;
						this.extraSprites.add(sprite3);
					}
				}
			}

			this.sprite = this.legsSprite;
			this.def = IsoSpriteInstance.get(this.sprite);
			this.def.Frame = 0.0F;
			this.def.Flip = false;
			this.x = gameCharacter.getX();
			this.y = gameCharacter.getY();
			this.z = gameCharacter.getZ();
			this.nx = this.x;
			this.ny = this.y;
			this.offsetX = gameCharacter.offsetX;
			this.offsetY = gameCharacter.offsetY;
			this.solid = false;
			this.shootable = false;
			this.OutlineOnMouseover = true;
			this.container = gameCharacter.getInventory();
			gameCharacter.setInventory(new ItemContainer());
			this.ClothingItem_Torso = gameCharacter.getClothingItem_Torso();
			this.ClothingItem_Legs = gameCharacter.getClothingItem_Legs();
			this.ClothingItem_Feet = gameCharacter.getClothingItem_Feet();
			this.container.setExplored(gameCharacter instanceof IsoPlayer || gameCharacter instanceof IsoZombie && ((IsoZombie)gameCharacter).isReanimatedPlayer());
			this.container.parent = this;
			if (gameCharacter.bFemale) {
				this.container.type = "inventoryfemale";
			} else {
				this.container.type = "inventorymale";
			}

			this.container.Capacity = 8;
			boolean boolean2 = gameCharacter.isOnFire();
			if (gameCharacter instanceof IsoZombie) {
				this.DescriptorID = ((IsoZombie)gameCharacter).getDescriptor().getID();
				if (!boolean1) {
					if (GameServer.bServer) {
						this.square.revisionUp();
						GameServer.sendDeleteZombie((IsoZombie)gameCharacter);
					} else {
						for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
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
				}
			} else {
				if (gameCharacter instanceof IsoSurvivor) {
					this.getCell().getSurvivorList().remove((IsoSurvivor)gameCharacter);
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
			this.realWorldDeathTime = System.currentTimeMillis();
			if (!GameClient.bClient) {
				int1 = 0;
				for (int int3 = 0; int3 < AllBodies.size() && !(((IsoDeadBody)AllBodies.get(int3)).deathTime >= this.deathTime); ++int3) {
					++int1;
				}

				AllBodies.add(int1, this);
			}

			if (!GameServer.bServer) {
				FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
			}

			gameCharacter = null;
		}
	}

	public IsoDeadBody(IsoCell cell) {
		super(cell, false);
		this.wasZombie = false;
		this.bUseParts = false;
		this.SpeakTime = 0.0F;
		this.extraSprites = new ArrayList(0);
		this.deathTime = -1.0F;
		this.reanimateTime = -1.0F;
		this.tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
		this.burnTimer = 0.0F;
		this.Speaking = false;
		this.sayLine = "";
		this.SpeakColor = Color.white;
		this.solid = false;
		this.shootable = false;
		this.bUseParts = true;
	}

	public InventoryItem getItem() {
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.CorpseMale");
		inventoryItem.storeInByteData(this);
		return inventoryItem;
	}

	private void initSpriteParts() {
		SharedDescriptors.Descriptor descriptor = SharedDescriptors.getDescriptor(this.DescriptorID);
		SurvivorDesc survivorDesc = descriptor == null ? this.desc : descriptor.desc;
		int int1 = descriptor == null ? 1 : descriptor.palette;
		IsoZombie zombie = (IsoZombie)tempZombie.get();
		if (survivorDesc != null) {
			zombie.setDescriptor(survivorDesc);
			if (survivorDesc.isFemale()) {
				zombie.SpriteName = "KateZ";
			} else {
				zombie.SpriteName = "BobZ";
			}

			zombie.palette = int1;
			zombie.bFemale = survivorDesc.isFemale();
			zombie.InitSpritePartsZombie();
		}

		zombie.PlayAnim("ZombieDeath");
		zombie.def.Frame = (float)(zombie.sprite.CurrentAnim.Frames.size() - 1);
		int int2 = (int)zombie.def.Frame;
		if (zombie.getTorsoSprite() != null) {
			this.torsoSprite = null;
			this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)zombie.torsoSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), zombie.torsoSprite.TintMod.toColor());
			this.torsoSprite.Animate = false;
		}

		this.legsSprite = null;
		this.legsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)zombie.legsSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), zombie.legsSprite.TintMod.toColor());
		this.legsSprite.TintMod.r = zombie.legsSprite.TintMod.r;
		this.legsSprite.TintMod.g = zombie.legsSprite.TintMod.g;
		this.legsSprite.TintMod.b = zombie.legsSprite.TintMod.b;
		float float1 = this.modelLightAdjust(zombie);
		ColorInfo colorInfo = this.legsSprite.TintMod;
		colorInfo.r *= float1;
		colorInfo = this.legsSprite.TintMod;
		colorInfo.g *= float1;
		colorInfo = this.legsSprite.TintMod;
		colorInfo.b *= float1;
		this.sprite = this.legsSprite;
		this.legsSprite.Animate = false;
		if (zombie.getHeadSprite() != null) {
			this.headSprite = null;
			this.headSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)zombie.headSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), zombie.headSprite.TintMod.toColor());
			this.headSprite.Animate = false;
		}

		float float2;
		Texture texture;
		if (zombie.getTopSprite() != null && zombie.topSprite.CurrentAnim != null && zombie.topSprite.CurrentAnim.Frames.size() > int2) {
			this.topSprite = null;
			texture = ((IsoDirectionFrame)zombie.topSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
			if (texture != null && texture.getName() != null) {
				this.topSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(texture.getName(), zombie.topSprite.TintMod.toColor());
				this.topSprite.TintMod.r = zombie.getTopSprite().TintMod.r;
				this.topSprite.TintMod.g = zombie.getTopSprite().TintMod.g;
				this.topSprite.TintMod.b = zombie.getTopSprite().TintMod.b;
				float2 = this.modelLightAdjust(zombie);
				colorInfo = this.topSprite.TintMod;
				colorInfo.r *= float2;
				colorInfo = this.topSprite.TintMod;
				colorInfo.g *= float2;
				colorInfo = this.topSprite.TintMod;
				colorInfo.b *= float2;
				this.topSprite.Animate = false;
			}
		}

		if (zombie.getBottomsSprite() != null && zombie.bottomsSprite.CurrentAnim != null && zombie.bottomsSprite.CurrentAnim.Frames.size() > int2) {
			this.bottomsSprite = null;
			texture = ((IsoDirectionFrame)zombie.bottomsSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
			if (texture != null && texture.getName() != null) {
				this.bottomsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(texture.getName(), zombie.bottomsSprite.TintMod.toColor());
				this.bottomsSprite.TintMod.r = zombie.getBottomsSprite().TintMod.r;
				this.bottomsSprite.TintMod.g = zombie.getBottomsSprite().TintMod.g;
				this.bottomsSprite.TintMod.b = zombie.getBottomsSprite().TintMod.b;
				float2 = this.modelLightAdjust(zombie);
				colorInfo = this.bottomsSprite.TintMod;
				colorInfo.r *= float2;
				colorInfo = this.bottomsSprite.TintMod;
				colorInfo.g *= float2;
				colorInfo = this.bottomsSprite.TintMod;
				colorInfo.b *= float2;
				this.bottomsSprite.Animate = false;
			}
		}

		if (zombie.getHairSprite() != null && zombie.hairSprite.CurrentAnim != null && zombie.hairSprite.CurrentAnim.Frames.size() > int2) {
			texture = null;
			Texture texture2 = ((IsoDirectionFrame)zombie.hairSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir);
			if (texture2 != null && texture2.getName() != null) {
				IsoSprite sprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)zombie.hairSprite.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), zombie.hairSprite.TintMod.toColor());
				sprite.TintMod.r = zombie.getHairSprite().TintMod.r;
				sprite.TintMod.g = zombie.getHairSprite().TintMod.g;
				sprite.TintMod.b = zombie.getHairSprite().TintMod.b;
				float float3 = this.modelLightAdjust(zombie);
				colorInfo = sprite.TintMod;
				colorInfo.r *= float3;
				colorInfo = sprite.TintMod;
				colorInfo.g *= float3;
				colorInfo = sprite.TintMod;
				colorInfo.b *= float3;
				sprite.Animate = false;
				this.extraSprites.add(sprite);
			}
		}

		for (int int3 = 0; int3 < zombie.getExtraSprites().size(); ++int3) {
			IsoSprite sprite2 = (IsoSprite)zombie.getExtraSprites().get(int3);
			IsoSprite sprite3 = null;
			if (sprite2.CurrentAnim.Frames.size() > int2 && sprite2 != null && sprite2.CurrentAnim != null && ((IsoDirectionFrame)sprite2.CurrentAnim.Frames.get(int2)).getTexture(this.dir) != null) {
				sprite3 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)sprite2.CurrentAnim.Frames.get(int2)).getTexture(this.dir).getName(), sprite2.TintMod.toColor());
				sprite3.TintMod.r = sprite2.TintMod.r;
				sprite3.TintMod.g = sprite2.TintMod.g;
				sprite3.TintMod.b = sprite2.TintMod.b;
				if (zombie.legsSprite.modelSlot != null) {
					colorInfo = sprite3.TintMod;
					colorInfo.r *= 0.5F;
					colorInfo = sprite3.TintMod;
					colorInfo.g *= 0.5F;
					colorInfo = sprite3.TintMod;
					colorInfo.b *= 0.5F;
				}

				sprite3.Animate = false;
				this.extraSprites.add(sprite3);
			}
		}

		this.def = IsoSpriteInstance.get(this.sprite);
		this.def.Frame = 0.0F;
		this.def.Flip = false;
	}

	private IsoSprite loadSprite(ByteBuffer byteBuffer) {
		String string = GameWindow.ReadString(byteBuffer);
		Color color = this.tempColor;
		color.r = byteBuffer.getFloat();
		color.g = byteBuffer.getFloat();
		color.b = byteBuffer.getFloat();
		color.a = byteBuffer.getFloat();
		IsoSprite sprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(string, color);
		sprite.TintMod.r = color.r;
		sprite.TintMod.g = color.g;
		sprite.TintMod.b = color.b;
		sprite.TintMod.a = color.a;
		sprite.Animate = false;
		return sprite;
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.wasZombie = byteBuffer.get() == 1;
		boolean boolean1 = byteBuffer.get() == 1;
		int int2;
		if (boolean1) {
			this.sprite = null;
			if (int1 >= 27) {
				this.DescriptorID = byteBuffer.getShort();
			}
		} else {
			this.legsSprite = this.loadSprite(byteBuffer);
			this.sprite = this.legsSprite;
			if (byteBuffer.get() == 1) {
				this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(GameWindow.ReadString(byteBuffer));
			}

			if (byteBuffer.get() == 1) {
				this.headSprite = this.loadSprite(byteBuffer);
			}

			if (byteBuffer.get() == 1) {
				this.bottomsSprite = this.loadSprite(byteBuffer);
			}

			if (byteBuffer.get() == 1) {
				this.shoeSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(GameWindow.ReadString(byteBuffer));
			}

			if (byteBuffer.get() == 1) {
				this.topSprite = this.loadSprite(byteBuffer);
			}

			int2 = byteBuffer.getInt();
			for (int int3 = 0; int3 < int2; ++int3) {
				IsoSprite sprite = this.loadSprite(byteBuffer);
				this.extraSprites.add(sprite);
			}
		}

		if (int1 >= 57 && byteBuffer.get() == 1) {
			this.desc = new SurvivorDesc(true);
			this.desc.load(byteBuffer, int1, (IsoGameCharacter)null);
		}

		if (byteBuffer.get() == 1) {
			int2 = byteBuffer.getInt();
			try {
				this.container = new ItemContainer();
				this.container.ID = int2;
				this.container.parent = this;
				this.container.Capacity = 8;
				this.container.SourceGrid = this.square;
				ArrayList arrayList = this.container.load(byteBuffer, int1, false);
				if (int1 >= 32) {
					short short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size()) {
						this.ClothingItem_Torso = (InventoryItem)arrayList.get(short1);
					}

					short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size()) {
						this.ClothingItem_Legs = (InventoryItem)arrayList.get(short1);
					}

					short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size()) {
						this.ClothingItem_Feet = (InventoryItem)arrayList.get(short1);
					}
				}
			} catch (Exception exception) {
				if (this.container != null) {
					DebugLog.log("Failed to stream in container ID: " + this.container.ID);
				}
			}
		}

		if (int1 >= 57) {
			this.deathTime = byteBuffer.getFloat();
		}

		if (int1 >= 65) {
			this.reanimateTime = byteBuffer.getFloat();
		}

		if (boolean1 && (GameClient.bClient || GameServer.bServer && ServerGUI.isCreated())) {
			this.initSpriteParts();
			this.checkClothing();
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		super.save(byteBuffer);
		byteBuffer.put((byte)(this.wasZombie ? 1 : 0));
		if (!GameServer.bServer && !GameClient.bClient) {
			byteBuffer.put((byte)0);
			try {
				this.legsSprite.name = ((IsoDirectionFrame)this.legsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			GameWindow.WriteString(byteBuffer, this.legsSprite.name);
			byteBuffer.putFloat(this.legsSprite.TintMod.r);
			byteBuffer.putFloat(this.legsSprite.TintMod.g);
			byteBuffer.putFloat(this.legsSprite.TintMod.b);
			byteBuffer.putFloat(this.legsSprite.TintMod.a);
			if (this.torsoSprite != null && ((IsoDirectionFrame)this.torsoSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
				this.torsoSprite.name = ((IsoDirectionFrame)this.torsoSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.torsoSprite.name);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.headSprite != null && ((IsoDirectionFrame)this.headSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
				this.headSprite.name = ((IsoDirectionFrame)this.headSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.headSprite.name);
				byteBuffer.putFloat(this.headSprite.TintMod.r);
				byteBuffer.putFloat(this.headSprite.TintMod.g);
				byteBuffer.putFloat(this.headSprite.TintMod.b);
				byteBuffer.putFloat(this.headSprite.TintMod.a);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.bottomsSprite != null && ((IsoDirectionFrame)this.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
				this.bottomsSprite.name = ((IsoDirectionFrame)this.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.bottomsSprite.name);
				byteBuffer.putFloat(this.bottomsSprite.TintMod.r);
				byteBuffer.putFloat(this.bottomsSprite.TintMod.g);
				byteBuffer.putFloat(this.bottomsSprite.TintMod.b);
				byteBuffer.putFloat(this.bottomsSprite.TintMod.a);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.shoeSprite != null && ((IsoDirectionFrame)this.shoeSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
				this.shoeSprite.name = ((IsoDirectionFrame)this.shoeSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.shoeSprite.name);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.topSprite != null && ((IsoDirectionFrame)this.topSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
				this.topSprite.name = ((IsoDirectionFrame)this.topSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				byteBuffer.put((byte)1);
				GameWindow.WriteString(byteBuffer, this.topSprite.name);
				byteBuffer.putFloat(this.topSprite.TintMod.r);
				byteBuffer.putFloat(this.topSprite.TintMod.g);
				byteBuffer.putFloat(this.topSprite.TintMod.b);
				byteBuffer.putFloat(this.topSprite.TintMod.a);
			} else {
				byteBuffer.put((byte)0);
			}

			byteBuffer.putInt(this.extraSprites.size());
			for (int int1 = 0; int1 < this.extraSprites.size(); ++int1) {
				IsoSprite sprite = (IsoSprite)this.extraSprites.get(int1);
				sprite.name = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
				GameWindow.WriteString(byteBuffer, sprite.name);
				byteBuffer.putFloat(sprite.TintMod.r);
				byteBuffer.putFloat(sprite.TintMod.g);
				byteBuffer.putFloat(sprite.TintMod.b);
				byteBuffer.putFloat(sprite.TintMod.a);
			}
		} else {
			byteBuffer.put((byte)1);
			byteBuffer.putShort((short)this.DescriptorID);
		}

		if (this.desc != null) {
			byteBuffer.put((byte)1);
			this.desc.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.container != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.container.ID);
			ArrayList arrayList = this.container.save(byteBuffer, false);
			if (this.ClothingItem_Torso != null) {
				byteBuffer.putShort((short)arrayList.indexOf(this.ClothingItem_Torso));
			} else {
				byteBuffer.putShort((short)-1);
			}

			if (this.ClothingItem_Legs != null) {
				byteBuffer.putShort((short)arrayList.indexOf(this.ClothingItem_Legs));
			} else {
				byteBuffer.putShort((short)-1);
			}

			if (this.ClothingItem_Feet != null) {
				byteBuffer.putShort((short)arrayList.indexOf(this.ClothingItem_Feet));
			} else {
				byteBuffer.putShort((short)-1);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.putFloat(this.deathTime);
		byteBuffer.putFloat(this.reanimateTime);
	}

	public void softReset() {
		this.square.RemoveTileObject(this);
	}

	public void renderlast() {
		if (this.Speaking) {
			float float1 = (float)this.sx;
			float float2 = (float)this.sy;
			float1 = (float)((int)float1);
			float2 = (float)((int)float2);
			float1 -= (float)((int)IsoCamera.getOffX());
			float2 -= (float)((int)IsoCamera.getOffY());
			float1 += 8.0F;
			float2 += 32.0F;
			if (this.sayLine != null) {
				IndieGL.End();
				TextManager.instance.DrawStringCentre(UIFont.Medium, (double)((int)float1), (double)((int)float2), this.sayLine, (double)this.SpeakColor.r, (double)this.SpeakColor.g, (double)this.SpeakColor.b, (double)this.SpeakColor.a);
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		boolean boolean2 = this.isHighlighted();
		float float4;
		if (ModelManager.instance.bDebugEnableModels && PerformanceSettings.modelsEnabled && PerformanceSettings.numberOf3D > 0 && PerformanceSettings.corpses3D && ModelManager.instance.bCreated) {
			if (this.atlasTex == null) {
				this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
				DeadBodyAtlas.instance.render();
			}

			if (this.atlasTex != null) {
				if (IsoSprite.globalOffsetX == -1) {
					IsoSprite.globalOffsetX = -((int)IsoCamera.frameState.OffX);
					IsoSprite.globalOffsetY = -((int)IsoCamera.frameState.OffY);
				}

				this.sx = 0;
				float float5;
				if (this.sx == 0) {
					float5 = IsoUtils.XToScreen(float1, float2, float3, 0);
					float4 = IsoUtils.YToScreen(float1, float2, float3, 0);
					float5 = (float)((int)float5);
					float4 = (float)((int)float4);
					this.sx = (int)float5;
					this.sy = (int)float4;
				}

				float5 = (float)(this.sx + IsoSprite.globalOffsetX - 44 + 8);
				float4 = (float)(this.sy + IsoSprite.globalOffsetY - 119 + 60 + 32);
				if (Core.TileScale == 1) {
					float5 = (float)(this.sx + IsoSprite.globalOffsetX + IsoGameCharacter.RENDER_OFFSET_X - 38);
					float4 = (float)(this.sy + IsoSprite.globalOffsetY + IsoGameCharacter.RENDER_OFFSET_Y + 59);
				}

				byte byte1 = 72;
				byte byte2 = 64;
				float5 -= (float)((DeadBodyAtlas.ENTRY_WID - byte1) / 2);
				float4 -= (float)((DeadBodyAtlas.ENTRY_HGT - byte2) / 2);
				if (boolean2) {
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
				if (!boolean2 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
					this.getCurrentSquare().interpolateLight(colorInfo, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
				}

				if (!boolean2) {
					float float6 = RenderSettings.getInstance().getAmbientForPlayer(IsoCamera.frameState.playerIndex);
					if (this.getSquare() != null && this.getSquare().getRoom() != null) {
						float6 *= 0.6F;
					}

					ColorInfo colorInfo2 = inf;
					colorInfo2.r *= float6;
					colorInfo2 = inf;
					colorInfo2.g *= float6;
					colorInfo2 = inf;
					colorInfo2.b *= float6;
				}

				this.atlasTex.render((int)float5, (int)float4, this.atlasTex.getWidth(), this.atlasTex.getHeight(), colorInfo.r, colorInfo.g, colorInfo.b, colorInfo.a);
				if (Core.bDebug) {
				}

				this.sx = (int)(IsoUtils.XToScreen(float1, float2, float3, 0) - (float)(IsoGameCharacter.RENDER_OFFSET_X * Core.TileScale));
				this.sy = (int)(IsoUtils.YToScreen(float1, float2, float3, 0) - (float)(IsoGameCharacter.RENDER_OFFSET_Y * Core.TileScale));
				if (IsoObjectPicker.Instance.wasDirty) {
					this.renderObjectPicker(this.getX(), this.getY(), this.getZ(), colorInfo);
				}

				return;
			}
		}

		int int1 = Core.TileScale;
		float4 = (float)(IsoGameCharacter.RENDER_OFFSET_X * int1);
		float float7 = (float)(IsoGameCharacter.RENDER_OFFSET_Y * int1);
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this.sprite);
		}

		this.def.setScale((float)int1, (float)int1);
		this.def.Frame = 0.0F;
		if (boolean2) {
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
		if (!boolean2 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null && (!GameServer.bServer || !ServerGUI.isCreated())) {
			this.getCurrentSquare().interpolateLight(colorInfo, float1 - (float)this.getCurrentSquare().getX(), float2 - (float)this.getCurrentSquare().getY());
		}

		if (!this.bUseParts) {
			this.sprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
		} else {
			if (this.legsSprite != null) {
				this.legsSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			if (this.torsoSprite != null) {
				this.torsoSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			if (this.headSprite != null) {
				this.headSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			if (this.bottomsSprite != null) {
				this.bottomsSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			if (this.shoeSprite != null) {
				this.shoeSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			if (this.topSprite != null) {
				this.topSprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}

			for (int int2 = 0; int2 < this.extraSprites.size(); ++int2) {
				IsoSprite sprite = (IsoSprite)this.extraSprites.get(int2);
				sprite.render(this.def, this, float1, float2, float3, this.dir, float4, float7, colorInfo);
			}
		}

		if (Core.bDebug) {
		}
	}

	public void renderObjectPicker(float float1, float float2, float float3, ColorInfo colorInfo) {
		int int1 = Core.TileScale;
		float float4 = (float)(IsoGameCharacter.RENDER_OFFSET_X * int1);
		float float5 = (float)(IsoGameCharacter.RENDER_OFFSET_Y * int1);
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this.sprite);
		}

		this.def.setScale((float)int1, (float)int1);
		if (!this.bUseParts) {
			this.sprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, float4, float5, colorInfo);
		} else {
			this.legsSprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, float4, float5, colorInfo);
			if (this.torsoSprite != null) {
				this.torsoSprite.renderObjectPicker(this.def, this, float1, float2, float3, this.dir, float4, float5, colorInfo);
			}
		}
	}

	public void Burn() {
		if (!GameClient.bClient) {
			if (this.getSquare() != null && this.getSquare().getProperties().Is(IsoFlagType.burning)) {
				this.burnTimer += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
				if (this.burnTimer >= 5.0F) {
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
						GameServer.removeCorpseFromMap(this);
					}

					this.getSquare().removeCorpse(this, true);
				}
			}
		}
	}

	public void checkClothing() {
		if (this.ClothingItem_Torso == null || this.container == null || this.container.getItems().indexOf(this.ClothingItem_Torso) == -1) {
			this.topSprite = null;
			this.ClothingItem_Torso = null;
			if (this.desc != null) {
				this.desc.toppal = null;
			}

			this.atlasTex = null;
		}

		if (this.ClothingItem_Legs == null || this.container == null || this.container.getItems().indexOf(this.ClothingItem_Legs) == -1) {
			this.bottomsSprite = null;
			this.ClothingItem_Legs = null;
			if (this.desc != null) {
				this.desc.bottomspal = null;
			}

			this.atlasTex = null;
		}

		if (this.ClothingItem_Feet == null || this.container == null || this.container.getItems().indexOf(this.ClothingItem_Feet) == -1) {
			this.shoeSprite = null;
			this.ClothingItem_Feet = null;
			if (this.desc != null) {
				this.desc.shoespal = null;
			}

			this.atlasTex = null;
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
		if (TutorialManager.instance.ProfanityFilter) {
			this.sayLine = this.sayLine.replace("Fuck", "****");
			this.sayLine = this.sayLine.replace("fuck", "****");
			this.sayLine = this.sayLine.replace("Shit", "****");
			this.sayLine = this.sayLine.replace("shit", "****");
			this.sayLine = this.sayLine.replace("FUCK", "****");
			this.sayLine = this.sayLine.replace("SHIT", "****");
		}

		this.Speaking = true;
	}

	public String getSayLine() {
		return this.sayLine;
	}

	public String getTalkerType() {
		return "Talker";
	}

	private float modelLightAdjust(IsoGameCharacter gameCharacter) {
		return 1.0F;
	}

	public void addToWorld() {
		super.addToWorld();
		if (!GameServer.bServer) {
			FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
		}

		if (!GameClient.bClient) {
			if (this.reanimateTime > 0.0F && !IsoWorld.instance.getCell().getStaticUpdaterObjectList().contains(this)) {
				IsoWorld.instance.getCell().getStaticUpdaterObjectList().add(this);
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

			int int1 = 0;
			for (int int2 = 0; int2 < AllBodies.size() && !(((IsoDeadBody)AllBodies.get(int2)).deathTime >= this.deathTime); ++int2) {
				++int1;
			}

			AllBodies.add(int1, this);
		}
	}

	public void removeFromWorld() {
		if (!GameServer.bServer) {
			FliesSound.instance.corpseRemoved((int)this.getX(), (int)this.getY(), (int)this.getZ());
		}

		if (!GameClient.bClient) {
			AllBodies.remove(this);
		}

		super.removeFromWorld();
	}

	public static void updateBodies() {
		if (!GameClient.bClient) {
			float float1 = (float)SandboxOptions.instance.HoursForCorpseRemoval.getValue();
			if (!(float1 <= 0.0F)) {
				float float2 = (float)GameTime.getInstance().getWorldAgeHours();
				for (int int1 = 0; int1 < AllBodies.size(); ++int1) {
					IsoDeadBody deadBody = (IsoDeadBody)AllBodies.get(int1);
					if (ServerOptions.instance.RemovePlayerCorpsesOnCorpseRemoval.getValue() || deadBody.wasZombie) {
						if (deadBody.deathTime > float2) {
							deadBody.deathTime = float2;
						}

						float float3 = float2 - deadBody.deathTime;
						if (float3 < float1) {
							break;
						}

						if (GameServer.bServer) {
							GameServer.removeCorpseFromMap(deadBody);
						}

						deadBody.removeFromWorld();
						deadBody.removeFromSquare();
						--int1;
					}
				}
			}
		}
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
			float2 = 0.5F;
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
		this.realWorldDeathTime = 0L;
	}

	public void update() {
		if (this.current == null) {
			this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
		}

		if (!GameClient.bClient) {
			if (this.reanimateTime > 0.0F) {
				if (System.currentTimeMillis() - this.realWorldDeathTime < 10000L) {
					return;
				}

				float float1 = (float)GameTime.getInstance().getWorldAgeHours();
				if (this.reanimateTime <= float1) {
					this.reanimate();
				}
			}
		}
	}

	private static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException numberFormatException) {
			return false;
		}
	}

	private String getSpriteTextureName(IsoSprite sprite) {
		if (sprite == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < sprite.AnimStack.size(); ++int1) {
				IsoAnim anim = (IsoAnim)sprite.AnimStack.get(int1);
				for (int int2 = 0; int2 < anim.Frames.size(); ++int2) {
					IsoDirectionFrame directionFrame = (IsoDirectionFrame)anim.Frames.get(int2);
					IsoDirections[] directionsArray = IsoDirections.values();
					int int3 = directionsArray.length;
					for (int int4 = 0; int4 < int3; ++int4) {
						IsoDirections directions = directionsArray[int4];
						if (directions != IsoDirections.Max && directionFrame.getTexture(directions) != null) {
							return directionFrame.getTexture(directions).getName();
						}
					}
				}
			}

			return null;
		}
	}

	private int getTorsoNumber(SurvivorDesc survivorDesc) {
		String string = survivorDesc.getTorso();
		if (string != null && string.contains("_")) {
			String[] stringArray = string.split("_");
			return stringArray.length == 2 && isInteger(stringArray[1]) ? Integer.valueOf(stringArray[1]) - 1 : 0;
		} else {
			return 0;
		}
	}

	private SurvivorDesc createSurvivorDesc() {
		if (this.desc != null) {
			int int1 = this.getTorsoNumber(this.desc);
			if (this.desc.isFemale()) {
				if (int1 == 0) {
					this.desc.skinColor.r = Rand.Next(0.9F, 1.0F);
					this.desc.skinColor.g = Rand.Next(0.75F, 0.88F);
					this.desc.skinColor.b = Rand.Next(0.45F, 0.58F);
				} else {
					this.desc.skinColor.r = Rand.Next(0.5F, 0.6F);
					this.desc.skinColor.g = Rand.Next(0.3F, 0.4F);
					this.desc.skinColor.b = Rand.Next(0.15F, 0.23F);
				}
			} else if (int1 < 4) {
				this.desc.skinColor.r = Rand.Next(0.9F, 1.0F);
				this.desc.skinColor.g = Rand.Next(0.75F, 0.88F);
				this.desc.skinColor.b = Rand.Next(0.45F, 0.58F);
			} else {
				this.desc.skinColor.r = Rand.Next(0.5F, 0.6F);
				this.desc.skinColor.g = Rand.Next(0.3F, 0.4F);
				this.desc.skinColor.b = Rand.Next(0.15F, 0.23F);
			}

			return this.desc;
		} else if (!GameServer.bServer && !GameClient.bClient) {
			String string = this.getSpriteTextureName(this.legsSprite);
			boolean boolean1 = string.startsWith("Kate");
			SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor(SurvivorFactory.SurvivorType.Aggressive, boolean1);
			String string2;
			if (!string.startsWith("Male_") && !string.startsWith("Kate_")) {
				String string3 = string.substring(0, string.indexOf("_"));
				string2 = string3.replace("BobZ", "").replace("KateZ", "");
				survivorDesc.setTorsoNumber(string2.isEmpty() ? 1 : Integer.parseInt(string2));
				survivorDesc.setTorso(string3);
			} else {
				String[] stringArray = string.split("_");
				if (isInteger(stringArray[1]) && isInteger(stringArray[2])) {
					int int2 = Integer.parseInt(stringArray[1]);
					survivorDesc.setTorsoNumber(int2 - 1);
					survivorDesc.setTorso(stringArray[0] + "_" + stringArray[1]);
				} else {
					survivorDesc.setTorsoNumber(0);
					survivorDesc.setTorso(stringArray[0]);
				}
			}

			if (string.startsWith("Male_")) {
				if (survivorDesc.getTorsoNumber() < 4) {
					survivorDesc.skinColor.r = Rand.Next(0.9F, 1.0F);
					survivorDesc.skinColor.g = Rand.Next(0.75F, 0.88F);
					survivorDesc.skinColor.b = Rand.Next(0.45F, 0.58F);
				} else {
					survivorDesc.skinColor.r = Rand.Next(0.5F, 0.6F);
					survivorDesc.skinColor.g = Rand.Next(0.3F, 0.4F);
					survivorDesc.skinColor.b = Rand.Next(0.15F, 0.23F);
				}
			} else if (string.startsWith("Kate_")) {
				if (survivorDesc.getTorsoNumber() == 0) {
					survivorDesc.skinColor.r = Rand.Next(0.9F, 1.0F);
					survivorDesc.skinColor.g = Rand.Next(0.75F, 0.88F);
					survivorDesc.skinColor.b = Rand.Next(0.45F, 0.58F);
				} else {
					survivorDesc.skinColor.r = Rand.Next(0.5F, 0.6F);
					survivorDesc.skinColor.g = Rand.Next(0.3F, 0.4F);
					survivorDesc.skinColor.b = Rand.Next(0.15F, 0.23F);
				}
			}

			survivorDesc.hair = "none";
			survivorDesc.hairNoColor = "none";
			survivorDesc.beard = "none";
			survivorDesc.beardNoColor = null;
			survivorDesc.extra.clear();
			for (int int3 = 0; int3 < this.extraSprites.size(); ++int3) {
				string2 = this.getSpriteTextureName((IsoSprite)this.extraSprites.get(int3));
				if (string2.startsWith("F_Hair_White_")) {
					survivorDesc.setHairNoColor("F_Hair_");
					survivorDesc.setHair(survivorDesc.getHairNoColor() + "White");
				} else {
					String[] stringArray2;
					if (string2.startsWith("F_Hair_")) {
						stringArray2 = string2.split("_");
						survivorDesc.setHairNoColor("F_Hair_" + stringArray2[2] + "_");
						survivorDesc.setHair(survivorDesc.getHairNoColor() + "White");
					} else if (string2.startsWith("Hair_")) {
						stringArray2 = string2.split("_");
						survivorDesc.setHairNoColor(stringArray2[0] + "_" + stringArray2[1] + "_");
						survivorDesc.setHair(survivorDesc.getHairNoColor() + "White");
					} else if (string2.startsWith("Beard_")) {
						stringArray2 = string2.split("_");
						survivorDesc.beardNoColor = "Beard_" + stringArray2[1] + "_";
						survivorDesc.beard = survivorDesc.getBeardNoColor() + "White";
						survivorDesc.extra.add(survivorDesc.beard);
					}
				}
			}

			if (this.ClothingItem_Torso != null) {
				survivorDesc.setTop(this.ClothingItem_Torso.getType());
				survivorDesc.setToppal(this.ClothingItem_Torso.getType() + "_White");
				survivorDesc.topColor.set(this.ClothingItem_Torso.col);
			}

			if (this.ClothingItem_Legs != null) {
				survivorDesc.setBottoms(this.ClothingItem_Legs.getType());
				survivorDesc.setBottomspal(this.ClothingItem_Legs.getType() + "_White");
				survivorDesc.trouserColor.set(this.ClothingItem_Legs.col);
			}

			return survivorDesc;
		} else {
			SharedDescriptors.Descriptor descriptor = SharedDescriptors.getDescriptor(this.DescriptorID);
			if (descriptor == null) {
				descriptor = SharedDescriptors.pickRandomDescriptor();
			}

			return new SurvivorDesc(descriptor.desc);
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

		SurvivorDesc survivorDesc = this.createSurvivorDesc();
		survivorDesc.setID(0);
		int int1 = !survivorDesc.getTorso().startsWith("BobZ") && !survivorDesc.getTorso().startsWith("KateZ") ? 1 : survivorDesc.getTorsoNumber();
		IsoZombie zombie = new IsoZombie(IsoWorld.instance.CurrentCell, survivorDesc, int1);
		if (this.desc == null && !GameClient.bClient && (!GameServer.bServer || ServerGUI.isCreated())) {
			zombie.legsSprite.TintMod.r = this.legsSprite.TintMod.r;
			zombie.legsSprite.TintMod.g = this.legsSprite.TintMod.g;
			zombie.legsSprite.TintMod.b = this.legsSprite.TintMod.b;
			for (int int2 = 0; int2 < this.extraSprites.size(); ++int2) {
				IsoSprite sprite = (IsoSprite)this.extraSprites.get(int2);
				String string = this.getSpriteTextureName(sprite);
				if (string != null) {
					if (!string.startsWith("Hair_") && !string.startsWith("F_Hair_")) {
						if (string.startsWith("Beard_")) {
							IsoSprite sprite2 = zombie.getExtraSprites().isEmpty() ? null : (IsoSprite)zombie.getExtraSprites().get(0);
							String string2 = this.getSpriteTextureName(sprite2);
							if (string2 != null && string2.startsWith("Beard_")) {
								sprite2.TintMod.r = sprite.TintMod.r;
								sprite2.TintMod.g = sprite.TintMod.g;
								sprite2.TintMod.b = sprite.TintMod.b;
							} else {
								DebugLog.log("ERROR: unhandled extraSprite in IsoDeadBody.reanimate()");
							}
						} else {
							DebugLog.log("ERROR: unhandled extraSprite in IsoDeadBody.reanimate()");
						}
					} else if (zombie.hairSprite != null) {
						zombie.hairSprite.TintMod.r = sprite.TintMod.r;
						zombie.hairSprite.TintMod.g = sprite.TintMod.g;
						zombie.hairSprite.TintMod.b = sprite.TintMod.b;
					} else {
						DebugLog.log("ERROR: unhandled hairSprite in IsoDeadBody.reanimate()");
					}
				}
			}
		}

		if (this.container == null) {
			this.container = new ItemContainer();
		}

		zombie.setInventory(this.container);
		this.container = new ItemContainer();
		zombie.setClothingItem_Torso(this.ClothingItem_Torso);
		zombie.setClothingItem_Legs(this.ClothingItem_Legs);
		zombie.setClothingItem_Feet(this.ClothingItem_Feet);
		zombie.setX(this.getX());
		zombie.setY(this.getY());
		zombie.setZ(this.getZ());
		zombie.setCurrent(this.getCurrentSquare());
		zombie.setDir(this.dir);
		zombie.setAlpha(1.0F);
		zombie.setTargetAlpha(1.0F);
		zombie.setOnFloor(false);
		zombie.bCrawling = false;
		zombie.walkVariant = "ZombieWalk";
		zombie.DoZombieStats();
		if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
			zombie.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
			zombie.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
		}

		if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
			zombie.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
		}

		zombie.setReanimatedPlayer(true);
		if (GameServer.bServer) {
			zombie.OnlineID = short1;
			ServerMap.instance.ZombieMap.put(zombie.OnlineID, zombie);
		}

		SharedDescriptors.createPlayerZombieDescriptor(zombie);
		if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie)) {
			IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
		}

		if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie) && !IsoWorld.instance.CurrentCell.getAddList().contains(zombie)) {
			IsoWorld.instance.CurrentCell.getAddList().add(zombie);
		}

		if (GameServer.bServer) {
			GameServer.sendZombie(zombie);
			GameServer.removeCorpseFromMap(this);
		}

		this.removeFromWorld();
		this.removeFromSquare();
		LuaEventManager.triggerEvent("OnContainerUpdate");
		zombie.changeState(ReanimatePlayerState.instance());
		if (this.player != null) {
			if (GameServer.bServer) {
				GameServer.sendReanimatedZombieID(this.player, zombie);
			} else if (!GameClient.bClient && this.player.isLocalPlayer()) {
				this.player.ReanimatedCorpse = zombie;
			}

			this.player.setLeaveBodyTimedown(3601.0F);
		}
	}

	public static void Reset() {
		AllBodies.clear();
	}

	public void Collision(Vector2 vector2, IsoObject object) {
		if (object instanceof BaseVehicle) {
			BaseVehicle baseVehicle = (BaseVehicle)object;
			float float1 = 15.0F;
			Vector3f vector3f = baseVehicle.getLinearVelocity(baseVehicle.tempVector3f_1);
			vector3f.y = 0.0F;
			Vector3f vector3f2 = baseVehicle.tempVector3f_2.set(baseVehicle.x - this.x, 0.0F, baseVehicle.z - this.z);
			vector3f2.normalize();
			vector3f.mul((Vector3fc)vector3f2);
			float float2 = vector3f.length();
			float2 = Math.min(float2, float1);
			if (float2 < 0.05F) {
				return;
			}

			if (Math.abs(baseVehicle.getCurrentSpeedKmHour()) > 20.0F) {
				baseVehicle.doChrHitImpulse(this);
			}
		}
	}
}
