package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.SmartTexture;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.util.StringUtils;


public class ModelInstance {
	public static float MODEL_LIGHT_MULT_OUTSIDE = 1.7F;
	public static float MODEL_LIGHT_MULT_ROOM = 1.7F;
	public Model model;
	public AnimationPlayer AnimPlayer;
	public SkinningData data;
	public Texture tex;
	public ModelInstanceTextureInitializer m_textureInitializer;
	public IsoGameCharacter character;
	public IsoMovingObject object;
	public float tintR = 1.0F;
	public float tintG = 1.0F;
	public float tintB = 1.0F;
	public ModelInstance parent;
	public int parentBone;
	public String parentBoneName = null;
	public float hue;
	public float depthBias;
	public ModelInstance matrixModel;
	public SoftwareModelMeshInstance softwareMesh;
	public final ArrayList sub = new ArrayList();
	private int instanceSkip;
	private ItemVisual itemVisual = null;
	public boolean bResetAfterRender = false;
	private Object m_owner = null;
	public int renderRefCount;
	private static final int INITIAL_SKIP_VALUE = Integer.MAX_VALUE;
	private int skipped = Integer.MAX_VALUE;
	public final Object m_lock = "ModelInstance Thread Lock";
	public ModelScript m_modelScript = null;
	public String attachmentNameSelf = null;
	public String attachmentNameParent = null;
	public float scale = 1.0F;
	public String maskVariableValue = null;
	public ModelInstance.PlayerData[] playerData;
	private static final ColorInfo tempColorInfo = new ColorInfo();
	private static final ColorInfo tempColorInfo2 = new ColorInfo();

	public ModelInstance init(Model model, IsoGameCharacter gameCharacter, AnimationPlayer animationPlayer) {
		this.data = (SkinningData)model.Tag;
		this.model = model;
		this.tex = model.tex;
		if (!model.bStatic && animationPlayer == null) {
			animationPlayer = AnimationPlayer.alloc(model);
		}

		this.AnimPlayer = animationPlayer;
		this.character = gameCharacter;
		this.object = gameCharacter;
		return this;
	}

	public boolean isRendering() {
		return this.renderRefCount > 0;
	}

	public void reset() {
		if (this.tex instanceof SmartTexture) {
			Texture texture = this.tex;
			Objects.requireNonNull(texture);
			RenderThread.queueInvokeOnRenderContext(texture::destroy);
		}

		this.AnimPlayer = null;
		this.character = null;
		this.data = null;
		this.hue = 0.0F;
		this.itemVisual = null;
		this.matrixModel = null;
		this.model = null;
		this.object = null;
		this.parent = null;
		this.parentBone = 0;
		this.parentBoneName = null;
		this.skipped = Integer.MAX_VALUE;
		this.sub.clear();
		this.softwareMesh = null;
		this.tex = null;
		if (this.m_textureInitializer != null) {
			this.m_textureInitializer.release();
			this.m_textureInitializer = null;
		}

		this.tintR = 1.0F;
		this.tintG = 1.0F;
		this.tintB = 1.0F;
		this.bResetAfterRender = false;
		this.renderRefCount = 0;
		this.scale = 1.0F;
		this.m_owner = null;
		this.m_modelScript = null;
		this.attachmentNameSelf = null;
		this.attachmentNameParent = null;
		this.maskVariableValue = null;
		if (this.playerData != null) {
			ModelInstance.PlayerData.pool.release((Object[])this.playerData);
			Arrays.fill(this.playerData, (Object)null);
		}
	}

	public void LoadTexture(String string) {
		if (string != null && string.length() != 0) {
			this.tex = Texture.getSharedTexture("media/textures/" + string + ".png");
			if (this.tex == null) {
				if (string.equals("Vest_White")) {
					this.tex = Texture.getSharedTexture("media/textures/Shirt_White.png");
				} else if (string.contains("Hair")) {
					this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
				} else if (string.contains("Beard")) {
					this.tex = Texture.getSharedTexture("media/textures/F_Hair_White.png");
				} else {
					DebugLog.log("ERROR: model texture \"" + string + "\" wasn\'t found");
				}
			}
		} else {
			this.tex = null;
		}
	}

	public void dismember(int int1) {
		this.AnimPlayer.dismember(int1);
	}

	public void UpdateDir() {
		if (this.AnimPlayer != null) {
			this.AnimPlayer.UpdateDir(this.character);
		}
	}

	public void Update() {
		float float1;
		if (this.character != null) {
			float1 = this.character.DistTo(IsoPlayer.getInstance());
			if (!this.character.amputations.isEmpty() && float1 > 0.0F && this.AnimPlayer != null) {
				this.AnimPlayer.dismembered.clear();
				ArrayList arrayList = this.character.amputations;
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					String string = (String)arrayList.get(int1);
					this.AnimPlayer.dismember((Integer)this.AnimPlayer.getSkinningData().BoneIndices.get(string));
				}
			}

			if (Math.abs(this.character.speedMod - 0.5957F) < 1.0E-4F) {
				boolean boolean1 = false;
			}
		}

		this.instanceSkip = 0;
		if (this.AnimPlayer != null) {
			if (this.matrixModel == null) {
				if (this.skipped >= this.instanceSkip) {
					if (this.skipped == Integer.MAX_VALUE) {
						this.skipped = 1;
					}

					float1 = GameTime.instance.getTimeDelta() * (float)this.skipped;
					this.AnimPlayer.Update(float1);
				} else {
					this.AnimPlayer.DoAngles();
				}

				this.AnimPlayer.parentPlayer = null;
			} else {
				this.AnimPlayer.parentPlayer = this.matrixModel.AnimPlayer;
			}
		}

		if (this.skipped >= this.instanceSkip) {
			this.skipped = 0;
		}

		++this.skipped;
	}

	public void SetForceDir(Vector2 vector2) {
		if (this.AnimPlayer != null) {
			this.AnimPlayer.SetForceDir(vector2);
		}
	}

	public void setInstanceSkip(int int1) {
		this.instanceSkip = int1;
		for (int int2 = 0; int2 < this.sub.size(); ++int2) {
			ModelInstance modelInstance = (ModelInstance)this.sub.get(int2);
			modelInstance.instanceSkip = int1;
		}
	}

	public void destroySmartTextures() {
		if (this.tex instanceof SmartTexture) {
			this.tex.destroy();
			this.tex = null;
		}

		for (int int1 = 0; int1 < this.sub.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)this.sub.get(int1);
			modelInstance.destroySmartTextures();
		}
	}

	public void updateLights() {
		int int1 = IsoCamera.frameState.playerIndex;
		if (this.playerData == null) {
			this.playerData = new ModelInstance.PlayerData[4];
		}

		boolean boolean1 = this.playerData[int1] == null;
		if (this.playerData[int1] == null) {
			this.playerData[int1] = (ModelInstance.PlayerData)ModelInstance.PlayerData.pool.alloc();
		}

		this.playerData[int1].updateLights(this.character, boolean1);
	}

	public ItemVisual getItemVisual() {
		return this.itemVisual;
	}

	public void setItemVisual(ItemVisual itemVisual) {
		this.itemVisual = itemVisual;
	}

	public void applyModelScriptScale(String string) {
		this.m_modelScript = ScriptManager.instance.getModelScript(string);
		if (this.m_modelScript != null) {
			this.scale = this.m_modelScript.scale;
		}
	}

	public ModelAttachment getAttachment(int int1) {
		return this.m_modelScript == null ? null : this.m_modelScript.getAttachment(int1);
	}

	public ModelAttachment getAttachmentById(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			return this.m_modelScript == null ? null : this.m_modelScript.getAttachmentById(string);
		}
	}

	public Matrix4f getAttachmentMatrix(ModelAttachment modelAttachment, Matrix4f matrix4f) {
		matrix4f.translation(modelAttachment.getOffset());
		Vector3f vector3f = modelAttachment.getRotate();
		matrix4f.rotateXYZ(vector3f.x * 0.017453292F, vector3f.y * 0.017453292F, vector3f.z * 0.017453292F);
		return matrix4f;
	}

	public Matrix4f getAttachmentMatrix(int int1, Matrix4f matrix4f) {
		ModelAttachment modelAttachment = this.getAttachment(int1);
		return modelAttachment == null ? matrix4f.identity() : this.getAttachmentMatrix(modelAttachment, matrix4f);
	}

	public Matrix4f getAttachmentMatrixById(String string, Matrix4f matrix4f) {
		ModelAttachment modelAttachment = this.getAttachmentById(string);
		return modelAttachment == null ? matrix4f.identity() : this.getAttachmentMatrix(modelAttachment, matrix4f);
	}

	public void setOwner(Object object) {
		Objects.requireNonNull(object);
		assert this.m_owner == null;
		this.m_owner = object;
	}

	public void clearOwner(Object object) {
		Objects.requireNonNull(object);
		assert this.m_owner == object;
		this.m_owner = null;
	}

	public Object getOwner() {
		return this.m_owner;
	}

	public void setTextureInitializer(ModelInstanceTextureInitializer modelInstanceTextureInitializer) {
		this.m_textureInitializer = modelInstanceTextureInitializer;
	}

	public ModelInstanceTextureInitializer getTextureInitializer() {
		return this.m_textureInitializer;
	}

	public boolean hasTextureCreator() {
		return this.m_textureInitializer != null && this.m_textureInitializer.isDirty();
	}

	public static final class PlayerData {
		ModelInstance.FrameLightInfo[] frameLights;
		ArrayList chosenLights;
		Vector3f targetAmbient;
		Vector3f currentAmbient;
		ModelInstance.EffectLight[] effectLightsMain;
		private static final ObjectPool pool = new ObjectPool(ModelInstance.PlayerData::new);

		private void registerFrameLight(IsoGridSquare.ResultLight resultLight) {
			this.chosenLights.add(resultLight);
		}

		private void initFrameLightsForFrame() {
			if (this.frameLights == null) {
				this.effectLightsMain = new ModelInstance.EffectLight[5];
				for (int int1 = 0; int1 < 5; ++int1) {
					this.effectLightsMain[int1] = new ModelInstance.EffectLight();
				}

				this.frameLights = new ModelInstance.FrameLightInfo[5];
				this.chosenLights = new ArrayList();
				this.targetAmbient = new Vector3f();
				this.currentAmbient = new Vector3f();
			}

			ModelInstance.EffectLight[] effectLightArray = this.effectLightsMain;
			int int2 = effectLightArray.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				ModelInstance.EffectLight effectLight = effectLightArray[int3];
				effectLight.radius = -1;
			}

			this.chosenLights.clear();
		}

		private void completeFrameLightsForFrame() {
			int int1;
			for (int1 = 0; int1 < 5; ++int1) {
				if (this.frameLights[int1] != null) {
					this.frameLights[int1].foundThisFrame = false;
				}
			}

			for (int1 = 0; int1 < this.chosenLights.size(); ++int1) {
				IsoGridSquare.ResultLight resultLight = (IsoGridSquare.ResultLight)this.chosenLights.get(int1);
				boolean boolean1 = false;
				int int2 = 0;
				int int3 = 0;
				label80: {
					while (true) {
						if (int3 >= 5) {
							break label80;
						}

						if (this.frameLights[int3] != null && this.frameLights[int3].active) {
							if (resultLight.id != -1) {
								if (resultLight.id == this.frameLights[int3].id) {
									break;
								}
							} else if (this.frameLights[int3].x == resultLight.x && this.frameLights[int3].y == resultLight.y && this.frameLights[int3].z == resultLight.z) {
								break;
							}
						}

						++int3;
					}

					boolean1 = true;
					int2 = int3;
				}

				if (boolean1) {
					this.frameLights[int2].foundThisFrame = true;
					this.frameLights[int2].x = resultLight.x;
					this.frameLights[int2].y = resultLight.y;
					this.frameLights[int2].z = resultLight.z;
					this.frameLights[int2].flags = resultLight.flags;
					this.frameLights[int2].radius = resultLight.radius;
					this.frameLights[int2].targetColor.x = resultLight.r;
					this.frameLights[int2].targetColor.y = resultLight.g;
					this.frameLights[int2].targetColor.z = resultLight.b;
					this.frameLights[int2].Stage = ModelInstance.FrameLightBlendStatus.In;
				} else {
					for (int3 = 0; int3 < 5; ++int3) {
						if (this.frameLights[int3] == null || !this.frameLights[int3].active) {
							if (this.frameLights[int3] == null) {
								this.frameLights[int3] = new ModelInstance.FrameLightInfo();
							}

							this.frameLights[int3].x = resultLight.x;
							this.frameLights[int3].y = resultLight.y;
							this.frameLights[int3].z = resultLight.z;
							this.frameLights[int3].r = resultLight.r;
							this.frameLights[int3].g = resultLight.g;
							this.frameLights[int3].b = resultLight.b;
							this.frameLights[int3].flags = resultLight.flags;
							this.frameLights[int3].radius = resultLight.radius;
							this.frameLights[int3].id = resultLight.id;
							this.frameLights[int3].currentColor.x = 0.0F;
							this.frameLights[int3].currentColor.y = 0.0F;
							this.frameLights[int3].currentColor.z = 0.0F;
							this.frameLights[int3].targetColor.x = resultLight.r;
							this.frameLights[int3].targetColor.y = resultLight.g;
							this.frameLights[int3].targetColor.z = resultLight.b;
							this.frameLights[int3].Stage = ModelInstance.FrameLightBlendStatus.In;
							this.frameLights[int3].active = true;
							this.frameLights[int3].foundThisFrame = true;
							break;
						}
					}
				}
			}

			float float1 = GameTime.getInstance().getMultiplier();
			for (int int4 = 0; int4 < 5; ++int4) {
				ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int4];
				if (frameLightInfo != null && frameLightInfo.active) {
					if (!frameLightInfo.foundThisFrame) {
						frameLightInfo.targetColor.x = 0.0F;
						frameLightInfo.targetColor.y = 0.0F;
						frameLightInfo.targetColor.z = 0.0F;
						frameLightInfo.Stage = ModelInstance.FrameLightBlendStatus.Out;
					}

					frameLightInfo.currentColor.x = this.step(frameLightInfo.currentColor.x, frameLightInfo.targetColor.x, java.lang.Math.signum(frameLightInfo.targetColor.x - frameLightInfo.currentColor.x) / (60.0F * float1));
					frameLightInfo.currentColor.y = this.step(frameLightInfo.currentColor.y, frameLightInfo.targetColor.y, java.lang.Math.signum(frameLightInfo.targetColor.y - frameLightInfo.currentColor.y) / (60.0F * float1));
					frameLightInfo.currentColor.z = this.step(frameLightInfo.currentColor.z, frameLightInfo.targetColor.z, java.lang.Math.signum(frameLightInfo.targetColor.z - frameLightInfo.currentColor.z) / (60.0F * float1));
					if (frameLightInfo.Stage == ModelInstance.FrameLightBlendStatus.Out && frameLightInfo.currentColor.x < 0.01F && frameLightInfo.currentColor.y < 0.01F && frameLightInfo.currentColor.z < 0.01F) {
						frameLightInfo.active = false;
					}
				}
			}
		}

		private void sortLights(IsoGameCharacter gameCharacter) {
			for (int int1 = 0; int1 < this.frameLights.length; ++int1) {
				ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int1];
				if (frameLightInfo != null) {
					if (!frameLightInfo.active) {
						frameLightInfo.distSq = Float.MAX_VALUE;
					} else {
						frameLightInfo.distSq = IsoUtils.DistanceToSquared(gameCharacter.x, gameCharacter.y, gameCharacter.z, (float)frameLightInfo.x + 0.5F, (float)frameLightInfo.y + 0.5F, (float)frameLightInfo.z);
					}
				}
			}

			Arrays.sort(this.frameLights, (var0,gameCharacterx)->{
				boolean int1 = var0 == null || var0.radius == -1 || !var0.active;
				boolean frameLightInfo = gameCharacterx == null || gameCharacterx.radius == -1 || !gameCharacterx.active;
				if (int1 && frameLightInfo) {
					return 0;
				} else if (int1) {
					return 1;
				} else if (frameLightInfo) {
					return -1;
				} else if (var0.Stage.ordinal() < gameCharacterx.Stage.ordinal()) {
					return -1;
				} else {
					return var0.Stage.ordinal() > gameCharacterx.Stage.ordinal() ? 1 : (int)java.lang.Math.signum(var0.distSq - gameCharacterx.distSq);
				}
			});
		}

		private void updateLights(IsoGameCharacter gameCharacter, boolean boolean1) {
			this.initFrameLightsForFrame();
			if (gameCharacter != null) {
				if (gameCharacter.getCurrentSquare() != null) {
					IsoGridSquare.ILighting iLighting = gameCharacter.getCurrentSquare().lighting[IsoCamera.frameState.playerIndex];
					int int1 = Math.min(iLighting.resultLightCount(), 4);
					int int2;
					for (int2 = 0; int2 < int1; ++int2) {
						IsoGridSquare.ResultLight resultLight = iLighting.getResultLight(int2);
						this.registerFrameLight(resultLight);
					}

					if (boolean1) {
						for (int2 = 0; int2 < this.frameLights.length; ++int2) {
							if (this.frameLights[int2] != null) {
								this.frameLights[int2].active = false;
							}
						}
					}

					this.completeFrameLightsForFrame();
					gameCharacter.getCurrentSquare().interpolateLight(ModelInstance.tempColorInfo, gameCharacter.x % 1.0F, gameCharacter.y % 1.0F);
					this.targetAmbient.x = ModelInstance.tempColorInfo.r;
					this.targetAmbient.y = ModelInstance.tempColorInfo.g;
					this.targetAmbient.z = ModelInstance.tempColorInfo.b;
					if (gameCharacter.z - (float)((int)gameCharacter.z) > 0.2F) {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z + 1);
						if (square != null) {
							ColorInfo colorInfo = ModelInstance.tempColorInfo2;
							square.lighting[IsoCamera.frameState.playerIndex].lightInfo();
							square.interpolateLight(colorInfo, gameCharacter.x % 1.0F, gameCharacter.y % 1.0F);
							ModelInstance.tempColorInfo.interp(colorInfo, (gameCharacter.z - ((float)((int)gameCharacter.z) + 0.2F)) / 0.8F, ModelInstance.tempColorInfo);
							this.targetAmbient.set(ModelInstance.tempColorInfo.r, ModelInstance.tempColorInfo.g, ModelInstance.tempColorInfo.b);
						}
					}

					float float1 = GameTime.getInstance().getMultiplier();
					this.currentAmbient.x = this.step(this.currentAmbient.x, this.targetAmbient.x, (this.targetAmbient.x - this.currentAmbient.x) / (10.0F * float1));
					this.currentAmbient.y = this.step(this.currentAmbient.y, this.targetAmbient.y, (this.targetAmbient.y - this.currentAmbient.y) / (10.0F * float1));
					this.currentAmbient.z = this.step(this.currentAmbient.z, this.targetAmbient.z, (this.targetAmbient.z - this.currentAmbient.z) / (10.0F * float1));
					if (boolean1) {
						this.setCurrentToTarget();
					}

					this.sortLights(gameCharacter);
					float float2 = 0.7F;
					for (int int3 = 0; int3 < 5; ++int3) {
						ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int3];
						if (frameLightInfo != null && frameLightInfo.active) {
							ModelInstance.EffectLight effectLight = this.effectLightsMain[int3];
							if ((frameLightInfo.flags & 1) != 0) {
								effectLight.set(gameCharacter.x, gameCharacter.y, (float)((int)gameCharacter.z + 1), frameLightInfo.currentColor.x * float2, frameLightInfo.currentColor.y * float2, frameLightInfo.currentColor.z * float2, frameLightInfo.radius);
							} else if ((frameLightInfo.flags & 2) != 0) {
								if (gameCharacter instanceof IsoPlayer) {
									int int4;
									if (GameClient.bClient) {
										int4 = ((IsoPlayer)gameCharacter).OnlineID + 1;
									} else {
										int4 = ((IsoPlayer)gameCharacter).PlayerIndex + 1;
									}

									int int5 = ((IsoPlayer)gameCharacter).PlayerIndex;
									int int6 = int5 * 4 + 1;
									int int7 = int5 * 4 + 3 + 1;
									if (frameLightInfo.id < int6 || frameLightInfo.id > int7) {
										effectLight.set((float)frameLightInfo.x, (float)frameLightInfo.y, (float)frameLightInfo.z, frameLightInfo.currentColor.x, frameLightInfo.currentColor.y, frameLightInfo.currentColor.z, frameLightInfo.radius);
									}
								} else {
									effectLight.set((float)frameLightInfo.x, (float)frameLightInfo.y, (float)frameLightInfo.z, frameLightInfo.currentColor.x * 2.0F, frameLightInfo.currentColor.y, frameLightInfo.currentColor.z, frameLightInfo.radius);
								}
							} else {
								effectLight.set((float)frameLightInfo.x + 0.5F, (float)frameLightInfo.y + 0.5F, (float)frameLightInfo.z + 0.5F, frameLightInfo.currentColor.x * float2, frameLightInfo.currentColor.y * float2, frameLightInfo.currentColor.z * float2, frameLightInfo.radius);
							}
						}
					}

					if (int1 <= 3 && gameCharacter instanceof IsoPlayer && gameCharacter.getTorchStrength() > 0.0F) {
						this.effectLightsMain[2].set(gameCharacter.x + gameCharacter.getForwardDirection().x * 0.5F, gameCharacter.y + gameCharacter.getForwardDirection().y * 0.5F, gameCharacter.z + 0.25F, 1.0F, 1.0F, 1.0F, 2);
					}

					float float3 = 0.0F;
					float float4 = 1.0F;
					float float5 = this.lerp(float3, float4, this.currentAmbient.x);
					float float6 = this.lerp(float3, float4, this.currentAmbient.y);
					float float7 = this.lerp(float3, float4, this.currentAmbient.z);
					if (gameCharacter.getCurrentSquare().isOutside()) {
						float5 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
						float6 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
						float7 *= ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
						this.effectLightsMain[3].set(gameCharacter.x - 2.0F, gameCharacter.y - 2.0F, gameCharacter.z + 1.0F, float5 / 4.0F, float6 / 4.0F, float7 / 4.0F, 5000);
						this.effectLightsMain[4].set(gameCharacter.x + 2.0F, gameCharacter.y + 2.0F, gameCharacter.z + 1.0F, float5 / 4.0F, float6 / 4.0F, float7 / 4.0F, 5000);
					} else if (gameCharacter.getCurrentSquare().getRoom() != null) {
						float5 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
						float6 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
						float7 *= ModelInstance.MODEL_LIGHT_MULT_ROOM;
						this.effectLightsMain[3].set(gameCharacter.x - 2.0F, gameCharacter.y - 2.0F, gameCharacter.z + 1.0F, float5 / 4.0F, float6 / 4.0F, float7 / 4.0F, 5000);
						this.effectLightsMain[4].set(gameCharacter.x + 2.0F, gameCharacter.y + 2.0F, gameCharacter.z + 1.0F, float5 / 4.0F, float6 / 4.0F, float7 / 4.0F, 5000);
					}
				}
			}
		}

		private float lerp(float float1, float float2, float float3) {
			return float1 + (float2 - float1) * float3;
		}

		private void setCurrentToTarget() {
			for (int int1 = 0; int1 < this.frameLights.length; ++int1) {
				ModelInstance.FrameLightInfo frameLightInfo = this.frameLights[int1];
				if (frameLightInfo != null) {
					frameLightInfo.currentColor.set(frameLightInfo.targetColor);
				}
			}

			this.currentAmbient.set((Vector3fc)this.targetAmbient);
		}

		private float step(float float1, float float2, float float3) {
			if (float1 < float2) {
				return ClimateManager.clamp(0.0F, float2, float1 + float3);
			} else {
				return float1 > float2 ? ClimateManager.clamp(float2, 1.0F, float1 + float3) : float1;
			}
		}
	}

	public static final class FrameLightInfo {
		public ModelInstance.FrameLightBlendStatus Stage;
		public int id;
		public int x;
		public int y;
		public int z;
		public float distSq;
		public int radius;
		public float r;
		public float g;
		public float b;
		public int flags;
		public final org.lwjgl.util.vector.Vector3f currentColor = new org.lwjgl.util.vector.Vector3f();
		public final org.lwjgl.util.vector.Vector3f targetColor = new org.lwjgl.util.vector.Vector3f();
		public boolean active;
		public boolean foundThisFrame;
	}

	public static enum FrameLightBlendStatus {

		In,
		During,
		Out;

		private static ModelInstance.FrameLightBlendStatus[] $values() {
			return new ModelInstance.FrameLightBlendStatus[]{In, During, Out};
		}
	}

	public static final class EffectLight {
		public float x;
		public float y;
		public float z;
		public float r;
		public float g;
		public float b;
		public int radius;

		public void set(float float1, float float2, float float3, float float4, float float5, float float6, int int1) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.r = float4;
			this.g = float5;
			this.b = float6;
			this.radius = int1;
		}
	}
}
