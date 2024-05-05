package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlTransient;
import zombie.core.ImmutableColor;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public class ClothingItemReference implements Cloneable {
	public float probability = 1.0F;
	public String itemGUID;
	public ArrayList subItems = new ArrayList();
	public boolean bRandomized = false;
	@XmlTransient
	public boolean m_Immutable = false;
	@XmlTransient
	public final ClothingItemReference.RandomData RandomData = new ClothingItemReference.RandomData();

	public void setModID(String string) {
		this.itemGUID = string + "-" + this.itemGUID;
		Iterator iterator = this.subItems.iterator();
		while (iterator.hasNext()) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)iterator.next();
			clothingItemReference.setModID(string);
		}
	}

	public ClothingItem getClothingItem() {
		String string = this.itemGUID;
		if (!this.bRandomized) {
			throw new RuntimeException("not randomized yet");
		} else {
			if (this.RandomData.m_PickedItemRef != null) {
				string = this.RandomData.m_PickedItemRef.itemGUID;
			}

			return OutfitManager.instance.getClothingItem(string);
		}
	}

	public void randomize() {
		if (this.m_Immutable) {
			throw new RuntimeException("trying to randomize an immutable ClothingItemReference");
		} else {
			this.RandomData.reset();
			for (int int1 = 0; int1 < this.subItems.size(); ++int1) {
				ClothingItemReference clothingItemReference = (ClothingItemReference)this.subItems.get(int1);
				clothingItemReference.randomize();
			}

			this.RandomData.m_PickedItemRef = this.pickRandomItemInternal();
			this.bRandomized = true;
			ClothingItem clothingItem = this.getClothingItem();
			if (clothingItem == null) {
				this.RandomData.m_Active = false;
			} else {
				this.RandomData.m_Active = OutfitRNG.Next(0.0F, 1.0F) <= this.probability;
				if (clothingItem.m_AllowRandomHue) {
					this.RandomData.m_Hue = (float)OutfitRNG.Next(200) / 100.0F - 1.0F;
				}

				if (clothingItem.m_AllowRandomTint) {
					this.RandomData.m_Tint = OutfitRNG.randomImmutableColor();
				} else {
					this.RandomData.m_Tint = ImmutableColor.white;
				}

				this.RandomData.m_BaseTexture = (String)OutfitRNG.pickRandom(clothingItem.m_BaseTextures);
				this.RandomData.m_TextureChoice = (String)OutfitRNG.pickRandom(clothingItem.textureChoices);
				if (!StringUtils.isNullOrWhitespace(clothingItem.m_DecalGroup)) {
					this.RandomData.m_Decal = ClothingDecals.instance.getRandomDecal(clothingItem.m_DecalGroup);
				}
			}
		}
	}

	private ClothingItemReference pickRandomItemInternal() {
		if (this.subItems.isEmpty()) {
			return this;
		} else {
			int int1 = OutfitRNG.Next(this.subItems.size() + 1);
			if (int1 == 0) {
				return this;
			} else {
				ClothingItemReference clothingItemReference = (ClothingItemReference)this.subItems.get(int1 - 1);
				return clothingItemReference.RandomData.m_PickedItemRef;
			}
		}
	}

	public ClothingItemReference clone() {
		try {
			ClothingItemReference clothingItemReference = new ClothingItemReference();
			clothingItemReference.probability = this.probability;
			clothingItemReference.itemGUID = this.itemGUID;
			PZArrayUtil.copy(clothingItemReference.subItems, this.subItems, ClothingItemReference::clone);
			return clothingItemReference;
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new RuntimeException("ClothingItemReference clone failed.", cloneNotSupportedException);
		}
	}

	public static class RandomData {
		public boolean m_Active = true;
		public float m_Hue = 0.0F;
		public ImmutableColor m_Tint;
		public String m_BaseTexture;
		public ClothingItemReference m_PickedItemRef;
		public String m_TextureChoice;
		public String m_Decal;

		public RandomData() {
			this.m_Tint = ImmutableColor.white;
		}

		public void reset() {
			this.m_Active = true;
			this.m_Hue = 0.0F;
			this.m_Tint = ImmutableColor.white;
			this.m_BaseTexture = null;
			this.m_PickedItemRef = null;
			this.m_TextureChoice = null;
			this.m_Decal = null;
		}
	}
}
