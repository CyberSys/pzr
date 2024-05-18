package zombie.ui;

import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.core.textures.TexturePackPage;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.network.GameClient;


public class Clock extends UIElement {
	Texture[] digits;
	Texture texture = null;
	Texture slash = null;
	Texture colon = null;
	Texture texAM = null;
	Texture texPM = null;
	public boolean digital = false;
	private IsoPlayer clockPlayer = null;
	public static Clock instance = null;
	private String cacheTemp = "0 C";
	private int cacheTempCntr = 0;

	public Clock(int int1, int int2) {
		this.x = (double)int1;
		this.y = (double)int2;
		instance = this;
	}

	public void render() {
		if (this.visible) {
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

			float float2 = float1 - (float)((int)float1);
			float2 *= 60.0F;
			int int1 = (int)float1;
			int int2 = (int)(float2 / 10.0F);
			int int3 = 0;
			boolean boolean1 = false;
			if (int1 > 9) {
				int3 = int1 / 10;
			}

			int int4 = int1 % 10;
			this.DrawTextureScaled(this.texture, 0.0, 0.0, (double)this.texture.getWidth(), (double)this.height, 0.75);
			int int5;
			if (this.digits == null) {
				this.digits = new Texture[20];
				for (int5 = 0; int5 < 10; ++int5) {
					this.digits[int5] = TexturePackPage.getTexture("media/ui/ClockDigit_" + int5 + ".png");
					this.digits[int5 + 10] = TexturePackPage.getTexture("media/ui/ClockDigitTiny_" + int5 + ".png");
				}
			}

			int5 = 0;
			boolean boolean2 = false;
			int int6 = 0;
			boolean boolean3 = false;
			int int7;
			if (GameTime.getInstance().getDay() + 1 > 9) {
				int5 = (GameTime.getInstance().getDay() + 1) / 10;
				int7 = (GameTime.getInstance().getDay() + 1) % 10;
			} else {
				int7 = GameTime.getInstance().getDay() + 1;
			}

			int int8;
			if (GameTime.getInstance().getMonth() + 1 > 9) {
				int6 = (GameTime.getInstance().getMonth() + 1) / 10;
				int8 = (GameTime.getInstance().getMonth() + 1) % 10;
			} else {
				int8 = GameTime.getInstance().getMonth() + 1;
			}

			if (this.slash == null) {
				this.slash = TexturePackPage.getTexture("media/ui/ClockDigitTiny_Slash.png");
			}

			if (this.colon == null) {
				this.colon = TexturePackPage.getTexture("media/ui/ClockDigit_Colon.png");
			}

			byte byte1 = 5;
			byte byte2 = 5;
			this.DrawTexture(this.digits[int3], (double)byte1, (double)byte2, 1.0);
			int int9 = byte1 + 11;
			this.DrawTexture(this.digits[int4], (double)int9, (double)byte2, 1.0);
			int9 += 11;
			this.DrawTexture(this.colon, (double)int9, (double)byte2, 1.0);
			int9 += 11;
			this.DrawTexture(this.digits[int2], (double)int9, (double)byte2, 1.0);
			int9 += 11;
			this.DrawTexture(this.digits[0], (double)int9, (double)byte2, 1.0);
			int9 += 16;
			if (!Core.getInstance().getOptionClock24Hour()) {
				byte2 = 16;
			}

			if (Core.getInstance().getOptionClockFormat() == 1) {
				this.DrawTexture(this.digits[int6 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int8 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.slash, (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int5 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int7 + 10], (double)int9, (double)byte2, 1.0);
			} else {
				this.DrawTexture(this.digits[int5 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int7 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.slash, (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int6 + 10], (double)int9, (double)byte2, 1.0);
				int9 += 5;
				this.DrawTexture(this.digits[int8 + 10], (double)int9, (double)byte2, 1.0);
			}

			if (!Core.getInstance().getOptionClock24Hour()) {
				if (this.texAM == null) {
					this.texAM = Texture.getSharedTexture("media/ui/ClockAM.png");
				}

				if (this.texPM == null) {
					this.texPM = Texture.getSharedTexture("media/ui/ClockPM.png");
				}

				int9 -= 20;
				byte2 = 5;
				if (GameTime.getInstance().getTimeOfDay() < 12.0F) {
					this.DrawTexture(this.texAM, (double)int9, (double)byte2, 1.0);
				} else {
					this.DrawTexture(this.texPM, (double)int9, (double)byte2, 1.0);
				}
			}

			if (this.digital && this.clockPlayer != null) {
				--this.cacheTempCntr;
				if (this.cacheTempCntr <= 0) {
					float float3 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.clockPlayer, false);
					this.cacheTemp = Temperature.getTemperatureString(float3);
					this.cacheTempCntr = 30;
				}

				this.DrawTextCentre(UIFont.DebugConsole, this.cacheTemp, 50.0, (double)(this.height - 16.0F), 0.32549020648002625, 0.9098039269447327, 0.9372549057006836, 1.0);
			}

			super.render();
		}
	}

	public void resize() {
		this.visible = false;
		this.digital = false;
		this.clockPlayer = null;
		if (IsoPlayer.instance != null) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getInventory().contains("Type:AlarmClock")) {
					this.visible = UIManager.VisibleAllUI;
					this.digital = player.getInventory().contains("DigitalWatch2");
					this.clockPlayer = player;
					break;
				}
			}
		}

		if (Core.bDebug) {
			this.visible = UIManager.VisibleAllUI;
		}

		if (this.texture == null) {
			this.texture = Texture.getSharedTexture("media/ui/ClockBackground.png");
		}

		if (this.digital) {
			this.setHeight((double)((float)this.texture.getHeight() + 12.0F));
		} else {
			this.setHeight((double)this.texture.getHeight());
		}
	}
}
