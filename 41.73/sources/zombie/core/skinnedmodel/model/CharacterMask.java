package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import zombie.characterTextures.BloodBodyPartType;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.Pool;


public final class CharacterMask {
	private final boolean[] m_visibleFlags = createFlags(CharacterMask.Part.values().length, true);

	public boolean isBloodBodyPartVisible(BloodBodyPartType bloodBodyPartType) {
		CharacterMask.Part[] partArray = bloodBodyPartType.getCharacterMaskParts();
		int int1 = partArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			CharacterMask.Part part = partArray[int2];
			if (this.isPartVisible(part)) {
				return true;
			}
		}

		return false;
	}

	private static boolean[] createFlags(int int1, boolean boolean1) {
		boolean[] booleanArray = new boolean[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			booleanArray[int2] = boolean1;
		}

		return booleanArray;
	}

	public void setAllVisible(boolean boolean1) {
		Arrays.fill(this.m_visibleFlags, boolean1);
	}

	public void copyFrom(CharacterMask characterMask) {
		System.arraycopy(characterMask.m_visibleFlags, 0, this.m_visibleFlags, 0, this.m_visibleFlags.length);
	}

	public void setPartVisible(CharacterMask.Part part, boolean boolean1) {
		if (part.hasSubdivisions()) {
			CharacterMask.Part[] partArray = part.subDivisions();
			int int1 = partArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				CharacterMask.Part part2 = partArray[int2];
				this.setPartVisible(part2, boolean1);
			}
		} else {
			this.m_visibleFlags[part.getValue()] = boolean1;
		}
	}

	public void setPartsVisible(ArrayList arrayList, boolean boolean1) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			int int2 = (Integer)arrayList.get(int1);
			CharacterMask.Part part = CharacterMask.Part.fromInt(int2);
			if (part == null) {
				if (DebugLog.isEnabled(DebugType.Clothing)) {
					DebugLog.Clothing.warn("MaskValue out of bounds: " + int2);
				}
			} else {
				this.setPartVisible(part, boolean1);
			}
		}
	}

	public boolean isPartVisible(CharacterMask.Part part) {
		if (part == null) {
			return false;
		} else if (!part.hasSubdivisions()) {
			return this.m_visibleFlags[part.getValue()];
		} else {
			boolean boolean1 = true;
			for (int int1 = 0; boolean1 && int1 < part.subDivisions().length; ++int1) {
				CharacterMask.Part part2 = part.subDivisions()[int1];
				boolean1 = this.m_visibleFlags[part2.getValue()];
			}

			return boolean1;
		}
	}

	public boolean isTorsoVisible() {
		return this.isPartVisible(CharacterMask.Part.Torso);
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{VisibleFlags:(" + this.contentsToString() + ")}";
	}

	public String contentsToString() {
		if (this.isAllVisible()) {
			return "All Visible";
		} else if (this.isNothingVisible()) {
			return "Nothing Visible";
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			int int1 = 0;
			for (int int2 = 0; int1 < CharacterMask.Part.leaves().length; ++int1) {
				CharacterMask.Part part = CharacterMask.Part.leaves()[int1];
				if (this.isPartVisible(part)) {
					if (int2 > 0) {
						stringBuilder.append(',');
					}

					stringBuilder.append(part);
					++int2;
				}
			}

			return stringBuilder.toString();
		}
	}

	private boolean isAll(boolean boolean1) {
		boolean boolean2 = true;
		int int1 = 0;
		for (int int2 = CharacterMask.Part.leaves().length; boolean2 && int1 < int2; ++int1) {
			CharacterMask.Part part = CharacterMask.Part.leaves()[int1];
			boolean2 = this.isPartVisible(part) == boolean1;
		}

		return boolean2;
	}

	public boolean isNothingVisible() {
		return this.isAll(false);
	}

	public boolean isAllVisible() {
		return this.isAll(true);
	}

	public void forEachVisible(Consumer consumer) {
		try {
			for (int int1 = 0; int1 < CharacterMask.Part.leaves().length; ++int1) {
				CharacterMask.Part part = CharacterMask.Part.leaves()[int1];
				if (this.isPartVisible(part)) {
					consumer.accept(part);
				}
			}
		} finally {
			Pool.tryRelease((Object)consumer);
		}
	}

	public static enum Part {

		Head,
		Torso,
		Pelvis,
		LeftArm,
		LeftHand,
		RightArm,
		RightHand,
		LeftLeg,
		LeftFoot,
		RightLeg,
		RightFoot,
		Dress,
		Chest,
		Waist,
		Belt,
		Crotch,
		value,
		parent,
		isSubdivided,
		subDivisions,
		m_bloodBodyPartTypes,
		s_leaves;

		private Part(int int1) {
			this.value = int1;
			this.parent = null;
			this.isSubdivided = false;
		}
		private Part(int int1, CharacterMask.Part part) {
			this.value = int1;
			this.parent = part;
			this.isSubdivided = false;
		}
		private Part(int int1, boolean boolean1) {
			this.value = int1;
			this.parent = null;
			this.isSubdivided = boolean1;
		}
		public static int count() {
			return values().length;
		}
		public static CharacterMask.Part[] leaves() {
			return s_leaves;
		}
		public static CharacterMask.Part fromInt(int int1) {
			return int1 >= 0 && int1 < count() ? values()[int1] : null;
		}
		public int getValue() {
			return this.value;
		}
		public CharacterMask.Part getParent() {
			return this.parent;
		}
		public boolean isSubdivision() {
			return this.parent != null;
		}
		public boolean hasSubdivisions() {
			return this.isSubdivided;
		}
		public CharacterMask.Part[] subDivisions() {
			if (this.subDivisions != null) {
				return this.subDivisions;
			} else {
				if (!this.isSubdivided) {
					this.subDivisions = new CharacterMask.Part[0];
				}

				ArrayList arrayList = new ArrayList();
				CharacterMask.Part[] partArray = values();
				int int1 = partArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					CharacterMask.Part part = partArray[int2];
					if (part.parent == this) {
						arrayList.add(part);
					}
				}

				this.subDivisions = (CharacterMask.Part[])arrayList.toArray(new CharacterMask.Part[0]);
				return this.subDivisions;
			}
		}
		private static CharacterMask.Part[] leavesInternal() {
			ArrayList arrayList = new ArrayList();
			CharacterMask.Part[] partArray = values();
			int int1 = partArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				CharacterMask.Part part = partArray[int2];
				if (!part.hasSubdivisions()) {
					arrayList.add(part);
				}
			}

			return (CharacterMask.Part[])arrayList.toArray(new CharacterMask.Part[0]);
		}
		public BloodBodyPartType[] getBloodBodyPartTypes() {
			if (this.m_bloodBodyPartTypes != null) {
				return this.m_bloodBodyPartTypes;
			} else {
				ArrayList arrayList = new ArrayList();
				switch (this) {
				case Head: 
					arrayList.add(BloodBodyPartType.Head);
					break;
				
				case Torso: 
					arrayList.add(BloodBodyPartType.Torso_Upper);
					arrayList.add(BloodBodyPartType.Torso_Lower);
					break;
				
				case Pelvis: 
					arrayList.add(BloodBodyPartType.UpperLeg_L);
					arrayList.add(BloodBodyPartType.UpperLeg_R);
					arrayList.add(BloodBodyPartType.Groin);
					break;
				
				case LeftArm: 
					arrayList.add(BloodBodyPartType.UpperArm_L);
					arrayList.add(BloodBodyPartType.ForeArm_L);
					break;
				
				case LeftHand: 
					arrayList.add(BloodBodyPartType.Hand_L);
					break;
				
				case RightArm: 
					arrayList.add(BloodBodyPartType.UpperArm_R);
					arrayList.add(BloodBodyPartType.ForeArm_R);
					break;
				
				case RightHand: 
					arrayList.add(BloodBodyPartType.Hand_R);
					break;
				
				case LeftLeg: 
					arrayList.add(BloodBodyPartType.UpperLeg_L);
					arrayList.add(BloodBodyPartType.LowerLeg_L);
					break;
				
				case LeftFoot: 
					arrayList.add(BloodBodyPartType.Foot_L);
					break;
				
				case RightLeg: 
					arrayList.add(BloodBodyPartType.UpperLeg_R);
					arrayList.add(BloodBodyPartType.LowerLeg_R);
					break;
				
				case RightFoot: 
					arrayList.add(BloodBodyPartType.Foot_R);
				
				case Dress: 
				
				default: 
					break;
				
				case Chest: 
					arrayList.add(BloodBodyPartType.Torso_Upper);
					break;
				
				case Waist: 
					arrayList.add(BloodBodyPartType.Torso_Lower);
					break;
				
				case Belt: 
					arrayList.add(BloodBodyPartType.UpperLeg_L);
					arrayList.add(BloodBodyPartType.UpperLeg_R);
					break;
				
				case Crotch: 
					arrayList.add(BloodBodyPartType.Groin);
				
				}

				this.m_bloodBodyPartTypes = new BloodBodyPartType[arrayList.size()];
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					this.m_bloodBodyPartTypes[int1] = (BloodBodyPartType)arrayList.get(int1);
				}

				return this.m_bloodBodyPartTypes;
			}
		}
		private static CharacterMask.Part[] $values() {
			return new CharacterMask.Part[]{Head, Torso, Pelvis, LeftArm, LeftHand, RightArm, RightHand, LeftLeg, LeftFoot, RightLeg, RightFoot, Dress, Chest, Waist, Belt, Crotch};
		}
	}
}
