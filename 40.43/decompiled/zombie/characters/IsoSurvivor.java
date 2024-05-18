package zombie.characters;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.LOSThread;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.DieState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.ReanimateState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.behaviors.BehaviorHub;
import zombie.behaviors.survivor.orders.GuardOrder;
import zombie.behaviors.survivor.orders.LootBuilding;
import zombie.behaviors.survivor.orders.Order;
import zombie.behaviors.survivor.orders.Needs.DrinkWater;
import zombie.behaviors.survivor.orders.Needs.Heal;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.utils.OnceEvery;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.scripting.ScriptManager;
import zombie.ui.ObjectTooltip;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;

public class IsoSurvivor extends IsoLivingCharacter {
   public boolean NoGoreDeath = false;
   public BehaviorHub behaviours = new BehaviorHub();
   public boolean Draggable = false;
   public IsoGameCharacter following = null;
   boolean Dragging;
   int repathDelay = 0;
   public int nightsSurvived = 0;
   public int ping = 0;
   public IsoPushableObject collidePushable;
   private boolean tryToTeamUp = true;
   public boolean bLastSpottedPlayer = false;
   public boolean bSpottedPlayer = false;
   public boolean bWillJoinPlayer = false;
   public boolean HasBeenDragged = false;
   public IsoGameCharacter[] ClosestTwoSurvivors = new IsoGameCharacter[2];
   int NeightbourUpdate = 20;
   int NeightbourUpdateMax = 20;
   public Vector2 lmove = new Vector2(0.0F, 0.0F);
   public ArrayList LastLocalNeutralList = new ArrayList();
   OnceEvery LOSUpdate = new OnceEvery(0.4F, true);
   public int dangerTile = 0;
   public int lastDangerTile = 0;
   IsoGameCharacter aimAt = null;
   Stack availableTemp = new Stack();
   public static int SatisfiedByFoodLevel = 100;
   public static int SatisfiedByWeaponLevel = 80;

   public static byte[] createChecksum(String var0) throws Exception {
      FileInputStream var1 = new FileInputStream(var0);
      byte[] var2 = new byte[1024];
      MessageDigest var3 = MessageDigest.getInstance("MD5");

      int var4;
      do {
         var4 = var1.read(var2);
         if (var4 > 0) {
            var3.update(var2, 0, var4);
         }
      } while(var4 != -1);

      var1.close();
      return var3.digest();
   }

   public static String getMD5Checksum(String var0) throws Exception {
      byte[] var1 = createChecksum(var0);
      String var2 = "";

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 = var2 + Integer.toString((var1[var3] & 255) + 256, 16).substring(1);
      }

      return var2;
   }

   public static boolean DoChecksumCheck(String var0, String var1) {
      String var2 = "";

      try {
         var2 = getMD5Checksum(var0);
         if (!var2.equals(var1)) {
            return false;
         }
      } catch (Exception var6) {
         var2 = "";

         try {
            var2 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + var0);
         } catch (Exception var5) {
            return false;
         }
      }

      return var2.equals(var1);
   }

   public void Despawn() {
      if (this.descriptor != null) {
         this.descriptor.Instance = null;
         if (this.descriptor.Group != null && this.descriptor.Group.Leader == this.descriptor) {
            this.descriptor.Group.Despawn();
         }
      }

   }

   public static boolean DoChecksumCheck() {
      if (!DoChecksumCheck("zombie/GameWindow.class", "c4a62b8857f0fb6b9c103ff6ef127a9b")) {
         return false;
      } else if (!DoChecksumCheck("zombie/GameWindow$1.class", "5d93dc446b2dc49092fe4ecb5edf5f17")) {
         return false;
      } else if (!DoChecksumCheck("zombie/GameWindow$2.class", "a3e3d2c8cf6f0efaa1bf7f6ceb572073")) {
         return false;
      } else if (!DoChecksumCheck("zombie/gameStates/MainScreenState.class", "206848ba7cb764293dd2c19780263854")) {
         return false;
      } else if (!DoChecksumCheck("zombie/FrameLoader$1.class", "0ebfcc9557cc28d53aa982a71616bf5b")) {
         return false;
      } else {
         return DoChecksumCheck("zombie/FrameLoader.class", "d5b1f7b2886a499d848c204f6a815776");
      }
   }

   public String getObjectName() {
      return "Survivor";
   }

   public IsoSurvivor(IsoCell var1) {
      super(var1, 0.0F, 0.0F, 0.0F);
      this.OutlineOnMouseover = true;
      this.getCell().getSurvivorList().add(this);
      LuaEventManager.triggerEvent("OnCreateSurvivor", this);
   }

   public IsoSurvivor(IsoCell var1, int var2, int var3, int var4) {
      super(var1, (float)var2, (float)var3, (float)var4);
      this.getCell().getSurvivorList().add(this);
      this.OutlineOnMouseover = true;
      this.descriptor = new SurvivorDesc();
      this.PathSpeed = 0.05F;
      this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
      this.sprite.LoadFramesPcx("Wife", "death", 1);
      this.sprite.LoadFramesPcx("Wife", "dragged", 1);
      this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
      this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
      this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
      this.name = "Kate";
      this.solid = false;
      this.IgnoreStaggerBack = true;
      this.SpeakColour = new Color(204, 100, 100);
      this.dir = IsoDirections.S;
      this.OutlineOnMouseover = true;
      this.finder.maxSearchDistance = 120;
      this.CreateBehaviors();
      LuaEventManager.triggerEvent("OnCreateSurvivor", this);
      LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
   }

   public IsoSurvivor(SurvivorDesc var1, IsoCell var2, int var3, int var4, int var5) {
      super(var2, (float)var3, (float)var4, (float)var5);
      this.bFemale = var1.isFemale();
      this.descriptor = var1;
      var1.setInstance(this);
      this.OutlineOnMouseover = true;
      this.PathSpeed = 0.05F;
      String var6 = "Zombie_palette";
      var6 = var6 + "01";
      this.InitSpriteParts(var1, var1.legs, var1.torso, var1.head, var1.top, var1.bottoms, var1.shoes, var1.skinpal, var1.toppal, var1.bottomspal, var1.shoespal, var1.hair, var1.extra);
      this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
      this.finder.maxSearchDistance = 120;
      this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
      this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
      this.CreateBehaviors();
      this.Dressup(var1);
      LuaEventManager.triggerEventGarbage("OnCreateSurvivor", this);
      LuaEventManager.triggerEventGarbage("OnCreateLivingCharacter", this, this.descriptor);
   }

   public void reloadSpritePart() {
      this.sprite.AnimMap.clear();
      this.sprite.AnimStack.clear();
      this.sprite.CurrentAnim = null;
      this.extraSprites = new ArrayList();
      if (this.isFemale()) {
         if (this.descriptor.top != null) {
            this.descriptor.top = this.descriptor.top.replace("Shirt", "Blouse");
         }

         if (this.descriptor.toppal != null) {
            this.descriptor.toppal = this.descriptor.toppal.replace("Shirt", "Blouse");
         }
      } else {
         if (this.descriptor.top != null) {
            this.descriptor.top = this.descriptor.top.replace("Blouse", "Shirt");
         }

         if (this.descriptor.toppal != null) {
            this.descriptor.toppal = this.descriptor.toppal.replace("Blouse", "Shirt");
         }

         if (this.descriptor.bottoms != null) {
            this.descriptor.bottoms = this.descriptor.bottoms.replace("Skirt", "Trousers");
         }

         if (this.descriptor.bottomspal != null) {
            this.descriptor.bottomspal = this.descriptor.bottomspal.replace("Skirt", "Trousers");
         }
      }

      InventoryItem var1 = this.ClothingItem_Torso;
      if (var1 != null) {
         this.getInventory().Remove(var1);
         this.ClothingItem_Torso = null;
         this.topSprite = null;
      }

      if (this.descriptor.toppal != null && !this.descriptor.toppal.isEmpty()) {
         this.ClothingItem_Torso = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite(this.descriptor.toppal.replace("_White", "")));
         if (var1 != null) {
            this.ClothingItem_Torso.col.set(this.descriptor.topColor != null ? this.descriptor.topColor : new Color(1, 1, 1));
         }
      }

      InventoryItem var2 = this.ClothingItem_Legs;
      if (var2 != null) {
         this.getInventory().Remove(var2);
         this.ClothingItem_Legs = null;
         this.bottomsSprite = null;
      }

      if (this.descriptor.bottomspal != null && !this.descriptor.bottomspal.isEmpty()) {
         this.ClothingItem_Legs = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite(this.descriptor.bottomspal.replace("_White", "")));
         if (var2 != null) {
            this.ClothingItem_Legs.col.set(this.descriptor.trouserColor != null ? this.descriptor.trouserColor : new Color(1, 1, 1));
         }
      }

      InventoryItem var3 = this.ClothingItem_Feet;
      if (this.descriptor.shoes != null && !this.descriptor.shoes.isEmpty()) {
         if (var3 == null) {
            this.ClothingItem_Feet = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite("Shoes"));
            this.ClothingItem_Feet.col = new Color(64, 64, 64);
         }
      } else if (var3 != null) {
         this.getInventory().Remove(var3);
         this.ClothingItem_Feet = null;
      }

      this.InitSpriteParts(this.descriptor, this.descriptor.legs, this.descriptor.torso, this.descriptor.head, this.descriptor.top, this.descriptor.bottoms, this.descriptor.shoes, this.descriptor.skinpal, this.descriptor.toppal, this.descriptor.bottomspal, this.descriptor.shoespal, this.descriptor.hair, this.descriptor.extra);
   }

   public IsoSurvivor(SurvivorDesc var1, IsoCell var2, int var3, int var4, int var5, boolean var6) {
      super(var2, (float)var3, (float)var4, (float)var5);
      this.bFemale = var1.isFemale();
      this.descriptor = var1;
      if (var6) {
         var1.setInstance(this);
      }

      this.OutlineOnMouseover = true;
      this.PathSpeed = 0.05F;
      String var7 = "Zombie_palette";
      var7 = var7 + "01";
      this.InitSpriteParts(var1, var1.legs, var1.torso, var1.head, var1.top, var1.bottoms, var1.shoes, var1.skinpal, var1.toppal, var1.bottomspal, var1.shoespal, var1.hair, var1.extra);
      this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
      this.finder.maxSearchDistance = 120;
      this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
      this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
      this.CreateBehaviors();
      this.Dressup(var1);
      LuaEventManager.triggerEvent("OnCreateSurvivor", this);
   }

   public IsoSurvivor(SurvivorPersonality.Personality var1, SurvivorDesc var2, IsoCell var3, int var4, int var5, int var6) {
      super(var3, (float)var4, (float)var5, (float)var6);
      this.bFemale = var2.isFemale();
      this.getCell().getSurvivorList().add(this);
      if (var1 == SurvivorPersonality.Personality.Kate) {
         this.OutlineOnMouseover = true;
         this.sprite.LoadFramesPcx("Wife", "death", 1);
         this.sprite.LoadFramesPcx("Wife", "dragged", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
         this.solid = false;
         this.IgnoreStaggerBack = true;
         this.SpeakColour = new Color(204, 100, 100);
         this.dir = IsoDirections.S;
         this.descriptor = var2;
         var2.setInstance(this);
         this.PathSpeed = 0.05F;
         this.finder.maxSearchDistance = 120;
         this.CreateBehaviors();
         this.bOnBed = true;
         this.offsetY += 25.0F;
         this.offsetX -= 10.0F;
         this.ApplyInBedOffset(true);
         this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
         this.inflictWound(IsoGameCharacter.BodyLocation.Leg, 1.0F, false, 1.0F);
         this.OutlineOnMouseover = true;
      } else {
         this.OutlineOnMouseover = true;
         this.descriptor = var2;
         var2.setInstance(this);
         this.PathSpeed = 0.06F;
         String var7 = "Zombie_palette";
         var7 = var7 + "01";
         this.InitSpriteParts(var2, var2.legs, var2.torso, var2.head, var2.top, var2.bottoms, var2.shoes, var2.skinpal, var2.toppal, var2.bottomspal, var2.shoespal, var2.hair, var2.extra);
         this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
         this.finder.maxSearchDistance = 120;
         this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
         this.Personality = SurvivorPersonality.CreatePersonality(var1);
         this.CreateBehaviors();
         this.Dressup(var2);
         LuaEventManager.triggerEvent("OnCreateSurvivor", this);
         LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
      }
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      SurvivorDesc var3 = this.descriptor;
      this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
      if (this.Personality.type == SurvivorPersonality.Personality.Kate) {
         this.OutlineOnMouseover = true;
         this.sprite.LoadFramesPcx("Wife", "death", 1);
         this.sprite.LoadFramesPcx("Wife", "dragged", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
         this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
         this.setSolid(false);
         this.IgnoreStaggerBack = true;
         this.SpeakColour = new Color(204, 100, 100);
         this.dir = IsoDirections.S;
         this.descriptor = var3;
         var3.setInstance(this);
         this.PathSpeed = 0.05F;
         this.finder.maxSearchDistance = 120;
         this.CreateBehaviors();
         this.bOnBed = true;
         this.offsetY += 5.0F;
         this.offsetX -= 21.0F;
         this.ApplyInBedOffset(true);
         this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
      } else {
         this.bFemale = var3.isFemale();
         this.InitSpriteParts(var3, var3.legs, var3.torso, var3.head, var3.top, var3.bottoms, var3.shoes, var3.skinpal, var3.toppal, var3.bottomspal, var3.shoespal, var3.hair, var3.extra);
         this.CreateBehaviors();
         this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
         this.finder.maxSearchDistance = 120;
         this.PathSpeed = 0.06F;
      }
   }

   public void DoTooltip(ObjectTooltip var1) {
      boolean var2 = true;
      String var3 = "";
      var3 = this.descriptor.forename + " " + this.descriptor.surname;
      int var4 = 5;
      var1.DrawText(var3, 5.0D, (double)var4, 1.0D, 1.0D, 0.800000011920929D, 1.0D);

      for(int var5 = 0; var5 < this.wounds.size(); ++var5) {
         IsoGameCharacter.Wound var6 = (IsoGameCharacter.Wound)this.wounds.get(var5);
         var4 += 25;
         var1.DrawText("Broken Leg", 5.0D, (double)var4, 0.5D, 0.5D, 0.0D, 1.0D);
         var4 += 14;
         if (var6.tourniquet) {
            var1.DrawText("  Stemmed", 5.0D, (double)var4, 0.0D, 1.0D, 0.0D, 1.0D);
            var4 += 14;
         }

         if (var6.bandaged) {
            var1.DrawText("  Bandaged", 5.0D, (double)var4, 0.0D, 1.0D, 0.0D, 1.0D);
            var4 += 14;
         }

         if (var6.bleeding > 0.5F) {
            var1.DrawText("  Bleeding Badly", 5.0D, (double)var4, 1.0D, 0.0D, 0.0D, 1.0D);
         } else if (var6.bleeding > 0.0F) {
            var1.DrawText("  Bleeding", 5.0D, (double)var4, 0.5D, 0.5D, 0.0D, 1.0D);
         }
      }

      var1.setHeight((double)(var4 + 32));
   }

   public boolean HasTooltip() {
      return true;
   }

   public void spotted(IsoMovingObject var1) {
      if (var1 == IsoPlayer.instance) {
         ScriptManager.instance.Trigger("OnSpotPlayer", this.getScriptName());
         this.LastKnownLocation.put("Player", new IsoGameCharacter.Location((int)var1.getX(), (int)var1.getY(), (int)var1.getZ()));
         this.bSpottedPlayer = true;
         if (this.getZ() == IsoPlayer.instance.getZ() && IsoUtils.DistanceManhatten(this.getX(), this.getY(), IsoPlayer.instance.getX(), IsoPlayer.instance.getY()) < 8.0F && this.getCurrentSquare().getRoom() == IsoPlayer.instance.getCurrentSquare().getRoom()) {
            this.Meet(IsoPlayer.instance);
         }
      }

      for(int var2 = 0; var2 < this.EnemyList.size(); ++var2) {
         if (((IsoGameCharacter)this.EnemyList.get(var2)).descriptor.InGroupWith(this)) {
            this.EnemyList.remove(var2);
            --var2;
         }
      }

   }

   public boolean onMouseLeftClick(int var1, int var2) {
      if (IsoPlayer.instance != null && IsoPlayer.instance.isAiming) {
         return false;
      } else {
         if (IsoCamera.CamCharacter != IsoPlayer.instance && Core.bDebug) {
            IsoCamera.CamCharacter = this;
         }

         if (this != TutorialManager.instance.wife && UIManager.getDragInventory() == null) {
         }

         if (IsoPlayer.instance.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && IsoPlayer.instance.DistTo(this) < 4.0F && UIManager.getDragInventory() != null) {
            UIManager.getDragInventory().Use(this);
         }

         if (this.Draggable && this == TutorialManager.instance.wife && IsoUtils.DistanceTo(IsoPlayer.instance.getX(), IsoPlayer.instance.getY(), this.getX(), this.getY()) < 2.0F) {
            this.Draggable = true;
            this.Dragging = !this.Dragging;
            if (this.Dragging) {
               IsoPlayer.instance.DragCharacter = this;
               this.sprite.PlayAnim("dragged");
               this.ApplyInBedOffset(false);
            } else {
               IsoPlayer.instance.DragCharacter = null;
            }
         }

         if (IsoPlayer.instance.Health <= 0.0F || IsoPlayer.instance.BodyDamage.getHealth() <= 0.0F) {
            this.Dragging = false;
         }

         return true;
      }
   }

   public boolean AttemptAttack() {
      return this.DoAttack(1.0F);
   }

   public boolean DoAttack(float var1) {
      if (this.stateMachine.getCurrent() == SwipeStatePlayer.instance()) {
         return false;
      } else {
         if (var1 > 90.0F) {
            var1 = 90.0F;
         }

         var1 /= 25.0F;
         this.useChargeDelta = var1;
         if (this.useChargeDelta < 0.1F) {
            this.useChargeDelta = 1.0F;
         }

         if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() < 0.0F)) {
            if (this.leftHandItem != null && this.AttackDelay <= 0.0F && (!this.sprite.CurrentAnim.name.contains("Attack") || this.def.Frame >= (float)(this.sprite.CurrentAnim.Frames.size() - 1)) || this.def.Frame == 0.0F) {
               InventoryItem var2 = this.leftHandItem;
               if (var2 instanceof HandWeapon) {
                  this.useHandWeapon = (HandWeapon)var2;
                  if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.enduranceRecharging) {
                     return false;
                  }

                  if (UIManager.getPicked() != null) {
                     this.attackTargetSquare = UIManager.getPicked().square;
                     if (UIManager.getPicked().tile instanceof IsoMovingObject) {
                        this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
                     }
                  }

                  if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
                     return false;
                  }

                  if (this.useHandWeapon.getOtherHandRequire() == null || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
                     float var3 = this.useHandWeapon.getSwingTime();
                     if (this.useHandWeapon.isUseEndurance() && this.stats.enduranceRecharging) {
                        var3 *= 1.3F;
                     }

                     if (var3 < this.useHandWeapon.getMinimumSwingTime()) {
                        var3 = this.useHandWeapon.getMinimumSwingTime();
                     }

                     var3 *= this.useHandWeapon.getSpeedMod(this);
                     var3 *= 1.0F / GameTime.instance.getMultiplier();
                     this.AttackDelayMax = this.AttackDelay = (float)((int)(var3 * 60.0F));
                     this.AttackDelayUse = (float)((int)(this.AttackDelayMax * this.useHandWeapon.getDoSwingBeforeImpact()));
                     this.AttackDelayUse = this.AttackDelayMax - this.AttackDelayUse - 2.0F;
                     this.AttackWasSuperAttack = this.superAttack;
                     if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                        this.stateMachine.changeState(SwipeStatePlayer.instance());
                     }

                     if (this.useHandWeapon.getAmmoType() != null) {
                        this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
                     }

                     if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
                        this.leftHandItem.Use();
                     }

                     if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
                        this.rightHandItem.Use();
                     }

                     return true;
                  }

                  return false;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   public void update() {
      this.bCollidedWithPushable = false;
      if (this.getCurrentSquare() == null) {
         this.ensureOnTile();
      }

      LuaEventManager.triggerEvent("OnNPCSurvivorUpdate", this);
      this.lastDangerTile = this.dangerTile;
      this.dangerTile = IsoWorld.instance.CurrentCell.getDangerScore((int)this.getX(), (int)this.getY());
      if (this.LOSUpdate.Check()) {
         LOSThread.instance.AddJob(this);
      }

      if (this.getLastSquare() != this.getCurrentSquare() && this.getCurrentSquare() != null && this.getLastSquare() != null) {
         IsoObject var1 = this.getCurrentSquare().getDoorFrameTo(this.getLastSquare());
         if (var1 != null && var1 instanceof IsoDoor) {
            if (((IsoDoor)var1).open && this.RemoteID == -1) {
               IsoGridSquare var2 = var1.square;
               if (((IsoDoor)var1).north) {
                  var2 = IsoWorld.instance.CurrentCell.getGridSquare(var2.getX(), var2.getY() - 1, var2.getZ());
               } else {
                  var2 = IsoWorld.instance.CurrentCell.getGridSquare(var2.getX() - 1, var2.getY(), var2.getZ());
               }

               if (var1.square.getRoom() == null || var2.getRoom() == null) {
                  ((IsoDoor)var1).ToggleDoor(this);
               }
            }
         } else if (var1 != null && var1 instanceof IsoThumpable && ((IsoThumpable)var1).open && this.RemoteID == -1) {
            ((IsoThumpable)var1).ToggleDoor(this);
         }
      }

      boolean var14;
      if (IsoCamera.CamCharacter == this) {
         var14 = false;
      }

      if (this.descriptor.Instance == null) {
         this.descriptor.Instance = this;
      }

      this.leftHandItem = this.inventory.getBestWeapon(this.descriptor);
      if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() <= 0.0F)) {
         if ((this.Moodles.getMoodleLevel(MoodleType.Hungry) > 1 || this.BodyDamage.getHealth() < 100.0F && this.Moodles.getMoodleLevel(MoodleType.FoodEaten) < 2) && !this.HasPersonalNeed("Heal")) {
            this.PersonalNeeds.add(new Heal(this));
         }

         if (this.Moodles.getMoodleLevel(MoodleType.Thirst) > 0 && !this.HasPersonalNeed("DrinkWater")) {
            this.PersonalNeeds.add(new DrinkWater(this));
         }

         var14 = true;
         this.stats.fatigue = 0.0F;
         --this.NeightbourUpdate;
         boolean var15 = false;
         Stats var10000 = this.stats;
         var10000.stress += 1.0E-6F * (float)this.LocalRelevantEnemyList.size();
         if (this.NeightbourUpdate <= 0) {
            if (IsoPlayer.DemoMode) {
               WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 90, 90);
            }

            this.LastLocalNeutralList.clear();
            this.LastLocalNeutralList.addAll(this.LocalNeutralList);
            this.LocalNeutralList.clear();
            this.NeightbourUpdate = this.NeightbourUpdateMax;
            this.VeryCloseEnemyList.clear();
            this.ClosestTwoSurvivors[0] = null;
            this.ClosestTwoSurvivors[1] = null;
            this.LastLocalEnemies = this.LocalEnemyList.size();
            this.LocalEnemyList.clear();
            this.LocalRelevantEnemyList.clear();
            this.dangerLevels = 0.0F;
            synchronized(this.LocalList) {
               for(int var4 = 0; var4 < this.LocalList.size(); ++var4) {
                  IsoMovingObject var5 = (IsoMovingObject)this.LocalList.get(var4);
                  if (var5 != this && var5 instanceof IsoGameCharacter && (!(var5 instanceof IsoZombie) || !((IsoZombie)var5).Ghost) && (!(var5 instanceof IsoGameCharacter) || ((IsoGameCharacter)var5).VisibleToNPCs) && var5.getCurrentSquare() != null) {
                     int var6 = (int)(this.getX() - var5.getX());
                     int var7 = (int)(this.getY() - var5.getY());
                     int var8 = Math.abs(var6);
                     int var9 = Math.abs(var7);
                     if (var8 < 1) {
                        var8 = 1;
                     }

                     if (var9 < 1) {
                        var9 = 1;
                     }

                     var5.ensureOnTile();
                     if (var5 instanceof IsoZombie && var5.getCurrentSquare() != null && var5.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && var5.getZ() == this.getZ()) {
                        float var10 = 5.0F / (float)var8;
                        float var11 = 5.0F / (float)var9;
                        this.dangerLevels += var10 + var11;
                     }

                     if (var8 < 8 && var9 < 8 && var5.getCurrentSquare() != null && var5.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
                        if (var5 instanceof IsoSurvivor && !this.LastLocalNeutralList.contains(var5)) {
                           this.Meet((IsoSurvivor)var5);
                        }

                        if (var5 instanceof IsoSurvivor || var5 instanceof IsoPlayer) {
                           if (this.ClosestTwoSurvivors[0] == null) {
                              this.ClosestTwoSurvivors[0] = (IsoGameCharacter)var5;
                           } else if (this.ClosestTwoSurvivors[1] == null) {
                              this.ClosestTwoSurvivors[1] = (IsoGameCharacter)var5;
                           }
                        }

                        if (var8 < 3 && var9 < 3 && this.getZ() == var5.getZ() && var5.getCurrentSquare() != null && var5.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && var5 instanceof IsoZombie) {
                           this.VeryCloseEnemyList.add(var5);
                        }
                     }

                     if (var5.getCurrentSquare() != null && var5.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && var5.getCurrentSquare() != null && this.getCurrentSquare() != null && !(var5 instanceof IsoZombie) && this.EnemyList.contains((IsoGameCharacter)var5)) {
                     }

                     if (this instanceof IsoGameCharacter) {
                        if (!(var5 instanceof IsoZombie) && !this.EnemyList.contains((IsoGameCharacter)var5)) {
                           if (this.descriptor.Group == ((IsoGameCharacter)var5).descriptor.Group) {
                              this.LocalGroupList.add((IsoGameCharacter)var5);
                           }

                           this.LocalNeutralList.add((IsoGameCharacter)var5);
                        } else if (!(var5 instanceof IsoZombie) || !((IsoZombie)var5).Ghost) {
                           this.LocalRelevantEnemyList.add((IsoGameCharacter)var5);
                           this.LocalEnemyList.add((IsoGameCharacter)var5);
                           var5.spotted(this, false);
                        }
                     }
                  }
               }
            }

            if (this.LastLocalEnemies < this.LocalEnemyList.size()) {
            }
         }

         if (!this.getAllowBehaviours()) {
            this.setNx(this.getScriptnx());
            this.setNy(this.getScriptny());
         }

         if (this.getTimeSinceZombieAttack() == 1) {
            this.masterBehaviorList.reset();
         }

         super.update();
         if (this.stateMachine.getCurrent() != StaggerBackState.instance() && this.stateMachine.getCurrent() != StaggerBackDieState.instance() && this.stateMachine.getCurrent() != FakeDeadZombieState.instance() && this.stateMachine.getCurrent() != ReanimateState.instance()) {
            if (this.behaviours != null) {
               this.behaviours.SetTriggerValue("Hunger", this.stats.hunger);
               this.behaviours.SetTriggerValue("IdleBoredom", this.stats.idleboredom);
            }

            Vector2 var3 = new Vector2(this.getNx() - this.getLx(), this.getNy() - this.getLy());
            if ((this.Health <= 0.0F || this.BodyDamage.getHealth() < 0.0F) && this == TutorialManager.instance.wife && !this.NoGoreDeath) {
               this.PlayAnim("death");
            }

            var3.x *= this.getGlobalMovementMod();
            var3.y *= this.getGlobalMovementMod();
            if (this.Dragging) {
               this.HasBeenDragged = true;
               if (IsoPlayer.instance.dir == IsoDirections.N || IsoPlayer.instance.dir == IsoDirections.S || IsoPlayer.instance.dir == IsoDirections.E || IsoPlayer.instance.dir == IsoDirections.W) {
                  this.dir = IsoPlayer.instance.dir;
               }
            }

            if (this.HasBeenDragged) {
               this.sprite.PlayAnim("dragged");
            }

            if (this != TutorialManager.instance.wife) {
               if (this.RemoteID == -1 && !this.isDead()) {
                  if (var3.getLength() > 0.0F && this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                     if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
                        this.def.setFrameSpeedPerFrame(0.3F);
                     }

                     if (var3.getLength() > 0.07F && this.lmove.getLength() > 0.07F) {
                        this.def.Looped = true;
                        this.PlayAnimNoReset("Run");
                        this.def.Finished = false;
                     } else {
                        this.def.Looped = true;
                        this.PlayAnimNoReset("Walk");
                        this.def.Finished = false;
                     }
                  } else if (this.lmove.getLength() == 0.0F) {
                     InventoryItem var16;
                     if (this.legsSprite != null && !this.legsSprite.CurrentAnim.name.contains("Attack_")) {
                        this.def.setFrameSpeedPerFrame(0.1F);
                        if (this.aimAt == null) {
                           this.PlayAnim("Idle");
                        } else {
                           var16 = this.leftHandItem;
                           if (var16 instanceof HandWeapon && var16.getSwingAnim() != null) {
                              this.useHandWeapon = (HandWeapon)var16;
                              this.PlayAnimFrame("Attack_" + var16.getSwingAnim(), 0);
                           } else {
                              this.def.setFrameSpeedPerFrame(0.1F);
                              this.PlayAnim("Idle");
                           }
                        }
                     } else if (this.aimAt != null) {
                        var16 = this.leftHandItem;
                        if (var16 instanceof HandWeapon && var16.getSwingAnim() != null) {
                           this.useHandWeapon = (HandWeapon)var16;
                           this.PlayAnimFrame("Attack_" + var16.getSwingAnim(), 0);
                        } else {
                           this.def.setFrameSpeedPerFrame(0.1F);
                           this.PlayAnim("Idle");
                        }

                        Vector2 var17 = new Vector2(this.getX(), this.getY());
                        Vector2 var18 = new Vector2(this.aimAt.getX(), this.aimAt.getY());
                        var18.x -= var17.x;
                        var18.y -= var17.y;
                        var18.normalize();
                        this.DirectionFromVector(var18);
                        this.angle.x = var18.x;
                        this.angle.y = var18.y;
                        if (this.aimAt.Health <= 0.0F || this.aimAt.BodyDamage.getHealth() <= 0.0F) {
                           this.aimAt = null;
                        }
                     }
                  }
               }

               this.seperate();
               this.lmove.x = var3.x;
               this.lmove.y = var3.y;
               --this.repathDelay;
            }
         }
      } else {
         this.stateMachine.changeState(DieState.instance());
         this.stateMachine.Lock = true;
         super.update();
      }
   }

   public void SetAllFrames(short var1) {
      this.def.Frame = (float)var1;
   }

   public void renderlast() {
      super.renderlast();
      if (IsoCamera.CamCharacter == this) {
         IndieGL.End();
         byte var1 = 50;
         int var2 = var1 + 20;
         this.masterProper.renderDebug(var2);
      }

   }

   private void CreateBehaviors() {
      if (this.Personality != null) {
         this.Personality.CreateBehaviours(this);
      }

   }

   public void OnDeath() {
      if (this == TutorialManager.instance.wife && !this.NoGoreDeath) {
         this.PlayAnimUnlooped("death");
      }

   }

   public void Aim(IsoGameCharacter var1) {
      this.aimAt = var1;
   }

   private void Meet(IsoGameCharacter var1) {
      if (this.tryToTeamUp) {
         if (this.RemoteID == -1) {
            if (var1.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
               if (var1.getAllowBehaviours()) {
                  if (this.getAllowBehaviours()) {
                     this.descriptor.meet(var1.descriptor);
                     if (!this.MeetList.contains(var1.descriptor.ID)) {
                        if (!var1.getActiveInInstances().isEmpty() || !this.getActiveInInstances().isEmpty()) {
                           return;
                        }

                        if (var1.Speaking || this.Speaking) {
                           return;
                        }

                        this.MeetList.add(var1.descriptor.ID);
                        var1.MeetList.add(this.descriptor.ID);
                        if (var1.descriptor.Group != this.descriptor.Group && !(var1 instanceof IsoPlayer)) {
                           this.MeetFirstTime(var1);
                        }

                        LuaEventManager.triggerEvent("OnCharacterMeet", this, var1, 0);
                     } else {
                        if (!var1.getActiveInInstances().isEmpty() || !this.getActiveInInstances().isEmpty()) {
                           return;
                        }

                        if (var1.Speaking || this.Speaking) {
                           return;
                        }

                        LuaEventManager.triggerEvent("OnCharacterMeet", this, var1, (Integer)this.descriptor.MetCount.get(var1.descriptor.ID) - 1);
                     }

                  }
               }
            }
         }
      }
   }

   private void MeetAgain(IsoGameCharacter var1) {
      Integer var2;
      if (var1.BodyDamage.getNumPartsBitten() <= 0 && var1.BodyDamage.getNumPartsScratched() <= 0) {
         var2 = null;
         String var6 = "Base.MeetAgain";
         Object var3 = null;
         Object var4 = null;
         if (var1 instanceof IsoSurvivor) {
            if (Rand.Next(2) == 0) {
               var3 = this;
               var4 = var1;
            } else {
               var3 = var1;
               var4 = this;
            }
         } else {
            var3 = this;
            var4 = var1;
         }

         if (IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
         }

         Integer var5 = ScriptManager.instance.getFlagIntValue(var6 + "Count");
         ScriptManager.instance.PlayInstanceScript((String)null, var6 + (Rand.Next(var5) + 1), "Met", (IsoGameCharacter)var4, "Other", (IsoGameCharacter)var3);
      } else if (this.BodyDamage.getNumPartsBitten() <= 0 && this.BodyDamage.getNumPartsScratched() <= 0) {
         var2 = ScriptManager.instance.getFlagIntValue("Base.YouBeenBitCount");
         if (IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
         }

         ScriptManager.instance.PlayInstanceScript((String)null, "Base.YouBeenBit" + (Rand.Next(var2) + 1), "Bitten", var1, "Other", this);
      }

   }

   public void FollowMe(IsoGameCharacter var1) {
      String var2 = null;
      var2 = "Base.FollowMe";
      ScriptManager.instance.PlayInstanceScript((String)null, var2, "Follower", this, "Leader", var1);
   }

   public void StayHere(IsoGameCharacter var1) {
      String var2 = null;
      var2 = "Base.StayHere";
      ScriptManager.instance.PlayInstanceScript((String)null, var2, "Follower", this, "Leader", var1);
   }

   public void Guard(IsoPlayer var1) {
      var1.GuardModeUI = 1;
      var1.GuardChosen = this;
      String var2 = null;
      var2 = "Base.GuardA";
      ScriptManager.instance.PlayInstanceScript((String)null, var2, "Follower", this, "Leader", var1);
   }

   public void DoGuard(IsoPlayer var1) {
      String var2 = null;
      var2 = "Base.GuardB";
      ScriptManager.instance.PlayInstanceScript((String)null, var2, "Follower", this, "Leader", var1);
      this.Orders.push(new GuardOrder(this, var1.GuardStand, var1.GuardFace));
   }

   public void MeetFirstTime(IsoGameCharacter var1, boolean var2, boolean var3) {
      if (this.tryToTeamUp) {
         if (var3 && (var1.BodyDamage.getNumPartsBitten() > 0 || var1.BodyDamage.getNumPartsScratched() > 0)) {
            if (this.BodyDamage.getNumPartsBitten() <= 0 && this.BodyDamage.getNumPartsScratched() <= 0) {
               Integer var8 = ScriptManager.instance.getFlagIntValue("Base.YouBeenBitCount");
               ScriptManager.instance.PlayInstanceScript((String)null, "Base.YouBeenBit" + (Rand.Next(var8) + 1), "Bitten", var1, "Other", this);
            }
         } else {
            String var4 = null;
            var4 = "Base.FirstMeet";
            Object var5 = null;
            Object var6 = null;
            if (var1 instanceof IsoSurvivor) {
               if (Rand.Next(2) == 0) {
                  var5 = this;
                  var6 = var1;
               } else {
                  var5 = var1;
                  var6 = this;
               }
            } else if (var2) {
               var6 = this;
               var5 = var1;
            } else {
               var5 = this;
               var6 = var1;
            }

            Integer var7 = ScriptManager.instance.getFlagIntValue(var4 + "Count");
            ScriptManager.instance.PlayInstanceScript((String)null, var4 + (Rand.Next(var7) + 1), "Met", (IsoGameCharacter)var6, "Other", (IsoGameCharacter)var5);
         }

      }
   }

   public void MeetFirstTime(IsoGameCharacter var1) {
      this.MeetFirstTime(var1, false, true);
   }

   public void Killed(IsoGameCharacter var1) {
      if (!this.Speaking) {
         if (this.getActiveInInstances().isEmpty()) {
            if (Rand.Next(30) == 0) {
               IsoGameCharacter var3 = this.ClosestTwoSurvivors[0];
               IsoGameCharacter var4 = this.ClosestTwoSurvivors[1];
               Integer var5 = 3;
               if (var4 == null || !var4.getActiveInInstances().isEmpty() || var4.DistTo(this) > 8.0F) {
                  var4 = null;
                  var5 = var5 - 1;
               }

               if (var3 != null && (!var3.getActiveInInstances().isEmpty() || var3.DistTo(this) > 8.0F)) {
                  var3 = var4;
                  var4 = null;
                  var5 = var5 - 1;
               } else if (var3 == null) {
                  var5 = var5 - 1;
               }

               String var6 = "Base.Killed";
               var5 = Rand.Next(var5) + 1;
               if (var1 instanceof IsoZombie) {
                  var6 = var6 + "Zombie_";
               } else {
                  var6 = var6 + "Survivor_";
               }

               var6 = var6 + var5 + "Man";
               Integer var7 = ScriptManager.instance.getFlagIntValue(var6 + "Count");
               if (var5 == 3) {
                  ScriptManager.instance.PlayInstanceScript((String)null, var6 + (Rand.Next(var7) + 1), "B", var4, "A", var3, "Killer", this);
               }

               if (var5 == 2) {
                  ScriptManager.instance.PlayInstanceScript((String)null, var6 + (Rand.Next(var7) + 1), "A", var3, "Killer", this);
               }

               if (var5 == 1) {
                  ScriptManager.instance.PlayInstanceScript((String)null, var6 + (Rand.Next(var7) + 1), (String)"Killer", (IsoGameCharacter)this);
               }

            }
         }
      }
   }

   public void ChewedByZombies() {
      IsoGameCharacter var2 = this.ClosestTwoSurvivors[0];
      IsoGameCharacter var3 = this.ClosestTwoSurvivors[1];
      Integer var4 = 3;
      if (var3 == null || !var3.getActiveInInstances().isEmpty()) {
         var3 = null;
         var4 = var4 - 1;
      }

      if (var2 == null || var2.getActiveInInstances().isEmpty() && !(var2.DistTo(this) > 8.0F)) {
         if (var2 == null) {
            var4 = var4 - 1;
         }
      } else {
         var2 = var3;
         var3 = null;
         var4 = var4 - 1;
      }

      if (var4 == 3) {
         var4 = 2;
      }

      var4 = Rand.Next(var4) + 1;
      String var5 = "Base.ChewedByZombies";
      var5 = var5 + "_" + var4 + "Man";
      Integer var6 = ScriptManager.instance.getFlagIntValue(var5 + "Count");
      if (var4 == 2) {
         ScriptManager.instance.PlayInstanceScript((String)null, var5 + (Rand.Next(var6) + 1), "A", var2, "Chewed", this);
      }

      if (var4 == 1) {
         ScriptManager.instance.PlayInstanceScript((String)null, var5 + (Rand.Next(var6) + 1), (String)"Chewed", (IsoGameCharacter)this);
      }

   }

   private void DoRandomTalk() {
   }

   public void GivenItemBy(IsoGameCharacter var1, String var2, boolean var3) {
      if (!this.Speaking) {
         if (this.getActiveInInstances().isEmpty()) {
            String var4 = null;
            var4 = "Base.GivenItem";
            if (var3) {
               var4 = var4 + "Needed";
            } else {
               var4 = var4 + "Unneeded";
            }

            Object var5 = null;
            Object var6 = null;
            Integer var7 = ScriptManager.instance.getFlagIntValue(var4 + "Count");
            ScriptManager.instance.PlayInstanceScript((String)null, var4 + (Rand.Next(var7) + 1), "Giver", var1, "Taker", this);
         }
      }
   }

   public void PatchedUpBy(IsoGameCharacter var1) {
      if (!this.Speaking) {
         if (this.getActiveInInstances().isEmpty()) {
            String var2 = null;
            var2 = "Base.PatchedUp";
            Object var3 = null;
            Object var4 = null;
            Integer var5 = ScriptManager.instance.getFlagIntValue(var2 + "Count");
            ScriptManager.instance.PlayInstanceScript((String)null, var2 + (Rand.Next(var5) + 1), "Medic", var1, "Hurt", this);
         }
      }
   }

   public Stack getAvailableMembers() {
      this.availableTemp.clear();

      for(int var1 = 0; var1 < this.descriptor.Group.Members.size(); ++var1) {
         boolean var2 = false;
         SurvivorDesc var3 = (SurvivorDesc)this.descriptor.Group.Members.get(var1);
         if (var3 != this.descriptor) {
            if (var3.Instance.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() != null && var3.Instance.getCurrentSquare().getRoom().building == this.getCurrentSquare().getRoom().building) {
               var2 = true;
            }

            if (var3.Instance.DistTo(this) < 10.0F) {
               var2 = true;
            }

            if (var2) {
               this.availableTemp.add(var3.Instance);
            }
         }
      }

      return this.availableTemp;
   }

   private boolean HasPersonalNeed(String var1) {
      for(int var2 = 0; var2 < this.PersonalNeeds.size(); ++var2) {
         if (((Order)this.PersonalNeeds.get(var2)).type.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean SatisfiedWithInventory(LootBuilding.LootStyle var1, IsoSurvivor.SatisfiedBy var2) {
      float var3 = 0.0F;
      switch(var2) {
      case Food:
         var3 = this.inventory.getTotalFoodScore(this.descriptor);
         if (var3 > (float)SatisfiedByFoodLevel) {
            return true;
         }
      case Weapons:
         var3 = this.inventory.getTotalWeaponScore(this.descriptor);
         if (var3 > (float)SatisfiedByWeaponLevel) {
            return true;
         }
      default:
         return false;
      }
   }

   public boolean getTryToTeamUp() {
      return this.tryToTeamUp;
   }

   public void setTryToTeamUp(boolean var1) {
      this.tryToTeamUp = var1;
   }

   public void reloadSpriteColors() {
      InventoryItem var1 = this.ClothingItem_Torso;
      if (var1 != null && this.topSprite != null && this.descriptor.topColor != null) {
         this.topSprite.TintMod.r = this.descriptor.topColor.r;
         this.topSprite.TintMod.g = this.descriptor.topColor.g;
         this.topSprite.TintMod.b = this.descriptor.topColor.b;
         this.topSprite.TintMod.desaturate(0.5F);
      }

      InventoryItem var2 = this.ClothingItem_Legs;
      if (var2 != null && this.bottomsSprite != null && this.descriptor.trouserColor != null) {
         this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
         this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
         this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
         this.bottomsSprite.TintMod.desaturate(0.5F);
      }

   }

   public static enum SatisfiedBy {
      Food,
      Weapons,
      Water;
   }
}
