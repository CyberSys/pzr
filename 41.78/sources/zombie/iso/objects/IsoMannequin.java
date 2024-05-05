package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.gameStates.GameLoadingState;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelScript;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public class IsoMannequin extends IsoObject implements IHumanVisual {
	private static final ColorInfo inf = new ColorInfo();
	private boolean bInit = false;
	private boolean bFemale = false;
	private boolean bZombie = false;
	private boolean bSkeleton = false;
	private String mannequinScriptName = null;
	private String modelScriptName = null;
	private String textureName = null;
	private String animSet = null;
	private String animState = null;
	private String pose = null;
	private String outfit = null;
	private final HumanVisual humanVisual = new HumanVisual(this);
	private final ItemVisuals itemVisuals = new ItemVisuals();
	private final WornItems wornItems = new WornItems(BodyLocations.getGroup("Human"));
	private MannequinScript mannequinScript = null;
	private ModelScript modelScript = null;
	private final IsoMannequin.PerPlayer[] perPlayer = new IsoMannequin.PerPlayer[4];
	private boolean bAnimate = false;
	private AnimatedModel animatedModel = null;
	private IsoMannequin.Drawer[] drawers = null;
	private float screenX;
	private float screenY;
	private static final IsoMannequin.StaticPerPlayer[] staticPerPlayer = new IsoMannequin.StaticPerPlayer[4];

	public IsoMannequin(IsoCell cell) {
		super(cell);
		for (int int1 = 0; int1 < 4; ++int1) {
			this.perPlayer[int1] = new IsoMannequin.PerPlayer();
		}
	}

	public IsoMannequin(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		for (int int1 = 0; int1 < 4; ++int1) {
			this.perPlayer[int1] = new IsoMannequin.PerPlayer();
		}
	}

	public String getObjectName() {
		return "Mannequin";
	}

	public HumanVisual getHumanVisual() {
		return this.humanVisual;
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		this.wornItems.getItemVisuals(itemVisuals);
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

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		if (inventoryItem instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)inventoryItem).getBodyLocation())) {
			return true;
		} else {
			return inventoryItem instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)inventoryItem).canBeEquipped());
		}
	}

	public String getMannequinScriptName() {
		return this.mannequinScriptName;
	}

	public void setMannequinScriptName(String string) {
		if (!StringUtils.isNullOrWhitespace(string)) {
			if (ScriptManager.instance.getMannequinScript(string) != null) {
				this.mannequinScriptName = string;
				this.bInit = true;
				this.mannequinScript = null;
				this.textureName = null;
				this.animSet = null;
				this.animState = null;
				this.pose = null;
				this.outfit = null;
				this.humanVisual.clear();
				this.itemVisuals.clear();
				this.wornItems.clear();
				this.initMannequinScript();
				this.initModelScript();
				if (this.outfit == null) {
					Outfit outfit = OutfitManager.instance.GetRandomNonProfessionalOutfit(this.bFemale);
					this.humanVisual.dressInNamedOutfit(outfit.m_Name, this.itemVisuals);
				} else if (!"none".equalsIgnoreCase(this.outfit)) {
					this.humanVisual.dressInNamedOutfit(this.outfit, this.itemVisuals);
				}

				this.humanVisual.setHairModel("");
				this.humanVisual.setBeardModel("");
				this.createInventory(this.itemVisuals);
				this.validateSkinTexture();
				this.validatePose();
				this.syncModel();
			}
		}
	}

	public String getPose() {
		return this.pose;
	}

	public void setRenderDirection(IsoDirections directions) {
		int int1 = IsoCamera.frameState.playerIndex;
		if (directions != this.perPlayer[int1].renderDirection) {
			this.perPlayer[int1].renderDirection = directions;
		}
	}

	public void rotate(IsoDirections directions) {
		if (directions != null && directions != IsoDirections.Max) {
			this.dir = directions;
			for (int int1 = 0; int1 < 4; ++int1) {
				this.perPlayer[int1].atlasTex = null;
			}

			if (GameServer.bServer) {
				this.sendObjectChange("rotate");
			}
		}
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("rotate".equals(string)) {
			byteBuffer.put((byte)this.dir.index());
		} else {
			super.saveChange(string, kahluaTable, byteBuffer);
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("rotate".equals(string)) {
			byte byte1 = byteBuffer.get();
			this.rotate(IsoDirections.fromIndex(byte1));
		} else {
			super.loadChange(string, byteBuffer);
		}
	}

	public void getVariables(Map map) {
		map.put("Female", this.bFemale ? "true" : "false");
		map.put("Pose", this.getPose());
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.dir = IsoDirections.fromIndex(byteBuffer.get());
		this.bInit = byteBuffer.get() == 1;
		this.bFemale = byteBuffer.get() == 1;
		this.bZombie = byteBuffer.get() == 1;
		this.bSkeleton = byteBuffer.get() == 1;
		if (int1 >= 191) {
			this.mannequinScriptName = GameWindow.ReadString(byteBuffer);
		}

		this.pose = GameWindow.ReadString(byteBuffer);
		this.humanVisual.load(byteBuffer, int1);
		this.textureName = this.humanVisual.getSkinTexture();
		this.wornItems.clear();
		if (this.container == null) {
			this.container = new ItemContainer("mannequin", this.getSquare(), this);
			this.container.setExplored(true);
		}

		this.container.clear();
		if (byteBuffer.get() == 1) {
			try {
				this.container.ID = byteBuffer.getInt();
				ArrayList arrayList = this.container.load(byteBuffer, int1);
				byte byte1 = byteBuffer.get();
				for (int int2 = 0; int2 < byte1; ++int2) {
					String string = GameWindow.ReadString(byteBuffer);
					short short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string) != null) {
						this.wornItems.setItem(string, (InventoryItem)arrayList.get(short1));
					}
				}
			} catch (Exception exception) {
				if (this.container != null) {
					DebugLog.log("Failed to stream in container ID: " + this.container.ID);
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		ItemContainer itemContainer = this.container;
		this.container = null;
		super.save(byteBuffer, boolean1);
		this.container = itemContainer;
		byteBuffer.put((byte)this.dir.index());
		byteBuffer.put((byte)(this.bInit ? 1 : 0));
		byteBuffer.put((byte)(this.bFemale ? 1 : 0));
		byteBuffer.put((byte)(this.bZombie ? 1 : 0));
		byteBuffer.put((byte)(this.bSkeleton ? 1 : 0));
		GameWindow.WriteString(byteBuffer, this.mannequinScriptName);
		GameWindow.WriteString(byteBuffer, this.pose);
		this.humanVisual.save(byteBuffer);
		if (itemContainer != null) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(itemContainer.ID);
			ArrayList arrayList = itemContainer.save(byteBuffer);
			if (this.wornItems.size() > 127) {
				throw new RuntimeException("too many worn items");
			}

			byteBuffer.put((byte)this.wornItems.size());
			this.wornItems.forEach((boolean1x)->{
				GameWindow.WriteString(byteBuffer, boolean1x.getLocation());
				byteBuffer.putShort((short)arrayList.indexOf(boolean1x.getItem()));
			});
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void saveState(ByteBuffer byteBuffer) throws IOException {
		if (!this.bInit) {
			this.initOutfit();
		}

		this.save(byteBuffer);
	}

	public void loadState(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.get();
		byteBuffer.get();
		this.load(byteBuffer, 195);
		this.initOutfit();
		this.validateSkinTexture();
		this.validatePose();
		this.syncModel();
	}

	public void addToWorld() {
		super.addToWorld();
		this.initOutfit();
		this.validateSkinTexture();
		this.validatePose();
		this.syncModel();
	}

	private void initMannequinScript() {
		if (!StringUtils.isNullOrWhitespace(this.mannequinScriptName)) {
			this.mannequinScript = ScriptManager.instance.getMannequinScript(this.mannequinScriptName);
		}

		if (this.mannequinScript == null) {
			this.modelScriptName = this.bFemale ? "FemaleBody" : "MaleBody";
			this.textureName = this.bFemale ? "F_Mannequin_White" : "M_Mannequin_White";
			this.animSet = "mannequin";
			this.animState = this.bFemale ? "female" : "male";
			this.outfit = null;
		} else {
			this.bFemale = this.mannequinScript.isFemale();
			this.modelScriptName = this.mannequinScript.getModelScriptName();
			if (this.textureName == null) {
				this.textureName = this.mannequinScript.getTexture();
			}

			this.animSet = this.mannequinScript.getAnimSet();
			this.animState = this.mannequinScript.getAnimState();
			if (this.pose == null) {
				this.pose = this.mannequinScript.getPose();
			}

			if (this.outfit == null) {
				this.outfit = this.mannequinScript.getOutfit();
			}
		}
	}

	private void initModelScript() {
		if (!StringUtils.isNullOrWhitespace(this.modelScriptName)) {
			this.modelScript = ScriptManager.instance.getModelScript(this.modelScriptName);
		}
	}

	private void validateSkinTexture() {
	}

	private void validatePose() {
		AnimationSet animationSet = AnimationSet.GetAnimationSet(this.animSet, false);
		if (animationSet == null) {
			DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" doesn\'t exist", this.animSet);
			this.pose = "Invalid";
		} else {
			AnimState animState = animationSet.GetState(this.animState);
			if (animState == null) {
				DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" state \"%s\" doesn\'t exist", this.animSet, this.animState);
				this.pose = "Invalid";
			} else {
				Iterator iterator = animState.m_Nodes.iterator();
				AnimNode animNode;
				do {
					if (!iterator.hasNext()) {
						if (animState.m_Nodes == null) {
							DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" state \"%s\" node \"%s\" doesn\'t exist", this.animSet, this.animState, this.pose);
							this.pose = "Invalid";
							return;
						}

						AnimNode animNode2 = (AnimNode)PZArrayUtil.pickRandom(animState.m_Nodes);
						this.pose = animNode2.m_Name;
						return;
					}

					animNode = (AnimNode)iterator.next();
				}		 while (!animNode.m_Name.equalsIgnoreCase(this.pose));
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		int int1 = IsoCamera.frameState.playerIndex;
		float1 += 0.5F;
		float2 += 0.5F;
		this.calcScreenPos(float1, float2, float3);
		this.renderShadow(float1, float2, float3);
		if (this.bAnimate) {
			this.animatedModel.update();
			IsoMannequin.Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
			drawer.init(float1, float2, float3);
			SpriteRenderer.instance.drawGeneric(drawer);
		} else {
			IsoDirections directions = this.dir;
			IsoMannequin.PerPlayer perPlayer = this.perPlayer[int1];
			if (perPlayer.renderDirection != null && perPlayer.renderDirection != IsoDirections.Max) {
				this.dir = perPlayer.renderDirection;
				perPlayer.renderDirection = null;
				perPlayer.bWasRenderDirection = true;
				perPlayer.atlasTex = null;
			} else if (perPlayer.bWasRenderDirection) {
				perPlayer.bWasRenderDirection = false;
				perPlayer.atlasTex = null;
			}

			if (perPlayer.atlasTex == null) {
				perPlayer.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
				DeadBodyAtlas.instance.render();
			}

			this.dir = directions;
			if (perPlayer.atlasTex != null) {
				if (this.isHighlighted()) {
					inf.r = this.getHighlightColor().r;
					inf.g = this.getHighlightColor().g;
					inf.b = this.getHighlightColor().b;
					inf.a = this.getHighlightColor().a;
				} else {
					inf.r = colorInfo.r;
					inf.g = colorInfo.g;
					inf.b = colorInfo.b;
					inf.a = colorInfo.a;
				}

				colorInfo = inf;
				if (!this.isHighlighted() && PerformanceSettings.LightingFrameSkip < 3) {
					this.square.interpolateLight(colorInfo, float1 - (float)this.square.getX(), float2 - (float)this.square.getY());
				}

				perPlayer.atlasTex.render((float)((int)this.screenX), (float)((int)this.screenY), colorInfo.r, colorInfo.g, colorInfo.b, this.getAlpha(int1));
				if (Core.bDebug) {
				}
			}
		}
	}

	public void renderFxMask(float float1, float float2, float float3, boolean boolean1) {
	}

	private void calcScreenPos(float float1, float float2, float float3) {
		if (IsoSprite.globalOffsetX == -1.0F) {
			IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
			IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
		}

		this.screenX = IsoUtils.XToScreen(float1, float2, float3, 0);
		this.screenY = IsoUtils.YToScreen(float1, float2, float3, 0);
		this.sx = this.screenX;
		this.sy = this.screenY;
		this.screenX = this.sx + IsoSprite.globalOffsetX;
		this.screenY = this.sy + IsoSprite.globalOffsetY;
		IsoObject[] objectArray = (IsoObject[])this.square.getObjects().getElements();
		for (int int1 = 0; int1 < this.square.getObjects().size(); ++int1) {
			IsoObject object = objectArray[int1];
			if (object.isTableSurface()) {
				this.screenY -= (object.getSurfaceOffset() + 1.0F) * (float)Core.TileScale;
			}
		}
	}

	private void renderShadow(float float1, float float2, float float3) {
		Texture texture = Texture.getSharedTexture("dropshadow");
		int int1 = IsoCamera.frameState.playerIndex;
		float float4 = 0.8F * this.getAlpha(int1);
		ColorInfo colorInfo = this.square.lighting[int1].lightInfo();
		float4 *= (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
		float4 *= 0.8F;
		float float5 = this.screenX - (float)texture.getWidth() / 2.0F * (float)Core.TileScale;
		float float6 = this.screenY - (float)texture.getHeight() / 2.0F * (float)Core.TileScale;
		SpriteRenderer.instance.render(texture, float5, float6, (float)texture.getWidth() * (float)Core.TileScale, (float)texture.getHeight() * (float)Core.TileScale, 1.0F, 1.0F, 1.0F, float4, (Consumer)null);
	}

	private void initOutfit() {
		if (this.bInit) {
			this.initMannequinScript();
			this.initModelScript();
		} else {
			this.bInit = true;
			this.getPropertiesFromSprite();
			this.getPropertiesFromZone();
			this.initMannequinScript();
			this.initModelScript();
			if (this.outfit == null) {
				Outfit outfit = OutfitManager.instance.GetRandomNonProfessionalOutfit(this.bFemale);
				this.humanVisual.dressInNamedOutfit(outfit.m_Name, this.itemVisuals);
			} else if (!"none".equalsIgnoreCase(this.outfit)) {
				this.humanVisual.dressInNamedOutfit(this.outfit, this.itemVisuals);
			}

			this.humanVisual.setHairModel("");
			this.humanVisual.setBeardModel("");
			this.createInventory(this.itemVisuals);
		}
	}

	private void getPropertiesFromSprite() {
		String string = this.sprite.name;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case 1420407857: 
			if (string.equals("location_shop_mall_01_65")) {
				byte1 = 0;
			}

			break;
		
		case 1420407858: 
			if (string.equals("location_shop_mall_01_66")) {
				byte1 = 1;
			}

			break;
		
		case 1420407859: 
			if (string.equals("location_shop_mall_01_67")) {
				byte1 = 2;
			}

			break;
		
		case 1420407860: 
			if (string.equals("location_shop_mall_01_68")) {
				byte1 = 3;
			}

			break;
		
		case 1420407861: 
			if (string.equals("location_shop_mall_01_69")) {
				byte1 = 4;
			}

		
		case 1420407862: 
		
		case 1420407863: 
		
		case 1420407864: 
		
		case 1420407865: 
		
		case 1420407866: 
		
		case 1420407867: 
		
		case 1420407868: 
		
		case 1420407869: 
		
		case 1420407870: 
		
		case 1420407871: 
		
		case 1420407872: 
		
		case 1420407873: 
		
		case 1420407874: 
		
		case 1420407875: 
		
		case 1420407876: 
		
		case 1420407877: 
		
		case 1420407878: 
		
		case 1420407879: 
		
		case 1420407880: 
		
		case 1420407881: 
		
		case 1420407882: 
		
		case 1420407884: 
		
		case 1420407885: 
		
		default: 
			break;
		
		case 1420407883: 
			if (string.equals("location_shop_mall_01_70")) {
				byte1 = 5;
			}

			break;
		
		case 1420407886: 
			if (string.equals("location_shop_mall_01_73")) {
				byte1 = 6;
			}

			break;
		
		case 1420407887: 
			if (string.equals("location_shop_mall_01_74")) {
				byte1 = 7;
			}

			break;
		
		case 1420407888: 
			if (string.equals("location_shop_mall_01_75")) {
				byte1 = 8;
			}

			break;
		
		case 1420407889: 
			if (string.equals("location_shop_mall_01_76")) {
				byte1 = 9;
			}

			break;
		
		case 1420407890: 
			if (string.equals("location_shop_mall_01_77")) {
				byte1 = 10;
			}

			break;
		
		case 1420407891: 
			if (string.equals("location_shop_mall_01_78")) {
				byte1 = 11;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.mannequinScriptName = "FemaleWhite01";
			this.dir = IsoDirections.SE;
			break;
		
		case 1: 
			this.mannequinScriptName = "FemaleWhite02";
			this.dir = IsoDirections.S;
			break;
		
		case 2: 
			this.mannequinScriptName = "FemaleWhite03";
			this.dir = IsoDirections.SE;
			break;
		
		case 3: 
			this.mannequinScriptName = "MaleWhite01";
			this.dir = IsoDirections.SE;
			break;
		
		case 4: 
			this.mannequinScriptName = "MaleWhite02";
			this.dir = IsoDirections.S;
			break;
		
		case 5: 
			this.mannequinScriptName = "MaleWhite03";
			this.dir = IsoDirections.SE;
			break;
		
		case 6: 
			this.mannequinScriptName = "FemaleBlack01";
			this.dir = IsoDirections.SE;
			break;
		
		case 7: 
			this.mannequinScriptName = "FemaleBlack02";
			this.dir = IsoDirections.S;
			break;
		
		case 8: 
			this.mannequinScriptName = "FemaleBlack03";
			this.dir = IsoDirections.SE;
			break;
		
		case 9: 
			this.mannequinScriptName = "MaleBlack01";
			this.dir = IsoDirections.SE;
			break;
		
		case 10: 
			this.mannequinScriptName = "MaleBlack02";
			this.dir = IsoDirections.S;
			break;
		
		case 11: 
			this.mannequinScriptName = "MaleBlack03";
			this.dir = IsoDirections.SE;
		
		}
	}

	private void getPropertiesFromZone() {
		if (this.getObjectIndex() != -1) {
			IsoMetaCell metaCell = IsoWorld.instance.getMetaGrid().getCellData(this.square.x / 300, this.square.y / 300);
			if (metaCell != null && metaCell.mannequinZones != null) {
				ArrayList arrayList = metaCell.mannequinZones;
				IsoMannequin.MannequinZone mannequinZone = null;
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					mannequinZone = (IsoMannequin.MannequinZone)arrayList.get(int1);
					if (mannequinZone.contains(this.square.x, this.square.y, this.square.z)) {
						break;
					}

					mannequinZone = null;
				}

				if (mannequinZone != null) {
					if (mannequinZone.bFemale != -1) {
						this.bFemale = mannequinZone.bFemale == 1;
					}

					if (mannequinZone.dir != IsoDirections.Max) {
						this.dir = mannequinZone.dir;
					}

					if (mannequinZone.mannequinScript != null) {
						this.mannequinScriptName = mannequinZone.mannequinScript;
					}

					if (mannequinZone.skin != null) {
						this.textureName = mannequinZone.skin;
					}

					if (mannequinZone.pose != null) {
						this.pose = mannequinZone.pose;
					}

					if (mannequinZone.outfit != null) {
						this.outfit = mannequinZone.outfit;
					}
				}
			}
		}
	}

	private void syncModel() {
		this.humanVisual.setForceModelScript(this.modelScriptName);
		String string = this.modelScriptName;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case 133253487: 
			if (string.equals("MaleBody")) {
				byte1 = 1;
			}

			break;
		
		case 1328258862: 
			if (string.equals("FemaleBody")) {
				byte1 = 0;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.humanVisual.setForceModel(ModelManager.instance.m_femaleModel);
			break;
		
		case 1: 
			this.humanVisual.setForceModel(ModelManager.instance.m_maleModel);
			break;
		
		default: 
			this.humanVisual.setForceModel(ModelManager.instance.getLoadedModel(this.modelScriptName));
		
		}
		this.humanVisual.setSkinTextureName(this.textureName);
		this.wornItems.getItemVisuals(this.itemVisuals);
		int int1;
		for (int1 = 0; int1 < 4; ++int1) {
			this.perPlayer[int1].atlasTex = null;
		}

		if (this.bAnimate) {
			if (this.animatedModel == null) {
				this.animatedModel = new AnimatedModel();
				this.drawers = new IsoMannequin.Drawer[3];
				for (int1 = 0; int1 < this.drawers.length; ++int1) {
					this.drawers[int1] = new IsoMannequin.Drawer();
				}
			}

			this.animatedModel.setAnimSetName(this.getAnimSetName());
			this.animatedModel.setState(this.getAnimStateName());
			this.animatedModel.setVariable("Female", this.bFemale);
			this.animatedModel.setVariable("Pose", this.getPose());
			this.animatedModel.setAngle(this.dir.ToVector());
			this.animatedModel.setModelData(this.humanVisual, this.itemVisuals);
		}
	}

	private void createInventory(ItemVisuals itemVisuals) {
		if (this.container == null) {
			this.container = new ItemContainer("mannequin", this.getSquare(), this);
			this.container.setExplored(true);
		}

		this.container.clear();
		this.wornItems.setFromItemVisuals(itemVisuals);
		this.wornItems.addItemsToItemContainer(this.container);
	}

	public void wearItem(InventoryItem inventoryItem, IsoGameCharacter gameCharacter) {
		if (this.container.contains(inventoryItem)) {
			ItemVisual itemVisual = inventoryItem.getVisual();
			if (itemVisual != null) {
				if (inventoryItem instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)inventoryItem).getBodyLocation())) {
					this.wornItems.setItem(((Clothing)inventoryItem).getBodyLocation(), inventoryItem);
				} else {
					if (!(inventoryItem instanceof InventoryContainer) || StringUtils.isNullOrWhitespace(((InventoryContainer)inventoryItem).canBeEquipped())) {
						return;
					}

					this.wornItems.setItem(((InventoryContainer)inventoryItem).canBeEquipped(), inventoryItem);
				}

				if (gameCharacter != null) {
					ArrayList arrayList = this.container.getItems();
					for (int int1 = 0; int1 < arrayList.size(); ++int1) {
						InventoryItem inventoryItem2 = (InventoryItem)arrayList.get(int1);
						if (!this.wornItems.contains(inventoryItem2)) {
							this.container.removeItemOnServer(inventoryItem2);
							this.container.Remove(inventoryItem2);
							gameCharacter.getInventory().AddItem(inventoryItem2);
							--int1;
						}
					}
				}

				this.syncModel();
			}
		}
	}

	public void checkClothing(InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < this.wornItems.size(); ++int1) {
			InventoryItem inventoryItem2 = this.wornItems.getItemByIndex(int1);
			if (this.container == null || this.container.getItems().indexOf(inventoryItem2) == -1) {
				this.wornItems.remove(inventoryItem2);
				this.syncModel();
				--int1;
			}
		}
	}

	public String getAnimSetName() {
		return this.animSet;
	}

	public String getAnimStateName() {
		return this.animState;
	}

	public void getCustomSettingsFromItem(InventoryItem inventoryItem) throws IOException {
		if (inventoryItem instanceof Moveable) {
			ByteBuffer byteBuffer = inventoryItem.getByteData();
			if (byteBuffer == null) {
				return;
			}

			byteBuffer.rewind();
			int int1 = byteBuffer.getInt();
			byteBuffer.get();
			byteBuffer.get();
			this.load(byteBuffer, int1);
		}
	}

	public void setCustomSettingsToItem(InventoryItem inventoryItem) throws IOException {
		if (inventoryItem instanceof Moveable) {
			synchronized (SliceY.SliceBufferLock) {
				ByteBuffer byteBuffer = SliceY.SliceBuffer;
				byteBuffer.clear();
				byteBuffer.putInt(195);
				this.save(byteBuffer);
				byteBuffer.flip();
				inventoryItem.byteData = ByteBuffer.allocate(byteBuffer.limit());
				inventoryItem.byteData.put(byteBuffer);
			}

			if (this.container != null) {
				inventoryItem.setActualWeight(inventoryItem.getActualWeight() + this.container.getContentsWeight());
			}
		}
	}

	public static boolean isMannequinSprite(IsoSprite sprite) {
		return "Mannequin".equals(sprite.getProperties().Val("CustomName"));
	}

	private void resetMannequin() {
		this.bInit = false;
		this.bFemale = false;
		this.bZombie = false;
		this.bSkeleton = false;
		this.mannequinScriptName = null;
		this.modelScriptName = null;
		this.textureName = null;
		this.animSet = null;
		this.animState = null;
		this.pose = null;
		this.outfit = null;
		this.humanVisual.clear();
		this.itemVisuals.clear();
		this.wornItems.clear();
		this.mannequinScript = null;
		this.modelScript = null;
		this.bAnimate = false;
	}

	public static void renderMoveableItem(Moveable moveable, int int1, int int2, int int3, IsoDirections directions) {
		int int4 = IsoCamera.frameState.playerIndex;
		IsoMannequin.StaticPerPlayer staticPerPlayer = staticPerPlayer[int4];
		if (staticPerPlayer == null) {
			staticPerPlayer = staticPerPlayer[int4] = new IsoMannequin.StaticPerPlayer(int4);
		}

		staticPerPlayer.renderMoveableItem(moveable, int1, int2, int3, directions);
	}

	public static void renderMoveableObject(IsoMannequin mannequin, int int1, int int2, int int3, IsoDirections directions) {
		mannequin.setRenderDirection(directions);
	}

	public static IsoDirections getDirectionFromItem(Moveable moveable, int int1) {
		IsoMannequin.StaticPerPlayer staticPerPlayer = staticPerPlayer[int1];
		if (staticPerPlayer == null) {
			staticPerPlayer = staticPerPlayer[int1] = new IsoMannequin.StaticPerPlayer(int1);
		}

		return staticPerPlayer.getDirectionFromItem(moveable);
	}

	private static final class PerPlayer {
		private DeadBodyAtlas.BodyTexture atlasTex = null;
		IsoDirections renderDirection = null;
		boolean bWasRenderDirection = false;
	}

	private final class Drawer extends TextureDraw.GenericDrawer {
		float x;
		float y;
		float z;
		float m_animPlayerAngle;
		boolean bRendered;

		public void init(float float1, float float2, float float3) {
			this.x = float1;
			this.y = float2;
			this.z = float3;
			this.bRendered = false;
			IsoMannequin.this.animatedModel.renderMain();
			this.m_animPlayerAngle = IsoMannequin.this.animatedModel.getAnimationPlayer().getRenderedAngle();
		}

		public void render() {
			IsoMannequin.this.animatedModel.DoRenderToWorld(this.x, this.y, this.z, this.m_animPlayerAngle);
			this.bRendered = true;
		}

		public void postRender() {
			IsoMannequin.this.animatedModel.postRender(this.bRendered);
		}
	}

	public static final class MannequinZone extends IsoMetaGrid.Zone {
		public int bFemale = -1;
		public IsoDirections dir;
		public String mannequinScript;
		public String pose;
		public String skin;
		public String outfit;

		public MannequinZone(String string, String string2, int int1, int int2, int int3, int int4, int int5, KahluaTable kahluaTable) {
			super(string, string2, int1, int2, int3, int4, int5);
			this.dir = IsoDirections.Max;
			this.mannequinScript = null;
			this.pose = null;
			this.skin = null;
			this.outfit = null;
			if (kahluaTable != null) {
				Object object = kahluaTable.rawget("Female");
				if (object instanceof Boolean) {
					this.bFemale = object == Boolean.TRUE ? 1 : 0;
				}

				object = kahluaTable.rawget("Direction");
				if (object instanceof String) {
					this.dir = IsoDirections.valueOf((String)object);
				}

				object = kahluaTable.rawget("Outfit");
				if (object instanceof String) {
					this.outfit = (String)object;
				}

				object = kahluaTable.rawget("Script");
				if (object instanceof String) {
					this.mannequinScript = (String)object;
				}

				object = kahluaTable.rawget("Skin");
				if (object instanceof String) {
					this.skin = (String)object;
				}

				object = kahluaTable.rawget("Pose");
				if (object instanceof String) {
					this.pose = (String)object;
				}
			}
		}
	}

	private static final class StaticPerPlayer {
		final int playerIndex;
		Moveable _moveable = null;
		Moveable _failedItem = null;
		IsoMannequin _mannequin = null;

		StaticPerPlayer(int int1) {
			this.playerIndex = int1;
		}

		void renderMoveableItem(Moveable moveable, int int1, int int2, int int3, IsoDirections directions) {
			if (this.checkItem(moveable)) {
				if (this._moveable != moveable) {
					this._moveable = moveable;
					try {
						this._mannequin.getCustomSettingsFromItem(this._moveable);
					} catch (IOException ioException) {
					}

					this._mannequin.initOutfit();
					this._mannequin.validateSkinTexture();
					this._mannequin.validatePose();
					this._mannequin.syncModel();
					this._mannequin.perPlayer[this.playerIndex].atlasTex = null;
				}

				this._mannequin.square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
				if (this._mannequin.square != null) {
					this._mannequin.perPlayer[this.playerIndex].renderDirection = directions;
					IsoMannequin.inf.set(1.0F, 1.0F, 1.0F, 1.0F);
					this._mannequin.render((float)int1, (float)int2, (float)int3, IsoMannequin.inf, false, false, (Shader)null);
				}
			}
		}

		IsoDirections getDirectionFromItem(Moveable moveable) {
			if (!this.checkItem(moveable)) {
				return IsoDirections.S;
			} else {
				this._moveable = null;
				try {
					this._mannequin.getCustomSettingsFromItem(moveable);
					return this._mannequin.getDir();
				} catch (Exception exception) {
					return IsoDirections.S;
				}
			}
		}

		boolean checkItem(Moveable moveable) {
			if (moveable == null) {
				return false;
			} else {
				String string = moveable.getWorldSprite();
				IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
				if (sprite != null && IsoMannequin.isMannequinSprite(sprite)) {
					if (moveable.getByteData() == null) {
						Thread thread = Thread.currentThread();
						if (thread != GameWindow.GameThread && thread != GameLoadingState.loader && thread == GameServer.MainThread) {
							return false;
						} else {
							if (this._mannequin == null || this._mannequin.getCell() != IsoWorld.instance.CurrentCell) {
								this._mannequin = new IsoMannequin(IsoWorld.instance.CurrentCell);
							}

							if (this._failedItem == moveable) {
								return false;
							} else {
								try {
									this._mannequin.resetMannequin();
									this._mannequin.sprite = sprite;
									this._mannequin.initOutfit();
									this._mannequin.validateSkinTexture();
									this._mannequin.validatePose();
									this._mannequin.syncModel();
									this._mannequin.setCustomSettingsToItem(moveable);
									return true;
								} catch (IOException ioException) {
									this._failedItem = moveable;
									return false;
								}
							}
						}
					} else {
						if (this._mannequin == null || this._mannequin.getCell() != IsoWorld.instance.CurrentCell) {
							this._mannequin = new IsoMannequin(IsoWorld.instance.CurrentCell);
						}

						return true;
					}
				} else {
					return false;
				}
			}
		}
	}
}
