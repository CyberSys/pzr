package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import java.util.Locale;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.core.ImmutableColor;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.DebugType;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public class PopTemplateManager {
	public static final PopTemplateManager instance = new PopTemplateManager();
	public final ArrayList m_MaleSkins = new ArrayList();
	public final ArrayList m_FemaleSkins = new ArrayList();
	public final ArrayList m_MaleSkins_Zombie1 = new ArrayList();
	public final ArrayList m_FemaleSkins_Zombie1 = new ArrayList();
	public final ArrayList m_MaleSkins_Zombie2 = new ArrayList();
	public final ArrayList m_FemaleSkins_Zombie2 = new ArrayList();
	public final ArrayList m_MaleSkins_Zombie3 = new ArrayList();
	public final ArrayList m_FemaleSkins_Zombie3 = new ArrayList();
	public final ArrayList m_SkeletonMaleSkins_Zombie = new ArrayList();
	public final ArrayList m_SkeletonFemaleSkins_Zombie = new ArrayList();
	public static final int SKELETON_BURNED_SKIN_INDEX = 0;
	public static final int SKELETON_NORMAL_SKIN_INDEX = 1;
	public static final int SKELETON_MUSCLE_SKIN_INDEX = 2;

	public void init() {
		ItemManager.init();
		int int1;
		for (int1 = 1; int1 <= 5; ++int1) {
			this.m_MaleSkins.add("MaleBody0" + int1);
		}

		for (int1 = 1; int1 <= 5; ++int1) {
			this.m_FemaleSkins.add("FemaleBody0" + int1);
		}

		for (int1 = 1; int1 <= 4; ++int1) {
			this.m_MaleSkins_Zombie1.add("M_ZedBody0" + int1 + "_level1");
			this.m_FemaleSkins_Zombie1.add("F_ZedBody0" + int1 + "_level1");
			this.m_MaleSkins_Zombie2.add("M_ZedBody0" + int1 + "_level2");
			this.m_FemaleSkins_Zombie2.add("F_ZedBody0" + int1 + "_level2");
			this.m_MaleSkins_Zombie3.add("M_ZedBody0" + int1 + "_level3");
			this.m_FemaleSkins_Zombie3.add("F_ZedBody0" + int1 + "_level3");
		}

		this.m_SkeletonMaleSkins_Zombie.add("SkeletonBurned");
		this.m_SkeletonMaleSkins_Zombie.add("Skeleton");
		this.m_SkeletonMaleSkins_Zombie.add("SkeletonMuscle");
		this.m_SkeletonFemaleSkins_Zombie.add("SkeletonBurned");
		this.m_SkeletonFemaleSkins_Zombie.add("Skeleton");
		this.m_SkeletonFemaleSkins_Zombie.add("SkeletonMuscle");
	}

	public ModelInstance addClothingItem(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot, ItemVisual itemVisual, ClothingItem clothingItem) {
		String string = clothingItem.getModel(gameCharacter.isFemale());
		if (StringUtils.isNullOrWhitespace(string)) {
			if (DebugLog.isEnabled(DebugType.Clothing)) {
				DebugLog.Clothing.debugln("No model specified by item: " + clothingItem.m_Name);
			}

			return null;
		} else {
			string = this.processModelFileName(string);
			String string2 = itemVisual.getTextureChoice(clothingItem);
			ImmutableColor immutableColor = itemVisual.getTint(clothingItem);
			itemVisual.getHue(clothingItem);
			String string3 = clothingItem.m_AttachBone;
			String string4 = clothingItem.m_Shader;
			ModelInstance modelInstance;
			if (string3 != null && string3.length() > 0) {
				modelInstance = ModelManager.instance.newStaticInstance(modelSlot, string, string2, string3, string4);
			} else {
				modelInstance = ModelManager.instance.newAdditionalModelInstance(string, string2, gameCharacter, modelSlot.model.AnimPlayer, string4);
			}

			if (modelInstance == null) {
				return null;
			} else {
				this.postProcessNewItemInstance(modelInstance, modelSlot, immutableColor);
				modelInstance.setItemVisual(itemVisual);
				return modelInstance;
			}
		}
	}

	private void addHeadHairItem(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot, String string, String string2, ImmutableColor immutableColor) {
		if (StringUtils.isNullOrWhitespace(string)) {
			if (DebugLog.isEnabled(DebugType.Clothing)) {
				DebugLog.Clothing.warn("No model specified.");
			}
		} else {
			string = this.processModelFileName(string);
			ModelInstance modelInstance = ModelManager.instance.newAdditionalModelInstance(string, string2, gameCharacter, modelSlot.model.AnimPlayer, (String)null);
			if (modelInstance != null) {
				this.postProcessNewItemInstance(modelInstance, modelSlot, immutableColor);
			}
		}
	}

	private void addHeadHair(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot, HumanVisual humanVisual, ItemVisual itemVisual, boolean boolean1) {
		ImmutableColor immutableColor = humanVisual.getHairColor();
		if (boolean1) {
			immutableColor = humanVisual.getBeardColor();
		}

		HairStyle hairStyle;
		if (gameCharacter.isFemale()) {
			if (!boolean1) {
				hairStyle = HairStyles.instance.FindFemaleStyle(humanVisual.getHairModel());
				if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
					hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
				}

				if (hairStyle != null && hairStyle.isValid()) {
					if (DebugLog.isEnabled(DebugType.Clothing)) {
						DebugLog.Clothing.debugln("  Adding female hair: " + hairStyle.name);
					}

					this.addHeadHairItem(gameCharacter, modelSlot, hairStyle.model, hairStyle.texture, immutableColor);
				}
			}
		} else if (!boolean1) {
			hairStyle = HairStyles.instance.FindMaleStyle(humanVisual.getHairModel());
			if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
				hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
			}

			if (hairStyle != null && hairStyle.isValid()) {
				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.debugln("  Adding male hair: " + hairStyle.name);
				}

				this.addHeadHairItem(gameCharacter, modelSlot, hairStyle.model, hairStyle.texture, immutableColor);
			}
		} else {
			BeardStyle beardStyle = BeardStyles.instance.FindStyle(humanVisual.getBeardModel());
			if (beardStyle != null && beardStyle.isValid()) {
				if (itemVisual != null && itemVisual.getClothingItem() != null && !StringUtils.isNullOrEmpty(itemVisual.getClothingItem().m_HatCategory) && itemVisual.getClothingItem().m_HatCategory.contains("nobeard")) {
					return;
				}

				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.debugln("  Adding beard: " + beardStyle.name);
				}

				this.addHeadHairItem(gameCharacter, modelSlot, beardStyle.model, beardStyle.texture, immutableColor);
			}
		}
	}

	public void populateCharacterModelSlot(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot) {
		if (!(gameCharacter instanceof IHumanVisual)) {
			DebugLog.Clothing.warn("Supplied character is not an IHumanVisual. Ignored. " + gameCharacter);
		} else {
			HumanVisual humanVisual = ((IHumanVisual)gameCharacter).getHumanVisual();
			ItemVisuals itemVisuals = new ItemVisuals();
			gameCharacter.getItemVisuals(itemVisuals);
			CharacterMask characterMask = HumanVisual.GetMask(itemVisuals);
			if (DebugLog.isEnabled(DebugType.Clothing)) {
				DebugLogStream debugLogStream = DebugLog.Clothing;
				String string = gameCharacter.getClass().getName();
				debugLogStream.debugln("characterType:" + string + ", name:" + gameCharacter.getName());
			}

			if (characterMask.isPartVisible(CharacterMask.Part.Head)) {
				this.addHeadHair(gameCharacter, modelSlot, humanVisual, itemVisuals.findHat(), false);
				this.addHeadHair(gameCharacter, modelSlot, humanVisual, itemVisuals.findMask(), true);
			}

			int int1;
			ItemVisual itemVisual;
			ClothingItem clothingItem;
			for (int1 = itemVisuals.size() - 1; int1 >= 0; --int1) {
				itemVisual = (ItemVisual)itemVisuals.get(int1);
				clothingItem = itemVisual.getClothingItem();
				if (clothingItem == null) {
					if (DebugLog.isEnabled(DebugType.Clothing)) {
						DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual);
					}
				} else if (!this.isItemModelHidden(gameCharacter.getBodyLocationGroup(), itemVisuals, itemVisual)) {
					this.addClothingItem(gameCharacter, modelSlot, itemVisual, clothingItem);
				}
			}

			for (int1 = humanVisual.getBodyVisuals().size() - 1; int1 >= 0; --int1) {
				itemVisual = (ItemVisual)humanVisual.getBodyVisuals().get(int1);
				clothingItem = itemVisual.getClothingItem();
				if (clothingItem == null) {
					if (DebugLog.isEnabled(DebugType.Clothing)) {
						DebugLog.Clothing.warn("ClothingItem not found for ItemVisual:" + itemVisual);
					}
				} else {
					this.addClothingItem(gameCharacter, modelSlot, itemVisual, clothingItem);
				}
			}

			gameCharacter.postUpdateModelTextures();
			gameCharacter.updateSpeedModifiers();
		}
	}

	public boolean isItemModelHidden(BodyLocationGroup bodyLocationGroup, ItemVisuals itemVisuals, ItemVisual itemVisual) {
		Item item = itemVisual.getScriptItem();
		if (item != null && bodyLocationGroup.getLocation(item.getBodyLocation()) != null) {
			for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
				if (itemVisuals.get(int1) != itemVisual) {
					Item item2 = ((ItemVisual)itemVisuals.get(int1)).getScriptItem();
					if (item2 != null && bodyLocationGroup.getLocation(item2.getBodyLocation()) != null && bodyLocationGroup.isHideModel(item2.getBodyLocation(), item.getBodyLocation())) {
						return true;
					}
				}
			}

			return false;
		} else {
			return false;
		}
	}

	private String processModelFileName(String string) {
		string = string.replaceAll("\\\\", "/");
		string = string.toLowerCase(Locale.ENGLISH);
		return string;
	}

	private void postProcessNewItemInstance(ModelInstance modelInstance, ModelManager.ModelSlot modelSlot, ImmutableColor immutableColor) {
		modelInstance.depthBias = 0.0F;
		modelInstance.matrixModel = modelSlot.model;
		modelInstance.tintR = immutableColor.r;
		modelInstance.tintG = immutableColor.g;
		modelInstance.tintB = immutableColor.b;
		modelInstance.parent = modelSlot.model;
		modelInstance.AnimPlayer = modelSlot.model.AnimPlayer;
		modelSlot.model.sub.add(0, modelInstance);
		modelSlot.sub.add(0, modelInstance);
		modelInstance.setOwner(modelSlot);
	}
}
