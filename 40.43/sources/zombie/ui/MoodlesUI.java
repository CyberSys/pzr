package zombie.ui;

import java.util.Stack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;
import zombie.characters.IsoGameCharacter;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.textures.Texture;
import zombie.input.Mouse;


public class MoodlesUI extends UIElement {
	public float clientH = 0.0F;
	public float clientW = 0.0F;
	public boolean Movable = false;
	public int ncclientH = 0;
	public int ncclientW = 0;
	private static MoodlesUI instance = null;
	private static final float OFFSCREEN_Y = 10000.0F;
	public Stack nestedItems = new Stack();
	float alpha = 1.0F;
	Texture Back_Bad_1 = null;
	Texture Back_Bad_2 = null;
	Texture Back_Bad_3 = null;
	Texture Back_Bad_4 = null;
	Texture Back_Good_1 = null;
	Texture Back_Good_2 = null;
	Texture Back_Good_3 = null;
	Texture Back_Good_4 = null;
	Texture Back_Neutral = null;
	Texture Endurance = null;
	Texture Bleeding = null;
	Texture Angry = null;
	Texture Stress = null;
	Texture Thirst = null;
	Texture Panic = null;
	Texture Hungry = null;
	Texture Injured = null;
	Texture Pain = null;
	Texture Sick = null;
	Texture Bored = null;
	Texture Unhappy = null;
	Texture Tired = null;
	Texture HeavyLoad = null;
	Texture Drunk = null;
	Texture Wet = null;
	Texture HasACold = null;
	Texture Dead = null;
	Texture Zombie = null;
	Texture Windchill = null;
	Texture FoodEaten = null;
	Texture Hyperthermia = null;
	Texture Hypothermia = null;
	public static Texture plusRed;
	public static Texture plusGreen;
	public static Texture minusRed;
	public static Texture minusGreen;
	public static Texture chevronUp;
	public static Texture chevronUpBorder;
	public static Texture chevronDown;
	public static Texture chevronDownBorder;
	float MoodleDistY = 36.0F;
	boolean MouseOver = false;
	int MouseOverSlot = 0;
	int NumUsedSlots = 0;
	private int DebugKeyDelay = 0;
	private int DistFromRighEdge = 46;
	private int[] GoodBadNeutral;
	private int[] MoodleLevel;
	private float[] MoodleOscilationLevel;
	private float[] MoodleSlotsDesiredPos;
	private float[] MoodleSlotsPos;
	private int[] MoodleTypeInSlot;
	private float Oscilator;
	private float OscilatorDecelerator;
	private float OscilatorRate;
	private float OscilatorScalar;
	private float OscilatorStartLevel;
	private float OscilatorStep;
	private IsoGameCharacter UseCharacter;
	private boolean alphaIncrease;

	public MoodlesUI() {
		this.GoodBadNeutral = new int[MoodleType.ToIndex(MoodleType.MAX)];
		this.MoodleLevel = new int[MoodleType.ToIndex(MoodleType.MAX)];
		this.MoodleOscilationLevel = new float[MoodleType.ToIndex(MoodleType.MAX)];
		this.MoodleSlotsDesiredPos = new float[MoodleType.ToIndex(MoodleType.MAX)];
		this.MoodleSlotsPos = new float[MoodleType.ToIndex(MoodleType.MAX)];
		this.MoodleTypeInSlot = new int[MoodleType.ToIndex(MoodleType.MAX)];
		this.Oscilator = 0.0F;
		this.OscilatorDecelerator = 0.96F;
		this.OscilatorRate = 0.8F;
		this.OscilatorScalar = 15.6F;
		this.OscilatorStartLevel = 1.0F;
		this.OscilatorStep = 0.0F;
		this.UseCharacter = null;
		this.alphaIncrease = true;
		this.x = (double)(Core.getInstance().getScreenWidth() - this.DistFromRighEdge);
		this.y = 74.0;
		this.width = 32.0F;
		this.height = 500.0F;
		this.Back_Bad_1 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_1.png");
		this.Back_Bad_2 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_2.png");
		this.Back_Bad_3 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_3.png");
		this.Back_Bad_4 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_4.png");
		this.Back_Good_1 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_1.png");
		this.Back_Good_2 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_2.png");
		this.Back_Good_3 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_3.png");
		this.Back_Good_4 = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Good_4.png");
		this.Back_Neutral = Texture.getSharedTexture("media/ui/Moodles/Moodle_Bkg_Bad_1.png");
		this.Endurance = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Endurance.png");
		this.Tired = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Tired.png");
		this.Hungry = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Hungry.png");
		this.Panic = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Panic.png");
		this.Sick = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Sick.png");
		this.Bored = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Bored.png");
		this.Unhappy = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Unhappy.png");
		this.Bleeding = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Bleeding.png");
		this.Wet = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Wet.png");
		this.HasACold = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Cold.png");
		this.Angry = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Angry.png");
		this.Stress = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Stressed.png");
		this.Thirst = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Thirsty.png");
		this.Injured = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Injured.png");
		this.Pain = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Pain.png");
		this.HeavyLoad = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_HeavyLoad.png");
		this.Drunk = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Drunk.png");
		this.Dead = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Dead.png");
		this.Zombie = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Zombie.png");
		this.FoodEaten = Texture.getSharedTexture("media/ui/Moodles/Moodle_Icon_Hungry.png");
		this.Hyperthermia = Texture.getSharedTexture("media/ui/weather/Moodle_Icon_TempHot.png");
		this.Hypothermia = Texture.getSharedTexture("media/ui/weather/Moodle_Icon_TempCold.png");
		this.Windchill = Texture.getSharedTexture("media/ui/Moodle_Icon_Windchill.png");
		plusRed = Texture.getSharedTexture("media/ui/Moodle_internal_plus_red.png");
		minusRed = Texture.getSharedTexture("media/ui/Moodle_internal_minus_red.png");
		plusGreen = Texture.getSharedTexture("media/ui/Moodle_internal_plus_green.png");
		minusGreen = Texture.getSharedTexture("media/ui/Moodle_internal_minus_green.png");
		chevronUp = Texture.getSharedTexture("media/ui/Moodle_chevron_up.png");
		chevronUpBorder = Texture.getSharedTexture("media/ui/Moodle_chevron_up_border.png");
		chevronDown = Texture.getSharedTexture("media/ui/Moodle_chevron_down.png");
		chevronDownBorder = Texture.getSharedTexture("media/ui/Moodle_chevron_down_border.png");
		for (int int1 = 0; int1 < MoodleType.ToIndex(MoodleType.MAX); ++int1) {
			this.MoodleSlotsPos[int1] = 10000.0F;
			this.MoodleSlotsDesiredPos[int1] = 10000.0F;
		}

		this.clientW = this.width;
		this.clientH = this.height;
		instance = this;
	}

	public boolean CurrentlyAnimating() {
		boolean boolean1 = false;
		for (int int1 = 0; int1 < MoodleType.ToIndex(MoodleType.MAX); ++int1) {
			if (this.MoodleSlotsPos[int1] != this.MoodleSlotsDesiredPos[int1]) {
				boolean1 = true;
			}
		}

		return boolean1;
	}

	public void Nest(UIElement uIElement, int int1, int int2, int int3, int int4) {
		this.AddChild(uIElement);
		this.nestedItems.add(new Rectangle(int4, int1, int2, int3));
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (!this.isVisible()) {
			return Boolean.FALSE;
		} else {
			this.MouseOver = true;
			super.onMouseMove(double1, double2);
			this.MouseOverSlot = (int)(((double)((float)Mouse.getYA()) - this.getY()) / (double)this.MoodleDistY);
			if (this.MouseOverSlot >= this.NumUsedSlots) {
				this.MouseOverSlot = 1000;
			}

			return Boolean.TRUE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		super.onMouseMoveOutside(double1, double2);
		this.MouseOverSlot = 1000;
		this.MouseOver = false;
	}

	public void render() {
		if (this.UseCharacter != null) {
			float float1 = (float)PerformanceSettings.LockFPS / 30.0F;
			this.OscilatorStep += this.OscilatorRate / float1;
			this.Oscilator = (float)Math.sin((double)this.OscilatorStep);
			int int1 = 0;
			for (int int2 = 0; int2 < MoodleType.ToIndex(MoodleType.MAX); ++int2) {
				if (this.MoodleSlotsPos[int2] != 10000.0F) {
					float float2;
					Texture texture;
					Texture texture2;
					float2 = this.Oscilator * this.OscilatorScalar * this.MoodleOscilationLevel[int2];
					texture = this.Back_Neutral;
					texture2 = this.Tired;
					label92: switch (this.GoodBadNeutral[int2]) {
					case 0: 
						texture = this.Back_Neutral;
						break;
					
					case 1: 
						switch (this.MoodleLevel[int2]) {
						case 1: 
							texture = this.Back_Good_1;
							break label92;
						
						case 2: 
							texture = this.Back_Good_2;
							break label92;
						
						case 3: 
							texture = this.Back_Good_3;
							break label92;
						
						case 4: 
							texture = this.Back_Good_4;
						
						default: 
							break label92;
						
						}

					
					case 2: 
						switch (this.MoodleLevel[int2]) {
						case 1: 
							texture = this.Back_Bad_1;
							break;
						
						case 2: 
							texture = this.Back_Bad_2;
							break;
						
						case 3: 
							texture = this.Back_Bad_3;
							break;
						
						case 4: 
							texture = this.Back_Bad_4;
						
						}

					
					}

					switch (int2) {
					case 0: 
						texture2 = this.Endurance;
						break;
					
					case 1: 
						texture2 = this.Tired;
						break;
					
					case 2: 
						texture2 = this.Hungry;
						break;
					
					case 3: 
						texture2 = this.Panic;
						break;
					
					case 4: 
						texture2 = this.Sick;
						break;
					
					case 5: 
						texture2 = this.Bored;
						break;
					
					case 6: 
						texture2 = this.Unhappy;
						break;
					
					case 7: 
						texture2 = this.Bleeding;
						break;
					
					case 8: 
						texture2 = this.Wet;
						break;
					
					case 9: 
						texture2 = this.HasACold;
						break;
					
					case 10: 
						texture2 = this.Angry;
						break;
					
					case 11: 
						texture2 = this.Stress;
						break;
					
					case 12: 
						texture2 = this.Thirst;
						break;
					
					case 13: 
						texture2 = this.Injured;
						break;
					
					case 14: 
						texture2 = this.Pain;
						break;
					
					case 15: 
						texture2 = this.HeavyLoad;
						break;
					
					case 16: 
						texture2 = this.Drunk;
						break;
					
					case 17: 
						texture2 = this.Dead;
						break;
					
					case 18: 
						texture2 = this.Zombie;
						break;
					
					case 19: 
						texture2 = this.FoodEaten;
						break;
					
					case 20: 
						texture2 = this.Hyperthermia;
						break;
					
					case 21: 
						texture2 = this.Hypothermia;
						break;
					
					case 22: 
						texture2 = this.Windchill;
					
					}

					if (MoodleType.FromIndex(int2).name().equals(Core.getInstance().getBlinkingMoodle())) {
						if (this.alphaIncrease) {
							this.alpha += 0.1F * (30.0F / (float)PerformanceSettings.instance.getUIRenderFPS());
							if (this.alpha > 1.0F) {
								this.alpha = 1.0F;
								this.alphaIncrease = false;
							}
						} else {
							this.alpha -= 0.1F * (30.0F / (float)PerformanceSettings.instance.getUIRenderFPS());
							if (this.alpha < 0.0F) {
								this.alpha = 0.0F;
								this.alphaIncrease = true;
							}
						}
					}

					if (Core.getInstance().getBlinkingMoodle() == null) {
						this.alpha = 1.0F;
					}

					this.DrawTexture(texture, (double)(0 + (int)float2), (double)((int)this.MoodleSlotsPos[int2]), (double)this.alpha);
					this.DrawTexture(texture2, (double)(0 + (int)float2), (double)((int)this.MoodleSlotsPos[int2]), (double)this.alpha);
					int int3;
					int int4;
					if (this.UseCharacter.getMoodles().getMoodleChevronCount(int2) > 0) {
						boolean boolean1 = this.UseCharacter.getMoodles().getMoodleChevronIsUp(int2);
						Color color = this.UseCharacter.getMoodles().getMoodleChevronColor(int2);
						color.a = this.alpha;
						for (int3 = 0; int3 < this.UseCharacter.getMoodles().getMoodleChevronCount(int2); ++int3) {
							int4 = int3 * 4;
							this.DrawTextureCol(boolean1 ? chevronUp : chevronDown, (double)(0 + (int)float2 + 16), (double)((int)this.MoodleSlotsPos[int2] + 20 - int4), color);
							this.DrawTextureCol(boolean1 ? chevronUpBorder : chevronDownBorder, (double)(0 + (int)float2 + 16), (double)((int)this.MoodleSlotsPos[int2] + 20 - int4), color);
						}
					}

					if (this.MouseOver && int1 == this.MouseOverSlot) {
						String string = this.UseCharacter.getMoodles().getMoodleDisplayString(int2);
						String string2 = this.UseCharacter.getMoodles().getMoodleDescriptionString(int2);
						int3 = TextManager.instance.font.getWidth(string);
						int4 = TextManager.instance.font.getWidth(string2);
						int int5 = Math.max(int3, int4);
						int int6 = TextManager.instance.font.getLineHeight();
						int int7 = (int)this.MoodleSlotsPos[int2] + 1;
						int int8 = (2 + int6) * 2;
						this.DrawTextureScaledColor((Texture)null, -10.0 - (double)int5 - 6.0, (double)int7 - 2.0, (double)int5 + 12.0, (double)int8, 0.0, 0.0, 0.0, 0.6);
						this.DrawTextRight(string, -10.0, (double)int7, 1.0, 1.0, 1.0, 1.0);
						this.DrawTextRight(string2, -10.0, (double)(int7 + int6), 0.800000011920929, 0.800000011920929, 0.800000011920929, 1.0);
					}

					++int1;
				}
			}

			super.render();
		}
	}

	public void update() {
		super.update();
		if (this.UseCharacter != null) {
			if (!this.CurrentlyAnimating()) {
				if (this.DebugKeyDelay > 0) {
					--this.DebugKeyDelay;
				} else if (Keyboard.isKeyDown(57)) {
					this.DebugKeyDelay = 10;
				}
			}

			float float1 = (float)PerformanceSettings.LockFPS / 30.0F;
			float[] floatArray;
			int int1;
			for (int1 = 0; int1 < MoodleType.ToIndex(MoodleType.MAX); ++int1) {
				floatArray = this.MoodleOscilationLevel;
				floatArray[int1] -= this.MoodleOscilationLevel[int1] * (1.0F - this.OscilatorDecelerator) / float1;
				if ((double)this.MoodleOscilationLevel[int1] < 0.01) {
					this.MoodleOscilationLevel[int1] = 0.0F;
				}
			}

			if (this.UseCharacter.getMoodles().UI_RefreshNeeded()) {
				int1 = 0;
				for (int int2 = 0; int2 < MoodleType.ToIndex(MoodleType.MAX); ++int2) {
					if (this.UseCharacter.getMoodles().getMoodleLevel(int2) > 0) {
						boolean boolean1 = false;
						if (this.MoodleLevel[int2] != this.UseCharacter.getMoodles().getMoodleLevel(int2)) {
							boolean1 = true;
							this.MoodleLevel[int2] = this.UseCharacter.getMoodles().getMoodleLevel(int2);
							this.MoodleOscilationLevel[int2] = this.OscilatorStartLevel;
						}

						this.MoodleSlotsDesiredPos[int2] = this.MoodleDistY * (float)int1;
						if (boolean1) {
							if (this.MoodleSlotsPos[int2] == 10000.0F) {
								this.MoodleSlotsPos[int2] = this.MoodleSlotsDesiredPos[int2] + 500.0F;
								this.MoodleOscilationLevel[int2] = 0.0F;
							}

							this.GoodBadNeutral[int2] = this.UseCharacter.getMoodles().getGoodBadNeutral(int2);
						} else {
							this.MoodleOscilationLevel[int2] = 0.0F;
						}

						this.MoodleTypeInSlot[int1] = int2;
						++int1;
					} else {
						this.MoodleSlotsPos[int2] = 10000.0F;
						this.MoodleSlotsDesiredPos[int2] = 10000.0F;
						this.MoodleOscilationLevel[int2] = 0.0F;
						this.MoodleLevel[int2] = 0;
					}
				}

				this.NumUsedSlots = int1;
			}

			for (int1 = 0; int1 < MoodleType.ToIndex(MoodleType.MAX); ++int1) {
				if (Math.abs(this.MoodleSlotsPos[int1] - this.MoodleSlotsDesiredPos[int1]) > 0.8F) {
					floatArray = this.MoodleSlotsPos;
					floatArray[int1] += (this.MoodleSlotsDesiredPos[int1] - this.MoodleSlotsPos[int1]) * 0.15F;
				} else {
					this.MoodleSlotsPos[int1] = this.MoodleSlotsDesiredPos[int1];
				}
			}
		}
	}

	public void setCharacter(IsoGameCharacter gameCharacter) {
		if (gameCharacter != this.UseCharacter) {
			this.UseCharacter = gameCharacter;
			if (this.UseCharacter != null && this.UseCharacter.getMoodles() != null) {
				this.UseCharacter.getMoodles().setMoodlesStateChanged(true);
			}
		}
	}

	public static MoodlesUI getInstance() {
		return instance;
	}
}
