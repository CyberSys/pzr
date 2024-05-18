package zombie.ui;

import zombie.GameTime;
import zombie.core.textures.Texture;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoStove;

public class NewContainerPanel extends UIElement {
   public Texture BL;
   public Texture BM;
   public Texture BR;
   public InventoryFlowControl Flow;
   public Texture ML;
   public Texture MM;
   public Texture MR;
   public Texture TL;
   public Texture TM;
   public Texture TR;
   DialogButton button;
   IsoStove stove = null;

   public NewContainerPanel(int var1, int var2, int var3, int var4, ItemContainer var5) {
      this.x = (double)var1;
      this.y = (double)var2;
      this.Flow = new InventoryFlowControl(2, 8, var3, var4, var5);
      this.width = (float)(var3 * 32 + 4);
      this.height = (float)(var4 * 32 + 16);
      this.followGameWorld = true;
      this.TL = Texture.getSharedTexture("media/ui/Container_TL.png");
      this.TM = Texture.getSharedTexture("media/ui/Container_TM.png");
      this.TR = Texture.getSharedTexture("media/ui/Container_TR.png");
      this.ML = Texture.getSharedTexture("media/ui/Container_ML.png");
      this.MM = Texture.getSharedTexture("media/ui/Container_MM.png");
      this.MR = Texture.getSharedTexture("media/ui/Container_MR.png");
      this.BL = Texture.getSharedTexture("media/ui/Container_BL.png");
      this.BM = Texture.getSharedTexture("media/ui/Container_BM.png");
      this.BR = Texture.getSharedTexture("media/ui/Container_BR.png");
      this.AddChild(this.Flow);
   }

   public NewContainerPanel(int var1, int var2, int var3, int var4, IsoStove var5) {
      this.stove = var5;
      this.x = (double)var1;
      this.y = (double)var2;
      this.Flow = new InventoryFlowControl(2, 8, var3, var4, var5.container);
      this.width = (float)(var3 * 32 + 4);
      this.height = (float)(var4 * 32 + 16);
      this.followGameWorld = true;
      this.TL = Texture.getSharedTexture("media/ui/Container_TL.png");
      this.TM = Texture.getSharedTexture("media/ui/Container_TM.png");
      this.TR = Texture.getSharedTexture("media/ui/Container_TR.png");
      this.ML = Texture.getSharedTexture("media/ui/Container_ML.png");
      this.MM = Texture.getSharedTexture("media/ui/Container_MM.png");
      this.MR = Texture.getSharedTexture("media/ui/Container_MR.png");
      this.BL = Texture.getSharedTexture("media/ui/Container_BL.png");
      this.BM = Texture.getSharedTexture("media/ui/Container_BM.png");
      this.BR = Texture.getSharedTexture("media/ui/Container_BR.png");
      this.AddChild(this.Flow);
      boolean var6 = var5.Activated();
      if (var6) {
         this.button = new DialogButton(this, 21.0F, -5.0F, "On", "On");
         this.AddChild(this.button);
      } else {
         this.button = new DialogButton(this, 21.0F, -5.0F, "Off", "Off");
         this.AddChild(this.button);
      }

   }

   public void ButtonClicked(String var1) {
      if (GameTime.instance.NightsSurvived < 30) {
         if (var1.equals("On")) {
            this.button.name = "Off";
            this.stove.Toggle();
            this.button.text = "Off";
         }

         if (var1.equals("Off")) {
            this.button.name = "On";
            this.button.text = "On";
            this.stove.Toggle();
         }

      }
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         super.onMouseDown(var1, var3);
         IsoGridSquare.setRecalcLightTime(0);
         return Boolean.TRUE;
      } else {
         return Boolean.TRUE;
      }
   }

   public Boolean onMouseUp(double var1, double var3) {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         super.onMouseUp(var1, var3);
         IsoGridSquare.setRecalcLightTime(0);
         return Boolean.TRUE;
      } else {
         return Boolean.TRUE;
      }
   }

   public void render() {
      this.DrawTexture(this.TL, 0.0D, 0.0D, 0.800000011920929D);
      this.DrawTextureScaled(this.TM, 8.0D, 0.0D, this.getWidth() - 16.0D, 8.0D, 0.800000011920929D);
      this.DrawTexture(this.TR, this.getWidth() - 8.0D, 0.0D, 0.800000011920929D);
      this.DrawTextureScaled(this.ML, 0.0D, 8.0D, 8.0D, this.getHeight() - 16.0D, 0.800000011920929D);
      this.DrawTextureScaled(this.MM, 8.0D, 8.0D, this.getWidth() - 16.0D, this.getHeight() - 16.0D, 0.800000011920929D);
      this.DrawTextureScaled(this.MR, this.getWidth() - 8.0D, 8.0D, 8.0D, this.getHeight() - 16.0D, 0.800000011920929D);
      this.DrawTexture(this.BL, 0.0D, this.getHeight() - 8.0D, 0.800000011920929D);
      this.DrawTextureScaled(this.BM, 8.0D, this.getHeight() - 8.0D, this.getWidth() - 16.0D, 8.0D, 0.800000011920929D);
      this.DrawTexture(this.BR, this.getWidth() - 8.0D, this.getHeight() - 8.0D, 0.800000011920929D);
      super.render();
   }
}
