package zombie.core.textures;

import java.util.ArrayList;
import zombie.core.opengl.SmartShader;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;


public final class TextureCombinerCommand {
	public int x = -1;
	public int y = -1;
	public int w = -1;
	public int h = -1;
	public Texture mask;
	public Texture tex;
	public int blendSrc;
	public int blendDest;
	public SmartShader shader;
	public ArrayList shaderParams = null;
	public static final ObjectPool pool = new ObjectPool(TextureCombinerCommand::new);

	public String toString() {
		String string = System.lineSeparator();
		return "{" + string + "\tpos: " + this.x + "," + this.y + string + "\tsize: " + this.w + "," + this.h + string + "\tmask:" + this.mask + string + "\ttex:" + this.tex + string + "\tblendSrc:" + this.blendSrc + string + "\tblendDest:" + this.blendDest + string + "\tshader:" + this.shader + string + "\tshaderParams:" + PZArrayUtil.arrayToString((Iterable)this.shaderParams) + string + "}";
	}

	public TextureCombinerCommand init(Texture texture) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = 770;
		this.blendDest = 771;
		return this;
	}

	public TextureCombinerCommand init(Texture texture, int int1, int int2) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = int1;
		this.blendDest = int2;
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = 770;
		this.blendDest = 771;
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, Texture texture2, int int1, int int2) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = int1;
		this.blendDest = int2;
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
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, ArrayList arrayList, Texture texture2, int int1, int int2) {
		this.tex = this.requireNonNull(texture);
		this.shader = smartShader;
		this.blendSrc = int1;
		this.blendDest = int2;
		this.mask = this.requireNonNull(texture2);
		if (this.shaderParams == null) {
			this.shaderParams = new ArrayList();
		}

		this.shaderParams.clear();
		this.shaderParams.addAll(arrayList);
		return this;
	}

	public TextureCombinerCommand init(Texture texture, SmartShader smartShader, ArrayList arrayList) {
		this.tex = this.requireNonNull(texture);
		this.blendSrc = 770;
		this.blendDest = 771;
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
