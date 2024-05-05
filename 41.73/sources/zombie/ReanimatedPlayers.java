package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.logger.ExceptionLogger;
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


public final class ReanimatedPlayers {
	public static ReanimatedPlayers instance = new ReanimatedPlayers();
	private final ArrayList Zombies = new ArrayList();

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
			if (!GameServer.bServer) {
				zombie.setSceneCulled(true);
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
				if (!GameClient.bClient) {
					if (!this.Zombies.contains(zombie)) {
						this.Zombies.add(zombie);
						noise("added to Zombies " + zombie);
						zombie.setStateMachineLocked(false);
						zombie.changeState(ZombieIdleState.instance());
					}
				}
			}
		}
	}

	public void saveReanimatedPlayers() {
		if (!GameClient.bClient) {
			ArrayList arrayList = new ArrayList();
			try {
				ByteBuffer byteBuffer = SliceY.SliceBuffer;
				byteBuffer.clear();
				byteBuffer.putInt(194);
				arrayList.addAll(this.Zombies);
				ArrayList arrayList2 = IsoWorld.instance.CurrentCell.getZombieList();
				Iterator iterator = arrayList2.iterator();
				while (true) {
					IsoZombie zombie;
					if (!iterator.hasNext()) {
						byteBuffer.putInt(arrayList.size());
						iterator = arrayList.iterator();
						while (iterator.hasNext()) {
							zombie = (IsoZombie)iterator.next();
							zombie.save(byteBuffer);
						}

						File file = ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin");
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
						bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
						bufferedOutputStream.flush();
						bufferedOutputStream.close();
						break;
					}

					zombie = (IsoZombie)iterator.next();
					if (zombie.isReanimatedPlayer() && !zombie.isDead() && !arrayList.contains(zombie)) {
						arrayList.add(zombie);
					}
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return;
			}

			noise("saved " + arrayList.size() + " zombies");
		}
	}

	public void loadReanimatedPlayers() {
		if (!GameClient.bClient) {
			this.Zombies.clear();
			File file = ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				try {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
					try {
						synchronized (SliceY.SliceBufferLock) {
							ByteBuffer byteBuffer = SliceY.SliceBuffer;
							byteBuffer.clear();
							int int1 = bufferedInputStream.read(byteBuffer.array());
							byteBuffer.limit(int1);
							this.loadReanimatedPlayers(byteBuffer);
						}
					} catch (Throwable throwable) {
						try {
							bufferedInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedInputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileInputStream.close();
			} catch (FileNotFoundException fileNotFoundException) {
				return;
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return;
			}

			noise("loaded " + this.Zombies.size() + " zombies");
		}
	}

	private void loadReanimatedPlayers(ByteBuffer byteBuffer) throws IOException, RuntimeException {
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
	}
}
