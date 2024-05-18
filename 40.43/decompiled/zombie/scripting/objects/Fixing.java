package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;

public class Fixing extends BaseScriptObject {
   private String name = null;
   private ArrayList require = null;
   private LinkedList fixers = new LinkedList();
   private Fixing.Fixer globalItem = null;
   private float conditionModifier = 1.0F;

   public void Load(String var1, String[] var2) {
      this.setName(var1);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (!var2[var3].trim().isEmpty() && var2[var3].contains(":")) {
            String[] var4 = var2[var3].split(":");
            String var5 = var4[0].trim();
            String var6 = var4[1].trim();
            if (var5.equals("Require")) {
               List var10 = Arrays.asList(var6.split(";"));

               for(int var11 = 0; var11 < var10.size(); ++var11) {
                  this.addRequiredItem(((String)var10.get(var11)).trim());
               }
            } else if (!var5.equals("Fixer")) {
               if (var5.equals("GlobalItem")) {
                  if (var6.contains("=")) {
                     this.setGlobalItem(new Fixing.Fixer(var6.split("=")[0], (LinkedList)null, new Integer(var6.split("=")[1])));
                  } else {
                     this.setGlobalItem(new Fixing.Fixer(var6, (LinkedList)null, 1));
                  }
               } else if (var5.equals("ConditionModifier")) {
                  this.setConditionModifier(Float.parseFloat(var6.trim()));
               }
            } else if (!var6.contains(";")) {
               if (var6.contains("=")) {
                  this.fixers.add(new Fixing.Fixer(var6.split("=")[0], (LinkedList)null, new Integer(var6.split("=")[1])));
               } else {
                  this.fixers.add(new Fixing.Fixer(var6, (LinkedList)null, 1));
               }
            } else {
               LinkedList var7 = new LinkedList();
               List var8 = Arrays.asList(var6.split(";"));

               for(int var9 = 1; var9 < var8.size(); ++var9) {
                  var7.add(new Fixing.FixerSkill(((String)var8.get(var9)).trim().split("=")[0].trim(), new Integer(((String)var8.get(var9)).trim().split("=")[1].trim())));
               }

               if (var6.split(";")[0].trim().contains("=")) {
                  this.fixers.add(new Fixing.Fixer(var6.split(";")[0].trim().split("=")[0], var7, new Integer(var6.split(";")[0].trim().split("=")[1])));
               } else {
                  this.fixers.add(new Fixing.Fixer(var6.split(";")[0].trim(), var7, 1));
               }
            }
         }
      }

   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public ArrayList getRequiredItem() {
      return this.require;
   }

   public void addRequiredItem(String var1) {
      if (this.require == null) {
         this.require = new ArrayList();
      }

      this.require.add(var1);
   }

   public LinkedList getFixers() {
      return this.fixers;
   }

   public Fixing.Fixer usedInFixer(InventoryItem var1, IsoGameCharacter var2) {
      for(int var3 = 0; var3 < this.getFixers().size(); ++var3) {
         Fixing.Fixer var4 = (Fixing.Fixer)this.getFixers().get(var3);
         if (var4.getFixerName().equals(var1.getType())) {
            if (var1 instanceof DrainableComboItem) {
               DrainableComboItem var5 = (DrainableComboItem)var1;
               if (!(var5.getUsedDelta() < 1.0F)) {
                  return var4;
               }

               if (var5.getUsedDelta() / var5.getUseDelta() >= (float)var4.getNumberOfUse()) {
                  return var4;
               }
            } else if (var2.getInventory().getItemCount(this.getModule().getName() + "." + var4.getFixerName()) >= var4.getNumberOfUse()) {
               return var4;
            }
         }
      }

      return null;
   }

   public InventoryItem haveGlobalItem(IsoGameCharacter var1) {
      for(int var2 = 0; var2 < var1.getInventory().getItems().size(); ++var2) {
         InventoryItem var3 = (InventoryItem)var1.getInventory().getItems().get(var2);
         if (this.getGlobalItem().getFixerName().equals(var3.getType()) && this.countUses(var1, this.getGlobalItem(), (InventoryItem)null) > this.getGlobalItem().getNumberOfUse()) {
            return var3;
         }
      }

      return null;
   }

   public InventoryItem haveThisFixer(IsoGameCharacter var1, Fixing.Fixer var2, InventoryItem var3) {
      if (this.countUses(var1, var2, var3) < var2.getNumberOfUse()) {
         return null;
      } else {
         for(int var4 = 0; var4 < var1.getInventory().getItems().size(); ++var4) {
            InventoryItem var5 = (InventoryItem)var1.getInventory().getItems().get(var4);
            if (var5 != var3 && var2.getFixerName().equals(var5.getType())) {
               return var5;
            }
         }

         return null;
      }
   }

   public int countUses(IsoGameCharacter var1, Fixing.Fixer var2, InventoryItem var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < var1.getInventory().getItems().size(); ++var5) {
         InventoryItem var6 = (InventoryItem)var1.getInventory().getItems().get(var5);
         if (var6 != var3 && var2.getFixerName().equals(var6.getType())) {
            if (var6 instanceof DrainableComboItem) {
               DrainableComboItem var7 = (DrainableComboItem)var6;
               var4 = (int)((double)var4 + Math.floor((double)(var7.getUsedDelta() / var7.getUseDelta())));
            } else {
               ++var4;
            }
         }
      }

      return var4;
   }

   public Fixing.Fixer getGlobalItem() {
      return this.globalItem;
   }

   public void setGlobalItem(Fixing.Fixer var1) {
      this.globalItem = var1;
   }

   public float getConditionModifier() {
      return this.conditionModifier;
   }

   public void setConditionModifier(float var1) {
      this.conditionModifier = var1;
   }

   public class FixerSkill {
      private String skillName = null;
      private int skillLvl = 0;

      public FixerSkill(String var2, int var3) {
         this.skillName = var2;
         this.skillLvl = var3;
      }

      public String getSkillName() {
         return this.skillName;
      }

      public int getSkillLevel() {
         return this.skillLvl;
      }
   }

   public class Fixer {
      private String fixerName = null;
      private LinkedList skills = null;
      private int numberOfUse = 1;

      public Fixer(String var2, LinkedList var3, int var4) {
         this.fixerName = var2;
         this.skills = var3;
         this.numberOfUse = var4;
      }

      public String getFixerName() {
         return this.fixerName;
      }

      public LinkedList getFixerSkills() {
         return this.skills;
      }

      public int getNumberOfUse() {
         return this.numberOfUse;
      }
   }
}
