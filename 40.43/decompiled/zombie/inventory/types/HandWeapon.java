package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public class HandWeapon extends InventoryItem {
   public float SplatSize = 1.0F;
   protected String ammoType = null;
   protected boolean angleFalloff = false;
   protected boolean bCanBarracade = false;
   protected boolean directional = false;
   protected float doSwingBeforeImpact = 0.0F;
   protected String impactSound = "ZombieImpact";
   protected boolean knockBackOnNoDeath = true;
   protected float maxAngle = 1.0F;
   protected float maxDamage = 1.5F;
   protected int maxHitCount = 1000;
   protected float maxRange = 1.0F;
   protected boolean ranged = false;
   protected float minAngle = 0.5F;
   protected float minDamage = 0.4F;
   protected float minimumSwingTime = 0.5F;
   protected float minRange = 0.0F;
   protected float noiseFactor = 0.0F;
   protected String otherHandRequire = null;
   protected boolean otherHandUse = false;
   protected String physicsObject = null;
   protected float pushBackMod = 1.0F;
   protected boolean rangeFalloff = false;
   protected boolean shareDamage = true;
   protected int soundRadius = 0;
   protected int soundVolume = 0;
   protected boolean splatBloodOnNoDeath = false;
   protected int splatNumber = 2;
   protected String swingSound = "BatSwing";
   protected float swingTime = 1.0F;
   protected float toHitModifier = 1.0F;
   protected boolean useEndurance = true;
   protected boolean useSelf = false;
   protected String weaponSprite = null;
   protected float otherBoost = 1.0F;
   protected int DoorDamage = 1;
   protected String doorHitSound = "ChopDoor";
   protected int ConditionLowerChance = 10000;
   protected boolean MultipleHitConditionAffected = true;
   protected boolean shareEndurance = true;
   protected boolean AlwaysKnockdown = false;
   protected float EnduranceMod = 1.0F;
   protected float KnockdownMod = 1.0F;
   protected boolean CantAttackWithLowestEndurance = false;
   public boolean bIsAimedFirearm = false;
   public boolean bIsAimedHandWeapon = false;
   public String RunAnim = "Run";
   public String IdleAnim = "Idle";
   public float HitAngleMod = 0.0F;
   private String SubCategory = "";
   private ArrayList Categories = null;
   private int AimingPerkCritModifier = 0;
   private float AimingPerkRangeModifier = 0.0F;
   private float AimingPerkHitChanceModifier = 0.0F;
   private int HitChance = 0;
   private float AimingPerkMinAngleModifier = 0.0F;
   private int RecoilDelay = 0;
   private boolean PiercingBullets = false;
   private float soundGain = 1.0F;
   private WeaponPart scope = null;
   private WeaponPart canon = null;
   private WeaponPart clip = null;
   private WeaponPart recoilpad = null;
   private WeaponPart sling = null;
   private WeaponPart stock = null;
   private int ClipSize = 0;
   private int reloadTime = 0;
   private int aimingTime = 0;
   private float minRangeRanged = 0.0F;
   private int treeDamage = 0;
   private String bulletOutSound = null;
   private String shellFallSound = null;
   private int triggerExplosionTimer = 0;
   private boolean canBePlaced = false;
   private int explosionRange = 0;
   private int explosionPower = 0;
   private int fireRange = 0;
   private int firePower = 0;
   private int smokeRange = 0;
   private int noiseRange = 0;
   private float extraDamage = 0.0F;
   private int explosionTimer = 0;
   private String placedSprite = null;
   private boolean canBeReused = false;
   private int sensorRange = 0;
   public int ProjectileCount = 1;
   public float aimingMod = 1.0F;
   public float CriticalChance = 20.0F;
   private String hitSound = "BatHit";

   public float getSplatSize() {
      return this.SplatSize;
   }

   public boolean CanStack(InventoryItem var1) {
      return false;
   }

   public String getCategory() {
      return this.mainCategory != null ? this.mainCategory : "Weapon";
   }

   public HandWeapon(String var1, String var2, String var3, String var4) {
      super(var1, var2, var3, var4);
      this.cat = ItemType.Weapon;
   }

   public HandWeapon(String var1, String var2, String var3, Item var4) {
      super(var1, var2, var3, var4);
      this.cat = ItemType.Weapon;
   }

   public int getSaveType() {
      return Item.Type.Weapon.ordinal();
   }

   public float getScore(SurvivorDesc var1) {
      float var2 = 0.0F;
      if (this.ammoType != null && !this.ammoType.equals("none") && !this.container.contains(this.ammoType)) {
         var2 -= 100000.0F;
      }

      if (this.Condition == 0) {
         var2 -= 100000.0F;
      }

      var2 += this.maxDamage * 10.0F;
      var2 += this.maxAngle * 5.0F;
      var2 -= this.minimumSwingTime * 0.1F;
      var2 -= this.swingTime;
      if (var1 != null && var1.getInstance().getThreatLevel() <= 2 && this.soundRadius > 5) {
         if (var2 > 0.0F && (float)this.soundRadius > var2) {
            var2 = 1.0F;
         }

         var2 -= (float)this.soundRadius;
      }

      return var2;
   }

   public boolean TestCanBarracade(IsoGameCharacter var1) {
      return var1.getInventory().contains("Nails") && var1.getInventory().contains("Plank");
   }

   public void DoTooltip(ObjectTooltip var1, ObjectTooltip.Layout var2) {
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 0.8F;
      float var7 = 1.0F;
      float var8 = 0.0F;
      float var9 = 0.6F;
      float var10 = 0.0F;
      float var11 = 0.7F;
      ObjectTooltip.LayoutItem var3 = var2.addItem();
      var3.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", var4, var5, var6, var7);
      float var12 = (float)this.Condition / (float)this.ConditionMax;
      var3.setProgress(var12, var8, var9, var10, var11);
      float var13;
      float var14;
      if (this.getMaxDamage() > 0.0F) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Damage") + ":", var4, var5, var6, var7);
         var12 = this.getMaxDamage() + this.getMinDamage();
         var13 = 5.0F;
         var14 = var12 / var13;
         var3.setProgress(var14, var8, var9, var10, var11);
      }

      if (this.isRanged()) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Range") + ":", var4, var5, var6, 1.0F);
         var12 = this.getMaxRange(IsoPlayer.instance);
         var13 = 40.0F;
         var14 = var12 / var13;
         var3.setProgress(var14, var8, var9, var10, var11);
      }

      if (this.isTwoHandWeapon()) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_item_TwoHandWeapon"), var4, var5, var6, var7);
      }

      if (this.CantAttackWithLowestEndurance) {
         var3 = var2.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Unusable_at_max_exertion"), 1.0F, 0.0F, 0.0F, 1.0F);
      }

      String var19 = this.getAmmoType();
      if (var19 == null && this.hasModData()) {
         Object var20 = this.getModData().rawget("defaultAmmo");
         if (var20 instanceof String) {
            var19 = (String)var20;
         }
      }

      if (var19 != null) {
         Item var21 = ScriptManager.instance.FindItem(var19);
         if (var21 == null) {
            var21 = ScriptManager.instance.FindItem(this.getModule() + "." + var19);
         }

         if (var21 != null) {
            var3 = var2.addItem();
            var3.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", var4, var5, var6, var7);
            var3.setValue(var21.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
         }

         Object var22 = this.getModData().rawget("currentCapacity");
         Object var15 = this.getModData().rawget("maxCapacity");
         if (var22 instanceof Double && var15 instanceof Double) {
            String var16 = ((Double)var22).intValue() + " / " + ((Double)var15).intValue();
            Object var17 = this.getModData().rawget("roundChambered");
            if (var17 instanceof Double && ((Double)var17).intValue() == 1) {
               var16 = ((Double)var22).intValue() + "+1 / " + ((Double)var15).intValue();
            } else {
               Object var18 = this.getModData().rawget("emptyShellChambered");
               if (var18 instanceof Double && ((Double)var18).intValue() == 1) {
                  var16 = ((Double)var22).intValue() + "+x / " + ((Double)var15).intValue();
               }
            }

            var3 = var2.addItem();
            var3.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            var3.setValue(var16, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      ObjectTooltip.Layout var23 = var1.beginLayout();
      if (this.getStock() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Stock") + ":", var4, var5, var6, var7);
         var3.setValue(this.getStock().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.getSling() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Sling") + ":", var4, var5, var6, var7);
         var3.setValue(this.getSling().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.getScope() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Scope") + ":", var4, var5, var6, var7);
         var3.setValue(this.getScope().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.getCanon() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Canon") + ":", var4, var5, var6, var7);
         var3.setValue(this.getCanon().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.getClip() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_Clip") + ":", var4, var5, var6, var7);
         var3.setValue(this.getClip().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.getRecoilpad() != null) {
         var3 = var23.addItem();
         var3.setLabel(Translator.getText("Tooltip_weapon_RecoilPad") + ":", var4, var5, var6, var7);
         var3.setValue(this.getRecoilpad().getName(), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (!var23.items.isEmpty()) {
         var2.next = var23;
         var23.nextPadY = var1.getLineSpacing();
      } else {
         var1.endLayout(var23);
      }

   }

   public float getDamageMod(IsoGameCharacter var1) {
      int var2 = var1.getPerkLevel(PerkFactory.Perks.Blunt);
      if (this.ScriptItem.Categories.contains("Blunt")) {
         if (var2 >= 3 && var2 <= 6) {
            return 1.1F;
         }

         if (var2 >= 7) {
            return 1.2F;
         }
      }

      int var3 = var1.getPerkLevel(PerkFactory.Perks.Axe);
      if (this.ScriptItem.Categories.contains("Axe")) {
         if (var3 >= 3 && var3 <= 6) {
            return 1.1F;
         }

         if (var3 >= 7) {
            return 1.2F;
         }
      }

      return 1.0F;
   }

   public float getRangeMod(IsoGameCharacter var1) {
      int var2 = var1.getPerkLevel(PerkFactory.Perks.Blunt);
      if (this.ScriptItem.Categories.contains("Blunt") && var2 >= 7) {
         return 1.2F;
      } else {
         int var3 = var1.getPerkLevel(PerkFactory.Perks.Axe);
         return this.ScriptItem.Categories.contains("Axe") && var3 >= 7 ? 1.2F : 1.0F;
      }
   }

   public float getFatigueMod(IsoGameCharacter var1) {
      int var2 = var1.getPerkLevel(PerkFactory.Perks.Blunt);
      if (this.ScriptItem.Categories.contains("Blunt") && var2 >= 8) {
         return 0.8F;
      } else {
         int var3 = var1.getPerkLevel(PerkFactory.Perks.Axe);
         return this.ScriptItem.Categories.contains("Axe") && var3 >= 8 ? 0.8F : 1.0F;
      }
   }

   public float getKnockbackMod(IsoGameCharacter var1) {
      int var2 = var1.getPerkLevel(PerkFactory.Perks.Axe);
      return this.ScriptItem.Categories.contains("Axe") && var2 >= 6 ? 2.0F : 1.0F;
   }

   public float getSpeedMod(IsoGameCharacter var1) {
      int var2;
      if (this.ScriptItem.Categories.contains("Blunt")) {
         var2 = var1.getPerkLevel(PerkFactory.Perks.Blunt);
         if (var2 >= 10) {
            return 0.65F;
         }

         if (var2 >= 9) {
            return 0.68F;
         }

         if (var2 >= 8) {
            return 0.71F;
         }

         if (var2 >= 7) {
            return 0.74F;
         }

         if (var2 >= 6) {
            return 0.77F;
         }

         if (var2 >= 5) {
            return 0.8F;
         }

         if (var2 >= 4) {
            return 0.83F;
         }

         if (var2 >= 3) {
            return 0.86F;
         }

         if (var2 >= 2) {
            return 0.9F;
         }

         if (var2 >= 1) {
            return 0.95F;
         }
      }

      if (this.ScriptItem.Categories.contains("Axe")) {
         var2 = var1.getPerkLevel(PerkFactory.Perks.Axe);
         float var3 = 1.0F;
         if (var1.HasTrait("Axeman")) {
            var3 = 0.95F;
         }

         if (var2 >= 10) {
            return 0.65F * var3;
         } else if (var2 >= 9) {
            return 0.68F * var3;
         } else if (var2 >= 8) {
            return 0.71F * var3;
         } else if (var2 >= 7) {
            return 0.74F * var3;
         } else if (var2 >= 6) {
            return 0.77F * var3;
         } else if (var2 >= 5) {
            return 0.8F * var3;
         } else if (var2 >= 4) {
            return 0.83F * var3;
         } else if (var2 >= 3) {
            return 0.86F * var3;
         } else if (var2 >= 2) {
            return 0.9F * var3;
         } else {
            return var2 >= 1 ? 0.95F * var3 : 1.0F * var3;
         }
      } else {
         return 1.0F;
      }
   }

   public float getToHitMod(IsoGameCharacter var1) {
      int var2 = var1.getPerkLevel(PerkFactory.Perks.Blunt);
      if (this.ScriptItem.Categories.contains("Blunt")) {
         if (var2 == 1) {
            return 1.2F;
         }

         if (var2 == 2) {
            return 1.3F;
         }

         if (var2 == 3) {
            return 1.4F;
         }

         if (var2 == 4) {
            return 1.5F;
         }

         if (var2 == 5) {
            return 1.6F;
         }

         if (var2 == 6) {
            return 1.7F;
         }

         if (var2 == 7) {
            return 1.8F;
         }

         if (var2 == 8) {
            return 1.9F;
         }

         if (var2 == 9) {
            return 2.0F;
         }

         if (var2 == 10) {
            return 100.0F;
         }
      }

      int var3 = var1.getPerkLevel(PerkFactory.Perks.Axe);
      if (this.ScriptItem.Categories.contains("Axe")) {
         if (var3 == 1) {
            return 1.2F;
         }

         if (var3 == 2) {
            return 1.3F;
         }

         if (var3 == 3) {
            return 1.4F;
         }

         if (var3 == 4) {
            return 1.5F;
         }

         if (var3 == 5) {
            return 1.6F;
         }

         if (var3 == 6) {
            return 1.7F;
         }

         if (var3 == 7) {
            return 1.8F;
         }

         if (var3 == 8) {
            return 1.9F;
         }

         if (var3 == 9) {
            return 2.0F;
         }

         if (var3 == 10) {
            return 100.0F;
         }
      }

      return 1.0F;
   }

   public String getAmmoType() {
      return this.ammoType;
   }

   public void setAmmoType(String var1) {
      this.ammoType = var1;
   }

   public boolean isAngleFalloff() {
      return this.angleFalloff;
   }

   public void setAngleFalloff(boolean var1) {
      this.angleFalloff = var1;
   }

   public boolean isCanBarracade() {
      return this.bCanBarracade;
   }

   public void setCanBarracade(boolean var1) {
      this.bCanBarracade = var1;
   }

   public boolean isDirectional() {
      return this.directional;
   }

   public void setDirectional(boolean var1) {
      this.directional = var1;
   }

   public float getDoSwingBeforeImpact() {
      return this.doSwingBeforeImpact;
   }

   public void setDoSwingBeforeImpact(float var1) {
      this.doSwingBeforeImpact = var1;
   }

   public String getImpactSound() {
      return this.impactSound;
   }

   public void setImpactSound(String var1) {
      this.impactSound = var1;
   }

   public boolean isKnockBackOnNoDeath() {
      return this.knockBackOnNoDeath;
   }

   public void setKnockBackOnNoDeath(boolean var1) {
      this.knockBackOnNoDeath = var1;
   }

   public float getMaxAngle() {
      return this.maxAngle;
   }

   public void setMaxAngle(float var1) {
      this.maxAngle = var1;
   }

   public float getMaxDamage() {
      return this.maxDamage;
   }

   public void setMaxDamage(float var1) {
      this.maxDamage = var1;
   }

   public int getMaxHitCount() {
      return this.maxHitCount;
   }

   public void setMaxHitCount(int var1) {
      this.maxHitCount = var1;
   }

   public float getMaxRange() {
      return this.maxRange;
   }

   public float getMaxRange(IsoGameCharacter var1) {
      return this.isRanged() ? this.maxRange + this.getAimingPerkRangeModifier() * (float)(var1.getPerkLevel(PerkFactory.Perks.Aiming) / 2) : this.maxRange;
   }

   public void setMaxRange(float var1) {
      this.maxRange = var1;
   }

   public boolean isRanged() {
      return this.ranged;
   }

   public void setRanged(boolean var1) {
      this.ranged = var1;
   }

   public float getMinAngle() {
      return this.minAngle;
   }

   public void setMinAngle(float var1) {
      this.minAngle = var1;
   }

   public float getMinDamage() {
      return this.minDamage;
   }

   public void setMinDamage(float var1) {
      this.minDamage = var1;
   }

   public float getMinimumSwingTime() {
      return this.minimumSwingTime;
   }

   public void setMinimumSwingTime(float var1) {
      this.minimumSwingTime = var1;
   }

   public float getMinRange() {
      return this.minRange;
   }

   public void setMinRange(float var1) {
      this.minRange = var1;
   }

   public float getNoiseFactor() {
      return this.noiseFactor;
   }

   public void setNoiseFactor(float var1) {
      this.noiseFactor = var1;
   }

   public String getOtherHandRequire() {
      return this.otherHandRequire;
   }

   public void setOtherHandRequire(String var1) {
      this.otherHandRequire = var1;
   }

   public boolean isOtherHandUse() {
      return this.otherHandUse;
   }

   public void setOtherHandUse(boolean var1) {
      this.otherHandUse = var1;
   }

   public String getPhysicsObject() {
      return this.physicsObject;
   }

   public void setPhysicsObject(String var1) {
      this.physicsObject = var1;
   }

   public float getPushBackMod() {
      return this.pushBackMod;
   }

   public void setPushBackMod(float var1) {
      this.pushBackMod = var1;
   }

   public boolean isRangeFalloff() {
      return this.rangeFalloff;
   }

   public void setRangeFalloff(boolean var1) {
      this.rangeFalloff = var1;
   }

   public boolean isShareDamage() {
      return this.shareDamage;
   }

   public void setShareDamage(boolean var1) {
      this.shareDamage = var1;
   }

   public int getSoundRadius() {
      return this.soundRadius;
   }

   public void setSoundRadius(int var1) {
      this.soundRadius = var1;
   }

   public int getSoundVolume() {
      return this.soundVolume;
   }

   public void setSoundVolume(int var1) {
      this.soundVolume = var1;
   }

   public boolean isSplatBloodOnNoDeath() {
      return this.splatBloodOnNoDeath;
   }

   public void setSplatBloodOnNoDeath(boolean var1) {
      this.splatBloodOnNoDeath = var1;
   }

   public int getSplatNumber() {
      return this.splatNumber;
   }

   public void setSplatNumber(int var1) {
      this.splatNumber = var1;
   }

   public String getSwingSound() {
      return this.swingSound;
   }

   public void setSwingSound(String var1) {
      this.swingSound = var1;
   }

   public float getSwingTime() {
      return this.swingTime;
   }

   public void setSwingTime(float var1) {
      this.swingTime = var1;
   }

   public float getToHitModifier() {
      return this.toHitModifier;
   }

   public void setToHitModifier(float var1) {
      this.toHitModifier = var1;
   }

   public boolean isUseEndurance() {
      return this.useEndurance;
   }

   public void setUseEndurance(boolean var1) {
      this.useEndurance = var1;
   }

   public boolean isUseSelf() {
      return this.useSelf;
   }

   public void setUseSelf(boolean var1) {
      this.useSelf = var1;
   }

   public String getWeaponSprite() {
      return this.weaponSprite;
   }

   public void setWeaponSprite(String var1) {
      this.weaponSprite = var1;
   }

   public float getOtherBoost() {
      return this.otherBoost;
   }

   public void setOtherBoost(float var1) {
      this.otherBoost = var1;
   }

   public int getDoorDamage() {
      return this.DoorDamage;
   }

   public void setDoorDamage(int var1) {
      this.DoorDamage = var1;
   }

   public String getDoorHitSound() {
      return this.doorHitSound;
   }

   public void setDoorHitSound(String var1) {
      this.doorHitSound = var1;
   }

   public int getConditionLowerChance() {
      return this.ConditionLowerChance;
   }

   public void setConditionLowerChance(int var1) {
      this.ConditionLowerChance = var1;
   }

   public boolean isMultipleHitConditionAffected() {
      return this.MultipleHitConditionAffected;
   }

   public void setMultipleHitConditionAffected(boolean var1) {
      this.MultipleHitConditionAffected = var1;
   }

   public boolean isShareEndurance() {
      return this.shareEndurance;
   }

   public void setShareEndurance(boolean var1) {
      this.shareEndurance = var1;
   }

   public boolean isAlwaysKnockdown() {
      return this.AlwaysKnockdown;
   }

   public void setAlwaysKnockdown(boolean var1) {
      this.AlwaysKnockdown = var1;
   }

   public float getEnduranceMod() {
      return this.EnduranceMod;
   }

   public void setEnduranceMod(float var1) {
      this.EnduranceMod = var1;
   }

   public float getKnockdownMod() {
      return this.KnockdownMod;
   }

   public void setKnockdownMod(float var1) {
      this.KnockdownMod = var1;
   }

   public boolean isCantAttackWithLowestEndurance() {
      return this.CantAttackWithLowestEndurance;
   }

   public void setCantAttackWithLowestEndurance(boolean var1) {
      this.CantAttackWithLowestEndurance = var1;
   }

   public boolean isAimedFirearm() {
      return this.bIsAimedFirearm;
   }

   public boolean isAimedHandWeapon() {
      return this.bIsAimedHandWeapon;
   }

   public int getProjectileCount() {
      return this.ProjectileCount;
   }

   public float getAimingMod() {
      return this.aimingMod;
   }

   public boolean isAimed() {
      return this.bIsAimedFirearm || this.bIsAimedHandWeapon;
   }

   public void setCriticalChance(float var1) {
      this.CriticalChance = var1;
   }

   public float getCriticalChance() {
      return this.CriticalChance;
   }

   public void setSubCategory(String var1) {
      this.SubCategory = var1;
   }

   public String getSubCategory() {
      return this.SubCategory;
   }

   public void setZombieHitSound(String var1) {
      this.hitSound = var1;
   }

   public String getZombieHitSound() {
      return this.hitSound;
   }

   public ArrayList getCategories() {
      return this.Categories;
   }

   public void setCategories(ArrayList var1) {
      this.Categories = var1;
   }

   public int getAimingPerkCritModifier() {
      return this.AimingPerkCritModifier;
   }

   public void setAimingPerkCritModifier(int var1) {
      this.AimingPerkCritModifier = var1;
   }

   public float getAimingPerkRangeModifier() {
      return this.AimingPerkRangeModifier;
   }

   public void setAimingPerkRangeModifier(float var1) {
      this.AimingPerkRangeModifier = var1;
   }

   public int getHitChance() {
      return this.HitChance;
   }

   public void setHitChance(int var1) {
      this.HitChance = var1;
   }

   public float getAimingPerkHitChanceModifier() {
      return this.AimingPerkHitChanceModifier;
   }

   public void setAimingPerkHitChanceModifier(float var1) {
      this.AimingPerkHitChanceModifier = var1;
   }

   public float getAimingPerkMinAngleModifier() {
      return this.AimingPerkMinAngleModifier;
   }

   public void setAimingPerkMinAngleModifier(float var1) {
      this.AimingPerkMinAngleModifier = var1;
   }

   public int getRecoilDelay() {
      return this.RecoilDelay;
   }

   public void setRecoilDelay(int var1) {
      this.RecoilDelay = var1;
   }

   public boolean isPiercingBullets() {
      return this.PiercingBullets;
   }

   public void setPiercingBullets(boolean var1) {
      this.PiercingBullets = var1;
   }

   public float getSoundGain() {
      return this.soundGain;
   }

   public void setSoundGain(float var1) {
      this.soundGain = var1;
   }

   public WeaponPart getScope() {
      return this.scope;
   }

   public void setScope(WeaponPart var1) {
      this.scope = var1;
   }

   public WeaponPart getClip() {
      return this.clip;
   }

   public void setClip(WeaponPart var1) {
      this.clip = var1;
   }

   public WeaponPart getCanon() {
      return this.canon;
   }

   public void setCanon(WeaponPart var1) {
      this.canon = var1;
   }

   public WeaponPart getRecoilpad() {
      return this.recoilpad;
   }

   public void setRecoilpad(WeaponPart var1) {
      this.recoilpad = var1;
   }

   public int getClipSize() {
      return this.ClipSize;
   }

   public void setClipSize(int var1) {
      this.ClipSize = var1;
      this.getModData().rawset("maxCapacity", (new Integer(var1)).doubleValue());
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      super.save(var1, var2);
      var1.putFloat(this.maxRange);
      var1.putFloat(this.minRangeRanged);
      var1.putInt(this.ClipSize);
      var1.putFloat(this.minDamage);
      var1.putFloat(this.maxDamage);
      var1.putInt(this.RecoilDelay);
      var1.putInt(this.aimingTime);
      var1.putInt(this.reloadTime);
      var1.putInt(this.HitChance);
      var1.putFloat(this.minAngle);
      if (this.getScope() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getScope().getModule() + "." + this.getScope().getType());
      } else {
         var1.put((byte)0);
      }

      if (this.getClip() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getClip().getModule() + "." + this.getClip().getType());
      } else {
         var1.put((byte)0);
      }

      if (this.getRecoilpad() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getRecoilpad().getModule() + "." + this.getRecoilpad().getType());
      } else {
         var1.put((byte)0);
      }

      if (this.getSling() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getSling().getModule() + "." + this.getSling().getType());
      } else {
         var1.put((byte)0);
      }

      if (this.getStock() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getStock().getModule() + "." + this.getStock().getType());
      } else {
         var1.put((byte)0);
      }

      if (this.getCanon() != null) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getCanon().getModule() + "." + this.getCanon().getType());
      } else {
         var1.put((byte)0);
      }

      var1.putInt(this.getExplosionTimer());
      var1.putFloat(this.maxAngle);
   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      super.load(var1, var2, var3);
      if (var2 >= 36) {
         this.setMaxRange(var1.getFloat());
         this.setMinRangeRanged(var1.getFloat());
         this.setClipSize(var1.getInt());
         this.setMinDamage(var1.getFloat());
         this.setMaxDamage(var1.getFloat());
         this.setRecoilDelay(var1.getInt());
         this.setAimingTime(var1.getInt());
         this.setReloadTime(var1.getInt());
         this.setHitChance(var1.getInt());
         this.setMinAngle(var1.getFloat());
         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var1.get() == 1) {
            this.attachWeaponPart((WeaponPart)InventoryItemFactory.CreateItem(GameWindow.ReadString(var1)), false);
         }

         if (var2 >= 62) {
            this.setExplosionTimer(var1.getInt());
         }
      }

      if (var2 >= 105) {
         this.setMaxAngle(var1.getFloat());
      }

   }

   public float getMinRangeRanged() {
      return this.minRangeRanged;
   }

   public void setMinRangeRanged(float var1) {
      this.minRangeRanged = var1;
   }

   public int getReloadTime() {
      return this.reloadTime;
   }

   public void setReloadTime(int var1) {
      this.reloadTime = var1;
   }

   public WeaponPart getSling() {
      return this.sling;
   }

   public void setSling(WeaponPart var1) {
      this.sling = var1;
   }

   public int getAimingTime() {
      return this.aimingTime;
   }

   public void setAimingTime(int var1) {
      this.aimingTime = var1;
   }

   public WeaponPart getStock() {
      return this.stock;
   }

   public void setStock(WeaponPart var1) {
      this.stock = var1;
   }

   public int getTreeDamage() {
      return this.treeDamage;
   }

   public void setTreeDamage(int var1) {
      this.treeDamage = var1;
   }

   public String getBulletOutSound() {
      return this.bulletOutSound;
   }

   public void setBulletOutSound(String var1) {
      this.bulletOutSound = var1;
   }

   public String getShellFallSound() {
      return this.shellFallSound;
   }

   public void setShellFallSound(String var1) {
      this.shellFallSound = var1;
   }

   public void setWeaponPart(String var1, WeaponPart var2) {
      if (var2 == null || var1.equals(var2.getPartType())) {
         if (var1.equals(Translator.getText("Tooltip_weapon_Scope"))) {
            this.scope = var2;
         } else if (var1.equals(Translator.getText("Tooltip_weapon_Clip"))) {
            this.clip = var2;
         } else if (var1.equals(Translator.getText("Tooltip_weapon_Sling"))) {
            this.sling = var2;
         } else if (var1.equals(Translator.getText("Tooltip_weapon_Canon"))) {
            this.canon = var2;
         } else if (var1.equals(Translator.getText("Tooltip_weapon_Stock"))) {
            this.stock = var2;
         } else if (var1.equals(Translator.getText("Tooltip_weapon_RecoilPad"))) {
            this.recoilpad = var2;
         } else {
            DebugLog.log("ERROR: unknown WeaponPart type \"" + var1 + "\"");
         }

      }
   }

   public WeaponPart getWeaponPart(String var1) {
      if (var1.equals(Translator.getText("Tooltip_weapon_Scope"))) {
         return this.scope;
      } else if (var1.equals(Translator.getText("Tooltip_weapon_Clip"))) {
         return this.clip;
      } else if (var1.equals(Translator.getText("Tooltip_weapon_Sling"))) {
         return this.sling;
      } else if (var1.equals(Translator.getText("Tooltip_weapon_Canon"))) {
         return this.canon;
      } else if (var1.equals(Translator.getText("Tooltip_weapon_Stock"))) {
         return this.stock;
      } else if (var1.equals(Translator.getText("Tooltip_weapon_RecoilPad"))) {
         return this.recoilpad;
      } else {
         DebugLog.log("ERROR: unknown WeaponPart type \"" + var1 + "\"");
         return null;
      }
   }

   public void attachWeaponPart(WeaponPart var1) {
      this.attachWeaponPart(var1, true);
   }

   public void attachWeaponPart(WeaponPart var1, boolean var2) {
      if (var1 != null) {
         WeaponPart var3 = this.getWeaponPart(var1.getPartType());
         if (var3 != null) {
            this.detachWeaponPart(var3);
         }

         this.setWeaponPart(var1.getPartType(), var1);
         if (var2) {
            this.setMaxRange(this.getMaxRange() + var1.getMaxRange());
            this.setMinRangeRanged(this.getMinRangeRanged() + var1.getMinRangeRanged());
            this.setClipSize(this.getClipSize() + var1.getClipSize());
            this.setReloadTime(this.getReloadTime() + var1.getReloadTime());
            this.setRecoilDelay((int)((float)this.getRecoilDelay() + var1.getRecoilDelay()));
            this.setAimingTime(this.getAimingTime() + var1.getAimingTime());
            this.setHitChance(this.getHitChance() + var1.getHitChance());
            this.setMinAngle(this.getMinAngle() + var1.getAngle());
            this.setActualWeight(this.getActualWeight() + var1.getWeightModifier());
            this.setWeight(this.getWeight() + var1.getWeightModifier());
            this.setMinDamage(this.getMinDamage() + var1.getDamage());
            this.setMaxDamage(this.getMaxDamage() + var1.getDamage());
         }

      }
   }

   public void detachWeaponPart(WeaponPart var1) {
      if (var1 != null) {
         WeaponPart var2 = this.getWeaponPart(var1.getPartType());
         if (var2 == var1) {
            this.setWeaponPart(var1.getPartType(), (WeaponPart)null);
            this.setMaxRange(this.getMaxRange() - var1.getMaxRange());
            this.setMinRangeRanged(this.getMinRangeRanged() - var1.getMinRangeRanged());
            this.setClipSize(this.getClipSize() - var1.getClipSize());
            this.setReloadTime(this.getReloadTime() - var1.getReloadTime());
            this.setRecoilDelay((int)((float)this.getRecoilDelay() - var1.getRecoilDelay()));
            this.setAimingTime(this.getAimingTime() - var1.getAimingTime());
            this.setHitChance(this.getHitChance() - var1.getHitChance());
            this.setMinAngle(this.getMinAngle() - var1.getAngle());
            this.setActualWeight(this.getActualWeight() - var1.getWeightModifier());
            this.setWeight(this.getWeight() - var1.getWeightModifier());
            this.setMinDamage(this.getMinDamage() - var1.getDamage());
            this.setMaxDamage(this.getMaxDamage() - var1.getDamage());
         }
      }
   }

   public int getTriggerExplosionTimer() {
      return this.triggerExplosionTimer;
   }

   public void setTriggerExplosionTimer(int var1) {
      this.triggerExplosionTimer = var1;
   }

   public boolean canBePlaced() {
      return this.canBePlaced;
   }

   public void setCanBePlaced(boolean var1) {
      this.canBePlaced = var1;
   }

   public int getExplosionRange() {
      return this.explosionRange;
   }

   public void setExplosionRange(int var1) {
      this.explosionRange = var1;
   }

   public int getExplosionPower() {
      return this.explosionPower;
   }

   public void setExplosionPower(int var1) {
      this.explosionPower = var1;
   }

   public int getFireRange() {
      return this.fireRange;
   }

   public void setFireRange(int var1) {
      this.fireRange = var1;
   }

   public int getSmokeRange() {
      return this.smokeRange;
   }

   public void setSmokeRange(int var1) {
      this.smokeRange = var1;
   }

   public int getFirePower() {
      return this.firePower;
   }

   public void setFirePower(int var1) {
      this.firePower = var1;
   }

   public int getNoiseRange() {
      return this.noiseRange;
   }

   public void setNoiseRange(int var1) {
      this.noiseRange = var1;
   }

   public float getExtraDamage() {
      return this.extraDamage;
   }

   public void setExtraDamage(float var1) {
      this.extraDamage = var1;
   }

   public int getExplosionTimer() {
      return this.explosionTimer;
   }

   public void setExplosionTimer(int var1) {
      this.explosionTimer = var1;
   }

   public String getPlacedSprite() {
      return this.placedSprite;
   }

   public void setPlacedSprite(String var1) {
      this.placedSprite = var1;
   }

   public boolean canBeReused() {
      return this.canBeReused;
   }

   public void setCanBeReused(boolean var1) {
      this.canBeReused = var1;
   }

   public int getSensorRange() {
      return this.sensorRange;
   }

   public void setSensorRange(int var1) {
      this.sensorRange = var1;
   }

   public String getRunAnim() {
      return this.RunAnim;
   }
}
