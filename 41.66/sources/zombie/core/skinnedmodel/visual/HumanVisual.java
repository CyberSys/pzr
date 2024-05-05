package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameWindow;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.HairOutfitDefinitions;
import zombie.characters.SurvivorDesc;
import zombie.characters.WornItems.BodyLocation;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemReference;
import zombie.core.skinnedmodel.population.DefaultClothing;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoWorld;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public final class HumanVisual extends BaseVisual {
	private final IHumanVisual owner;
	private ImmutableColor skinColor;
	private int skinTexture;
	private String skinTextureName;
	public int zombieRotStage;
	private ImmutableColor hairColor;
	private ImmutableColor beardColor;
	private String hairModel;
	private String beardModel;
	private int bodyHair;
	private final byte[] blood;
	private final byte[] dirt;
	private final byte[] holes;
	private final ItemVisuals bodyVisuals;
	private Outfit outfit;
	private String nonAttachedHair;
	private static final ArrayList itemVisualLocations = new ArrayList();
	private static final int LASTSTAND_VERSION1 = 1;
	private static final int LASTSTAND_VERSION = 1;

	public HumanVisual(IHumanVisual iHumanVisual) {
		this.skinColor = ImmutableColor.white;
		this.skinTexture = -1;
		this.skinTextureName = null;
		this.zombieRotStage = -1;
		this.bodyHair = -1;
		this.blood = new byte[BloodBodyPartType.MAX.index()];
		this.dirt = new byte[BloodBodyPartType.MAX.index()];
		this.holes = new byte[BloodBodyPartType.MAX.index()];
		this.bodyVisuals = new ItemVisuals();
		this.outfit = null;
		this.nonAttachedHair = null;
		this.owner = iHumanVisual;
		Arrays.fill(this.blood, (byte)0);
		Arrays.fill(this.dirt, (byte)0);
		Arrays.fill(this.holes, (byte)0);
	}

	public boolean isFemale() {
		return this.owner.isFemale();
	}

	public boolean isZombie() {
		return this.owner.isZombie();
	}

	public boolean isSkeleton() {
		return this.owner.isSkeleton();
	}

	public void setSkinColor(ImmutableColor immutableColor) {
		this.skinColor = immutableColor;
	}

	public ImmutableColor getSkinColor() {
		if (this.skinColor == null) {
			this.skinColor = new ImmutableColor(SurvivorDesc.getRandomSkinColor());
		}

		return this.skinColor;
	}

	public void setBodyHairIndex(int int1) {
		this.bodyHair = int1;
	}

	public int getBodyHairIndex() {
		return this.bodyHair;
	}

	public void setSkinTextureIndex(int int1) {
		this.skinTexture = int1;
	}

	public int getSkinTextureIndex() {
		return this.skinTexture;
	}

	public void setSkinTextureName(String string) {
		this.skinTextureName = string;
	}

	public float lerp(float float1, float float2, float float3) {
		if (float3 < 0.0F) {
			float3 = 0.0F;
		}

		if (float3 >= 1.0F) {
			float3 = 1.0F;
		}

		float float4 = float2 - float1;
		float float5 = float4 * float3;
		return float1 + float5;
	}

	public int pickRandomZombieRotStage() {
		int int1 = Math.max((int)IsoWorld.instance.getWorldAgeDays(), 0);
		float float1 = 20.0F;
		float float2 = 90.0F;
		float float3 = 100.0F;
		float float4 = 20.0F;
		float float5 = 10.0F;
		float float6 = 30.0F;
		if (int1 >= 180) {
			float4 = 0.0F;
			float6 = 10.0F;
		}

		float float7 = (float)int1 - float1;
		float float8 = float7 / (float2 - float1);
		float float9 = this.lerp(float3, float4, float8);
		float float10 = this.lerp(float5, float6, float8);
		float float11 = (float)OutfitRNG.Next(100);
		if (float11 < float9) {
			return 1;
		} else {
			return float11 < float10 + float9 ? 2 : 3;
		}
	}

	public String getSkinTexture() {
		if (this.skinTextureName != null) {
			return this.skinTextureName;
		} else {
			String string = "";
			ArrayList arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins : PopTemplateManager.instance.m_MaleSkins;
			if (this.owner.isZombie() && this.owner.isSkeleton()) {
				if (this.owner.isFemale()) {
					arrayList = PopTemplateManager.instance.m_SkeletonFemaleSkins_Zombie;
				} else {
					arrayList = PopTemplateManager.instance.m_SkeletonMaleSkins_Zombie;
				}
			} else if (this.owner.isZombie()) {
				if (this.zombieRotStage < 1 || this.zombieRotStage > 3) {
					this.zombieRotStage = this.pickRandomZombieRotStage();
				}

				switch (this.zombieRotStage) {
				case 1: 
					arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie1 : PopTemplateManager.instance.m_MaleSkins_Zombie1;
					break;
				
				case 2: 
					arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie2 : PopTemplateManager.instance.m_MaleSkins_Zombie2;
					break;
				
				case 3: 
					arrayList = this.owner.isFemale() ? PopTemplateManager.instance.m_FemaleSkins_Zombie3 : PopTemplateManager.instance.m_MaleSkins_Zombie3;
				
				}
			} else if (!this.owner.isFemale()) {
				string = !this.owner.isZombie() && this.bodyHair >= 0 ? "a" : "";
			}

			if (this.skinTexture < 0 || this.skinTexture >= arrayList.size()) {
				this.skinTexture = OutfitRNG.Next(arrayList.size());
			}

			String string2 = (String)arrayList.get(this.skinTexture);
			return string2 + string;
		}
	}

	public void setHairColor(ImmutableColor immutableColor) {
		this.hairColor = immutableColor;
	}

	public ImmutableColor getHairColor() {
		if (this.hairColor == null) {
			this.hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor(this.outfit != null ? this.outfit.m_Name : null);
		}

		return this.hairColor;
	}

	public void setBeardColor(ImmutableColor immutableColor) {
		this.beardColor = immutableColor;
	}

	public ImmutableColor getBeardColor() {
		if (this.beardColor == null) {
			this.beardColor = this.getHairColor();
		}

		return this.beardColor;
	}

	public void setHairModel(String string) {
		this.hairModel = string;
	}

	public String getHairModel() {
		if (this.owner.isFemale()) {
			if (HairStyles.instance.FindFemaleStyle(this.hairModel) == null) {
				this.hairModel = HairStyles.instance.getRandomFemaleStyle(this.outfit != null ? this.outfit.m_Name : null);
			}
		} else if (HairStyles.instance.FindMaleStyle(this.hairModel) == null) {
			this.hairModel = HairStyles.instance.getRandomMaleStyle(this.outfit != null ? this.outfit.m_Name : null);
		}

		return this.hairModel;
	}

	public void setBeardModel(String string) {
		this.beardModel = string;
	}

	public String getBeardModel() {
		if (this.owner.isFemale()) {
			this.beardModel = null;
		} else if (BeardStyles.instance.FindStyle(this.beardModel) == null) {
			this.beardModel = BeardStyles.instance.getRandomStyle(this.outfit != null ? this.outfit.m_Name : null);
		}

		return this.beardModel;
	}

	public void setBlood(BloodBodyPartType bloodBodyPartType, float float1) {
		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		this.blood[bloodBodyPartType.index()] = (byte)((int)(float1 * 255.0F));
	}

	public float getBlood(BloodBodyPartType bloodBodyPartType) {
		return (float)(this.blood[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void setDirt(BloodBodyPartType bloodBodyPartType, float float1) {
		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		this.dirt[bloodBodyPartType.index()] = (byte)((int)(float1 * 255.0F));
	}

	public float getDirt(BloodBodyPartType bloodBodyPartType) {
		return (float)(this.dirt[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void setHole(BloodBodyPartType bloodBodyPartType) {
		this.holes[bloodBodyPartType.index()] = -1;
	}

	public float getHole(BloodBodyPartType bloodBodyPartType) {
		return (float)(this.holes[bloodBodyPartType.index()] & 255) / 255.0F;
	}

	public void removeBlood() {
		Arrays.fill(this.blood, (byte)0);
	}

	public void removeDirt() {
		Arrays.fill(this.dirt, (byte)0);
	}

	public void randomBlood() {
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			this.setBlood(BloodBodyPartType.FromIndex(int1), OutfitRNG.Next(0.0F, 1.0F));
		}
	}

	public void randomDirt() {
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			this.setDirt(BloodBodyPartType.FromIndex(int1), OutfitRNG.Next(0.0F, 1.0F));
		}
	}

	public float getTotalBlood() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.blood.length; ++int1) {
			float1 += (float)(this.blood[int1] & 255) / 255.0F;
		}

		return float1;
	}

	public void clear() {
		this.skinColor = ImmutableColor.white;
		this.skinTexture = -1;
		this.skinTextureName = null;
		this.zombieRotStage = -1;
		this.hairColor = null;
		this.beardColor = null;
		this.hairModel = null;
		this.nonAttachedHair = null;
		this.beardModel = null;
		this.bodyHair = -1;
		Arrays.fill(this.blood, (byte)0);
		Arrays.fill(this.dirt, (byte)0);
		Arrays.fill(this.holes, (byte)0);
		this.bodyVisuals.clear();
	}

	public void copyFrom(HumanVisual humanVisual) {
		if (humanVisual == null) {
			this.clear();
		} else {
			humanVisual.getHairColor();
			humanVisual.getHairModel();
			humanVisual.getBeardModel();
			humanVisual.getSkinTexture();
			this.skinColor = humanVisual.skinColor;
			this.skinTexture = humanVisual.skinTexture;
			this.skinTextureName = humanVisual.skinTextureName;
			this.zombieRotStage = humanVisual.zombieRotStage;
			this.hairColor = humanVisual.hairColor;
			this.beardColor = humanVisual.beardColor;
			this.hairModel = humanVisual.hairModel;
			this.nonAttachedHair = humanVisual.nonAttachedHair;
			this.beardModel = humanVisual.beardModel;
			this.bodyHair = humanVisual.bodyHair;
			this.outfit = humanVisual.outfit;
			System.arraycopy(humanVisual.blood, 0, this.blood, 0, this.blood.length);
			System.arraycopy(humanVisual.dirt, 0, this.dirt, 0, this.dirt.length);
			System.arraycopy(humanVisual.holes, 0, this.holes, 0, this.holes.length);
			this.bodyVisuals.clear();
			this.bodyVisuals.addAll(humanVisual.bodyVisuals);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byte byte1 = 0;
		if (this.hairColor != null) {
			byte1 = (byte)(byte1 | 4);
		}

		if (this.beardColor != null) {
			byte1 = (byte)(byte1 | 2);
		}

		if (this.skinColor != null) {
			byte1 = (byte)(byte1 | 8);
		}

		if (this.beardModel != null) {
			byte1 = (byte)(byte1 | 16);
		}

		if (this.hairModel != null) {
			byte1 = (byte)(byte1 | 32);
		}

		if (this.skinTextureName != null) {
			byte1 = (byte)(byte1 | 64);
		}

		byteBuffer.put(byte1);
		if (this.hairColor != null) {
			byteBuffer.put(this.hairColor.getRedByte());
			byteBuffer.put(this.hairColor.getGreenByte());
			byteBuffer.put(this.hairColor.getBlueByte());
		}

		if (this.beardColor != null) {
			byteBuffer.put(this.beardColor.getRedByte());
			byteBuffer.put(this.beardColor.getGreenByte());
			byteBuffer.put(this.beardColor.getBlueByte());
		}

		if (this.skinColor != null) {
			byteBuffer.put(this.skinColor.getRedByte());
			byteBuffer.put(this.skinColor.getGreenByte());
			byteBuffer.put(this.skinColor.getBlueByte());
		}

		byteBuffer.put((byte)this.bodyHair);
		byteBuffer.put((byte)this.skinTexture);
		byteBuffer.put((byte)this.zombieRotStage);
		if (this.skinTextureName != null) {
			GameWindow.WriteString(byteBuffer, this.skinTextureName);
		}

		if (this.beardModel != null) {
			GameWindow.WriteString(byteBuffer, this.beardModel);
		}

		if (this.hairModel != null) {
			GameWindow.WriteString(byteBuffer, this.hairModel);
		}

		byteBuffer.put((byte)this.blood.length);
		int int1;
		for (int1 = 0; int1 < this.blood.length; ++int1) {
			byteBuffer.put(this.blood[int1]);
		}

		byteBuffer.put((byte)this.dirt.length);
		for (int1 = 0; int1 < this.dirt.length; ++int1) {
			byteBuffer.put(this.dirt[int1]);
		}

		byteBuffer.put((byte)this.holes.length);
		for (int1 = 0; int1 < this.holes.length; ++int1) {
			byteBuffer.put(this.holes[int1]);
		}

		byteBuffer.put((byte)this.bodyVisuals.size());
		for (int1 = 0; int1 < this.bodyVisuals.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)this.bodyVisuals.get(int1);
			itemVisual.save(byteBuffer);
		}

		GameWindow.WriteString(byteBuffer, this.getNonAttachedHair());
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		int int2 = byteBuffer.get() & 255;
		int int3;
		int int4;
		int int5;
		if ((int2 & 4) != 0) {
			int3 = byteBuffer.get() & 255;
			int4 = byteBuffer.get() & 255;
			int5 = byteBuffer.get() & 255;
			this.hairColor = new ImmutableColor(int3, int4, int5);
		}

		if ((int2 & 2) != 0) {
			int3 = byteBuffer.get() & 255;
			int4 = byteBuffer.get() & 255;
			int5 = byteBuffer.get() & 255;
			this.beardColor = new ImmutableColor(int3, int4, int5);
		}

		if ((int2 & 8) != 0) {
			int3 = byteBuffer.get() & 255;
			int4 = byteBuffer.get() & 255;
			int5 = byteBuffer.get() & 255;
			this.skinColor = new ImmutableColor(int3, int4, int5);
		}

		this.bodyHair = byteBuffer.get();
		this.skinTexture = byteBuffer.get();
		if (int1 >= 156) {
			this.zombieRotStage = byteBuffer.get();
		}

		if ((int2 & 64) != 0) {
			this.skinTextureName = GameWindow.ReadString(byteBuffer);
		}

		if ((int2 & 16) != 0) {
			this.beardModel = GameWindow.ReadString(byteBuffer);
		}

		if ((int2 & 32) != 0) {
			this.hairModel = GameWindow.ReadString(byteBuffer);
		}

		byte byte1 = byteBuffer.get();
		byte byte2;
		for (int4 = 0; int4 < byte1; ++int4) {
			byte2 = byteBuffer.get();
			if (int4 < this.blood.length) {
				this.blood[int4] = byte2;
			}
		}

		if (int1 >= 163) {
			byte1 = byteBuffer.get();
			for (int4 = 0; int4 < byte1; ++int4) {
				byte2 = byteBuffer.get();
				if (int4 < this.dirt.length) {
					this.dirt[int4] = byte2;
				}
			}
		}

		byte1 = byteBuffer.get();
		for (int4 = 0; int4 < byte1; ++int4) {
			byte2 = byteBuffer.get();
			if (int4 < this.holes.length) {
				this.holes[int4] = byte2;
			}
		}

		byte1 = byteBuffer.get();
		for (int4 = 0; int4 < byte1; ++int4) {
			ItemVisual itemVisual = new ItemVisual();
			itemVisual.load(byteBuffer, int1);
			this.bodyVisuals.add(itemVisual);
		}

		this.setNonAttachedHair(GameWindow.ReadString(byteBuffer));
	}

	public ModelInstance createModelInstance() {
		return null;
	}

	public static CharacterMask GetMask(ItemVisuals itemVisuals) {
		CharacterMask characterMask = new CharacterMask();
		for (int int1 = itemVisuals.size() - 1; int1 >= 0; --int1) {
			((ItemVisual)itemVisuals.get(int1)).getClothingItemCombinedMask(characterMask);
		}

		return characterMask;
	}

	public void synchWithOutfit(Outfit outfit) {
		if (outfit != null) {
			this.hairColor = outfit.RandomData.m_hairColor;
			this.beardColor = this.hairColor;
			this.hairModel = this.owner.isFemale() ? outfit.RandomData.m_femaleHairName : outfit.RandomData.m_maleHairName;
			this.beardModel = this.owner.isFemale() ? null : outfit.RandomData.m_beardName;
			this.getSkinTexture();
		}
	}

	public void dressInNamedOutfit(String string, ItemVisuals itemVisuals) {
		itemVisuals.clear();
		if (!StringUtils.isNullOrWhitespace(string)) {
			Outfit outfit = this.owner.isFemale() ? OutfitManager.instance.FindFemaleOutfit(string) : OutfitManager.instance.FindMaleOutfit(string);
			if (outfit != null) {
				Outfit outfit2 = outfit.clone();
				outfit2.Randomize();
				this.dressInOutfit(outfit2, itemVisuals);
			}
		}
	}

	public void dressInClothingItem(String string, ItemVisuals itemVisuals) {
		this.dressInClothingItem(string, itemVisuals, true);
	}

	public void dressInClothingItem(String string, ItemVisuals itemVisuals, boolean boolean1) {
		if (boolean1) {
			this.clear();
			itemVisuals.clear();
		}

		ClothingItem clothingItem = OutfitManager.instance.getClothingItem(string);
		if (clothingItem != null) {
			Outfit outfit = new Outfit();
			ClothingItemReference clothingItemReference = new ClothingItemReference();
			clothingItemReference.itemGUID = string;
			outfit.m_items.add(clothingItemReference);
			outfit.m_Pants = false;
			outfit.m_Top = false;
			outfit.Randomize();
			this.dressInOutfit(outfit, itemVisuals);
		}
	}

	private void dressInOutfit(Outfit outfit, ItemVisuals itemVisuals) {
		this.setOutfit(outfit);
		this.getItemVisualLocations(itemVisuals, itemVisualLocations);
		String string;
		if (outfit.m_Pants) {
			string = outfit.m_AllowPantsHue ? DefaultClothing.instance.pickPantsHue() : (outfit.m_AllowPantsTint ? DefaultClothing.instance.pickPantsTint() : DefaultClothing.instance.pickPantsTexture());
			this.addClothingItem(itemVisuals, itemVisualLocations, string, (ClothingItemReference)null);
		}

		if (outfit.m_Top && outfit.RandomData.m_hasTop) {
			if (outfit.RandomData.m_hasTShirt) {
				if (outfit.RandomData.m_hasTShirtDecal && outfit.GetMask().isTorsoVisible() && outfit.m_AllowTShirtDecal) {
					string = outfit.m_AllowTopTint ? DefaultClothing.instance.pickTShirtDecalTint() : DefaultClothing.instance.pickTShirtDecalTexture();
				} else {
					string = outfit.m_AllowTopTint ? DefaultClothing.instance.pickTShirtTint() : DefaultClothing.instance.pickTShirtTexture();
				}
			} else {
				string = outfit.m_AllowTopTint ? DefaultClothing.instance.pickVestTint() : DefaultClothing.instance.pickVestTexture();
			}

			this.addClothingItem(itemVisuals, itemVisualLocations, string, (ClothingItemReference)null);
		}

		for (int int1 = 0; int1 < outfit.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)outfit.m_items.get(int1);
			ClothingItem clothingItem = clothingItemReference.getClothingItem();
			if (clothingItem != null && clothingItem.isReady()) {
				this.addClothingItem(itemVisuals, itemVisualLocations, clothingItem.m_Name, clothingItemReference);
			}
		}

		outfit.m_Pants = false;
		outfit.m_Top = false;
		outfit.RandomData.m_topTexture = null;
		outfit.RandomData.m_pantsTexture = null;
	}

	public ItemVisuals getBodyVisuals() {
		return this.bodyVisuals;
	}

	public ItemVisual addBodyVisual(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			Item item = ScriptManager.instance.getItemForClothingItem(string);
			if (item == null) {
				return null;
			} else {
				ClothingItem clothingItem = item.getClothingItemAsset();
				if (clothingItem == null) {
					return null;
				} else {
					for (int int1 = 0; int1 < this.bodyVisuals.size(); ++int1) {
						if (((ItemVisual)this.bodyVisuals.get(int1)).getClothingItemName().equals(string)) {
							return null;
						}
					}

					ClothingItemReference clothingItemReference = new ClothingItemReference();
					clothingItemReference.itemGUID = clothingItem.m_GUID;
					clothingItemReference.randomize();
					ItemVisual itemVisual = new ItemVisual();
					itemVisual.setItemType(item.getFullName());
					itemVisual.synchWithOutfit(clothingItemReference);
					this.bodyVisuals.add(itemVisual);
					return itemVisual;
				}
			}
		}
	}

	private void getItemVisualLocations(ItemVisuals itemVisuals, ArrayList arrayList) {
		arrayList.clear();
		for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
			Item item = itemVisual.getScriptItem();
			if (item == null) {
				arrayList.add((Object)null);
			} else {
				String string = item.getBodyLocation();
				if (StringUtils.isNullOrWhitespace(string)) {
					string = item.CanBeEquipped;
				}

				arrayList.add(string);
			}
		}
	}

	public ItemVisual addClothingItem(ItemVisuals itemVisuals, Item item) {
		if (item == null) {
			return null;
		} else {
			ClothingItem clothingItem = item.getClothingItemAsset();
			if (clothingItem == null) {
				return null;
			} else if (!clothingItem.isReady()) {
				return null;
			} else {
				this.getItemVisualLocations(itemVisuals, itemVisualLocations);
				return this.addClothingItem(itemVisuals, itemVisualLocations, clothingItem.m_Name, (ClothingItemReference)null);
			}
		}
	}

	private ItemVisual addClothingItem(ItemVisuals itemVisuals, ArrayList arrayList, String string, ClothingItemReference clothingItemReference) {
		assert itemVisuals.size() == arrayList.size();
		if (clothingItemReference != null && !clothingItemReference.RandomData.m_Active) {
			return null;
		} else if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			Item item = ScriptManager.instance.getItemForClothingItem(string);
			if (item == null) {
				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.warn("Could not find item type for %s", string);
				}

				return null;
			} else {
				ClothingItem clothingItem = item.getClothingItemAsset();
				if (clothingItem == null) {
					return null;
				} else if (!clothingItem.isReady()) {
					return null;
				} else {
					String string2 = item.getBodyLocation();
					if (StringUtils.isNullOrWhitespace(string2)) {
						string2 = item.CanBeEquipped;
					}

					if (StringUtils.isNullOrWhitespace(string2)) {
						return null;
					} else {
						if (clothingItemReference == null) {
							clothingItemReference = new ClothingItemReference();
							clothingItemReference.itemGUID = clothingItem.m_GUID;
							clothingItemReference.randomize();
						}

						if (!clothingItemReference.RandomData.m_Active) {
							return null;
						} else {
							BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
							BodyLocation bodyLocation = bodyLocationGroup.getLocation(string2);
							if (bodyLocation == null) {
								DebugLog.General.error("The game can\'t found location \'" + string2 + "\' for the item \'" + item.name + "\'");
								return null;
							} else {
								int int1;
								if (!bodyLocation.isMultiItem()) {
									int1 = arrayList.indexOf(string2);
									if (int1 != -1) {
										itemVisuals.remove(int1);
										arrayList.remove(int1);
									}
								}

								for (int1 = 0; int1 < itemVisuals.size(); ++int1) {
									if (bodyLocationGroup.isExclusive(string2, (String)arrayList.get(int1))) {
										itemVisuals.remove(int1);
										arrayList.remove(int1);
										--int1;
									}
								}

								assert itemVisuals.size() == arrayList.size();
								int1 = bodyLocationGroup.indexOf(string2);
								int int2 = itemVisuals.size();
								for (int int3 = 0; int3 < itemVisuals.size(); ++int3) {
									if (bodyLocationGroup.indexOf((String)arrayList.get(int3)) > int1) {
										int2 = int3;
										break;
									}
								}

								ItemVisual itemVisual = new ItemVisual();
								itemVisual.setItemType(item.getFullName());
								itemVisual.synchWithOutfit(clothingItemReference);
								itemVisuals.add(int2, itemVisual);
								arrayList.add(int2, string2);
								return itemVisual;
							}
						}
					}
				}
			}
		}
	}

	public Outfit getOutfit() {
		return this.outfit;
	}

	public void setOutfit(Outfit outfit) {
		this.outfit = outfit;
	}

	public String getNonAttachedHair() {
		return this.nonAttachedHair;
	}

	public void setNonAttachedHair(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			string = null;
		}

		this.nonAttachedHair = string;
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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("version=");
		stringBuilder.append(1);
		stringBuilder.append(";");
		if (this.getHairColor() != null) {
			stringBuilder.append("hairColor=");
			toString(this.getHairColor(), stringBuilder);
			stringBuilder.append(";");
		}

		if (this.getBeardColor() != null) {
			stringBuilder.append("beardColor=");
			toString(this.getBeardColor(), stringBuilder);
			stringBuilder.append(";");
		}

		if (this.getSkinColor() != null) {
			stringBuilder.append("skinColor=");
			toString(this.getSkinColor(), stringBuilder);
			stringBuilder.append(";");
		}

		stringBuilder.append("bodyHair=");
		stringBuilder.append(this.getBodyHairIndex());
		stringBuilder.append(";");
		stringBuilder.append("skinTexture=");
		stringBuilder.append(this.getSkinTextureIndex());
		stringBuilder.append(";");
		if (this.getSkinTexture() != null) {
			stringBuilder.append("skinTextureName=");
			stringBuilder.append(this.getSkinTexture());
			stringBuilder.append(";");
		}

		if (this.getHairModel() != null) {
			stringBuilder.append("hairModel=");
			stringBuilder.append(this.getHairModel());
			stringBuilder.append(";");
		}

		if (this.getBeardModel() != null) {
			stringBuilder.append("beardModel=");
			stringBuilder.append(this.getBeardModel());
			stringBuilder.append(";");
		}

		return stringBuilder.toString();
	}

	public boolean loadLastStandString(String string) {
		string = string.trim();
		if (!StringUtils.isNullOrWhitespace(string) && string.startsWith("version=")) {
			boolean boolean1 = true;
			String[] stringArray = string.split(";");
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				int int2 = stringArray[int1].indexOf(61);
				if (int2 != -1) {
					String string2 = stringArray[int1].substring(0, int2).trim();
					String string3 = stringArray[int1].substring(int2 + 1).trim();
					byte byte1 = -1;
					switch (string2.hashCode()) {
					case -1669441005: 
						if (string2.equals("beardColor")) {
							byte1 = 1;
						}

						break;
					
					case -1660213799: 
						if (string2.equals("beardModel")) {
							byte1 = 2;
						}

						break;
					
					case -1427300215: 
						if (string2.equals("skinTextureName")) {
							byte1 = 8;
						}

						break;
					
					case 284826785: 
						if (string2.equals("hairColor")) {
							byte1 = 4;
						}

						break;
					
					case 294053991: 
						if (string2.equals("hairModel")) {
							byte1 = 5;
						}

						break;
					
					case 351608024: 
						if (string2.equals("version")) {
							byte1 = 0;
						}

						break;
					
					case 1702284452: 
						if (string2.equals("bodyHair")) {
							byte1 = 3;
						}

						break;
					
					case 2011381734: 
						if (string2.equals("skinColor")) {
							byte1 = 6;
						}

						break;
					
					case 2130170078: 
						if (string2.equals("skinTexture")) {
							byte1 = 7;
						}

					
					}

					ImmutableColor immutableColor;
					switch (byte1) {
					case 0: 
						int int3 = Integer.parseInt(string3);
						if (int3 < 1 || int3 > 1) {
							return false;
						}

						break;
					
					case 1: 
						immutableColor = colorFromString(string3);
						if (immutableColor != null) {
							this.setBeardColor(immutableColor);
						}

						break;
					
					case 2: 
						this.setBeardModel(string3);
						break;
					
					case 3: 
						try {
							this.setBodyHairIndex(Integer.parseInt(string3));
						} catch (NumberFormatException numberFormatException) {
						}

						break;
					
					case 4: 
						immutableColor = colorFromString(string3);
						if (immutableColor != null) {
							this.setHairColor(immutableColor);
						}

						break;
					
					case 5: 
						this.setHairModel(string3);
						break;
					
					case 6: 
						immutableColor = colorFromString(string3);
						if (immutableColor != null) {
							this.setSkinColor(immutableColor);
						}

						break;
					
					case 7: 
						try {
							this.setSkinTextureIndex(Integer.parseInt(string3));
						} catch (NumberFormatException numberFormatException2) {
						}

						break;
					
					case 8: 
						this.setSkinTextureName(string3);
					
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
