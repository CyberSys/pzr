package zombie.characters.Moodles;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;

public class Moodles {
   boolean MoodlesStateChanged = false;
   private Stack MoodleList = new Stack();
   private final IsoGameCharacter Parent;

   public Moodles(IsoGameCharacter var1) {
      this.Parent = var1;
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
   }

   public int getGoodBadNeutral(int var1) {
      return MoodleType.GoodBadNeutral(((Moodle)this.MoodleList.get(var1)).Type);
   }

   public String getMoodleDisplayString(int var1) {
      return MoodleType.getDisplayName(((Moodle)this.MoodleList.get(var1)).Type, ((Moodle)this.MoodleList.get(var1)).getLevel());
   }

   public String getMoodleDescriptionString(int var1) {
      return MoodleType.getDescriptionText(((Moodle)this.MoodleList.get(var1)).Type, ((Moodle)this.MoodleList.get(var1)).getLevel());
   }

   public int getMoodleLevel(int var1) {
      return ((Moodle)this.MoodleList.get(var1)).getLevel();
   }

   public int getMoodleLevel(MoodleType var1) {
      return ((Moodle)this.MoodleList.get(MoodleType.ToIndex(var1))).getLevel();
   }

   public int getMoodleChevronCount(int var1) {
      return ((Moodle)this.MoodleList.get(var1)).getChevronCount();
   }

   public boolean getMoodleChevronIsUp(int var1) {
      return ((Moodle)this.MoodleList.get(var1)).isChevronIsUp();
   }

   public Color getMoodleChevronColor(int var1) {
      return ((Moodle)this.MoodleList.get(var1)).getChevronColor();
   }

   public MoodleType getMoodleType(int var1) {
      return ((Moodle)this.MoodleList.get(var1)).Type;
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

   public void setMoodlesStateChanged(boolean var1) {
      this.MoodlesStateChanged = var1;
   }

   public void Update() {
      boolean var1 = false;

      for(int var2 = 0; var2 < this.MoodleList.size(); ++var2) {
         if (((Moodle)this.MoodleList.get(var2)).Update()) {
            this.MoodlesStateChanged = true;
            var1 = true;
         }
      }

   }
}
