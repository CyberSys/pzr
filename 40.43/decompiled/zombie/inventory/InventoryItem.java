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

   public void setEquipParent(IsoGameCharacter var1) {
      this.equipParent = var1;
   }

   public IsoGameCharacter getEquipParent() {
      return this.equipParent == null || this.equipParent.getPrimaryHandItem() != this && this.equipParent.getSecondaryHandItem() != this ? null : this.equipParent;
   }

   public void setWorldItem(IsoWorldInventoryObject var1) {
      this.worldItem = var1;
   }

   public void setJobDelta(float var1) {
      this.jobDelta = var1;
   }

   public float getJobDelta() {
      return this.jobDelta;
   }

   public void setJobType(String var1) {
      this.jobType = var1;
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

   public void storeInByteData(IsoObject var1) {
      tempBuffer.clear();

      try {
         var1.save(tempBuffer);
      } catch (IOException var3) {
         var3.printStackTrace();
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

   public InventoryItem(String var1, String var2, String var3, String var4) {
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
      this.texture = Texture.trygetTexture(var4);
      if (this.texture == null) {
         this.texture = Texture.getSharedTexture("media/inventory/Question_On.png");
      }

      this.module = var1;
      this.name = var2;
      this.originalName = var2;
      this.type = var3;
      this.WorldTexture = var4.replace("Item_", "media/inventory/world/WItem_");
      this.WorldTexture = this.WorldTexture + ".png";
   }

   public InventoryItem(String var1, String var2, String var3, Item var4) {
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
      this.texture = var4.NormalTexture;
      this.module = var1;
      this.name = var2;
      this.originalName = var2;
      this.type = var3;
      this.WorldTexture = var4.WorldTextureName;
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
         float var1 = (this.Age - (float)this.OffAge) / (float)(this.OffAgeMax - this.OffAge);
         return var1;
      }
   }

   public boolean CanStack(InventoryItem var1) {
      return false;
   }

   public boolean ModDataMatches(InventoryItem var1) {
      KahluaTable var2 = var1.getModData();
      KahluaTable var3 = var1.getModData();
      if (var2 == null && var3 == null) {
         return true;
      } else if (var2 == null) {
         return false;
      } else if (var3 == null) {
         return false;
      } else if (var2.len() != var3.len()) {
         return false;
      } else {
         KahluaTableIterator var4 = var2.iterator();

         Object var5;
         Object var6;
         do {
            if (!var4.advance()) {
               return true;
            }

            var5 = var3.rawget(var4.getKey());
            var6 = var4.getValue();
         } while(var5.equals(var6));

         return false;
      }
   }

   public void DoTooltip(ObjectTooltip var1) {
      var1.render();
      UIFont var2 = var1.getFont();
      int var3 = var1.getLineSpacing();
      byte var4 = 5;
      String var5 = "";
      if (this.Burnt) {
         var5 = var5 + this.BurntString + " ";
      } else if (this.OffAge < 1000000000 && this.Age < (float)this.OffAge) {
         var5 = var5 + this.FreshString + " ";
      } else if (this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax) {
         var5 = var5 + this.OffString + " ";
      }

      if (this.isCooked() && !this.Burnt) {
         var5 = var5 + this.CookedString + " ";
      } else if (this.IsCookable && !this.Burnt && !(this instanceof DrainableComboItem)) {
         var5 = var5 + this.UnCookedString + " ";
      }

      if (this instanceof Food && ((Food)this).isFrozen()) {
         var5 = var5 + this.FrozenString + " ";
      }

      var5 = var5.trim();
      String var6;
      if (var5.isEmpty()) {
         var1.DrawText(var2, var6 = this.getName(), 5.0D, (double)var4, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
      } else if (this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax) {
         var1.DrawText(var2, var6 = Translator.getText("IGUI_FoodNaming", var5, this.name), 5.0D, (double)var4, 1.0D, 0.10000000149011612D, 0.10000000149011612D, 1.0D);
      } else {
         var1.DrawText(var2, var6 = Translator.getText("IGUI_FoodNaming", var5, this.name), 5.0D, (double)var4, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
      }

      var1.adjustWidth(5, var6);
      int var16 = var4 + var3 + 5;
      int var7;
      int var8;
      int var9;
      InventoryItem var10;
      if (this.extraItems != null) {
         var1.DrawText(var2, Translator.getText("Tooltip_item_Contains"), 5.0D, (double)var16, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
         var7 = 5 + TextManager.instance.MeasureStringX(var2, Translator.getText("Tooltip_item_Contains")) + 4;
         var8 = (var3 - 10) / 2;

         for(var9 = 0; var9 < this.extraItems.size(); ++var9) {
            var10 = InventoryItemFactory.CreateItem((String)this.extraItems.get(var9));
            var1.DrawTextureScaled(var10.getTex(), (double)var7, (double)(var16 + var8), 10.0D, 10.0D, 1.0D);
            var7 += 11;
         }

         var16 = var16 + var3 + 5;
      }

      if (this instanceof Food && ((Food)this).spices != null) {
         var1.DrawText(var2, Translator.getText("Tooltip_item_Spices"), 5.0D, (double)var16, 1.0D, 1.0D, 0.800000011920929D, 1.0D);
         var7 = 5 + TextManager.instance.MeasureStringX(var2, Translator.getText("Tooltip_item_Spices")) + 4;
         var8 = (var3 - 10) / 2;

         for(var9 = 0; var9 < ((Food)this).spices.size(); ++var9) {
            var10 = InventoryItemFactory.CreateItem((String)((Food)this).spices.get(var9));
            var1.DrawTextureScaled(var10.getTex(), (double)var7, (double)(var16 + var8), 10.0D, 10.0D, 1.0D);
            var7 += 11;
         }

         var16 = var16 + var3 + 5;
      }

      ObjectTooltip.Layout var17 = var1.beginLayout();
      var17.setMinLabelWidth(80);
      ObjectTooltip.LayoutItem var18 = var17.addItem();
      var18.setLabel(Translator.getText("Tooltip_item_Weight") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
      var18.setValueRightNoPlus(this.isEquipped() ? this.getEquippedWeight() : this.getUnequippedWeight());
      if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
         var18 = var17.addItem();
         var18.setLabel("getActualWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus(this.getActualWeight());
         var18 = var17.addItem();
         var18.setLabel("getWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus(this.getWeight());
         var18 = var17.addItem();
         var18.setLabel("getEquippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus(this.getEquippedWeight());
         var18 = var17.addItem();
         var18.setLabel("getUnequippedWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus(this.getUnequippedWeight());
         var18 = var17.addItem();
         var18.setLabel("getContentsWeight()", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus(this.getContentsWeight());
         if (this instanceof Key || "Doorknob".equals(this.type)) {
            var18 = var17.addItem();
            var18.setLabel("DBG: keyId", 1.0F, 1.0F, 0.8F, 1.0F);
            var18.setValueRightNoPlus(this.getKeyId());
         }

         var18 = var17.addItem();
         var18.setLabel("ID", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRightNoPlus((int)this.id);
      }

      float var19;
      if (this instanceof Clothing) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_item_Insulation") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
         var19 = ((Clothing)this).getInsulation();
         if (var19 > 0.8F) {
            var18.setProgress(var19, 0.0F, 0.6F, 0.0F, 0.7F);
         } else if (var19 > 0.6F) {
            var18.setProgress(var19, 0.3F, 0.6F, 0.0F, 0.7F);
         } else if (var19 > 0.4F) {
            var18.setProgress(var19, 0.6F, 0.6F, 0.0F, 0.7F);
         } else if (var19 > 0.2F) {
            var18.setProgress(var19, 0.6F, 0.3F, 0.0F, 0.7F);
         } else {
            var18.setProgress(var19, 0.6F, 0.0F, 0.0F, 0.7F);
         }
      }

      if (this.getFatigueChange() != 0.0F) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_item_Fatigue") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValueRight((int)(this.getFatigueChange() * 100.0F), false);
      }

      if (this instanceof DrainableComboItem) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("IGUI_invpanel_Remaining") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
         var19 = ((DrainableComboItem)this).getUsedDelta();
         var18.setProgress(var19, 0.0F, 0.6F, 0.0F, 0.7F);
      }

      if (this.isTaintedWater()) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_item_TaintedWater"), 1.0F, 0.5F, 0.5F, 1.0F);
      }

      this.DoTooltip(var1, var17);
      if (this.getRemoteControlID() != -1) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_TrapControllerID"), 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValue(Integer.toString(this.getRemoteControlID()), 1.0F, 1.0F, 0.8F, 1.0F);
      }

      if (!FixingManager.getFixes(this).isEmpty()) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_weapon_Repaired") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
         if (this.getHaveBeenRepaired() == 1) {
            var18.setValue(Translator.getText("Tooltip_never"), 1.0F, 1.0F, 1.0F, 1.0F);
         } else {
            var18.setValue(this.getHaveBeenRepaired() - 1 + "x", 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      if (!(this instanceof HandWeapon)) {
         String var21 = null;
         if (this.hasModData()) {
            Object var20 = this.getModData().rawget("moduleName");
            Object var11 = this.getModData().rawget("ammoType");
            if (var20 instanceof String && var11 instanceof String) {
               var21 = (String)var20 + "." + (String)var11;
               Item var12 = ScriptManager.instance.FindItem(var21);
               if (var12 != null) {
                  var18 = var17.addItem();
                  var18.setLabel(Translator.getText("Tooltip_weapon_Ammo") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                  var18.setValue(var12.getDisplayName(), 1.0F, 1.0F, 1.0F, 1.0F);
                  Object var13 = this.getModData().rawget("currentCapacity");
                  Object var14 = this.getModData().rawget("maxCapacity");
                  if (var13 instanceof Double && var14 instanceof Double) {
                     String var15 = ((Double)var13).intValue() + " / " + ((Double)var14).intValue();
                     var18 = var17.addItem();
                     var18.setLabel(Translator.getText("Tooltip_weapon_AmmoCount") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
                     var18.setValue(var15, 1.0F, 1.0F, 1.0F, 1.0F);
                  }
               }
            }
         }
      }

      if (this.isWet()) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_Wetness") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
         var19 = this.getWetCooldown() / 10000.0F;
         var18.setProgress(var19, 0.0F, 0.6F, 0.0F, 0.7F);
      }

      if (this.getMaxCapacity() > 0) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_container_Capacity") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
         var19 = (float)this.getMaxCapacity();
         if (this.isConditionAffectsCapacity()) {
            var19 = VehiclePart.getNumberByCondition((float)this.getMaxCapacity(), (float)this.getCondition(), 5.0F);
         }

         if (this.getItemCapacity() > -1.0F) {
            var18.setValue(this.getItemCapacity() + " / " + var19, 1.0F, 1.0F, 0.8F, 1.0F);
         } else {
            var18.setValue("0 / " + var19, 1.0F, 1.0F, 0.8F, 1.0F);
         }
      }

      if (this.getConditionMax() > 0 && this.getMechanicType() > 0) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
         var18.setValue(this.getCondition() + " / " + this.getConditionMax(), 1.0F, 1.0F, 0.8F, 1.0F);
      }

      if (this.getTooltip() != null) {
         var18 = var17.addItem();
         var18.setLabel(Translator.getText(this.tooltip), 1.0F, 1.0F, 0.8F, 1.0F);
      }

      var16 = var17.render(5, var16, var1);
      var1.endLayout(var17);
      var16 += var1.padBottom;
      var1.setHeight((double)var16);
      if (var1.getWidth() < 150.0D) {
         var1.setWidth(150.0D);
      }

   }

   public void DoTooltip(ObjectTooltip var1, ObjectTooltip.Layout var2) {
   }

   public void SetContainerPosition(int var1, int var2) {
      this.containerX = var1;
      this.containerY = var2;
   }

   public void Use() {
      this.Use(false);
   }

   public void UseItem() {
      this.Use(false);
   }

   public void Use(boolean var1) {
      this.Use(var1, false);
   }

   public void Use(boolean var1, boolean var2) {
      if (this.DisappearOnUse || var1) {
         --this.uses;
         if (this.replaceOnUse != null && !var2 && !var1 && this.container != null) {
            String var3 = this.replaceOnUse;
            if (!this.replaceOnUse.contains(".")) {
               var3 = this.module + "." + var3;
            }

            InventoryItem var4 = this.container.AddItem(var3);
            if (var4 != null) {
               var4.setConditionFromModData(this);
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
                  IsoGameCharacter var5 = (IsoGameCharacter)this.container.parent;
                  if (var5.getPrimaryHandItem() == this) {
                     var5.setPrimaryHandItem((InventoryItem)null);
                  }

                  if (var5.getSecondaryHandItem() == this) {
                     var5.setSecondaryHandItem((InventoryItem)null);
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

   public void Use(IsoGameCharacter var1) {
      boolean var2 = false;
      boolean var3 = false;
      if (this.AlwaysWelcomeGift) {
         var2 = true;
      }

      if ("Pills".equals(this.type)) {
         var3 = true;
      }

      if ("Pillow".equals(this.type)) {
         var3 = false;
         if (var1 == TutorialManager.instance.wife) {
         }
      }

      Iterator var4;
      IsoGameCharacter.Wound var5;
      if (this.CanBandage) {
         var4 = var1.getWounds().iterator();

         while(var4.hasNext()) {
            var5 = (IsoGameCharacter.Wound)var4.next();
            if (!var5.bandaged) {
               var5.bandaged = true;
               var3 = true;
            }
         }

         if (!var1.getScriptName().equals("Kate")) {
            var3 = var1.getBodyDamage().UseBandageOnMostNeededPart();
         }

         if (var3) {
            if (var1 instanceof IsoSurvivor) {
               ((IsoSurvivor)var1).PatchedUpBy(IsoPlayer.getInstance());
            }

            var2 = true;
         }
      }

      if (this instanceof Food) {
         var2 = true;
      }

      if (this instanceof HandWeapon) {
         var2 = true;
      }

      if ("Belt".equals(this.type)) {
         var4 = var1.getWounds().iterator();

         while(var4.hasNext()) {
            var5 = (IsoGameCharacter.Wound)var4.next();
            if (!var5.tourniquet) {
               var5.tourniquet = true;
               var3 = true;
               var5.bleeding -= 0.5F;
            }
         }
      }

      if (var3) {
         this.Use();
         var1.getUsedItemsOn().add(this.type);
      } else if (var1 instanceof IsoSurvivor && var1 != TutorialManager.instance.wife) {
         this.Use(true, true);
      }

      if (!var1.getScriptName().equals("none")) {
         ScriptManager.instance.Trigger("OnUseItemOnCharacter", var1.getScriptName(), this.type);
      } else if (var1 instanceof IsoSurvivor) {
         if (this.uses == 1) {
            IsoPlayer.instance.getInventory().Remove(this);
            ((IsoSurvivor)var1).getInventory().AddItem(this);
         } else {
            this.Use(true);
            ((IsoSurvivor)var1).getInventory().AddItem(this.getFullType());
         }

         ((IsoSurvivor)var1).GivenItemBy(IsoPlayer.getInstance(), this.type, var2);
      }

   }

   public boolean shouldUpdateInWorld() {
      if (!GameServer.bServer && !this.rainFactorZero && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null) {
         IsoGridSquare var1 = this.getWorldItem().getSquare();
         return var1 != null && var1.isOutside();
      } else {
         return false;
      }
   }

   public void update() {
      if (this.isWet()) {
         this.wetCooldown -= 1.0F * GameTime.instance.getMultiplier();
         if (this.wetCooldown <= 0.0F) {
            InventoryItem var1 = InventoryItemFactory.CreateItem(this.itemWhenDry);
            if (this.getWorldItem() != null) {
               this.getWorldItem().getSquare().AddWorldInventoryItem(var1, this.getWorldItem().getX(), this.getWorldItem().getY(), this.getWorldItem().getZ());
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
               this.getContainer().addItem(var1);
               this.getContainer().Remove(this);
            }

            this.setWet(false);
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
         }
      }

      if (!GameServer.bServer && !this.rainFactorZero && this.getWorldItem() != null && this.canStoreWater() && this.getReplaceOnUseOn() != null && this.getReplaceOnUseOnString() != null && RainManager.isRaining()) {
         IsoWorldInventoryObject var4 = this.getWorldItem();
         IsoGridSquare var2 = var4.getSquare();
         if (var2 != null && var2.isOutside()) {
            InventoryItem var3 = InventoryItemFactory.CreateItem(this.getReplaceOnUseOnString());
            if (var3 instanceof DrainableComboItem && var3.canStoreWater()) {
               if (((DrainableComboItem)var3).getRainFactor() == 0.0F) {
                  this.rainFactorZero = true;
                  return;
               }

               ((DrainableComboItem)var3).setUsedDelta(0.0F);
               if (GameClient.bClient) {
                  var4.removeFromWorld();
                  var4.removeFromSquare();
                  IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
                  var2.AddWorldInventoryItem(var3, var4.xoff, var4.yoff, var4.zoff, false);
               } else {
                  var4.item = var3;
                  var3.setWorldItem(var4);
                  this.setWorldItem((IsoWorldInventoryObject)null);
                  var4.updateSprite();
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

   public void updateSound(BaseSoundEmitter var1) {
   }

   public String getFullType() {
      return this.module + "." + this.type;
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      var2 = false;
      if (GameWindow.DEBUG_SAVE) {
         DebugLog.log(this.getFullType());
      }

      if (!var2) {
         GameWindow.WriteString(var1, this.getFullType());
      } else {
         var1.putInt((Integer)Item.NetItemToID.get(this.getFullType()));
      }

      var1.put((byte)this.getSaveType());
      var1.putInt(this.uses);
      var1.putLong(this.id);
      if (this.table != null && !this.table.isEmpty()) {
         var1.put((byte)1);
         this.table.save(var1);
      } else {
         var1.put((byte)0);
      }

      if (this.IsDrainable() && ((DrainableComboItem)this).getUsedDelta() < 1.0F) {
         var1.put((byte)1);
         var1.putFloat(((DrainableComboItem)this).getUsedDelta());
      } else {
         var1.put((byte)0);
      }

      var1.put((byte)this.getCondition());
      var1.put((byte)(this.isActivated() ? 1 : 0));
      var1.putShort((short)this.getHaveBeenRepaired());
      if (this.name != null && !this.name.equals(this.originalName)) {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.name);
      } else {
         var1.put((byte)0);
      }

      if (this.byteData == null) {
         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         this.byteData.rewind();
         var1.putInt(this.byteData.limit());
         var1.put(this.byteData);
         this.byteData.flip();
      }

      if (this.extraItems != null) {
         var1.putInt(this.extraItems.size());

         for(int var3 = 0; var3 < this.extraItems.size(); ++var3) {
            GameWindow.WriteString(var1, (String)this.extraItems.get(var3));
         }
      } else {
         var1.putInt(0);
      }

      var1.put((byte)(this.isCustomName() ? 1 : 0));
      var1.putFloat(this.isCustomWeight() ? this.getActualWeight() : -1.0F);
      var1.putInt(this.getKeyId());
      var1.put((byte)(this.isTaintedWater() ? 1 : 0));
      var1.putInt(this.getRemoteControlID());
      var1.putInt(this.getRemoteRange());
      var1.putFloat(this.colorRed);
      var1.putFloat(this.colorGreen);
      var1.putFloat(this.colorBlue);
      GameWindow.WriteString(var1, this.getWorker());
      var1.putFloat(this.wetCooldown);
      var1.put((byte)(this.isFavorite() ? 1 : 0));
      if (this.isCustomColor()) {
         var1.put((byte)1);
         var1.putFloat(this.getColor().r);
         var1.putFloat(this.getColor().g);
         var1.putFloat(this.getColor().b);
         var1.putFloat(this.getColor().a);
      } else {
         var1.put((byte)0);
      }

      GameWindow.WriteString(var1, this.stashMap);
      var1.putFloat(this.itemCapacity);
      var1.put((byte)(this.isInfected() ? 1 : 0));
   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      var3 = false;
      this.uses = var1.getInt();
      this.id = var1.getLong();
      if (var1.get() == 1) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

      if (var1.get() == 1) {
         ((DrainableComboItem)this).setUsedDelta(var1.getFloat());
      }

      this.setCondition(var1.get(), false);
      this.activated = var1.get() == 1;
      this.setHaveBeenRepaired(var1.getShort());
      if (var1.get() != 0) {
         this.name = GameWindow.ReadString(var1);
      }

      int var4;
      int var5;
      if (var1.get() == 1) {
         var4 = var1.getInt();
         this.byteData = ByteBuffer.allocate(var4);

         for(var5 = 0; var5 < var4; ++var5) {
            this.byteData.put(var1.get());
         }

         this.byteData.flip();
      }

      if (var2 >= 30) {
         var4 = var1.getInt();
         if (var4 > 0) {
            this.extraItems = new ArrayList();

            for(var5 = 0; var5 < var4; ++var5) {
               this.extraItems.add(GameWindow.ReadString(var1));
            }
         }
      }

      if (var2 >= 31) {
         this.setCustomName(var1.get() == 1);
      }

      if (var2 >= 55) {
         float var6 = var1.getFloat();
         if (var6 >= 0.0F) {
            this.setActualWeight(var6);
            this.setCustomWeight(true);
         }
      }

      if (var2 >= 57) {
         this.setKeyId(var1.getInt());
      }

      if (var2 >= 59) {
         this.setTaintedWater(var1.get() == 1);
      }

      if (var2 >= 62) {
         this.setRemoteControlID(var1.getInt());
         this.setRemoteRange(var1.getInt());
      }

      if (var2 >= 76) {
         this.setColorRed(var1.getFloat());
         this.setColorGreen(var1.getFloat());
         this.setColorBlue(var1.getFloat());
         this.setColor(new Color(this.colorRed, this.colorGreen, this.colorBlue));
      }

      if (var2 >= 90) {
         this.setWorker(GameWindow.ReadString(var1));
      }

      if (var2 >= 93) {
         this.setWetCooldown(var1.getFloat());
      }

      if (var2 >= 94) {
         this.setFavorite(var1.get() == 1);
      }

      if (var2 >= 105 && var1.get() == 1) {
         this.setColor(new Color(var1.getFloat(), var1.getFloat(), var1.getFloat(), var1.getFloat()));
      }

      if (var2 >= 107) {
         this.stashMap = GameWindow.ReadString(var1);
      }

      if (var2 >= 116) {
         this.itemCapacity = var1.getFloat();
      }

      if (var2 >= 120) {
         this.setInfected(var1.get() == 1);
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

   static InventoryItem LoadFromFile(DataInputStream var0) throws IOException {
      GameWindow.ReadString(var0);
      return null;
   }

   public float getScore(SurvivorDesc var1) {
      return 0.0F;
   }

   public IsoGameCharacter getPreviousOwner() {
      return this.previousOwner;
   }

   public void setPreviousOwner(IsoGameCharacter var1) {
      this.previousOwner = var1;
   }

   public Item getScriptItem() {
      return this.ScriptItem;
   }

   public void setScriptItem(Item var1) {
      this.ScriptItem = var1;
   }

   public ItemType getCat() {
      return this.cat;
   }

   public void setCat(ItemType var1) {
      this.cat = var1;
   }

   public String getConsumeMenu() {
      return this.consumeMenu;
   }

   public void setConsumeMenu(String var1) {
      this.consumeMenu = var1;
   }

   public ItemContainer getContainer() {
      return this.container;
   }

   public void setContainer(ItemContainer var1) {
      this.container = var1;
   }

   public ItemContainer getOutermostContainer() {
      if (this.container != null && !"floor".equals(this.container.type)) {
         ItemContainer var1;
         for(var1 = this.container; var1.getContainingItem() != null && var1.getContainingItem().getContainer() != null && !"floor".equals(var1.getContainingItem().getContainer().type); var1 = var1.getContainingItem().getContainer()) {
         }

         return var1;
      } else {
         return null;
      }
   }

   public boolean isInLocalPlayerInventory() {
      if (!GameClient.bClient) {
         return false;
      } else {
         ItemContainer var1 = this.getOutermostContainer();
         if (var1 == null) {
            return false;
         } else {
            return var1.getParent() instanceof IsoPlayer ? ((IsoPlayer)var1.getParent()).isLocalPlayer() : false;
         }
      }
   }

   public boolean isInPlayerInventory() {
      ItemContainer var1 = this.getOutermostContainer();
      return var1 == null ? false : var1.getParent() instanceof IsoPlayer;
   }

   public int getContainerX() {
      return this.containerX;
   }

   public void setContainerX(int var1) {
      this.containerX = var1;
   }

   public int getContainerY() {
      return this.containerY;
   }

   public void setContainerY(int var1) {
      this.containerY = var1;
   }

   public boolean isDisappearOnUse() {
      return this.DisappearOnUse;
   }

   public void setDisappearOnUse(boolean var1) {
      this.DisappearOnUse = var1;
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

   public void setName(String var1) {
      this.name = var1;
   }

   public String getReplaceOnUse() {
      return this.replaceOnUse;
   }

   public void setReplaceOnUse(String var1) {
      this.replaceOnUse = var1;
   }

   public int getConditionMax() {
      return this.ConditionMax;
   }

   public void setConditionMax(int var1) {
      this.ConditionMax = var1;
   }

   public ItemContainer getRightClickContainer() {
      return this.rightClickContainer;
   }

   public void setRightClickContainer(ItemContainer var1) {
      this.rightClickContainer = var1;
   }

   public String getSwingAnim() {
      return this.swingAnim;
   }

   public void setSwingAnim(String var1) {
      this.swingAnim = var1;
   }

   public Texture getTexture() {
      return this.texture;
   }

   public void setTexture(Texture var1) {
      this.texture = var1;
   }

   public Texture getTexturerotten() {
      return this.texturerotten;
   }

   public void setTexturerotten(Texture var1) {
      this.texturerotten = var1;
   }

   public Texture getTextureCooked() {
      return this.textureCooked;
   }

   public void setTextureCooked(Texture var1) {
      this.textureCooked = var1;
   }

   public Texture getTextureBurnt() {
      return this.textureBurnt;
   }

   public void setTextureBurnt(Texture var1) {
      this.textureBurnt = var1;
   }

   public void setType(String var1) {
      this.type = var1;
   }

   public int getUses() {
      return 1;
   }

   public void setUses(int var1) {
   }

   public float getAge() {
      return this.Age;
   }

   public void setAge(float var1) {
      this.Age = var1;
   }

   public float getLastAged() {
      return this.LastAged;
   }

   public void setLastAged(float var1) {
      this.LastAged = var1;
   }

   public void updateAge() {
   }

   public void setAutoAge() {
   }

   public boolean isIsCookable() {
      return this.IsCookable;
   }

   public void setIsCookable(boolean var1) {
      this.IsCookable = var1;
   }

   public float getCookingTime() {
      return this.CookingTime;
   }

   public void setCookingTime(float var1) {
      this.CookingTime = var1;
   }

   public float getMinutesToCook() {
      return this.MinutesToCook;
   }

   public void setMinutesToCook(float var1) {
      this.MinutesToCook = var1;
   }

   public float getMinutesToBurn() {
      return this.MinutesToBurn;
   }

   public void setMinutesToBurn(float var1) {
      this.MinutesToBurn = var1;
   }

   public boolean isCooked() {
      return this.Cooked;
   }

   public void setCooked(boolean var1) {
      this.Cooked = var1;
   }

   public boolean isBurnt() {
      return this.Burnt;
   }

   public void setBurnt(boolean var1) {
      this.Burnt = var1;
   }

   public int getOffAge() {
      return this.OffAge;
   }

   public void setOffAge(int var1) {
      this.OffAge = var1;
   }

   public int getOffAgeMax() {
      return this.OffAgeMax;
   }

   public void setOffAgeMax(int var1) {
      this.OffAgeMax = var1;
   }

   public float getWeight() {
      return this.Weight;
   }

   public void setWeight(float var1) {
      this.Weight = var1;
   }

   public float getActualWeight() {
      return this.ActualWeight;
   }

   public void setActualWeight(float var1) {
      this.ActualWeight = var1;
   }

   public String getWorldTexture() {
      return this.WorldTexture;
   }

   public void setWorldTexture(String var1) {
      this.WorldTexture = var1;
   }

   public String getDescription() {
      return this.Description;
   }

   public void setDescription(String var1) {
      this.Description = var1;
   }

   public int getCondition() {
      return this.Condition;
   }

   public void setCondition(int var1, boolean var2) {
      if (this.Condition > 0 && var1 <= 0 && var2 && this.getBreakSound() != null && !this.getBreakSound().isEmpty() && IsoPlayer.instance != null) {
         IsoPlayer.instance.playSound(this.getBreakSound(), true);
      }

      this.Condition = var1;
      this.setBroken(var1 <= 0);
   }

   public void setCondition(int var1) {
      this.setCondition(var1, true);
   }

   public String getOffString() {
      return this.OffString;
   }

   public void setOffString(String var1) {
      this.OffString = var1;
   }

   public String getCookedString() {
      return this.CookedString;
   }

   public void setCookedString(String var1) {
      this.CookedString = var1;
   }

   public String getUnCookedString() {
      return this.UnCookedString;
   }

   public void setUnCookedString(String var1) {
      this.UnCookedString = var1;
   }

   public String getBurntString() {
      return this.BurntString;
   }

   public void setBurntString(String var1) {
      this.BurntString = var1;
   }

   public String getModule() {
      return this.module;
   }

   public void setModule(String var1) {
      this.module = var1;
   }

   public boolean isAlwaysWelcomeGift() {
      return this.AlwaysWelcomeGift;
   }

   public void setAlwaysWelcomeGift(boolean var1) {
      this.AlwaysWelcomeGift = var1;
   }

   public boolean isCanBandage() {
      return this.CanBandage;
   }

   public void setCanBandage(boolean var1) {
      this.CanBandage = var1;
   }

   public float getBoredomChange() {
      return this.boredomChange;
   }

   public void setBoredomChange(float var1) {
      this.boredomChange = var1;
   }

   public float getUnhappyChange() {
      return this.unhappyChange;
   }

   public void setUnhappyChange(float var1) {
      this.unhappyChange = var1;
   }

   public float getStressChange() {
      return this.stressChange;
   }

   public void setStressChange(float var1) {
      this.stressChange = var1;
   }

   public ArrayList getTaken() {
      return this.Taken;
   }

   public void setTaken(ArrayList var1) {
      this.Taken = var1;
   }

   public IsoDirections getPlaceDir() {
      return this.placeDir;
   }

   public void setPlaceDir(IsoDirections var1) {
      this.placeDir = var1;
   }

   public IsoDirections getNewPlaceDir() {
      return this.newPlaceDir;
   }

   public void setNewPlaceDir(IsoDirections var1) {
      this.newPlaceDir = var1;
   }

   public void setReplaceOnUseOn(String var1) {
      this.ReplaceOnUseOn = var1;
   }

   public String getReplaceOnUseOn() {
      return this.ReplaceOnUseOn;
   }

   public String getReplaceOnUseOnString() {
      String var1 = this.getReplaceOnUseOn();
      if (var1.split("-")[0].trim().contains("WaterSource")) {
         var1 = var1.split("-")[1];
         if (!var1.contains(".")) {
            var1 = this.getModule() + "." + var1;
         }
      }

      return var1;
   }

   public void setIsWaterSource(boolean var1) {
      this.IsWaterSource = var1;
   }

   public boolean isWaterSource() {
      return this.IsWaterSource;
   }

   boolean CanStackNoTemp(InventoryItem var1) {
      return false;
   }

   public void CopyModData(KahluaTable var1) {
      if (var1 != null) {
         KahluaTableIterator var2 = var1.iterator();
         KahluaTable var3 = this.getModData();

         while(var2.advance()) {
            var3.rawset(var2.getKey(), var2.getValue());
         }

      }
   }

   public int getCount() {
      return this.Count;
   }

   public void setCount(int var1) {
      this.Count = var1;
   }

   public boolean isActivated() {
      return this.activated;
   }

   public void setActivated(boolean var1) {
      this.activated = var1;
   }

   public void setCanBeActivated(boolean var1) {
      this.canBeActivated = var1;
   }

   public boolean canBeActivated() {
      return this.canBeActivated;
   }

   public void setLightStrength(float var1) {
      this.lightStrength = var1;
   }

   public float getLightStrength() {
      return this.lightStrength;
   }

   public boolean isTorchCone() {
      return this.isTorchCone;
   }

   public void setTorchCone(boolean var1) {
      this.isTorchCone = var1;
   }

   public int getLightDistance() {
      return this.lightDistance;
   }

   public void setLightDistance(int var1) {
      this.lightDistance = var1;
   }

   public boolean canStoreWater() {
      return this.CanStoreWater;
   }

   public float getFatigueChange() {
      return this.fatigueChange;
   }

   public void setFatigueChange(float var1) {
      this.fatigueChange = var1;
   }

   public float getCurrentCondition() {
      Float var1 = (float)this.Condition / (float)this.ConditionMax;
      return Float.valueOf(var1 * 100.0F);
   }

   public void setColor(Color var1) {
      this.col = var1;
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

   public void setTwoHandWeapon(boolean var1) {
      this.isTwoHandWeapon = var1;
   }

   public String getCustomMenuOption() {
      return this.customMenuOption;
   }

   public void setCustomMenuOption(String var1) {
      this.customMenuOption = var1;
   }

   public void setTooltip(String var1) {
      this.tooltip = var1;
   }

   public String getTooltip() {
      return this.tooltip;
   }

   public String getDisplayCategory() {
      return this.displayCategory;
   }

   public void setDisplayCategory(String var1) {
      this.displayCategory = var1;
   }

   public int getHaveBeenRepaired() {
      return this.haveBeenRepaired;
   }

   public void setHaveBeenRepaired(int var1) {
      this.haveBeenRepaired = var1;
   }

   public boolean isBroken() {
      return this.broken;
   }

   public void setBroken(boolean var1) {
      this.broken = var1;
   }

   public String getReplaceOnBreak() {
      return this.replaceOnBreak;
   }

   public void setReplaceOnBreak(String var1) {
      this.replaceOnBreak = var1;
   }

   public String getDisplayName() {
      return this.name;
   }

   public void setTrap(Boolean var1) {
      this.trap = var1;
   }

   public boolean isTrap() {
      return this.trap;
   }

   public void addExtraItem(String var1) {
      if (this.extraItems == null) {
         this.extraItems = new ArrayList();
      }

      this.extraItems.add(var1);
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

   public void setCustomName(boolean var1) {
      this.customName = var1;
   }

   public boolean isFishingLure() {
      return this.fishingLure;
   }

   public void setFishingLure(boolean var1) {
      this.fishingLure = var1;
   }

   public void copyConditionModData(InventoryItem var1) {
      if (var1.hasModData()) {
         KahluaTableIterator var2 = var1.getModData().iterator();

         while(var2.advance()) {
            if (var2.getKey() instanceof String && ((String)var2.getKey()).startsWith("condition:")) {
               this.getModData().rawset(var2.getKey(), var2.getValue());
            }
         }
      }

   }

   public void setConditionFromModData(InventoryItem var1) {
      if (var1.hasModData()) {
         Object var2 = var1.getModData().rawget("condition:" + this.getType());
         if (var2 != null && var2 instanceof Double) {
            this.setCondition((int)Math.round((Double)var2 * (double)this.getConditionMax()));
         }
      }

   }

   public String getBreakSound() {
      return this.breakSound;
   }

   public void setBreakSound(String var1) {
      this.breakSound = var1;
   }

   public void setBeingFilled(boolean var1) {
      this.beingFilled = var1;
   }

   public boolean isBeingFilled() {
      return this.beingFilled;
   }

   public boolean isAlcoholic() {
      return this.alcoholic;
   }

   public void setAlcoholic(boolean var1) {
      this.alcoholic = var1;
   }

   public float getAlcoholPower() {
      return this.alcoholPower;
   }

   public void setAlcoholPower(float var1) {
      this.alcoholPower = var1;
   }

   public float getBandagePower() {
      return this.bandagePower;
   }

   public void setBandagePower(float var1) {
      this.bandagePower = var1;
   }

   public float getReduceInfectionPower() {
      return this.ReduceInfectionPower;
   }

   public void setReduceInfectionPower(float var1) {
      this.ReduceInfectionPower = var1;
   }

   public final void saveWithSize(ByteBuffer var1, boolean var2) throws IOException {
      int var3 = var1.position();
      var1.putInt(0);
      int var4 = var1.position();
      this.save(var1, var2);
      int var5 = var1.position();
      var1.position(var3);
      var1.putInt(var5 - var4);
      var1.position(var5);
   }

   public boolean isCustomWeight() {
      return this.customWeight;
   }

   public void setCustomWeight(boolean var1) {
      this.customWeight = var1;
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

   public void setKeyId(int var1) {
      this.keyId = var1;
   }

   public boolean isTaintedWater() {
      return this.taintedWater;
   }

   public void setTaintedWater(boolean var1) {
      this.taintedWater = var1;
   }

   public boolean isRemoteController() {
      return this.remoteController;
   }

   public void setRemoteController(boolean var1) {
      this.remoteController = var1;
   }

   public boolean canBeRemote() {
      return this.canBeRemote;
   }

   public void setCanBeRemote(boolean var1) {
      this.canBeRemote = var1;
   }

   public int getRemoteControlID() {
      return this.remoteControlID;
   }

   public void setRemoteControlID(int var1) {
      this.remoteControlID = var1;
   }

   public int getRemoteRange() {
      return this.remoteRange;
   }

   public void setRemoteRange(int var1) {
      this.remoteRange = var1;
   }

   public String getExplosionSound() {
      return this.explosionSound;
   }

   public void setExplosionSound(String var1) {
      this.explosionSound = var1;
   }

   public String getCountDownSound() {
      return this.countDownSound;
   }

   public void setCountDownSound(String var1) {
      this.countDownSound = var1;
   }

   public float getColorRed() {
      return this.colorRed;
   }

   public void setColorRed(float var1) {
      this.colorRed = var1;
   }

   public float getColorGreen() {
      return this.colorGreen;
   }

   public void setColorGreen(float var1) {
      this.colorGreen = var1;
   }

   public float getColorBlue() {
      return this.colorBlue;
   }

   public void setColorBlue(float var1) {
      this.colorBlue = var1;
   }

   public String getEvolvedRecipeName() {
      return this.evolvedRecipeName;
   }

   public void setEvolvedRecipeName(String var1) {
      this.evolvedRecipeName = var1;
   }

   public float getMetalValue() {
      return this.metalValue;
   }

   public void setMetalValue(float var1) {
      this.metalValue = var1;
   }

   public float getItemHeat() {
      return this.itemHeat;
   }

   public void setItemHeat(float var1) {
      if (var1 > 2.0F) {
         var1 = 2.0F;
      }

      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.itemHeat = var1;
   }

   public float getInvHeat() {
      return 1.0F - this.itemHeat;
   }

   public float getMeltingTime() {
      return this.meltingTime;
   }

   public void setMeltingTime(float var1) {
      if (var1 > 100.0F) {
         var1 = 100.0F;
      }

      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.meltingTime = var1;
   }

   public String getWorker() {
      return this.worker;
   }

   public void setWorker(String var1) {
      this.worker = var1;
   }

   public long getID() {
      return this.id;
   }

   public void setID(long var1) {
      this.id = var1;
   }

   public boolean isWet() {
      return this.isWet;
   }

   public void setWet(boolean var1) {
      this.isWet = var1;
   }

   public float getWetCooldown() {
      return this.wetCooldown;
   }

   public void setWetCooldown(float var1) {
      this.wetCooldown = var1;
   }

   public String getItemWhenDry() {
      return this.itemWhenDry;
   }

   public void setItemWhenDry(String var1) {
      this.itemWhenDry = var1;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public void setFavorite(boolean var1) {
      this.favorite = var1;
   }

   public ArrayList getRequireInHandOrInventory() {
      return this.requireInHandOrInventory;
   }

   public void setRequireInHandOrInventory(ArrayList var1) {
      this.requireInHandOrInventory = var1;
   }

   public boolean isCustomColor() {
      return this.customColor;
   }

   public void setCustomColor(boolean var1) {
      this.customColor = var1;
   }

   public String getMap() {
      return this.map;
   }

   public void setMap(String var1) {
      this.map = var1;
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

   public void setStashMap(String var1) {
      this.stashMap = var1;
   }

   public int getMechanicType() {
      return this.mechanicType;
   }

   public void setMechanicType(int var1) {
      this.mechanicType = var1;
   }

   public float getItemCapacity() {
      return this.itemCapacity;
   }

   public void setItemCapacity(float var1) {
      this.itemCapacity = var1;
   }

   public int getMaxCapacity() {
      return this.maxCapacity;
   }

   public void setMaxCapacity(int var1) {
      this.maxCapacity = var1;
   }

   public boolean isConditionAffectsCapacity() {
      return this.ScriptItem != null && this.ScriptItem.isConditionAffectsCapacity();
   }

   public float getBrakeForce() {
      return this.brakeForce;
   }

   public void setBrakeForce(float var1) {
      this.brakeForce = var1;
   }

   public int getChanceToSpawnDamaged() {
      return this.chanceToSpawnDamaged;
   }

   public void setChanceToSpawnDamaged(int var1) {
      this.chanceToSpawnDamaged = var1;
   }

   public float getConditionLowerNormal() {
      return this.conditionLowerNormal;
   }

   public void setConditionLowerNormal(float var1) {
      this.conditionLowerNormal = var1;
   }

   public float getConditionLowerOffroad() {
      return this.conditionLowerOffroad;
   }

   public void setConditionLowerOffroad(float var1) {
      this.conditionLowerOffroad = var1;
   }

   public float getWheelFriction() {
      return this.wheelFriction;
   }

   public void setWheelFriction(float var1) {
      this.wheelFriction = var1;
   }

   public float getSuspensionDamping() {
      return this.suspensionDamping;
   }

   public void setSuspensionDamping(float var1) {
      this.suspensionDamping = var1;
   }

   public float getSuspensionCompression() {
      return this.suspensionCompression;
   }

   public void setSuspensionCompression(float var1) {
      this.suspensionCompression = var1;
   }

   public void setInfected(boolean var1) {
      this.zombieInfected = var1;
   }

   public boolean isInfected() {
      return this.zombieInfected;
   }

   public float getEngineLoudness() {
      return this.engineLoudness;
   }

   public void setEngineLoudness(float var1) {
      this.engineLoudness = var1;
   }
}
