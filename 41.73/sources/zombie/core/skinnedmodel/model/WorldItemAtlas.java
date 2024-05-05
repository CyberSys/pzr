package zombie.core.skinnedmodel.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.util.StringUtils;
import zombie.util.Type;


public final class WorldItemAtlas {
	public static final int ATLAS_SIZE = 512;
	public static final int MATRIX_SIZE = 1024;
	private static final float MAX_ZOOM = 2.5F;
	private TextureFBO fbo;
	public static final WorldItemAtlas instance = new WorldItemAtlas();
	private final HashMap itemTextureMap = new HashMap();
	private final ArrayList AtlasList = new ArrayList();
	private final WorldItemAtlas.ItemParams itemParams = new WorldItemAtlas.ItemParams();
	private final WorldItemAtlas.Checksummer checksummer = new WorldItemAtlas.Checksummer();
	private static final Stack JobPool = new Stack();
	private final ArrayList RenderJobs = new ArrayList();
	private final ObjectPool itemTextureDrawerPool = new ObjectPool(WorldItemAtlas.ItemTextureDrawer::new);
	private final ObjectPool weaponPartParamPool = new ObjectPool(WorldItemAtlas.WeaponPartParams::new);
	private final ArrayList m_tempWeaponPartList = new ArrayList();
	private static final Matrix4f s_attachmentXfrm = new Matrix4f();
	private static final ImmutableColor ROTTEN_FOOD_COLOR = new ImmutableColor(0.5F, 0.5F, 0.5F);

	public WorldItemAtlas.ItemTexture getItemTexture(InventoryItem inventoryItem) {
		return this.itemParams.init(inventoryItem) ? this.getItemTexture(this.itemParams) : null;
	}

	public WorldItemAtlas.ItemTexture getItemTexture(WorldItemAtlas.ItemParams itemParams) {
		String string = this.getItemKey(itemParams);
		WorldItemAtlas.ItemTexture itemTexture = (WorldItemAtlas.ItemTexture)this.itemTextureMap.get(string);
		if (itemTexture != null) {
			return itemTexture;
		} else {
			WorldItemAtlas.AtlasEntry atlasEntry = new WorldItemAtlas.AtlasEntry();
			atlasEntry.key = string;
			itemTexture = new WorldItemAtlas.ItemTexture();
			itemTexture.itemParams.copyFrom(itemParams);
			itemTexture.entry = atlasEntry;
			this.itemTextureMap.put(string, itemTexture);
			this.RenderJobs.add(WorldItemAtlas.RenderJob.getNew().init(itemParams, atlasEntry));
			return itemTexture;
		}
	}

	private void assignEntryToAtlas(WorldItemAtlas.AtlasEntry atlasEntry, int int1, int int2) {
		if (atlasEntry.atlas == null) {
			for (int int3 = 0; int3 < this.AtlasList.size(); ++int3) {
				WorldItemAtlas.Atlas atlas = (WorldItemAtlas.Atlas)this.AtlasList.get(int3);
				if (!atlas.isFull() && atlas.ENTRY_WID == int1 && atlas.ENTRY_HGT == int2) {
					atlas.addEntry(atlasEntry);
					return;
				}
			}

			WorldItemAtlas.Atlas atlas2 = new WorldItemAtlas.Atlas(512, 512, int1, int2);
			atlas2.addEntry(atlasEntry);
			this.AtlasList.add(atlas2);
		}
	}

	private String getItemKey(WorldItemAtlas.ItemParams itemParams) {
		try {
			this.checksummer.reset();
			this.checksummer.update(itemParams.m_model.Name);
			if (itemParams.m_weaponParts != null) {
				for (int int1 = 0; int1 < itemParams.m_weaponParts.size(); ++int1) {
					WorldItemAtlas.WeaponPartParams weaponPartParams = (WorldItemAtlas.WeaponPartParams)itemParams.m_weaponParts.get(int1);
					this.checksummer.update(weaponPartParams.m_model.Name);
				}
			}

			this.checksummer.update((int)(itemParams.worldScale * 1000.0F));
			this.checksummer.update((byte)((int)(itemParams.m_tintR * 255.0F)));
			this.checksummer.update((byte)((int)(itemParams.m_tintG * 255.0F)));
			this.checksummer.update((byte)((int)(itemParams.m_tintB * 255.0F)));
			this.checksummer.update((int)(itemParams.m_angle.x * 1000.0F));
			this.checksummer.update((int)(itemParams.m_angle.y * 1000.0F));
			this.checksummer.update((int)(itemParams.m_angle.z * 1000.0F));
			this.checksummer.update((byte)itemParams.m_foodState.ordinal());
			return this.checksummer.checksumToString();
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
			return "bogus";
		}
	}

	public void render() {
		int int1;
		for (int1 = 0; int1 < this.AtlasList.size(); ++int1) {
			WorldItemAtlas.Atlas atlas = (WorldItemAtlas.Atlas)this.AtlasList.get(int1);
			if (atlas.clear) {
				SpriteRenderer.instance.drawGeneric(new WorldItemAtlas.ClearAtlasTexture(atlas));
			}
		}

		if (!this.RenderJobs.isEmpty()) {
			for (int1 = 0; int1 < this.RenderJobs.size(); ++int1) {
				WorldItemAtlas.RenderJob renderJob = (WorldItemAtlas.RenderJob)this.RenderJobs.get(int1);
				if (renderJob.done != 1 || renderJob.renderRefCount <= 0) {
					if (renderJob.done == 1 && renderJob.renderRefCount == 0) {
						this.RenderJobs.remove(int1--);
						assert !JobPool.contains(renderJob);
						JobPool.push(renderJob);
					} else {
						renderJob.entry.bRenderMainOK = renderJob.renderMain();
						if (renderJob.entry.bRenderMainOK) {
							++renderJob.renderRefCount;
							SpriteRenderer.instance.drawGeneric(renderJob);
						}
					}
				}
			}
		}
	}

	public void renderUI() {
		if (DebugOptions.instance.WorldItemAtlasRender.getValue() && GameKeyboard.isKeyPressed(209)) {
			this.Reset();
		}

		if (DebugOptions.instance.WorldItemAtlasRender.getValue()) {
			int int1 = 512 / Core.TileScale;
			int1 /= 2;
			int int2 = 0;
			int int3 = 0;
			for (int int4 = 0; int4 < this.AtlasList.size(); ++int4) {
				WorldItemAtlas.Atlas atlas = (WorldItemAtlas.Atlas)this.AtlasList.get(int4);
				SpriteRenderer.instance.renderi((Texture)null, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 0.75F, (Consumer)null);
				SpriteRenderer.instance.renderi(atlas.tex, int2, int3, int1, int1, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
				float float1 = (float)int1 / (float)atlas.tex.getWidth();
				int int5;
				for (int5 = 0; int5 <= atlas.tex.getWidth() / atlas.ENTRY_WID; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, (int)((float)int2 + (float)(int5 * atlas.ENTRY_WID) * float1), int3, (int)((float)int2 + (float)(int5 * atlas.ENTRY_WID) * float1), int3 + int1, 0.5F, 0.5F, 0.5F, 1.0F);
				}

				for (int5 = 0; int5 <= atlas.tex.getHeight() / atlas.ENTRY_HGT; ++int5) {
					SpriteRenderer.instance.renderline((Texture)null, int2, (int)((float)(int3 + int1) - (float)(int5 * atlas.ENTRY_HGT) * float1), int2 + int1, (int)((float)(int3 + int1) - (float)(int5 * atlas.ENTRY_HGT) * float1), 0.5F, 0.5F, 0.5F, 1.0F);
				}

				int3 += int1;
				if (int3 + int1 > Core.getInstance().getScreenHeight()) {
					int3 = 0;
					int2 += int1;
				}
			}
		}
	}

	public void Reset() {
		if (this.fbo != null) {
			this.fbo.destroyLeaveTexture();
			this.fbo = null;
		}

		this.AtlasList.forEach(WorldItemAtlas.Atlas::Reset);
		this.AtlasList.clear();
		this.itemTextureMap.values().forEach(WorldItemAtlas.ItemTexture::Reset);
		this.itemTextureMap.clear();
		JobPool.forEach(WorldItemAtlas.RenderJob::Reset);
		JobPool.clear();
		this.RenderJobs.clear();
	}

	private static final class ItemParams {
		float worldScale = 1.0F;
		float worldZRotation = 0.0F;
		WorldItemAtlas.ItemParams.FoodState m_foodState;
		private Model m_model;
		private ArrayList m_weaponParts;
		private float m_hue;
		private float m_tintR;
		private float m_tintG;
		private float m_tintB;
		private final Vector3f m_angle;
		private final Matrix4f m_transform;
		private float m_ambientR;
		private float m_ambientG;
		private float m_ambientB;
		private float alpha;

		ItemParams() {
			this.m_foodState = WorldItemAtlas.ItemParams.FoodState.Normal;
			this.m_angle = new Vector3f();
			this.m_transform = new Matrix4f();
			this.m_ambientR = 1.0F;
			this.m_ambientG = 1.0F;
			this.m_ambientB = 1.0F;
			this.alpha = 1.0F;
		}

		void copyFrom(WorldItemAtlas.ItemParams itemParams) {
			this.worldScale = itemParams.worldScale;
			this.worldZRotation = itemParams.worldZRotation;
			this.m_foodState = itemParams.m_foodState;
			this.m_model = itemParams.m_model;
			if (this.m_weaponParts != null) {
				WorldItemAtlas.instance.weaponPartParamPool.release((List)this.m_weaponParts);
				this.m_weaponParts.clear();
			}

			if (itemParams.m_weaponParts != null) {
				if (this.m_weaponParts == null) {
					this.m_weaponParts = new ArrayList();
				}

				for (int int1 = 0; int1 < itemParams.m_weaponParts.size(); ++int1) {
					WorldItemAtlas.WeaponPartParams weaponPartParams = (WorldItemAtlas.WeaponPartParams)itemParams.m_weaponParts.get(int1);
					this.m_weaponParts.add(((WorldItemAtlas.WeaponPartParams)WorldItemAtlas.instance.weaponPartParamPool.alloc()).init(weaponPartParams));
				}
			}

			this.m_hue = itemParams.m_hue;
			this.m_tintR = itemParams.m_tintR;
			this.m_tintG = itemParams.m_tintG;
			this.m_tintB = itemParams.m_tintB;
			this.m_angle.set((Vector3fc)itemParams.m_angle);
			this.m_transform.set((Matrix4fc)itemParams.m_transform);
		}

		boolean init(InventoryItem inventoryItem) {
			this.Reset();
			this.worldScale = inventoryItem.worldScale;
			this.worldZRotation = (float)inventoryItem.worldZRotation;
			float float1 = 0.0F;
			String string = StringUtils.discardNullOrWhitespace(inventoryItem.getWorldStaticItem());
			String string2;
			if (string != null) {
				ModelScript modelScript = ScriptManager.instance.getModelScript(string);
				if (modelScript == null) {
					return false;
				} else {
					String string3 = modelScript.getMeshName();
					string2 = modelScript.getTextureName();
					String string4 = modelScript.getShaderName();
					ImmutableColor immutableColor = ImmutableColor.white;
					float float2 = 1.0F;
					Food food = (Food)Type.tryCastTo(inventoryItem, Food.class);
					if (food != null) {
						this.m_foodState = this.getFoodState(food);
						ModelScript modelScript2;
						if (food.isCooked()) {
							modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Cooked");
							if (modelScript2 != null) {
								string2 = modelScript2.getTextureName();
								string3 = modelScript2.getMeshName();
								string4 = modelScript2.getShaderName();
								modelScript = modelScript2;
							}
						}

						if (food.isBurnt()) {
							modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Burnt");
							if (modelScript2 != null) {
								string2 = modelScript2.getTextureName();
								string3 = modelScript2.getMeshName();
								string4 = modelScript2.getShaderName();
								modelScript = modelScript2;
							}
						}

						if (food.isRotten()) {
							modelScript2 = ScriptManager.instance.getModelScript(inventoryItem.getWorldStaticItem() + "Rotten");
							if (modelScript2 != null) {
								string2 = modelScript2.getTextureName();
								string3 = modelScript2.getMeshName();
								string4 = modelScript2.getShaderName();
								modelScript = modelScript2;
							} else {
								immutableColor = WorldItemAtlas.ROTTEN_FOOD_COLOR;
							}
						}
					}

					Clothing clothing = (Clothing)Type.tryCastTo(inventoryItem, Clothing.class);
					if (clothing != null || inventoryItem.getClothingItem() != null) {
						String string5 = modelScript.getTextureName(true);
						ItemVisual itemVisual = inventoryItem.getVisual();
						ClothingItem clothingItem = inventoryItem.getClothingItem();
						ImmutableColor immutableColor2 = itemVisual.getTint(clothingItem);
						if (string5 == null) {
							if (clothingItem.textureChoices.isEmpty()) {
								string5 = itemVisual.getBaseTexture(clothingItem);
							} else {
								string5 = itemVisual.getTextureChoice(clothingItem);
							}
						}

						if (string5 != null) {
							string2 = string5;
							immutableColor = immutableColor2;
						}
					}

					boolean boolean1 = modelScript.bStatic;
					Model model = ModelManager.instance.tryGetLoadedModel(string3, string2, boolean1, string4, true);
					if (model == null) {
						ModelManager.instance.loadAdditionalModel(string3, string2, boolean1, string4);
					}

					model = ModelManager.instance.getLoadedModel(string3, string2, boolean1, string4);
					if (model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady()) {
						this.init(inventoryItem, model, modelScript, float2, immutableColor, float1, false);
						if (this.worldScale != 1.0F) {
							this.m_transform.scale(modelScript.scale * this.worldScale);
						} else if (modelScript.scale != 1.0F) {
							this.m_transform.scale(modelScript.scale);
						}

						this.m_angle.x = 0.0F;
						this.m_angle.y = this.worldZRotation;
						this.m_angle.z = 0.0F;
						return true;
					} else {
						return false;
					}
				}
			} else {
				Clothing clothing2 = (Clothing)Type.tryCastTo(inventoryItem, Clothing.class);
				String string6;
				String string7;
				Model model2;
				float float3;
				ImmutableColor immutableColor3;
				if (clothing2 == null) {
					HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(inventoryItem, HandWeapon.class);
					if (handWeapon != null) {
						string2 = StringUtils.discardNullOrWhitespace(handWeapon.getStaticModel());
						if (string2 == null) {
							return false;
						} else {
							ModelScript modelScript3 = ScriptManager.instance.getModelScript(string2);
							if (modelScript3 == null) {
								return false;
							} else {
								string6 = modelScript3.getMeshName();
								string7 = modelScript3.getTextureName();
								String string8 = modelScript3.getShaderName();
								boolean boolean2 = modelScript3.bStatic;
								model2 = ModelManager.instance.tryGetLoadedModel(string6, string7, boolean2, string8, false);
								if (model2 == null) {
									ModelManager.instance.loadAdditionalModel(string6, string7, boolean2, string8);
								}

								model2 = ModelManager.instance.getLoadedModel(string6, string7, boolean2, string8);
								if (model2 != null && model2.isReady() && model2.Mesh != null && model2.Mesh.isReady()) {
									float3 = 1.0F;
									immutableColor3 = ImmutableColor.white;
									this.init(inventoryItem, model2, modelScript3, float3, immutableColor3, float1, true);
									if (this.worldScale != 1.0F) {
										this.m_transform.scale(modelScript3.scale * this.worldScale);
									} else if (modelScript3.scale != 1.0F) {
										this.m_transform.scale(modelScript3.scale);
									}

									this.m_angle.x = 0.0F;
									this.m_angle.y = this.worldZRotation;
									return this.initWeaponParts(handWeapon, modelScript3);
								} else {
									return false;
								}
							}
						}
					} else {
						return false;
					}
				} else {
					ClothingItem clothingItem2 = inventoryItem.getClothingItem();
					ItemVisual itemVisual2 = inventoryItem.getVisual();
					boolean boolean3 = false;
					string6 = clothingItem2.getModel(boolean3);
					if (clothingItem2 != null && itemVisual2 != null && !StringUtils.isNullOrWhitespace(string6) && "Bip01_Head".equalsIgnoreCase(clothingItem2.m_AttachBone) && (!clothing2.isCosmetic() || "Eyes".equals(inventoryItem.getBodyLocation()))) {
						string7 = itemVisual2.getTextureChoice(clothingItem2);
						boolean boolean4 = clothingItem2.m_Static;
						String string9 = clothingItem2.m_Shader;
						model2 = ModelManager.instance.tryGetLoadedModel(string6, string7, boolean4, string9, false);
						if (model2 == null) {
							ModelManager.instance.loadAdditionalModel(string6, string7, boolean4, string9);
						}

						model2 = ModelManager.instance.getLoadedModel(string6, string7, boolean4, string9);
						if (model2 != null && model2.isReady() && model2.Mesh != null && model2.Mesh.isReady()) {
							float3 = itemVisual2.getHue(clothingItem2);
							immutableColor3 = itemVisual2.getTint(clothingItem2);
							this.init(inventoryItem, model2, (ModelScript)null, float3, immutableColor3, float1, false);
							this.m_angle.x = 180.0F + float1;
							this.m_angle.y = this.worldZRotation;
							this.m_angle.z = -90.0F;
							this.m_transform.translate(-0.08F, 0.0F, 0.05F);
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}

		boolean initWeaponParts(HandWeapon handWeapon, ModelScript modelScript) {
			ArrayList arrayList = handWeapon.getModelWeaponPart();
			if (arrayList == null) {
				return true;
			} else {
				ArrayList arrayList2 = handWeapon.getAllWeaponParts(WorldItemAtlas.instance.m_tempWeaponPartList);
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
						WorldItemAtlas.WeaponPartParams weaponPartParams = (WorldItemAtlas.WeaponPartParams)WorldItemAtlas.instance.weaponPartParamPool.alloc();
						weaponPartParams.m_model = model;
						weaponPartParams.m_attachmentNameSelf = modelWeaponPart.attachmentNameSelf;
						weaponPartParams.m_attachmentNameParent = modelWeaponPart.attachmentParent;
						weaponPartParams.initTransform(modelScript, modelScript2);
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

		void init(InventoryItem inventoryItem, Model model, ModelScript modelScript, float float1, ImmutableColor immutableColor, float float2, boolean boolean1) {
			this.m_model = model;
			this.m_tintR = immutableColor.r;
			this.m_tintG = immutableColor.g;
			this.m_tintB = immutableColor.b;
			this.m_hue = float1;
			this.m_angle.set(0.0F);
			this.m_transform.identity();
			this.m_ambientR = this.m_ambientG = this.m_ambientB = 1.0F;
			if (boolean1) {
				this.m_transform.rotateXYZ(0.0F, 3.1415927F, 1.5707964F);
			}

			if (modelScript != null) {
				ModelAttachment modelAttachment = modelScript.getAttachmentById("world");
				if (modelAttachment != null) {
					ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, WorldItemAtlas.s_attachmentXfrm);
					WorldItemAtlas.s_attachmentXfrm.invert();
					this.m_transform.mul((Matrix4fc)WorldItemAtlas.s_attachmentXfrm);
				}
			}

			if (model.Mesh != null && model.Mesh.isReady() && model.Mesh.m_transform != null) {
				model.Mesh.m_transform.transpose();
				this.m_transform.mul((Matrix4fc)model.Mesh.m_transform);
				model.Mesh.m_transform.transpose();
			}
		}

		WorldItemAtlas.ItemParams.FoodState getFoodState(Food food) {
			WorldItemAtlas.ItemParams.FoodState foodState = WorldItemAtlas.ItemParams.FoodState.Normal;
			if (food.isCooked()) {
				foodState = WorldItemAtlas.ItemParams.FoodState.Cooked;
			}

			if (food.isBurnt()) {
				foodState = WorldItemAtlas.ItemParams.FoodState.Burnt;
			}

			if (food.isRotten()) {
				foodState = WorldItemAtlas.ItemParams.FoodState.Rotten;
			}

			return foodState;
		}

		boolean isStillValid(InventoryItem inventoryItem) {
			if (inventoryItem.worldScale == this.worldScale && (float)inventoryItem.worldZRotation == this.worldZRotation) {
				Food food = (Food)Type.tryCastTo(inventoryItem, Food.class);
				return food == null || this.getFoodState(food) == this.m_foodState;
			} else {
				return false;
			}
		}

		void Reset() {
			this.m_model = null;
			this.m_foodState = WorldItemAtlas.ItemParams.FoodState.Normal;
			if (this.m_weaponParts != null) {
				WorldItemAtlas.instance.weaponPartParamPool.release((List)this.m_weaponParts);
				this.m_weaponParts.clear();
			}
		}

		static enum FoodState {

			Normal,
			Cooked,
			Burnt,
			Rotten;

			private static WorldItemAtlas.ItemParams.FoodState[] $values() {
				return new WorldItemAtlas.ItemParams.FoodState[]{Normal, Cooked, Burnt, Rotten};
			}
		}
	}

	private static final class Checksummer {
		private MessageDigest md;
		private final StringBuilder sb = new StringBuilder();

		public void reset() throws NoSuchAlgorithmException {
			if (this.md == null) {
				this.md = MessageDigest.getInstance("MD5");
			}

			this.md.reset();
		}

		public void update(byte byte1) {
			this.md.update(byte1);
		}

		public void update(boolean boolean1) {
			this.md.update((byte)(boolean1 ? 1 : 0));
		}

		public void update(int int1) {
			this.md.update((byte)(int1 & 255));
			this.md.update((byte)(int1 >> 8 & 255));
			this.md.update((byte)(int1 >> 16 & 255));
			this.md.update((byte)(int1 >> 24 & 255));
		}

		public void update(String string) {
			if (string != null && !string.isEmpty()) {
				this.md.update(string.getBytes());
			}
		}

		public void update(ImmutableColor immutableColor) {
			this.update((byte)((int)(immutableColor.r * 255.0F)));
			this.update((byte)((int)(immutableColor.g * 255.0F)));
			this.update((byte)((int)(immutableColor.b * 255.0F)));
		}

		public void update(IsoGridSquare.ResultLight resultLight, float float1, float float2, float float3) {
			if (resultLight != null && resultLight.radius > 0) {
				this.update((int)((float)resultLight.x - float1));
				this.update((int)((float)resultLight.y - float2));
				this.update((int)((float)resultLight.z - float3));
				this.update((byte)((int)(resultLight.r * 255.0F)));
				this.update((byte)((int)(resultLight.g * 255.0F)));
				this.update((byte)((int)(resultLight.b * 255.0F)));
				this.update((byte)resultLight.radius);
			}
		}

		public String checksumToString() {
			byte[] byteArray = this.md.digest();
			this.sb.setLength(0);
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				this.sb.append(byteArray[int1] & 255);
			}

			return this.sb.toString();
		}
	}

	public static final class ItemTexture {
		final WorldItemAtlas.ItemParams itemParams = new WorldItemAtlas.ItemParams();
		WorldItemAtlas.AtlasEntry entry;

		public boolean isStillValid(InventoryItem inventoryItem) {
			return this.entry == null ? false : this.itemParams.isStillValid(inventoryItem);
		}

		public boolean isRenderMainOK() {
			return this.entry.bRenderMainOK;
		}

		public boolean isTooBig() {
			return this.entry.bTooBig;
		}

		public void render(float float1, float float2, float float3, float float4, float float5, float float6) {
			if (this.entry.ready && this.entry.tex.isReady()) {
				SpriteRenderer.instance.m_states.getPopulatingActiveState().render(this.entry.tex, float1 - ((float)this.entry.w / 2.0F - this.entry.offsetX) / 2.5F, float2 - ((float)this.entry.h / 2.0F - this.entry.offsetY) / 2.5F, (float)this.entry.w / 2.5F, (float)this.entry.h / 2.5F, float3, float4, float5, float6, (Consumer)null);
			} else {
				SpriteRenderer.instance.drawGeneric(((WorldItemAtlas.ItemTextureDrawer)WorldItemAtlas.instance.itemTextureDrawerPool.alloc()).init(this, float1, float2, float3, float4, float5, float6));
			}
		}

		void Reset() {
			this.itemParams.Reset();
			this.entry = null;
		}
	}

	private static final class AtlasEntry {
		public WorldItemAtlas.Atlas atlas;
		public String key;
		public int x;
		public int y;
		public int w;
		public int h;
		public float offsetX;
		public float offsetY;
		public Texture tex;
		public boolean ready = false;
		public boolean bRenderMainOK = false;
		public boolean bTooBig = false;

		public void Reset() {
			this.atlas = null;
			this.tex.destroy();
			this.tex = null;
			this.ready = false;
			this.bRenderMainOK = false;
			this.bTooBig = false;
		}
	}

	private static final class RenderJob extends TextureDraw.GenericDrawer {
		public final WorldItemAtlas.ItemParams itemParams = new WorldItemAtlas.ItemParams();
		public WorldItemAtlas.AtlasEntry entry;
		public int done = 0;
		public int renderRefCount;
		public boolean bClearThisSlotOnly;
		int entryW;
		int entryH;
		final int[] m_viewport = new int[4];
		final Matrix4f m_matri4f = new Matrix4f();
		final Matrix4f m_projection = new Matrix4f();
		final Matrix4f m_modelView = new Matrix4f();
		final Vector3f m_scenePos = new Vector3f();
		final float[] m_bounds = new float[4];
		static final Vector3f tempVector3f = new Vector3f(0.0F, 5.0F, -2.0F);
		static final Matrix4f tempMatrix4f_1 = new Matrix4f();
		static final Matrix4f tempMatrix4f_2 = new Matrix4f();
		static final float[] xs = new float[8];
		static final float[] ys = new float[8];

		public static WorldItemAtlas.RenderJob getNew() {
			return WorldItemAtlas.JobPool.isEmpty() ? new WorldItemAtlas.RenderJob() : (WorldItemAtlas.RenderJob)WorldItemAtlas.JobPool.pop();
		}

		public WorldItemAtlas.RenderJob init(WorldItemAtlas.ItemParams itemParams, WorldItemAtlas.AtlasEntry atlasEntry) {
			this.itemParams.copyFrom(itemParams);
			this.entry = atlasEntry;
			this.bClearThisSlotOnly = false;
			this.entryW = 0;
			this.entryH = 0;
			this.done = 0;
			this.renderRefCount = 0;
			return this;
		}

		public boolean renderMain() {
			Model model = this.itemParams.m_model;
			return model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady();
		}

		public void render() {
			if (this.done != 1) {
				Model model = this.itemParams.m_model;
				if (model != null && model.Mesh != null && model.Mesh.isReady()) {
					float float1 = 0.0F;
					float float2 = 0.0F;
					this.calcMatrices(this.m_projection, this.m_modelView, float1, float2);
					this.calcModelBounds(this.m_bounds);
					this.calcModelOffset();
					this.calcEntrySize();
					if (this.entryW > 0 && this.entryH > 0) {
						if (this.entryW <= 512 && this.entryH <= 512) {
							WorldItemAtlas.instance.assignEntryToAtlas(this.entry, this.entryW, this.entryH);
							GL11.glPushAttrib(1048575);
							GL11.glPushClientAttrib(-1);
							GL11.glDepthMask(true);
							GL11.glColorMask(true, true, true, true);
							GL11.glDisable(3089);
							TextureFBO textureFBO = WorldItemAtlas.instance.fbo;
							if (textureFBO.getTexture() != this.entry.atlas.tex) {
								textureFBO.setTexture(this.entry.atlas.tex);
							}

							textureFBO.startDrawing(this.entry.atlas.clear, this.entry.atlas.clear);
							if (this.entry.atlas.clear) {
								this.entry.atlas.clear = false;
							}

							this.clearColorAndDepth();
							int int1 = this.entry.x - (int)this.entry.offsetX - (1024 - this.entry.w) / 2;
							int int2 = -((int)this.entry.offsetY) - (1024 - this.entry.h) / 2;
							int2 += 512 - (this.entry.y + this.entry.h);
							GL11.glViewport(int1, int2, 1024, 1024);
							boolean boolean1 = this.renderModel(this.itemParams.m_model, (Matrix4f)null);
							if (this.itemParams.m_weaponParts != null && !this.itemParams.m_weaponParts.isEmpty()) {
								for (int int3 = 0; int3 < this.itemParams.m_weaponParts.size(); ++int3) {
									WorldItemAtlas.WeaponPartParams weaponPartParams = (WorldItemAtlas.WeaponPartParams)this.itemParams.m_weaponParts.get(int3);
									if (!this.renderModel(weaponPartParams.m_model, weaponPartParams.m_transform)) {
										boolean1 = false;
										break;
									}
								}
							}

							textureFBO.endDrawing();
							if (!boolean1) {
								GL11.glPopAttrib();
								GL11.glPopClientAttrib();
							} else {
								this.entry.ready = true;
								this.done = 1;
								Texture.lastTextureID = -1;
								SpriteRenderer.ringBuffer.restoreBoundTextures = true;
								SpriteRenderer.ringBuffer.restoreVBOs = true;
								GL11.glPopAttrib();
								GL11.glPopClientAttrib();
							}
						} else {
							this.entry.bTooBig = true;
							this.done = 1;
						}
					}
				}
			}
		}

		public void postRender() {
			if (this.entry != null) {
				assert this.renderRefCount > 0;
				--this.renderRefCount;
			}
		}

		void clearColorAndDepth() {
			GL11.glEnable(3089);
			GL11.glScissor(this.entry.x, 512 - (this.entry.y + this.entry.h), this.entry.w, this.entry.h);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			GL11.glClear(16640);
			GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
			this.restoreScreenStencil();
			GL11.glDisable(3089);
		}

		void restoreScreenStencil() {
			int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
			int int2 = int1 != 0 && int1 != 2 ? Core.getInstance().getOffscreenTrueWidth() / 2 : 0;
			int int3 = int1 != 0 && int1 != 1 ? Core.getInstance().getOffscreenTrueHeight() / 2 : 0;
			int int4 = Core.getInstance().getOffscreenTrueWidth();
			int int5 = Core.getInstance().getOffscreenTrueHeight();
			if (IsoPlayer.numPlayers > 1) {
				int4 /= 2;
			}

			if (IsoPlayer.numPlayers > 2) {
				int5 /= 2;
			}

			GL11.glScissor(int2, int3, int4, int5);
		}

		boolean renderModel(Model model, Matrix4f matrix4f) {
			if (!model.bStatic) {
				return false;
			} else {
				if (model.Effect == null) {
					model.CreateShader("basicEffect");
				}

				Shader shader = model.Effect;
				if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
					if (model.tex != null && !model.tex.isReady()) {
						return false;
					} else {
						PZGLUtil.pushAndLoadMatrix(5889, this.m_projection);
						Matrix4f matrix4f2 = tempMatrix4f_1.set((Matrix4fc)this.m_modelView);
						Matrix4f matrix4f3 = tempMatrix4f_2.set((Matrix4fc)this.itemParams.m_transform).invert();
						matrix4f2.mul((Matrix4fc)matrix4f3);
						PZGLUtil.pushAndLoadMatrix(5888, matrix4f2);
						GL11.glBlendFunc(770, 771);
						GL11.glDepthFunc(513);
						GL11.glDepthMask(true);
						GL11.glDepthRange(0.0, 1.0);
						GL11.glEnable(2929);
						GL11.glColor3f(1.0F, 1.0F, 1.0F);
						shader.Start();
						if (model.tex == null) {
							shader.setTexture(Texture.getErrorTexture(), "Texture", 0);
						} else {
							shader.setTexture(model.tex, "Texture", 0);
						}

						shader.setDepthBias(0.0F);
						shader.setAmbient(this.itemParams.m_ambientR * 0.4F, this.itemParams.m_ambientG * 0.4F, this.itemParams.m_ambientB * 0.4F);
						shader.setLightingAmount(1.0F);
						shader.setHueShift(this.itemParams.m_hue);
						shader.setTint(this.itemParams.m_tintR, this.itemParams.m_tintG, this.itemParams.m_tintB);
						shader.setAlpha(this.itemParams.alpha);
						for (int int1 = 0; int1 < 5; ++int1) {
							shader.setLight(int1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
						}

						Vector3f vector3f = tempVector3f;
						vector3f.x = 0.0F;
						vector3f.y = 5.0F;
						vector3f.z = -2.0F;
						vector3f.rotateY(this.itemParams.m_angle.y * 0.017453292F);
						float float1 = 1.5F;
						shader.setLight(4, vector3f.x, vector3f.z, vector3f.y, this.itemParams.m_ambientR / 4.0F * float1, this.itemParams.m_ambientG / 4.0F * float1, this.itemParams.m_ambientB / 4.0F * float1, 5000.0F, Float.NaN, 0.0F, 0.0F, 0.0F, (IsoMovingObject)null);
						if (matrix4f == null) {
							shader.setTransformMatrix(this.itemParams.m_transform, false);
						} else {
							tempMatrix4f_1.set((Matrix4fc)this.itemParams.m_transform);
							tempMatrix4f_1.mul((Matrix4fc)matrix4f);
							shader.setTransformMatrix(tempMatrix4f_1, false);
						}

						model.Mesh.Draw(shader);
						shader.End();
						if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
							Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 0.5F, 1.0F);
						}

						PZGLUtil.popMatrix(5889);
						PZGLUtil.popMatrix(5888);
						return true;
					}
				} else {
					return false;
				}
			}
		}

		void calcMatrices(Matrix4f matrix4f, Matrix4f matrix4f2, float float1, float float2) {
			matrix4f.setOrtho(-0.26666668F, 0.26666668F, 0.26666668F, -0.26666668F, -10.0F, 10.0F);
			matrix4f2.identity();
			float float3 = 0.047085002F;
			matrix4f2.scale(float3 * (float)Core.TileScale / 2.0F);
			boolean boolean1 = true;
			if (boolean1) {
				matrix4f2.rotate(0.5235988F, 1.0F, 0.0F, 0.0F);
				matrix4f2.rotate(2.3561945F, 0.0F, 1.0F, 0.0F);
			} else {
				matrix4f2.rotate(1.5707964F, 0.0F, 1.0F, 0.0F);
			}

			matrix4f2.scale(-3.75F, 3.75F, 3.75F);
			matrix4f2.rotateXYZ(this.itemParams.m_angle.x * 0.017453292F, this.itemParams.m_angle.y * 0.017453292F, this.itemParams.m_angle.z * 0.017453292F);
			matrix4f2.translate(float1, 0.0F, float2);
			matrix4f2.mul((Matrix4fc)this.itemParams.m_transform);
		}

		void calcModelBounds(float[] floatArray) {
			floatArray[0] = Float.MAX_VALUE;
			floatArray[1] = Float.MAX_VALUE;
			floatArray[2] = -3.4028235E38F;
			floatArray[3] = -3.4028235E38F;
			this.calcModelBounds(this.itemParams.m_model, this.m_modelView, floatArray);
			if (this.itemParams.m_weaponParts != null) {
				for (int int1 = 0; int1 < this.itemParams.m_weaponParts.size(); ++int1) {
					WorldItemAtlas.WeaponPartParams weaponPartParams = (WorldItemAtlas.WeaponPartParams)this.itemParams.m_weaponParts.get(int1);
					Matrix4f matrix4f = tempMatrix4f_1.set((Matrix4fc)this.m_modelView).mul((Matrix4fc)weaponPartParams.m_transform);
					this.calcModelBounds(weaponPartParams.m_model, matrix4f, floatArray);
				}
			}

			float float1 = 2.0F;
			floatArray[0] *= float1;
			floatArray[1] *= float1;
			floatArray[2] *= float1;
			floatArray[3] *= float1;
		}

		void calcModelBounds(Model model, Matrix4f matrix4f, float[] floatArray) {
			Vector3f vector3f = model.Mesh.minXYZ;
			Vector3f vector3f2 = model.Mesh.maxXYZ;
			xs[0] = vector3f.x;
			ys[0] = vector3f.y;
			xs[1] = vector3f.x;
			ys[1] = vector3f2.y;
			xs[2] = vector3f2.x;
			ys[2] = vector3f2.y;
			xs[3] = vector3f2.x;
			ys[3] = vector3f.y;
			for (int int1 = 0; int1 < 4; ++int1) {
				this.sceneToUI(xs[int1], ys[int1], vector3f.z, this.m_projection, matrix4f, this.m_scenePos);
				floatArray[0] = PZMath.min(floatArray[0], this.m_scenePos.x);
				floatArray[2] = PZMath.max(floatArray[2], this.m_scenePos.x);
				floatArray[1] = PZMath.min(floatArray[1], this.m_scenePos.y);
				floatArray[3] = PZMath.max(floatArray[3], this.m_scenePos.y);
				this.sceneToUI(xs[int1], ys[int1], vector3f2.z, this.m_projection, matrix4f, this.m_scenePos);
				floatArray[0] = PZMath.min(floatArray[0], this.m_scenePos.x);
				floatArray[2] = PZMath.max(floatArray[2], this.m_scenePos.x);
				floatArray[1] = PZMath.min(floatArray[1], this.m_scenePos.y);
				floatArray[3] = PZMath.max(floatArray[3], this.m_scenePos.y);
			}
		}

		void calcModelOffset() {
			float float1 = this.m_bounds[0];
			float float2 = this.m_bounds[1];
			float float3 = this.m_bounds[2];
			float float4 = this.m_bounds[3];
			this.entry.offsetX = float1 + (float3 - float1) / 2.0F - 512.0F;
			this.entry.offsetY = float2 + (float4 - float2) / 2.0F - 512.0F;
		}

		void calcEntrySize() {
			float float1 = this.m_bounds[0];
			float float2 = this.m_bounds[1];
			float float3 = this.m_bounds[2];
			float float4 = this.m_bounds[3];
			float float5 = 2.0F;
			float1 -= float5;
			float2 -= float5;
			float3 += float5;
			float4 += float5;
			byte byte1 = 16;
			float1 = (float)Math.floor((double)(float1 / (float)byte1)) * (float)byte1;
			float3 = (float)Math.ceil((double)(float3 / (float)byte1)) * (float)byte1;
			float2 = (float)Math.floor((double)(float2 / (float)byte1)) * (float)byte1;
			float4 = (float)Math.ceil((double)(float4 / (float)byte1)) * (float)byte1;
			this.entryW = (int)(float3 - float1);
			this.entryH = (int)(float4 - float2);
		}

		Vector3f sceneToUI(float float1, float float2, float float3, Matrix4f matrix4f, Matrix4f matrix4f2, Vector3f vector3f) {
			Matrix4f matrix4f3 = this.m_matri4f;
			matrix4f3.set((Matrix4fc)matrix4f);
			matrix4f3.mul((Matrix4fc)matrix4f2);
			this.m_viewport[0] = 0;
			this.m_viewport[1] = 0;
			this.m_viewport[2] = 512;
			this.m_viewport[3] = 512;
			matrix4f3.project(float1, float2, float3, this.m_viewport, vector3f);
			return vector3f;
		}

		public void Reset() {
			this.itemParams.Reset();
			this.entry = null;
		}
	}

	private final class Atlas {
		public final int ENTRY_WID;
		public final int ENTRY_HGT;
		public Texture tex;
		public final ArrayList EntryList = new ArrayList();
		public boolean clear = true;

		public Atlas(int int1, int int2, int int3, int int4) {
			this.ENTRY_WID = int3;
			this.ENTRY_HGT = int4;
			this.tex = new Texture(int1, int2, 16);
			if (WorldItemAtlas.this.fbo == null) {
				WorldItemAtlas.this.fbo = new TextureFBO(this.tex, false);
			}
		}

		public boolean isFull() {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.tex.getHeight() / this.ENTRY_HGT;
			return this.EntryList.size() >= int1 * int2;
		}

		public WorldItemAtlas.AtlasEntry addItem(String string) {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.EntryList.size();
			int int3 = int2 % int1;
			int int4 = int2 / int1;
			WorldItemAtlas.AtlasEntry atlasEntry = new WorldItemAtlas.AtlasEntry();
			atlasEntry.atlas = this;
			atlasEntry.key = string;
			atlasEntry.x = int3 * this.ENTRY_WID;
			atlasEntry.y = int4 * this.ENTRY_HGT;
			atlasEntry.w = this.ENTRY_WID;
			atlasEntry.h = this.ENTRY_HGT;
			atlasEntry.tex = this.tex.split(string, atlasEntry.x, this.tex.getHeight() - (atlasEntry.y + this.ENTRY_HGT), atlasEntry.w, atlasEntry.h);
			atlasEntry.tex.setName(string);
			this.EntryList.add(atlasEntry);
			return atlasEntry;
		}

		public void addEntry(WorldItemAtlas.AtlasEntry atlasEntry) {
			int int1 = this.tex.getWidth() / this.ENTRY_WID;
			int int2 = this.EntryList.size();
			int int3 = int2 % int1;
			int int4 = int2 / int1;
			atlasEntry.atlas = this;
			atlasEntry.x = int3 * this.ENTRY_WID;
			atlasEntry.y = int4 * this.ENTRY_HGT;
			atlasEntry.w = this.ENTRY_WID;
			atlasEntry.h = this.ENTRY_HGT;
			atlasEntry.tex = this.tex.split(atlasEntry.key, atlasEntry.x, this.tex.getHeight() - (atlasEntry.y + this.ENTRY_HGT), atlasEntry.w, atlasEntry.h);
			atlasEntry.tex.setName(atlasEntry.key);
			this.EntryList.add(atlasEntry);
		}

		public void Reset() {
			this.EntryList.forEach(WorldItemAtlas.AtlasEntry::Reset);
			this.EntryList.clear();
			if (!this.tex.isDestroyed()) {
				RenderThread.invokeOnRenderContext(()->{
					GL11.glDeleteTextures(this.tex.getID());
				});
			}

			this.tex = null;
		}
	}

	private static final class WeaponPartParams {
		Model m_model;
		String m_attachmentNameSelf;
		String m_attachmentNameParent;
		final Matrix4f m_transform = new Matrix4f();

		WorldItemAtlas.WeaponPartParams init(WorldItemAtlas.WeaponPartParams weaponPartParams) {
			this.m_model = weaponPartParams.m_model;
			this.m_attachmentNameSelf = weaponPartParams.m_attachmentNameSelf;
			this.m_attachmentNameParent = weaponPartParams.m_attachmentNameParent;
			this.m_transform.set((Matrix4fc)weaponPartParams.m_transform);
			return this;
		}

		void initTransform(ModelScript modelScript, ModelScript modelScript2) {
			this.m_transform.identity();
			Matrix4f matrix4f = WorldItemAtlas.s_attachmentXfrm;
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

	private static final class ClearAtlasTexture extends TextureDraw.GenericDrawer {
		WorldItemAtlas.Atlas m_atlas;

		ClearAtlasTexture(WorldItemAtlas.Atlas atlas) {
			this.m_atlas = atlas;
		}

		public void render() {
			TextureFBO textureFBO = WorldItemAtlas.instance.fbo;
			if (textureFBO != null && this.m_atlas.tex != null) {
				if (this.m_atlas.clear) {
					if (textureFBO.getTexture() != this.m_atlas.tex) {
						textureFBO.setTexture(this.m_atlas.tex);
					}

					textureFBO.startDrawing(false, false);
					GL11.glPushAttrib(2048);
					GL11.glViewport(0, 0, textureFBO.getWidth(), textureFBO.getHeight());
					GL11.glMatrixMode(5889);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					int int1 = this.m_atlas.tex.getWidth();
					int int2 = this.m_atlas.tex.getHeight();
					GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
					GL11.glMatrixMode(5888);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					GL11.glDisable(3089);
					GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
					GL11.glClear(16640);
					GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
					textureFBO.endDrawing();
					GL11.glEnable(3089);
					GL11.glMatrixMode(5889);
					GL11.glPopMatrix();
					GL11.glMatrixMode(5888);
					GL11.glPopMatrix();
					GL11.glPopAttrib();
					this.m_atlas.clear = false;
				}
			}
		}
	}

	private static final class ItemTextureDrawer extends TextureDraw.GenericDrawer {
		WorldItemAtlas.ItemTexture itemTexture;
		float x;
		float y;
		float r;
		float g;
		float b;
		float a;

		WorldItemAtlas.ItemTextureDrawer init(WorldItemAtlas.ItemTexture itemTexture, float float1, float float2, float float3, float float4, float float5, float float6) {
			this.itemTexture = itemTexture;
			this.x = float1;
			this.y = float2;
			this.r = float3;
			this.g = float4;
			this.b = float5;
			this.a = float6;
			return this;
		}

		public void render() {
			WorldItemAtlas.AtlasEntry atlasEntry = this.itemTexture.entry;
			if (atlasEntry != null && atlasEntry.ready && atlasEntry.tex.isReady()) {
				int int1 = (int)(this.x - ((float)atlasEntry.w / 2.0F - atlasEntry.offsetX) / 2.5F);
				int int2 = (int)(this.y - ((float)atlasEntry.h / 2.0F - atlasEntry.offsetY) / 2.5F);
				int int3 = (int)((float)atlasEntry.w / 2.5F);
				int int4 = (int)((float)atlasEntry.h / 2.5F);
				atlasEntry.tex.bind();
				GL11.glBegin(7);
				GL11.glColor4f(this.r, this.g, this.b, this.a);
				GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yStart);
				GL11.glVertex2i(int1, int2);
				GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yStart);
				GL11.glVertex2i(int1 + int3, int2);
				GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yEnd);
				GL11.glVertex2i(int1 + int3, int2 + int4);
				GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yEnd);
				GL11.glVertex2i(int1, int2 + int4);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnd();
				SpriteRenderer.ringBuffer.restoreBoundTextures = true;
			}
		}

		public void postRender() {
			this.itemTexture = null;
			WorldItemAtlas.instance.itemTextureDrawerPool.release((Object)this);
		}
	}
}
