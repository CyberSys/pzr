package zombie.characters.Moodles;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.iso.weather.Temperature;


public class Moodle {
	MoodleType Type;
	private int Level;
	IsoGameCharacter Parent;
	private int painTimer;
	private Color chevronColor;
	private boolean chevronIsUp;
	private int chevronCount;
	private int chevronMax;
	private static Color colorNeg = new Color(0.88235295F, 0.15686275F, 0.15686275F);
	private static Color colorPos = new Color(0.15686275F, 0.88235295F, 0.15686275F);

	public Moodle(MoodleType moodleType, IsoGameCharacter gameCharacter) {
		this(moodleType, gameCharacter, 0);
	}

	public Moodle(MoodleType moodleType, IsoGameCharacter gameCharacter, int int1) {
		this.painTimer = 0;
		this.chevronColor = Color.white;
		this.chevronIsUp = true;
		this.chevronCount = 0;
		this.chevronMax = 0;
		this.Parent = gameCharacter;
		this.Type = moodleType;
		this.Level = 0;
		this.chevronMax = int1;
	}

	public int getChevronCount() {
		return this.chevronCount;
	}

	public boolean isChevronIsUp() {
		return this.chevronIsUp;
	}

	public Color getChevronColor() {
		return this.chevronColor;
	}

	public boolean chevronDifference(int int1, boolean boolean1, Color color) {
		return int1 != this.chevronCount || boolean1 != this.chevronIsUp || color != this.chevronColor;
	}

	public void setChevron(int int1, boolean boolean1, Color color) {
		if (int1 < 0) {
			int1 = 0;
		}

		if (int1 > this.chevronMax) {
			int1 = this.chevronMax;
		}

		this.chevronCount = int1;
		this.chevronIsUp = boolean1;
		this.chevronColor = color != null ? color : Color.white;
	}

	public int getLevel() {
		return this.Level;
	}

	public void SetLevel(int int1) {
		if (int1 < 0) {
			int1 = 0;
		}

		if (int1 > 4) {
			int1 = 4;
		}

		this.Level = int1;
	}

	public boolean Update() {
		boolean boolean1 = false;
		byte byte1;
		if (this.Parent.isDead()) {
			boolean boolean2 = false;
			if (this.Type != MoodleType.Dead && this.Type != MoodleType.Zombie) {
				byte1 = 0;
				if (byte1 != this.getLevel()) {
					this.SetLevel(byte1);
					boolean1 = true;
				}

				return boolean1;
			}
		}

		if (this.Type == MoodleType.Endurance) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().endurance > 0.75F) {
					byte1 = 0;
				} else if (this.Parent.getStats().endurance > 0.5F) {
					byte1 = 1;
				} else if (this.Parent.getStats().endurance > 0.25F) {
					byte1 = 2;
				} else if (this.Parent.getStats().endurance > 0.1F) {
					byte1 = 3;
				} else {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Angry) {
			byte1 = 0;
			if (this.Parent.getStats().Anger > 0.75F) {
				byte1 = 4;
			} else if (this.Parent.getStats().Anger > 0.5F) {
				byte1 = 3;
			} else if (this.Parent.getStats().Anger > 0.25F) {
				byte1 = 2;
			} else if (this.Parent.getStats().Anger > 0.1F) {
				byte1 = 1;
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Tired) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().fatigue > 0.6F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().fatigue > 0.7F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().fatigue > 0.8F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().fatigue > 0.9F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Hungry) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().hunger > 0.15F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().hunger > 0.25F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().hunger > 0.45F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().hunger > 0.7F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Panic) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().Panic > 6.0F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().Panic > 30.0F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().Panic > 65.0F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().Panic > 80.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Sick) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				this.Parent.getStats().Sickness = this.Parent.getBodyDamage().getApparentInfectionLevel() / 100.0F;
				if (this.Parent.getStats().Sickness > 0.25F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().Sickness > 0.5F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().Sickness > 0.75F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().Sickness > 0.9F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Bored) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				this.Parent.getStats().Boredom = this.Parent.getBodyDamage().getBoredomLevel() / 100.0F;
				if (this.Parent.getStats().Boredom > 0.25F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().Boredom > 0.5F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().Boredom > 0.75F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().Boredom > 0.9F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Unhappy) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getBodyDamage().getUnhappynessLevel() > 20.0F) {
					byte1 = 1;
				}

				if (this.Parent.getBodyDamage().getUnhappynessLevel() > 45.0F) {
					byte1 = 2;
				}

				if (this.Parent.getBodyDamage().getUnhappynessLevel() > 60.0F) {
					byte1 = 3;
				}

				if (this.Parent.getBodyDamage().getUnhappynessLevel() > 80.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Stress) {
			byte1 = 0;
			if (this.Parent.getStats().getStress() > 0.9F) {
				byte1 = 4;
			} else if (this.Parent.getStats().getStress() > 0.75F) {
				byte1 = 3;
			} else if (this.Parent.getStats().getStress() > 0.5F) {
				byte1 = 2;
			} else if (this.Parent.getStats().getStress() > 0.25F) {
				byte1 = 1;
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Thirst) {
			byte1 = 0;
			if (this.Parent.getStats().thirst > 0.12F) {
				byte1 = 1;
			}

			if (this.Parent.getStats().thirst > 0.25F) {
				byte1 = 2;
			}

			if (this.Parent.getStats().thirst > 0.7F) {
				byte1 = 3;
			}

			if (this.Parent.getStats().thirst > 0.84F) {
				byte1 = 4;
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		int int1;
		if (this.Type == MoodleType.Bleeding) {
			int1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				int1 = this.Parent.getBodyDamage().getNumPartsBleeding();
				if (int1 > 4) {
					int1 = 4;
				}
			}

			if (int1 != this.getLevel()) {
				this.SetLevel(int1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Wet) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getBodyDamage().getWetness() > 15.0F) {
					byte1 = 1;
				}

				if (this.Parent.getBodyDamage().getWetness() > 40.0F) {
					byte1 = 2;
				}

				if (this.Parent.getBodyDamage().getWetness() > 70.0F) {
					byte1 = 3;
				}

				if (this.Parent.getBodyDamage().getWetness() > 90.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.HasACold) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getBodyDamage().getColdStrength() > 20.0F) {
					byte1 = 1;
				}

				if (this.Parent.getBodyDamage().getColdStrength() > 40.0F) {
					byte1 = 2;
				}

				if (this.Parent.getBodyDamage().getColdStrength() > 60.0F) {
					byte1 = 3;
				}

				if (this.Parent.getBodyDamage().getColdStrength() > 75.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Injured) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (100.0F - this.Parent.getBodyDamage().getHealth() > 20.0F) {
					byte1 = 1;
				}

				if (100.0F - this.Parent.getBodyDamage().getHealth() > 40.0F) {
					byte1 = 2;
				}

				if (100.0F - this.Parent.getBodyDamage().getHealth() > 60.0F) {
					byte1 = 3;
				}

				if (100.0F - this.Parent.getBodyDamage().getHealth() > 75.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Pain) {
			++this.painTimer;
			if (this.painTimer < 120) {
				return false;
			}

			this.painTimer = 0;
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().Pain > 10.0F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().Pain > 20.0F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().Pain > 50.0F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().Pain > 75.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.HeavyLoad) {
			byte1 = 0;
			float float1 = this.Parent.getInventory().getCapacityWeight();
			float float2 = (float)this.Parent.getMaxWeight();
			float float3 = float1 / float2;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if ((double)float3 >= 1.75) {
					byte1 = 4;
				} else if ((double)float3 >= 1.5) {
					byte1 = 3;
				} else if ((double)float3 >= 1.25) {
					byte1 = 2;
				} else if (float3 > 1.0F) {
					byte1 = 1;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Drunk) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getStats().Drunkenness > 10.0F) {
					byte1 = 1;
				}

				if (this.Parent.getStats().Drunkenness > 30.0F) {
					byte1 = 2;
				}

				if (this.Parent.getStats().Drunkenness > 50.0F) {
					byte1 = 3;
				}

				if (this.Parent.getStats().Drunkenness > 70.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Dead) {
			byte1 = 0;
			if (this.Parent.isDead()) {
				byte1 = 4;
				if (!this.Parent.getBodyDamage().IsFakeInfected() && this.Parent.getBodyDamage().getInfectionLevel() >= 0.001F) {
					byte1 = 0;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Zombie) {
			byte1 = 0;
			if (this.Parent.isDead() && !this.Parent.getBodyDamage().IsFakeInfected() && this.Parent.getBodyDamage().getInfectionLevel() >= 0.001F) {
				byte1 = 4;
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.FoodEaten) {
			byte1 = 0;
			if (this.Parent.getBodyDamage().getHealth() != 0.0F) {
				if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > 0.0F) {
					byte1 = 1;
				}

				if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > (float)this.Parent.getBodyDamage().getStandardHealthFromFoodTime()) {
					byte1 = 2;
				}

				if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > (float)this.Parent.getBodyDamage().getStandardHealthFromFoodTime() * 2.0F) {
					byte1 = 3;
				}

				if (this.Parent.getBodyDamage().getHealthFromFoodTimer() > (float)this.Parent.getBodyDamage().getStandardHealthFromFoodTime() * 3.0F) {
					byte1 = 4;
				}
			}

			if (byte1 != this.getLevel()) {
				this.SetLevel(byte1);
				boolean1 = true;
			}
		}

		int1 = this.chevronCount;
		boolean boolean3 = this.chevronIsUp;
		Color color = this.chevronColor;
		if ((this.Type == MoodleType.Hyperthermia || this.Type == MoodleType.Hypothermia) && this.Parent instanceof IsoPlayer) {
			if (!(this.Parent.getBodyDamage().getTemperature() < 36.5F) && !(this.Parent.getBodyDamage().getTemperature() > 37.5F)) {
				int1 = 0;
			} else {
				Temperature.PlayerTempVars playerTempVars = Temperature.getPlayerTemperatureVars((IsoPlayer)this.Parent);
				if (playerTempVars == null) {
					int1 = 0;
				} else {
					boolean3 = playerTempVars.getTickChangePm() >= 0.0F;
					if (playerTempVars.getChangeSpeed() >= 0.0F) {
						if (playerTempVars.getChangeSpeed() > 0.66F) {
							int1 = 3;
						} else if (playerTempVars.getChangeSpeed() > 0.33F) {
							int1 = 2;
						} else {
							int1 = 1;
						}
					} else {
						int1 = 0;
					}
				}
			}
		}

		byte byte2;
		if (this.Type == MoodleType.Hyperthermia) {
			byte2 = 0;
			if (int1 > 0) {
				color = boolean3 ? colorNeg : colorPos;
			}

			if (this.Parent.getBodyDamage().getTemperature() != 0.0F) {
				if (this.Parent.getBodyDamage().getTemperature() > 37.5F) {
					byte2 = 1;
				}

				if (this.Parent.getBodyDamage().getTemperature() > 39.0F) {
					byte2 = 2;
				}

				if (this.Parent.getBodyDamage().getTemperature() > 40.0F) {
					byte2 = 3;
				}

				if (this.Parent.getBodyDamage().getTemperature() > 41.0F) {
					byte2 = 4;
				}
			}

			if (byte2 != this.getLevel() || byte2 > 0 && this.chevronDifference(int1, boolean3, color)) {
				this.SetLevel(byte2);
				this.setChevron(int1, boolean3, color);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Hypothermia) {
			byte2 = 0;
			if (int1 > 0) {
				color = boolean3 ? colorPos : colorNeg;
			}

			if (this.Parent.getBodyDamage().getTemperature() != 0.0F) {
				if (this.Parent.getBodyDamage().getTemperature() < 36.5F && this.Parent.getStats().Drunkenness <= 30.0F) {
					byte2 = 1;
				}

				if (this.Parent.getBodyDamage().getTemperature() < 33.0F && this.Parent.getStats().Drunkenness <= 70.0F) {
					byte2 = 2;
				}

				if (this.Parent.getBodyDamage().getTemperature() < 30.0F) {
					byte2 = 3;
				}

				if (this.Parent.getBodyDamage().getTemperature() < 25.0F) {
					byte2 = 4;
				}
			}

			if (byte2 != this.getLevel() || byte2 > 0 && this.chevronDifference(int1, boolean3, color)) {
				this.SetLevel(byte2);
				this.setChevron(int1, boolean3, color);
				boolean1 = true;
			}
		}

		if (this.Type == MoodleType.Windchill) {
			byte2 = 0;
			if (this.Parent instanceof IsoPlayer) {
				Temperature.PlayerTempVars playerTempVars2 = Temperature.getPlayerTemperatureVars((IsoPlayer)this.Parent);
				if (playerTempVars2 != null && !playerTempVars2.IsInVehicle() && !playerTempVars2.IsInside()) {
					if (playerTempVars2.getWindChillAmount() > 5.0F) {
						byte2 = 1;
					}

					if (playerTempVars2.getWindChillAmount() > 10.0F) {
						byte2 = 2;
					}

					if (playerTempVars2.getWindChillAmount() > 15.0F) {
						byte2 = 3;
					}

					if (playerTempVars2.getWindChillAmount() > 20.0F) {
						byte2 = 4;
					}
				}
			}

			if (byte2 != this.getLevel()) {
				this.SetLevel(byte2);
				boolean1 = true;
			}
		}

		return boolean1;
	}
}
