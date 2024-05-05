package zombie.core.textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.CharacterSmartTexture;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.SmartShader;
import zombie.core.skinnedmodel.model.CharacterMask;
import zombie.core.utils.WrappedBuffer;
import zombie.debug.DebugLog;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;


public class SmartTexture extends Texture {
	public final ArrayList commands = new ArrayList();
	public Texture result;
	private boolean dirty = true;
	private static SmartShader hue;
	private static SmartShader tint;
	private static SmartShader masked;
	private static SmartShader dirtMask;
	private final HashMap categoryMap = new HashMap();
	private static SmartShader bodyMask;
	private static SmartShader bodyMaskTint;
	private static SmartShader bodyMaskHue;
	private static final ArrayList bodyMaskParams = new ArrayList();
	private static SmartShader addHole;
	private static final ArrayList addHoleParams = new ArrayList();
	private static SmartShader removeHole;
	private static final ArrayList removeHoleParams = new ArrayList();
	private static SmartShader blit;

	public SmartTexture() {
		this.name = "SmartTexture";
	}

	void addToCat(int int1) {
		ArrayList arrayList = null;
		if (!this.categoryMap.containsKey(int1)) {
			arrayList = new ArrayList();
			this.categoryMap.put(int1, arrayList);
		} else {
			arrayList = (ArrayList)this.categoryMap.get(int1);
		}

		arrayList.add(this.commands.size());
	}

	public TextureCombinerCommand getFirstFromCategory(int int1) {
		return !this.categoryMap.containsKey(int1) ? null : (TextureCombinerCommand)this.commands.get((Integer)((ArrayList)this.categoryMap.get(int1)).get(0));
	}

	public void addOverlayPatches(String string, String string2, int int1) {
		if (blit == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		this.add((String)string, blit, (String)string2, arrayList, 770, 771);
	}

	public void addOverlay(String string, String string2, float float1, int int1) {
		if (masked == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("intensity", float1));
		arrayList.add(new TextureCombinerShaderParam("bloodDark", 0.5F, 0.5F));
		this.add((String)string, masked, (String)string2, arrayList, 774, 771);
	}

	public void addDirtOverlay(String string, String string2, float float1, int int1) {
		if (dirtMask == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("intensity", float1));
		this.add((String)string, dirtMask, (String)string2, arrayList, 774, 771);
	}

	public void addOverlay(String string) {
		if (tint == null) {
			this.create();
		}

		this.add((String)string, 774, 771);
	}

	public void addRect(String string, int int1, int int2, int int3, int int4) {
		this.commands.add(TextureCombinerCommand.get().init(Texture.getSharedTexture(string), int1, int2, int3, int4));
		this.dirty = true;
	}

	public void destroy() {
		if (this.result != null) {
			TextureCombiner.instance.releaseTexture(this.result);
		}

		this.clear();
		this.dirty = false;
	}

	public void addTint(String string, int int1, float float1, float float2, float float3) {
		this.addTint(Texture.getSharedTexture(string), int1, float1, float2, float3);
	}

	public void addTint(Texture texture, int int1, float float1, float float2, float float3) {
		if (tint == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("R", float1));
		arrayList.add(new TextureCombinerShaderParam("G", float2));
		arrayList.add(new TextureCombinerShaderParam("B", float3));
		this.add(texture, tint, arrayList);
	}

	public void addHue(String string, int int1, float float1) {
		this.addHue(Texture.getSharedTexture(string), int1, float1);
	}

	public void addHue(Texture texture, int int1, float float1) {
		if (hue == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("HueChange", float1));
		this.add(texture, hue, arrayList);
	}

	public Texture addHole(BloodBodyPartType bloodBodyPartType) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string = "media/textures/HoleTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		if (addHole == null) {
			this.create();
		}

		this.addToCat(CharacterSmartTexture.ClothingItemCategory);
		this.calculate();
		Texture texture = this.result;
		this.clear();
		this.result = null;
		this.commands.add(TextureCombinerCommand.get().init(texture, addHole, addHoleParams, Texture.getSharedTexture(string), 770, 0));
		this.dirty = true;
		return texture;
	}

	public void removeHole(String string, BloodBodyPartType bloodBodyPartType) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string2 = "media/textures/HoleTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		this.removeHole(Texture.getSharedTexture(string), Texture.getSharedTexture(string2), bloodBodyPartType);
	}

	public void removeHole(Texture texture, BloodBodyPartType bloodBodyPartType) {
		String[] stringArray = CharacterSmartTexture.MaskFiles;
		String string = "media/textures/HoleTextures/" + stringArray[bloodBodyPartType.index()] + ".png";
		this.removeHole(texture, Texture.getSharedTexture(string), bloodBodyPartType);
	}

	public void removeHole(Texture texture, Texture texture2, BloodBodyPartType bloodBodyPartType) {
		if (removeHole == null) {
			this.create();
		}

		this.addToCat(CharacterSmartTexture.ClothingItemCategory);
		this.commands.add(TextureCombinerCommand.get().init(texture, removeHole, removeHoleParams, texture2, 770, 771));
		this.dirty = true;
	}

	public void mask(String string, String string2, int int1) {
		this.mask(Texture.getSharedTexture(string), Texture.getSharedTexture(string2), int1);
	}

	public void mask(Texture texture, Texture texture2, int int1) {
		if (bodyMask == null) {
			this.create();
		}

		this.addToCat(int1);
		this.commands.add(TextureCombinerCommand.get().init(texture, bodyMask, bodyMaskParams, texture2, 770, 771));
		this.dirty = true;
	}

	public void maskHue(String string, String string2, int int1, float float1) {
		this.maskHue(Texture.getSharedTexture(string), Texture.getSharedTexture(string2), int1, float1);
	}

	public void maskHue(Texture texture, Texture texture2, int int1, float float1) {
		if (bodyMask == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("HueChange", float1));
		this.commands.add(TextureCombinerCommand.get().init(texture, bodyMaskHue, arrayList, texture2, 770, 771));
		this.dirty = true;
	}

	public void maskTint(String string, String string2, int int1, float float1, float float2, float float3) {
		this.maskTint(Texture.getSharedTexture(string), Texture.getSharedTexture(string2), int1, float1, float2, float3);
	}

	public void maskTint(Texture texture, Texture texture2, int int1, float float1, float float2, float float3) {
		if (bodyMask == null) {
			this.create();
		}

		this.addToCat(int1);
		ArrayList arrayList = new ArrayList();
		arrayList.add(new TextureCombinerShaderParam("R", float1));
		arrayList.add(new TextureCombinerShaderParam("G", float2));
		arrayList.add(new TextureCombinerShaderParam("B", float3));
		this.commands.add(TextureCombinerCommand.get().init(texture, bodyMaskTint, arrayList, texture2, 770, 771));
		this.dirty = true;
	}

	public void addMaskedTexture(CharacterMask characterMask, String string, String string2, int int1, ImmutableColor immutableColor, float float1) {
		addMaskedTexture(this, characterMask, string, Texture.getSharedTexture(string2), int1, immutableColor, float1);
	}

	public void addMaskedTexture(CharacterMask characterMask, String string, Texture texture, int int1, ImmutableColor immutableColor, float float1) {
		addMaskedTexture(this, characterMask, string, texture, int1, immutableColor, float1);
	}

	private static void addMaskFlags(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int1) {
		Consumer consumer = Lambda.consumer(smartTexture, string, texture, int1, (smartTexturex,characterMaskx,stringx,texturex,int1x)->{
    characterMaskx.mask(texturex, Texture.getSharedTexture(stringx + "/" + smartTexturex + ".png"), int1x);
});
		characterMask.forEachVisible(consumer);
	}

	private static void addMaskFlagsHue(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int1, float float1) {
		Consumer consumer = Lambda.consumer(smartTexture, string, texture, int1, float1, (smartTexturex,characterMaskx,stringx,texturex,int1x,float1x)->{
    characterMaskx.maskHue(texturex, Texture.getSharedTexture(stringx + "/" + smartTexturex + ".png"), int1x, float1x);
});
		characterMask.forEachVisible(consumer);
	}

	private static void addMaskFlagsTint(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int1, ImmutableColor immutableColor) {
		Consumer consumer = Lambda.consumer(smartTexture, string, texture, int1, immutableColor, (smartTexturex,characterMaskx,stringx,texturex,int1x,immutableColorx)->{
    characterMaskx.maskTint(texturex, Texture.getSharedTexture(stringx + "/" + smartTexturex + ".png"), int1x, immutableColorx.r, immutableColorx.g, immutableColorx.b);
});
		characterMask.forEachVisible(consumer);
	}

	private static void addMaskedTexture(SmartTexture smartTexture, CharacterMask characterMask, String string, Texture texture, int int1, ImmutableColor immutableColor, float float1) {
		if (!characterMask.isNothingVisible()) {
			if (characterMask.isAllVisible()) {
				if (!ImmutableColor.white.equals(immutableColor)) {
					smartTexture.addTint(texture, int1, immutableColor.r, immutableColor.g, immutableColor.b);
				} else if (!(float1 < -1.0E-4F) && !(float1 > 1.0E-4F)) {
					smartTexture.add(texture);
				} else {
					smartTexture.addHue(texture, int1, float1);
				}
			} else {
				if (!ImmutableColor.white.equals(immutableColor)) {
					addMaskFlagsTint(smartTexture, characterMask, string, texture, int1, immutableColor);
				} else if (!(float1 < -1.0E-4F) && !(float1 > 1.0E-4F)) {
					addMaskFlags(smartTexture, characterMask, string, texture, int1);
				} else {
					addMaskFlagsHue(smartTexture, characterMask, string, texture, int1, float1);
				}
			}
		}
	}

	public void addTexture(String string, int int1, ImmutableColor immutableColor, float float1) {
		addTexture(this, string, int1, immutableColor, float1);
	}

	private static void addTexture(SmartTexture smartTexture, String string, int int1, ImmutableColor immutableColor, float float1) {
		if (!ImmutableColor.white.equals(immutableColor)) {
			smartTexture.addTint(string, int1, immutableColor.r, immutableColor.g, immutableColor.b);
		} else if (!(float1 < -1.0E-4F) && !(float1 > 1.0E-4F)) {
			smartTexture.add(string);
		} else {
			smartTexture.addHue(string, int1, float1);
		}
	}

	private void create() {
		tint = new SmartShader("hueChange");
		hue = new SmartShader("hueChange");
		masked = new SmartShader("overlayMask");
		dirtMask = new SmartShader("dirtMask");
		bodyMask = new SmartShader("bodyMask");
		bodyMaskHue = new SmartShader("bodyMaskHue");
		bodyMaskTint = new SmartShader("bodyMaskTint");
		addHole = new SmartShader("addHole");
		removeHole = new SmartShader("removeHole");
		blit = new SmartShader("blit");
	}

	public WrappedBuffer getData() {
		synchronized (this) {
			if (this.dirty) {
				this.calculate();
			}

			return this.result.dataid.getData();
		}
	}

	public synchronized void bind() {
		if (this.dirty) {
			this.calculate();
		}

		this.result.bind(3553);
	}

	public int getID() {
		synchronized (this) {
			if (this.dirty) {
				this.calculate();
			}
		}
		return this.result.dataid.id;
	}

	public void calculate() {
		synchronized (this) {
			if (Core.bDebug) {
				GL11.glGetError();
			}

			try {
				this.result = TextureCombiner.instance.combine(this.commands);
			} catch (Exception exception) {
				DebugLog.General.error(exception.getClass().getSimpleName() + " encountered while combining texture.");
				DebugLog.General.error("Intended width : " + TextureCombiner.getResultingWidth(this.commands));
				DebugLog.General.error("Intended height: " + TextureCombiner.getResultingHeight(this.commands));
				DebugLog.General.error("");
				DebugLog.General.error("Commands list: " + PZArrayUtil.arrayToString((Iterable)this.commands));
				DebugLog.General.error("");
				DebugLog.General.error("Stack trace: ");
				ExceptionLogger.logException(exception);
				DebugLog.General.error("This SmartTexture will no longer be valid.");
				this.width = -1;
				this.height = -1;
				this.dirty = false;
				return;
			}

			this.width = this.result.width;
			this.height = this.result.height;
			this.dirty = false;
		}
	}

	public void clear() {
		TextureCombinerCommand.pool.release((List)this.commands);
		this.commands.clear();
		this.categoryMap.clear();
		this.dirty = false;
	}

	public void add(String string) {
		this.add(Texture.getSharedTexture(string));
	}

	public void add(Texture texture) {
		if (blit == null) {
			this.create();
		}

		this.commands.add(TextureCombinerCommand.get().init(texture, blit));
		this.dirty = true;
	}

	public void add(String string, SmartShader smartShader, ArrayList arrayList) {
		this.add(Texture.getSharedTexture(string), smartShader, arrayList);
	}

	public void add(Texture texture, SmartShader smartShader, ArrayList arrayList) {
		this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, arrayList));
		this.dirty = true;
	}

	public void add(String string, SmartShader smartShader, String string2, int int1, int int2) {
		this.add(Texture.getSharedTexture(string), smartShader, Texture.getSharedTexture(string2), int1, int2);
	}

	public void add(Texture texture, SmartShader smartShader, Texture texture2, int int1, int int2) {
		this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, texture2, int1, int2));
		this.dirty = true;
	}

	public void add(String string, int int1, int int2) {
		this.add(Texture.getSharedTexture(string), int1, int2);
	}

	public void add(Texture texture, int int1, int int2) {
		this.commands.add(TextureCombinerCommand.get().init(texture, int1, int2));
		this.dirty = true;
	}

	public void add(String string, SmartShader smartShader, String string2, ArrayList arrayList, int int1, int int2) {
		this.add(Texture.getSharedTexture(string), smartShader, Texture.getSharedTexture(string2), arrayList, int1, int2);
	}

	public void add(Texture texture, SmartShader smartShader, Texture texture2, ArrayList arrayList, int int1, int int2) {
		this.commands.add(TextureCombinerCommand.get().init(texture, smartShader, arrayList, texture2, int1, int2));
		this.dirty = true;
	}

	public void save(String string) {
		if (this.dirty) {
			this.calculate();
		}

		this.result.save(string);
	}

	protected void setDirty() {
		this.dirty = true;
	}

	public boolean isEmpty() {
		return this.result == null ? true : this.result.isEmpty();
	}

	public boolean isFailure() {
		return this.result == null ? false : this.result.isFailure();
	}

	public boolean isReady() {
		return this.result == null ? false : this.result.isReady();
	}
}
