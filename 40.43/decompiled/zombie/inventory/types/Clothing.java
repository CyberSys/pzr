package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.IsoWorld;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public class Clothing extends InventoryItem {
   private float temperature;
   private float insulation;
   protected Item.ClothingBodyLocation bodyLocation;
   protected String SpriteName;
   protected String palette;
   public float bloodLevel;
   private float dirtyness;
   private final String dirtyString;
   private final String bloodyString;
   private final String wornString;
   private int ConditionLowerChance;

   public String getCategory() {
      return this.mainCategory != null ? this.mainCategory : "Clothing";
   }

   public Clothing(String var1, String var2, String var3, String var4, String var5, String var6) {
      super(var1, var2, var3, var4);
      this.bodyLocation = Item.ClothingBodyLocation.None;
      this.SpriteName = null;
      this.bloodLevel = 0.0F;
      this.dirtyness = 0.0F;
      this.dirtyString = Translator.getText("Tooltip_clothing_dirty");
      this.bloodyString = Translator.getText("Tooltip_clothing_bloody");
      this.wornString = Translator.getText("Tooltip_clothing_worn");
      this.ConditionLowerChance = 10000;
      this.module = var1;
      this.SpriteName = var6;
      this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
      this.palette = var5;
   }

   public Clothing(String var1, String var2, String var3, Item var4, String var5, String var6) {
      super(var1, var2, var3, var4);
      this.bodyLocation = Item.ClothingBodyLocation.None;
      this.SpriteName = null;
      this.bloodLevel = 0.0F;
      this.dirtyness = 0.0F;
      this.dirtyString = Translator.getText("Tooltip_clothing_dirty");
      this.bloodyString = Translator.getText("Tooltip_clothing_bloody");
      this.wornString = Translator.getText("Tooltip_clothing_worn");
      this.ConditionLowerChance = 10000;
      this.module = var1;
      this.SpriteName = var6;
      this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
      this.palette = var5;
   }

   public int getSaveType() {
      return Item.Type.Clothing.ordinal();
   }

   public void Unwear() {
      if (this.container.parent instanceof IsoGameCharacter) {
         IsoGameCharacter var1 = (IsoGameCharacter)this.container.parent;
         if (this.bodyLocation == Item.ClothingBodyLocation.Bottoms && var1.getClothingItem_Legs() == this) {
            var1.setClothingItem_Legs((InventoryItem)null);
            var1.SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
            LuaEventManager.triggerEvent("OnClothingUpdated", var1);
         }

         if (this.bodyLocation == Item.ClothingBodyLocation.Top && var1.getClothingItem_Torso() == this) {
            var1.setClothingItem_Torso((InventoryItem)null);
            var1.SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
            LuaEventManager.triggerEvent("OnClothingUpdated", var1);
         }

         if (this.bodyLocation == Item.ClothingBodyLocation.Shoes && var1.getClothingItem_Feet() == this) {
            var1.setClothingItem_Feet((InventoryItem)null);
            var1.SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
            LuaEventManager.triggerEvent("OnClothingUpdated", var1);
         }

         IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
      }

   }

   public void DoTooltip(ObjectTooltip var1, ObjectTooltip.Layout var2) {
      if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
         ObjectTooltip.LayoutItem var3;
         int var4;
         if (this.bloodLevel != 0.0F) {
            var3 = var2.addItem();
            var3.setLabel("DBG: bloodLevel:", 1.0F, 1.0F, 0.8F, 1.0F);
            var4 = (int)this.bloodLevel;
            var3.setValueRight(var4, false);
         }

         if (this.dirtyness != 0.0F) {
            var3 = var2.addItem();
            var3.setLabel("DBG: dirtyness:", 1.0F, 1.0F, 0.8F, 1.0F);
            var4 = (int)this.dirtyness;
            var3.setValueRight(var4, false);
         }
      }

   }

   public boolean isDirty() {
      return this.dirtyness > 25.0F;
   }

   public boolean isBloody() {
      return this.bloodLevel > 25.0F;
   }

   public String getName() {
      String var1 = "";
      if (this.isDirty()) {
         var1 = var1 + this.dirtyString + ", ";
      }

      if (this.isBloody()) {
         var1 = var1 + this.bloodyString + ", ";
      }

      if (this.getCondition() < this.getConditionMax() / 3) {
         var1 = var1 + this.wornString + ", ";
      }

      if (var1.length() > 2) {
         var1 = var1.substring(0, var1.length() - 2);
      }

      var1 = var1.trim();
      return var1.isEmpty() ? this.name : Translator.getText("IGUI_ClothingNaming", var1, this.name);
   }

   public void update() {
      if (this.container != null && SandboxOptions.instance.ClothingDegradation.getValue() != 1) {
         if (this.container.parent instanceof IsoGameCharacter) {
            IsoGameCharacter var1 = (IsoGameCharacter)this.container.parent;
            if (var1 instanceof IsoPlayer) {
               if (((IsoPlayer)var1).IsRunning()) {
                  this.dirtyness += this.getClothingDirtynessIncreaseLevel() * GameTime.instance.getMultiplier();
               }

               if (!var1.getCharacterActions().isEmpty()) {
                  this.dirtyness += ((BaseAction)var1.getCharacterActions().get(0)).caloriesModifier * (this.getClothingDirtynessIncreaseLevel() / 3.0F);
               }

               if (var1.getBodyDamage().getTemperature() > 37.5F) {
                  this.dirtyness += this.getClothingDirtynessIncreaseLevel() * GameTime.instance.getMultiplier();
               }

               if (var1.getBodyDamage().getTemperature() > 38.0F) {
                  this.dirtyness += this.getClothingDirtynessIncreaseLevel() * 2.0F * GameTime.instance.getMultiplier();
               }
            }
         }

      }
   }

   public boolean finishupdate() {
      if (this.container != null && this.container.parent instanceof IsoGameCharacter) {
         return !this.isEquipped();
      } else {
         return true;
      }
   }

   public void Use(boolean var1, boolean var2) {
      if (this.uses <= 1) {
         this.Unwear();
      }

      super.Use(var1, var2);
   }

   public boolean CanStack(InventoryItem var1) {
      return this.ModDataMatches(var1) && this.palette == null && ((Clothing)var1).palette == null || this.palette.equals(((Clothing)var1).palette);
   }

   public static Clothing CreateFromSprite(String var0) {
      try {
         Clothing var1 = null;
         var1 = (Clothing)InventoryItemFactory.CreateItem(var0, 1.0F);
         return var1;
      } catch (Exception var2) {
         return null;
      }
   }

   public void save(ByteBuffer var1, boolean var2) throws IOException {
      super.save(var1, var2);
      if (this.getSpriteName() == null) {
         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         GameWindow.WriteString(var1, this.getSpriteName());
      }

      var1.put((byte)0);
      var1.putFloat(this.col.r);
      var1.putFloat(this.col.g);
      var1.putFloat(this.col.b);
      var1.putFloat(this.dirtyness);
      var1.putFloat(this.bloodLevel);
      var1.putFloat(this.insulation);
   }

   public void load(ByteBuffer var1, int var2, boolean var3) throws IOException {
      super.load(var1, var2, var3);
      if (var1.get() == 1) {
         this.setSpriteName(GameWindow.ReadString(var1));
      }

      String var4;
      if (var1.get() == 1) {
         var4 = GameWindow.ReadString(var1);
      }

      var4 = null;
      if (var2 < 46 && var1.get() == 1) {
         var4 = GameWindow.ReadString(var1);
      }

      this.col = new Color(var1.getFloat(), var1.getFloat(), var1.getFloat());
      if (var2 < 46 && IsoPlayer.instance != null) {
         if ("torso".equals(var4)) {
            IsoPlayer.instance.setClothingItem_Torso(this);
         } else if ("leg".equals(var4)) {
            IsoPlayer.instance.setClothingItem_Legs(this);
         } else if ("feet".equals(var4)) {
            IsoPlayer.instance.setClothingItem_Feet(this);
         } else if ("back".equals(var4)) {
            IsoPlayer.instance.setClothingItem_Back(this);
         } else if ("primary".equals(var4)) {
            IsoPlayer.instance.setPrimaryHandItem(this);
         } else if ("secondary".equals(var4)) {
            IsoPlayer.instance.setSecondaryHandItem(this);
         }
      }

      if (var2 >= 110) {
         this.dirtyness = var1.getFloat();
         this.bloodLevel = var1.getFloat();
      }

      if (var2 >= 139) {
         this.insulation = var1.getFloat();
      }

   }

   public Item.ClothingBodyLocation getBodyLocation() {
      return this.bodyLocation;
   }

   public void setBodyLocation(Item.ClothingBodyLocation var1) {
      this.bodyLocation = var1;
   }

   public String getSpriteName() {
      return this.SpriteName;
   }

   public void setSpriteName(String var1) {
      this.SpriteName = var1;
   }

   public String getPalette() {
      return this.palette;
   }

   public void setPalette(String var1) {
      this.palette = var1;
   }

   public float getTemperature() {
      return this.temperature;
   }

   public void setTemperature(float var1) {
      this.temperature = var1;
   }

   public void setDirtyness(float var1) {
      this.dirtyness = var1;
   }

   public void setBloodLevel(float var1) {
      this.bloodLevel = var1;
   }

   public float getDirtyness() {
      return this.dirtyness;
   }

   public float getBloodlevel() {
      return this.bloodLevel;
   }

   public int getConditionLowerChance() {
      return this.ConditionLowerChance;
   }

   public void setConditionLowerChance(int var1) {
      this.ConditionLowerChance = var1;
   }

   public void setCondition(int var1) {
      this.setCondition(var1, true);
      if (var1 == 0) {
         this.Unwear();
         this.container.Remove((InventoryItem)this);
      }

   }

   public float getClothingDirtynessIncreaseLevel() {
      if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
         return 2.5E-4F;
      } else {
         return SandboxOptions.instance.ClothingDegradation.getValue() == 4 ? 0.025F : 0.0025F;
      }
   }

   public float getInsulation() {
      return this.insulation;
   }

   public void setInsulation(float var1) {
      this.insulation = var1;
   }
}
