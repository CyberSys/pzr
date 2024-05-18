package zombie.inventory;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Key;
import zombie.inventory.types.Literature;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.VehiclePart;


public class InventoryItem {
	protected IsoGameCharacter previousOwner = null;
	protected Item ScriptItem = null;
	protected ItemType cat;
	protected String consumeMenu;
	protected ItemContainer container;
	protected int containerX;
	protected int containerY;
	protected boolean DisappearOnUse;
	protected String name;
	protected String replaceOnUse;
	protected int ConditionMax;
	protected ItemContainer rightClickContainer;
	protected String swingAnim;
	protected Texture texture;
	protected Texture texturerotten;
	protected Texture textureCooked;
	protected Texture textureBurnt;
	protected String type;
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
	protected boolean AlwaysWelcomeGift;
	protected boolean CanBandage;
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
	private boolean isTwoHandWeapon;
	private String customMenuOption;
	private String tooltip;
	private String displayCategory;
	private int haveBeenRepaired;
	private boolean broken;
	private String replaceOnBreak;
	private String originalName;
	public long id;
	public boolean RequiresEquippedBothHands;
	public ByteBuffer byteData;
	private boolean trap;
	public ArrayList extraItems;
	private boolean customName;
	private boolean fishingLure;
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
	private int mechanicType;
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
			object.save(tempBuffer);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		tempBuffer.flip();
		if (this.byteData == null || this.byteData.capacity() < tempBuffer.limit() - 5 + 8) {
			this.byteData = ByteBuffer.allocate(tempBuffer.limit() - 5 + 8);
		}

		tempBuffer.get();
		tempBuffer.getInt();
		this.byteData.rewind();
		this.byteData.put((byte)87);
		this.byteData.put((byte)86);
		this.byteData.put((byte)69);
		this.byteData.put((byte)82);
		this.byteData.putInt(143);
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
		this.consumeMenu = "Eat";
		this.containerX = 0;
		this.containerY = 0;
		this.DisappearOnUse = true;
		this.replaceOnUse = null;
		this.ConditionMax = 100;
		this.rightClickContainer = null;
		this.swingAnim = "Rifle";
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
		this.Condition = 100;
		this.OffString = Translator.getText("Tooltip_food_Rotten");
		this.FreshString = Translator.getText("Tooltip_food_Fresh");
		this.CookedString = Translator.getText("Tooltip_food_Cooked");
		this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
		this.FrozenString = Translator.getText("Tooltip_food_Frozen");
		this.BurntString = Translator.getText("Tooltip_food_Burnt");
		this.brokenString = Translator.getText("Tooltip_broken");
		this.module = "Base";
		this.AlwaysWelcomeGift = false;
		this.CanBandage = false;
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
		this.isTwoHandWeapon = false;
		this.customMenuOption = null;
		this.tooltip = null;
		this.displayCategory = null;
		this.haveBeenRepaired = 1;
		this.broken = false;
		this.replaceOnBreak = null;
		this.originalName = null;
		this.id = 0L;
		this.trap = false;
		this.extraItems = null;
		this.customName = false;
		this.fishingLure = false;
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
		this.mechanicType = -1;
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
		this.WorldTexture = string4.replace("Item_", "media/inventory/world/WItem_");
		this.WorldTexture = this.WorldTexture + ".png";
	}

	public InventoryItem(String string, String string2, String string3, Item item) {
		this.cat = ItemType.None;
		this.consumeMenu = "Eat";
		this.containerX = 0;
		this.containerY = 0;
		this.DisappearOnUse = true;
		this.replaceOnUse = null;
		this.ConditionMax = 100;
		this.rightClickContainer = null;
		this.swingAnim = "Rifle";
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
		this.Condition = 100;
		this.OffString = Translator.getText("Tooltip_food_Rotten");
		this.FreshString = Translator.getText("Tooltip_food_Fresh");
		this.CookedString = Translator.getText("Tooltip_food_Cooked");
		this.UnCookedString = Translator.getText("Tooltip_food_Uncooked");
		this.FrozenString = Translator.getText("Tooltip_food_Frozen");
		this.BurntString = Translator.getText("Tooltip_food_Burnt");
		this.brokenString = Translator.getText("Tooltip_broken");
		this.module = "Base";
		this.AlwaysWelcomeGift = false;
		this.CanBandage = false;
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
		this.isTwoHandWeapon = false;
		this.customMenuOption = null;
		this.tooltip = null;
		this.displayCategory = null;
		this.haveBeenRepaired = 1;
		this.broken = false;
		this.replaceOnBreak = null;
		this.originalName = null;
		this.id = 0L;
		this.trap = false;
		this.extraItems = null;
		this.customName = false;
		this.fishingLure = false;
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
		this.mechanicType = -1;
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
		layoutItem.setValueRightNoPlus(this.isEquipped() ? this.getEquippedWeight() : this.getUnequippedWeight());
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
			layoutItem.setValueRightNoPlus((int)this.id);
		}

		float float1;
		if (this instanceof Clothing) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_Insulation") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
			float1 = ((Clothing)this).getInsulation();
			if (float1 > 0.8F) {
				layoutItem.setProgress(float1, 0.0F, 0.6F, 0.0F, 0.7F);
			} else if (float1 > 0.6F) {
				layoutItem.setProgress(float1, 0.3F, 0.6F, 0.0F, 0.7F);
			} else if (float1 > 0.4F) {
				layoutItem.setProgress(float1, 0.6F, 0.6F, 0.0F, 0.7F);
			} else if (float1 > 0.2F) {
				layoutItem.setProgress(float1, 0.6F, 0.3F, 0.0F, 0.7F);
			} else {
				layoutItem.setProgress(float1, 0.6F, 0.0F, 0.0F, 0.7F);
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

		if (!(this instanceof HandWeapon)) {
			String string3 = null;
			if (this.hasModData()) {
				Object object = this.getModData().rawget("moduleName");
				Object object2 = this.getModData().rawget("ammoType");
				if (object instanceof String && object2 instanceof String) {
					string3 = (String)object + "." + (String)object2;
					Item item = ScriptManager.instance.FindItem(string3);
					if (item != null) {
						layoutItem = layout.addItem();
						layoutItem.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
						layoutItem.setValue(item.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
						Object object3 = this.getModData().rawget("currentCapacity");
						Object object4 = this.getModData().rawget("maxCapacity");
						if (object3 instanceof Double && object4 instanceof Double) {
							String string4 = ((Double)object3).intValue() + " / " + ((Double)object4).intValue();
							layoutItem = layout.addItem();
							layoutItem.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
							layoutItem.setValue(string4, 1.0F, 1.0F, 1.0F, 1.0F);
						}
					}
				}
			}
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
		if (this.DisappearOnUse || boolean1) {
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
				this.container.dirty = true;
			}

			if (this.uses <= 0) {
				if (this.keepOnDeplete) {
					return;
				}

				if (this.container != null) {
					if (this.container.parent instanceof IsoGameCharacter && !(this instanceof HandWeapon)) {
						IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
						if (gameCharacter.getPrimaryHandItem() == this) {
							gameCharacter.setPrimaryHandItem((InventoryItem)null);
						}

						if (gameCharacter.getSecondaryHandItem() == this) {
							gameCharacter.setSecondaryHandItem((InventoryItem)null);
						}
					}

					this.container.Items.remove(this);
					this.container.dirty = true;
					this.container.setDrawDirty(true);
					this.container = null;
					if (this == UIManager.getDragInventory()) {
						UIManager.setDragInventory((InventoryItem)null);
					}
				}
			}
		}
	}

	public void Use(IsoGameCharacter gameCharacter) {
		boolean boolean1 = false;
		boolean boolean2 = false;
		if (this.AlwaysWelcomeGift) {
			boolean1 = true;
		}

		if ("Pills".equals(this.type)) {
			boolean2 = true;
		}

		if ("Pillow".equals(this.type)) {
			boolean2 = false;
			if (gameCharacter == TutorialManager.instance.wife) {
			}
		}

		Iterator iterator;
		IsoGameCharacter.Wound wound;
		if (this.CanBandage) {
			iterator = gameCharacter.getWounds().iterator();
			while (iterator.hasNext()) {
				wound = (IsoGameCharacter.Wound)iterator.next();
				if (!wound.bandaged) {
					wound.bandaged = true;
					boolean2 = true;
				}
			}

			if (!gameCharacter.getScriptName().equals("Kate")) {
				boolean2 = gameCharacter.getBodyDamage().UseBandageOnMostNeededPart();
			}

			if (boolean2) {
				if (gameCharacter instanceof IsoSurvivor) {
					((IsoSurvivor)gameCharacter).PatchedUpBy(IsoPlayer.getInstance());
				}

				boolean1 = true;
			}
		}

		if (this instanceof Food) {
			boolean1 = true;
		}

		if (this instanceof HandWeapon) {
			boolean1 = true;
		}

		if ("Belt".equals(this.type)) {
			iterator = gameCharacter.getWounds().iterator();
			while (iterator.hasNext()) {
				wound = (IsoGameCharacter.Wound)iterator.next();
				if (!wound.tourniquet) {
					wound.tourniquet = true;
					boolean2 = true;
					wound.bleeding -= 0.5F;
				}
			}
		}

		if (boolean2) {
			this.Use();
			gameCharacter.getUsedItemsOn().add(this.type);
		} else if (gameCharacter instanceof IsoSurvivor && gameCharacter != TutorialManager.instance.wife) {
			this.Use(true, true);
		}

		if (!gameCharacter.getScriptName().equals("none")) {
			ScriptManager.instance.Trigger("OnUseItemOnCharacter", gameCharacter.getScriptName(), this.type);
		} else if (gameCharacter instanceof IsoSurvivor) {
			if (this.uses == 1) {
				IsoPlayer.instance.getInventory().Remove(this);
				((IsoSurvivor)gameCharacter).getInventory().AddItem(this);
			} else {
				this.Use(true);
				((IsoSurvivor)gameCharacter).getInventory().AddItem(this.getFullType());
			}

			((IsoSurvivor)gameCharacter).GivenItemBy(IsoPlayer.getInstance(), this.type, boolean1);
		}
	}

	public boolean shouldUpdateInWorld() {
		if (!GameServer.bServer && !this.rainFactorZero && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null) {
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
				if (this.getWorldItem() != null) {
					this.getWorldItem().getSquare().AddWorldInventoryItem(inventoryItem, this.getWorldItem().getX(), this.getWorldItem().getY(), this.getWorldItem().getZ());
					this.getWorldItem().getSquare().transmitRemoveItemFromSquare(this.getWorldItem());
					this.getWorldItem().getSquare().getWorldObjects().remove(this.getWorldItem());
					this.getWorldItem().getSquare().getObjects().remove(this.getWorldItem());
					if (this.getContainer() != null) {
						this.getContainer().setDirty(true);
						this.getContainer().setDrawDirty(true);
					}

					this.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
					this.setWorldItem((IsoWorldInventoryObject)null);
				} else if (this.getContainer() != null) {
					this.getContainer().addItem(inventoryItem);
					this.getContainer().Remove(this);
				}

				this.setWet(false);
				IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
			}
		}

		if (!GameServer.bServer && !this.rainFactorZero && this.getWorldItem() != null && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && RainManager.isRaining()) {
			IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
			IsoGridSquare square = worldInventoryObject.getSquare();
			if (square != null && square.isOutside()) {
				InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(this.getReplaceOnUseOnString());
				if (inventoryItem2 instanceof DrainableComboItem && inventoryItem2.canStoreWater()) {
					if (((DrainableComboItem)inventoryItem2).getRainFactor() == 0.0F) {
						this.rainFactorZero = true;
						return;
					}

					((DrainableComboItem)inventoryItem2).setUsedDelta(0.0F);
					if (GameClient.bClient) {
						worldInventoryObject.removeFromWorld();
						worldInventoryObject.removeFromSquare();
						IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
						square.AddWorldInventoryItem(inventoryItem2, worldInventoryObject.xoff, worldInventoryObject.yoff, worldInventoryObject.zoff, false);
					} else {
						worldInventoryObject.item = inventoryItem2;
						inventoryItem2.setWorldItem(worldInventoryObject);
						this.setWorldItem((IsoWorldInventoryObject)null);
						worldInventoryObject.updateSprite();
					}

					LuaEventManager.triggerEvent("OnContainerUpdate");
				}
			}
		}
	}

	public boolean finishupdate() {
		if (!GameServer.bServer && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && this.getWorldItem() != null && this.getWorldItem().getSquare() != null) {
			return false;
		} else {
			return !this.isWet();
		}
	}

	public void updateSound(BaseSoundEmitter baseSoundEmitter) {
	}

	public String getFullType() {
		return this.module + "." + this.type;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		boolean1 = false;
		if (GameWindow.DEBUG_SAVE) {
			DebugLog.log(this.getFullType());
		}

		if (!boolean1) {
			GameWindow.WriteString(byteBuffer, this.getFullType());
		} else {
			byteBuffer.putInt((Integer)Item.NetItemToID.get(this.getFullType()));
		}

		byteBuffer.put((byte)this.getSaveType());
		byteBuffer.putInt(this.uses);
		byteBuffer.putLong(this.id);
		if (this.table != null && !this.table.isEmpty()) {
			byteBuffer.put((byte)1);
			this.table.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.IsDrainable() && ((DrainableComboItem)this).getUsedDelta() < 1.0F) {
			byteBuffer.put((byte)1);
			byteBuffer.putFloat(((DrainableComboItem)this).getUsedDelta());
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.put((byte)this.getCondition());
		byteBuffer.put((byte)(this.isActivated() ? 1 : 0));
		byteBuffer.putShort((short)this.getHaveBeenRepaired());
		if (this.name != null && !this.name.equals(this.originalName)) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.name);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.byteData == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.byteData.rewind();
			byteBuffer.putInt(this.byteData.limit());
			byteBuffer.put(this.byteData);
			this.byteData.flip();
		}

		if (this.extraItems != null) {
			byteBuffer.putInt(this.extraItems.size());
			for (int int1 = 0; int1 < this.extraItems.size(); ++int1) {
				GameWindow.WriteString(byteBuffer, (String)this.extraItems.get(int1));
			}
		} else {
			byteBuffer.putInt(0);
		}

		byteBuffer.put((byte)(this.isCustomName() ? 1 : 0));
		byteBuffer.putFloat(this.isCustomWeight() ? this.getActualWeight() : -1.0F);
		byteBuffer.putInt(this.getKeyId());
		byteBuffer.put((byte)(this.isTaintedWater() ? 1 : 0));
		byteBuffer.putInt(this.getRemoteControlID());
		byteBuffer.putInt(this.getRemoteRange());
		byteBuffer.putFloat(this.colorRed);
		byteBuffer.putFloat(this.colorGreen);
		byteBuffer.putFloat(this.colorBlue);
		GameWindow.WriteString(byteBuffer, this.getWorker());
		byteBuffer.putFloat(this.wetCooldown);
		byteBuffer.put((byte)(this.isFavorite() ? 1 : 0));
		if (this.isCustomColor()) {
			byteBuffer.put((byte)1);
			byteBuffer.putFloat(this.getColor().r);
			byteBuffer.putFloat(this.getColor().g);
			byteBuffer.putFloat(this.getColor().b);
			byteBuffer.putFloat(this.getColor().a);
		} else {
			byteBuffer.put((byte)0);
		}

		GameWindow.WriteString(byteBuffer, this.stashMap);
		byteBuffer.putFloat(this.itemCapacity);
		byteBuffer.put((byte)(this.isInfected() ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		boolean1 = false;
		this.uses = byteBuffer.getInt();
		this.id = byteBuffer.getLong();
		if (byteBuffer.get() == 1) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			((DrainableComboItem)this).setUsedDelta(byteBuffer.getFloat());
		}

		this.setCondition(byteBuffer.get(), false);
		this.activated = byteBuffer.get() == 1;
		this.setHaveBeenRepaired(byteBuffer.getShort());
		if (byteBuffer.get() != 0) {
			this.name = GameWindow.ReadString(byteBuffer);
		}

		int int2;
		int int3;
		if (byteBuffer.get() == 1) {
			int2 = byteBuffer.getInt();
			this.byteData = ByteBuffer.allocate(int2);
			for (int3 = 0; int3 < int2; ++int3) {
				this.byteData.put(byteBuffer.get());
			}

			this.byteData.flip();
		}

		if (int1 >= 30) {
			int2 = byteBuffer.getInt();
			if (int2 > 0) {
				this.extraItems = new ArrayList();
				for (int3 = 0; int3 < int2; ++int3) {
					this.extraItems.add(GameWindow.ReadString(byteBuffer));
				}
			}
		}

		if (int1 >= 31) {
			this.setCustomName(byteBuffer.get() == 1);
		}

		if (int1 >= 55) {
			float float1 = byteBuffer.getFloat();
			if (float1 >= 0.0F) {
				this.setActualWeight(float1);
				this.setCustomWeight(true);
			}
		}

		if (int1 >= 57) {
			this.setKeyId(byteBuffer.getInt());
		}

		if (int1 >= 59) {
			this.setTaintedWater(byteBuffer.get() == 1);
		}

		if (int1 >= 62) {
			this.setRemoteControlID(byteBuffer.getInt());
			this.setRemoteRange(byteBuffer.getInt());
		}

		if (int1 >= 76) {
			this.setColorRed(byteBuffer.getFloat());
			this.setColorGreen(byteBuffer.getFloat());
			this.setColorBlue(byteBuffer.getFloat());
			this.setColor(new Color(this.colorRed, this.colorGreen, this.colorBlue));
		}

		if (int1 >= 90) {
			this.setWorker(GameWindow.ReadString(byteBuffer));
		}

		if (int1 >= 93) {
			this.setWetCooldown(byteBuffer.getFloat());
		}

		if (int1 >= 94) {
			this.setFavorite(byteBuffer.get() == 1);
		}

		if (int1 >= 105 && byteBuffer.get() == 1) {
			this.setColor(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat()));
		}

		if (int1 >= 107) {
			this.stashMap = GameWindow.ReadString(byteBuffer);
		}

		if (int1 >= 116) {
			this.itemCapacity = byteBuffer.getFloat();
		}

		if (int1 >= 120) {
			this.setInfected(byteBuffer.get() == 1);
		}
	}

	public boolean IsFood() {
		return this instanceof Food;
	}

	public boolean IsWeapon() {
		return this instanceof HandWeapon;
	}

	public boolean IsDrainable() {
		return this instanceof DrainableComboItem;
	}

	public boolean IsLiterature() {
		return this instanceof Literature;
	}

	public boolean IsClothing() {
		return this instanceof Clothing;
	}

	static InventoryItem LoadFromFile(DataInputStream dataInputStream) throws IOException {
		GameWindow.ReadString(dataInputStream);
		return null;
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

	public String getConsumeMenu() {
		return this.consumeMenu;
	}

	public void setConsumeMenu(String string) {
		this.consumeMenu = string;
	}

	public ItemContainer getContainer() {
		return this.container;
	}

	public void setContainer(ItemContainer itemContainer) {
		this.container = itemContainer;
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
		return this.DisappearOnUse;
	}

	public void setDisappearOnUse(boolean boolean1) {
		this.DisappearOnUse = boolean1;
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
		this.name = string;
	}

	public String getReplaceOnUse() {
		return this.replaceOnUse;
	}

	public void setReplaceOnUse(String string) {
		this.replaceOnUse = string;
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
		return this.swingAnim;
	}

	public void setSwingAnim(String string) {
		this.swingAnim = string;
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
	}

	public int getUses() {
		return 1;
	}

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
		return this.ActualWeight;
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
		if (this.Condition > 0 && int1 <= 0 && boolean1 && this.getBreakSound() != null && !this.getBreakSound().isEmpty() && IsoPlayer.instance != null) {
			IsoPlayer.instance.playSound(this.getBreakSound(), true);
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
	}

	public boolean isAlwaysWelcomeGift() {
		return this.AlwaysWelcomeGift;
	}

	public void setAlwaysWelcomeGift(boolean boolean1) {
		this.AlwaysWelcomeGift = boolean1;
	}

	public boolean isCanBandage() {
		return this.CanBandage;
	}

	public void setCanBandage(boolean boolean1) {
		this.CanBandage = boolean1;
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
				string = this.getModule() + "." + string;
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
		if (kahluaTable != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			KahluaTable kahluaTable2 = this.getModData();
			while (kahluaTableIterator.advance()) {
				kahluaTable2.rawset(kahluaTableIterator.getKey(), kahluaTableIterator.getValue());
			}
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

	public int getLightDistance() {
		return this.lightDistance;
	}

	public void setLightDistance(int int1) {
		this.lightDistance = int1;
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
		return this.isTwoHandWeapon;
	}

	public void setTwoHandWeapon(boolean boolean1) {
		this.isTwoHandWeapon = boolean1;
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

	public String getReplaceOnBreak() {
		return this.replaceOnBreak;
	}

	public void setReplaceOnBreak(String string) {
		this.replaceOnBreak = string;
	}

	public String getDisplayName() {
		return this.name;
	}

	public void setTrap(Boolean Boolean1) {
		this.trap = Boolean1;
	}

	public boolean isTrap() {
		return this.trap;
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

	public boolean isCustomName() {
		return this.customName;
	}

	public void setCustomName(boolean boolean1) {
		this.customName = boolean1;
	}

	public boolean isFishingLure() {
		return this.fishingLure;
	}

	public void setFishingLure(boolean boolean1) {
		this.fishingLure = boolean1;
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
		return 0.0F;
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

	public long getID() {
		return this.id;
	}

	public void setID(long long1) {
		this.id = long1;
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

	public String getMap() {
		return this.map;
	}

	public void setMap(String string) {
		this.map = string;
	}

	public void doBuildingtStash() {
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
		return this.mechanicType;
	}

	public void setMechanicType(int int1) {
		this.mechanicType = int1;
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
}
