package zombie.core.opengl;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;


public final class CharacterModelCamera extends ModelCamera {
	public static final CharacterModelCamera instance = new CharacterModelCamera();

	public void Begin() {
		if (this.m_bUseWorldIso) {
			Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, this.m_useAngle, this.m_bInVehicle);
			GL11.glDepthMask(this.bDepthMask);
		} else {
			short short1 = 1024;
			short short2 = 1024;
			float float1 = 42.75F;
			float float2 = 0.0F;
			float float3 = -0.45F;
			float float4 = 0.0F;
			GL11.glMatrixMode(5889);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			float float5 = (float)short1 / (float)short2;
			boolean boolean1 = false;
			if (boolean1) {
				GL11.glOrtho((double)(-float1 * float5), (double)(float1 * float5), (double)float1, (double)(-float1), -100.0, 100.0);
			} else {
				GL11.glOrtho((double)(-float1 * float5), (double)(float1 * float5), (double)(-float1), (double)float1, -100.0, 100.0);
			}

			float float6 = Math.sqrt(2048.0F);
			GL11.glScalef(-float6, float6, float6);
			GL11.glMatrixMode(5888);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(float2, float3, float4);
			GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotated(Math.toDegrees((double)this.m_useAngle) + 45.0, 0.0, 1.0, 0.0);
			GL11.glDepthRange(0.0, 1.0);
			GL11.glDepthMask(this.bDepthMask);
		}
	}

	public void End() {
		if (this.m_bUseWorldIso) {
			Core.getInstance().DoPopIsoStuff();
		} else {
			GL11.glDepthFunc(519);
			GL11.glMatrixMode(5889);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5888);
			GL11.glPopMatrix();
		}
	}
}
