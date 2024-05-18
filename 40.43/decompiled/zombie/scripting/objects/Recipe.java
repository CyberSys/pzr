package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import zombie.characters.skills.PerkFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;

public class Recipe extends BaseScriptObject {
   private boolean canBeDoneFromFloor = false;
   public float TimeToMake = 0.0F;
   public String Sound;
   public ArrayList Source = new ArrayList();
   public Recipe.Result Result = null;
   public String LuaTest = null;
   public String LuaCreate = null;
   public String LuaGrab = null;
   public String name = "recipe";
   private String originalname;
   private String nearItem;
   public Map skillRequired = null;
   public String LuaGiveXP;
   private boolean needToBeLearn = false;
   private String category = null;
   private boolean removeResultItem = false;
   private float heat = 0.0F;
   private boolean noBrokenItems = false;

   public boolean isCanBeDoneFromFloor() {
      return this.canBeDoneFromFloor;
   }

   public void setCanBeDoneFromFloor(boolean var1) {
      this.canBeDoneFromFloor = var1;
   }

   public Recipe() {
      this.setOriginalname("recipe");
   }

   public int FindIndexOf(InventoryItem var1) {
      return -1;
   }

   public ArrayList getSource() {
      return this.Source;
   }

   public int getNumberOfNeededItem() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.getSource().size(); ++var2) {
         Recipe.Source var3 = (Recipe.Source)this.getSource().get(var2);
         if (!var3.getItems().isEmpty()) {
            var1 = (int)((float)var1 + var3.getCount());
         }
      }

      return var1;
   }

   public float getTimeToMake() {
      return this.TimeToMake;
   }

   public String getName() {
      return this.name;
   }

   public void Load(String var1, String[] var2) {
      this.name = Translator.getRecipeName(var1);
      this.originalname = var1;
      boolean var3 = false;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (!var2[var4].trim().isEmpty()) {
            if (var2[var4].contains(":")) {
               String[] var5 = var2[var4].split(":");
               String var6 = var5[0].trim();
               String var7 = var5[1].trim();
               if (var6.equals("Override")) {
                  var3 = var7.trim().equalsIgnoreCase("true");
               }

               if (var6.equals("Time")) {
                  this.TimeToMake = Float.parseFloat(var7);
               }

               if (var6.equals("Sound")) {
                  this.Sound = var7.trim();
               }

               if (var6.equals("Result")) {
                  this.DoResult(var7);
               }

               if (var6.equals("OnTest")) {
                  this.LuaTest = var7;
               }

               if (var6.equals("OnCreate")) {
                  this.LuaCreate = var7;
               }

               if (var6.equals("OnGrab")) {
                  this.LuaGrab = var7;
               }

               if (var6.toLowerCase().equals("needtobelearn")) {
                  this.setNeedToBeLearn(var7.trim().equalsIgnoreCase("true"));
               }

               if (var6.toLowerCase().equals("category")) {
                  this.setCategory(var7.trim());
               }

               if (var6.equals("RemoveResultItem")) {
                  this.removeResultItem = var7.trim().equalsIgnoreCase("true");
               }

               if (var6.equals("CanBeDoneFromFloor")) {
                  this.setCanBeDoneFromFloor(var7.trim().equalsIgnoreCase("true"));
               }

               if (var6.equals("NearItem")) {
                  this.setNearItem(var7.trim());
               }

               if (var6.equals("SkillRequired")) {
                  this.skillRequired = new HashMap();
                  String[] var8 = var7.split(";");

                  for(int var9 = 0; var9 < var8.length; ++var9) {
                     String[] var10 = var8[var9].split("=");
                     this.skillRequired.put(var10[0], Integer.parseInt(var10[1]));
                  }
               }

               if (var6.equals("OnGiveXP")) {
                  this.LuaGiveXP = var7;
               }

               if (var6.equals("Obsolete") && var7.trim().toLowerCase().equals("true")) {
                  this.module.RecipeMap.remove(this);
                  this.module.RecipesWithDotInName.remove(this);
                  return;
               }

               if (var6.equals("Heat")) {
                  this.heat = Float.parseFloat(var7);
               }

               if (var6.equals("NoBrokenItems")) {
                  this.noBrokenItems = var7.trim().equalsIgnoreCase("true");
               }
            } else {
               this.DoSource(var2[var4].trim());
            }
         }
      }

      if (var3) {
         Recipe var11 = this.module.getRecipe(var1);
         if (var11 != null && var11 != this) {
            this.module.RecipeMap.remove(var11);
            this.module.RecipesWithDotInName.remove(var1);
         }
      }

   }

   private void DoSource(String var1) {
      Recipe.Source var2 = new Recipe.Source();
      if (var1.contains("=")) {
         var2.count = new Float(var1.split("=")[1].trim());
         var1 = var1.split("=")[0].trim();
      }

      if (var1.indexOf("keep") == 0) {
         var1 = var1.replace("keep ", "");
         var2.keep = true;
      }

      if (var1.contains(";")) {
         String[] var3 = var1.split(";");
         var1 = var3[0];
         var2.use = Float.parseFloat(var3[1]);
      }

      if (var1.indexOf("destroy") == 0) {
         var1 = var1.replace("destroy ", "");
         var2.destroy = true;
      }

      if (var1.equals("null")) {
         var2.getItems().clear();
      } else if (var1.contains("/")) {
         var1 = var1.replaceFirst("keep ", "").trim();
         var2.getItems().addAll(Arrays.asList(var1.split("/")));
      } else {
         var2.getItems().add(var1);
      }

      if (!var1.isEmpty()) {
         this.Source.add(var2);
      }

   }

   private void DoResult(String var1) {
      Recipe.Result var2 = new Recipe.Result();
      String[] var3;
      if (var1.contains("=")) {
         var3 = var1.split("=");
         var1 = var3[0].trim();
         var2.count = Integer.parseInt(var3[1].trim());
      }

      if (var1.contains(";")) {
         var3 = var1.split(";");
         var1 = var3[0].trim();
         var2.drainableCount = Integer.parseInt(var3[1].trim());
      }

      if (var1.contains(".")) {
         var2.type = var1.split("\\.")[1];
         var2.module = var1.split("\\.")[0];
      } else {
         var2.type = var1;
      }

      this.Result = var2;
   }

   public Recipe.Result getResult() {
      return this.Result;
   }

   public String getSound() {
      return this.Sound;
   }

   public String getOriginalname() {
      return this.originalname;
   }

   public void setOriginalname(String var1) {
      this.originalname = var1;
   }

   public boolean needToBeLearn() {
      return this.needToBeLearn;
   }

   public void setNeedToBeLearn(boolean var1) {
      this.needToBeLearn = var1;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String var1) {
      this.category = var1;
   }

   public ArrayList getRequiredSkills() {
      ArrayList var1 = null;
      if (this.skillRequired != null) {
         var1 = new ArrayList();
         Iterator var2 = this.skillRequired.keySet().iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            String var4 = PerkFactory.getPerk(PerkFactory.Perks.FromString(var3)).name + " ";
            var4 = var4 + this.skillRequired.get(var3);
            var1.add(var4);
         }
      }

      return var1;
   }

   public Recipe.Source findSource(String var1) {
      for(int var2 = 0; var2 < this.Source.size(); ++var2) {
         Recipe.Source var3 = (Recipe.Source)this.Source.get(var2);

         for(int var4 = 0; var4 < var3.getItems().size(); ++var4) {
            if (((String)var3.getItems().get(var4)).equals(var1)) {
               return var3;
            }
         }
      }

      return null;
   }

   public boolean isDestroy(String var1) {
      Recipe.Source var2 = this.findSource(var1);
      if (var2 != null) {
         return var2.isDestroy();
      } else {
         throw new RuntimeException("recipe " + this.getOriginalname() + " doesn't use item " + var1);
      }
   }

   public boolean isKeep(String var1) {
      Recipe.Source var2 = this.findSource(var1);
      if (var2 != null) {
         return var2.isKeep();
      } else {
         throw new RuntimeException("recipe " + this.getOriginalname() + " doesn't use item " + var1);
      }
   }

   public float getHeat() {
      return this.heat;
   }

   public boolean noBrokenItems() {
      return this.noBrokenItems;
   }

   public int getWaterAmountNeeded() {
      Recipe.Source var1 = this.findSource("Water");
      return var1 != null ? (int)var1.getCount() : 0;
   }

   public String getNearItem() {
      return this.nearItem;
   }

   public void setNearItem(String var1) {
      this.nearItem = var1;
   }

   public boolean isRemoveResultItem() {
      return this.removeResultItem;
   }

   public void setRemoveResultItem(boolean var1) {
      this.removeResultItem = var1;
   }

   public class Source {
      public boolean keep = false;
      private ArrayList items = new ArrayList();
      public boolean destroy = false;
      public float count = 1.0F;
      public float use = 0.0F;

      public boolean isDestroy() {
         return this.destroy;
      }

      public boolean isKeep() {
         return this.keep;
      }

      public float getCount() {
         return this.count;
      }

      public float getUse() {
         return this.use;
      }

      public ArrayList getItems() {
         return this.items;
      }

      public String getOnlyItem() {
         if (this.items.size() != 1) {
            throw new RuntimeException("items.size() == " + this.items.size());
         } else {
            return (String)this.items.get(0);
         }
      }
   }

   public class Result {
      public String type;
      public int count = 1;
      public int drainableCount = 0;
      public String module = null;

      public String getType() {
         return this.type;
      }

      public int getCount() {
         return this.count;
      }

      public String getModule() {
         return this.module;
      }

      public String getFullType() {
         return this.module + "." + this.type;
      }

      public int getDrainableCount() {
         return this.drainableCount;
      }
   }
}
