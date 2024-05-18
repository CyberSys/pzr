package zombie.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import zombie.debug.DebugLog;

public class ConfigFile {
   protected ArrayList options;
   protected int version;

   private void fileError(String var1, int var2, String var3) {
      DebugLog.log(var1 + ":" + var2 + " " + var3);
   }

   public boolean read(String var1) {
      this.options = new ArrayList();
      this.version = 0;
      File var2 = new File(var1);
      if (!var2.exists()) {
         return false;
      } else {
         DebugLog.log("reading " + var1);

         try {
            FileReader var3 = new FileReader(var2);
            Throwable var4 = null;

            try {
               BufferedReader var5 = new BufferedReader(var3);
               Throwable var6 = null;

               try {
                  int var7 = 0;

                  while(true) {
                     String var8 = var5.readLine();
                     if (var8 == null) {
                        return true;
                     }

                     ++var7;
                     var8 = var8.trim();
                     if (!var8.isEmpty() && !var8.startsWith("#")) {
                        if (!var8.contains("=")) {
                           this.fileError(var1, var7, var8);
                        } else {
                           String[] var9 = var8.split("=");
                           if ("Version".equals(var9[0])) {
                              try {
                                 this.version = Integer.parseInt(var9[1]);
                              } catch (NumberFormatException var36) {
                                 this.fileError(var1, var7, "expected version number, got \"" + var9[1] + "\"");
                              }
                           } else {
                              StringConfigOption var10 = new StringConfigOption(var9[0], var9.length > 1 ? var9[1] : "");
                              this.options.add(var10);
                           }
                        }
                     }
                  }
               } catch (Throwable var37) {
                  var6 = var37;
                  throw var37;
               } finally {
                  if (var5 != null) {
                     if (var6 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var35) {
                           var6.addSuppressed(var35);
                        }
                     } else {
                        var5.close();
                     }
                  }

               }
            } catch (Throwable var39) {
               var4 = var39;
               throw var39;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var34) {
                        var4.addSuppressed(var34);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Exception var41) {
            var41.printStackTrace();
            return false;
         }
      }
   }

   public boolean write(String var1, int var2, ArrayList var3) {
      File var4 = new File(var1);
      DebugLog.log("writing " + var1);

      try {
         FileWriter var5 = new FileWriter(var4, false);
         Throwable var6 = null;

         try {
            if (var2 != 0) {
               var5.write("Version=" + var2 + System.lineSeparator());
            }

            for(int var7 = 0; var7 < var3.size(); ++var7) {
               ConfigOption var8 = (ConfigOption)var3.get(var7);
               var5.write(var8.getName() + "=" + var8.getValueAsString() + System.lineSeparator());
            }
         } catch (Throwable var17) {
            var6 = var17;
            throw var17;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var16) {
                     var6.addSuppressed(var16);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return true;
      } catch (Exception var19) {
         var19.printStackTrace();
         return false;
      }
   }

   public ArrayList getOptions() {
      return this.options;
   }

   public int getVersion() {
      return this.version;
   }
}
