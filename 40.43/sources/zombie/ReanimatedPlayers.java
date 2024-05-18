package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.ai.states.ZombieStandState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelManager;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public class ReanimatedPlayers {
	public static ReanimatedPlayers instance = new ReanimatedPlayers();
	private ArrayList Zombies = new ArrayList();

	private static void noise(String string) {
		DebugLog.log("reanimate: " + string);
	}

	public void addReanimatedPlayersToChunk(IsoChunk chunk) {
		int int1 = chunk.wx * 10;
		int int2 = chunk.wy * 10;
		int int3 = int1 + 10;
		int int4 = int2 + 10;
		for (int int5 = 0; int5 < this.Zombies.size(); ++int5) {
			IsoZombie zombie = (IsoZombie)this.Zombies.get(int5);
			if (zombie.getX() >= (float)int1 && zombie.getX() < (float)int3 && zombie.getY() >= (float)int2 && zombie.getY() < (float)int4) {
				IsoGridSquare square = chunk.getGridSquare((int)zombie.getX() - int1, (int)zombie.getY() - int2, (int)zombie.getZ());
				if (square != null) {
					if (GameServer.bServer) {
						if (zombie.OnlineID != -1) {
							noise("ERROR? OnlineID != -1 for reanimated player zombie");
						}

						zombie.OnlineID = ServerMap.instance.getUniqueZombieId();
						if (zombie.OnlineID == -1) {
							continue;
						}

						ServerMap.instance.ZombieMap.put(zombie.OnlineID, zombie);
					}

					zombie.setCurrent(square);
					assert !IsoWorld.instance.CurrentCell.getObjectList().contains(zombie);
					assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie);
					IsoWorld.instance.CurrentCell.getObjectList().add(zombie);
					IsoWorld.instance.CurrentCell.getZombieList().add(zombie);
					this.Zombies.remove(int5);
					--int5;
					SharedDescriptors.createPlayerZombieDescriptor(zombie);
					noise("added to world " + zombie);
				}
			}
		}
	}

	public void removeReanimatedPlayerFromWorld(IsoZombie zombie) {
		if (zombie.isReanimatedPlayer()) {
			if (!GameServer.bServer && ModelManager.instance.Contains.contains(zombie) && !ModelManager.instance.ToRemove.contains(zombie)) {
				ModelManager.instance.Remove((IsoGameCharacter)zombie);
			}

			if (zombie.isOnFire()) {
				IsoFireManager.RemoveBurningCharacter(zombie);
				zombie.setOnFire(false);
			}

			if (zombie.AttachedAnimSprite != null) {
				ArrayList arrayList = zombie.AttachedAnimSprite;
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoSpriteInstance spriteInstance = (IsoSpriteInstance)arrayList.get(int1);
					IsoSpriteInstance.add(spriteInstance);
				}

				zombie.AttachedAnimSprite.clear();
			}

			if (zombie.AttachedAnimSpriteActual != null) {
				zombie.AttachedAnimSpriteActual.clear();
			}

			if (!GameServer.bServer) {
				for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					IsoPlayer player = IsoPlayer.players[int2];
					if (player != null && player.ReanimatedCorpse == zombie) {
						player.ReanimatedCorpse = null;
						player.ReanimatedCorpseID = -1;
					}
				}
			}

			if (GameServer.bServer && zombie.OnlineID != -1) {
				ServerMap.instance.ZombieMap.remove(zombie.OnlineID);
				zombie.OnlineID = -1;
			}

			SharedDescriptors.releasePlayerZombieDescriptor(zombie);
			assert !VirtualZombieManager.instance.isReused(zombie);
			if (!zombie.isDead()) {
				if (!this.Zombies.contains(zombie)) {
					this.Zombies.add(zombie);
					noise("added to Zombies " + zombie);
					zombie.getStateMachine().Lock = false;
					zombie.getStateMachine().setCurrent(ZombieStandState.instance());
				}
			}
		}
	}

	public void saveReanimatedPlayers() {
		if (!GameClient.bClient) {
			if (SliceY.SliceBuffer == null) {
				SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
			}

			ByteBuffer byteBuffer = SliceY.SliceBuffer;
			ArrayList arrayList = new ArrayList();
			try {
				byteBuffer.rewind();
				byteBuffer.putInt(143);
				arrayList.addAll(this.Zombies);
				int int1;
				for (int1 = 0; int1 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++int1) {
					IsoZombie zombie = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(int1);
					if (zombie.isReanimatedPlayer() && !zombie.isDead() && !arrayList.contains(zombie)) {
						arrayList.add(zombie);
					}
				}

				byteBuffer.putInt(arrayList.size());
				for (int1 = 0; int1 < arrayList.size(); ++int1) {
					((IsoZombie)arrayList.get(int1)).save(byteBuffer);
				}

				File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "reanimated.bin");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
				return;
			}

			noise("saved " + arrayList.size() + " zombies");
		}
	}

	public void loadReanimatedPlayers() {
		if (!GameClient.bClient) {
			this.Zombies.clear();
			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "reanimated.bin");
			if (file.exists()) {
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				ByteBuffer byteBuffer = SliceY.SliceBuffer;
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
					bufferedInputStream.read(byteBuffer.array());
					bufferedInputStream.close();
					byteBuffer.rewind();
					int int1 = byteBuffer.getInt();
					int int2 = byteBuffer.getInt();
					for (int int3 = 0; int3 < int2; ++int3) {
						IsoObject object = IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, byteBuffer);
						if (!(object instanceof IsoZombie)) {
							throw new RuntimeException("expected IsoZombie here");
						}

						IsoZombie zombie = (IsoZombie)object;
						zombie.load(byteBuffer, int1);
						zombie.getDescriptor().setID(0);
						zombie.setReanimatedPlayer(true);
						IsoWorld.instance.CurrentCell.getAddList().remove(zombie);
						IsoWorld.instance.CurrentCell.getObjectList().remove(zombie);
						IsoWorld.instance.CurrentCell.getZombieList().remove(zombie);
						this.Zombies.add(zombie);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					return;
				}

				noise("loaded " + this.Zombies.size() + " zombies");
			}
		}
	}
}
