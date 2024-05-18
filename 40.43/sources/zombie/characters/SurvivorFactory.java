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

	public static SurvivorDesc[] CreateFamily(int int1) {
		SurvivorDesc[] survivorDescArray = new SurvivorDesc[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			survivorDescArray[int2] = CreateSurvivor();
			if (int2 > 0) {
				survivorDescArray[int2].surname = survivorDescArray[0].surname;
			}
		}

		return survivorDescArray;
	}

	public static SurvivorDesc CreateSurvivor() {
		switch (Rand.Next(3)) {
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

	public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType survivorType, boolean boolean1) {
		SurvivorDesc survivorDesc = new SurvivorDesc();
		survivorDesc.setType(survivorType);
		IsoGameCharacter.SurvivorMap.put(survivorDesc.ID, survivorDesc);
		survivorDesc.setFemale(boolean1);
		randomName(survivorDesc);
		Integer integer = Rand.Next(4);
		survivorDesc.skinpal = "Skin_0" + integer.toString();
		if (survivorDesc.isFemale()) {
			survivorDesc.setTorsoNumber(Rand.Next(2));
			setTorso(survivorDesc);
			switch (Rand.Next(2)) {
			case 0: 
				survivorDesc.top = "Blouse";
				break;
			
			case 1: 
				survivorDesc.top = "Vest";
			
			}

			switch (Rand.Next(2)) {
			case 0: 
				survivorDesc.bottoms = "Trousers";
				break;
			
			case 1: 
				survivorDesc.bottoms = "Skirt";
			
			}

			survivorDesc.setHairNumber(Rand.Next(5));
			setHairNoColor(survivorDesc);
			survivorDesc.hair = survivorDesc.hairNoColor + "White";
		} else {
			survivorDesc.setTorsoNumber(Rand.Next(8));
			setTorso(survivorDesc);
			switch (Rand.Next(2)) {
			case 0: 
				survivorDesc.top = "Shirt";
				break;
			
			case 1: 
				survivorDesc.top = "Vest";
			
			}

			survivorDesc.setHairNumber(Rand.Next(6));
			setHairNoColor(survivorDesc);
			String string = "";
			string = "White";
			if (survivorDesc.hairNoColor != "none") {
				survivorDesc.hair = survivorDesc.hairNoColor + "White";
			}

			if (Rand.Next(2) == 0) {
				survivorDesc.setBeardNumber(Rand.Next(4));
				setBeardNoColor(survivorDesc);
				survivorDesc.extra.add(survivorDesc.getBeardNoColor() + "White");
			}
		}

		survivorDesc.toppal = survivorDesc.top + "_White";
		survivorDesc.bottomspal = survivorDesc.bottoms + "_White";
		survivorDesc.trouserColor = new Color((Color)SurvivorDesc.TrouserCommonColors.get(Rand.Next(SurvivorDesc.TrouserCommonColors.size())));
		survivorDesc.hairColor = new Color((Color)SurvivorDesc.HairCommonColors.get(Rand.Next(SurvivorDesc.HairCommonColors.size())));
		survivorDesc.topColor = new Color(30 + Rand.Next(225), 30 + Rand.Next(225), 30 + Rand.Next(225));
		survivorDesc.skinColor = SurvivorDesc.getRandomSkinColor();
		return survivorDesc;
	}

	public static void setBeardNoColor(SurvivorDesc survivorDesc) {
		String string = "";
		switch (survivorDesc.getBeardNumber()) {
		case 0: 
			string = "Beard_Full_";
			break;
		
		case 1: 
			string = "Beard_Chops_";
			break;
		
		case 2: 
			string = "Beard_Only_";
			break;
		
		case 3: 
			string = "Beard_Goatee_";
		
		}
		survivorDesc.beardNoColor = string;
	}

	public static void setTorso(SurvivorDesc survivorDesc) {
		if (survivorDesc.isFemale()) {
			if (survivorDesc.getTorsoNumber() == 0) {
				survivorDesc.torso = "Kate";
			} else if (survivorDesc.getTorsoNumber() == 1) {
				survivorDesc.torso = "Kate_2";
			}
		} else if (survivorDesc.getTorsoNumber() == 0) {
			survivorDesc.torso = "Male";
		} else {
			survivorDesc.torso = "Male_" + (survivorDesc.getTorsoNumber() + 1);
		}
	}

	public static void setHairNoColor(SurvivorDesc survivorDesc) {
		if (survivorDesc.isFemale()) {
			switch (survivorDesc.getHairNumber()) {
			case 0: 
				survivorDesc.hairNoColor = "F_Hair_Bob_";
				break;
			
			case 1: 
				survivorDesc.hairNoColor = "F_Hair_Long_";
				break;
			
			case 2: 
				survivorDesc.hairNoColor = "F_Hair_Long2_";
				break;
			
			case 3: 
				survivorDesc.hairNoColor = "F_Hair_OverEye_";
				break;
			
			case 4: 
				survivorDesc.hairNoColor = "F_Hair_";
				break;
			
			case 5: 
				survivorDesc.hairNoColor = "F_Hair_";
			
			}
		} else {
			switch (survivorDesc.getHairNumber()) {
			case 0: 
				survivorDesc.hairNoColor = "Hair_Baldspot_";
				break;
			
			case 1: 
				survivorDesc.hairNoColor = "Hair_Picard_";
				break;
			
			case 2: 
				survivorDesc.hairNoColor = "Hair_Recede_";
				break;
			
			case 3: 
				survivorDesc.hairNoColor = "Hair_Short_";
				break;
			
			case 4: 
				survivorDesc.hairNoColor = "Hair_Messy_";
				break;
			
			case 5: 
				survivorDesc.hairNoColor = "none";
			
			}
		}
	}

	public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType survivorType) {
		return CreateSurvivor(survivorType, Rand.Next(2) == 0);
	}

	public static SurvivorDesc[] CreateSurvivorGroup(int int1) {
		SurvivorDesc[] survivorDescArray = new SurvivorDesc[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			survivorDescArray[int2] = CreateSurvivor();
		}

		return survivorDescArray;
	}

	public static IsoSurvivor InstansiateInCell(SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3) {
		survivorDesc.Instance = new IsoSurvivor(survivorDesc, cell, int1, int2, int3);
		return (IsoSurvivor)survivorDesc.Instance;
	}

	public static void randomName(SurvivorDesc survivorDesc) {
		if (survivorDesc.isFemale()) {
			survivorDesc.forename = (String)FemaleForenames.get(Rand.Next(FemaleForenames.size()));
		} else {
			survivorDesc.forename = (String)MaleForenames.get(Rand.Next(MaleForenames.size()));
		}

		survivorDesc.surname = (String)Surnames.get(Rand.Next(Surnames.size()));
	}

	public static void addSurname(String string) {
		Surnames.add(string);
	}

	public static void addFemaleForename(String string) {
		FemaleForenames.add(string);
	}

	public static void addMaleForename(String string) {
		MaleForenames.add(string);
	}
	public static enum SurvivorType {

		Friendly,
		Neutral,
		Aggressive;
	}
}
