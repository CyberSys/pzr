package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import zombie.FrameLoader;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.ui.UIManager;

public class LoginState extends GameState {
   public static int HEREBEWHEREYOUCRACKOBVIOUSLY_PLEASESPREADTHEWORD = 0;
   public static boolean LoggedIn = true;
   public static String version = "RC3";

   public void enter() {
      String var1 = GameWindow.getCacheDir() + File.separator;
      File var2 = new File(var1);
      if (!var2.exists()) {
         var2.mkdirs();
      }

      var1 = var1 + "2133243254543.log";
      FrameLoader.makefile = new File(var1);
      if (!FrameLoader.makefile.exists() && !LoggedIn) {
         LuaEventManager.triggerEvent("OnLoginState", this);
      } else {
         LoggedIn = true;
      }

   }

   public boolean Test(String var1, String var2) {
      var1 = var1.trim();
      var2 = var2.trim();

      try {
         LoggedIn = validateUser(var1.trim(), var2.trim(), (String)null);
      } catch (MalformedURLException var6) {
         var6.printStackTrace();
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      if (LoggedIn) {
         File var3 = new File(GameWindow.getCacheDir() + File.separator + "2133243254543.log");

         try {
            if (var3.exists()) {
               var3.delete();
               var3.createNewFile();
            } else {
               var3.createNewFile();
            }
         } catch (Exception var5) {
         }
      }

      return LoggedIn;
   }

   public boolean Test(String var1) {
      var1 = var1.trim();

      try {
         LoggedIn = validateUser((String)null, (String)null, var1);
      } catch (MalformedURLException var5) {
         var5.printStackTrace();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      if (LoggedIn) {
         File var2 = new File(GameWindow.getCacheDir() + File.separator + "2133243254543.log");

         try {
            if (var2.exists()) {
               var2.delete();
               var2.createNewFile();
            } else {
               var2.createNewFile();
            }
         } catch (Exception var4) {
         }
      }

      return LoggedIn;
   }

   public String getCachedUsername() {
      return "";
   }

   public String getCachedPassword() {
      return "";
   }

   private static boolean validateUser(String var0, String var1, String var2) throws MalformedURLException, IOException {
      String var3 = null;

      try {
         if (var0 != null && !var0.isEmpty()) {
            var3 = "http://www.projectzomboid.com/scripts/auth.php?username=" + var0 + "&password=" + var1;
         } else {
            var3 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + var2;
         }

         URL var4 = new URL(var3);
         URLConnection var10 = var4.openConnection();
         BufferedReader var11 = new BufferedReader(new InputStreamReader(var10.getInputStream()));

         String var12;
         do {
            if ((var12 = var11.readLine()) == null) {
               return false;
            }
         } while(!var12.contains("success"));

         return true;
      } catch (Exception var9) {
         if (var0 != null && !var0.isEmpty()) {
            var3 = "http://www.projectzomboid.com/scripts/auth.php?username=" + var0 + "&password=" + var1;
         } else {
            var3 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + var2;
         }

         URL var5 = new URL(var3);
         URLConnection var6 = var5.openConnection();
         BufferedReader var7 = new BufferedReader(new InputStreamReader(var6.getInputStream()));

         String var8;
         do {
            if ((var8 = var7.readLine()) == null) {
               return false;
            }
         } while(!var8.contains("success"));

         return true;
      }
   }

   public static void DrawTexture(Texture var0, int var1, int var2, int var3, int var4, float var5) {
      SpriteRenderer.instance.render(var0, var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var5);
   }

   public void render() {
      Core.getInstance().StartFrame();
      Core.getInstance().EndFrame();
      Core.getInstance().StartFrameUI();
      UIManager.render();
      Core.getInstance().EndFrameUI();
   }

   public GameStateMachine.StateAction update() {
      if (LoggedIn && version.equals(GameWindow.version) && HEREBEWHEREYOUCRACKOBVIOUSLY_PLEASESPREADTHEWORD == 0) {
         LuaEventManager.triggerEvent("OnLoginStateSuccess");
         return GameStateMachine.StateAction.Continue;
      } else {
         return GameStateMachine.StateAction.Remain;
      }
   }
}
