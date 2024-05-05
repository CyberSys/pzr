package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public class Moveable extends InventoryItem {
	protected String worldSprite = "";
	private boolean isLight = false;
	private boolean lightUseBattery = false;
	private boolean lightHasBattery = false;
	private String lightBulbItem = "Base.LightBulb";
	private float lightPower = 0.0F;
	private float lightDelta = 2.5E-4F;
	private float lightR = 1.0F;
	private float lightG = 1.0F;
	private float lightB = 1.0F;
	private boolean isMultiGridAnchor = false;
	private IsoSpriteGrid spriteGrid;
	private String customNameFull = "Moveable Object";
	private String movableFullName = "Moveable Object";
	protected boolean canBeDroppedOnFloor = false;
	private boolean hasReadWorldSprite = false;
	protected String customItem = null;

	public Moveable(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.cat = ItemType.Moveable;
	}

	public Moveable(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
		this.cat = ItemType.Moveable;
	}

	public String getName() {
		if ("Moveable Object".equals(this.movableFullName)) {
			return this.name;
		} else if (this.movableFullName.equals(this.name)) {
			return Translator.getMoveableDisplayName(this.customNameFull);
		} else {
			String string = Translator.getMoveableDisplayName(this.movableFullName);
			return string + this.customNameFull.substring(this.movableFullName.length());
		}
	}

	public String getDisplayName() {
		return this.getName();
	}

	public boolean CanBeDroppedOnFloor() {
		if (this.worldSprite != null && this.spriteGrid != null) {
			IsoSprite sprite = IsoSpriteManager.instance.getSprite(this.worldSprite);
			PropertyContainer propertyContainer = sprite.getProperties();
			return this.canBeDroppedOnFloor || !propertyContainer.Is("ForceSingleItem");
		} else {
			return this.canBeDroppedOnFloor;
		}
	}

	public String getMovableFullName() {
		return this.movableFullName;
	}

	public String getCustomNameFull() {
		return this.customNameFull;
	}

	public boolean isMultiGridAnchor() {
		return this.isMultiGridAnchor;
	}

	public IsoSpriteGrid getSpriteGrid() {
		return this.spriteGrid;
	}

	public String getWorldSprite() {
		return this.worldSprite;
	}

	public boolean ReadFromWorldSprite(String string) {
		if (string == null) {
			return false;
		} else if (this.hasReadWorldSprite && this.worldSprite != null && this.worldSprite.equalsIgnoreCase(string)) {
			return true;
		} else {
			this.customItem = null;
			try {
				IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
				if (sprite != null) {
					PropertyContainer propertyContainer = sprite.getProperties();
					if (propertyContainer.Is("IsMoveAble")) {
						if (propertyContainer.Is("CustomItem")) {
							this.customItem = propertyContainer.Val("CustomItem");
							Item item = ScriptManager.instance.FindItem(this.customItem);
							if (item != null) {
								this.Weight = this.ActualWeight = item.ActualWeight;
							}

							this.worldSprite = string;
							if (sprite.getSpriteGrid() != null) {
								this.spriteGrid = sprite.getSpriteGrid();
								int int1 = sprite.getSpriteGrid().getSpriteIndex(sprite);
								this.isMultiGridAnchor = int1 == 0;
							}

							return true;
						}

						this.isLight = propertyContainer.Is("lightR");
						this.worldSprite = string;
						float float1 = 1.0F;
						if (propertyContainer.Is("PickUpWeight")) {
							float1 = Float.parseFloat(propertyContainer.Val("PickUpWeight")) / 10.0F;
						}

						this.Weight = float1;
						this.ActualWeight = float1;
						this.setCustomWeight(true);
						String string2 = "Moveable Object";
						if (propertyContainer.Is("CustomName")) {
							if (propertyContainer.Is("GroupName")) {
								String string3 = propertyContainer.Val("GroupName");
								string2 = string3 + " " + propertyContainer.Val("CustomName");
							} else {
								string2 = propertyContainer.Val("CustomName");
							}
						}

						this.movableFullName = string2;
						this.name = string2;
						this.customNameFull = string2;
						if (sprite.getSpriteGrid() != null) {
							this.spriteGrid = sprite.getSpriteGrid();
							int int2 = sprite.getSpriteGrid().getSpriteIndex(sprite);
							int int3 = sprite.getSpriteGrid().getSpriteCount();
							this.isMultiGridAnchor = int2 == 0;
							if (!propertyContainer.Is("ForceSingleItem")) {
								this.name = this.name + " (" + (int2 + 1) + "/" + int3 + ")";
							} else {
								this.name = this.name + " (1/1)";
							}

							this.customNameFull = this.name;
							Texture texture = null;
							String string4 = "Item_Flatpack";
							if (string4 != null) {
								texture = Texture.getSharedTexture(string4);
								this.setColor(new Color(Rand.Next(0.7F, 1.0F), Rand.Next(0.7F, 1.0F), Rand.Next(0.7F, 1.0F)));
							}

							if (texture == null) {
								texture = Texture.getSharedTexture("media/inventory/Question_On.png");
							}

							this.setTexture(texture);
						} else if (this.texture == null || this.texture.getName() == null || this.texture.getName().equals("Item_Moveable_object") || this.texture.getName().equals("Question_On")) {
							Texture texture2 = null;
							String string5 = null;
							string5 = string;
							if (string != null) {
								texture2 = Texture.getSharedTexture(string);
								if (texture2 != null) {
									texture2 = texture2.splitIcon();
								}
							}

							if (texture2 == null) {
								if (!propertyContainer.Is("MoveType")) {
									string5 = "Item_Moveable_object";
								} else if (propertyContainer.Val("MoveType").equals("WallObject")) {
									string5 = "Item_Moveable_wallobject";
								} else if (propertyContainer.Val("MoveType").equals("WindowObject")) {
									string5 = "Item_Moveable_windowobject";
								} else if (propertyContainer.Val("MoveType").equals("Window")) {
									string5 = "Item_Moveable_window";
								} else if (propertyContainer.Val("MoveType").equals("FloorTile")) {
									string5 = "Item_Moveable_floortile";
								} else if (propertyContainer.Val("MoveType").equals("FloorRug")) {
									string5 = "Item_Moveable_floorrug";
								} else if (propertyContainer.Val("MoveType").equals("Vegitation")) {
									string5 = "Item_Moveable_vegitation";
								}

								if (string5 != null) {
									texture2 = Texture.getSharedTexture(string5);
								}
							}

							if (texture2 == null) {
								texture2 = Texture.getSharedTexture("media/inventory/Question_On.png");
							}

							this.setTexture(texture2);
						}

						this.hasReadWorldSprite = true;
						return true;
					}
				}
			} catch (Exception exception) {
				System.out.println("Error in Moveable item: " + exception.getMessage());
			}

			System.out.println("Warning: Moveable not valid");
			return false;
		}
	}

	public boolean isLight() {
		return this.isLight;
	}

	public void setLight(boolean boolean1) {
		this.isLight = boolean1;
	}

	public boolean isLightUseBattery() {
		return this.lightUseBattery;
	}

	public void setLightUseBattery(boolean boolean1) {
		this.lightUseBattery = boolean1;
	}

	public boolean isLightHasBattery() {
		return this.lightHasBattery;
	}

	public void setLightHasBattery(boolean boolean1) {
		this.lightHasBattery = boolean1;
	}

	public String getLightBulbItem() {
		return this.lightBulbItem;
	}

	public void setLightBulbItem(String string) {
		this.lightBulbItem = string;
	}

	public float getLightPower() {
		return this.lightPower;
	}

	public void setLightPower(float float1) {
		this.lightPower = float1;
	}

	public float getLightDelta() {
		return this.lightDelta;
	}

	public void setLightDelta(float float1) {
		this.lightDelta = float1;
	}

	public float getLightR() {
		return this.lightR;
	}

	public void setLightR(float float1) {
		this.lightR = float1;
	}

	public float getLightG() {
		return this.lightG;
	}

	public void setLightG(float float1) {
		this.lightG = float1;
	}

	public float getLightB() {
		return this.lightB;
	}

	public void setLightB(float float1) {
		this.lightB = float1;
	}

	public int getSaveType() {
		return Item.Type.Moveable.ordinal();
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		GameWindow.WriteString(byteBuffer, this.worldSprite);
		byteBuffer.put((byte)(this.isLight ? 1 : 0));
		if (this.isLight) {
			byteBuffer.put((byte)(this.lightUseBattery ? 1 : 0));
			byteBuffer.put((byte)(this.lightHasBattery ? 1 : 0));
			byteBuffer.put((byte)(this.lightBulbItem != null ? 1 : 0));
			if (this.lightBulbItem != null) {
				GameWindow.WriteString(byteBuffer, this.lightBulbItem);
			}

			byteBuffer.putFloat(this.lightPower);
			byteBuffer.putFloat(this.lightDelta);
			byteBuffer.putFloat(this.lightR);
			byteBuffer.putFloat(this.lightG);
			byteBuffer.putFloat(this.lightB);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.worldSprite = GameWindow.ReadString(byteBuffer);
		if (!this.ReadFromWorldSprite(this.worldSprite) && this instanceof Radio) {
			String string = this.fullType != null ? this.fullType : "unknown";
			DebugLog.log("Moveable.load -> Radio item = " + string);
		}

		if (this.customItem == null && !StringUtils.isNullOrWhitespace(this.worldSprite) && !this.type.equalsIgnoreCase(this.worldSprite)) {
			this.type = this.worldSprite;
			this.fullType = this.module + "." + this.worldSprite;
		}

		this.isLight = byteBuffer.get() == 1;
		if (this.isLight) {
			this.lightUseBattery = byteBuffer.get() == 1;
			this.lightHasBattery = byteBuffer.get() == 1;
			if (byteBuffer.get() == 1) {
				this.lightBulbItem = GameWindow.ReadString(byteBuffer);
			}

			this.lightPower = byteBuffer.getFloat();
			this.lightDelta = byteBuffer.getFloat();
			this.lightR = byteBuffer.getFloat();
			this.lightG = byteBuffer.getFloat();
			this.lightB = byteBuffer.getFloat();
		}
	}

	public void setWorldSprite(String string) {
		this.worldSprite = string;
	}
}
