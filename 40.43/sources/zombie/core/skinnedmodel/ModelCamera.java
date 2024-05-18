package zombie.core.skinnedmodel;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.iso.Vector2;
import zombie.vehicles.BaseVehicle;


public class ModelCamera {
	public static ModelCamera instance = new ModelCamera(1920, 1080);
	protected Matrix4 _projection = new Matrix4();
	protected Matrix4 _modelview;
	protected Vector3 _position = new Vector3(0.75F, 0.5F, -1.5F);
	protected Vector3 _target = new Vector3(0.0F, 0.5F, 0.0F);
	int w = 1920;
	int h = 1080;
	float _orbit = 0.0F;
	float _distance = 2.4F;
	private DoubleBuffer clipBuffer = BufferUtils.createDoubleBuffer(4);
	public float VehicleScaleHack = 1.125F;
	float angle = 0.0F;

	public ModelCamera(int int1, int int2) {
		this._orbit = (float)Math.toRadians(90.0);
	}

	public void End() {
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}

	public void Begin(Model model) {
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		this._projection.clearToPerspective(0.7853982F, (float)this.w, (float)this.h, 0.1F, 1000.0F);
		GL11.glLoadMatrix(this._projection.getBuffer());
		this._orbit = 135.0F;
		this._position.z(1.5F);
		float float1 = (float)((double)this._distance * -Math.sin((double)this._orbit * 0.017453292519943295) * Math.cos(-0.2617993877991494));
		float float2 = (float)((double)this._distance * -Math.sin(-0.2617993877991494));
		float float3 = (float)((double)(-this._distance) * Math.cos((double)this._orbit * 0.017453292519943295) * Math.cos(-0.2617993877991494));
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluLookAt(this._target.x() + float1, this._target.y() + float2, this._target.z() + float3, this._target.x(), this._target.y(), this._target.z(), 0.0F, -1.0F, 0.0F);
	}

	public void BeginVehicle(Model model, ModelInstance modelInstance) {
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		float float1 = this.VehicleScaleHack;
		GL11.glOrtho((double)(-128.0F * float1), (double)(128.0F * float1), (double)(-128.0F * float1), (double)(128.0F * float1), -1000.0, 1000.0);
		float float2 = (float)Math.sqrt(2048.0);
		GL11.glScalef(float2, float2, float2);
		if (BaseVehicle.LEMMY_FLIP_FIX) {
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
		}

		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(BaseVehicle.LEMMY_FLIP_FIX ? 45.0F : -45.0F, 0.0F, 1.0F, 0.0F);
	}

	public void setDir(Vector2 vector2, ModelInstance modelInstance) {
		this.angle = (float)((double)vector2.getDirection() + 3.141592653589793);
		if ((double)this.angle > 6.283185307179586) {
			this.angle = (float)((double)this.angle - 6.283185307179586);
		}

		modelInstance.AnimPlayer.targetAngle = (float)(6.283185307179586 - (double)this.angle);
	}
}
