package zombie;

import java.util.ArrayList;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.network.GameServer;


public final class AmbientSoundManager extends BaseAmbientStreamManager {
	public final ArrayList ambient = new ArrayList();
	private final Vector2 tempo = new Vector2();
	private int electricityShutOffState = -1;
	private long electricityShutOffTime = 0L;
	public boolean initialized = false;

	public void update() {
		if (this.initialized) {
			this.updatePowerSupply();
			this.doOneShotAmbients();
		}
	}

	public void addAmbient(String string, int int1, int int2, int int3, float float1) {
	}

	public void addAmbientEmitter(float float1, float float2, int int1, String string) {
	}

	public void addDaytimeAmbientEmitter(float float1, float float2, int int1, String string) {
	}

	public void doOneShotAmbients() {
		for (int int1 = 0; int1 < this.ambient.size(); ++int1) {
			AmbientSoundManager.Ambient ambient = (AmbientSoundManager.Ambient)this.ambient.get(int1);
			if (ambient.finished()) {
				DebugLog.log(DebugType.Sound, "ambient: removing ambient sound " + ambient.name);
				this.ambient.remove(int1--);
			} else {
				ambient.update();
			}
		}
	}

	public void init() {
		if (!this.initialized) {
			this.initialized = true;
		}
	}

	public void addBlend(String string, float float1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
	}

	protected void addRandomAmbient() {
		if (!GameServer.Players.isEmpty()) {
			IsoPlayer player = (IsoPlayer)GameServer.Players.get(Rand.Next(GameServer.Players.size()));
			if (player != null) {
				String string = null;
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

				if (string != null) {
					float float1 = player.x;
					float float2 = player.y;
					double double1 = (double)Rand.Next(-3.1415927F, 3.1415927F);
					this.tempo.x = (float)Math.cos(double1);
					this.tempo.y = (float)Math.sin(double1);
					this.tempo.setLength(1000.0F);
					float1 += this.tempo.x;
					float2 += this.tempo.y;
					AmbientSoundManager.Ambient ambient = new AmbientSoundManager.Ambient(string, float1, float2, 50.0F, Rand.Next(0.2F, 0.5F));
					this.ambient.add(ambient);
					GameServer.sendAmbient(string, (int)float1, (int)float2, 50, Rand.Next(0.2F, 0.5F));
				}
			}
		}
	}

	public void doGunEvent() {
		ArrayList arrayList = GameServer.getPlayers();
		if (!arrayList.isEmpty()) {
			IsoPlayer player = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
			String string = null;
			float float1 = player.x;
			float float2 = player.y;
			short short1 = 600;
			double double1 = (double)Rand.Next(-3.1415927F, 3.1415927F);
			this.tempo.x = (float)Math.cos(double1);
			this.tempo.y = (float)Math.sin(double1);
			this.tempo.setLength((float)(short1 - 100));
			float1 += this.tempo.x;
			float2 += this.tempo.y;
			WorldSoundManager.instance.addSound((Object)null, (int)float1 + Rand.Next(-10, 10), (int)float2 + Rand.Next(-10, 10), 0, 600, 600);
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

			float float3 = 1.0F;
			AmbientSoundManager.Ambient ambient = new AmbientSoundManager.Ambient(string, float1, float2, 700.0F, float3);
			this.ambient.add(ambient);
			GameServer.sendAmbient(string, (int)float1, (int)float2, (int)Math.ceil((double)ambient.radius), ambient.volume);
		}
	}

	public void doAlarm(RoomDef roomDef) {
		if (roomDef != null && roomDef.building != null && roomDef.building.bAlarmed) {
			float float1 = 1.0F;
			AmbientSoundManager.Ambient ambient = new AmbientSoundManager.Ambient("burglar2", (float)(roomDef.x + roomDef.getW() / 2), (float)(roomDef.y + roomDef.getH() / 2), 700.0F, float1);
			ambient.duration = 49;
			ambient.worldSoundDelay = 3;
			roomDef.building.bAlarmed = false;
			roomDef.building.setAllExplored(true);
			this.ambient.add(ambient);
			GameServer.sendAlarm(roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2);
		}
	}

	public void stop() {
		this.ambient.clear();
		this.initialized = false;
	}

	private void updatePowerSupply() {
		boolean boolean1 = GameTime.getInstance().NightsSurvived < SandboxOptions.getInstance().getElecShutModifier();
		if (this.electricityShutOffState == -1) {
			IsoWorld.instance.setHydroPowerOn(boolean1);
		}

		if (this.electricityShutOffState == 0) {
			if (boolean1) {
				IsoWorld.instance.setHydroPowerOn(true);
				this.checkHaveElectricity();
				this.electricityShutOffTime = 0L;
			} else if (this.electricityShutOffTime != 0L && System.currentTimeMillis() >= this.electricityShutOffTime) {
				this.electricityShutOffTime = 0L;
				IsoWorld.instance.setHydroPowerOn(false);
				this.checkHaveElectricity();
			}
		}

		if (this.electricityShutOffState == 1 && !boolean1) {
			this.electricityShutOffTime = System.currentTimeMillis() + 2650L;
		}

		this.electricityShutOffState = boolean1 ? 1 : 0;
	}

	private void checkHaveElectricity() {
	}

	public class Ambient {
		public float x;
		public float y;
		public String name;
		public float radius;
		public float volume;
		long startTime;
		public int duration;
		public int worldSoundDelay = 0;

		public Ambient(String string, float float1, float float2, float float3, float float4) {
			this.name = string;
			this.x = float1;
			this.y = float2;
			this.radius = float3;
			this.volume = float4;
			this.startTime = System.currentTimeMillis() / 1000L;
			this.duration = 2;
			this.update();
			LuaEventManager.triggerEvent("OnAmbientSound", string, float1, float2);
		}

		public boolean finished() {
			long long1 = System.currentTimeMillis() / 1000L;
			return long1 - this.startTime >= (long)this.duration;
		}

		public void update() {
			long long1 = System.currentTimeMillis() / 1000L;
			if (long1 - this.startTime >= (long)this.worldSoundDelay) {
				WorldSoundManager.instance.addSound((Object)null, (int)this.x, (int)this.y, 0, 600, 600);
			}
		}
	}
}
