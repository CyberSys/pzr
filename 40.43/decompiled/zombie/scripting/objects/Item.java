package zombie.scripting.objects;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.inventory.types.KeyRing;
import zombie.inventory.types.Literature;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.inventory.types.WeaponPart;
import zombie.radio.devices.DeviceData;
import zombie.scripting.ScriptManager;

public class Item extends BaseScriptObject {
   public String DisplayName = "Blooo";
   public String Icon = "None";
   public boolean ResizeWorldIcon = true;
   public float ScaleWorldIcon = 1.0F;
   public String CloseKillMove = null;
   public float ActualWeight = 1.0F;
   public float HungerChange = 0.0F;
   public float CriticalChance = 20.0F;
   public int Count = 1;
   public int DaysFresh = 1000000000;
   public int DaysTotallyRotten = 1000000000;
   public int MinutesToCook = 60;
   public int MinutesToBurn = 120;
   public boolean IsCookable = false;
   public float StressChange = 0.0F;
   public float BoredomChange = 0.0F;
   public float UnhappyChange = 0.0F;
   public boolean AlwaysWelcomeGift = false;
   public String ReplaceOnDeplete = null;
   public boolean Ranged = false;
   public boolean CanStoreWater = false;
   public float MaxRange = 1.0F;
   public float MinRange = 0.0F;
   public float ThistChange = 0.0F;
   public float FatigueChange = 0.0F;
   public float MinAngle = 1.0F;
   public boolean RequiresEquippedBothHands = false;
   public float MaxDamage = 1.5F;
   public float MinDamage = 0.0F;
   public float MinimumSwingTime = 0.0F;
   public String SwingSound = "BatSwing";
   public String WeaponSprite;
   public boolean AngleFalloff = false;
   public int SoundVolume = 0;
   public float ToHitModifier = 1.0F;
   public int SoundRadius = 0;
   public float OtherCharacterVolumeBoost;
   public ArrayList Categories = new ArrayList();
   public String ImpactSound = "ZombieImpact";
   public float SwingTime = 1.0F;
   public boolean KnockBackOnNoDeath = true;
   public boolean SplatBloodOnNoDeath = false;
   public float SwingAmountBeforeImpact = 0.0F;
   public String AmmoType = null;
   public int DoorDamage = 1;
   public int ConditionLowerChance = 1000000;
   public int ConditionMax = 100;
   public boolean CanBandage = false;
   public String name;
   public int MaxHitCount = 1000;
   public boolean UseSelf = false;
   public boolean OtherHandUse = false;
   public String OtherHandRequire;
   public String PhysicsObject;
   public String SwingAnim = "Rifle";
   public float WeaponWeight = 1.0F;
   public float EnduranceChange = 0.0F;
   public String IdleAnim = "Idle";
   public String RunAnim = "Run";
   public ArrayList RequireInHandOrInventory = null;
   public String DoorHitSound = "ChopDoor";
   public String ReplaceOnUse = null;
   public boolean DangerousUncooked = false;
   public boolean Alcoholic = false;
   public float PushBackMod = 1.0F;
   public int SplatNumber = 2;
   public float NPCSoundBoost = 1.0F;
   public boolean RangeFalloff = false;
   public boolean UseEndurance = true;
   public boolean MultipleHitConditionAffected = true;
   public boolean ShareDamage = true;
   public boolean ShareEndurance = false;
   public boolean CanBarricade = false;
   public boolean UseWhileEquipped = true;
   public boolean DisappearOnUse = true;
   public float UseDelta = 0.03125F;
   public boolean AlwaysKnockdown = false;
   public float EnduranceMod = 1.0F;
   public float KnockdownMod = 1.0F;
   public boolean CantAttackWithLowestEndurance = false;
   public String ReplaceOnUseOn = null;
   public boolean IsWaterSource = false;
   public String FoodType = null;
   public boolean Poison = false;
   public Integer PoisonDetectionLevel = null;
   public int PoisonPower = 0;
   public KahluaTable DefaultModData = null;
   public boolean IsAimedFirearm = false;
   public boolean IsAimedHandWeapon = false;
   public boolean CanStack = true;
   public float AimingMod = 1.0F;
   public int ProjectileCount = 1;
   public float HitAngleMod = 0.0F;
   public float SplatSize = 1.0F;
   public float Temperature = 0.0F;
   public int NumberOfPages = -1;
   public int LvlSkillTrained = -1;
   public int NumLevelsTrained = 1;
   public String SkillTrained = "";
   public int Capacity = 0;
   public int WeightReduction = 0;
   public String SubCategory = "";
   public boolean ActivatedItem = false;
   public float LightStrength = 0.0F;
   public boolean TorchCone = false;
   public int LightDistance = 0;
   public String CanBeEquipped = "";
   public boolean TwoHandWeapon = false;
   public String CustomContextMenu = null;
   public String Tooltip = null;
   public List ReplaceOnCooked = null;
   public String DisplayCategory = null;
   public Boolean Trap = false;
   public boolean OBSOLETE = false;
   public boolean FishingLure = false;
   public boolean canBeWrite = false;
   public int AimingPerkCritModifier = 0;
   public float AimingPerkRangeModifier = 0.0F;
   public float AimingPerkHitChanceModifier = 0.0F;
   public int HitChance = 0;
   public float AimingPerkMinAngleModifier = 0.0F;
   public int RecoilDelay = 0;
   public boolean PiercingBullets = false;
   public float SoundGain = 1.0F;
   private float maxRangeModifier = 0.0F;
   private float minRangeRangedModifier = 0.0F;
   private float damageModifier = 0.0F;
   private float recoilDelayModifier = 0.0F;
   private int clipSizeModifier = 0;
   private ArrayList mountOn = null;
   private String partType = null;
   private int ClipSize = 0;
   private int reloadTime = 0;
   private int reloadTimeModifier = 0;
   private int aimingTime = 0;
   private int aimingTimeModifier = 0;
   private int hitChanceModifier = 0;
   private float angleModifier = 0.0F;
   private float weightModifier = 0.0F;
   private int PageToWrite = 0;
   private boolean RemoveNegativeEffectOnCooked = false;
   private int treeDamage = 0;
   private float alcoholPower = 0.0F;
   private String PutInSound = null;
   private String OpenSound = null;
   private String CloseSound = null;
   private String breakSound = null;
   private String customEatSound = null;
   private String bulletOutSound = null;
   private String ShellFallSound = null;
   private float bandagePower = 0.0F;
   private float ReduceInfectionPower = 0.0F;
   private String OnCooked = null;
   private String OnlyAcceptCategory = null;
   private boolean padlock = false;
   private boolean digitalPadlock = false;
   private List teachedRecipes = null;
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
   private String PlacedSprite = null;
   private boolean canBeReused = false;
   private int sensorRange = 0;
   private boolean canBeRemote = false;
   private boolean remoteController = false;
   private int remoteRange = 0;
   private String countDownSound = null;
   private String explosionSound = null;
   private int fluReduction = 0;
   private int ReduceFoodSickness = 0;
   private int painReduction = 0;
   private float rainFactor = 0.0F;
   public int colorRed = 255;
   public int colorGreen = 255;
   public int colorBlue = 255;
   public boolean twoWay = false;
   public int transmitRange = 0;
   public int micRange = 0;
   public float baseVolumeRange = 0.0F;
   public boolean isPortable = false;
   public boolean isTelevision = false;
   public int minChannel = 88000;
   public int maxChannel = 108000;
   public boolean usesBattery = false;
   public boolean isHighTier = false;
   public String HerbalistType;
   private float carbohydrates = 0.0F;
   private float lipids = 0.0F;
   private float proteins = 0.0F;
   private float calories = 0.0F;
   private boolean packaged = false;
   private boolean cantBeFrozen = false;
   private String evolvedRecipeName = null;
   private String ReplaceOnRotten = null;
   private float metalValue = 0.0F;
   private String AlarmSound = null;
   private String itemWhenDry = null;
   private float wetCooldown = 0.0F;
   private boolean isWet = false;
   private String onEat = null;
   private boolean cantBeConsolided = false;
   private boolean BadInMicrowave = false;
   private boolean GoodHot = false;
   private boolean BadCold = false;
   private String map = null;
   private boolean keepOnDeplete = false;
   private int vehicleType = 0;
   private int maxCapacity = -1;
   private int itemCapacity = -1;
   private boolean ConditionAffectsCapacity = false;
   private float brakeForce = 0.0F;
   private int chanceToSpawnDamaged = 0;
   private float conditionLowerNormal = 0.0F;
   private float conditionLowerOffroad = 0.0F;
   private float wheelFriction = 0.0F;
   private float suspensionDamping = 0.0F;
   private float suspensionCompression = 0.0F;
   private float engineLoudness = 0.0F;
   private String worldObjectSprite = null;
   public String ItemName;
   public Texture NormalTexture;
   public List SpecialTextures = new ArrayList();
   public List SpecialWorldTextureNames = new ArrayList();
   public String WorldTextureName;
   public Texture WorldTexture;
   private float insulation = 0.0F;
   public String HitSound = "BatHit";
   public Item.ClothingBodyLocation bodyLocation;
   public Stack PaletteChoices;
   public String SpriteName;
   public String PalettesStart;
   public static HashMap NetIDToItem = new HashMap();
   public static HashMap NetItemToID = new HashMap();
   static int IDMax = 0;
   public Item.Type type;
   private boolean Spice;
   private int UseForPoison;

   public Item() {
      this.bodyLocation = Item.ClothingBodyLocation.None;
      this.PaletteChoices = new Stack();
      this.SpriteName = null;
      this.PalettesStart = "";
      this.type = Item.Type.Normal;
      this.Spice = false;
   }

   public String getDisplayName() {
      return this.DisplayName;
   }

   public void setDisplayName(String var1) {
      this.DisplayName = var1;
   }

   public String getDisplayCategory() {
      return this.DisplayCategory;
   }

   public String getIcon() {
      return this.Icon;
   }

   public void setIcon(String var1) {
      this.Icon = var1;
   }

   public float getActualWeight() {
      return this.ActualWeight;
   }

   public void setActualWeight(float var1) {
      this.ActualWeight = var1;
   }

   public float getHungerChange() {
      return this.HungerChange;
   }

   public void setHungerChange(float var1) {
      this.HungerChange = var1;
   }

   public int getCount() {
      return this.Count;
   }

   public void setCount(int var1) {
      this.Count = var1;
   }

   public int getDaysFresh() {
      return this.DaysFresh;
   }

   public void setDaysFresh(int var1) {
      this.DaysFresh = var1;
   }

   public int getDaysTotallyRotten() {
      return this.DaysTotallyRotten;
   }

   public void setDaysTotallyRotten(int var1) {
      this.DaysTotallyRotten = var1;
   }

   public int getMinutesToCook() {
      return this.MinutesToCook;
   }

   public void setMinutesToCook(int var1) {
      this.MinutesToCook = var1;
   }

   public int getMinutesToBurn() {
      return this.MinutesToBurn;
   }

   public void setMinutesToBurn(int var1) {
      this.MinutesToBurn = var1;
   }

   public boolean isIsCookable() {
      return this.IsCookable;
   }

   public void setIsCookable(boolean var1) {
      this.IsCookable = var1;
   }

   public float getStressChange() {
      return this.StressChange;
   }

   public void setStressChange(float var1) {
      this.StressChange = var1;
   }

   public float getBoredomChange() {
      return this.BoredomChange;
   }

   public void setBoredomChange(float var1) {
      this.BoredomChange = var1;
   }

   public float getUnhappyChange() {
      return this.UnhappyChange;
   }

   public void setUnhappyChange(float var1) {
      this.UnhappyChange = var1;
   }

   public boolean isAlwaysWelcomeGift() {
      return this.AlwaysWelcomeGift;
   }

   public void setAlwaysWelcomeGift(boolean var1) {
      this.AlwaysWelcomeGift = var1;
   }

   public boolean isRanged() {
      return this.Ranged;
   }

   public boolean getCanStoreWater() {
      return this.CanStoreWater;
   }

   public void setRanged(boolean var1) {
      this.Ranged = var1;
   }

   public float getMaxRange() {
      return this.MaxRange;
   }

   public void setMaxRange(float var1) {
      this.MaxRange = var1;
   }

   public float getMinAngle() {
      return this.MinAngle;
   }

   public void setMinAngle(float var1) {
      this.MinAngle = var1;
   }

   public float getMaxDamage() {
      return this.MaxDamage;
   }

   public void setMaxDamage(float var1) {
      this.MaxDamage = var1;
   }

   public float getMinDamage() {
      return this.MinDamage;
   }

   public void setMinDamage(float var1) {
      this.MinDamage = var1;
   }

   public float getMinimumSwingTime() {
      return this.MinimumSwingTime;
   }

   public void setMinimumSwingTime(float var1) {
      this.MinimumSwingTime = var1;
   }

   public String getSwingSound() {
      return this.SwingSound;
   }

   public void setSwingSound(String var1) {
      this.SwingSound = var1;
   }

   public String getWeaponSprite() {
      return this.WeaponSprite;
   }

   public void setWeaponSprite(String var1) {
      this.WeaponSprite = var1;
   }

   public boolean isAngleFalloff() {
      return this.AngleFalloff;
   }

   public void setAngleFalloff(boolean var1) {
      this.AngleFalloff = var1;
   }

   public int getSoundVolume() {
      return this.SoundVolume;
   }

   public void setSoundVolume(int var1) {
      this.SoundVolume = var1;
   }

   public float getToHitModifier() {
      return this.ToHitModifier;
   }

   public void setToHitModifier(float var1) {
      this.ToHitModifier = var1;
   }

   public int getSoundRadius() {
      return this.SoundRadius;
   }

   public void setSoundRadius(int var1) {
      this.SoundRadius = var1;
   }

   public float getOtherCharacterVolumeBoost() {
      return this.OtherCharacterVolumeBoost;
   }

   public void setOtherCharacterVolumeBoost(float var1) {
      this.OtherCharacterVolumeBoost = var1;
   }

   public ArrayList getCategories() {
      return this.Categories;
   }

   public void setCategories(ArrayList var1) {
      this.Categories = var1;
   }

   public String getImpactSound() {
      return this.ImpactSound;
   }

   public void setImpactSound(String var1) {
      this.ImpactSound = var1;
   }

   public float getSwingTime() {
      return this.SwingTime;
   }

   public void setSwingTime(float var1) {
      this.SwingTime = var1;
   }

   public boolean isKnockBackOnNoDeath() {
      return this.KnockBackOnNoDeath;
   }

   public void setKnockBackOnNoDeath(boolean var1) {
      this.KnockBackOnNoDeath = var1;
   }

   public boolean isSplatBloodOnNoDeath() {
      return this.SplatBloodOnNoDeath;
   }

   public void setSplatBloodOnNoDeath(boolean var1) {
      this.SplatBloodOnNoDeath = var1;
   }

   public float getSwingAmountBeforeImpact() {
      return this.SwingAmountBeforeImpact;
   }

   public void setSwingAmountBeforeImpact(float var1) {
      this.SwingAmountBeforeImpact = var1;
   }

   public String getAmmoType() {
      return this.AmmoType;
   }

   public void setAmmoType(String var1) {
      this.AmmoType = var1;
   }

   public int getDoorDamage() {
      return this.DoorDamage;
   }

   public void setDoorDamage(int var1) {
      this.DoorDamage = var1;
   }

   public int getConditionLowerChance() {
      return this.ConditionLowerChance;
   }

   public void setConditionLowerChance(int var1) {
      this.ConditionLowerChance = var1;
   }

   public int getConditionMax() {
      return this.ConditionMax;
   }

   public void setConditionMax(int var1) {
      this.ConditionMax = var1;
   }

   public boolean isCanBandage() {
      return this.CanBandage;
   }

   public void setCanBandage(boolean var1) {
      this.CanBandage = var1;
   }

   public String getName() {
      return this.name;
   }

   public String getModuleName() {
      return this.module.name;
   }

   public String getFullName() {
      return this.module.name + "." + this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public int getMaxHitCount() {
      return this.MaxHitCount;
   }

   public void setMaxHitCount(int var1) {
      this.MaxHitCount = var1;
   }

   public boolean isUseSelf() {
      return this.UseSelf;
   }

   public void setUseSelf(boolean var1) {
      this.UseSelf = var1;
   }

   public boolean isOtherHandUse() {
      return this.OtherHandUse;
   }

   public void setOtherHandUse(boolean var1) {
      this.OtherHandUse = var1;
   }

   public String getOtherHandRequire() {
      return this.OtherHandRequire;
   }

   public void setOtherHandRequire(String var1) {
      this.OtherHandRequire = var1;
   }

   public String getPhysicsObject() {
      return this.PhysicsObject;
   }

   public void setPhysicsObject(String var1) {
      this.PhysicsObject = var1;
   }

   public String getSwingAnim() {
      return this.SwingAnim;
   }

   public void setSwingAnim(String var1) {
      this.SwingAnim = var1;
   }

   public float getWeaponWeight() {
      return this.WeaponWeight;
   }

   public void setWeaponWeight(float var1) {
      this.WeaponWeight = var1;
   }

   public float getEnduranceChange() {
      return this.EnduranceChange;
   }

   public void setEnduranceChange(float var1) {
      this.EnduranceChange = var1;
   }

   public String getBreakSound() {
      return this.breakSound;
   }

   public String getBulletOutSound() {
      return this.bulletOutSound;
   }

   public String getCloseSound() {
      return this.CloseSound;
   }

   public String getCustomEatSound() {
      return this.customEatSound;
   }

   public String getCountDownSound() {
      return this.countDownSound;
   }

   public String getExplosionSound() {
      return this.explosionSound;
   }

   public String getOpenSound() {
      return this.OpenSound;
   }

   public String getPutInSound() {
      return this.PutInSound;
   }

   public String getShellFallSound() {
      return this.ShellFallSound;
   }

   public String getDoorHitSound() {
      return this.DoorHitSound;
   }

   public void setDoorHitSound(String var1) {
      this.DoorHitSound = var1;
   }

   public String getReplaceOnUse() {
      return this.ReplaceOnUse;
   }

   public void setReplaceOnUse(String var1) {
      this.ReplaceOnUse = var1;
   }

   public boolean isDangerousUncooked() {
      return this.DangerousUncooked;
   }

   public void setDangerousUncooked(boolean var1) {
      this.DangerousUncooked = var1;
   }

   public boolean isAlcoholic() {
      return this.Alcoholic;
   }

   public void setAlcoholic(boolean var1) {
      this.Alcoholic = var1;
   }

   public float getPushBackMod() {
      return this.PushBackMod;
   }

   public void setPushBackMod(float var1) {
      this.PushBackMod = var1;
   }

   public int getSplatNumber() {
      return this.SplatNumber;
   }

   public void setSplatNumber(int var1) {
      this.SplatNumber = var1;
   }

   public float getNPCSoundBoost() {
      return this.NPCSoundBoost;
   }

   public void setNPCSoundBoost(float var1) {
      this.NPCSoundBoost = var1;
   }

   public boolean isRangeFalloff() {
      return this.RangeFalloff;
   }

   public void setRangeFalloff(boolean var1) {
      this.RangeFalloff = var1;
   }

   public boolean isUseEndurance() {
      return this.UseEndurance;
   }

   public void setUseEndurance(boolean var1) {
      this.UseEndurance = var1;
   }

   public boolean isMultipleHitConditionAffected() {
      return this.MultipleHitConditionAffected;
   }

   public void setMultipleHitConditionAffected(boolean var1) {
      this.MultipleHitConditionAffected = var1;
   }

   public boolean isShareDamage() {
      return this.ShareDamage;
   }

   public void setShareDamage(boolean var1) {
      this.ShareDamage = var1;
   }

   public boolean isShareEndurance() {
      return this.ShareEndurance;
   }

   public void setShareEndurance(boolean var1) {
      this.ShareEndurance = var1;
   }

   public boolean isCanBarricade() {
      return this.CanBarricade;
   }

   public void setCanBarricade(boolean var1) {
      this.CanBarricade = var1;
   }

   public boolean isUseWhileEquipped() {
      return this.UseWhileEquipped;
   }

   public void setUseWhileEquipped(boolean var1) {
      this.UseWhileEquipped = var1;
   }

   public boolean isDisappearOnUse() {
      return this.DisappearOnUse;
   }

   public void setDisappearOnUse(boolean var1) {
      this.DisappearOnUse = var1;
   }

   public float getUseDelta() {
      return this.UseDelta;
   }

   public void setUseDelta(float var1) {
      this.UseDelta = var1;
   }

   public void setUseDelta(String var1) {
      this.HitSound = var1;
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

   public Item.ClothingBodyLocation getBodyLocation() {
      return this.bodyLocation;
   }

   public void setBodyLocation(Item.ClothingBodyLocation var1) {
      this.bodyLocation = var1;
   }

   public Stack getPaletteChoices() {
      return this.PaletteChoices;
   }

   public void setPaletteChoices(Stack var1) {
      this.PaletteChoices = var1;
   }

   public String getSpriteName() {
      return this.SpriteName;
   }

   public void setSpriteName(String var1) {
      this.SpriteName = var1;
   }

   public String getPalettesStart() {
      return this.PalettesStart;
   }

   public void setPalettesStart(String var1) {
      this.PalettesStart = var1;
   }

   public Item.Type getType() {
      return this.type;
   }

   public void setType(Item.Type var1) {
      this.type = var1;
   }

   public String getTypeString() {
      return this.type.name();
   }

   public void Load(String var1, String[] var2) {
      this.name = var1;
      int var3 = IDMax++;
      NetIDToItem.put(var3, this.module.name + "." + var1);
      NetItemToID.put(this.module.name + "." + var1, var3);
      String[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         this.DoParam(var7);
      }

      if (this.type == Item.Type.Clothing) {
         for(int var8 = 0; var8 < this.PaletteChoices.size(); ++var8) {
            this.InstanceItem((String)this.PaletteChoices.get(var8));
         }

         if (this.PaletteChoices.isEmpty()) {
            this.InstanceItem((String)null);
         }
      }

   }

   public InventoryItem InstanceItem(String var1) {
      Object var2 = null;
      if (this.type == Item.Type.Key) {
         var2 = new Key(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
         ((Key)var2).setDigitalPadlock(this.digitalPadlock);
         ((Key)var2).setPadlock(this.padlock);
         if (((Key)var2).isPadlock()) {
            ((Key)var2).setNumberOfKey(2);
            ((Key)var2).setKeyId(Rand.Next(10000000));
         }
      }

      if (this.type == Item.Type.KeyRing) {
         var2 = new KeyRing(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
      }

      if (this.type == Item.Type.WeaponPart) {
         var2 = new WeaponPart(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
         WeaponPart var3 = (WeaponPart)var2;
         var3.setDamage(this.damageModifier);
         var3.setClipSize(this.clipSizeModifier);
         var3.setMaxRange(this.maxRangeModifier);
         var3.setMinRangeRanged(this.minRangeRangedModifier);
         var3.setRecoilDelay(this.recoilDelayModifier);
         var3.setMountOn(this.mountOn);
         var3.setPartType(this.partType);
         var3.setReloadTime(this.reloadTimeModifier);
         var3.setAimingTime(this.aimingTimeModifier);
         var3.setHitChance(this.hitChanceModifier);
         var3.setAngle(this.angleModifier);
         var3.setWeightModifier(this.weightModifier);
      }

      if (this.type == Item.Type.Container) {
         var2 = new InventoryContainer(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
         InventoryContainer var6 = (InventoryContainer)var2;
         var6.setItemCapacity((float)this.Capacity);
         var6.setCapacity(this.Capacity);
         var6.setWeightReduction(this.WeightReduction);
         var6.setCanBeEquipped(this.CanBeEquipped);
         var6.getInventory().setPutSound(this.PutInSound);
         var6.getInventory().setCloseSound(this.CloseSound);
         var6.getInventory().setOpenSound(this.OpenSound);
         var6.getInventory().setOnlyAcceptCategory(this.OnlyAcceptCategory);
      }

      if (this.type == Item.Type.Food) {
         var2 = new Food(this.module.name, this.DisplayName, this.name, this);
         Food var7 = (Food)var2;
         var7.Poison = this.Poison;
         var7.setPoisonLevelForRecipe(this.PoisonDetectionLevel);
         var7.setFoodType(this.FoodType);
         var7.setPoisonPower(this.PoisonPower);
         var7.setUseForPoison(this.UseForPoison);
         var7.setThirstChange(this.ThistChange / 100.0F);
         var7.setHungChange(this.HungerChange / 100.0F);
         var7.setBaseHunger(this.HungerChange / 100.0F);
         var7.setEndChange(this.EnduranceChange / 100.0F);
         var7.setOffAge(this.DaysFresh);
         var7.setOffAgeMax(this.DaysTotallyRotten);
         var7.setIsCookable(this.IsCookable);
         var7.setMinutesToCook((float)this.MinutesToCook);
         var7.setMinutesToBurn((float)this.MinutesToBurn);
         var7.setbDangerousUncooked(this.DangerousUncooked);
         var7.setReplaceOnUse(this.ReplaceOnUse);
         var7.setReplaceOnCooked(this.ReplaceOnCooked);
         var7.setSpice(this.Spice);
         var7.setRemoveNegativeEffectOnCooked(this.RemoveNegativeEffectOnCooked);
         var7.setCustomEatSound(this.customEatSound);
         var7.setOnCooked(this.OnCooked);
         var7.setFluReduction(this.fluReduction);
         var7.setReduceFoodSickness(this.ReduceFoodSickness);
         var7.setPainReduction((float)this.painReduction);
         var7.setHerbalistType(this.HerbalistType);
         var7.setCarbohydrates(this.carbohydrates);
         var7.setLipids(this.lipids);
         var7.setProteins(this.proteins);
         var7.setCalories(this.calories);
         var7.setPackaged(this.packaged);
         var7.setCanBeFrozen(!this.cantBeFrozen);
         var7.setReplaceOnRotten(this.ReplaceOnRotten);
         var7.setOnEat(this.onEat);
         var7.setBadInMicrowave(this.BadInMicrowave);
         var7.setGoodHot(this.GoodHot);
         var7.setBadCold(this.BadCold);
      }

      if (this.type == Item.Type.Literature) {
         var2 = new Literature(this.module.name, this.DisplayName, this.name, this);
         Literature var8 = (Literature)var2;
         var8.setReplaceOnUse(this.ReplaceOnUse);
         var8.setNumberOfPages(this.NumberOfPages);
         var8.setAlreadyReadPages(0);
         var8.setSkillTrained(this.SkillTrained);
         var8.setLvlSkillTrained(this.LvlSkillTrained);
         var8.setNumLevelsTrained(this.NumLevelsTrained);
         var8.setCanBeWrite(this.canBeWrite);
         var8.setPageToWrite(this.PageToWrite);
         var8.setTeachedRecipes(this.teachedRecipes);
      } else if (this.type == Item.Type.AlarmClock) {
         var2 = new AlarmClock(this.module.name, this.DisplayName, this.name, this);
         AlarmClock var9 = (AlarmClock)var2;
         var9.setAlarmSound(this.AlarmSound);
         var9.setSoundRadius(this.SoundRadius);
      } else if (this.type == Item.Type.Weapon) {
         var2 = new HandWeapon(this.module.name, this.DisplayName, this.name, this);
         HandWeapon var10 = (HandWeapon)var2;
         var10.setMultipleHitConditionAffected(this.MultipleHitConditionAffected);
         var10.setConditionLowerChance(this.ConditionLowerChance);
         var10.SplatSize = this.SplatSize;
         var10.aimingMod = this.AimingMod;
         var10.ProjectileCount = this.ProjectileCount;
         var10.setMinDamage(this.MinDamage);
         var10.setMaxDamage(this.MaxDamage);
         var10.setPhysicsObject(this.PhysicsObject);
         var10.setOtherHandRequire(this.OtherHandRequire);
         var10.setOtherHandUse(this.OtherHandUse);
         var10.setMaxRange(this.MaxRange);
         var10.setMinRange(this.MinRange);
         var10.setShareEndurance(this.ShareEndurance);
         var10.setKnockdownMod(this.KnockdownMod);
         var10.bIsAimedFirearm = this.IsAimedFirearm;
         var10.RunAnim = this.RunAnim;
         var10.IdleAnim = this.IdleAnim;
         var10.HitAngleMod = (float)Math.toRadians((double)this.HitAngleMod);
         var10.bIsAimedHandWeapon = this.IsAimedHandWeapon;
         var10.setCantAttackWithLowestEndurance(this.CantAttackWithLowestEndurance);
         var10.setAlwaysKnockdown(this.AlwaysKnockdown);
         var10.setEnduranceMod(this.EnduranceMod);
         var10.setUseSelf(this.UseSelf);
         var10.setSwingAnim(this.SwingAnim);
         var10.setMaxHitCount(this.MaxHitCount);
         var10.setMinimumSwingTime(this.MinimumSwingTime);
         var10.setSwingTime(this.SwingTime);
         var10.setSwingAnim(this.SwingAnim);
         var10.setDoSwingBeforeImpact(this.SwingAmountBeforeImpact);
         var10.setMinAngle(this.MinAngle);
         var10.setWeaponSprite(this.WeaponSprite);
         var10.setDoorDamage(this.DoorDamage);
         var10.setTreeDamage(this.treeDamage);
         var10.setDoorHitSound(this.DoorHitSound);
         var10.setZombieHitSound(this.HitSound);
         var10.setPushBackMod(this.PushBackMod);
         var10.setWeight(this.WeaponWeight);
         var10.setImpactSound(this.ImpactSound);
         var10.setSplatNumber(this.SplatNumber);
         var10.setKnockBackOnNoDeath(this.KnockBackOnNoDeath);
         var10.setSplatBloodOnNoDeath(this.SplatBloodOnNoDeath);
         var10.setSwingSound(this.SwingSound);
         var10.setBulletOutSound(this.bulletOutSound);
         var10.setShellFallSound(this.ShellFallSound);
         var10.setAngleFalloff(this.AngleFalloff);
         var10.setSoundVolume(this.SoundVolume);
         var10.setSoundRadius(this.SoundRadius);
         var10.setToHitModifier(this.ToHitModifier);
         var10.setOtherBoost(this.NPCSoundBoost);
         var10.setRanged(this.Ranged);
         var10.setRangeFalloff(this.RangeFalloff);
         var10.setUseEndurance(this.UseEndurance);
         var10.setCriticalChance(this.CriticalChance);
         var10.setShareDamage(this.ShareDamage);
         var10.setAmmoType(this.AmmoType);
         var10.setCanBarracade(this.CanBarricade);
         var10.setWeaponSprite(this.WeaponSprite);
         var10.setSubCategory(this.SubCategory);
         var10.setCategories(this.Categories);
         var10.setSoundGain(this.SoundGain);
         var10.setAimingPerkCritModifier(this.AimingPerkCritModifier);
         var10.setAimingPerkRangeModifier(this.AimingPerkRangeModifier);
         var10.setAimingPerkHitChanceModifier(this.AimingPerkHitChanceModifier);
         var10.setHitChance(this.HitChance);
         var10.setRecoilDelay(this.RecoilDelay);
         var10.setAimingPerkMinAngleModifier(this.AimingPerkMinAngleModifier);
         var10.setPiercingBullets(this.PiercingBullets);
         var10.setClipSize(this.ClipSize);
         var10.setReloadTime(this.reloadTime);
         var10.setAimingTime(this.aimingTime);
         var10.setTriggerExplosionTimer(this.triggerExplosionTimer);
         var10.setSensorRange(this.sensorRange);
         var10.setPlacedSprite(this.PlacedSprite);
         var10.setExplosionTimer(this.explosionTimer);
         var10.setCanBePlaced(this.canBePlaced);
         var10.setCanBeReused(this.canBeReused);
         var10.setExplosionRange(this.explosionRange);
         var10.setExplosionPower(this.explosionPower);
         var10.setFireRange(this.fireRange);
         var10.setFirePower(this.firePower);
         var10.setSmokeRange(this.smokeRange);
         var10.setNoiseRange(this.noiseRange);
         var10.setExtraDamage(this.extraDamage);
      } else if (this.type == Item.Type.Normal) {
         var2 = new ComboItem(this.module.name, this.DisplayName, this.name, this);
      } else if (this.type == Item.Type.Clothing) {
         String var11 = "";
         String var4 = null;
         if (!this.PaletteChoices.isEmpty() || var1 != null) {
            int var5 = Rand.Next(this.PaletteChoices.size());
            var4 = (String)this.PaletteChoices.get(var5);
            if (var1 != null) {
               var4 = var1;
            }

            var11 = "_" + var4.replace(this.PalettesStart, "");
         }

         var2 = new Clothing(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon.replace(".png", "") + var11, var4, this.SpriteName);
         ((Clothing)var2).setBodyLocation(this.bodyLocation);
         ((Clothing)var2).setTemperature(this.Temperature);
         ((Clothing)var2).setInsulation(this.insulation);
         ((Clothing)var2).setConditionLowerChance(this.ConditionLowerChance);
      } else if (this.type == Item.Type.Drainable) {
         var2 = new DrainableComboItem(this.module.name, this.DisplayName, this.name, this);
         DrainableComboItem var13 = (DrainableComboItem)var2;
         var13.setUseWhileEquiped(this.UseWhileEquipped);
         var13.setDisappearOnUse(this.DisappearOnUse);
         var13.setUseDelta(this.UseDelta);
         var13.setReplaceOnDeplete(this.ReplaceOnDeplete);
         var13.setIsCookable(this.IsCookable);
         var13.setRainFactor(this.rainFactor);
         var13.setCanConsolidate(!this.cantBeConsolided);
      } else if (this.type == Item.Type.Radio) {
         var2 = new Radio(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
         Radio var14 = (Radio)var2;
         DeviceData var12 = var14.getDeviceData();
         if (var12 != null) {
            if (this.DisplayName != null) {
               var12.setDeviceName(this.DisplayName);
            }

            var12.setIsTwoWay(this.twoWay);
            var12.setTransmitRange(this.transmitRange);
            var12.setMicRange(this.micRange);
            var12.setBaseVolumeRange(this.baseVolumeRange);
            var12.setIsPortable(this.isPortable);
            var12.setIsTelevision(this.isTelevision);
            var12.setMinChannelRange(this.minChannel);
            var12.setMaxChannelRange(this.maxChannel);
            var12.setIsBatteryPowered(this.usesBattery);
            var12.setIsHighTier(this.isHighTier);
            var12.setUseDelta(this.UseDelta);
            var12.generatePresets();
            var12.setRandomChannel();
         }

         var14.ReadFromWorldSprite(this.worldObjectSprite);
      } else if (this.type == Item.Type.Moveable) {
         var2 = new Moveable(this.module.name, this.DisplayName, this.name, this);
         Moveable var15 = (Moveable)var2;
         var15.ReadFromWorldSprite(this.worldObjectSprite);
         this.ActualWeight = var15.getActualWeight();
      }

      if (this.colorRed < 255 || this.colorGreen < 255 || this.colorBlue < 255) {
         ((InventoryItem)var2).setColor(new Color((float)this.colorRed / 255.0F, (float)this.colorGreen / 255.0F, (float)this.colorBlue / 255.0F));
      }

      ((InventoryItem)var2).setAlcoholPower(this.alcoholPower);
      ((InventoryItem)var2).setCondition(this.ConditionMax);
      ((InventoryItem)var2).setCanBeActivated(this.ActivatedItem);
      ((InventoryItem)var2).setLightStrength(this.LightStrength);
      ((InventoryItem)var2).setTorchCone(this.TorchCone);
      ((InventoryItem)var2).setLightDistance(this.LightDistance);
      ((InventoryItem)var2).setConditionMax(this.ConditionMax);
      ((InventoryItem)var2).setActualWeight(this.ActualWeight);
      ((InventoryItem)var2).setWeight(this.ActualWeight);
      ((InventoryItem)var2).setUses(this.Count);
      ((InventoryItem)var2).setAlwaysWelcomeGift(this.AlwaysWelcomeGift);
      ((InventoryItem)var2).setCanBandage(this.CanBandage);
      ((InventoryItem)var2).setScriptItem(this);
      ((InventoryItem)var2).setBoredomChange(this.BoredomChange);
      ((InventoryItem)var2).setStressChange(this.StressChange / 100.0F);
      ((InventoryItem)var2).setUnhappyChange(this.UnhappyChange);
      ((InventoryItem)var2).setReplaceOnUseOn(this.ReplaceOnUseOn);
      ((InventoryItem)var2).setRequireInHandOrInventory(this.RequireInHandOrInventory);
      ((InventoryItem)var2).setIsWaterSource(this.IsWaterSource);
      ((InventoryItem)var2).CanStoreWater = this.CanStoreWater;
      ((InventoryItem)var2).CanStack = this.CanStack;
      ((InventoryItem)var2).CopyModData(this.DefaultModData);
      ((InventoryItem)var2).setCount(this.Count);
      ((InventoryItem)var2).setFatigueChange(this.FatigueChange / 100.0F);
      ((InventoryItem)var2).setTwoHandWeapon(this.TwoHandWeapon);
      ((InventoryItem)var2).setTooltip(this.Tooltip);
      ((InventoryItem)var2).setDisplayCategory(this.DisplayCategory);
      ((InventoryItem)var2).setAlcoholic(this.Alcoholic);
      ((InventoryItem)var2).RequiresEquippedBothHands = this.RequiresEquippedBothHands;
      ((InventoryItem)var2).setTrap(this.Trap);
      ((InventoryItem)var2).setFishingLure(this.FishingLure);
      ((InventoryItem)var2).setBreakSound(this.breakSound);
      ((InventoryItem)var2).setReplaceOnUse(this.ReplaceOnUse);
      ((InventoryItem)var2).setBandagePower(this.bandagePower);
      ((InventoryItem)var2).setReduceInfectionPower(this.ReduceInfectionPower);
      ((InventoryItem)var2).setCanBeRemote(this.canBeRemote);
      ((InventoryItem)var2).setRemoteController(this.remoteController);
      ((InventoryItem)var2).setRemoteRange(this.remoteRange);
      ((InventoryItem)var2).setCountDownSound(this.countDownSound);
      ((InventoryItem)var2).setExplosionSound(this.explosionSound);
      ((InventoryItem)var2).setColorRed((float)this.colorRed / 255.0F);
      ((InventoryItem)var2).setColorGreen((float)this.colorGreen / 255.0F);
      ((InventoryItem)var2).setColorBlue((float)this.colorBlue / 255.0F);
      ((InventoryItem)var2).setEvolvedRecipeName(this.evolvedRecipeName);
      ((InventoryItem)var2).setMetalValue(this.metalValue);
      ((InventoryItem)var2).setWet(this.isWet);
      ((InventoryItem)var2).setWetCooldown(this.wetCooldown);
      ((InventoryItem)var2).setItemWhenDry(this.itemWhenDry);
      ((InventoryItem)var2).setMap(this.map);
      ((InventoryItem)var2).keepOnDeplete = this.keepOnDeplete;
      ((InventoryItem)var2).setMechanicType(this.vehicleType);
      ((InventoryItem)var2).setItemCapacity((float)this.itemCapacity);
      ((InventoryItem)var2).setMaxCapacity(this.maxCapacity);
      ((InventoryItem)var2).setBrakeForce(this.brakeForce);
      ((InventoryItem)var2).setChanceToSpawnDamaged(this.chanceToSpawnDamaged);
      ((InventoryItem)var2).setConditionLowerNormal(this.conditionLowerNormal);
      ((InventoryItem)var2).setConditionLowerOffroad(this.conditionLowerOffroad);
      ((InventoryItem)var2).setWheelFriction(this.wheelFriction);
      ((InventoryItem)var2).setSuspensionCompression(this.suspensionCompression);
      ((InventoryItem)var2).setEngineLoudness(this.engineLoudness);
      ((InventoryItem)var2).setSuspensionDamping(this.suspensionDamping);
      if (this.CustomContextMenu != null) {
         ((InventoryItem)var2).setCustomMenuOption(Translator.getText("ContextMenu_" + this.CustomContextMenu));
      }

      ((InventoryItem)var2).CloseKillMove = this.CloseKillMove;
      return (InventoryItem)var2;
   }

   public void DoParam(String var1) {
      if (var1.trim().length() != 0) {
         try {
            String[] var2 = var1.split("=");
            String var3 = var2[0].trim();
            String var4 = var2[1].trim();
            if (var3.trim().equalsIgnoreCase("BodyLocation")) {
               Item.ClothingBodyLocation var10001 = this.bodyLocation;
               this.bodyLocation = Item.ClothingBodyLocation.valueOf(var4.trim());
            } else {
               String[] var5;
               int var6;
               if (var3.trim().equalsIgnoreCase("Palettes")) {
                  var5 = var4.split("/");

                  for(var6 = 0; var6 < var5.length; ++var6) {
                     this.PaletteChoices.add(var5[var6].trim());
                  }
               } else if (var3.trim().equalsIgnoreCase("HitSound")) {
                  this.HitSound = var4.trim();
                  if (this.HitSound.equals("null")) {
                     this.HitSound = null;
                  }
               } else if (var3.trim().equalsIgnoreCase("PalettesStart")) {
                  this.PalettesStart = var4.trim();
               } else if (var3.trim().equalsIgnoreCase("DisplayName")) {
                  this.DisplayName = Translator.getDisplayItemName(var4.trim());
               } else if (var3.trim().equalsIgnoreCase("MetalValue")) {
                  this.metalValue = new Float(var4.trim());
               } else if (var3.trim().equalsIgnoreCase("SpriteName")) {
                  this.SpriteName = var4.trim();
               } else if (var3.trim().equalsIgnoreCase("Type")) {
                  this.type = Item.Type.valueOf(var4.trim());
               } else if (var3.trim().equalsIgnoreCase("SplatSize")) {
                  this.SplatSize = Float.parseFloat(var4);
               } else if (var3.trim().equalsIgnoreCase("CanStoreWater")) {
                  this.CanStoreWater = var4.equalsIgnoreCase("true");
               } else if (var3.trim().equalsIgnoreCase("IsWaterSource")) {
                  this.IsWaterSource = var4.equalsIgnoreCase("true");
               } else if (var3.trim().equalsIgnoreCase("Poison")) {
                  this.Poison = var4.equalsIgnoreCase("true");
               } else if (var3.trim().equalsIgnoreCase("FoodType")) {
                  this.FoodType = var4.trim();
               } else if (var3.trim().equalsIgnoreCase("PoisonDetectionLevel")) {
                  this.PoisonDetectionLevel = Integer.parseInt(var4);
               } else if (var3.trim().equalsIgnoreCase("PoisonPower")) {
                  this.PoisonPower = Integer.parseInt(var4);
               } else if (var3.trim().equalsIgnoreCase("UseForPoison")) {
                  this.UseForPoison = Integer.parseInt(var4);
               } else if (var3.trim().equalsIgnoreCase("SwingAnim")) {
                  this.SwingAnim = var4;
               } else {
                  String var8;
                  if (var3.trim().equalsIgnoreCase("Icon")) {
                     this.Icon = var4;
                     this.ItemName = "Item_" + this.Icon;
                     this.NormalTexture = Texture.trygetTexture(this.ItemName);
                     if (this.NormalTexture == null) {
                        this.NormalTexture = Texture.getSharedTexture("media/inventory/Question_On.png");
                     }

                     this.WorldTextureName = this.ItemName.replace("Item_", "media/inventory/world/WItem_");
                     this.WorldTextureName = this.WorldTextureName + ".png";
                     this.WorldTexture = Texture.getSharedTexture(this.WorldTextureName);
                     if (this.type == Item.Type.Food) {
                        Texture var17 = Texture.trygetTexture(this.ItemName + "Rotten");
                        String var18 = this.WorldTextureName.replace(".png", "Rotten.png");
                        if (var17 == null) {
                           var17 = Texture.trygetTexture(this.ItemName + "Spoiled");
                           var18 = var18.replace("Rotten.png", "Spoiled.png");
                        }

                        this.SpecialWorldTextureNames.add(var18);
                        this.SpecialTextures.add(var17);
                        this.SpecialTextures.add(Texture.trygetTexture(this.ItemName + "Cooked"));
                        this.SpecialWorldTextureNames.add(this.WorldTextureName.replace(".png", "Cooked.png"));
                        Texture var7 = Texture.trygetTexture(this.ItemName + "Overdone");
                        var8 = this.WorldTextureName.replace(".png", "Overdone.png");
                        if (var7 == null) {
                           var7 = Texture.trygetTexture(this.ItemName + "Burnt");
                           var8 = var8.replace("Overdone.png", "Burnt.png");
                        }

                        this.SpecialTextures.add(var7);
                        this.SpecialWorldTextureNames.add(var8);
                     }
                  } else if (var3.trim().equalsIgnoreCase("ResizeWorldIcon")) {
                     if (var4.equalsIgnoreCase("true")) {
                        this.ResizeWorldIcon = true;
                     } else if (var4.equalsIgnoreCase("false")) {
                        this.ResizeWorldIcon = false;
                     } else {
                        this.ResizeWorldIcon = false;
                        this.ScaleWorldIcon = Float.parseFloat(var4);
                     }
                  } else if (var3.trim().equalsIgnoreCase("DoorHitSound")) {
                     this.DoorHitSound = var4;
                  } else if (var3.trim().equalsIgnoreCase("Weight")) {
                     this.ActualWeight = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("HungerChange")) {
                     this.HungerChange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("ThirstChange")) {
                     this.ThistChange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("FatigueChange")) {
                     this.FatigueChange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("EnduranceChange")) {
                     this.EnduranceChange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("CriticalChance")) {
                     this.CriticalChance = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("DaysFresh")) {
                     this.DaysFresh = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("DaysTotallyRotten")) {
                     this.DaysTotallyRotten = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("IsCookable")) {
                     this.IsCookable = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("MinutesToCook")) {
                     this.MinutesToCook = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinutesToBurn")) {
                     this.MinutesToBurn = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("BoredomChange")) {
                     this.BoredomChange = (float)Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("StressChange")) {
                     this.StressChange = (float)Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("UnhappyChange")) {
                     this.UnhappyChange = (float)Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ReplaceOnDeplete")) {
                     this.ReplaceOnDeplete = var4;
                  } else if (var3.trim().equalsIgnoreCase("ReplaceOnUseOn")) {
                     this.ReplaceOnUseOn = var4;
                  } else if (var3.trim().equalsIgnoreCase("Ranged")) {
                     this.Ranged = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("UseSelf")) {
                     this.UseSelf = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("OtherHandUse")) {
                     this.OtherHandUse = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("DangerousUncooked")) {
                     this.DangerousUncooked = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("MaxRange")) {
                     this.MaxRange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinRange")) {
                     this.MinRange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinAngle")) {
                     this.MinAngle = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MaxDamage")) {
                     this.MaxDamage = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("ConditionLowerChanceOneIn")) {
                     this.ConditionLowerChance = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("MultipleHitConditionAffected")) {
                     this.MultipleHitConditionAffected = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("CanBandage")) {
                     this.CanBandage = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("ConditionMax")) {
                     this.ConditionMax = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("SoundGain")) {
                     this.SoundGain = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinDamage")) {
                     this.MinDamage = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinimumSwingTime")) {
                     this.MinimumSwingTime = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("SwingSound")) {
                     this.SwingSound = var4;
                  } else if (var3.trim().equalsIgnoreCase("ReplaceOnUse")) {
                     this.ReplaceOnUse = var4;
                  } else if (var3.trim().equalsIgnoreCase("WeaponSprite")) {
                     this.WeaponSprite = var4;
                  } else if (var3.trim().equalsIgnoreCase("AimingPerkCritModifier")) {
                     this.AimingPerkCritModifier = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("AimingPerkRangeModifier")) {
                     this.AimingPerkRangeModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("AimingPerkHitChanceModifier")) {
                     this.AimingPerkHitChanceModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("AngleModifier")) {
                     this.angleModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("WeightModifier")) {
                     this.weightModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("AimingPerkMinAngleModifier")) {
                     this.AimingPerkMinAngleModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("HitChance")) {
                     this.HitChance = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("RecoilDelay")) {
                     this.RecoilDelay = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("PiercingBullets")) {
                     this.PiercingBullets = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("AngleFalloff")) {
                     this.AngleFalloff = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("SoundVolume")) {
                     this.SoundVolume = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ToHitModifier")) {
                     this.ToHitModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("SoundRadius")) {
                     this.SoundRadius = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("Categories")) {
                     var5 = var4.split(";");

                     for(var6 = 0; var6 < var5.length; ++var6) {
                        this.Categories.add(var5[var6].trim());
                     }
                  } else if (var3.trim().equalsIgnoreCase("OtherCharacterVolumeBoost")) {
                     this.OtherCharacterVolumeBoost = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("ImpactSound")) {
                     this.ImpactSound = var4;
                     if (this.ImpactSound.equals("null")) {
                        this.ImpactSound = null;
                     }
                  } else if (var3.trim().equalsIgnoreCase("SwingTime")) {
                     this.SwingTime = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("KnockBackOnNoDeath")) {
                     this.KnockBackOnNoDeath = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("Alcoholic")) {
                     this.Alcoholic = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("SplatBloodOnNoDeath")) {
                     this.SplatBloodOnNoDeath = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("SwingAmountBeforeImpact")) {
                     this.SwingAmountBeforeImpact = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("AmmoType")) {
                     this.AmmoType = var4;
                  } else if (var3.trim().equalsIgnoreCase("HitAngleMod")) {
                     this.HitAngleMod = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("OtherHandRequire")) {
                     this.OtherHandRequire = var4;
                  } else if (var3.trim().equalsIgnoreCase("AlwaysWelcomeGift")) {
                     this.AlwaysWelcomeGift = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("CantAttackWithLowestEndurance")) {
                     this.CantAttackWithLowestEndurance = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("EnduranceMod")) {
                     this.EnduranceMod = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("KnockdownMod")) {
                     this.KnockdownMod = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("DoorDamage")) {
                     this.DoorDamage = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("MaxHitCount")) {
                     this.MaxHitCount = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("PhysicsObject")) {
                     this.PhysicsObject = var4;
                  } else if (var3.trim().equalsIgnoreCase("Count")) {
                     this.Count = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("SwingAnim")) {
                     this.SwingAnim = var4;
                  } else if (var3.trim().equalsIgnoreCase("WeaponWeight")) {
                     this.WeaponWeight = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("IdleAnim")) {
                     this.IdleAnim = var4;
                  } else if (var3.trim().equalsIgnoreCase("RunAnim")) {
                     this.RunAnim = var4;
                  } else if (var3.trim().equalsIgnoreCase("RequireInHandOrInventory")) {
                     this.RequireInHandOrInventory = new ArrayList(Arrays.asList(var4.split("/")));
                  } else if (var3.trim().equalsIgnoreCase("PushBackMod")) {
                     this.PushBackMod = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("NPCSoundBoost")) {
                     this.NPCSoundBoost = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("SplatNumber")) {
                     this.SplatNumber = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("RangeFalloff")) {
                     this.RangeFalloff = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("UseEndurance")) {
                     this.UseEndurance = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("ShareDamage")) {
                     this.ShareDamage = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("ShareEndurance")) {
                     this.ShareEndurance = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("AlwaysKnockdown")) {
                     this.AlwaysKnockdown = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("IsAimedFirearm")) {
                     this.IsAimedFirearm = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("bulletOutSound")) {
                     this.bulletOutSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("ShellFallSound")) {
                     this.ShellFallSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("IsAimedHandWeapon")) {
                     this.IsAimedHandWeapon = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("AimingMod")) {
                     this.AimingMod = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("ProjectileCount")) {
                     this.ProjectileCount = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("CanStack")) {
                     this.IsAimedFirearm = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("HerbalistType")) {
                     this.HerbalistType = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("CanBarricade")) {
                     this.CanBarricade = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("UseWhileEquipped")) {
                     this.UseWhileEquipped = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("DisappearOnUse")) {
                     this.DisappearOnUse = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("Temperature")) {
                     this.Temperature = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("Insulation")) {
                     this.insulation = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("CloseKillMove")) {
                     this.CloseKillMove = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("UseDelta")) {
                     this.UseDelta = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("RainFactor")) {
                     this.rainFactor = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("NumberOfPages")) {
                     this.NumberOfPages = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("SkillTrained")) {
                     this.SkillTrained = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("LvlSkillTrained")) {
                     this.LvlSkillTrained = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("NumLevelsTrained")) {
                     this.NumLevelsTrained = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("Capacity")) {
                     this.Capacity = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("MaxCapacity")) {
                     this.maxCapacity = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ItemCapacity")) {
                     this.itemCapacity = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ConditionAffectsCapacity")) {
                     this.ConditionAffectsCapacity = Boolean.parseBoolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("BrakeForce")) {
                     this.brakeForce = (float)new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ChanceToSpawnDamaged")) {
                     this.chanceToSpawnDamaged = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ClipSize")) {
                     this.ClipSize = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ReloadTime")) {
                     this.reloadTime = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("AimingTime")) {
                     this.aimingTime = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("AimingTimeModifier")) {
                     this.aimingTimeModifier = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ReloadTimeModifier")) {
                     this.reloadTimeModifier = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("HitChanceModifier")) {
                     this.hitChanceModifier = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("WeightReduction")) {
                     this.WeightReduction = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("CanBeEquipped")) {
                     this.CanBeEquipped = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("SubCategory")) {
                     this.SubCategory = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("ActivatedItem")) {
                     this.ActivatedItem = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("LightStrength")) {
                     this.LightStrength = new Float(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("TorchCone")) {
                     this.TorchCone = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("LightDistance")) {
                     this.LightDistance = new Integer(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("TwoHandWeapon")) {
                     this.TwoHandWeapon = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("Tooltip")) {
                     this.Tooltip = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("DisplayCategory")) {
                     this.DisplayCategory = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("BadInMicrowave")) {
                     this.BadInMicrowave = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("GoodHot")) {
                     this.GoodHot = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("BadCold")) {
                     this.BadCold = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("AlarmSound")) {
                     this.AlarmSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("RequiresEquippedBothHands")) {
                     this.RequiresEquippedBothHands = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("ReplaceOnCooked")) {
                     this.ReplaceOnCooked = Arrays.asList(var4.trim().split(";"));
                  } else if (var3.trim().equalsIgnoreCase("CustomContextMenu")) {
                     this.CustomContextMenu = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("Trap")) {
                     this.Trap = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("Wet")) {
                     this.isWet = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("WetCooldown")) {
                     this.wetCooldown = Float.parseFloat(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ItemWhenDry")) {
                     this.itemWhenDry = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("FishingLure")) {
                     this.FishingLure = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("CanBeWrite")) {
                     this.canBeWrite = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("PageToWrite")) {
                     this.PageToWrite = Integer.parseInt(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("Spice")) {
                     this.Spice = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("RemoveNegativeEffectOnCooked")) {
                     this.RemoveNegativeEffectOnCooked = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("ClipSizeModifier")) {
                     this.clipSizeModifier = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("RecoilDelayModifier")) {
                     this.recoilDelayModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MaxRangeModifier")) {
                     this.maxRangeModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("MinRangeModifier")) {
                     this.minRangeRangedModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("DamageModifier")) {
                     this.damageModifier = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("Map")) {
                     this.map = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("PutInSound")) {
                     this.PutInSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("CloseSound")) {
                     this.CloseSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("OpenSound")) {
                     this.OpenSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("BreakSound")) {
                     this.breakSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("TreeDamage")) {
                     this.treeDamage = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("CustomEatSound")) {
                     this.customEatSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("AlcoholPower")) {
                     this.alcoholPower = Float.parseFloat(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("BandagePower")) {
                     this.bandagePower = Float.parseFloat(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ReduceInfectionPower")) {
                     this.ReduceInfectionPower = Float.parseFloat(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("OnCooked")) {
                     this.OnCooked = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("OnlyAcceptCategory")) {
                     this.OnlyAcceptCategory = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("Padlock")) {
                     this.padlock = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("DigitalPadlock")) {
                     this.digitalPadlock = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("triggerExplosionTimer")) {
                     this.triggerExplosionTimer = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("sensorRange")) {
                     this.sensorRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("remoteRange")) {
                     this.remoteRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("CountDownSound")) {
                     this.countDownSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("explosionSound")) {
                     this.explosionSound = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("PlacedSprite")) {
                     this.PlacedSprite = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("explosionTimer")) {
                     this.explosionTimer = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("explosionRange")) {
                     this.explosionRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("explosionPower")) {
                     this.explosionPower = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("fireRange")) {
                     this.fireRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("firePower")) {
                     this.firePower = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("canBePlaced")) {
                     this.canBePlaced = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("CanBeReused")) {
                     this.canBeReused = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("canBeRemote")) {
                     this.canBeRemote = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("remoteController")) {
                     this.remoteController = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("smokeRange")) {
                     this.smokeRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("noiseRange")) {
                     this.noiseRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("extraDamage")) {
                     this.extraDamage = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("TwoWay")) {
                     this.twoWay = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("TransmitRange")) {
                     this.transmitRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("MicRange")) {
                     this.micRange = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("BaseVolumeRange")) {
                     this.baseVolumeRange = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("IsPortable")) {
                     this.isPortable = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("IsTelevision")) {
                     this.isTelevision = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("MinChannel")) {
                     this.minChannel = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("MaxChannel")) {
                     this.maxChannel = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("UsesBattery")) {
                     this.usesBattery = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("IsHighTier")) {
                     this.isHighTier = new Boolean(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("WorldObjectSprite")) {
                     this.worldObjectSprite = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("fluReduction")) {
                     this.fluReduction = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ReduceFoodSickness")) {
                     this.ReduceFoodSickness = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("painReduction")) {
                     this.painReduction = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ColorRed")) {
                     this.colorRed = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ColorGreen")) {
                     this.colorGreen = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("ColorBlue")) {
                     this.colorBlue = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("calories")) {
                     this.calories = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("carbohydrates")) {
                     this.carbohydrates = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("lipids")) {
                     this.lipids = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("proteins")) {
                     this.proteins = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("Packaged")) {
                     this.packaged = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("CantBeFrozen")) {
                     this.cantBeFrozen = var4.trim().equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("EvolvedRecipeName")) {
                     this.evolvedRecipeName = Translator.getDisplayItemName(var4.trim());
                  } else if (var3.trim().equalsIgnoreCase("ReplaceOnRotten")) {
                     this.ReplaceOnRotten = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("CantBeConsolided")) {
                     this.cantBeConsolided = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("OnEat")) {
                     this.onEat = var4.trim();
                  } else if (var3.trim().equalsIgnoreCase("KeepOnDeplete")) {
                     this.keepOnDeplete = var4.equalsIgnoreCase("true");
                  } else if (var3.trim().equalsIgnoreCase("VehicleType")) {
                     this.vehicleType = Integer.parseInt(var4);
                  } else if (var3.trim().equalsIgnoreCase("conditionLowerOffroad")) {
                     this.conditionLowerOffroad = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("ConditionLowerStandard")) {
                     this.conditionLowerNormal = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("wheelFriction")) {
                     this.wheelFriction = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("suspensionDamping")) {
                     this.suspensionDamping = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("suspensionCompression")) {
                     this.suspensionCompression = Float.parseFloat(var4);
                  } else if (var3.trim().equalsIgnoreCase("engineLoudness")) {
                     this.engineLoudness = Float.parseFloat(var4);
                  } else {
                     String var20;
                     if (var3.trim().equalsIgnoreCase("TeachedRecipes")) {
                        this.teachedRecipes = new ArrayList();
                        var5 = var4.split(";");

                        for(var6 = 0; var6 < var5.length; ++var6) {
                           var20 = var5[var6].trim();
                           this.teachedRecipes.add(var20);
                           if (Translator.debug) {
                              Translator.getRecipeName(var20);
                           }
                        }
                     } else if (var3.trim().equalsIgnoreCase("MountOn")) {
                        this.mountOn = new ArrayList();
                        var5 = var4.split(";");

                        for(var6 = 0; var6 < var5.length; ++var6) {
                           this.mountOn.add(var5[var6].trim());
                        }
                     } else if (var3.trim().equalsIgnoreCase("PartType")) {
                        this.partType = var4;
                     } else if (var3.trim().equalsIgnoreCase("EvolvedRecipe")) {
                        var5 = var4.split(";");

                        for(var6 = 0; var6 < var5.length; ++var6) {
                           var20 = var5[var6];
                           var8 = null;
                           int var9 = 0;
                           boolean var10 = false;
                           if (!var20.contains(":")) {
                              var8 = var20;
                           } else {
                              var8 = var20.split(":")[0];
                              String var11 = var20.split(":")[1];
                              if (!var11.contains("|")) {
                                 var9 = Integer.parseInt(var20.split(":")[1]);
                              } else {
                                 String[] var12 = var11.split("\\|");

                                 for(int var13 = 0; var13 < var12.length; ++var13) {
                                    if ("Cooked".equals(var12[var13])) {
                                       var10 = true;
                                    }
                                 }

                                 var9 = Integer.parseInt(var12[0]);
                              }
                           }

                           ItemRecipe var21 = new ItemRecipe(this.name, this.module.getName(), var9);
                           EvolvedRecipe var22 = null;
                           Iterator var23 = ((ScriptModule)ScriptManager.instance.ModuleMap.get("Base")).EvolvedRecipeMap.iterator();

                           while(var23.hasNext()) {
                              EvolvedRecipe var14 = (EvolvedRecipe)var23.next();
                              if (var14.name.equals(var8)) {
                                 var22 = var14;
                                 break;
                              }
                           }

                           var21.cooked = var10;
                           if (var22 == null) {
                              var22 = new EvolvedRecipe(var8);
                              ((ScriptModule)ScriptManager.instance.ModuleMap.get("Base")).EvolvedRecipeMap.add(var22);
                           }

                           var22.itemsList.put(this.name, var21);
                        }
                     } else if (var3.trim().equalsIgnoreCase("OBSOLETE")) {
                        this.OBSOLETE = var4.trim().toLowerCase().equals("true");
                     } else {
                        DebugLog.log("adding unknown item param \"" + var3.trim() + "\" = \"" + var4.trim() + "\"");
                        if (this.DefaultModData == null) {
                           this.DefaultModData = LuaManager.platform.newTable();
                        }

                        try {
                           Double var19 = Double.parseDouble(var4.trim());
                           this.DefaultModData.rawset(var3.trim(), var19);
                        } catch (Exception var15) {
                           this.DefaultModData.rawset(var3.trim(), var4);
                        }
                     }
                  }
               }
            }

         } catch (Exception var16) {
            throw new InvalidParameterException("Error: " + var1.trim() + " is not a valid parameter in item: " + this.name);
         }
      }
   }

   public float getTemperature() {
      return this.Temperature;
   }

   public void setTemperature(float var1) {
      this.Temperature = var1;
   }

   public boolean isConditionAffectsCapacity() {
      return this.ConditionAffectsCapacity;
   }

   public float getInsulation() {
      return this.insulation;
   }

   public void setInsulation(float var1) {
      this.insulation = var1;
      if (this.insulation < 0.0F) {
         this.insulation = 0.0F;
      }

      if (this.insulation > 1.0F) {
         this.insulation = 1.0F;
      }

   }

   public boolean getObsolete() {
      return this.OBSOLETE;
   }

   public static enum Type {
      Normal,
      Weapon,
      Food,
      Literature,
      Drainable,
      Clothing,
      Container,
      WeaponPart,
      Key,
      KeyRing,
      Moveable,
      Radio,
      AlarmClock;
   }

   public static enum ClothingBodyLocation {
      None,
      Head,
      Top,
      Bottoms,
      Shoes;
   }
}
