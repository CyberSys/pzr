package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import zombie.GameWindow;
import zombie.characterTextures.BloodBodyPartType;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public final class ItemVisual {
	private String m_fullType;
	private String m_clothingItemName;
	private String m_alternateModelName;
	public static final float NULL_HUE = Float.POSITIVE_INFINITY;
	public float m_Hue = Float.POSITIVE_INFINITY;
	public ImmutableColor m_Tint = null;
	public int m_BaseTexture = -1;
	public int m_TextureChoice = -1;
	public String m_Decal = null;
	private byte[] blood;
	private byte[] dirt;
	private byte[] holes;
	private byte[] basicPatches;
	private byte[] denimPatches;
	private byte[] leatherPatches;
	private InventoryItem inventoryItem = null;
	private static final int LASTSTAND_VERSION1 = 1;
	private static final int LASTSTAND_VERSION = 1;

	public ItemVisual() {
	}

	public ItemVisual(ItemVisual itemVisual) {
		this.copyFrom(itemVisual);
	}

	public void setItemType(String string) {
		Objects.requireNonNull(string);
		assert string.contains(".");
		this.m_fullType = string;
	}

	public String getItemType() {
		return this.m_fullType;
	}

	public void setAlternateModelName(String string) {
		this.m_alternateModelName = string;
	}

	public String getAlternateModelName() {
		return this.m_alternateModelName;
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{ m_clothingItemName:\"" + this.m_clothingItemName + "\"}";
	}

	public String getClothingItemName() {
		return this.m_clothingItemName;
	}

	public void setClothingItemName(String string) {
		this.m_clothingItemName = string;
	}

	public Item getScriptItem() {
		return StringUtils.isNullOrWhitespace(this.m_fullType) ? null : ScriptManager.instance.getItem(this.m_fullType);
	}

	public ClothingItem getClothingItem() {
		Item item = this.getScriptItem();
		if (item == null) {
			return null;
		} else {
			if (!StringUtils.isNullOrWhitespace(this.m_alternateModelName)) {
				if ("LeftHand".equalsIgnoreCase(this.m_alternateModelName)) {
					return item.replaceSecondHand.clothingItem;
				}

				if ("RightHand".equalsIgnoreCase(this.m_alternateModelName)) {
					return item.replacePrimaryHand.clothingItem;
				}
			}

			return item.getClothingItemAsset();
		}
	}

	public void getClothingItemCombinedMask(CharacterMask characterMask) {
		ClothingItem.tryGetCombinedMask(this.getClothingItem(), characterMask);
	}

	public void setHue(float float1) {
		float1 = Math.max(float1, -1.0F);
		float1 = Math.min(float1, 1.0F);
		this.m_Hue = float1;
	}

	public float getHue(ClothingItem clothingItem) {
		if (clothingItem.m_AllowRandomHue) {
			if (this.m_Hue == Float.POSITIVE_INFINITY) {
				this.m_Hue = (float)OutfitRNG.Next(200) / 100.0F - 1.0F;
			}

			return this.m_Hue;
		} else {
			return this.m_Hue = 0.0F;
		}
	}

	public void setTint(ImmutableColor immutableColor) {
		this.m_Tint = immutableColor;
	}

	public ImmutableColor getTint(ClothingItem clothingItem) {
		if (clothingItem.m_AllowRandomTint) {
			if (this.m_Tint == null) {
				this.m_Tint = OutfitRNG.randomImmutableColor();
			}

			return this.m_Tint;
		} else {
			return this.m_Tint = ImmutableColor.white;
		}
	}

	public ImmutableColor getTint() {
		return this.m_Tint;
	}

	public String getBaseTexture(ClothingItem clothingItem) {
		if (clothingItem.m_BaseTextures.isEmpty()) {
			this.m_BaseTexture = -1;
			return null;
		} else {
			if (this.m_BaseTexture < 0 || this.m_BaseTexture >= clothingItem.m_BaseTextures.size()) {
				this.m_BaseTexture = OutfitRNG.Next(clothingItem.m_BaseTextures.size());
			}

			return (String)clothingItem.m_BaseTextures.get(this.m_BaseTexture);
		}
	}

	public String getTextureChoice(ClothingItem clothingItem) {
		if (clothingItem.textureChoices.isEmpty()) {
			this.m_TextureChoice = -1;
			return null;
		} else {
			if (this.m_TextureChoice < 0 || this.m_TextureChoice >= clothingItem.textureChoices.size()) {
				this.m_TextureChoice = OutfitRNG.Next(clothingItem.textureChoices.size());
			}

			return (String)clothingItem.textureChoices.get(this.m_TextureChoice);
		}
	}

	public void setDecal(String string) {
		this.m_Decal = string;
	}

	public String getDecal(ClothingItem clothingItem) {
		if (StringUtils.isNullOrWhitespace(clothingItem.m_DecalGroup)) {
			return this.m_Decal = null;
		} else {
			if (this.m_Decal == null) {
				this.m_Decal = ClothingDecals.instance.getRandomDecal(clothingItem.m_DecalGroup);
			}

			return this.m_Decal;
		}
	}

	public void pickUninitializedValues(ClothingItem clothingItem) {
		if (clothingItem != null && clothingItem.isReady()) {
			this.getHue(clothingItem);
			this.getTint(clothingItem);
			this.getBaseTexture(clothingItem);
			this.getTextureChoice(clothingItem);
			this.getDecal(clothingItem);
		}
	}

	public void synchWithOutfit(ClothingItemReference clothingItemReference) {
		ClothingItem clothingItem = clothingItemReference.getClothingItem();
		this.m_clothingItemName = clothingItem.m_Name;
		this.m_Hue = clothingItemReference.RandomData.m_Hue;
		this.m_Tint = clothingItemReference.RandomData.m_Tint;
		this.m_BaseTexture = clothingItem.m_BaseTextures.indexOf(clothingItemReference.RandomData.m_BaseTexture);
		this.m_TextureChoice = clothingItem.textureChoices.indexOf(clothingItemReference.RandomData.m_TextureChoice);
		this.m_Decal = clothingItemReference.RandomData.m_Decal;
	}

	public void clear() {
		this.m_fullType = null;
		this.m_clothingItemName = null;
		this.m_alternateModelName = null;
		this.m_Hue = Float.POSITIVE_INFINITY;
		this.m_Tint = null;
		this.m_BaseTexture = -1;
		this.m_TextureChoice = -1;
		this.m_Decal = null;
		if (this.blood != null) {
			Arrays.fill(this.blood, (byte)0);
		}

		if (this.dirt != null) {
			Arrays.fill(this.dirt, (byte)0);
		}

		if (this.holes != null) {
			Arrays.fill(this.holes, (byte)0);
		}

		if (this.basicPatches != null) {
			Arrays.fill(this.basicPatches, (byte)0);
		}

		if (this.denimPatches != null) {
			Arrays.fill(this.denimPatches, (byte)0);
		}

		if (this.leatherPatches != null) {
			Arrays.fill(this.leatherPatches, (byte)0);
		}
	}

	public void copyFrom(ItemVisual itemVisual) {
		if (itemVisual == null) {
			this.clear();
		} else {
			ClothingItem clothingItem = itemVisual.getClothingItem();
			if (clothingItem != null) {
				itemVisual.pickUninitializedValues(clothingItem);
			}

			this.m_fullType = itemVisual.m_fullType;
			this.m_clothingItemName = itemVisual.m_clothingItemName;
			this.m_alternateModelName = itemVisual.m_alternateModelName;
			this.m_Hue = itemVisual.m_Hue;
			this.m_Tint = itemVisual.m_Tint;
			this.m_BaseTexture = itemVisual.m_BaseTexture;
			this.m_TextureChoice = itemVisual.m_TextureChoice;
			this.m_Decal = itemVisual.m_Decal;
			this.copyBlood(itemVisual);
			this.copyHoles(itemVisual);
			this.copyPatches(itemVisual);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byte byte1 = 0;
		if (this.m_Tint != null) {
			byte1 = (byte)(byte1 | 1);
		}

		if (this.m_BaseTexture != -1) {
			byte1 = (byte)(byte1 | 2);
		}

		if (this.m_TextureChoice != -1) {
			byte1 = (byte)(byte1 | 4);
		}

		if (this.m_Hue != Float.POSITIVE_INFINITY) {
			byte1 = (byte)(byte1 | 8);
		}

		if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
			byte1 = (byte)(byte1 | 16);
		}

		byteBuffer.put(byte1);
		GameWindow.WriteString(byteBuffer, this.m_fullType);
		GameWindow.WriteString(byteBuffer, this.m_alternateModelName);
		GameWindow.WriteString(byteBuffer, this.m_clothingItemName);
		if (this.m_Tint != null) {
			byteBuffer.put(this.m_Tint.getRedByte());
			byteBuffer.put(this.m_Tint.getGreenByte());
			byteBuffer.put(this.m_Tint.getBlueByte());
		}

		if (this.m_BaseTexture != -1) {
			byteBuffer.put((byte)this.m_BaseTexture);
		}

		if (this.m_TextureChoice != -1) {
			byteBuffer.put((byte)this.m_TextureChoice);
		}

		if (this.m_Hue != Float.POSITIVE_INFINITY) {
			byteBuffer.putFloat(this.m_Hue);
		}

		if (!StringUtils.isNullOrWhitespace(this.m_Decal)) {
			GameWindow.WriteString(byteBuffer, this.m_Decal);
		}

		int int1;
		if (this.blood != null) {
			byteBuffer.put((byte)this.blood.length);
			for (int1 = 0; int1 < this.blood.length; ++int1) {
				byteBuffer.put(this.blood[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.dirt != null) {
			byteBuffer.put((byte)this.dirt.length);
			for (int1 = 0; int1 < this.dirt.length; ++int1) {
				byteBuffer.put(this.dirt[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.holes != null) {
			byteBuffer.put((byte)this.holes.length);
			for (int1 = 0; int1 < this.holes.length; ++int1) {
				byteBuffer.put(this.holes[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.basicPatches != null) {
			byteBuffer.put((byte)this.basicPatches.length);
			for (int1 = 0; int1 < this.basicPatches.length; ++int1) {
				byteBuffer.put(this.basicPatches[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.denimPatches != null) {
			byteBuffer.put((byte)this.denimPatches.length);
			for (int1 = 0; int1 < this.denimPatches.length; ++int1) {
				byteBuffer.put(this.denimPatches[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.leatherPatches != null) {
			byteBuffer.put((byte)this.leatherPatches.length);
			for (int1 = 0; int1 < this.leatherPatches.length; ++int1) {
				byteBuffer.put(this.leatherPatches[int1]);
			}
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		int int2 = byteBuffer.get() & 255;
		if (int1 >= 164) {
			this.m_fullType = GameWindow.ReadString(byteBuffer);
			this.m_alternateModelName = GameWindow.ReadString(byteBuffer);
		}

		this.m_clothingItemName = GameWindow.ReadString(byteBuffer);
		if (int1 < 164) {
			this.m_fullType = ScriptManager.instance.getItemTypeForClothingItem(this.m_clothingItemName);
		}

		int int3;
		if ((int2 & 1) != 0) {
			int int4 = byteBuffer.get() & 255;
			int3 = byteBuffer.get() & 255;
			int int5 = byteBuffer.get() & 255;
			this.m_Tint = new ImmutableColor(int4, int3, int5);
		}

		if ((int2 & 2) != 0) {
			this.m_BaseTexture = byteBuffer.get();
		}

		if ((int2 & 4) != 0) {
			this.m_TextureChoice = byteBuffer.get();
		}

		if (int1 >= 146) {
			if ((int2 & 8) != 0) {
				this.m_Hue = byteBuffer.getFloat();
			}

			if ((int2 & 16) != 0) {
				this.m_Decal = GameWindow.ReadString(byteBuffer);
			}
		}

		byte byte1 = byteBuffer.get();
		if (byte1 > 0 && this.blood == null) {
			this.blood = new byte[BloodBodyPartType.MAX.index()];
		}

		byte byte2;
		for (int3 = 0; int3 < byte1; ++int3) {
			byte2 = byteBuffer.get();
			if (int3 < this.blood.length) {
				this.blood[int3] = byte2;
			}
		}

		if (int1 >= 163) {
			byte1 = byteBuffer.get();
			if (byte1 > 0 && this.dirt == null) {
				this.dirt = new byte[BloodBodyPartType.MAX.index()];
			}

			for (int3 = 0; int3 < byte1; ++int3) {
				byte2 = byteBuffer.get();
				if (int3 < this.dirt.length) {
					this.dirt[int3] = byte2;
				}
			}
		}

		byte1 = byteBuffer.get();
		if (byte1 > 0 && this.holes == null) {
			this.holes = new byte[BloodBodyPartType.MAX.index()];
		}

		for (int3 = 0; int3 < byte1; ++int3) {
			byte2 = byteBuffer.get();
			if (int3 < this.holes.length) {
				this.holes[int3] = byte2;
			}
		}

		if (int1 >= 154) {
			byte1 = byteBuffer.get();
			if (byte1 > 0 && this.basicPatches == null) {
				this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			for (int3 = 0; int3 < byte1; ++int3) {
				byte2 = byteBuffer.get();
				if (int3 < this.basicPatches.length) {
					this.basicPatches[int3] = byte2;
				}
			}
		}

		if (int1 >= 155) {
			byte1 = byteBuffer.get();
			if (byte1 > 0 && this.denimPatches == null) {
				this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			for (int3 = 0; int3 < byte1; ++int3) {
				byte2 = byteBuffer.get();
				if (int3 < this.denimPatches.length) {
					this.denimPatches[int3] = byte2;
				}
			}

			byte1 = byteBuffer.get();
			if (byte1 > 0 && this.leatherPatches == null) {
				this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			for (int3 = 0; int3 < byte1; ++int3) {
				byte2 = byteBuffer.get();
				if (int3 < this.leatherPatches.length) {
					this.leatherPatches[int3] = byte2;
				}
			}
		}
	}

	public void setDenimPatch(BloodBodyPartType bloodBodyPartType) {
		if (this.denimPatches == null) {
			this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
		}

		this.denimPatches[bloodBodyPartType.index()] = -1;
	}

	public float getDenimPatch(BloodBodyPartType bloodBodyPartType) {
		return this.denimPatches == null ? 0.0F : (float)(this.denimPatches[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void setLeatherPatch(BloodBodyPartType bloodBodyPartType) {
		if (this.leatherPatches == null) {
			this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
		}

		this.leatherPatches[bloodBodyPartType.index()] = -1;
	}

	public float getLeatherPatch(BloodBodyPartType bloodBodyPartType) {
		return this.leatherPatches == null ? 0.0F : (float)(this.leatherPatches[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void setBasicPatch(BloodBodyPartType bloodBodyPartType) {
		if (this.basicPatches == null) {
			this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
		}

		this.basicPatches[bloodBodyPartType.index()] = -1;
	}

	public float getBasicPatch(BloodBodyPartType bloodBodyPartType) {
		return this.basicPatches == null ? 0.0F : (float)(this.basicPatches[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public int getBasicPatchesNumber() {
		if (this.basicPatches == null) {
			return 0;
		} else {
			int int1 = 0;
			for (int int2 = 0; int2 < this.basicPatches.length; ++int2) {
				if (this.basicPatches[int2] != 0) {
					++int1;
				}
			}

			return int1;
		}
	}

	public void setHole(BloodBodyPartType bloodBodyPartType) {
		if (this.holes == null) {
			this.holes = new byte[BloodBodyPartType.MAX.index()];
		}

		this.holes[bloodBodyPartType.index()] = -1;
	}

	public float getHole(BloodBodyPartType bloodBodyPartType) {
		return this.holes == null ? 0.0F : (float)(this.holes[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public int getHolesNumber() {
		if (this.holes == null) {
			return 0;
		} else {
			int int1 = 0;
			for (int int2 = 0; int2 < this.holes.length; ++int2) {
				if (this.holes[int2] != 0) {
					++int1;
				}
			}

			return int1;
		}
	}

	public void setBlood(BloodBodyPartType bloodBodyPartType, float float1) {
		if (this.blood == null) {
			this.blood = new byte[BloodBodyPartType.MAX.index()];
		}

		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		this.blood[bloodBodyPartType.index()] = (byte)((int)(float1 * 255.0F));
	}

	public float getBlood(BloodBodyPartType bloodBodyPartType) {
		return this.blood == null ? 0.0F : (float)(this.blood[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public float getDirt(BloodBodyPartType bloodBodyPartType) {
		return this.dirt == null ? 0.0F : (float)(this.dirt[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void setDirt(BloodBodyPartType bloodBodyPartType, float float1) {
		if (this.dirt == null) {
			this.dirt = new byte[BloodBodyPartType.MAX.index()];
		}

		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		this.dirt[bloodBodyPartType.index()] = (byte)((int)(float1 * 255.0F));
	}

	public void copyBlood(ItemVisual itemVisual) {
		if (itemVisual.blood != null) {
			if (this.blood == null) {
				this.blood = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.blood, 0, this.blood, 0, this.blood.length);
		} else if (this.blood != null) {
			Arrays.fill(this.blood, (byte)0);
		}
	}

	public void copyDirt(ItemVisual itemVisual) {
		if (itemVisual.dirt != null) {
			if (this.dirt == null) {
				this.dirt = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.dirt, 0, this.dirt, 0, this.dirt.length);
		} else if (this.dirt != null) {
			Arrays.fill(this.dirt, (byte)0);
		}
	}

	public void copyHoles(ItemVisual itemVisual) {
		if (itemVisual.holes != null) {
			if (this.holes == null) {
				this.holes = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.holes, 0, this.holes, 0, this.holes.length);
		} else if (this.holes != null) {
			Arrays.fill(this.holes, (byte)0);
		}
	}

	public void copyPatches(ItemVisual itemVisual) {
		if (itemVisual.basicPatches != null) {
			if (this.basicPatches == null) {
				this.basicPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.basicPatches, 0, this.basicPatches, 0, this.basicPatches.length);
		} else if (this.basicPatches != null) {
			Arrays.fill(this.basicPatches, (byte)0);
		}

		if (itemVisual.denimPatches != null) {
			if (this.denimPatches == null) {
				this.denimPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.denimPatches, 0, this.denimPatches, 0, this.denimPatches.length);
		} else if (this.denimPatches != null) {
			Arrays.fill(this.denimPatches, (byte)0);
		}

		if (itemVisual.leatherPatches != null) {
			if (this.leatherPatches == null) {
				this.leatherPatches = new byte[BloodBodyPartType.MAX.index()];
			}

			System.arraycopy(itemVisual.leatherPatches, 0, this.leatherPatches, 0, this.leatherPatches.length);
		} else if (this.leatherPatches != null) {
			Arrays.fill(this.leatherPatches, (byte)0);
		}
	}

	public void removeHole(int int1) {
		if (this.holes != null) {
			this.holes[int1] = 0;
		}
	}

	public void removePatch(int int1) {
		if (this.basicPatches != null) {
			this.basicPatches[int1] = 0;
		}

		if (this.denimPatches != null) {
			this.denimPatches[int1] = 0;
		}

		if (this.leatherPatches != null) {
			this.leatherPatches[int1] = 0;
		}
	}

	public void removeBlood() {
		if (this.blood != null) {
			Arrays.fill(this.blood, (byte)0);
		}
	}

	public void removeDirt() {
		if (this.dirt != null) {
			Arrays.fill(this.dirt, (byte)0);
		}
	}

	public float getTotalBlood() {
		float float1 = 0.0F;
		if (this.blood != null) {
			for (int int1 = 0; int1 < this.blood.length; ++int1) {
				float1 += (float)(this.blood[int1] & 255) / 255.0F;
			}
		}

		return float1;
	}

	public InventoryItem getInventoryItem() {
		return this.inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public void setBaseTexture(int int1) {
		this.m_BaseTexture = int1;
	}

	public int getBaseTexture() {
		return this.m_BaseTexture;
	}

	public void setTextureChoice(int int1) {
		this.m_TextureChoice = int1;
	}

	public int getTextureChoice() {
		return this.m_TextureChoice;
	}

	private static StringBuilder toString(ImmutableColor immutableColor, StringBuilder stringBuilder) {
		stringBuilder.append(immutableColor.getRedByte() & 255);
		stringBuilder.append(",");
		stringBuilder.append(immutableColor.getGreenByte() & 255);
		stringBuilder.append(",");
		stringBuilder.append(immutableColor.getBlueByte() & 255);
		return stringBuilder;
	}

	private static ImmutableColor colorFromString(String string) {
		String[] stringArray = string.split(",");
		if (stringArray.length == 3) {
			try {
				int int1 = Integer.parseInt(stringArray[0]);
				int int2 = Integer.parseInt(stringArray[1]);
				int int3 = Integer.parseInt(stringArray[2]);
				return new ImmutableColor((float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F);
			} catch (NumberFormatException numberFormatException) {
			}
		}

		return null;
	}

	public String getLastStandString() {
		Item item = this.getScriptItem();
		if (item == null) {
			return null;
		} else {
			ClothingItem clothingItem = this.getClothingItem();
			if (clothingItem == null) {
				return null;
			} else {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("version=");
				stringBuilder.append(1);
				stringBuilder.append(";");
				stringBuilder.append("type=");
				stringBuilder.append(this.inventoryItem.getFullType());
				stringBuilder.append(";");
				ImmutableColor immutableColor = this.getTint(clothingItem);
				stringBuilder.append("tint=");
				toString(immutableColor, stringBuilder);
				stringBuilder.append(";");
				int int1 = this.getBaseTexture();
				if (int1 != -1) {
					stringBuilder.append("baseTexture=");
					stringBuilder.append(int1);
					stringBuilder.append(";");
				}

				int int2 = this.getTextureChoice();
				if (int2 != -1) {
					stringBuilder.append("textureChoice=");
					stringBuilder.append(int2);
					stringBuilder.append(";");
				}

				float float1 = this.getHue(clothingItem);
				if (float1 != 0.0F) {
					stringBuilder.append("hue=");
					stringBuilder.append(float1);
					stringBuilder.append(";");
				}

				String string = this.getDecal(clothingItem);
				if (!StringUtils.isNullOrWhitespace(string)) {
					stringBuilder.append("decal=");
					stringBuilder.append(string);
					stringBuilder.append(";");
				}

				return stringBuilder.toString();
			}
		}
	}

	public static InventoryItem createLastStandItem(String string) {
		string = string.trim();
		if (!StringUtils.isNullOrWhitespace(string) && string.startsWith("version=")) {
			InventoryItem inventoryItem = null;
			ItemVisual itemVisual = null;
			boolean boolean1 = true;
			String[] stringArray = string.split(";");
			if (stringArray.length >= 2 && stringArray[1].trim().startsWith("type=")) {
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					int int2 = stringArray[int1].indexOf(61);
					if (int2 != -1) {
						String string2 = stringArray[int1].substring(0, int2).trim();
						String string3 = stringArray[int1].substring(int2 + 1).trim();
						byte byte1 = -1;
						switch (string2.hashCode()) {
						case -174809444: 
							if (string2.equals("textureChoice")) {
								byte1 = 4;
							}

							break;
						
						case 103672: 
							if (string2.equals("hue")) {
								byte1 = 3;
							}

							break;
						
						case 3560187: 
							if (string2.equals("tint")) {
								byte1 = 5;
							}

							break;
						
						case 3575610: 
							if (string2.equals("type")) {
								byte1 = 6;
							}

							break;
						
						case 95459245: 
							if (string2.equals("decal")) {
								byte1 = 2;
							}

							break;
						
						case 351608024: 
							if (string2.equals("version")) {
								byte1 = 0;
							}

							break;
						
						case 883640586: 
							if (string2.equals("baseTexture")) {
								byte1 = 1;
							}

						
						}

						switch (byte1) {
						case 0: 
							int int3 = Integer.parseInt(string3);
							if (int3 < 1 || int3 > 1) {
								return null;
							}

							break;
						
						case 1: 
							try {
								itemVisual.setBaseTexture(Integer.parseInt(string3));
							} catch (NumberFormatException numberFormatException) {
							}

							break;
						
						case 2: 
							if (!StringUtils.isNullOrWhitespace(string3)) {
								itemVisual.setDecal(string3);
							}

							break;
						
						case 3: 
							try {
								itemVisual.setHue(Float.parseFloat(string3));
							} catch (NumberFormatException numberFormatException2) {
							}

							break;
						
						case 4: 
							try {
								itemVisual.setTextureChoice(Integer.parseInt(string3));
							} catch (NumberFormatException numberFormatException3) {
							}

							break;
						
						case 5: 
							ImmutableColor immutableColor = colorFromString(string3);
							if (immutableColor != null) {
								itemVisual.setTint(immutableColor);
							}

							break;
						
						case 6: 
							inventoryItem = InventoryItemFactory.CreateItem(string3);
							if (inventoryItem == null) {
								return null;
							}

							itemVisual = inventoryItem.getVisual();
							if (itemVisual == null) {
								return null;
							}

						
						}
					}
				}

				return inventoryItem;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
