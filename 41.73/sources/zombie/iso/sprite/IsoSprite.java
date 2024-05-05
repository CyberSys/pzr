package zombie.iso.sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.CharacterModelCamera;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.ModelCameraRenderData;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Mask;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWater;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.WorldConverter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleModelCamera;


public final class IsoSprite {
	public static int maxCount = 0;
	public static float alphaStep = 0.05F;
	public static float globalOffsetX = -1.0F;
	public static float globalOffsetY = -1.0F;
	private static final ColorInfo info = new ColorInfo();
	private static final HashMap AnimNameSet = new HashMap();
	public int firerequirement;
	public String burntTile;
	public boolean forceAmbient;
	public boolean solidfloor;
	public boolean canBeRemoved;
	public boolean attachedFloor;
	public boolean cutW;
	public boolean cutN;
	public boolean solid;
	public boolean solidTrans;
	public boolean invisible;
	public boolean alwaysDraw;
	public boolean forceRender;
	public boolean moveWithWind = false;
	public boolean isBush = false;
	public static final byte RL_DEFAULT = 0;
	public static final byte RL_FLOOR = 1;
	public byte renderLayer = 0;
	public int windType = 1;
	public boolean Animate = true;
	public IsoAnim CurrentAnim = null;
	public boolean DeleteWhenFinished = false;
	public boolean Loop = true;
	public short soffX = 0;
	public short soffY = 0;
	public final PropertyContainer Properties = new PropertyContainer();
	public final ColorInfo TintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
	public final HashMap AnimMap = new HashMap(2);
	public final ArrayList AnimStack = new ArrayList(1);
	public String name;
	public int tileSheetIndex = 0;
	public int ID = 20000000;
	public IsoSpriteInstance def;
	public ModelManager.ModelSlot modelSlot;
	IsoSpriteManager parentManager;
	private IsoObjectType type;
	private String parentObjectName;
	private IsoSpriteGrid spriteGrid;
	public boolean treatAsWallOrder;
	private boolean hideForWaterRender;

	public void setHideForWaterRender() {
		this.hideForWaterRender = true;
	}

	public IsoSprite() {
		this.type = IsoObjectType.MAX;
		this.parentObjectName = null;
		this.treatAsWallOrder = false;
		this.hideForWaterRender = false;
		this.parentManager = IsoSpriteManager.instance;
		this.def = IsoSpriteInstance.get(this);
	}

	public IsoSprite(IsoSpriteManager spriteManager) {
		this.type = IsoObjectType.MAX;
		this.parentObjectName = null;
		this.treatAsWallOrder = false;
		this.hideForWaterRender = false;
		this.parentManager = spriteManager;
		this.def = IsoSpriteInstance.get(this);
	}

	public static IsoSprite CreateSprite(IsoSpriteManager spriteManager) {
		IsoSprite sprite = new IsoSprite(spriteManager);
		return sprite;
	}

	public static IsoSprite CreateSpriteUsingCache(String string, String string2, int int1) {
		IsoSprite sprite = CreateSprite(IsoSpriteManager.instance);
		return sprite.setFromCache(string, string2, int1);
	}

	public static IsoSprite getSprite(IsoSpriteManager spriteManager, int int1) {
		if (WorldConverter.instance.TilesetConversions != null && !WorldConverter.instance.TilesetConversions.isEmpty() && WorldConverter.instance.TilesetConversions.containsKey(int1)) {
			int1 = (Integer)WorldConverter.instance.TilesetConversions.get(int1);
		}

		return spriteManager.IntMap.containsKey(int1) ? (IsoSprite)spriteManager.IntMap.get(int1) : null;
	}

	public static void setSpriteID(IsoSpriteManager spriteManager, int int1, IsoSprite sprite) {
		if (spriteManager.IntMap.containsKey(sprite.ID)) {
			spriteManager.IntMap.remove(sprite.ID);
			sprite.ID = int1;
			spriteManager.IntMap.put(int1, sprite);
		}
	}

	public static IsoSprite getSprite(IsoSpriteManager spriteManager, IsoSprite sprite, int int1) {
		if (sprite.name.contains("_")) {
			String[] stringArray = sprite.name.split("_");
			int int2 = Integer.parseInt(stringArray[stringArray.length - 1].trim());
			int2 += int1;
			HashMap hashMap = spriteManager.NamedMap;
			String string = sprite.name.substring(0, sprite.name.lastIndexOf("_"));
			return (IsoSprite)hashMap.get(string + "_" + int2);
		} else {
			return null;
		}
	}

	public static IsoSprite getSprite(IsoSpriteManager spriteManager, String string, int int1) {
		IsoSprite sprite = (IsoSprite)spriteManager.NamedMap.get(string);
		String string2 = sprite.name.substring(0, sprite.name.lastIndexOf(95));
		String string3 = sprite.name.substring(sprite.name.lastIndexOf(95) + 1);
		if (sprite.name.contains("_")) {
			int int2 = Integer.parseInt(string3.trim());
			int2 += int1;
			return spriteManager.getSprite(string2 + "_" + int2);
		} else {
			return null;
		}
	}

	public static void DisposeAll() {
		AnimNameSet.clear();
	}

	public static boolean HasCache(String string) {
		return AnimNameSet.containsKey(string);
	}

	public IsoSpriteInstance newInstance() {
		return IsoSpriteInstance.get(this);
	}

	public PropertyContainer getProperties() {
		return this.Properties;
	}

	public String getParentObjectName() {
		return this.parentObjectName;
	}

	public void setParentObjectName(String string) {
		this.parentObjectName = string;
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		GameWindow.WriteString(dataOutputStream, this.name);
	}

	public void load(DataInputStream dataInputStream) throws IOException {
		this.name = GameWindow.ReadString(dataInputStream);
		this.LoadFramesNoDirPageSimple(this.name);
	}

	public void Dispose() {
		Iterator iterator = this.AnimMap.values().iterator();
		while (iterator.hasNext()) {
			IsoAnim anim = (IsoAnim)iterator.next();
			anim.Dispose();
		}

		this.AnimMap.clear();
		this.AnimStack.clear();
		this.CurrentAnim = null;
	}

	public boolean isMaskClicked(IsoDirections directions, int int1, int int2) {
		try {
			Texture texture = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[directions.index()];
			if (texture == null) {
				return false;
			} else {
				Mask mask = texture.getMask();
				if (mask == null) {
					return false;
				} else {
					int1 = (int)((float)int1 - texture.offsetX);
					int2 = (int)((float)int2 - texture.offsetY);
					return mask.get(int1, int2);
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
			return true;
		}
	}

	public boolean isMaskClicked(IsoDirections directions, int int1, int int2, boolean boolean1) {
		if (this.CurrentAnim == null) {
			return false;
		} else {
			this.initSpriteInstance();
			try {
				if (this.CurrentAnim != null && this.CurrentAnim.Frames != null && !(this.def.Frame >= (float)this.CurrentAnim.Frames.size())) {
					Texture texture = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[directions.index()];
					if (texture == null) {
						return false;
					} else {
						Mask mask = texture.getMask();
						if (mask == null) {
							return false;
						} else {
							if (boolean1) {
								int1 = (int)((float)int1 - ((float)(texture.getWidthOrig() - texture.getWidth()) - texture.offsetX));
								int2 = (int)((float)int2 - texture.offsetY);
								int1 = texture.getWidth() - int1;
							} else {
								int1 = (int)((float)int1 - texture.offsetX);
								int2 = (int)((float)int2 - texture.offsetY);
							}

							return int1 >= 0 && int2 >= 0 && int1 <= texture.getWidth() && int2 <= texture.getHeight() ? mask.get(int1, int2) : false;
						}
					}
				} else {
					return false;
				}
			} catch (Exception exception) {
				Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
				return true;
			}
		}
	}

	public float getMaskClickedY(IsoDirections directions, int int1, int int2, boolean boolean1) {
		try {
			Texture texture = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)this.def.Frame)).directions[directions.index()];
			if (texture == null) {
				return 10000.0F;
			} else {
				Mask mask = texture.getMask();
				if (mask == null) {
					return 10000.0F;
				} else {
					if (boolean1) {
						int1 = (int)((float)int1 - ((float)(texture.getWidthOrig() - texture.getWidth()) - texture.offsetX));
						int2 = (int)((float)int2 - texture.offsetY);
						int1 = texture.getWidth() - int1;
					} else {
						int1 = (int)((float)int1 - texture.offsetX);
						int2 = (int)((float)int2 - texture.offsetY);
						int1 = texture.getWidth() - int1;
					}

					return (float)int2;
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
			return 10000.0F;
		}
	}

	public Texture LoadFrameExplicit(String string) {
		this.CurrentAnim = new IsoAnim();
		this.AnimMap.put("default", this.CurrentAnim);
		this.CurrentAnim.ID = this.AnimStack.size();
		this.AnimStack.add(this.CurrentAnim);
		return this.CurrentAnim.LoadFrameExplicit(string);
	}

	public void LoadFrames(String string, String string2, int int1) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFrames(string, string2, int1);
		}
	}

	public void LoadFramesReverseAltName(String string, String string2, String string3, int int1) {
		if (!this.AnimMap.containsKey(string3)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string3, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesReverseAltName(string, string2, string3, int1);
		}
	}

	public void LoadFramesNoDirPage(String string, String string2, int int1) {
		this.CurrentAnim = new IsoAnim();
		this.AnimMap.put(string2, this.CurrentAnim);
		this.CurrentAnim.ID = this.AnimStack.size();
		this.AnimStack.add(this.CurrentAnim);
		this.CurrentAnim.LoadFramesNoDirPage(string, string2, int1);
	}

	public void LoadFramesNoDirPageDirect(String string, String string2, int int1) {
		this.CurrentAnim = new IsoAnim();
		this.AnimMap.put(string2, this.CurrentAnim);
		this.CurrentAnim.ID = this.AnimStack.size();
		this.AnimStack.add(this.CurrentAnim);
		this.CurrentAnim.LoadFramesNoDirPageDirect(string, string2, int1);
	}

	public void LoadFramesNoDirPageSimple(String string) {
		if (this.AnimMap.containsKey("default")) {
			IsoAnim anim = (IsoAnim)this.AnimMap.get("default");
			this.AnimStack.remove(anim);
			this.AnimMap.remove("default");
		}

		this.CurrentAnim = new IsoAnim();
		this.AnimMap.put("default", this.CurrentAnim);
		this.CurrentAnim.ID = this.AnimStack.size();
		this.AnimStack.add(this.CurrentAnim);
		this.CurrentAnim.LoadFramesNoDirPage(string);
	}

	public void ReplaceCurrentAnimFrames(String string) {
		if (this.CurrentAnim != null) {
			this.CurrentAnim.Frames.clear();
			this.CurrentAnim.LoadFramesNoDirPage(string);
		}
	}

	public void LoadFramesPageSimple(String string, String string2, String string3, String string4) {
		this.CurrentAnim = new IsoAnim();
		this.AnimMap.put("default", this.CurrentAnim);
		this.CurrentAnim.ID = this.AnimStack.size();
		this.AnimStack.add(this.CurrentAnim);
		this.CurrentAnim.LoadFramesPageSimple(string, string2, string3, string4);
	}

	public void LoadFramesPcx(String string, String string2, int int1) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesPcx(string, string2, int1);
		}
	}

	public void PlayAnim(IsoAnim anim) {
		if (this.CurrentAnim == null || this.CurrentAnim != anim) {
			this.CurrentAnim = anim;
		}
	}

	public void PlayAnim(String string) {
		if ((this.CurrentAnim == null || !this.CurrentAnim.name.equals(string)) && this.AnimMap.containsKey(string)) {
			this.CurrentAnim = (IsoAnim)this.AnimMap.get(string);
		}
	}

	public void PlayAnimUnlooped(String string) {
		if (this.AnimMap.containsKey(string)) {
			if (this.CurrentAnim == null || !this.CurrentAnim.name.equals(string)) {
				this.CurrentAnim = (IsoAnim)this.AnimMap.get(string);
			}

			this.CurrentAnim.looped = false;
		}
	}

	public void ChangeTintMod(ColorInfo colorInfo) {
		this.TintMod.r = colorInfo.r;
		this.TintMod.g = colorInfo.g;
		this.TintMod.b = colorInfo.b;
		this.TintMod.a = colorInfo.a;
	}

	public void RenderGhostTile(int int1, int int2, int int3) {
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
		spriteInstance.alpha = spriteInstance.targetAlpha = 0.6F;
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo(), true);
	}

	public void RenderGhostTileRed(int int1, int int2, int int3) {
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
		spriteInstance.tintr = 0.65F;
		spriteInstance.tintg = 0.2F;
		spriteInstance.tintb = 0.2F;
		spriteInstance.alpha = spriteInstance.targetAlpha = 0.6F;
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo(), true);
	}

	public void RenderGhostTileColor(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		this.RenderGhostTileColor(int1, int2, int3, 0.0F, 0.0F, float1, float2, float3, float4);
	}

	public void RenderGhostTileColor(int int1, int int2, int int3, float float1, float float2, float float3, float float4, float float5, float float6) {
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
		spriteInstance.tintr = float3;
		spriteInstance.tintg = float4;
		spriteInstance.tintb = float5;
		spriteInstance.alpha = spriteInstance.targetAlpha = float6;
		IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
		int int4 = Core.TileScale;
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * int4) + float1, (float)(96 * int4) + float2, IsoGridSquare.getDefColorInfo(), true);
	}

	public boolean hasActiveModel() {
		if (!ModelManager.instance.bDebugEnableModels) {
			return false;
		} else if (!ModelManager.instance.isCreated()) {
			return false;
		} else {
			return this.modelSlot != null && this.modelSlot.active;
		}
	}

	public void renderVehicle(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		if (spriteInstance != null) {
			if (this.hasActiveModel()) {
				SpriteRenderer.instance.drawGeneric(((ModelCameraRenderData)ModelCameraRenderData.s_pool.alloc()).init(VehicleModelCamera.instance, this.modelSlot));
				SpriteRenderer.instance.drawModel(this.modelSlot);
				if (!BaseVehicle.RENDER_TO_TEXTURE) {
					return;
				}
			}

			info.r = colorInfo.r;
			info.g = colorInfo.g;
			info.b = colorInfo.b;
			info.a = colorInfo.a;
			try {
				if (boolean1) {
					spriteInstance.renderprep(object);
				}

				float float6 = 0.0F;
				float float7 = 0.0F;
				if (globalOffsetX == -1.0F) {
					globalOffsetX = -IsoCamera.frameState.OffX;
					globalOffsetY = -IsoCamera.frameState.OffY;
				}

				if (object == null || object.sx == 0.0F || object instanceof IsoMovingObject) {
					float6 = IsoUtils.XToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					float7 = IsoUtils.YToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					float6 -= float4;
					float7 -= float5;
					if (object != null) {
						object.sx = float6;
						object.sy = float7;
					}
				}

				if (object != null) {
					float6 = object.sx + globalOffsetX;
					float7 = object.sy + globalOffsetY;
					float6 += (float)this.soffX;
					float7 += (float)this.soffY;
				} else {
					float6 += globalOffsetX;
					float7 += globalOffsetY;
					float6 += (float)this.soffX;
					float7 += (float)this.soffY;
				}

				ColorInfo colorInfo2;
				if (boolean1) {
					if (spriteInstance.tintr != 1.0F || spriteInstance.tintg != 1.0F || spriteInstance.tintb != 1.0F) {
						colorInfo2 = info;
						colorInfo2.r *= spriteInstance.tintr;
						colorInfo2 = info;
						colorInfo2.g *= spriteInstance.tintg;
						colorInfo2 = info;
						colorInfo2.b *= spriteInstance.tintb;
					}

					info.a = spriteInstance.alpha;
				}

				if (!this.hasActiveModel() && (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F)) {
					colorInfo2 = info;
					colorInfo2.r *= this.TintMod.r;
					colorInfo2 = info;
					colorInfo2.g *= this.TintMod.g;
					colorInfo2 = info;
					colorInfo2.b *= this.TintMod.b;
				}

				if (this.hasActiveModel()) {
					float float8 = spriteInstance.getScaleX() * (float)Core.TileScale;
					float float9 = -spriteInstance.getScaleY() * (float)Core.TileScale;
					float float10 = 0.666F;
					float8 /= 4.0F * float10;
					float9 /= 4.0F * float10;
					int int1 = ModelManager.instance.bitmap.getTexture().getWidth();
					int int2 = ModelManager.instance.bitmap.getTexture().getHeight();
					float6 -= (float)int1 * float8 / 2.0F;
					float7 -= (float)int2 * float9 / 2.0F;
					float float11 = ((BaseVehicle)object).jniTransform.origin.y / 2.46F;
					float7 += 96.0F * float11 / float9 / float10;
					float7 += 27.84F / float9 / float10;
					if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
						SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), float6, float7, (float)int1 * float8, (float)int2 * float9, 1.0F, 1.0F, 1.0F, info.a, (Consumer)null);
					} else {
						SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), float6, float7, (float)int1 * float8, (float)int2 * float9, info.r, info.g, info.b, info.a, (Consumer)null);
					}

					if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
						LineDrawer.drawRect(float6, float7, (float)int1 * float8, (float)int2 * float9, 1.0F, 1.0F, 1.0F, 1.0F, 1);
					}
				}

				info.r = 1.0F;
				info.g = 1.0F;
				info.b = 1.0F;
			} catch (Exception exception) {
				Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	private IsoSpriteInstance getSpriteInstance() {
		this.initSpriteInstance();
		return this.def;
	}

	private void initSpriteInstance() {
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this);
		}
	}

	public final void render(IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		this.render(object, float1, float2, float3, directions, float4, float5, colorInfo, boolean1, (Consumer)null);
	}

	public final void render(IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1, Consumer consumer) {
		this.render(this.getSpriteInstance(), object, float1, float2, float3, directions, float4, float5, colorInfo, boolean1, consumer);
	}

	public final void render(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		this.render(spriteInstance, object, float1, float2, float3, directions, float4, float5, colorInfo, boolean1, (Consumer)null);
	}

	public void render(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1, Consumer consumer) {
		if (this.hasActiveModel()) {
			this.renderActiveModel();
		} else {
			this.renderCurrentAnim(spriteInstance, object, float1, float2, float3, directions, float4, float5, colorInfo, boolean1, consumer);
		}
	}

	public void renderCurrentAnim(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1, Consumer consumer) {
		if (DebugOptions.instance.IsoSprite.RenderSprites.getValue()) {
			if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
				float float6 = this.getCurrentSpriteFrame(spriteInstance);
				info.set(colorInfo);
				Vector3 vector3 = IsoSprite.l_renderCurrentAnim.colorInfoBackup.set(info.r, info.g, info.b);
				Vector2 vector2 = IsoSprite.l_renderCurrentAnim.spritePos.set(0.0F, 0.0F);
				this.prepareToRenderSprite(spriteInstance, object, float1, float2, float3, directions, float4, float5, boolean1, (int)float6, vector2);
				this.performRenderFrame(spriteInstance, object, directions, (int)float6, vector2.x, vector2.y, consumer);
				info.r = vector3.x;
				info.g = vector3.y;
				info.b = vector3.z;
			}
		}
	}

	private float getCurrentSpriteFrame(IsoSpriteInstance spriteInstance) {
		if (this.CurrentAnim.FramesArray == null) {
			this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
		}

		if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
			this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
		}

		float float1;
		if (spriteInstance.Frame >= (float)this.CurrentAnim.Frames.size()) {
			float1 = (float)(this.CurrentAnim.FramesArray.length - 1);
		} else if (spriteInstance.Frame < 0.0F) {
			spriteInstance.Frame = 0.0F;
			float1 = 0.0F;
		} else {
			float1 = spriteInstance.Frame;
		}

		return float1;
	}

	private void prepareToRenderSprite(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, boolean boolean1, int int1, Vector2 vector2) {
		if (boolean1) {
			spriteInstance.renderprep(object);
		}

		float float6 = 0.0F;
		float float7 = 0.0F;
		if (globalOffsetX == -1.0F) {
			globalOffsetX = -IsoCamera.frameState.OffX;
			globalOffsetY = -IsoCamera.frameState.OffY;
		}

		if (object != null && object.sx != 0.0F && !(object instanceof IsoMovingObject)) {
			if (object != null) {
				float6 = object.sx + globalOffsetX;
				float7 = object.sy + globalOffsetY;
				float6 += (float)this.soffX;
				float7 += (float)this.soffY;
			} else {
				float6 += globalOffsetX;
				float7 += globalOffsetY;
				float6 += (float)this.soffX;
				float7 += (float)this.soffY;
			}
		} else {
			float6 = IsoUtils.XToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
			float7 = IsoUtils.YToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
			float6 -= float4;
			float7 -= float5;
			if (object != null) {
				object.sx = float6;
				object.sy = float7;
			}

			float6 += globalOffsetX;
			float7 += globalOffsetY;
			float6 += (float)this.soffX;
			float7 += (float)this.soffY;
		}

		if (object instanceof IsoMovingObject && this.CurrentAnim != null && this.CurrentAnim.FramesArray[int1].getTexture(directions) != null) {
			float6 -= (float)(this.CurrentAnim.FramesArray[int1].getTexture(directions).getWidthOrig() / 2) * spriteInstance.getScaleX();
			float7 -= (float)this.CurrentAnim.FramesArray[int1].getTexture(directions).getHeightOrig() * spriteInstance.getScaleY();
		}

		ColorInfo colorInfo;
		if (boolean1) {
			if (spriteInstance.tintr != 1.0F || spriteInstance.tintg != 1.0F || spriteInstance.tintb != 1.0F) {
				colorInfo = info;
				colorInfo.r *= spriteInstance.tintr;
				colorInfo = info;
				colorInfo.g *= spriteInstance.tintg;
				colorInfo = info;
				colorInfo.b *= spriteInstance.tintb;
			}

			info.a = spriteInstance.alpha;
			if (spriteInstance.bMultiplyObjectAlpha && object != null) {
				colorInfo = info;
				colorInfo.a *= object.getAlpha(IsoCamera.frameState.playerIndex);
			}
		}

		if (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F) {
			colorInfo = info;
			colorInfo.r *= this.TintMod.r;
			colorInfo = info;
			colorInfo.g *= this.TintMod.g;
			colorInfo = info;
			colorInfo.b *= this.TintMod.b;
		}

		vector2.set(float6, float7);
	}

	private void performRenderFrame(IsoSpriteInstance spriteInstance, IsoObject object, IsoDirections directions, int int1, float float1, float float2, Consumer consumer) {
		if (int1 < this.CurrentAnim.FramesArray.length) {
			IsoDirectionFrame directionFrame = this.CurrentAnim.FramesArray[int1];
			Texture texture = directionFrame.getTexture(directions);
			if (texture != null) {
				if (Core.TileScale == 2 && texture.getWidthOrig() == 64 && texture.getHeightOrig() == 128) {
					spriteInstance.setScale(2.0F, 2.0F);
				}

				if (Core.TileScale == 2 && spriteInstance.scaleX == 2.0F && spriteInstance.scaleY == 2.0F && texture.getWidthOrig() == 128 && texture.getHeightOrig() == 256) {
					spriteInstance.setScale(1.0F, 1.0F);
				}

				if (!(spriteInstance.scaleX <= 0.0F) && !(spriteInstance.scaleY <= 0.0F)) {
					float float3 = (float)texture.getWidth();
					float float4 = (float)texture.getHeight();
					float float5 = spriteInstance.scaleX;
					float float6 = spriteInstance.scaleY;
					if (float5 != 1.0F) {
						float1 += texture.getOffsetX() * (float5 - 1.0F);
						float3 *= float5;
					}

					if (float6 != 1.0F) {
						float2 += texture.getOffsetY() * (float6 - 1.0F);
						float4 *= float6;
					}

					if (DebugOptions.instance.IsoSprite.MovingObjectEdges.getValue() && object instanceof IsoMovingObject) {
						this.renderSpriteOutline(float1, float2, texture, float5, float6);
					}

					if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue() && StringUtils.equals(texture.getName(), "dropshadow")) {
						this.renderSpriteOutline(float1, float2, texture, float5, float6);
					}

					if (!this.hideForWaterRender || !IsoWater.getInstance().getShaderEnable()) {
						if (object != null && object.getObjectRenderEffectsToApply() != null) {
							directionFrame.render(object.getObjectRenderEffectsToApply(), float1, float2, float3, float4, directions, info, spriteInstance.Flip, consumer);
						} else {
							directionFrame.render(float1, float2, float3, float4, directions, info, spriteInstance.Flip, consumer);
						}
					}

					if (int1 < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && object != null) {
						boolean boolean1 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
						if (spriteInstance.Flip) {
							boolean1 = !boolean1;
						}

						float1 = object.sx + globalOffsetX;
						float2 = object.sy + globalOffsetY;
						if (object instanceof IsoMovingObject) {
							float1 -= (float)(texture.getWidthOrig() / 2) * float5;
							float2 -= (float)texture.getHeightOrig() * float6;
						}

						IsoObjectPicker.Instance.Add((int)float1, (int)float2, (int)((float)texture.getWidthOrig() * float5), (int)((float)texture.getHeightOrig() * float6), object.square, object, boolean1, float5, float6);
					}
				}
			}
		}
	}

	private void renderSpriteOutline(float float1, float float2, Texture texture, float float3, float float4) {
		LineDrawer.drawRect(float1, float2, (float)texture.getWidthOrig() * float3, (float)texture.getHeightOrig() * float4, 1.0F, 1.0F, 1.0F, 1.0F, 1);
		LineDrawer.drawRect(float1 + texture.getOffsetX() * float3, float2 + texture.getOffsetY() * float4, (float)texture.getWidth() * float3, (float)texture.getHeight() * float4, 1.0F, 1.0F, 1.0F, 1.0F, 1);
	}

	public void renderActiveModel() {
		if (DebugOptions.instance.IsoSprite.RenderModels.getValue()) {
			this.modelSlot.model.updateLights();
			SpriteRenderer.instance.drawGeneric(((ModelCameraRenderData)ModelCameraRenderData.s_pool.alloc()).init(CharacterModelCamera.instance, this.modelSlot));
			SpriteRenderer.instance.drawModel(this.modelSlot);
		}
	}

	public void renderBloodSplat(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
			boolean boolean1 = true;
			boolean boolean2 = true;
			byte byte1 = 0;
			byte byte2 = 0;
			try {
				if (globalOffsetX == -1.0F) {
					globalOffsetX = -IsoCamera.frameState.OffX;
					globalOffsetY = -IsoCamera.frameState.OffY;
				}

				float float4 = IsoUtils.XToScreen(float1, float2, float3, 0);
				float float5 = IsoUtils.YToScreen(float1, float2, float3, 0);
				float4 = (float)((int)float4);
				float5 = (float)((int)float5);
				float4 -= (float)byte1;
				float5 -= (float)byte2;
				float4 += globalOffsetX;
				float5 += globalOffsetY;
				if (!(float4 >= (float)IsoCamera.frameState.OffscreenWidth) && !(float4 + 64.0F <= 0.0F)) {
					if (!(float5 >= (float)IsoCamera.frameState.OffscreenHeight) && !(float5 + 64.0F <= 0.0F)) {
						info.r = colorInfo.r;
						info.g = colorInfo.g;
						info.b = colorInfo.b;
						info.a = colorInfo.a;
						((IsoDirectionFrame)this.CurrentAnim.Frames.get(0)).render(float4, float5, IsoDirections.N, info, false, (Consumer)null);
					}
				}
			} catch (Exception exception) {
				Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	public void renderObjectPicker(IsoSpriteInstance spriteInstance, IsoObject object, IsoDirections directions) {
		if (this.CurrentAnim != null) {
			if (spriteInstance != null) {
				if (IsoPlayer.getInstance() == IsoPlayer.players[0]) {
					if (!this.CurrentAnim.Frames.isEmpty()) {
						if (spriteInstance.Frame >= (float)this.CurrentAnim.Frames.size()) {
							spriteInstance.Frame = 0.0F;
						}

						if (((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions) != null) {
							float float1 = object.sx + globalOffsetX;
							float float2 = object.sy + globalOffsetY;
							if (object instanceof IsoMovingObject) {
								float1 -= (float)(((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions).getWidthOrig() / 2) * spriteInstance.getScaleX();
								float2 -= (float)((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions).getHeightOrig() * spriteInstance.getScaleY();
							}

							if (spriteInstance.Frame < (float)this.CurrentAnim.Frames.size() && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
								Texture texture = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions);
								boolean boolean1 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
								if (spriteInstance.Flip) {
									boolean1 = !boolean1;
								}

								IsoObjectPicker.Instance.Add((int)float1, (int)float2, (int)((float)texture.getWidthOrig() * spriteInstance.getScaleX()), (int)((float)texture.getHeightOrig() * spriteInstance.getScaleY()), object.square, object, boolean1, spriteInstance.getScaleX(), spriteInstance.getScaleY());
							}
						}
					}
				}
			}
		}
	}

	public Texture getTextureForFrame(int int1, IsoDirections directions) {
		if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
			if (this.CurrentAnim.FramesArray == null) {
				this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
			}

			if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
				this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
			}

			if (int1 >= this.CurrentAnim.FramesArray.length) {
				int1 = this.CurrentAnim.FramesArray.length - 1;
			}

			if (int1 < 0) {
				int1 = 0;
			}

			return this.CurrentAnim.FramesArray[int1].getTexture(directions);
		} else {
			return null;
		}
	}

	public Texture getTextureForCurrentFrame(IsoDirections directions) {
		this.initSpriteInstance();
		return this.getTextureForFrame((int)this.def.Frame, directions);
	}

	public void update() {
		this.update(this.def);
	}

	public void update(IsoSpriteInstance spriteInstance) {
		if (spriteInstance == null) {
			spriteInstance = IsoSpriteInstance.get(this);
		}

		if (this.CurrentAnim != null) {
			if (this.Animate && !spriteInstance.Finished) {
				float float1 = spriteInstance.Frame;
				if (!GameTime.isGamePaused()) {
					spriteInstance.Frame += spriteInstance.AnimFrameIncrease * GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
				}

				if ((int)spriteInstance.Frame >= this.CurrentAnim.Frames.size() && this.Loop && spriteInstance.Looped) {
					spriteInstance.Frame = 0.0F;
				}

				if ((int)float1 != (int)spriteInstance.Frame) {
					spriteInstance.NextFrame = true;
				}

				if ((int)spriteInstance.Frame >= this.CurrentAnim.Frames.size() && (!this.Loop || !spriteInstance.Looped)) {
					spriteInstance.Finished = true;
					spriteInstance.Frame = (float)this.CurrentAnim.FinishUnloopedOnFrame;
					if (this.DeleteWhenFinished) {
						this.Dispose();
						this.Animate = false;
					}
				}
			}
		}
	}

	public void CacheAnims(String string) {
		this.name = string;
		Stack stack = new Stack();
		for (int int1 = 0; int1 < this.AnimStack.size(); ++int1) {
			IsoAnim anim = (IsoAnim)this.AnimStack.get(int1);
			String string2 = string + anim.name;
			stack.add(string2);
			if (!IsoAnim.GlobalAnimMap.containsKey(string2)) {
				IsoAnim.GlobalAnimMap.put(string2, anim);
			}
		}

		AnimNameSet.put(string, stack.toArray());
	}

	public void LoadCache(String string) {
		Object[] objectArray = (Object[])AnimNameSet.get(string);
		this.name = string;
		for (int int1 = 0; int1 < objectArray.length; ++int1) {
			String string2 = (String)objectArray[int1];
			IsoAnim anim = (IsoAnim)IsoAnim.GlobalAnimMap.get(string2);
			this.AnimMap.put(anim.name, anim);
			this.AnimStack.add(anim);
			this.CurrentAnim = anim;
		}
	}

	public IsoSprite setFromCache(String string, String string2, int int1) {
		String string3 = string + string2;
		if (HasCache(string3)) {
			this.LoadCache(string3);
		} else {
			this.LoadFramesNoDirPage(string, string2, int1);
			this.CacheAnims(string3);
		}

		return this;
	}

	public IsoObjectType getType() {
		return this.type;
	}

	public void setType(IsoObjectType objectType) {
		this.type = objectType;
	}

	public void AddProperties(IsoSprite sprite) {
		this.getProperties().AddProperties(sprite.getProperties());
	}

	public int getID() {
		return this.ID;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public ColorInfo getTintMod() {
		return this.TintMod;
	}

	public void setTintMod(ColorInfo colorInfo) {
		this.TintMod.set(colorInfo);
	}

	public void setAnimate(boolean boolean1) {
		this.Animate = boolean1;
	}

	public IsoSpriteGrid getSpriteGrid() {
		return this.spriteGrid;
	}

	public void setSpriteGrid(IsoSpriteGrid spriteGrid) {
		this.spriteGrid = spriteGrid;
	}

	public boolean isMoveWithWind() {
		return this.moveWithWind;
	}

	public int getSheetGridIdFromName() {
		return this.name != null ? getSheetGridIdFromName(this.name) : -1;
	}

	public static int getSheetGridIdFromName(String string) {
		if (string != null) {
			int int1 = string.lastIndexOf(95);
			if (int1 > 0 && int1 + 1 < string.length()) {
				return Integer.parseInt(string.substring(int1 + 1));
			}
		}

		return -1;
	}

	private static class l_renderCurrentAnim {
		static final Vector3 colorInfoBackup = new Vector3();
		static final Vector2 spritePos = new Vector2();
	}
}
