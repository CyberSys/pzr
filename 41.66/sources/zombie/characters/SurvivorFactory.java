package zombie.characters;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;


public final class SurvivorFactory {
	public static final ArrayList FemaleForenames = new ArrayList();
	public static final ArrayList MaleForenames = new ArrayList();
	public static final ArrayList Surnames = new ArrayList();

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
		IsoGameCharacter.getSurvivorMap().put(survivorDesc.ID, survivorDesc);
		survivorDesc.setFemale(boolean1);
		randomName(survivorDesc);
		if (survivorDesc.isFemale()) {
			setTorso(survivorDesc);
		} else {
			setTorso(survivorDesc);
		}

		return survivorDesc;
	}

	public static void setTorso(SurvivorDesc survivorDesc) {
		if (survivorDesc.isFemale()) {
			survivorDesc.torso = "Kate";
		} else {
			survivorDesc.torso = "Male";
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

		private static SurvivorFactory.SurvivorType[] $values() {
			return new SurvivorFactory.SurvivorType[]{Friendly, Neutral, Aggressive};
		}
	}
}
