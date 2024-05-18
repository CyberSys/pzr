package zombie.input;

import org.lwjgl.input.Keyboard;
import zombie.FrameLoader;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.ui.UIManager;

public class GameKeyboard {
   static boolean[] bDown;
   static boolean[] bLastDown;
   static boolean[] bEatKey;
   static int isDownFor = 0;
   public static boolean doLuaKeyPressed = true;

   public static void update() {
      if (!FrameLoader.bDedicated) {
         short var0 = 256;
         if (bDown == null) {
            bDown = new boolean[var0];
            bLastDown = new boolean[var0];
            bEatKey = new boolean[var0];
         }

         for(int var1 = 1; var1 < var0; ++var1) {
            bLastDown[var1] = bDown[var1];
            bDown[var1] = Keyboard.isKeyDown(var1);
            if (bDown[Core.getInstance().getKey("Run")]) {
               ++isDownFor;
            }

            if (!bDown[var1] && bLastDown[var1]) {
               if (bEatKey[var1]) {
                  bEatKey[var1] = false;
                  continue;
               }

               if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
                  continue;
               }

               if (Core.bDebug && !doLuaKeyPressed) {
                  System.out.println("KEY RELEASED " + var1 + " doLuaKeyPressed=false");
               }

               if (var1 == Core.getInstance().getKey("Run") && Core.getInstance().isToggleToRun() && isDownFor < 5000) {
                  IsoPlayer.instance.setForceRun(!IsoPlayer.instance.isForceRun());
               }

               if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
                  LuaEventManager.triggerEvent("OnKeyPressed", var1);
               }

               if (LuaManager.thread == UIManager.defaultthread) {
                  LuaEventManager.triggerEvent("OnCustomUIKey", var1);
                  LuaEventManager.triggerEvent("OnCustomUIKeyReleased", var1);
               }

               isDownFor = 0;
            }

            if (bDown[var1] && LuaManager.thread == UIManager.defaultthread) {
               LuaEventManager.triggerEvent("OnKeyKeepPressed", var1);
            }

            if (bDown[var1] && !bLastDown[var1] && (Core.CurrentTextEntryBox == null || !Core.CurrentTextEntryBox.DoingTextEntry) && !bEatKey[var1]) {
               if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
                  LuaEventManager.triggerEvent("OnKeyStartPressed", var1);
               }

               if (LuaManager.thread == UIManager.defaultthread) {
                  LuaEventManager.triggerEvent("OnCustomUIKeyPressed", var1);
               }
            }
         }

      }
   }

   public static boolean isKeyDown(int var0) {
      if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
         return false;
      } else {
         return bDown == null ? false : bDown[var0];
      }
   }

   public static boolean wasKeyDown(int var0) {
      return Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry ? false : bLastDown[var0];
   }

   public static void eatKeyPress(int var0) {
      if (var0 >= 0 && var0 < bEatKey.length) {
         bEatKey[var0] = true;
      }
   }

   public static void setDoLuaKeyPressed(boolean var0) {
      doLuaKeyPressed = var0;
   }
}
