package zombie.ui;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;


public final class Clock extends UIElement {
	Texture background = null;
	Texture[] digitsLarge;
	Texture[] digitsSmall;
	Texture colon = null;
	Texture slash = null;
	Texture minus = null;
	Texture dot = null;
	Texture tempC = null;
	Texture tempF = null;
	Texture tempE = null;
	Texture texAM = null;
	Texture texPM = null;
	Texture alarmOn = null;
	Texture alarmRinging = null;
	Color displayColour = new Color(100, 200, 210, 255);
	Color ghostColour = new Color(40, 40, 40, 128);
	int uxOriginal;
	int uyOriginal;
	int largeDigitSpacing;
	int smallDigitSpacing;
	int colonSpacing;
	int ampmSpacing;
	int alarmBellSpacing;
	int decimalSpacing;
	int degreeSpacing;
	int slashSpacing;
	int tempDateSpacing;
	int dateOffset;
	int minusOffset;
	int amVerticalSpacing;
	int pmVerticalSpacing;
	int alarmBellVerticalSpacing;
	int displayVerticalSpacing;
	int decimalVerticalSpacing;
	public boolean digital = false;
	public boolean isAlarmSet = false;
	public boolean isAlarmRinging = false;
	private IsoPlayer clockPlayer = null;
	public static Clock instance = null;

	public Clock(int int1, int int2) {
		this.x = (double)int1;
		this.y = (double)int2;
		instance = this;
	}

	public void render() {
		if (this.visible) {
			this.assignTextures(Core.getInstance().getOptionClockSize() == 2);
			this.DrawTexture(this.background, 0.0, 0.0, 0.75);
			this.renderDisplay(true, this.ghostColour);
			this.renderDisplay(false, this.displayColour);
			super.render();
		}
	}

	private void renderDisplay(boolean boolean1, Color color) {
		int int1 = this.uxOriginal;
		int int2 = this.uyOriginal;
		for (int int3 = 0; int3 < 4; ++int3) {
			int[] intArray = this.timeDigits();
			if (boolean1) {
				this.DrawTextureCol(this.digitsLarge[8], (double)int1, (double)int2, color);
			} else {
				this.DrawTextureCol(this.digitsLarge[intArray[int3]], (double)int1, (double)int2, color);
			}

			int1 += this.digitsLarge[0].getWidth();
			if (int3 == 1) {
				int1 += this.colonSpacing;
				this.DrawTextureCol(this.colon, (double)int1, (double)int2, color);
				int1 += this.colon.getWidth() + this.colonSpacing;
			} else if (int3 < 3) {
				int1 += this.largeDigitSpacing;
			}
		}

		int1 += this.ampmSpacing;
		if (!Core.getInstance().getOptionClock24Hour() || boolean1) {
			if (boolean1) {
				this.DrawTextureCol(this.texAM, (double)int1, (double)(int2 + this.amVerticalSpacing), color);
				this.DrawTextureCol(this.texPM, (double)int1, (double)(int2 + this.pmVerticalSpacing), color);
			} else if (GameTime.getInstance().getTimeOfDay() < 12.0F) {
				this.DrawTextureCol(this.texAM, (double)int1, (double)(int2 + this.amVerticalSpacing), color);
			} else {
				this.DrawTextureCol(this.texPM, (double)int1, (double)(int2 + this.pmVerticalSpacing), color);
			}
		}

		if (!this.isAlarmRinging && !boolean1) {
			if (this.isAlarmSet) {
				this.DrawTextureCol(this.alarmOn, (double)(int1 + this.texAM.getWidth() + this.alarmBellSpacing), (double)(int2 + this.alarmBellVerticalSpacing), color);
			}
		} else {
			this.DrawTextureCol(this.alarmRinging, (double)(int1 + this.texAM.getWidth() + this.alarmBellSpacing), (double)(int2 + this.alarmBellVerticalSpacing), color);
		}

		if (this.digital || boolean1) {
			int1 = this.uxOriginal;
			int2 += this.digitsLarge[0].getHeight() + this.displayVerticalSpacing;
			int[] intArray2;
			int int4;
			if (this.clockPlayer == null) {
				int1 += this.dateOffset;
			} else {
				intArray2 = this.tempDigits();
				if (intArray2[0] == 1 || boolean1) {
					this.DrawTextureCol(this.minus, (double)int1, (double)int2, color);
				}

				int1 += this.minusOffset;
				if (intArray2[1] == 1 || boolean1) {
					this.DrawTextureCol(this.digitsSmall[1], (double)int1, (double)int2, color);
				}

				int1 += this.digitsSmall[0].getWidth() + this.smallDigitSpacing;
				for (int4 = 2; int4 < 5; ++int4) {
					if (boolean1) {
						this.DrawTextureCol(this.digitsSmall[8], (double)int1, (double)int2, color);
					} else {
						this.DrawTextureCol(this.digitsSmall[intArray2[int4]], (double)int1, (double)int2, color);
					}

					int1 += this.digitsSmall[0].getWidth();
					if (int4 == 3) {
						int1 += this.decimalSpacing;
						this.DrawTextureCol(this.dot, (double)int1, (double)(int2 + this.decimalVerticalSpacing), color);
						int1 += this.dot.getWidth() + this.decimalSpacing;
					} else if (int4 < 4) {
						int1 += this.smallDigitSpacing;
					}
				}

				int1 += this.degreeSpacing;
				this.DrawTextureCol(this.dot, (double)int1, (double)int2, color);
				int1 += this.dot.getWidth() + this.degreeSpacing;
				if (boolean1) {
					this.DrawTextureCol(this.tempE, (double)int1, (double)int2, color);
				} else if (intArray2[5] == 0) {
					this.DrawTextureCol(this.tempC, (double)int1, (double)int2, color);
				} else {
					this.DrawTextureCol(this.tempF, (double)int1, (double)int2, color);
				}

				int1 += this.digitsSmall[0].getWidth() + this.tempDateSpacing;
			}

			intArray2 = this.dateDigits();
			for (int4 = 0; int4 < 4; ++int4) {
				if (boolean1) {
					this.DrawTextureCol(this.digitsSmall[8], (double)int1, (double)int2, color);
				} else {
					this.DrawTextureCol(this.digitsSmall[intArray2[int4]], (double)int1, (double)int2, color);
				}

				int1 += this.digitsSmall[0].getWidth();
				if (int4 == 1) {
					int1 += this.slashSpacing;
					this.DrawTextureCol(this.slash, (double)int1, (double)int2, color);
					int1 += this.slash.getWidth() + this.slashSpacing;
				} else if (int4 < 3) {
					int1 += this.smallDigitSpacing;
				}
			}
		}
	}

	private void assignTextures(boolean boolean1) {
		if (this.digitsLarge == null) {
			String string = "Medium";
			String string2 = "Small";
			if (boolean1) {
				string = "Large";
				string2 = "Medium";
				this.assignLargeOffsets();
			} else {
				this.assignSmallOffsets();
			}

			this.digitsLarge = new Texture[10];
			this.digitsSmall = new Texture[10];
			for (int int1 = 0; int1 < 10; ++int1) {
				this.digitsLarge[int1] = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string + int1 + ".png");
				this.digitsSmall[int1] = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + int1 + ".png");
			}

			this.colon = Texture.getSharedTexture("media/ui/ClockAssets/ClockDivide" + string + ".png");
			this.slash = Texture.getSharedTexture("media/ui/ClockAssets/DateDivide" + string2 + ".png");
			this.minus = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + "Minus.png");
			this.dot = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + "Dot.png");
			this.tempC = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + "C.png");
			this.tempF = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + "F.png");
			this.tempE = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string2 + "E.png");
			this.texAM = Texture.getSharedTexture("media/ui/ClockAssets/ClockAm" + string + ".png");
			this.texPM = Texture.getSharedTexture("media/ui/ClockAssets/ClockPm" + string + ".png");
			this.alarmOn = Texture.getSharedTexture("media/ui/ClockAssets/ClockAlarm" + string + "Set.png");
			this.alarmRinging = Texture.getSharedTexture("media/ui/ClockAssets/ClockAlarm" + string + "Sound.png");
		}
	}

	private void assignSmallOffsets() {
		this.uxOriginal = 3;
		this.uyOriginal = 3;
		this.largeDigitSpacing = 1;
		this.smallDigitSpacing = 1;
		this.colonSpacing = 1;
		this.ampmSpacing = 1;
		this.alarmBellSpacing = 1;
		this.decimalSpacing = 1;
		this.degreeSpacing = 1;
		this.slashSpacing = 1;
		this.tempDateSpacing = 5;
		this.dateOffset = 33;
		this.minusOffset = 0;
		this.amVerticalSpacing = 7;
		this.pmVerticalSpacing = 12;
		this.alarmBellVerticalSpacing = 1;
		this.displayVerticalSpacing = 2;
		this.decimalVerticalSpacing = 6;
	}

	private void assignLargeOffsets() {
		this.uxOriginal = 3;
		this.uyOriginal = 3;
		this.largeDigitSpacing = 2;
		this.smallDigitSpacing = 1;
		this.colonSpacing = 3;
		this.ampmSpacing = 3;
		this.alarmBellSpacing = 5;
		this.decimalSpacing = 2;
		this.degreeSpacing = 2;
		this.slashSpacing = 2;
		this.tempDateSpacing = 8;
		this.dateOffset = 65;
		this.minusOffset = -2;
		this.amVerticalSpacing = 15;
		this.pmVerticalSpacing = 25;
		this.alarmBellVerticalSpacing = 1;
		this.displayVerticalSpacing = 5;
		this.decimalVerticalSpacing = 15;
	}

	private int[] timeDigits() {
		float float1 = GameTime.getInstance().getTimeOfDay();
		if (GameClient.bClient && GameClient.bFastForward) {
			float1 = GameTime.getInstance().ServerTimeOfDay;
		}

		if (!Core.getInstance().getOptionClock24Hour()) {
			if (float1 >= 13.0F) {
				float1 -= 12.0F;
			}

			if (float1 < 1.0F) {
				float1 += 12.0F;
			}
		}

		int int1 = (int)float1;
		float float2 = (float1 - (float)((int)float1)) * 60.0F;
		int int2 = int1 / 10;
		int int3 = int1 % 10;
		int int4 = (int)(float2 / 10.0F);
		return new int[]{int2, int3, int4, 0};
	}

	private int[] dateDigits() {
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		int int1 = (GameTime.getInstance().getDay() + 1) / 10;
		int int2 = (GameTime.getInstance().getDay() + 1) % 10;
		int int3 = (GameTime.getInstance().getMonth() + 1) / 10;
		int int4 = (GameTime.getInstance().getMonth() + 1) % 10;
		return Core.getInstance().getOptionClockFormat() == 1 ? new int[]{int3, int4, int1, int2} : new int[]{int1, int2, int3, int4};
	}

	private int[] tempDigits() {
		float float1 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.clockPlayer, false);
		byte byte1 = 0;
		byte byte2 = 0;
		if (!Core.OptionTemperatureDisplayCelsius) {
			float1 = float1 * 1.8F + 32.0F;
			byte2 = 1;
		}

		if (float1 < 0.0F) {
			byte1 = 1;
			float1 *= -1.0F;
		}

		int int1 = (int)float1 / 100;
		int int2 = (int)(float1 % 100.0F) / 10;
		int int3 = (int)float1 % 10;
		int int4 = (int)(float1 * 10.0F) % 10;
		return new int[]{byte1, int1, int2, int3, int4, byte2};
	}

	public void resize() {
		this.visible = false;
		this.digital = false;
		this.clockPlayer = null;
		this.isAlarmSet = false;
		this.isAlarmRinging = false;
		if (IsoPlayer.getInstance() != null) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead()) {
					for (int int2 = 0; int2 < player.getWornItems().size(); ++int2) {
						InventoryItem inventoryItem = player.getWornItems().getItemByIndex(int2);
						if (inventoryItem instanceof AlarmClock || inventoryItem instanceof AlarmClockClothing) {
							this.visible = UIManager.VisibleAllUI;
							this.digital |= inventoryItem.hasTag("Digital");
							if (inventoryItem instanceof AlarmClock) {
								if (((AlarmClock)inventoryItem).isAlarmSet()) {
									this.isAlarmSet = true;
								}

								if (((AlarmClock)inventoryItem).isRinging()) {
									this.isAlarmRinging = true;
								}
							} else {
								if (((AlarmClockClothing)inventoryItem).isAlarmSet()) {
									this.isAlarmSet = true;
								}

								if (((AlarmClockClothing)inventoryItem).isRinging()) {
									this.isAlarmRinging = true;
								}
							}

							this.clockPlayer = player;
						}
					}

					if (this.clockPlayer != null) {
						break;
					}

					ArrayList arrayList = player.getInventory().getItems();
					for (int int3 = 0; int3 < arrayList.size(); ++int3) {
						InventoryItem inventoryItem2 = (InventoryItem)arrayList.get(int3);
						if (inventoryItem2 instanceof AlarmClock || inventoryItem2 instanceof AlarmClockClothing) {
							this.visible = UIManager.VisibleAllUI;
							this.digital |= inventoryItem2.hasTag("Digital");
							if (inventoryItem2 instanceof AlarmClock) {
								if (((AlarmClock)inventoryItem2).isAlarmSet()) {
									this.isAlarmSet = true;
								}

								if (((AlarmClock)inventoryItem2).isRinging()) {
									this.isAlarmRinging = true;
								}
							} else {
								if (((AlarmClockClothing)inventoryItem2).isAlarmSet()) {
									this.isAlarmSet = true;
								}

								if (((AlarmClockClothing)inventoryItem2).isRinging()) {
									this.isAlarmRinging = true;
								}
							}

							this.clockPlayer = player;
						}
					}
				}
			}
		}

		if (DebugOptions.instance.CheatClockVisible.getValue()) {
			this.digital = true;
			this.visible = UIManager.VisibleAllUI;
		}

		if (this.background == null) {
			if (Core.getInstance().getOptionClockSize() == 2) {
				this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockLargeBackground.png");
			} else {
				this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockSmallBackground.png");
			}
		}

		this.setHeight((double)this.background.getHeight());
		this.setWidth((double)this.background.getWidth());
	}

	public boolean isDateVisible() {
		return this.visible && this.digital;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (!this.isVisible()) {
			return false;
		} else {
			int int1;
			IsoPlayer player;
			int int2;
			InventoryItem inventoryItem;
			if (this.isAlarmRinging) {
				if (IsoPlayer.getInstance() != null) {
					for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						player = IsoPlayer.players[int1];
						if (player != null && !player.isDead()) {
							for (int2 = 0; int2 < player.getWornItems().size(); ++int2) {
								inventoryItem = player.getWornItems().getItemByIndex(int2);
								if (inventoryItem instanceof AlarmClock) {
									((AlarmClock)inventoryItem).stopRinging();
								} else if (inventoryItem instanceof AlarmClockClothing) {
									((AlarmClockClothing)inventoryItem).stopRinging();
								}
							}

							for (int2 = 0; int2 < player.getInventory().getItems().size(); ++int2) {
								inventoryItem = (InventoryItem)player.getInventory().getItems().get(int2);
								if (inventoryItem instanceof AlarmClock) {
									((AlarmClock)inventoryItem).stopRinging();
								} else if (inventoryItem instanceof AlarmClockClothing) {
									((AlarmClockClothing)inventoryItem).stopRinging();
								}
							}
						}
					}
				}
			} else if (this.isAlarmSet) {
				if (IsoPlayer.getInstance() != null) {
					for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						player = IsoPlayer.players[int1];
						if (player != null && !player.isDead()) {
							for (int2 = 0; int2 < player.getWornItems().size(); ++int2) {
								inventoryItem = player.getWornItems().getItemByIndex(int2);
								if (inventoryItem instanceof AlarmClock && ((AlarmClock)inventoryItem).isAlarmSet()) {
									((AlarmClock)inventoryItem).setAlarmSet(false);
								} else if (inventoryItem instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem).isAlarmSet()) {
									((AlarmClockClothing)inventoryItem).setAlarmSet(false);
								}
							}

							for (int2 = 0; int2 < player.getInventory().getItems().size(); ++int2) {
								inventoryItem = (InventoryItem)player.getInventory().getItems().get(int2);
								if (inventoryItem instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem).isAlarmSet()) {
									((AlarmClockClothing)inventoryItem).setAlarmSet(false);
								}

								if (inventoryItem instanceof AlarmClock && ((AlarmClock)inventoryItem).isAlarmSet()) {
									((AlarmClock)inventoryItem).setAlarmSet(false);
								}
							}
						}
					}
				}
			} else if (IsoPlayer.getInstance() != null) {
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					player = IsoPlayer.players[int1];
					if (player != null && !player.isDead()) {
						for (int2 = 0; int2 < player.getWornItems().size(); ++int2) {
							inventoryItem = player.getWornItems().getItemByIndex(int2);
							if (inventoryItem instanceof AlarmClock && ((AlarmClock)inventoryItem).isDigital() && !((AlarmClock)inventoryItem).isAlarmSet()) {
								((AlarmClock)inventoryItem).setAlarmSet(true);
								if (this.isAlarmSet) {
									return true;
								}
							}

							if (inventoryItem instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem).isDigital() && !((AlarmClockClothing)inventoryItem).isAlarmSet()) {
								((AlarmClockClothing)inventoryItem).setAlarmSet(true);
								if (this.isAlarmSet) {
									return true;
								}
							}
						}

						for (int2 = 0; int2 < player.getInventory().getItems().size(); ++int2) {
							inventoryItem = (InventoryItem)player.getInventory().getItems().get(int2);
							if (inventoryItem instanceof AlarmClock && ((AlarmClock)inventoryItem).isDigital() && !((AlarmClock)inventoryItem).isAlarmSet()) {
								((AlarmClock)inventoryItem).setAlarmSet(true);
								if (this.isAlarmSet) {
									return true;
								}
							}

							if (inventoryItem instanceof AlarmClockClothing && ((AlarmClockClothing)inventoryItem).isDigital() && !((AlarmClockClothing)inventoryItem).isAlarmSet()) {
								((AlarmClockClothing)inventoryItem).setAlarmSet(true);
								if (this.isAlarmSet) {
									return true;
								}
							}
						}
					}
				}
			}

			return true;
		}
	}
}
