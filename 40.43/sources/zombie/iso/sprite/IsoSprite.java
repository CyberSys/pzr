package zombie.iso.sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.ModelCamera;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Mask;
import zombie.core.textures.PaletteManager;
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
import zombie.iso.IsoWorld;
import zombie.iso.WorldConverter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.ui.UIManager;


public class IsoSprite {
	public static int maxCount = 0;
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
	public boolean moveWithWind = false;
	public boolean isBush = false;
	public int windType = 1;
	public static float alphaStep = 0.05F;
	public boolean Animate = true;
	public boolean AnimateWhenPaused = false;
	public IsoAnim CurrentAnim = null;
	public boolean DeleteWhenFinished = false;
	public short sprOffX = 0;
	public boolean Loop = true;
	public short soffX = 0;
	public short soffY = 0;
	public PropertyContainer Properties = new PropertyContainer();
	public ColorInfo TintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
	public HashMap AnimMap = new HashMap(2);
	public ArrayList AnimStack = new ArrayList(1);
	float lx = -1.0F;
	float ly = -1.0F;
	float lz = -1.0F;
	IsoSpriteManager parentManager;
	public float Angle = 0.0F;
	public String name;
	private String parentObjectName = null;
	public boolean Scissor;
	public int ID = 20000000;
	public IsoSpriteInstance def;
	static ColorInfo info = new ColorInfo();
	public static int globalOffsetX = -1;
	public static int globalOffsetY = -1;
	public ModelManager.ModelSlot modelSlot;
	static HashMap AnimNameSet = new HashMap();
	IsoObjectType type;
	private IsoSpriteGrid spriteGrid;

	public IsoSpriteInstance newInstance() {
		return IsoSpriteInstance.get(this);
	}

	public PropertyContainer getProperties() {
		return this.Properties;
	}

	public void setProperties(PropertyContainer propertyContainer) {
		this.Properties = propertyContainer;
	}

	public String getParentObjectName() {
		return this.parentObjectName;
	}

	public IsoSprite() {
		this.type = IsoObjectType.MAX;
		if (IsoWorld.instance.CurrentCell != null) {
			this.parentManager = IsoWorld.instance.CurrentCell.SpriteManager;
		}

		this.def = IsoSpriteInstance.get(this);
	}

	public IsoSprite(IsoSpriteManager spriteManager) {
		this.type = IsoObjectType.MAX;
		this.parentManager = spriteManager;
		this.def = IsoSpriteInstance.get(this);
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		GameWindow.WriteString(dataOutputStream, this.name);
	}

	public void load(DataInputStream dataInputStream) throws IOException {
		this.name = GameWindow.ReadString(dataInputStream);
		this.LoadFramesNoDirPageSimple(this.name);
	}

	public static IsoSprite CreateSprite(IsoSpriteManager spriteManager) {
		IsoSprite sprite = new IsoSprite(spriteManager);
		return sprite;
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
			return (IsoSprite)spriteManager.NamedMap.get(sprite.name.substring(0, sprite.name.lastIndexOf("_")) + "_" + int2);
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

	public static void DisposeAll() {
		AnimNameSet.clear();
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
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			return true;
		}
	}

	public boolean isMaskClicked(IsoDirections directions, int int1, int int2, boolean boolean1) {
		if (this.CurrentAnim == null) {
			return false;
		} else {
			if (this.def == null) {
				this.def = IsoSpriteInstance.get(this);
			}

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
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
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
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
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

	public void DupeFrame() {
		this.CurrentAnim.DupeFrame();
	}

	public void LoadExtraFrame(String string, String string2, int int1) {
		this.CurrentAnim.LoadExtraFrame(string, string2, int1);
	}

	public void LoadFramesBits(String string, String string2, String string3, int int1) {
		if (!this.AnimMap.containsKey(string3)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string3, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesBits(string, string2, string3, int1);
		}
	}

	public void LoadFramesUseOtherFrame(String string, String string2, String string3, String string4, int int1, String string5) {
		if (!this.AnimMap.containsKey(string3)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string3, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesUseOtherFrame(string, string2, string3, string4, int1, string5);
		}
	}

	public void AddFramesUseOtherFrame(String string, String string2, String string3, String string4, int int1, String string5) {
		this.CurrentAnim.LoadFramesUseOtherFrame(string, string2, string3, string4, int1, string5);
	}

	public void LoadFramesBits(String string, String string2, String string3, int int1, String string4) {
		if (!this.AnimMap.containsKey(string3)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string3, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesBits(string, string2, string3, int1, string4);
		}
	}

	public void LoadFramesBits(String string, String string2, int int1) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesBits(string, string2, int1);
		}
	}

	public void LoadFramesBitRepeatFrame(String string, String string2, String string3, int int1, String string4) {
		this.CurrentAnim = (IsoAnim)this.AnimMap.get(string3);
	}

	public void LoadFramesBitRepeatFrame(String string, String string2, int int1) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesBitRepeatFrame(string, string2, int1);
		}
	}

	public void LoadFramesNoDir(String string, String string2, int int1) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesNoDir(string, string2, int1);
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

	public void LoadFramesNoDirPalette(String string, String string2, int int1, String string3) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesNoDirPalette(string, string2, int1, string3);
		}
	}

	public void LoadFramesPalette(String string, String string2, int int1, PaletteManager.PaletteInfo paletteInfo) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesPalette(string, string2, int1, paletteInfo);
		}
	}

	public void LoadFramesPalette(String string, String string2, int int1, String string3) {
		if (!this.AnimMap.containsKey(string2)) {
			this.CurrentAnim = new IsoAnim();
			this.AnimMap.put(string2, this.CurrentAnim);
			this.CurrentAnim.ID = this.AnimStack.size();
			this.AnimStack.add(this.CurrentAnim);
			this.CurrentAnim.LoadFramesPalette(string, string2, int1, string3);
		}
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

	public void PlayAnimNoReset(String string) {
		if (this.AnimMap.containsKey(string) && (this.CurrentAnim == null || !this.CurrentAnim.name.equals(string))) {
			this.CurrentAnim = (IsoAnim)this.AnimMap.get(string);
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
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo());
	}

	public void RenderGhostTileRed(int int1, int int2, int int3) {
		IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
		spriteInstance.tintr = 0.65F;
		spriteInstance.tintg = 0.2F;
		spriteInstance.tintb = 0.2F;
		spriteInstance.alpha = spriteInstance.targetAlpha = 0.6F;
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * Core.TileScale), (float)(96 * Core.TileScale), IsoGridSquare.getDefColorInfo());
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
		this.render(spriteInstance, (IsoObject)null, (float)int1, (float)int2, (float)int3, IsoDirections.N, (float)(32 * int4) + float1, (float)(96 * int4) + float2, IsoGridSquare.getDefColorInfo());
	}

	public void render(IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo) {
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this);
		}

		this.render(this.def, object, float1, float2, float3, directions, float4, float5, colorInfo, true);
	}

	public void render(IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this);
		}

		this.render(this.def, object, float1, float2, float3, directions, float4, float5, colorInfo, boolean1);
	}

	public float getScreenY(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 0.0F;
		float6 = IsoUtils.YToScreen(float1 + this.def.offX, float2 + this.def.offY, float3 + this.def.offZ, 0);
		return float6;
	}

	public float getScreenX(float float1, float float2, float float3, float float4, float float5) {
		float float6 = 0.0F;
		float6 = IsoUtils.XToScreen(float1 + this.def.offX, float2 + this.def.offY, float3 + this.def.offZ, 0);
		return float6;
	}

	public void drawAt(IsoSpriteInstance spriteInstance, IsoObject object, int int1, int int2, IsoDirections directions) {
		if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
			try {
				float float1 = spriteInstance.Frame;
				if (spriteInstance.Frame >= (float)this.CurrentAnim.Frames.size()) {
					float1 = (float)(this.CurrentAnim.Frames.size() - 1);
				}

				if (spriteInstance.Frame < 0.0F) {
					spriteInstance.Frame = 0.0F;
					float1 = 0.0F;
				}

				if (spriteInstance != null) {
					spriteInstance.renderprep(object);
				}

				if (this.CurrentAnim == null) {
					return;
				}

				if (object instanceof IsoMovingObject && this.CurrentAnim != null && ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)float1)).getTexture(directions) != null) {
					int1 -= ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)float1)).getTexture(directions).getWidthOrig() / 2;
					int2 -= ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)float1)).getTexture(directions).getHeightOrig();
				}

				info.r = 1.0F;
				info.g = 1.0F;
				info.b = 1.0F;
				info.a = 1.0F;
				if ((int)float1 < this.CurrentAnim.Frames.size()) {
					((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)float1)).renderexplicit(int1, int2, directions, 1.0F, this.TintMod);
				} else {
					boolean boolean1 = false;
				}
			} catch (Exception exception) {
				IndieGL.End();
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	public void render(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo) {
		this.render(spriteInstance, object, float1, float2, float3, directions, float4, float5, colorInfo, true);
	}

	public boolean hasActiveModel() {
		if (!ModelManager.instance.bDebugEnableModels) {
			return false;
		} else if (!PerformanceSettings.modelsEnabled) {
			return false;
		} else if (!ModelManager.instance.bCreated) {
			return false;
		} else {
			return this.modelSlot != null && this.modelSlot.active && !this.modelSlot.bRemove;
		}
	}

	public void renderVehicle(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		if (spriteInstance != null) {
			if (this.hasActiveModel()) {
				SpriteRenderer.instance.drawModel(this.modelSlot);
			}

			info.r = colorInfo.r;
			info.g = colorInfo.g;
			info.b = colorInfo.b;
			info.a = colorInfo.a;
			try {
				if (spriteInstance != null && boolean1) {
					spriteInstance.renderprep(object);
				}

				float float6 = 0.0F;
				float float7 = 0.0F;
				if (globalOffsetX == -1) {
					globalOffsetX = -((int)IsoCamera.frameState.OffX);
					globalOffsetY = -((int)IsoCamera.frameState.OffY);
				}

				if (object == null || object.sx == 0 || object instanceof IsoMovingObject) {
					float6 = IsoUtils.XToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					float7 = IsoUtils.YToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					this.lx = float1;
					this.ly = float2;
					this.lz = float3;
					float6 = (float)((int)float6);
					float7 = (float)((int)float7);
					float6 -= float4;
					float7 -= float5;
					if (object != null) {
						object.sx = (int)float6;
						object.sy = (int)float7;
					}
				}

				if (object != null) {
					float6 = (float)(object.sx + globalOffsetX);
					float7 = (float)(object.sy + globalOffsetY);
					float6 += (float)this.soffX;
					float7 += (float)this.soffY;
				} else {
					float6 += (float)globalOffsetX;
					float7 += (float)globalOffsetY;
					float6 += (float)this.soffX;
					float7 += (float)this.soffY;
				}

				float float8 = info.r;
				float float9 = info.g;
				float float10 = info.b;
				ColorInfo colorInfo2;
				if (spriteInstance != null && boolean1) {
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
					float float11 = 2.0F * spriteInstance.getScaleX() * (float)Core.TileScale;
					float float12 = -2.0F * spriteInstance.getScaleY() * (float)Core.TileScale;
					float float13 = ModelCamera.instance.VehicleScaleHack;
					float11 *= float13;
					float12 *= float13;
					int int1 = ModelManager.instance.bitmap.getTexture().getWidth();
					int int2 = ModelManager.instance.bitmap.getTexture().getHeight();
					float6 -= (float)int1 * float11 / 16.0F;
					float7 -= (float)int2 * float12 / 8.0F;
					float6 += 0.0F * float11;
					float7 += 69.0F * float12 / 2.0F + 43.0F * float12;
					float7 += 27.0F * (float13 - 1.0F) * (float)Core.TileScale;
					if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
						SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), float6, float7, (float)int1 * float11 / 8.0F, (float)int2 * float12 / 8.0F, 1.0F, 1.0F, 1.0F, info.a);
					} else {
						SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), float6, float7, (float)int1 * float11 / 8.0F, (float)int2 * float12 / 8.0F, info.r, info.g, info.b, info.a);
					}

					if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
						LineDrawer.drawRect(float6, float7, (float)int1 * float11 / 8.0F, (float)int2 * float12 / 8.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1);
					}
				}

				info.r = 1.0F;
				info.g = 1.0F;
				info.b = 1.0F;
			} catch (Exception exception) {
				IndieGL.End();
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	public void render(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo, boolean boolean1) {
		if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
			if (this.CurrentAnim.FramesArray == null) {
				this.CurrentAnim.FramesArray = (IsoDirectionFrame[])this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
			}

			if (this.modelSlot != null && this.modelSlot.active && !this.modelSlot.bRemove && this.hasActiveModel()) {
				SpriteRenderer.instance.drawModel(this.modelSlot);
			}

			info.r = colorInfo.r;
			info.g = colorInfo.g;
			info.b = colorInfo.b;
			info.a = colorInfo.a;
			try {
				float float6 = spriteInstance.Frame;
				if (spriteInstance.Frame >= (float)this.CurrentAnim.Frames.size()) {
					float6 = (float)(this.CurrentAnim.FramesArray.length - 1);
				}

				if (spriteInstance.Frame < 0.0F) {
					spriteInstance.Frame = 0.0F;
					float6 = 0.0F;
				}

				if (spriteInstance != null && boolean1) {
					if (spriteInstance.bCopyTargetAlpha && object != null) {
						spriteInstance.targetAlpha = object.targetAlpha[IsoCamera.frameState.playerIndex];
						spriteInstance.alpha = object.alpha[IsoCamera.frameState.playerIndex];
					} else {
						spriteInstance.renderprep(object);
					}
				}

				float float7 = 0.0F;
				float float8 = 0.0F;
				if (this.CurrentAnim == null) {
					return;
				}

				if (globalOffsetX == -1) {
					globalOffsetX = -((int)IsoCamera.frameState.OffX);
					globalOffsetY = -((int)IsoCamera.frameState.OffY);
				}

				if (object == null || object.sx == 0 || object instanceof IsoMovingObject) {
					float7 = IsoUtils.XToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					float8 = IsoUtils.YToScreen(float1 + spriteInstance.offX, float2 + spriteInstance.offY, float3 + spriteInstance.offZ, 0);
					this.lx = float1;
					this.ly = float2;
					this.lz = float3;
					float7 = (float)((int)float7);
					float8 = (float)((int)float8);
					float7 -= float4;
					float8 -= float5;
					if (object != null) {
						object.sx = (int)float7;
						object.sy = (int)float8;
					}
				}

				if (object != null) {
					float7 = (float)(object.sx + globalOffsetX);
					float8 = (float)(object.sy + globalOffsetY);
					float7 += (float)this.soffX;
					float8 += (float)this.soffY;
				} else {
					float7 += (float)globalOffsetX;
					float8 += (float)globalOffsetY;
					float7 += (float)this.soffX;
					float8 += (float)this.soffY;
				}

				if (object instanceof IsoMovingObject && !this.hasActiveModel() && this.CurrentAnim != null && this.CurrentAnim.FramesArray[(int)float6].getTexture(directions) != null) {
					float7 -= (float)(this.CurrentAnim.FramesArray[(int)float6].getTexture(directions).getWidthOrig() / 2) * spriteInstance.getScaleX();
					float8 -= (float)this.CurrentAnim.FramesArray[(int)float6].getTexture(directions).getHeightOrig() * spriteInstance.getScaleY();
				}

				float float9 = info.r;
				float float10 = info.g;
				float float11 = info.b;
				ColorInfo colorInfo2;
				if (spriteInstance != null && boolean1) {
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

				if ((!(object instanceof IsoMovingObject) || !this.hasActiveModel()) && (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F)) {
					colorInfo2 = info;
					colorInfo2.r *= this.TintMod.r;
					colorInfo2 = info;
					colorInfo2.g *= this.TintMod.g;
					colorInfo2 = info;
					colorInfo2.b *= this.TintMod.b;
				}

				if ((int)float6 < this.CurrentAnim.FramesArray.length) {
					Texture texture = this.CurrentAnim.FramesArray[(int)float6].getTexture(directions);
					if (Core.TileScale == 2 && texture != null && texture.getWidthOrig() == 64 && texture.getHeightOrig() == 128) {
						spriteInstance.setScale(2.0F, 2.0F);
					}

					if (Core.TileScale == 2 && spriteInstance.scaleX == 2.0F && spriteInstance.scaleY == 2.0F && texture != null && texture.getWidthOrig() == 128 && texture.getHeightOrig() == 256) {
						spriteInstance.setScale(1.0F, 1.0F);
					}

					float float12;
					float float13;
					boolean boolean2;
					if (object instanceof IsoMovingObject && this.hasActiveModel()) {
						float12 = spriteInstance.getScaleX();
						float13 = spriteInstance.getScaleY();
						int int1 = ModelManager.instance.bitmap.getTexture().getWidth();
						int int2 = ModelManager.instance.bitmap.getTexture().getHeight();
						float7 -= (float)int1 * float12 / 16.0F;
						float8 -= (float)int2 * float13 / 8.0F;
						float7 += (float)IsoGameCharacter.RENDER_OFFSET_X * float12;
						float8 += 69.0F * float13 / 2.0F + (float)IsoGameCharacter.RENDER_OFFSET_Y * float13;
						SpriteRenderer.instance.render((Texture)ModelManager.instance.bitmap.getTexture(), float7, float8, (float)int1 * float12 / 8.0F, (float)int2 * float13 / 8.0F, info.r, info.g, info.b, info.a);
						if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
							LineDrawer.drawRect(float7, float8, (float)int1 * float12 / 8.0F, (float)int2 * float13 / 8.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1);
						}

						if ((int)float6 < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
							Texture texture2 = this.CurrentAnim.FramesArray[(int)float6].getTexture(directions);
							if (object != null) {
								boolean2 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
								if (spriteInstance.Flip) {
									boolean2 = !boolean2;
								}

								float7 = (float)(object.sx + globalOffsetX);
								float8 = (float)(object.sy + globalOffsetY);
								if (object instanceof IsoMovingObject) {
									float7 -= (float)(texture2.getWidthOrig() / 2) * float12;
									float8 -= (float)texture2.getHeightOrig() * float13;
								}

								IsoObjectPicker.Instance.Add((int)float7, (int)float8, (int)((float)texture2.getWidthOrig() * float12), (int)((float)texture2.getHeightOrig() * float13), object.square, object, boolean2, float12, float13);
							}
						}
					} else {
						float12 = spriteInstance.scaleX;
						float13 = spriteInstance.scaleY;
						if (float12 == 1.0F && float13 == 1.0F) {
							IsoDirectionFrame directionFrame = this.CurrentAnim.FramesArray[(int)float6];
							if (object != null && object.getObjectRenderEffectsToApply() != null) {
								directionFrame.render(object.getObjectRenderEffectsToApply(), (float)((int)float7), (float)((int)float8), directions, info, spriteInstance.Flip, this.Angle);
							} else {
								directionFrame.render((float)((int)float7), (float)((int)float8), directions, info, spriteInstance.Flip, this.Angle);
							}

							if (IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && (int)float6 < this.CurrentAnim.FramesArray.length && object != null && texture != null) {
								boolean boolean3 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
								if (spriteInstance.Flip) {
									boolean3 = !boolean3;
								}

								float7 = (float)(object.sx + globalOffsetX);
								float8 = (float)(object.sy + globalOffsetY);
								if (object instanceof IsoMovingObject) {
									float7 -= (float)(texture.getWidthOrig() / 2) * float12;
									float8 -= (float)texture.getHeightOrig() * float13;
								}

								IsoObjectPicker.Instance.Add((int)float7, (int)float8, (int)((float)texture.getWidthOrig() * float12), (int)((float)texture.getHeightOrig() * float13), object.square, object, boolean3, float12, float13);
							}
						} else {
							Texture texture3 = this.CurrentAnim.FramesArray[(int)float6].getTexture(directions);
							if (texture3 != null && float12 > 0.0F && float13 > 0.0F) {
								float float14 = (float)texture3.getWidth();
								float float15 = (float)texture3.getHeight();
								if (Core.bDebug) {
								}

								if (float12 != 1.0F) {
									float7 += texture3.getOffsetX() * (float12 - 1.0F);
									float14 *= float12;
								}

								if (float13 != 1.0F) {
									float8 += texture3.getOffsetY() * (float13 - 1.0F);
									float15 *= float13;
								}

								if (object != null && object.getObjectRenderEffectsToApply() != null) {
									this.CurrentAnim.FramesArray[(int)float6].render(object.getObjectRenderEffectsToApply(), (float)((int)float7), (float)((int)float8), float14, float15, directions, info, spriteInstance.Flip, this.Angle);
								} else {
									this.CurrentAnim.FramesArray[(int)float6].render((float)((int)float7), (float)((int)float8), float14, float15, directions, info, spriteInstance.Flip, this.Angle);
								}

								if ((int)float6 < this.CurrentAnim.FramesArray.length && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0 && object != null) {
									boolean2 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
									if (spriteInstance.Flip) {
										boolean2 = !boolean2;
									}

									float7 = (float)(object.sx + globalOffsetX);
									float8 = (float)(object.sy + globalOffsetY);
									if (object instanceof IsoMovingObject) {
										float7 -= (float)(texture3.getWidthOrig() / 2) * float12;
										float8 -= (float)texture3.getHeightOrig() * float13;
									}

									IsoObjectPicker.Instance.Add((int)float7, (int)float8, (int)((float)texture3.getWidthOrig() * float12), (int)((float)texture3.getHeightOrig() * float13), object.square, object, boolean2, float12, float13);
								}
							}
						}
					}
				}

				info.r = float9;
				info.g = float10;
				info.b = float11;
			} catch (Exception exception) {
				IndieGL.End();
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	public void renderBloodSplat(float float1, float float2, float float3, ColorInfo colorInfo) {
		if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
			boolean boolean1 = true;
			boolean boolean2 = true;
			byte byte1 = 0;
			byte byte2 = 0;
			try {
				if (globalOffsetX == -1) {
					globalOffsetX = -((int)IsoCamera.frameState.OffX);
					globalOffsetY = -((int)IsoCamera.frameState.OffY);
				}

				float float4 = IsoUtils.XToScreen(float1, float2, float3, 0);
				float float5 = IsoUtils.YToScreen(float1, float2, float3, 0);
				float4 = (float)((int)float4);
				float5 = (float)((int)float5);
				float4 -= (float)byte1;
				float5 -= (float)byte2;
				float4 += (float)globalOffsetX;
				float5 += (float)globalOffsetY;
				if (!(float4 >= (float)IsoCamera.frameState.OffscreenWidth) && !(float4 + 64.0F <= 0.0F)) {
					if (!(float5 >= (float)IsoCamera.frameState.OffscreenHeight) && !(float5 + 64.0F <= 0.0F)) {
						info.r = colorInfo.r;
						info.g = colorInfo.g;
						info.b = colorInfo.b;
						info.a = colorInfo.a;
						((IsoDirectionFrame)this.CurrentAnim.Frames.get(0)).render((float)((int)float4), (float)((int)float5), IsoDirections.N, info, false, this.Angle);
					}
				}
			} catch (Exception exception) {
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			}
		}
	}

	public void renderObjectPicker(IsoSpriteInstance spriteInstance, IsoObject object, float float1, float float2, float float3, IsoDirections directions, float float4, float float5, ColorInfo colorInfo) {
		if (this.CurrentAnim != null) {
			if (spriteInstance != null) {
				if (IsoPlayer.instance == IsoPlayer.players[0]) {
					if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
						if (this.CurrentAnim.Frames.size() != 0) {
							if (spriteInstance.Frame >= (float)this.CurrentAnim.Frames.size()) {
								spriteInstance.Frame = 0.0F;
							}

							if (((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions) != null) {
								float float6 = (float)(object.sx + globalOffsetX);
								float float7 = (float)(object.sy + globalOffsetY);
								if (object instanceof IsoMovingObject) {
									float6 -= (float)(((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions).getWidthOrig() / 2) * spriteInstance.getScaleX();
									float7 -= (float)((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions).getHeightOrig() * spriteInstance.getScaleY();
								}

								if (spriteInstance.Frame < (float)this.CurrentAnim.Frames.size() && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
									Texture texture = ((IsoDirectionFrame)this.CurrentAnim.Frames.get((int)spriteInstance.Frame)).getTexture(directions);
									if (object != null) {
										boolean boolean1 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
										if (spriteInstance.Flip) {
											boolean1 = !boolean1;
										}

										IsoObjectPicker.Instance.Add((int)float6, (int)float7, (int)((float)texture.getWidthOrig() * spriteInstance.getScaleX()), (int)((float)texture.getHeightOrig() * spriteInstance.getScaleY()), object.square, object, boolean1, spriteInstance.getScaleX(), spriteInstance.getScaleY());
									}
								}
							}
						}
					}
				}
			}
		}
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
				if (this.AnimateWhenPaused) {
					spriteInstance.Frame = (float)((double)spriteInstance.Frame + (double)spriteInstance.AnimFrameIncrease * UIManager.getSecondsSinceLastRender() * 60.0);
				} else if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() > 0) {
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

	public static boolean HasCache(String string) {
		return AnimNameSet.containsKey(string);
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

	public void setName(String string) {
		this.name = string;
	}

	public void setParentObjectName(String string) {
		this.parentObjectName = string;
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

	public IsoSpriteInstance getDefaultSpriteInst() {
		return this.def;
	}

	public String getName() {
		return this.name;
	}

	public void setTintMod(ColorInfo colorInfo) {
		this.TintMod = colorInfo;
	}

	public ColorInfo getTintMod() {
		return this.TintMod;
	}

	public void setAnimate(boolean boolean1) {
		this.Animate = boolean1;
	}

	public void setAnimateWhenPaused(boolean boolean1) {
		this.AnimateWhenPaused = boolean1;
	}

	public void setSpriteGrid(IsoSpriteGrid spriteGrid) {
		this.spriteGrid = spriteGrid;
	}

	public IsoSpriteGrid getSpriteGrid() {
		return this.spriteGrid;
	}

	public boolean isMoveWithWind() {
		return this.moveWithWind;
	}
}
