package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.CharacterSmartTexture;
import zombie.characterTextures.ItemSmartTexture;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.ClothingDecal;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureCombiner;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.popman.ObjectPool;
import zombie.util.Lambda;
import zombie.util.StringUtils;


public final class ModelInstanceTextureCreator extends TextureDraw.GenericDrawer {
	private boolean bZombie;
	public int renderRefCount;
	private final CharacterMask mask = new CharacterMask();
	private final boolean[] holeMask;
	private final ItemVisuals itemVisuals;
	private final ModelInstanceTextureCreator.CharacterData chrData;
	private final ArrayList itemData;
	private final CharacterSmartTexture characterSmartTexture;
	private final ItemSmartTexture itemSmartTexture;
	private final ArrayList tempTextures;
	private boolean bRendered;
	private final ArrayList texturesNotReady;
	public int testNotReady;
	private static final ObjectPool pool = new ObjectPool(ModelInstanceTextureCreator::new);

	public ModelInstanceTextureCreator() {
		this.holeMask = new boolean[BloodBodyPartType.MAX.index()];
		this.itemVisuals = new ItemVisuals();
		this.chrData = new ModelInstanceTextureCreator.CharacterData();
		this.itemData = new ArrayList();
		this.characterSmartTexture = new CharacterSmartTexture();
		this.itemSmartTexture = new ItemSmartTexture((String)null);
		this.tempTextures = new ArrayList();
		this.bRendered = false;
		this.texturesNotReady = new ArrayList();
		this.testNotReady = -1;
	}

	public void init(IsoGameCharacter gameCharacter) {
		ModelManager.ModelSlot modelSlot = gameCharacter.legsSprite.modelSlot;
		HumanVisual humanVisual = ((IHumanVisual)gameCharacter).getHumanVisual();
		gameCharacter.getItemVisuals(this.itemVisuals);
		this.init(humanVisual, this.itemVisuals, modelSlot.model);
		this.itemVisuals.clear();
	}

	public void init(BaseVisual baseVisual, ItemVisuals itemVisuals, ModelInstance modelInstance) {
		if (baseVisual instanceof HumanVisual) {
			this.init((HumanVisual)baseVisual, itemVisuals, modelInstance);
		} else {
			throw new IllegalArgumentException("unhandled BaseVisual " + baseVisual);
		}
	}

	public void init(HumanVisual humanVisual, ItemVisuals itemVisuals, ModelInstance modelInstance) {
		boolean boolean1 = DebugLog.isEnabled(DebugType.Clothing);
		this.bRendered = false;
		this.bZombie = humanVisual.isZombie();
		CharacterMask characterMask = this.mask;
		characterMask.setAllVisible(true);
		String string = "media/textures/Body/Masks";
		Arrays.fill(this.holeMask, false);
		ModelInstanceTextureCreator.ItemData.pool.release((List)this.itemData);
		this.itemData.clear();
		this.texturesNotReady.clear();
		BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
		int int1;
		String string2;
		for (int1 = itemVisuals.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
			ClothingItem clothingItem = itemVisual.getClothingItem();
			if (clothingItem == null) {
				if (boolean1) {
					DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual);
				}
			} else if (!clothingItem.isReady()) {
				if (boolean1) {
					DebugLog.Clothing.warn("ClothingItem not ready for ItemVisual:" + itemVisual);
				}
			} else if (!PopTemplateManager.instance.isItemModelHidden(bodyLocationGroup, itemVisuals, itemVisual)) {
				ModelInstance modelInstance2 = this.findModelInstance(modelInstance.sub, itemVisual);
				if (modelInstance2 == null) {
					string2 = clothingItem.getModel(humanVisual.isFemale());
					if (!StringUtils.isNullOrWhitespace(string2)) {
						if (boolean1) {
							DebugLog.Clothing.warn("ModelInstance not found for ItemVisual:" + itemVisual);
						}

						continue;
					}
				}

				this.addClothingItem(modelInstance2, itemVisual, clothingItem, characterMask, string);
				int int2;
				for (int2 = 0; int2 < BloodBodyPartType.MAX.index(); ++int2) {
					BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
					if (itemVisual.getHole(bloodBodyPartType) > 0.0F && characterMask.isBloodBodyPartVisible(bloodBodyPartType)) {
						this.holeMask[int2] = true;
					}
				}

				for (int2 = 0; int2 < clothingItem.m_Masks.size(); ++int2) {
					CharacterMask.Part part = CharacterMask.Part.fromInt((Integer)clothingItem.m_Masks.get(int2));
					BloodBodyPartType[] bloodBodyPartTypeArray = part.getBloodBodyPartTypes();
					int int3 = bloodBodyPartTypeArray.length;
					for (int int4 = 0; int4 < int3; ++int4) {
						BloodBodyPartType bloodBodyPartType2 = bloodBodyPartTypeArray[int4];
						if (itemVisual.getHole(bloodBodyPartType2) <= 0.0F) {
							this.holeMask[bloodBodyPartType2.index()] = false;
						}
					}
				}

				itemVisual.getClothingItemCombinedMask(characterMask);
				if (!StringUtils.equalsIgnoreCase(clothingItem.m_UnderlayMasksFolder, "media/textures/Body/Masks")) {
					string = clothingItem.m_UnderlayMasksFolder;
				}
			}
		}

		this.chrData.modelInstance = modelInstance;
		this.chrData.mask.copyFrom(characterMask);
		this.chrData.maskFolder = "media/textures/Body/Masks";
		this.chrData.baseTexture = "media/textures/Body/" + humanVisual.getSkinTexture() + ".png";
		Arrays.fill(this.chrData.blood, 0.0F);
		for (int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			BloodBodyPartType bloodBodyPartType3 = BloodBodyPartType.FromIndex(int1);
			this.chrData.blood[int1] = humanVisual.getBlood(bloodBodyPartType3);
			this.chrData.dirt[int1] = humanVisual.getDirt(bloodBodyPartType3);
		}

		Texture texture = Texture.getSharedTexture(this.chrData.baseTexture);
		if (texture != null && !texture.isReady()) {
			this.texturesNotReady.add(texture);
		}

		if (!this.chrData.mask.isAllVisible() && !this.chrData.mask.isNothingVisible()) {
			String string3 = this.chrData.maskFolder;
			Consumer consumer = Lambda.consumer(string3, this.texturesNotReady, (var0,humanVisualx,itemVisualsx)->{
				Texture modelInstance = Texture.getSharedTexture(humanVisualx + "/" + var0 + ".png");
				if (modelInstance != null && !modelInstance.isReady()) {
					itemVisualsx.add(modelInstance);
				}
			});

			this.chrData.mask.forEachVisible(consumer);
		}

		texture = Texture.getSharedTexture("media/textures/BloodTextures/BloodOverlay.png");
		if (texture != null && !texture.isReady()) {
			this.texturesNotReady.add(texture);
		}

		texture = Texture.getSharedTexture("media/textures/BloodTextures/GrimeOverlay.png");
		if (texture != null && !texture.isReady()) {
			this.texturesNotReady.add(texture);
		}

		texture = Texture.getSharedTexture("media/textures/patches/patchesmask.png");
		if (texture != null && !texture.isReady()) {
			this.texturesNotReady.add(texture);
		}

		int int5;
		String string4;
		for (int5 = 0; int5 < BloodBodyPartType.MAX.index(); ++int5) {
			BloodBodyPartType bloodBodyPartType4 = BloodBodyPartType.FromIndex(int5);
			String[] stringArray = CharacterSmartTexture.MaskFiles;
			String string5 = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType4.index()] + ".png";
			texture = Texture.getSharedTexture(string5);
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			stringArray = CharacterSmartTexture.MaskFiles;
			string2 = "media/textures/HoleTextures/" + stringArray[bloodBodyPartType4.index()] + ".png";
			texture = Texture.getSharedTexture(string2);
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			stringArray = CharacterSmartTexture.BasicPatchesMaskFiles;
			string4 = "media/textures/patches/" + stringArray[bloodBodyPartType4.index()] + ".png";
			texture = Texture.getSharedTexture(string4);
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			stringArray = CharacterSmartTexture.DenimPatchesMaskFiles;
			String string6 = "media/textures/patches/" + stringArray[bloodBodyPartType4.index()] + ".png";
			texture = Texture.getSharedTexture(string6);
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}

			stringArray = CharacterSmartTexture.LeatherPatchesMaskFiles;
			String string7 = "media/textures/patches/" + stringArray[bloodBodyPartType4.index()] + ".png";
			texture = Texture.getSharedTexture(string7);
			if (texture != null && !texture.isReady()) {
				this.texturesNotReady.add(texture);
			}
		}

		characterMask.setAllVisible(true);
		string = "media/textures/Body/Masks";
		for (int5 = humanVisual.getBodyVisuals().size() - 1; int5 >= 0; --int5) {
			ItemVisual itemVisual2 = (ItemVisual)humanVisual.getBodyVisuals().get(int5);
			ClothingItem clothingItem2 = itemVisual2.getClothingItem();
			if (clothingItem2 == null) {
				if (boolean1) {
					DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual2);
				}
			} else if (!clothingItem2.isReady()) {
				if (boolean1) {
					DebugLog.Clothing.warn("ClothingItem not ready for ItemVisual:" + itemVisual2);
				}
			} else {
				ModelInstance modelInstance3 = this.findModelInstance(modelInstance.sub, itemVisual2);
				if (modelInstance3 == null) {
					string4 = clothingItem2.getModel(humanVisual.isFemale());
					if (!StringUtils.isNullOrWhitespace(string4)) {
						if (boolean1) {
							DebugLog.Clothing.warn("ModelInstance not found for ItemVisual:" + itemVisual2);
						}

						continue;
					}
				}

				this.addClothingItem(modelInstance3, itemVisual2, clothingItem2, characterMask, string);
			}
		}
	}

	private ModelInstance findModelInstance(ArrayList arrayList, ItemVisual itemVisual) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)arrayList.get(int1);
			ItemVisual itemVisual2 = modelInstance.getItemVisual();
			if (itemVisual2 != null && itemVisual2.getClothingItem() == itemVisual.getClothingItem()) {
				return modelInstance;
			}
		}

		return null;
	}

	private void addClothingItem(ModelInstance modelInstance, ItemVisual itemVisual, ClothingItem clothingItem, CharacterMask characterMask, String string) {
		String string2 = modelInstance == null ? itemVisual.getBaseTexture(clothingItem) : itemVisual.getTextureChoice(clothingItem);
		ImmutableColor immutableColor = itemVisual.getTint(clothingItem);
		float float1 = itemVisual.getHue(clothingItem);
		ModelInstanceTextureCreator.ItemData itemData = (ModelInstanceTextureCreator.ItemData)ModelInstanceTextureCreator.ItemData.pool.alloc();
		itemData.modelInstance = modelInstance;
		itemData.category = CharacterSmartTexture.ClothingItemCategory;
		itemData.mask.copyFrom(characterMask);
		itemData.maskFolder = clothingItem.m_MasksFolder;
		if (StringUtils.equalsIgnoreCase(itemData.maskFolder, "media/textures/Body/Masks")) {
			itemData.maskFolder = string;
		}

		if (StringUtils.equalsIgnoreCase(itemData.maskFolder, "none")) {
			itemData.mask.setAllVisible(true);
		}

		if (itemData.maskFolder.contains("Clothes/Hat/Masks")) {
			itemData.mask.setAllVisible(true);
		}

		itemData.baseTexture = "media/textures/" + string2 + ".png";
		itemData.tint = immutableColor;
		itemData.hue = float1;
		itemData.decalTexture = null;
		Arrays.fill(itemData.basicPatches, 0.0F);
		Arrays.fill(itemData.denimPatches, 0.0F);
		Arrays.fill(itemData.leatherPatches, 0.0F);
		Arrays.fill(itemData.blood, 0.0F);
		Arrays.fill(itemData.dirt, 0.0F);
		Arrays.fill(itemData.hole, 0.0F);
		int int1 = ModelManager.instance.getTextureFlags();
		Texture texture = Texture.getSharedTexture(itemData.baseTexture, int1);
		if (texture != null && !texture.isReady()) {
			this.texturesNotReady.add(texture);
		}

		String string3;
		if (!itemData.mask.isAllVisible() && !itemData.mask.isNothingVisible()) {
			string3 = itemData.maskFolder;
			Consumer consumer = Lambda.consumer(string3, this.texturesNotReady, (var0,modelInstancex,itemVisualx)->{
				Texture clothingItem = Texture.getSharedTexture(modelInstancex + "/" + var0 + ".png");
				if (clothingItem != null && !clothingItem.isReady()) {
					itemVisualx.add(clothingItem);
				}
			});

			itemData.mask.forEachVisible(consumer);
		}

		if (Core.getInstance().isOptionSimpleClothingTextures(this.bZombie)) {
			this.itemData.add(itemData);
		} else {
			string3 = itemVisual.getDecal(clothingItem);
			if (!StringUtils.isNullOrWhitespace(string3)) {
				ClothingDecal clothingDecal = ClothingDecals.instance.getDecal(string3);
				if (clothingDecal != null && clothingDecal.isValid()) {
					itemData.decalTexture = clothingDecal.texture;
					itemData.decalX = clothingDecal.x;
					itemData.decalY = clothingDecal.y;
					itemData.decalWidth = clothingDecal.width;
					itemData.decalHeight = clothingDecal.height;
					texture = Texture.getSharedTexture("media/textures/" + itemData.decalTexture + ".png");
					if (texture != null && !texture.isReady()) {
						this.texturesNotReady.add(texture);
					}
				}
			}

			for (int int2 = 0; int2 < BloodBodyPartType.MAX.index(); ++int2) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
				itemData.blood[int2] = itemVisual.getBlood(bloodBodyPartType);
				itemData.dirt[int2] = itemVisual.getDirt(bloodBodyPartType);
				itemData.basicPatches[int2] = itemVisual.getBasicPatch(bloodBodyPartType);
				itemData.denimPatches[int2] = itemVisual.getDenimPatch(bloodBodyPartType);
				itemData.leatherPatches[int2] = itemVisual.getLeatherPatch(bloodBodyPartType);
				itemData.hole[int2] = itemVisual.getHole(bloodBodyPartType);
				if (itemData.hole[int2] > 0.0F) {
					String[] stringArray = CharacterSmartTexture.MaskFiles;
					String string4 = "media/textures/HoleTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
					texture = Texture.getSharedTexture(string4);
					if (texture != null && !texture.isReady()) {
						this.texturesNotReady.add(texture);
					}
				}

				if (itemData.hole[int2] == 0.0F && this.holeMask[int2]) {
					itemData.hole[int2] = -1.0F;
					if (itemData.mask.isBloodBodyPartVisible(bloodBodyPartType)) {
					}
				}
			}

			this.itemData.add(itemData);
		}
	}

	public void render() {
		if (!this.bRendered) {
			for (int int1 = 0; int1 < this.texturesNotReady.size(); ++int1) {
				Texture texture = (Texture)this.texturesNotReady.get(int1);
				if (!texture.isReady()) {
					return;
				}
			}

			GL11.glPushAttrib(2048);
			try {
				this.tempTextures.clear();
				CharacterSmartTexture characterSmartTexture = this.createFullCharacterTexture();
				assert characterSmartTexture == this.characterSmartTexture;
				if (!(this.chrData.modelInstance.tex instanceof CharacterSmartTexture)) {
					this.chrData.modelInstance.tex = new CharacterSmartTexture();
				}

				((CharacterSmartTexture)this.chrData.modelInstance.tex).clear();
				this.applyCharacterTexture(characterSmartTexture.result, (CharacterSmartTexture)this.chrData.modelInstance.tex);
				characterSmartTexture.clear();
				this.tempTextures.add(characterSmartTexture.result);
				characterSmartTexture.result = null;
				characterSmartTexture = (CharacterSmartTexture)this.chrData.modelInstance.tex;
				int int2 = this.itemData.size() - 1;
				while (true) {
					if (int2 < 0) {
						characterSmartTexture.calculate();
						characterSmartTexture.clear();
						this.itemSmartTexture.clear();
						for (int2 = 0; int2 < this.tempTextures.size(); ++int2) {
							for (int int3 = 0; int3 < this.itemData.size(); ++int3) {
								ModelInstance modelInstance = ((ModelInstanceTextureCreator.ItemData)this.itemData.get(int3)).modelInstance;
								assert modelInstance == null || this.tempTextures.get(int2) != modelInstance.tex;
							}

							TextureCombiner.instance.releaseTexture((Texture)this.tempTextures.get(int2));
						}

						this.tempTextures.clear();
						break;
					}

					label184: {
						ModelInstanceTextureCreator.ItemData itemData = (ModelInstanceTextureCreator.ItemData)this.itemData.get(int2);
						Texture texture2;
						if (this.isSimpleTexture(itemData)) {
							int int4 = ModelManager.instance.getTextureFlags();
							texture2 = Texture.getSharedTexture(itemData.baseTexture, int4);
							if (!this.isItemSmartTextureRequired(itemData)) {
								itemData.modelInstance.tex = texture2;
								break label184;
							}
						} else {
							ItemSmartTexture itemSmartTexture = this.createFullItemTexture(itemData);
							assert itemSmartTexture == this.itemSmartTexture;
							texture2 = itemSmartTexture.result;
							this.tempTextures.add(itemSmartTexture.result);
							itemSmartTexture.result = null;
						}

						if (itemData.modelInstance == null) {
							this.applyItemTexture(itemData, texture2, characterSmartTexture);
						} else {
							if (!(itemData.modelInstance.tex instanceof ItemSmartTexture)) {
								itemData.modelInstance.tex = new ItemSmartTexture((String)null);
							}

							((ItemSmartTexture)itemData.modelInstance.tex).clear();
							this.applyItemTexture(itemData, texture2, (ItemSmartTexture)itemData.modelInstance.tex);
							((ItemSmartTexture)itemData.modelInstance.tex).calculate();
							((ItemSmartTexture)itemData.modelInstance.tex).clear();
						}
					}

					--int2;
				}
			} finally {
				GL11.glPopAttrib();
			}

			this.bRendered = true;
		}
	}

	private CharacterSmartTexture createFullCharacterTexture() {
		CharacterSmartTexture characterSmartTexture = this.characterSmartTexture;
		characterSmartTexture.clear();
		characterSmartTexture.addTexture(this.chrData.baseTexture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0F);
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
			if (this.chrData.dirt[int1] > 0.0F) {
				characterSmartTexture.addDirt(bloodBodyPartType, this.chrData.dirt[int1], (IsoGameCharacter)null);
			}

			if (this.chrData.blood[int1] > 0.0F) {
				characterSmartTexture.addBlood(bloodBodyPartType, this.chrData.blood[int1], (IsoGameCharacter)null);
			}
		}

		characterSmartTexture.calculate();
		return characterSmartTexture;
	}

	private void applyCharacterTexture(Texture texture, CharacterSmartTexture characterSmartTexture) {
		characterSmartTexture.addMaskedTexture(this.chrData.mask, this.chrData.maskFolder, texture, CharacterSmartTexture.BodyCategory, ImmutableColor.white, 0.0F);
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
			if (this.holeMask[int1]) {
				characterSmartTexture.removeHole(texture, bloodBodyPartType);
			}
		}
	}

	private boolean isSimpleTexture(ModelInstanceTextureCreator.ItemData itemData) {
		if (itemData.hue != 0.0F) {
			return false;
		} else {
			ImmutableColor immutableColor = itemData.tint;
			if (itemData.modelInstance != null) {
				immutableColor = ImmutableColor.white;
			}

			if (!immutableColor.equals(ImmutableColor.white)) {
				return false;
			} else if (itemData.decalTexture != null) {
				return false;
			} else {
				for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
					if (itemData.blood[int1] > 0.0F) {
						return false;
					}

					if (itemData.dirt[int1] > 0.0F) {
						return false;
					}

					if (itemData.hole[int1] > 0.0F) {
						return false;
					}

					if (itemData.basicPatches[int1] > 0.0F) {
						return false;
					}

					if (itemData.denimPatches[int1] > 0.0F) {
						return false;
					}

					if (itemData.leatherPatches[int1] > 0.0F) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private ItemSmartTexture createFullItemTexture(ModelInstanceTextureCreator.ItemData itemData) {
		ItemSmartTexture itemSmartTexture = this.itemSmartTexture;
		itemSmartTexture.clear();
		ImmutableColor immutableColor = itemData.tint;
		if (itemData.modelInstance != null) {
			itemData.modelInstance.tintR = itemData.modelInstance.tintG = itemData.modelInstance.tintB = 1.0F;
		}

		itemSmartTexture.addTexture(itemData.baseTexture, itemData.category, immutableColor, itemData.hue);
		if (itemData.decalTexture != null) {
			itemSmartTexture.addRect("media/textures/" + itemData.decalTexture + ".png", itemData.decalX, itemData.decalY, itemData.decalWidth, itemData.decalHeight);
		}

		int int1;
		BloodBodyPartType bloodBodyPartType;
		for (int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			if (itemData.blood[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				itemSmartTexture.addBlood("media/textures/BloodTextures/BloodOverlay.png", bloodBodyPartType, itemData.blood[int1]);
			}

			if (itemData.dirt[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				itemSmartTexture.addDirt("media/textures/BloodTextures/GrimeOverlay.png", bloodBodyPartType, itemData.dirt[int1]);
			}

			if (itemData.basicPatches[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				itemSmartTexture.setBasicPatches(bloodBodyPartType);
			}

			if (itemData.denimPatches[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				itemSmartTexture.setDenimPatches(bloodBodyPartType);
			}

			if (itemData.leatherPatches[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				itemSmartTexture.setLeatherPatches(bloodBodyPartType);
			}
		}

		for (int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			if (itemData.hole[int1] > 0.0F) {
				bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				Texture texture = itemSmartTexture.addHole(bloodBodyPartType);
				assert texture != itemSmartTexture.result;
				this.tempTextures.add(texture);
			}
		}

		itemSmartTexture.calculate();
		return itemSmartTexture;
	}

	private boolean isItemSmartTextureRequired(ModelInstanceTextureCreator.ItemData itemData) {
		if (itemData.modelInstance == null) {
			return true;
		} else if (itemData.modelInstance.tex instanceof ItemSmartTexture) {
			return true;
		} else {
			for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
				if (itemData.hole[int1] < 0.0F) {
					return true;
				}
			}

			return !itemData.mask.isAllVisible();
		}
	}

	private void applyItemTexture(ModelInstanceTextureCreator.ItemData itemData, Texture texture, SmartTexture smartTexture) {
		smartTexture.addMaskedTexture(itemData.mask, itemData.maskFolder, texture, itemData.category, ImmutableColor.white, 0.0F);
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			if (itemData.hole[int1] < 0.0F) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int1);
				smartTexture.removeHole(texture, bloodBodyPartType);
			}
		}
	}

	public void postRender() {
		if (!this.bRendered) {
			boolean boolean1;
			if (this.chrData.modelInstance.character == null) {
				boolean1 = true;
			} else {
				boolean1 = true;
			}
		}

		for (int int1 = 0; int1 < this.itemData.size(); ++int1) {
			((ModelInstanceTextureCreator.ItemData)this.itemData.get(int1)).modelInstance = null;
		}

		this.chrData.modelInstance = null;
		this.texturesNotReady.clear();
		ModelInstanceTextureCreator.ItemData.pool.release((List)this.itemData);
		this.itemData.clear();
		pool.release((Object)this);
	}

	public boolean isRendered() {
		return this.testNotReady > 0 ? false : this.bRendered;
	}

	public static ModelInstanceTextureCreator alloc() {
		return (ModelInstanceTextureCreator)pool.alloc();
	}

	private static final class CharacterData {
		ModelInstance modelInstance;
		final CharacterMask mask = new CharacterMask();
		String maskFolder;
		String baseTexture;
		final float[] blood;
		final float[] dirt;

		private CharacterData() {
			this.blood = new float[BloodBodyPartType.MAX.index()];
			this.dirt = new float[BloodBodyPartType.MAX.index()];
		}
	}

	private static final class ItemData {
		ModelInstance modelInstance;
		final CharacterMask mask = new CharacterMask();
		String maskFolder;
		String baseTexture;
		int category;
		ImmutableColor tint;
		float hue;
		String decalTexture;
		int decalX;
		int decalY;
		int decalWidth;
		int decalHeight;
		final float[] blood;
		final float[] dirt;
		final float[] basicPatches;
		final float[] denimPatches;
		final float[] leatherPatches;
		final float[] hole;
		static final ObjectPool pool = new ObjectPool(ModelInstanceTextureCreator.ItemData::new);

		private ItemData() {
			this.blood = new float[BloodBodyPartType.MAX.index()];
			this.dirt = new float[BloodBodyPartType.MAX.index()];
			this.basicPatches = new float[BloodBodyPartType.MAX.index()];
			this.denimPatches = new float[BloodBodyPartType.MAX.index()];
			this.leatherPatches = new float[BloodBodyPartType.MAX.index()];
			this.hole = new float[BloodBodyPartType.MAX.index()];
		}
	}
}
