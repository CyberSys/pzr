package zombie.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.input.Mouse;

public class ScrollBar extends UIElement {
   public Color BackgroundColour = new Color(255, 255, 255, 255);
   public Color ButtonColour = new Color(255, 255, 255, 127);
   public Color ButtonHighlightColour = new Color(255, 255, 255, 255);
   public boolean IsVerticle = true;
   private int FullLength = 114;
   private int InsideLength = 100;
   private int EndLength = 7;
   private float ButtonInsideLength = 30.0F;
   private int ButtonEndLength = 6;
   private int Thickness = 10;
   private int ButtonThickness = 9;
   private float ButtonOffset = 40.0F;
   private int MouseDragStartPos = 0;
   private float ButtonDragStartPos = 0.0F;
   private Texture BackVertical;
   private Texture TopVertical;
   private Texture BottomVertical;
   private Texture ButtonBackVertical;
   private Texture ButtonTopVertical;
   private Texture ButtonBottomVertical;
   private Texture BackHorizontal;
   private Texture LeftHorizontal;
   private Texture RightHorizontal;
   private Texture ButtonBackHorizontal;
   private Texture ButtonLeftHorizontal;
   private Texture ButtonRightHorizontal;
   private boolean mouseOver = false;
   private boolean BeingDragged = false;
   private ListBox ParentListBox = null;
   private UITextBox2 ParentTextBox = null;
   UIEventHandler messageParent;
   private String name;

   public ScrollBar(String var1, UIEventHandler var2, int var3, int var4, int var5, boolean var6) {
      this.messageParent = var2;
      this.name = var1;
      this.x = (double)((float)var3);
      this.y = (double)((float)var4);
      this.FullLength = var5;
      this.InsideLength = var5 - this.EndLength * 2;
      this.IsVerticle = true;
      this.width = (float)this.Thickness;
      this.height = (float)var5;
      this.ButtonInsideLength = this.height - (float)(this.ButtonEndLength * 2);
      this.ButtonOffset = 0.0F;
      this.BackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Middle.png");
      this.TopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Top.png");
      this.BottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bkg_Bottom.png");
      this.ButtonBackVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Middle.png");
      this.ButtonTopVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Top.png");
      this.ButtonBottomVertical = Texture.getSharedTexture("media/ui/ScrollbarV_Bottom.png");
      this.BackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Middle.png");
      this.LeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Bottom.png");
      this.RightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bkg_Top.png");
      this.ButtonBackHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Middle.png");
      this.ButtonLeftHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Bottom.png");
      this.ButtonRightHorizontal = Texture.getSharedTexture("media/ui/ScrollbarH_Top.png");
   }

   public void SetParentListBox(ListBox var1) {
      this.ParentListBox = var1;
   }

   public void SetParentTextBox(UITextBox2 var1) {
      this.ParentTextBox = var1;
   }

   public void setHeight(double var1) {
      super.setHeight(var1);
      this.FullLength = (int)var1;
      this.InsideLength = (int)var1 - this.EndLength * 2;
   }

   public void render() {
      if (this.IsVerticle) {
         this.DrawTextureScaledCol(this.TopVertical, 0.0D, 0.0D, (double)this.Thickness, (double)this.EndLength, this.BackgroundColour);
         this.DrawTextureScaledCol(this.BackVertical, 0.0D, (double)(0 + this.EndLength), (double)this.Thickness, (double)this.InsideLength, this.BackgroundColour);
         this.DrawTextureScaledCol(this.BottomVertical, 0.0D, (double)(0 + this.EndLength + this.InsideLength), (double)this.Thickness, (double)this.EndLength, this.BackgroundColour);
         Color var1;
         if (this.mouseOver) {
            var1 = this.ButtonHighlightColour;
         } else {
            var1 = this.ButtonColour;
         }

         this.DrawTextureScaledCol(this.ButtonTopVertical, 1.0D, (double)((int)this.ButtonOffset + 1), (double)this.ButtonThickness, (double)this.ButtonEndLength, var1);
         this.DrawTextureScaledCol(this.ButtonBackVertical, 1.0D, (double)((int)this.ButtonOffset + 1 + this.ButtonEndLength), (double)this.ButtonThickness, (double)this.ButtonInsideLength, var1);
         this.DrawTextureScaledCol(this.ButtonBottomVertical, 1.0D, (double)((float)((int)this.ButtonOffset + 1 + this.ButtonEndLength) + this.ButtonInsideLength), (double)this.ButtonThickness, (double)this.ButtonEndLength, var1);
      }

   }

   public Boolean onMouseMove(double var1, double var3) {
      this.mouseOver = true;
      return Boolean.TRUE;
   }

   public void onMouseMoveOutside(double var1, double var3) {
      this.mouseOver = false;
   }

   public Boolean onMouseUp(double var1, double var3) {
      this.BeingDragged = false;
      return Boolean.FALSE;
   }

   public Boolean onMouseDown(double var1, double var3) {
      boolean var5 = false;
      if (var3 >= (double)this.ButtonOffset && var3 <= (double)(this.ButtonOffset + this.ButtonInsideLength + (float)(this.ButtonEndLength * 2))) {
         var5 = true;
      }

      if (var5) {
         this.BeingDragged = true;
         this.MouseDragStartPos = Mouse.getY();
         this.ButtonDragStartPos = this.ButtonOffset;
      } else {
         this.ButtonOffset = (float)(var3 - (double)((this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) / 2.0F));
      }

      if (this.ButtonOffset < 0.0F) {
         this.ButtonOffset = 0.0F;
      }

      if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F) {
         this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
      }

      return Boolean.FALSE;
   }

   public void update() {
      super.update();
      int var1;
      if (this.BeingDragged) {
         var1 = this.MouseDragStartPos - Mouse.getY();
         this.ButtonOffset = this.ButtonDragStartPos - (float)var1;
         if (this.ButtonOffset < 0.0F) {
            this.ButtonOffset = 0.0F;
         }

         if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F) {
            this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
         }

         if (!Mouse.isButtonDown(0)) {
            this.BeingDragged = false;
         }
      }

      float var2;
      if (this.ParentListBox != null) {
         if ((double)(this.ParentListBox.Items.size() * this.ParentListBox.itemHeight) > this.ParentListBox.getHeight()) {
            if (this.ParentListBox.Items.size() > 0) {
               float var5 = (float)this.ParentListBox.getHeight().intValue() / (float)((this.ParentListBox.Items.size() + 1) * this.ParentListBox.itemHeight);
               this.ButtonInsideLength = (float)((int)((float)this.getHeight().intValue() * var5) - this.ButtonEndLength * 2);
               if (this.ButtonOffset < 0.0F) {
                  this.ButtonOffset = 0.0F;
               }

               if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F) {
                  this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
               }

               var2 = this.ButtonOffset / (float)this.getHeight().intValue();
               this.ParentListBox.topIndex = (int)((float)this.ParentListBox.Items.size() * var2);
            } else {
               this.ButtonOffset = 0.0F;
               this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
               this.ParentListBox.topIndex = 0;
            }
         } else {
            this.ButtonOffset = 0.0F;
            this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
            this.ParentListBox.topIndex = 0;
         }
      } else if (this.ParentTextBox != null) {
         var1 = TextManager.instance.getFontFromEnum(this.ParentTextBox.font).getLineHeight();
         if (this.ParentTextBox.Lines.size() > this.ParentTextBox.NumVisibleLines) {
            if (this.ParentTextBox.Lines.size() > 0) {
               int var3 = this.ParentTextBox.NumVisibleLines;
               if (var3 * var1 > this.ParentTextBox.getHeight().intValue() - this.ParentTextBox.getInset() * 2) {
                  --var3;
               }

               var2 = (float)var3 / (float)this.ParentTextBox.Lines.size();
               this.ButtonInsideLength = (float)((int)((float)this.getHeight().intValue() * var2) - this.ButtonEndLength * 2);
               if (this.ButtonOffset < 0.0F) {
                  this.ButtonOffset = 0.0F;
               }

               if (this.ButtonOffset > (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F) {
                  this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
               }

               float var4 = this.ButtonOffset / (float)this.getHeight().intValue();
               this.ParentTextBox.TopLineIndex = (int)((float)this.ParentTextBox.Lines.size() * var4);
            } else {
               this.ButtonOffset = 0.0F;
               this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
               this.ParentTextBox.TopLineIndex = 0;
            }
         } else {
            this.ButtonOffset = 0.0F;
            this.ButtonInsideLength = (float)(this.getHeight().intValue() - this.ButtonEndLength * 2);
            this.ParentTextBox.TopLineIndex = 0;
         }
      }

   }

   public void scrollToBottom() {
      this.ButtonOffset = (float)this.getHeight().intValue() - (this.ButtonInsideLength + (float)(this.ButtonEndLength * 2)) - 1.0F;
   }
}
