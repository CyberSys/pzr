package zombie.ai.sadisticAIDirector;

import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoWindow;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class SleepingEvent {
	public static final SleepingEvent instance = new SleepingEvent();
	public static boolean zombiesInvasion = false;

	public void setPlayerFallAsleep(IsoPlayer player, int int1) {
		SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
		sleepingEventData.reset();
		if (ClimateManager.getInstance().isRaining() && this.isExposedToPrecipitation(player)) {
			sleepingEventData.bRaining = true;
			sleepingEventData.bWasRainingAtStart = true;
			sleepingEventData.rainTimeStartHours = GameTime.getInstance().getWorldAgeHours();
		}

		sleepingEventData.sleepingTime = (float)int1;
		player.setTimeOfSleep(GameTime.instance.getTimeOfDay());
		this.doDelayToSleep(player);
		this.checkNightmare(player, int1);
		if (sleepingEventData.nightmareWakeUp <= -1) {
			if (SandboxOptions.instance.SleepingEvent.getValue() != 1 && zombiesInvasion) {
				if (player.getCurrentSquare() == null || player.getCurrentSquare().getZone() == null || !player.getCurrentSquare().getZone().haveConstruction) {
					boolean boolean1 = false;
					if ((GameTime.instance.getHour() >= 0 && GameTime.instance.getHour() < 5 || GameTime.instance.getHour() > 18) && int1 >= 4) {
						boolean1 = true;
					}

					byte byte1 = 20;
					if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
						byte1 = 45;
					}

					if (Rand.Next(100) <= byte1 && player.getCell().getZombieList().size() >= 1 && int1 >= 4) {
						int int2 = 0;
						if (player.getCurrentBuilding() != null) {
							IsoGridSquare square = null;
							IsoWindow window = null;
							for (int int3 = 0; int3 < 3; ++int3) {
								for (int int4 = player.getCurrentBuilding().getDef().getX() - 2; int4 < player.getCurrentBuilding().getDef().getX2() + 2; ++int4) {
									for (int int5 = player.getCurrentBuilding().getDef().getY() - 2; int5 < player.getCurrentBuilding().getDef().getY2() + 2; ++int5) {
										square = IsoWorld.instance.getCell().getGridSquare(int4, int5, int3);
										if (square != null) {
											boolean boolean2 = square.haveElectricity() || GameTime.instance.NightsSurvived < SandboxOptions.instance.getElecShutModifier();
											if (boolean2) {
												for (int int6 = 0; int6 < square.getObjects().size(); ++int6) {
													IsoObject object = (IsoObject)square.getObjects().get(int6);
													if (object.getContainer() != null && (object.getContainer().getType().equals("fridge") || object.getContainer().getType().equals("freezer"))) {
														int2 += 3;
													}

													if (object instanceof IsoStove && ((IsoStove)object).Activated()) {
														int2 += 5;
													}

													if (object instanceof IsoTelevision && ((IsoTelevision)object).getDeviceData().getIsTurnedOn()) {
														int2 += 30;
													}

													if (object instanceof IsoRadio && ((IsoRadio)object).getDeviceData().getIsTurnedOn()) {
														int2 += 30;
													}
												}
											}

											window = square.getWindow();
											if (window != null) {
												int2 += this.checkWindowStatus(window);
											}

											IsoDoor door = square.getIsoDoor();
											if (door != null && door.isExteriorDoor((IsoGameCharacter)null) && door.IsOpen()) {
												int2 += 25;
												sleepingEventData.openDoor = door;
											}
										}
									}
								}
							}

							if (SandboxOptions.instance.SleepingEvent.getValue() == 3) {
								int2 = (int)((double)int2 * 1.5);
							}

							if (int2 > 70) {
								int2 = 70;
							}

							if (!boolean1) {
								int2 /= 2;
							}

							if (Rand.Next(100) <= int2) {
								sleepingEventData.forceWakeUpTime = Rand.Next(int1 - 4, int1 - 1);
								sleepingEventData.zombiesIntruders = true;
							}
						}
					}
				}
			}
		}
	}

	private void doDelayToSleep(IsoPlayer player) {
		float float1 = 0.3F;
		float float2 = 2.0F;
		if (player.Traits.Insomniac.isSet()) {
			float1 = 1.0F;
		}

		if (player.getMoodles().getMoodleLevel(MoodleType.Pain) > 0) {
			float1 += 1.0F + (float)player.getMoodles().getMoodleLevel(MoodleType.Pain) * 0.2F;
		}

		if (player.getMoodles().getMoodleLevel(MoodleType.Stress) > 0) {
			float1 *= 1.2F;
		}

		if ("badBed".equals(player.getBedType())) {
			float1 *= 1.3F;
		} else if ("goodBed".equals(player.getBedType())) {
			float1 *= 0.8F;
		} else if ("floor".equals(player.getBedType())) {
			float1 *= 1.6F;
		}

		if (player.Traits.NightOwl.isSet()) {
			float1 *= 0.5F;
		}

		if (player.getSleepingTabletEffect() > 1000.0F) {
			float1 = 0.1F;
		}

		if (float1 > float2) {
			float1 = float2;
		}

		float float3 = Rand.Next(0.0F, float1);
		player.setDelayToSleep(GameTime.instance.getTimeOfDay() + float3);
	}

	private void checkNightmare(IsoPlayer player, int int1) {
		if (!GameClient.bClient) {
			SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
			if (int1 >= 3) {
				int int2 = 5 + player.getMoodles().getMoodleLevel(MoodleType.Stress) * 10;
				if (Rand.Next(100) < int2) {
					sleepingEventData.nightmareWakeUp = Rand.Next(3, int1 - 2);
				}
			}
		}
	}

	private int checkWindowStatus(IsoWindow window) {
		IsoGridSquare square = window.getSquare();
		if (window.getSquare().getRoom() == null) {
			if (!window.north) {
				square = window.getSquare().getCell().getGridSquare(window.getSquare().getX() - 1, window.getSquare().getY(), window.getSquare().getZ());
			} else {
				square = window.getSquare().getCell().getGridSquare(window.getSquare().getX(), window.getSquare().getY() - 1, window.getSquare().getZ());
			}
		}

		boolean boolean1 = false;
		boolean boolean2 = false;
		for (int int1 = 0; int1 < square.getRoom().lightSwitches.size(); ++int1) {
			if (((IsoLightSwitch)square.getRoom().lightSwitches.get(int1)).isActivated()) {
				boolean2 = true;
				break;
			}
		}

		int int2;
		IsoBarricade barricade;
		if (boolean2) {
			int2 = 20;
			if (window.HasCurtains() != null && !window.HasCurtains().open) {
				int2 -= 17;
			}

			barricade = window.getBarricadeOnOppositeSquare();
			if (barricade == null) {
				barricade = window.getBarricadeOnSameSquare();
			}

			if (barricade != null && (barricade.getNumPlanks() > 4 || barricade.isMetal())) {
				int2 -= 20;
			}

			if (int2 < 0) {
				int2 = 0;
			}

			if (square.getZ() > 0) {
				int2 /= 2;
			}

			return int2;
		} else {
			int2 = 5;
			if (window.HasCurtains() != null && !window.HasCurtains().open) {
				int2 -= 5;
			}

			barricade = window.getBarricadeOnOppositeSquare();
			if (barricade == null) {
				barricade = window.getBarricadeOnSameSquare();
			}

			if (barricade != null && (barricade.getNumPlanks() > 3 || barricade.isMetal())) {
				int2 -= 5;
			}

			if (int2 < 0) {
				int2 = 0;
			}

			if (square.getZ() > 0) {
				int2 /= 2;
			}

			return int2;
		}
	}

	public void update(IsoPlayer player) {
		if (player != null) {
			SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
			if (sleepingEventData.nightmareWakeUp == (int)player.getAsleepTime()) {
				Stats stats = player.getStats();
				stats.Panic += 70.0F;
				stats = player.getStats();
				stats.stress += 0.5F;
				WorldSoundManager.instance.addSound(player, (int)player.getX(), (int)player.getY(), (int)player.getZ(), 6, 1);
				SoundManager.instance.setMusicWakeState(player, "WakeNightmare");
				this.wakeUp(player);
			}

			if (sleepingEventData.forceWakeUpTime == (int)player.getAsleepTime() && sleepingEventData.zombiesIntruders) {
				this.spawnZombieIntruders(player);
				WorldSoundManager.instance.addSound(player, (int)player.getX(), (int)player.getY(), (int)player.getZ(), 6, 1);
				SoundManager.instance.setMusicWakeState(player, "WakeZombies");
				this.wakeUp(player);
			}

			this.updateRain(player);
			this.updateSnow(player);
			this.updateTemperature(player);
			this.updateWetness(player);
		}
	}

	private void updateRain(IsoPlayer player) {
		SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
		if (!ClimateManager.getInstance().isRaining()) {
			sleepingEventData.bRaining = false;
			sleepingEventData.bWasRainingAtStart = false;
			sleepingEventData.rainTimeStartHours = -1.0;
		} else if (this.isExposedToPrecipitation(player)) {
			double double1 = GameTime.getInstance().getWorldAgeHours();
			if (!sleepingEventData.bWasRainingAtStart) {
				if (!sleepingEventData.bRaining) {
					sleepingEventData.rainTimeStartHours = double1;
				}

				if (sleepingEventData.getHoursSinceRainStarted() >= 0.16666666666666666) {
				}
			}

			sleepingEventData.bRaining = true;
		}
	}

	private void updateSnow(IsoPlayer player) {
		if (ClimateManager.getInstance().isSnowing()) {
			if (this.isExposedToPrecipitation(player)) {
				;
			}
		}
	}

	private void updateTemperature(IsoPlayer player) {
	}

	private void updateWetness(IsoPlayer player) {
	}

	private boolean isExposedToPrecipitation(IsoGameCharacter gameCharacter) {
		if (gameCharacter.getCurrentSquare() == null) {
			return false;
		} else if (!gameCharacter.getCurrentSquare().isInARoom() && !gameCharacter.getCurrentSquare().haveRoof) {
			if (gameCharacter.getBed() != null && "Tent".equals(gameCharacter.getBed().getName())) {
				return false;
			} else {
				BaseVehicle baseVehicle = gameCharacter.getVehicle();
				return baseVehicle == null || !baseVehicle.hasRoof(baseVehicle.getSeat(gameCharacter));
			}
		} else {
			return false;
		}
	}

	private void spawnZombieIntruders(IsoPlayer player) {
		SleepingEventData sleepingEventData = player.getOrCreateSleepingEventData();
		IsoGridSquare square = null;
		if (sleepingEventData.openDoor != null) {
			square = sleepingEventData.openDoor.getSquare();
		} else {
			sleepingEventData.weakestWindow = this.getWeakestWindow(player);
			if (sleepingEventData.weakestWindow != null && sleepingEventData.weakestWindow.getZ() == 0.0F) {
				if (!sleepingEventData.weakestWindow.north) {
					if (sleepingEventData.weakestWindow.getSquare().getRoom() == null) {
						square = sleepingEventData.weakestWindow.getSquare();
					} else {
						square = sleepingEventData.weakestWindow.getSquare().getCell().getGridSquare(sleepingEventData.weakestWindow.getSquare().getX() - 1, sleepingEventData.weakestWindow.getSquare().getY(), sleepingEventData.weakestWindow.getSquare().getZ());
					}
				} else if (sleepingEventData.weakestWindow.getSquare().getRoom() == null) {
					square = sleepingEventData.weakestWindow.getSquare();
				} else {
					square = sleepingEventData.weakestWindow.getSquare().getCell().getGridSquare(sleepingEventData.weakestWindow.getSquare().getX(), sleepingEventData.weakestWindow.getSquare().getY() + 1, sleepingEventData.weakestWindow.getSquare().getZ());
				}

				IsoBarricade barricade = sleepingEventData.weakestWindow.getBarricadeOnOppositeSquare();
				if (barricade == null) {
					barricade = sleepingEventData.weakestWindow.getBarricadeOnSameSquare();
				}

				if (barricade != null) {
					barricade.Damage(Rand.Next(500, 900));
				} else {
					sleepingEventData.weakestWindow.Damage(200.0F);
					sleepingEventData.weakestWindow.smashWindow();
					if (sleepingEventData.weakestWindow.HasCurtains() != null) {
						sleepingEventData.weakestWindow.removeSheet((IsoGameCharacter)null);
					}

					if (square != null) {
						square.addBrokenGlass();
					}
				}
			}
		}

		player.getStats().setPanic(player.getStats().getPanic() + (float)Rand.Next(30, 60));
		if (square != null) {
			if (IsoWorld.getZombiesEnabled()) {
				int int1 = Rand.Next(3) + 1;
				for (int int2 = 0; int2 < int1; ++int2) {
					VirtualZombieManager.instance.choices.clear();
					VirtualZombieManager.instance.choices.add(square);
					IsoZombie zombie = VirtualZombieManager.instance.createRealZombieAlways(IsoDirections.fromIndex(Rand.Next(8)).index(), false);
					if (zombie != null) {
						zombie.setTarget(player);
						zombie.pathToCharacter(player);
						zombie.spotted(player, true);
						ZombieSpawnRecorder.instance.record(zombie, this.getClass().getSimpleName());
					}
				}
			}
		}
	}

	private IsoWindow getWeakestWindow(IsoPlayer player) {
		IsoGridSquare square = null;
		IsoWindow window = null;
		IsoWindow window2 = null;
		int int1 = 0;
		for (int int2 = player.getCurrentBuilding().getDef().getX() - 2; int2 < player.getCurrentBuilding().getDef().getX2() + 2; ++int2) {
			for (int int3 = player.getCurrentBuilding().getDef().getY() - 2; int3 < player.getCurrentBuilding().getDef().getY2() + 2; ++int3) {
				square = IsoWorld.instance.getCell().getGridSquare(int2, int3, 0);
				if (square != null) {
					window = square.getWindow();
					if (window != null) {
						int int4 = this.checkWindowStatus(window);
						if (int4 > int1) {
							int1 = int4;
							window2 = window;
						}
					}
				}
			}
		}

		return window2;
	}

	public void wakeUp(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null) {
			this.wakeUp(gameCharacter, false);
		}
	}

	public void wakeUp(IsoGameCharacter gameCharacter, boolean boolean1) {
		SleepingEventData sleepingEventData = gameCharacter.getOrCreateSleepingEventData();
		if (GameClient.bClient && !boolean1) {
			GameClient.instance.wakeUpPlayer((IsoPlayer)gameCharacter);
		}

		boolean boolean2 = false;
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (player != null && player.isLocalPlayer()) {
			UIManager.setFadeBeforeUI(player.getPlayerNum(), true);
			UIManager.FadeIn((double)player.getPlayerNum(), 2.0);
			if (!GameClient.bClient && IsoPlayer.allPlayersAsleep()) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(1);
				boolean2 = true;
			}

			gameCharacter.setLastHourSleeped((int)player.getHoursSurvived());
		}

		gameCharacter.setForceWakeUpTime(-1.0F);
		gameCharacter.setAsleep(false);
		if (boolean2) {
			try {
				GameWindow.save(true);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}
		}

		BodyPart bodyPart = gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck);
		float float1 = sleepingEventData.sleepingTime / 8.0F;
		if ("goodBed".equals(gameCharacter.getBedType())) {
			gameCharacter.getStats().setFatigue(gameCharacter.getStats().getFatigue() - Rand.Next(0.05F, 0.12F) * float1);
			if (gameCharacter.getStats().getFatigue() < 0.0F) {
				gameCharacter.getStats().setFatigue(0.0F);
			}
		} else if ("badBed".equals(gameCharacter.getBedType())) {
			gameCharacter.getStats().setFatigue(gameCharacter.getStats().getFatigue() + Rand.Next(0.1F, 0.2F) * float1);
			if (Rand.Next(5) == 0) {
				bodyPart.AddDamage(Rand.Next(5.0F, 15.0F));
				bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0F, 50.0F));
			}
		} else if ("floor".equals(gameCharacter.getBedType())) {
			gameCharacter.getStats().setFatigue(gameCharacter.getStats().getFatigue() + Rand.Next(0.15F, 0.25F) * float1);
			if (Rand.Next(5) == 0) {
				bodyPart.AddDamage(Rand.Next(10.0F, 20.0F));
				bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(30.0F, 50.0F));
			}
		} else if (Rand.Next(10) == 0) {
			bodyPart.AddDamage(Rand.Next(3.0F, 12.0F));
			bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + Rand.Next(10.0F, 30.0F));
		}

		sleepingEventData.reset();
	}
}
