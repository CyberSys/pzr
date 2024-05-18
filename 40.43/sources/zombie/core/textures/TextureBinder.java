package zombie.core.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;


public class TextureBinder {
	public static TextureBinder instance = new TextureBinder();
	public int maxTextureUnits = 0;
	public int[] textureUnitIDs;
	public int textureUnitIDStart = 33984;
	public int textureIndex = 0;
	public int activeTextureIndex = 0;

	public TextureBinder() {
		this.maxTextureUnits = 1;
		this.textureUnitIDs = new int[this.maxTextureUnits];
		for (int int1 = 0; int1 < this.maxTextureUnits; ++int1) {
			this.textureUnitIDs[int1] = -1;
		}
	}

	public void bind(int int1) {
		for (int int2 = 0; int2 < this.maxTextureUnits; ++int2) {
			if (this.textureUnitIDs[int2] == int1) {
				int int3 = int2 + this.textureUnitIDStart;
				GL13.glActiveTexture(int3);
				this.activeTextureIndex = int3;
				return;
			}
		}

		this.textureUnitIDs[this.textureIndex] = int1;
		GL13.glActiveTexture(this.textureUnitIDStart + this.textureIndex);
		GL11.glBindTexture(3553, int1);
		this.activeTextureIndex = this.textureUnitIDStart + this.textureIndex;
		++this.textureIndex;
		if (this.textureIndex >= this.maxTextureUnits) {
			this.textureIndex = 0;
		}
	}
}
