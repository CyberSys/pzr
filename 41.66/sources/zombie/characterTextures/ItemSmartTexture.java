package zombie.characterTextures;

import zombie.core.textures.SmartTexture;
import zombie.core.textures.TextureCombinerCommand;
import zombie.core.textures.TextureCombinerShaderParam;
import zombie.util.StringUtils;


public final class ItemSmartTexture extends SmartTexture {
	public static final int DecalOverlayCategory = 300;
	private String m_texName = null;

	public ItemSmartTexture(String string) {
		if (string != null) {
			this.add(string);
			this.m_texName = string;
		}
	}

	public ItemSmartTexture(String string, float float1) {
		this.addHue("media/textures/" + string + ".png", 300, float1);
		this.m_texName = string;
	}

	public void setDenimPatches(BloodBodyPartType bloodBodyPartType) {
		if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.DenimPatchesMaskFiles[bloodBodyPartType.index()])) {
			String[] stringArray = CharacterSmartTexture.DenimPatchesMaskFiles;
			String string = "media/textures/patches/" + stringArray[bloodBodyPartType.index()] + ".png";
			int int1 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
			this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int1);
		}
	}

	public void setLeatherPatches(BloodBodyPartType bloodBodyPartType) {
		if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.LeatherPatchesMaskFiles[bloodBodyPartType.index()])) {
			String[] stringArray = CharacterSmartTexture.LeatherPatchesMaskFiles;
			String string = "media/textures/patches/" + stringArray[bloodBodyPartType.index()] + ".png";
			int int1 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
			this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int1);
		}
	}

	public void setBasicPatches(BloodBodyPartType bloodBodyPartType) {
		if (!StringUtils.isNullOrEmpty(CharacterSmartTexture.BasicPatchesMaskFiles[bloodBodyPartType.index()])) {
			String[] stringArray = CharacterSmartTexture.BasicPatchesMaskFiles;
			String string = "media/textures/patches/" + stringArray[bloodBodyPartType.index()] + ".png";
			int int1 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
			this.addOverlayPatches(string, "media/textures/patches/patchesmask.png", int1);
		}
	}

	public void setBlood(String string, BloodBodyPartType bloodBodyPartType, float float1) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string2 = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		int int1 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
		this.setBlood(string, string2, float1, int1);
	}

	public void setBlood(String string, String string2, float float1, int int1) {
		float1 = Math.max(0.0F, Math.min(1.0F, float1));
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
			this.addOverlay(string, string2, float1, int1);
		}
	}

	public float addBlood(String string, BloodBodyPartType bloodBodyPartType, float float1) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string2 = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		int int1 = CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index();
		return this.addBlood(string, string2, float1, int1);
	}

	public float addDirt(String string, BloodBodyPartType bloodBodyPartType, float float1) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string2 = "media/textures/BloodTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		int int1 = CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index();
		return this.addDirt(string, string2, float1, int1);
	}

	public float addBlood(String string, String string2, float float1, int int1) {
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (textureCombinerCommand == null) {
			this.addOverlay(string, string2, float1, int1);
			return float1;
		} else {
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

			this.addOverlay(string, string2, float1, int1);
			return float1;
		}
	}

	public float addDirt(String string, String string2, float float1, int int1) {
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(int1);
		if (textureCombinerCommand == null) {
			this.addDirtOverlay(string, string2, float1, int1);
			return float1;
		} else {
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

			this.addOverlay(string, string2, float1, int1);
			return float1;
		}
	}

	public void removeBlood() {
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			this.removeBlood(BloodBodyPartType.FromIndex(int1));
		}
	}

	public void removeDirt() {
		for (int int1 = 0; int1 < BloodBodyPartType.MAX.index(); ++int1) {
			this.removeDirt(BloodBodyPartType.FromIndex(int1));
		}
	}

	public void removeBlood(BloodBodyPartType bloodBodyPartType) {
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(CharacterSmartTexture.DecalOverlayCategory + bloodBodyPartType.index());
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

	public void removeDirt(BloodBodyPartType bloodBodyPartType) {
		TextureCombinerCommand textureCombinerCommand = this.getFirstFromCategory(CharacterSmartTexture.DirtOverlayCategory + bloodBodyPartType.index());
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

	public String getTexName() {
		return this.m_texName;
	}
}
