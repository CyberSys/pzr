package zombie.characters;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.iso.IsoCell;

public class SurvivorFactory {
   public static ArrayList FemaleForenames = new ArrayList();
   public static ArrayList MaleForenames = new ArrayList();
   public static ArrayList Surnames = new ArrayList();

   public static void Reset() {
      FemaleForenames.clear();
      MaleForenames.clear();
      Surnames.clear();
      SurvivorDesc.HairCommonColors.clear();
      SurvivorDesc.TrouserCommonColors.clear();
   }

   public static SurvivorDesc[] CreateFamily(int var0) {
      SurvivorDesc[] var1 = new SurvivorDesc[var0];

      for(int var2 = 0; var2 < var0; ++var2) {
         var1[var2] = CreateSurvivor();
         if (var2 > 0) {
            var1[var2].surname = var1[0].surname;
         }
      }

      return var1;
   }

   public static SurvivorDesc CreateSurvivor() {
      switch(Rand.Next(3)) {
      case 0:
         return CreateSurvivor(SurvivorFactory.SurvivorType.Friendly);
      case 1:
         return CreateSurvivor(SurvivorFactory.SurvivorType.Neutral);
      case 2:
         return CreateSurvivor(SurvivorFactory.SurvivorType.Aggressive);
      default:
         return null;
      }
   }

   public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType var0, boolean var1) {
      SurvivorDesc var2 = new SurvivorDesc();
      var2.setType(var0);
      IsoGameCharacter.SurvivorMap.put(var2.ID, var2);
      var2.setFemale(var1);
      randomName(var2);
      Integer var3 = Rand.Next(4);
      var2.skinpal = "Skin_0" + var3.toString();
      if (var2.isFemale()) {
         var2.setTorsoNumber(Rand.Next(2));
         setTorso(var2);
         switch(Rand.Next(2)) {
         case 0:
            var2.top = "Blouse";
            break;
         case 1:
            var2.top = "Vest";
         }

         switch(Rand.Next(2)) {
         case 0:
            var2.bottoms = "Trousers";
            break;
         case 1:
            var2.bottoms = "Skirt";
         }

         var2.setHairNumber(Rand.Next(5));
         setHairNoColor(var2);
         var2.hair = var2.hairNoColor + "White";
      } else {
         var2.setTorsoNumber(Rand.Next(8));
         setTorso(var2);
         switch(Rand.Next(2)) {
         case 0:
            var2.top = "Shirt";
            break;
         case 1:
            var2.top = "Vest";
         }

         var2.setHairNumber(Rand.Next(6));
         setHairNoColor(var2);
         String var4 = "";
         var4 = "White";
         if (var2.hairNoColor != "none") {
            var2.hair = var2.hairNoColor + "White";
         }

         if (Rand.Next(2) == 0) {
            var2.setBeardNumber(Rand.Next(4));
            setBeardNoColor(var2);
            var2.extra.add(var2.getBeardNoColor() + "White");
         }
      }

      var2.toppal = var2.top + "_White";
      var2.bottomspal = var2.bottoms + "_White";
      var2.trouserColor = new Color((Color)SurvivorDesc.TrouserCommonColors.get(Rand.Next(SurvivorDesc.TrouserCommonColors.size())));
      var2.hairColor = new Color((Color)SurvivorDesc.HairCommonColors.get(Rand.Next(SurvivorDesc.HairCommonColors.size())));
      var2.topColor = new Color(30 + Rand.Next(225), 30 + Rand.Next(225), 30 + Rand.Next(225));
      var2.skinColor = SurvivorDesc.getRandomSkinColor();
      return var2;
   }

   public static void setBeardNoColor(SurvivorDesc var0) {
      String var1 = "";
      switch(var0.getBeardNumber()) {
      case 0:
         var1 = "Beard_Full_";
         break;
      case 1:
         var1 = "Beard_Chops_";
         break;
      case 2:
         var1 = "Beard_Only_";
         break;
      case 3:
         var1 = "Beard_Goatee_";
      }

      var0.beardNoColor = var1;
   }

   public static void setTorso(SurvivorDesc var0) {
      if (var0.isFemale()) {
         if (var0.getTorsoNumber() == 0) {
            var0.torso = "Kate";
         } else if (var0.getTorsoNumber() == 1) {
            var0.torso = "Kate_2";
         }
      } else if (var0.getTorsoNumber() == 0) {
         var0.torso = "Male";
      } else {
         var0.torso = "Male_" + (var0.getTorsoNumber() + 1);
      }

   }

   public static void setHairNoColor(SurvivorDesc var0) {
      if (var0.isFemale()) {
         switch(var0.getHairNumber()) {
         case 0:
            var0.hairNoColor = "F_Hair_Bob_";
            break;
         case 1:
            var0.hairNoColor = "F_Hair_Long_";
            break;
         case 2:
            var0.hairNoColor = "F_Hair_Long2_";
            break;
         case 3:
            var0.hairNoColor = "F_Hair_OverEye_";
            break;
         case 4:
            var0.hairNoColor = "F_Hair_";
            break;
         case 5:
            var0.hairNoColor = "F_Hair_";
         }
      } else {
         switch(var0.getHairNumber()) {
         case 0:
            var0.hairNoColor = "Hair_Baldspot_";
            break;
         case 1:
            var0.hairNoColor = "Hair_Picard_";
            break;
         case 2:
            var0.hairNoColor = "Hair_Recede_";
            break;
         case 3:
            var0.hairNoColor = "Hair_Short_";
            break;
         case 4:
            var0.hairNoColor = "Hair_Messy_";
            break;
         case 5:
            var0.hairNoColor = "none";
         }
      }

   }

   public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType var0) {
      return CreateSurvivor(var0, Rand.Next(2) == 0);
   }

   public static SurvivorDesc[] CreateSurvivorGroup(int var0) {
      SurvivorDesc[] var1 = new SurvivorDesc[var0];

      for(int var2 = 0; var2 < var0; ++var2) {
         var1[var2] = CreateSurvivor();
      }

      return var1;
   }

   public static IsoSurvivor InstansiateInCell(SurvivorDesc var0, IsoCell var1, int var2, int var3, int var4) {
      var0.Instance = new IsoSurvivor(var0, var1, var2, var3, var4);
      return (IsoSurvivor)var0.Instance;
   }

   public static void randomName(SurvivorDesc var0) {
      if (var0.isFemale()) {
         var0.forename = (String)FemaleForenames.get(Rand.Next(FemaleForenames.size()));
      } else {
         var0.forename = (String)MaleForenames.get(Rand.Next(MaleForenames.size()));
      }

      var0.surname = (String)Surnames.get(Rand.Next(Surnames.size()));
   }

   public static void addSurname(String var0) {
      Surnames.add(var0);
   }

   public static void addFemaleForename(String var0) {
      FemaleForenames.add(var0);
   }

   public static void addMaleForename(String var0) {
      MaleForenames.add(var0);
   }

   public static enum SurvivorType {
      Friendly,
      Neutral,
      Aggressive;
   }
}
