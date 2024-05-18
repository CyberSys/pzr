package zombie.iso;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionRegions;
import zombie.erosion.season.ErosionIceQueen;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.IngameState;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.CoopSlave;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.vehicles.VehicleManager;


public class WorldConverter {
	public static WorldConverter instance = new WorldConverter();
	public static boolean converting;
	int oldID = 0;
	public HashMap TilesetConversions = null;
	IsoSpriteManager sprManager;

	public void convert(String string, IsoSpriteManager spriteManager) throws IOException {
		this.sprManager = spriteManager;
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + "map_ver.bin");
		if (file.exists()) {
			converting = true;
			FileInputStream fileInputStream = new FileInputStream(file);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			int int1 = dataInputStream.readInt();
			dataInputStream.close();
			if (int1 < 143) {
				if (int1 < 24) {
					GameLoadingState.build23Stop = true;
					return;
				}

				try {
					this.convert(string, int1, 143);
				} catch (Exception exception) {
					IngameState.createWorld(string);
					IngameState.copyWorld(string + "_backup", string);
					exception.printStackTrace();
				}
			}

			converting = false;
		}
	}

	private void convert(String string, int int1, int int2) {
		if (!GameClient.bClient) {
			GameLoadingState.convertingWorld = true;
			String string2 = Core.GameSaveWorld;
			IngameState.createWorld(string + "_backup");
			IngameState.copyWorld(string, Core.GameSaveWorld);
			Core.GameSaveWorld = string2;
			if (int2 >= 14 && int1 < 14) {
				try {
					this.convertchunks(string, 25, 25);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			} else if (int1 == 7) {
				try {
					this.convertchunks(string);
				} catch (IOException ioException2) {
					ioException2.printStackTrace();
				}
			}

			if (int1 <= 4) {
				this.loadconversionmap(int1, "tiledefinitions");
				this.loadconversionmap(int1, "newtiledefinitions");
				try {
					this.convertchunks(string);
				} catch (IOException ioException3) {
					ioException3.printStackTrace();
				}
			}

			GameLoadingState.convertingWorld = false;
		}
	}

	private void convertchunks(String string) throws IOException {
		IsoCell cell = new IsoCell(this.sprManager, 300, 300);
		IsoChunkMap chunkMap = new IsoChunkMap(cell);
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}

		String[] stringArray = file.list();
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			if (string2.contains(".bin") && !string2.equals("map.bin") && !string2.equals("map_p.bin") && !string2.matches("map_p[0-9]+\\.bin") && !string2.equals("map_t.bin") && !string2.equals("map_c.bin") && !string2.equals("map_ver.bin") && !string2.equals("map_sand.bin") && !string2.equals("map_mov.bin") && !string2.equals("map_meta.bin") && !string2.equals("map_cm.bin") && !string2.equals("pc.bin") && !string2.startsWith("zpop_") && !string2.startsWith("chunkdata_")) {
				String[] stringArray3 = string2.replace(".bin", "").replace("map_", "").split("_");
				int int3 = Integer.parseInt(stringArray3[0]);
				int int4 = Integer.parseInt(stringArray3[1]);
				chunkMap.LoadChunkForLater(int3, int4, 0, 0);
				chunkMap.SwapChunkBuffers();
				chunkMap.getChunk(0, 0).Save(true);
			}
		}
	}

	private void convertchunks(String string, int int1, int int2) throws IOException {
		IsoCell cell = new IsoCell(this.sprManager, 300, 300);
		new IsoChunkMap(cell);
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}

		String[] stringArray = file.list();
		IsoWorld.saveoffsetx = int1;
		IsoWorld.saveoffsety = int2;
		IsoWorld.instance.MetaGrid.Create();
		WorldStreamer.instance.create();
		String[] stringArray2 = stringArray;
		int int3 = stringArray.length;
		for (int int4 = 0; int4 < int3; ++int4) {
			String string2 = stringArray2[int4];
			if (string2.contains(".bin") && !string2.equals("map.bin") && !string2.equals("map_p.bin") && !string2.matches("map_p[0-9]+\\.bin") && !string2.equals("map_t.bin") && !string2.equals("map_c.bin") && !string2.equals("map_ver.bin") && !string2.equals("map_sand.bin") && !string2.equals("map_mov.bin") && !string2.equals("map_meta.bin") && !string2.equals("map_cm.bin") && !string2.equals("pc.bin") && !string2.startsWith("zpop_") && !string2.startsWith("chunkdata_")) {
				String[] stringArray3 = string2.replace(".bin", "").replace("map_", "").split("_");
				int int5 = Integer.parseInt(stringArray3[0]);
				int int6 = Integer.parseInt(stringArray3[1]);
				IsoChunk chunk = new IsoChunk(cell);
				chunk.refs.add(cell.ChunkMap[0]);
				WorldStreamer.instance.addJobConvert(chunk, 0, 0, int5, int6);
				while (!chunk.bLoaded) {
					try {
						Thread.sleep(20L);
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}

				chunk.wx += int1 * 30;
				chunk.wy += int2 * 30;
				chunk.jobType = IsoChunk.JobType.Convert;
				chunk.Save(true);
				File file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + string2);
				while (!ChunkSaveWorker.instance.toSaveQueue.isEmpty()) {
					try {
						Thread.sleep(13L);
					} catch (InterruptedException interruptedException2) {
						interruptedException2.printStackTrace();
					}
				}

				file2.delete();
			}
		}
	}

	private void loadconversionmap(int int1, String string) {
		String string2 = "media/" + string + "_" + int1 + ".tiles";
		File file = new File(string2);
		if (file.exists()) {
			try {
				RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
				int int2 = IsoWorld.readInt(randomAccessFile);
				for (int int3 = 0; int3 < int2; ++int3) {
					Thread.sleep(4L);
					String string3 = IsoWorld.readString(randomAccessFile);
					String string4 = string3.trim();
					IsoWorld.readString(randomAccessFile);
					int int4 = IsoWorld.readInt(randomAccessFile);
					int int5 = IsoWorld.readInt(randomAccessFile);
					int int6 = IsoWorld.readInt(randomAccessFile);
					for (int int7 = 0; int7 < int6; ++int7) {
						IsoSprite sprite = (IsoSprite)this.sprManager.NamedMap.get(string4 + "_" + int7);
						if (this.TilesetConversions == null) {
							this.TilesetConversions = new HashMap();
						}

						this.TilesetConversions.put(this.oldID, sprite.ID);
						++this.oldID;
						int int8 = IsoWorld.readInt(randomAccessFile);
						for (int int9 = 0; int9 < int8; ++int9) {
							string3 = IsoWorld.readString(randomAccessFile);
							String string5 = string3.trim();
							string3 = IsoWorld.readString(randomAccessFile);
							String string6 = string3.trim();
						}
					}
				}
			} catch (Exception exception) {
			}
		}
	}

	public void softreset(IsoSpriteManager spriteManager) {
		this.sprManager = spriteManager;
		String string = GameServer.ServerName;
		Core.GameSaveWorld = string;
		IsoCell cell = new IsoCell(this.sprManager, 300, 300);
		new IsoChunkMap(cell);
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}

		String[] stringArray = file.list();
		if (CoopSlave.instance != null) {
			CoopSlave.instance.sendMessage("softreset-count", (String)null, Integer.toString(stringArray.length));
		}

		IsoWorld.instance.MetaGrid.Create();
		ServerMap.instance.init(IsoWorld.instance.MetaGrid);
		new ErosionIceQueen(this.sprManager);
		ErosionRegions.init();
		WorldStreamer.instance.create();
		VehicleManager.instance = new VehicleManager();
		int int1 = stringArray.length;
		DebugLog.log("processing " + int1 + " files");
		String[] stringArray2 = stringArray;
		int int2 = stringArray.length;
		for (int int3 = 0; int3 < int2; ++int3) {
			String string2 = stringArray2[int3];
			--int1;
			if (!string2.contains("descriptors") && string2.contains(".bin") && !string2.equals("map.bin") && !string2.equals("map_p.bin") && !string2.matches("map_p[0-9]+\\.bin") && !string2.equals("map_c.bin") && !string2.equals("map_ver.bin") && !string2.equals("map_sand.bin") && !string2.equals("map_mov.bin") && !string2.equals("map_cm.bin") && !string2.equals("pc.bin") && !string2.startsWith("chunkdata_") && !string2.startsWith("gos_")) {
				File file2;
				if (string2.startsWith("zpop_")) {
					file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + string2);
					file2.delete();
				} else if (string2.equals("map_t.bin")) {
					file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + string2);
					file2.delete();
				} else if (!string2.equals("map_meta.bin") && !string2.equals("map_zone.bin")) {
					if (string2.equals("reanimated.bin")) {
						file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + string2);
						file2.delete();
					} else {
						System.out.println("Soft clearing chunk: " + string2);
						String[] stringArray3 = string2.replace(".bin", "").replace("map_", "").split("_");
						int int4 = Integer.parseInt(stringArray3[0]);
						int int5 = Integer.parseInt(stringArray3[1]);
						IsoChunk chunk = new IsoChunk(cell);
						chunk.refs.add(cell.ChunkMap[0]);
						WorldStreamer.instance.addJobWipe(chunk, 0, 0, int4, int5);
						while (!chunk.bLoaded) {
							try {
								Thread.sleep(20L);
							} catch (InterruptedException interruptedException) {
								interruptedException.printStackTrace();
							}
						}

						chunk.jobType = IsoChunk.JobType.Convert;
						chunk.FloorBloodSplats.clear();
						try {
							chunk.Save(true);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						chunk.doReuseGridsquares();
						if (int1 % 100 == 0) {
							DebugLog.log(int1 + " files to go");
						}

						if (CoopSlave.instance != null && int1 % 10 == 0) {
							CoopSlave.instance.sendMessage("softreset-remaining", (String)null, Integer.toString(int1));
						}
					}
				} else {
					file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + string + File.separator + string2);
					file2.delete();
				}
			}
		}

		GameServer.ResetID = Rand.Next(10000000);
		ServerOptions.instance.putSaveOption("ResetID", (new Integer(GameServer.ResetID)).toString());
		IsoWorld.instance.CurrentCell = null;
		DebugLog.log("soft-reset complete, server terminated");
		if (CoopSlave.instance != null) {
			CoopSlave.instance.sendMessage("softreset-finished", (String)null, "");
		}

		SteamUtils.shutdown();
		System.exit(0);
	}
}
