package zombie.characters.BodyDamage;



public enum Metabolics {

	Sleeping,
	SeatedResting,
	StandingAtRest,
	SedentaryActivity,
	Default,
	DrivingCar,
	LightDomestic,
	HeavyDomestic,
	DefaultExercise,
	UsingTools,
	LightWork,
	MediumWork,
	DiggingSpade,
	HeavyWork,
	ForestryAxe,
	Walking2kmh,
	Walking5kmh,
	Running10kmh,
	Running15kmh,
	JumpFence,
	ClimbRope,
	Fitness,
	FitnessHeavy,
	MAX,
	met;

	private Metabolics(float float1) {
		this.met = float1;
	}
	public float getMet() {
		return this.met;
	}
	public float getWm2() {
		return MetToWm2(this.met);
	}
	public float getW() {
		return MetToW(this.met);
	}
	public float getBtuHr() {
		return MetToBtuHr(this.met);
	}
	public static float MetToWm2(float float1) {
		return 58.0F * float1;
	}
	public static float MetToW(float float1) {
		return MetToWm2(float1) * 1.8F;
	}
	public static float MetToBtuHr(float float1) {
		return 356.0F * float1;
	}
	private static Metabolics[] $values() {
		return new Metabolics[]{Sleeping, SeatedResting, StandingAtRest, SedentaryActivity, Default, DrivingCar, LightDomestic, HeavyDomestic, DefaultExercise, UsingTools, LightWork, MediumWork, DiggingSpade, HeavyWork, ForestryAxe, Walking2kmh, Walking5kmh, Running10kmh, Running15kmh, JumpFence, ClimbRope, Fitness, FitnessHeavy, MAX};
	}
}
