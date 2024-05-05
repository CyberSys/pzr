package zombie.core.skinnedmodel.advancedanimation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.joml.Math;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjglx.BufferUtils;
import zombie.GameProfiler;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.SurvivorDesc;
import zombie.characters.AttachedItems.AttachedModelName;
import zombie.characters.AttachedItems.AttachedModelNames;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.action.ActionContext;
import zombie.characters.action.ActionGroup;
import zombie.characters.action.IActionStateChanged;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceRenderData;
import zombie.core.skinnedmodel.model.ModelInstanceTextureCreator;
import zombie.core.skinnedmodel.model.ModelInstanceTextureInitializer;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.skinnedmodel.population.BeardStyle;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.HairStyle;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.ui.UIManager;
import zombie.util.IPooledObject;
import zombie.util.Lambda;
import zombie.util.Pool;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;


public final class AnimatedModel extends AnimationVariableSource implements IAnimatable,IAnimEventCallback,IActionStateChanged,IHumanVisual {
	private String animSetName = "player-avatar";
	private String outfitName;
	private IsoGameCharacter character;
	private HumanVisual baseVisual = null;
	private final ItemVisuals itemVisuals = new ItemVisuals();
	private String primaryHandModelName;
	private String secondaryHandModelName;
	private final AttachedModelNames attachedModelNames = new AttachedModelNames();
	private ModelInstance modelInstance;
	private boolean bFemale = false;
	private boolean bZombie = false;
	private boolean bSkeleton = false;
	private String state;
	private final Vector2 angle = new Vector2();
	private final Vector3f offset = new Vector3f(0.0F, -0.45F, 0.0F);
	private boolean bIsometric = true;
	private boolean flipY = false;
	private float m_alpha = 1.0F;
	private AnimationPlayer animPlayer = null;
	private final ActionContext actionContext = new ActionContext(this);
	private final AdvancedAnimator advancedAnimator = new AdvancedAnimator();
	private float trackTime = 0.0F;
	private final String m_UID = String.format("%s-%s", this.getClass().getSimpleName(), UUID.randomUUID().toString());
	private float lightsOriginX;
	private float lightsOriginY;
	private float lightsOriginZ;
	private final IsoGridSquare.ResultLight[] lights = new IsoGridSquare.ResultLight[5];
	private final ColorInfo ambient = new ColorInfo();
	private boolean bOutside = true;
	private boolean bRoom = false;
	private boolean bUpdateTextures;
	private boolean bClothingChanged;
	private boolean bAnimate = true;
	private ModelInstanceTextureCreator textureCreator;
	private final AnimatedModel.StateInfo[] stateInfos = new AnimatedModel.StateInfo[3];
	private boolean bReady;
	private static final ObjectPool instDataPool = new ObjectPool(AnimatedModel.AnimatedModelInstanceRenderData::new);
	private final AnimatedModel.UIModelCamera uiModelCamera = new AnimatedModel.UIModelCamera();
	private static final AnimatedModel.WorldModelCamera worldModelCamera = new AnimatedModel.WorldModelCamera();

	public AnimatedModel() {
		this.advancedAnimator.init(this);
		this.advancedAnimator.animCallbackHandlers.add(this);
		this.actionContext.onStateChanged.add(this);
		int int1;
		for (int1 = 0; int1 < this.lights.length; ++int1) {
			this.lights[int1] = new IsoGridSquare.ResultLight();
		}

		for (int1 = 0; int1 < this.stateInfos.length; ++int1) {
			this.stateInfos[int1] = new AnimatedModel.StateInfo();
		}
	}

	public void setVisual(HumanVisual humanVisual) {
		this.baseVisual = humanVisual;
	}

	public BaseVisual getVisual() {
		return this.baseVisual;
	}

	public HumanVisual getHumanVisual() {
		return (HumanVisual)Type.tryCastTo(this.baseVisual, HumanVisual.class);
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		itemVisuals.clear();
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public boolean isZombie() {
		return this.bZombie;
	}

	public boolean isSkeleton() {
		return this.bSkeleton;
	}

	public void setAnimSetName(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			throw new IllegalArgumentException("invalid AnimSet \"" + string + "\"");
		} else {
			this.animSetName = string;
		}
	}

	public void setOutfitName(String string, boolean boolean1, boolean boolean2) {
		this.outfitName = string;
		this.bFemale = boolean1;
		this.bZombie = boolean2;
	}

	public void setCharacter(IsoGameCharacter gameCharacter) {
		this.outfitName = null;
		if (this.baseVisual != null) {
			this.baseVisual.clear();
		}

		this.itemVisuals.clear();
		if (gameCharacter instanceof IHumanVisual) {
			gameCharacter.getItemVisuals(this.itemVisuals);
			this.character = gameCharacter;
			if (gameCharacter.getAttachedItems() != null) {
				this.attachedModelNames.initFrom(gameCharacter.getAttachedItems());
			}

			this.setModelData(((IHumanVisual)gameCharacter).getHumanVisual(), this.itemVisuals);
		}
	}

	public void setSurvivorDesc(SurvivorDesc survivorDesc) {
		this.outfitName = null;
		if (this.baseVisual != null) {
			this.baseVisual.clear();
		}

		this.itemVisuals.clear();
		survivorDesc.getWornItems().getItemVisuals(this.itemVisuals);
		this.attachedModelNames.clear();
		this.setModelData(survivorDesc.getHumanVisual(), this.itemVisuals);
	}

	public void setPrimaryHandModelName(String string) {
		this.primaryHandModelName = string;
	}

	public void setSecondaryHandModelName(String string) {
		this.secondaryHandModelName = string;
	}

	public void setAttachedModelNames(AttachedModelNames attachedModelNames) {
		this.attachedModelNames.copyFrom(attachedModelNames);
	}

	public void setModelData(HumanVisual humanVisual, ItemVisuals itemVisuals) {
		AnimationPlayer animationPlayer = this.animPlayer;
		Model model = this.animPlayer == null ? null : animationPlayer.getModel();
		if (this.baseVisual != humanVisual) {
			if (this.baseVisual == null) {
				this.baseVisual = new HumanVisual(this);
			}

			this.baseVisual.copyFrom(humanVisual);
		}

		if (this.itemVisuals != itemVisuals) {
			this.itemVisuals.clear();
			this.itemVisuals.addAll(itemVisuals);
		}

		if (this.baseVisual != humanVisual) {
			this.bFemale = false;
			this.bZombie = false;
			this.bSkeleton = false;
			if (humanVisual != null) {
				this.bFemale = humanVisual.isFemale();
				this.bZombie = humanVisual.isZombie();
				this.bSkeleton = humanVisual.isSkeleton();
			}
		}

		if (this.modelInstance != null) {
			ModelManager.instance.resetModelInstanceRecurse(this.modelInstance, this);
		}

		Model model2 = humanVisual.getModel();
		this.getAnimationPlayer().setModel(model2);
		this.modelInstance = ModelManager.instance.newInstance(model2, (IsoGameCharacter)null, this.getAnimationPlayer());
		this.modelInstance.m_modelScript = humanVisual.getModelScript();
		this.modelInstance.setOwner(this);
		this.populateCharacterModelSlot();
		this.DoCharacterModelEquipped();
		boolean boolean1 = false;
		if (this.bAnimate) {
			AnimationSet animationSet = AnimationSet.GetAnimationSet(this.GetAnimSetName(), false);
			if (animationSet != this.advancedAnimator.animSet || animationPlayer != this.getAnimationPlayer() || model != model2) {
				boolean1 = true;
			}
		} else {
			boolean1 = true;
		}

		if (boolean1) {
			this.advancedAnimator.OnAnimDataChanged(false);
		}

		if (this.bAnimate) {
			ActionGroup actionGroup = ActionGroup.getActionGroup(this.GetAnimSetName());
			if (actionGroup != this.actionContext.getGroup()) {
				this.actionContext.setGroup(actionGroup);
			}

			this.advancedAnimator.SetState(this.actionContext.getCurrentStateName(), PZArrayUtil.listConvert(this.actionContext.getChildStates(), (var0)->{
				return var0.name;
			}));
		} else if (!StringUtils.isNullOrWhitespace(this.state)) {
			this.advancedAnimator.SetState(this.state);
		}

		if (boolean1) {
			float float1 = GameTime.getInstance().FPSMultiplier;
			GameTime.getInstance().FPSMultiplier = 100.0F;
			try {
				this.advancedAnimator.update();
			} finally {
				GameTime.getInstance().FPSMultiplier = float1;
			}
		}

		if (Core.bDebug && !this.bAnimate && this.stateInfoMain().readyData.isEmpty()) {
			this.getAnimationPlayer().resetBoneModelTransforms();
		}

		this.trackTime = 0.0F;
		this.stateInfoMain().bModelsReady = this.isReadyToRender();
	}

	public void setAmbient(ColorInfo colorInfo, boolean boolean1, boolean boolean2) {
		this.ambient.set(colorInfo.r, colorInfo.g, colorInfo.b, 1.0F);
		this.bOutside = boolean1;
		this.bRoom = boolean2;
	}

	public void setLights(IsoGridSquare.ResultLight[] resultLightArray, float float1, float float2, float float3) {
		this.lightsOriginX = float1;
		this.lightsOriginY = float2;
		this.lightsOriginZ = float3;
		for (int int1 = 0; int1 < resultLightArray.length; ++int1) {
			this.lights[int1].copyFrom(resultLightArray[int1]);
		}
	}

	public void setState(String string) {
		this.state = string;
	}

	public String getState() {
		return this.state;
	}

	public void setAngle(Vector2 vector2) {
		this.angle.set(vector2);
	}

	public void setOffset(float float1, float float2, float float3) {
		this.offset.set(float1, float2, float3);
	}

	public void setIsometric(boolean boolean1) {
		this.bIsometric = boolean1;
	}

	public boolean isIsometric() {
		return this.bIsometric;
	}

	public void setFlipY(boolean boolean1) {
		this.flipY = boolean1;
	}

	public void setAlpha(float float1) {
		this.m_alpha = float1;
	}

	public void setTrackTime(float float1) {
		this.trackTime = float1;
	}

	public void clothingItemChanged(String string) {
		this.bClothingChanged = true;
	}

	public void setAnimate(boolean boolean1) {
		this.bAnimate = boolean1;
	}

	private void initOutfit() {
		String string = this.outfitName;
		this.outfitName = null;
		if (!StringUtils.isNullOrWhitespace(string)) {
			ModelManager.instance.create();
			this.baseVisual.dressInNamedOutfit(string, this.itemVisuals);
			this.setModelData(this.baseVisual, this.itemVisuals);
		}
	}

	private void populateCharacterModelSlot() {
		HumanVisual humanVisual = this.getHumanVisual();
		if (humanVisual == null) {
			this.bUpdateTextures = true;
		} else {
			CharacterMask characterMask = HumanVisual.GetMask(this.itemVisuals);
			if (characterMask.isPartVisible(CharacterMask.Part.Head)) {
				this.addHeadHair(this.itemVisuals.findHat());
			}

			int int1;
			ItemVisual itemVisual;
			ClothingItem clothingItem;
			for (int1 = this.itemVisuals.size() - 1; int1 >= 0; --int1) {
				itemVisual = (ItemVisual)this.itemVisuals.get(int1);
				clothingItem = itemVisual.getClothingItem();
				if (clothingItem != null && clothingItem.isReady() && !this.isItemModelHidden(this.itemVisuals, itemVisual)) {
					this.addClothingItem(itemVisual, clothingItem);
				}
			}

			for (int1 = humanVisual.getBodyVisuals().size() - 1; int1 >= 0; --int1) {
				itemVisual = (ItemVisual)humanVisual.getBodyVisuals().get(int1);
				clothingItem = itemVisual.getClothingItem();
				if (clothingItem != null && clothingItem.isReady()) {
					this.addClothingItem(itemVisual, clothingItem);
				}
			}

			this.bUpdateTextures = true;
			Lambda.forEachFrom(PZArrayUtil::forEach, (List)this.modelInstance.sub, this.modelInstance, (var0,humanVisualx)->{
				var0.AnimPlayer = humanVisualx.AnimPlayer;
			});
		}
	}

	private void addHeadHair(ItemVisual itemVisual) {
		HumanVisual humanVisual = this.getHumanVisual();
		ImmutableColor immutableColor = humanVisual.getHairColor();
		ImmutableColor immutableColor2 = humanVisual.getBeardColor();
		HairStyle hairStyle;
		if (this.isFemale()) {
			hairStyle = HairStyles.instance.FindFemaleStyle(humanVisual.getHairModel());
			if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
				hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
			}

			if (hairStyle != null && hairStyle.isValid()) {
				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.debugln("  Adding female hair: " + hairStyle.name);
				}

				this.addHeadHairItem(hairStyle.model, hairStyle.texture, immutableColor);
			}
		} else {
			hairStyle = HairStyles.instance.FindMaleStyle(humanVisual.getHairModel());
			if (hairStyle != null && itemVisual != null && itemVisual.getClothingItem() != null) {
				hairStyle = HairStyles.instance.getAlternateForHat(hairStyle, itemVisual.getClothingItem().m_HatCategory);
			}

			if (hairStyle != null && hairStyle.isValid()) {
				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.debugln("  Adding male hair: " + hairStyle.name);
				}

				this.addHeadHairItem(hairStyle.model, hairStyle.texture, immutableColor);
			}

			BeardStyle beardStyle = BeardStyles.instance.FindStyle(humanVisual.getBeardModel());
			if (beardStyle != null && beardStyle.isValid()) {
				if (itemVisual != null && itemVisual.getClothingItem() != null && !StringUtils.isNullOrEmpty(itemVisual.getClothingItem().m_HatCategory) && itemVisual.getClothingItem().m_HatCategory.contains("nobeard")) {
					return;
				}

				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.debugln("  Adding beard: " + beardStyle.name);
				}

				this.addHeadHairItem(beardStyle.model, beardStyle.texture, immutableColor2);
			}
		}
	}

	private void addHeadHairItem(String string, String string2, ImmutableColor immutableColor) {
		if (StringUtils.isNullOrWhitespace(string)) {
			if (DebugLog.isEnabled(DebugType.Clothing)) {
				DebugLog.Clothing.warn("No model specified.");
			}
		} else {
			string = this.processModelFileName(string);
			ModelInstance modelInstance = ModelManager.instance.newAdditionalModelInstance(string, string2, (IsoGameCharacter)null, this.modelInstance.AnimPlayer, (String)null);
			if (modelInstance != null) {
				this.postProcessNewItemInstance(this.modelInstance, modelInstance, immutableColor);
			}
		}
	}

	private void addClothingItem(ItemVisual itemVisual, ClothingItem clothingItem) {
		String string = clothingItem.getModel(this.bFemale);
		if (StringUtils.isNullOrWhitespace(string)) {
			if (DebugLog.isEnabled(DebugType.Clothing)) {
				DebugLog.Clothing.debugln("No model specified by item: " + clothingItem.m_Name);
			}
		} else {
			string = this.processModelFileName(string);
			String string2 = itemVisual.getTextureChoice(clothingItem);
			ImmutableColor immutableColor = itemVisual.getTint(clothingItem);
			String string3 = clothingItem.m_AttachBone;
			String string4 = clothingItem.m_Shader;
			ModelInstance modelInstance;
			if (string3 != null && string3.length() > 0) {
				modelInstance = this.addStatic(string, string2, string3, string4);
			} else {
				modelInstance = ModelManager.instance.newAdditionalModelInstance(string, string2, (IsoGameCharacter)null, this.modelInstance.AnimPlayer, string4);
			}

			if (modelInstance != null) {
				this.postProcessNewItemInstance(this.modelInstance, modelInstance, immutableColor);
				modelInstance.setItemVisual(itemVisual);
			}
		}
	}

	private boolean isItemModelHidden(ItemVisuals itemVisuals, ItemVisual itemVisual) {
		BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
		return PopTemplateManager.instance.isItemModelHidden(bodyLocationGroup, itemVisuals, itemVisual);
	}

	private String processModelFileName(String string) {
		string = string.replaceAll("\\\\", "/");
		string = string.toLowerCase(Locale.ENGLISH);
		return string;
	}

	private void postProcessNewItemInstance(ModelInstance modelInstance, ModelInstance modelInstance2, ImmutableColor immutableColor) {
		modelInstance2.depthBias = 0.0F;
		modelInstance2.matrixModel = this.modelInstance;
		modelInstance2.tintR = immutableColor.r;
		modelInstance2.tintG = immutableColor.g;
		modelInstance2.tintB = immutableColor.b;
		modelInstance2.AnimPlayer = this.modelInstance.AnimPlayer;
		modelInstance.sub.add(modelInstance2);
		modelInstance2.setOwner(this);
	}

	private void DoCharacterModelEquipped() {
		ModelInstance modelInstance;
		if (!StringUtils.isNullOrWhitespace(this.primaryHandModelName)) {
			modelInstance = this.addStatic(this.primaryHandModelName, "Bip01_Prop1");
			this.postProcessNewItemInstance(this.modelInstance, modelInstance, ImmutableColor.white);
		}

		if (!StringUtils.isNullOrWhitespace(this.secondaryHandModelName)) {
			modelInstance = this.addStatic(this.secondaryHandModelName, "Bip01_Prop2");
			this.postProcessNewItemInstance(this.modelInstance, modelInstance, ImmutableColor.white);
		}

		for (int int1 = 0; int1 < this.attachedModelNames.size(); ++int1) {
			AttachedModelName attachedModelName = this.attachedModelNames.get(int1);
			ModelInstance modelInstance2 = ModelManager.instance.addStatic((ModelInstance)null, attachedModelName.modelName, attachedModelName.attachmentNameSelf, attachedModelName.attachmentNameParent);
			this.postProcessNewItemInstance(this.modelInstance, modelInstance2, ImmutableColor.white);
			if (attachedModelName.bloodLevel > 0.0F && !Core.getInstance().getOptionSimpleWeaponTextures()) {
				ModelInstanceTextureInitializer modelInstanceTextureInitializer = ModelInstanceTextureInitializer.alloc();
				modelInstanceTextureInitializer.init(modelInstance2, attachedModelName.bloodLevel);
				modelInstance2.setTextureInitializer(modelInstanceTextureInitializer);
			}

			for (int int2 = 0; int2 < attachedModelName.getChildCount(); ++int2) {
				AttachedModelName attachedModelName2 = attachedModelName.getChildByIndex(int2);
				ModelInstance modelInstance3 = ModelManager.instance.addStatic(modelInstance2, attachedModelName2.modelName, attachedModelName2.attachmentNameSelf, attachedModelName2.attachmentNameParent);
				modelInstance2.sub.remove(modelInstance3);
				this.postProcessNewItemInstance(modelInstance2, modelInstance3, ImmutableColor.white);
			}
		}
	}

	private ModelInstance addStatic(String string, String string2) {
		String string3 = string;
		String string4 = string;
		String string5 = null;
		ModelScript modelScript = ScriptManager.instance.getModelScript(string);
		if (modelScript != null) {
			string3 = modelScript.getMeshName();
			string4 = modelScript.getTextureName();
			string5 = modelScript.getShaderName();
		}

		return this.addStatic(string3, string4, string2, string5);
	}

	private ModelInstance addStatic(String string, String string2, String string3, String string4) {
		if (DebugLog.isEnabled(DebugType.Animation)) {
			DebugLog.Animation.debugln("Adding Static Model:" + string);
		}

		Model model = ModelManager.instance.tryGetLoadedModel(string, string2, true, string4, false);
		if (model == null) {
			ModelManager.instance.loadStaticModel(string.toLowerCase(), string2, string4);
			model = ModelManager.instance.getLoadedModel(string, string2, true, string4);
			if (model == null) {
				DebugLog.General.error("ModelManager.addStatic> Model not found. model:" + string + " tex:" + string2);
				return null;
			}
		}

		ModelInstance modelInstance = ModelManager.instance.newInstance(model, (IsoGameCharacter)null, this.modelInstance.AnimPlayer);
		modelInstance.parent = this.modelInstance;
		if (this.modelInstance.AnimPlayer != null) {
			modelInstance.parentBone = this.modelInstance.AnimPlayer.getSkinningBoneIndex(string3, modelInstance.parentBone);
			modelInstance.parentBoneName = string3;
		}

		return modelInstance;
	}

	private AnimatedModel.StateInfo stateInfoMain() {
		int int1 = SpriteRenderer.instance.getMainStateIndex();
		return this.stateInfos[int1];
	}

	private AnimatedModel.StateInfo stateInfoRender() {
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		return this.stateInfos[int1];
	}

	public void update() {
		GameProfiler.getInstance().invokeAndMeasure("AnimatedModel.Update", this, AnimatedModel::updateInternal);
	}

	private void updateInternal() {
		this.initOutfit();
		if (this.bClothingChanged) {
			this.bClothingChanged = false;
			this.setModelData(this.baseVisual, this.itemVisuals);
		}

		this.modelInstance.SetForceDir(this.angle);
		GameTime gameTime = GameTime.getInstance();
		float float1 = gameTime.FPSMultiplier;
		if (this.bAnimate) {
			if (UIManager.useUIFBO) {
				gameTime.FPSMultiplier *= GameWindow.averageFPS / (float)Core.OptionUIRenderFPS;
			}

			this.actionContext.update();
			this.advancedAnimator.update();
			this.animPlayer.Update();
			int int1 = SpriteRenderer.instance.getMainStateIndex();
			AnimatedModel.StateInfo stateInfo = this.stateInfos[int1];
			if (!stateInfo.readyData.isEmpty()) {
				ModelInstance modelInstance = ((AnimatedModel.AnimatedModelInstanceRenderData)stateInfo.readyData.get(0)).modelInstance;
				if (modelInstance != this.modelInstance && modelInstance.AnimPlayer != this.modelInstance.AnimPlayer) {
					modelInstance.Update();
				}
			}

			gameTime.FPSMultiplier = float1;
		} else {
			gameTime.FPSMultiplier = 100.0F;
			try {
				this.advancedAnimator.update();
			} finally {
				gameTime.FPSMultiplier = float1;
			}

			if (this.trackTime > 0.0F && this.animPlayer.getMultiTrack().getTrackCount() > 0) {
				((AnimationTrack)this.animPlayer.getMultiTrack().getTracks().get(0)).setCurrentTimeValue(this.trackTime);
			}

			this.animPlayer.Update(0.0F);
		}
	}

	private boolean isModelInstanceReady(ModelInstance modelInstance) {
		if (modelInstance.model != null && modelInstance.model.isReady()) {
			if (modelInstance.model.Mesh.isReady() && modelInstance.model.Mesh.vb != null) {
				for (int int1 = 0; int1 < modelInstance.sub.size(); ++int1) {
					ModelInstance modelInstance2 = (ModelInstance)modelInstance.sub.get(int1);
					if (!this.isModelInstanceReady(modelInstance2)) {
						return false;
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void incrementRefCount(ModelInstance modelInstance) {
		++modelInstance.renderRefCount;
		for (int int1 = 0; int1 < modelInstance.sub.size(); ++int1) {
			ModelInstance modelInstance2 = (ModelInstance)modelInstance.sub.get(int1);
			this.incrementRefCount(modelInstance2);
		}
	}

	private void initRenderData(AnimatedModel.StateInfo stateInfo, AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData, ModelInstance modelInstance) {
		AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData2 = ((AnimatedModel.AnimatedModelInstanceRenderData)instDataPool.alloc()).init(modelInstance);
		stateInfo.instData.add(animatedModelInstanceRenderData2);
		animatedModelInstanceRenderData2.transformToParent(animatedModelInstanceRenderData);
		for (int int1 = 0; int1 < modelInstance.sub.size(); ++int1) {
			ModelInstance modelInstance2 = (ModelInstance)modelInstance.sub.get(int1);
			this.initRenderData(stateInfo, animatedModelInstanceRenderData2, modelInstance2);
		}
	}

	public boolean isReadyToRender() {
		if (!this.animPlayer.isReady()) {
			return false;
		} else {
			return this.isModelInstanceReady(this.modelInstance);
		}
	}

	public int renderMain() {
		AnimatedModel.StateInfo stateInfo = this.stateInfoMain();
		if (this.modelInstance != null) {
			if (this.bUpdateTextures) {
				this.bUpdateTextures = false;
				this.textureCreator = ModelInstanceTextureCreator.alloc();
				this.textureCreator.init(this.getVisual(), this.itemVisuals, this.modelInstance);
			}

			this.incrementRefCount(this.modelInstance);
			instDataPool.release((List)stateInfo.instData);
			stateInfo.instData.clear();
			if (!stateInfo.bModelsReady && this.isReadyToRender()) {
				float float1 = GameTime.getInstance().FPSMultiplier;
				GameTime.getInstance().FPSMultiplier = 100.0F;
				try {
					this.advancedAnimator.update();
				} finally {
					GameTime.getInstance().FPSMultiplier = float1;
				}

				this.animPlayer.Update(0.0F);
				stateInfo.bModelsReady = true;
			}

			this.initRenderData(stateInfo, (AnimatedModel.AnimatedModelInstanceRenderData)null, this.modelInstance);
		}

		stateInfo.modelInstance = this.modelInstance;
		stateInfo.textureCreator = this.textureCreator != null && !this.textureCreator.isRendered() ? this.textureCreator : null;
		for (int int1 = 0; int1 < stateInfo.readyData.size(); ++int1) {
			AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData = (AnimatedModel.AnimatedModelInstanceRenderData)stateInfo.readyData.get(int1);
			animatedModelInstanceRenderData.init(animatedModelInstanceRenderData.modelInstance);
			animatedModelInstanceRenderData.transformToParent(stateInfo.getParentData(animatedModelInstanceRenderData.modelInstance));
		}

		stateInfo.bRendered = false;
		return SpriteRenderer.instance.getMainStateIndex();
	}

	public boolean isRendered() {
		return this.stateInfoRender().bRendered;
	}

	private void doneWithTextureCreator(ModelInstanceTextureCreator modelInstanceTextureCreator) {
		if (modelInstanceTextureCreator != null) {
			for (int int1 = 0; int1 < this.stateInfos.length; ++int1) {
				if (this.stateInfos[int1].textureCreator == modelInstanceTextureCreator) {
					return;
				}
			}

			if (modelInstanceTextureCreator.isRendered()) {
				modelInstanceTextureCreator.postRender();
				if (modelInstanceTextureCreator == this.textureCreator) {
					this.textureCreator = null;
				}
			} else if (modelInstanceTextureCreator != this.textureCreator) {
				modelInstanceTextureCreator.postRender();
			}
		}
	}

	private void release(ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData = (AnimatedModel.AnimatedModelInstanceRenderData)arrayList.get(int1);
			if (animatedModelInstanceRenderData.modelInstance.getTextureInitializer() != null) {
				animatedModelInstanceRenderData.modelInstance.getTextureInitializer().postRender();
			}

			ModelManager.instance.derefModelInstance(animatedModelInstanceRenderData.modelInstance);
		}

		instDataPool.release((List)arrayList);
	}

	public void postRender(boolean boolean1) {
		int int1 = SpriteRenderer.instance.getMainStateIndex();
		AnimatedModel.StateInfo stateInfo = this.stateInfos[int1];
		ModelInstanceTextureCreator modelInstanceTextureCreator = stateInfo.textureCreator;
		stateInfo.textureCreator = null;
		this.doneWithTextureCreator(modelInstanceTextureCreator);
		stateInfo.modelInstance = null;
		if (this.bAnimate && stateInfo.bRendered) {
			this.release(stateInfo.readyData);
			stateInfo.readyData.clear();
			stateInfo.readyData.addAll(stateInfo.instData);
			stateInfo.instData.clear();
		} else if (!this.bAnimate) {
		}

		this.release(stateInfo.instData);
		stateInfo.instData.clear();
	}

	public void DoRender(ModelCamera modelCamera) {
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		AnimatedModel.StateInfo stateInfo = this.stateInfos[int1];
		this.bReady = true;
		ModelInstanceTextureCreator modelInstanceTextureCreator = stateInfo.textureCreator;
		if (modelInstanceTextureCreator != null && !modelInstanceTextureCreator.isRendered()) {
			modelInstanceTextureCreator.render();
			if (!modelInstanceTextureCreator.isRendered()) {
				this.bReady = false;
			}
		}

		if (!this.isModelInstanceReady(this.modelInstance)) {
			this.bReady = false;
		}

		for (int int2 = 0; int2 < stateInfo.instData.size(); ++int2) {
			AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData = (AnimatedModel.AnimatedModelInstanceRenderData)stateInfo.instData.get(int2);
			ModelInstanceTextureInitializer modelInstanceTextureInitializer = animatedModelInstanceRenderData.modelInstance.getTextureInitializer();
			if (modelInstanceTextureInitializer != null && !modelInstanceTextureInitializer.isRendered()) {
				modelInstanceTextureInitializer.render();
				if (!modelInstanceTextureInitializer.isRendered()) {
					this.bReady = false;
				}
			}
		}

		if (this.bReady && !stateInfo.bModelsReady) {
			this.bReady = false;
		}

		if (this.bReady || !stateInfo.readyData.isEmpty()) {
			GL11.glPushClientAttrib(-1);
			GL11.glPushAttrib(1048575);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glEnable(3008);
			GL11.glAlphaFunc(516, 0.0F);
			modelCamera.Begin();
			this.StartCharacter();
			this.Render();
			this.EndCharacter();
			modelCamera.End();
			GL11.glDepthFunc(519);
			GL11.glPopAttrib();
			GL11.glPopClientAttrib();
			Texture.lastTextureID = -1;
			SpriteRenderer.ringBuffer.restoreVBOs = true;
			stateInfo.bRendered = this.bReady;
		}
	}

	public void DoRender(int int1, int int2, int int3, int int4, float float1, float float2) {
		GL11.glClear(256);
		this.uiModelCamera.x = int1;
		this.uiModelCamera.y = int2;
		this.uiModelCamera.w = int3;
		this.uiModelCamera.h = int4;
		this.uiModelCamera.sizeV = float1;
		this.uiModelCamera.m_animPlayerAngle = float2;
		this.DoRender(this.uiModelCamera);
	}

	public void DoRenderToWorld(float float1, float float2, float float3, float float4) {
		worldModelCamera.x = float1;
		worldModelCamera.y = float2;
		worldModelCamera.z = float3;
		worldModelCamera.angle = float4;
		this.DoRender(worldModelCamera);
	}

	private void debugDrawAxes() {
		if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
			Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 1.0F, 4.0F);
		}
	}

	private void StartCharacter() {
		GL11.glEnable(2929);
		GL11.glEnable(3042);
		if (UIManager.useUIFBO) {
			GL14.glBlendFuncSeparate(770, 771, 1, 771);
		} else {
			GL11.glBlendFunc(770, 771);
		}

		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glDisable(3089);
		GL11.glDepthMask(true);
	}

	private void EndCharacter() {
		GL11.glDepthMask(false);
		GL11.glViewport(0, 0, Core.width, Core.height);
	}

	private void Render() {
		int int1 = SpriteRenderer.instance.getRenderStateIndex();
		AnimatedModel.StateInfo stateInfo = this.stateInfos[int1];
		ModelInstance modelInstance = stateInfo.modelInstance;
		if (modelInstance == null) {
			boolean boolean1 = true;
		} else {
			ArrayList arrayList = this.bReady ? stateInfo.instData : stateInfo.readyData;
			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData = (AnimatedModel.AnimatedModelInstanceRenderData)arrayList.get(int2);
				this.DrawChar(animatedModelInstanceRenderData);
			}
		}

		this.debugDrawAxes();
	}

	private void DrawChar(AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData) {
		ModelInstance modelInstance = animatedModelInstanceRenderData.modelInstance;
		FloatBuffer floatBuffer = animatedModelInstanceRenderData.matrixPalette;
		if (modelInstance != null) {
			if (modelInstance.AnimPlayer != null) {
				if (modelInstance.AnimPlayer.hasSkinningData()) {
					if (modelInstance.model != null) {
						if (modelInstance.model.isReady()) {
							if (modelInstance.tex != null || modelInstance.model.tex != null) {
								GL11.glEnable(2884);
								GL11.glCullFace(1028);
								GL11.glEnable(2929);
								GL11.glEnable(3008);
								GL11.glDepthFunc(513);
								GL11.glDepthRange(0.0, 1.0);
								GL11.glAlphaFunc(516, 0.01F);
								if (modelInstance.model.Effect == null) {
									modelInstance.model.CreateShader("basicEffect");
								}

								Shader shader = modelInstance.model.Effect;
								int int1;
								if (shader != null) {
									shader.Start();
									if (modelInstance.model.bStatic) {
										shader.setTransformMatrix(animatedModelInstanceRenderData.xfrm, true);
									} else {
										shader.setMatrixPalette(floatBuffer, true);
									}

									shader.setLight(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, modelInstance);
									shader.setLight(1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, modelInstance);
									shader.setLight(2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, modelInstance);
									shader.setLight(3, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, modelInstance);
									shader.setLight(4, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, modelInstance);
									float float1 = 0.7F;
									for (int1 = 0; int1 < this.lights.length; ++int1) {
										IsoGridSquare.ResultLight resultLight = this.lights[int1];
										if (resultLight.radius > 0) {
											shader.setLight(int1, (float)resultLight.x + 0.5F, (float)resultLight.y + 0.5F, (float)resultLight.z + 0.5F, resultLight.r * float1, resultLight.g * float1, resultLight.b * float1, (float)resultLight.radius, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, (IsoMovingObject)null);
										}
									}

									if (modelInstance.tex != null) {
										shader.setTexture(modelInstance.tex, "Texture", 0);
									} else if (modelInstance.model.tex != null) {
										shader.setTexture(modelInstance.model.tex, "Texture", 0);
									}

									float float2;
									if (this.bOutside) {
										float2 = ModelInstance.MODEL_LIGHT_MULT_OUTSIDE;
										shader.setLight(3, this.lightsOriginX - 2.0F, this.lightsOriginY - 2.0F, this.lightsOriginZ + 1.0F, this.ambient.r * float2 / 4.0F, this.ambient.g * float2 / 4.0F, this.ambient.b * float2 / 4.0F, 5000.0F, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, (IsoMovingObject)null);
										shader.setLight(4, this.lightsOriginX + 2.0F, this.lightsOriginY + 2.0F, this.lightsOriginZ + 1.0F, this.ambient.r * float2 / 4.0F, this.ambient.g * float2 / 4.0F, this.ambient.b * float2 / 4.0F, 5000.0F, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, (IsoMovingObject)null);
									} else if (this.bRoom) {
										float2 = ModelInstance.MODEL_LIGHT_MULT_ROOM;
										shader.setLight(4, this.lightsOriginX + 2.0F, this.lightsOriginY + 2.0F, this.lightsOriginZ + 1.0F, this.ambient.r * float2 / 4.0F, this.ambient.g * float2 / 4.0F, this.ambient.b * float2 / 4.0F, 5000.0F, animatedModelInstanceRenderData.m_animPlayerAngle, this.lightsOriginX, this.lightsOriginY, this.lightsOriginZ, (IsoMovingObject)null);
									}

									shader.setDepthBias(modelInstance.depthBias / 50.0F);
									shader.setAmbient(this.ambient.r * 0.45F, this.ambient.g * 0.45F, this.ambient.b * 0.45F);
									shader.setLightingAmount(1.0F);
									shader.setHueShift(modelInstance.hue);
									shader.setTint(modelInstance.tintR, modelInstance.tintG, modelInstance.tintB);
									shader.setAlpha(this.m_alpha);
								}

								modelInstance.model.Mesh.Draw(shader);
								if (shader != null) {
									shader.End();
								}

								if (Core.bDebug && DebugOptions.instance.ModelRenderLights.getValue() && modelInstance.parent == null) {
									Model model;
									if (this.lights[0].radius > 0) {
										model = modelInstance.model;
										Model.debugDrawLightSource((float)this.lights[0].x, (float)this.lights[0].y, (float)this.lights[0].z, 0.0F, 0.0F, 0.0F, -animatedModelInstanceRenderData.m_animPlayerAngle);
									}

									if (this.lights[1].radius > 0) {
										model = modelInstance.model;
										Model.debugDrawLightSource((float)this.lights[1].x, (float)this.lights[1].y, (float)this.lights[1].z, 0.0F, 0.0F, 0.0F, -animatedModelInstanceRenderData.m_animPlayerAngle);
									}

									if (this.lights[2].radius > 0) {
										model = modelInstance.model;
										Model.debugDrawLightSource((float)this.lights[2].x, (float)this.lights[2].y, (float)this.lights[2].z, 0.0F, 0.0F, 0.0F, -animatedModelInstanceRenderData.m_animPlayerAngle);
									}
								}

								if (Core.bDebug && DebugOptions.instance.ModelRenderBones.getValue()) {
									GL11.glDisable(2929);
									GL11.glDisable(3553);
									GL11.glLineWidth(1.0F);
									GL11.glBegin(1);
									for (int int2 = 0; int2 < modelInstance.AnimPlayer.modelTransforms.length; ++int2) {
										int1 = (Integer)modelInstance.AnimPlayer.getSkinningData().SkeletonHierarchy.get(int2);
										if (int1 >= 0) {
											Color color = Model.debugDrawColours[int2 % Model.debugDrawColours.length];
											GL11.glColor3f(color.r, color.g, color.b);
											Matrix4f matrix4f = modelInstance.AnimPlayer.modelTransforms[int2];
											GL11.glVertex3f(matrix4f.m03, matrix4f.m13, matrix4f.m23);
											matrix4f = modelInstance.AnimPlayer.modelTransforms[int1];
											GL11.glVertex3f(matrix4f.m03, matrix4f.m13, matrix4f.m23);
										}
									}

									GL11.glEnd();
									GL11.glColor3f(1.0F, 1.0F, 1.0F);
									GL11.glEnable(2929);
								}
							}
						}
					}
				}
			}
		}
	}

	public void releaseAnimationPlayer() {
		if (this.animPlayer != null) {
			this.animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.animPlayer);
		}
	}

	public void OnAnimEvent(AnimLayer animLayer, AnimEvent animEvent) {
		if (!StringUtils.isNullOrWhitespace(animEvent.m_EventName)) {
			int int1 = animLayer.getDepth();
			this.actionContext.reportEvent(int1, animEvent.m_EventName);
		}
	}

	public AnimationPlayer getAnimationPlayer() {
		Model model = this.getVisual().getModel();
		if (this.animPlayer != null && this.animPlayer.getModel() != model) {
			this.animPlayer = (AnimationPlayer)Pool.tryRelease((IPooledObject)this.animPlayer);
		}

		if (this.animPlayer == null) {
			this.animPlayer = AnimationPlayer.alloc(model);
		}

		return this.animPlayer;
	}

	public void actionStateChanged(ActionContext actionContext) {
		this.advancedAnimator.SetState(actionContext.getCurrentStateName(), PZArrayUtil.listConvert(actionContext.getChildStates(), (var0)->{
			return var0.name;
		}));
	}

	public AnimationPlayerRecorder getAnimationPlayerRecorder() {
		return null;
	}

	public boolean isAnimationRecorderActive() {
		return false;
	}

	public ActionContext getActionContext() {
		return this.actionContext;
	}

	public AdvancedAnimator getAdvancedAnimator() {
		return this.advancedAnimator;
	}

	public ModelInstance getModelInstance() {
		return this.modelInstance;
	}

	public String GetAnimSetName() {
		return this.animSetName;
	}

	public String getUID() {
		return this.m_UID;
	}

	public static final class StateInfo {
		ModelInstance modelInstance;
		ModelInstanceTextureCreator textureCreator;
		final ArrayList instData = new ArrayList();
		final ArrayList readyData = new ArrayList();
		boolean bModelsReady;
		boolean bRendered;

		AnimatedModel.AnimatedModelInstanceRenderData getParentData(ModelInstance modelInstance) {
			for (int int1 = 0; int1 < this.readyData.size(); ++int1) {
				AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData = (AnimatedModel.AnimatedModelInstanceRenderData)this.readyData.get(int1);
				if (animatedModelInstanceRenderData.modelInstance == modelInstance.parent) {
					return animatedModelInstanceRenderData;
				}
			}

			return null;
		}
	}

	private final class UIModelCamera extends ModelCamera {
		int x;
		int y;
		int w;
		int h;
		float sizeV;
		float m_animPlayerAngle;

		public void Begin() {
			GL11.glViewport(this.x, this.y, this.w, this.h);
			GL11.glMatrixMode(5889);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			float float1 = (float)this.w / (float)this.h;
			if (AnimatedModel.this.flipY) {
				GL11.glOrtho((double)(-this.sizeV * float1), (double)(this.sizeV * float1), (double)this.sizeV, (double)(-this.sizeV), -100.0, 100.0);
			} else {
				GL11.glOrtho((double)(-this.sizeV * float1), (double)(this.sizeV * float1), (double)(-this.sizeV), (double)this.sizeV, -100.0, 100.0);
			}

			float float2 = Math.sqrt(2048.0F);
			GL11.glScalef(-float2, float2, float2);
			GL11.glMatrixMode(5888);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			if (AnimatedModel.this.bIsometric) {
				GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotated((double)(this.m_animPlayerAngle * 57.295776F + 45.0F), 0.0, 1.0, 0.0);
			} else {
				GL11.glRotated((double)(this.m_animPlayerAngle * 57.295776F), 0.0, 1.0, 0.0);
			}

			GL11.glTranslatef(AnimatedModel.this.offset.x(), AnimatedModel.this.offset.y(), AnimatedModel.this.offset.z());
		}

		public void End() {
			GL11.glMatrixMode(5889);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5888);
			GL11.glPopMatrix();
		}
	}

	private static final class AnimatedModelInstanceRenderData {
		ModelInstance modelInstance;
		FloatBuffer matrixPalette;
		public final org.joml.Matrix4f xfrm = new org.joml.Matrix4f();
		float m_animPlayerAngle;

		AnimatedModel.AnimatedModelInstanceRenderData init(ModelInstance modelInstance) {
			this.modelInstance = modelInstance;
			this.xfrm.identity();
			this.m_animPlayerAngle = Float.NaN;
			if (modelInstance.AnimPlayer != null) {
				this.m_animPlayerAngle = modelInstance.AnimPlayer.getRenderedAngle();
				if (!modelInstance.model.bStatic) {
					SkinningData skinningData = (SkinningData)modelInstance.model.Tag;
					if (Core.bDebug && skinningData == null) {
						DebugLog.General.warn("skinningData is null, matrixPalette may be invalid");
					}

					Matrix4f[] matrix4fArray = modelInstance.AnimPlayer.getSkinTransforms(skinningData);
					if (this.matrixPalette == null || this.matrixPalette.capacity() < matrix4fArray.length * 16) {
						this.matrixPalette = BufferUtils.createFloatBuffer(matrix4fArray.length * 16);
					}

					this.matrixPalette.clear();
					for (int int1 = 0; int1 < matrix4fArray.length; ++int1) {
						matrix4fArray[int1].store(this.matrixPalette);
					}

					this.matrixPalette.flip();
				}
			}

			if (modelInstance.getTextureInitializer() != null) {
				modelInstance.getTextureInitializer().renderMain();
			}

			return this;
		}

		public AnimatedModel.AnimatedModelInstanceRenderData transformToParent(AnimatedModel.AnimatedModelInstanceRenderData animatedModelInstanceRenderData) {
			if (!(this.modelInstance instanceof VehicleModelInstance) && !(this.modelInstance instanceof VehicleSubModelInstance)) {
				if (animatedModelInstanceRenderData == null) {
					return this;
				} else {
					this.xfrm.set((Matrix4fc)animatedModelInstanceRenderData.xfrm);
					this.xfrm.transpose();
					org.joml.Matrix4f matrix4f = (org.joml.Matrix4f)((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).alloc();
					ModelAttachment modelAttachment = animatedModelInstanceRenderData.modelInstance.getAttachmentById(this.modelInstance.attachmentNameParent);
					if (modelAttachment == null) {
						if (this.modelInstance.parentBoneName != null && animatedModelInstanceRenderData.modelInstance.AnimPlayer != null) {
							ModelInstanceRenderData.applyBoneTransform(animatedModelInstanceRenderData.modelInstance, this.modelInstance.parentBoneName, this.xfrm);
						}
					} else {
						ModelInstanceRenderData.applyBoneTransform(animatedModelInstanceRenderData.modelInstance, modelAttachment.getBone(), this.xfrm);
						ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, matrix4f);
						this.xfrm.mul((Matrix4fc)matrix4f);
					}

					ModelAttachment modelAttachment2 = this.modelInstance.getAttachmentById(this.modelInstance.attachmentNameSelf);
					if (modelAttachment2 != null) {
						ModelInstanceRenderData.makeAttachmentTransform(modelAttachment2, matrix4f);
						matrix4f.invert();
						this.xfrm.mul((Matrix4fc)matrix4f);
					}

					if (this.modelInstance.model.Mesh != null && this.modelInstance.model.Mesh.isReady() && this.modelInstance.model.Mesh.m_transform != null) {
						this.xfrm.mul((Matrix4fc)this.modelInstance.model.Mesh.m_transform);
					}

					if (this.modelInstance.scale != 1.0F) {
						this.xfrm.scale(this.modelInstance.scale);
					}

					this.xfrm.transpose();
					((BaseVehicle.Matrix4fObjectPool)BaseVehicle.TL_matrix4f_pool.get()).release(matrix4f);
					return this;
				}
			} else {
				return this;
			}
		}
	}

	private static final class WorldModelCamera extends ModelCamera {
		float x;
		float y;
		float z;
		float angle;

		public void Begin() {
			Core.getInstance().DoPushIsoStuff(this.x, this.y, this.z, this.angle, false);
			GL11.glDepthMask(true);
		}

		public void End() {
			Core.getInstance().DoPopIsoStuff();
		}
	}
}
