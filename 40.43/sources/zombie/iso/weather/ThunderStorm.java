package zombie.iso.weather;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class ThunderStorm {
	public static int MAP_MIN_X = -3000;
	public static int MAP_MIN_Y = -3000;
	public static int MAP_MAX_X = 25000;
	public static int MAP_MAX_Y = 20000;
	private boolean hasActiveThunderClouds = false;
	private float cloudMaxRadius = 20000.0F;
	private ThunderStorm.ThunderEvent[] events = new ThunderStorm.ThunderEvent[30];
	private ThunderStorm.ThunderCloud[] clouds = new ThunderStorm.ThunderCloud[3];
	private ClimateManager climateManager;
	private ArrayList cloudCache;
	private boolean donoise = false;
	private int strikeRadius = 4000;
	private final ThunderStorm.PlayerLightningInfo[] lightningInfos = new ThunderStorm.PlayerLightningInfo[4];
	private ThunderStorm.ThunderEvent networkThunderEvent = new ThunderStorm.ThunderEvent();
	private ThunderStorm.ThunderCloud dummyCloud;

	public ArrayList getClouds() {
		if (this.cloudCache == null) {
			this.cloudCache = new ArrayList(this.clouds.length);
			for (int int1 = 0; int1 < this.clouds.length; ++int1) {
				this.cloudCache.add(this.clouds[int1]);
			}
		}

		return this.cloudCache;
	}

	public ThunderStorm(ClimateManager climateManager) {
		this.climateManager = climateManager;
		int int1;
		for (int1 = 0; int1 < this.events.length; ++int1) {
			this.events[int1] = new ThunderStorm.ThunderEvent();
		}

		for (int1 = 0; int1 < this.clouds.length; ++int1) {
			this.clouds[int1] = new ThunderStorm.ThunderCloud();
		}

		for (int1 = 0; int1 < 4; ++int1) {
			this.lightningInfos[int1] = new ThunderStorm.PlayerLightningInfo();
		}
	}

	private ThunderStorm.ThunderEvent getFreeEvent() {
		for (int int1 = 0; int1 < this.events.length; ++int1) {
			if (!this.events[int1].isRunning) {
				return this.events[int1];
			}
		}

		return null;
	}

	private ThunderStorm.ThunderCloud getFreeCloud() {
		for (int int1 = 0; int1 < this.clouds.length; ++int1) {
			if (!this.clouds[int1].isRunning) {
				return this.clouds[int1];
			}
		}

		return null;
	}

	private ThunderStorm.ThunderCloud getCloud(int int1) {
		byte byte1 = 0;
		return byte1 < this.clouds.length ? this.clouds[byte1] : null;
	}

	public boolean HasActiveThunderClouds() {
		return this.hasActiveThunderClouds;
	}

	public void noise(String string) {
		if (this.donoise && (Core.bDebug || GameServer.bServer && GameServer.bDebug)) {
			DebugLog.log("thunderstorm: " + string);
		}
	}

	public void stopAllClouds() {
		for (int int1 = 0; int1 < this.clouds.length; ++int1) {
			this.stopCloud(int1);
		}
	}

	public void stopCloud(int int1) {
		ThunderStorm.ThunderCloud thunderCloud = this.getCloud(int1);
		if (thunderCloud != null) {
			thunderCloud.isRunning = false;
		}
	}

	private float addToAngle(float float1, float float2) {
		float1 += float2;
		if (float1 > 360.0F) {
			float1 -= 360.0F;
		} else if (float1 < 0.0F) {
			float1 += 360.0F;
		}

		return float1;
	}

	public void startThunderCloud(float float1, float float2, float float3, float float4, float float5, double double1, boolean boolean1) {
		if (!GameClient.bClient) {
			ThunderStorm.ThunderCloud thunderCloud = this.getFreeCloud();
			if (thunderCloud != null) {
				float2 = this.addToAngle(float2, Rand.Next(-10.0F, 10.0F));
				thunderCloud.startTime = GameTime.instance.getWorldAgeHours();
				thunderCloud.endTime = thunderCloud.startTime + double1;
				thunderCloud.duration = double1;
				thunderCloud.strength = ClimateManager.clamp01(float1);
				thunderCloud.angle = float2;
				thunderCloud.radius = float3;
				if (thunderCloud.radius > this.cloudMaxRadius) {
					thunderCloud.radius = this.cloudMaxRadius;
				}

				thunderCloud.eventFrequency = float4;
				thunderCloud.thunderRatio = ClimateManager.clamp01(float5);
				float float6 = this.addToAngle(float2, 180.0F);
				int int1 = MAP_MAX_X - MAP_MIN_X;
				int int2 = MAP_MAX_Y - MAP_MIN_Y;
				int int3 = (int)Math.sqrt(Math.pow((double)int1, 2.0) + Math.pow((double)int2, 2.0));
				int3 /= 2;
				int int4 = Rand.Next(MAP_MIN_X + int1 / 5, MAP_MAX_X - int1 / 5);
				int int5 = Rand.Next(MAP_MIN_Y + int2 / 5, MAP_MAX_Y - int2 / 5);
				int int6;
				if (boolean1) {
					if (!GameServer.bServer) {
						int4 = IsoPlayer.instance.getCurrentSquare().getX();
						int5 = IsoPlayer.instance.getCurrentSquare().getY();
					} else {
						if (GameServer.Players.isEmpty()) {
							return;
						}

						ArrayList arrayList = GameServer.getPlayers();
						for (int6 = arrayList.size() - 1; int6 >= 0; --int6) {
							if (((IsoPlayer)arrayList.get(int6)).getCurrentSquare() == null) {
								arrayList.remove(int6);
							}
						}

						if (!arrayList.isEmpty()) {
							IsoPlayer player = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
							int4 = player.getCurrentSquare().getX();
							int5 = player.getCurrentSquare().getY();
						}
					}
				}

				int int7 = int3 + Rand.Next(1500, 7500);
				int6 = (int)((double)int4 + (double)int7 * Math.cos(Math.toRadians((double)float6)));
				int int8 = (int)((double)int5 + (double)int7 * Math.sin(Math.toRadians((double)float6)));
				int7 = int3 + Rand.Next(1500, 7500);
				int int9 = (int)((double)int4 + (double)int7 * Math.cos(Math.toRadians((double)float2)));
				int int10 = (int)((double)int5 + (double)int7 * Math.sin(Math.toRadians((double)float2)));
				thunderCloud.startX = int6;
				thunderCloud.startY = int8;
				thunderCloud.endX = int9;
				thunderCloud.endY = int10;
				thunderCloud.currentX = int6;
				thunderCloud.currentY = int8;
				thunderCloud.isRunning = true;
				thunderCloud.suspendTimer.init(3);
			}
		}
	}

	public void update(double double1) {
		int int1;
		if (!GameClient.bClient || GameServer.bServer) {
			this.hasActiveThunderClouds = false;
			for (int1 = 0; int1 < this.clouds.length; ++int1) {
				ThunderStorm.ThunderCloud thunderCloud = this.clouds[int1];
				if (thunderCloud.isRunning) {
					if (double1 < thunderCloud.endTime) {
						float float1 = (float)((double1 - thunderCloud.startTime) / thunderCloud.duration);
						thunderCloud.currentX = (int)ClimateManager.lerp(float1, (float)thunderCloud.startX, (float)thunderCloud.endX);
						thunderCloud.currentY = (int)ClimateManager.lerp(float1, (float)thunderCloud.startY, (float)thunderCloud.endY);
						thunderCloud.suspendTimer.update();
						this.hasActiveThunderClouds = true;
						if (thunderCloud.suspendTimer.finished()) {
							float float2 = Rand.Next(3.5F - 3.0F * thunderCloud.strength, 24.0F - 20.0F * thunderCloud.strength);
							thunderCloud.suspendTimer.init((int)(float2 * 60.0F));
							float float3 = Rand.Next(0.0F, 1.0F);
							if (float3 < 0.6F) {
								this.strikeRadius = (int)(thunderCloud.radius / 2.0F) / 3;
							} else if (float3 < 0.9F) {
								this.strikeRadius = (int)(thunderCloud.radius / 2.0F) / 4 * 3;
							} else {
								this.strikeRadius = (int)(thunderCloud.radius / 2.0F);
							}

							if (Rand.Next(0.0F, 1.0F) < thunderCloud.thunderRatio) {
								this.noise("trigger thunder event");
								this.triggerThunderEvent(Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius), Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius), true, true, Rand.Next(0.0F, 1.0F) > 0.4F);
							} else {
								this.triggerThunderEvent(Rand.Next(thunderCloud.currentX - this.strikeRadius, thunderCloud.currentX + this.strikeRadius), Rand.Next(thunderCloud.currentY - this.strikeRadius, thunderCloud.currentY + this.strikeRadius), false, false, true);
								this.noise("trigger rumble event");
							}
						}
					} else {
						thunderCloud.isRunning = false;
					}
				}
			}
		}

		if (GameClient.bClient || !GameServer.bServer) {
			for (int1 = 0; int1 < 4; ++int1) {
				ThunderStorm.PlayerLightningInfo playerLightningInfo = this.lightningInfos[int1];
				if (playerLightningInfo.lightningState == ThunderStorm.LightningState.ApplyLightning) {
					playerLightningInfo.timer.update();
					if (!playerLightningInfo.timer.finished()) {
						playerLightningInfo.lightningMod = ClimateManager.clamp01(playerLightningInfo.timer.ratio());
						ClimateManager.ClimateFloat climateFloat = this.climateManager.dayLightStrength;
						climateFloat.finalValue += (1.0F - this.climateManager.dayLightStrength.finalValue) * (1.0F - playerLightningInfo.lightningMod);
						IsoPlayer player = IsoPlayer.players[int1];
						if (player != null) {
							player.dirtyRecalcGridStackTime = 1.0F;
						}
					} else {
						this.noise("apply lightning done.");
						playerLightningInfo.timer.init(2);
						playerLightningInfo.lightningStrength = 0.0F;
						playerLightningInfo.lightningState = ThunderStorm.LightningState.Idle;
					}
				}
			}

			for (int1 = 0; int1 < this.events.length; ++int1) {
				ThunderStorm.ThunderEvent thunderEvent = this.events[int1];
				if (thunderEvent.isRunning) {
					thunderEvent.soundDelay.update();
					if (thunderEvent.soundDelay.finished()) {
						thunderEvent.isRunning = false;
						if (!Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
							long long1;
							GameSound gameSound;
							GameSoundClip gameSoundClip;
							long long2;
							if (thunderEvent.doStrike) {
								this.noise("thunder sound");
								gameSound = GameSounds.getSound("Thunder");
								gameSoundClip = gameSound == null ? null : gameSound.getRandomClip();
								if (gameSoundClip != null && gameSoundClip.event != null && !gameSoundClip.event.isEmpty()) {
									long2 = javafmod.FMOD_Studio_System_GetEvent("event:/" + gameSoundClip.event);
									long1 = javafmod.FMOD_Studio_System_CreateEventInstance(long2);
									javafmod.FMOD_Studio_EventInstance3D(long1, (float)thunderEvent.eventX, (float)thunderEvent.eventY, 100.0F);
									javafmod.FMOD_Studio_SetVolume(long1, gameSoundClip.getEffectiveVolume());
									javafmod.FMOD_Studio_StartEvent(long1);
									javafmod.FMOD_Studio_ReleaseEventInstance(long1);
								}
							}

							if (thunderEvent.doRumble) {
								this.noise("rumble sound");
								gameSound = GameSounds.getSound("RumbleThunder");
								gameSoundClip = gameSound == null ? null : gameSound.getRandomClip();
								if (gameSoundClip != null && gameSoundClip.event != null && !gameSoundClip.event.isEmpty()) {
									long2 = javafmod.FMOD_Studio_System_GetEvent("event:/" + gameSoundClip.event);
									long1 = javafmod.FMOD_Studio_System_CreateEventInstance(long2);
									javafmod.FMOD_Studio_EventInstance3D(long1, (float)thunderEvent.eventX, (float)thunderEvent.eventY, 200.0F);
									javafmod.FMOD_Studio_SetVolume(long1, gameSoundClip.getEffectiveVolume());
									javafmod.FMOD_Studio_StartEvent(long1);
									javafmod.FMOD_Studio_ReleaseEventInstance(long1);
								}
							}
						}
					}
				}
			}
		}
	}

	public void applyLightningForPlayer(RenderSettings.PlayerRenderSettings playerRenderSettings, int int1, IsoPlayer player) {
		ThunderStorm.PlayerLightningInfo playerLightningInfo = this.lightningInfos[int1];
		if (playerLightningInfo.lightningState == ThunderStorm.LightningState.ApplyLightning) {
			ClimateColorInfo climateColorInfo = playerRenderSettings.CM_GlobalLight;
			playerLightningInfo.lightningColor.getExterior().r = climateColorInfo.getExterior().r + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().r);
			playerLightningInfo.lightningColor.getExterior().g = climateColorInfo.getExterior().g + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().g);
			playerLightningInfo.lightningColor.getExterior().b = climateColorInfo.getExterior().b + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getExterior().b);
			playerLightningInfo.lightningColor.getInterior().r = climateColorInfo.getInterior().r + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().r);
			playerLightningInfo.lightningColor.getInterior().g = climateColorInfo.getInterior().g + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().g);
			playerLightningInfo.lightningColor.getInterior().b = climateColorInfo.getInterior().b + playerLightningInfo.lightningStrength * (1.0F - climateColorInfo.getInterior().b);
			playerLightningInfo.lightningColor.interp(playerRenderSettings.CM_GlobalLight, playerLightningInfo.lightningMod, playerLightningInfo.outColor);
			playerRenderSettings.CM_GlobalLight.getExterior().r = playerLightningInfo.outColor.getExterior().r;
			playerRenderSettings.CM_GlobalLight.getExterior().g = playerLightningInfo.outColor.getExterior().g;
			playerRenderSettings.CM_GlobalLight.getExterior().b = playerLightningInfo.outColor.getExterior().b;
			playerRenderSettings.CM_GlobalLight.getInterior().r = playerLightningInfo.outColor.getInterior().r;
			playerRenderSettings.CM_GlobalLight.getInterior().g = playerLightningInfo.outColor.getInterior().g;
			playerRenderSettings.CM_GlobalLight.getInterior().b = playerLightningInfo.outColor.getInterior().b;
			playerRenderSettings.CM_Ambient = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, playerRenderSettings.CM_Ambient);
			playerRenderSettings.CM_DayLightStrength = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, playerRenderSettings.CM_DayLightStrength);
			playerRenderSettings.CM_Desaturation = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0F, playerRenderSettings.CM_Desaturation);
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				playerRenderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 1.0F, playerRenderSettings.CM_GlobalLightIntensity);
			} else {
				playerRenderSettings.CM_GlobalLightIntensity = ClimateManager.lerp(playerLightningInfo.lightningMod, 0.0F, playerRenderSettings.CM_GlobalLightIntensity);
			}
		}
	}

	public boolean isModifyingNight() {
		return false;
	}

	public void triggerThunderEvent(int int1, int int2, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (GameServer.bServer) {
			this.networkThunderEvent.eventX = int1;
			this.networkThunderEvent.eventY = int2;
			this.networkThunderEvent.doStrike = boolean1;
			this.networkThunderEvent.doLightning = boolean2;
			this.networkThunderEvent.doRumble = boolean3;
			this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)2, (UdpConnection)null);
		} else if (!GameClient.bClient) {
			this.enqueueThunderEvent(int1, int2, boolean1, boolean2, boolean3);
		}
	}

	public void writeNetThunderEvent(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putInt(this.networkThunderEvent.eventX);
		byteBuffer.putInt(this.networkThunderEvent.eventY);
		byteBuffer.put((byte)(this.networkThunderEvent.doStrike ? 1 : 0));
		byteBuffer.put((byte)(this.networkThunderEvent.doLightning ? 1 : 0));
		byteBuffer.put((byte)(this.networkThunderEvent.doRumble ? 1 : 0));
	}

	public void readNetThunderEvent(ByteBuffer byteBuffer) throws IOException {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		this.enqueueThunderEvent(int1, int2, boolean1, boolean2, boolean3);
	}

	public void enqueueThunderEvent(int int1, int int2, boolean boolean1, boolean boolean2, boolean boolean3) {
		LuaEventManager.triggerEvent("OnThunderEvent", int1, int2, boolean1, boolean2, boolean3);
		if (boolean1 || boolean3) {
			int int3 = 9999999;
			for (int int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
				IsoPlayer player = IsoPlayer.players[int4];
				if (player != null) {
					int int5 = this.GetDistance((int)player.getX(), (int)player.getY(), int1, int2);
					if (int5 < int3) {
						int3 = int5;
					}

					if (boolean2) {
						this.lightningInfos[int4].distance = int5;
						this.lightningInfos[int4].x = int1;
						this.lightningInfos[int4].y = int2;
					}
				}
			}

			this.noise("dist to player = " + int3);
			if (int3 < 10000) {
				ThunderStorm.ThunderEvent thunderEvent = this.getFreeEvent();
				if (thunderEvent != null) {
					thunderEvent.doRumble = boolean3;
					thunderEvent.doStrike = boolean1;
					thunderEvent.eventX = int1;
					thunderEvent.eventY = int2;
					thunderEvent.isRunning = true;
					thunderEvent.soundDelay.init((int)((float)int3 / 300.0F * 60.0F));
					if (boolean2) {
						for (int int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
							IsoPlayer player2 = IsoPlayer.players[int6];
							if (player2 != null && (float)this.lightningInfos[int6].distance < 7500.0F) {
								float float1 = 1.0F - (float)this.lightningInfos[int6].distance / 7500.0F;
								this.lightningInfos[int6].lightningState = ThunderStorm.LightningState.ApplyLightning;
								if (float1 > this.lightningInfos[int6].lightningStrength) {
									this.lightningInfos[int6].lightningStrength = float1;
									this.lightningInfos[int6].timer.init(20 + (int)(80.0F * this.lightningInfos[int6].lightningStrength));
								}
							}
						}
					}
				}
			}
		}
	}

	private int GetDistance(int int1, int int2, int int3, int int4) {
		return (int)Math.sqrt(Math.pow((double)(int1 - int3), 2.0) + Math.pow((double)(int2 - int4), 2.0));
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		if (GameClient.bClient && !GameServer.bServer) {
			dataOutputStream.writeByte(0);
		} else {
			dataOutputStream.writeByte(this.clouds.length);
			for (int int1 = 0; int1 < this.clouds.length; ++int1) {
				ThunderStorm.ThunderCloud thunderCloud = this.clouds[int1];
				dataOutputStream.writeBoolean(thunderCloud.isRunning);
				if (thunderCloud.isRunning) {
					dataOutputStream.writeInt(thunderCloud.startX);
					dataOutputStream.writeInt(thunderCloud.startY);
					dataOutputStream.writeInt(thunderCloud.endX);
					dataOutputStream.writeInt(thunderCloud.endY);
					dataOutputStream.writeFloat(thunderCloud.radius);
					dataOutputStream.writeFloat(thunderCloud.angle);
					dataOutputStream.writeFloat(thunderCloud.strength);
					dataOutputStream.writeFloat(thunderCloud.thunderRatio);
					dataOutputStream.writeDouble(thunderCloud.startTime);
					dataOutputStream.writeDouble(thunderCloud.endTime);
					dataOutputStream.writeDouble(thunderCloud.duration);
				}
			}
		}
	}

	public void load(DataInputStream dataInputStream) throws IOException {
		byte byte1 = dataInputStream.readByte();
		if (byte1 != 0) {
			if (byte1 > this.clouds.length && this.dummyCloud == null) {
				this.dummyCloud = new ThunderStorm.ThunderCloud();
			}

			for (int int1 = 0; int1 < byte1; ++int1) {
				boolean boolean1 = dataInputStream.readBoolean();
				ThunderStorm.ThunderCloud thunderCloud;
				if (int1 >= this.clouds.length) {
					thunderCloud = this.dummyCloud;
				} else {
					thunderCloud = this.clouds[int1];
				}

				thunderCloud.isRunning = boolean1;
				if (boolean1) {
					thunderCloud.startX = dataInputStream.readInt();
					thunderCloud.startY = dataInputStream.readInt();
					thunderCloud.endX = dataInputStream.readInt();
					thunderCloud.endY = dataInputStream.readInt();
					thunderCloud.radius = dataInputStream.readFloat();
					thunderCloud.angle = dataInputStream.readFloat();
					thunderCloud.strength = dataInputStream.readFloat();
					thunderCloud.thunderRatio = dataInputStream.readFloat();
					thunderCloud.startTime = dataInputStream.readDouble();
					thunderCloud.endTime = dataInputStream.readDouble();
					thunderCloud.duration = dataInputStream.readDouble();
				}
			}
		}
	}

	private class PlayerLightningInfo {
		public ThunderStorm.LightningState lightningState;
		public GameTime.AnimTimer timer;
		public float lightningStrength;
		public float lightningMod;
		public ClimateColorInfo lightningColor;
		public ClimateColorInfo outColor;
		public int x;
		public int y;
		public int distance;

		private PlayerLightningInfo() {
			this.lightningState = ThunderStorm.LightningState.Idle;
			this.timer = new GameTime.AnimTimer();
			this.lightningStrength = 1.0F;
			this.lightningMod = 0.0F;
			this.lightningColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
			this.outColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
			this.x = 0;
			this.y = 0;
			this.distance = 0;
		}

		PlayerLightningInfo(Object object) {
			this();
		}
	}
	private static enum LightningState {

		Idle,
		ApplyLightning;
	}

	public static class ThunderCloud {
		private int currentX;
		private int currentY;
		private int startX;
		private int startY;
		private int endX;
		private int endY;
		private double startTime;
		private double endTime;
		private double duration;
		private float strength;
		private float angle;
		private float radius;
		private float eventFrequency;
		private float thunderRatio;
		private boolean isRunning = false;
		private GameTime.AnimTimer suspendTimer = new GameTime.AnimTimer();

		public int getCurrentX() {
			return this.currentX;
		}

		public int getCurrentY() {
			return this.currentY;
		}

		public float getRadius() {
			return this.radius;
		}

		public boolean isRunning() {
			return this.isRunning;
		}

		public float getStrength() {
			return this.strength;
		}

		public double lifeTime() {
			return (this.startTime - this.endTime) / this.duration;
		}
	}

	private static class ThunderEvent {
		private int eventX;
		private int eventY;
		private boolean doLightning;
		private boolean doRumble;
		private boolean doStrike;
		private GameTime.AnimTimer soundDelay;
		private boolean isRunning;

		private ThunderEvent() {
			this.doLightning = false;
			this.doRumble = false;
			this.doStrike = false;
			this.soundDelay = new GameTime.AnimTimer();
			this.isRunning = false;
		}

		ThunderEvent(Object object) {
			this();
		}
	}
}
