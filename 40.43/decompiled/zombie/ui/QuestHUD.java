package zombie.ui;

import java.util.Iterator;
import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.Quests.QuestTask;
import zombie.core.Core;

public class QuestHUD extends UIElement implements UIEventHandler {
   private boolean FirstQuestSet = false;
   private float QuestOscilationLevel = 0.0F;
   private float QuestOscilator = 0.0F;
   private float QuestOscilatorDecelerator = 0.93F;
   private float QuestOscilatorRate = 0.8F;
   private float QuestOscilatorScalar = 15.6F;
   private float QuestOscilatorStartLevel = 1.0F;
   private float QuestOscilatorStep = 0.0F;
   private float QuestDefaultXOffset = 0.0F;
   DialogButton QuestPanelButton = null;

   public QuestHUD() {
      this.QuestPanelButton = new DialogButton(this, 222.0F, 50.0F, "Quest Manager", "Quest Manager");
      this.AddChild(this.QuestPanelButton);
      this.FirstQuestSet = false;
   }

   public void TriggerQuestWiggle() {
      this.QuestOscilationLevel = this.QuestOscilatorStartLevel;
   }

   public void render() {
      if (this.QuestPanelButton.clicked) {
         UIManager.questPanel.setVisible(!UIManager.questPanel.isVisible());
         this.QuestPanelButton.clicked = false;
         UIManager.questPanel.setX((double)(Core.getInstance().getScreenWidth() - 463));
         UIManager.questPanel.setY(66.0D);
      }

      this.QuestOscilatorStep += this.QuestOscilatorRate;
      this.QuestOscilator = (float)Math.sin((double)this.QuestOscilatorStep);
      float var1 = this.QuestOscilator * this.QuestOscilatorScalar * this.QuestOscilationLevel;
      this.QuestOscilationLevel *= this.QuestOscilatorDecelerator;
      String var2 = "";
      String var3 = "";
      super.render();
      Iterator var4;
      if (QuestPanel.instance.ActiveQuest != null && !QuestPanel.instance.ActiveQuest.Failed && !QuestPanel.instance.ActiveQuest.Complete) {
         if (QuestPanel.instance.ActiveQuest != null) {
            var4 = QuestPanel.instance.ActiveQuest.QuestTaskStack.iterator();

            while(var4.hasNext()) {
               QuestTask var6 = (QuestTask)var4.next();
               if (var6.Unlocked && !var6.Failed && !var6.Complete && !var6.Hidden) {
                  var2 = QuestPanel.instance.ActiveQuest.getName();
                  var3 = var6.getName();
                  this.DrawTextRight(UIFont.Medium, var2, this.getWidth(), 0.0D, 1.0D, 1.0D, 1.0D, 1.0D);
                  this.DrawTextRight(UIFont.Small, var3, this.getWidth() - 3.0D + (double)((int)var1), 19.0D, 1.0D, 1.0D, 1.0D, 1.0D);
                  return;
               }
            }
         }
      } else {
         QuestPanel.instance.ActiveQuest = null;
         this.FirstQuestSet = false;
         var4 = QuestManager.instance.QuestStack.iterator();

         while(var4.hasNext()) {
            Quest var5 = (Quest)var4.next();
            if (var5.Unlocked && !var5.Complete && !var5.Failed && !this.FirstQuestSet) {
               this.FirstQuestSet = true;
               QuestPanel.instance.SetActiveQuest(var5);
            }
         }
      }

   }

   public void DoubleClick(String var1, int var2, int var3) {
   }

   public void Selected(String var1, int var2, int var3) {
   }

   public void ModalClick(String var1, String var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
