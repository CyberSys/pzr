package zombie.ui;

import zombie.iso.Vector2;

public class MovementBlender extends UIElement {
   public double sx = 0.0D;
   public double sy = 0.0D;
   public double Time = 0.0D;
   public double TimeMax = 0.0D;
   public double tx = 0.0D;
   public double ty = 0.0D;
   float lamount = -100.0F;

   public MovementBlender(UIElement var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.sx = this.x;
      this.sy = this.y;
      this.tx = this.x;
      this.ty = this.y;
      var1.x = 0.0D;
      var1.y = 0.0D;
      this.width = var1.width;
      this.height = var1.height;
      this.AddChild(var1);
   }

   public void MoveTo(float var1, float var2, float var3) {
      if (this.tx != (double)var1 || this.ty != (double)var2) {
         this.TimeMax = (double)(var3 * 30.0F);
         this.Time = 0.0D;
         this.sx = this.getX();
         this.sy = this.getY();
         this.tx = (double)var1;
         this.ty = (double)var2;
      }
   }

   public boolean Running() {
      double var1 = this.Time / this.TimeMax;
      return !(var1 > 1.0D);
   }

   public void update() {
      super.update();
      ++this.Time;
      double var1 = this.Time / this.TimeMax;
      if (var1 > 1.0D) {
         var1 = 1.0D;
      }

      if (var1 != 1.0D || this.lamount != 1.0F) {
         this.lamount = (float)var1;
         Vector2 var3 = new Vector2();
         Vector2 var4 = new Vector2();
         Vector2 var5 = new Vector2();
         var4.x = (float)this.sx;
         var4.y = (float)this.sy;
         var5.x = (float)this.tx;
         var5.y = (float)this.ty;
         var1 = var1 > 1.0D ? 1.0D : (var1 < 0.0D ? 0.0D : var1);
         var1 = var1 * var1 * (3.0D - 2.0D * var1);
         var3.x = (float)((double)var4.x + (double)(var5.x - var4.x) * var1);
         var3.y = (float)((double)var4.y + (double)(var5.y - var4.y) * var1);
         this.setX((double)var3.x);
         this.setY((double)var3.y);
      }
   }
}
