package zombie.characters.BodyDamage;

import zombie.core.Translator;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.debug.DebugLog;



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

	public static BodyPartType FromIndex(int int1) {
		switch (int1) {
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
	public int index() {
		return ToIndex(this);
	}
	public static BodyPartType FromString(String string) {
		if (string.equals("Hand_L")) {
			return Hand_L;
		} else if (string.equals("Hand_R")) {
			return Hand_R;
		} else if (string.equals("ForeArm_L")) {
			return ForeArm_L;
		} else if (string.equals("ForeArm_R")) {
			return ForeArm_R;
		} else if (string.equals("UpperArm_L")) {
			return UpperArm_L;
		} else if (string.equals("UpperArm_R")) {
			return UpperArm_R;
		} else if (string.equals("Torso_Upper")) {
			return Torso_Upper;
		} else if (string.equals("Torso_Lower")) {
			return Torso_Lower;
		} else if (string.equals("Head")) {
			return Head;
		} else if (string.equals("Neck")) {
			return Neck;
		} else if (string.equals("Groin")) {
			return Groin;
		} else if (string.equals("UpperLeg_L")) {
			return UpperLeg_L;
		} else if (string.equals("UpperLeg_R")) {
			return UpperLeg_R;
		} else if (string.equals("LowerLeg_L")) {
			return LowerLeg_L;
		} else if (string.equals("LowerLeg_R")) {
			return LowerLeg_R;
		} else if (string.equals("Foot_L")) {
			return Foot_L;
		} else {
			return string.equals("Foot_R") ? Foot_R : MAX;
		}
	}
	public static float getPainModifyer(int int1) {
		switch (int1) {
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
	public static String getDisplayName(BodyPartType bodyPartType) {
		if (bodyPartType == Hand_L) {
			return Translator.getText("IGUI_health_Left_Hand");
		} else if (bodyPartType == Hand_R) {
			return Translator.getText("IGUI_health_Right_Hand");
		} else if (bodyPartType == ForeArm_L) {
			return Translator.getText("IGUI_health_Left_Forearm");
		} else if (bodyPartType == ForeArm_R) {
			return Translator.getText("IGUI_health_Right_Forearm");
		} else if (bodyPartType == UpperArm_L) {
			return Translator.getText("IGUI_health_Left_Upper_Arm");
		} else if (bodyPartType == UpperArm_R) {
			return Translator.getText("IGUI_health_Right_Upper_Arm");
		} else if (bodyPartType == Torso_Upper) {
			return Translator.getText("IGUI_health_Upper_Torso");
		} else if (bodyPartType == Torso_Lower) {
			return Translator.getText("IGUI_health_Lower_Torso");
		} else if (bodyPartType == Head) {
			return Translator.getText("IGUI_health_Head");
		} else if (bodyPartType == Neck) {
			return Translator.getText("IGUI_health_Neck");
		} else if (bodyPartType == Groin) {
			return Translator.getText("IGUI_health_Groin");
		} else if (bodyPartType == UpperLeg_L) {
			return Translator.getText("IGUI_health_Left_Thigh");
		} else if (bodyPartType == UpperLeg_R) {
			return Translator.getText("IGUI_health_Right_Thigh");
		} else if (bodyPartType == LowerLeg_L) {
			return Translator.getText("IGUI_health_Left_Shin");
		} else if (bodyPartType == LowerLeg_R) {
			return Translator.getText("IGUI_health_Right_Shin");
		} else if (bodyPartType == Foot_L) {
			return Translator.getText("IGUI_health_Left_Foot");
		} else {
			return bodyPartType == Foot_R ? Translator.getText("IGUI_health_Right_Foot") : Translator.getText("IGUI_health_Unknown_Body_Part");
		}
	}
	public static int ToIndex(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 0;
		} else {
			switch (bodyPartType) {
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
	public static String ToString(BodyPartType bodyPartType) {
		if (bodyPartType == Hand_L) {
			return "Hand_L";
		} else if (bodyPartType == Hand_R) {
			return "Hand_R";
		} else if (bodyPartType == ForeArm_L) {
			return "ForeArm_L";
		} else if (bodyPartType == ForeArm_R) {
			return "ForeArm_R";
		} else if (bodyPartType == UpperArm_L) {
			return "UpperArm_L";
		} else if (bodyPartType == UpperArm_R) {
			return "UpperArm_R";
		} else if (bodyPartType == Torso_Upper) {
			return "Torso_Upper";
		} else if (bodyPartType == Torso_Lower) {
			return "Torso_Lower";
		} else if (bodyPartType == Head) {
			return "Head";
		} else if (bodyPartType == Neck) {
			return "Neck";
		} else if (bodyPartType == Groin) {
			return "Groin";
		} else if (bodyPartType == UpperLeg_L) {
			return "UpperLeg_L";
		} else if (bodyPartType == UpperLeg_R) {
			return "UpperLeg_R";
		} else if (bodyPartType == LowerLeg_L) {
			return "LowerLeg_L";
		} else if (bodyPartType == LowerLeg_R) {
			return "LowerLeg_R";
		} else if (bodyPartType == Foot_L) {
			return "Foot_L";
		} else {
			return bodyPartType == Foot_R ? "Foot_R" : "Unkown Body Part";
		}
	}
	public static float getDamageModifyer(int int1) {
		switch (int1) {
		case 0: 
			return 0.1F;
		
		case 1: 
			return 0.1F;
		
		case 2: 
			return 0.2F;
		
		case 3: 
			return 0.2F;
		
		case 4: 
			return 0.3F;
		
		case 5: 
			return 0.3F;
		
		case 6: 
			return 0.35F;
		
		case 7: 
			return 0.4F;
		
		case 8: 
			return 0.6F;
		
		case 9: 
			return 0.7F;
		
		case 10: 
			return 0.4F;
		
		case 11: 
			return 0.3F;
		
		case 12: 
			return 0.3F;
		
		case 13: 
			return 0.2F;
		
		case 14: 
			return 0.2F;
		
		case 15: 
			return 0.2F;
		
		case 16: 
			return 0.2F;
		
		default: 
			return 1.0F;
		
		}
	}
	public static float getBleedingTimeModifyer(int int1) {
		switch (int1) {
		case 0: 
			return 0.2F;
		
		case 1: 
			return 0.2F;
		
		case 2: 
			return 0.3F;
		
		case 3: 
			return 0.3F;
		
		case 4: 
			return 0.4F;
		
		case 5: 
			return 0.4F;
		
		case 6: 
			return 0.5F;
		
		case 7: 
			return 0.9F;
		
		case 8: 
			return 1.0F;
		
		case 9: 
			return 1.5F;
		
		case 10: 
			return 0.5F;
		
		case 11: 
			return 0.4F;
		
		case 12: 
			return 0.4F;
		
		case 13: 
			return 0.3F;
		
		case 14: 
			return 0.3F;
		
		case 15: 
			return 0.2F;
		
		case 16: 
			return 0.2F;
		
		default: 
			return 1.0F;
		
		}
	}
	public static float GetSkinSurface(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 0.001F;
		} else {
			switch (bodyPartType) {
			case Hand_L: 
			
			case Hand_R: 
				return 0.01F;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				return 0.035F;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				return 0.045F;
			
			case Torso_Upper: 
				return 0.18F;
			
			case Torso_Lower: 
				return 0.12F;
			
			case Head: 
				return 0.08F;
			
			case Neck: 
				return 0.02F;
			
			case Groin: 
				return 0.06F;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				return 0.09F;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				return 0.07F;
			
			case Foot_L: 
			
			case Foot_R: 
				return 0.02F;
			
			default: 
				DebugLog.log("Warning: couldnt get skinSurface for body part \'" + bodyPartType + "\'.");
				return 0.001F;
			
			}
		}
	}
	public static float GetDistToCore(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 0.0F;
		} else {
			switch (bodyPartType) {
			case Hand_L: 
			
			case Hand_R: 
				return 0.8F;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				return 0.6F;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				return 0.3F;
			
			case Torso_Upper: 
				return 0.0F;
			
			case Torso_Lower: 
				return 0.05F;
			
			case Head: 
				return 0.05F;
			
			case Neck: 
				return 0.02F;
			
			case Groin: 
				return 0.3F;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				return 0.45F;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				return 0.75F;
			
			case Foot_L: 
			
			case Foot_R: 
				return 1.0F;
			
			default: 
				DebugLog.log("Warning: couldnt get distToCore for body part \'" + bodyPartType + "\'.");
				return 0.0F;
			
			}
		}
	}
	public static float GetUmbrellaMod(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 1.0F;
		} else {
			switch (bodyPartType) {
			case Hand_L: 
			
			case Hand_R: 
				return 0.35F;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				return 0.3F;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				return 0.25F;
			
			case Torso_Upper: 
				return 0.2F;
			
			case Torso_Lower: 
				return 0.25F;
			
			case Head: 
				return 0.05F;
			
			case Neck: 
				return 0.1F;
			
			case Groin: 
				return 0.3F;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				return 0.55F;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				return 0.75F;
			
			case Foot_L: 
			
			case Foot_R: 
				return 1.0F;
			
			default: 
				return 1.0F;
			
			}
		}
	}
	public static float GetMaxActionPenalty(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 0.0F;
		} else {
			switch (bodyPartType) {
			case Hand_L: 
			
			case Hand_R: 
				return 1.0F;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				return 0.6F;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				return 0.4F;
			
			case Torso_Upper: 
				return 0.2F;
			
			case Torso_Lower: 
				return 0.1F;
			
			case Head: 
				return 0.4F;
			
			case Neck: 
				return 0.05F;
			
			case Groin: 
				return 0.05F;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				return 0.1F;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				return 0.1F;
			
			case Foot_L: 
			
			case Foot_R: 
				return 0.1F;
			
			default: 
				DebugLog.log("Warning: couldnt get maxActionPenalty for body part \'" + bodyPartType + "\'.");
				return 0.0F;
			
			}
		}
	}
	public static float GetMaxMovementPenalty(BodyPartType bodyPartType) {
		if (bodyPartType == null) {
			return 0.0F;
		} else {
			switch (bodyPartType) {
			case Hand_L: 
			
			case Hand_R: 
				return 0.05F;
			
			case ForeArm_L: 
			
			case ForeArm_R: 
				return 0.1F;
			
			case UpperArm_L: 
			
			case UpperArm_R: 
				return 0.1F;
			
			case Torso_Upper: 
				return 0.05F;
			
			case Torso_Lower: 
				return 0.05F;
			
			case Head: 
				return 0.25F;
			
			case Neck: 
				return 0.05F;
			
			case Groin: 
				return 0.15F;
			
			case UpperLeg_L: 
			
			case UpperLeg_R: 
				return 0.4F;
			
			case LowerLeg_L: 
			
			case LowerLeg_R: 
				return 0.6F;
			
			case Foot_L: 
			
			case Foot_R: 
				return 1.0F;
			
			default: 
				DebugLog.log("Warning: couldnt get maxMovementPenalty for body part \'" + bodyPartType + "\'.");
				return 0.0F;
			
			}
		}
	}
	public String getBandageModel() {
		switch (this) {
		case Hand_L: 
			return "Base.Bandage_LeftHand";
		
		case Hand_R: 
			return "Base.Bandage_RightHand";
		
		case ForeArm_L: 
			return "Base.Bandage_LeftLowerArm";
		
		case ForeArm_R: 
			return "Base.Bandage_RightLowerArm";
		
		case UpperArm_L: 
			return "Base.Bandage_LeftUpperArm";
		
		case UpperArm_R: 
			return "Base.Bandage_RightUpperArm";
		
		case Torso_Upper: 
			return "Base.Bandage_Chest";
		
		case Torso_Lower: 
			return "Base.Bandage_Abdomen";
		
		case Head: 
			return "Base.Bandage_Head";
		
		case Neck: 
			return "Base.Bandage_Neck";
		
		case Groin: 
			return "Base.Bandage_Groin";
		
		case UpperLeg_L: 
			return "Base.Bandage_LeftUpperLeg";
		
		case UpperLeg_R: 
			return "Base.Bandage_RightUpperLeg";
		
		case LowerLeg_L: 
			return "Base.Bandage_LeftLowerLeg";
		
		case LowerLeg_R: 
			return "Base.Bandage_RightLowerLeg";
		
		case Foot_L: 
			return "Base.Bandage_LeftFoot";
		
		case Foot_R: 
			return "Base.Bandage_RightFoot";
		
		case MAX: 
		
		default: 
			return null;
		
		}
	}
	public String getBiteWoundModel(boolean boolean1) {
		String string = "Female";
		if (!boolean1) {
			string = "Male";
		}

		switch (this) {
		case Hand_L: 
			return "Base.Wound_LHand_Bite_" + string;
		
		case Hand_R: 
			return "Base.Wound_RHand_Bite_" + string;
		
		case ForeArm_L: 
			return "Base.Wound_LForearm_Bite_" + string;
		
		case ForeArm_R: 
			return "Base.Wound_RForearm_Bite_" + string;
		
		case UpperArm_L: 
			return "Base.Wound_LUArm_Bite_" + string;
		
		case UpperArm_R: 
			return "Base.Wound_RUArm_Bite_" + string;
		
		case Torso_Upper: 
			return "Base.Wound_Chest_Bite_" + string;
		
		case Torso_Lower: 
			return "Base.Wound_Abdomen_Bite_" + string;
		
		case Head: 
			return "Base.Wound_Neck_Bite_" + string;
		
		case Neck: 
			return "Base.Wound_Neck_Bite_" + string;
		
		case Groin: 
			return "Base.Wound_Groin_Bite_" + string;
		
		case UpperLeg_L: 
			return null;
		
		case UpperLeg_R: 
			return null;
		
		case LowerLeg_L: 
			return null;
		
		case LowerLeg_R: 
			return null;
		
		case Foot_L: 
			return null;
		
		case Foot_R: 
			return null;
		
		case MAX: 
		
		default: 
			return null;
		
		}
	}
	public String getScratchWoundModel(boolean boolean1) {
		String string = "Female";
		if (!boolean1) {
			string = "Male";
		}

		switch (this) {
		case Hand_L: 
			return "Base.Wound_LHand_Scratch_" + string;
		
		case Hand_R: 
			return "Base.Wound_RHand_Scratch_" + string;
		
		case ForeArm_L: 
			return "Base.Wound_LForearm_Scratch_" + string;
		
		case ForeArm_R: 
			return "Base.Wound_RForearm_Scratch_" + string;
		
		case UpperArm_L: 
			return "Base.Wound_LUArm_Scratch_" + string;
		
		case UpperArm_R: 
			return "Base.Wound_RUArm_Scratch_" + string;
		
		case Torso_Upper: 
			return "Base.Wound_Chest_Scratch_" + string;
		
		case Torso_Lower: 
			return "Base.Wound_Abdomen_Scratch_" + string;
		
		case Head: 
			return "Base.Wound_Neck_Scratch_" + string;
		
		case Neck: 
			return "Base.Wound_Neck_Scratch_" + string;
		
		case Groin: 
			return "Base.Wound_Groin_Scratch_" + string;
		
		case UpperLeg_L: 
			return null;
		
		case UpperLeg_R: 
			return null;
		
		case LowerLeg_L: 
			return null;
		
		case LowerLeg_R: 
			return null;
		
		case Foot_L: 
			return null;
		
		case Foot_R: 
			return null;
		
		case MAX: 
		
		default: 
			return null;
		
		}
	}
	public String getCutWoundModel(boolean boolean1) {
		String string = "Female";
		if (!boolean1) {
			string = "Male";
		}

		switch (this) {
		case Hand_L: 
			return "Base.Wound_LHand_Laceration_" + string;
		
		case Hand_R: 
			return "Base.Wound_RHand_Laceration_" + string;
		
		case ForeArm_L: 
			return "Base.Wound_LForearm_Laceration_" + string;
		
		case ForeArm_R: 
			return "Base.Wound_RForearm_Laceration_" + string;
		
		case UpperArm_L: 
			return "Base.Wound_LUArm_Laceration_" + string;
		
		case UpperArm_R: 
			return "Base.Wound_RUArm_Laceration_" + string;
		
		case Torso_Upper: 
			return "Base.Wound_Chest_Laceration_" + string;
		
		case Torso_Lower: 
			return "Base.Wound_Abdomen_Laceration_" + string;
		
		case Head: 
			return "Base.Wound_Neck_Laceration_" + string;
		
		case Neck: 
			return "Base.Wound_Neck_Laceration_" + string;
		
		case Groin: 
			return "Base.Wound_Groin_Laceration_" + string;
		
		case UpperLeg_L: 
			return null;
		
		case UpperLeg_R: 
			return null;
		
		case LowerLeg_L: 
			return null;
		
		case LowerLeg_R: 
			return null;
		
		case Foot_L: 
			return null;
		
		case Foot_R: 
			return null;
		
		case MAX: 
		
		default: 
			return null;
		
		}
	}
	public static BodyPartType getRandom() {
		return FromIndex(OutfitRNG.Next(0, MAX.index()));
	}
	private static BodyPartType[] $values() {
		return new BodyPartType[]{Hand_L, Hand_R, ForeArm_L, ForeArm_R, UpperArm_L, UpperArm_R, Torso_Upper, Torso_Lower, Head, Neck, Groin, UpperLeg_L, UpperLeg_R, LowerLeg_L, LowerLeg_R, Foot_L, Foot_R, MAX};
	}
}
