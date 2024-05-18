package zombie.characters.BodyDamage;

import zombie.core.Translator;

public enum BodyPartType {
   Hand_L,
   Hand_R,
   ForeArm_L,
   ForeArm_R,
   UpperArm_L,
   UpperArm_R,
   Torso_Upper,
   Torso_Lower,
   Head,
   Neck,
   Groin,
   UpperLeg_L,
   UpperLeg_R,
   LowerLeg_L,
   LowerLeg_R,
   Foot_L,
   Foot_R,
   MAX;

   public static BodyPartType FromIndex(int var0) {
      switch(var0) {
      case 0:
         return Hand_L;
      case 1:
         return Hand_R;
      case 2:
         return ForeArm_L;
      case 3:
         return ForeArm_R;
      case 4:
         return UpperArm_L;
      case 5:
         return UpperArm_R;
      case 6:
         return Torso_Upper;
      case 7:
         return Torso_Lower;
      case 8:
         return Head;
      case 9:
         return Neck;
      case 10:
         return Groin;
      case 11:
         return UpperLeg_L;
      case 12:
         return UpperLeg_R;
      case 13:
         return LowerLeg_L;
      case 14:
         return LowerLeg_R;
      case 15:
         return Foot_L;
      case 16:
         return Foot_R;
      default:
         return MAX;
      }
   }

   public static BodyPartType FromString(String var0) {
      if (var0.equals("Hand_L")) {
         return Hand_L;
      } else if (var0.equals("Hand_R")) {
         return Hand_R;
      } else if (var0.equals("ForeArm_L")) {
         return ForeArm_L;
      } else if (var0.equals("ForeArm_R")) {
         return ForeArm_R;
      } else if (var0.equals("UpperArm_L")) {
         return UpperArm_L;
      } else if (var0.equals("UpperArm_R")) {
         return UpperArm_R;
      } else if (var0.equals("Torso_Upper")) {
         return Torso_Upper;
      } else if (var0.equals("Torso_Lower")) {
         return Torso_Lower;
      } else if (var0.equals("Head")) {
         return Head;
      } else if (var0.equals("Neck")) {
         return Neck;
      } else if (var0.equals("Groin")) {
         return Groin;
      } else if (var0.equals("UpperLeg_L")) {
         return UpperLeg_L;
      } else if (var0.equals("UpperLeg_R")) {
         return UpperLeg_R;
      } else if (var0.equals("LowerLeg_L")) {
         return LowerLeg_L;
      } else if (var0.equals("LowerLeg_R")) {
         return LowerLeg_R;
      } else if (var0.equals("Foot_L")) {
         return Foot_L;
      } else {
         return var0.equals("Foot_R") ? Foot_R : MAX;
      }
   }

   public static float getPainModifyer(int var0) {
      switch(var0) {
      case 0:
         return 0.5F;
      case 1:
         return 0.5F;
      case 2:
         return 0.6F;
      case 3:
         return 0.6F;
      case 4:
         return 0.6F;
      case 5:
         return 0.6F;
      case 6:
         return 0.7F;
      case 7:
         return 0.78F;
      case 8:
         return 0.8F;
      case 9:
         return 0.8F;
      case 10:
         return 0.7F;
      case 11:
         return 0.7F;
      case 12:
         return 0.7F;
      case 13:
         return 0.6F;
      case 14:
         return 0.6F;
      case 15:
         return 0.5F;
      case 16:
         return 0.5F;
      default:
         return 1.0F;
      }
   }

   public static String getDisplayName(BodyPartType var0) {
      if (var0 == Hand_L) {
         return Translator.getText("IGUI_health_Left_Hand");
      } else if (var0 == Hand_R) {
         return Translator.getText("IGUI_health_Right_Hand");
      } else if (var0 == ForeArm_L) {
         return Translator.getText("IGUI_health_Left_Forearm");
      } else if (var0 == ForeArm_R) {
         return Translator.getText("IGUI_health_Right_Forearm");
      } else if (var0 == UpperArm_L) {
         return Translator.getText("IGUI_health_Left_Upper_Arm");
      } else if (var0 == UpperArm_R) {
         return Translator.getText("IGUI_health_Right_Upper_Arm");
      } else if (var0 == Torso_Upper) {
         return Translator.getText("IGUI_health_Upper_Torso");
      } else if (var0 == Torso_Lower) {
         return Translator.getText("IGUI_health_Lower_Torso");
      } else if (var0 == Head) {
         return Translator.getText("IGUI_health_Head");
      } else if (var0 == Neck) {
         return Translator.getText("IGUI_health_Neck");
      } else if (var0 == Groin) {
         return Translator.getText("IGUI_health_Groin");
      } else if (var0 == UpperLeg_L) {
         return Translator.getText("IGUI_health_Left_Thigh");
      } else if (var0 == UpperLeg_R) {
         return Translator.getText("IGUI_health_Right_Thigh");
      } else if (var0 == LowerLeg_L) {
         return Translator.getText("IGUI_health_Left_Shin");
      } else if (var0 == LowerLeg_R) {
         return Translator.getText("IGUI_health_Right_Shin");
      } else if (var0 == Foot_L) {
         return Translator.getText("IGUI_health_Left_Foot");
      } else {
         return var0 == Foot_R ? Translator.getText("IGUI_health_Right_Foot") : Translator.getText("IGUI_health_Unknown_Body_Part");
      }
   }

   public static int ToIndex(BodyPartType var0) {
      if (var0 == null) {
         return 0;
      } else {
         switch(var0) {
         case Hand_L:
            return 0;
         case Hand_R:
            return 1;
         case ForeArm_L:
            return 2;
         case ForeArm_R:
            return 3;
         case UpperArm_L:
            return 4;
         case UpperArm_R:
            return 5;
         case Torso_Upper:
            return 6;
         case Torso_Lower:
            return 7;
         case Head:
            return 8;
         case Neck:
            return 9;
         case Groin:
            return 10;
         case UpperLeg_L:
            return 11;
         case UpperLeg_R:
            return 12;
         case LowerLeg_L:
            return 13;
         case LowerLeg_R:
            return 14;
         case Foot_L:
            return 15;
         case Foot_R:
            return 16;
         case MAX:
            return 17;
         default:
            return 17;
         }
      }
   }

   public static String ToString(BodyPartType var0) {
      if (var0 == Hand_L) {
         return "Hand_L";
      } else if (var0 == Hand_R) {
         return "Hand_R";
      } else if (var0 == ForeArm_L) {
         return "ForeArm_L";
      } else if (var0 == ForeArm_R) {
         return "ForeArm_R";
      } else if (var0 == UpperArm_L) {
         return "UpperArm_L";
      } else if (var0 == UpperArm_R) {
         return "UpperArm_R";
      } else if (var0 == Torso_Upper) {
         return "Torso_Upper";
      } else if (var0 == Torso_Lower) {
         return "Torso_Lower";
      } else if (var0 == Head) {
         return "Head";
      } else if (var0 == Neck) {
         return "Neck";
      } else if (var0 == Groin) {
         return "Groin";
      } else if (var0 == UpperLeg_L) {
         return "UpperLeg_L";
      } else if (var0 == UpperLeg_R) {
         return "UpperLeg_R";
      } else if (var0 == LowerLeg_L) {
         return "LowerLeg_L";
      } else if (var0 == LowerLeg_R) {
         return "LowerLeg_R";
      } else if (var0 == Foot_L) {
         return "Foot_L";
      } else {
         return var0 == Foot_R ? "Foot_R" : "Unkown Body Part";
      }
   }

   public static float getDamageModifyer(int var0) {
      switch(var0) {
      case 0:
         return 0.2F;
      case 1:
         return 0.2F;
      case 2:
         return 0.3F;
      case 3:
         return 0.3F;
      case 4:
         return 0.3F;
      case 5:
         return 0.3F;
      case 6:
         return 0.4F;
      case 7:
         return 0.48F;
      case 8:
         return 0.6F;
      case 9:
         return 0.6F;
      case 10:
         return 0.5F;
      case 11:
         return 0.5F;
      case 12:
         return 0.5F;
      case 13:
         return 0.4F;
      case 14:
         return 0.4F;
      case 15:
         return 0.3F;
      case 16:
         return 0.3F;
      default:
         return 1.0F;
      }
   }
}
