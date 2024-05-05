package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.util.StringUtils;


public final class WorldItemModelDrawer extends TextureDraw.GenericDrawer {
	private static final ObjectPool s_modelDrawerPool = new ObjectPool(WorldItemModelDrawer::new);
	private static final ObjectPool s_weaponPartParamPool = new ObjectPool(WorldItemModelDrawer.WeaponPartParams::new);
	private static final ArrayList s_tempWeaponPartList = new ArrayList();
	private static final ColorInfo tempColorInfo = new ColorInfo();
	private static final Matrix4f s_attachmentXfrm = new Matrix4f();
	private static final ImmutableColor ROTTEN_FOOD_COLOR = new ImmutableColor(0.5F, 0.5F, 0.5F);
	public static boolean NEW_WAY = true;
	private Model m_model;
	private ArrayList m_weaponParts;
	private float m_hue;
	private float m_tintR;
	private float m_tintG;
	private float m_tintB;
	private float m_x;
	private float m_y;
	private float m_z;
	private final Vector3f m_angle = new Vector3f();
	private final Matrix4f m_transform = new Matrix4f();
	private float m_ambientR;
	private float m_ambientG;
	private float m_ambientB;
	private float alpha = 1.0F;
	static final Vector3f tempVector3f = new Vector3f(0.0F, 5.0F, -2.0F);

	public static boolean renderMain(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3, float float4) {
		return renderMain(inventoryItem, square, float1, float2, float3, float4, -1.0F);
	}

	public static boolean renderMain(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3, float float4, float float5) {
		if (inventoryItem != null && square != null) {
			Core.getInstance();
			if (!Core.Option3DGroundItem) {
				return false;
			} else if (renderAtlasTexture(inventoryItem, square, float1, float2, float3, float4, float5)) {
				return true;
			} else {
				ModelScript modelScript;
				String string;
				String string2;
				String string3;
				WorldItemModelDrawer worldItemModelDrawer;
				Model model;
				if (!StringUtils.isNullOrEmpty(inventoryItem.getWorldStaticItem())) {
					modelScript = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem());
					if (modelScript != null) {
						string = modelScript.getMeshName();
						string2 = modelScript.getTextureName();
						string3 = modelScript.getShaderName();
						ImmutableColor immutableColor = ImmutableColor.white;
						float float6 = 1.0F;
						if (inventoryItem instanceof Food) {
							ModelScript modelScript2;
							if (((Food)inventoryItem).isCooked()) {
								modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Cooked");
								if (modelScript2 != null) {
									string2 = modelScript2.getTextureName();
									string = modelScript2.getMeshName();
									string3 = modelScript2.getShaderName();
									modelScript = modelScript2;
								}
							}

							if (((Food)inventoryItem).isBurnt()) {
								modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Burnt");
								if (modelScript2 != null) {
									string2 = modelScript2.getTextureName();
									string = modelScript2.getMeshName();
									string3 = modelScript2.getShaderName();
									modelScript = modelScript2;
								}
							}

							if (((Food)inventoryItem).isRotten()) {
								modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Rotten");
								if (modelScript2 != null) {
									string2 = modelScript2.getTextureName();
									string = modelScript2.getMeshName();
									string3 = modelScript2.getShaderName();
									modelScript = modelScript2;
								} else {
									immutableColor = ROTTEN_FOOD_COLOR;
								}
							}
						}

						if (inventoryItem instanceof Clothing || inventoryItem.getClothingItem() != null) {
							string2 = modelScript.getTextureName(true);
							ItemVisual itemVisual = inventoryItem.getVisual();
							ClothingItem clothingItem = inventoryItem.getClothingItem();
							immutableColor = itemVisual.getTint(clothingItem);
							if (string2 == null) {
								if (clothingItem.textureChoices.isEmpty()) {
									string2 = itemVisual.getBaseTexture(clothingItem);
								} else {
									string2 = itemVisual.getTextureChoice(clothingItem);
								}
							}
						}

						boolean boolean1 = modelScript.bStatic;
						model = ModelManager.instance.tryGetLoadedModel(string, string2, boolean1, string3, true);
						if (model == null) {
							ModelManager.instance.loadAdditionalModel(string, string2, boolean1, string3);
						}

						model = ModelManager.instance.getLoadedModel(string, string2, boolean1, string3);
						if (model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady()) {
							worldItemModelDrawer = (WorldItemModelDrawer)s_modelDrawerPool.alloc();
							worldItemModelDrawer.init(inventoryItem, square, float1, float2, float3, model, modelScript, float6, immutableColor, float4, false);
							if (modelScript.scale != 1.0F) {
								worldItemModelDrawer.m_transform.scale(modelScript.scale);
							}

							if (inventoryItem.worldScale != 1.0F) {
								worldItemModelDrawer.m_transform.scale(inventoryItem.worldScale);
							}

							worldItemModelDrawer.m_angle.x = 0.0F;
							if (float5 < 0.0F) {
								worldItemModelDrawer.m_angle.y = (float)inventoryItem.worldZRotation;
							} else {
								worldItemModelDrawer.m_angle.y = float5;
							}

							worldItemModelDrawer.m_angle.z = 0.0F;
							if (Core.bDebug) {
							}

							SpriteRenderer.instance.drawGeneric(worldItemModelDrawer);
							return true;
						}
					}
				} else if (inventoryItem instanceof Clothing) {
					ClothingItem clothingItem2 = inventoryItem.getClothingItem();
					ItemVisual itemVisual2 = inventoryItem.getVisual();
					if (clothingItem2 != null && itemVisual2 != null && "Bip01_Head".equalsIgnoreCase(clothingItem2.m_AttachBone) && (!((Clothing)inventoryItem).isCosmetic() || "Eyes".equals(inventoryItem.getBodyLocation()))) {
						boolean boolean2 = false;
						string3 = clothingItem2.getModel(boolean2);
						if (!StringUtils.isNullOrWhitespace(string3)) {
							String string4 = itemVisual2.getTextureChoice(clothingItem2);
							boolean boolean3 = clothingItem2.m_Static;
							String string5 = clothingItem2.m_Shader;
							model = ModelManager.instance.tryGetLoadedModel(string3, string4, boolean3, string5, false);
							if (model == null) {
								ModelManager.instance.loadAdditionalModel(string3, string4, boolean3, string5);
							}

							model = ModelManager.instance.getLoadedModel(string3, string4, boolean3, string5);
							if (model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady()) {
								worldItemModelDrawer = (WorldItemModelDrawer)s_modelDrawerPool.alloc();
								float float7 = itemVisual2.getHue(clothingItem2);
								ImmutableColor immutableColor2 = itemVisual2.getTint(clothingItem2);
								worldItemModelDrawer.init(inventoryItem, square, float1, float2, float3, model, (ModelScript)null, float7, immutableColor2, float4, false);
								if (NEW_WAY) {
									worldItemModelDrawer.m_angle.x = 180.0F + float4;
									if (float5 < 0.0F) {
										worldItemModelDrawer.m_angle.y = (float)inventoryItem.worldZRotation;
									} else {
										worldItemModelDrawer.m_angle.y = float5;
									}

									worldItemModelDrawer.m_angle.z = -90.0F;
									if (Core.bDebug) {
									}

									worldItemModelDrawer.m_transform.translate(-0.08F, 0.0F, 0.05F);
								}

								SpriteRenderer.instance.drawGeneric(worldItemModelDrawer);
								return true;
							}
						}
					}
				}

				if (inventoryItem instanceof HandWeapon) {
					modelScript = ScriptManager.instance.getModelScript(inventoryItem.getStaticModel());
					if (modelScript != null) {
						string = modelScript.getMeshName();
						string2 = modelScript.getTextureName();
						string3 = modelScript.getShaderName();
						boolean boolean4 = modelScript.bStatic;
						Model model2 = ModelManager.instance.tryGetLoadedModel(string, string2, boolean4, string3, false);
						if (model2 == null) {
							ModelManager.instance.loadAdditionalModel(string, string2, boolean4, string3);
						}

						model2 = ModelManager.instance.getLoadedModel(string, string2, boolean4, string3);
						if (model2 != null && model2.isReady() && model2.Mesh != null && model2.Mesh.isReady()) {
							WorldItemModelDrawer worldItemModelDrawer2 = (WorldItemModelDrawer)s_modelDrawerPool.alloc();
							float float8 = 1.0F;
							ImmutableColor immutableColor3 = ImmutableColor.white;
							worldItemModelDrawer2.init(inventoryItem, square, float1, float2, float3, model2, modelScript, float8, immutableColor3, float4, true);
							if (modelScript.scale != 1.0F) {
								worldItemModelDrawer2.m_transform.scale(modelScript.scale);
							}

							if (inventoryItem.worldScale != 1.0F) {
								worldItemModelDrawer2.m_transform.scale(inventoryItem.worldScale);
							}

							worldItemModelDrawer2.m_angle.x = 0.0F;
							if (!NEW_WAY) {
								worldItemModelDrawer2.m_angle.y = 180.0F;
							}

							if (float5 < 0.0F) {
								worldItemModelDrawer2.m_angle.y = (float)inventoryItem.worldZRotation;
							} else {
								worldItemModelDrawer2.m_angle.y = float5;
							}

							if (!worldItemModelDrawer2.initWeaponParts((HandWeapon)inventoryItem, modelScript)) {
								worldItemModelDrawer2.reset();
								s_modelDrawerPool.release((Object)worldItemModelDrawer2);
								return false;
							}

							SpriteRenderer.instance.drawGeneric(worldItemModelDrawer2);
							return true;
						}
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	private static boolean renderAtlasTexture(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3, float float4, float float5) {
		if (float4 > 0.0F) {
			return false;
		} else if (float5 >= 0.0F) {
			return false;
		} else {
			boolean boolean1 = !Core.bDebug || !GameKeyboard.isKeyDown(199);
			if (!boolean1) {
				return false;
			} else {
				if (inventoryItem.atlasTexture != null && !inventoryItem.atlasTexture.isStillValid(inventoryItem)) {
					inventoryItem.atlasTexture = null;
				}

				if (inventoryItem.atlasTexture == null) {
					inventoryItem.atlasTexture = WorldItemAtlas.instance.getItemTexture(inventoryItem);
				}

				if (inventoryItem.atlasTexture == null) {
					return false;
				} else if (inventoryItem.atlasTexture.isTooBig()) {
					return false;
				} else {
					if (IsoSprite.globalOffsetX == -1.0F) {
						IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
						IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
					}

					float float6 = IsoUtils.XToScreen(float1, float2, float3, 0);
					float float7 = IsoUtils.YToScreen(float1, float2, float3, 0);
					float6 += IsoSprite.globalOffsetX;
					float7 += IsoSprite.globalOffsetY;
					square.interpolateLight(tempColorInfo, float1 % 1.0F, float2 % 1.0F);
					float float8 = IsoWorldInventoryObject.getSurfaceAlpha(square, float3 - (float)((int)float3));
					inventoryItem.atlasTexture.render(float6, float7, tempColorInfo.r, tempColorInfo.g, tempColorInfo.b, float8);
					WorldItemAtlas.instance.render();
					return inventoryItem.atlasTexture.isRenderMainOK();
				}
			}
		}
	}

	private void init(InventoryItem inventoryItem, IsoGridSquare square, float float1, float float2, float float3, Model model, ModelScript modelScript, float float4, ImmutableColor immutableColor, float float5, boolean boolean1) {
		this.m_model = model;
		if (this.m_weaponParts != null) {
			s_weaponPartParamPool.release((List)this.m_weaponParts);
			this.m_weaponParts.clear();
		}

		this.m_tintR = immutableColor.r;
		this.m_tintG = immutableColor.g;
		this.m_tintB = immutableColor.b;
		this.m_hue = float4;
		this.m_x = float1;
		this.m_y = float2;
		this.m_z = float3;
		this.m_transform.rotationZ((90.0F + float5) * 0.017453292F);
		if (inventoryItem instanceof Clothing) {
			float float6 = -0.08F;
			float float7 = 0.05F;
			this.m_transform.translate(float6, 0.0F, float7);
		}

		this.m_angle.x = 0.0F;
		this.m_angle.y = 525.0F;
		this.m_angle.z = 0.0F;
		if (NEW_WAY) {
			this.m_transform.identity();
			this.m_angle.y = 0.0F;
			if (boolean1) {
				this.m_transform.rotateXYZ(0.0F, 3.1415927F, 1.5707964F);
			}

			if (modelScript != null) {
				ModelAttachment modelAttachment = modelScript.getAttachmentById("world");
				if (modelAttachment != null) {
					ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, s_attachmentXfrm);
					s_attachmentXfrm.invert();
					this.m_transform.mul((Matrix4fc)s_attachmentXfrm);
				}
			}

			if (model.Mesh != null && model.Mesh.isReady() && model.Mesh.m_transform != null) {
				model.Mesh.m_transform.transpose();
				this.m_transform.mul((Matrix4fc)model.Mesh.m_transform);
				model.Mesh.m_transform.transpose();
			}
		}

		square.interpolateLight(tempColorInfo, this.m_x % 1.0F, this.m_y % 1.0F);
		if (GameServer.bServer && ServerGUI.isCreated()) {
			tempColorInfo.set(1.0F, 1.0F, 1.0F, 1.0F);
		}

		this.m_ambientR = tempColorInfo.r;
		this.m_ambientG = tempColorInfo.g;
		this.m_ambientB = tempColorInfo.b;
		this.alpha = IsoWorldInventoryObject.getSurfaceAlpha(square, float3 - (float)((int)float3));
	}

	boolean initWeaponParts(HandWeapon handWeapon, ModelScript modelScript) {
		ArrayList arrayList = handWeapon.getModelWeaponPart();
		if (arrayList == null) {
			return true;
		} else {
			ArrayList arrayList2 = handWeapon.getAllWeaponParts(s_tempWeaponPartList);
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				WeaponPart weaponPart = (WeaponPart)arrayList2.get(int1);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					ModelWeaponPart modelWeaponPart = (ModelWeaponPart)arrayList.get(int2);
					if (weaponPart.getFullType().equals(modelWeaponPart.partType)) {
						if (!this.initWeaponPart(modelWeaponPart, modelScript)) {
							return false;
						}

						break;
					}
				}
			}

			return true;
		}
	}

	boolean initWeaponPart(ModelWeaponPart modelWeaponPart, ModelScript modelScript) {
		String string = StringUtils.discardNullOrWhitespace(modelWeaponPart.modelName);
		if (string == null) {
			return false;
		} else {
			ModelScript modelScript2 = ScriptManager.instance.getModelScript(string);
			if (modelScript2 == null) {
				return false;
			} else {
				String string2 = modelScript2.getMeshName();
				String string3 = modelScript2.getTextureName();
				String string4 = modelScript2.getShaderName();
				boolean boolean1 = modelScript2.bStatic;
				Model model = ModelManager.instance.tryGetLoadedModel(string2, string3, boolean1, string4, false);
				if (model == null) {
					ModelManager.instance.loadAdditionalModel(string2, string3, boolean1, string4);
				}

				model = ModelManager.instance.getLoadedModel(string2, string3, boolean1, string4);
				if (model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady()) {
					WorldItemModelDrawer.WeaponPartParams weaponPartParams = (WorldItemModelDrawer.WeaponPartParams)s_weaponPartParamPool.alloc();
					weaponPartParams.m_model = model;
					weaponPartParams.m_attachmentNameSelf = modelWeaponPart.attachmentNameSelf;
					weaponPartParams.m_attachmentNameParent = modelWeaponPart.attachmentParent;
					weaponPartParams.initTransform(modelScript, modelScript2);
					this.m_transform.mul((Matrix4fc)weaponPartParams.m_transform, weaponPartParams.m_transform);
					if (this.m_weaponParts == null) {
						this.m_weaponParts = new ArrayList();
					}

					this.m_weaponParts.add(weaponPartParams);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void render() {
		GL11.glPushAttrib(1048575);
		GL11.glPushClientAttrib(-1);
		Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, 0.0F, false);
		GL11.glRotated(-180.0, 0.0, 1.0, 0.0);
		GL11.glRotated((double)this.m_angle.x, 1.0, 0.0, 0.0);
		GL11.glRotated((double)this.m_angle.y, 0.0, 1.0, 0.0);
		GL11.glRotated((double)this.m_angle.z, 0.0, 0.0, 1.0);
		GL11.glBlendFunc(770, 771);
		GL11.glDepthFunc(513);
		GL11.glDepthMask(true);
		GL11.glDepthRange(0.0, 1.0);
		GL11.glEnable(2929);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		this.renderModel(this.m_model, this.m_transform);
		if (this.m_weaponParts != null) {
			for (int int1 = 0; int1 < this.m_weaponParts.size(); ++int1) {
				WorldItemModelDrawer.WeaponPartParams weaponPartParams = (WorldItemModelDrawer.WeaponPartParams)this.m_weaponParts.get(int1);
				this.renderModel(weaponPartParams.m_model, weaponPartParams.m_transform);
			}
		}

		if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
			Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 0.5F, 1.0F);
		}

		Core.getInstance().DoPopIsoStuff();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		SpriteRenderer.ringBuffer.restoreBoundTextures = true;
		SpriteRenderer.ringBuffer.restoreVBOs = true;
	}

	void renderModel(Model model, Matrix4f matrix4f) {
		if (model.bStatic) {
			if (model.Effect == null) {
				model.CreateShader("basicEffect");
			}

			Shader shader = model.Effect;
			if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
				shader.Start();
				if (model.tex != null) {
					shader.setTexture(model.tex, "Texture", 0);
				}

				shader.setDepthBias(0.0F);
				shader.setAmbient(this.m_ambientR * 0.4F, this.m_ambientG * 0.4F, this.m_ambientB * 0.4F);
				shader.setLightingAmount(1.0F);
				shader.setHueShift(this.m_hue);
				shader.setTint(this.m_tintR, this.m_tintG, this.m_tintB);
				shader.setAlpha(this.alpha);
				for (int int1 = 0; int1 < 5; ++int1) {
					shader.setLight(int1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
				}

				Vector3f vector3f = tempVector3f;
				vector3f.x = 0.0F;
				vector3f.y = 5.0F;
				vector3f.z = -2.0F;
				vector3f.rotateY((float)Math.toRadians((double)this.m_angle.y));
				float float1 = 1.5F;
				shader.setLight(4, vector3f.x, vector3f.z, vector3f.y, this.m_ambientR / 4.0F * float1, this.m_ambientG / 4.0F * float1, this.m_ambientB / 4.0F * float1, 5000.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
				shader.setTransformMatrix(matrix4f, false);
				model.Mesh.Draw(shader);
				shader.End();
			}
		}
	}

	public void postRender() {
		this.reset();
		s_modelDrawerPool.release((Object)this);
	}

	void reset() {
		if (this.m_weaponParts != null) {
			s_weaponPartParamPool.release((List)this.m_weaponParts);
			this.m_weaponParts.clear();
		}
	}

	private static final class WeaponPartParams {
		Model m_model;
		String m_attachmentNameSelf;
		String m_attachmentNameParent;
		final Matrix4f m_transform = new Matrix4f();

		void initTransform(ModelScript modelScript, ModelScript modelScript2) {
			this.m_transform.identity();
			Matrix4f matrix4f = WorldItemModelDrawer.s_attachmentXfrm;
			ModelAttachment modelAttachment = modelScript.getAttachmentById(this.m_attachmentNameParent);
			if (modelAttachment != null) {
				ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, matrix4f);
				this.m_transform.mul((Matrix4fc)matrix4f);
			}

			ModelAttachment modelAttachment2 = modelScript2.getAttachmentById(this.m_attachmentNameSelf);
			if (modelAttachment2 != null) {
				ModelInstanceRenderData.makeAttachmentTransform(modelAttachment2, matrix4f);
				matrix4f.invert();
				this.m_transform.mul((Matrix4fc)matrix4f);
			}
		}
	}
}
