package zombie.profanity.locales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.profanity.Phonizer;
import zombie.profanity.ProfanityFilter;

public abstract class Locale {
   protected String id;
   protected int storeVowelsAmount = 3;
   protected String phoneticRules = "";
   protected Map phonizers = new HashMap();
   protected Map filterWords = new HashMap();
   protected ArrayList whitelistWords = new ArrayList();
   protected Pattern pattern;
   private Pattern preProcessLeet = Pattern.compile("(?<leet>[\\$@3470])\\k<leet>*|(?<nonWord>[^A-Z\\s\\$@3470]+)");
   private Pattern preProcessDoubles = Pattern.compile("(?<doublechar>[A-Z])\\k<doublechar>+");
   private Pattern preProcessVowels = Pattern.compile("(?<vowel>[AOUIE])");

   protected Locale(String var1) {
      this.id = var1;
      this.Init();
      this.finalizeData();
      this.loadFilterWords();
      this.loadWhiteListWords();
      ProfanityFilter.printDebug("Done init locale: " + this.id);
   }

   public String getID() {
      return this.id;
   }

   public String getPhoneticRules() {
      return this.phoneticRules;
   }

   public int getFilterWordsCount() {
      return this.filterWords.size();
   }

   protected abstract void Init();

   public void addWhiteListWord(String var1) {
      var1 = var1.toUpperCase().trim();
      if (!this.whitelistWords.contains(var1)) {
         this.whitelistWords.add(var1);
      }

   }

   public void removeWhiteListWord(String var1) {
      var1 = var1.toUpperCase().trim();
      if (this.whitelistWords.contains(var1)) {
         this.whitelistWords.remove(var1);
      }

   }

   public void addFilterWord(String var1) {
      String var2 = this.phonizeWord(var1);
      if (var2.length() > 2) {
         String var3 = "";
         if (this.filterWords.containsKey(var2)) {
            var3 = var3 + (String)this.filterWords.get(var2) + ",";
         }

         ProfanityFilter.printDebug("Adding word: " + var1 + ", Phonized: " + var2);
         this.filterWords.put(var2, var3 + var1.toLowerCase());
      } else {
         ProfanityFilter.printDebug("Refusing word: " + var1 + ", Phonized: " + var2 + ", null or phonized < 2 characters");
      }

   }

   public void removeFilterWord(String var1) {
      String var2 = this.phonizeWord(var1);
      if (this.filterWords.containsKey(var2)) {
         this.filterWords.remove(var2);
      }

   }

   public String filterWord(String var1) {
      String var2 = this.phonizeWord(var1);
      return this.filterWords.containsKey(var2) ? (new String(new char[var1.length()])).replace('\u0000', '*') : var1;
   }

   public String returnMatchSetForWord(String var1) {
      String var2 = this.phonizeWord(var1);
      return this.filterWords.containsKey(var2) ? (String)this.filterWords.get(var2) : null;
   }

   public String returnPhonizedWord(String var1) {
      return this.phonizeWord(var1);
   }

   protected String phonizeWord(String var1) {
      var1 = var1.toUpperCase().trim();
      if (this.whitelistWords.contains(var1)) {
         return var1;
      } else {
         var1 = this.preProcessWord(var1);
         if (this.phonizers.size() <= 0) {
            return var1;
         } else {
            Matcher var2 = this.pattern.matcher(var1);
            StringBuffer var3 = new StringBuffer();

            while(true) {
               while(var2.find()) {
                  Iterator var4 = this.phonizers.entrySet().iterator();

                  while(var4.hasNext()) {
                     Entry var5 = (Entry)var4.next();
                     if (var2.group((String)var5.getKey()) != null) {
                        ((Phonizer)var5.getValue()).execute(var2, var3);
                        break;
                     }
                  }
               }

               var2.appendTail(var3);
               return var3.toString();
            }
         }
      }
   }

   private String preProcessWord(String var1) {
      Matcher var2 = this.preProcessLeet.matcher(var1);
      StringBuffer var3 = new StringBuffer();

      while(var2.find()) {
         if (var2.group("leet") != null) {
            String var4 = var2.group("leet").toString();
            byte var5 = -1;
            switch(var4.hashCode()) {
            case 36:
               if (var4.equals("$")) {
                  var5 = 0;
               }
               break;
            case 48:
               if (var4.equals("0")) {
                  var5 = 5;
               }
               break;
            case 51:
               if (var4.equals("3")) {
                  var5 = 3;
               }
               break;
            case 52:
               if (var4.equals("4")) {
                  var5 = 1;
               }
               break;
            case 55:
               if (var4.equals("7")) {
                  var5 = 4;
               }
               break;
            case 64:
               if (var4.equals("@")) {
                  var5 = 2;
               }
            }

            switch(var5) {
            case 0:
               var2.appendReplacement(var3, "S");
               break;
            case 1:
            case 2:
               var2.appendReplacement(var3, "A");
               break;
            case 3:
               var2.appendReplacement(var3, "E");
               break;
            case 4:
               var2.appendReplacement(var3, "T");
               break;
            case 5:
               var2.appendReplacement(var3, "O");
            }
         } else if (var2.group("nonWord") != null) {
            var2.appendReplacement(var3, "");
         }
      }

      var2.appendTail(var3);
      var2 = this.preProcessDoubles.matcher(var3.toString());
      var3.delete(0, var3.capacity());

      while(var2.find()) {
         if (var2.group("doublechar") != null) {
            var2.appendReplacement(var3, "${doublechar}");
         }
      }

      var2.appendTail(var3);
      var2 = this.preProcessVowels.matcher(var3.toString());
      var3.delete(0, var3.capacity());
      int var6 = 0;

      while(var2.find()) {
         if (var2.group("vowel") != null) {
            if (var6 < this.storeVowelsAmount) {
               var2.appendReplacement(var3, "${vowel}");
               ++var6;
            } else {
               var2.appendReplacement(var3, "");
            }
         }
      }

      var2.appendTail(var3);
      return var3.toString();
   }

   protected void addPhonizer(Phonizer var1) {
      if (var1 != null && !this.phonizers.containsKey(var1.getName())) {
         this.phonizers.put(var1.getName(), var1);
      }

   }

   protected void finalizeData() {
      this.phoneticRules = "";
      int var1 = this.phonizers.size();
      int var2 = 0;
      Iterator var3 = this.phonizers.values().iterator();

      while(var3.hasNext()) {
         Phonizer var4 = (Phonizer)var3.next();
         String var10001 = this.phoneticRules;
         this.phoneticRules = var10001 + var4.getRegex();
         ++var2;
         if (var2 < var1) {
            this.phoneticRules = this.phoneticRules + "|";
         }
      }

      ProfanityFilter.printDebug("PhoneticRules: " + this.phoneticRules);
      this.pattern = Pattern.compile(this.phoneticRules);
   }

   protected void loadFilterWords() {
      try {
         File var1 = new File(ProfanityFilter.LOCALES_DIR + "blacklist_" + this.id + ".txt");
         FileReader var2 = new FileReader(var1);
         BufferedReader var3 = new BufferedReader(var2);

         String var4;
         int var5;
         for(var5 = 0; (var4 = var3.readLine()) != null; ++var5) {
            this.addFilterWord(var4);
         }

         var2.close();
         ProfanityFilter.printDebug("BlackList, " + var5 + " added.");
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   protected void loadWhiteListWords() {
      try {
         File var1 = new File(ProfanityFilter.LOCALES_DIR + "whitelist_" + this.id + ".txt");
         FileReader var2 = new FileReader(var1);
         BufferedReader var3 = new BufferedReader(var2);

         String var4;
         int var5;
         for(var5 = 0; (var4 = var3.readLine()) != null; ++var5) {
            this.addWhiteListWord(var4);
         }

         var2.close();
         ProfanityFilter.printDebug("WhiteList, " + var5 + " added.");
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }
}
