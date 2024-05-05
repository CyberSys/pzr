package zombie.core.textures;

import java.util.ArrayList;
import zombie.core.opengl.SmartShader;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;


public final class TextureCombinerCommand {
	public static final int DEFAULT_SRC_A = 1;
	public static final int DEFAULT_DST_A = 771;
	public int x = -1;
	public int y = -1;
	public int w = -1;
	public int h = -1;
	public Texture mask;
	public Texture tex;
	public int blendSrc;
	public int blendDest;
	public int blendSrcA;
	public int blendDestA;
	public SmartShader shader;
	public ArrayList shaderParams = null;
	public static final ObjectPool pool = new ObjectPool(TextureCombinerCommand::new);

	public String toString() {
		String string = System.lineSeparator();
		return "{" + string + "\tpos: " + this.x + "," + this.y + string + "\tsize: " + this.w + "," + this.h + string + "\tmask:" + this.mask + string + "\ttex:" + this.tex + string + "\tblendSrc:" + this.blendSrc + string + "\tblendDest:" + this.blendDest + string + "\tblendSrcA:" + this.blendSrcA + string + "\tblendDestA:" + this.blendDestA + string + "\tshader:" + this.shader + string + "\tshaderParams:" + PZArrayUtil.arrayToString((Iterable)this.shaderParams) + string + "}";
	}

	public TextureCombinerCommand init(Texture texture) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = 770;
		this.blendDest = 771;
		this.blendSrcA = 1;
		this.blendDestA = 771;
		return this;
	}

	public TextureCombinerCommand initSeparate(Texture texture, int int1, int int2, int int3, int int4) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = int1;
		this.blendDest = int2;
		this.blendSrcA = int3;
		this.blendDestA = int4;
		return this;
	}

	public TextureCombinerCommand init(Texture texture, int int1, int int2) {
		return this.initSeparate(texture, int1, int2, 1, 771);
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = 770;
		this.blendDest = 771;
		this.blendSrcA = 1;
		this.blendDestA = 771;
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, Texture texture2, int int1, int int2) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = int1;
		this.blendDest = int2;
		this.blendSrcA = 1;
		this.blendDestA = 771;
		this.mask = this.requireNonNull(texture2);
		return this;
	}

	public TextureCombinerCommand init(Texture texture, int int1, int int2, int int3, int int4) {
		this.tex = this.requireNonNull(texture);
		this.x = int1;
		this.y = int2;
		this.w = int3;
		this.h = int4;
		this.blendSrc = 770;
		this.blendDest = 771;
		this.blendSrcA = 1;
		this.blendDestA = 771;
		return this;
	}

	public TextureCombinerCommand initSeparate(Texture texture, SmartShader smartShader, ArrayList arrayList, Texture texture2, int int1, int int2, int int3, int int4) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = int1;
		this.blendDest = int2;
		this.blendSrcA = int3;
		this.blendDestA = int4;
		this.mask = this.requireNonNull(texture2);
		if (this.shaderParams == null) {
			this.shaderParams = new ArrayList();
		}

		this.shaderParams.clear();
		this.shaderParams.addAll(arrayList);
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, ArrayList arrayList, Texture texture2, int int1, int int2) {
		return this.initSeparate(texture, smartShader, arrayList, texture2, int1, int2, 1, 771);
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, ArrayList arrayList) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = 770;
		this.blendDest = 771;
		this.blendSrcA = 1;
		this.blendDestA = 771;
		this.shader = smartShader;
		if (this.shaderParams == null) {
			this.shaderParams = new ArrayList();
		}

		this.shaderParams.clear();
		this.shaderParams.addAll(arrayList);
		return this;
	}

	private Texture requireNonNull(Texture texture) {
		return texture == null ? Texture.getErrorTexture() : texture;
	}

	public static TextureCombinerCommand get() {
		TextureCombinerCommand textureCombinerCommand = (TextureCombinerCommand)pool.alloc();
		textureCombinerCommand.x = -1;
		textureCombinerCommand.tex = null;
		textureCombinerCommand.mask = null;
		textureCombinerCommand.shader = null;
		if (textureCombinerCommand.shaderParams != null) {
			textureCombinerCommand.shaderParams.clear();
		}

		return textureCombinerCommand;
	}
}
