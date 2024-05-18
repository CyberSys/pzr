package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Math;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.Util;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderSettings;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.Matrix4;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.shader.ShaderManager;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.iso.sprite.SkyBox;


public class Model {
	public static ArrayList ModelList = new ArrayList();
	public String Name;
	public Matrix4 Transform;
	public ModelMesh Mesh = new ModelMesh();
	public Model Parent;
	public ArrayList Children;
	public Shader Effect;
	public int ID = 0;
	public Object Tag;
	public boolean bStatic = false;
	public Texture tex = null;

	public Model(boolean boolean1) {
		this.Transform = Matrix4.Identity;
		this.Children = new ArrayList();
		this.ID = ModelList.size();
		ModelList.add(this);
		this.bStatic = boolean1;
	}

	public void Draw(ModelInstance modelInstance) {
		float float1;
		if (modelInstance.character == null) {
			GL11.glEnable(2884);
			GL11.glCullFace(1029);
			GL11.glEnable(2929);
			GL11.glDepthFunc(513);
			ModelCamera.instance.BeginVehicle(this, modelInstance);
			int int1 = SpriteRenderer.instance.states[2].index;
			if (this.Effect != null) {
				this.Effect.Start();
				float1 = 1.0F - Math.min(RenderSettings.getInstance().getPlayerSettings(IsoPlayer.getPlayerIndex()).getDarkness() * 0.6F, 0.8F);
				float1 *= 0.9F;
				synchronized (modelInstance.lights) {
					if (modelInstance.lights[0] != null) {
						this.Effect.setLight(0, (float)modelInstance.lights[0].x, (float)modelInstance.lights[0].y, (float)modelInstance.lights[0].z, modelInstance.lights[0].r, modelInstance.lights[0].g, modelInstance.lights[0].b, (float)modelInstance.lights[0].radius, modelInstance);
					} else {
						this.Effect.setLight(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
					}

					if (modelInstance.lights[1] != null) {
						this.Effect.setLight(1, (float)modelInstance.lights[1].x, (float)modelInstance.lights[1].y, (float)modelInstance.lights[1].z, modelInstance.lights[1].r, modelInstance.lights[1].g, modelInstance.lights[1].b, (float)modelInstance.lights[1].radius, modelInstance);
					} else {
						this.Effect.setLight(1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
					}

					if (modelInstance.lights[2] != null) {
						this.Effect.setLight(2, (float)modelInstance.lights[2].x, (float)modelInstance.lights[2].y, (float)modelInstance.lights[2].z, modelInstance.lights[2].r, modelInstance.lights[2].g, modelInstance.lights[2].b, (float)modelInstance.lights[2].radius, modelInstance);
					} else {
						this.Effect.setLight(2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
					}
				}

				if (modelInstance.isVehicleBody) {
					GL13.glActiveTexture(33984);
					this.Effect.setTexture(modelInstance.tex);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33985);
					this.Effect.setTexture(modelInstance.textureRust);
					GL11.glTexEnvi(8960, 8704, 7681);
					this.Effect.setTextureRustA(modelInstance.textureRustA);
					GL13.glActiveTexture(33986);
					this.Effect.setTexture(modelInstance.textureMask);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33987);
					this.Effect.setTexture(modelInstance.textureLights);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33988);
					this.Effect.setTexture(modelInstance.textureDamage1Overlay);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33989);
					this.Effect.setTexture(modelInstance.textureDamage1Shell);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33990);
					this.Effect.setTexture(modelInstance.textureDamage2Overlay);
					GL11.glTexEnvi(8960, 8704, 7681);
					GL13.glActiveTexture(33991);
					this.Effect.setTexture(modelInstance.textureDamage2Shell);
					GL11.glTexEnvi(8960, 8704, 7681);
					if (Core.getInstance().getPerfReflectionsOnLoad()) {
						try {
							GL13.glActiveTexture(33992);
							this.Effect.setTexture((Texture)SkyBox.getInstance().getTextureCurrent());
							GL11.glTexEnvi(8960, 8704, 7681);
							GL13.glActiveTexture(33993);
							this.Effect.setTexture((Texture)SkyBox.getInstance().getTexturePrev());
							GL11.glTexEnvi(8960, 8704, 7681);
							Util.checkGLError();
						} catch (Throwable throwable) {
						}

						this.Effect.setReflectionParam(SkyBox.getInstance().getTextureShift(), modelInstance.refWindows, modelInstance.refBody);
					}

					this.Effect.setTextureUninstall1(modelInstance.textureUninstall1);
					this.Effect.setTextureUninstall2(modelInstance.textureUninstall2);
					this.Effect.setTextureLightsEnables2(modelInstance.textureLightsEnables2);
					this.Effect.setTextureDamage1Enables1(modelInstance.textureDamage1Enables1);
					this.Effect.setTextureDamage1Enables2(modelInstance.textureDamage1Enables2);
					this.Effect.setTextureDamage2Enables1(modelInstance.textureDamage2Enables1);
					this.Effect.setTextureDamage2Enables2(modelInstance.textureDamage2Enables2);
					this.Effect.setTexturePainColor(modelInstance.painColor, 1.0F);
				} else if (modelInstance.isVehicleWheel) {
					this.Effect.setShaderAlpha(1.0F);
					this.Effect.setTexture(modelInstance.tex);
				} else {
					this.Effect.setTexture(modelInstance.tex);
				}

				this.Effect.setAmbient(float1);
				this.Effect.setTint(modelInstance.tintR, modelInstance.tintG, modelInstance.tintB);
				if (this.bStatic) {
					this.Effect.setTransformMatrix(modelInstance.xfrm[int1]);
				}

				this.Mesh.Draw(this.Effect);
				this.Effect.End();
			}

			if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue() && modelInstance.isVehicleBody) {
				for (int int2 = 0; int2 < 8; ++int2) {
					GL13.glActiveTexture('蓀' + int2);
					GL11.glDisable(3553);
				}

				GL11.glDisable(2929);
				GL11.glLineWidth(4.0F);
				GL11.glBegin(1);
				GL11.glColor3f(1.0F, 0.0F, 0.0F);
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glVertex3f(1.0F, 0.0F, 0.0F);
				GL11.glColor3f(0.0F, 1.0F, 0.0F);
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glVertex3f(0.0F, 1.0F, 0.0F);
				GL11.glColor3f(0.0F, 0.0F, 1.0F);
				GL11.glVertex3f(0.0F, 0.0F, 0.0F);
				GL11.glVertex3f(0.0F, 0.0F, 1.0F);
				GL11.glEnd();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				GL11.glEnable(2929);
				GL13.glActiveTexture(33984);
				GL11.glEnable(3553);
			}

			ModelCamera.instance.End();
		} else if (modelInstance.AnimPlayer != null) {
			if (modelInstance.character.getCurrentSquare() != null) {
				synchronized (modelInstance.lights) {
					GL11.glEnable(2884);
					GL11.glCullFace(1029);
					GL11.glEnable(2929);
					GL11.glDepthFunc(513);
					ModelCamera.instance.Begin(this);
					if (this.Effect != null) {
						this.Effect.Start();
						this.Effect.setMatrixPalette(modelInstance.AnimPlayer.skinTransforms);
						float1 = 1.0F;
						if (!(modelInstance.character instanceof DeadBodyAtlas.AtlasCharacter)) {
							float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.instance.PlayerIndex);
							if (modelInstance.character.getCurrentSquare() != null && modelInstance.character.getCurrentSquare().getRoom() != null) {
								float1 *= 0.6F;
							}
						}

						if (modelInstance.lights[0] != null) {
							this.Effect.setLight(0, (float)modelInstance.lights[0].x, (float)modelInstance.lights[0].y, (float)modelInstance.lights[0].z, modelInstance.lights[0].r, modelInstance.lights[0].g, modelInstance.lights[0].b, (float)modelInstance.lights[0].radius, modelInstance);
						} else {
							this.Effect.setLight(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
						}

						if (IsoPlayer.instance.getTorchStrength() > 0.0F) {
							float float2 = IsoPlayer.instance.getX();
							float float3 = IsoPlayer.instance.getY();
							float float4 = IsoPlayer.instance.getZ();
							float2 += IsoPlayer.instance.angle.x * 0.5F;
							float3 += IsoPlayer.instance.angle.y * 0.5F;
							float2 -= 0.5F;
							float3 -= 0.5F;
							float4 -= 0.5F;
							this.Effect.setLight(1, float2, float3, float4, 1.0F, 0.85F, 0.6F, 16.0F, modelInstance);
						} else if (modelInstance.lights[1] != null) {
							this.Effect.setLight(1, (float)modelInstance.lights[1].x, (float)modelInstance.lights[1].y, (float)modelInstance.lights[1].z, modelInstance.lights[1].r, modelInstance.lights[1].g, modelInstance.lights[1].b, (float)modelInstance.lights[1].radius, modelInstance);
						} else {
							this.Effect.setLight(1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
						}

						if (modelInstance.lights[2] != null) {
							this.Effect.setLight(2, (float)modelInstance.lights[2].x, (float)modelInstance.lights[2].y, (float)modelInstance.lights[2].z, modelInstance.lights[2].r, modelInstance.lights[2].g, modelInstance.lights[2].b, (float)modelInstance.lights[2].radius, modelInstance);
						} else {
							this.Effect.setLight(2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modelInstance);
						}

						this.Effect.setTexture(modelInstance.tex);
						this.Effect.setAmbient(float1);
					}

					this.Effect.setTint(modelInstance.tintR, modelInstance.tintG, modelInstance.tintB);
					if (this.bStatic) {
						this.Effect.setTexture(this.tex);
						this.Effect.setTransformMatrix(modelInstance.AnimPlayer.GetPropBoneMatrix());
					}

					this.Mesh.Draw(this.Effect);
					if (this.Effect != null) {
						this.Effect.End();
					}

					if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
						GL11.glLineWidth(4.0F);
						GL11.glDisable(3553);
						GL11.glDisable(2929);
						GL11.glBegin(1);
						GL11.glColor3f(1.0F, 0.0F, 0.0F);
						GL11.glVertex3f(0.0F, 0.0F, 0.0F);
						GL11.glVertex3f(1.0F, 0.0F, 0.0F);
						GL11.glColor3f(0.0F, 1.0F, 0.0F);
						GL11.glVertex3f(0.0F, 0.0F, 0.0F);
						GL11.glVertex3f(0.0F, 1.0F, 0.0F);
						GL11.glColor3f(0.0F, 0.0F, 1.0F);
						GL11.glVertex3f(0.0F, 0.0F, 0.0F);
						GL11.glVertex3f(0.0F, 0.0F, 1.0F);
						GL11.glEnd();
						GL11.glColor3f(1.0F, 1.0F, 1.0F);
						GL11.glEnable(2929);
						GL11.glLineWidth(1.0F);
					}

					if (Core.bDebug) {
					}

					ModelCamera.instance.End();
				}
			}
		}
	}

	public void CreateShader(String string) {
		this.Effect = ShaderManager.instance.getOrCreateShader(string, this.bStatic);
	}
}
