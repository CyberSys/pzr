package zombie.characters.Moodles;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;


public final class Moodles {
	boolean MoodlesStateChanged = false;
	private Stack MoodleList = new Stack();
	private final IsoGameCharacter Parent;

	public Moodles(IsoGameCharacter gameCharacter) {
		this.Parent = gameCharacter;
		this.MoodleList.add(new Moodle(MoodleType.Endurance, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Tired, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Hungry, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Panic, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Sick, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Bored, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Unhappy, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Bleeding, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Wet, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.HasACold, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Angry, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Stress, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Thirst, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Injured, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Pain, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.HeavyLoad, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Drunk, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Dead, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Zombie, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.FoodEaten, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.Hyperthermia, this.Parent, 3));
		this.MoodleList.add(new Moodle(MoodleType.Hypothermia, this.Parent, 3));
		this.MoodleList.add(new Moodle(MoodleType.Windchill, this.Parent));
		this.MoodleList.add(new Moodle(MoodleType.CantSprint, this.Parent));
	}

	public int getGoodBadNeutral(int int1) {
		return MoodleType.GoodBadNeutral(((Moodle)this.MoodleList.get(int1)).Type);
	}

	public String getMoodleDisplayString(int int1) {
		return MoodleType.getDisplayName(((Moodle)this.MoodleList.get(int1)).Type, ((Moodle)this.MoodleList.get(int1)).getLevel());
	}

	public String getMoodleDescriptionString(int int1) {
		return MoodleType.getDescriptionText(((Moodle)this.MoodleList.get(int1)).Type, ((Moodle)this.MoodleList.get(int1)).getLevel());
	}

	public int getMoodleLevel(int int1) {
		return ((Moodle)this.MoodleList.get(int1)).getLevel();
	}

	public int getMoodleLevel(MoodleType moodleType) {
		return ((Moodle)this.MoodleList.get(MoodleType.ToIndex(moodleType))).getLevel();
	}

	public int getMoodleChevronCount(int int1) {
		return ((Moodle)this.MoodleList.get(int1)).getChevronCount();
	}

	public boolean getMoodleChevronIsUp(int int1) {
		return ((Moodle)this.MoodleList.get(int1)).isChevronIsUp();
	}

	public Color getMoodleChevronColor(int int1) {
		return ((Moodle)this.MoodleList.get(int1)).getChevronColor();
	}

	public MoodleType getMoodleType(int int1) {
		return ((Moodle)this.MoodleList.get(int1)).Type;
	}

	public int getNumMoodles() {
		return this.MoodleList.size();
	}

	public void Randomise() {
	}

	public boolean UI_RefreshNeeded() {
		if (this.MoodlesStateChanged) {
			this.MoodlesStateChanged = false;
			return true;
		} else {
			return false;
		}
	}

	public void setMoodlesStateChanged(boolean boolean1) {
		this.MoodlesStateChanged = boolean1;
	}

	public void Update() {
		for (int int1 = 0; int1 < this.MoodleList.size(); ++int1) {
			if (((Moodle)this.MoodleList.get(int1)).Update()) {
				this.MoodlesStateChanged = true;
			}
		}
	}
}
