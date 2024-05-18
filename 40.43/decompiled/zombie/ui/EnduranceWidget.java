package zombie.ui;

import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;

public class EnduranceWidget extends UIElement {
   Color col;
   StatBar endurance;
   Texture run;

   public EnduranceWidget(int var1, int var2) {
      this.x = (double)var1;
      this.y = (double)var2;
      this.run = TexturePackPage.getTexture("Endurance_Run");
      this.endurance = new StatBar(0, 0, TexturePackPage.getTexture("EnduranceBar_Border"), TexturePackPage.getTexture("EnduranceBar_Fill"), true, Color.red);
      this.col = Color.green;
      this.AddChild(this.endurance);
      this.update();
   }

   public void render() {
      this.DrawTextureScaledCol(this.run, 0.0D, 0.0D, (double)this.run.getWidth(), (double)this.run.getHeight(), this.col);
      super.render();
   }

   public void update() {
      super.update();
      float var1 = IsoPlayer.getInstance().getStats().endurance;
      if (var1 > IsoPlayer.getInstance().getStats().endurancewarn) {
         this.col = new Color(0.3F, 0.7F, 0.1F);
      } else if (var1 > IsoPlayer.getInstance().getStats().endurancedanger) {
         this.col = new Color(0.9F, 0.5F, 0.1F);
      } else {
         this.col = Color.red;
      }

      this.endurance.col = this.col;
      this.endurance.setValue(var1);
   }
}
