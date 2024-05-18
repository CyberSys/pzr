package zombie.core.input;

import java.io.IOException;

public final class XINPUT_STATE {
   public int wButtons;
   public int bLeftTrigger;
   public int bRightTrigger;
   public int sThumbLX;
   public int sThumbLY;
   public int sThumbRX;
   public int sThumbRY;
   public boolean bConnected;

   public void setDisconnected() {
      this.bConnected = false;
   }

   public void set(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.bConnected = true;
      this.wButtons = var1;
      this.bLeftTrigger = var2;
      this.bRightTrigger = var3;
      this.sThumbLX = var4;
      this.sThumbLY = var5;
      this.sThumbRX = var6;
      this.sThumbRY = var7;
   }

   public native boolean nGetState(int var1) throws IOException;
}
