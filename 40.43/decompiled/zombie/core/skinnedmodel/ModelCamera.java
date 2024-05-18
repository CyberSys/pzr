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

   public ModelCamera(int var1, int var2) {
      this._orbit = (float)Math.toRadians(90.0D);
   }

   public void End() {
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
   }

   public void Begin(Model var1) {
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      this._projection.clearToPerspective(0.7853982F, (float)this.w, (float)this.h, 0.1F, 1000.0F);
      GL11.glLoadMatrix(this._projection.getBuffer());
      this._orbit = 135.0F;
      this._position.z(1.5F);
      float var2 = (float)((double)this._distance * -Math.sin((double)this._orbit * 0.017453292519943295D) * Math.cos(-0.2617993877991494D));
      float var3 = (float)((double)this._distance * -Math.sin(-0.2617993877991494D));
      float var4 = (float)((double)(-this._distance) * Math.cos((double)this._orbit * 0.017453292519943295D) * Math.cos(-0.2617993877991494D));
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GLU.gluLookAt(this._target.x() + var2, this._target.y() + var3, this._target.z() + var4, this._target.x(), this._target.y(), this._target.z(), 0.0F, -1.0F, 0.0F);
   }

   public void BeginVehicle(Model var1, ModelInstance var2) {
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      float var3 = this.VehicleScaleHack;
      GL11.glOrtho((double)(-128.0F * var3), (double)(128.0F * var3), (double)(-128.0F * var3), (double)(128.0F * var3), -1000.0D, 1000.0D);
      float var4 = (float)Math.sqrt(2048.0D);
      GL11.glScalef(var4, var4, var4);
      if (BaseVehicle.LEMMY_FLIP_FIX) {
         GL11.glScalef(-1.0F, -1.0F, 1.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(BaseVehicle.LEMMY_FLIP_FIX ? 45.0F : -45.0F, 0.0F, 1.0F, 0.0F);
   }

   public void setDir(Vector2 var1, ModelInstance var2) {
      this.angle = (float)((double)var1.getDirection() + 3.141592653589793D);
      if ((double)this.angle > 6.283185307179586D) {
         this.angle = (float)((double)this.angle - 6.283185307179586D);
      }

      var2.AnimPlayer.targetAngle = (float)(6.283185307179586D - (double)this.angle);
   }
}
