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

	public Clothing(String string, String string2, String string3, String string4, String string5, String string6) {
		super(string, string2, string3, string4);
		this.bodyLocation = Item.ClothingBodyLocation.None;
		this.SpriteName = null;
		this.bloodLevel = 0.0F;
		this.dirtyness = 0.0F;
		this.dirtyString = Translator.getText("Tooltip_clothing_dirty");
		this.bloodyString = Translator.getText("Tooltip_clothing_bloody");
		this.wornString = Translator.getText("Tooltip_clothing_worn");
		this.ConditionLowerChance = 10000;
		this.module = string;
		this.SpriteName = string6;
		this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
		this.palette = string5;
	}

	public Clothing(String string, String string2, String string3, Item item, String string4, String string5) {
		super(string, string2, string3, item);
		this.bodyLocation = Item.ClothingBodyLocation.None;
		this.SpriteName = null;
		this.bloodLevel = 0.0F;
		this.dirtyness = 0.0F;
		this.dirtyString = Translator.getText("Tooltip_clothing_dirty");
		this.bloodyString = Translator.getText("Tooltip_clothing_bloody");
		this.wornString = Translator.getText("Tooltip_clothing_worn");
		this.ConditionLowerChance = 10000;
		this.module = string;
		this.SpriteName = string5;
		this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
		this.palette = string4;
	}

	public int getSaveType() {
		return Item.Type.Clothing.ordinal();
	}

	public void Unwear() {
		if (this.container.parent instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
			if (this.bodyLocation == Item.ClothingBodyLocation.Bottoms && gameCharacter.getClothingItem_Legs() == this) {
				gameCharacter.setClothingItem_Legs((InventoryItem)null);
				gameCharacter.SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", gameCharacter);
			}

			if (this.bodyLocation == Item.ClothingBodyLocation.Top && gameCharacter.getClothingItem_Torso() == this) {
				gameCharacter.setClothingItem_Torso((InventoryItem)null);
				gameCharacter.SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", gameCharacter);
			}

			if (this.bodyLocation == Item.ClothingBodyLocation.Shoes && gameCharacter.getClothingItem_Feet() == this) {
				gameCharacter.setClothingItem_Feet((InventoryItem)null);
				gameCharacter.SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
				LuaEventManager.triggerEvent("OnClothingUpdated", gameCharacter);
			}

			IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
			ObjectTooltip.LayoutItem layoutItem;
			int int1;
			if (this.bloodLevel != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: bloodLevel:", 1.0F, 1.0F, 0.8F, 1.0F);
				int1 = (int)this.bloodLevel;
				layoutItem.setValueRight(int1, false);
			}

			if (this.dirtyness != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: dirtyness:", 1.0F, 1.0F, 0.8F, 1.0F);
				int1 = (int)this.dirtyness;
				layoutItem.setValueRight(int1, false);
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
		String string = "";
		if (this.isDirty()) {
			string = string + this.dirtyString + ", ";
		}

		if (this.isBloody()) {
			string = string + this.bloodyString + ", ";
		}

		if (this.getCondition() < this.getConditionMax() / 3) {
			string = string + this.wornString + ", ";
		}

		if (string.length() > 2) {
			string = string.substring(0, string.length() - 2);
		}

		string = string.trim();
		return string.isEmpty() ? this.name : Translator.getText("IGUI_ClothingNaming", string, this.name);
	}

	public void update() {
		if (this.container != null && SandboxOptions.instance.ClothingDegradation.getValue() != 1) {
			if (this.container.parent instanceof IsoGameCharacter) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
				if (gameCharacter instanceof IsoPlayer) {
					if (((IsoPlayer)gameCharacter).IsRunning()) {
						this.dirtyness += this.getClothingDirtynessIncreaseLevel() * GameTime.instance.getMultiplier();
					}

					if (!gameCharacter.getCharacterActions().isEmpty()) {
						this.dirtyness += ((BaseAction)gameCharacter.getCharacterActions().get(0)).caloriesModifier * (this.getClothingDirtynessIncreaseLevel() / 3.0F);
					}

					if (gameCharacter.getBodyDamage().getTemperature() > 37.5F) {
						this.dirtyness += this.getClothingDirtynessIncreaseLevel() * GameTime.instance.getMultiplier();
					}

					if (gameCharacter.getBodyDamage().getTemperature() > 38.0F) {
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

	public void Use(boolean boolean1, boolean boolean2) {
		if (this.uses <= 1) {
			this.Unwear();
		}

		super.Use(boolean1, boolean2);
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return this.ModDataMatches(inventoryItem) && this.palette == null && ((Clothing)inventoryItem).palette == null || this.palette.equals(((Clothing)inventoryItem).palette);
	}

	public static Clothing CreateFromSprite(String string) {
		try {
			Clothing clothing = null;
			clothing = (Clothing)InventoryItemFactory.CreateItem(string, 1.0F);
			return clothing;
		} catch (Exception exception) {
			return null;
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (this.getSpriteName() == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getSpriteName());
		}

		byteBuffer.put((byte)0);
		byteBuffer.putFloat(this.col.r);
		byteBuffer.putFloat(this.col.g);
		byteBuffer.putFloat(this.col.b);
		byteBuffer.putFloat(this.dirtyness);
		byteBuffer.putFloat(this.bloodLevel);
		byteBuffer.putFloat(this.insulation);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (byteBuffer.get() == 1) {
			this.setSpriteName(GameWindow.ReadString(byteBuffer));
		}

		String string;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		}

		string = null;
		if (int1 < 46 && byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		}

		this.col = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		if (int1 < 46 && IsoPlayer.instance != null) {
			if ("torso".equals(string)) {
				IsoPlayer.instance.setClothingItem_Torso(this);
			} else if ("leg".equals(string)) {
				IsoPlayer.instance.setClothingItem_Legs(this);
			} else if ("feet".equals(string)) {
				IsoPlayer.instance.setClothingItem_Feet(this);
			} else if ("back".equals(string)) {
				IsoPlayer.instance.setClothingItem_Back(this);
			} else if ("primary".equals(string)) {
				IsoPlayer.instance.setPrimaryHandItem(this);
			} else if ("secondary".equals(string)) {
				IsoPlayer.instance.setSecondaryHandItem(this);
			}
		}

		if (int1 >= 110) {
			this.dirtyness = byteBuffer.getFloat();
			this.bloodLevel = byteBuffer.getFloat();
		}

		if (int1 >= 139) {
			this.insulation = byteBuffer.getFloat();
		}
	}

	public Item.ClothingBodyLocation getBodyLocation() {
		return this.bodyLocation;
	}

	public void setBodyLocation(Item.ClothingBodyLocation clothingBodyLocation) {
		this.bodyLocation = clothingBodyLocation;
	}

	public String getSpriteName() {
		return this.SpriteName;
	}

	public void setSpriteName(String string) {
		this.SpriteName = string;
	}

	public String getPalette() {
		return this.palette;
	}

	public void setPalette(String string) {
		this.palette = string;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public void setTemperature(float float1) {
		this.temperature = float1;
	}

	public void setDirtyness(float float1) {
		this.dirtyness = float1;
	}

	public void setBloodLevel(float float1) {
		this.bloodLevel = float1;
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

	public void setConditionLowerChance(int int1) {
		this.ConditionLowerChance = int1;
	}

	public void setCondition(int int1) {
		this.setCondition(int1, true);
		if (int1 == 0) {
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

	public void setInsulation(float float1) {
		this.insulation = float1;
	}
}
