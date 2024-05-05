package zombie.util;

import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.WorldSimulation;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.popman.ZombiePopulationManager;
import zombie.ui.UIManager;


public final class AddCoopPlayer {
	private AddCoopPlayer.Stage stage;
	private IsoPlayer player;

	public AddCoopPlayer(IsoPlayer player) {
		this.stage = AddCoopPlayer.Stage.Init;
		this.player = player;
	}

	public void update() {
		IsoCell cell;
		int int1;
		ByteBufferWriter byteBufferWriter;
		switch (this.stage) {
		case Init: 
			if (GameClient.bClient) {
				byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putByte((byte)this.player.PlayerIndex);
				byteBufferWriter.putUTF(this.player.username != null ? this.player.username : "");
				byteBufferWriter.putFloat(this.player.x);
				byteBufferWriter.putFloat(this.player.y);
				PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
				this.stage = AddCoopPlayer.Stage.ReceiveClientConnect;
			} else {
				this.stage = AddCoopPlayer.Stage.StartMapLoading;
			}

		
		case ReceiveClientConnect: 
		
		case ReceivePlayerConnect: 
		
		case Finished: 
		
		default: 
			break;
		
		case StartMapLoading: 
			cell = IsoWorld.instance.CurrentCell;
			int int2 = this.player.PlayerIndex;
			IsoChunkMap chunkMap = cell.ChunkMap[int2];
			IsoChunkMap.bSettingChunk.lock();
			try {
				chunkMap.Unload();
				chunkMap.ignore = false;
				int1 = (int)(this.player.x / 10.0F);
				int int3 = (int)(this.player.y / 10.0F);
				try {
					if (LightingJNI.init) {
						LightingJNI.teleport(int2, int1 - IsoChunkMap.ChunkGridWidth / 2, int3 - IsoChunkMap.ChunkGridWidth / 2);
					}
				} catch (Exception exception) {
				}

				if (!GameServer.bServer && !GameClient.bClient) {
					ZombiePopulationManager.instance.playerSpawnedAt((int)this.player.x, (int)this.player.y, (int)this.player.z);
				}

				chunkMap.WorldX = int1;
				chunkMap.WorldY = int3;
				WorldSimulation.instance.activateChunkMap(int2);
				int int4 = int1 - IsoChunkMap.ChunkGridWidth / 2;
				int int5 = int3 - IsoChunkMap.ChunkGridWidth / 2;
				int int6 = int1 + IsoChunkMap.ChunkGridWidth / 2 + 1;
				int int7 = int3 + IsoChunkMap.ChunkGridWidth / 2 + 1;
				int int8 = int4;
				while (true) {
					if (int8 >= int6) {
						chunkMap.SwapChunkBuffers();
						break;
					}

					for (int int9 = int5; int9 < int7; ++int9) {
						if (IsoWorld.instance.getMetaGrid().isValidChunk(int8, int9)) {
							IsoChunk chunk = chunkMap.LoadChunkForLater(int8, int9, int8 - int4, int9 - int5);
							if (chunk != null && chunk.bLoaded) {
								cell.setCacheChunk(chunk, int2);
							}
						}
					}

					++int8;
				}
			} finally {
				IsoChunkMap.bSettingChunk.unlock();
			}

			this.stage = AddCoopPlayer.Stage.CheckMapLoading;
			break;
		
		case CheckMapLoading: 
			cell = IsoWorld.instance.CurrentCell;
			IsoChunkMap chunkMap2 = cell.ChunkMap[this.player.PlayerIndex];
			chunkMap2.update();
			for (int int10 = 0; int10 < IsoChunkMap.ChunkGridWidth; ++int10) {
				for (int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; ++int1) {
					if (IsoWorld.instance.getMetaGrid().isValidChunk(chunkMap2.getWorldXMin() + int1, chunkMap2.getWorldYMin() + int10) && chunkMap2.getChunk(int1, int10) == null) {
						return;
					}
				}
			}

			IsoGridSquare square = cell.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z);
			if (square != null && square.getRoom() != null) {
				square.getRoom().def.setExplored(true);
				square.getRoom().building.setAllExplored(true);
			}

			this.stage = GameClient.bClient ? AddCoopPlayer.Stage.SendPlayerConnect : AddCoopPlayer.Stage.AddToWorld;
			break;
		
		case SendPlayerConnect: 
			byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)2);
			byteBufferWriter.putByte((byte)this.player.PlayerIndex);
			GameClient.instance.writePlayerConnectData(byteBufferWriter, this.player);
			PacketTypes.PacketType.AddCoopPlayer.send(GameClient.connection);
			this.stage = AddCoopPlayer.Stage.ReceivePlayerConnect;
			break;
		
		case AddToWorld: 
			IsoPlayer.players[this.player.PlayerIndex] = this.player;
			LosUtil.cachecleared[this.player.PlayerIndex] = true;
			this.player.updateLightInfo();
			cell = IsoWorld.instance.CurrentCell;
			this.player.setCurrent(cell.getGridSquare((int)this.player.x, (int)this.player.y, (int)this.player.z));
			this.player.updateUsername();
			this.player.setSceneCulled(false);
			if (cell.isSafeToAdd()) {
				cell.getObjectList().add(this.player);
			} else {
				cell.getAddList().add(this.player);
			}

			this.player.getInventory().addItemsToProcessItems();
			LuaEventManager.triggerEvent("OnCreatePlayer", this.player.PlayerIndex, this.player);
			if (this.player.isAsleep()) {
				UIManager.setFadeBeforeUI(this.player.PlayerIndex, true);
				UIManager.FadeOut((double)this.player.PlayerIndex, 2.0);
				UIManager.setFadeTime((double)this.player.PlayerIndex, 0.0);
			}

			this.stage = AddCoopPlayer.Stage.Finished;
			SoundManager.instance.stopMusic(IsoPlayer.DEATH_MUSIC_NAME);
		
		}
	}

	public boolean isFinished() {
		return this.stage == AddCoopPlayer.Stage.Finished;
	}

	public void accessGranted(int int1) {
		if (this.player.PlayerIndex == int1) {
			DebugLog.log("coop player=" + (int1 + 1) + "/4 access granted");
			this.stage = AddCoopPlayer.Stage.StartMapLoading;
		}
	}

	public void accessDenied(int int1, String string) {
		if (this.player.PlayerIndex == int1) {
			DebugLog.log("coop player=" + (int1 + 1) + "/4 access denied: " + string);
			IsoCell cell = IsoWorld.instance.CurrentCell;
			int int2 = this.player.PlayerIndex;
			IsoChunkMap chunkMap = cell.ChunkMap[int2];
			chunkMap.Unload();
			chunkMap.ignore = true;
			this.stage = AddCoopPlayer.Stage.Finished;
			LuaEventManager.triggerEvent("OnCoopJoinFailed", int1);
		}
	}

	public void receivePlayerConnect(int int1) {
		if (this.player.PlayerIndex == int1) {
			this.stage = AddCoopPlayer.Stage.AddToWorld;
			this.update();
		}
	}

	public boolean isLoadingThisSquare(int int1, int int2) {
		int int3 = (int)(this.player.x / 10.0F);
		int int4 = (int)(this.player.y / 10.0F);
		int int5 = int3 - IsoChunkMap.ChunkGridWidth / 2;
		int int6 = int4 - IsoChunkMap.ChunkGridWidth / 2;
		int int7 = int5 + IsoChunkMap.ChunkGridWidth;
		int int8 = int6 + IsoChunkMap.ChunkGridWidth;
		int1 /= 10;
		int2 /= 10;
		return int1 >= int5 && int1 < int7 && int2 >= int6 && int2 < int8;
	}

	public static enum Stage {

		Init,
		ReceiveClientConnect,
		StartMapLoading,
		CheckMapLoading,
		SendPlayerConnect,
		ReceivePlayerConnect,
		AddToWorld,
		Finished;

		private static AddCoopPlayer.Stage[] $values() {
			return new AddCoopPlayer.Stage[]{Init, ReceiveClientConnect, StartMapLoading, CheckMapLoading, SendPlayerConnect, ReceivePlayerConnect, AddToWorld, Finished};
		}
	}
}
