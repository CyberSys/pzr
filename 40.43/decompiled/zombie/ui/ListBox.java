package zombie.ui;

import java.util.Stack;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.interfaces.IListBoxItem;

public class ListBox extends UIElement {
   public Color background = new Color(255, 255, 255, 10);
   public Color selColour = new Color(0, 0, 255, 10);
   public Color selColourDis = new Color(255, 0, 255, 30);
   public int itemHeight = 20;
   public int topIndex = 0;
   int timeSinceClick = 0;
   boolean clicked = false;
   private boolean mouseOver = false;
   public int Selected = -1;
   public int LastSelected = -1;
   UIEventHandler messageParent;
   private String name;
   public Stack Items = new Stack();

   public ListBox(String var1, UIEventHandler var2) {
      this.messageParent = var2;
      this.name = var1;
   }

   public void SetItemHeight(int var1) {
      this.itemHeight = var1;
   }

   private void Selected(int var1) {
      this.messageParent.Selected(this.name, var1, this.LastSelected);
   }

   public void remove(IListBoxItem var1) {
      for(int var2 = 0; var2 < this.Items.size(); ++var2) {
         if (((ListBox.ListItem)this.Items.get(var2)).item == var1) {
            this.Items.remove(var2);
            --var2;
         }
      }

   }

   public IListBoxItem getSelected() {
      return this.Selected != -1 ? ((ListBox.ListItem)this.Items.get(this.Selected)).item : null;
   }

   public void AddItem(IListBoxItem var1, Color var2, Color var3, Color var4) {
      this.Items.add(new ListBox.ListItem(var1, var2, var3, var4));
   }

   public void AddItem(IListBoxItem var1, Texture var2, Color var3, Color var4, Color var5) {
      this.Items.add(new ListBox.ListItem(var1, var2, var3, var4, var5));
   }

   public void AddItem(IListBoxItem var1, Color var2, Color var3, Color var4, boolean var5) {
      this.Items.add(new ListBox.ListItem(var1, var2, var3, var4, var5));
   }

   public void AddItem(IListBoxItem var1, String var2, Color var3, Color var4, Color var5) {
      this.Items.add(new ListBox.ListItem(var1, var3, var4, var5));
   }

   public void AddItem(IListBoxItem var1, String var2, Color var3, Color var4, Color var5, boolean var6) {
      this.Items.add(new ListBox.ListItem(var1, var3, var4, var5, var6));
   }

   public void render() {
      this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0D, 0.0D, (double)this.getWidth().intValue(), (double)this.getHeight().intValue(), this.background);

      for(int var1 = this.topIndex; var1 < this.Items.size() && (double)var1 < (double)this.topIndex + this.getHeight() / (double)this.itemHeight; ++var1) {
         Texture var2 = ((ListBox.ListItem)this.Items.get(var1)).Icon;
         ListBox.ListItem var3 = (ListBox.ListItem)this.Items.get(var1);
         int var4 = (var1 - this.topIndex) * this.itemHeight;
         if (this.Selected == var1) {
            if (((ListBox.ListItem)this.Items.get(this.Selected)).bDisabled) {
               this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0D, (double)(var4 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), this.selColourDis);
            } else {
               this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0D, (double)(var4 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), this.selColour);
            }
         } else if (((ListBox.ListItem)this.Items.get(var1)).bDisabled) {
            this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0D, (double)(var4 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), new Color(0.4F, 0.2F, 0.2F, 0.5F));
         } else {
            this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), 0.0D, (double)(var4 + 1), (double)this.getWidth().intValue(), (double)(this.itemHeight - 2), var3.backCol);
         }

         if (var2 == null) {
            this.DrawText(var3.item.getLeftLabel(), 10.0D, (double)(var4 + this.itemHeight / 2 - 6), (double)var3.leftCol.r, (double)var3.leftCol.g, (double)var3.leftCol.b, (double)var3.leftCol.a);
            this.DrawTextRight(var3.item.getRightLabel(), 0.0D + this.getWidth() - 10.0D, (double)(var4 + this.itemHeight / 2 - 6), (double)var3.rightCol.r, (double)var3.rightCol.g, (double)var3.rightCol.b, (double)var3.rightCol.a);
         } else {
            if (((ListBox.ListItem)this.Items.get(var1)).bDisabled) {
               this.DrawTextureScaledCol(var2, 2.0D, (double)(var4 + 2), (double)var2.getWidth(), (double)var2.getWidth(), Color.gray);
            } else {
               this.DrawTextureScaledCol(var2, 2.0D, (double)(var4 + 2), (double)var2.getWidth(), (double)var2.getWidth(), Color.white);
            }

            this.DrawText(var3.item.getLeftLabel(), (double)(10 + var2.getWidth() + 4), (double)(var4 + this.itemHeight / 2 - 6), (double)var3.leftCol.r, (double)var3.leftCol.g, (double)var3.leftCol.b, (double)var3.leftCol.a);
            this.DrawTextRight(var3.item.getRightLabel(), 0.0D + this.getWidth() - 10.0D + (double)var2.getWidth() + 4.0D, (double)(var4 + this.itemHeight / 2 - 6), (double)var3.rightCol.r, (double)var3.rightCol.g, (double)var3.rightCol.b, (double)var3.rightCol.a);
         }
      }

   }

   public Boolean onMouseMove(double var1, double var3) {
      this.mouseOver = true;
      return Boolean.TRUE;
   }

   public void onMouseMoveOutside(double var1, double var3) {
      this.mouseOver = false;
   }

   private void DoubleClick(double var1, double var3) {
      if (this.Selected != -1 && !((ListBox.ListItem)this.Items.get(this.Selected)).bDisabled) {
         this.messageParent.DoubleClick(this.name, (int)var1, (int)var3);
      }

   }

   public Boolean onMouseUp(double var1, double var3) {
      if (this.clicked) {
         this.timeSinceClick = 0;
         this.LastSelected = this.Selected;
         int var5 = (int)var3 / this.itemHeight;
         var5 += this.topIndex;
         if (var5 < this.Items.size() && var5 >= 0) {
            this.Selected = var5;
            this.Selected(var5);
         }
      }

      this.clicked = false;
      this.setCapture(false);
      return Boolean.FALSE;
   }

   public void update() {
      super.update();
      ++this.timeSinceClick;
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (this.timeSinceClick < 10) {
         this.DoubleClick(var1, var3);
      }

      this.clicked = true;
      this.setCapture(true);
      return Boolean.FALSE;
   }

   public static class ListItem {
      public Color leftCol;
      public Color rightCol;
      public Color backCol;
      public IListBoxItem item;
      public boolean bDisabled = false;
      public Texture Icon = null;

      public ListItem(IListBoxItem var1, Color var2, Color var3, Color var4) {
         this.item = var1;
         this.bDisabled = false;
         this.leftCol = var2;
         this.rightCol = var3;
         this.backCol = var4;
      }

      public ListItem(IListBoxItem var1, Texture var2, Color var3, Color var4, Color var5) {
         this.item = var1;
         this.bDisabled = false;
         this.leftCol = var3;
         this.rightCol = var4;
         this.backCol = var5;
         this.Icon = var2;
      }

      public ListItem(IListBoxItem var1, Color var2, Color var3, Color var4, boolean var5) {
         this.item = var1;
         this.bDisabled = var5;
         this.leftCol = var2;
         this.rightCol = var3;
         this.backCol = var4;
      }
   }
}
