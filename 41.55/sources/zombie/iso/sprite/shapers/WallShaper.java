package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.Color;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class WallShaper implements Consumer {
	public final int[] col = new int[4];
	protected int colTint = 0;

	public void setTintColor(int int1) {
		this.colTint = int1;
	}

	public void accept(TextureDraw textureDraw) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Lighting.getValue()) {
			textureDraw.col0 = Color.blendBGR(textureDraw.col0, this.col[0]);
			textureDraw.col1 = Color.blendBGR(textureDraw.col1, this.col[1]);
			textureDraw.col2 = Color.blendBGR(textureDraw.col2, this.col[2]);
			textureDraw.col3 = Color.blendBGR(textureDraw.col3, this.col[3]);
		}

		if (this.colTint != 0) {
			textureDraw.col0 = Color.tintABGR(textureDraw.col0, this.colTint);
			textureDraw.col1 = Color.tintABGR(textureDraw.col1, this.colTint);
			textureDraw.col2 = Color.tintABGR(textureDraw.col2, this.colTint);
			textureDraw.col3 = Color.tintABGR(textureDraw.col3, this.colTint);
		}
	}
}
