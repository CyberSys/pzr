package zombie.Quests.questactions;

import zombie.iso.IsoObject;
import zombie.ui.UIElement;
import zombie.ui.UIManager;

public class QuestAction_TutorialIcon implements QuestAction {
   boolean bAutoExpand;
   UIElement Parent;
   private String message;
   private IsoObject obj;
   private String title;
   private final int x;
   private final int y;
   private final float yoff;

   public QuestAction_TutorialIcon(String var1, String var2, IsoObject var3, boolean var4, float var5) {
      this.bAutoExpand = var4;
      this.obj = var3;
      this.message = var2;
      this.title = var1;
      this.x = 0;
      this.y = 0;
      this.yoff = var5;
   }

   public QuestAction_TutorialIcon(UIElement var1, int var2, int var3, String var4, String var5, boolean var6) {
      this.bAutoExpand = var6;
      this.message = var5;
      this.title = var4;
      this.yoff = 0.0F;
      this.Parent = var1;
      this.x = var2;
      this.y = var3;
   }

   public void Execute() {
      if (this.Parent == null) {
         UIManager.AddTutorial((float)this.obj.square.getX(), (float)this.obj.square.getY(), (float)this.obj.square.getZ(), this.title, this.message, this.bAutoExpand, this.yoff);
      } else {
         UIManager.AddTutorial(this.Parent, (double)this.x, (double)this.y, this.title, this.message, this.bAutoExpand);
      }

   }
}
