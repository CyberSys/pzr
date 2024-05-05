package zombie.core.particle;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import zombie.core.opengl.PZGLUtil;
import zombie.core.textures.Texture;


public class MuzzleFlash {
	private static Texture muzzleFlashStar;
	private static Texture muzzleFlashSide;

	public static void init() {
		muzzleFlashStar = Texture.getSharedTexture("media/textures/muzzle-flash-star.png");
		muzzleFlashSide = Texture.getSharedTexture("media/textures/muzzle-flash-side.png");
	}

	public static void render(Matrix4f matrix4f) {
		if (muzzleFlashStar != null && muzzleFlashStar.isReady()) {
			if (muzzleFlashSide != null && muzzleFlashSide.isReady()) {
				PZGLUtil.pushAndMultMatrix(5888, matrix4f);
				GL11.glDisable(2884);
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				muzzleFlashStar.bind();
				float float1 = 0.15F;
				GL11.glBegin(7);
				GL11.glTexCoord2f(0.0F, 1.0F);
				GL11.glVertex3f(-float1 / 2.0F, float1 / 2.0F, 0.0F);
				GL11.glTexCoord2f(1.0F, 1.0F);
				GL11.glVertex3f(float1 / 2.0F, float1 / 2.0F, 0.0F);
				GL11.glTexCoord2f(1.0F, 0.0F);
				GL11.glVertex3f(float1 / 2.0F, -float1 / 2.0F, 0.0F);
				GL11.glTexCoord2f(0.0F, 0.0F);
				GL11.glVertex3f(-float1 / 2.0F, -float1 / 2.0F, 0.0F);
				GL11.glEnd();
				muzzleFlashSide.bind();
				float1 = 0.05F;
				GL11.glBegin(7);
				GL11.glTexCoord2f(0.0F, 1.0F);
				GL11.glVertex3f(0.0F, float1 / 2.0F, 0.0F);
				GL11.glTexCoord2f(1.0F, 1.0F);
				GL11.glVertex3f(0.0F, float1 / 2.0F, float1 * 2.0F);
				GL11.glTexCoord2f(1.0F, 0.0F);
				GL11.glVertex3f(0.0F, -float1 / 2.0F, float1 * 2.0F);
				GL11.glTexCoord2f(0.0F, 0.0F);
				GL11.glVertex3f(0.0F, -float1 / 2.0F, 0.0F);
				GL11.glEnd();
				GL11.glEnable(2884);
				PZGLUtil.popMatrix(5888);
			}
		}
	}
}
