package zombie.core.logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.network.MD5Checksum;

public class ZipLogs {
   static ArrayList filePaths = new ArrayList();

   public static void addZipFile(boolean var0) {
      FileSystem var1 = null;

      try {
         String var2 = GameWindow.getCacheDir() + File.separator + "logs.zip";
         String var3 = (new File(var2)).toURI().toString();
         URI var4 = URI.create("jar:" + var3);
         Path var5 = FileSystems.getDefault().getPath(var2).toAbsolutePath();
         HashMap var6 = new HashMap();
         var6.put("create", String.valueOf(Files.notExists(var5, new LinkOption[0])));

         try {
            var1 = FileSystems.newFileSystem(var4, var6);
         } catch (IOException var16) {
            var16.printStackTrace();
            return;
         }

         long var7 = getMD5FromZip(var1, "/meta/console.txt.md5");
         long var9 = getMD5FromZip(var1, "/meta/coop-console.txt.md5");
         long var11 = getMD5FromZip(var1, "/meta/server-console.txt.md5");
         addLogToZip(var1, "console", "console.txt", var7);
         addLogToZip(var1, "coop-console", "coop-console.txt", var9);
         addLogToZip(var1, "server-console", "server-console.txt", var11);
         addToZip(var1, "/configs/options.ini", "options.ini");
         addToZip(var1, "/configs/popman-options.ini", "popman-options.ini");
         addToZip(var1, "/configs/latestSave.ini", "latestSave.ini");
         addToZip(var1, "/configs/debug-options.ini", "debug-options.ini");
         addToZip(var1, "/configs/sounds.ini", "sounds.ini");
         addToZip(var1, "/addition/translationProblems.txt", "translationProblems.txt");
         addToZip(var1, "/addition/gamepadBinding.config", "gamepadBinding.config");
         addFilelistToZip(var1, "/addition/mods.txt", "mods");
         addDirToZipLua(var1, "/lua", "Lua");
         addDirToZip(var1, "/db", "db");
         addDirToZip(var1, "/server", "Server");
         if (var0) {
            addSaveToZip(var1, "/save/map_t.bin", "map_t.bin");
            addSaveToZip(var1, "/save/map_ver.bin", "map_ver.bin");
            addSaveToZip(var1, "/save/map.bin", "map.bin");
            addSaveToZip(var1, "/save/map_sand.bin", "map_sand.bin");
            addSaveToZip(var1, "/save/reanimated.bin", "reanimated.bin");
            addSaveToZip(var1, "/save/zombies.ini", "zombies.ini");
            addSaveToZip(var1, "/save/descriptors.bin", "descriptors.bin");
            addSaveToZip(var1, "/save/map_p.bin", "map_p.bin");
            addSaveToZip(var1, "/save/map_meta.bin", "map_meta.bin");
            addSaveToZip(var1, "/save/map_zone.bin", "map_zone.bin");
            addSaveToZip(var1, "/save/serverid.dat", "serverid.dat");
            addSaveToZip(var1, "/save/thumb.png", "thumb.png");
         }

         try {
            var1.close();
         } catch (IOException var15) {
            var15.printStackTrace();
         }
      } catch (Exception var17) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var14) {
               var14.printStackTrace();
            }
         }

         var17.printStackTrace();
      }

   }

   private static void copyToZip(Path var0, Path var1, Path var2) throws IOException {
      Path var3 = var0.resolve(var1.relativize(var2).toString());
      if (Files.isDirectory(var2, new LinkOption[0])) {
         Files.createDirectories(var3);
      } else {
         Files.copy(var2, var3);
      }

   }

   private static void addToZip(FileSystem var0, String var1, String var2) {
      try {
         Path var3 = var0.getPath(var1);
         Files.createDirectories(var3.getParent());
         Path var4 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + var2).toAbsolutePath();
         Files.deleteIfExists(var3);
         if (Files.exists(var4, new LinkOption[0])) {
            Files.copy(var4, var3, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   private static void addSaveToZip(FileSystem var0, String var1, String var2) {
      try {
         Path var3 = var0.getPath(var1);
         Files.createDirectories(var3.getParent());
         Path var4 = FileSystems.getDefault().getPath(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + var2).toAbsolutePath();
         Files.deleteIfExists(var3);
         if (Files.exists(var4, new LinkOption[0])) {
            Files.copy(var4, var3, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   private static void addDirToZip(FileSystem var0, String var1, String var2) {
      try {
         Path var3 = var0.getPath(var1);
         deleteDirectory(var0, var3);
         Files.createDirectories(var3);
         Path var4 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + var2).toAbsolutePath();
         Stream var5 = Files.walk(var4);
         var5.forEach((var2x) -> {
            try {
               copyToZip(var3, var4, var2x);
            } catch (IOException var4x) {
               throw new RuntimeException(var4x);
            }
         });
      } catch (IOException var6) {
      }

   }

   private static void addDirToZipLua(FileSystem var0, String var1, String var2) {
      try {
         Path var3 = var0.getPath(var1);
         deleteDirectory(var0, var3);
         Files.createDirectories(var3);
         Path var4 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + var2).toAbsolutePath();
         Stream var5 = Files.walk(var4);
         var5.forEach((var2x) -> {
            try {
               if (!var2x.endsWith("ServerList.txt") && !var2x.endsWith("ServerListSteam.txt")) {
                  copyToZip(var3, var4, var2x);
               }

            } catch (IOException var4x) {
               throw new RuntimeException(var4x);
            }
         });
      } catch (IOException var6) {
      }

   }

   private static void addFilelistToZip(FileSystem var0, String var1, String var2) {
      try {
         Path var3 = var0.getPath(var1);
         Path var4 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + var2).toAbsolutePath();
         Stream var5 = Files.list(var4);
         String var6 = (String)var5.map(Path::getFileName).map(Path::toString).collect(Collectors.joining("; "));
         Files.deleteIfExists(var3);
         Files.write(var3, var6.getBytes(), new OpenOption[0]);
      } catch (IOException var7) {
      }

   }

   static void deleteDirectory(FileSystem var0, Path var1) {
      filePaths.clear();
      getDirectoryFiles(var1);
      Iterator var2 = filePaths.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();

         try {
            Files.delete(var0.getPath(var3));
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

   }

   static void getDirectoryFiles(Path var0) {
      try {
         Stream var1 = Files.walk(var0);
         var1.forEach((var1x) -> {
            if (!var1x.toString().equals(var0.toString())) {
               if (Files.isDirectory(var1x, new LinkOption[0])) {
                  getDirectoryFiles(var1x);
               } else if (!filePaths.contains(var1x.toString())) {
                  filePaths.add(var1x.toString());
               }
            }

         });
         filePaths.add(var0.toString());
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   private static void addLogToZip(FileSystem var0, String var1, String var2, long var3) {
      long var5;
      try {
         var5 = MD5Checksum.createChecksum(GameWindow.getCacheDir() + File.separator + var2);
      } catch (Exception var16) {
         var5 = 0L;
      }

      File var7 = new File(GameWindow.getCacheDir() + File.separator + var2);
      if (var7.exists() && !var7.isDirectory() && var5 != var3) {
         Path var8;
         try {
            var8 = var0.getPath("/" + var1 + "/log_5.txt");
            Files.delete(var8);
         } catch (Exception var15) {
         }

         Path var9;
         Path var10;
         for(int var17 = 5; var17 > 0; --var17) {
            var9 = var0.getPath("/" + var1 + "/log_" + var17 + ".txt");
            var10 = var0.getPath("/" + var1 + "/log_" + (var17 + 1) + ".txt");

            try {
               Files.move(var9, var10);
            } catch (Exception var14) {
            }
         }

         try {
            var8 = var0.getPath("/" + var1 + "/log_1.txt");
            Files.createDirectories(var8.getParent());
            var9 = FileSystems.getDefault().getPath(GameWindow.getCacheDir() + File.separator + var2).toAbsolutePath();
            Files.copy(var9, var8, StandardCopyOption.REPLACE_EXISTING);
            var10 = var0.getPath("/meta/" + var2 + ".md5");
            Files.createDirectories(var10.getParent());

            try {
               Files.delete(var10);
            } catch (Exception var12) {
            }

            Files.write(var10, String.valueOf(var5).getBytes(), new OpenOption[0]);
         } catch (Exception var13) {
            var13.printStackTrace();
         }
      }

   }

   private static long getMD5FromZip(FileSystem var0, String var1) {
      long var2 = 0L;

      try {
         Path var4 = var0.getPath(var1);
         if (Files.exists(var4, new LinkOption[0])) {
            List var5 = Files.readAllLines(var4);
            var2 = Long.parseLong((String)var5.get(0));
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return var2;
   }
}
