package zombie.core.skinnedmodel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.Util;
import zombie.DebugFileWatcher;
import zombie.GameWindow;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedModels;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.SoftwareSkinnedModelAnim;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceTextureInitializer;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.FireShader;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.ParticlesFire;
import zombie.iso.PlayerCamera;
import zombie.iso.PuddlesShader;
import zombie.iso.SmokeShader;
import zombie.iso.Vector2;
import zombie.iso.WaterShader;
import zombie.iso.sprite.SkyBox;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.AnimationsMesh;
import zombie.scripting.objects.ItemReplacement;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;


public final class ModelManager {
	public static boolean NoOpenGL = false;
	public static final ModelManager instance = new ModelManager();
	private final HashMap m_modelMap = new HashMap();
	public Model m_maleModel;
	public Model m_femaleModel;
	public Model m_skeletonMaleModel;
	public Model m_skeletonFemaleModel;
	public TextureFBO bitmap;
	private boolean m_bCreated = false;
	public boolean bDebugEnableModels = true;
	public boolean bCreateSoftwareMeshes = false;
	public final HashMap SoftwareMeshAnims = new HashMap();
	private final ArrayList m_modelSlots = new ArrayList();
	private final ObjectPool m_modelInstancePool = new ObjectPool(ModelInstance::new);
	private final ArrayList m_tempWeaponPartList = new ArrayList();
	private ModelMesh m_animModel;
	private final HashMap m_animationAssets = new HashMap();
	private final ModelManager.ModAnimations m_gameAnimations = new ModelManager.ModAnimations("game");
	private final HashMap m_modAnimations = new HashMap();
	private final ArrayList m_cachedAnims = new ArrayList();
	private final HashSet m_contains = new HashSet();
	private final ArrayList m_torches = new ArrayList();
	private final Stack m_freeLights = new Stack();
	private final ArrayList m_torchLights = new ArrayList();
	private final ArrayList ToRemove = new ArrayList();
	private final ArrayList ToResetNextFrame = new ArrayList();
	private final ArrayList ToResetEquippedNextFrame = new ArrayList();
	private final ArrayList m_resetAfterRender = new ArrayList();
	private final Stack m_lights = new Stack();
	private final Stack m_lightsTemp = new Stack();
	private final Vector2 m_tempVec2 = new Vector2();
	private final Vector2 m_tempVec2_2 = new Vector2();
	private static final TreeMap modelMetaData;
	static String basicEffect;
	static String isStaticTrue;
	static String shaderEquals;
	static String texA;
	static String amp;
	static HashMap toLower;
	static HashMap toLowerTex;
	static HashMap toLowerKeyRoot;
	static StringBuilder builder;

	public boolean isCreated() {
		return this.m_bCreated;
	}

	public void create() {
		if (!this.m_bCreated) {
			if (!GameServer.bServer || ServerGUI.isCreated()) {
				Texture texture = new Texture(1024, 1024, 16);
				PerformanceSettings.UseFBOs = false;
				try {
					this.bitmap = new TextureFBO(texture, false);
				} catch (Exception exception) {
					exception.printStackTrace();
					PerformanceSettings.UseFBOs = false;
					DebugLog.Animation.error("FBO not compatible with gfx card at this time.");
					return;
				}
			}

			DebugLog.Animation.println("Loading 3D models");
			this.initAnimationMeshes(false);
			this.m_modAnimations.put(this.m_gameAnimations.m_modID, this.m_gameAnimations);
			AnimationsMesh animationsMesh = ScriptManager.instance.getAnimationsMesh("Human");
			ModelMesh modelMesh = animationsMesh.modelMesh;
			if (!NoOpenGL && this.bCreateSoftwareMeshes) {
				SoftwareSkinnedModelAnim softwareSkinnedModelAnim = new SoftwareSkinnedModelAnim((StaticAnimation[])this.m_cachedAnims.toArray(new StaticAnimation[0]), modelMesh.softwareMesh, modelMesh.skinningData);
				this.SoftwareMeshAnims.put(modelMesh.getPath().getPath(), softwareSkinnedModelAnim);
			}

			Model model = this.loadModel("skinned/malebody", (String)null, modelMesh);
			Model model2 = this.loadModel("skinned/femalebody", (String)null, modelMesh);
			Model model3 = this.loadModel("skinned/Male_Skeleton", (String)null, modelMesh);
			Model model4 = this.loadModel("skinned/Female_Skeleton", (String)null, modelMesh);
			this.m_animModel = modelMesh;
			this.loadModAnimations();
			model.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
			model.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
			model.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
			model2.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
			model2.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
			model2.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
			this.m_maleModel = model;
			this.m_femaleModel = model2;
			this.m_skeletonMaleModel = model3;
			this.m_skeletonFemaleModel = model4;
			this.m_bCreated = true;
			AdvancedAnimator.systemInit();
			PopTemplateManager.instance.init();
		}
	}

	public void loadAdditionalModel(String string, String string2, boolean boolean1, String string3) {
		boolean boolean2 = this.bCreateSoftwareMeshes;
		if (DebugLog.isEnabled(DebugType.Animation)) {
			DebugLog.Animation.debugln("createSoftwareMesh: %B, model: %s", boolean2, string);
		}

		Model model = this.loadModelInternal(string, string2, string3, this.m_animModel, boolean1);
		if (boolean2) {
			SoftwareSkinnedModelAnim softwareSkinnedModelAnim = new SoftwareSkinnedModelAnim((StaticAnimation[])this.m_cachedAnims.toArray(new StaticAnimation[0]), model.softwareMesh, (SkinningData)model.Tag);
			this.SoftwareMeshAnims.put(string.toLowerCase(), softwareSkinnedModelAnim);
		}
	}

	public ModelInstance newAdditionalModelInstance(String string, String string2, IsoGameCharacter gameCharacter, AnimationPlayer animationPlayer, String string3) {
		Model model = this.tryGetLoadedModel(string, string2, false, string3, false);
		if (model == null) {
			boolean boolean1 = false;
			instance.loadAdditionalModel(string, string2, boolean1, string3);
		}

		model = this.getLoadedModel(string, string2, false, string3);
		return this.newInstance(model, gameCharacter, animationPlayer);
	}

	private void loadAnimsFromDir(String string, ModelMesh modelMesh) {
		File file = new File(ZomboidFileSystem.instance.base, string);
		this.loadAnimsFromDir(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaRootFile().toURI(), file, modelMesh, this.m_gameAnimations);
	}

	private void loadAnimsFromDir(URI uRI, URI uRI2, File file, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
		if (!file.exists()) {
			DebugLog.General.error("ERROR: %s", file.getPath());
			for (File file2 = file.getParentFile(); file2 != null; file2 = file2.getParentFile()) {
				DebugLog.General.error(" - Parent exists: %B, %s", file2.exists(), file2.getPath());
			}
		}

		if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			if (fileArray != null) {
				boolean boolean1 = false;
				File[] fileArray2 = fileArray;
				int int1 = fileArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					File file3 = fileArray2[int2];
					if (file3.isDirectory()) {
						this.loadAnimsFromDir(uRI, uRI2, file3, modelMesh, modAnimations);
					} else {
						String string = ZomboidFileSystem.instance.getAnimName(uRI2, file3);
						this.loadAnim(string, modelMesh, modAnimations);
						boolean1 = true;
						if (!NoOpenGL && RenderThread.RenderThread == null) {
							Display.processMessages();
						}
					}
				}

				if (boolean1) {
					DebugFileWatcher.instance.add((new ModelManager.AnimDirReloader(uRI, uRI2, file.getPath(), modelMesh, modAnimations)).GetFileWatcher());
				}
			}
		}
	}

	public void RenderSkyBox(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		int int5 = TextureFBO.getCurrentID();
		switch (int3) {
		case 1: 
			GL30.glBindFramebuffer(36160, int4);
			break;
		
		case 2: 
			ARBFramebufferObject.glBindFramebuffer(36160, int4);
			break;
		
		case 3: 
			EXTFramebufferObject.glBindFramebufferEXT(36160, int4);
		
		}
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, 1.0, 1.0, 0.0, -1.0, 1.0);
		GL11.glViewport(0, 0, 512, 512);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ARBShaderObjects.glUseProgramObjectARB(int1);
		if (Shader.ShaderMap.containsKey(int1)) {
			((Shader)Shader.ShaderMap.get(int1)).startRenderThread(textureDraw);
		}

		GL11.glColor4f(0.13F, 0.96F, 0.13F, 1.0F);
		GL11.glBegin(7);
		GL11.glTexCoord2f(0.0F, 1.0F);
		GL11.glVertex2f(0.0F, 0.0F);
		GL11.glTexCoord2f(1.0F, 1.0F);
		GL11.glVertex2f(0.0F, 1.0F);
		GL11.glTexCoord2f(1.0F, 0.0F);
		GL11.glVertex2f(1.0F, 1.0F);
		GL11.glTexCoord2f(0.0F, 0.0F);
		GL11.glVertex2f(1.0F, 0.0F);
		GL11.glEnd();
		ARBShaderObjects.glUseProgramObjectARB(0);
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int2);
		GL11.glViewport(0, 0, playerCamera.OffscreenWidth, playerCamera.OffscreenHeight);
		switch (int3) {
		case 1: 
			GL30.glBindFramebuffer(36160, int5);
			break;
		
		case 2: 
			ARBFramebufferObject.glBindFramebuffer(36160, int5);
			break;
		
		case 3: 
			EXTFramebufferObject.glBindFramebufferEXT(36160, int5);
		
		}
		SkyBox.getInstance().swapTextureFBO();
	}

	public void RenderWater(TextureDraw textureDraw, int int1, int int2, boolean boolean1) {
		try {
			Util.checkGLError();
		} catch (Throwable throwable) {
		}

		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		IsoWater.getInstance().waterProjection();
		PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int2);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ARBShaderObjects.glUseProgramObjectARB(int1);
		Shader shader = (Shader)Shader.ShaderMap.get(int1);
		if (shader instanceof WaterShader) {
			((WaterShader)shader).updateWaterParams(textureDraw, int2);
		}

		IsoWater.getInstance().waterGeometry(boolean1);
		ARBShaderObjects.glUseProgramObjectARB(0);
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		if (!PZGLUtil.checkGLError(true)) {
			DebugLog.General.println("DEBUG: EXCEPTION RenderWater");
			PZGLUtil.printGLState(DebugLog.General);
		}
	}

	public void RenderPuddles(int int1, int int2, int int3) {
		PZGLUtil.checkGLError(true);
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		IsoPuddles.getInstance().puddlesProjection();
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ARBShaderObjects.glUseProgramObjectARB(int1);
		Shader shader = (Shader)Shader.ShaderMap.get(int1);
		if (shader instanceof PuddlesShader) {
			((PuddlesShader)shader).updatePuddlesParams(int2, int3);
		}

		IsoPuddles.getInstance().puddlesGeometry(int3);
		ARBShaderObjects.glUseProgramObjectARB(0);
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		if (!PZGLUtil.checkGLError(true)) {
			DebugLog.General.println("DEBUG: EXCEPTION RenderPuddles");
			PZGLUtil.printGLState(DebugLog.General);
		}
	}

	public void RenderParticles(TextureDraw textureDraw, int int1, int int2) {
		int int3 = ParticlesFire.getInstance().getFireShaderID();
		int int4 = ParticlesFire.getInstance().getSmokeShaderID();
		int int5 = ParticlesFire.getInstance().getVapeShaderID();
		try {
			Util.checkGLError();
		} catch (Throwable throwable) {
		}

		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, SpriteRenderer.instance.getRenderingPlayerCamera(int1).OffscreenWidth, SpriteRenderer.instance.getRenderingPlayerCamera(int1).OffscreenHeight);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		float float1 = ParticlesFire.getInstance().getShaderTime();
		GL11.glBlendFunc(770, 1);
		ARBShaderObjects.glUseProgramObjectARB(int3);
		Shader shader = (Shader)Shader.ShaderMap.get(int3);
		if (shader instanceof FireShader) {
			((FireShader)shader).updateFireParams(textureDraw, int1, float1);
		}

		ParticlesFire.getInstance().getGeometryFire(int2);
		GL11.glBlendFunc(770, 771);
		ARBShaderObjects.glUseProgramObjectARB(int4);
		shader = (Shader)Shader.ShaderMap.get(int4);
		if (shader instanceof SmokeShader) {
			((SmokeShader)shader).updateSmokeParams(textureDraw, int1, float1);
		}

		ParticlesFire.getInstance().getGeometry(int2);
		ARBShaderObjects.glUseProgramObjectARB(0);
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		GL11.glPopClientAttrib();
		Texture.lastTextureID = -1;
		GL11.glViewport(0, 0, SpriteRenderer.instance.getRenderingPlayerCamera(int1).OffscreenWidth, SpriteRenderer.instance.getRenderingPlayerCamera(int1).OffscreenHeight);
		if (!PZGLUtil.checkGLError(true)) {
			DebugLog.General.println("DEBUG: EXCEPTION RenderParticles");
			PZGLUtil.printGLState(DebugLog.General);
		}
	}

	public void Reset(IsoGameCharacter gameCharacter) {
		if (gameCharacter.legsSprite != null && gameCharacter.legsSprite.modelSlot != null) {
			ModelManager.ModelSlot modelSlot = gameCharacter.legsSprite.modelSlot;
			this.resetModelInstance(modelSlot.model, modelSlot);
			for (int int1 = 0; int1 < modelSlot.sub.size(); ++int1) {
				ModelInstance modelInstance = (ModelInstance)modelSlot.sub.get(int1);
				if (modelInstance != gameCharacter.primaryHandModel && modelInstance != gameCharacter.secondaryHandModel && !modelSlot.attachedModels.contains(modelInstance)) {
					this.resetModelInstanceRecurse(modelInstance, modelSlot);
				}
			}

			this.derefModelInstances(gameCharacter.getReadyModelData());
			gameCharacter.getReadyModelData().clear();
			this.dressInRandomOutfit(gameCharacter);
			Model model = this.getBodyModel(gameCharacter);
			modelSlot.model = this.newInstance(model, gameCharacter, gameCharacter.getAnimationPlayer());
			modelSlot.model.setOwner(modelSlot);
			modelSlot.model.m_modelScript = ScriptManager.instance.getModelScript(gameCharacter.isFemale() ? "FemaleBody" : "MaleBody");
			this.DoCharacterModelParts(gameCharacter, modelSlot);
		}
	}

	public void reloadAllOutfits() {
		Iterator iterator = this.m_contains.iterator();
		while (iterator.hasNext()) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)iterator.next();
			gameCharacter.reloadOutfit();
		}
	}

	public void Add(IsoGameCharacter gameCharacter) {
		if (this.m_bCreated) {
			if (gameCharacter.isSceneCulled()) {
				if (this.ToRemove.contains(gameCharacter)) {
					this.ToRemove.remove(gameCharacter);
					gameCharacter.legsSprite.modelSlot.bRemove = false;
				} else {
					ModelManager.ModelSlot modelSlot = this.getSlot(gameCharacter);
					modelSlot.framesSinceStart = 0;
					if (modelSlot.model != null) {
						ModelInstance modelInstance = modelSlot.model;
						Objects.requireNonNull(modelInstance);
						RenderThread.invokeOnRenderContext(modelInstance::destroySmartTextures);
					}

					this.dressInRandomOutfit(gameCharacter);
					Model model = this.getBodyModel(gameCharacter);
					modelSlot.model = this.newInstance(model, gameCharacter, gameCharacter.getAnimationPlayer());
					modelSlot.model.setOwner(modelSlot);
					modelSlot.model.m_modelScript = ScriptManager.instance.getModelScript(gameCharacter.isFemale() ? "FemaleBody" : "MaleBody");
					this.DoCharacterModelParts(gameCharacter, modelSlot);
					modelSlot.active = true;
					modelSlot.character = gameCharacter;
					modelSlot.model.character = gameCharacter;
					modelSlot.model.object = gameCharacter;
					modelSlot.model.SetForceDir(modelSlot.model.character.getForwardDirection());
					for (int int1 = 0; int1 < modelSlot.sub.size(); ++int1) {
						ModelInstance modelInstance2 = (ModelInstance)modelSlot.sub.get(int1);
						modelInstance2.character = gameCharacter;
						modelInstance2.object = gameCharacter;
					}

					gameCharacter.legsSprite.modelSlot = modelSlot;
					this.m_contains.add(gameCharacter);
					gameCharacter.onCullStateChanged(this, false);
					if (modelSlot.model.AnimPlayer != null && modelSlot.model.AnimPlayer.isBoneTransformsNeedFirstFrame()) {
						try {
							modelSlot.Update();
						} catch (Throwable throwable) {
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	public void dressInRandomOutfit(IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null && !zombie.isReanimatedPlayer() && !zombie.wasFakeDead()) {
			if (DebugOptions.instance.ZombieOutfitRandom.getValue() && !gameCharacter.isPersistentOutfitInit()) {
				zombie.bDressInRandomOutfit = true;
			}

			if (zombie.bDressInRandomOutfit) {
				zombie.bDressInRandomOutfit = false;
				zombie.dressInRandomOutfit();
			}

			if (!gameCharacter.isPersistentOutfitInit()) {
				zombie.dressInPersistentOutfitID(gameCharacter.getPersistentOutfitID());
			}
		} else {
			if (GameClient.bClient && zombie != null && !gameCharacter.isPersistentOutfitInit() && gameCharacter.getPersistentOutfitID() != 0) {
				zombie.dressInPersistentOutfitID(gameCharacter.getPersistentOutfitID());
			}
		}
	}

	public Model getBodyModel(IsoGameCharacter gameCharacter) {
		if (gameCharacter.isZombie() && ((IsoZombie)gameCharacter).isSkeleton()) {
			return gameCharacter.isFemale() ? this.m_skeletonFemaleModel : this.m_skeletonMaleModel;
		} else {
			return gameCharacter.isFemale() ? this.m_femaleModel : this.m_maleModel;
		}
	}

	public boolean ContainsChar(IsoGameCharacter gameCharacter) {
		return this.m_contains.contains(gameCharacter) && !this.ToRemove.contains(gameCharacter);
	}

	public void ResetCharacterEquippedHands(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.legsSprite != null && gameCharacter.legsSprite.modelSlot != null) {
			this.DoCharacterModelEquipped(gameCharacter, gameCharacter.legsSprite.modelSlot);
		}
	}

	private void DoCharacterModelEquipped(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot) {
		if (gameCharacter.primaryHandModel != null) {
			gameCharacter.clearVariable("RightHandMask");
			gameCharacter.primaryHandModel.maskVariableValue = null;
			this.resetModelInstanceRecurse(gameCharacter.primaryHandModel, modelSlot);
			modelSlot.sub.remove(gameCharacter.primaryHandModel);
			modelSlot.model.sub.remove(gameCharacter.primaryHandModel);
			gameCharacter.primaryHandModel = null;
		}

		if (gameCharacter.secondaryHandModel != null) {
			gameCharacter.clearVariable("LeftHandMask");
			gameCharacter.secondaryHandModel.maskVariableValue = null;
			this.resetModelInstanceRecurse(gameCharacter.secondaryHandModel, modelSlot);
			modelSlot.sub.remove(gameCharacter.secondaryHandModel);
			modelSlot.model.sub.remove(gameCharacter.secondaryHandModel);
			gameCharacter.secondaryHandModel = null;
		}

		int int1;
		for (int1 = 0; int1 < modelSlot.attachedModels.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)modelSlot.attachedModels.get(int1);
			this.resetModelInstanceRecurse(modelInstance, modelSlot);
			modelSlot.sub.remove(modelInstance);
			modelSlot.model.sub.remove(modelInstance);
		}

		modelSlot.attachedModels.clear();
		for (int1 = 0; int1 < gameCharacter.getAttachedItems().size(); ++int1) {
			AttachedItem attachedItem = gameCharacter.getAttachedItems().get(int1);
			String string = attachedItem.getItem().getStaticModel();
			if (!StringUtils.isNullOrWhitespace(string)) {
				String string2 = gameCharacter.getAttachedItems().getGroup().getLocation(attachedItem.getLocation()).getAttachmentName();
				ModelInstance modelInstance2 = this.addStatic(modelSlot.model, string, string2, string2);
				if (modelInstance2 != null) {
					modelInstance2.setOwner(modelSlot);
					modelSlot.sub.add(modelInstance2);
					HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(attachedItem.getItem(), HandWeapon.class);
					if (handWeapon != null) {
						this.addWeaponPartModels(modelSlot, handWeapon, modelInstance2);
						if (!Core.getInstance().getOptionSimpleWeaponTextures()) {
							ModelInstanceTextureInitializer modelInstanceTextureInitializer = ModelInstanceTextureInitializer.alloc();
							modelInstanceTextureInitializer.init(modelInstance2, handWeapon);
							modelInstance2.setTextureInitializer(modelInstanceTextureInitializer);
						}
					}

					modelSlot.attachedModels.add(modelInstance2);
				}
			}
		}

		if (gameCharacter instanceof IsoZombie) {
		}

		InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
		InventoryItem inventoryItem2 = gameCharacter.getSecondaryHandItem();
		if (gameCharacter.isHideWeaponModel()) {
			inventoryItem = null;
			inventoryItem2 = null;
		}

		if (gameCharacter instanceof IsoPlayer && gameCharacter.forceNullOverride) {
			inventoryItem = null;
			inventoryItem2 = null;
			gameCharacter.forceNullOverride = false;
		}

		boolean boolean1 = false;
		BaseAction baseAction = gameCharacter.getCharacterActions().isEmpty() ? null : (BaseAction)gameCharacter.getCharacterActions().get(0);
		if (baseAction != null && baseAction.overrideHandModels) {
			boolean1 = true;
			inventoryItem = null;
			if (baseAction.getPrimaryHandItem() != null) {
				inventoryItem = baseAction.getPrimaryHandItem();
			} else if (baseAction.getPrimaryHandMdl() != null) {
				gameCharacter.primaryHandModel = this.addStatic(modelSlot, baseAction.getPrimaryHandMdl(), "Bip01_Prop1");
			}

			inventoryItem2 = null;
			if (baseAction.getSecondaryHandItem() != null) {
				inventoryItem2 = baseAction.getSecondaryHandItem();
			} else if (baseAction.getSecondaryHandMdl() != null) {
				gameCharacter.secondaryHandModel = this.addStatic(modelSlot, baseAction.getSecondaryHandMdl(), "Bip01_Prop2");
			}
		}

		if (!StringUtils.isNullOrEmpty(gameCharacter.overridePrimaryHandModel)) {
			boolean1 = true;
			gameCharacter.primaryHandModel = this.addStatic(modelSlot, gameCharacter.overridePrimaryHandModel, "Bip01_Prop1");
		}

		if (!StringUtils.isNullOrEmpty(gameCharacter.overrideSecondaryHandModel)) {
			boolean1 = true;
			gameCharacter.secondaryHandModel = this.addStatic(modelSlot, gameCharacter.overrideSecondaryHandModel, "Bip01_Prop2");
		}

		ItemReplacement itemReplacement;
		if (inventoryItem != null) {
			itemReplacement = inventoryItem.getItemReplacementPrimaryHand();
			gameCharacter.primaryHandModel = this.addEquippedModelInstance(gameCharacter, modelSlot, inventoryItem, "Bip01_Prop1", itemReplacement, boolean1);
		}

		if (inventoryItem2 != null && inventoryItem != inventoryItem2) {
			itemReplacement = inventoryItem2.getItemReplacementSecondHand();
			gameCharacter.secondaryHandModel = this.addEquippedModelInstance(gameCharacter, modelSlot, inventoryItem2, "Bip01_Prop2", itemReplacement, boolean1);
		}
	}

	private ModelInstance addEquippedModelInstance(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot, InventoryItem inventoryItem, String string, ItemReplacement itemReplacement, boolean boolean1) {
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(inventoryItem, HandWeapon.class);
		ModelInstance modelInstance;
		if (handWeapon != null) {
			String string2 = handWeapon.getStaticModel();
			modelInstance = this.addStatic(modelSlot, string2, string);
			this.addWeaponPartModels(modelSlot, handWeapon, modelInstance);
			if (Core.getInstance().getOptionSimpleWeaponTextures()) {
				return modelInstance;
			} else {
				ModelInstanceTextureInitializer modelInstanceTextureInitializer = ModelInstanceTextureInitializer.alloc();
				modelInstanceTextureInitializer.init(modelInstance, handWeapon);
				modelInstance.setTextureInitializer(modelInstanceTextureInitializer);
				return modelInstance;
			}
		} else {
			if (inventoryItem != null) {
				if (itemReplacement != null && !StringUtils.isNullOrEmpty(itemReplacement.maskVariableValue) && (itemReplacement.clothingItem != null || !StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel()))) {
					modelInstance = this.addMaskingModel(modelSlot, gameCharacter, inventoryItem, itemReplacement, itemReplacement.maskVariableValue, itemReplacement.attachment, string);
					return modelInstance;
				}

				if (boolean1 && !StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel())) {
					modelInstance = this.addStatic(modelSlot, inventoryItem.getStaticModel(), string);
					return modelInstance;
				}
			}

			return null;
		}
	}

	private ModelInstance addMaskingModel(ModelManager.ModelSlot modelSlot, IsoGameCharacter gameCharacter, InventoryItem inventoryItem, ItemReplacement itemReplacement, String string, String string2, String string3) {
		ModelInstance modelInstance = null;
		ItemVisual itemVisual = inventoryItem.getVisual();
		if (itemReplacement.clothingItem != null && itemVisual != null) {
			modelInstance = PopTemplateManager.instance.addClothingItem(gameCharacter, modelSlot, itemVisual, itemReplacement.clothingItem);
		} else {
			if (StringUtils.isNullOrWhitespace(inventoryItem.getStaticModel())) {
				return null;
			}

			String string4 = null;
			if (itemVisual != null && inventoryItem.getClothingItem() != null) {
				string4 = (String)inventoryItem.getClothingItem().getTextureChoices().get(itemVisual.getTextureChoice());
			}

			if (!StringUtils.isNullOrEmpty(string2)) {
				modelInstance = this.addStaticForcedTex(modelSlot.model, inventoryItem.getStaticModel(), string2, string2, string4);
			} else {
				modelInstance = this.addStaticForcedTex(modelSlot, inventoryItem.getStaticModel(), string3, string4);
			}

			modelInstance.maskVariableValue = string;
			if (itemVisual != null) {
				modelInstance.tintR = itemVisual.m_Tint.r;
				modelInstance.tintG = itemVisual.m_Tint.g;
				modelInstance.tintB = itemVisual.m_Tint.b;
			}
		}

		if (!StringUtils.isNullOrEmpty(string)) {
			gameCharacter.setVariable(itemReplacement.maskVariableName, string);
			gameCharacter.bUpdateEquippedTextures = true;
		}

		return modelInstance;
	}

	private void addWeaponPartModels(ModelManager.ModelSlot modelSlot, HandWeapon handWeapon, ModelInstance modelInstance) {
		ArrayList arrayList = handWeapon.getModelWeaponPart();
		if (arrayList != null) {
			ArrayList arrayList2 = handWeapon.getAllWeaponParts(this.m_tempWeaponPartList);
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				WeaponPart weaponPart = (WeaponPart)arrayList2.get(int1);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					ModelWeaponPart modelWeaponPart = (ModelWeaponPart)arrayList.get(int2);
					if (weaponPart.getFullType().equals(modelWeaponPart.partType)) {
						ModelInstance modelInstance2 = this.addStatic(modelInstance, modelWeaponPart.modelName, modelWeaponPart.attachmentNameSelf, modelWeaponPart.attachmentParent);
						modelInstance2.setOwner(modelSlot);
					}
				}
			}
		}
	}

	public void resetModelInstance(ModelInstance modelInstance, Object object) {
		if (modelInstance != null) {
			modelInstance.clearOwner(object);
			if (modelInstance.isRendering()) {
				modelInstance.bResetAfterRender = true;
			} else {
				if (modelInstance instanceof VehicleModelInstance) {
					return;
				}

				if (modelInstance instanceof VehicleSubModelInstance) {
					return;
				}

				modelInstance.reset();
				this.m_modelInstancePool.release((Object)modelInstance);
			}
		}
	}

	public void resetModelInstanceRecurse(ModelInstance modelInstance, Object object) {
		if (modelInstance != null) {
			this.resetModelInstancesRecurse(modelInstance.sub, object);
			this.resetModelInstance(modelInstance, object);
		}
	}

	public void resetModelInstancesRecurse(ArrayList arrayList, Object object) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)arrayList.get(int1);
			this.resetModelInstance(modelInstance, object);
		}
	}

	public void derefModelInstance(ModelInstance modelInstance) {
		if (modelInstance != null) {
			assert modelInstance.renderRefCount > 0;
			--modelInstance.renderRefCount;
			if (modelInstance.bResetAfterRender && !modelInstance.isRendering()) {
				assert modelInstance.getOwner() == null;
				if (modelInstance instanceof VehicleModelInstance) {
					return;
				}

				if (modelInstance instanceof VehicleSubModelInstance) {
					return;
				}

				modelInstance.reset();
				this.m_modelInstancePool.release((Object)modelInstance);
			}
		}
	}

	public void derefModelInstances(ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			ModelInstance modelInstance = (ModelInstance)arrayList.get(int1);
			this.derefModelInstance(modelInstance);
		}
	}

	private void DoCharacterModelParts(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot) {
		if (modelSlot.isRendering()) {
			boolean boolean1 = false;
		}

		if (DebugLog.isEnabled(DebugType.Clothing)) {
			DebugLog.Clothing.debugln("Char: " + gameCharacter + " Slot: " + modelSlot);
		}

		modelSlot.sub.clear();
		PopTemplateManager.instance.populateCharacterModelSlot(gameCharacter, modelSlot);
		this.DoCharacterModelEquipped(gameCharacter, modelSlot);
	}

	public void update() {
		int int1;
		IsoGameCharacter gameCharacter;
		for (int1 = 0; int1 < this.ToResetNextFrame.size(); ++int1) {
			gameCharacter = (IsoGameCharacter)this.ToResetNextFrame.get(int1);
			this.Reset(gameCharacter);
		}

		this.ToResetNextFrame.clear();
		for (int1 = 0; int1 < this.ToResetEquippedNextFrame.size(); ++int1) {
			gameCharacter = (IsoGameCharacter)this.ToResetEquippedNextFrame.get(int1);
			this.ResetCharacterEquippedHands(gameCharacter);
		}

		this.ToResetEquippedNextFrame.clear();
		for (int1 = 0; int1 < this.ToRemove.size(); ++int1) {
			gameCharacter = (IsoGameCharacter)this.ToRemove.get(int1);
			this.DoRemove(gameCharacter);
		}

		this.ToRemove.clear();
		for (int1 = 0; int1 < this.m_resetAfterRender.size(); ++int1) {
			ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.m_resetAfterRender.get(int1);
			if (!modelSlot.isRendering()) {
				modelSlot.reset();
				this.m_resetAfterRender.remove(int1--);
			}
		}

		this.m_lights.clear();
		if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
			this.m_lights.addAll(IsoWorld.instance.CurrentCell.getLamppostPositions());
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				BaseVehicle baseVehicle = (BaseVehicle)arrayList.get(int2);
				if (baseVehicle.sprite != null && baseVehicle.sprite.hasActiveModel()) {
					((VehicleModelInstance)baseVehicle.sprite.modelSlot.model).UpdateLights();
				}
			}
		}

		this.m_freeLights.addAll(this.m_torchLights);
		this.m_torchLights.clear();
		this.m_torches.clear();
		LightingJNI.getTorches(this.m_torches);
		for (int1 = 0; int1 < this.m_torches.size(); ++int1) {
			IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)this.m_torches.get(int1);
			IsoLightSource lightSource = this.m_freeLights.isEmpty() ? new IsoLightSource(0, 0, 0, 1.0F, 1.0F, 1.0F, 1) : (IsoLightSource)this.m_freeLights.pop();
			lightSource.x = (int)torchInfo.x;
			lightSource.y = (int)torchInfo.y;
			lightSource.z = (int)torchInfo.z;
			lightSource.r = 1.0F;
			lightSource.g = 0.85F;
			lightSource.b = 0.6F;
			lightSource.radius = (int)Math.ceil((double)torchInfo.dist);
			this.m_torchLights.add(lightSource);
		}
	}

	private ModelManager.ModelSlot addNewSlot(IsoGameCharacter gameCharacter) {
		ModelManager.ModelSlot modelSlot = new ModelManager.ModelSlot(this.m_modelSlots.size(), (ModelInstance)null, gameCharacter);
		this.m_modelSlots.add(modelSlot);
		return modelSlot;
	}

	public ModelManager.ModelSlot getSlot(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.m_modelSlots.size(); ++int1) {
			ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.m_modelSlots.get(int1);
			if (!modelSlot.bRemove && !modelSlot.isRendering() && !modelSlot.active) {
				return modelSlot;
			}
		}

		return this.addNewSlot(gameCharacter);
	}

	private boolean DoRemove(IsoGameCharacter gameCharacter) {
		if (!this.m_contains.contains(gameCharacter)) {
			return false;
		} else {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < this.m_modelSlots.size(); ++int1) {
				ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.m_modelSlots.get(int1);
				if (modelSlot.character == gameCharacter) {
					gameCharacter.legsSprite.modelSlot = null;
					this.m_contains.remove(gameCharacter);
					if (!gameCharacter.isSceneCulled()) {
						gameCharacter.onCullStateChanged(this, true);
					}

					if (!this.m_resetAfterRender.contains(modelSlot)) {
						this.m_resetAfterRender.add(modelSlot);
					}

					boolean1 = true;
				}
			}

			return boolean1;
		}
	}

	public void Remove(IsoGameCharacter gameCharacter) {
		if (!gameCharacter.isSceneCulled()) {
			if (!this.ToRemove.contains(gameCharacter)) {
				gameCharacter.legsSprite.modelSlot.bRemove = true;
				this.ToRemove.add(gameCharacter);
				gameCharacter.onCullStateChanged(this, true);
			} else if (this.ContainsChar(gameCharacter)) {
				throw new IllegalStateException("IsoGameCharacter.isSceneCulled() = true inconsistent with ModelManager.ContainsChar() = true");
			}
		}
	}

	public void Remove(BaseVehicle baseVehicle) {
		if (baseVehicle.sprite != null && baseVehicle.sprite.modelSlot != null) {
			ModelManager.ModelSlot modelSlot = baseVehicle.sprite.modelSlot;
			if (!this.m_resetAfterRender.contains(modelSlot)) {
				this.m_resetAfterRender.add(modelSlot);
			}

			baseVehicle.sprite.modelSlot = null;
		}
	}

	public void ResetNextFrame(IsoGameCharacter gameCharacter) {
		if (!this.ToResetNextFrame.contains(gameCharacter)) {
			this.ToResetNextFrame.add(gameCharacter);
		}
	}

	public void ResetEquippedNextFrame(IsoGameCharacter gameCharacter) {
		if (!this.ToResetEquippedNextFrame.contains(gameCharacter)) {
			this.ToResetEquippedNextFrame.add(gameCharacter);
		}
	}

	public void Reset() {
		RenderThread.invokeOnRenderContext(()->{
			Iterator iterator = this.ToRemove.iterator();
			while (iterator.hasNext()) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)iterator.next();
				this.DoRemove(gameCharacter);
			}

			this.ToRemove.clear();
			try {
				if (!this.m_contains.isEmpty()) {
					IsoGameCharacter[] gameCharacterArray = (IsoGameCharacter[])this.m_contains.toArray(new IsoGameCharacter[0]);
					IsoGameCharacter[] gameCharacterArray2 = gameCharacterArray;
					int int1 = gameCharacterArray.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						IsoGameCharacter gameCharacter2 = gameCharacterArray2[int2];
						this.DoRemove(gameCharacter2);
					}
				}

				this.m_modelSlots.clear();
			} catch (Exception exception) {
				DebugLog.Animation.error("Exception thrown removing Models.");
				exception.printStackTrace();
			}
		});
		this.m_lights.clear();
		this.m_lightsTemp.clear();
	}

	public void getClosestThreeLights(IsoMovingObject movingObject, IsoLightSource[] lightSourceArray) {
		this.m_lightsTemp.clear();
		Iterator iterator = this.m_lights.iterator();
		while (true) {
			IsoLightSource lightSource;
			do {
				do {
					do {
						if (!iterator.hasNext()) {
							if (movingObject instanceof BaseVehicle) {
								for (int int1 = 0; int1 < this.m_torches.size(); ++int1) {
									IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)this.m_torches.get(int1);
									if (!(IsoUtils.DistanceTo(movingObject.x, movingObject.y, torchInfo.x, torchInfo.y) >= torchInfo.dist) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)movingObject.x, (int)movingObject.y, (int)movingObject.z, (int)torchInfo.x, (int)torchInfo.y, (int)torchInfo.z, false) != LosUtil.TestResults.Blocked) {
										if (torchInfo.bCone) {
											Vector2 vector2 = this.m_tempVec2;
											vector2.x = torchInfo.x - movingObject.x;
											vector2.y = torchInfo.y - movingObject.y;
											vector2.normalize();
											Vector2 vector22 = this.m_tempVec2_2;
											vector22.x = torchInfo.angleX;
											vector22.y = torchInfo.angleY;
											vector22.normalize();
											float float1 = vector2.dot(vector22);
											if (float1 >= -0.92F) {
												continue;
											}
										}

										this.m_lightsTemp.add((IsoLightSource)this.m_torchLights.get(int1));
									}
								}
							}

							PZArrayUtil.sort(this.m_lightsTemp, Lambda.comparator(movingObject, (var0,movingObjectx,lightSourceArrayx)->{
								float iterator = lightSourceArrayx.DistTo(var0.x, var0.y);
								float lightSource = lightSourceArrayx.DistTo(movingObjectx.x, movingObjectx.y);
								if (iterator > lightSource) {
									return 1;
								} else {
									return iterator < lightSource ? -1 : 0;
								}
							}));

							lightSourceArray[0] = lightSourceArray[1] = lightSourceArray[2] = null;
							if (this.m_lightsTemp.size() > 0) {
								lightSourceArray[0] = (IsoLightSource)this.m_lightsTemp.get(0);
							}

							if (this.m_lightsTemp.size() > 1) {
								lightSourceArray[1] = (IsoLightSource)this.m_lightsTemp.get(1);
							}

							if (this.m_lightsTemp.size() > 2) {
								lightSourceArray[2] = (IsoLightSource)this.m_lightsTemp.get(2);
							}

							return;
						}

						lightSource = (IsoLightSource)iterator.next();
					}			 while (!lightSource.bActive);
				}		 while (lightSource.life == 0);
			}	 while (lightSource.localToBuilding != null && movingObject.getCurrentBuilding() != lightSource.localToBuilding);

			if (!(IsoUtils.DistanceTo(movingObject.x, movingObject.y, (float)lightSource.x + 0.5F, (float)lightSource.y + 0.5F) >= (float)lightSource.radius) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)movingObject.x, (int)movingObject.y, (int)movingObject.z, lightSource.x, lightSource.y, lightSource.z, false) != LosUtil.TestResults.Blocked) {
				this.m_lightsTemp.add(lightSource);
			}
		}
	}

	public void addVehicle(BaseVehicle baseVehicle) {
		if (this.m_bCreated) {
			if (!GameServer.bServer || ServerGUI.isCreated()) {
				if (baseVehicle != null && baseVehicle.getScript() != null) {
					VehicleScript vehicleScript = baseVehicle.getScript();
					String string = baseVehicle.getScript().getModel().file;
					Model model = this.getLoadedModel(string);
					if (model == null) {
						DebugLog.Animation.error("Failed to find vehicle model: %s", string);
					} else {
						if (DebugLog.isEnabled(DebugType.Animation)) {
							DebugLog.Animation.debugln("%s", string);
						}

						VehicleModelInstance vehicleModelInstance = new VehicleModelInstance();
						vehicleModelInstance.init(model, (IsoGameCharacter)null, baseVehicle.getAnimationPlayer());
						vehicleModelInstance.applyModelScriptScale(string);
						baseVehicle.getSkin();
						VehicleScript.Skin skin = vehicleScript.getTextures();
						if (baseVehicle.getSkinIndex() >= 0 && baseVehicle.getSkinIndex() < vehicleScript.getSkinCount()) {
							skin = vehicleScript.getSkin(baseVehicle.getSkinIndex());
						}

						vehicleModelInstance.LoadTexture(skin.texture);
						vehicleModelInstance.tex = skin.textureData;
						vehicleModelInstance.textureMask = skin.textureDataMask;
						vehicleModelInstance.textureDamage1Overlay = skin.textureDataDamage1Overlay;
						vehicleModelInstance.textureDamage1Shell = skin.textureDataDamage1Shell;
						vehicleModelInstance.textureDamage2Overlay = skin.textureDataDamage2Overlay;
						vehicleModelInstance.textureDamage2Shell = skin.textureDataDamage2Shell;
						vehicleModelInstance.textureLights = skin.textureDataLights;
						vehicleModelInstance.textureRust = skin.textureDataRust;
						if (vehicleModelInstance.tex != null) {
							vehicleModelInstance.tex.bindAlways = true;
						} else {
							DebugLog.Animation.error("texture not found:", baseVehicle.getSkin());
						}

						ModelManager.ModelSlot modelSlot = this.getSlot((IsoGameCharacter)null);
						modelSlot.model = vehicleModelInstance;
						vehicleModelInstance.setOwner(modelSlot);
						vehicleModelInstance.object = baseVehicle;
						modelSlot.sub.clear();
						for (int int1 = 0; int1 < baseVehicle.models.size(); ++int1) {
							BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)baseVehicle.models.get(int1);
							Model model2 = this.getLoadedModel(modelInfo.scriptModel.file);
							if (model2 == null) {
								DebugLog.Animation.error("vehicle.models[%d] not found: %s", int1, modelInfo.scriptModel.file);
							} else {
								VehicleSubModelInstance vehicleSubModelInstance = new VehicleSubModelInstance();
								vehicleSubModelInstance.init(model2, (IsoGameCharacter)null, modelInfo.getAnimationPlayer());
								vehicleSubModelInstance.setOwner(modelSlot);
								vehicleSubModelInstance.applyModelScriptScale(modelInfo.scriptModel.file);
								vehicleSubModelInstance.object = baseVehicle;
								vehicleSubModelInstance.parent = vehicleModelInstance;
								vehicleModelInstance.sub.add(vehicleSubModelInstance);
								vehicleSubModelInstance.modelInfo = modelInfo;
								if (vehicleSubModelInstance.tex == null) {
									vehicleSubModelInstance.tex = vehicleModelInstance.tex;
								}

								modelSlot.sub.add(vehicleSubModelInstance);
								modelInfo.modelInstance = vehicleSubModelInstance;
							}
						}

						modelSlot.active = true;
						baseVehicle.sprite.modelSlot = modelSlot;
					}
				}
			}
		}
	}

	public ModelInstance addStatic(ModelManager.ModelSlot modelSlot, String string, String string2, String string3, String string4) {
		ModelInstance modelInstance = this.newStaticInstance(modelSlot, string, string2, string3, string4);
		if (modelInstance == null) {
			return null;
		} else {
			modelSlot.sub.add(modelInstance);
			modelInstance.setOwner(modelSlot);
			modelSlot.model.sub.add(modelInstance);
			return modelInstance;
		}
	}

	public ModelInstance newStaticInstance(ModelManager.ModelSlot modelSlot, String string, String string2, String string3, String string4) {
		if (DebugLog.isEnabled(DebugType.Animation)) {
			DebugLog.Animation.debugln("Adding Static Model:" + string);
		}

		Model model = this.tryGetLoadedModel(string, string2, true, string4, false);
		if (model == null && string != null) {
			this.loadStaticModel(string, string2, string4);
			model = this.getLoadedModel(string, string2, true, string4);
			if (model == null) {
				if (DebugLog.isEnabled(DebugType.Animation)) {
					DebugLog.Animation.error("Model not found. model:" + string + " tex:" + string2);
				}

				return null;
			}
		}

		if (string == null) {
			model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
		}

		ModelInstance modelInstance = this.newInstance(model, modelSlot.character, modelSlot.model.AnimPlayer);
		modelInstance.parent = modelSlot.model;
		if (modelSlot.model.AnimPlayer != null) {
			modelInstance.parentBone = modelSlot.model.AnimPlayer.getSkinningBoneIndex(string3, modelInstance.parentBone);
			modelInstance.parentBoneName = string3;
		}

		modelInstance.AnimPlayer = modelSlot.model.AnimPlayer;
		return modelInstance;
	}

	private ModelInstance addStatic(ModelManager.ModelSlot modelSlot, String string, String string2) {
		return this.addStaticForcedTex(modelSlot, string, string2, (String)null);
	}

	private ModelInstance addStaticForcedTex(ModelManager.ModelSlot modelSlot, String string, String string2, String string3) {
		String string4 = ScriptManager.getItemName(string);
		String string5 = ScriptManager.getItemName(string);
		String string6 = null;
		ModelManager.ModelMetaData modelMetaData = (ModelManager.ModelMetaData)modelMetaData.get(string);
		if (modelMetaData != null) {
			if (!StringUtils.isNullOrWhitespace(modelMetaData.meshName)) {
				string4 = modelMetaData.meshName;
			}

			if (!StringUtils.isNullOrWhitespace(modelMetaData.textureName)) {
				string5 = modelMetaData.textureName;
			}

			if (!StringUtils.isNullOrWhitespace(modelMetaData.shaderName)) {
				string6 = modelMetaData.shaderName;
			}
		}

		if (!StringUtils.isNullOrEmpty(string3)) {
			string5 = string3;
		}

		ModelScript modelScript = ScriptManager.instance.getModelScript(string);
		if (modelScript != null) {
			string4 = modelScript.getMeshName();
			string5 = modelScript.getTextureName();
			string6 = modelScript.getShaderName();
			ModelInstance modelInstance = this.addStatic(modelSlot, string4, string5, string2, string6);
			if (modelInstance != null) {
				modelInstance.applyModelScriptScale(string);
			}

			return modelInstance;
		} else {
			return this.addStatic(modelSlot, string4, string5, string2, string6);
		}
	}

	public ModelInstance addStatic(ModelInstance modelInstance, String string, String string2, String string3) {
		return this.addStaticForcedTex(modelInstance, string, string2, string3, (String)null);
	}

	public ModelInstance addStaticForcedTex(ModelInstance modelInstance, String string, String string2, String string3, String string4) {
		String string5 = ScriptManager.getItemName(string);
		String string6 = ScriptManager.getItemName(string);
		String string7 = null;
		ModelScript modelScript = ScriptManager.instance.getModelScript(string);
		if (modelScript != null) {
			string5 = modelScript.getMeshName();
			string6 = modelScript.getTextureName();
			string7 = modelScript.getShaderName();
		}

		if (!StringUtils.isNullOrEmpty(string4)) {
			string6 = string4;
		}

		Model model = this.tryGetLoadedModel(string5, string6, true, string7, false);
		if (model == null && string5 != null) {
			this.loadStaticModel(string5, string6, string7);
			model = this.getLoadedModel(string5, string6, true, string7);
			if (model == null) {
				if (DebugLog.isEnabled(DebugType.Animation)) {
					DebugLog.Animation.error("Model not found. model:" + string5 + " tex:" + string6);
				}

				return null;
			}
		}

		if (string5 == null) {
			model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
		}

		if (model == null) {
			return null;
		} else {
			ModelInstance modelInstance2 = (ModelInstance)this.m_modelInstancePool.alloc();
			if (modelInstance != null) {
				modelInstance2.init(model, modelInstance.character, modelInstance.AnimPlayer);
				modelInstance2.parent = modelInstance;
				modelInstance.sub.add(modelInstance2);
			} else {
				modelInstance2.init(model, (IsoGameCharacter)null, (AnimationPlayer)null);
			}

			if (modelScript != null) {
				modelInstance2.applyModelScriptScale(string);
			}

			modelInstance2.attachmentNameSelf = string2;
			modelInstance2.attachmentNameParent = string3;
			return modelInstance2;
		}
	}

	private String modifyShaderName(String string) {
		if ((StringUtils.equals(string, "vehicle") || StringUtils.equals(string, "vehicle_multiuv") || StringUtils.equals(string, "vehicle_norandom_multiuv")) && !Core.getInstance().getPerfReflectionsOnLoad()) {
			string = string + "_noreflect";
		}

		return string;
	}

	private Model loadModelInternal(String string, String string2, String string3, ModelMesh modelMesh, boolean boolean1) {
		string3 = this.modifyShaderName(string3);
		Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
		modelAssetParams.animationsModel = modelMesh;
		modelAssetParams.bStatic = boolean1;
		modelAssetParams.meshName = string;
		modelAssetParams.shaderName = string3;
		modelAssetParams.textureName = string2;
		modelAssetParams.textureFlags = this.getTextureFlags();
		String string4 = this.createModelKey(string, string2, boolean1, string3);
		Model model = (Model)ModelAssetManager.instance.load(new AssetPath(string4), modelAssetParams);
		if (model != null) {
			this.putLoadedModel(string, string2, boolean1, string3, model);
		}

		return model;
	}

	public int getTextureFlags() {
		int int1 = TextureID.bUseCompression ? 4 : 0;
		if (Core.OptionModelTextureMipmaps) {
		}

		return int1;
	}

	public void setModelMetaData(String string, String string2, String string3, boolean boolean1) {
		this.setModelMetaData(string, string, string2, string3, boolean1);
	}

	public void setModelMetaData(String string, String string2, String string3, String string4, boolean boolean1) {
		ModelManager.ModelMetaData modelMetaData = new ModelManager.ModelMetaData();
		modelMetaData.meshName = string2;
		modelMetaData.textureName = string3;
		modelMetaData.shaderName = string4;
		modelMetaData.bStatic = boolean1;
		modelMetaData.put(string, modelMetaData);
	}

	public Model loadStaticModel(String string, String string2, String string3) {
		String string4 = this.modifyShaderName(string3);
		return this.loadModelInternal(string, string2, string4, (ModelMesh)null, true);
	}

	private Model loadModel(String string, String string2, ModelMesh modelMesh) {
		return this.loadModelInternal(string, string2, "basicEffect", modelMesh, false);
	}

	public Model getLoadedModel(String string) {
		ModelScript modelScript = ScriptManager.instance.getModelScript(string);
		if (modelScript != null) {
			if (modelScript.loadedModel != null) {
				return modelScript.loadedModel;
			} else {
				modelScript.shaderName = this.modifyShaderName(modelScript.shaderName);
				Model model = this.tryGetLoadedModel(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.bStatic, modelScript.getShaderName(), false);
				if (model != null) {
					modelScript.loadedModel = model;
					return model;
				} else {
					AnimationsMesh animationsMesh = modelScript.animationsMesh == null ? null : ScriptManager.instance.getAnimationsMesh(modelScript.animationsMesh);
					ModelMesh modelMesh = animationsMesh == null ? null : animationsMesh.modelMesh;
					model = modelScript.bStatic ? this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), (ModelMesh)null, true) : this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), modelMesh, false);
					modelScript.loadedModel = model;
					return model;
				}
			}
		} else {
			ModelManager.ModelMetaData modelMetaData = (ModelManager.ModelMetaData)modelMetaData.get(string);
			Model model2;
			if (modelMetaData != null) {
				modelMetaData.shaderName = this.modifyShaderName(modelMetaData.shaderName);
				model2 = this.tryGetLoadedModel(modelMetaData.meshName, modelMetaData.textureName, modelMetaData.bStatic, modelMetaData.shaderName, false);
				if (model2 != null) {
					return model2;
				} else {
					return modelMetaData.bStatic ? this.loadStaticModel(modelMetaData.meshName, modelMetaData.textureName, modelMetaData.shaderName) : this.loadModel(modelMetaData.meshName, modelMetaData.textureName, this.m_animModel);
				}
			} else {
				model2 = this.tryGetLoadedModel(string, (String)null, false, (String)null, false);
				if (model2 != null) {
					return model2;
				} else {
					String string2 = string.toLowerCase().trim();
					Iterator iterator = this.m_modelMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry entry = (Entry)iterator.next();
						String string3 = (String)entry.getKey();
						if (string3.startsWith(string2)) {
							Model model3 = (Model)entry.getValue();
							if (model3 != null && (string3.length() == string2.length() || string3.charAt(string2.length()) == '&')) {
								model2 = model3;
								break;
							}
						}
					}

					if (model2 == null && DebugLog.isEnabled(DebugType.Animation)) {
						DebugLog.Animation.error("ModelManager.getLoadedModel> Model missing for key=\"" + string2 + "\"");
					}

					return model2;
				}
			}
		}
	}

	public Model getLoadedModel(String string, String string2, boolean boolean1, String string3) {
		return this.tryGetLoadedModel(string, string2, boolean1, string3, true);
	}

	public Model tryGetLoadedModel(String string, String string2, boolean boolean1, String string3, boolean boolean2) {
		String string4 = this.createModelKey(string, string2, boolean1, string3);
		if (string4 == null) {
			return null;
		} else {
			Model model = (Model)this.m_modelMap.get(string4);
			if (model == null && boolean2 && DebugLog.isEnabled(DebugType.Animation)) {
				DebugLog.Animation.error("ModelManager.getLoadedModel> Model missing for key=\"" + string4 + "\"");
			}

			return model;
		}
	}

	public void putLoadedModel(String string, String string2, boolean boolean1, String string3, Model model) {
		String string4 = this.createModelKey(string, string2, boolean1, string3);
		if (string4 != null) {
			Model model2 = (Model)this.m_modelMap.get(string4);
			if (model2 != model) {
				if (model2 != null) {
					DebugLog.Animation.debugln("Override key=\"%s\" old=%s new=%s", string4, model2, model);
				} else {
					DebugLog.Animation.debugln("key=\"%s\" model=%s", string4, model);
				}

				this.m_modelMap.put(string4, model);
				model.Name = string4;
			}
		}
	}

	private String createModelKey(String string, String string2, boolean boolean1, String string3) {
		builder.delete(0, builder.length());
		if (string == null) {
			return null;
		} else {
			if (!toLowerKeyRoot.containsKey(string)) {
				toLowerKeyRoot.put(string, string.toLowerCase(Locale.ENGLISH).trim());
			}

			builder.append((String)toLowerKeyRoot.get(string));
			builder.append(amp);
			if (StringUtils.isNullOrWhitespace(string3)) {
				string3 = basicEffect;
			}

			builder.append(shaderEquals);
			if (!toLower.containsKey(string3)) {
				toLower.put(string3, string3.toLowerCase().trim());
			}

			builder.append((String)toLower.get(string3));
			if (!StringUtils.isNullOrWhitespace(string2)) {
				builder.append(texA);
				if (!toLowerTex.containsKey(string2)) {
					toLowerTex.put(string2, string2.toLowerCase().trim());
				}

				builder.append((String)toLowerTex.get(string2));
			}

			if (boolean1) {
				builder.append(isStaticTrue);
			}

			return builder.toString();
		}
	}

	private String createModelKey2(String string, String string2, boolean boolean1, String string3) {
		if (string == null) {
			return null;
		} else {
			if (StringUtils.isNullOrWhitespace(string3)) {
				string3 = "basicEffect";
			}

			String string4 = "shader=" + string3.toLowerCase().trim();
			if (!StringUtils.isNullOrWhitespace(string2)) {
				string4 = string4 + ";tex=" + string2.toLowerCase().trim();
			}

			if (boolean1) {
				string4 = string4 + ";isStatic=true";
			}

			String string5 = string.toLowerCase(Locale.ENGLISH).trim();
			return string5 + "&" + string4;
		}
	}

	private AnimationAsset loadAnim(String string, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
		DebugLog.Animation.debugln("Adding asset to queue: %s", string);
		AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
		animationAssetParams.animationsMesh = modelMesh;
		AnimationAsset animationAsset = (AnimationAsset)AnimationAssetManager.instance.load(new AssetPath(string), animationAssetParams);
		animationAsset.skinningData = modelMesh.skinningData;
		this.putAnimationAsset(string, animationAsset, modAnimations);
		return animationAsset;
	}

	private void putAnimationAsset(String string, AnimationAsset animationAsset, ModelManager.ModAnimations modAnimations) {
		String string2 = string.toLowerCase();
		AnimationAsset animationAsset2 = (AnimationAsset)modAnimations.m_animationAssetMap.getOrDefault(string2, (Object)null);
		if (animationAsset2 != null) {
			DebugLog.Animation.debugln("Overwriting asset: %s", this.animAssetToString(animationAsset2));
			DebugLog.Animation.debugln("New asset		: %s", this.animAssetToString(animationAsset));
			modAnimations.m_animationAssetList.remove(animationAsset2);
		}

		animationAsset.modelManagerKey = string2;
		animationAsset.modAnimations = modAnimations;
		modAnimations.m_animationAssetMap.put(string2, animationAsset);
		modAnimations.m_animationAssetList.add(animationAsset);
	}

	private String animAssetToString(AnimationAsset animationAsset) {
		if (animationAsset == null) {
			return "null";
		} else {
			AssetPath assetPath = animationAsset.getPath();
			return assetPath == null ? "null-path" : String.valueOf(assetPath.getPath());
		}
	}

	private AnimationAsset getAnimationAsset(String string) {
		String string2 = string.toLowerCase(Locale.ENGLISH);
		return (AnimationAsset)this.m_animationAssets.get(string2);
	}

	private AnimationAsset getAnimationAssetRequired(String string) {
		AnimationAsset animationAsset = this.getAnimationAsset(string);
		if (animationAsset == null) {
			throw new NullPointerException("Required Animation Asset not found: " + string);
		} else {
			return animationAsset;
		}
	}

	public void addAnimationClip(String string, AnimationClip animationClip) {
		this.m_animModel.skinningData.AnimationClips.put(string, animationClip);
	}

	public AnimationClip getAnimationClip(String string) {
		return (AnimationClip)this.m_animModel.skinningData.AnimationClips.get(string);
	}

	public Collection getAllAnimationClips() {
		return this.m_animModel.skinningData.AnimationClips.values();
	}

	public ModelInstance newInstance(Model model, IsoGameCharacter gameCharacter, AnimationPlayer animationPlayer) {
		if (model == null) {
			System.err.println("ModelManager.newInstance> Model is null.");
			return null;
		} else {
			ModelInstance modelInstance = (ModelInstance)this.m_modelInstancePool.alloc();
			modelInstance.init(model, gameCharacter, animationPlayer);
			return modelInstance;
		}
	}

	public boolean isLoadingAnimations() {
		Iterator iterator = this.m_animationAssets.values().iterator();
		AnimationAsset animationAsset;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			animationAsset = (AnimationAsset)iterator.next();
		} while (!animationAsset.isEmpty());

		return true;
	}

	public void reloadModelsMatching(String string) {
		string = string.toLowerCase(Locale.ENGLISH);
		Set set = this.m_modelMap.keySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String string2 = (String)iterator.next();
			if (string2.contains(string)) {
				Model model = (Model)this.m_modelMap.get(string2);
				if (!model.isEmpty()) {
					DebugLog.General.printf("reloading model %s\n", string2);
					ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
					meshAssetParams.animationsMesh = null;
					if (model.Mesh.vb == null) {
						meshAssetParams.bStatic = string2.contains(";isStatic=true");
					} else {
						meshAssetParams.bStatic = model.Mesh.vb.bStatic;
					}

					MeshAssetManager.instance.reload(model.Mesh, meshAssetParams);
				}
			}
		}
	}

	public void loadModAnimations() {
		Iterator iterator = this.m_modAnimations.values().iterator();
		while (iterator.hasNext()) {
			ModelManager.ModAnimations modAnimations = (ModelManager.ModAnimations)iterator.next();
			modAnimations.setPriority(modAnimations == this.m_gameAnimations ? 0 : -1);
		}

		ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();
		ArrayList arrayList2 = ZomboidFileSystem.instance.getModIDs();
		for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
			String string = (String)arrayList2.get(int1);
			ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
			if (mod != null && mod.animsXFile.isDirectory()) {
				ModelManager.ModAnimations modAnimations2 = (ModelManager.ModAnimations)this.m_modAnimations.get(string);
				if (modAnimations2 != null) {
					modAnimations2.setPriority(int1 + 1);
				} else {
					modAnimations2 = new ModelManager.ModAnimations(string);
					modAnimations2.setPriority(int1 + 1);
					this.m_modAnimations.put(string, modAnimations2);
					Iterator iterator2 = arrayList.iterator();
					while (iterator2.hasNext()) {
						AnimationsMesh animationsMesh = (AnimationsMesh)iterator2.next();
						Iterator iterator3 = animationsMesh.animationDirectories.iterator();
						while (iterator3.hasNext()) {
							String string2 = (String)iterator3.next();
							if (animationsMesh.modelMesh.isReady()) {
								File file = new File(mod.animsXFile, string2);
								if (file.exists()) {
									this.loadAnimsFromDir(mod.baseFile.toURI(), mod.mediaFile.toURI(), file, animationsMesh.modelMesh, modAnimations2);
								}
							}
						}
					}

					this.loadHumanAnimations(mod, modAnimations2);
				}
			}
		}

		this.setActiveAnimations();
	}

	void setActiveAnimations() {
		this.m_animationAssets.clear();
		ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			AnimationsMesh animationsMesh = (AnimationsMesh)iterator.next();
			if (animationsMesh.modelMesh.isReady()) {
				animationsMesh.modelMesh.skinningData.AnimationClips.clear();
			}
		}

		iterator = this.m_modAnimations.values().iterator();
		label45: while (true) {
			ModelManager.ModAnimations modAnimations;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				modAnimations = (ModelManager.ModAnimations)iterator.next();
			}	 while (!modAnimations.isActive());

			Iterator iterator2 = modAnimations.m_animationAssetList.iterator();
			while (true) {
				AnimationAsset animationAsset;
				AnimationAsset animationAsset2;
				do {
					if (!iterator2.hasNext()) {
						continue label45;
					}

					animationAsset = (AnimationAsset)iterator2.next();
					animationAsset2 = (AnimationAsset)this.m_animationAssets.get(animationAsset.modelManagerKey);
				}		 while (animationAsset2 != null && animationAsset2 != animationAsset && animationAsset2.modAnimations.m_priority > modAnimations.m_priority);

				this.m_animationAssets.put(animationAsset.modelManagerKey, animationAsset);
				if (animationAsset.isReady()) {
					animationAsset.skinningData.AnimationClips.putAll(animationAsset.AnimationClips);
				}
			}
		}
	}

	public void animationAssetLoaded(AnimationAsset animationAsset) {
		if (animationAsset.modAnimations.isActive()) {
			AnimationAsset animationAsset2 = (AnimationAsset)this.m_animationAssets.get(animationAsset.modelManagerKey);
			if (animationAsset2 == null || animationAsset2 == animationAsset || animationAsset2.modAnimations.m_priority <= animationAsset.modAnimations.m_priority) {
				this.m_animationAssets.put(animationAsset.modelManagerKey, animationAsset);
				animationAsset.skinningData.AnimationClips.putAll(animationAsset.AnimationClips);
			}
		}
	}

	public void initAnimationMeshes(boolean boolean1) {
		ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();
		Iterator iterator;
		AnimationsMesh animationsMesh;
		for (iterator = arrayList.iterator(); iterator.hasNext(); animationsMesh.modelMesh.m_animationsMesh = animationsMesh.modelMesh) {
			animationsMesh = (AnimationsMesh)iterator.next();
			ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
			meshAssetParams.bStatic = false;
			meshAssetParams.animationsMesh = null;
			animationsMesh.modelMesh = (ModelMesh)MeshAssetManager.instance.getAssetTable().get(animationsMesh.meshFile);
			if (animationsMesh.modelMesh == null) {
				animationsMesh.modelMesh = (ModelMesh)MeshAssetManager.instance.load(new AssetPath(animationsMesh.meshFile), meshAssetParams);
			}
		}

		if (!boolean1) {
			while (this.isLoadingAnimationMeshes()) {
				GameWindow.fileSystem.updateAsyncTransactions();
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException) {
				}

				if (!GameServer.bServer) {
					Core.getInstance().StartFrame();
					Core.getInstance().EndFrame();
					Core.getInstance().StartFrameUI();
					Core.getInstance().EndFrameUI();
				}
			}

			iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				animationsMesh = (AnimationsMesh)iterator.next();
				Iterator iterator2 = animationsMesh.animationDirectories.iterator();
				while (iterator2.hasNext()) {
					String string = (String)iterator2.next();
					if (animationsMesh.modelMesh.isReady()) {
						File file = new File(ZomboidFileSystem.instance.base, "media/anims_X/" + string);
						if (file.exists()) {
							this.loadAnimsFromDir("media/anims_X/" + string, animationsMesh.modelMesh);
						}
					}
				}
			}
		}
	}

	private boolean isLoadingAnimationMeshes() {
		ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();
		Iterator iterator = arrayList.iterator();
		AnimationsMesh animationsMesh;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			animationsMesh = (AnimationsMesh)iterator.next();
		} while (animationsMesh.modelMesh.isFailure() || animationsMesh.modelMesh.isReady());

		return true;
	}

	private void loadHumanAnimations(ChooseGameInfo.Mod mod, ModelManager.ModAnimations modAnimations) {
		AnimationsMesh animationsMesh = ScriptManager.instance.getAnimationsMesh("Human");
		if (animationsMesh != null && animationsMesh.modelMesh != null && animationsMesh.modelMesh.isReady()) {
			File[] fileArray = mod.animsXFile.listFiles();
			if (fileArray != null) {
				URI uRI = mod.animsXFile.toURI();
				File[] fileArray2 = fileArray;
				int int1 = fileArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					File file = fileArray2[int2];
					if (file.isDirectory()) {
						if (!this.isAnimationsMeshDirectory(file.getName())) {
							this.loadAnimsFromDir(mod.baseFile.toURI(), mod.mediaFile.toURI(), file, animationsMesh.modelMesh, modAnimations);
						}
					} else {
						String string = ZomboidFileSystem.instance.getAnimName(uRI, file);
						this.loadAnim(string, animationsMesh.modelMesh, modAnimations);
					}
				}
			}
		}
	}

	private boolean isAnimationsMeshDirectory(String string) {
		ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();
		Iterator iterator = arrayList.iterator();
		AnimationsMesh animationsMesh;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			animationsMesh = (AnimationsMesh)iterator.next();
		} while (!animationsMesh.animationDirectories.contains(string));

		return true;
	}

	static  {
		modelMetaData = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		basicEffect = "basicEffect";
		isStaticTrue = ";isStatic=true";
		shaderEquals = "shader=";
		texA = ";tex=";
		amp = "&";
		toLower = new HashMap();
		toLowerTex = new HashMap();
		toLowerKeyRoot = new HashMap();
		builder = new StringBuilder();
	}

	public static final class ModAnimations {
		public final String m_modID;
		public final ArrayList m_animationAssetList = new ArrayList();
		public final HashMap m_animationAssetMap = new HashMap();
		public int m_priority;

		public ModAnimations(String string) {
			this.m_modID = string;
		}

		public void setPriority(int int1) {
			assert int1 >= -1;
			this.m_priority = int1;
		}

		public boolean isActive() {
			return this.m_priority != -1;
		}
	}

	class AnimDirReloader implements PredicatedFileWatcher.IPredicatedFileWatcherCallback {
		URI m_baseURI;
		URI m_mediaURI;
		String m_dir;
		String m_dirSecondary;
		String m_dirAbsolute;
		String m_dirSecondaryAbsolute;
		ModelMesh m_animationsModel;
		ModelManager.ModAnimations m_modAnimations;

		public AnimDirReloader(URI uRI, URI uRI2, String string, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
			string = ZomboidFileSystem.instance.getRelativeFile(uRI, string);
			this.m_baseURI = uRI;
			this.m_mediaURI = uRI2;
			this.m_dir = ZomboidFileSystem.instance.normalizeFolderPath(string);
			this.m_dirAbsolute = ZomboidFileSystem.instance.normalizeFolderPath((new File(new File(this.m_baseURI), this.m_dir)).toString());
			if (this.m_dir.contains("/anims/")) {
				this.m_dirSecondary = this.m_dir.replace("/anims/", "/anims_X/");
				this.m_dirSecondaryAbsolute = ZomboidFileSystem.instance.normalizeFolderPath((new File(new File(this.m_baseURI), this.m_dirSecondary)).toString());
			}

			this.m_animationsModel = modelMesh;
			this.m_modAnimations = modAnimations;
		}

		private boolean IsInDir(String string) {
			string = ZomboidFileSystem.instance.normalizeFolderPath(string);
			try {
				if (this.m_dirSecondary == null) {
					return string.startsWith(this.m_dirAbsolute);
				} else {
					return string.startsWith(this.m_dirAbsolute) || string.startsWith(this.m_dirSecondaryAbsolute);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				return false;
			}
		}

		public void call(String string) {
			String string2 = string.toLowerCase();
			if (string2.endsWith(".fbx") || string2.endsWith(".x") || string2.endsWith(".txt")) {
				String string3 = ZomboidFileSystem.instance.getAnimName(this.m_mediaURI, new File(string));
				AnimationAsset animationAsset = ModelManager.this.getAnimationAsset(string3);
				if (animationAsset != null) {
					if (!animationAsset.isEmpty()) {
						DebugLog.General.debugln("Reloading animation: %s", ModelManager.this.animAssetToString(animationAsset));
						assert animationAsset.getRefCount() == 1;
						AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
						animationAssetParams.animationsMesh = this.m_animationsModel;
						AnimationAssetManager.instance.reload(animationAsset, animationAssetParams);
					}
				} else {
					ModelManager.this.loadAnim(string3, this.m_animationsModel, this.m_modAnimations);
				}
			}
		}

		public PredicatedFileWatcher GetFileWatcher() {
			return new PredicatedFileWatcher(this.m_dir, this::IsInDir, this);
		}
	}

	public static class ModelSlot {
		public int ID;
		public ModelInstance model;
		public IsoGameCharacter character;
		public final ArrayList sub = new ArrayList();
		protected final AttachedModels attachedModels = new AttachedModels();
		public boolean active;
		public boolean bRemove;
		public int renderRefCount = 0;
		public int framesSinceStart;

		public ModelSlot(int int1, ModelInstance modelInstance, IsoGameCharacter gameCharacter) {
			this.ID = int1;
			this.model = modelInstance;
			this.character = gameCharacter;
		}

		public void Update() {
			if (this.character != null && !this.bRemove) {
				++this.framesSinceStart;
				if (this != this.character.legsSprite.modelSlot) {
					boolean boolean1 = false;
				}

				if (this.model.AnimPlayer != this.character.getAnimationPlayer()) {
					this.model.AnimPlayer = this.character.getAnimationPlayer();
				}

				synchronized (this.model.m_lock) {
					this.model.UpdateDir();
					this.model.Update();
					for (int int1 = 0; int1 < this.sub.size(); ++int1) {
						((ModelInstance)this.sub.get(int1)).AnimPlayer = this.model.AnimPlayer;
					}
				}
			}
		}

		public boolean isRendering() {
			return this.renderRefCount > 0;
		}

		public void reset() {
			ModelManager.instance.resetModelInstanceRecurse(this.model, this);
			if (this.character != null) {
				this.character.primaryHandModel = null;
				this.character.secondaryHandModel = null;
				ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
				this.character.getReadyModelData().clear();
			}

			this.active = false;
			this.character = null;
			this.bRemove = false;
			this.renderRefCount = 0;
			this.model = null;
			this.sub.clear();
			this.attachedModels.clear();
		}
	}

	private static final class ModelMetaData {
		String meshName;
		String textureName;
		String shaderName;
		boolean bStatic;
	}
}
