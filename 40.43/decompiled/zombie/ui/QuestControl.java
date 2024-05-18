package zombie.ui;

import java.util.Iterator;
import java.util.Stack;
import zombie.Quests.Completable;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.textures.Texture;

public class QuestControl extends UIElement {
   public boolean bShort = false;
   public Texture bullet = Texture.getSharedTexture("media/ui/Quest_Bullet.png");
   public Texture failed = Texture.getSharedTexture("media/ui/Quest_Failed.png");
   public Stack IndentLines = new Stack();
   public int originalWidth = 0;
   public Stack Status = new Stack();
   public Texture succeed = Texture.getSharedTexture("media/ui/Quest_Succeed.png");
   Stack Lines = new Stack();
   int maxWidthUsed = 0;

   public QuestControl(boolean var1) {
      this.bShort = var1;
      this.originalWidth = 500;
      this.width = 500.0F;
   }

   public Double getHeight() {
      int var1 = this.Lines.size() * 17;
      return var1 < 50 ? BoxedStaticValues.toDouble(50.0D) : BoxedStaticValues.toDouble((double)var1);
   }

   public void render() {
      this.setWidth((double)this.originalWidth);
      this.Paginate();
      this.setHeight(this.getHeight());
      int var1 = 0;
      int var2 = 0;

      for(Iterator var3 = this.Lines.iterator(); var3.hasNext(); ++var1) {
         String var4 = (String)var3.next();
         Completable var5 = (Completable)this.Status.get(var1);
         int var6 = 0;
         if (this.IndentLines.contains(var1)) {
            var6 += 16;
         }

         if (var5 != null) {
            Texture var7 = this.bullet;
            if (var5.IsComplete()) {
               var7 = this.succeed;
            }

            if (var5.IsFailed()) {
               var7 = this.failed;
            }

            this.DrawTextureCol(var7, (double)var6, (double)(var2 + 2), Color.white);
         }

         this.DrawText(var4, (double)(var6 + 16), (double)var2, 1.0D, 1.0D, 1.0D, 1.0D);
         var2 += 17;
      }

   }

   private void Paginate() {
      this.maxWidthUsed = 0;
      boolean var1 = false;
      this.Lines.clear();
      this.Status.clear();
      this.IndentLines.clear();
      boolean var2 = false;
      boolean var3 = false;
      Iterator var4 = QuestManager.instance.QuestStack.iterator();

      label84:
      while(true) {
         Quest var5;
         String var6;
         do {
            do {
               do {
                  while(true) {
                     do {
                        if (!var4.hasNext()) {
                           if (this.getParent() != null) {
                              this.getParent().setHeight(this.getHeight() + 32.0D);
                           }

                           this.setWidth((double)this.maxWidthUsed);
                           return;
                        }

                        var5 = (Quest)var4.next();
                     } while(!var5.Unlocked);

                     if (!this.bShort || !var5.Complete && !var5.Failed) {
                        break;
                     }
                  }
               } while(this.bShort && var2);

               var6 = var5.getName();
               this.PaginateText(var6, false, var5);
            } while(var5.Complete);
         } while(var5.Failed);

         Iterator var7 = var5.QuestTaskStack.iterator();

         while(true) {
            QuestTask var8;
            do {
               do {
                  if (!var7.hasNext()) {
                     var2 = true;
                     continue label84;
                  }

                  var8 = (QuestTask)var7.next();
               } while(!var8.Unlocked);
            } while(this.bShort && (var8.Complete || var8.Failed || var8.Hidden || var8.getName().length() == 0));

            var6 = var8.getName();
            this.PaginateText(var6, true, var8);
            var3 = true;
         }
      }
   }

   private void PaginateText(String var1, boolean var2, Completable var3) {
      float var4 = (float)this.getWidth().intValue();
      if (var2) {
         var4 -= 20.0F;
      }

      boolean var5 = false;
      int var6 = 0;

      do {
         int var7 = var1.indexOf(" ", var6 + 1);
         int var8 = var7;
         if (var7 == -1) {
            var8 = var1.length();
         }

         int var9 = TextManager.instance.MeasureStringX(UIFont.Small, var1.substring(0, var8));
         if ((float)var9 >= var4) {
            this.maxWidthUsed = this.originalWidth;
            String var10 = var1.substring(0, var6);
            var1 = var1.substring(var6 + 1);
            this.Lines.add(var10);
            if (var2) {
               this.IndentLines.add(this.Lines.size() - 1);
            }

            if (!var5) {
               this.Status.add(var3);
               var5 = true;
            } else {
               this.Status.add((Object)null);
            }

            var7 = 0;
         } else {
            if (var9 > this.maxWidthUsed) {
               this.maxWidthUsed = var9 + 16;
            }

            if (var7 == -1) {
               this.Lines.add(var1);
               if (!var5) {
                  this.Status.add(var3);
                  var5 = true;
               } else {
                  this.Status.add((Object)null);
               }

               if (var2) {
                  this.IndentLines.add(this.Lines.size() - 1);
               }

               return;
            }
         }

         var6 = var7;
      } while(var1.length() > 0);

      if (var2) {
         this.maxWidthUsed += 20;
      }

      if (this.maxWidthUsed > this.originalWidth) {
         this.maxWidthUsed = this.originalWidth;
      }

   }
}
