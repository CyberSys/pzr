package zombie.core.input;

import java.io.IOException;
import java.util.ArrayDeque;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Rumbler;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;

public class XInputController extends AbstractController {
   final short dwUserIndex;
   private final XINPUT_STATE stateLast = new XINPUT_STATE();
   private final XINPUT_STATE stateCur = new XINPUT_STATE();
   private long lastPoll;
   public static final int XINPUT_GAMEPAD_DPAD_UP = 1;
   public static final int XINPUT_GAMEPAD_DPAD_DOWN = 2;
   public static final int XINPUT_GAMEPAD_DPAD_LEFT = 4;
   public static final int XINPUT_GAMEPAD_DPAD_RIGHT = 8;
   public static final int XINPUT_GAMEPAD_START = 16;
   public static final int XINPUT_GAMEPAD_BACK = 32;
   public static final int XINPUT_GAMEPAD_LEFT_THUMB = 64;
   public static final int XINPUT_GAMEPAD_RIGHT_THUMB = 128;
   public static final int XINPUT_GAMEPAD_LEFT_SHOULDER = 256;
   public static final int XINPUT_GAMEPAD_RIGHT_SHOULDER = 512;
   public static final int XINPUT_GAMEPAD_A = 4096;
   public static final int XINPUT_GAMEPAD_B = 8192;
   public static final int XINPUT_GAMEPAD_X = 16384;
   public static final int XINPUT_GAMEPAD_Y = 32768;
   public static final int DPAD_ALL = 15;
   public static final int[] ComponentButtonBits = new int[]{4096, 8192, 16384, 32768, 256, 512, 32, 16, 64, 128, 0, 0, 0, 0, 0, 0};
   private final ArrayDeque events = new ArrayDeque();

   public static XInputController create(int var0, String var1) {
      Component[] var2 = new Component[16];
      byte var3 = 0;
      int var4 = var3 + 1;
      var2[var3] = new XInputComponent("A", Button.A);
      var2[var4++] = new XInputComponent("B", Button.B);
      var2[var4++] = new XInputComponent("X", Button.X);
      var2[var4++] = new XInputComponent("Y", Button.Y);
      var2[var4++] = new XInputComponent("LeftShoulder", Button.LEFT_THUMB2);
      var2[var4++] = new XInputComponent("RightShoulder", Button.RIGHT_THUMB2);
      var2[var4++] = new XInputComponent("Back", Button.BACK);
      var2[var4++] = new XInputComponent("Start", Button.SELECT);
      var2[var4++] = new XInputComponent("LeftThumb", Button.LEFT_THUMB);
      var2[var4++] = new XInputComponent("RightThumb", Button.RIGHT_THUMB);
      var2[var4++] = new XInputComponent("LeftY", Axis.Y);
      var2[var4++] = new XInputComponent("LeftX", Axis.X);
      var2[var4++] = new XInputComponent("RightY", Axis.RY);
      var2[var4++] = new XInputComponent("RightX", Axis.RX);
      var2[var4++] = new XInputComponent("Trigger", Axis.Z);
      var2[var4++] = new XInputComponent("POV", Axis.POV);
      return new XInputController(var0, var1, var2, new Controller[0], new Rumbler[0]);
   }

   protected XInputController(int var1, String var2, Component[] var3, Controller[] var4, Rumbler[] var5) {
      super(var2, var3, var4, var5);
      this.dwUserIndex = (short)var1;
   }

   protected void pollDevice() throws IOException {
      if (!this.stateCur.bConnected) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.lastPoll < 5000L) {
            return;
         }

         this.lastPoll = var1;
      }

      if (this.stateCur.nGetState(this.dwUserIndex)) {
         float var7;
         if (this.stateLast.wButtons != this.stateCur.wButtons) {
            boolean var4;
            if ((this.stateLast.wButtons & 15) != (this.stateCur.wButtons & 15)) {
               var7 = 0.0F;
               boolean var2 = (this.stateCur.wButtons & 1) != 0;
               boolean var3 = (this.stateCur.wButtons & 2) != 0;
               var4 = (this.stateCur.wButtons & 4) != 0;
               boolean var5 = (this.stateCur.wButtons & 8) != 0;
               if (var2 && !var4 && !var5) {
                  var7 = 0.25F;
               } else if (var2 && var5) {
                  var7 = 0.375F;
               } else if (var5 && !var3) {
                  var7 = 0.5F;
               } else if (var5 && var3) {
                  var7 = 0.625F;
               } else if (var3 && !var4) {
                  var7 = 0.75F;
               } else if (var3 && var4) {
                  var7 = 0.875F;
               } else if (var4 && !var2) {
                  var7 = 1.0F;
               } else if (var4 && var2) {
                  var7 = 0.125F;
               }

               XInputController.XInputEvent var6 = XInputController.XInputEvent.alloc();
               var6.set(this.getComponent(Axis.POV), var7);
               this.events.addLast(var6);
            }

            Component[] var11 = this.getComponents();

            for(int var8 = 0; var8 < var11.length; ++var8) {
               int var10 = ComponentButtonBits[var8];
               if (var10 != 0) {
                  var4 = (this.stateCur.wButtons & var10) != 0;
                  if ((this.stateLast.wButtons & var10) != (this.stateCur.wButtons & var10)) {
                     XInputController.XInputEvent var12 = XInputController.XInputEvent.alloc();
                     var12.set(var11[var8], var4 ? 1.0F : 0.0F);
                     this.events.addLast(var12);
                  }
               }
            }

            this.stateLast.wButtons = this.stateCur.wButtons;
         }

         XInputController.XInputEvent var9;
         if (this.stateLast.bLeftTrigger != this.stateCur.bLeftTrigger || this.stateLast.bRightTrigger != this.stateCur.bRightTrigger) {
            var7 = (float)(this.stateCur.bLeftTrigger - this.stateCur.bRightTrigger) / 255.0F;
            var9 = XInputController.XInputEvent.alloc();
            var9.set(this.getComponent(Axis.Z), var7);
            this.events.addLast(var9);
            this.stateLast.bLeftTrigger = this.stateCur.bLeftTrigger;
            this.stateLast.bRightTrigger = this.stateCur.bRightTrigger;
         }

         if (this.stateLast.sThumbLX != this.stateCur.sThumbLX) {
            var7 = this.stateCur.sThumbLX > 0 ? (float)this.stateCur.sThumbLX / 32767.0F : (float)this.stateCur.sThumbLX / 32768.0F;
            var9 = XInputController.XInputEvent.alloc();
            var9.set(this.getComponent(Axis.X), var7);
            this.events.addLast(var9);
            this.stateLast.sThumbLX = this.stateCur.sThumbLX;
         }

         if (this.stateLast.sThumbLY != this.stateCur.sThumbLY) {
            var7 = this.stateCur.sThumbLY > 0 ? (float)this.stateCur.sThumbLY / 32767.0F : (float)this.stateCur.sThumbLY / 32768.0F;
            var7 *= -1.0F;
            var9 = XInputController.XInputEvent.alloc();
            var9.set(this.getComponent(Axis.Y), var7);
            this.events.addLast(var9);
            this.stateLast.sThumbLY = this.stateCur.sThumbLY;
         }

         if (this.stateLast.sThumbRX != this.stateCur.sThumbRX) {
            var7 = this.stateCur.sThumbRX > 0 ? (float)this.stateCur.sThumbRX / 32767.0F : (float)this.stateCur.sThumbRX / 32768.0F;
            var9 = XInputController.XInputEvent.alloc();
            var9.set(this.getComponent(Axis.RX), var7);
            this.events.addLast(var9);
            this.stateLast.sThumbRX = this.stateCur.sThumbRX;
         }

         if (this.stateLast.sThumbRY != this.stateCur.sThumbRY) {
            var7 = this.stateCur.sThumbRY > 0 ? (float)this.stateCur.sThumbRY / 32767.0F : (float)this.stateCur.sThumbRY / 32768.0F;
            var7 *= -1.0F;
            var9 = XInputController.XInputEvent.alloc();
            var9.set(this.getComponent(Axis.RY), var7);
            this.events.addLast(var9);
            this.stateLast.sThumbRY = this.stateCur.sThumbRY;
         }

      }
   }

   protected boolean getNextDeviceEvent(Event var1) throws IOException {
      if (this.events.isEmpty()) {
         return false;
      } else {
         XInputController.XInputEvent var2 = (XInputController.XInputEvent)this.events.removeFirst();
         var1.set(var2.component, var2.value, var2.ns);
         XInputController.XInputEvent.release(var2);
         return true;
      }
   }

   public boolean isConnected() {
      return this.stateCur.bConnected;
   }

   private static final class XInputEvent {
      Component component;
      float value;
      long ns;
      private static final ArrayDeque freeEvents = new ArrayDeque();

      void set(Component var1, float var2) {
         this.component = var1;
         this.value = var2;
         this.ns = System.nanoTime();
         ((XInputComponent)var1).pollData = var2;
      }

      static XInputController.XInputEvent alloc() {
         return freeEvents.isEmpty() ? new XInputController.XInputEvent() : (XInputController.XInputEvent)freeEvents.pop();
      }

      static void release(XInputController.XInputEvent var0) {
         freeEvents.push(var0);
      }
   }
}
