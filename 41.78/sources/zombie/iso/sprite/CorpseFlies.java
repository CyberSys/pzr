package zombie.iso.sprite;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;


public final class CorpseFlies {
	private static Texture TEXTURE;
	private static final int FRAME_WIDTH = 128;
	private static final int FRAME_HEIGHT = 128;
	private static final int COLUMNS = 8;
	private static final int ROWS = 7;
	private static final int NUM_FRAMES = 56;
	private static float COUNTER = 0.0F;
	private static int FRAME = 0;

	public static void render(int int1, int int2, int int3) {
		if (TEXTURE == null) {
			TEXTURE = Texture.getSharedTexture("media/textures/CorpseFlies.png");
		}

		if (TEXTURE != null && TEXTURE.isReady()) {
			int int4 = (FRAME + int1 + int2) % 56;
			int int5 = int4 % 8;
			int int6 = int4 / 8;
			float float1 = (float)(int5 * 128) / (float)TEXTURE.getWidth();
			float float2 = (float)(int6 * 128) / (float)TEXTURE.getHeight();
			float float3 = (float)((int5 + 1) * 128) / (float)TEXTURE.getWidth();
			float float4 = (float)((int6 + 1) * 128) / (float)TEXTURE.getHeight();
			float float5 = IsoUtils.XToScreen((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 0) + IsoSprite.globalOffsetX;
			float float6 = IsoUtils.YToScreen((float)int1 + 0.5F, (float)int2 + 0.5F, (float)int3, 0) + IsoSprite.globalOffsetY;
			byte byte1 = 64;
			int int7 = byte1 * Core.TileScale;
			float5 -= (float)(int7 / 2);
			float6 -= (float)(int7 + 16 * Core.TileScale);
			if (Core.bDebug) {
			}

			SpriteRenderer.instance.render(TEXTURE, float5, float6, (float)int7, (float)int7, 1.0F, 1.0F, 1.0F, 1.0F, float1, float2, float3, float2, float3, float4, float1, float4);
		}
	}

	public static void update() {
		if (!GameServer.bServer) {
			COUNTER += GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 1000.0F;
			float float1 = 20.0F;
			if (COUNTER > 1000.0F / float1) {
				COUNTER %= 1000.0F / float1;
				++FRAME;
				FRAME %= 56;
			}
		}
	}

	public static void Reset() {
		TEXTURE = null;
	}
}
