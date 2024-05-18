package zombie.erosion;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.erosion.utils.Noise2D;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class ErosionMain {
	private static ErosionMain instance;
	private ErosionConfig cfg;
	private boolean debug;
	private IsoSpriteManager sprMngr;
	private ErosionIceQueen IceQueen;
	private boolean isSnow;
	private String world;
	private String cfgPath;
	private IsoChunk chunk;
	private ErosionData.Chunk chunkModData;
	private Noise2D noiseMain;
	private Noise2D noiseMoisture;
	private Noise2D noiseMinerals;
	private Noise2D noiseKudzu;
	private ErosionWorld World;
	private ErosionSeason Season;
	private int tickUnit = 144;
	private int ticks = 0;
	private int eTicks = 0;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private int epoch = 0;
	private static final int[][] soilTable = new int[][]{{1, 1, 1, 1, 1, 4, 4, 4, 4, 4}, {1, 1, 1, 1, 2, 5, 4, 4, 4, 4}, {1, 1, 1, 2, 2, 5, 5, 4, 4, 4}, {1, 1, 2, 2, 3, 6, 5, 5, 4, 4}, {1, 2, 2, 3, 3, 6, 6, 5, 5, 4}, {7, 8, 8, 9, 9, 12, 12, 11, 11, 10}, {7, 7, 8, 8, 9, 12, 11, 11, 10, 10}, {7, 7, 7, 8, 8, 11, 11, 10, 10, 10}, {7, 7, 7, 7, 8, 11, 10, 10, 10, 10}, {7, 7, 7, 7, 7, 10, 10, 10, 10, 10}};
	private int snowFrac = 0;
	private int snowFracYesterday = 0;
	private int[] snowFracOnDay;

	public static ErosionMain getInstance() {
		return instance;
	}

	public ErosionMain(IsoSpriteManager spriteManager, boolean boolean1) {
		instance = this;
		this.sprMngr = spriteManager;
		this.debug = boolean1;
		this.start();
	}

	public ErosionConfig getConfig() {
		return this.cfg;
	}

	public ErosionSeason getSeasons() {
		return this.Season;
	}

	public int getEtick() {
		return this.eTicks;
	}

	public IsoSpriteManager getSpriteManager() {
		return this.sprMngr;
	}

	public void mainTimer() {
		if (GameClient.bClient) {
			if (Core.bDebug) {
				this.cfg.writeFile(this.cfgPath);
			}
		} else {
			int int1 = SandboxOptions.instance.ErosionDays.getValue();
			if (this.debug) {
				++this.eTicks;
			} else if (int1 < 0) {
				this.eTicks = 0;
			} else if (int1 > 0) {
				++this.ticks;
				this.eTicks = (int)((float)this.ticks / 144.0F / (float)int1 * 100.0F);
			} else {
				++this.ticks;
				if (this.ticks >= this.tickUnit) {
					this.ticks = 0;
					++this.eTicks;
				}
			}

			if (this.eTicks < 0) {
				this.eTicks = Integer.MAX_VALUE;
			}

			GameTime gameTime = GameTime.getInstance();
			if (gameTime.getDay() != this.day || gameTime.getMonth() != this.month || gameTime.getYear() != this.year) {
				this.month = gameTime.getMonth();
				this.year = gameTime.getYear();
				this.day = gameTime.getDay();
				++this.epoch;
				this.Season.setDay(this.day, this.month, this.year);
				this.snowCheck();
			}

			if (GameServer.bServer) {
				for (int int2 = 0; int2 < ServerMap.instance.LoadedCells.size(); ++int2) {
					ServerMap.ServerCell serverCell = (ServerMap.ServerCell)ServerMap.instance.LoadedCells.get(int2);
					if (serverCell.bLoaded) {
						for (int int3 = 0; int3 < 7; ++int3) {
							for (int int4 = 0; int4 < 7; ++int4) {
								IsoChunk chunk = serverCell.chunks[int4][int3];
								if (chunk != null) {
									ErosionData.Chunk chunk2 = chunk.getErosionData();
									if (chunk2.eTickStamp != this.eTicks || chunk2.epoch != this.epoch) {
										for (int int5 = 0; int5 < 10; ++int5) {
											for (int int6 = 0; int6 < 10; ++int6) {
												IsoGridSquare square = chunk.getGridSquare(int6, int5, 0);
												if (square != null) {
													this.loadGridsquare(square);
												}
											}
										}

										chunk2.eTickStamp = this.eTicks;
										chunk2.epoch = this.epoch;
									}
								}
							}
						}
					}
				}
			}

			this.cfg.time.ticks = this.ticks;
			this.cfg.time.eticks = this.eTicks;
			this.cfg.time.epoch = this.epoch;
			this.cfg.writeFile(this.cfgPath);
		}
	}

	public void snowCheck() {
	}

	public int getSnowFraction() {
		return this.snowFrac;
	}

	public int getSnowFractionYesterday() {
		return this.snowFracYesterday;
	}

	public boolean isSnow() {
		return this.isSnow;
	}

	public void sendState(ByteBuffer byteBuffer) {
		if (GameServer.bServer) {
			byteBuffer.putInt(this.eTicks);
			byteBuffer.putInt(this.ticks);
			byteBuffer.putInt(this.epoch);
			byteBuffer.put((byte)this.getSnowFraction());
			byteBuffer.put((byte)this.getSnowFractionYesterday());
			byteBuffer.putFloat(GameTime.getInstance().getTimeOfDay());
		}
	}

	public void receiveState(ByteBuffer byteBuffer) {
		if (GameClient.bClient) {
			int int1 = this.eTicks;
			int int2 = this.epoch;
			this.eTicks = byteBuffer.getInt();
			this.ticks = byteBuffer.getInt();
			this.epoch = byteBuffer.getInt();
			this.cfg.time.ticks = this.ticks;
			this.cfg.time.eticks = this.eTicks;
			this.cfg.time.epoch = this.epoch;
			byte byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			float float1 = byteBuffer.getFloat();
			GameTime gameTime = GameTime.getInstance();
			if (gameTime.getDay() != this.day || gameTime.getMonth() != this.month || gameTime.getYear() != this.year) {
				this.month = gameTime.getMonth();
				this.year = gameTime.getYear();
				this.day = gameTime.getDay();
				this.Season.setDay(this.day, this.month, this.year);
			}

			if (int1 != this.eTicks || int2 != this.epoch) {
				this.updateMapNow();
			}
		}
	}

	private void loadGridsquare(IsoGridSquare square) {
		if (square != null && square.chunk != null && square.getZ() == 0) {
			this.getChunk(square);
			ErosionData.Square square2 = square.getErosionData();
			if (!square2.init) {
				this.initGridSquare(square, square2);
				this.World.validateSpawn(square, square2, this.chunkModData);
			}

			if (square2.doNothing) {
				return;
			}

			if (this.chunkModData.eTickStamp >= this.eTicks && this.chunkModData.epoch == this.epoch) {
				return;
			}

			this.World.update(square, square2, this.chunkModData, this.eTicks);
		}
	}

	private void initGridSquare(IsoGridSquare square, ErosionData.Square square2) {
		int int1 = square.getX();
		int int2 = square.getY();
		square2.noiseMain = this.noiseMain.layeredNoise((float)int1 / 10.0F, (float)int2 / 10.0F);
		square2.noiseMainInt = (int)Math.floor((double)(square2.noiseMain * 100.0F));
		square2.noiseKudzu = this.noiseKudzu.layeredNoise((float)int1 / 10.0F, (float)int2 / 10.0F);
		square2.soil = this.chunkModData.soil;
		square2.magicNum = (float)square2.rand(int1, int2, 100) / 100.0F;
		square2.regions.clear();
		square2.init = true;
	}

	private void getChunk(IsoGridSquare square) {
		this.chunk = square.getChunk();
		this.chunkModData = this.chunk.getErosionData();
		if (!this.chunkModData.init) {
			this.initChunk(this.chunk, this.chunkModData);
		}
	}

	private void initChunk(IsoChunk chunk, ErosionData.Chunk chunk2) {
		chunk2.set(chunk);
		float float1 = (float)chunk2.x / 5.0F;
		float float2 = (float)chunk2.y / 5.0F;
		float float3 = this.noiseMoisture.layeredNoise(float1, float2);
		float float4 = this.noiseMinerals.layeredNoise(float1, float2);
		int int1 = float3 < 1.0F ? (int)Math.floor((double)(float3 * 10.0F)) : 9;
		int int2 = float4 < 1.0F ? (int)Math.floor((double)(float4 * 10.0F)) : 9;
		chunk2.init = true;
		chunk2.eTickStamp = -1;
		chunk2.epoch = -1;
		chunk2.moisture = float3;
		chunk2.minerals = float4;
		chunk2.soil = soilTable[int1][int2] - 1;
	}

	private boolean initConfig() {
		String string = "erosion.ini";
		if (GameClient.bClient) {
			this.cfg = GameClient.instance.erosionConfig;
			assert this.cfg != null;
			GameClient.instance.erosionConfig = null;
			this.cfgPath = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + string;
			return true;
		} else {
			this.cfg = new ErosionConfig();
			this.cfgPath = GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + string;
			File file = new File(this.cfgPath);
			if (file.exists()) {
				DebugLog.log("erosion: reading " + file.getAbsolutePath());
				if (this.cfg.readFile(file.getAbsolutePath())) {
					return true;
				}

				this.cfg = new ErosionConfig();
			}

			file = new File(GameWindow.getCacheDir() + File.separator + string);
			if (!file.exists() && !Core.getInstance().isNoSave()) {
				File file2 = new File("media" + File.separator + "data" + File.separator + string);
				if (file2.exists()) {
					try {
						DebugLog.log("erosion: copying " + file2.getAbsolutePath() + " to " + file.getAbsolutePath());
						Files.copy(file2.toPath(), file.toPath());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}

			if (file.exists()) {
				DebugLog.log("erosion: reading " + file.getAbsolutePath());
				if (!this.cfg.readFile(file.getAbsolutePath())) {
					this.cfg = new ErosionConfig();
				}
			}

			int int1 = SandboxOptions.instance.getErosionSpeed();
			ErosionConfig.Time time;
			switch (int1) {
			case 1: 
				time = this.cfg.time;
				time.tickunit /= 5;
				break;
			
			case 2: 
				time = this.cfg.time;
				time.tickunit /= 2;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				time = this.cfg.time;
				time.tickunit *= 2;
				break;
			
			case 5: 
				time = this.cfg.time;
				time.tickunit *= 5;
			
			}

			float float1 = (float)(this.cfg.time.tickunit * 100) / 144.0F;
			float float2 = (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
			this.cfg.time.eticks = (int)Math.floor((double)(Math.min(1.0F, float2 / float1) * 100.0F));
			int int2 = SandboxOptions.instance.ErosionDays.getValue();
			if (int2 > 0) {
				this.cfg.time.tickunit = 144;
				this.cfg.time.eticks = (int)Math.floor((double)(Math.min(1.0F, float2 / (float)int2) * 100.0F));
			}

			return true;
		}
	}

	public void start() {
		if (this.initConfig()) {
			this.world = Core.GameSaveWorld;
			this.tickUnit = this.cfg.time.tickunit;
			this.ticks = this.cfg.time.ticks;
			this.eTicks = this.cfg.time.eticks;
			this.month = GameTime.getInstance().getMonth();
			this.year = GameTime.getInstance().getYear();
			this.day = GameTime.getInstance().getDay();
			this.debug = !GameServer.bServer && this.cfg.debug.enabled;
			this.cfg.consolePrint();
			this.noiseMain = new Noise2D();
			this.noiseMain.addLayer(this.cfg.seeds.seedMain_0, 0.5F, 3.0F);
			this.noiseMain.addLayer(this.cfg.seeds.seedMain_1, 2.0F, 5.0F);
			this.noiseMain.addLayer(this.cfg.seeds.seedMain_2, 5.0F, 8.0F);
			this.noiseMoisture = new Noise2D();
			this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_0, 2.0F, 3.0F);
			this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_1, 1.6F, 5.0F);
			this.noiseMoisture.addLayer(this.cfg.seeds.seedMoisture_2, 0.6F, 8.0F);
			this.noiseMinerals = new Noise2D();
			this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_0, 2.0F, 3.0F);
			this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_1, 1.6F, 5.0F);
			this.noiseMinerals.addLayer(this.cfg.seeds.seedMinerals_2, 0.6F, 8.0F);
			this.noiseKudzu = new Noise2D();
			this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_0, 6.0F, 3.0F);
			this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_1, 3.0F, 5.0F);
			this.noiseKudzu.addLayer(this.cfg.seeds.seedKudzu_2, 0.5F, 8.0F);
			this.Season = new ErosionSeason();
			ErosionConfig.Season season = this.cfg.season;
			int int1 = season.tempMin;
			int int2 = season.tempMax;
			if (SandboxOptions.instance.getTemperatureModifier() == 1) {
				int1 -= 10;
				int2 -= 10;
			} else if (SandboxOptions.instance.getTemperatureModifier() == 2) {
				int1 -= 5;
				int2 -= 5;
			} else if (SandboxOptions.instance.getTemperatureModifier() == 4) {
				int1 = (int)((double)int1 + 7.5);
				int2 += 4;
			} else if (SandboxOptions.instance.getTemperatureModifier() == 5) {
				int1 += 15;
				int2 += 8;
			}

			this.Season.init(season.lat, int2, int1, season.tempDiff, season.seasonLag, season.noon, season.seedA, season.seedB, season.seedC);
			this.Season.setRain(season.jan, season.feb, season.mar, season.apr, season.may, season.jun, season.jul, season.aug, season.sep, season.oct, season.nov, season.dec);
			this.Season.setDay(this.day, this.month, this.year);
			LuaEventManager.triggerEvent("OnInitSeasons", this.Season);
			this.IceQueen = new ErosionIceQueen(this.sprMngr);
			this.World = new ErosionWorld();
			if (this.World.init()) {
				this.snowCheck();
				if (this.debug) {
				}

				if (GameServer.bServer) {
				}
			}
		}
	}

	private void loadChunk(IsoChunk chunk) {
		ErosionData.Chunk chunk2 = chunk.getErosionData();
		if (!chunk2.init) {
			this.initChunk(chunk, chunk2);
		}

		chunk2.eTickStamp = this.eTicks;
		chunk2.epoch = this.epoch;
	}

	public void DebugUpdateMapNow() {
		this.updateMapNow();
	}

	private void updateMapNow() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(int1);
			if (!chunkMap.ignore) {
				IsoChunkMap.bSettingChunk.lock();
				try {
					for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; ++int2) {
						for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
							IsoChunk chunk = chunkMap.getChunk(int3, int2);
							if (chunk != null) {
								ErosionData.Chunk chunk2 = chunk.getErosionData();
								if (chunk2.eTickStamp != this.eTicks || chunk2.epoch != this.epoch) {
									for (int int4 = 0; int4 < 10; ++int4) {
										for (int int5 = 0; int5 < 10; ++int5) {
											IsoGridSquare square = chunk.getGridSquare(int5, int4, 0);
											if (square != null) {
												this.loadGridsquare(square);
											}
										}
									}

									chunk2.eTickStamp = this.eTicks;
									chunk2.epoch = this.epoch;
								}
							}
						}
					}
				} finally {
					IsoChunkMap.bSettingChunk.unlock();
				}
			}
		}
	}

	public static void LoadGridsquare(IsoGridSquare square) {
		instance.loadGridsquare(square);
	}

	public static void ChunkLoaded(IsoChunk chunk) {
		instance.loadChunk(chunk);
	}

	public static void EveryTenMinutes() {
		instance.mainTimer();
	}

	public static void Reset() {
		instance = null;
	}
}
