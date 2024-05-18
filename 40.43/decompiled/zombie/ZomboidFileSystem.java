package zombie;

import gnu.trove.map.hash.THashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import zombie.core.Core;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class ZomboidFileSystem {
   public static ZomboidFileSystem instance = new ZomboidFileSystem();
   ArrayList loadList = new ArrayList();
   Map modDirList = new HashMap();
   private ArrayList modFolders;
   private ArrayList modFoldersOrder;
   public HashMap ActiveFileMap = new HashMap();
   THashMap RelativeMap = new THashMap();
   public boolean IgnoreActiveFileMap = false;
   public File base;
   public URI baseURI;
   public ArrayList mods = new ArrayList();
   private HashSet LoadedPacks = new HashSet();

   public void searchFolders(File var1) {
      if (!GameServer.bServer) {
         Thread.yield();
         Core.getInstance().DoFrameReady();
      }

      if (var1.isDirectory()) {
         String var2 = var1.getAbsolutePath().replace("\\", "/").replace("./", "");
         if (var2.contains("media/maps/")) {
            this.loadList.add(var2);
         }

         String[] var3 = var1.list();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            this.searchFolders(new File(var1.getAbsolutePath() + File.separator + var3[var4]));
         }
      } else {
         this.loadList.add(var1.getAbsolutePath().replace("\\", "/").replace("./", ""));
      }

   }

   public String getString(String var1) {
      if (this.IgnoreActiveFileMap) {
         return var1;
      } else {
         if (this.RelativeMap.containsKey(var1)) {
            var1 = (String)this.RelativeMap.get(var1);
         } else {
            String var2 = var1;
            var1 = this.getRelativeFile(var1);
            this.RelativeMap.put(var2, var1);
         }

         return this.ActiveFileMap.containsKey(var1) ? (String)this.ActiveFileMap.get(var1) : var1;
      }
   }

   public String getAbsolutePath(String var1) {
      return this.ActiveFileMap.containsKey(var1) ? (String)this.ActiveFileMap.get(var1) : null;
   }

   public void init() {
      this.base = (new File("./")).getAbsoluteFile();
      this.baseURI = this.base.toURI();
      File var1 = (new File("./media/")).getAbsoluteFile();
      this.searchFolders(var1);

      for(int var2 = 0; var2 < this.loadList.size(); ++var2) {
         String var3 = this.getRelativeFile((String)this.loadList.get(var2));
         this.ActiveFileMap.put(var3, (new File((String)this.loadList.get(var2))).getAbsolutePath());
      }

      this.loadList.clear();
   }

   public void Reset() {
      this.loadList.clear();
      this.ActiveFileMap.clear();
      this.modDirList.clear();
      this.mods.clear();
      this.modFolders = null;
   }

   public void resetModFolders() {
      this.modFolders = null;
   }

   public void getInstalledItemModsFolders(ArrayList var1) {
      if (SteamUtils.isSteamModeEnabled()) {
         String[] var2 = SteamWorkshop.instance.GetInstalledItemFolders();
         if (var2 != null) {
            String[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];
               File var7 = new File(var6 + File.separator + "mods");
               if (var7.exists()) {
                  var1.add(var7.getAbsolutePath());
               }
            }
         }
      }

   }

   public void getStagedItemModsFolders(ArrayList var1) {
      if (SteamUtils.isSteamModeEnabled()) {
         ArrayList var2 = SteamWorkshop.instance.getStageFolders();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            File var4 = new File((String)var2.get(var3) + File.separator + "Contents" + File.separator + "mods");
            if (var4.exists()) {
               var1.add(var4.getAbsolutePath());
            }
         }
      }

   }

   private void getAllModFoldersAux(String var1, List var2) {
      Filter var3 = new Filter() {
         public boolean accept(Path var1) throws IOException {
            return Files.isDirectory(var1, new LinkOption[0]) && Files.exists(var1.resolve("mod.info"), new LinkOption[0]);
         }
      };
      Path var4 = FileSystems.getDefault().getPath(var1);
      if (Files.exists(var4, new LinkOption[0])) {
         try {
            DirectoryStream var5 = Files.newDirectoryStream(var4, var3);
            Throwable var6 = null;

            try {
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  Path var8 = (Path)var7.next();
                  if (var8.getFileName().toString().toLowerCase().equals("examplemod")) {
                     System.out.println("MOD: refusing to list " + var8.getFileName());
                  } else {
                     var2.add(var8.toAbsolutePath().toString());
                  }
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
         } catch (Exception var19) {
            var19.printStackTrace();
         }

      }
   }

   public void setModFoldersOrder(String var1) {
      this.modFoldersOrder = new ArrayList(Arrays.asList(var1.split(",")));
   }

   public void getAllModFolders(List var1) {
      if (this.modFolders == null) {
         this.modFolders = new ArrayList();
         if (this.modFoldersOrder == null) {
            this.setModFoldersOrder("workshop,steam,mods");
         }

         ArrayList var2 = new ArrayList();

         int var3;
         for(var3 = 0; var3 < this.modFoldersOrder.size(); ++var3) {
            String var4 = (String)this.modFoldersOrder.get(var3);
            if ("workshop".equals(var4)) {
               this.getStagedItemModsFolders(var2);
            }

            if ("steam".equals(var4)) {
               this.getInstalledItemModsFolders(var2);
            }

            if ("mods".equals(var4)) {
               var2.add(Core.getMyDocumentFolder() + File.separator + "mods");
            }
         }

         for(var3 = 0; var3 < var2.size(); ++var3) {
            this.getAllModFoldersAux((String)var2.get(var3), this.modFolders);
         }
      }

      var1.clear();
      var1.addAll(this.modFolders);
   }

   public ArrayList getWorkshopItemMods(long var1) {
      ArrayList var3 = new ArrayList();
      if (!SteamUtils.isSteamModeEnabled()) {
         return var3;
      } else {
         String var4 = SteamWorkshop.instance.GetItemInstallFolder(var1);
         if (var4 == null) {
            return var3;
         } else {
            File var5 = new File(var4 + File.separator + "mods");
            if (var5.exists() && var5.isDirectory()) {
               File[] var6 = var5.listFiles();
               ChooseGameInfo var7 = new ChooseGameInfo();
               File[] var8 = var6;
               int var9 = var6.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  File var11 = var8[var10];
                  if (var11.isDirectory()) {
                     ChooseGameInfo.Mod var12 = var7.readModInfo(var11.getAbsolutePath());
                     if (var12 != null) {
                        var3.add(var12);
                     }
                  }
               }

               return var3;
            } else {
               return var3;
            }
         }
      }
   }

   public String searchForModInfo(File var1, String var2) {
      if (var1.isDirectory()) {
         String[] var3 = var1.list();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            String var5 = this.searchForModInfo(new File(var1.getAbsolutePath() + File.separator + var3[var4]), var2);
            if (var5 != null) {
               return var5;
            }
         }
      } else if (var1.getAbsolutePath().endsWith("mod.info")) {
         ChooseGameInfo var6 = new ChooseGameInfo();
         ChooseGameInfo.Mod var7 = var6.readModInfo(var1.getAbsoluteFile().getParent());
         if (var7 == null) {
            return null;
         }

         if (var7.getId() != null && !var7.getId().isEmpty()) {
            this.modDirList.put(var2, var7.getDir());
         }

         if (var7.getId().equals(var2)) {
            return var7.getDir();
         }
      }

      return null;
   }

   public void loadMod(String var1) {
      if (this.getModDir(var1) != null) {
         System.out.println("MOD: loading " + var1);
         File var2 = new File(this.getModDir(var1));
         URI var3 = var2.toURI();
         this.loadList.clear();
         this.searchFolders(var2);

         for(int var4 = 0; var4 < this.loadList.size(); ++var4) {
            String var5 = this.getRelativeFile(var3, (String)this.loadList.get(var4));
            if (this.ActiveFileMap.containsKey(var5) && !var5.endsWith("mod.info") && !var5.endsWith("poster.png")) {
               System.out.println("MOD: mod \"" + var1 + "\" overrides " + var5);
            }

            this.ActiveFileMap.put(var5, (new File((String)this.loadList.get(var4))).getAbsolutePath());
         }

         this.loadList.clear();
      }

   }

   private ArrayList readLoadedTxt() {
      ArrayList var1 = new ArrayList();
      String var2 = Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "loaded.txt";
      if (!(new File(var2)).exists()) {
         return var1;
      } else {
         try {
            FileReader var3 = new FileReader(var2);
            Throwable var4 = null;

            try {
               BufferedReader var5 = new BufferedReader(var3);
               Throwable var6 = null;

               try {
                  for(String var7 = var5.readLine(); var7 != null; var7 = var5.readLine()) {
                     var7 = var7.trim();
                     if (!var7.isEmpty()) {
                        var1.add(var7);
                     }
                  }
               } catch (Throwable var31) {
                  var6 = var31;
                  throw var31;
               } finally {
                  if (var5 != null) {
                     if (var6 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var30) {
                           var6.addSuppressed(var30);
                        }
                     } else {
                        var5.close();
                     }
                  }

               }
            } catch (Throwable var33) {
               var4 = var33;
               throw var33;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var29) {
                        var4.addSuppressed(var29);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Exception var35) {
            var35.printStackTrace();
         }

         return var1;
      }
   }

   public void loadMods() {
      Core.getInstance();
      if (Core.OptionModsEnabled) {
         if (GameClient.bClient) {
            this.loadTranslationMods();
            this.loadMods(GameClient.instance.ServerMods);
         } else {
            this.base = (new File("./")).getAbsoluteFile();

            try {
               ArrayList var1 = this.readLoadedTxt();
               this.loadMods(var1);
            } catch (Exception var2) {
               var2.printStackTrace();
            }

         }
      }
   }

   private boolean isTranslationMod(String var1) {
      ChooseGameInfo var2 = new ChooseGameInfo();
      ChooseGameInfo.Mod var3 = var2.getModDetails(var1);
      if (var3 == null) {
         return false;
      } else {
         boolean var4 = false;
         File var5 = new File(var3.getDir());
         URI var6 = var5.toURI();
         this.loadList.clear();
         this.searchFolders(var5);

         for(int var7 = 0; var7 < this.loadList.size(); ++var7) {
            String var8 = this.getRelativeFile(var6, (String)this.loadList.get(var7));
            if (var8.endsWith(".lua")) {
               return false;
            }

            if (var8.startsWith("media/maps/")) {
               return false;
            }

            if (var8.startsWith("media/scripts/")) {
               return false;
            }

            if (var8.startsWith("media/lua/")) {
               if (!var8.startsWith("media/lua/shared/Translate/")) {
                  return false;
               }

               var4 = true;
            }
         }

         this.loadList.clear();
         return var4;
      }
   }

   private void loadTranslationMods() {
      if (GameClient.bClient) {
         ArrayList var1 = this.readLoadedTxt();
         ArrayList var2 = new ArrayList();
         if (this.loadModsAux(var1, var2) == null) {
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               if (this.isTranslationMod(var4)) {
                  DebugLog.log("MOD: loading translation mod \"" + var4 + "\"");
                  this.loadMod(var4);
               }
            }
         }

      }
   }

   private String loadModAndRequired(String var1, ArrayList var2) {
      if (var1.isEmpty()) {
         return null;
      } else if (var1.toLowerCase().equals("examplemod")) {
         DebugLog.log("MOD: refusing to load " + var1);
         return null;
      } else if (var2.contains(var1)) {
         return null;
      } else {
         ChooseGameInfo var3 = new ChooseGameInfo();
         ChooseGameInfo.Mod var4 = var3.getModDetails(var1);
         if (var4 == null) {
            if (GameServer.bServer) {
               GameServer.ServerMods.remove(var1);
            }

            DebugLog.log("MOD: required mod \"" + var1 + "\" not found");
            return var1;
         } else {
            if (var4.getRequire() != null) {
               String var5 = this.loadModsAux(var4.getRequire(), var2);
               if (var5 != null) {
                  return var5;
               }
            }

            var2.add(var1);
            return null;
         }
      }
   }

   public String loadModsAux(ArrayList var1, ArrayList var2) {
      Iterator var3 = var1.iterator();

      String var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         String var4 = (String)var3.next();
         var5 = this.loadModAndRequired(var4, var2);
      } while(var5 == null);

      return var5;
   }

   public void loadMods(ArrayList var1) {
      this.mods.clear();
      Iterator var2 = var1.iterator();

      String var3;
      while(var2.hasNext()) {
         var3 = (String)var2.next();
         this.loadModAndRequired(var3, this.mods);
      }

      var2 = this.mods.iterator();

      while(var2.hasNext()) {
         var3 = (String)var2.next();
         this.loadMod(var3);
      }

   }

   public ArrayList getModIDs() {
      return this.mods;
   }

   public String getModDir(String var1) {
      return (String)this.modDirList.get(var1);
   }

   public String getRelativeFile(String var1) {
      return this.getRelativeFile(this.baseURI, var1);
   }

   public String getRelativeFile(URI var1, String var2) {
      URI var3 = (new File(var2)).toURI();
      URI var4 = var1.relativize(var3);
      if (!var4.equals(var3)) {
         return var2.endsWith("/") ? var4.getPath() + "/" : var4.getPath();
      } else {
         return var2;
      }
   }

   public void saveModsFile() {
      this.base = (new File("./")).getAbsoluteFile();

      try {
         File var1 = new File(Core.getMyDocumentFolder() + File.separator + "mods");
         if (!var1.exists()) {
            var1.mkdir();
         }

         BufferedWriter var2 = new BufferedWriter(new FileWriter(new File(Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "loaded.txt")));
         String var3 = "";

         for(int var4 = 0; var4 < this.mods.size(); ++var4) {
            var2.write((String)this.mods.get(var4));
            var2.newLine();
         }

         var2.close();
      } catch (FileNotFoundException var5) {
         var5.printStackTrace();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public void loadModPackFiles() {
      ChooseGameInfo var1 = new ChooseGameInfo();
      Iterator var2 = this.mods.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();

         try {
            ChooseGameInfo.Mod var4 = var1.getModDetails(var3);
            if (var4 != null) {
               Iterator var5 = var4.getPacks().iterator();

               while(var5.hasNext()) {
                  String var6 = (String)var5.next();
                  String var7 = this.getRelativeFile("media/texturepacks/" + var6 + ".pack");
                  if (!this.ActiveFileMap.containsKey(var7)) {
                     System.out.println("MOD: pack file \"" + var6 + "\" needed by " + var3 + " not found");
                  } else if (!this.LoadedPacks.contains(var6)) {
                     GameWindow.LoadTexturePack(var6);
                     this.LoadedPacks.add(var6);
                  }
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }

   }

   public void loadModTileDefs() {
      HashSet var1 = new HashSet();
      ChooseGameInfo var2 = new ChooseGameInfo();
      Iterator var3 = this.mods.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();

         try {
            ChooseGameInfo.Mod var5 = var2.getModDetails(var4);
            if (var5 != null) {
               Iterator var6 = var5.getTileDefs().iterator();

               while(var6.hasNext()) {
                  ChooseGameInfo.TileDef var7 = (ChooseGameInfo.TileDef)var6.next();
                  if (var1.contains(var7.fileNumber)) {
                     System.out.println("MOD: ERROR tiledef fileNumber " + var7.fileNumber + " used by more than one mod");
                  } else {
                     String var8 = var7.name;
                     String var9 = this.getRelativeFile("media/" + var8 + ".tiles");
                     if (!this.ActiveFileMap.containsKey(var9)) {
                        System.out.println("MOD: tiledef file \"" + var7.name + "\" needed by " + var4 + " not found");
                     } else {
                        var8 = (String)this.ActiveFileMap.get(var9);
                        IsoWorld.instance.LoadTileDefinitions(IsoWorld.instance.spriteManager, var8, var7.fileNumber);
                     }
                  }
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      }

   }

   public void loadModTileDefPropertyStrings() {
      HashSet var1 = new HashSet();
      ChooseGameInfo var2 = new ChooseGameInfo();
      Iterator var3 = this.mods.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();

         try {
            ChooseGameInfo.Mod var5 = var2.getModDetails(var4);
            if (var5 != null) {
               Iterator var6 = var5.getTileDefs().iterator();

               while(var6.hasNext()) {
                  ChooseGameInfo.TileDef var7 = (ChooseGameInfo.TileDef)var6.next();
                  if (var1.contains(var7.fileNumber)) {
                     System.out.println("MOD: ERROR tiledef fileNumber " + var7.fileNumber + " used by more than one mod");
                  } else {
                     String var8 = var7.name;
                     String var9 = this.getRelativeFile("media/" + var8 + ".tiles");
                     if (!this.ActiveFileMap.containsKey(var9)) {
                        System.out.println("MOD: tiledef file \"" + var7.name + "\" needed by " + var4 + " not found");
                     } else {
                        var8 = (String)this.ActiveFileMap.get(var9);
                        IsoWorld.instance.LoadTileDefinitionsPropertyStrings(IsoWorld.instance.spriteManager, var8, var7.fileNumber);
                     }
                  }
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      }

   }
}
