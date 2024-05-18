package zombie.ui;

import java.util.Iterator;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.input.Mouse;

public class QuestPanel extends NewWindow implements UIEventHandler {
   public static QuestPanel instance;
   ScrollBar QuestScrollbar = null;
   UITextBox2 QuestTextBox = null;
   private int MaxQuestIcons = 1000;
   int[] QuestIcons;
   int NumUsedIconSlots;
   int MouseOverX;
   int MouseOverY;
   String TempText;
   public Quest ActiveQuest;
   Quest MouseOverQuest;

   public QuestPanel(int var1, int var2) {
      super(var1, var2, 10, 10, true);
      this.QuestIcons = new int[this.MaxQuestIcons];
      this.NumUsedIconSlots = 0;
      this.MouseOverX = 0;
      this.MouseOverY = 0;
      this.TempText = null;
      this.ActiveQuest = null;
      this.MouseOverQuest = null;
      boolean var3 = true;
      boolean var4 = true;
      this.ResizeToFitY = false;
      this.visible = false;
      instance = this;
      this.width = 340.0F;
      this.height = 400.0F;
      this.Movable = true;
      this.MouseOverX = 0;
      this.MouseOverY = 0;
      this.QuestTextBox = new UITextBox2(UIFont.Small, 4, 21, (int)(this.getWidth() - 20.0D), (int)(this.getHeight() - 26.0D), "Quest Text", true);
      this.QuestScrollbar = new ScrollBar("QuestScrollBar", this, (int)(this.getWidth() - 15.0D), 21, (int)(this.getHeight() - 26.0D), true);
      this.QuestScrollbar.SetParentTextBox(this.QuestTextBox);
      this.AddChild(this.QuestTextBox);
      this.AddChild(this.QuestScrollbar);
   }

   public void render() {
      if (this.isVisible()) {
         super.render();
         this.DrawTextCentre("Quests", this.getWidth() / 2.0D, 2.0D, 1.0D, 1.0D, 1.0D, 1.0D);
         if (this.QuestTextBox.Lines != null) {
            int var1 = this.QuestTextBox.TopLineIndex;

            for(int var2 = 0; var1 < this.MaxQuestIcons && var1 < this.QuestTextBox.Lines.size() && var2 < this.QuestTextBox.NumVisibleLines; ++var1) {
               Texture var3 = null;
               if (this.QuestIcons[var1] != 0) {
                  switch(this.QuestIcons[var1]) {
                  case 1:
                     var3 = Texture.getSharedTexture("media/ui/Quest_Succeed.png");
                     break;
                  case 2:
                     var3 = Texture.getSharedTexture("media/ui/Quest_Failed.png");
                     break;
                  case 3:
                     var3 = Texture.getSharedTexture("media/ui/Quest_Bullet.png");
                  }
               }

               if (var3 != null) {
                  this.DrawTextureCol(var3, 8.0D, (double)(28 + var2 * 14), Color.white);
               }

               ++var2;
            }
         }

      }
   }

   public void update() {
      if (this.isVisible()) {
         super.update();
         float var1 = (float)this.getAbsoluteY().intValue();
         float var2 = var1 - 40.0F;
         float var3 = (float)Core.getInstance().getOffscreenHeight(0) - var1;
         if (var3 > 0.0F) {
            var2 /= var3;
         } else {
            var2 = 1.0F;
         }

         var2 *= 4.0F;
         var2 = 1.0F - var2;
         if (var2 < 0.0F) {
            var2 = 0.0F;
         }

         String var4 = "";
         String var5 = "";
         int var6 = 0;
         int var7 = 0;
         this.NumUsedIconSlots = 0;

         for(int var8 = 0; var8 < this.MaxQuestIcons; ++var8) {
            this.QuestIcons[var8] = 0;
         }

         this.MouseOverQuest = null;
         this.QuestTextBox.ClearHighlights();
         if (QuestManager.instance.QuestStack != null) {
            Iterator var12 = QuestManager.instance.QuestStack.iterator();

            label128:
            while(true) {
               Quest var9;
               do {
                  do {
                     if (!var12.hasNext()) {
                        break label128;
                     }

                     var9 = (Quest)var12.next();
                  } while(var9 == null);
               } while(!var9.Unlocked);

               if (var9.Complete) {
                  this.QuestIcons[var7] = 1;
               } else if (var9.Failed) {
                  this.QuestIcons[var7] = 2;
               }

               if (!var9.Complete && !var9.Failed) {
                  if (var9 == this.ActiveQuest) {
                     this.QuestTextBox.HighlightLines[var7] = 1;
                  }

                  if (var6 == 0) {
                     this.TempText = var9.getName() + ".\n";
                     ++var7;
                  } else {
                     this.TempText = this.TempText + var9.getName() + ".\n";
                     ++var7;
                  }
               } else if (var6 == 0) {
                  this.TempText = "    " + var9.getName() + ".\n";
                  ++var7;
               } else {
                  this.TempText = this.TempText + "    " + var9.getName() + ".\n";
                  ++var7;
               }

               ++var6;
               if (this.ActiveQuest != var9 && !var9.Complete && !var9.Failed) {
                  int var10 = var7 - 1 - this.QuestTextBox.TopLineIndex;
                  int var11 = 28 + var10 * 14;
                  if (this.MouseOverX > 10 && this.MouseOverX < 276 && this.MouseOverY >= var11 - 6 && this.MouseOverY <= var11 + 19) {
                     this.MouseOverQuest = var9;
                     this.QuestTextBox.HighlightLines[var7 - 1] = 2;
                  }
               }

               if (this.ActiveQuest == var9 && !var9.Complete && !var9.Failed) {
                  Iterator var13 = var9.QuestTaskStack.iterator();

                  while(var13.hasNext()) {
                     QuestTask var14 = (QuestTask)var13.next();
                     if (var14.Unlocked && !var14.Hidden) {
                        if (var14.Complete) {
                           this.QuestIcons[var7] = 1;
                        } else if (var14.Failed) {
                           this.QuestIcons[var7] = 2;
                        } else {
                           this.QuestIcons[var7] = 3;
                        }

                        this.TempText = this.TempText + "   " + var14.getName() + ".\n";
                        this.QuestTextBox.HighlightLines[var7] = 1;
                        ++var7;
                     }
                  }
               }

               this.TempText = this.TempText + "    \n";
               ++var7;
            }
         }

         this.NumUsedIconSlots = var7;
         if (this.TempText != null) {
            this.QuestTextBox.SetText(this.TempText);
         }

         if (this.ActiveQuest != null && (this.ActiveQuest.Complete || this.ActiveQuest.Failed)) {
            this.ActiveQuest = null;
            if (UIManager.getOnscreenQuest() != null) {
               UIManager.getOnscreenQuest().TriggerQuestWiggle();
            }
         }

      }
   }

   public void DoubleClick(String var1, int var2, int var3) {
   }

   public void Selected(String var1, int var2, int var3) {
   }

   public void SetActiveQuest(Quest var1) {
      this.ActiveQuest = var1;
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (!this.isVisible()) {
         return false;
      } else {
         super.onMouseDown(var1, var3);
         if (this.MouseOverQuest != null) {
            this.ActiveQuest = this.MouseOverQuest;
            if (UIManager.getOnscreenQuest() != null) {
               UIManager.getOnscreenQuest().TriggerQuestWiggle();
            }
         }

         return Boolean.TRUE;
      }
   }

   public Boolean onMouseMove(double var1, double var3) {
      if (!this.isVisible()) {
         return Boolean.FALSE;
      } else {
         super.onMouseMove(var1, var3);
         this.MouseOverX = Mouse.getXA() - this.getAbsoluteX().intValue();
         this.MouseOverY = Mouse.getYA() - this.getAbsoluteY().intValue();
         return Boolean.FALSE;
      }
   }

   public void onMouseMoveOutside(double var1, double var3) {
      super.onMouseMoveOutside(var1, var3);
      this.MouseOverX = 0;
      this.MouseOverY = 0;
      this.MouseOverQuest = null;
   }

   public void ModalClick(String var1, String var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
