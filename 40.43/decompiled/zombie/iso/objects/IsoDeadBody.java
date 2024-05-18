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
   private static final ThreadLocal tempZombie = new ThreadLocal() {
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

   public IsoDeadBody(IsoGameCharacter var1) {
      this(var1, false);
   }

   public IsoDeadBody(IsoGameCharacter var1, boolean var2) {
      super(var1.getCell(), false);
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
      IsoGridSquare var3 = var1.getCurrentSquare();
      if (var3 != null) {
         if (GameClient.bClient && !var2 && var1 instanceof IsoZombie && !GameClient.instance.RecentlyDied.contains(((IsoZombie)var1).OnlineID)) {
            GameClient.instance.RecentlyDied.add(((IsoZombie)var1).OnlineID);
         }

         this.square = var3;
         this.current = var3;
         this.bUseParts = true;
         if (var1 instanceof IsoPlayer) {
            ((IsoPlayer)var1).removeSaveFile();
         }

         var3.getStaticMovingObjects().add(this);
         if (var1 instanceof IsoSurvivor) {
            IsoWorld var10000 = IsoWorld.instance;
            var10000.TotalSurvivorNights += ((IsoSurvivor)((IsoSurvivor)var1)).nightsSurvived;
            ++IsoWorld.instance.TotalSurvivorsDead;
            if (IsoWorld.instance.SurvivorSurvivalRecord < ((IsoSurvivor)((IsoSurvivor)var1)).nightsSurvived) {
               IsoWorld.instance.SurvivorSurvivalRecord = ((IsoSurvivor)((IsoSurvivor)var1)).nightsSurvived;
            }
         }

         this.wasZombie = var1 instanceof IsoZombie;
         if (this.wasZombie && ((IsoZombie)var1).bCrawling) {
            if (!"ZombieCrawl".equals(var1.legsSprite.CurrentAnim.name)) {
               var1.PlayAnim("ZombieCrawl");
               var1.def.Frame = 4.0F;
            }
         } else if (!"ZombieDeath".equals(var1.legsSprite.CurrentAnim.name)) {
            var1.PlayAnim("ZombieDeath");
            var1.def.Frame = (float)(var1.sprite.CurrentAnim.Frames.size() - 1);
         }

         if (!var2 && !var1.getScriptName().equals("none")) {
            ScriptManager.instance.Trigger("OnCharacterDeath", var1.getScriptName());
         }

         this.dir = var1.dir;
         this.Collidable = false;
         int var12;
         if (!GameServer.bServer || ServerGUI.isCreated()) {
            int var4 = (int)var1.def.Frame;
            if (var4 > var1.legsSprite.CurrentAnim.Frames.size() - 1) {
               var4 = var1.legsSprite.CurrentAnim.Frames.size() - 1;
            }

            if (var1.getTorsoSprite() != null) {
               this.torsoSprite = null;
               this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var1.torsoSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir).getName(), var1.torsoSprite.TintMod.toColor());
               this.torsoSprite.Animate = false;
            }

            this.legsSprite = null;
            this.legsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var1.legsSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir).getName(), var1.legsSprite.TintMod.toColor());
            this.legsSprite.TintMod.r = var1.legsSprite.TintMod.r;
            this.legsSprite.TintMod.g = var1.legsSprite.TintMod.g;
            this.legsSprite.TintMod.b = var1.legsSprite.TintMod.b;
            float var5 = this.modelLightAdjust(var1);
            ColorInfo var17 = this.legsSprite.TintMod;
            var17.r *= var5;
            var17 = this.legsSprite.TintMod;
            var17.g *= var5;
            var17 = this.legsSprite.TintMod;
            var17.b *= var5;
            this.sprite = this.legsSprite;
            this.legsSprite.Animate = false;
            if (var1.getHeadSprite() != null) {
               this.headSprite = null;
               this.headSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var1.headSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir).getName(), var1.headSprite.TintMod.toColor());
               this.headSprite.Animate = false;
            }

            float var6;
            Texture var9;
            if (var1.getTopSprite() != null && var1.topSprite.CurrentAnim != null && var1.topSprite.CurrentAnim.Frames.size() > var4) {
               this.topSprite = null;
               var9 = ((IsoDirectionFrame)var1.topSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir);
               if (var9 != null && var9.getName() != null) {
                  this.topSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(var9.getName(), var1.topSprite.TintMod.toColor());
                  this.topSprite.TintMod.r = var1.getTopSprite().TintMod.r;
                  this.topSprite.TintMod.g = var1.getTopSprite().TintMod.g;
                  this.topSprite.TintMod.b = var1.getTopSprite().TintMod.b;
                  var6 = this.modelLightAdjust(var1);
                  var17 = this.topSprite.TintMod;
                  var17.r *= var6;
                  var17 = this.topSprite.TintMod;
                  var17.g *= var6;
                  var17 = this.topSprite.TintMod;
                  var17.b *= var6;
                  this.topSprite.Animate = false;
               }
            }

            if (var1.getBottomsSprite() != null && var1.bottomsSprite.CurrentAnim != null && var1.bottomsSprite.CurrentAnim.Frames.size() > var4) {
               this.bottomsSprite = null;
               var9 = ((IsoDirectionFrame)var1.bottomsSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir);
               if (var9 != null && var9.getName() != null) {
                  this.bottomsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(var9.getName(), var1.bottomsSprite.TintMod.toColor());
                  this.bottomsSprite.TintMod.r = var1.getBottomsSprite().TintMod.r;
                  this.bottomsSprite.TintMod.g = var1.getBottomsSprite().TintMod.g;
                  this.bottomsSprite.TintMod.b = var1.getBottomsSprite().TintMod.b;
                  var6 = this.modelLightAdjust(var1);
                  var17 = this.bottomsSprite.TintMod;
                  var17.r *= var6;
                  var17 = this.bottomsSprite.TintMod;
                  var17.g *= var6;
                  var17 = this.bottomsSprite.TintMod;
                  var17.b *= var6;
                  this.bottomsSprite.Animate = false;
               }
            }

            this.sprite = this.legsSprite;
            if (var1.getHairSprite() != null && var1.hairSprite.CurrentAnim != null && var1.hairSprite.CurrentAnim.Frames.size() > var4) {
               var9 = null;
               Texture var11 = ((IsoDirectionFrame)var1.hairSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir);
               if (var11 != null && var11.getName() != null) {
                  IsoSprite var10 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var1.hairSprite.CurrentAnim.Frames.get(var4)).getTexture(this.dir).getName(), var1.hairSprite.TintMod.toColor());
                  var10.TintMod.r = var1.getHairSprite().TintMod.r;
                  var10.TintMod.g = var1.getHairSprite().TintMod.g;
                  var10.TintMod.b = var1.getHairSprite().TintMod.b;
                  float var7 = this.modelLightAdjust(var1);
                  var17 = var10.TintMod;
                  var17.r *= var7;
                  var17 = var10.TintMod;
                  var17.g *= var7;
                  var17 = var10.TintMod;
                  var17.b *= var7;
                  var10.Animate = false;
                  this.extraSprites.add(var10);
               }
            }

            for(var12 = 0; var12 < var1.getExtraSprites().size(); ++var12) {
               IsoSprite var13 = (IsoSprite)var1.getExtraSprites().get(var12);
               IsoSprite var15 = null;
               if (var13.CurrentAnim.Frames.size() > var4 && var13 != null && var13.CurrentAnim != null && ((IsoDirectionFrame)var13.CurrentAnim.Frames.get(var4)).getTexture(this.dir) != null) {
                  var15 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var13.CurrentAnim.Frames.get(var4)).getTexture(this.dir).getName(), var13.TintMod.toColor());
                  var15.TintMod.r = var13.TintMod.r;
                  var15.TintMod.g = var13.TintMod.g;
                  var15.TintMod.b = var13.TintMod.b;
                  if (var1.legsSprite.modelSlot != null) {
                     var17 = var15.TintMod;
                     var17.r *= 0.5F;
                     var17 = var15.TintMod;
                     var17.g *= 0.5F;
                     var17 = var15.TintMod;
                     var17.b *= 0.5F;
                  }

                  var15.Animate = false;
                  this.extraSprites.add(var15);
               }
            }
         }

         this.sprite = this.legsSprite;
         this.def = IsoSpriteInstance.get(this.sprite);
         this.def.Frame = 0.0F;
         this.def.Flip = false;
         this.x = var1.getX();
         this.y = var1.getY();
         this.z = var1.getZ();
         this.nx = this.x;
         this.ny = this.y;
         this.offsetX = var1.offsetX;
         this.offsetY = var1.offsetY;
         this.solid = false;
         this.shootable = false;
         this.OutlineOnMouseover = true;
         this.container = var1.getInventory();
         var1.setInventory(new ItemContainer());
         this.ClothingItem_Torso = var1.getClothingItem_Torso();
         this.ClothingItem_Legs = var1.getClothingItem_Legs();
         this.ClothingItem_Feet = var1.getClothingItem_Feet();
         this.container.setExplored(var1 instanceof IsoPlayer || var1 instanceof IsoZombie && ((IsoZombie)var1).isReanimatedPlayer());
         this.container.parent = this;
         if (var1.bFemale) {
            this.container.type = "inventoryfemale";
         } else {
            this.container.type = "inventorymale";
         }

         this.container.Capacity = 8;
         boolean var8 = var1.isOnFire();
         if (var1 instanceof IsoZombie) {
            this.DescriptorID = ((IsoZombie)var1).getDescriptor().getID();
            if (!var2) {
               if (GameServer.bServer) {
                  this.square.revisionUp();
                  GameServer.sendDeleteZombie((IsoZombie)var1);
               } else {
                  for(var12 = 0; var12 < IsoPlayer.numPlayers; ++var12) {
                     IsoPlayer var14 = IsoPlayer.players[var12];
                     if (var14 != null && var14.ReanimatedCorpse == var1) {
                        var14.ReanimatedCorpse = null;
                        var14.ReanimatedCorpseID = -1;
                     }
                  }

                  if (!GameClient.bClient && var1.emitter != null) {
                     var1.emitter.tick();
                  }
               }
            }
         } else {
            if (var1 instanceof IsoSurvivor) {
               this.getCell().getSurvivorList().remove((IsoSurvivor)var1);
            }

            this.desc = new SurvivorDesc(var1.getDescriptor());
            if (var1 instanceof IsoPlayer) {
               if (GameServer.bServer) {
                  this.player = (IsoPlayer)var1;
               } else if (!GameClient.bClient && ((IsoPlayer)var1).isLocalPlayer()) {
                  this.player = (IsoPlayer)var1;
               }
            }
         }

         var1.removeFromWorld();
         var1.removeFromSquare();
         this.sayLine = var1.getSayLine();
         this.SpeakColor = var1.getSpeakColour();
         this.SpeakTime = var1.getSpeakTime();
         this.Speaking = var1.isSpeaking();
         if (var8) {
            if (!GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
               IsoFireManager.StartFire(this.getCell(), this.getSquare(), true, 100, 500);
            }

            this.container.setExplored(true);
         }

         if (!var2 && !GameServer.bServer) {
            LuaEventManager.triggerEvent("OnContainerUpdate", this);
         }

         if (var1 instanceof IsoPlayer) {
            ((IsoPlayer)var1).bDeathFinished = true;
         }

         this.deathTime = (float)GameTime.getInstance().getWorldAgeHours();
         this.realWorldDeathTime = System.currentTimeMillis();
         if (!GameClient.bClient) {
            var12 = 0;

            for(int var16 = 0; var16 < AllBodies.size() && !(((IsoDeadBody)AllBodies.get(var16)).deathTime >= this.deathTime); ++var16) {
               ++var12;
            }

            AllBodies.add(var12, this);
         }

         if (!GameServer.bServer) {
            FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
         }

         var1 = null;
      }
   }

   public IsoDeadBody(IsoCell var1) {
      super(var1, false);
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
      InventoryItem var1 = InventoryItemFactory.CreateItem("Base.CorpseMale");
      var1.storeInByteData(this);
      return var1;
   }

   private void initSpriteParts() {
      SharedDescriptors.Descriptor var1 = SharedDescriptors.getDescriptor(this.DescriptorID);
      SurvivorDesc var2 = var1 == null ? this.desc : var1.desc;
      int var3 = var1 == null ? 1 : var1.palette;
      IsoZombie var4 = (IsoZombie)tempZombie.get();
      if (var2 != null) {
         var4.setDescriptor(var2);
         if (var2.isFemale()) {
            var4.SpriteName = "KateZ";
         } else {
            var4.SpriteName = "BobZ";
         }

         var4.palette = var3;
         var4.bFemale = var2.isFemale();
         var4.InitSpritePartsZombie();
      }

      var4.PlayAnim("ZombieDeath");
      var4.def.Frame = (float)(var4.sprite.CurrentAnim.Frames.size() - 1);
      int var5 = (int)var4.def.Frame;
      if (var4.getTorsoSprite() != null) {
         this.torsoSprite = null;
         this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var4.torsoSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir).getName(), var4.torsoSprite.TintMod.toColor());
         this.torsoSprite.Animate = false;
      }

      this.legsSprite = null;
      this.legsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var4.legsSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir).getName(), var4.legsSprite.TintMod.toColor());
      this.legsSprite.TintMod.r = var4.legsSprite.TintMod.r;
      this.legsSprite.TintMod.g = var4.legsSprite.TintMod.g;
      this.legsSprite.TintMod.b = var4.legsSprite.TintMod.b;
      float var6 = this.modelLightAdjust(var4);
      ColorInfo var10000 = this.legsSprite.TintMod;
      var10000.r *= var6;
      var10000 = this.legsSprite.TintMod;
      var10000.g *= var6;
      var10000 = this.legsSprite.TintMod;
      var10000.b *= var6;
      this.sprite = this.legsSprite;
      this.legsSprite.Animate = false;
      if (var4.getHeadSprite() != null) {
         this.headSprite = null;
         this.headSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var4.headSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir).getName(), var4.headSprite.TintMod.toColor());
         this.headSprite.Animate = false;
      }

      float var7;
      Texture var9;
      if (var4.getTopSprite() != null && var4.topSprite.CurrentAnim != null && var4.topSprite.CurrentAnim.Frames.size() > var5) {
         this.topSprite = null;
         var9 = ((IsoDirectionFrame)var4.topSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir);
         if (var9 != null && var9.getName() != null) {
            this.topSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(var9.getName(), var4.topSprite.TintMod.toColor());
            this.topSprite.TintMod.r = var4.getTopSprite().TintMod.r;
            this.topSprite.TintMod.g = var4.getTopSprite().TintMod.g;
            this.topSprite.TintMod.b = var4.getTopSprite().TintMod.b;
            var7 = this.modelLightAdjust(var4);
            var10000 = this.topSprite.TintMod;
            var10000.r *= var7;
            var10000 = this.topSprite.TintMod;
            var10000.g *= var7;
            var10000 = this.topSprite.TintMod;
            var10000.b *= var7;
            this.topSprite.Animate = false;
         }
      }

      if (var4.getBottomsSprite() != null && var4.bottomsSprite.CurrentAnim != null && var4.bottomsSprite.CurrentAnim.Frames.size() > var5) {
         this.bottomsSprite = null;
         var9 = ((IsoDirectionFrame)var4.bottomsSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir);
         if (var9 != null && var9.getName() != null) {
            this.bottomsSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(var9.getName(), var4.bottomsSprite.TintMod.toColor());
            this.bottomsSprite.TintMod.r = var4.getBottomsSprite().TintMod.r;
            this.bottomsSprite.TintMod.g = var4.getBottomsSprite().TintMod.g;
            this.bottomsSprite.TintMod.b = var4.getBottomsSprite().TintMod.b;
            var7 = this.modelLightAdjust(var4);
            var10000 = this.bottomsSprite.TintMod;
            var10000.r *= var7;
            var10000 = this.bottomsSprite.TintMod;
            var10000.g *= var7;
            var10000 = this.bottomsSprite.TintMod;
            var10000.b *= var7;
            this.bottomsSprite.Animate = false;
         }
      }

      if (var4.getHairSprite() != null && var4.hairSprite.CurrentAnim != null && var4.hairSprite.CurrentAnim.Frames.size() > var5) {
         var9 = null;
         Texture var11 = ((IsoDirectionFrame)var4.hairSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir);
         if (var11 != null && var11.getName() != null) {
            IsoSprite var10 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var4.hairSprite.CurrentAnim.Frames.get(var5)).getTexture(this.dir).getName(), var4.hairSprite.TintMod.toColor());
            var10.TintMod.r = var4.getHairSprite().TintMod.r;
            var10.TintMod.g = var4.getHairSprite().TintMod.g;
            var10.TintMod.b = var4.getHairSprite().TintMod.b;
            float var8 = this.modelLightAdjust(var4);
            var10000 = var10.TintMod;
            var10000.r *= var8;
            var10000 = var10.TintMod;
            var10000.g *= var8;
            var10000 = var10.TintMod;
            var10000.b *= var8;
            var10.Animate = false;
            this.extraSprites.add(var10);
         }
      }

      for(int var12 = 0; var12 < var4.getExtraSprites().size(); ++var12) {
         IsoSprite var13 = (IsoSprite)var4.getExtraSprites().get(var12);
         IsoSprite var14 = null;
         if (var13.CurrentAnim.Frames.size() > var5 && var13 != null && var13.CurrentAnim != null && ((IsoDirectionFrame)var13.CurrentAnim.Frames.get(var5)).getTexture(this.dir) != null) {
            var14 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(((IsoDirectionFrame)var13.CurrentAnim.Frames.get(var5)).getTexture(this.dir).getName(), var13.TintMod.toColor());
            var14.TintMod.r = var13.TintMod.r;
            var14.TintMod.g = var13.TintMod.g;
            var14.TintMod.b = var13.TintMod.b;
            if (var4.legsSprite.modelSlot != null) {
               var10000 = var14.TintMod;
               var10000.r *= 0.5F;
               var10000 = var14.TintMod;
               var10000.g *= 0.5F;
               var10000 = var14.TintMod;
               var10000.b *= 0.5F;
            }

            var14.Animate = false;
            this.extraSprites.add(var14);
         }
      }

      this.def = IsoSpriteInstance.get(this.sprite);
      this.def.Frame = 0.0F;
      this.def.Flip = false;
   }

   private IsoSprite loadSprite(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      Color var3 = this.tempColor;
      var3.r = var1.getFloat();
      var3.g = var1.getFloat();
      var3.b = var1.getFloat();
      var3.a = var1.getFloat();
      IsoSprite var4 = IsoWorld.instance.spriteManager.getOrAddSpriteCache(var2, var3);
      var4.TintMod.r = var3.r;
      var4.TintMod.g = var3.g;
      var4.TintMod.b = var3.b;
      var4.TintMod.a = var3.a;
      var4.Animate = false;
      return var4;
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.wasZombie = var1.get() == 1;
      boolean var3 = var1.get() == 1;
      int var4;
      if (var3) {
         this.sprite = null;
         if (var2 >= 27) {
            this.DescriptorID = var1.getShort();
         }
      } else {
         this.legsSprite = this.loadSprite(var1);
         this.sprite = this.legsSprite;
         if (var1.get() == 1) {
            this.torsoSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(GameWindow.ReadString(var1));
         }

         if (var1.get() == 1) {
            this.headSprite = this.loadSprite(var1);
         }

         if (var1.get() == 1) {
            this.bottomsSprite = this.loadSprite(var1);
         }

         if (var1.get() == 1) {
            this.shoeSprite = IsoWorld.instance.spriteManager.getOrAddSpriteCache(GameWindow.ReadString(var1));
         }

         if (var1.get() == 1) {
            this.topSprite = this.loadSprite(var1);
         }

         var4 = var1.getInt();

         for(int var5 = 0; var5 < var4; ++var5) {
            IsoSprite var6 = this.loadSprite(var1);
            this.extraSprites.add(var6);
         }
      }

      if (var2 >= 57 && var1.get() == 1) {
         this.desc = new SurvivorDesc(true);
         this.desc.load(var1, var2, (IsoGameCharacter)null);
      }

      if (var1.get() == 1) {
         var4 = var1.getInt();

         try {
            this.container = new ItemContainer();
            this.container.ID = var4;
            this.container.parent = this;
            this.container.Capacity = 8;
            this.container.SourceGrid = this.square;
            ArrayList var9 = this.container.load(var1, var2, false);
            if (var2 >= 32) {
               short var10 = var1.getShort();
               if (var10 >= 0 && var10 < var9.size()) {
                  this.ClothingItem_Torso = (InventoryItem)var9.get(var10);
               }

               var10 = var1.getShort();
               if (var10 >= 0 && var10 < var9.size()) {
                  this.ClothingItem_Legs = (InventoryItem)var9.get(var10);
               }

               var10 = var1.getShort();
               if (var10 >= 0 && var10 < var9.size()) {
                  this.ClothingItem_Feet = (InventoryItem)var9.get(var10);
               }
            }
         } catch (Exception var8) {
            if (this.container != null) {
               DebugLog.log("Failed to stream in container ID: " + this.container.ID);
            }
         }
      }

      if (var2 >= 57) {
         this.deathTime = var1.getFloat();
      }

      if (var2 >= 65) {
         this.reanimateTime = var1.getFloat();
      }

      if (var3 && (GameClient.bClient || GameServer.bServer && ServerGUI.isCreated())) {
         this.initSpriteParts();
         this.checkClothing();
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.wasZombie ? 1 : 0));
      if (!GameServer.bServer && !GameClient.bClient) {
         var1.put((byte)0);

         try {
            this.legsSprite.name = ((IsoDirectionFrame)this.legsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         GameWindow.WriteString(var1, this.legsSprite.name);
         var1.putFloat(this.legsSprite.TintMod.r);
         var1.putFloat(this.legsSprite.TintMod.g);
         var1.putFloat(this.legsSprite.TintMod.b);
         var1.putFloat(this.legsSprite.TintMod.a);
         if (this.torsoSprite != null && ((IsoDirectionFrame)this.torsoSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
            this.torsoSprite.name = ((IsoDirectionFrame)this.torsoSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.torsoSprite.name);
         } else {
            var1.put((byte)0);
         }

         if (this.headSprite != null && ((IsoDirectionFrame)this.headSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
            this.headSprite.name = ((IsoDirectionFrame)this.headSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.headSprite.name);
            var1.putFloat(this.headSprite.TintMod.r);
            var1.putFloat(this.headSprite.TintMod.g);
            var1.putFloat(this.headSprite.TintMod.b);
            var1.putFloat(this.headSprite.TintMod.a);
         } else {
            var1.put((byte)0);
         }

         if (this.bottomsSprite != null && ((IsoDirectionFrame)this.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
            this.bottomsSprite.name = ((IsoDirectionFrame)this.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.bottomsSprite.name);
            var1.putFloat(this.bottomsSprite.TintMod.r);
            var1.putFloat(this.bottomsSprite.TintMod.g);
            var1.putFloat(this.bottomsSprite.TintMod.b);
            var1.putFloat(this.bottomsSprite.TintMod.a);
         } else {
            var1.put((byte)0);
         }

         if (this.shoeSprite != null && ((IsoDirectionFrame)this.shoeSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
            this.shoeSprite.name = ((IsoDirectionFrame)this.shoeSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.shoeSprite.name);
         } else {
            var1.put((byte)0);
         }

         if (this.topSprite != null && ((IsoDirectionFrame)this.topSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir) != null) {
            this.topSprite.name = ((IsoDirectionFrame)this.topSprite.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.topSprite.name);
            var1.putFloat(this.topSprite.TintMod.r);
            var1.putFloat(this.topSprite.TintMod.g);
            var1.putFloat(this.topSprite.TintMod.b);
            var1.putFloat(this.topSprite.TintMod.a);
         } else {
            var1.put((byte)0);
         }

         var1.putInt(this.extraSprites.size());

         for(int var2 = 0; var2 < this.extraSprites.size(); ++var2) {
            IsoSprite var3 = (IsoSprite)this.extraSprites.get(var2);
            var3.name = ((IsoDirectionFrame)var3.CurrentAnim.Frames.get(0)).getTexture(this.dir).getName();
            GameWindow.WriteString(var1, var3.name);
            var1.putFloat(var3.TintMod.r);
            var1.putFloat(var3.TintMod.g);
            var1.putFloat(var3.TintMod.b);
            var1.putFloat(var3.TintMod.a);
         }
      } else {
         var1.put((byte)1);
         var1.putShort((short)this.DescriptorID);
      }

      if (this.desc != null) {
         var1.put((byte)1);
         this.desc.save(var1);
      } else {
         var1.put((byte)0);
      }

      if (this.container != null) {
         var1.put((byte)1);
         var1.putInt(this.container.ID);
         ArrayList var5 = this.container.save(var1, false);
         if (this.ClothingItem_Torso != null) {
            var1.putShort((short)var5.indexOf(this.ClothingItem_Torso));
         } else {
            var1.putShort((short)-1);
         }

         if (this.ClothingItem_Legs != null) {
            var1.putShort((short)var5.indexOf(this.ClothingItem_Legs));
         } else {
            var1.putShort((short)-1);
         }

         if (this.ClothingItem_Feet != null) {
            var1.putShort((short)var5.indexOf(this.ClothingItem_Feet));
         } else {
            var1.putShort((short)-1);
         }
      } else {
         var1.put((byte)0);
      }

      var1.putFloat(this.deathTime);
      var1.putFloat(this.reanimateTime);
   }

   public void softReset() {
      this.square.RemoveTileObject(this);
   }

   public void renderlast() {
      if (this.Speaking) {
         float var1 = (float)this.sx;
         float var2 = (float)this.sy;
         var1 = (float)((int)var1);
         var2 = (float)((int)var2);
         var1 -= (float)((int)IsoCamera.getOffX());
         var2 -= (float)((int)IsoCamera.getOffY());
         var1 += 8.0F;
         var2 += 32.0F;
         if (this.sayLine != null) {
            IndieGL.End();
            TextManager.instance.DrawStringCentre(UIFont.Medium, (double)((int)var1), (double)((int)var2), this.sayLine, (double)this.SpeakColor.r, (double)this.SpeakColor.g, (double)this.SpeakColor.b, (double)this.SpeakColor.a);
         }
      }

   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      this.offsetX = 0.0F;
      this.offsetY = 0.0F;
      boolean var6 = this.isHighlighted();
      float var8;
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
            float var12;
            if (this.sx == 0) {
               var12 = IsoUtils.XToScreen(var1, var2, var3, 0);
               var8 = IsoUtils.YToScreen(var1, var2, var3, 0);
               var12 = (float)((int)var12);
               var8 = (float)((int)var8);
               this.sx = (int)var12;
               this.sy = (int)var8;
            }

            var12 = (float)(this.sx + IsoSprite.globalOffsetX - 44 + 8);
            var8 = (float)(this.sy + IsoSprite.globalOffsetY - 119 + 60 + 32);
            if (Core.TileScale == 1) {
               var12 = (float)(this.sx + IsoSprite.globalOffsetX + IsoGameCharacter.RENDER_OFFSET_X - 38);
               var8 = (float)(this.sy + IsoSprite.globalOffsetY + IsoGameCharacter.RENDER_OFFSET_Y + 59);
            }

            byte var13 = 72;
            byte var14 = 64;
            var12 -= (float)((DeadBodyAtlas.ENTRY_WID - var13) / 2);
            var8 -= (float)((DeadBodyAtlas.ENTRY_HGT - var14) / 2);
            if (var6) {
               inf.r = this.getHighlightColor().r;
               inf.g = this.getHighlightColor().g;
               inf.b = this.getHighlightColor().b;
               inf.a = this.getHighlightColor().a;
            } else {
               inf.r = var4.r;
               inf.g = var4.g;
               inf.b = var4.b;
               inf.a = var4.a;
            }

            var4 = inf;
            if (!var6 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
               this.getCurrentSquare().interpolateLight(var4, var1 - (float)this.getCurrentSquare().getX(), var2 - (float)this.getCurrentSquare().getY());
            }

            if (!var6) {
               float var15 = RenderSettings.getInstance().getAmbientForPlayer(IsoCamera.frameState.playerIndex);
               if (this.getSquare() != null && this.getSquare().getRoom() != null) {
                  var15 *= 0.6F;
               }

               ColorInfo var10000 = inf;
               var10000.r *= var15;
               var10000 = inf;
               var10000.g *= var15;
               var10000 = inf;
               var10000.b *= var15;
            }

            this.atlasTex.render((int)var12, (int)var8, this.atlasTex.getWidth(), this.atlasTex.getHeight(), var4.r, var4.g, var4.b, var4.a);
            if (Core.bDebug) {
            }

            this.sx = (int)(IsoUtils.XToScreen(var1, var2, var3, 0) - (float)(IsoGameCharacter.RENDER_OFFSET_X * Core.TileScale));
            this.sy = (int)(IsoUtils.YToScreen(var1, var2, var3, 0) - (float)(IsoGameCharacter.RENDER_OFFSET_Y * Core.TileScale));
            if (IsoObjectPicker.Instance.wasDirty) {
               this.renderObjectPicker(this.getX(), this.getY(), this.getZ(), var4);
            }

            return;
         }
      }

      int var7 = Core.TileScale;
      var8 = (float)(IsoGameCharacter.RENDER_OFFSET_X * var7);
      float var9 = (float)(IsoGameCharacter.RENDER_OFFSET_Y * var7);
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this.sprite);
      }

      this.def.setScale((float)var7, (float)var7);
      this.def.Frame = 0.0F;
      if (var6) {
         inf.r = this.getHighlightColor().r;
         inf.g = this.getHighlightColor().g;
         inf.b = this.getHighlightColor().b;
         inf.a = this.getHighlightColor().a;
      } else {
         inf.r = var4.r;
         inf.g = var4.g;
         inf.b = var4.b;
         inf.a = var4.a;
      }

      var4 = inf;
      if (!var6 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null && (!GameServer.bServer || !ServerGUI.isCreated())) {
         this.getCurrentSquare().interpolateLight(var4, var1 - (float)this.getCurrentSquare().getX(), var2 - (float)this.getCurrentSquare().getY());
      }

      if (!this.bUseParts) {
         this.sprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
      } else {
         if (this.legsSprite != null) {
            this.legsSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         if (this.torsoSprite != null) {
            this.torsoSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         if (this.headSprite != null) {
            this.headSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         if (this.bottomsSprite != null) {
            this.bottomsSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         if (this.shoeSprite != null) {
            this.shoeSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         if (this.topSprite != null) {
            this.topSprite.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }

         for(int var10 = 0; var10 < this.extraSprites.size(); ++var10) {
            IsoSprite var11 = (IsoSprite)this.extraSprites.get(var10);
            var11.render(this.def, this, var1, var2, var3, this.dir, var8, var9, var4);
         }
      }

      if (Core.bDebug) {
      }

   }

   public void renderObjectPicker(float var1, float var2, float var3, ColorInfo var4) {
      int var5 = Core.TileScale;
      float var6 = (float)(IsoGameCharacter.RENDER_OFFSET_X * var5);
      float var7 = (float)(IsoGameCharacter.RENDER_OFFSET_Y * var5);
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this.sprite);
      }

      this.def.setScale((float)var5, (float)var5);
      if (!this.bUseParts) {
         this.sprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, var6, var7, var4);
      } else {
         this.legsSprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, var6, var7, var4);
         if (this.torsoSprite != null) {
            this.torsoSprite.renderObjectPicker(this.def, this, var1, var2, var3, this.dir, var6, var7, var4);
         }
      }

   }

   public void Burn() {
      if (!GameClient.bClient) {
         if (this.getSquare() != null && this.getSquare().getProperties().Is(IsoFlagType.burning)) {
            this.burnTimer += GameTime.instance.getMultipliedSecondsSinceLastUpdate();
            if (this.burnTimer >= 5.0F) {
               boolean var1 = true;

               for(int var2 = 0; var2 < this.getSquare().getObjects().size(); ++var2) {
                  IsoObject var3 = (IsoObject)this.getSquare().getObjects().get(var2);
                  if (var3.getName() != null && "burnedCorpse".equals(var3.getName())) {
                     var1 = false;
                     break;
                  }
               }

               if (var1) {
                  IsoObject var4 = new IsoObject(this.getSquare(), "floors_burnt_01_" + Rand.Next(1, 3), "burnedCorpse");
                  this.getSquare().getObjects().add(var4);
                  var4.transmitCompleteItemToClients();
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

   public void Say(String var1) {
      this.SpeakTime = (float)(var1.length() * 4);
      if (this.SpeakTime < 60.0F) {
         this.SpeakTime = 60.0F;
      }

      this.sayLine = var1;
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

   private float modelLightAdjust(IsoGameCharacter var1) {
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

         float var1 = (float)GameTime.getInstance().getWorldAgeHours();
         if (this.deathTime < 0.0F) {
            this.deathTime = var1;
         }

         if (this.deathTime > var1) {
            this.deathTime = var1;
         }

         int var2 = 0;

         for(int var3 = 0; var3 < AllBodies.size() && !(((IsoDeadBody)AllBodies.get(var3)).deathTime >= this.deathTime); ++var3) {
            ++var2;
         }

         AllBodies.add(var2, this);
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
         float var0 = (float)SandboxOptions.instance.HoursForCorpseRemoval.getValue();
         if (!(var0 <= 0.0F)) {
            float var1 = (float)GameTime.getInstance().getWorldAgeHours();

            for(int var2 = 0; var2 < AllBodies.size(); ++var2) {
               IsoDeadBody var3 = (IsoDeadBody)AllBodies.get(var2);
               if (ServerOptions.instance.RemovePlayerCorpsesOnCorpseRemoval.getValue() || var3.wasZombie) {
                  if (var3.deathTime > var1) {
                     var3.deathTime = var1;
                  }

                  float var4 = var1 - var3.deathTime;
                  if (var4 < var0) {
                     break;
                  }

                  if (GameServer.bServer) {
                     GameServer.removeCorpseFromMap(var3);
                  }

                  var3.removeFromWorld();
                  var3.removeFromSquare();
                  --var2;
               }
            }

         }
      }
   }

   public void setReanimateTime(float var1) {
      this.reanimateTime = var1;
      if (!GameClient.bClient) {
         ArrayList var2 = IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList();
         if (this.reanimateTime > 0.0F && !var2.contains(this)) {
            var2.add(this);
         } else if (this.reanimateTime <= 0.0F && var2.contains(this)) {
            var2.remove(this);
         }

      }
   }

   private float getReanimateDelay() {
      float var1 = 0.0F;
      float var2 = 0.0F;
      switch(SandboxOptions.instance.Lore.Reanimate.getValue()) {
      case 1:
      default:
         break;
      case 2:
         var2 = 0.5F;
         break;
      case 3:
         var2 = 0.016666668F;
         break;
      case 4:
         var2 = 12.0F;
         break;
      case 5:
         var1 = 48.0F;
         var2 = 72.0F;
         break;
      case 6:
         var1 = 168.0F;
         var2 = 336.0F;
      }

      if (Core.bTutorial) {
         var2 = 0.25F;
      }

      return var1 == var2 ? var1 : Rand.Next(var1, var2);
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

            float var1 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.reanimateTime <= var1) {
               this.reanimate();
            }
         }

      }
   }

   private static boolean isInteger(String var0) {
      try {
         Integer.parseInt(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   private String getSpriteTextureName(IsoSprite var1) {
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < var1.AnimStack.size(); ++var2) {
            IsoAnim var3 = (IsoAnim)var1.AnimStack.get(var2);

            for(int var4 = 0; var4 < var3.Frames.size(); ++var4) {
               IsoDirectionFrame var5 = (IsoDirectionFrame)var3.Frames.get(var4);
               IsoDirections[] var6 = IsoDirections.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  IsoDirections var9 = var6[var8];
                  if (var9 != IsoDirections.Max && var5.getTexture(var9) != null) {
                     return var5.getTexture(var9).getName();
                  }
               }
            }
         }

         return null;
      }
   }

   private int getTorsoNumber(SurvivorDesc var1) {
      String var2 = var1.getTorso();
      if (var2 != null && var2.contains("_")) {
         String[] var3 = var2.split("_");
         return var3.length == 2 && isInteger(var3[1]) ? Integer.valueOf(var3[1]) - 1 : 0;
      } else {
         return 0;
      }
   }

   private SurvivorDesc createSurvivorDesc() {
      if (this.desc != null) {
         int var8 = this.getTorsoNumber(this.desc);
         if (this.desc.isFemale()) {
            if (var8 == 0) {
               this.desc.skinColor.r = Rand.Next(0.9F, 1.0F);
               this.desc.skinColor.g = Rand.Next(0.75F, 0.88F);
               this.desc.skinColor.b = Rand.Next(0.45F, 0.58F);
            } else {
               this.desc.skinColor.r = Rand.Next(0.5F, 0.6F);
               this.desc.skinColor.g = Rand.Next(0.3F, 0.4F);
               this.desc.skinColor.b = Rand.Next(0.15F, 0.23F);
            }
         } else if (var8 < 4) {
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
         String var7 = this.getSpriteTextureName(this.legsSprite);
         boolean var2 = var7.startsWith("Kate");
         SurvivorDesc var3 = SurvivorFactory.CreateSurvivor(SurvivorFactory.SurvivorType.Aggressive, var2);
         String var11;
         if (!var7.startsWith("Male_") && !var7.startsWith("Kate_")) {
            String var9 = var7.substring(0, var7.indexOf("_"));
            var11 = var9.replace("BobZ", "").replace("KateZ", "");
            var3.setTorsoNumber(var11.isEmpty() ? 1 : Integer.parseInt(var11));
            var3.setTorso(var9);
         } else {
            String[] var4 = var7.split("_");
            if (isInteger(var4[1]) && isInteger(var4[2])) {
               int var5 = Integer.parseInt(var4[1]);
               var3.setTorsoNumber(var5 - 1);
               var3.setTorso(var4[0] + "_" + var4[1]);
            } else {
               var3.setTorsoNumber(0);
               var3.setTorso(var4[0]);
            }
         }

         if (var7.startsWith("Male_")) {
            if (var3.getTorsoNumber() < 4) {
               var3.skinColor.r = Rand.Next(0.9F, 1.0F);
               var3.skinColor.g = Rand.Next(0.75F, 0.88F);
               var3.skinColor.b = Rand.Next(0.45F, 0.58F);
            } else {
               var3.skinColor.r = Rand.Next(0.5F, 0.6F);
               var3.skinColor.g = Rand.Next(0.3F, 0.4F);
               var3.skinColor.b = Rand.Next(0.15F, 0.23F);
            }
         } else if (var7.startsWith("Kate_")) {
            if (var3.getTorsoNumber() == 0) {
               var3.skinColor.r = Rand.Next(0.9F, 1.0F);
               var3.skinColor.g = Rand.Next(0.75F, 0.88F);
               var3.skinColor.b = Rand.Next(0.45F, 0.58F);
            } else {
               var3.skinColor.r = Rand.Next(0.5F, 0.6F);
               var3.skinColor.g = Rand.Next(0.3F, 0.4F);
               var3.skinColor.b = Rand.Next(0.15F, 0.23F);
            }
         }

         var3.hair = "none";
         var3.hairNoColor = "none";
         var3.beard = "none";
         var3.beardNoColor = null;
         var3.extra.clear();

         for(int var10 = 0; var10 < this.extraSprites.size(); ++var10) {
            var11 = this.getSpriteTextureName((IsoSprite)this.extraSprites.get(var10));
            if (var11.startsWith("F_Hair_White_")) {
               var3.setHairNoColor("F_Hair_");
               var3.setHair(var3.getHairNoColor() + "White");
            } else {
               String[] var6;
               if (var11.startsWith("F_Hair_")) {
                  var6 = var11.split("_");
                  var3.setHairNoColor("F_Hair_" + var6[2] + "_");
                  var3.setHair(var3.getHairNoColor() + "White");
               } else if (var11.startsWith("Hair_")) {
                  var6 = var11.split("_");
                  var3.setHairNoColor(var6[0] + "_" + var6[1] + "_");
                  var3.setHair(var3.getHairNoColor() + "White");
               } else if (var11.startsWith("Beard_")) {
                  var6 = var11.split("_");
                  var3.beardNoColor = "Beard_" + var6[1] + "_";
                  var3.beard = var3.getBeardNoColor() + "White";
                  var3.extra.add(var3.beard);
               }
            }
         }

         if (this.ClothingItem_Torso != null) {
            var3.setTop(this.ClothingItem_Torso.getType());
            var3.setToppal(this.ClothingItem_Torso.getType() + "_White");
            var3.topColor.set(this.ClothingItem_Torso.col);
         }

         if (this.ClothingItem_Legs != null) {
            var3.setBottoms(this.ClothingItem_Legs.getType());
            var3.setBottomspal(this.ClothingItem_Legs.getType() + "_White");
            var3.trouserColor.set(this.ClothingItem_Legs.col);
         }

         return var3;
      } else {
         SharedDescriptors.Descriptor var1 = SharedDescriptors.getDescriptor(this.DescriptorID);
         if (var1 == null) {
            var1 = SharedDescriptors.pickRandomDescriptor();
         }

         return new SurvivorDesc(var1.desc);
      }
   }

   public void reanimate() {
      short var1 = -1;
      if (GameServer.bServer) {
         var1 = ServerMap.instance.getUniqueZombieId();
         if (var1 == -1) {
            return;
         }
      }

      SurvivorDesc var2 = this.createSurvivorDesc();
      var2.setID(0);
      int var3 = !var2.getTorso().startsWith("BobZ") && !var2.getTorso().startsWith("KateZ") ? 1 : var2.getTorsoNumber();
      IsoZombie var4 = new IsoZombie(IsoWorld.instance.CurrentCell, var2, var3);
      if (this.desc == null && !GameClient.bClient && (!GameServer.bServer || ServerGUI.isCreated())) {
         var4.legsSprite.TintMod.r = this.legsSprite.TintMod.r;
         var4.legsSprite.TintMod.g = this.legsSprite.TintMod.g;
         var4.legsSprite.TintMod.b = this.legsSprite.TintMod.b;

         for(int var5 = 0; var5 < this.extraSprites.size(); ++var5) {
            IsoSprite var6 = (IsoSprite)this.extraSprites.get(var5);
            String var7 = this.getSpriteTextureName(var6);
            if (var7 != null) {
               if (!var7.startsWith("Hair_") && !var7.startsWith("F_Hair_")) {
                  if (var7.startsWith("Beard_")) {
                     IsoSprite var8 = var4.getExtraSprites().isEmpty() ? null : (IsoSprite)var4.getExtraSprites().get(0);
                     String var9 = this.getSpriteTextureName(var8);
                     if (var9 != null && var9.startsWith("Beard_")) {
                        var8.TintMod.r = var6.TintMod.r;
                        var8.TintMod.g = var6.TintMod.g;
                        var8.TintMod.b = var6.TintMod.b;
                     } else {
                        DebugLog.log("ERROR: unhandled extraSprite in IsoDeadBody.reanimate()");
                     }
                  } else {
                     DebugLog.log("ERROR: unhandled extraSprite in IsoDeadBody.reanimate()");
                  }
               } else if (var4.hairSprite != null) {
                  var4.hairSprite.TintMod.r = var6.TintMod.r;
                  var4.hairSprite.TintMod.g = var6.TintMod.g;
                  var4.hairSprite.TintMod.b = var6.TintMod.b;
               } else {
                  DebugLog.log("ERROR: unhandled hairSprite in IsoDeadBody.reanimate()");
               }
            }
         }
      }

      if (this.container == null) {
         this.container = new ItemContainer();
      }

      var4.setInventory(this.container);
      this.container = new ItemContainer();
      var4.setClothingItem_Torso(this.ClothingItem_Torso);
      var4.setClothingItem_Legs(this.ClothingItem_Legs);
      var4.setClothingItem_Feet(this.ClothingItem_Feet);
      var4.setX(this.getX());
      var4.setY(this.getY());
      var4.setZ(this.getZ());
      var4.setCurrent(this.getCurrentSquare());
      var4.setDir(this.dir);
      var4.setAlpha(1.0F);
      var4.setTargetAlpha(1.0F);
      var4.setOnFloor(false);
      var4.bCrawling = false;
      var4.walkVariant = "ZombieWalk";
      var4.DoZombieStats();
      if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
         var4.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
      }

      if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
         var4.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
      }

      if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
         var4.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
      }

      var4.setReanimatedPlayer(true);
      if (GameServer.bServer) {
         var4.OnlineID = var1;
         ServerMap.instance.ZombieMap.put(var4.OnlineID, var4);
      }

      SharedDescriptors.createPlayerZombieDescriptor(var4);
      if (!IsoWorld.instance.CurrentCell.getZombieList().contains(var4)) {
         IsoWorld.instance.CurrentCell.getZombieList().add(var4);
      }

      if (!IsoWorld.instance.CurrentCell.getObjectList().contains(var4) && !IsoWorld.instance.CurrentCell.getAddList().contains(var4)) {
         IsoWorld.instance.CurrentCell.getAddList().add(var4);
      }

      if (GameServer.bServer) {
         GameServer.sendZombie(var4);
         GameServer.removeCorpseFromMap(this);
      }

      this.removeFromWorld();
      this.removeFromSquare();
      LuaEventManager.triggerEvent("OnContainerUpdate");
      var4.changeState(ReanimatePlayerState.instance());
      if (this.player != null) {
         if (GameServer.bServer) {
            GameServer.sendReanimatedZombieID(this.player, var4);
         } else if (!GameClient.bClient && this.player.isLocalPlayer()) {
            this.player.ReanimatedCorpse = var4;
         }

         this.player.setLeaveBodyTimedown(3601.0F);
      }

   }

   public static void Reset() {
      AllBodies.clear();
   }

   public void Collision(Vector2 var1, IsoObject var2) {
      if (var2 instanceof BaseVehicle) {
         BaseVehicle var3 = (BaseVehicle)var2;
         float var4 = 15.0F;
         Vector3f var5 = var3.getLinearVelocity(var3.tempVector3f_1);
         var5.y = 0.0F;
         Vector3f var6 = var3.tempVector3f_2.set(var3.x - this.x, 0.0F, var3.z - this.z);
         var6.normalize();
         var5.mul((Vector3fc)var6);
         float var7 = var5.length();
         var7 = Math.min(var7, var4);
         if (var7 < 0.05F) {
            return;
         }

         if (Math.abs(var3.getCurrentSpeedKmHour()) > 20.0F) {
            var3.doChrHitImpulse(this);
         }
      }

   }
}
