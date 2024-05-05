package zombie.network;


public class NetworkVariables {

	public static enum ZombieState {

		Attack,
		AttackNetwork,
		AttackVehicle,
		AttackVehicleNetwork,
		Bumped,
		ClimbFence,
		ClimbWindow,
		EatBody,
		FaceTarget,
		FakeDead,
		FakeDeadAttack,
		FakeDeadAttackNetwork,
		FallDown,
		Falling,
		GetDown,
		Getup,
		HitReaction,
		HitReactionHit,
		HitWhileStaggered,
		Idle,
		Lunge,
		LungeNetwork,
		OnGround,
		PathFind,
		Sitting,
		StaggerBack,
		Thump,
		TurnAlerted,
		WalkToward,
		WalkTowardNetwork,
		FakeZombieStay,
		FakeZombieNormal,
		FakeZombieAttack,
		zombieState;

		private ZombieState(String string) {
			this.zombieState = string;
		}
		public String toString() {
			return this.zombieState;
		}
		public static NetworkVariables.ZombieState fromString(String string) {
			NetworkVariables.ZombieState[] zombieStateArray = values();
			int int1 = zombieStateArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.ZombieState zombieState = zombieStateArray[int2];
				if (zombieState.zombieState.equalsIgnoreCase(string)) {
					return zombieState;
				}
			}

			return Idle;
		}
		public static NetworkVariables.ZombieState fromByte(Byte Byte1) {
			NetworkVariables.ZombieState[] zombieStateArray = values();
			int int1 = zombieStateArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.ZombieState zombieState = zombieStateArray[int2];
				if (zombieState.ordinal() == Byte1) {
					return zombieState;
				}
			}

			return Idle;
		}
		private static NetworkVariables.ZombieState[] $values() {
			return new NetworkVariables.ZombieState[]{Attack, AttackNetwork, AttackVehicle, AttackVehicleNetwork, Bumped, ClimbFence, ClimbWindow, EatBody, FaceTarget, FakeDead, FakeDeadAttack, FakeDeadAttackNetwork, FallDown, Falling, GetDown, Getup, HitReaction, HitReactionHit, HitWhileStaggered, Idle, Lunge, LungeNetwork, OnGround, PathFind, Sitting, StaggerBack, Thump, TurnAlerted, WalkToward, WalkTowardNetwork, FakeZombieStay, FakeZombieNormal, FakeZombieAttack};
		}
	}
	public static enum ThumpType {

		TTNone,
		TTDoor,
		TTClaw,
		TTBang,
		thumpType;

		private ThumpType(String string) {
			this.thumpType = string;
		}
		public String toString() {
			return this.thumpType;
		}
		public static NetworkVariables.ThumpType fromString(String string) {
			NetworkVariables.ThumpType[] thumpTypeArray = values();
			int int1 = thumpTypeArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.ThumpType thumpType = thumpTypeArray[int2];
				if (thumpType.thumpType.equalsIgnoreCase(string)) {
					return thumpType;
				}
			}

			return TTNone;
		}
		public static NetworkVariables.ThumpType fromByte(Byte Byte1) {
			NetworkVariables.ThumpType[] thumpTypeArray = values();
			int int1 = thumpTypeArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.ThumpType thumpType = thumpTypeArray[int2];
				if (thumpType.ordinal() == Byte1) {
					return thumpType;
				}
			}

			return TTNone;
		}
		private static NetworkVariables.ThumpType[] $values() {
			return new NetworkVariables.ThumpType[]{TTNone, TTDoor, TTClaw, TTBang};
		}
	}
	public static enum WalkType {

		WT1,
		WT2,
		WT3,
		WT4,
		WT5,
		WTSprint1,
		WTSprint2,
		WTSprint3,
		WTSprint4,
		WTSprint5,
		WTSlow1,
		WTSlow2,
		WTSlow3,
		walkType;

		private WalkType(String string) {
			this.walkType = string;
		}
		public String toString() {
			return this.walkType;
		}
		public static NetworkVariables.WalkType fromString(String string) {
			NetworkVariables.WalkType[] walkTypeArray = values();
			int int1 = walkTypeArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.WalkType walkType = walkTypeArray[int2];
				if (walkType.walkType.equalsIgnoreCase(string)) {
					return walkType;
				}
			}

			return WT1;
		}
		public static NetworkVariables.WalkType fromByte(byte byte1) {
			NetworkVariables.WalkType[] walkTypeArray = values();
			int int1 = walkTypeArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.WalkType walkType = walkTypeArray[int2];
				if (walkType.ordinal() == byte1) {
					return walkType;
				}
			}

			return WT1;
		}
		private static NetworkVariables.WalkType[] $values() {
			return new NetworkVariables.WalkType[]{WT1, WT2, WT3, WT4, WT5, WTSprint1, WTSprint2, WTSprint3, WTSprint4, WTSprint5, WTSlow1, WTSlow2, WTSlow3};
		}
	}
	public static enum PredictionTypes {

		None,
		Moving,
		Static,
		Thump,
		Climb,
		Lunge,
		LungeHalf,
		Walk,
		WalkHalf,
		PathFind;

		public static NetworkVariables.PredictionTypes fromByte(byte byte1) {
			NetworkVariables.PredictionTypes[] predictionTypesArray = values();
			int int1 = predictionTypesArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				NetworkVariables.PredictionTypes predictionTypes = predictionTypesArray[int2];
				if (predictionTypes.ordinal() == byte1) {
					return predictionTypes;
				}
			}

			return None;
		}
		private static NetworkVariables.PredictionTypes[] $values() {
			return new NetworkVariables.PredictionTypes[]{None, Moving, Static, Thump, Climb, Lunge, LungeHalf, Walk, WalkHalf, PathFind};
		}
	}
}
