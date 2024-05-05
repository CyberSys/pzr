package zombie.characterTextures;

import org.lwjgl.opengl.GL11;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.TextureCombinerCommand;
import zombie.core.textures.TextureCombinerShaderParam;


public final class CharacterSmartTexture extends SmartTexture {
	public static int BodyCategory = 0;
	public static int ClothingBottomCategory = 1;
	public static int ClothingTopCategory = 2;
	public static int ClothingItemCategory = 3;
	public static int DecalOverlayCategory = 300;
	public static int DirtOverlayCategory = 400;
	public static final String[] MaskFiles = new String[]{"BloodMaskHandL", "BloodMaskHandR", "BloodMaskLArmL", "BloodMaskLArmR", "BloodMaskUArmL", "BloodMaskUArmR", "BloodMaskChest", "BloodMaskStomach", "BloodMaskHead", "BloodMaskNeck", "BloodMaskGroin", "BloodMaskULegL", "BloodMaskULegR", "BloodMaskLLegL", "BloodMaskLLegR", "BloodMaskFootL", "BloodMaskFootR", "BloodMaskBack"};
	public static final String[] BasicPatchesMaskFiles = new String[]{"patches_left_hand_sheet", "patches_right_hand_sheet", "patches_left_lower_arm_sheet", "patches_right_lower_arm_sheet", "patches_left_upper_arm_sheet", "patches_right_upper_arm_sheet", "patches_chest_sheet", "patches_abdomen_sheet", "", "", "patches_groin_sheet", "patches_left_upper_leg_sheet", "patches_right_upper_leg_sheet", "patches_left_lower_leg_sheet", "patches_right_lower_leg_sheet", "", "", "patches_back_sheet"};
	public static final String[] DenimPatchesMaskFiles = new String[]{"patches_left_hand_denim", "patches_right_hand_denim", "patches_left_lower_arm_denim", "patches_right_lower_arm_denim", "patches_left_upper_arm_denim", "patches_right_upper_arm_denim", "patches_chest_denim", "patches_abdomen_denim", "", "", "patches_groin_denim", "patches_left_upper_leg_denim", "patches_right_upper_leg_denim", "patches_left_lower_leg_denim", "patches_right_lower_leg_denim", "", "", "patches_back_denim"};
	public static final String[] LeatherPatchesMaskFiles = new String[]{"patches_left_hand_leather", "patches_right_hand_leather", "patches_left_lower_arm_leather", "patches_right_lower_arm_leather", "patches_left_upper_arm_leather", "patches_right_upper_arm_leather", "patches_chest_leather", "patches_abdomen_leather", "", "", "patches_groin_leather", "patches_left_upper_leg_leather", "patches_right_upper_leg_leather", "patches_left_lower_leg_leather", "patches_right_lower_leg_leather", "", "", "patches_back_leather"};

	public void setBlood(BloodBodyPartType bloodBodyPartType, float float1) {
		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		int int1 = DecalOverlayCategory + bloodBodyPartType.index();
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (textureCombinerCommand != null) {
			for (int int2 = 0; int2 < textureCombinerCommand.shaderParams.size(); ++int2) {
				TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)textureCombinerCommand.shaderParams.get(int2);
				if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != float1 || textureCombinerShaderParam.max != float1)) {
					textureCombinerShaderParam.min = textureCombinerShaderParam.max = float1;
					this.setDirty();
				}
			}
		} else if (float1 > 0.0F) {
			String[] stringArray = MaskFiles;
			String string = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
			this.addOverlay("media/textures/BloodTextures/BloodOverlay.png", string, float1, int1);
		}
	}

	public void setDirt(BloodBodyPartType bloodBodyPartType, float float1) {
		float1 = Math.max(0.0F, Math.min(1.0F, float1));
		int int1 = DirtOverlayCategory + bloodBodyPartType.index();
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (textureCombinerCommand != null) {
			for (int int2 = 0; int2 < textureCombinerCommand.shaderParams.size(); ++int2) {
				TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)textureCombinerCommand.shaderParams.get(int2);
				if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != float1 || textureCombinerShaderParam.max != float1)) {
					textureCombinerShaderParam.min = textureCombinerShaderParam.max = float1;
					this.setDirty();
				}
			}
		} else if (float1 > 0.0F) {
			String[] stringArray = MaskFiles;
			String string = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
			this.addDirtOverlay("media/textures/BloodTextures/GrimeOverlay.png", string, float1, int1);
		}
	}

	public void removeBlood() {
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			this.removeBlood(BloodBodyPartType.FromIndex(int1));
		}
	}

	public void removeBlood(BloodBodyPartType bloodBodyPartType) {
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(DecalOverlayCategory + bloodBodyPartType.index());
		if (textureCombinerCommand != null) {
			for (int int1 = 0; int1 < textureCombinerCommand.shaderParams.size(); ++int1) {
				TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)textureCombinerCommand.shaderParams.get(int1);
				if (textureCombinerShaderParam.name.equals("intensity") && (textureCombinerShaderParam.min != 0.0F || textureCombinerShaderParam.max != 0.0F)) {
					textureCombinerShaderParam.min = textureCombinerShaderParam.max = 0.0F;
					this.setDirty();
				}
			}
		}
	}

	public float addBlood(BloodBodyPartType bloodBodyPartType, float float1, IsoGameCharacter gameCharacter) {
		int int1 = DecalOverlayCategory + bloodBodyPartType.index();
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (bloodBodyPartType == BloodBodyPartType.Head && gameCharacter != null) {
			ModelInstance modelInstance;
			if (gameCharacter.hair != null) {
				modelInstance = gameCharacter.hair;
				modelInstance.tintR -= 0.022F;
				if (gameCharacter.hair.tintR < 0.0F) {
					gameCharacter.hair.tintR = 0.0F;
				}

				modelInstance = gameCharacter.hair;
				modelInstance.tintG -= 0.03F;
				if (gameCharacter.hair.tintG < 0.0F) {
					gameCharacter.hair.tintG = 0.0F;
				}

				modelInstance = gameCharacter.hair;
				modelInstance.tintB -= 0.03F;
				if (gameCharacter.hair.tintB < 0.0F) {
					gameCharacter.hair.tintB = 0.0F;
				}
			}

			if (gameCharacter.beard != null) {
				modelInstance = gameCharacter.beard;
				modelInstance.tintR -= 0.022F;
				if (gameCharacter.beard.tintR < 0.0F) {
					gameCharacter.beard.tintR = 0.0F;
				}

				modelInstance = gameCharacter.beard;
				modelInstance.tintG -= 0.03F;
				if (gameCharacter.beard.tintG < 0.0F) {
					gameCharacter.beard.tintG = 0.0F;
				}

				modelInstance = gameCharacter.beard;
				modelInstance.tintB -= 0.03F;
				if (gameCharacter.beard.tintB < 0.0F) {
					gameCharacter.beard.tintB = 0.0F;
				}
			}
		}

		if (textureCombinerCommand != null) {
			for (int int2 = 0; int2 < textureCombinerCommand.shaderParams.size(); ++int2) {
				TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)textureCombinerCommand.shaderParams.get(int2);
				if (textureCombinerShaderParam.name.equals("intensity")) {
					float float2 = textureCombinerShaderParam.min;
					float2 += float1;
					float2 = Math.min(1.0F, float2);
					if (textureCombinerShaderParam.min != float2 || textureCombinerShaderParam.max != float2) {
						textureCombinerShaderParam.min = textureCombinerShaderParam.max = float2;
						this.setDirty();
					}

					return float2;
				}
			}
		} else {
			String[] stringArray = MaskFiles;
			String string = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
			this.addOverlay("media/textures/BloodTextures/BloodOverlay.png", string, float1, int1);
		}

		return float1;
	}

	public float addDirt(BloodBodyPartType bloodBodyPartType, float float1, IsoGameCharacter gameCharacter) {
		int int1 = DirtOverlayCategory + bloodBodyPartType.index();
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (bloodBodyPartType == BloodBodyPartType.Head && gameCharacter != null) {
			ModelInstance modelInstance;
			if (gameCharacter.hair != null) {
				modelInstance = gameCharacter.hair;
				modelInstance.tintR -= 0.022F;
				if (gameCharacter.hair.tintR < 0.0F) {
					gameCharacter.hair.tintR = 0.0F;
				}

				modelInstance = gameCharacter.hair;
				modelInstance.tintG -= 0.03F;
				if (gameCharacter.hair.tintG < 0.0F) {
					gameCharacter.hair.tintG = 0.0F;
				}

				modelInstance = gameCharacter.hair;
				modelInstance.tintB -= 0.03F;
				if (gameCharacter.hair.tintB < 0.0F) {
					gameCharacter.hair.tintB = 0.0F;
				}
			}

			if (gameCharacter.beard != null) {
				modelInstance = gameCharacter.beard;
				modelInstance.tintR -= 0.022F;
				if (gameCharacter.beard.tintR < 0.0F) {
					gameCharacter.beard.tintR = 0.0F;
				}

				modelInstance = gameCharacter.beard;
				modelInstance.tintG -= 0.03F;
				if (gameCharacter.beard.tintG < 0.0F) {
					gameCharacter.beard.tintG = 0.0F;
				}

				modelInstance = gameCharacter.beard;
				modelInstance.tintB -= 0.03F;
				if (gameCharacter.beard.tintB < 0.0F) {
					gameCharacter.beard.tintB = 0.0F;
				}
			}
		}

		if (textureCombinerCommand != null) {
			for (int int2 = 0; int2 < textureCombinerCommand.shaderParams.size(); ++int2) {
				TextureCombinerShaderParam textureCombinerShaderParam = (TextureCombinerShaderParam)textureCombinerCommand.shaderParams.get(int2);
				if (textureCombinerShaderParam.name.equals("intensity")) {
					float float2 = textureCombinerShaderParam.min;
					float2 += float1;
					float2 = Math.min(1.0F, float2);
					if (textureCombinerShaderParam.min != float2 || textureCombinerShaderParam.max != float2) {
						textureCombinerShaderParam.min = textureCombinerShaderParam.max = float2;
						this.setDirty();
					}

					return float2;
				}
			}
		} else {
			String[] stringArray = MaskFiles;
			String string = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
			this.addDirtOverlay("media/textures/BloodTextures/GrimeOverlay.png", string, float1, int1);
		}

		return float1;
	}

	public void addShirtDecal(String string) {
		GL11.glTexParameteri(3553, 10241, 9729);
		GL11.glTexParameteri(3553, 10240, 9729);
		this.addRect(string, 102, 118, 52, 52);
	}
}
