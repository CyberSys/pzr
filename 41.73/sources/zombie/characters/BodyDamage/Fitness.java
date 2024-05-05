package zombie.characters.BodyDamage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;


public final class Fitness {
	private IsoGameCharacter parent = null;
	private HashMap regularityMap = new HashMap();
	private int fitnessLvl = 0;
	private int strLvl = 0;
	private final HashMap stiffnessTimerMap = new HashMap();
	private final HashMap stiffnessIncMap = new HashMap();
	private final ArrayList bodypartToIncStiffness = new ArrayList();
	private final HashMap exercises = new HashMap();
	private final HashMap exeTimer = new HashMap();
	private int lastUpdate = -1;
	private Fitness.FitnessExercise currentExe;
	private static final int HOURS_FOR_STIFFNESS = 12;
	private static final float BASE_STIFFNESS_INC = 0.5F;
	private static final float BASE_ENDURANCE_RED = 0.015F;
	private static final float BASE_REGULARITY_INC = 0.08F;
	private static final float BASE_REGULARITY_DEC = 0.002F;
	private static final float BASE_PAIN_INC = 2.5F;

	public Fitness(IsoGameCharacter gameCharacter) {
		this.setParent(gameCharacter);
	}

	public void update() {
		int int1 = GameTime.getInstance().getMinutes() / 10;
		if (this.lastUpdate == -1) {
			this.lastUpdate = int1;
		}

		if (int1 != this.lastUpdate) {
			this.lastUpdate = int1;
			ArrayList arrayList = new ArrayList();
			this.decreaseRegularity();
			Iterator iterator = this.stiffnessTimerMap.keySet().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				Integer integer = (Integer)this.stiffnessTimerMap.get(string);
				integer = integer - 1;
				if (integer <= 0) {
					arrayList.add(string);
					this.bodypartToIncStiffness.add(string);
				} else {
					this.stiffnessTimerMap.put(string, integer);
				}
			}

			int int2;
			for (int2 = 0; int2 < arrayList.size(); ++int2) {
				this.stiffnessTimerMap.remove(arrayList.get(int2));
			}

			for (int2 = 0; int2 < this.bodypartToIncStiffness.size(); ++int2) {
				String string2 = (String)this.bodypartToIncStiffness.get(int2);
				Float Float1 = (Float)this.stiffnessIncMap.get(string2);
				if (Float1 == null) {
					return;
				}

				Float1 = Float1 - 1.0F;
				this.increasePain(string2);
				if (Float1 <= 0.0F) {
					this.bodypartToIncStiffness.remove(int2);
					this.stiffnessIncMap.remove(string2);
					--int2;
				} else {
					this.stiffnessIncMap.put(string2, Float1);
				}
			}
		}
	}

	private void decreaseRegularity() {
		Iterator iterator = this.regularityMap.keySet().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			if (this.exeTimer.containsKey(string) && GameTime.getInstance().getCalender().getTimeInMillis() - (Long)this.exeTimer.get(string) > 86400000L) {
				float float1 = (Float)this.regularityMap.get(string);
				float1 -= 0.002F;
				this.regularityMap.put(string, float1);
			}
		}
	}

	private void increasePain(String string) {
		int int1;
		BodyPart bodyPart;
		if ("arms".equals(string)) {
			for (int1 = BodyPartType.ForeArm_L.index(); int1 < BodyPartType.UpperArm_R.index() + 1; ++int1) {
				bodyPart = this.parent.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
				bodyPart.setStiffness(bodyPart.getStiffness() + 2.5F);
			}
		}

		if ("legs".equals(string)) {
			for (int1 = BodyPartType.UpperLeg_L.index(); int1 < BodyPartType.LowerLeg_R.index() + 1; ++int1) {
				bodyPart = this.parent.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
				bodyPart.setStiffness(bodyPart.getStiffness() + 2.5F);
			}
		}

		BodyPart bodyPart2;
		if ("chest".equals(string)) {
			bodyPart2 = this.parent.getBodyDamage().getBodyPart(BodyPartType.Torso_Upper);
			bodyPart2.setStiffness(bodyPart2.getStiffness() + 2.5F);
		}

		if ("abs".equals(string)) {
			bodyPart2 = this.parent.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
			bodyPart2.setStiffness(bodyPart2.getStiffness() + 2.5F);
		}
	}

	public void setCurrentExercise(String string) {
		this.currentExe = (Fitness.FitnessExercise)this.exercises.get(string);
	}

	public void exerciseRepeat() {
		this.fitnessLvl = this.parent.getPerkLevel(PerkFactory.Perks.Fitness);
		this.strLvl = this.parent.getPerkLevel(PerkFactory.Perks.Strength);
		this.incRegularity();
		this.reduceEndurance();
		this.incFutureStiffness();
		this.incStats();
		this.updateExeTimer();
	}

	private void updateExeTimer() {
		this.exeTimer.put(this.currentExe.type, GameTime.getInstance().getCalender().getTimeInMillis());
	}

	public void incRegularity() {
		float float1 = 0.08F;
		byte byte1 = 4;
		double double1 = Math.log((double)((float)this.fitnessLvl / 5.0F + (float)byte1));
		float1 = (float)((double)float1 * (Math.log((double)(byte1 + 1)) / double1));
		Float Float1 = (Float)this.regularityMap.get(this.currentExe.type);
		if (Float1 == null) {
			Float1 = 0.0F;
		}

		Float1 = Float1 + float1;
		Float1 = Math.min(Math.max(Float1, 0.0F), 100.0F);
		this.regularityMap.put(this.currentExe.type, Float1);
	}

	public void reduceEndurance() {
		float float1 = 0.015F;
		Float Float1 = (Float)this.regularityMap.get(this.currentExe.type);
		if (Float1 == null) {
			Float1 = 0.0F;
		}

		byte byte1 = 50;
		double double1 = Math.log((double)(Float1 / 50.0F + (float)byte1));
		float1 = (float)((double)float1 * (double1 / Math.log((double)(byte1 + 1))));
		if (this.currentExe.metabolics == Metabolics.FitnessHeavy) {
			float1 *= 1.3F;
		}

		float1 *= (float)(1 + this.parent.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) / 3);
		this.parent.getStats().setEndurance(this.parent.getStats().getEndurance() - float1);
	}

	public void incFutureStiffness() {
		Float Float1 = (Float)this.regularityMap.get(this.currentExe.type);
		if (Float1 == null) {
			Float1 = 0.0F;
		}

		for (int int1 = 0; int1 < this.currentExe.stiffnessInc.size(); ++int1) {
			float float1 = 0.5F;
			String string = (String)this.currentExe.stiffnessInc.get(int1);
			if (!this.stiffnessTimerMap.containsKey(string) && !this.bodypartToIncStiffness.contains(string)) {
				this.stiffnessTimerMap.put(string, 72);
			}

			Float Float2 = (Float)this.stiffnessIncMap.get(string);
			if (Float2 == null) {
				Float2 = 0.0F;
			}

			float1 *= (120.0F - Float1) / 170.0F;
			if (this.currentExe.metabolics == Metabolics.FitnessHeavy) {
				float1 *= 1.3F;
			}

			float1 *= (float)(1 + this.parent.getMoodles().getMoodleLevel(MoodleType.Tired) / 3);
			Float2 = Float2 + float1;
			Float2 = Math.min(Float2, 150.0F);
			this.stiffnessIncMap.put(string, Float2);
		}
	}

	public void incStats() {
		float float1 = 0.0F;
		float float2 = 0.0F;
		for (int int1 = 0; int1 < this.currentExe.stiffnessInc.size(); ++int1) {
			String string = (String)this.currentExe.stiffnessInc.get(int1);
			if ("arms".equals(string)) {
				float1 += 4.0F;
			}

			if ("chest".equals(string)) {
				float1 += 2.0F;
			}

			if ("legs".equals(string)) {
				float2 += 4.0F;
			}

			if ("abs".equals(string)) {
				float2 += 2.0F;
			}
		}

		if (this.strLvl > 5) {
			float1 *= (float)(1 + (this.strLvl - 5) / 10);
		}

		if (this.fitnessLvl > 5) {
			float2 *= (float)(1 + (this.fitnessLvl - 5) / 10);
		}

		float1 *= this.currentExe.xpModifier;
		float2 *= this.currentExe.xpModifier;
		this.parent.getXp().AddXP(PerkFactory.Perks.Strength, float1);
		this.parent.getXp().AddXP(PerkFactory.Perks.Fitness, float2);
	}

	public void resetValues() {
		this.stiffnessIncMap.clear();
		this.stiffnessTimerMap.clear();
		this.regularityMap.clear();
	}

	public void removeStiffnessValue(String string) {
		this.stiffnessIncMap.remove(string);
		this.stiffnessTimerMap.remove(string);
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putInt(this.stiffnessIncMap.size());
		Iterator iterator = this.stiffnessIncMap.keySet().iterator();
		String string;
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
			byteBuffer.putFloat((Float)this.stiffnessIncMap.get(string));
		}

		byteBuffer.putInt(this.stiffnessTimerMap.size());
		iterator = this.stiffnessTimerMap.keySet().iterator();
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
			byteBuffer.putInt((Integer)this.stiffnessTimerMap.get(string));
		}

		byteBuffer.putInt(this.regularityMap.size());
		iterator = this.regularityMap.keySet().iterator();
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
			byteBuffer.putFloat((Float)this.regularityMap.get(string));
		}

		byteBuffer.putInt(this.bodypartToIncStiffness.size());
		for (int int1 = 0; int1 < this.bodypartToIncStiffness.size(); ++int1) {
			GameWindow.WriteString(byteBuffer, (String)this.bodypartToIncStiffness.get(int1));
		}

		byteBuffer.putInt(this.exeTimer.size());
		iterator = this.exeTimer.keySet().iterator();
		while (iterator.hasNext()) {
			string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
			byteBuffer.putLong((Long)this.exeTimer.get(string));
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		if (int1 >= 167) {
			int int2 = byteBuffer.getInt();
			int int3;
			if (int2 > 0) {
				for (int3 = 0; int3 < int2; ++int3) {
					this.stiffnessIncMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getFloat());
				}
			}

			int2 = byteBuffer.getInt();
			if (int2 > 0) {
				for (int3 = 0; int3 < int2; ++int3) {
					this.stiffnessTimerMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getInt());
				}
			}

			int2 = byteBuffer.getInt();
			if (int2 > 0) {
				for (int3 = 0; int3 < int2; ++int3) {
					this.regularityMap.put(GameWindow.ReadString(byteBuffer), byteBuffer.getFloat());
				}
			}

			int2 = byteBuffer.getInt();
			if (int2 > 0) {
				for (int3 = 0; int3 < int2; ++int3) {
					this.bodypartToIncStiffness.add(GameWindow.ReadString(byteBuffer));
				}
			}

			if (int1 >= 169) {
				int2 = byteBuffer.getInt();
				if (int2 > 0) {
					for (int3 = 0; int3 < int2; ++int3) {
						this.exeTimer.put(GameWindow.ReadString(byteBuffer), byteBuffer.getLong());
					}
				}
			}
		}
	}

	public boolean onGoingStiffness() {
		return !this.bodypartToIncStiffness.isEmpty();
	}

	public int getCurrentExeStiffnessTimer(String string) {
		string = string.split(",")[0];
		return this.stiffnessTimerMap.get(string) != null ? (Integer)this.stiffnessTimerMap.get(string) : 0;
	}

	public float getCurrentExeStiffnessInc(String string) {
		string = string.split(",")[0];
		return this.stiffnessIncMap.get(string) != null ? (Float)this.stiffnessIncMap.get(string) : 0.0F;
	}

	public IsoGameCharacter getParent() {
		return this.parent;
	}

	public void setParent(IsoGameCharacter gameCharacter) {
		this.parent = gameCharacter;
	}

	public float getRegularity(String string) {
		Float Float1 = (Float)this.regularityMap.get(string);
		if (Float1 == null) {
			Float1 = 0.0F;
		}

		return Float1;
	}

	public HashMap getRegularityMap() {
		return this.regularityMap;
	}

	public void setRegularityMap(HashMap hashMap) {
		this.regularityMap = hashMap;
	}

	public void init() {
		if (this.exercises.isEmpty()) {
			KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)LuaManager.env.rawget("FitnessExercises");
			KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl.rawget("exercisesType");
			Iterator iterator = kahluaTableImpl2.delegate.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				this.exercises.put((String)entry.getKey(), new Fitness.FitnessExercise((KahluaTableImpl)entry.getValue()));
			}

			this.initRegularityMapProfession();
		}
	}

	public void initRegularityMapProfession() {
		if (this.regularityMap.isEmpty()) {
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			if (this.parent.getDescriptor().getProfession().equals("fitnessInstructor")) {
				boolean2 = true;
			}

			if (this.parent.getDescriptor().getProfession().equals("fireofficer")) {
				boolean1 = true;
			}

			if (this.parent.getDescriptor().getProfession().equals("securityguard")) {
				boolean3 = true;
			}

			if (boolean1 || boolean2 || boolean3) {
				float float1;
				for (Iterator iterator = this.exercises.keySet().iterator(); iterator.hasNext(); this.regularityMap.put((String)iterator.next(), float1)) {
					float1 = (float)Rand.Next(7, 12);
					if (boolean1) {
						float1 = (float)Rand.Next(10, 20);
					}

					if (boolean2) {
						float1 = (float)Rand.Next(40, 60);
					}
				}
			}
		}
	}

	public static final class FitnessExercise {
		String type = null;
		Metabolics metabolics = null;
		ArrayList stiffnessInc = null;
		float xpModifier = 1.0F;

		public FitnessExercise(KahluaTableImpl kahluaTableImpl) {
			this.type = kahluaTableImpl.rawgetStr("type");
			this.metabolics = (Metabolics)kahluaTableImpl.rawget("metabolics");
			this.stiffnessInc = new ArrayList(Arrays.asList(kahluaTableImpl.rawgetStr("stiffness").split(",")));
			if (kahluaTableImpl.rawgetFloat("xpMod") > 0.0F) {
				this.xpModifier = kahluaTableImpl.rawgetFloat("xpMod");
			}
		}
	}
}
