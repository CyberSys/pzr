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
import zombie.characterTextures.BloodClothingType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
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
import zombie.network.GameClient;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.radio.media.MediaData;
import zombie.scripting.ScriptManager;
import zombie.util.StringUtils;
import zombie.world.WorldDictionary;


public final class Item extends BaseScriptObject {
	public String clothingExtraSubmenu = null;
	public String DisplayName = null;
	public boolean Hidden = false;
	public String Icon = "None";
	public boolean Medical = false;
	public boolean CannedFood = false;
	public boolean SurvivalGear = false;
	public boolean MechanicsItem = false;
	public boolean UseWorldItem = false;
	public float ScaleWorldIcon = 1.0F;
	public String CloseKillMove = null;
	public float WeaponLength = 0.4F;
	public float ActualWeight = 1.0F;
	public float WeightWet = 0.0F;
	public float WeightEmpty = 0.0F;
	public float HungerChange = 0.0F;
	public float CriticalChance = 20.0F;
	public int Count = 1;
	public int DaysFresh = 1000000000;
	public int DaysTotallyRotten = 1000000000;
	public int MinutesToCook = 60;
	public int MinutesToBurn = 120;
	public boolean IsCookable = false;
	private String CookingSound = null;
	public float StressChange = 0.0F;
	public float BoredomChange = 0.0F;
	public float UnhappyChange = 0.0F;
	public boolean AlwaysWelcomeGift = false;
	public String ReplaceOnDeplete = null;
	public boolean Ranged = false;
	public boolean CanStoreWater = false;
	public float MaxRange = 1.0F;
	public float MinRange = 0.0F;
	public float ThirstChange = 0.0F;
	public float FatigueChange = 0.0F;
	public float MinAngle = 1.0F;
	public boolean RequiresEquippedBothHands = false;
	public float MaxDamage = 1.5F;
	public float MinDamage = 0.0F;
	public float MinimumSwingTime = 0.0F;
	public String SwingSound = "BaseballBatSwing";
	public String WeaponSprite;
	public boolean AngleFalloff = false;
	public int SoundVolume = 0;
	public float ToHitModifier = 1.0F;
	public int SoundRadius = 0;
	public float OtherCharacterVolumeBoost;
	public final ArrayList Categories = new ArrayList();
	public final ArrayList Tags = new ArrayList();
	public String ImpactSound = "BaseballBatHit";
	public float SwingTime = 1.0F;
	public boolean KnockBackOnNoDeath = true;
	public boolean SplatBloodOnNoDeath = false;
	public float SwingAmountBeforeImpact = 0.0F;
	public String AmmoType = null;
	public int maxAmmo = 0;
	public String GunType = null;
	public int DoorDamage = 1;
	public int ConditionLowerChance = 1000000;
	public int ConditionMax = 10;
	public boolean CanBandage = false;
	public String name;
	public String moduleDotType;
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
	public String attachmentType = null;
	public String makeUpType = null;
	public String consolidateOption = null;
	public ArrayList RequireInHandOrInventory = null;
	public String DoorHitSound = "BaseballBatHit";
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
	public boolean UseWhileUnequipped = false;
	public int TicksPerEquipUse = 30;
	public boolean DisappearOnUse = true;
	public float UseDelta = 0.03125F;
	public boolean AlwaysKnockdown = false;
	public float EnduranceMod = 1.0F;
	public float KnockdownMod = 1.0F;
	public boolean CantAttackWithLowestEndurance = false;
	public String ReplaceOnUseOn = null;
	public boolean IsWaterSource = false;
	public ArrayList attachmentsProvided = null;
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
	public boolean ProtectFromRainWhenEquipped = false;
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
	private String AcceptItemFunction = null;
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
	private int noiseDuration = 0;
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
	public float torchDot = 0.96F;
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
	public String map = null;
	private boolean keepOnDeplete = false;
	public int vehicleType = 0;
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
	public String ClothingItem = null;
	private ClothingItem clothingItemAsset = null;
	private String staticModel = null;
	public String primaryAnimMask = null;
	public String secondaryAnimMask = null;
	public String primaryAnimMaskAttachment = null;
	public String secondaryAnimMaskAttachment = null;
	public String replaceInSecondHand = null;
	public String replaceInPrimaryHand = null;
	public String replaceWhenUnequip = null;
	public ItemReplacement replacePrimaryHand = null;
	public ItemReplacement replaceSecondHand = null;
	public String worldObjectSprite = null;
	public String ItemName;
	public Texture NormalTexture;
	public List SpecialTextures = new ArrayList();
	public List SpecialWorldTextureNames = new ArrayList();
	public String WorldTextureName;
	public Texture WorldTexture;
	public String eatType;
	private ArrayList IconsForTexture;
	private float baseSpeed = 1.0F;
	private ArrayList bloodClothingType;
	private float stompPower = 1.0F;
	public float runSpeedModifier = 1.0F;
	public float combatSpeedModifier = 1.0F;
	public ArrayList clothingItemExtra;
	public ArrayList clothingItemExtraOption;
	private Boolean removeOnBroken = false;
	public Boolean canHaveHoles = true;
	private boolean cosmetic = false;
	private String ammoBox = null;
	public boolean hairDye = false;
	private String insertAmmoStartSound = null;
	private String insertAmmoSound = null;
	private String insertAmmoStopSound = null;
	private String ejectAmmoStartSound = null;
	private String ejectAmmoSound = null;
	private String ejectAmmoStopSound = null;
	private String rackSound = null;
	private String clickSound = "Stormy9mmClick";
	private String equipSound = null;
	private String unequipSound = null;
	private String bringToBearSound = null;
	private String magazineType = null;
	private String weaponReloadType = null;
	private boolean rackAfterShoot = false;
	private float jamGunChance = 1.0F;
	private ArrayList modelWeaponPart = null;
	private boolean haveChamber = true;
	private boolean manuallyRemoveSpentRounds = false;
	private float biteDefense = 0.0F;
	private float scratchDefense = 0.0F;
	private float bulletDefense = 0.0F;
	private String damageCategory = null;
	private boolean damageMakeHole = false;
	public float neckProtectionModifier = 1.0F;
	private String attachmentReplacement = null;
	private boolean insertAllBulletsReload = false;
	private int chanceToFall = 0;
	public String fabricType = null;
	public boolean equippedNoSprint = false;
	public String worldStaticModel = null;
	private float critDmgMultiplier = 0.0F;
	private float insulation = 0.0F;
	private float windresist = 0.0F;
	private float waterresist = 0.0F;
	private String fireMode = null;
	private ArrayList fireModePossibilities = null;
	public boolean RemoveUnhappinessWhenCooked = false;
	private short registry_id = -1;
	private boolean existsAsVanilla = false;
	private String modID;
	private String fileAbsPath;
	public float stopPower = 5.0F;
	private String recordedMediaCat;
	private byte acceptMediaType = -1;
	private boolean noTransmit = false;
	public String HitSound = "BaseballBatHit";
	public String hitFloorSound = "BatOnFloor";
	public String BodyLocation = "";
	public Stack PaletteChoices = new Stack();
	public String SpriteName = null;
	public String PalettesStart = "";
	public static HashMap NetIDToItem = new HashMap();
	public static HashMap NetItemToID = new HashMap();
	static int IDMax = 0;
	public Item.Type type;
	private boolean Spice;
	private int UseForPoison;

	public Item() {
		this.type = Item.Type.Normal;
		this.Spice = false;
	}

	public String getDisplayName() {
		return this.DisplayName;
	}

	public void setDisplayName(String string) {
		this.DisplayName = string;
	}

	public boolean isHidden() {
		return this.Hidden;
	}

	public String getDisplayCategory() {
		return this.DisplayCategory;
	}

	public String getIcon() {
		return this.Icon;
	}

	public void setIcon(String string) {
		this.Icon = string;
	}

	public int getNoiseDuration() {
		return this.noiseDuration;
	}

	public Texture getNormalTexture() {
		return this.NormalTexture;
	}

	public int getNumberOfPages() {
		return this.NumberOfPages;
	}

	public float getActualWeight() {
		return this.ActualWeight;
	}

	public void setActualWeight(float float1) {
		this.ActualWeight = float1;
	}

	public float getWeightWet() {
		return this.WeightWet;
	}

	public void setWeightWet(float float1) {
		this.WeightWet = float1;
	}

	public float getWeightEmpty() {
		return this.WeightEmpty;
	}

	public void setWeightEmpty(float float1) {
		this.WeightEmpty = float1;
	}

	public float getHungerChange() {
		return this.HungerChange;
	}

	public void setHungerChange(float float1) {
		this.HungerChange = float1;
	}

	public float getThirstChange() {
		return this.ThirstChange;
	}

	public void setThirstChange(float float1) {
		this.ThirstChange = float1;
	}

	public int getCount() {
		return this.Count;
	}

	public void setCount(int int1) {
		this.Count = int1;
	}

	public int getDaysFresh() {
		return this.DaysFresh;
	}

	public void setDaysFresh(int int1) {
		this.DaysFresh = int1;
	}

	public int getDaysTotallyRotten() {
		return this.DaysTotallyRotten;
	}

	public void setDaysTotallyRotten(int int1) {
		this.DaysTotallyRotten = int1;
	}

	public int getMinutesToCook() {
		return this.MinutesToCook;
	}

	public void setMinutesToCook(int int1) {
		this.MinutesToCook = int1;
	}

	public int getMinutesToBurn() {
		return this.MinutesToBurn;
	}

	public void setMinutesToBurn(int int1) {
		this.MinutesToBurn = int1;
	}

	public boolean isIsCookable() {
		return this.IsCookable;
	}

	public void setIsCookable(boolean boolean1) {
		this.IsCookable = boolean1;
	}

	public String getCookingSound() {
		return this.CookingSound;
	}

	public float getStressChange() {
		return this.StressChange;
	}

	public void setStressChange(float float1) {
		this.StressChange = float1;
	}

	public float getBoredomChange() {
		return this.BoredomChange;
	}

	public void setBoredomChange(float float1) {
		this.BoredomChange = float1;
	}

	public float getUnhappyChange() {
		return this.UnhappyChange;
	}

	public void setUnhappyChange(float float1) {
		this.UnhappyChange = float1;
	}

	public boolean isAlwaysWelcomeGift() {
		return this.AlwaysWelcomeGift;
	}

	public void setAlwaysWelcomeGift(boolean boolean1) {
		this.AlwaysWelcomeGift = boolean1;
	}

	public boolean isRanged() {
		return this.Ranged;
	}

	public boolean getCanStoreWater() {
		return this.CanStoreWater;
	}

	public void setRanged(boolean boolean1) {
		this.Ranged = boolean1;
	}

	public float getMaxRange() {
		return this.MaxRange;
	}

	public void setMaxRange(float float1) {
		this.MaxRange = float1;
	}

	public float getMinAngle() {
		return this.MinAngle;
	}

	public void setMinAngle(float float1) {
		this.MinAngle = float1;
	}

	public float getMaxDamage() {
		return this.MaxDamage;
	}

	public void setMaxDamage(float float1) {
		this.MaxDamage = float1;
	}

	public float getMinDamage() {
		return this.MinDamage;
	}

	public void setMinDamage(float float1) {
		this.MinDamage = float1;
	}

	public float getMinimumSwingTime() {
		return this.MinimumSwingTime;
	}

	public void setMinimumSwingTime(float float1) {
		this.MinimumSwingTime = float1;
	}

	public String getSwingSound() {
		return this.SwingSound;
	}

	public void setSwingSound(String string) {
		this.SwingSound = string;
	}

	public String getWeaponSprite() {
		return this.WeaponSprite;
	}

	public void setWeaponSprite(String string) {
		this.WeaponSprite = string;
	}

	public boolean isAngleFalloff() {
		return this.AngleFalloff;
	}

	public void setAngleFalloff(boolean boolean1) {
		this.AngleFalloff = boolean1;
	}

	public int getSoundVolume() {
		return this.SoundVolume;
	}

	public void setSoundVolume(int int1) {
		this.SoundVolume = int1;
	}

	public float getToHitModifier() {
		return this.ToHitModifier;
	}

	public void setToHitModifier(float float1) {
		this.ToHitModifier = float1;
	}

	public int getSoundRadius() {
		return this.SoundRadius;
	}

	public void setSoundRadius(int int1) {
		this.SoundRadius = int1;
	}

	public float getOtherCharacterVolumeBoost() {
		return this.OtherCharacterVolumeBoost;
	}

	public void setOtherCharacterVolumeBoost(float float1) {
		this.OtherCharacterVolumeBoost = float1;
	}

	public ArrayList getCategories() {
		return this.Categories;
	}

	public void setCategories(ArrayList arrayList) {
		this.Categories.clear();
		this.Categories.addAll(arrayList);
	}

	public ArrayList getTags() {
		return this.Tags;
	}

	public String getImpactSound() {
		return this.ImpactSound;
	}

	public void setImpactSound(String string) {
		this.ImpactSound = string;
	}

	public float getSwingTime() {
		return this.SwingTime;
	}

	public void setSwingTime(float float1) {
		this.SwingTime = float1;
	}

	public boolean isKnockBackOnNoDeath() {
		return this.KnockBackOnNoDeath;
	}

	public void setKnockBackOnNoDeath(boolean boolean1) {
		this.KnockBackOnNoDeath = boolean1;
	}

	public boolean isSplatBloodOnNoDeath() {
		return this.SplatBloodOnNoDeath;
	}

	public void setSplatBloodOnNoDeath(boolean boolean1) {
		this.SplatBloodOnNoDeath = boolean1;
	}

	public float getSwingAmountBeforeImpact() {
		return this.SwingAmountBeforeImpact;
	}

	public void setSwingAmountBeforeImpact(float float1) {
		this.SwingAmountBeforeImpact = float1;
	}

	public String getAmmoType() {
		return this.AmmoType;
	}

	public void setAmmoType(String string) {
		this.AmmoType = string;
	}

	public int getDoorDamage() {
		return this.DoorDamage;
	}

	public void setDoorDamage(int int1) {
		this.DoorDamage = int1;
	}

	public int getConditionLowerChance() {
		return this.ConditionLowerChance;
	}

	public void setConditionLowerChance(int int1) {
		this.ConditionLowerChance = int1;
	}

	public int getConditionMax() {
		return this.ConditionMax;
	}

	public void setConditionMax(int int1) {
		this.ConditionMax = int1;
	}

	public boolean isCanBandage() {
		return this.CanBandage;
	}

	public void setCanBandage(boolean boolean1) {
		this.CanBandage = boolean1;
	}

	public boolean isCosmetic() {
		return this.cosmetic;
	}

	public String getName() {
		return this.name;
	}

	public String getModuleName() {
		return this.module.name;
	}

	public String getFullName() {
		return this.moduleDotType;
	}

	public void setName(String string) {
		this.name = string;
		this.moduleDotType = this.module.name + "." + string;
	}

	public int getMaxHitCount() {
		return this.MaxHitCount;
	}

	public void setMaxHitCount(int int1) {
		this.MaxHitCount = int1;
	}

	public boolean isUseSelf() {
		return this.UseSelf;
	}

	public void setUseSelf(boolean boolean1) {
		this.UseSelf = boolean1;
	}

	public boolean isOtherHandUse() {
		return this.OtherHandUse;
	}

	public void setOtherHandUse(boolean boolean1) {
		this.OtherHandUse = boolean1;
	}

	public String getOtherHandRequire() {
		return this.OtherHandRequire;
	}

	public void setOtherHandRequire(String string) {
		this.OtherHandRequire = string;
	}

	public String getPhysicsObject() {
		return this.PhysicsObject;
	}

	public void setPhysicsObject(String string) {
		this.PhysicsObject = string;
	}

	public String getSwingAnim() {
		return this.SwingAnim;
	}

	public void setSwingAnim(String string) {
		this.SwingAnim = string;
	}

	public float getWeaponWeight() {
		return this.WeaponWeight;
	}

	public void setWeaponWeight(float float1) {
		this.WeaponWeight = float1;
	}

	public float getEnduranceChange() {
		return this.EnduranceChange;
	}

	public void setEnduranceChange(float float1) {
		this.EnduranceChange = float1;
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

	public String getClothingItem() {
		return this.ClothingItem;
	}

	public void setClothingItemAsset(ClothingItem clothingItem) {
		this.clothingItemAsset = clothingItem;
	}

	public ClothingItem getClothingItemAsset() {
		return this.clothingItemAsset;
	}

	public ArrayList getClothingItemExtra() {
		return this.clothingItemExtra;
	}

	public ArrayList getClothingItemExtraOption() {
		return this.clothingItemExtraOption;
	}

	public String getFabricType() {
		return this.fabricType;
	}

	public ArrayList getIconsForTexture() {
		return this.IconsForTexture;
	}

	public String getCustomEatSound() {
		return this.customEatSound;
	}

	public String getCountDownSound() {
		return this.countDownSound;
	}

	public String getBringToBearSound() {
		return this.bringToBearSound;
	}

	public String getEjectAmmoStartSound() {
		return this.ejectAmmoStartSound;
	}

	public String getEjectAmmoSound() {
		return this.ejectAmmoSound;
	}

	public String getEjectAmmoStopSound() {
		return this.ejectAmmoStopSound;
	}

	public String getInsertAmmoStartSound() {
		return this.insertAmmoStartSound;
	}

	public String getInsertAmmoSound() {
		return this.insertAmmoSound;
	}

	public String getInsertAmmoStopSound() {
		return this.insertAmmoStopSound;
	}

	public String getEquipSound() {
		return this.equipSound;
	}

	public String getUnequipSound() {
		return this.unequipSound;
	}

	public String getExplosionSound() {
		return this.explosionSound;
	}

	public String getStaticModel() {
		return this.staticModel;
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

	public String getSkillTrained() {
		return this.SkillTrained;
	}

	public String getDoorHitSound() {
		return this.DoorHitSound;
	}

	public void setDoorHitSound(String string) {
		this.DoorHitSound = string;
	}

	public boolean isManuallyRemoveSpentRounds() {
		return this.manuallyRemoveSpentRounds;
	}

	public float getRainFactor() {
		return this.rainFactor;
	}

	public String getReplaceOnUse() {
		return this.ReplaceOnUse;
	}

	public void setReplaceOnUse(String string) {
		this.ReplaceOnUse = string;
	}

	public String getReplaceOnDeplete() {
		return this.ReplaceOnDeplete;
	}

	public void setReplaceOnDeplete(String string) {
		this.ReplaceOnDeplete = string;
	}

	public boolean isDangerousUncooked() {
		return this.DangerousUncooked;
	}

	public void setDangerousUncooked(boolean boolean1) {
		this.DangerousUncooked = boolean1;
	}

	public boolean isAlcoholic() {
		return this.Alcoholic;
	}

	public void setAlcoholic(boolean boolean1) {
		this.Alcoholic = boolean1;
	}

	public float getPushBackMod() {
		return this.PushBackMod;
	}

	public void setPushBackMod(float float1) {
		this.PushBackMod = float1;
	}

	public int getSplatNumber() {
		return this.SplatNumber;
	}

	public void setSplatNumber(int int1) {
		this.SplatNumber = int1;
	}

	public float getNPCSoundBoost() {
		return this.NPCSoundBoost;
	}

	public void setNPCSoundBoost(float float1) {
		this.NPCSoundBoost = float1;
	}

	public boolean isRangeFalloff() {
		return this.RangeFalloff;
	}

	public void setRangeFalloff(boolean boolean1) {
		this.RangeFalloff = boolean1;
	}

	public boolean isUseEndurance() {
		return this.UseEndurance;
	}

	public void setUseEndurance(boolean boolean1) {
		this.UseEndurance = boolean1;
	}

	public boolean isMultipleHitConditionAffected() {
		return this.MultipleHitConditionAffected;
	}

	public void setMultipleHitConditionAffected(boolean boolean1) {
		this.MultipleHitConditionAffected = boolean1;
	}

	public boolean isShareDamage() {
		return this.ShareDamage;
	}

	public void setShareDamage(boolean boolean1) {
		this.ShareDamage = boolean1;
	}

	public boolean isShareEndurance() {
		return this.ShareEndurance;
	}

	public void setShareEndurance(boolean boolean1) {
		this.ShareEndurance = boolean1;
	}

	public boolean isCanBarricade() {
		return this.CanBarricade;
	}

	public void setCanBarricade(boolean boolean1) {
		this.CanBarricade = boolean1;
	}

	public boolean isUseWhileEquipped() {
		return this.UseWhileEquipped;
	}

	public void setUseWhileEquipped(boolean boolean1) {
		this.UseWhileEquipped = boolean1;
	}

	public boolean isUseWhileUnequipped() {
		return this.UseWhileUnequipped;
	}

	public void setUseWhileUnequipped(boolean boolean1) {
		this.UseWhileUnequipped = boolean1;
	}

	public void setTicksPerEquipUse(int int1) {
		this.TicksPerEquipUse = int1;
	}

	public float getTicksPerEquipUse() {
		return (float)this.TicksPerEquipUse;
	}

	public boolean isDisappearOnUse() {
		return this.DisappearOnUse;
	}

	public void setDisappearOnUse(boolean boolean1) {
		this.DisappearOnUse = boolean1;
	}

	public float getUseDelta() {
		return this.UseDelta;
	}

	public void setUseDelta(float float1) {
		this.UseDelta = float1;
	}

	public boolean isAlwaysKnockdown() {
		return this.AlwaysKnockdown;
	}

	public void setAlwaysKnockdown(boolean boolean1) {
		this.AlwaysKnockdown = boolean1;
	}

	public float getEnduranceMod() {
		return this.EnduranceMod;
	}

	public void setEnduranceMod(float float1) {
		this.EnduranceMod = float1;
	}

	public float getKnockdownMod() {
		return this.KnockdownMod;
	}

	public void setKnockdownMod(float float1) {
		this.KnockdownMod = float1;
	}

	public boolean isCantAttackWithLowestEndurance() {
		return this.CantAttackWithLowestEndurance;
	}

	public void setCantAttackWithLowestEndurance(boolean boolean1) {
		this.CantAttackWithLowestEndurance = boolean1;
	}

	public String getBodyLocation() {
		return this.BodyLocation;
	}

	public void setBodyLocation(String string) {
		this.BodyLocation = string;
	}

	public Stack getPaletteChoices() {
		return this.PaletteChoices;
	}

	public void setPaletteChoices(Stack stack) {
		this.PaletteChoices = stack;
	}

	public String getSpriteName() {
		return this.SpriteName;
	}

	public void setSpriteName(String string) {
		this.SpriteName = string;
	}

	public String getPalettesStart() {
		return this.PalettesStart;
	}

	public void setPalettesStart(String string) {
		this.PalettesStart = string;
	}

	public Item.Type getType() {
		return this.type;
	}

	public void setType(Item.Type type) {
		this.type = type;
	}

	public String getTypeString() {
		return this.type.name();
	}

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.moduleDotType = this.module.name + "." + string;
		int int1 = IDMax++;
		NetIDToItem.put(int1, this.moduleDotType);
		NetItemToID.put(this.moduleDotType, int1);
		this.modID = ScriptManager.getCurrentLoadFileMod();
		if (this.modID.equals("pz-vanilla")) {
			this.existsAsVanilla = true;
		}

		this.fileAbsPath = ScriptManager.getCurrentLoadFileAbsPath();
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string2 = stringArray2[int3];
			this.DoParam(string2);
		}

		if (this.DisplayName == null) {
			this.DisplayName = this.getFullName();
			this.Hidden = true;
		}

		if (!StringUtils.isNullOrWhitespace(this.replaceInPrimaryHand)) {
			stringArray2 = this.replaceInPrimaryHand.trim().split("\\s+");
			if (stringArray2.length == 2) {
				this.replacePrimaryHand = new ItemReplacement();
				this.replacePrimaryHand.clothingItemName = stringArray2[0].trim();
				this.replacePrimaryHand.maskVariableValue = stringArray2[1].trim();
				this.replacePrimaryHand.maskVariableName = "RightHandMask";
			}
		}

		if (!StringUtils.isNullOrWhitespace(this.replaceInSecondHand)) {
			stringArray2 = this.replaceInSecondHand.trim().split("\\s+");
			if (stringArray2.length == 2) {
				this.replaceSecondHand = new ItemReplacement();
				this.replaceSecondHand.clothingItemName = stringArray2[0].trim();
				this.replaceSecondHand.maskVariableValue = stringArray2[1].trim();
				this.replaceSecondHand.maskVariableName = "LeftHandMask";
			}
		}

		if (!StringUtils.isNullOrWhitespace(this.primaryAnimMask)) {
			this.replacePrimaryHand = new ItemReplacement();
			this.replacePrimaryHand.maskVariableValue = this.primaryAnimMask;
			this.replacePrimaryHand.maskVariableName = "RightHandMask";
			this.replacePrimaryHand.attachment = this.primaryAnimMaskAttachment;
		}

		if (!StringUtils.isNullOrWhitespace(this.secondaryAnimMask)) {
			this.replaceSecondHand = new ItemReplacement();
			this.replaceSecondHand.maskVariableValue = this.secondaryAnimMask;
			this.replaceSecondHand.maskVariableName = "LeftHandMask";
			this.replaceSecondHand.attachment = this.secondaryAnimMaskAttachment;
		}

		WorldDictionary.onLoadItem(this);
	}

	public InventoryItem InstanceItem(String string) {
		Object object = null;
		if (this.type == Item.Type.Key) {
			object = new Key(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
			((Key)object).setDigitalPadlock(this.digitalPadlock);
			((Key)object).setPadlock(this.padlock);
			if (((Key)object).isPadlock()) {
				((Key)object).setNumberOfKey(2);
				((Key)object).setKeyId(Rand.Next(10000000));
			}
		}

		if (this.type == Item.Type.KeyRing) {
			object = new KeyRing(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
		}

		if (this.type == Item.Type.WeaponPart) {
			object = new WeaponPart(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
			WeaponPart weaponPart = (WeaponPart)object;
			weaponPart.setDamage(this.damageModifier);
			weaponPart.setClipSize(this.clipSizeModifier);
			weaponPart.setMaxRange(this.maxRangeModifier);
			weaponPart.setMinRangeRanged(this.minRangeRangedModifier);
			weaponPart.setRecoilDelay(this.recoilDelayModifier);
			weaponPart.setMountOn(this.mountOn);
			weaponPart.setPartType(this.partType);
			weaponPart.setReloadTime(this.reloadTimeModifier);
			weaponPart.setAimingTime(this.aimingTimeModifier);
			weaponPart.setHitChance(this.hitChanceModifier);
			weaponPart.setAngle(this.angleModifier);
			weaponPart.setWeightModifier(this.weightModifier);
		}

		if (this.type == Item.Type.Container) {
			object = new InventoryContainer(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
			InventoryContainer inventoryContainer = (InventoryContainer)object;
			inventoryContainer.setItemCapacity((float)this.Capacity);
			inventoryContainer.setCapacity(this.Capacity);
			inventoryContainer.setWeightReduction(this.WeightReduction);
			inventoryContainer.setCanBeEquipped(this.CanBeEquipped);
			inventoryContainer.getInventory().setPutSound(this.PutInSound);
			inventoryContainer.getInventory().setCloseSound(this.CloseSound);
			inventoryContainer.getInventory().setOpenSound(this.OpenSound);
			inventoryContainer.getInventory().setOnlyAcceptCategory(this.OnlyAcceptCategory);
			inventoryContainer.getInventory().setAcceptItemFunction(this.AcceptItemFunction);
		}

		if (this.type == Item.Type.Food) {
			object = new Food(this.module.name, this.DisplayName, this.name, this);
			Food food = (Food)object;
			food.Poison = this.Poison;
			food.setPoisonLevelForRecipe(this.PoisonDetectionLevel);
			food.setFoodType(this.FoodType);
			food.setPoisonPower(this.PoisonPower);
			food.setUseForPoison(this.UseForPoison);
			food.setThirstChange(this.ThirstChange / 100.0F);
			food.setHungChange(this.HungerChange / 100.0F);
			food.setBaseHunger(this.HungerChange / 100.0F);
			food.setEndChange(this.EnduranceChange / 100.0F);
			food.setOffAge(this.DaysFresh);
			food.setOffAgeMax(this.DaysTotallyRotten);
			food.setIsCookable(this.IsCookable);
			food.setMinutesToCook((float)this.MinutesToCook);
			food.setMinutesToBurn((float)this.MinutesToBurn);
			food.setbDangerousUncooked(this.DangerousUncooked);
			food.setReplaceOnUse(this.ReplaceOnUse);
			food.setReplaceOnCooked(this.ReplaceOnCooked);
			food.setSpice(this.Spice);
			food.setRemoveNegativeEffectOnCooked(this.RemoveNegativeEffectOnCooked);
			food.setCustomEatSound(this.customEatSound);
			food.setOnCooked(this.OnCooked);
			food.setFluReduction(this.fluReduction);
			food.setReduceFoodSickness(this.ReduceFoodSickness);
			food.setPainReduction((float)this.painReduction);
			food.setHerbalistType(this.HerbalistType);
			food.setCarbohydrates(this.carbohydrates);
			food.setLipids(this.lipids);
			food.setProteins(this.proteins);
			food.setCalories(this.calories);
			food.setPackaged(this.packaged);
			food.setCanBeFrozen(!this.cantBeFrozen);
			food.setReplaceOnRotten(this.ReplaceOnRotten);
			food.setOnEat(this.onEat);
			food.setBadInMicrowave(this.BadInMicrowave);
			food.setGoodHot(this.GoodHot);
			food.setBadCold(this.BadCold);
		}

		if (this.type == Item.Type.Literature) {
			object = new Literature(this.module.name, this.DisplayName, this.name, this);
			Literature literature = (Literature)object;
			literature.setReplaceOnUse(this.ReplaceOnUse);
			literature.setNumberOfPages(this.NumberOfPages);
			literature.setAlreadyReadPages(0);
			literature.setSkillTrained(this.SkillTrained);
			literature.setLvlSkillTrained(this.LvlSkillTrained);
			literature.setNumLevelsTrained(this.NumLevelsTrained);
			literature.setCanBeWrite(this.canBeWrite);
			literature.setPageToWrite(this.PageToWrite);
			literature.setTeachedRecipes(this.teachedRecipes);
		} else if (this.type == Item.Type.AlarmClock) {
			object = new AlarmClock(this.module.name, this.DisplayName, this.name, this);
			AlarmClock alarmClock = (AlarmClock)object;
			alarmClock.setAlarmSound(this.AlarmSound);
			alarmClock.setSoundRadius(this.SoundRadius);
		} else {
			String string2;
			int int1;
			String string3;
			String string4;
			if (this.type == Item.Type.AlarmClockClothing) {
				string3 = "";
				string2 = null;
				if (!this.PaletteChoices.isEmpty() || string != null) {
					int1 = Rand.Next(this.PaletteChoices.size());
					string2 = (String)this.PaletteChoices.get(int1);
					if (string != null) {
						string2 = string;
					}

					string4 = string2.replace(this.PalettesStart, "");
					string3 = "_" + string4;
				}

				object = new AlarmClockClothing(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon.replace(".png", "") + string3, string2, this.SpriteName);
				AlarmClockClothing alarmClockClothing = (AlarmClockClothing)object;
				alarmClockClothing.setTemperature(this.Temperature);
				alarmClockClothing.setInsulation(this.insulation);
				alarmClockClothing.setConditionLowerChance(this.ConditionLowerChance);
				alarmClockClothing.setStompPower(this.stompPower);
				alarmClockClothing.setRunSpeedModifier(this.runSpeedModifier);
				alarmClockClothing.setCombatSpeedModifier(this.combatSpeedModifier);
				alarmClockClothing.setRemoveOnBroken(this.removeOnBroken);
				alarmClockClothing.setCanHaveHoles(this.canHaveHoles);
				alarmClockClothing.setWeightWet(this.WeightWet);
				alarmClockClothing.setBiteDefense(this.biteDefense);
				alarmClockClothing.setBulletDefense(this.bulletDefense);
				alarmClockClothing.setNeckProtectionModifier(this.neckProtectionModifier);
				alarmClockClothing.setScratchDefense(this.scratchDefense);
				alarmClockClothing.setChanceToFall(this.chanceToFall);
				alarmClockClothing.setWindresistance(this.windresist);
				alarmClockClothing.setWaterResistance(this.waterresist);
				alarmClockClothing.setAlarmSound(this.AlarmSound);
				alarmClockClothing.setSoundRadius(this.SoundRadius);
			} else if (this.type == Item.Type.Weapon) {
				object = new HandWeapon(this.module.name, this.DisplayName, this.name, this);
				HandWeapon handWeapon = (HandWeapon)object;
				handWeapon.setMultipleHitConditionAffected(this.MultipleHitConditionAffected);
				handWeapon.setConditionLowerChance(this.ConditionLowerChance);
				handWeapon.SplatSize = this.SplatSize;
				handWeapon.aimingMod = this.AimingMod;
				handWeapon.setMinDamage(this.MinDamage);
				handWeapon.setMaxDamage(this.MaxDamage);
				handWeapon.setBaseSpeed(this.baseSpeed);
				handWeapon.setPhysicsObject(this.PhysicsObject);
				handWeapon.setOtherHandRequire(this.OtherHandRequire);
				handWeapon.setOtherHandUse(this.OtherHandUse);
				handWeapon.setMaxRange(this.MaxRange);
				handWeapon.setMinRange(this.MinRange);
				handWeapon.setShareEndurance(this.ShareEndurance);
				handWeapon.setKnockdownMod(this.KnockdownMod);
				handWeapon.bIsAimedFirearm = this.IsAimedFirearm;
				handWeapon.RunAnim = this.RunAnim;
				handWeapon.IdleAnim = this.IdleAnim;
				handWeapon.HitAngleMod = (float)Math.toRadians((double)this.HitAngleMod);
				handWeapon.bIsAimedHandWeapon = this.IsAimedHandWeapon;
				handWeapon.setCantAttackWithLowestEndurance(this.CantAttackWithLowestEndurance);
				handWeapon.setAlwaysKnockdown(this.AlwaysKnockdown);
				handWeapon.setEnduranceMod(this.EnduranceMod);
				handWeapon.setUseSelf(this.UseSelf);
				handWeapon.setMaxHitCount(this.MaxHitCount);
				handWeapon.setMinimumSwingTime(this.MinimumSwingTime);
				handWeapon.setSwingTime(this.SwingTime);
				handWeapon.setDoSwingBeforeImpact(this.SwingAmountBeforeImpact);
				handWeapon.setMinAngle(this.MinAngle);
				handWeapon.setDoorDamage(this.DoorDamage);
				handWeapon.setTreeDamage(this.treeDamage);
				handWeapon.setDoorHitSound(this.DoorHitSound);
				handWeapon.setHitFloorSound(this.hitFloorSound);
				handWeapon.setZombieHitSound(this.HitSound);
				handWeapon.setPushBackMod(this.PushBackMod);
				handWeapon.setWeight(this.WeaponWeight);
				handWeapon.setImpactSound(this.ImpactSound);
				handWeapon.setSplatNumber(this.SplatNumber);
				handWeapon.setKnockBackOnNoDeath(this.KnockBackOnNoDeath);
				handWeapon.setSplatBloodOnNoDeath(this.SplatBloodOnNoDeath);
				handWeapon.setSwingSound(this.SwingSound);
				handWeapon.setBulletOutSound(this.bulletOutSound);
				handWeapon.setShellFallSound(this.ShellFallSound);
				handWeapon.setAngleFalloff(this.AngleFalloff);
				handWeapon.setSoundVolume(this.SoundVolume);
				handWeapon.setSoundRadius(this.SoundRadius);
				handWeapon.setToHitModifier(this.ToHitModifier);
				handWeapon.setOtherBoost(this.NPCSoundBoost);
				handWeapon.setRanged(this.Ranged);
				handWeapon.setRangeFalloff(this.RangeFalloff);
				handWeapon.setUseEndurance(this.UseEndurance);
				handWeapon.setCriticalChance(this.CriticalChance);
				handWeapon.setCritDmgMultiplier(this.critDmgMultiplier);
				handWeapon.setShareDamage(this.ShareDamage);
				handWeapon.setCanBarracade(this.CanBarricade);
				handWeapon.setWeaponSprite(this.WeaponSprite);
				handWeapon.setOriginalWeaponSprite(this.WeaponSprite);
				handWeapon.setSubCategory(this.SubCategory);
				handWeapon.setCategories(this.Categories);
				handWeapon.setSoundGain(this.SoundGain);
				handWeapon.setAimingPerkCritModifier(this.AimingPerkCritModifier);
				handWeapon.setAimingPerkRangeModifier(this.AimingPerkRangeModifier);
				handWeapon.setAimingPerkHitChanceModifier(this.AimingPerkHitChanceModifier);
				handWeapon.setHitChance(this.HitChance);
				handWeapon.setRecoilDelay(this.RecoilDelay);
				handWeapon.setAimingPerkMinAngleModifier(this.AimingPerkMinAngleModifier);
				handWeapon.setPiercingBullets(this.PiercingBullets);
				handWeapon.setClipSize(this.ClipSize);
				handWeapon.setReloadTime(this.reloadTime);
				handWeapon.setAimingTime(this.aimingTime);
				handWeapon.setTriggerExplosionTimer(this.triggerExplosionTimer);
				handWeapon.setSensorRange(this.sensorRange);
				handWeapon.setWeaponLength(this.WeaponLength);
				handWeapon.setPlacedSprite(this.PlacedSprite);
				handWeapon.setExplosionTimer(this.explosionTimer);
				handWeapon.setCanBePlaced(this.canBePlaced);
				handWeapon.setCanBeReused(this.canBeReused);
				handWeapon.setExplosionRange(this.explosionRange);
				handWeapon.setExplosionPower(this.explosionPower);
				handWeapon.setFireRange(this.fireRange);
				handWeapon.setFirePower(this.firePower);
				handWeapon.setSmokeRange(this.smokeRange);
				handWeapon.setNoiseRange(this.noiseRange);
				handWeapon.setExtraDamage(this.extraDamage);
				handWeapon.setAmmoBox(this.ammoBox);
				handWeapon.setRackSound(this.rackSound);
				handWeapon.setClickSound(this.clickSound);
				handWeapon.setMagazineType(this.magazineType);
				handWeapon.setWeaponReloadType(this.weaponReloadType);
				handWeapon.setInsertAllBulletsReload(this.insertAllBulletsReload);
				handWeapon.setRackAfterShoot(this.rackAfterShoot);
				handWeapon.setJamGunChance(this.jamGunChance);
				handWeapon.setModelWeaponPart(this.modelWeaponPart);
				handWeapon.setHaveChamber(this.haveChamber);
				handWeapon.setDamageCategory(this.damageCategory);
				handWeapon.setDamageMakeHole(this.damageMakeHole);
				handWeapon.setFireMode(this.fireMode);
				handWeapon.setFireModePossibilities(this.fireModePossibilities);
			} else if (this.type == Item.Type.Normal) {
				object = new ComboItem(this.module.name, this.DisplayName, this.name, this);
			} else if (this.type == Item.Type.Clothing) {
				string3 = "";
				string2 = null;
				if (!this.PaletteChoices.isEmpty() || string != null) {
					int1 = Rand.Next(this.PaletteChoices.size());
					string2 = (String)this.PaletteChoices.get(int1);
					if (string != null) {
						string2 = string;
					}

					string4 = string2.replace(this.PalettesStart, "");
					string3 = "_" + string4;
				}

				object = new Clothing(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon.replace(".png", "") + string3, string2, this.SpriteName);
				Clothing clothing = (Clothing)object;
				clothing.setTemperature(this.Temperature);
				clothing.setInsulation(this.insulation);
				clothing.setConditionLowerChance(this.ConditionLowerChance);
				clothing.setStompPower(this.stompPower);
				clothing.setRunSpeedModifier(this.runSpeedModifier);
				clothing.setCombatSpeedModifier(this.combatSpeedModifier);
				clothing.setRemoveOnBroken(this.removeOnBroken);
				clothing.setCanHaveHoles(this.canHaveHoles);
				clothing.setWeightWet(this.WeightWet);
				clothing.setBiteDefense(this.biteDefense);
				clothing.setBulletDefense(this.bulletDefense);
				clothing.setNeckProtectionModifier(this.neckProtectionModifier);
				clothing.setScratchDefense(this.scratchDefense);
				clothing.setChanceToFall(this.chanceToFall);
				clothing.setWindresistance(this.windresist);
				clothing.setWaterResistance(this.waterresist);
			} else if (this.type == Item.Type.Drainable) {
				object = new DrainableComboItem(this.module.name, this.DisplayName, this.name, this);
				DrainableComboItem drainableComboItem = (DrainableComboItem)object;
				drainableComboItem.setUseWhileEquiped(this.UseWhileEquipped);
				drainableComboItem.setUseWhileUnequiped(this.UseWhileUnequipped);
				drainableComboItem.setTicksPerEquipUse(this.TicksPerEquipUse);
				drainableComboItem.setUseDelta(this.UseDelta);
				drainableComboItem.setReplaceOnDeplete(this.ReplaceOnDeplete);
				drainableComboItem.setIsCookable(this.IsCookable);
				drainableComboItem.setRainFactor(this.rainFactor);
				drainableComboItem.setCanConsolidate(!this.cantBeConsolided);
				drainableComboItem.setWeightEmpty(this.WeightEmpty);
			} else if (this.type == Item.Type.Radio) {
				object = new Radio(this.module.name, this.DisplayName, this.name, "Item_" + this.Icon);
				Radio radio = (Radio)object;
				DeviceData deviceData = radio.getDeviceData();
				if (deviceData != null) {
					if (this.DisplayName != null) {
						deviceData.setDeviceName(this.DisplayName);
					}

					deviceData.setIsTwoWay(this.twoWay);
					deviceData.setTransmitRange(this.transmitRange);
					deviceData.setMicRange(this.micRange);
					deviceData.setBaseVolumeRange(this.baseVolumeRange);
					deviceData.setIsPortable(this.isPortable);
					deviceData.setIsTelevision(this.isTelevision);
					deviceData.setMinChannelRange(this.minChannel);
					deviceData.setMaxChannelRange(this.maxChannel);
					deviceData.setIsBatteryPowered(this.usesBattery);
					deviceData.setIsHighTier(this.isHighTier);
					deviceData.setUseDelta(this.UseDelta);
					deviceData.setMediaType(this.acceptMediaType);
					deviceData.setNoTransmit(this.noTransmit);
					deviceData.generatePresets();
					deviceData.setRandomChannel();
				}

				radio.ReadFromWorldSprite(this.worldObjectSprite);
			} else if (this.type == Item.Type.Moveable) {
				object = new Moveable(this.module.name, this.DisplayName, this.name, this);
				Moveable moveable = (Moveable)object;
				moveable.ReadFromWorldSprite(this.worldObjectSprite);
				this.ActualWeight = moveable.getActualWeight();
			}
		}

		if (this.colorRed < 255 || this.colorGreen < 255 || this.colorBlue < 255) {
			((InventoryItem)object).setColor(new Color((float)this.colorRed / 255.0F, (float)this.colorGreen / 255.0F, (float)this.colorBlue / 255.0F));
		}

		((InventoryItem)object).setAlcoholPower(this.alcoholPower);
		((InventoryItem)object).setConditionMax(this.ConditionMax);
		((InventoryItem)object).setCondition(this.ConditionMax);
		((InventoryItem)object).setCanBeActivated(this.ActivatedItem);
		((InventoryItem)object).setLightStrength(this.LightStrength);
		((InventoryItem)object).setTorchCone(this.TorchCone);
		((InventoryItem)object).setLightDistance(this.LightDistance);
		((InventoryItem)object).setActualWeight(this.ActualWeight);
		((InventoryItem)object).setWeight(this.ActualWeight);
		((InventoryItem)object).setUses(this.Count);
		((InventoryItem)object).setScriptItem(this);
		((InventoryItem)object).setBoredomChange(this.BoredomChange);
		((InventoryItem)object).setStressChange(this.StressChange / 100.0F);
		((InventoryItem)object).setUnhappyChange(this.UnhappyChange);
		((InventoryItem)object).setReplaceOnUseOn(this.ReplaceOnUseOn);
		((InventoryItem)object).setRequireInHandOrInventory(this.RequireInHandOrInventory);
		((InventoryItem)object).setAttachmentsProvided(this.attachmentsProvided);
		((InventoryItem)object).setAttachmentReplacement(this.attachmentReplacement);
		((InventoryItem)object).setIsWaterSource(this.IsWaterSource);
		((InventoryItem)object).CanStoreWater = this.CanStoreWater;
		((InventoryItem)object).CanStack = this.CanStack;
		((InventoryItem)object).copyModData(this.DefaultModData);
		((InventoryItem)object).setCount(this.Count);
		((InventoryItem)object).setFatigueChange(this.FatigueChange / 100.0F);
		((InventoryItem)object).setTooltip(this.Tooltip);
		((InventoryItem)object).setDisplayCategory(this.DisplayCategory);
		((InventoryItem)object).setAlcoholic(this.Alcoholic);
		((InventoryItem)object).RequiresEquippedBothHands = this.RequiresEquippedBothHands;
		((InventoryItem)object).setBreakSound(this.breakSound);
		((InventoryItem)object).setReplaceOnUse(this.ReplaceOnUse);
		((InventoryItem)object).setBandagePower(this.bandagePower);
		((InventoryItem)object).setReduceInfectionPower(this.ReduceInfectionPower);
		((InventoryItem)object).setCanBeRemote(this.canBeRemote);
		((InventoryItem)object).setRemoteController(this.remoteController);
		((InventoryItem)object).setRemoteRange(this.remoteRange);
		((InventoryItem)object).setCountDownSound(this.countDownSound);
		((InventoryItem)object).setExplosionSound(this.explosionSound);
		((InventoryItem)object).setColorRed((float)this.colorRed / 255.0F);
		((InventoryItem)object).setColorGreen((float)this.colorGreen / 255.0F);
		((InventoryItem)object).setColorBlue((float)this.colorBlue / 255.0F);
		((InventoryItem)object).setEvolvedRecipeName(this.evolvedRecipeName);
		((InventoryItem)object).setMetalValue(this.metalValue);
		((InventoryItem)object).setWet(this.isWet);
		((InventoryItem)object).setWetCooldown(this.wetCooldown);
		((InventoryItem)object).setItemWhenDry(this.itemWhenDry);
		((InventoryItem)object).setMap(this.map);
		((InventoryItem)object).keepOnDeplete = this.keepOnDeplete;
		((InventoryItem)object).setItemCapacity((float)this.itemCapacity);
		((InventoryItem)object).setMaxCapacity(this.maxCapacity);
		((InventoryItem)object).setBrakeForce(this.brakeForce);
		((InventoryItem)object).setChanceToSpawnDamaged(this.chanceToSpawnDamaged);
		((InventoryItem)object).setConditionLowerNormal(this.conditionLowerNormal);
		((InventoryItem)object).setConditionLowerOffroad(this.conditionLowerOffroad);
		((InventoryItem)object).setWheelFriction(this.wheelFriction);
		((InventoryItem)object).setSuspensionCompression(this.suspensionCompression);
		((InventoryItem)object).setEngineLoudness(this.engineLoudness);
		((InventoryItem)object).setSuspensionDamping(this.suspensionDamping);
		if (this.CustomContextMenu != null) {
			((InventoryItem)object).setCustomMenuOption(Translator.getText("ContextMenu_" + this.CustomContextMenu));
		}

		if (this.IconsForTexture != null && !this.IconsForTexture.isEmpty()) {
			((InventoryItem)object).setIconsForTexture(this.IconsForTexture);
		}

		((InventoryItem)object).setBloodClothingType(this.bloodClothingType);
		((InventoryItem)object).CloseKillMove = this.CloseKillMove;
		((InventoryItem)object).setAmmoType(this.AmmoType);
		((InventoryItem)object).setMaxAmmo(this.maxAmmo);
		((InventoryItem)object).setGunType(this.GunType);
		((InventoryItem)object).setAttachmentType(this.attachmentType);
		if (this.recordedMediaCat != null) {
			MediaData mediaData = ZomboidRadio.getInstance().getRecordedMedia().getRandomFromCategory(this.recordedMediaCat);
			if (mediaData != null) {
				((InventoryItem)object).setRecordedMediaIndex(mediaData.getIndex());
			}
		}

		long long1 = OutfitRNG.getSeed();
		OutfitRNG.setSeed((long)Rand.Next(Integer.MAX_VALUE));
		((InventoryItem)object).synchWithVisual();
		OutfitRNG.setSeed(long1);
		((InventoryItem)object).setRegistry_id(this);
		return (InventoryItem)object;
	}

	public void DoParam(String string) {
		if (string.trim().length() != 0) {
			try {
				String[] stringArray = string.split("=");
				String string2 = stringArray[0].trim();
				String string3 = stringArray[1].trim();
				if (string2.trim().equalsIgnoreCase("BodyLocation")) {
					this.BodyLocation = string3.trim();
				} else {
					String[] stringArray2;
					int int1;
					if (string2.trim().equalsIgnoreCase("Palettes")) {
						stringArray2 = string3.split("/");
						for (int1 = 0; int1 < stringArray2.length; ++int1) {
							this.PaletteChoices.add(stringArray2[int1].trim());
						}
					} else if (string2.trim().equalsIgnoreCase("HitSound")) {
						this.HitSound = string3.trim();
						if (this.HitSound.equals("null")) {
							this.HitSound = null;
						}
					} else if (string2.trim().equalsIgnoreCase("HitFloorSound")) {
						this.hitFloorSound = string3.trim();
					} else if (string2.trim().equalsIgnoreCase("PalettesStart")) {
						this.PalettesStart = string3.trim();
					} else if (string2.trim().equalsIgnoreCase("DisplayName")) {
						this.DisplayName = Translator.getDisplayItemName(string3.trim());
						this.DisplayName = Translator.getItemNameFromFullType(this.getFullName());
					} else if (string2.trim().equalsIgnoreCase("MetalValue")) {
						this.metalValue = new Float(string3.trim());
					} else if (string2.trim().equalsIgnoreCase("SpriteName")) {
						this.SpriteName = string3.trim();
					} else if (string2.trim().equalsIgnoreCase("Type")) {
						this.type = Item.Type.valueOf(string3.trim());
					} else if (string2.trim().equalsIgnoreCase("SplatSize")) {
						this.SplatSize = Float.parseFloat(string3);
					} else if (string2.trim().equalsIgnoreCase("CanStoreWater")) {
						this.CanStoreWater = string3.equalsIgnoreCase("true");
					} else if (string2.trim().equalsIgnoreCase("IsWaterSource")) {
						this.IsWaterSource = string3.equalsIgnoreCase("true");
					} else if (string2.trim().equalsIgnoreCase("Poison")) {
						this.Poison = string3.equalsIgnoreCase("true");
					} else if (string2.trim().equalsIgnoreCase("FoodType")) {
						this.FoodType = string3.trim();
					} else if (string2.trim().equalsIgnoreCase("PoisonDetectionLevel")) {
						this.PoisonDetectionLevel = Integer.parseInt(string3);
					} else if (string2.trim().equalsIgnoreCase("PoisonPower")) {
						this.PoisonPower = Integer.parseInt(string3);
					} else if (string2.trim().equalsIgnoreCase("UseForPoison")) {
						this.UseForPoison = Integer.parseInt(string3);
					} else if (string2.trim().equalsIgnoreCase("SwingAnim")) {
						this.SwingAnim = string3;
					} else {
						String string4;
						if (string2.trim().equalsIgnoreCase("Icon")) {
							this.Icon = string3;
							this.ItemName = "Item_" + this.Icon;
							this.NormalTexture = Texture.trygetTexture(this.ItemName);
							if (this.NormalTexture == null) {
								this.NormalTexture = Texture.getSharedTexture("media/inventory/Question_On.png");
							}

							this.WorldTextureName = this.ItemName.replace("Item_", "media/inventory/world/WItem_");
							this.WorldTextureName = this.WorldTextureName + ".png";
							this.WorldTexture = Texture.getSharedTexture(this.WorldTextureName);
							if (this.type == Item.Type.Food) {
								Texture texture = Texture.trygetTexture(this.ItemName + "Rotten");
								String string5 = this.WorldTextureName.replace(".png", "Rotten.png");
								if (texture == null) {
									texture = Texture.trygetTexture(this.ItemName + "Spoiled");
									string5 = string5.replace("Rotten.png", "Spoiled.png");
								}

								this.SpecialWorldTextureNames.add(string5);
								this.SpecialTextures.add(texture);
								this.SpecialTextures.add(Texture.trygetTexture(this.ItemName + "Cooked"));
								this.SpecialWorldTextureNames.add(this.WorldTextureName.replace(".png", "Cooked.png"));
								Texture texture2 = Texture.trygetTexture(this.ItemName + "Overdone");
								string4 = this.WorldTextureName.replace(".png", "Overdone.png");
								if (texture2 == null) {
									texture2 = Texture.trygetTexture(this.ItemName + "Burnt");
									string4 = string4.replace("Overdone.png", "Burnt.png");
								}

								this.SpecialTextures.add(texture2);
								this.SpecialWorldTextureNames.add(string4);
							}
						} else if (string2.trim().equalsIgnoreCase("UseWorldItem")) {
							this.UseWorldItem = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("Medical")) {
							this.Medical = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("CannedFood")) {
							this.CannedFood = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("MechanicsItem")) {
							this.MechanicsItem = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("SurvivalGear")) {
							this.SurvivalGear = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("ScaleWorldIcon")) {
							this.ScaleWorldIcon = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("HairDye")) {
							this.hairDye = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("DoorHitSound")) {
							this.DoorHitSound = string3;
						} else if (string2.trim().equalsIgnoreCase("Weight")) {
							this.ActualWeight = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("WeightWet")) {
							this.WeightWet = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("WeightEmpty")) {
							this.WeightEmpty = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("HungerChange")) {
							this.HungerChange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("ThirstChange")) {
							this.ThirstChange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("FatigueChange")) {
							this.FatigueChange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("EnduranceChange")) {
							this.EnduranceChange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("CriticalChance")) {
							this.CriticalChance = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("critDmgMultiplier")) {
							this.critDmgMultiplier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("DaysFresh")) {
							this.DaysFresh = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("DaysTotallyRotten")) {
							this.DaysTotallyRotten = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("IsCookable")) {
							this.IsCookable = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("CookingSound")) {
							this.CookingSound = string3;
						} else if (string2.trim().equalsIgnoreCase("MinutesToCook")) {
							this.MinutesToCook = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("MinutesToBurn")) {
							this.MinutesToBurn = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("BoredomChange")) {
							this.BoredomChange = (float)Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("StressChange")) {
							this.StressChange = (float)Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("UnhappyChange")) {
							this.UnhappyChange = (float)Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("RemoveUnhappinessWhenCooked")) {
							this.RemoveUnhappinessWhenCooked = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("ReplaceOnDeplete")) {
							this.ReplaceOnDeplete = string3;
						} else if (string2.trim().equalsIgnoreCase("ReplaceOnUseOn")) {
							this.ReplaceOnUseOn = string3;
						} else if (string2.trim().equalsIgnoreCase("Ranged")) {
							this.Ranged = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("UseSelf")) {
							this.UseSelf = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("OtherHandUse")) {
							this.OtherHandUse = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("DangerousUncooked")) {
							this.DangerousUncooked = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("MaxRange")) {
							this.MaxRange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MinRange")) {
							this.MinRange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MinAngle")) {
							this.MinAngle = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MaxDamage")) {
							this.MaxDamage = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("BaseSpeed")) {
							this.baseSpeed = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("stompPower")) {
							this.stompPower = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("combatSpeedModifier")) {
							this.combatSpeedModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("runSpeedModifier")) {
							this.runSpeedModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("clothingItemExtra")) {
							this.clothingItemExtra = new ArrayList();
							stringArray2 = string3.split(";");
							for (int1 = 0; int1 < stringArray2.length; ++int1) {
								this.clothingItemExtra.add(stringArray2[int1]);
							}
						} else if (string2.trim().equalsIgnoreCase("clothingExtraSubmenu")) {
							this.clothingExtraSubmenu = string3;
						} else if (string2.trim().equalsIgnoreCase("removeOnBroken")) {
							this.removeOnBroken = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("canHaveHoles")) {
							this.canHaveHoles = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("Cosmetic")) {
							this.cosmetic = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("ammoBox")) {
							this.ammoBox = string3;
						} else if (string2.trim().equalsIgnoreCase("InsertAmmoStartSound")) {
							this.insertAmmoStartSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("InsertAmmoSound")) {
							this.insertAmmoSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("InsertAmmoStopSound")) {
							this.insertAmmoStopSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("EjectAmmoStartSound")) {
							this.ejectAmmoStartSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("EjectAmmoSound")) {
							this.ejectAmmoSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("EjectAmmoStopSound")) {
							this.ejectAmmoStopSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("rackSound")) {
							this.rackSound = string3;
						} else if (string2.trim().equalsIgnoreCase("clickSound")) {
							this.clickSound = string3;
						} else if (string2.equalsIgnoreCase("BringToBearSound")) {
							this.bringToBearSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.equalsIgnoreCase("EquipSound")) {
							this.equipSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.equalsIgnoreCase("UnequipSound")) {
							this.unequipSound = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("magazineType")) {
							this.magazineType = string3;
						} else if (string2.trim().equalsIgnoreCase("jamGunChance")) {
							this.jamGunChance = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("modelWeaponPart")) {
							if (this.modelWeaponPart == null) {
								this.modelWeaponPart = new ArrayList();
							}

							stringArray2 = string3.split("\\s+");
							if (stringArray2.length >= 2 && stringArray2.length <= 4) {
								ModelWeaponPart modelWeaponPart = null;
								for (int int2 = 0; int2 < this.modelWeaponPart.size(); ++int2) {
									ModelWeaponPart modelWeaponPart2 = (ModelWeaponPart)this.modelWeaponPart.get(int2);
									if (modelWeaponPart2.partType.equals(stringArray2[0])) {
										modelWeaponPart = modelWeaponPart2;
										break;
									}
								}

								if (modelWeaponPart == null) {
									modelWeaponPart = new ModelWeaponPart();
								}

								modelWeaponPart.partType = stringArray2[0];
								modelWeaponPart.modelName = stringArray2[1];
								modelWeaponPart.attachmentNameSelf = stringArray2.length > 2 ? stringArray2[2] : null;
								modelWeaponPart.attachmentParent = stringArray2.length > 3 ? stringArray2[3] : null;
								if (!modelWeaponPart.partType.contains(".")) {
									modelWeaponPart.partType = this.module.name + "." + modelWeaponPart.partType;
								}

								if (!modelWeaponPart.modelName.contains(".")) {
									modelWeaponPart.modelName = this.module.name + "." + modelWeaponPart.modelName;
								}

								if ("none".equalsIgnoreCase(modelWeaponPart.attachmentNameSelf)) {
									modelWeaponPart.attachmentNameSelf = null;
								}

								if ("none".equalsIgnoreCase(modelWeaponPart.attachmentParent)) {
									modelWeaponPart.attachmentParent = null;
								}

								this.modelWeaponPart.add(modelWeaponPart);
							}
						} else if (string2.trim().equalsIgnoreCase("rackAfterShoot")) {
							this.rackAfterShoot = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("haveChamber")) {
							this.haveChamber = Boolean.parseBoolean(string3);
						} else if (string2.equalsIgnoreCase("ManuallyRemoveSpentRounds")) {
							this.manuallyRemoveSpentRounds = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("biteDefense")) {
							this.biteDefense = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("bulletDefense")) {
							this.bulletDefense = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("neckProtectionModifier")) {
							this.neckProtectionModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("damageCategory")) {
							this.damageCategory = string3;
						} else if (string2.trim().equalsIgnoreCase("fireMode")) {
							this.fireMode = string3;
						} else if (string2.trim().equalsIgnoreCase("damageMakeHole")) {
							this.damageMakeHole = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("equippedNoSprint")) {
							this.equippedNoSprint = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("scratchDefense")) {
							this.scratchDefense = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("weaponReloadType")) {
							this.weaponReloadType = string3;
						} else if (string2.trim().equalsIgnoreCase("insertAllBulletsReload")) {
							this.insertAllBulletsReload = Boolean.parseBoolean(string3);
						} else if (string2.trim().equalsIgnoreCase("clothingItemExtraOption")) {
							this.clothingItemExtraOption = new ArrayList();
							stringArray2 = string3.split(";");
							for (int1 = 0; int1 < stringArray2.length; ++int1) {
								this.clothingItemExtraOption.add(stringArray2[int1]);
							}
						} else if (string2.trim().equalsIgnoreCase("ConditionLowerChanceOneIn")) {
							this.ConditionLowerChance = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("MultipleHitConditionAffected")) {
							this.MultipleHitConditionAffected = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("CanBandage")) {
							this.CanBandage = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ConditionMax")) {
							this.ConditionMax = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("SoundGain")) {
							this.SoundGain = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MinDamage")) {
							this.MinDamage = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MinimumSwingTime")) {
							this.MinimumSwingTime = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("SwingSound")) {
							this.SwingSound = string3;
						} else if (string2.trim().equalsIgnoreCase("ReplaceOnUse")) {
							this.ReplaceOnUse = string3;
						} else if (string2.trim().equalsIgnoreCase("WeaponSprite")) {
							this.WeaponSprite = string3;
						} else if (string2.trim().equalsIgnoreCase("AimingPerkCritModifier")) {
							this.AimingPerkCritModifier = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("AimingPerkRangeModifier")) {
							this.AimingPerkRangeModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("AimingPerkHitChanceModifier")) {
							this.AimingPerkHitChanceModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("AngleModifier")) {
							this.angleModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("WeightModifier")) {
							this.weightModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("AimingPerkMinAngleModifier")) {
							this.AimingPerkMinAngleModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("HitChance")) {
							this.HitChance = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("RecoilDelay")) {
							this.RecoilDelay = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("StopPower")) {
							this.stopPower = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("PiercingBullets")) {
							this.PiercingBullets = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("AngleFalloff")) {
							this.AngleFalloff = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("SoundVolume")) {
							this.SoundVolume = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ToHitModifier")) {
							this.ToHitModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("SoundRadius")) {
							this.SoundRadius = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("Categories")) {
							stringArray2 = string3.split(";");
							for (int1 = 0; int1 < stringArray2.length; ++int1) {
								this.Categories.add(stringArray2[int1].trim());
							}
						} else if (string2.trim().equalsIgnoreCase("Tags")) {
							stringArray2 = string3.split(";");
							for (int1 = 0; int1 < stringArray2.length; ++int1) {
								this.Tags.add(stringArray2[int1].trim());
							}
						} else if (string2.trim().equalsIgnoreCase("OtherCharacterVolumeBoost")) {
							this.OtherCharacterVolumeBoost = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("ImpactSound")) {
							this.ImpactSound = string3;
							if (this.ImpactSound.equals("null")) {
								this.ImpactSound = null;
							}
						} else if (string2.trim().equalsIgnoreCase("SwingTime")) {
							this.SwingTime = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("KnockBackOnNoDeath")) {
							this.KnockBackOnNoDeath = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("Alcoholic")) {
							this.Alcoholic = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("SplatBloodOnNoDeath")) {
							this.SplatBloodOnNoDeath = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("SwingAmountBeforeImpact")) {
							this.SwingAmountBeforeImpact = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("AmmoType")) {
							this.AmmoType = string3;
						} else if (string2.trim().equalsIgnoreCase("maxAmmo")) {
							this.maxAmmo = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("GunType")) {
							this.GunType = string3;
						} else if (string2.trim().equalsIgnoreCase("HitAngleMod")) {
							this.HitAngleMod = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("OtherHandRequire")) {
							this.OtherHandRequire = string3;
						} else if (string2.trim().equalsIgnoreCase("AlwaysWelcomeGift")) {
							this.AlwaysWelcomeGift = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("CantAttackWithLowestEndurance")) {
							this.CantAttackWithLowestEndurance = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("EnduranceMod")) {
							this.EnduranceMod = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("KnockdownMod")) {
							this.KnockdownMod = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("DoorDamage")) {
							this.DoorDamage = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("MaxHitCount")) {
							this.MaxHitCount = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("PhysicsObject")) {
							this.PhysicsObject = string3;
						} else if (string2.trim().equalsIgnoreCase("Count")) {
							this.Count = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("SwingAnim")) {
							this.SwingAnim = string3;
						} else if (string2.trim().equalsIgnoreCase("WeaponWeight")) {
							this.WeaponWeight = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("IdleAnim")) {
							this.IdleAnim = string3;
						} else if (string2.trim().equalsIgnoreCase("RunAnim")) {
							this.RunAnim = string3;
						} else if (string2.trim().equalsIgnoreCase("RequireInHandOrInventory")) {
							this.RequireInHandOrInventory = new ArrayList(Arrays.asList(string3.split("/")));
						} else if (string2.trim().equalsIgnoreCase("fireModePossibilities")) {
							this.fireModePossibilities = new ArrayList(Arrays.asList(string3.split("/")));
						} else if (string2.trim().equalsIgnoreCase("attachmentsProvided")) {
							this.attachmentsProvided = new ArrayList(Arrays.asList(string3.split(";")));
						} else if (string2.trim().equalsIgnoreCase("attachmentReplacement")) {
							this.attachmentReplacement = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("PushBackMod")) {
							this.PushBackMod = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("NPCSoundBoost")) {
							this.NPCSoundBoost = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("SplatNumber")) {
							this.SplatNumber = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("RangeFalloff")) {
							this.RangeFalloff = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("UseEndurance")) {
							this.UseEndurance = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ShareDamage")) {
							this.ShareDamage = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ShareEndurance")) {
							this.ShareEndurance = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("AlwaysKnockdown")) {
							this.AlwaysKnockdown = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("IsAimedFirearm")) {
							this.IsAimedFirearm = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("bulletOutSound")) {
							this.bulletOutSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("ShellFallSound")) {
							this.ShellFallSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("IsAimedHandWeapon")) {
							this.IsAimedHandWeapon = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("AimingMod")) {
							this.AimingMod = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("ProjectileCount")) {
							this.ProjectileCount = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("CanStack")) {
							this.IsAimedFirearm = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("HerbalistType")) {
							this.HerbalistType = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("CanBarricade")) {
							this.CanBarricade = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("UseWhileEquipped")) {
							this.UseWhileEquipped = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("UseWhileUnequipped")) {
							this.UseWhileUnequipped = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("TicksPerEquipUse")) {
							this.TicksPerEquipUse = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("DisappearOnUse")) {
							this.DisappearOnUse = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("Temperature")) {
							this.Temperature = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("Insulation")) {
							this.insulation = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("WindResistance")) {
							this.windresist = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("WaterResistance")) {
							this.waterresist = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("CloseKillMove")) {
							this.CloseKillMove = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("UseDelta")) {
							this.UseDelta = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("RainFactor")) {
							this.rainFactor = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("TorchDot")) {
							this.torchDot = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("NumberOfPages")) {
							this.NumberOfPages = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("SkillTrained")) {
							this.SkillTrained = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("LvlSkillTrained")) {
							this.LvlSkillTrained = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("NumLevelsTrained")) {
							this.NumLevelsTrained = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("Capacity")) {
							this.Capacity = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("MaxCapacity")) {
							this.maxCapacity = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ItemCapacity")) {
							this.itemCapacity = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ConditionAffectsCapacity")) {
							this.ConditionAffectsCapacity = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("BrakeForce")) {
							this.brakeForce = (float)Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ChanceToSpawnDamaged")) {
							this.chanceToSpawnDamaged = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("WeaponLength")) {
							this.WeaponLength = new Float(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ClipSize")) {
							this.ClipSize = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ReloadTime")) {
							this.reloadTime = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("AimingTime")) {
							this.aimingTime = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("AimingTimeModifier")) {
							this.aimingTimeModifier = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ReloadTimeModifier")) {
							this.reloadTimeModifier = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("HitChanceModifier")) {
							this.hitChanceModifier = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("WeightReduction")) {
							this.WeightReduction = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("CanBeEquipped")) {
							this.CanBeEquipped = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("SubCategory")) {
							this.SubCategory = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("ActivatedItem")) {
							this.ActivatedItem = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ProtectFromRainWhenEquipped")) {
							this.ProtectFromRainWhenEquipped = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("LightStrength")) {
							this.LightStrength = new Float(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("TorchCone")) {
							this.TorchCone = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("LightDistance")) {
							this.LightDistance = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("TwoHandWeapon")) {
							this.TwoHandWeapon = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("Tooltip")) {
							this.Tooltip = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("DisplayCategory")) {
							this.DisplayCategory = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("BadInMicrowave")) {
							this.BadInMicrowave = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("GoodHot")) {
							this.GoodHot = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("BadCold")) {
							this.BadCold = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("AlarmSound")) {
							this.AlarmSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("RequiresEquippedBothHands")) {
							this.RequiresEquippedBothHands = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ReplaceOnCooked")) {
							this.ReplaceOnCooked = Arrays.asList(string3.trim().split(";"));
						} else if (string2.trim().equalsIgnoreCase("CustomContextMenu")) {
							this.CustomContextMenu = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("Trap")) {
							this.Trap = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("Wet")) {
							this.isWet = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("WetCooldown")) {
							this.wetCooldown = Float.parseFloat(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ItemWhenDry")) {
							this.itemWhenDry = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("FishingLure")) {
							this.FishingLure = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("CanBeWrite")) {
							this.canBeWrite = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("PageToWrite")) {
							this.PageToWrite = Integer.parseInt(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("Spice")) {
							this.Spice = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("RemoveNegativeEffectOnCooked")) {
							this.RemoveNegativeEffectOnCooked = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("ClipSizeModifier")) {
							this.clipSizeModifier = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("RecoilDelayModifier")) {
							this.recoilDelayModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MaxRangeModifier")) {
							this.maxRangeModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("MinRangeModifier")) {
							this.minRangeRangedModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("DamageModifier")) {
							this.damageModifier = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("Map")) {
							this.map = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("PutInSound")) {
							this.PutInSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("CloseSound")) {
							this.CloseSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("OpenSound")) {
							this.OpenSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("BreakSound")) {
							this.breakSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("TreeDamage")) {
							this.treeDamage = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("CustomEatSound")) {
							this.customEatSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("AlcoholPower")) {
							this.alcoholPower = Float.parseFloat(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("BandagePower")) {
							this.bandagePower = Float.parseFloat(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("ReduceInfectionPower")) {
							this.ReduceInfectionPower = Float.parseFloat(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("OnCooked")) {
							this.OnCooked = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("OnlyAcceptCategory")) {
							this.OnlyAcceptCategory = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("AcceptItemFunction")) {
							this.AcceptItemFunction = StringUtils.discardNullOrWhitespace(string3);
						} else if (string2.trim().equalsIgnoreCase("Padlock")) {
							this.padlock = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("DigitalPadlock")) {
							this.digitalPadlock = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("triggerExplosionTimer")) {
							this.triggerExplosionTimer = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("sensorRange")) {
							this.sensorRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("remoteRange")) {
							this.remoteRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("CountDownSound")) {
							this.countDownSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("explosionSound")) {
							this.explosionSound = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("PlacedSprite")) {
							this.PlacedSprite = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("explosionTimer")) {
							this.explosionTimer = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("explosionRange")) {
							this.explosionRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("explosionPower")) {
							this.explosionPower = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("fireRange")) {
							this.fireRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("firePower")) {
							this.firePower = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("canBePlaced")) {
							this.canBePlaced = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("CanBeReused")) {
							this.canBeReused = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("canBeRemote")) {
							this.canBeRemote = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("remoteController")) {
							this.remoteController = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("smokeRange")) {
							this.smokeRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("noiseRange")) {
							this.noiseRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("noiseDuration")) {
							this.noiseDuration = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("extraDamage")) {
							this.extraDamage = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("TwoWay")) {
							this.twoWay = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("TransmitRange")) {
							this.transmitRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("MicRange")) {
							this.micRange = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("BaseVolumeRange")) {
							this.baseVolumeRange = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("IsPortable")) {
							this.isPortable = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("IsTelevision")) {
							this.isTelevision = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("MinChannel")) {
							this.minChannel = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("MaxChannel")) {
							this.maxChannel = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("UsesBattery")) {
							this.usesBattery = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("IsHighTier")) {
							this.isHighTier = Boolean.parseBoolean(string3.trim());
						} else if (string2.trim().equalsIgnoreCase("WorldObjectSprite")) {
							this.worldObjectSprite = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("fluReduction")) {
							this.fluReduction = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ReduceFoodSickness")) {
							this.ReduceFoodSickness = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("painReduction")) {
							this.painReduction = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ColorRed")) {
							this.colorRed = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ColorGreen")) {
							this.colorGreen = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ColorBlue")) {
							this.colorBlue = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("calories")) {
							this.calories = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("carbohydrates")) {
							this.carbohydrates = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("lipids")) {
							this.lipids = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("proteins")) {
							this.proteins = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("Packaged")) {
							this.packaged = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("CantBeFrozen")) {
							this.cantBeFrozen = string3.trim().equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("EvolvedRecipeName")) {
							Translator.setDefaultItemEvolvedRecipeName(this.getFullName(), string3);
							this.evolvedRecipeName = Translator.getItemEvolvedRecipeName(this.getFullName());
						} else if (string2.trim().equalsIgnoreCase("ReplaceOnRotten")) {
							this.ReplaceOnRotten = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("CantBeConsolided")) {
							this.cantBeConsolided = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("OnEat")) {
							this.onEat = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("KeepOnDeplete")) {
							this.keepOnDeplete = string3.equalsIgnoreCase("true");
						} else if (string2.trim().equalsIgnoreCase("VehicleType")) {
							this.vehicleType = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("ChanceToFall")) {
							this.chanceToFall = Integer.parseInt(string3);
						} else if (string2.trim().equalsIgnoreCase("conditionLowerOffroad")) {
							this.conditionLowerOffroad = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("ConditionLowerStandard")) {
							this.conditionLowerNormal = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("wheelFriction")) {
							this.wheelFriction = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("suspensionDamping")) {
							this.suspensionDamping = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("suspensionCompression")) {
							this.suspensionCompression = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("engineLoudness")) {
							this.engineLoudness = Float.parseFloat(string3);
						} else if (string2.trim().equalsIgnoreCase("attachmentType")) {
							this.attachmentType = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("makeUpType")) {
							this.makeUpType = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("consolidateOption")) {
							this.consolidateOption = string3.trim();
						} else if (string2.trim().equalsIgnoreCase("fabricType")) {
							this.fabricType = string3.trim();
						} else {
							String string6;
							if (string2.trim().equalsIgnoreCase("TeachedRecipes")) {
								this.teachedRecipes = new ArrayList();
								stringArray2 = string3.split(";");
								for (int1 = 0; int1 < stringArray2.length; ++int1) {
									string6 = stringArray2[int1].trim();
									this.teachedRecipes.add(string6);
									if (Translator.debug) {
										Translator.getRecipeName(string6);
									}
								}
							} else if (string2.trim().equalsIgnoreCase("MountOn")) {
								this.mountOn = new ArrayList();
								stringArray2 = string3.split(";");
								for (int1 = 0; int1 < stringArray2.length; ++int1) {
									this.mountOn.add(stringArray2[int1].trim());
								}
							} else if (string2.trim().equalsIgnoreCase("PartType")) {
								this.partType = string3;
							} else if (string2.trim().equalsIgnoreCase("ClothingItem")) {
								this.ClothingItem = string3;
							} else if (string2.trim().equalsIgnoreCase("EvolvedRecipe")) {
								stringArray2 = string3.split(";");
								for (int1 = 0; int1 < stringArray2.length; ++int1) {
									string6 = stringArray2[int1];
									string4 = null;
									int int3 = 0;
									boolean boolean1 = false;
									if (!string6.contains(":")) {
										string4 = string6;
									} else {
										string4 = string6.split(":")[0];
										String string7 = string6.split(":")[1];
										if (!string7.contains("|")) {
											int3 = Integer.parseInt(string6.split(":")[1]);
										} else {
											String[] stringArray3 = string7.split("\\|");
											for (int int4 = 0; int4 < stringArray3.length; ++int4) {
												if ("Cooked".equals(stringArray3[int4])) {
													boolean1 = true;
												}
											}

											int3 = Integer.parseInt(stringArray3[0]);
										}
									}

									ItemRecipe itemRecipe = new ItemRecipe(this.name, this.module.getName(), int3);
									EvolvedRecipe evolvedRecipe = null;
									Iterator iterator = ((ScriptModule)ScriptManager.instance.ModuleMap.get("Base")).EvolvedRecipeMap.iterator();
									while (iterator.hasNext()) {
										EvolvedRecipe evolvedRecipe2 = (EvolvedRecipe)iterator.next();
										if (evolvedRecipe2.name.equals(string4)) {
											evolvedRecipe = evolvedRecipe2;
											break;
										}
									}

									itemRecipe.cooked = boolean1;
									if (evolvedRecipe == null) {
										evolvedRecipe = new EvolvedRecipe(string4);
										((ScriptModule)ScriptManager.instance.ModuleMap.get("Base")).EvolvedRecipeMap.add(evolvedRecipe);
									}

									evolvedRecipe.itemsList.put(this.name, itemRecipe);
								}
							} else if (string2.trim().equalsIgnoreCase("StaticModel")) {
								this.staticModel = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("worldStaticModel")) {
								this.worldStaticModel = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("primaryAnimMask")) {
								this.primaryAnimMask = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("secondaryAnimMask")) {
								this.secondaryAnimMask = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("primaryAnimMaskAttachment")) {
								this.primaryAnimMaskAttachment = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("secondaryAnimMaskAttachment")) {
								this.secondaryAnimMaskAttachment = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("replaceInSecondHand")) {
								this.replaceInSecondHand = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("replaceInPrimaryHand")) {
								this.replaceInPrimaryHand = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("replaceWhenUnequip")) {
								this.replaceWhenUnequip = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("EatType")) {
								this.eatType = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("IconsForTexture")) {
								this.IconsForTexture = new ArrayList();
								stringArray2 = string3.split(";");
								for (int1 = 0; int1 < stringArray2.length; ++int1) {
									this.IconsForTexture.add(stringArray2[int1].trim());
								}
							} else if (string2.trim().equalsIgnoreCase("BloodLocation")) {
								this.bloodClothingType = new ArrayList();
								stringArray2 = string3.split(";");
								for (int1 = 0; int1 < stringArray2.length; ++int1) {
									this.bloodClothingType.add(BloodClothingType.fromString(stringArray2[int1].trim()));
								}
							} else if (string2.trim().equalsIgnoreCase("MediaCategory")) {
								this.recordedMediaCat = string3.trim();
							} else if (string2.trim().equalsIgnoreCase("AcceptMediaType")) {
								this.acceptMediaType = Byte.parseByte(string3.trim());
							} else if (string2.trim().equalsIgnoreCase("NoTransmit")) {
								this.noTransmit = Boolean.parseBoolean(string3.trim());
							} else if (string2.trim().equalsIgnoreCase("OBSOLETE")) {
								this.OBSOLETE = string3.trim().toLowerCase().equals("true");
							} else {
								String string8 = string2.trim();
								DebugLog.log("adding unknown item param \"" + string8 + "\" = \"" + string3.trim() + "\"");
								if (this.DefaultModData == null) {
									this.DefaultModData = LuaManager.platform.newTable();
								}

								try {
									Double Double1 = Double.parseDouble(string3.trim());
									this.DefaultModData.rawset(string2.trim(), Double1);
								} catch (Exception exception) {
									this.DefaultModData.rawset(string2.trim(), string3);
								}
							}
						}
					}
				}
			} catch (Exception exception2) {
				String string9 = string.trim();
				throw new InvalidParameterException("Error: " + string9 + " is not a valid parameter in item: " + this.name);
			}
		}
	}

	public int getLevelSkillTrained() {
		return this.LvlSkillTrained;
	}

	public int getNumLevelsTrained() {
		return this.NumLevelsTrained;
	}

	public int getMaxLevelTrained() {
		return this.LvlSkillTrained == -1 ? -1 : this.LvlSkillTrained + this.NumLevelsTrained;
	}

	public List getTeachedRecipes() {
		return this.teachedRecipes;
	}

	public float getTemperature() {
		return this.Temperature;
	}

	public void setTemperature(float float1) {
		this.Temperature = float1;
	}

	public boolean isConditionAffectsCapacity() {
		return this.ConditionAffectsCapacity;
	}

	public int getChanceToFall() {
		return this.chanceToFall;
	}

	public float getInsulation() {
		return this.insulation;
	}

	public void setInsulation(float float1) {
		this.insulation = float1;
	}

	public float getWindresist() {
		return this.windresist;
	}

	public void setWindresist(float float1) {
		this.windresist = float1;
	}

	public float getWaterresist() {
		return this.waterresist;
	}

	public void setWaterresist(float float1) {
		this.waterresist = float1;
	}

	public boolean getObsolete() {
		return this.OBSOLETE;
	}

	public String getAcceptItemFunction() {
		return this.AcceptItemFunction;
	}

	public ArrayList getBloodClothingType() {
		return this.bloodClothingType;
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{Module: " + (this.module != null ? this.module.name : "null") + ", Name:" + this.name + ", Type:" + this.type + "}";
	}

	public String getReplaceWhenUnequip() {
		return this.replaceWhenUnequip;
	}

	public void resolveItemTypes() {
		this.AmmoType = ScriptManager.instance.resolveItemType(this.module, this.AmmoType);
		this.magazineType = ScriptManager.instance.resolveItemType(this.module, this.magazineType);
		if (this.RequireInHandOrInventory != null) {
			for (int int1 = 0; int1 < this.RequireInHandOrInventory.size(); ++int1) {
				String string = (String)this.RequireInHandOrInventory.get(int1);
				string = ScriptManager.instance.resolveItemType(this.module, string);
				this.RequireInHandOrInventory.set(int1, string);
			}
		}
	}

	public short getRegistry_id() {
		return this.registry_id;
	}

	public void setRegistry_id(short short1) {
		if (this.registry_id != -1) {
			WorldDictionary.DebugPrintItem(short1);
			String string = this.getFullName() != null ? this.getFullName() : "unknown";
			throw new RuntimeException("Cannot override existing registry id, item: " + string);
		} else {
			this.registry_id = short1;
		}
	}

	public String getModID() {
		return this.modID;
	}

	public boolean getExistsAsVanilla() {
		return this.existsAsVanilla;
	}

	public String getFileAbsPath() {
		return this.fileAbsPath;
	}

	public void setModID(String string) {
		if (GameClient.bClient) {
			if (this.modID == null) {
				this.modID = string;
			} else if (!string.equals(this.modID) && Core.bDebug) {
				WorldDictionary.DebugPrintItem(this);
				throw new RuntimeException("Cannot override modID. ModID=" + (string != null ? string : "null"));
			}
		}
	}

	public String getRecordedMediaCat() {
		return this.recordedMediaCat;
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
		AlarmClock,
		AlarmClockClothing;

		private static Item.Type[] $values() {
			return new Item.Type[]{Normal, Weapon, Food, Literature, Drainable, Clothing, Container, WeaponPart, Key, KeyRing, Moveable, Radio, AlarmClock, AlarmClockClothing};
		}
	}
}
