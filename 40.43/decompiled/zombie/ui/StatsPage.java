package zombie.ui;

import java.io.FileNotFoundException;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.core.textures.Texture;

public class StatsPage extends DraggableWindow {
   IsoGameCharacter chr;
   int drawY = 0;

   public StatsPage(IsoGameCharacter var1) throws FileNotFoundException {
      super(100, 100, "ContainerTitlebar", "StatsPage");
      this.chr = var1;
      this.width = 128.0F;
      this.height = 256.0F;
   }

   public void drawBarStat(String var1, float var2, boolean var3) {
      float var4 = var2;
      float var5 = var2;
      if (var3) {
         var5 = 1.0F - var2;
      } else {
         var4 = 1.0F - var2;
      }

      Color var6 = new Color(var4, var5, 0.0F);
      byte var7 = 48;
      this.DrawText(var1, 5.0D, (double)this.drawY, 1.0D, 1.0D, 1.0D, 1.0D);
      this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)var7, (double)(this.drawY + 3), (double)((int)(var2 * 75.0F)), 8.0D, var6);
      this.drawY += 12;
   }

   public void drawBarStat(String var1, float var2, Color var3) {
      byte var4 = 48;
      this.DrawText(var1, 5.0D, (double)this.drawY, 1.0D, 1.0D, 1.0D, 1.0D);
      this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)var4, (double)(this.drawY + 3), (double)((int)(var2 * 75.0F)), 8.0D, var3);
      this.drawY += 12;
   }

   public void render() {
      super.render();
      if (this.isVisible()) {
         this.drawY = 20;
         this.drawBarStat("Hunger", this.chr.getStats().hunger, true);
         this.drawBarStat("Fatigue", this.chr.getStats().fatigue, true);
         this.drawY += 10;
         this.drawBarStat("Stress", this.chr.getStats().stress, true);
         this.drawBarStat("Morale", this.chr.getStats().morale, false);
      }
   }
}
