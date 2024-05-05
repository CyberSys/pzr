package zombie.iso.objects;

import fmod.fmod.FMODManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.types.HandWeapon;
import zombie.iso.CellLoader;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;


public class IsoTree extends IsoObject {
	public static final int MAX_SIZE = 6;
	public int LogYield = 1;
	public int damage = 500;
	public int size = 4;
	public boolean bRenderFlag;
	public float fadeAlpha;
	private static final IsoGameCharacter.Location[] s_chopTreeLocation = new IsoGameCharacter.Location[4];
	private static final ArrayList s_chopTreeIndicators = new ArrayList();
	private static IsoTree s_chopTreeHighlighted = null;

	public static IsoTree getNew() {
		synchronized (CellLoader.isoTreeCache) {
			if (CellLoader.isoTreeCache.isEmpty()) {
				return new IsoTree();
			} else {
				IsoTree tree = (IsoTree)CellLoader.isoTreeCache.pop();
				tree.sx = 0.0F;
				return tree;
			}
		}
	}

	public IsoTree() {
	}

	public IsoTree(IsoCell cell) {
		super(cell);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)this.LogYield);
		byteBuffer.put((byte)(this.damage / 10));
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.LogYield = byteBuffer.get();
		this.damage = byteBuffer.get() * 10;
		if (this.sprite != null && this.sprite.getProperties().Val("tree") != null) {
			this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
			if (this.size < 1) {
				this.size = 1;
			}

			if (this.size > 6) {
				this.size = 6;
			}
		}
	}

	protected void checkMoveWithWind() {
		this.checkMoveWithWind(true);
	}

	public void reset() {
		super.reset();
	}

	public IsoTree(IsoGridSquare square, String string) {
		super(square, string, false);
		this.initTree();
	}

	public IsoTree(IsoGridSquare square, IsoSprite sprite) {
		super(square.getCell(), square, sprite);
		this.initTree();
	}

	public void initTree() {
		this.setType(IsoObjectType.tree);
		if (this.sprite.getProperties().Val("tree") != null) {
			this.size = Integer.parseInt(this.sprite.getProperties().Val("tree"));
			if (this.size < 1) {
				this.size = 1;
			}

			if (this.size > 6) {
				this.size = 6;
			}
		} else {
			this.size = 4;
		}

		switch (this.size) {
		case 1: 
		
		case 2: 
			this.LogYield = 1;
			break;
		
		case 3: 
		
		case 4: 
			this.LogYield = 2;
			break;
		
		case 5: 
			this.LogYield = 3;
			break;
		
		case 6: 
			this.LogYield = 4;
		
		}
		this.damage = this.LogYield * 80;
	}

	public String getObjectName() {
		return "Tree";
	}

	public void Damage(float float1) {
		float float2 = float1 * 0.05F;
		this.damage = (int)((float)this.damage - float2);
		if (this.damage <= 0) {
			this.square.transmitRemoveItemFromSquare(this);
			this.square.RecalcAllWithNeighbours(true);
			int int1 = this.LogYield;
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
				}

				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
				}
			}

			this.reset();
			CellLoader.isoTreeCache.add(this);
			for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
				LosUtil.cachecleared[int2] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	public void HitByVehicle(BaseVehicle baseVehicle, float float1) {
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)this.square.x + 0.5F, (float)this.square.y + 0.5F, (float)this.square.z);
		long long1 = baseSoundEmitter.playSound("VehicleHitTree");
		baseSoundEmitter.setParameterValue(long1, FMODManager.instance.getParameterDescription("VehicleSpeed"), baseVehicle.getCurrentSpeedKmHour());
		WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		this.Damage((float)this.damage);
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		int int1 = handWeapon.getConditionLowerChance() * 2 + gameCharacter.getMaintenanceMod();
		if (!handWeapon.getCategories().contains("Axe")) {
			int1 = handWeapon.getConditionLowerChance() / 2 + gameCharacter.getMaintenanceMod();
		}

		if (Rand.NextBool(int1)) {
			handWeapon.setCondition(handWeapon.getCondition() - 1);
		}

		gameCharacter.getEmitter().playSound("ChopTree");
		WorldSoundManager.instance.addSound((Object)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
		this.setRenderEffect(RenderEffectType.Hit_Tree_Shudder, true);
		float float1 = (float)handWeapon.getTreeDamage();
		if (gameCharacter.Traits.Axeman.isSet() && handWeapon.getCategories().contains("Axe")) {
			float1 *= 1.5F;
		}

		this.damage = (int)((float)this.damage - float1);
		if (this.damage <= 0) {
			this.square.transmitRemoveItemFromSquare(this);
			gameCharacter.getEmitter().playSound("FallingTree");
			this.square.RecalcAllWithNeighbours(true);
			int int2 = this.LogYield;
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				this.square.AddWorldInventoryItem("Base.Log", 0.0F, 0.0F, 0.0F);
				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.TreeBranch", 0.0F, 0.0F, 0.0F);
				}

				if (Rand.Next(4) == 0) {
					this.square.AddWorldInventoryItem("Base.Twigs", 0.0F, 0.0F, 0.0F);
				}
			}

			this.reset();
			CellLoader.isoTreeCache.add(this);
			for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
				LosUtil.cachecleared[int3] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}

		LuaEventManager.triggerEvent("OnWeaponHitTree", gameCharacter, handWeapon);
	}

	public void setHealth(int int1) {
		this.damage = Math.max(int1, 0);
	}

	public int getHealth() {
		return this.damage;
	}

	public int getMaxHealth() {
		return this.LogYield * 80;
	}

	public int getSize() {
		return this.size;
	}

	public float getSlowFactor(IsoMovingObject movingObject) {
		float float1 = 1.0F;
		if (movingObject instanceof IsoGameCharacter) {
			if ("parkranger".equals(((IsoGameCharacter)movingObject).getDescriptor().getProfession())) {
				float1 = 1.5F;
			}

			if ("lumberjack".equals(((IsoGameCharacter)movingObject).getDescriptor().getProfession())) {
				float1 = 1.2F;
			}
		}

		if (this.size != 1 && this.size != 2) {
			return this.size != 3 && this.size != 4 ? 0.3F * float1 : 0.5F * float1;
		} else {
			return 0.8F * float1;
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.isHighlighted()) {
			if (this.square != null) {
				s_chopTreeHighlighted = this;
			}
		} else {
			int int1 = IsoCamera.frameState.playerIndex;
			if (!this.bRenderFlag && !(this.fadeAlpha < this.getTargetAlpha(int1))) {
				this.renderInner(float1, float2, float3, colorInfo, boolean1, false);
			} else {
				IndieGL.enableStencilTest();
				IndieGL.glStencilFunc(517, 128, 128);
				this.renderInner(float1, float2, float3, colorInfo, boolean1, false);
				float float4 = 0.044999998F * (GameTime.getInstance().getMultiplier() / 1.6F);
				if (this.bRenderFlag && this.fadeAlpha > 0.25F) {
					this.fadeAlpha -= float4;
					if (this.fadeAlpha < 0.25F) {
						this.fadeAlpha = 0.25F;
					}
				}

				float float5;
				if (!this.bRenderFlag) {
					float5 = this.getTargetAlpha(int1);
					if (this.fadeAlpha < float5) {
						this.fadeAlpha += float4;
						if (this.fadeAlpha > float5) {
							this.fadeAlpha = float5;
						}
					}
				}

				float5 = this.getAlpha(int1);
				float float6 = this.getTargetAlpha(int1);
				this.setAlphaAndTarget(int1, this.fadeAlpha);
				IndieGL.glStencilFunc(514, 128, 128);
				this.renderInner(float1, float2, float3, colorInfo, true, false);
				this.setAlpha(int1, float5);
				this.setTargetAlpha(int1, float6);
				if (IsoTree.TreeShader.instance.StartShader()) {
					IsoTree.TreeShader.instance.setOutlineColor(0.1F, 0.1F, 0.1F, 1.0F - this.fadeAlpha);
					this.renderInner(float1, float2, float3, colorInfo, true, true);
					IndieGL.EndShader();
				}

				IndieGL.glStencilFunc(519, 255, 255);
			}

			this.checkChopTreeIndicator(float1, float2, float3);
		}
	}

	private void renderInner(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2) {
		float float4;
		float float5;
		if (this.sprite != null && this.sprite.name != null && this.sprite.name.contains("JUMBO")) {
			float4 = this.offsetX;
			float5 = this.offsetY;
			this.offsetX = (float)(384 * Core.TileScale / 2 - 96 * Core.TileScale);
			this.offsetY = (float)(256 * Core.TileScale - 32 * Core.TileScale);
			if (this.offsetX != float4 || this.offsetY != float5) {
				this.sx = 0.0F;
			}
		} else {
			float4 = this.offsetX;
			float5 = this.offsetY;
			this.offsetX = (float)(32 * Core.TileScale);
			this.offsetY = (float)(96 * Core.TileScale);
			if (this.offsetX != float4 || this.offsetY != float5) {
				this.sx = 0.0F;
			}
		}

		if (boolean2 && this.sprite != null) {
			Texture texture = this.sprite.getTextureForCurrentFrame(this.dir);
			if (texture != null) {
				IsoTree.TreeShader.instance.setStepSize(0.25F, texture.getWidth(), texture.getHeight());
			}
		}

		super.render(float1, float2, float3, colorInfo, false, false, (Shader)null);
		if (this.AttachedAnimSprite != null) {
			int int1 = this.AttachedAnimSprite.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
				int int3 = IsoCamera.frameState.playerIndex;
				float float6 = this.getTargetAlpha(int3);
				this.setTargetAlpha(int3, 1.0F);
				spriteInstance.render(this, float1, float2, float3, this.dir, this.offsetX, this.offsetY, this.isHighlighted() ? this.getHighlightColor() : colorInfo);
				this.setTargetAlpha(int3, float6);
				spriteInstance.update();
			}
		}
	}

	public void setSprite(IsoSprite sprite) {
		super.setSprite(sprite);
		this.initTree();
	}

	public boolean isMaskClicked(int int1, int int2, boolean boolean1) {
		if (super.isMaskClicked(int1, int2, boolean1)) {
			return true;
		} else if (this.AttachedAnimSprite == null) {
			return false;
		} else {
			for (int int3 = 0; int3 < this.AttachedAnimSprite.size(); ++int3) {
				if (((IsoSpriteInstance)this.AttachedAnimSprite.get(int3)).parentSprite.isMaskClicked(this.dir, int1, int2, boolean1)) {
					return true;
				}
			}

			return false;
		}
	}

	public static void setChopTreeCursorLocation(int int1, int int2, int int3, int int4) {
		if (s_chopTreeLocation[int1] == null) {
			s_chopTreeLocation[int1] = new IsoGameCharacter.Location(-1, -1, -1);
		}

		IsoGameCharacter.Location location = s_chopTreeLocation[int1];
		location.x = int2;
		location.y = int3;
		location.z = int4;
	}

	private void checkChopTreeIndicator(float float1, float float2, float float3) {
		if (!this.isHighlighted()) {
			int int1 = IsoCamera.frameState.playerIndex;
			IsoGameCharacter.Location location = s_chopTreeLocation[int1];
			if (location != null && location.x != -1 && this.square != null) {
				if (this.getCell().getDrag(int1) == null) {
					location.x = -1;
				} else {
					if (IsoUtils.DistanceToSquared((float)this.square.x + 0.5F, (float)this.square.y + 0.5F, (float)location.x + 0.5F, (float)location.y + 0.5F) < 12.25F) {
						s_chopTreeIndicators.add(this.square);
					}
				}
			}
		}
	}

	public static void renderChopTreeIndicators() {
		if (!s_chopTreeIndicators.isEmpty()) {
			PZArrayUtil.forEach((List)s_chopTreeIndicators, IsoTree::renderChopTreeIndicator);
			s_chopTreeIndicators.clear();
		}

		if (s_chopTreeHighlighted != null) {
			IsoTree tree = s_chopTreeHighlighted;
			s_chopTreeHighlighted = null;
			tree.renderInner((float)tree.square.x, (float)tree.square.y, (float)tree.square.z, tree.getHighlightColor(), false, false);
		}
	}

	private static void renderChopTreeIndicator(IsoGridSquare square) {
		Texture texture = Texture.getSharedTexture("media/ui/chop_tree.png");
		if (texture != null && texture.isReady()) {
			float float1 = (float)square.x;
			float float2 = (float)square.y;
			float float3 = (float)square.z;
			float float4 = IsoUtils.XToScreen(float1, float2, float3, 0) + IsoSprite.globalOffsetX;
			float float5 = IsoUtils.YToScreen(float1, float2, float3, 0) + IsoSprite.globalOffsetY;
			float4 -= (float)(32 * Core.TileScale);
			float5 -= (float)(96 * Core.TileScale);
			SpriteRenderer.instance.render(texture, float4, float5, (float)(64 * Core.TileScale), (float)(128 * Core.TileScale), 0.0F, 0.5F, 0.0F, 0.75F, (Consumer)null);
		}
	}

	public static class TreeShader {
		public static final IsoTree.TreeShader instance = new IsoTree.TreeShader();
		private ShaderProgram shaderProgram;
		private int stepSize;
		private int outlineColor;

		public void initShader() {
			this.shaderProgram = ShaderProgram.createShaderProgram("tree", false, true);
			if (this.shaderProgram.isCompiled()) {
				this.stepSize = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "stepSize");
				this.outlineColor = ARBShaderObjects.glGetUniformLocationARB(this.shaderProgram.getShaderID(), "outlineColor");
				ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram.getShaderID());
				ARBShaderObjects.glUniform2fARB(this.stepSize, 0.001F, 0.001F);
				ARBShaderObjects.glUseProgramObjectARB(0);
			}
		}

		public void setOutlineColor(float float1, float float2, float float3, float float4) {
			SpriteRenderer.instance.ShaderUpdate4f(this.shaderProgram.getShaderID(), this.outlineColor, float1, float2, float3, float4);
		}

		public void setStepSize(float float1, int int1, int int2) {
			SpriteRenderer.instance.ShaderUpdate2f(this.shaderProgram.getShaderID(), this.stepSize, float1 / (float)int1, float1 / (float)int2);
		}

		public boolean StartShader() {
			if (this.shaderProgram == null) {
				RenderThread.invokeOnRenderContext(this::initShader);
			}

			if (this.shaderProgram.isCompiled()) {
				IndieGL.StartShader(this.shaderProgram.getShaderID(), 0);
				return true;
			} else {
				return false;
			}
		}
	}
}
