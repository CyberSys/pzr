package zombie.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.core.logger.ExceptionLogger;

public class ServerSettingsManager {
   public static final ServerSettingsManager instance = new ServerSettingsManager();
   protected ArrayList settings = new ArrayList();
   protected ArrayList suffixes = new ArrayList();

   public String getSettingsFolder() {
      return GameWindow.getCacheDir() + File.separator + "Server";
   }

   public String getNameInSettingsFolder(String var1) {
      return this.getSettingsFolder() + File.separator + var1;
   }

   public void readAllSettings() {
      this.settings.clear();
      File var1 = new File(this.getSettingsFolder());
      if (!var1.exists()) {
         var1.mkdirs();
      } else {
         Filter var2 = new Filter() {
            public boolean accept(Path var1) throws IOException {
               String var2 = var1.getFileName().toString();
               return !Files.isDirectory(var1, new LinkOption[0]) && var2.endsWith(".ini") && !var2.endsWith("_zombies.ini") && ServerSettingsManager.this.isValidName(var2.replace(".ini", ""));
            }
         };

         try {
            DirectoryStream var3 = Files.newDirectoryStream(var1.toPath(), var2);
            Throwable var4 = null;

            try {
               Iterator var5 = var3.iterator();

               while(var5.hasNext()) {
                  Path var6 = (Path)var5.next();
                  ServerSettings var7 = new ServerSettings(var6.getFileName().toString().replace(".ini", ""));
                  this.settings.add(var7);
               }
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Exception var18) {
            ExceptionLogger.logException(var18);
         }

      }
   }

   public int getSettingsCount() {
      return this.settings.size();
   }

   public ServerSettings getSettingsByIndex(int var1) {
      return var1 >= 0 && var1 < this.settings.size() ? (ServerSettings)this.settings.get(var1) : null;
   }

   public boolean isValidName(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         if (!var1.contains("/") && !var1.contains("\\") && !var1.contains(":") && !var1.contains(";") && !var1.contains("\"") && !var1.contains(".")) {
            return !var1.contains("_zombies");
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean anyFilesExist(String var1) {
      this.getSuffixes();

      for(int var2 = 0; var2 < this.suffixes.size(); ++var2) {
         File var3 = new File(this.getSettingsFolder() + File.separator + var1 + (String)this.suffixes.get(var2));
         if (var3.exists()) {
            return true;
         }
      }

      return false;
   }

   public boolean isValidNewName(String var1) {
      if (!this.isValidName(var1)) {
         return false;
      } else {
         return !this.anyFilesExist(var1);
      }
   }

   public ArrayList getSuffixes() {
      if (this.suffixes.isEmpty()) {
         this.suffixes.add(".ini");
         this.suffixes.add("_SandboxVars.lua");
         this.suffixes.add("_spawnpoints.lua");
         this.suffixes.add("_spawnregions.lua");
         this.suffixes.add("_zombies.ini");
      }

      return this.suffixes;
   }
}
