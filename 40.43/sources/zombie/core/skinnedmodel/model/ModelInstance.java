package zombie.core.skinnedmodel.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;


public class ModelInstance {
	public Model model;
	public AnimationPlayer AnimPlayer;
	SkinningData data;
	public Texture tex;
	public Texture textureRust = null;
	public Texture textureMask = null;
	public Texture textureLights = null;
	public Texture textureDamage1Overlay = null;
	public Texture textureDamage1Shell = null;
	public Texture textureDamage2Overlay = null;
	public Texture textureDamage2Shell = null;
	public boolean isVehicleBody = false;
	public boolean isVehicleWheel = false;
	public Matrix4f textureUninstall1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureUninstall2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureLightsEnables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureDamage1Enables1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureDamage1Enables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureDamage2Enables1 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public Matrix4f textureDamage2Enables2 = new Matrix4f(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public float textureRustA = 0.0F;
	public float refWindows = 0.5F;
	public float refBody = 0.4F;
	public float alpha = 0.0F;
	public Vector3f painColor = new Vector3f(0.0F, 0.5F, 0.5F);
	public IsoGameCharacter character;
	public IsoMovingObject object;
	public Vector3f[] origin = new Vector3f[3];
	public Matrix4f[] xfrm = new Matrix4f[3];
	public Vector3f[] worldPos = new Vector3f[3];
	public boolean updateLights;
	public float tintR = 1.0F;
	public float tintG = 1.0F;
	public float tintB = 1.0F;
	String lastAnimName = "";
	OnceEvery lightCheck = new OnceEvery(1.3F, true);
	public IsoLightSource[] lights = new IsoLightSource[3];

	public ModelInstance(Model model, IsoGameCharacter gameCharacter, AnimationPlayer animationPlayer, boolean boolean1, boolean boolean2) {
		this.data = (SkinningData)model.Tag;
		this.model = model;
		this.tex = model.tex;
		if (!model.bStatic) {
			if (animationPlayer == null) {
				animationPlayer = new AnimationPlayer(this.data);
			}

			this.AnimPlayer = animationPlayer;
		}

		this.character = gameCharacter;
		this.object = gameCharacter;
		this.isVehicleBody = boolean1;
		this.isVehicleWheel = boolean2;
	}

	public void LoadTexture(String string) {
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
				boolean boolean1 = false;
			}
		}
	}

	public void Draw() {
		this.model.Draw(this);
	}

	public AnimationTrack Play(String string, boolean boolean1, boolean boolean2, IsoGameCharacter gameCharacter) {
		if (this.model.bStatic) {
			return null;
		} else {
			boolean boolean3 = gameCharacter.legsSprite.CurrentAnim.FinishUnloopedOnFrame == 0;
			if (this.AnimPlayer == null) {
				this.AnimPlayer = new AnimationPlayer((SkinningData)this.model.Tag);
			}

			if (this.data != null) {
				AnimationTrack animationTrack;
				if (string.endsWith("_R")) {
					string = string.substring(0, string.length() - 2);
					animationTrack = this.AnimPlayer.StartClip((AnimationClip)this.data.AnimationClips.get(string), boolean1, boolean2, boolean3);
					if (animationTrack != null) {
						if (!this.lastAnimName.equals(string)) {
							animationTrack.syncToFrame(gameCharacter.def, gameCharacter.legsSprite.CurrentAnim);
						}

						animationTrack.reverse = true;
					}

					this.lastAnimName = string;
					return animationTrack;
				} else {
					animationTrack = this.AnimPlayer.StartClip((AnimationClip)this.data.AnimationClips.get(string), boolean1, boolean2, boolean3);
					if (animationTrack != null) {
						if (!this.lastAnimName.equals(string)) {
							animationTrack.syncToFrame(gameCharacter.def, gameCharacter.legsSprite.CurrentAnim);
						}

						animationTrack.reverse = false;
					}

					this.lastAnimName = string;
					return animationTrack;
				}
			} else {
				return null;
			}
		}
	}

	public void UpdateDir() {
		if (this.AnimPlayer != null) {
			this.SetDir(this.character.angle);
			if (this.character instanceof IsoZombie) {
				if (!this.character.IgnoreMovementForDirection && !((IsoZombie)this.character).bCrawling) {
					if (this.character.reqMovement.getLength() > 0.0F && this.character.getCurrentState() != ZombieStandState.instance()) {
						this.character.DirectionFromVector(this.character.reqMovement);
						this.SetDir(this.character.reqMovement);
					} else if (this.character.getCurrentState() != ZombieStandState.instance()) {
						this.SetDir(this.character.dir.ToVector());
					}
				} else {
					this.SetDir(this.character.dir.ToVector());
				}
			}
		}
	}

	public void Update(float float1) {
		if (this.AnimPlayer != null && !this.AnimPlayer.Tracks.isEmpty()) {
			this.AnimPlayer.Update(float1, true, (Matrix4f)null);
		}
	}

	private void attachToBone(int int1, ModelInstance modelInstance) {
	}

	public void SetDir(Vector2 vector2) {
		ModelCamera.instance.setDir(vector2, this);
	}
}
