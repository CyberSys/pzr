package zombie.iso.weather;

import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.core.Core;
import zombie.network.GameServer;


public class Temperature {
	public static boolean DO_DEFAULT_BASE = false;
	public static boolean DO_DAYLEN_MOD = true;
	public static String CELSIUS_POSTFIX = "\u00b0C";
	public static String FAHRENHEIT_POSTFIX = "\u00b0F";
	private static final float FavorableAirTempMean = 22.0F;
	private static final float FavorableAirTempMin = 16.0F;
	private static final float FavorableAirTempMax = 28.0F;
	private static final float FavorableBodyTemp = 37.0F;
	private static final float BodyMaxColdResistTemp = 8.0F;
	private static final float ClothMaxColdResistTemp = -25.0F;
	private static final float BodyMaxHeatResistTemp = 36.0F;
	private static final float ClothMaxHeatResistTemp = 36.0F;
	public static final float Hypothermia_1 = 36.5F;
	public static final float Hypothermia_2 = 33.0F;
	public static final float Hypothermia_3 = 30.0F;
	public static final float Hypothermia_4 = 25.0F;
	public static final float Hyperthermia_1 = 37.5F;
	public static final float Hyperthermia_2 = 39.0F;
	public static final float Hyperthermia_3 = 40.0F;
	public static final float Hyperthermia_4 = 41.0F;
	public static final float BodyMinTemp = 20.0F;
	public static final float BodyMaxTemp = 42.0F;
	private static String cacheTempString = "";
	private static float cacheTemp = -9000.0F;
	private static Temperature.PlayerTempVars[] playerTemperatures;

	public static String getCelsiusPostfix() {
		return CELSIUS_POSTFIX;
	}

	public static String getFahrenheitPostfix() {
		return FAHRENHEIT_POSTFIX;
	}

	public static String getTemperaturePostfix() {
		return Core.OptionTemperatureDisplayCelsius ? CELSIUS_POSTFIX : FAHRENHEIT_POSTFIX;
	}

	public static String getTemperatureString(float float1) {
		float float2 = Core.OptionTemperatureDisplayCelsius ? float1 : CelsiusToFahrenheit(float1);
		float2 = (float)Math.round(float2 * 10.0F) / 10.0F;
		if (cacheTemp != float2) {
			cacheTemp = float2;
			cacheTempString = float2 + " " + getTemperaturePostfix();
		}

		return cacheTempString;
	}

	public static float CelsiusToFahrenheit(float float1) {
		return float1 * 1.8F + 32.0F;
	}

	public static float FahrenheitToCelsius(float float1) {
		return (float1 - 32.0F) / 1.8F;
	}

	public static float WindchillCelsiusKph(float float1, float float2) {
		float float3 = 13.12F + 0.6215F * float1 - 11.37F * (float)Math.pow((double)float2, 0.1599999964237213) + 0.3965F * float1 * (float)Math.pow((double)float2, 0.1599999964237213);
		return float3 < float1 ? float3 : float1;
	}

	public static void reset() {
		playerTemperatures = null;
	}

	public static float getFractionForRealTimeRatePerMin(float float1) {
		if (DO_DEFAULT_BASE) {
			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutesDefault());
		} else if (!DO_DAYLEN_MOD) {
			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes());
		} else {
			float float2 = (float)SandboxOptions.instance.getDayLengthMinutes() / (float)SandboxOptions.instance.getDayLengthMinutesDefault();
			if (float2 < 1.0F) {
				float2 = 0.5F + 0.5F * float2;
			} else if (float2 > 1.0F) {
				float2 = 1.0F + float2 / 16.0F;
			}

			return float1 / (1440.0F / (float)SandboxOptions.instance.getDayLengthMinutes()) * float2;
		}
	}

	public static Temperature.PlayerTempVars getPlayerTemperatureVars(IsoPlayer player) {
		if (!GameServer.bServer && player != null) {
			for (int int1 = 0; int1 < IsoPlayer.players.length; ++int1) {
				IsoPlayer player2 = IsoPlayer.players[int1];
				if (player2 != null && !player2.isDead() && player2 == player && playerTemperatures != null && int1 < playerTemperatures.length) {
					return playerTemperatures[int1];
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public static void updateTemperatureForAllPlayers() {
		if (!GameServer.bServer) {
			int int1;
			if (playerTemperatures == null || playerTemperatures.length != IsoPlayer.players.length) {
				playerTemperatures = new Temperature.PlayerTempVars[IsoPlayer.players.length];
				for (int1 = 0; int1 < playerTemperatures.length; ++int1) {
					playerTemperatures[int1] = new Temperature.PlayerTempVars();
				}
			}

			for (int1 = 0; int1 < IsoPlayer.players.length; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead()) {
					updatePlayerTemperature(int1, player);
				}
			}
		}
	}

	public static void updatePlayerTemperature(int int1, IsoPlayer player) {
		if (!GameServer.bServer) {
			Temperature.PlayerTempVars playerTempVars = playerTemperatures[int1];
			ClimateManager climateManager = ClimateManager.getInstance();
			playerTempVars.isInVehicle = player.getVehicle() != null;
			playerTempVars.isInside = false;
			playerTempVars.windSpeed = climateManager.getWindspeedKph();
			if (player.getSquare() != null) {
				playerTempVars.isInside = player.getSquare().isInARoom();
			}

			playerTempVars.airTemperature = climateManager.getAirTemperatureForCharacter(player, true);
			playerTempVars.windChillAmount = 0.0F;
			if (playerTempVars.airTemperature < climateManager.getTemperature()) {
				playerTempVars.windChillAmount = climateManager.getTemperature() - playerTempVars.airTemperature;
			}

			float float1 = player.getTemperature();
			playerTempVars.clothing = ClimateManager.clamp01(player.getPlayerClothingInsulation());
			playerTempVars.clothing = (playerTempVars.clothing * playerTempVars.clothing + playerTempVars.clothing) / 2.0F;
			Stats stats = player.getStats();
			BodyDamage bodyDamage = player.getBodyDamage();
			playerTempVars.hunger = stats.getHunger();
			playerTempVars.thirst = stats.getThirst();
			playerTempVars.tired = stats.getFatigue();
			playerTempVars.excercise = ClimateManager.clamp01(1.0F - stats.getEndurance());
			playerTempVars.fitness = ClimateManager.clamp01((stats.getFitness() + 1.0F) / 2.0F);
			playerTempVars.drunkenness = stats.getDrunkenness();
			playerTempVars.sickness = stats.getSickness();
			playerTempVars.wetness = ClimateManager.clamp01(bodyDamage.getWetness() / 100.0F);
			playerTempVars.hasACold = ClimateManager.clamp01(bodyDamage.getColdStrength() / 100.0F);
			playerTempVars.excercise = playerTempVars.excercise * playerTempVars.excercise;
			playerTempVars.weight = player.getNutrition().getWeight();
			float float2 = ClimateManager.clamp01((playerTempVars.weight / 75.0F - 0.5F) * 0.666F);
			playerTempVars.undercooled = ClimateManager.clamp01(1.0F - (float1 - 20.0F) / 17.0F);
			float float3 = 0.8F;
			float3 += 0.4F * playerTempVars.fitness * playerTempVars.fitness;
			float3 += 0.4F * float2 * float2;
			float3 += player.HasTrait("Outdoorsman") ? 0.2F : 0.0F;
			float3 -= Math.max(0.2F * playerTempVars.hasACold, 0.25F * playerTempVars.sickness);
			float3 -= 0.25F * playerTempVars.drunkenness;
			playerTempVars.bodyColdResist = 0.5F + float3;
			playerTempVars.bodyColdResist = playerTempVars.bodyColdResist - 0.75F * playerTempVars.tired;
			playerTempVars.bodyColdResist = playerTempVars.bodyColdResist - 0.75F * playerTempVars.hunger;
			playerTempVars.bodyColdResist = (float)((double)playerTempVars.bodyColdResist + (double)(0.25F * playerTempVars.excercise) + 0.6 * (double)playerTempVars.excercise * (double)playerTempVars.fitness);
			playerTempVars.bodyColdResist = playerTempVars.bodyColdResist - 1.2F * playerTempVars.wetness * playerTempVars.wetness;
			playerTempVars.bodyColdResist = ClimateManager.clamp(0.0F, 2.0F, playerTempVars.bodyColdResist);
			float float4 = ClimateManager.clamp01(playerTempVars.bodyColdResist - 0.5F);
			playerTempVars.clothColdResist = 0.2F * playerTempVars.clothing;
			playerTempVars.clothColdResist = playerTempVars.clothColdResist + 0.8F * playerTempVars.clothing * (1.0F - (1.0F - float4) * (1.0F - float4));
			float float5 = float4 * 8.0F;
			float float6 = playerTempVars.clothColdResist * 41.0F;
			playerTempVars.debugColdBodyBonus = 0.0F;
			playerTempVars.debugColdClothBonus = 0.0F;
			if (playerTempVars.bodyColdResist > 1.5F) {
				playerTempVars.debugColdBodyBonus = 5.0F * ((playerTempVars.bodyColdResist - 1.5F) / 0.5F);
				float5 += playerTempVars.debugColdBodyBonus;
				playerTempVars.debugColdClothBonus = 7.0F * ((playerTempVars.bodyColdResist - 1.5F) / 0.5F);
				float6 += playerTempVars.debugColdClothBonus;
			} else if (playerTempVars.bodyColdResist < 0.5F) {
				playerTempVars.debugColdBodyBonus = -5.0F * (1.0F - playerTempVars.bodyColdResist / 0.5F);
				float5 += playerTempVars.debugColdBodyBonus;
				playerTempVars.debugColdClothBonus = -7.0F * (1.0F - playerTempVars.bodyColdResist / 0.5F);
				float6 += playerTempVars.debugColdClothBonus;
			}

			playerTempVars.coldResist = Math.max(float5, float6);
			if (playerTempVars.coldResist < 0.0F) {
				playerTempVars.coldResist = 0.0F;
			}

			playerTempVars.coldStrength = 0.0F;
			playerTempVars.coldChange = 0.0F;
			if (playerTempVars.airTemperature < 16.0F) {
				playerTempVars.coldStrength = 16.0F - playerTempVars.airTemperature;
			}

			playerTempVars.coldChange = playerTempVars.coldResist - playerTempVars.coldStrength;
			playerTempVars.overheated = ClimateManager.clamp01((float1 - 37.0F) / 5.0F);
			float float7 = 0.95F;
			float7 += 0.3F * playerTempVars.fitness * playerTempVars.fitness;
			float7 += player.HasTrait("Outdoorsman") ? 0.2F : 0.0F;
			float7 -= 0.45F * float2 * float2;
			float7 -= Math.max(0.2F * playerTempVars.hasACold, 0.25F * playerTempVars.sickness);
			playerTempVars.bodyHeatResist = 0.5F + float7;
			playerTempVars.bodyHeatResist = playerTempVars.bodyHeatResist - 1.2F * playerTempVars.thirst * playerTempVars.thirst;
			playerTempVars.bodyHeatResist = playerTempVars.bodyHeatResist - 0.2F * playerTempVars.tired;
			playerTempVars.bodyHeatResist = playerTempVars.bodyHeatResist - 0.5F * playerTempVars.excercise;
			playerTempVars.bodyHeatResist = playerTempVars.bodyHeatResist + 1.25F * playerTempVars.wetness;
			playerTempVars.bodyHeatResist = ClimateManager.clamp(0.0F, 2.0F, playerTempVars.bodyHeatResist);
			float4 = ClimateManager.clamp01(playerTempVars.bodyHeatResist - 0.5F);
			float float8 = 1.0F - playerTempVars.clothing;
			playerTempVars.clothHeatResist = float8 * 0.65F + 0.35F * float8 * float4;
			float5 = float4 * 8.0F;
			float6 = playerTempVars.clothHeatResist * 8.0F;
			playerTempVars.debugHeatBodyBonus = 0.0F;
			playerTempVars.debugHeatClothBonus = 0.0F;
			if (playerTempVars.bodyHeatResist > 1.5F) {
				playerTempVars.debugHeatBodyBonus = 3.0F * ((playerTempVars.bodyHeatResist - 1.5F) / 0.5F);
				float5 += playerTempVars.debugHeatBodyBonus;
				playerTempVars.debugHeatClothBonus = 2.0F * ((playerTempVars.bodyHeatResist - 1.5F) / 0.5F);
				float6 += playerTempVars.debugHeatClothBonus;
			} else if (playerTempVars.bodyHeatResist < 0.5F) {
				playerTempVars.debugHeatBodyBonus = -2.0F * (1.0F - playerTempVars.bodyHeatResist / 0.5F);
				float5 += playerTempVars.debugHeatBodyBonus;
				playerTempVars.debugHeatClothBonus = -1.0F * (1.0F - playerTempVars.bodyHeatResist / 0.5F);
				float6 += playerTempVars.debugHeatClothBonus;
			}

			playerTempVars.heatResist = Math.min(float5, float6);
			if (playerTempVars.heatResist < 0.0F) {
				playerTempVars.heatResist = 0.0F;
			}

			playerTempVars.heatStrength = 0.0F;
			playerTempVars.heatChange = 0.0F;
			if (playerTempVars.airTemperature > 28.0F) {
				playerTempVars.heatStrength = playerTempVars.airTemperature - 28.0F;
			}

			playerTempVars.heatChange = playerTempVars.heatStrength - playerTempVars.heatResist;
			playerTempVars.tickChangePm = 0.0F;
			playerTempVars.tickChange = 0.0F;
			playerTempVars.changeSpeed = -1.0F;
			if (playerTempVars.coldChange < 0.0F) {
				playerTempVars.calcTickChange(0.0F, -1.0F, playerTempVars.coldChange / -25.0F);
				float1 += playerTempVars.tickChange;
			} else if (playerTempVars.coldChange > 0.0F && float1 <= 37.0F) {
				playerTempVars.calcTickChange(2.0F, 8.0F, playerTempVars.coldChange / 50.0F);
				float1 += playerTempVars.tickChange;
				if (float1 > 37.0F) {
					float1 = 37.1F;
				}
			}

			if (playerTempVars.heatChange > 0.0F) {
				playerTempVars.calcTickChange(0.0F, 0.25F, playerTempVars.heatChange / 7.0F);
				float1 += playerTempVars.tickChange;
			} else if (playerTempVars.heatChange < 0.0F && float1 >= 37.0F) {
				playerTempVars.calcTickChange(-1.0F, -3.0F, playerTempVars.heatChange / -3.0F);
				float1 += playerTempVars.tickChange;
				if (float1 < 37.0F) {
					float1 = 36.9F;
				}
			}

			float1 = ClimateManager.clamp(20.0F, 42.0F, float1);
			player.setTemperature(float1);
			playerTempVars.damageState = player.getBodyDamage().ColdDamageStage;
			float float9;
			if (float1 <= 25.0F && playerTempVars.airTemperature < 0.0F) {
				float9 = ClimateManager.clamp01(playerTempVars.airTemperature / -40.0F);
				playerTempVars.damageState = ClimateManager.clamp01(playerTempVars.damageState + getFractionForRealTimeRatePerMin(0.025F + 0.125F * float9));
			} else if (playerTempVars.damageState > 0.0F) {
				float9 = 0.0F;
				if (playerTempVars.tickChangePm > 0.0F) {
					float9 = playerTempVars.changeSpeed;
				} else if (float1 >= 36.5F) {
					float9 = 1.0F;
				} else if (float1 >= 33.0F) {
					float9 = 0.75F;
				} else if (float1 >= 30.0F) {
					float9 = 0.5F;
				} else if (float1 >= 25.0F) {
					float9 = 0.25F;
				}

				if (float9 > 0.0F) {
					playerTempVars.damageState = playerTempVars.damageState - getFractionForRealTimeRatePerMin(0.2F + 0.3F * float9);
					playerTempVars.damageState = ClimateManager.clamp01(playerTempVars.damageState);
				}
			}

			player.getBodyDamage().ColdDamageStage = playerTempVars.damageState;
		}
	}

	public static class PlayerTempVars {
		private boolean isInVehicle = false;
		private boolean isInside = false;
		private float airTemperature = 0.0F;
		private float windSpeed = 0.0F;
		private float hunger;
		private float thirst;
		private float tired;
		private float excercise;
		private float weight;
		private float fitness;
		private float drunkenness;
		private float sickness;
		private float hasACold;
		private float wetness;
		private float clothing;
		private float bodyColdResist;
		private float clothColdResist;
		private float bodyHeatResist;
		private float clothHeatResist;
		private float coldResist;
		private float heatResist;
		private float coldStrength;
		private float heatStrength;
		private float coldChange;
		private float heatChange;
		private float debugColdBodyBonus;
		private float debugHeatBodyBonus;
		private float debugColdClothBonus;
		private float debugHeatClothBonus;
		private float tickChangePm;
		private float tickChange;
		private float changeSpeed = 0.0F;
		private float damageState = 0.0F;
		private float undercooled = 0.0F;
		private float overheated = 0.0F;
		private float windChillAmount = 0.0F;

		public boolean IsInVehicle() {
			return this.isInVehicle;
		}

		public boolean IsInside() {
			return this.isInside;
		}

		public float getWindSpeed() {
			return this.windSpeed;
		}

		public float getAirTemperature() {
			return this.airTemperature;
		}

		public float getHunger() {
			return this.hunger;
		}

		public float getThirst() {
			return this.thirst;
		}

		public float getTired() {
			return this.tired;
		}

		public float getExcercise() {
			return this.excercise;
		}

		public float getWeight() {
			return this.weight;
		}

		public float getFitness() {
			return this.fitness;
		}

		public float getDrunkenness() {
			return this.drunkenness;
		}

		public float getSickness() {
			return this.sickness;
		}

		public float getHasACold() {
			return this.hasACold;
		}

		public float getWetness() {
			return this.wetness;
		}

		public float getClothing() {
			return this.clothing;
		}

		public float getBodyColdResist() {
			return this.bodyColdResist;
		}

		public float getClothColdResist() {
			return this.clothColdResist;
		}

		public float getBodyHeatResist() {
			return this.bodyHeatResist;
		}

		public float getClothHeatResist() {
			return this.clothHeatResist;
		}

		public float getColdResist() {
			return this.coldResist;
		}

		public float getHeatResist() {
			return this.heatResist;
		}

		public float getColdStrength() {
			return this.coldStrength;
		}

		public float getHeatStrength() {
			return this.heatStrength;
		}

		public float getColdChange() {
			return this.coldChange;
		}

		public float getHeatChange() {
			return this.heatChange;
		}

		public float getTickChangePm() {
			return this.tickChangePm;
		}

		public float getTickChange() {
			return this.tickChange;
		}

		public float getChangeSpeed() {
			return this.changeSpeed;
		}

		public float getDamageState() {
			return this.damageState;
		}

		public float getDebugColdBodyBonus() {
			return this.debugColdBodyBonus;
		}

		public float getDebugHeatBodyBonus() {
			return this.debugHeatBodyBonus;
		}

		public float getDebugColdClothBonus() {
			return this.debugColdClothBonus;
		}

		public float getDebugHeatClothBonus() {
			return this.debugHeatClothBonus;
		}

		public float getUndercooled() {
			return this.undercooled;
		}

		public float getOverheated() {
			return this.overheated;
		}

		public float getWindChillAmount() {
			return this.windChillAmount;
		}

		private void calcTickChange(float float1, float float2, float float3) {
			this.changeSpeed = ClimateManager.clamp01(float3);
			this.tickChangePm = ClimateManager.lerp(this.changeSpeed, float1, float2);
			if (this.tickChangePm != 0.0F) {
				this.tickChange = Temperature.getFractionForRealTimeRatePerMin(this.tickChangePm);
			}
		}

		private void calcTickChangeOLD(float float1, float float2, float float3) {
			float1 = Math.min(float1, float2);
			float2 = Math.max(float1, float2);
			this.tickChangePm = ClimateManager.clamp(float1, float2, float3);
			this.changeSpeed = (this.tickChangePm - float1) / (float2 - float1);
			if (float1 < 0.0F) {
				this.changeSpeed = 1.0F - this.changeSpeed;
			}

			if (this.tickChangePm != 0.0F) {
				this.tickChange = Temperature.getFractionForRealTimeRatePerMin(this.tickChangePm);
			}
		}
	}
}
