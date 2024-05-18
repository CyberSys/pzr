package zombie.ui;

import java.util.Iterator;
import zombie.core.Color;
import zombie.core.textures.Texture;

public class HelpIcon extends UIElement {
   public static boolean doOthers = true;
   public boolean Closed = true;
   public UIElement follow;
   boolean clicked = false;
   boolean mouseOver = false;
   int origX;
   int origY;
   Texture tex = Texture.getSharedTexture("media/ui/Question_Off.png");
   Texture tex2 = Texture.getSharedTexture("media/ui/Question_On.png");
   TextBox text;
   String title;
   NewWindow window;

   public HelpIcon(int var1, int var2, String var3, String var4) {
      this.x = (double)var1;
      this.y = (double)var2;
      this.origX = var1;
      this.origY = var2;
      this.title = var3;
      this.followGameWorld = true;
      this.text = new TextBox(UIFont.Small, 0, 0, 180, var4);
      this.window = new NewWindow(0, 0, 200, 50, false);
      this.window.Movable = false;
      NewWindow var10000 = this.window;
      var10000.x += 0.0D;
      var10000 = this.window;
      var10000.y -= 0.0D;
      this.text.ResizeParent = true;
      this.window.Nest(this.text, 20, 10, 20, 10);
      this.window.Parent = this;
      this.window.ResizeToFitY = true;
      this.width = 16.0F;
      this.height = 16.0F;
      this.window.AddChild(new DialogButton(this, (float)(this.window.getWidth().intValue() - 30), (float)(this.window.getHeight().intValue() - 18), "Ok", "Ok"));
      this.window.AddChild(new DialogButton(this, 10.0F, (float)(this.window.getHeight().intValue() - 18), "No more", "No more"));
   }

   public void ButtonClicked(String var1) {
      if (var1.equals("Ok")) {
         this.setVisible(false);
         this.window.setVisible(false);
      }

      if (var1.equals("No more")) {
         this.setVisible(false);
         this.window.setVisible(false);
         doOthers = false;

         for(int var2 = 0; var2 < UIManager.getUI().size(); ++var2) {
            if (UIManager.getUI().get(var2) instanceof HelpIcon) {
               UIManager.getUI().remove(var2);
               --var2;
            }
         }
      }

   }

   public Boolean onMouseDown(double var1, double var3) {
      if (!this.isVisible()) {
         return Boolean.FALSE;
      } else if (!this.Closed) {
         return this.window.onMouseDown((double)((int)(var1 - this.window.getX())), (double)((int)(var3 - this.window.getY())));
      } else {
         this.clicked = true;
         return Boolean.FALSE;
      }
   }

   public Boolean onMouseMove(double var1, double var3) {
      if (!this.isVisible()) {
         return Boolean.FALSE;
      } else {
         this.mouseOver = true;
         return Boolean.FALSE;
      }
   }

   public void onMouseMoveOutside(double var1, double var3) {
      if (this.isVisible()) {
         this.clicked = false;
         this.mouseOver = false;
      }
   }

   public Boolean onMouseUp(double var1, double var3) {
      if (!this.isVisible()) {
         return Boolean.FALSE;
      } else if (!this.Closed) {
         return this.window.onMouseUp((double)((int)(var1 - this.window.getX())), (double)((int)(var3 - this.window.getY())));
      } else {
         if (this.clicked) {
            this.Closed = false;
            Iterator var5 = UIManager.getUI().iterator();

            while(var5.hasNext()) {
               UIElement var6 = (UIElement)var5.next();
               if (var6 instanceof HelpIcon && var6 != this) {
                  ((HelpIcon)var6).Closed = true;
               }
            }
         }

         this.clicked = false;
         return Boolean.FALSE;
      }
   }

   public void render() {
      if (this.isVisible()) {
         if (this.mouseOver) {
            this.DrawTextureCol(this.tex2, (double)(this.getX().intValue() - 8), (double)(this.getY().intValue() - 8), new Color(1.0F, 1.0F, 1.0F, 1.0F));
         } else {
            this.DrawTextureCol(this.tex, (double)(this.getX().intValue() - 8), (double)(this.getY().intValue() - 8), new Color(1.0F, 1.0F, 1.0F, 1.0F));
         }

         if (!this.Closed) {
            this.window.render();
         }

         super.render();
      }
   }

   public void update() {
      if (this.isVisible()) {
         super.update();
         if (this.follow != null) {
            this.setX(this.follow.getAbsoluteX() + (double)this.origX);
            this.setY(this.follow.getAbsoluteY() + (double)this.origY);
            this.window.setX(0.0D);
            this.window.setY(0.0D);
         }

         if (!this.Closed) {
            this.setWidth(this.window.getWidth() + 80.0D);
            this.setHeight(this.window.getHeight());
         }

         if (!this.Closed) {
            ((UIElement)this.window.getControls().get(1)).setX(this.window.getWidth() - 50.0D);
            ((UIElement)this.window.getControls().get(1)).setY(this.window.getHeight() - 18.0D);
            ((UIElement)this.window.getControls().get(2)).setX(10.0D);
            ((UIElement)this.window.getControls().get(2)).setY(this.window.getHeight() - 18.0D);
            this.window.update();
         }

      }
   }
}
