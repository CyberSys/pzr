package zombie.inventory;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Key;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.network.GameClient;
import zombie.radio.ZomboidRadio;
import zombie.radio.media.MediaData;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.ItemReplacement;
import zombie.ui.ObjectTooltip;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.vehicles.VehiclePart;
import zombie.world.ItemInfo;
import zombie.world.WorldDictionary;


public class InventoryItem {
	protected IsoGameCharacter previousOwner = null;
	protected Item ScriptItem = null;
	protected ItemType cat;
	protected ItemContainer container;
	protected int containerX;
	protected int containerY;
	protected String name;
	protected String replaceOnUse;
	protected String replaceOnUseFullType;
	protected int ConditionMax;
	protected ItemContainer rightClickContainer;
	protected Texture texture;
	protected Texture texturerotten;
	protected Texture textureCooked;
	protected Texture textureBurnt;
	protected String type;
	protected String fullType;
	protected int uses;
	protected float Age;
	protected float LastAged;
	protected boolean IsCookable;
	protected float CookingTime;
	protected float MinutesToCook;
	protected float MinutesToBurn;
	public boolean Cooked;
	protected boolean Burnt;
	protected int OffAge;
	protected int OffAgeMax;
	protected float Weight;
	protected float ActualWeight;
	protected String WorldTexture;
	protected String Description;
	protected int Condition;
	protected String OffString;
	protected String FreshString;
	protected String CookedString;
	protected String UnCookedString;
	protected String FrozenString;
	protected String BurntString;
	private String brokenString;
	protected String module;
	protected float boredomChange;
	protected float unhappyChange;
	protected float stressChange;
	protected ArrayList Taken;
	protected IsoDirections placeDir;
	protected IsoDirections newPlaceDir;
	private KahluaTable table;
	public String ReplaceOnUseOn;
	public Color col;
	public boolean IsWaterSource;
	public boolean CanStoreWater;
	public boolean CanStack;
	private boolean activated;
	private boolean isTorchCone;
	private int lightDistance;
	private int Count;
	public float fatigueChange;
	public IsoWorldInventoryObject worldItem;
	private String customMenuOption;
	private String tooltip;
	private String displayCategory;
	private int haveBeenRepaired;
	private boolean broken;
	private String originalName;
	public int id;
	public boolean RequiresEquippedBothHands;
	public ByteBuffer byteData;
	public ArrayList extraItems;
	private boolean customName;
	private String breakSound;
	protected boolean alcoholic;
	private float alcoholPower;
	private float bandagePower;
	private float ReduceInfectionPower;
	private boolean customWeight;
	private boolean customColor;
	private int keyId;
	private boolean taintedWater;
	private boolean remoteController;
	private boolean canBeRemote;
	private int remoteControlID;
	private int remoteRange;
	private float colorRed;
	private float colorGreen;
	private float colorBlue;
	private String countDownSound;
	private String explosionSound;
	private IsoGameCharacter equipParent;
	private String evolvedRecipeName;
	private float metalValue;
	private float itemHeat;
	private float meltingTime;
	private String worker;
	private boolean isWet;
	private float wetCooldown;
	private String itemWhenDry;
	private boolean favorite;
	protected ArrayList requireInHandOrInventory;
	private String map;
	private String stashMap;
	public boolean keepOnDeplete;
	private boolean zombieInfected;
	private boolean rainFactorZero;
	private float itemCapacity;
	private int maxCapacity;
	private float brakeForce;
	private int chanceToSpawnDamaged;
	private float conditionLowerNormal;
	private float conditionLowerOffroad;
	private float wheelFriction;
	private float suspensionDamping;
	private float suspensionCompression;
	private float engineLoudness;
	protected ItemVisual visual;
	protected String staticModel;
	private ArrayList iconsForTexture;
	private ArrayList bloodClothingType;
	private int stashChance;
	private String ammoType;
	private int maxAmmo;
	private int currentAmmoCount;
	private String gunType;
	private String attachmentType;
	private ArrayList attachmentsProvided;
	private int attachedSlot;
	private String attachedSlotType;
	private String attachmentReplacement;
	private String attachedToModel;
	private String m_alternateModelName;
	private short registry_id;
	public int worldZRotation;
	public float worldScale;
	private short recordedMediaIndex;
	private byte mediaType;
	private boolean isInitialised;
	private final int maxTextLength;
	public float jobDelta;
	public String jobType;
	static ByteBuffer tempBuffer = ByteBuffer.allocate(20000);
	public String mainCategory;
	private boolean canBeActivated;
	private float lightStrength;
	public String CloseKillMove;
	private boolean beingFilled;

	public int getSaveType() {
		throw new RuntimeException("InventoryItem.getSaveType() not implemented for " + this.getClass().getName());
	}

	public IsoWorldInventoryObject getWorldItem() {
		return this.worldItem;
	}

	public void setEquipParent(IsoGameCharacter gameCharacter) {
		this.equipParent = gameCharacter;
	}

	public IsoGameCharacter getEquipParent() {
		return this.equipParent == null || this.equipParent.getPrimaryHandItem() != this && this.equipParent.getSecondaryHandItem() != this ? null : this.equipParent;
	}

	public String getBringToBearSound() {
		return this.getScriptItem().getBringToBearSound();
	}

	public String getEquipSound() {
		return this.getScriptItem().getEquipSound();
	}

	public String getUnequipSound() {
		return this.getScriptItem().getUnequipSound();
	}

	public void setWorldItem(IsoWorldInventoryObject worldInventoryObject) {
		this.worldItem = worldInventoryObject;
	}

	public void setJobDelta(float float1) {
		this.jobDelta = float1;
	}

	public float getJobDelta() {
		return this.jobDelta;
	}

	public void setJobType(String string) {
		this.jobType = string;
	}

	public String getJobType() {
		return this.jobType;
	}

	public boolean hasModData() {
		return this.table != null && !this.table.isEmpty();
	}

	public KahluaTable getModData() {
		if (this.table == null) {
			this.table = LuaManager.platform.newTable();
		}

		return this.table;
	}

	public void storeInByteData(IsoObject object) {
		tempBuffer.clear();
		try {
			object.save(tempBuffer, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		tempBuffer.flip();
		if (this.byteData == null || this.byteData.capacity() < tempBuffer.limit() - 2 + 8) {
			this.byteData = ByteBuffer.allocate(tempBuffer.limit() - 2 + 8);
		}

		tempBuffer.get();
		tempBuffer.get();
		this.byteData.clear();
		this.byteData.put((byte)87);
		this.byteData.put((byte)86);
		this.byteData.put((byte)69);
		this.byteData.put((byte)82);
		this.byteData.putInt(186);
		this.byteData.put(tempBuffer);
		this.byteData.flip();
	}

	public ByteBuffer getByteData() {
		return this.byteData;
	}

	public boolean isRequiresEquippedBothHands() {
		return this.RequiresEquippedBothHands;
	}

	public float getA() {
		return this.col.a;
	}

	public float getR() {
		return this.col.r;
	}

	public float getG() {
		return this.col.g;
	}

	public float getB() {
		return this.col.b;
	}

	public InventoryItem(String string, String string2, String string3, String string4) {
		this.cat = ItemType.None;
		this.containerX = 0;
		this.containerY = 0;
		this.replaceOnUse = null;
		this.replaceOnUseFullType = null;
		this.ConditionMax = 10;
		this.rightClickContainer = null;
		this.uses = 1;
		this.Age = 0.0F;
		this.LastAged = -1.0F;
		this.IsCookable = false;
		this.CookingTime = 0.0F;
		this.MinutesToCook = 60.0F;
		this.MinutesToBurn = 120.0F;
		this.Cooked = false;
		this.Burnt = false;
		this.OffAge = 1000000000;
		this.OffAgeMax = 1000000000;
		this.Weight = 1.0F;
		this.ActualWeight = 1.0F;
		this.Condition = 10;
		this.OffString = Translator.getText("Tooltip_food_Rotten");
		this.FreshString = Translator.getText("Tooltip_food_Fresh");
		this.CookedString = Translator.getText("Tooltip_food_Cooked");
		this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
		this.FrozenString = Translator.getText("Tooltip_food_Frozen");
		this.BurntString = Translator.getText("Tooltip_food_Burnt");
		this.brokenString = Translator.getText("Tooltip_broken");
		this.module = "Base";
		this.boredomChange = 0.0F;
		this.unhappyChange = 0.0F;
		this.stressChange = 0.0F;
		this.Taken = new ArrayList();
		this.placeDir = IsoDirections.Max;
		this.newPlaceDir = IsoDirections.Max;
		this.table = null;
		this.ReplaceOnUseOn = null;
		this.col = Color.white;
		this.IsWaterSource = false;
		this.CanStoreWater = false;
		this.CanStack = false;
		this.activated = false;
		this.isTorchCone = false;
		this.lightDistance = 0;
		this.Count = 1;
		this.fatigueChange = 0.0F;
		this.worldItem = null;
		this.customMenuOption = null;
		this.tooltip = null;
		this.displayCategory = null;
		this.haveBeenRepaired = 1;
		this.broken = false;
		this.originalName = null;
		this.id = 0;
		this.extraItems = null;
		this.customName = false;
		this.breakSound = null;
		this.alcoholic = false;
		this.alcoholPower = 0.0F;
		this.bandagePower = 0.0F;
		this.ReduceInfectionPower = 0.0F;
		this.customWeight = false;
		this.customColor = false;
		this.keyId = -1;
		this.taintedWater = false;
		this.remoteController = false;
		this.canBeRemote = false;
		this.remoteControlID = -1;
		this.remoteRange = 0;
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.countDownSound = null;
		this.explosionSound = null;
		this.equipParent = null;
		this.evolvedRecipeName = null;
		this.metalValue = 0.0F;
		this.itemHeat = 1.0F;
		this.meltingTime = 0.0F;
		this.isWet = false;
		this.wetCooldown = -1.0F;
		this.itemWhenDry = null;
		this.favorite = false;
		this.requireInHandOrInventory = null;
		this.map = null;
		this.stashMap = null;
		this.keepOnDeplete = false;
		this.zombieInfected = false;
		this.rainFactorZero = false;
		this.itemCapacity = -1.0F;
		this.maxCapacity = -1;
		this.brakeForce = 0.0F;
		this.chanceToSpawnDamaged = 0;
		this.conditionLowerNormal = 0.0F;
		this.conditionLowerOffroad = 0.0F;
		this.wheelFriction = 0.0F;
		this.suspensionDamping = 0.0F;
		this.suspensionCompression = 0.0F;
		this.engineLoudness = 0.0F;
		this.visual = null;
		this.staticModel = null;
		this.iconsForTexture = null;
		this.bloodClothingType = new ArrayList();
		this.stashChance = 80;
		this.ammoType = null;
		this.maxAmmo = 0;
		this.currentAmmoCount = 0;
		this.gunType = null;
		this.attachmentType = null;
		this.attachmentsProvided = null;
		this.attachedSlot = -1;
		this.attachedSlotType = null;
		this.attachmentReplacement = null;
		this.attachedToModel = null;
		this.m_alternateModelName = null;
		this.registry_id = -1;
		this.worldZRotation = -1;
		this.worldScale = 1.0F;
		this.recordedMediaIndex = -1;
		this.mediaType = -1;
		this.isInitialised = false;
		this.maxTextLength = 256;
		this.jobDelta = 0.0F;
		this.jobType = null;
		this.mainCategory = null;
		this.CloseKillMove = null;
		this.beingFilled = false;
		this.col = Color.white;
		this.texture = Texture.trygetTexture(string4);
		if (this.texture == null) {
			this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
		}

		this.module = string;
		this.name = string2;
		this.originalName = string2;
		this.type = string3;
		this.fullType = string + "." + string3;
		this.WorldTexture = string4.replace("Item_", "media/inventory/world/WItem_");
		this.WorldTexture = this.WorldTexture + ".png";
	}

	public InventoryItem(String string, String string2, String string3, Item item) {
		this.cat = ItemType.None;
		this.containerX = 0;
		this.containerY = 0;
		this.replaceOnUse = null;
		this.replaceOnUseFullType = null;
		this.ConditionMax = 10;
		this.rightClickContainer = null;
		this.uses = 1;
		this.Age = 0.0F;
		this.LastAged = -1.0F;
		this.IsCookable = false;
		this.CookingTime = 0.0F;
		this.MinutesToCook = 60.0F;
		this.MinutesToBurn = 120.0F;
		this.Cooked = false;
		this.Burnt = false;
		this.OffAge = 1000000000;
		this.OffAgeMax = 1000000000;
		this.Weight = 1.0F;
		this.ActualWeight = 1.0F;
		this.Condition = 10;
		this.OffString = Translator.getText("Tooltip_food_Rotten");
		this.FreshString = Translator.getText("Tooltip_food_Fresh");
		this.CookedString = Translator.getText("Tooltip_food_Cooked");
		this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
		this.FrozenString = Translator.getText("Tooltip_food_Frozen");
		this.BurntString = Translator.getText("Tooltip_food_Burnt");
		this.brokenString = Translator.getText("Tooltip_broken");
		this.module = "Base";
		this.boredomChange = 0.0F;
		this.unhappyChange = 0.0F;
		this.stressChange = 0.0F;
		this.Taken = new ArrayList();
		this.placeDir = IsoDirections.Max;
		this.newPlaceDir = IsoDirections.Max;
		this.table = null;
		this.ReplaceOnUseOn = null;
		this.col = Color.white;
		this.IsWaterSource = false;
		this.CanStoreWater = false;
		this.CanStack = false;
		this.activated = false;
		this.isTorchCone = false;
		this.lightDistance = 0;
		this.Count = 1;
		this.fatigueChange = 0.0F;
		this.worldItem = null;
		this.customMenuOption = null;
		this.tooltip = null;
		this.displayCategory = null;
		this.haveBeenRepaired = 1;
		this.broken = false;
		this.originalName = null;
		this.id = 0;
		this.extraItems = null;
		this.customName = false;
		this.breakSound = null;
		this.alcoholic = false;
		this.alcoholPower = 0.0F;
		this.bandagePower = 0.0F;
		this.ReduceInfectionPower = 0.0F;
		this.customWeight = false;
		this.customColor = false;
		this.keyId = -1;
		this.taintedWater = false;
		this.remoteController = false;
		this.canBeRemote = false;
		this.remoteControlID = -1;
		this.remoteRange = 0;
		this.colorRed = 1.0F;
		this.colorGreen = 1.0F;
		this.colorBlue = 1.0F;
		this.countDownSound = null;
		this.explosionSound = null;
		this.equipParent = null;
		this.evolvedRecipeName = null;
		this.metalValue = 0.0F;
		this.itemHeat = 1.0F;
		this.meltingTime = 0.0F;
		this.isWet = false;
		this.wetCooldown = -1.0F;
		this.itemWhenDry = null;
		this.favorite = false;
		this.requireInHandOrInventory = null;
		this.map = null;
		this.stashMap = null;
		this.keepOnDeplete = false;
		this.zombieInfected = false;
		this.rainFactorZero = false;
		this.itemCapacity = -1.0F;
		this.maxCapacity = -1;
		this.brakeForce = 0.0F;
		this.chanceToSpawnDamaged = 0;
		this.conditionLowerNormal = 0.0F;
		this.conditionLowerOffroad = 0.0F;
		this.wheelFriction = 0.0F;
		this.suspensionDamping = 0.0F;
		this.suspensionCompression = 0.0F;
		this.engineLoudness = 0.0F;
		this.visual = null;
		this.staticModel = null;
		this.iconsForTexture = null;
		this.bloodClothingType = new ArrayList();
		this.stashChance = 80;
		this.ammoType = null;
		this.maxAmmo = 0;
		this.currentAmmoCount = 0;
		this.gunType = null;
		this.attachmentType = null;
		this.attachmentsProvided = null;
		this.attachedSlot = -1;
		this.attachedSlotType = null;
		this.attachmentReplacement = null;
		this.attachedToModel = null;
		this.m_alternateModelName = null;
		this.registry_id = -1;
		this.worldZRotation = -1;
		this.worldScale = 1.0F;
		this.recordedMediaIndex = -1;
		this.mediaType = -1;
		this.isInitialised = false;
		this.maxTextLength = 256;
		this.jobDelta = 0.0F;
		this.jobType = null;
		this.mainCategory = null;
		this.CloseKillMove = null;
		this.beingFilled = false;
		this.col = Color.white;
		this.texture = item.NormalTexture;
		this.module = string;
		this.name = string2;
		this.originalName = string2;
		this.type = string3;
		this.fullType = string + "." + string3;
		this.WorldTexture = item.WorldTextureName;
	}

	public String getType() {
		return this.type;
	}

	public Texture getTex() {
		return this.texture;
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Item";
	}

	public boolean IsRotten() {
		return this.Age > (float)this.OffAge;
	}

	public float HowRotten() {
		if (this.OffAgeMax - this.OffAge == 0) {
			return this.Age > (float)this.OffAge ? 1.0F : 0.0F;
		} else {
			float float1 = (this.Age - (float)this.OffAge) / (float)(this.OffAgeMax - this.OffAge);
			return float1;
		}
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return false;
	}

	public boolean ModDataMatches(InventoryItem inventoryItem) {
		KahluaTable kahluaTable = inventoryItem.getModData();
		KahluaTable kahluaTable2 = inventoryItem.getModData();
		if (kahluaTable == null && kahluaTable2 == null) {
			return true;
		} else if (kahluaTable == null) {
			return false;
		} else if (kahluaTable2 == null) {
			return false;
		} else if (kahluaTable.len() != kahluaTable2.len()) {
			return false;
		} else {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			Object object;
			Object object2;
			do {
				if (!kahluaTableIterator.advance()) {
					return true;
				}

				object = kahluaTable2.rawget(kahluaTableIterator.getKey());
				object2 = kahluaTableIterator.getValue();
			}	 while (object.equals(object2));

			return false;
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		objectTooltip.render();
		UIFont uIFont = objectTooltip.getFont();
		int int1 = objectTooltip.getLineSpacing();
		byte byte1 = 5;
		String string = "";
		if (this.Burnt) {
			string = string + this.BurntString + " ";
		} else if (this.OffAge < 1000000000 && this.Age < (float)this.OffAge) {
			string = string + this.FreshString + " ";
		} else if (this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax) {
			string = string + this.OffString + " ";
		}

		if (this.isCooked() && !this.Burnt) {
			string = string + this.CookedString + " ";
		} else if (this.IsCookable && !this.Burnt && !(this instanceof DrainableComboItem)) {
			string = string + this.UnCookedString + " ";
		}

		if (this instanceof Food && ((Food)this).isFrozen()) {
			string = string + this.FrozenString + " ";
		}

		string = string.trim();
		String string2;
		if (string.isEmpty()) {
			objectTooltip.DrawText(uIFont, string2 = this.getName(), 5.0, (double)byte1, 1.0, 1.0, 0.800000011920929, 1.0);
		} else if (this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax) {
			objectTooltip.DrawText(uIFont, string2 = Translator.getText("IGUI_FoodNaming", string, this.name), 5.0, (double)byte1, 1.0, 0.10000000149011612, 0.10000000149011612, 1.0);
		} else {
			objectTooltip.DrawText(uIFont, string2 = Translator.getText("IGUI_FoodNaming", string, this.name), 5.0, (double)byte1, 1.0, 1.0, 0.800000011920929, 1.0);
		}

		objectTooltip.adjustWidth(5, string2);
		int int2 = byte1 + int1 + 5;
		int int3;
		int int4;
		int int5;
		InventoryItem inventoryItem;
		if (this.extraItems != null) {
			objectTooltip.DrawText(uIFont, Translator.getText("Tooltip_item_Contains"), 5.0, (double)int2, 1.0, 1.0, 0.800000011920929, 1.0);
			int3 = 5 + TextManager.instance.MeasureStringX(uIFont, Translator.getText("Tooltip_item_Contains")) + 4;
			int4 = (int1 - 10) / 2;
			for (int5 = 0; int5 < this.extraItems.size(); ++int5) {
				inventoryItem = InventoryItemFactory.CreateItem((String)this.extraItems.get(int5));
				objectTooltip.DrawTextureScaled(inventoryItem.getTex(), (double)int3, (double)(int2 + int4), 10.0, 10.0, 1.0);
				int3 += 11;
			}

			int2 = int2 + int1 + 5;
		}

		if (this instanceof Food && ((Food)this).spices != null) {
			objectTooltip.DrawText(uIFont, Translator.getText("Tooltip_item_Spices"), 5.0, (double)int2, 1.0, 1.0, 0.800000011920929, 1.0);
			int3 = 5 + TextManager.instance.MeasureStringX(uIFont, Translator.getText("Tooltip_item_Spices")) + 4;
			int4 = (int1 - 10) / 2;
			for (int5 = 0; int5 < ((Food)this).spices.size(); ++int5) {
				inventoryItem = InventoryItemFactory.CreateItem((String)((Food)this).spices.get(int5));
				objectTooltip.DrawTextureScaled(inventoryItem.getTex(), (double)int3, (double)(int2 + int4), 10.0, 10.0, 1.0);
				int3 += 11;
			}

			int2 = int2 + int1 + 5;
		}

		ObjectTooltip.Layout layout = objectTooltip.beginLayout();
		layout.setMinLabelWidth(80);
		ObjectTooltip.LayoutItem layoutItem = layout.addItem();
		layoutItem.setLabel(Translator.getText("Tooltip_item_Weight") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
		boolean boolean1 = this.isEquipped();
		String string3;
		float float1;
		if (!(this instanceof HandWeapon) && !(this instanceof Clothing) && !(this instanceof DrainableComboItem)) {
			float1 = this.getUnequippedWeight();
			if (float1 > 0.0F && float1 < 0.01F) {
				float1 = 0.01F;
			}

			layoutItem.setValueRightNoPlus(float1);
		} else if (boolean1) {
			string3 = this.getCleanString(this.getEquippedWeight());
			layoutItem.setValue(string3 + "	(" + this.getCleanString(this.getUnequippedWeight()) + " " + Translator.getText("Tooltip_item_Unequipped") + ")", 1.0F, 1.0F, 1.0F, 1.0F);
		} else if (this.getAttachedSlot() > -1) {
			string3 = this.getCleanString(this.getHotbarEquippedWeight());
			layoutItem.setValue(string3 + "	(" + this.getCleanString(this.getUnequippedWeight()) + " " + Translator.getText("Tooltip_item_Unequipped") + ")", 1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			string3 = this.getCleanString(this.getUnequippedWeight());
			layoutItem.setValue(string3 + "	(" + this.getCleanString(this.getEquippedWeight()) + " " + Translator.getText("Tooltip_item_Equipped") + ")", 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (objectTooltip.getWeightOfStack() > 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_StackWeight") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			float1 = objectTooltip.getWeightOfStack();
			if (float1 > 0.0F && float1 < 0.01F) {
				float1 = 0.01F;
			}

			layoutItem.setValueRightNoPlus(float1);
		}

		if (this.getMaxAmmo() > 0 && !(this instanceof HandWeapon)) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValue(this.getCurrentAmmoCount() + " / " + this.getMaxAmmo(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.gunType != null) {
			Item item = ScriptManager.instance.FindItem(this.getGunType());
			if (item == null) {
				ScriptManager scriptManager = ScriptManager.instance;
				string3 = this.getModule();
				item = scriptManager.FindItem(string3 + "." + this.ammoType);
			}

			if (item != null) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("ContextMenu_GunType") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValue(item.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel("getActualWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getActualWeight());
			layoutItem = layout.addItem();
			layoutItem.setLabel("getWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getWeight());
			layoutItem = layout.addItem();
			layoutItem.setLabel("getEquippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getEquippedWeight());
			layoutItem = layout.addItem();
			layoutItem.setLabel("getUnequippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getUnequippedWeight());
			layoutItem = layout.addItem();
			layoutItem.setLabel("getContentsWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getContentsWeight());
			if (this instanceof Key || "Doorknob".equals(this.type)) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: keyId", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus(this.getKeyId());
			}

			layoutItem = layout.addItem();
			layoutItem.setLabel("ID", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.id);
			layoutItem = layout.addItem();
			layoutItem.setLabel("DictionaryID", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.registry_id);
			ClothingItem clothingItem = this.getClothingItem();
			if (clothingItem != null) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("ClothingItem", 1.0F, 1.0F, 1.0F, 1.0F);
				layoutItem.setValue(this.getClothingItem().m_Name, 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		if (this.getFatigueChange() != 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_Fatigue") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight((int)(this.getFatigueChange() * 100.0F), false);
		}

		if (this instanceof DrainableComboItem) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("IGUI_invpanel_Remaining") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
			float1 = ((DrainableComboItem)this).getUsedDelta();
			layoutItem.setProgress(float1, 0.0F, 0.6F, 0.0F, 0.7F);
		}

		if (this.isTaintedWater()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_TaintedWater"), 1.0F, 0.5F, 0.5F, 1.0F);
		}

		this.DoTooltip(objectTooltip, layout);
		if (this.getRemoteControlID() != -1) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_TrapControllerID"), 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValue(Integer.toString(this.getRemoteControlID()), 1.0F, 1.0F, 0.8F, 1.0F);
		}

		if (!FixingManager.getFixes(this).isEmpty()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Repaired") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			if (this.getHaveBeenRepaired() == 1) {
				layoutItem.setValue(Translator.getText("Tooltip_never"), 1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				layoutItem.setValue(this.getHaveBeenRepaired() - 1 + "x", 1.0F, 1.0F, 1.0F, 1.0F);
			}
		}

		if (this.isEquippedNoSprint()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_CantSprintEquipped"), 1.0F, 0.1F, 0.1F, 1.0F);
		}

		if (this.isWet()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_Wetness") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
			float1 = this.getWetCooldown() / 10000.0F;
			layoutItem.setProgress(float1, 0.0F, 0.6F, 0.0F, 0.7F);
		}

		if (this.getMaxCapacity() > 0) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			float1 = (float)this.getMaxCapacity();
			if (this.isConditionAffectsCapacity()) {
				float1 = VehiclePart.getNumberByCondition((float)this.getMaxCapacity(), (float)this.getCondition(), 5.0F);
			}

			if (this.getItemCapacity() > -1.0F) {
				layoutItem.setValue(this.getItemCapacity() + " / " + float1, 1.0F, 1.0F, 0.8F, 1.0F);
			} else {
				layoutItem.setValue("0 / " + float1, 1.0F, 1.0F, 0.8F, 1.0F);
			}
		}

		if (this.getConditionMax() > 0 && this.getMechanicType() > 0) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValue(this.getCondition() + " / " + this.getConditionMax(), 1.0F, 1.0F, 0.8F, 1.0F);
		}

		if (this.isRecordedMedia()) {
			MediaData mediaData = this.getMediaData();
			if (mediaData != null) {
				if (mediaData.getTranslatedTitle() != null) {
					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_media_title") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					layoutItem.setValue(mediaData.getTranslatedTitle(), 1.0F, 1.0F, 1.0F, 1.0F);
					if (mediaData.getTranslatedSubTitle() != null) {
						layoutItem = layout.addItem();
						layoutItem.setLabel("", 1.0F, 1.0F, 0.8F, 1.0F);
						layoutItem.setValue(mediaData.getTranslatedSubTitle(), 1.0F, 1.0F, 1.0F, 1.0F);
					}
				}

				if (mediaData.getTranslatedAuthor() != null) {
					layoutItem = layout.addItem();
					layoutItem.setLabel(Translator.getText("Tooltip_media_author") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					layoutItem.setValue(mediaData.getTranslatedAuthor(), 1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}

		if (Core.getInstance().getOptionShowItemModInfo() && !this.isVanilla()) {
			layoutItem = layout.addItem();
			Color color = Colors.CornFlowerBlue;
			layoutItem.setLabel("Mod: " + this.getModName(), color.r, color.g, color.b, 1.0F);
			ItemInfo itemInfo = WorldDictionary.getItemInfoFromID(this.registry_id);
			if (itemInfo != null && itemInfo.getModOverrides() != null) {
				layoutItem = layout.addItem();
				float float2 = 0.5F;
				if (itemInfo.getModOverrides().size() == 1) {
					layoutItem.setLabel("This item overrides: " + WorldDictionary.getModNameFromID((String)itemInfo.getModOverrides().get(0)), float2, float2, float2, 1.0F);
				} else {
					layoutItem.setLabel("This item overrides:", float2, float2, float2, 1.0F);
					for (int int6 = 0; int6 < itemInfo.getModOverrides().size(); ++int6) {
						layoutItem = layout.addItem();
						layoutItem.setLabel(" - " + WorldDictionary.getModNameFromID((String)itemInfo.getModOverrides().get(int6)), float2, float2, float2, 1.0F);
					}
				}
			}
		}

		if (this.getTooltip() != null) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText(this.tooltip), 1.0F, 1.0F, 0.8F, 1.0F);
		}

		int2 = layout.render(5, int2, objectTooltip);
		objectTooltip.endLayout(layout);
		int2 += objectTooltip.padBottom;
		objectTooltip.setHeight((double)int2);
		if (objectTooltip.getWidth() < 150.0) {
			objectTooltip.setWidth(150.0);
		}
	}

	public String getCleanString(float float1) {
		float float2 = (float)((int)(((double)float1 + 0.005) * 100.0)) / 100.0F;
		String string = Float.toString(float2);
		return string;
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
	}

	public void SetContainerPosition(int int1, int int2) {
		this.containerX = int1;
		this.containerY = int2;
	}

	public void Use() {
		this.Use(false);
	}

	public void UseItem() {
		this.Use(false);
	}

	public void Use(boolean boolean1) {
		this.Use(boolean1, false);
	}

	public void Use(boolean boolean1, boolean boolean2) {
		if (this.isDisappearOnUse() || boolean1) {
			--this.uses;
			if (this.replaceOnUse != null && !boolean2 && !boolean1 && this.container != null) {
				String string = this.replaceOnUse;
				if (!this.replaceOnUse.contains(".")) {
					string = this.module + "." + string;
				}

				InventoryItem inventoryItem = this.container.AddItem(string);
				if (inventoryItem != null) {
					inventoryItem.setConditionFromModData(this);
				}

				this.container.setDrawDirty(true);
				this.container.setDirty(true);
				inventoryItem.setFavorite(this.isFavorite());
			}

			if (this.uses <= 0) {
				if (this.keepOnDeplete) {
					return;
				}

				if (this.container != null) {
					if (this.container.parent instanceof IsoGameCharacter && !(this instanceof HandWeapon)) {
						IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
						gameCharacter.removeFromHands(this);
					}

					this.container.Items.remove(this);
					this.container.setDirty(true);
					this.container.setDrawDirty(true);
					this.container = null;
				}
			}
		}
	}

	public boolean shouldUpdateInWorld() {
		if (!GameClient.bClient && !this.rainFactorZero && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null) {
			IsoGridSquare square = this.getWorldItem().getSquare();
			return square != null && square.isOutside();
		} else {
			return false;
		}
	}

	public void update() {
		if (this.isWet()) {
			this.wetCooldown -= 1.0F * GameTime.instance.getMultiplier();
			if (this.wetCooldown <= 0.0F) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(this.itemWhenDry);
				if (this.isFavorite()) {
					inventoryItem.setFavorite(true);
				}

				IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
				if (worldInventoryObject != null) {
					IsoGridSquare square = worldInventoryObject.getSquare();
					square.AddWorldInventoryItem(inventoryItem, worldInventoryObject.getX() % 1.0F, worldInventoryObject.getY() % 1.0F, worldInventoryObject.getZ() % 1.0F);
					square.transmitRemoveItemFromSquare(worldInventoryObject);
					if (this.getContainer() != null) {
						this.getContainer().setDirty(true);
						this.getContainer().setDrawDirty(true);
					}

					square.chunk.recalcHashCodeObjects();
					this.setWorldItem((IsoWorldInventoryObject)null);
				} else if (this.getContainer() != null) {
					this.getContainer().addItem(inventoryItem);
					this.getContainer().Remove(this);
				}

				this.setWet(false);
				IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
				LuaEventManager.triggerEvent("OnContainerUpdate");
			}
		}

		if (!GameClient.bClient && !this.rainFactorZero && this.getWorldItem() != null && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && RainManager.isRaining()) {
			IsoWorldInventoryObject worldInventoryObject2 = this.getWorldItem();
			IsoGridSquare square2 = worldInventoryObject2.getSquare();
			if (square2 != null && square2.isOutside()) {
				InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(this.getReplaceOnUseOnString());
				inventoryItem2.setCondition(this.getCondition());
				if (inventoryItem2 instanceof DrainableComboItem && inventoryItem2.canStoreWater()) {
					if (((DrainableComboItem)inventoryItem2).getRainFactor() == 0.0F) {
						this.rainFactorZero = true;
						return;
					}

					((DrainableComboItem)inventoryItem2).setUsedDelta(0.0F);
					worldInventoryObject2.swapItem(inventoryItem2);
				}
			}
		}
	}

	public boolean finishupdate() {
		if (!GameClient.bClient && !this.rainFactorZero && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && this.getWorldItem() != null && this.getWorldItem().getObjectIndex() != -1) {
			return false;
		} else {
			return !this.isWet();
		}
	}

	public void updateSound(BaseSoundEmitter baseSoundEmitter) {
	}

	public String getFullType() {
		assert this.fullType != null && this.fullType.equals(this.module + "." + this.type);
		return this.fullType;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		boolean1 = false;
		if (GameWindow.DEBUG_SAVE) {
			DebugLog.log(this.getFullType());
		}

		byteBuffer.putShort(this.registry_id);
		byteBuffer.put((byte)this.getSaveType());
		byteBuffer.putInt(this.id);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
		if (this.uses != 1) {
			bitHeaderWrite.addFlags(1);
			if (this.uses > 32767) {
				byteBuffer.putShort((short)32767);
			} else {
				byteBuffer.putShort((short)this.uses);
			}
		}

		if (this.IsDrainable() && ((DrainableComboItem)this).getUsedDelta() < 1.0F) {
			bitHeaderWrite.addFlags(2);
			float float1 = ((DrainableComboItem)this).getUsedDelta();
			byte byte1 = (byte)((byte)((int)(float1 * 255.0F)) + -128);
			byteBuffer.put(byte1);
		}

		if (this.Condition != this.ConditionMax) {
			bitHeaderWrite.addFlags(4);
			byteBuffer.put((byte)this.getCondition());
		}

		if (this.visual != null) {
			bitHeaderWrite.addFlags(8);
			this.visual.save(byteBuffer);
		}

		if (this.isCustomColor() && (this.col.r != 1.0F || this.col.g != 1.0F || this.col.b != 1.0F || this.col.a != 1.0F)) {
			bitHeaderWrite.addFlags(16);
			byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().r));
			byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().g));
			byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().b));
			byteBuffer.put(Bits.packFloatUnitToByte(this.getColor().a));
		}

		if (this.itemCapacity != -1.0F) {
			bitHeaderWrite.addFlags(32);
			byteBuffer.putFloat(this.itemCapacity);
		}

		BitHeaderWrite bitHeaderWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
		if (this.table != null && !this.table.isEmpty()) {
			bitHeaderWrite2.addFlags(1);
			this.table.save(byteBuffer);
		}

		if (this.isActivated()) {
			bitHeaderWrite2.addFlags(2);
		}

		if (this.haveBeenRepaired != 1) {
			bitHeaderWrite2.addFlags(4);
			byteBuffer.putShort((short)this.getHaveBeenRepaired());
		}

		if (this.name != null && !this.name.equals(this.originalName)) {
			bitHeaderWrite2.addFlags(8);
			GameWindow.WriteString(byteBuffer, this.name);
		}

		if (this.byteData != null) {
			bitHeaderWrite2.addFlags(16);
			this.byteData.rewind();
			byteBuffer.putInt(this.byteData.limit());
			byteBuffer.put(this.byteData);
			this.byteData.flip();
		}

		if (this.extraItems != null && this.extraItems.size() > 0) {
			bitHeaderWrite2.addFlags(32);
			byteBuffer.putInt(this.extraItems.size());
			for (int int1 = 0; int1 < this.extraItems.size(); ++int1) {
				byteBuffer.putShort(WorldDictionary.getItemRegistryID((String)this.extraItems.get(int1)));
			}
		}

		if (this.isCustomName()) {
			bitHeaderWrite2.addFlags(64);
		}

		if (this.isCustomWeight()) {
			bitHeaderWrite2.addFlags(128);
			byteBuffer.putFloat(this.isCustomWeight() ? this.getActualWeight() : -1.0F);
		}

		if (this.keyId != -1) {
			bitHeaderWrite2.addFlags(256);
			byteBuffer.putInt(this.getKeyId());
		}

		if (this.isTaintedWater()) {
			bitHeaderWrite2.addFlags(512);
		}

		if (this.remoteControlID != -1 || this.remoteRange != 0) {
			bitHeaderWrite2.addFlags(1024);
			byteBuffer.putInt(this.getRemoteControlID());
			byteBuffer.putInt(this.getRemoteRange());
		}

		if (this.colorRed != 1.0F || this.colorGreen != 1.0F || this.colorBlue != 1.0F) {
			bitHeaderWrite2.addFlags(2048);
			byteBuffer.put(Bits.packFloatUnitToByte(this.colorRed));
			byteBuffer.put(Bits.packFloatUnitToByte(this.colorGreen));
			byteBuffer.put(Bits.packFloatUnitToByte(this.colorBlue));
		}

		if (this.worker != null) {
			bitHeaderWrite2.addFlags(4096);
			GameWindow.WriteString(byteBuffer, this.getWorker());
		}

		if (this.wetCooldown != -1.0F) {
			bitHeaderWrite2.addFlags(8192);
			byteBuffer.putFloat(this.wetCooldown);
		}

		if (this.isFavorite()) {
			bitHeaderWrite2.addFlags(16384);
		}

		if (this.stashMap != null) {
			bitHeaderWrite2.addFlags(32768);
			GameWindow.WriteString(byteBuffer, this.stashMap);
		}

		if (this.isInfected()) {
			bitHeaderWrite2.addFlags(65536);
		}

		if (this.currentAmmoCount != 0) {
			bitHeaderWrite2.addFlags(131072);
			byteBuffer.putInt(this.currentAmmoCount);
		}

		if (this.attachedSlot != -1) {
			bitHeaderWrite2.addFlags(262144);
			byteBuffer.putInt(this.attachedSlot);
		}

		if (this.attachedSlotType != null) {
			bitHeaderWrite2.addFlags(524288);
			GameWindow.WriteString(byteBuffer, this.attachedSlotType);
		}

		if (this.attachedToModel != null) {
			bitHeaderWrite2.addFlags(1048576);
			GameWindow.WriteString(byteBuffer, this.attachedToModel);
		}

		if (this.maxCapacity != -1) {
			bitHeaderWrite2.addFlags(2097152);
			byteBuffer.putInt(this.maxCapacity);
		}

		if (this.isRecordedMedia()) {
			bitHeaderWrite2.addFlags(4194304);
			byteBuffer.putShort(this.recordedMediaIndex);
		}

		if (this.worldZRotation > -1) {
			bitHeaderWrite2.addFlags(8388608);
			byteBuffer.putInt(this.worldZRotation);
		}

		if (this.worldScale != 1.0F) {
			bitHeaderWrite2.addFlags(16777216);
			byteBuffer.putFloat(this.worldScale);
		}

		if (this.isInitialised) {
			bitHeaderWrite2.addFlags(33554432);
		}

		if (!bitHeaderWrite2.equals(0)) {
			bitHeaderWrite.addFlags(64);
			bitHeaderWrite2.write();
		} else {
			byteBuffer.position(bitHeaderWrite2.getStartPosition());
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
		bitHeaderWrite2.release();
	}

	public static InventoryItem loadItem(ByteBuffer byteBuffer, int int1) throws IOException {
		return loadItem(byteBuffer, int1, true);
	}

	public static InventoryItem loadItem(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		int int2 = byteBuffer.getInt();
		if (int2 <= 0) {
			throw new IOException("InventoryItem.loadItem() invalid item data length: " + int2);
		} else {
			int int3 = byteBuffer.position();
			short short1 = byteBuffer.getShort();
			byte byte1 = -1;
			if (int1 >= 70) {
				byte1 = byteBuffer.get();
				if (byte1 < 0) {
					DebugLog.log("InventoryItem.loadItem() invalid item save-type " + byte1 + ", itemtype: " + WorldDictionary.getItemTypeDebugString(short1));
					return null;
				}
			}

			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(short1);
			if (boolean1 && byte1 != -1 && inventoryItem != null && inventoryItem.getSaveType() != byte1) {
				DebugLog.log("InventoryItem.loadItem() ignoring \"" + inventoryItem.getFullType() + "\" because type changed from " + byte1 + " to " + inventoryItem.getSaveType());
				inventoryItem = null;
			}

			if (inventoryItem != null) {
				try {
					inventoryItem.load(byteBuffer, int1);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					inventoryItem = null;
				}
			}

			if (inventoryItem != null) {
				if (int2 != -1 && byteBuffer.position() != int3 + int2) {
					byteBuffer.position(int3 + int2);
					DebugLog.log("InventoryItem.loadItem() data length not matching, resetting buffer position to \'" + (int3 + int2) + "\'. itemtype: " + WorldDictionary.getItemTypeDebugString(short1));
					if (Core.bDebug) {
						throw new IOException("InventoryItem.loadItem() read more data than save() wrote (" + WorldDictionary.getItemTypeDebugString(short1) + ")");
					}
				}

				return inventoryItem;
			} else {
				if (byteBuffer.position() >= int3 + int2) {
					if (byteBuffer.position() >= int3 + int2) {
						byteBuffer.position(int3 + int2);
						DebugLog.log("InventoryItem.loadItem() item == null, resetting buffer position to \'" + (int3 + int2) + "\'. itemtype: " + WorldDictionary.getItemTypeDebugString(short1));
					}
				} else {
					while (byteBuffer.position() < int3 + int2) {
						byteBuffer.get();
					}

					DebugLog.log("InventoryItem.loadItem() item == null, skipped bytes. itemtype: " + WorldDictionary.getItemTypeDebugString(short1));
				}

				return null;
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.id = byteBuffer.getInt();
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			if (bitHeaderRead.hasFlags(1)) {
				this.uses = byteBuffer.getShort();
			}

			float float1;
			if (bitHeaderRead.hasFlags(2)) {
				byte byte1 = byteBuffer.get();
				float1 = PZMath.clamp((float)(byte1 - -128) / 255.0F, 0.0F, 1.0F);
				((DrainableComboItem)this).setUsedDelta(float1);
			}

			if (bitHeaderRead.hasFlags(4)) {
				this.setCondition(byteBuffer.get(), false);
			}

			if (bitHeaderRead.hasFlags(8)) {
				this.visual = new ItemVisual();
				this.visual.load(byteBuffer, int1);
			}

			float float2;
			float float3;
			if (bitHeaderRead.hasFlags(16)) {
				float float4 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				float1 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				float2 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				float3 = Bits.unpackByteToFloatUnit(byteBuffer.get());
				this.setColor(new Color(float4, float1, float2, float3));
			}

			if (bitHeaderRead.hasFlags(32)) {
				this.itemCapacity = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(64)) {
				BitHeaderRead bitHeaderRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
				if (bitHeaderRead2.hasFlags(1)) {
					if (this.table == null) {
						this.table = LuaManager.platform.newTable();
					}

					this.table.load(byteBuffer, int1);
				}

				this.activated = bitHeaderRead2.hasFlags(2);
				if (bitHeaderRead2.hasFlags(4)) {
					this.setHaveBeenRepaired(byteBuffer.getShort());
				}

				if (bitHeaderRead2.hasFlags(8)) {
					this.name = GameWindow.ReadString(byteBuffer);
				}

				int int2;
				int int3;
				if (bitHeaderRead2.hasFlags(16)) {
					int2 = byteBuffer.getInt();
					this.byteData = ByteBuffer.allocate(int2);
					for (int3 = 0; int3 < int2; ++int3) {
						this.byteData.put(byteBuffer.get());
					}

					this.byteData.flip();
				}

				if (bitHeaderRead2.hasFlags(32)) {
					int2 = byteBuffer.getInt();
					if (int2 > 0) {
						this.extraItems = new ArrayList();
						for (int3 = 0; int3 < int2; ++int3) {
							short short1 = byteBuffer.getShort();
							String string = WorldDictionary.getItemTypeFromID(short1);
							this.extraItems.add(string);
						}
					}
				}

				this.setCustomName(bitHeaderRead2.hasFlags(64));
				if (bitHeaderRead2.hasFlags(128)) {
					float1 = byteBuffer.getFloat();
					if (float1 >= 0.0F) {
						this.setActualWeight(float1);
						this.setWeight(float1);
						this.setCustomWeight(true);
					}
				}

				if (bitHeaderRead2.hasFlags(256)) {
					this.setKeyId(byteBuffer.getInt());
				}

				this.setTaintedWater(bitHeaderRead2.hasFlags(512));
				if (bitHeaderRead2.hasFlags(1024)) {
					this.setRemoteControlID(byteBuffer.getInt());
					this.setRemoteRange(byteBuffer.getInt());
				}

				if (bitHeaderRead2.hasFlags(2048)) {
					float1 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					float2 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					float3 = Bits.unpackByteToFloatUnit(byteBuffer.get());
					this.setColorRed(float1);
					this.setColorGreen(float2);
					this.setColorBlue(float3);
					this.setColor(new Color(this.colorRed, this.colorGreen, this.colorBlue));
				}

				if (bitHeaderRead2.hasFlags(4096)) {
					this.setWorker(GameWindow.ReadString(byteBuffer));
				}

				if (bitHeaderRead2.hasFlags(8192)) {
					this.setWetCooldown(byteBuffer.getFloat());
				}

				this.setFavorite(bitHeaderRead2.hasFlags(16384));
				if (bitHeaderRead2.hasFlags(32768)) {
					this.stashMap = GameWindow.ReadString(byteBuffer);
				}

				this.setInfected(bitHeaderRead2.hasFlags(65536));
				if (bitHeaderRead2.hasFlags(131072)) {
					this.setCurrentAmmoCount(byteBuffer.getInt());
				}

				if (bitHeaderRead2.hasFlags(262144)) {
					this.attachedSlot = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(524288)) {
					if (int1 < 179) {
						short short2 = byteBuffer.getShort();
						this.attachedSlotType = null;
					} else {
						this.attachedSlotType = GameWindow.ReadString(byteBuffer);
					}
				}

				if (bitHeaderRead2.hasFlags(1048576)) {
					this.attachedToModel = GameWindow.ReadString(byteBuffer);
				}

				if (bitHeaderRead2.hasFlags(2097152)) {
					this.maxCapacity = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(4194304)) {
					this.setRecordedMediaIndex(byteBuffer.getShort());
				}

				if (bitHeaderRead2.hasFlags(8388608)) {
					this.setWorldZRotation(byteBuffer.getInt());
				}

				if (bitHeaderRead2.hasFlags(16777216)) {
					this.worldScale = byteBuffer.getFloat();
				}

				this.setInitialised(bitHeaderRead2.hasFlags(33554432));
				bitHeaderRead2.release();
			}
		}

		bitHeaderRead.release();
	}

	public boolean IsFood() {
		return false;
	}

	public boolean IsWeapon() {
		return false;
	}

	public boolean IsDrainable() {
		return false;
	}

	public boolean IsLiterature() {
		return false;
	}

	public boolean IsClothing() {
		return false;
	}

	public boolean IsInventoryContainer() {
		return false;
	}

	public boolean IsMap() {
		return false;
	}

	static InventoryItem LoadFromFile(DataInputStream dataInputStream) throws IOException {
		GameWindow.ReadString(dataInputStream);
		return null;
	}

	public ItemContainer getOutermostContainer() {
		if (this.container != null && !"floor".equals(this.container.type)) {
			ItemContainer itemContainer;
			for (itemContainer = this.container; itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getContainer() != null && !"floor".equals(itemContainer.getContainingItem().getContainer().type); itemContainer = itemContainer.getContainingItem().getContainer()) {
			}

			return itemContainer;
		} else {
			return null;
		}
	}

	public boolean isInLocalPlayerInventory() {
		if (!GameClient.bClient) {
			return false;
		} else {
			ItemContainer itemContainer = this.getOutermostContainer();
			if (itemContainer == null) {
				return false;
			} else {
				return itemContainer.getParent() instanceof IsoPlayer ? ((IsoPlayer)itemContainer.getParent()).isLocalPlayer() : false;
			}
		}
	}

	public boolean isInPlayerInventory() {
		ItemContainer itemContainer = this.getOutermostContainer();
		return itemContainer == null ? false : itemContainer.getParent() instanceof IsoPlayer;
	}

	public ItemReplacement getItemReplacementPrimaryHand() {
		return this.ScriptItem.replacePrimaryHand;
	}

	public ItemReplacement getItemReplacementSecondHand() {
		return this.ScriptItem.replaceSecondHand;
	}

	public ClothingItem getClothingItem() {
		if ("RightHand".equalsIgnoreCase(this.getAlternateModelName())) {
			return this.getItemReplacementPrimaryHand().clothingItem;
		} else {
			return "LeftHand".equalsIgnoreCase(this.getAlternateModelName()) ? this.getItemReplacementSecondHand().clothingItem : this.ScriptItem.getClothingItemAsset();
		}
	}

	public String getAlternateModelName() {
		if (this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)this.getContainer().getParent();
			if (gameCharacter.getPrimaryHandItem() == this && this.getItemReplacementPrimaryHand() != null) {
				return "RightHand";
			}

			if (gameCharacter.getSecondaryHandItem() == this && this.getItemReplacementSecondHand() != null) {
				return "LeftHand";
			}
		}

		return this.m_alternateModelName;
	}

	public ItemVisual getVisual() {
		ClothingItem clothingItem = this.getClothingItem();
		if (clothingItem != null && clothingItem.isReady()) {
			if (this.visual == null) {
				this.visual = new ItemVisual();
				this.visual.setItemType(this.getFullType());
				this.visual.pickUninitializedValues(clothingItem);
			}

			this.visual.setClothingItemName(clothingItem.m_Name);
			this.visual.setAlternateModelName(this.getAlternateModelName());
			return this.visual;
		} else {
			this.visual = null;
			return null;
		}
	}

	public boolean allowRandomTint() {
		ClothingItem clothingItem = this.getClothingItem();
		return clothingItem != null ? clothingItem.m_AllowRandomTint : false;
	}

	public void synchWithVisual() {
		if (this instanceof Clothing || this instanceof InventoryContainer) {
			ItemVisual itemVisual = this.getVisual();
			if (itemVisual != null) {
				if (this instanceof Clothing && this.getBloodClothingType() != null) {
					BloodClothingType.calcTotalBloodLevel((Clothing)this);
				}

				ClothingItem clothingItem = this.getClothingItem();
				if (clothingItem.m_AllowRandomTint) {
					this.setColor(new Color(itemVisual.m_Tint.r, itemVisual.m_Tint.g, itemVisual.m_Tint.b));
				} else {
					this.setColor(new Color(this.getColorRed(), this.getColorGreen(), this.getColorBlue()));
				}

				if ((clothingItem.m_BaseTextures.size() > 1 || itemVisual.m_TextureChoice > -1) && this.getIconsForTexture() != null) {
					String string = null;
					if (itemVisual.m_BaseTexture > -1 && this.getIconsForTexture().size() > itemVisual.m_BaseTexture) {
						string = (String)this.getIconsForTexture().get(itemVisual.m_BaseTexture);
					} else if (itemVisual.m_TextureChoice > -1 && this.getIconsForTexture().size() > itemVisual.m_TextureChoice) {
						string = (String)this.getIconsForTexture().get(itemVisual.m_TextureChoice);
					}

					if (!StringUtils.isNullOrWhitespace(string)) {
						this.texture = Texture.trygetTexture("Item_" + string);
						if (this.texture == null) {
							this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
						}
					}
				}
			}
		}
	}

	public int getContainerX() {
		return this.containerX;
	}

	public void setContainerX(int int1) {
		this.containerX = int1;
	}

	public int getContainerY() {
		return this.containerY;
	}

	public void setContainerY(int int1) {
		this.containerY = int1;
	}

	public boolean isDisappearOnUse() {
		return this.getScriptItem().isDisappearOnUse();
	}

	public String getName() {
		if (this.isBroken()) {
			return Translator.getText("IGUI_ItemNaming", this.brokenString, this.name);
		} else if (this.isTaintedWater()) {
			return Translator.getText("IGUI_ItemNameTaintedWater", this.name);
		} else if (this.getRemoteControlID() != -1) {
			return Translator.getText("IGUI_ItemNameControllerLinked", this.name);
		} else {
			return this.getMechanicType() > 0 ? Translator.getText("IGUI_ItemNameMechanicalType", this.name, Translator.getText("IGUI_VehicleType_" + this.getMechanicType())) : this.name;
		}
	}

	public void setName(String string) {
		if (string.length() > 256) {
			string = string.substring(0, Math.min(string.length(), 256));
		}

		this.name = string;
	}

	public String getReplaceOnUse() {
		return this.replaceOnUse;
	}

	public void setReplaceOnUse(String string) {
		this.replaceOnUse = string;
		this.replaceOnUseFullType = StringUtils.moduleDotType(this.getModule(), string);
	}

	public String getReplaceOnUseFullType() {
		return this.replaceOnUseFullType;
	}

	public int getConditionMax() {
		return this.ConditionMax;
	}

	public void setConditionMax(int int1) {
		this.ConditionMax = int1;
	}

	public ItemContainer getRightClickContainer() {
		return this.rightClickContainer;
	}

	public void setRightClickContainer(ItemContainer itemContainer) {
		this.rightClickContainer = itemContainer;
	}

	public String getSwingAnim() {
		return this.getScriptItem().SwingAnim;
	}

	public Texture getTexture() {
		return this.texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Texture getTexturerotten() {
		return this.texturerotten;
	}

	public void setTexturerotten(Texture texture) {
		this.texturerotten = texture;
	}

	public Texture getTextureCooked() {
		return this.textureCooked;
	}

	public void setTextureCooked(Texture texture) {
		this.textureCooked = texture;
	}

	public Texture getTextureBurnt() {
		return this.textureBurnt;
	}

	public void setTextureBurnt(Texture texture) {
		this.textureBurnt = texture;
	}

	public void setType(String string) {
		this.type = string;
		this.fullType = this.module + "." + string;
	}

	public int getCurrentUses() {
		return this.uses;
	}

	@Deprecated
	public int getUses() {
		return 1;
	}

	@Deprecated
	public void setUses(int int1) {
	}

	public float getAge() {
		return this.Age;
	}

	public void setAge(float float1) {
		this.Age = float1;
	}

	public float getLastAged() {
		return this.LastAged;
	}

	public void setLastAged(float float1) {
		this.LastAged = float1;
	}

	public void updateAge() {
	}

	public void setAutoAge() {
	}

	public boolean isIsCookable() {
		return this.IsCookable;
	}

	public boolean isCookable() {
		return this.IsCookable;
	}

	public void setIsCookable(boolean boolean1) {
		this.IsCookable = boolean1;
	}

	public float getCookingTime() {
		return this.CookingTime;
	}

	public void setCookingTime(float float1) {
		this.CookingTime = float1;
	}

	public float getMinutesToCook() {
		return this.MinutesToCook;
	}

	public void setMinutesToCook(float float1) {
		this.MinutesToCook = float1;
	}

	public float getMinutesToBurn() {
		return this.MinutesToBurn;
	}

	public void setMinutesToBurn(float float1) {
		this.MinutesToBurn = float1;
	}

	public boolean isCooked() {
		return this.Cooked;
	}

	public void setCooked(boolean boolean1) {
		this.Cooked = boolean1;
	}

	public boolean isBurnt() {
		return this.Burnt;
	}

	public void setBurnt(boolean boolean1) {
		this.Burnt = boolean1;
	}

	public int getOffAge() {
		return this.OffAge;
	}

	public void setOffAge(int int1) {
		this.OffAge = int1;
	}

	public int getOffAgeMax() {
		return this.OffAgeMax;
	}

	public void setOffAgeMax(int int1) {
		this.OffAgeMax = int1;
	}

	public float getWeight() {
		return this.Weight;
	}

	public void setWeight(float float1) {
		this.Weight = float1;
	}

	public float getActualWeight() {
		return this.getDisplayName().equals(this.getFullType()) ? 0.0F : this.ActualWeight;
	}

	public void setActualWeight(float float1) {
		this.ActualWeight = float1;
	}

	public String getWorldTexture() {
		return this.WorldTexture;
	}

	public void setWorldTexture(String string) {
		this.WorldTexture = string;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String string) {
		this.Description = string;
	}

	public int getCondition() {
		return this.Condition;
	}

	public void setCondition(int int1, boolean boolean1) {
		int1 = Math.max(0, int1);
		if (this.Condition > 0 && int1 <= 0 && boolean1 && this.getBreakSound() != null && !this.getBreakSound().isEmpty() && IsoPlayer.getInstance() != null) {
			IsoPlayer.getInstance().playSound(this.getBreakSound());
		}

		this.Condition = int1;
		this.setBroken(int1 <= 0);
	}

	public void setCondition(int int1) {
		this.setCondition(int1, true);
	}

	public String getOffString() {
		return this.OffString;
	}

	public void setOffString(String string) {
		this.OffString = string;
	}

	public String getCookedString() {
		return this.CookedString;
	}

	public void setCookedString(String string) {
		this.CookedString = string;
	}

	public String getUnCookedString() {
		return this.UnCookedString;
	}

	public void setUnCookedString(String string) {
		this.UnCookedString = string;
	}

	public String getBurntString() {
		return this.BurntString;
	}

	public void setBurntString(String string) {
		this.BurntString = string;
	}

	public String getModule() {
		return this.module;
	}

	public void setModule(String string) {
		this.module = string;
		this.fullType = string + "." + this.type;
	}

	public boolean isAlwaysWelcomeGift() {
		return this.getScriptItem().isAlwaysWelcomeGift();
	}

	public boolean isCanBandage() {
		return this.getScriptItem().isCanBandage();
	}

	public float getBoredomChange() {
		return this.boredomChange;
	}

	public void setBoredomChange(float float1) {
		this.boredomChange = float1;
	}

	public float getUnhappyChange() {
		return this.unhappyChange;
	}

	public void setUnhappyChange(float float1) {
		this.unhappyChange = float1;
	}

	public float getStressChange() {
		return this.stressChange;
	}

	public void setStressChange(float float1) {
		this.stressChange = float1;
	}

	public ArrayList getTags() {
		return this.ScriptItem.getTags();
	}

	public boolean hasTag(String string) {
		ArrayList arrayList = this.getTags();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			if (((String)arrayList.get(int1)).equalsIgnoreCase(string)) {
				return true;
			}
		}

		return false;
	}

	public ArrayList getTaken() {
		return this.Taken;
	}

	public void setTaken(ArrayList arrayList) {
		this.Taken = arrayList;
	}

	public IsoDirections getPlaceDir() {
		return this.placeDir;
	}

	public void setPlaceDir(IsoDirections directions) {
		this.placeDir = directions;
	}

	public IsoDirections getNewPlaceDir() {
		return this.newPlaceDir;
	}

	public void setNewPlaceDir(IsoDirections directions) {
		this.newPlaceDir = directions;
	}

	public void setReplaceOnUseOn(String string) {
		this.ReplaceOnUseOn = string;
	}

	public String getReplaceOnUseOn() {
		return this.ReplaceOnUseOn;
	}

	public String getReplaceOnUseOnString() {
		String string = this.getReplaceOnUseOn();
		if (string.split("-")[0].trim().contains("WaterSource")) {
			string = string.split("-")[1];
			if (!string.contains(".")) {
				String string2 = this.getModule();
				string = string2 + "." + string;
			}
		}

		return string;
	}

	public void setIsWaterSource(boolean boolean1) {
		this.IsWaterSource = boolean1;
	}

	public boolean isWaterSource() {
		return this.IsWaterSource;
	}

	boolean CanStackNoTemp(InventoryItem inventoryItem) {
		return false;
	}

	public void CopyModData(KahluaTable kahluaTable) {
		this.copyModData(kahluaTable);
	}

	public void copyModData(KahluaTable kahluaTable) {
		if (this.table != null) {
			this.table.wipe();
		}

		if (kahluaTable != null) {
			LuaManager.copyTable(this.getModData(), kahluaTable);
		}
	}

	public int getCount() {
		return this.Count;
	}

	public void setCount(int int1) {
		this.Count = int1;
	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivated(boolean boolean1) {
		this.activated = boolean1;
		if (this.canEmitLight() && GameClient.bClient && this.getEquipParent() != null) {
			if (this.getEquipParent().getPrimaryHandItem() == this) {
				this.getEquipParent().reportEvent("EventSetActivatedPrimary");
			} else if (this.getEquipParent().getSecondaryHandItem() == this) {
				this.getEquipParent().reportEvent("EventSetActivatedSecondary");
			}
		}
	}

	public void setActivatedRemote(boolean boolean1) {
		this.activated = boolean1;
	}

	public void setCanBeActivated(boolean boolean1) {
		this.canBeActivated = boolean1;
	}

	public boolean canBeActivated() {
		return this.canBeActivated;
	}

	public void setLightStrength(float float1) {
		this.lightStrength = float1;
	}

	public float getLightStrength() {
		return this.lightStrength;
	}

	public boolean isTorchCone() {
		return this.isTorchCone;
	}

	public void setTorchCone(boolean boolean1) {
		this.isTorchCone = boolean1;
	}

	public float getTorchDot() {
		return this.getScriptItem().torchDot;
	}

	public int getLightDistance() {
		return this.lightDistance;
	}

	public void setLightDistance(int int1) {
		this.lightDistance = int1;
	}

	public boolean canEmitLight() {
		if (this.getLightStrength() <= 0.0F) {
			return false;
		} else {
			Drainable drainable = (Drainable)Type.tryCastTo(this, Drainable.class);
			return drainable == null || !(drainable.getUsedDelta() <= 0.0F);
		}
	}

	public boolean isEmittingLight() {
		if (!this.canEmitLight()) {
			return false;
		} else {
			return !this.canBeActivated() || this.isActivated();
		}
	}

	public boolean canStoreWater() {
		return this.CanStoreWater;
	}

	public float getFatigueChange() {
		return this.fatigueChange;
	}

	public void setFatigueChange(float float1) {
		this.fatigueChange = float1;
	}

	public float getCurrentCondition() {
		Float Float1 = (float)this.Condition / (float)this.ConditionMax;
		return Float.valueOf(Float1 * 100.0F);
	}

	public void setColor(Color color) {
		this.col = color;
	}

	public Color getColor() {
		return this.col;
	}

	public ColorInfo getColorInfo() {
		return new ColorInfo(this.col.getRedFloat(), this.col.getGreenFloat(), this.col.getBlueFloat(), this.col.getAlphaFloat());
	}

	public boolean isTwoHandWeapon() {
		return this.getScriptItem().TwoHandWeapon;
	}

	public String getCustomMenuOption() {
		return this.customMenuOption;
	}

	public void setCustomMenuOption(String string) {
		this.customMenuOption = string;
	}

	public void setTooltip(String string) {
		this.tooltip = string;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public String getDisplayCategory() {
		return this.displayCategory;
	}

	public void setDisplayCategory(String string) {
		this.displayCategory = string;
	}

	public int getHaveBeenRepaired() {
		return this.haveBeenRepaired;
	}

	public void setHaveBeenRepaired(int int1) {
		this.haveBeenRepaired = int1;
	}

	public boolean isBroken() {
		return this.broken;
	}

	public void setBroken(boolean boolean1) {
		this.broken = boolean1;
	}

	public String getDisplayName() {
		return this.name;
	}

	public boolean isTrap() {
		return this.getScriptItem().Trap;
	}

	public void addExtraItem(String string) {
		if (this.extraItems == null) {
			this.extraItems = new ArrayList();
		}

		this.extraItems.add(string);
	}

	public boolean haveExtraItems() {
		return this.extraItems != null;
	}

	public ArrayList getExtraItems() {
		return this.extraItems;
	}

	public float getExtraItemsWeight() {
		if (!this.haveExtraItems()) {
			return 0.0F;
		} else {
			float float1 = 0.0F;
			for (int int1 = 0; int1 < this.extraItems.size(); ++int1) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem((String)this.extraItems.get(int1));
				float1 += inventoryItem.getActualWeight();
			}

			float1 *= 0.6F;
			return float1;
		}
	}

	public boolean isCustomName() {
		return this.customName;
	}

	public void setCustomName(boolean boolean1) {
		this.customName = boolean1;
	}

	public boolean isFishingLure() {
		return this.getScriptItem().FishingLure;
	}

	public void copyConditionModData(InventoryItem inventoryItem) {
		if (inventoryItem.hasModData()) {
			KahluaTableIterator kahluaTableIterator = inventoryItem.getModData().iterator();
			while (kahluaTableIterator.advance()) {
				if (kahluaTableIterator.getKey() instanceof String && ((String)kahluaTableIterator.getKey()).startsWith("condition:")) {
					this.getModData().rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
				}
			}
		}
	}

	public void setConditionFromModData(InventoryItem inventoryItem) {
		if (inventoryItem.hasModData()) {
			Object object = inventoryItem.getModData().rawget("condition:" + this.getType());
			if (object != null && object instanceof Double) {
				this.setCondition((int)Math.round((Double)object * (double)this.getConditionMax()));
			}
		}
	}

	public String getBreakSound() {
		return this.breakSound;
	}

	public void setBreakSound(String string) {
		this.breakSound = string;
	}

	public void setBeingFilled(boolean boolean1) {
		this.beingFilled = boolean1;
	}

	public boolean isBeingFilled() {
		return this.beingFilled;
	}

	public String getFillFromDispenserSound() {
		return this.getScriptItem().getFillFromDispenserSound();
	}

	public String getFillFromTapSound() {
		return this.getScriptItem().getFillFromTapSound();
	}

	public boolean isAlcoholic() {
		return this.alcoholic;
	}

	public void setAlcoholic(boolean boolean1) {
		this.alcoholic = boolean1;
	}

	public float getAlcoholPower() {
		return this.alcoholPower;
	}

	public void setAlcoholPower(float float1) {
		this.alcoholPower = float1;
	}

	public float getBandagePower() {
		return this.bandagePower;
	}

	public void setBandagePower(float float1) {
		this.bandagePower = float1;
	}

	public float getReduceInfectionPower() {
		return this.ReduceInfectionPower;
	}

	public void setReduceInfectionPower(float float1) {
		this.ReduceInfectionPower = float1;
	}

	public final void saveWithSize(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		int int1 = byteBuffer.position();
		byteBuffer.putInt(0);
		int int2 = byteBuffer.position();
		this.save(byteBuffer, boolean1);
		int int3 = byteBuffer.position();
		byteBuffer.position(int1);
		byteBuffer.putInt(int3 - int2);
		byteBuffer.position(int3);
	}

	public boolean isCustomWeight() {
		return this.customWeight;
	}

	public void setCustomWeight(boolean boolean1) {
		this.customWeight = boolean1;
	}

	public float getContentsWeight() {
		if (!StringUtils.isNullOrEmpty(this.getAmmoType())) {
			Item item = ScriptManager.instance.FindItem(this.getAmmoType());
			if (item != null) {
				return item.getActualWeight() * (float)this.getCurrentAmmoCount();
			}
		}

		return 0.0F;
	}

	public float getHotbarEquippedWeight() {
		return (this.getActualWeight() + this.getContentsWeight()) * 0.7F;
	}

	public float getEquippedWeight() {
		return (this.getActualWeight() + this.getContentsWeight()) * 0.3F;
	}

	public float getUnequippedWeight() {
		return this.getActualWeight() + this.getContentsWeight();
	}

	public boolean isEquipped() {
		return this.getContainer() != null && this.getContainer().getParent() instanceof IsoGameCharacter ? ((IsoGameCharacter)this.getContainer().getParent()).isEquipped(this) : false;
	}

	public int getKeyId() {
		return this.keyId;
	}

	public void setKeyId(int int1) {
		this.keyId = int1;
	}

	public boolean isTaintedWater() {
		return this.taintedWater;
	}

	public void setTaintedWater(boolean boolean1) {
		this.taintedWater = boolean1;
	}

	public boolean isRemoteController() {
		return this.remoteController;
	}

	public void setRemoteController(boolean boolean1) {
		this.remoteController = boolean1;
	}

	public boolean canBeRemote() {
		return this.canBeRemote;
	}

	public void setCanBeRemote(boolean boolean1) {
		this.canBeRemote = boolean1;
	}

	public int getRemoteControlID() {
		return this.remoteControlID;
	}

	public void setRemoteControlID(int int1) {
		this.remoteControlID = int1;
	}

	public int getRemoteRange() {
		return this.remoteRange;
	}

	public void setRemoteRange(int int1) {
		this.remoteRange = int1;
	}

	public String getExplosionSound() {
		return this.explosionSound;
	}

	public void setExplosionSound(String string) {
		this.explosionSound = string;
	}

	public String getCountDownSound() {
		return this.countDownSound;
	}

	public void setCountDownSound(String string) {
		this.countDownSound = string;
	}

	public float getColorRed() {
		return this.colorRed;
	}

	public void setColorRed(float float1) {
		this.colorRed = float1;
	}

	public float getColorGreen() {
		return this.colorGreen;
	}

	public void setColorGreen(float float1) {
		this.colorGreen = float1;
	}

	public float getColorBlue() {
		return this.colorBlue;
	}

	public void setColorBlue(float float1) {
		this.colorBlue = float1;
	}

	public String getEvolvedRecipeName() {
		return this.evolvedRecipeName;
	}

	public void setEvolvedRecipeName(String string) {
		this.evolvedRecipeName = string;
	}

	public float getMetalValue() {
		return this.metalValue;
	}

	public void setMetalValue(float float1) {
		this.metalValue = float1;
	}

	public float getItemHeat() {
		return this.itemHeat;
	}

	public void setItemHeat(float float1) {
		if (float1 > 2.0F) {
			float1 = 2.0F;
		}

		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.itemHeat = float1;
	}

	public float getInvHeat() {
		return 1.0F - this.itemHeat;
	}

	public float getMeltingTime() {
		return this.meltingTime;
	}

	public void setMeltingTime(float float1) {
		if (float1 > 100.0F) {
			float1 = 100.0F;
		}

		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		this.meltingTime = float1;
	}

	public String getWorker() {
		return this.worker;
	}

	public void setWorker(String string) {
		this.worker = string;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int int1) {
		this.id = int1;
	}

	public boolean isWet() {
		return this.isWet;
	}

	public void setWet(boolean boolean1) {
		this.isWet = boolean1;
	}

	public float getWetCooldown() {
		return this.wetCooldown;
	}

	public void setWetCooldown(float float1) {
		this.wetCooldown = float1;
	}

	public String getItemWhenDry() {
		return this.itemWhenDry;
	}

	public void setItemWhenDry(String string) {
		this.itemWhenDry = string;
	}

	public boolean isFavorite() {
		return this.favorite;
	}

	public void setFavorite(boolean boolean1) {
		this.favorite = boolean1;
	}

	public ArrayList getRequireInHandOrInventory() {
		return this.requireInHandOrInventory;
	}

	public void setRequireInHandOrInventory(ArrayList arrayList) {
		this.requireInHandOrInventory = arrayList;
	}

	public boolean isCustomColor() {
		return this.customColor;
	}

	public void setCustomColor(boolean boolean1) {
		this.customColor = boolean1;
	}

	public void doBuildingStash() {
		if (this.stashMap != null) {
			if (GameClient.bClient) {
				GameClient.sendBuildingStashToDo(this.stashMap);
			} else {
				StashSystem.prepareBuildingStash(this.stashMap);
			}
		}
	}

	public void setStashMap(String string) {
		this.stashMap = string;
	}

	public int getMechanicType() {
		return this.getScriptItem().vehicleType;
	}

	public float getItemCapacity() {
		return this.itemCapacity;
	}

	public void setItemCapacity(float float1) {
		this.itemCapacity = float1;
	}

	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	public void setMaxCapacity(int int1) {
		this.maxCapacity = int1;
	}

	public boolean isConditionAffectsCapacity() {
		return this.ScriptItem != null && this.ScriptItem.isConditionAffectsCapacity();
	}

	public float getBrakeForce() {
		return this.brakeForce;
	}

	public void setBrakeForce(float float1) {
		this.brakeForce = float1;
	}

	public int getChanceToSpawnDamaged() {
		return this.chanceToSpawnDamaged;
	}

	public void setChanceToSpawnDamaged(int int1) {
		this.chanceToSpawnDamaged = int1;
	}

	public float getConditionLowerNormal() {
		return this.conditionLowerNormal;
	}

	public void setConditionLowerNormal(float float1) {
		this.conditionLowerNormal = float1;
	}

	public float getConditionLowerOffroad() {
		return this.conditionLowerOffroad;
	}

	public void setConditionLowerOffroad(float float1) {
		this.conditionLowerOffroad = float1;
	}

	public float getWheelFriction() {
		return this.wheelFriction;
	}

	public void setWheelFriction(float float1) {
		this.wheelFriction = float1;
	}

	public float getSuspensionDamping() {
		return this.suspensionDamping;
	}

	public void setSuspensionDamping(float float1) {
		this.suspensionDamping = float1;
	}

	public float getSuspensionCompression() {
		return this.suspensionCompression;
	}

	public void setSuspensionCompression(float float1) {
		this.suspensionCompression = float1;
	}

	public void setInfected(boolean boolean1) {
		this.zombieInfected = boolean1;
	}

	public boolean isInfected() {
		return this.zombieInfected;
	}

	public float getEngineLoudness() {
		return this.engineLoudness;
	}

	public void setEngineLoudness(float float1) {
		this.engineLoudness = float1;
	}

	public String getStaticModel() {
		return this.getScriptItem().getStaticModel();
	}

	public ArrayList getIconsForTexture() {
		return this.iconsForTexture;
	}

	public void setIconsForTexture(ArrayList arrayList) {
		this.iconsForTexture = arrayList;
	}

	public float getScore(SurvivorDesc survivorDesc) {
		return 0.0F;
	}

	public IsoGameCharacter getPreviousOwner() {
		return this.previousOwner;
	}

	public void setPreviousOwner(IsoGameCharacter gameCharacter) {
		this.previousOwner = gameCharacter;
	}

	public Item getScriptItem() {
		return this.ScriptItem;
	}

	public void setScriptItem(Item item) {
		this.ScriptItem = item;
	}

	public ItemType getCat() {
		return this.cat;
	}

	public void setCat(ItemType itemType) {
		this.cat = itemType;
	}

	public ItemContainer getContainer() {
		return this.container;
	}

	public void setContainer(ItemContainer itemContainer) {
		this.container = itemContainer;
	}

	public ArrayList getBloodClothingType() {
		return this.bloodClothingType;
	}

	public void setBloodClothingType(ArrayList arrayList) {
		this.bloodClothingType = arrayList;
	}

	public void setBlood(BloodBodyPartType bloodBodyPartType, float float1) {
		ItemVisual itemVisual = this.getVisual();
		if (itemVisual != null) {
			itemVisual.setBlood(bloodBodyPartType, float1);
		}
	}

	public float getBlood(BloodBodyPartType bloodBodyPartType) {
		ItemVisual itemVisual = this.getVisual();
		return itemVisual != null ? itemVisual.getBlood(bloodBodyPartType) : 0.0F;
	}

	public void setDirt(BloodBodyPartType bloodBodyPartType, float float1) {
		ItemVisual itemVisual = this.getVisual();
		if (itemVisual != null) {
			itemVisual.setDirt(bloodBodyPartType, float1);
		}
	}

	public float getDirt(BloodBodyPartType bloodBodyPartType) {
		ItemVisual itemVisual = this.getVisual();
		return itemVisual != null ? itemVisual.getDirt(bloodBodyPartType) : 0.0F;
	}

	public String getClothingItemName() {
		return this.getScriptItem().ClothingItem;
	}

	public int getStashChance() {
		return this.stashChance;
	}

	public void setStashChance(int int1) {
		this.stashChance = int1;
	}

	public String getEatType() {
		return this.getScriptItem().eatType;
	}

	public boolean isUseWorldItem() {
		return this.getScriptItem().UseWorldItem;
	}

	public boolean isHairDye() {
		return this.getScriptItem().hairDye;
	}

	public String getAmmoType() {
		return this.ammoType;
	}

	public void setAmmoType(String string) {
		this.ammoType = string;
	}

	public int getMaxAmmo() {
		return this.maxAmmo;
	}

	public void setMaxAmmo(int int1) {
		this.maxAmmo = int1;
	}

	public int getCurrentAmmoCount() {
		return this.currentAmmoCount;
	}

	public void setCurrentAmmoCount(int int1) {
		this.currentAmmoCount = int1;
	}

	public String getGunType() {
		return this.gunType;
	}

	public void setGunType(String string) {
		this.gunType = string;
	}

	public boolean hasBlood() {
		if (this instanceof Clothing) {
			if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
				return false;
			}

			ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());
			if (arrayList == null) {
				return false;
			}

			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				if (this.getBlood((BloodBodyPartType)arrayList.get(int1)) > 0.0F) {
					return true;
				}
			}
		} else {
			if (this instanceof HandWeapon) {
				return ((HandWeapon)this).getBloodLevel() > 0.0F;
			}

			if (this instanceof InventoryContainer) {
				return ((InventoryContainer)this).getBloodLevel() > 0.0F;
			}
		}

		return false;
	}

	public boolean hasDirt() {
		if (this instanceof Clothing) {
			if (this.getBloodClothingType() == null || this.getBloodClothingType().isEmpty()) {
				return false;
			}

			ArrayList arrayList = BloodClothingType.getCoveredParts(this.getBloodClothingType());
			if (arrayList == null) {
				return false;
			}

			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				if (this.getDirt((BloodBodyPartType)arrayList.get(int1)) > 0.0F) {
					return true;
				}
			}
		}

		return false;
	}

	public String getAttachmentType() {
		return this.attachmentType;
	}

	public void setAttachmentType(String string) {
		this.attachmentType = string;
	}

	public int getAttachedSlot() {
		return this.attachedSlot;
	}

	public void setAttachedSlot(int int1) {
		this.attachedSlot = int1;
	}

	public ArrayList getAttachmentsProvided() {
		return this.attachmentsProvided;
	}

	public void setAttachmentsProvided(ArrayList arrayList) {
		this.attachmentsProvided = arrayList;
	}

	public String getAttachedSlotType() {
		return this.attachedSlotType;
	}

	public void setAttachedSlotType(String string) {
		this.attachedSlotType = string;
	}

	public String getAttachmentReplacement() {
		return this.attachmentReplacement;
	}

	public void setAttachmentReplacement(String string) {
		this.attachmentReplacement = string;
	}

	public String getAttachedToModel() {
		return this.attachedToModel;
	}

	public void setAttachedToModel(String string) {
		this.attachedToModel = string;
	}

	public String getFabricType() {
		return this.getScriptItem().fabricType;
	}

	public String getStringItemType() {
		Item item = ScriptManager.instance.FindItem(this.getFullType());
		if (item != null && item.getType() != null) {
			if (item.getType() == Item.Type.Food) {
				return item.CannedFood ? "CannedFood" : "Food";
			} else if ("Ammo".equals(item.getDisplayCategory())) {
				return "Ammo";
			} else if (item.getType() == Item.Type.Weapon && !item.isRanged()) {
				return "MeleeWeapon";
			} else if (item.getType() != Item.Type.WeaponPart && (item.getType() != Item.Type.Weapon || !item.isRanged()) && (item.getType() != Item.Type.Normal || StringUtils.isNullOrEmpty(item.getAmmoType()))) {
				if (item.getType() == Item.Type.Literature) {
					return "Literature";
				} else if (item.Medical) {
					return "Medical";
				} else if (item.SurvivalGear) {
					return "SurvivalGear";
				} else {
					return item.MechanicsItem ? "Mechanic" : "Other";
				}
			} else {
				return "RangedWeapon";
			}
		} else {
			return "Other";
		}
	}

	public boolean isProtectFromRainWhileEquipped() {
		return this.getScriptItem().ProtectFromRainWhenEquipped;
	}

	public boolean isEquippedNoSprint() {
		return this.getScriptItem().equippedNoSprint;
	}

	public String getBodyLocation() {
		return this.getScriptItem().BodyLocation;
	}

	public String getMakeUpType() {
		return this.getScriptItem().makeUpType;
	}

	public boolean isHidden() {
		return this.getScriptItem().isHidden();
	}

	public String getConsolidateOption() {
		return this.getScriptItem().consolidateOption;
	}

	public ArrayList getClothingItemExtra() {
		return this.getScriptItem().clothingItemExtra;
	}

	public ArrayList getClothingItemExtraOption() {
		return this.getScriptItem().clothingItemExtraOption;
	}

	public String getWorldStaticItem() {
		return this.getScriptItem().worldStaticModel;
	}

	public void setRegistry_id(Item item) {
		if (item.getFullName().equals(this.getFullType())) {
			this.registry_id = item.getRegistry_id();
		} else if (Core.bDebug) {
			WorldDictionary.DebugPrintItem(item);
			throw new RuntimeException("These types should always match");
		}
	}

	public short getRegistry_id() {
		return this.registry_id;
	}

	public String getModID() {
		return this.ScriptItem != null && this.ScriptItem.getModID() != null ? this.ScriptItem.getModID() : WorldDictionary.getItemModID(this.registry_id);
	}

	public String getModName() {
		return WorldDictionary.getModNameFromID(this.getModID());
	}

	public boolean isVanilla() {
		if (this.getModID() != null) {
			return this.getModID().equals("pz-vanilla");
		} else if (Core.bDebug) {
			WorldDictionary.DebugPrintItem(this);
			throw new RuntimeException("Item has no modID?");
		} else {
			return true;
		}
	}

	public short getRecordedMediaIndex() {
		return this.recordedMediaIndex;
	}

	public void setRecordedMediaIndex(short short1) {
		this.recordedMediaIndex = short1;
		if (this.recordedMediaIndex >= 0) {
			MediaData mediaData = ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex);
			this.mediaType = -1;
			if (mediaData != null) {
				this.name = mediaData.getTranslatedItemDisplayName();
				this.mediaType = mediaData.getMediaType();
			} else {
				this.recordedMediaIndex = -1;
			}
		} else {
			this.mediaType = -1;
			this.name = this.getScriptItem().getDisplayName();
		}
	}

	public void setRecordedMediaIndexInteger(int int1) {
		this.setRecordedMediaIndex((short)int1);
	}

	public boolean isRecordedMedia() {
		return this.recordedMediaIndex >= 0;
	}

	public MediaData getMediaData() {
		return this.isRecordedMedia() ? ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.recordedMediaIndex) : null;
	}

	public byte getMediaType() {
		return this.mediaType;
	}

	public void setMediaType(byte byte1) {
		this.mediaType = byte1;
	}

	public void setRecordedMediaData(MediaData mediaData) {
		if (mediaData != null && mediaData.getIndex() >= 0) {
			this.setRecordedMediaIndex(mediaData.getIndex());
		}
	}

	public void setWorldZRotation(int int1) {
		this.worldZRotation = int1;
	}

	public void setWorldScale(float float1) {
		this.worldScale = float1;
	}

	public String getLuaCreate() {
		return this.getScriptItem().getLuaCreate();
	}

	public boolean isInitialised() {
		return this.isInitialised;
	}

	public void setInitialised(boolean boolean1) {
		this.isInitialised = boolean1;
	}

	public void initialiseItem() {
		this.setInitialised(true);
		if (this.getLuaCreate() != null) {
			Object object = LuaManager.getFunctionObject(this.getLuaCreate());
			if (object != null) {
				LuaManager.caller.protectedCallVoid(LuaManager.thread, object, (Object)this);
			}
		}
	}
}
