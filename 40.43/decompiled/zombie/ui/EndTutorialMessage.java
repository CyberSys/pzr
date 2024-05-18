package zombie.ui;

import zombie.core.Core;
import zombie.gameStates.IngameState;

public class EndTutorialMessage extends NewWindow {
   public EndTutorialMessage() {
      super(Core.getInstance().getOffscreenWidth(0) / 2, Core.getInstance().getOffscreenHeight(0) / 2, 470, 10, true);
      this.IgnoreLossControl = true;
      String var1 = "This is the end of the tutorial<br><br>Now you're on your own<br><br>Whatever happens next, remember you always have a choice<br><br>But the smart choice may not be the one you want to make";
      TextBox var2 = new TextBox(UIFont.Medium, 0, 0, 450, var1);
      var2.Centred = true;
      var2.ResizeParent = true;
      var2.update();
      this.Nest(var2, 20, 10, 20, 10);
      this.update();
      this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2), (float)(this.getHeight().intValue() - 18), "Ok", "Ok"));
      this.x -= (double)(this.width / 2.0F);
      this.y -= (double)(this.height / 2.0F);
   }

   public void ButtonClicked(String var1) {
      if (var1.equals("Ok")) {
         this.setVisible(false);
         IngameState.instance.Paused = false;
      }

   }
}
