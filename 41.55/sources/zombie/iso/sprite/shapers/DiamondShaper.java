package zombie.iso.sprite.shapers;

import java.util.function.Consumer;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;


public class DiamondShaper implements Consumer {
	public static final DiamondShaper instance = new DiamondShaper();

	public void accept(TextureDraw textureDraw) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.MeshCutdown.getValue()) {
			float float1 = textureDraw.x0;
			float float2 = textureDraw.y0;
			float float3 = textureDraw.x1;
			float float4 = textureDraw.y1;
			float float5 = textureDraw.y2;
			float float6 = textureDraw.y3;
			float float7 = float3 - float1;
			float float8 = float5 - float4;
			float float9 = float1 + float7 * 0.5F;
			float float10 = float4 + float8 * 0.5F;
			float float11 = textureDraw.u0;
			float float12 = textureDraw.v0;
			float float13 = textureDraw.u1;
			float float14 = textureDraw.v1;
			float float15 = textureDraw.v2;
			float float16 = textureDraw.v3;
			float float17 = float13 - float11;
			float float18 = float15 - float12;
			float float19 = float11 + float17 * 0.5F;
			float float20 = float14 + float18 * 0.5F;
			textureDraw.x0 = float9;
			textureDraw.y0 = float2;
			textureDraw.u0 = float19;
			textureDraw.v0 = float12;
			textureDraw.x1 = float3;
			textureDraw.y1 = float10;
			textureDraw.u1 = float13;
			textureDraw.v1 = float20;
			textureDraw.x2 = float9;
			textureDraw.y2 = float6;
			textureDraw.u2 = float19;
			textureDraw.v2 = float16;
			textureDraw.x3 = float1;
			textureDraw.y3 = float10;
			textureDraw.u3 = float11;
			textureDraw.v3 = float20;
		}
	}
}
