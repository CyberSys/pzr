package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.debug.DebugLog;
import zombie.util.list.PZArrayUtil;


public class Outfit implements Cloneable {
	public String m_Name = "Outfit";
	public boolean m_Top = true;
	public boolean m_Pants = true;
	public final ArrayList m_TopTextures = new ArrayList();
	public final ArrayList m_PantsTextures = new ArrayList();
	public final ArrayList m_items = new ArrayList();
	public boolean m_AllowPantsHue = true;
	public boolean m_AllowPantsTint = false;
	public boolean m_AllowTopTint = true;
	public boolean m_AllowTShirtDecal = true;
	@XmlTransient
	public String m_modID;
	@XmlTransient
	public boolean m_Immutable = false;
	@XmlTransient
	public final Outfit.RandomData RandomData = new Outfit.RandomData();

	public void setModID(String string) {
		this.m_modID = string;
		Iterator iterator = this.m_items.iterator();
		while (iterator.hasNext()) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)iterator.next();
			clothingItemReference.setModID(string);
		}
	}

	public void AddItem(ClothingItemReference clothingItemReference) {
		this.m_items.add(clothingItemReference);
	}

	public void Randomize() {
		if (this.m_Immutable) {
			throw new RuntimeException("trying to randomize an immutable Outfit");
		} else {
			for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
				ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
				clothingItemReference.randomize();
			}

			this.RandomData.m_hairColor = HairOutfitDefinitions.instance.getRandomHaircutColor(this.m_Name);
			this.RandomData.m_femaleHairName = HairStyles.instance.getRandomFemaleStyle(this.m_Name);
			this.RandomData.m_maleHairName = HairStyles.instance.getRandomMaleStyle(this.m_Name);
			this.RandomData.m_beardName = BeardStyles.instance.getRandomStyle(this.m_Name);
			this.RandomData.m_topTint = OutfitRNG.randomImmutableColor();
			this.RandomData.m_pantsTint = OutfitRNG.randomImmutableColor();
			if (OutfitRNG.Next(4) == 0) {
				this.RandomData.m_pantsHue = (float)OutfitRNG.Next(200) / 100.0F - 1.0F;
			} else {
				this.RandomData.m_pantsHue = 0.0F;
			}

			this.RandomData.m_hasTop = OutfitRNG.Next(16) != 0;
			this.RandomData.m_hasTShirt = OutfitRNG.Next(2) == 0;
			this.RandomData.m_hasTShirtDecal = OutfitRNG.Next(4) == 0;
			if (this.m_Top) {
				this.RandomData.m_hasTop = true;
			}

			this.RandomData.m_topTexture = (String)OutfitRNG.pickRandom(this.m_TopTextures);
			this.RandomData.m_pantsTexture = (String)OutfitRNG.pickRandom(this.m_PantsTextures);
		}
	}

	public void randomizeItem(String string) {
		ClothingItemReference clothingItemReference = (ClothingItemReference)PZArrayUtil.find((List)this.m_items, (stringx)->{
    return stringx.itemGUID.equals(string);
});
		if (clothingItemReference != null) {
			clothingItemReference.randomize();
		} else {
			DebugLog.Clothing.println("Outfit.randomizeItem> Could not find itemGuid: " + string);
		}
	}

	public CharacterMask GetMask() {
		CharacterMask characterMask = new CharacterMask();
		for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
			if (clothingItemReference.RandomData.m_Active) {
				ClothingItem.tryGetCombinedMask(clothingItemReference, characterMask);
			}
		}

		return characterMask;
	}

	public boolean containsItemGuid(String string) {
		boolean boolean1 = false;
		for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
			if (clothingItemReference.itemGUID.equals(string)) {
				boolean1 = true;
				break;
			}
		}

		return boolean1;
	}

	public ClothingItemReference findItemByGUID(String string) {
		for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
			if (clothingItemReference.itemGUID.equals(string)) {
				return clothingItemReference;
			}
		}

		return null;
	}

	public Outfit clone() {
		try {
			Outfit outfit = new Outfit();
			outfit.m_Name = this.m_Name;
			outfit.m_Top = this.m_Top;
			outfit.m_Pants = this.m_Pants;
			outfit.m_PantsTextures.addAll(this.m_PantsTextures);
			outfit.m_TopTextures.addAll(this.m_TopTextures);
			PZArrayUtil.copy(outfit.m_items, this.m_items, ClothingItemReference::clone);
			outfit.m_AllowPantsHue = this.m_AllowPantsHue;
			outfit.m_AllowPantsTint = this.m_AllowPantsTint;
			outfit.m_AllowTopTint = this.m_AllowTopTint;
			outfit.m_AllowTShirtDecal = this.m_AllowTShirtDecal;
			return outfit;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new RuntimeException("Outfit clone failed.", cloneNotSupportedException);
		}
	}

	public ClothingItemReference findHat() {
		Iterator iterator = this.m_items.iterator();
		while (iterator.hasNext()) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)iterator.next();
			if (clothingItemReference.RandomData.m_Active) {
				ClothingItem clothingItem = clothingItemReference.getClothingItem();
				if (clothingItem != null && clothingItem.isHat()) {
					return clothingItemReference;
				}
			}
		}

		return null;
	}

	public boolean isEmpty() {
		for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
			ClothingItem clothingItem = OutfitManager.instance.getClothingItem(clothingItemReference.itemGUID);
			if (clothingItem != null && clothingItem.isEmpty()) {
				return true;
			}

			for (int int2 = 0; int2 < clothingItemReference.subItems.size(); ++int2) {
				ClothingItemReference clothingItemReference2 = (ClothingItemReference)clothingItemReference.subItems.get(int2);
				clothingItem = OutfitManager.instance.getClothingItem(clothingItemReference2.itemGUID);
				if (clothingItem != null && clothingItem.isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}

	public void loadItems() {
		for (int int1 = 0; int1 < this.m_items.size(); ++int1) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)this.m_items.get(int1);
			OutfitManager.instance.getClothingItem(clothingItemReference.itemGUID);
			for (int int2 = 0; int2 < clothingItemReference.subItems.size(); ++int2) {
				ClothingItemReference clothingItemReference2 = (ClothingItemReference)clothingItemReference.subItems.get(int2);
				OutfitManager.instance.getClothingItem(clothingItemReference2.itemGUID);
			}
		}
	}

	public static class RandomData {
		public ImmutableColor m_hairColor;
		public String m_maleHairName;
		public String m_femaleHairName;
		public String m_beardName;
		public ImmutableColor m_topTint;
		public ImmutableColor m_pantsTint;
		public float m_pantsHue;
		public boolean m_hasTop;
		public boolean m_hasTShirt;
		public boolean m_hasTShirtDecal;
		public String m_topTexture;
		public String m_pantsTexture;
	}
}
