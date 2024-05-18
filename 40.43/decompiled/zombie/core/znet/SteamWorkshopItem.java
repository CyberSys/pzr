package zombie.core.znet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.textures.PNGDecoder;

public class SteamWorkshopItem {
   private String workshopFolder;
   private String PublishedFileId;
   private String title = "";
   private String description = "";
   private String visibility = "public";
   private ArrayList tags = new ArrayList();
   private String changeNote = "";
   private boolean bHasMod;
   private boolean bHasMap;
   private ArrayList modIDs = new ArrayList();
   private ArrayList mapFolders = new ArrayList();
   private static final int VERSION1 = 1;
   private static final int LATEST_VERSION = 1;

   public SteamWorkshopItem(String var1) {
      this.workshopFolder = var1;
   }

   public String getContentFolder() {
      return this.workshopFolder + File.separator + "Contents";
   }

   public String getFolderName() {
      return (new File(this.workshopFolder)).getName();
   }

   public void setID(String var1) {
      if (var1 != null && !SteamUtils.isValidSteamID(var1)) {
         var1 = null;
      }

      this.PublishedFileId = var1;
   }

   public String getID() {
      return this.PublishedFileId;
   }

   public void setTitle(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      this.title = var1;
   }

   public String getTitle() {
      return this.title;
   }

   public void setDescription(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      this.description = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public void setVisibility(String var1) {
      this.visibility = var1;
   }

   public String getVisibility() {
      return this.visibility;
   }

   public void setVisibilityInteger(int var1) {
      switch(var1) {
      case 0:
         this.visibility = "public";
         break;
      case 1:
         this.visibility = "friendsOnly";
         break;
      case 2:
         this.visibility = "private";
         break;
      default:
         this.visibility = "public";
      }

   }

   public int getVisibilityInteger() {
      if ("public".equals(this.visibility)) {
         return 0;
      } else if ("friendsOnly".equals(this.visibility)) {
         return 1;
      } else {
         return "private".equals(this.visibility) ? 2 : 0;
      }
   }

   public void setTags(ArrayList var1) {
      this.tags.clear();
      this.tags.addAll(var1);
   }

   public static ArrayList getAllowedTags() {
      ArrayList var0 = new ArrayList();
      return var0;
   }

   public ArrayList getTags() {
      return this.tags;
   }

   public String getSubmitDescription() {
      String var1 = this.getDescription();
      if (!var1.isEmpty()) {
         var1 = var1 + "\n\n";
      }

      var1 = var1 + "Workshop ID: " + this.getID();

      int var2;
      for(var2 = 0; var2 < this.modIDs.size(); ++var2) {
         var1 = var1 + "\nMod ID: " + (String)this.modIDs.get(var2);
      }

      for(var2 = 0; var2 < this.mapFolders.size(); ++var2) {
         var1 = var1 + "\nMap Folder: " + (String)this.mapFolders.get(var2);
      }

      return var1;
   }

   public String[] getSubmitTags() {
      ArrayList var1 = new ArrayList();
      var1.addAll(this.tags);
      if (this.bHasMod) {
         var1.add("Mod");
      }

      if (this.bHasMap) {
         var1.add("Map");
      }

      return (String[])var1.toArray(new String[var1.size()]);
   }

   public String getPreviewImage() {
      return this.workshopFolder + File.separator + "preview.png";
   }

   public void setChangeNote(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      this.changeNote = var1;
   }

   public String getChangeNote() {
      return this.changeNote;
   }

   public boolean create() {
      return SteamWorkshop.instance.CreateWorkshopItem(this);
   }

   public boolean submitUpdate() {
      return SteamWorkshop.instance.SubmitWorkshopItem(this);
   }

   public boolean getUpdateProgress(KahluaTable var1) {
      if (var1 == null) {
         throw new NullPointerException("table is null");
      } else {
         long[] var2 = new long[2];
         if (SteamWorkshop.instance.GetItemUpdateProgress(var2)) {
            System.out.println(var2[0] + "/" + var2[1]);
            var1.rawset("processed", (double)var2[0]);
            var1.rawset("total", (double)Math.max(var2[1], 1L));
            return true;
         } else {
            return false;
         }
      }
   }

   public int getUpdateProgressTotal() {
      return 1;
   }

   private String validateFileTypes(Path var1) {
      try {
         DirectoryStream var2 = Files.newDirectoryStream(var1);
         Throwable var3 = null;

         try {
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Path var5 = (Path)var4.next();
               String var6;
               String var7;
               if (Files.isDirectory(var5, new LinkOption[0])) {
                  var6 = this.validateFileTypes(var5);
                  if (var6 != null) {
                     var7 = var6;
                     return var7;
                  }
               } else {
                  var6 = var5.getFileName().toString();
                  if (var6.endsWith(".exe") || var6.endsWith(".dll") || var6.endsWith(".bat") || var6.endsWith(".app") || var6.endsWith(".dylib") || var6.endsWith(".sh") || var6.endsWith(".so") || var6.endsWith(".zip")) {
                     var7 = "FileTypeNotAllowed";
                     return var7;
                  }
               }
            }

            return null;
         } catch (Throwable var19) {
            var3 = var19;
            throw var19;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var18) {
                     var3.addSuppressed(var18);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (Exception var21) {
         var21.printStackTrace();
         return "IOError";
      }
   }

   private String validateModDotInfo(Path var1) {
      String var2 = null;

      try {
         FileReader var3 = new FileReader(var1.toFile());
         Throwable var4 = null;

         try {
            BufferedReader var5 = new BufferedReader(var3);
            Throwable var6 = null;

            try {
               String var7;
               try {
                  while((var7 = var5.readLine()) != null) {
                     if (var7.startsWith("id=")) {
                        var2 = var7.replace("id=", "").trim();
                        break;
                     }
                  }
               } catch (Throwable var33) {
                  var6 = var33;
                  throw var33;
               }
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var32) {
                        var6.addSuppressed(var32);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var35) {
            var4 = var35;
            throw var35;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var31) {
                     var4.addSuppressed(var31);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (FileNotFoundException var37) {
         return "MissingModDotInfo";
      } catch (IOException var38) {
         var38.printStackTrace();
         return "IOError";
      }

      if (var2 != null && !var2.isEmpty()) {
         this.modIDs.add(var2);
         return null;
      } else {
         return "InvalidModDotInfo";
      }
   }

   private String validateMapDotInfo(Path var1) {
      return null;
   }

   private String validateMapFolder(Path var1) {
      boolean var2 = false;

      try {
         DirectoryStream var3 = Files.newDirectoryStream(var1);
         Throwable var4 = null;

         try {
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Path var6 = (Path)var5.next();
               if (!Files.isDirectory(var6, new LinkOption[0]) && "map.info".equals(var6.getFileName().toString())) {
                  String var7 = this.validateMapDotInfo(var6);
                  if (var7 != null) {
                     String var8 = var7;
                     return var8;
                  }

                  var2 = true;
               }
            }
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Exception var21) {
         var21.printStackTrace();
         return "IOError";
      }

      if (!var2) {
         return "MissingMapDotInfo";
      } else {
         this.mapFolders.add(var1.getFileName().toString());
         return null;
      }
   }

   private String validateMapsFolder(Path var1) {
      boolean var2 = false;

      try {
         DirectoryStream var3 = Files.newDirectoryStream(var1);
         Throwable var4 = null;

         try {
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Path var6 = (Path)var5.next();
               if (Files.isDirectory(var6, new LinkOption[0])) {
                  String var7 = this.validateMapFolder(var6);
                  if (var7 != null) {
                     String var8 = var7;
                     return var8;
                  }

                  var2 = true;
               }
            }
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Exception var21) {
         var21.printStackTrace();
         return "IOError";
      }

      if (!var2) {
         return null;
      } else {
         this.bHasMap = true;
         return null;
      }
   }

   private String validateMediaFolder(Path var1) {
      try {
         DirectoryStream var2 = Files.newDirectoryStream(var1);
         Throwable var3 = null;

         try {
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Path var5 = (Path)var4.next();
               if (Files.isDirectory(var5, new LinkOption[0]) && "maps".equals(var5.getFileName().toString())) {
                  String var6 = this.validateMapsFolder(var5);
                  if (var6 != null) {
                     String var7 = var6;
                     return var7;
                  }
               }
            }

            return null;
         } catch (Throwable var18) {
            var3 = var18;
            throw var18;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (Exception var20) {
         var20.printStackTrace();
         return "IOError";
      }
   }

   private String validateModFolder(Path var1) {
      boolean var2 = false;

      try {
         DirectoryStream var3 = Files.newDirectoryStream(var1);
         Throwable var4 = null;

         try {
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Path var6 = (Path)var5.next();
               String var7;
               String var8;
               if (Files.isDirectory(var6, new LinkOption[0])) {
                  if ("media".equals(var6.getFileName().toString())) {
                     var7 = this.validateMediaFolder(var6);
                     if (var7 != null) {
                        var8 = var7;
                        return var8;
                     }
                  }
               } else if ("mod.info".equals(var6.getFileName().toString())) {
                  var7 = this.validateModDotInfo(var6);
                  if (var7 != null) {
                     var8 = var7;
                     return var8;
                  }

                  var2 = true;
               }
            }

            return !var2 ? "MissingModDotInfo" : null;
         } catch (Throwable var20) {
            var4 = var20;
            throw var20;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var19) {
                     var4.addSuppressed(var19);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Exception var22) {
         var22.printStackTrace();
         return "IOError";
      }
   }

   private String validateModsFolder(Path var1) {
      boolean var2 = false;

      try {
         DirectoryStream var3 = Files.newDirectoryStream(var1);
         Throwable var4 = null;

         try {
            for(Iterator var5 = var3.iterator(); var5.hasNext(); var2 = true) {
               Path var6 = (Path)var5.next();
               String var7;
               if (!Files.isDirectory(var6, new LinkOption[0])) {
                  var7 = "FileNotAllowedInMods";
                  return var7;
               }

               var7 = this.validateModFolder(var6);
               if (var7 != null) {
                  String var8 = var7;
                  return var8;
               }
            }
         } catch (Throwable var20) {
            var4 = var20;
            throw var20;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var19) {
                     var4.addSuppressed(var19);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Exception var22) {
         var22.printStackTrace();
         return "IOError";
      }

      if (!var2) {
         return "EmptyModsFolder";
      } else {
         this.bHasMod = true;
         return null;
      }
   }

   private String validateBuildingsFolder(Path var1) {
      return null;
   }

   private String validateCreativeFolder(Path var1) {
      return null;
   }

   public String validatePreviewImage(Path var1) throws IOException {
      if (Files.exists(var1, new LinkOption[0]) && Files.isReadable(var1) && !Files.isDirectory(var1, new LinkOption[0])) {
         if (Files.size(var1) > 1024000L) {
            return "PreviewFileSize";
         } else {
            try {
               FileInputStream var2 = new FileInputStream(var1.toFile());
               Throwable var3 = null;

               String var7;
               try {
                  BufferedInputStream var4 = new BufferedInputStream(var2);
                  Throwable var5 = null;

                  try {
                     PNGDecoder var6 = new PNGDecoder(var4, false);
                     if (var6.getWidth() == 256 && var6.getHeight() == 256) {
                        return null;
                     }

                     var7 = "PreviewDimensions";
                  } catch (Throwable var35) {
                     var5 = var35;
                     throw var35;
                  } finally {
                     if (var4 != null) {
                        if (var5 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var34) {
                              var5.addSuppressed(var34);
                           }
                        } else {
                           var4.close();
                        }
                     }

                  }
               } catch (Throwable var37) {
                  var3 = var37;
                  throw var37;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var33) {
                           var3.addSuppressed(var33);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               return var7;
            } catch (IOException var39) {
               var39.printStackTrace();
               return "PreviewFormat";
            }
         }
      } else {
         return "PreviewNotFound";
      }
   }

   public String validateContents() {
      this.bHasMod = false;
      this.bHasMap = false;
      this.modIDs.clear();
      this.mapFolders.clear();

      try {
         Path var1 = FileSystems.getDefault().getPath(this.getContentFolder());
         if (!Files.isDirectory(var1, new LinkOption[0])) {
            return "MissingContents";
         } else {
            Path var2 = FileSystems.getDefault().getPath(this.getPreviewImage());
            String var3 = this.validatePreviewImage(var2);
            if (var3 != null) {
               return var3;
            } else {
               boolean var4 = false;

               try {
                  DirectoryStream var5 = Files.newDirectoryStream(var1);
                  Throwable var6 = null;

                  try {
                     for(Iterator var7 = var5.iterator(); var7.hasNext(); var4 = true) {
                        Path var8 = (Path)var7.next();
                        String var9;
                        if (!Files.isDirectory(var8, new LinkOption[0])) {
                           var9 = "FileNotAllowedInContents";
                           return var9;
                        }

                        if ("buildings".equals(var8.getFileName().toString())) {
                           var3 = this.validateBuildingsFolder(var8);
                           if (var3 != null) {
                              var9 = var3;
                              return var9;
                           }
                        } else if ("creative".equals(var8.getFileName().toString())) {
                           var3 = this.validateCreativeFolder(var8);
                           if (var3 != null) {
                              var9 = var3;
                              return var9;
                           }
                        } else {
                           if (!"mods".equals(var8.getFileName().toString())) {
                              var9 = "FolderNotAllowedInContents";
                              return var9;
                           }

                           var3 = this.validateModsFolder(var8);
                           if (var3 != null) {
                              var9 = var3;
                              return var9;
                           }
                        }
                     }

                     return !var4 ? "EmptyContentsFolder" : this.validateFileTypes(var1);
                  } catch (Throwable var25) {
                     var6 = var25;
                     throw var25;
                  } finally {
                     if (var5 != null) {
                        if (var6 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var24) {
                              var6.addSuppressed(var24);
                           }
                        } else {
                           var5.close();
                        }
                     }

                  }
               } catch (Exception var27) {
                  var27.printStackTrace();
                  return "IOError";
               }
            }
         }
      } catch (IOException var28) {
         var28.printStackTrace();
         return "IOError";
      }
   }

   public String getExtendedErrorInfo(String var1) {
      if ("FolderNotAllowedInContents".equals(var1)) {
         return "buildings/ creative/ mods/";
      } else {
         return "FileTypeNotAllowed".equals(var1) ? "*.exe *.dll *.bat *.app *.dylib *.sh *.so *.zip" : "";
      }
   }

   public boolean readWorkshopTxt() {
      String var1 = this.workshopFolder + File.separator + "workshop.txt";
      if (!(new File(var1)).exists()) {
         return true;
      } else {
         try {
            FileReader var2 = new FileReader(var1);
            Throwable var3 = null;

            boolean var37;
            try {
               BufferedReader var4 = new BufferedReader(var2);
               Throwable var5 = null;

               try {
                  String var6;
                  while((var6 = var4.readLine()) != null) {
                     var6 = var6.trim();
                     if (!var6.isEmpty() && !var6.startsWith("#") && !var6.startsWith("//")) {
                        if (var6.startsWith("id=")) {
                           String var7 = var6.replace("id=", "");
                           this.setID(var7);
                        } else if (var6.startsWith("description=")) {
                           if (!this.description.isEmpty()) {
                              this.description = this.description + "\n";
                           }

                           this.description = this.description + var6.replace("description=", "");
                        } else if (var6.startsWith("tags=")) {
                           this.tags.addAll(Arrays.asList(var6.replace("tags=", "").split(";")));
                        } else if (var6.startsWith("title=")) {
                           this.title = var6.replace("title=", "");
                        } else if (!var6.startsWith("version=") && var6.startsWith("visibility=")) {
                           this.visibility = var6.replace("visibility=", "");
                        }
                     }
                  }

                  var37 = true;
               } catch (Throwable var32) {
                  var5 = var32;
                  throw var32;
               } finally {
                  if (var4 != null) {
                     if (var5 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var31) {
                           var5.addSuppressed(var31);
                        }
                     } else {
                        var4.close();
                     }
                  }

               }
            } catch (Throwable var34) {
               var3 = var34;
               throw var34;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var30) {
                        var3.addSuppressed(var30);
                     }
                  } else {
                     var2.close();
                  }
               }

            }

            return var37;
         } catch (IOException var36) {
            var36.printStackTrace();
            return false;
         }
      }
   }

   public boolean writeWorkshopTxt() {
      String var1 = this.workshopFolder + File.separator + "workshop.txt";
      File var2 = new File(var1);

      try {
         FileWriter var3 = new FileWriter(var2);
         BufferedWriter var4 = new BufferedWriter(var3);
         var4.write("version=1");
         var4.newLine();
         var4.write("id=" + (this.PublishedFileId == null ? "" : this.PublishedFileId));
         var4.newLine();
         var4.write("title=" + this.title);
         var4.newLine();
         String[] var5 = this.description.split("\n");
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            var4.write("description=" + var8);
            var4.newLine();
         }

         String var10 = "";

         for(var6 = 0; var6 < this.tags.size(); ++var6) {
            var10 = var10 + (String)this.tags.get(var6);
            if (var6 < this.tags.size() - 1) {
               var10 = var10 + ";";
            }
         }

         var4.write("tags=" + var10);
         var4.newLine();
         var4.write("visibility=" + this.visibility);
         var4.newLine();
         var4.close();
         return true;
      } catch (IOException var9) {
         var9.printStackTrace();
         return false;
      }
   }

   public static enum ItemState {
      None(0),
      Subscribed(1),
      LegacyItem(2),
      Installed(4),
      NeedsUpdate(8),
      Downloading(16),
      DownloadPending(32);

      private final int value;

      private ItemState(int var3) {
         this.value = var3;
      }

      public int getValue() {
         return this.value;
      }

      public boolean and(SteamWorkshopItem.ItemState var1) {
         return (this.value & var1.value) != 0;
      }

      public boolean and(long var1) {
         return ((long)this.value & var1) != 0L;
      }

      public static String toString(long var0) {
         if (var0 == (long)None.getValue()) {
            return "None";
         } else {
            StringBuilder var2 = new StringBuilder();
            SteamWorkshopItem.ItemState[] var3 = values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               SteamWorkshopItem.ItemState var6 = var3[var5];
               if (var6 != None && var6.and(var0)) {
                  if (var2.length() > 0) {
                     var2.append("|");
                  }

                  var2.append(var6.name());
               }
            }

            return var2.toString();
         }
      }
   }
}
