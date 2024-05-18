package zombie.ui;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.network.GameClient;

public class Clock extends UIElement {
   Texture[] digits;
   Texture texture = null;
   Texture slash = null;
   Texture colon = null;
   Texture texAM = null;
   Texture texPM = null;
   public boolean digital = false;
   private IsoPlayer clockPlayer = null;
   public static Clock instance = null;
   private String cacheTemp = "0 C";
   private int cacheTempCntr = 0;

   public Clock(int var1, int var2) {
      this.x = (double)var1;
      this.y = (double)var2;
      instance = this;
   }

   public void render() {
      if (this.visible) {
         float var1 = GameTime.getInstance().getTimeOfDay();
         if (GameClient.bClient && GameClient.bFastForward) {
            var1 = GameTime.getInstance().ServerTimeOfDay;
         }

         if (!Core.getInstance().getOptionClock24Hour()) {
            if (var1 >= 13.0F) {
               var1 -= 12.0F;
            }

            if (var1 < 1.0F) {
               var1 += 12.0F;
            }
         }

         float var2 = var1 - (float)((int)var1);
         var2 *= 60.0F;
         int var3 = (int)var1;
         int var4 = (int)(var2 / 10.0F);
         int var5 = 0;
         boolean var6 = false;
         if (var3 > 9) {
            var5 = var3 / 10;
         }

         int var14 = var3 % 10;
         this.DrawTextureScaled(this.texture, 0.0D, 0.0D, (double)this.texture.getWidth(), (double)this.height, 0.75D);
         int var7;
         if (this.digits == null) {
            this.digits = new Texture[20];

            for(var7 = 0; var7 < 10; ++var7) {
               this.digits[var7] = TexturePackPage.getTexture("media/ui/ClockDigit_" + var7 + ".png");
               this.digits[var7 + 10] = TexturePackPage.getTexture("media/ui/ClockDigitTiny_" + var7 + ".png");
            }
         }

         var7 = 0;
         boolean var8 = false;
         int var9 = 0;
         boolean var10 = false;
         int var15;
         if (GameTime.getInstance().getDay() + 1 > 9) {
            var7 = (GameTime.getInstance().getDay() + 1) / 10;
            var15 = (GameTime.getInstance().getDay() + 1) % 10;
         } else {
            var15 = GameTime.getInstance().getDay() + 1;
         }

         int var16;
         if (GameTime.getInstance().getMonth() + 1 > 9) {
            var9 = (GameTime.getInstance().getMonth() + 1) / 10;
            var16 = (GameTime.getInstance().getMonth() + 1) % 10;
         } else {
            var16 = GameTime.getInstance().getMonth() + 1;
         }

         if (this.slash == null) {
            this.slash = TexturePackPage.getTexture("media/ui/ClockDigitTiny_Slash.png");
         }

         if (this.colon == null) {
            this.colon = TexturePackPage.getTexture("media/ui/ClockDigit_Colon.png");
         }

         byte var11 = 5;
         byte var12 = 5;
         this.DrawTexture(this.digits[var5], (double)var11, (double)var12, 1.0D);
         int var17 = var11 + 11;
         this.DrawTexture(this.digits[var14], (double)var17, (double)var12, 1.0D);
         var17 += 11;
         this.DrawTexture(this.colon, (double)var17, (double)var12, 1.0D);
         var17 += 11;
         this.DrawTexture(this.digits[var4], (double)var17, (double)var12, 1.0D);
         var17 += 11;
         this.DrawTexture(this.digits[0], (double)var17, (double)var12, 1.0D);
         var17 += 16;
         if (!Core.getInstance().getOptionClock24Hour()) {
            var12 = 16;
         }

         if (Core.getInstance().getOptionClockFormat() == 1) {
            this.DrawTexture(this.digits[var9 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var16 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.slash, (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var7 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var15 + 10], (double)var17, (double)var12, 1.0D);
         } else {
            this.DrawTexture(this.digits[var7 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var15 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.slash, (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var9 + 10], (double)var17, (double)var12, 1.0D);
            var17 += 5;
            this.DrawTexture(this.digits[var16 + 10], (double)var17, (double)var12, 1.0D);
         }

         if (!Core.getInstance().getOptionClock24Hour()) {
            if (this.texAM == null) {
               this.texAM = Texture.getSharedTexture("media/ui/ClockAM.png");
            }

            if (this.texPM == null) {
               this.texPM = Texture.getSharedTexture("media/ui/ClockPM.png");
            }

            var17 -= 20;
            var12 = 5;
            if (GameTime.getInstance().getTimeOfDay() < 12.0F) {
               this.DrawTexture(this.texAM, (double)var17, (double)var12, 1.0D);
            } else {
               this.DrawTexture(this.texPM, (double)var17, (double)var12, 1.0D);
            }
         }

         if (this.digital && this.clockPlayer != null) {
            --this.cacheTempCntr;
            if (this.cacheTempCntr <= 0) {
               float var13 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.clockPlayer, false);
               this.cacheTemp = Temperature.getTemperatureString(var13);
               this.cacheTempCntr = 30;
            }

            this.DrawTextCentre(UIFont.DebugConsole, this.cacheTemp, 50.0D, (double)(this.height - 16.0F), 0.32549020648002625D, 0.9098039269447327D, 0.9372549057006836D, 1.0D);
         }

         super.render();
      }
   }

   public void resize() {
      this.visible = false;
      this.digital = false;
      this.clockPlayer = null;
      if (IsoPlayer.instance != null) {
         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            IsoPlayer var2 = IsoPlayer.players[var1];
            if (var2 != null && var2.getInventory().contains("Type:AlarmClock")) {
               this.visible = UIManager.VisibleAllUI;
               this.digital = var2.getInventory().contains("DigitalWatch2");
               this.clockPlayer = var2;
               break;
            }
         }
      }

      if (Core.bDebug) {
         this.visible = UIManager.VisibleAllUI;
      }

      if (this.texture == null) {
         this.texture = Texture.getSharedTexture("media/ui/ClockBackground.png");
      }

      if (this.digital) {
         this.setHeight((double)((float)this.texture.getHeight() + 12.0F));
      } else {
         this.setHeight((double)this.texture.getHeight());
      }

   }
}
