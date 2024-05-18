package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Translator;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.SteamWorkshop;
import zombie.scripting.ScriptManager;

public class ChooseGameInfo extends GameState {
   Stack ModDetails = new Stack();
   Stack Mods = new Stack();
   Stack Stories = new Stack();
   Stack StoryDetails = new Stack();
   public int SelectedStory = 0;
   static HashMap Maps = new HashMap();

   public static void Reset() {
      Maps.clear();
   }

   public ChooseGameInfo.Mod getModDetails(String var1) {
      String var2 = ZomboidFileSystem.instance.getModDir(var1);
      if (var2 == null) {
         ArrayList var3 = new ArrayList();
         ZomboidFileSystem.instance.getAllModFolders(var3);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            File var5 = new File((String)var3.get(var4));
            var2 = ZomboidFileSystem.instance.searchForModInfo(var5, var1);
            if (var2 != null) {
               break;
            }
         }
      }

      return this.readModInfo(var2);
   }

   public ChooseGameInfo.Mod readModInfo(String var1) {
      if (var1 != null) {
         String var2 = var1 + File.separator + "mod.info";
         File var3 = new File(var2);
         if (!var3.exists()) {
            System.out.println("MOD: can't find \"" + var2 + "\"");
            return null;
         } else {
            ChooseGameInfo.Mod var4 = new ChooseGameInfo.Mod(var1);
            var4.setId(var3.getParentFile().getName());
            InputStreamReader var5 = null;

            try {
               var5 = IndieFileLoader.getStreamReader(var2);
            } catch (FileNotFoundException var27) {
               var27.printStackTrace();
            }

            String var6 = null;
            BufferedReader var7 = new BufferedReader(var5);

            while(true) {
               try {
                  while((var6 = var7.readLine()) != null) {
                     if (var6.contains("name=")) {
                        var4.name = var6.replace("name=", "");
                     } else if (var6.contains("poster=")) {
                        Texture var32 = Texture.getSharedTexture(var1 + File.separator + var6.replace("poster=", ""));
                        if (Core.bDebug && var32 == null) {
                           System.out.println("MOD: failed to load poster " + var1 + File.separator + var6.replace("poster=", ""));
                        }

                        var4.tex = var32;
                     } else if (var6.contains("description=")) {
                        var4.desc = var6.replace("description=", "");
                     } else if (var6.contains("require=")) {
                        var4.setRequire(new ArrayList(Arrays.asList(var6.replace("require=", "").split(","))));
                     } else if (!var6.contains("id=")) {
                        if (var6.contains("url=")) {
                           var4.setUrl(var6.replace("url=", ""));
                        } else {
                           File var9;
                           if (!var6.contains("pack=")) {
                              if (var6.contains("tiledef=")) {
                                 String[] var31 = var6.replace("tiledef=", "").trim().split("\\s+");
                                 if (var31.length != 2) {
                                    System.out.println("MOD: tiledef= line requires file name and file number");
                                    var9 = null;
                                    return var9;
                                 }

                                 String var34 = var31[0];

                                 int var10;
                                 try {
                                    var10 = Integer.parseInt(var31[1]);
                                 } catch (NumberFormatException var28) {
                                    System.out.println("MOD: tiledef= line requires file name and file number");
                                    Object var12 = null;
                                    return (ChooseGameInfo.Mod)var12;
                                 }

                                 if (var10 < 100 || var10 > 1000) {
                                    System.out.println("MOD: tiledef= file number must be from 100 to 1000");
                                 }

                                 var4.addTileDef(var34, var10);
                              }
                           } else {
                              String var8 = var6.replace("pack=", "").trim();
                              if (var8.isEmpty()) {
                                 System.out.println("MOD: pack= line requires a file name");
                                 var9 = null;
                                 return var9;
                              }

                              if (Core.TileScale == 2) {
                                 System.out.println("MOD: Looking for 2x texture packs");
                                 var9 = new File(var1 + File.separator + "media" + File.separator + "texturepacks" + File.separator + var8 + "2x.pack");
                                 if (var9.isFile()) {
                                    var8 = var8 + "2x";
                                 } else {
                                    System.out.println("MOD: 2x version of " + var8 + " not found. Loading default.");
                                 }
                              }

                              var4.addPack(var8);
                           }
                        }
                     } else {
                        var4.setId(var6.replace("id=", ""));
                     }
                  }

                  if (var4.getTexture() == null) {
                     var4.setTexture(Texture.getSharedTexture("media/ui/white.png"));
                  }

                  if (var4.getUrl() == null) {
                     var4.setUrl("");
                  }

                  ChooseGameInfo.Mod var33 = var4;
                  return var33;
               } catch (IOException var29) {
                  Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var29);
                  return null;
               } finally {
                  try {
                     var7.close();
                  } catch (Exception var26) {
                  }

               }
            }
         }
      } else {
         return null;
      }
   }

   public void getStoryDetails(ChooseGameInfo.Story var1, String var2) throws FileNotFoundException {
      InputStreamReader var3 = IndieFileLoader.getStreamReader("media/stories/" + var2 + "/story.info");
      String var4 = null;
      BufferedReader var5 = new BufferedReader(var3);

      try {
         while((var4 = var5.readLine()) != null) {
            String var6;
            if (var4.contains("name=")) {
               var6 = var4.replace("name=", "");
               var1.name = var6;
            } else if (var4.contains("poster=")) {
               var6 = var4.replace("poster=", "");
               Texture var7 = Texture.getSharedTexture("media/stories/" + var2 + "/" + var6);
               var1.tex = var7;
            } else if (var4.contains("description=")) {
               var6 = var4.replace("description=", "");
               var1.desc = var6;
            } else if (var4.contains("map=")) {
               var6 = var4.replace("map=", "");
               var1.map = var6;
            }
         }
      } catch (IOException var8) {
         Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var8);
      }

   }

   public static ChooseGameInfo.Map getMapDetails(String var0) {
      if (Maps.containsKey(var0)) {
         return (ChooseGameInfo.Map)Maps.get(var0);
      } else {
         File var1 = new File(ZomboidFileSystem.instance.getString("media/maps/" + var0 + "/map.info"));
         if (!var1.exists()) {
            return null;
         } else {
            ChooseGameInfo.Map var2 = new ChooseGameInfo.Map();
            var2.dir = (new File(var1.getParent())).getAbsolutePath();
            var2.title = var0;
            var2.lotsDir = new ArrayList();

            try {
               FileReader var3 = new FileReader(var1.getAbsolutePath());
               BufferedReader var4 = new BufferedReader(var3);
               String var5 = null;

               try {
                  while((var5 = var4.readLine()) != null) {
                     var5 = var5.trim();
                     if (var5.startsWith("title=")) {
                        var2.title = var5.replace("title=", "");
                     } else if (var5.startsWith("lots=")) {
                        var2.lotsDir.add(var5.replace("lots=", "").trim());
                     } else if (var5.startsWith("description=")) {
                        if (var2.desc == null) {
                           var2.desc = "";
                        }

                        var2.desc = var2.desc + var5.replace("description=", "");
                     } else if (var5.startsWith("fixed2x=")) {
                        var2.bFixed2x = Boolean.parseBoolean(var5.replace("fixed2x=", "").trim());
                     }
                  }
               } catch (IOException var8) {
                  Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var8);
               }

               var4.close();
               var2.thumb = Texture.getSharedTexture(var2.dir + "/thumb.png");
               if (Translator.getLanguage() != Translator.getDefaultLanguage()) {
                  var1 = new File(ZomboidFileSystem.instance.getString("media/lua/shared/Translate/" + Translator.getLanguage().toString() + "/" + var0 + "/description.txt"));
                  if (var1 != null && var1.exists()) {
                     var2.desc = "";
                     var4 = new BufferedReader(new InputStreamReader(new FileInputStream(var1), Charset.forName(Translator.getLanguage().charset())));
                     String var6 = null;

                     while((var6 = var4.readLine()) != null) {
                        var2.desc = var2.desc + var6;
                     }
                  }
               }
            } catch (Exception var9) {
               var9.printStackTrace();
               return null;
            }

            Maps.put(var0, var2);
            return var2;
         }
      }
   }

   public void enter() {
      this.getStoryList();
   }

   public void getStoryList() {
      TextureID.UseFiltering = true;
      Texture.getSharedTexture("media/ui/blank.png");
      this.StoryDetails.clear();

      try {
         this.Stories = ScriptManager.instance.getStoryList();
      } catch (IOException var6) {
         Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var6);
      } catch (URISyntaxException var7) {
         Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var7);
      }

      for(int var1 = 0; var1 < this.Stories.size(); ++var1) {
         String var2 = (String)this.Stories.get(var1);
         if (!var2.contains("Sandbox")) {
            Core.storyDirectory = "mods/";
            ChooseGameInfo.Story var3 = new ChooseGameInfo.Story(var2);

            try {
               this.getStoryDetails(var3, var2);
            } catch (FileNotFoundException var5) {
               Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var5);
            }

            this.StoryDetails.add(var3);
         }
      }

      TextureID.UseFiltering = false;
   }

   public static void DrawTexture(Texture var0, int var1, int var2, int var3, int var4, float var5) {
      var0.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var5);
   }

   public static class Map {
      private String dir;
      private Texture thumb;
      private String title;
      private ArrayList lotsDir;
      private String desc;
      private boolean bFixed2x;

      public void setDirectory(String var1) {
         this.dir = var1;
      }

      public String getDirectory() {
         return this.dir;
      }

      public void setThumbnail(Texture var1) {
         this.thumb = var1;
      }

      public Texture getThumbnail() {
         return this.thumb;
      }

      public void setTitle(String var1) {
         this.title = var1;
      }

      public String getTitle() {
         return this.title;
      }

      public ArrayList getLotDirectories() {
         return this.lotsDir;
      }

      public void setDescription(String var1) {
         this.desc = var1;
      }

      public String getDescription() {
         return this.desc;
      }

      public void setFixed2x(boolean var1) {
         this.bFixed2x = var1;
      }

      public boolean isFixed2x() {
         return this.bFixed2x;
      }
   }

   public class Mod {
      private ArrayList require;
      public String dir;
      public Texture tex;
      private String name = "Unnamed Mod";
      private String desc = "An adventure by someone or other.";
      private String id;
      private String url;
      private String workshopID;
      private boolean available = true;
      private ArrayList packs = new ArrayList();
      private ArrayList tileDefs = new ArrayList();

      public Mod(String var2) {
         this.dir = var2;
         File var3 = new File(var2);
         var3 = var3.getParentFile();
         if (var3 != null) {
            var3 = var3.getParentFile();
            if (var3 != null) {
               this.workshopID = SteamWorkshop.instance.getIDFromItemInstallFolder(var3.getAbsolutePath());
            }
         }

      }

      public Texture getTexture() {
         return this.tex;
      }

      public void setTexture(Texture var1) {
         this.tex = var1;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public String getDir() {
         return this.dir;
      }

      public String getDescription() {
         return this.desc;
      }

      public ArrayList getRequire() {
         return this.require;
      }

      public void setRequire(ArrayList var1) {
         this.require = var1;
      }

      public String getId() {
         return this.id;
      }

      public void setId(String var1) {
         this.id = var1;
      }

      public boolean isAvailable() {
         return this.available;
      }

      public void setAvailable(boolean var1) {
         this.available = var1;
      }

      public String getUrl() {
         return this.url == null ? "" : this.url;
      }

      public void setUrl(String var1) {
         if (var1.startsWith("http://theindiestone.com") || var1.startsWith("http://www.theindiestone.com") || var1.startsWith("http://pz-mods.net") || var1.startsWith("http://www.pz-mods.net")) {
            this.url = var1;
         }

      }

      public void addPack(String var1) {
         this.packs.add(var1);
      }

      public void addTileDef(String var1, int var2) {
         this.tileDefs.add(ChooseGameInfo.this.new TileDef(var1, var2));
      }

      public ArrayList getPacks() {
         return this.packs;
      }

      public ArrayList getTileDefs() {
         return this.tileDefs;
      }

      public String getWorkshopID() {
         return this.workshopID;
      }
   }

   public class TileDef {
      public String name;
      public int fileNumber;

      public TileDef(String var2, int var3) {
         this.name = var2;
         this.fileNumber = var3;
      }
   }

   public class Story {
      public String dir;
      public Texture tex;
      private String name = "Unnamed Story";
      private String desc = "An adventure by someone or other.";
      private String map = "Muldraugh, KY";

      public Story(String var2) {
         this.tex = this.tex;
         this.dir = var2;
      }

      public Texture getTexture() {
         return this.tex;
      }

      public String getName() {
         return this.name;
      }

      public String getDescription() {
         return this.desc;
      }

      public String getMap() {
         return this.map;
      }
   }
}
