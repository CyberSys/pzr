package zombie;

import fmod.javafmod;
import fmod.fmod.FMODSoundEmitter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Mouse;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.Alarm;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.objects.RainManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.ui.UIManager;


public class AmbientStreamManager extends BaseAmbientStreamManager {
	public static int OneInAmbienceChance = 2500;
	public static int MaxAmbientCount = 20;
	public static float MaxRange = 1000.0F;
	private ArrayList alarmList = new ArrayList();
	public static BaseAmbientStreamManager instance;
	public ArrayList ambient = new ArrayList();
	public ArrayList worldEmitters = new ArrayList();
	public ArrayDeque freeEmitters = new ArrayDeque();
	public ArrayList allAmbient = new ArrayList();
	public ArrayList nightAmbient = new ArrayList();
	public ArrayList dayAmbient = new ArrayList();
	public ArrayList rainAmbient = new ArrayList();
	public ArrayList indoorAmbient = new ArrayList();
	public ArrayList outdoorAmbient = new ArrayList();
	public ArrayList windAmbient = new ArrayList();
	public boolean initialized = false;
	Vector2 tempo = new Vector2();

	public static BaseAmbientStreamManager getInstance() {
		return instance;
	}

	public void update() {
		if (this.initialized) {
			if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() != 0) {
				if (IsoPlayer.instance != null) {
					if (IsoPlayer.instance.getCurrentSquare() != null) {
						float float1 = GameTime.instance.getTimeOfDay();
						for (int int1 = 0; int1 < this.worldEmitters.size(); ++int1) {
							AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = (AmbientStreamManager.WorldSoundEmitter)this.worldEmitters.get(int1);
							IsoGridSquare square;
							if (worldSoundEmitter.daytime != null) {
								square = IsoWorld.instance.CurrentCell.getGridSquare((double)worldSoundEmitter.x, (double)worldSoundEmitter.y, (double)worldSoundEmitter.z);
								if (square == null) {
									worldSoundEmitter.fmodEmitter.stopAll();
									SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
									this.worldEmitters.remove(worldSoundEmitter);
									this.freeEmitters.add(worldSoundEmitter);
									--int1;
								} else {
									if (float1 > worldSoundEmitter.dawn && float1 < worldSoundEmitter.dusk) {
										if (worldSoundEmitter.fmodEmitter.isEmpty()) {
											worldSoundEmitter.channel = worldSoundEmitter.fmodEmitter.playAmbientLoopedImpl(worldSoundEmitter.daytime);
										}
									} else if (!worldSoundEmitter.fmodEmitter.isEmpty()) {
										worldSoundEmitter.fmodEmitter.stopSound(worldSoundEmitter.channel);
										worldSoundEmitter.channel = 0L;
									}

									if (!worldSoundEmitter.fmodEmitter.isEmpty() && (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart())) {
										worldSoundEmitter.fmodEmitter.tick();
									}
								}
							} else if (IsoPlayer.instance != null && IsoPlayer.instance.HasTrait("Deaf")) {
								worldSoundEmitter.fmodEmitter.stopAll();
								SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
								this.worldEmitters.remove(worldSoundEmitter);
								this.freeEmitters.add(worldSoundEmitter);
								--int1;
							} else {
								square = IsoWorld.instance.CurrentCell.getGridSquare((double)worldSoundEmitter.x, (double)worldSoundEmitter.y, (double)worldSoundEmitter.z);
								if (square != null && !worldSoundEmitter.fmodEmitter.isEmpty()) {
									worldSoundEmitter.fmodEmitter.x = worldSoundEmitter.x;
									worldSoundEmitter.fmodEmitter.y = worldSoundEmitter.y;
									worldSoundEmitter.fmodEmitter.z = worldSoundEmitter.z;
									if (IsoWorld.instance.emitterUpdate || worldSoundEmitter.fmodEmitter.hasSoundsToStart()) {
										worldSoundEmitter.fmodEmitter.tick();
									}
								} else {
									worldSoundEmitter.fmodEmitter.stopAll();
									SoundManager.instance.unregisterEmitter(worldSoundEmitter.fmodEmitter);
									this.worldEmitters.remove(worldSoundEmitter);
									this.freeEmitters.add(worldSoundEmitter);
									--int1;
								}
							}
						}

						float float2 = GameTime.instance.getNight();
						boolean boolean1 = IsoPlayer.instance.getCurrentSquare().getRoom() != null;
						boolean boolean2 = RainManager.isRaining();
						int int2;
						for (int2 = 0; int2 < this.allAmbient.size(); ++int2) {
							((AmbientStreamManager.AmbientLoop)this.allAmbient.get(int2)).targVol = 1.0F;
						}

						AmbientStreamManager.AmbientLoop ambientLoop;
						for (int2 = 0; int2 < this.nightAmbient.size(); ++int2) {
							ambientLoop = (AmbientStreamManager.AmbientLoop)this.nightAmbient.get(int2);
							ambientLoop.targVol *= float2;
						}

						for (int2 = 0; int2 < this.dayAmbient.size(); ++int2) {
							ambientLoop = (AmbientStreamManager.AmbientLoop)this.dayAmbient.get(int2);
							ambientLoop.targVol *= 1.0F - float2;
						}

						for (int2 = 0; int2 < this.indoorAmbient.size(); ++int2) {
							ambientLoop = (AmbientStreamManager.AmbientLoop)this.indoorAmbient.get(int2);
							ambientLoop.targVol *= boolean1 ? 0.8F : 0.0F;
						}

						for (int2 = 0; int2 < this.outdoorAmbient.size(); ++int2) {
							ambientLoop = (AmbientStreamManager.AmbientLoop)this.outdoorAmbient.get(int2);
							ambientLoop.targVol *= boolean1 ? 0.15F : 0.8F;
						}

						for (int2 = 0; int2 < this.rainAmbient.size(); ++int2) {
							ambientLoop = (AmbientStreamManager.AmbientLoop)this.rainAmbient.get(int2);
							ambientLoop.targVol *= boolean2 ? 1.0F : 0.0F;
							if (((AmbientStreamManager.AmbientLoop)this.rainAmbient.get(int2)).channel != 0L) {
								javafmod.FMOD_Studio_SetParameter(((AmbientStreamManager.AmbientLoop)this.rainAmbient.get(int2)).channel, "Intensity", ClimateManager.getInstance().getPrecipitationIntensity());
							}
						}

						for (int2 = 0; int2 < this.allAmbient.size(); ++int2) {
							((AmbientStreamManager.AmbientLoop)this.allAmbient.get(int2)).update();
						}

						for (int2 = 0; int2 < this.alarmList.size(); ++int2) {
							((Alarm)this.alarmList.get(int2)).update();
							if (((Alarm)this.alarmList.get(int2)).finished) {
								this.alarmList.remove(int2);
								--int2;
							}
						}

						this.doOneShotAmbients();
					}
				}
			}
		}
	}

	public void doOneShotAmbients() {
		for (int int1 = 0; int1 < this.ambient.size(); ++int1) {
			AmbientStreamManager.Ambient ambient = (AmbientStreamManager.Ambient)this.ambient.get(int1);
			if (ambient.finished()) {
				DebugLog.log(DebugType.Sound, "ambient: removing ambient sound " + ambient.name);
				this.ambient.remove(int1--);
			} else {
				ambient.update();
			}
		}
	}

	public void addRandomAmbient() {
		if (!Core.GameMode.equals("LastStand") && !Core.GameMode.equals("Tutorial")) {
			ArrayList arrayList = new ArrayList();
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.isAlive()) {
					arrayList.add(player);
				}
			}

			if (!arrayList.isEmpty()) {
				IsoPlayer player2 = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
				String string = "";
				if (GameTime.instance.getHour() > 7 && GameTime.instance.getHour() < 21) {
					switch (Rand.Next(3)) {
					case 0: 
						if (Rand.Next(10) < 2) {
							string = "MetaDogBark";
						}

						break;
					
					case 1: 
						if (Rand.Next(10) < 3) {
							string = "MetaScream";
						}

					
					}
				} else {
					switch (Rand.Next(5)) {
					case 0: 
						if (Rand.Next(10) < 2) {
							string = "MetaDogBark";
						}

						break;
					
					case 1: 
						if (Rand.Next(13) < 3) {
							string = "MetaScream";
						}

						break;
					
					case 2: 
						string = "MetaOwl";
						break;
					
					case 3: 
						string = "MetaWolfHowl";
					
					}
				}

				if (!string.isEmpty()) {
					float float1 = player2.x;
					float float2 = player2.y;
					double double1 = (double)Rand.Next(-3.1415927F, 3.1415927F);
					this.tempo.x = (float)Math.cos(double1);
					this.tempo.y = (float)Math.sin(double1);
					this.tempo.setLength(1000.0F);
					float1 += this.tempo.x;
					float2 += this.tempo.y;
					if (!GameClient.bClient) {
						System.out.println("playing ambient: " + string + " at dist: " + Math.abs(float1 - player2.x) + "," + Math.abs(float2 - player2.y));
						AmbientStreamManager.Ambient ambient = new AmbientStreamManager.Ambient(string, float1, float2, 50.0F, Rand.Next(0.2F, 0.5F));
						this.ambient.add(ambient);
					}
				}
			}
		}
	}

	public void addBlend(String string, float float1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		AmbientStreamManager.AmbientLoop ambientLoop = new AmbientStreamManager.AmbientLoop(0.0F, string, float1);
		this.allAmbient.add(ambientLoop);
		if (boolean1) {
			this.indoorAmbient.add(ambientLoop);
		} else {
			this.outdoorAmbient.add(ambientLoop);
		}

		if (boolean2) {
			this.rainAmbient.add(ambientLoop);
		}

		if (boolean3) {
			this.nightAmbient.add(ambientLoop);
		}

		if (boolean4) {
			this.dayAmbient.add(ambientLoop);
		}
	}

	public void init() {
		if (!this.initialized) {
			this.initialized = true;
			this.addBlend("AmbientInsideWindRain", 1.0F, true, true, false, false);
			this.addBlend("AmbientRain", 1.0F, false, true, false, false);
			this.addBlend("AmbientOutsideWindyDay", 1.0F, false, false, false, false);
			this.addBlend("AmbientOutsideWindyDay", 1.0F, false, false, false, true);
			this.addBlend("AmbientOutsideWindyNight", 1.0F, false, false, true, false);
			this.addBlend("AmbientInsideWind", 1.0F, true, false, false, false);
			this.addBlend("AmbientOutsideCricketsNight", 1.0F, false, false, true, false);
			this.addBlend("AmbientOutsideBirdsDay", 1.0F, false, false, false, true);
		}
	}

	public void doGunEvent() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.isAlive()) {
				arrayList.add(player);
			}
		}

		if (!arrayList.isEmpty()) {
			IsoPlayer player2 = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
			String string = null;
			switch (Rand.Next(6)) {
			case 0: 
				string = "MetaAssaultRifle1";
				break;
			
			case 1: 
				string = "MetaPistol1";
				break;
			
			case 2: 
				string = "MetaShotgun1";
				break;
			
			case 3: 
				string = "MetaPistol2";
				break;
			
			case 4: 
				string = "MetaPistol3";
				break;
			
			case 5: 
				string = "MetaShotgun1";
			
			}

			float float1 = player2.x;
			float float2 = player2.y;
			short short1 = 600;
			double double1 = (double)Rand.Next(-3.1415927F, 3.1415927F);
			this.tempo.x = (float)Math.cos(double1);
			this.tempo.y = (float)Math.sin(double1);
			this.tempo.setLength((float)(short1 - 100));
			float1 += this.tempo.x;
			float2 += this.tempo.y;
			WorldSoundManager.instance.addSound((IsoObject)null, (int)float1, (int)float2, 0, short1, short1);
			float float3 = 1.0F;
			AmbientStreamManager.Ambient ambient = new AmbientStreamManager.Ambient(string, float1, float2, 700.0F, float3);
			this.ambient.add(ambient);
		}
	}

	public void doAlarm(RoomDef roomDef) {
		if (!roomDef.building.isAllExplored()) {
			roomDef.building.bAlarmed = false;
			roomDef.building.setAllExplored(true);
			this.alarmList.add(new Alarm(roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2));
		}
	}

	public void stop() {
		Iterator iterator = this.allAmbient.iterator();
		while (iterator.hasNext()) {
			AmbientStreamManager.AmbientLoop ambientLoop = (AmbientStreamManager.AmbientLoop)iterator.next();
			ambientLoop.stop();
		}

		this.allAmbient.clear();
		this.ambient.clear();
		this.dayAmbient.clear();
		this.indoorAmbient.clear();
		this.nightAmbient.clear();
		this.outdoorAmbient.clear();
		this.rainAmbient.clear();
		this.windAmbient.clear();
		this.alarmList.clear();
		this.initialized = false;
	}

	public void addAmbient(String string, int int1, int int2, int int3, float float1) {
		if (GameClient.bClient) {
			AmbientStreamManager.Ambient ambient = new AmbientStreamManager.Ambient(string, (float)int1, (float)int2, (float)int3, float1, true);
			this.ambient.add(ambient);
		}
	}

	public void addAmbientEmitter(float float1, float float2, int int1, String string) {
		AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = this.freeEmitters.isEmpty() ? new AmbientStreamManager.WorldSoundEmitter() : (AmbientStreamManager.WorldSoundEmitter)this.freeEmitters.pop();
		worldSoundEmitter.x = float1;
		worldSoundEmitter.y = float2;
		worldSoundEmitter.z = (float)int1;
		worldSoundEmitter.daytime = null;
		if (worldSoundEmitter.fmodEmitter == null) {
			worldSoundEmitter.fmodEmitter = new FMODSoundEmitter();
		}

		worldSoundEmitter.fmodEmitter.x = float1;
		worldSoundEmitter.fmodEmitter.y = float2;
		worldSoundEmitter.fmodEmitter.z = (float)int1;
		worldSoundEmitter.channel = worldSoundEmitter.fmodEmitter.playAmbientLoopedImpl(string);
		worldSoundEmitter.fmodEmitter.randomStart();
		SoundManager.instance.registerEmitter(worldSoundEmitter.fmodEmitter);
		this.worldEmitters.add(worldSoundEmitter);
	}

	public void addDaytimeAmbientEmitter(float float1, float float2, int int1, String string) {
		AmbientStreamManager.WorldSoundEmitter worldSoundEmitter = this.freeEmitters.isEmpty() ? new AmbientStreamManager.WorldSoundEmitter() : (AmbientStreamManager.WorldSoundEmitter)this.freeEmitters.pop();
		worldSoundEmitter.x = float1;
		worldSoundEmitter.y = float2;
		worldSoundEmitter.z = (float)int1;
		if (worldSoundEmitter.fmodEmitter == null) {
			worldSoundEmitter.fmodEmitter = new FMODSoundEmitter();
		}

		worldSoundEmitter.fmodEmitter.x = float1;
		worldSoundEmitter.fmodEmitter.y = float2;
		worldSoundEmitter.fmodEmitter.z = (float)int1;
		worldSoundEmitter.daytime = string;
		worldSoundEmitter.dawn = Rand.Next(7.0F, 8.0F);
		worldSoundEmitter.dusk = Rand.Next(19.0F, 20.0F);
		SoundManager.instance.registerEmitter(worldSoundEmitter.fmodEmitter);
		this.worldEmitters.add(worldSoundEmitter);
	}

	public static class AmbientLoop {
		public static float volChangeAmount = 0.01F;
		public float targVol;
		public float currVol;
		public String name;
		public float volumedelta = 1.0F;
		public long channel = -1L;
		public FMODSoundEmitter emitter = new FMODSoundEmitter();

		public AmbientLoop(float float1, String string, float float2) {
			this.volumedelta = float2;
			this.channel = this.emitter.playAmbientLoopedImpl(string);
			this.targVol = float1;
			this.currVol = 0.0F;
			this.update();
		}

		public void update() {
			if (this.targVol > this.currVol) {
				this.currVol += volChangeAmount;
				if (this.currVol > this.targVol) {
					this.currVol = this.targVol;
				}
			}

			if (this.targVol < this.currVol) {
				this.currVol -= volChangeAmount;
				if (this.currVol < this.targVol) {
					this.currVol = this.targVol;
				}
			}

			this.emitter.setVolumeAll(this.currVol * this.volumedelta);
			this.emitter.tick();
		}

		public void stop() {
			this.emitter.stopAll();
		}
	}

	public class Ambient {
		public float x;
		public float y;
		public String name;
		float radius;
		float volume;
		int worldSoundRadius;
		int worldSoundVolume;
		public boolean trackMouse;
		FMODSoundEmitter emitter;

		public Ambient(String string, float float1, float float2, float float3, float float4) {
			this(string, float1, float2, float3, float4, false);
		}

		public Ambient(String string, float float1, float float2, float float3, float float4, boolean boolean1) {
			this.trackMouse = false;
			this.emitter = new FMODSoundEmitter();
			this.name = string;
			this.x = float1;
			this.y = float2;
			this.radius = float3;
			this.volume = float4;
			this.emitter.x = float1;
			this.emitter.y = float2;
			this.emitter.z = 0.0F;
			this.emitter.playAmbientSound(string);
			this.update();
			LuaEventManager.triggerEvent("OnAmbientSound", string, float1, float2);
		}

		public boolean finished() {
			return this.emitter.isEmpty();
		}

		public void update() {
			this.emitter.tick();
			if (this.trackMouse && IsoPlayer.instance != null) {
				float float1 = (float)Mouse.getX();
				float float2 = (float)(Core.getInstance().getScreenHeight() - Mouse.getY() - 1);
				float1 -= (float)IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
				float2 -= (float)IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
				float1 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				float2 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
				int int1 = (int)IsoPlayer.instance.getZ();
				this.emitter.x = (float)((int)IsoUtils.XToIso(float1 - 30.0F, float2 - 366.0F, (float)int1));
				this.emitter.y = (float)((int)IsoUtils.YToIso(float1 - 30.0F, float2 - 366.0F, (float)int1));
			}

			if (!GameClient.bClient && this.worldSoundRadius > 0 && this.worldSoundVolume > 0) {
				WorldSoundManager.instance.addSound((IsoObject)null, (int)this.x, (int)this.y, 0, this.worldSoundRadius, this.worldSoundVolume);
			}
		}

		public void repeatWorldSounds(int int1, int int2) {
			this.worldSoundRadius = int1;
			this.worldSoundVolume = int2;
		}

		private IsoGameCharacter getClosestListener(float float1, float float2) {
			IsoPlayer player = null;
			float float3 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player2 = IsoPlayer.players[int1];
				if (player2 != null && player2.getCurrentSquare() != null) {
					float float4 = player2.getX();
					float float5 = player2.getY();
					float float6 = IsoUtils.DistanceToSquared(float4, float5, float1, float2);
					if (player2.HasTrait("HardOfHearing")) {
						float6 *= 4.5F;
					}

					if (player2.HasTrait("Deaf")) {
						float6 = Float.MAX_VALUE;
					}

					if (float6 < float3) {
						player = player2;
						float3 = float6;
					}
				}
			}

			return player;
		}
	}

	public static class WorldSoundEmitter {
		public FMODSoundEmitter fmodEmitter;
		public float x;
		public float y;
		public float z;
		public long channel = -1L;
		public String daytime;
		public float dawn;
		public float dusk;
	}
}
