package zombie.ai.sadisticAIDirector;

import java.io.FileNotFoundException;
import java.io.IOException;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Rand;
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
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.ui.UIManager;


public class SleepingEvent {
	private int forceWakeUpTime = -1;
	private boolean zombiesIntruders = true;
	private int nightmareWakeUp = -1;
	private IsoWindow weakestWindow = null;
	private IsoDoor openDoor = null;
	public static SleepingEvent instance = new SleepingEvent();

	public void setPlayerFallAsleep(IsoPlayer player, int int1) {
		player.setTimeOfSleep(GameTime.instance.getTimeOfDay());
		this.doDelayToSleep(player);
		this.checkNightmare(player, int1);
		if (this.nightmareWakeUp <= -1) {
			if (SandboxOptions.instance.SleepingEvent.getValue() != 1) {
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
												this.openDoor = door;
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
								this.forceWakeUpTime = Rand.Next(int1 - 4, int1 - 1);
								this.zombiesIntruders = true;
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
		if (player.HasTrait("Insomniac")) {
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
		if (!GameClient.bClient && int1 >= 3) {
			int int2 = 5 + player.getMoodles().getMoodleLevel(MoodleType.Stress) * 10;
			if (Rand.Next(100) < int2) {
				this.nightmareWakeUp = Rand.Next(3, int1 - 2);
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
			if (this.nightmareWakeUp == (int)player.getAsleepTime()) {
				Stats stats = player.getStats();
				stats.Panic += 70.0F;
				stats = player.getStats();
				stats.stress += 0.5F;
				WorldSoundManager.instance.addSound(player, (int)player.getX(), (int)player.getY(), (int)player.getZ(), 6, 1);
				this.wakeUp(player);
			}

			if (this.forceWakeUpTime == (int)player.getAsleepTime() && this.zombiesIntruders) {
				this.spawnZombieIntruders(player);
				WorldSoundManager.instance.addSound(player, (int)player.getX(), (int)player.getY(), (int)player.getZ(), 6, 1);
				this.wakeUp(player);
			}
		}
	}

	private void spawnZombieIntruders(IsoPlayer player) {
		IsoGridSquare square = null;
		if (this.openDoor != null) {
			square = this.openDoor.getSquare();
		} else {
			this.weakestWindow = this.getWeakestWindow(player);
			if (this.weakestWindow != null && this.weakestWindow.getZ() == 0.0F) {
				if (!this.weakestWindow.north) {
					if (this.weakestWindow.getSquare().getRoom() == null) {
						square = this.weakestWindow.getSquare();
					} else {
						square = this.weakestWindow.getSquare().getCell().getGridSquare(this.weakestWindow.getSquare().getX() - 1, this.weakestWindow.getSquare().getY(), this.weakestWindow.getSquare().getZ());
					}
				} else if (this.weakestWindow.getSquare().getRoom() == null) {
					square = this.weakestWindow.getSquare();
				} else {
					square = this.weakestWindow.getSquare().getCell().getGridSquare(this.weakestWindow.getSquare().getX(), this.weakestWindow.getSquare().getY() + 1, this.weakestWindow.getSquare().getZ());
				}

				IsoBarricade barricade = this.weakestWindow.getBarricadeOnOppositeSquare();
				if (barricade == null) {
					barricade = this.weakestWindow.getBarricadeOnSameSquare();
				}

				if (barricade != null) {
					barricade.Damage(Rand.Next(500, 900));
				} else {
					this.weakestWindow.Damage(200.0F);
					this.weakestWindow.smashWindow();
					if (this.weakestWindow.HasCurtains() != null) {
						this.weakestWindow.removeSheet((IsoGameCharacter)null);
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
						zombie.target = player;
						zombie.pathToCharacter(player);
						zombie.spotted(player, true);
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
		if (GameClient.bClient && !boolean1) {
			GameClient.instance.wakeUpPlayer((IsoPlayer)gameCharacter);
		}

		boolean boolean2 = false;
		boolean boolean3 = false;
		for (int int1 = 0; int1 < IsoPlayer.players.length; ++int1) {
			if (IsoPlayer.players[int1] == gameCharacter) {
				boolean3 = true;
			}
		}

		if (gameCharacter instanceof IsoPlayer && boolean3) {
			UIManager.setFadeBeforeUI(((IsoPlayer)gameCharacter).getPlayerNum(), true);
			UIManager.FadeIn((double)((IsoPlayer)gameCharacter).getPlayerNum(), 2.0);
			if (!GameClient.bClient && IsoPlayer.allPlayersAsleep()) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(1);
				boolean2 = true;
			}

			gameCharacter.setLastHourSleeped((int)((IsoPlayer)gameCharacter).getHoursSurvived());
		}

		gameCharacter.setForceWakeUpTime(-1.0F);
		gameCharacter.setAsleep(false);
		if (boolean2) {
			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		if ("goodBed".equals(gameCharacter.getBedType())) {
			gameCharacter.getStats().setFatigue(gameCharacter.getStats().getFatigue() - Rand.Next(0.05F, 0.12F));
			if (gameCharacter.getStats().getFatigue() < 0.0F) {
				gameCharacter.getStats().setFatigue(0.0F);
			}
		} else if ("badBed".equals(gameCharacter.getBedType())) {
			gameCharacter.getStats().setFatigue(gameCharacter.getStats().getFatigue() + Rand.Next(0.1F, 0.2F));
			if (Rand.Next(5) == 0) {
				gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).AddDamage(Rand.Next(5.0F, 15.0F));
				gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).setAdditionalPain(gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).getAdditionalPain() + Rand.Next(30.0F, 50.0F));
			}
		} else if (Rand.Next(10) == 0) {
			gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).AddDamage(Rand.Next(3.0F, 12.0F));
			gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).setAdditionalPain(gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Neck).getAdditionalPain() + Rand.Next(10.0F, 30.0F));
		}

		if (gameCharacter instanceof IsoPlayer) {
			ScriptManager.instance.Trigger("OnPlayerWake");
		}

		this.forceWakeUpTime = -1;
		this.zombiesIntruders = false;
		this.nightmareWakeUp = -1;
		this.openDoor = null;
		this.weakestWindow = null;
	}
}
