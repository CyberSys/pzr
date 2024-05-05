package zombie.globalObjects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoObject;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.util.Type;


public final class SGlobalObjectSystem extends GlobalObjectSystem {
	private static KahluaTable tempTable;
	protected int loadedWorldVersion = -1;
	protected final HashSet modDataKeys = new HashSet();
	protected final HashSet objectModDataKeys = new HashSet();
	protected final HashSet objectSyncKeys = new HashSet();

	public SGlobalObjectSystem(String string) {
		super(string);
	}

	protected GlobalObject makeObject(int int1, int int2, int int3) {
		return new SGlobalObject(this, int1, int2, int3);
	}

	public void setModDataKeys(KahluaTable kahluaTable) {
		this.modDataKeys.clear();
		if (kahluaTable != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			while (kahluaTableIterator.advance()) {
				Object object = kahluaTableIterator.getValue();
				if (!(object instanceof String)) {
					throw new IllegalArgumentException("expected string but got \"" + object + "\"");
				}

				this.modDataKeys.add((String)object);
			}
		}
	}

	public void setObjectModDataKeys(KahluaTable kahluaTable) {
		this.objectModDataKeys.clear();
		if (kahluaTable != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			while (kahluaTableIterator.advance()) {
				Object object = kahluaTableIterator.getValue();
				if (!(object instanceof String)) {
					throw new IllegalArgumentException("expected string but got \"" + object + "\"");
				}

				this.objectModDataKeys.add((String)object);
			}
		}
	}

	public void setObjectSyncKeys(KahluaTable kahluaTable) {
		this.objectSyncKeys.clear();
		if (kahluaTable != null) {
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			while (kahluaTableIterator.advance()) {
				Object object = kahluaTableIterator.getValue();
				if (!(object instanceof String)) {
					throw new IllegalArgumentException("expected string but got \"" + object + "\"");
				}

				this.objectSyncKeys.add((String)object);
			}
		}
	}

	public void update() {
	}

	public void chunkLoaded(int int1, int int2) {
		if (this.hasObjectsInChunk(int1, int2)) {
			Object object = this.modData.rawget("OnChunkLoaded");
			if (object == null) {
				throw new IllegalStateException("OnChunkLoaded method undefined for system \'" + this.name + "\'");
			} else {
				Double Double1 = BoxedStaticValues.toDouble((double)int1);
				Double Double2 = BoxedStaticValues.toDouble((double)int2);
				LuaManager.caller.pcall(LuaManager.thread, object, this.modData, Double1, Double2);
			}
		}
	}

	public void sendCommand(String string, KahluaTable kahluaTable) {
		SGlobalObjectNetwork.sendServerCommand(this.name, string, kahluaTable);
	}

	public void receiveClientCommand(String string, IsoPlayer player, KahluaTable kahluaTable) {
		Object object = this.modData.rawget("OnClientCommand");
		if (object == null) {
			throw new IllegalStateException("OnClientCommand method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcall(LuaManager.thread, object, this.modData, string, player, kahluaTable);
		}
	}

	public void addGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
		if (sGlobalObject == null) {
			throw new IllegalArgumentException("globalObject is null");
		} else if (sGlobalObject.system != this) {
			throw new IllegalArgumentException("object not in this system");
		} else {
			SGlobalObjectNetwork.addGlobalObjectOnClient(sGlobalObject);
		}
	}

	public void removeGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
		if (sGlobalObject == null) {
			throw new IllegalArgumentException("globalObject is null");
		} else if (sGlobalObject.system != this) {
			throw new IllegalArgumentException("object not in this system");
		} else {
			SGlobalObjectNetwork.removeGlobalObjectOnClient(sGlobalObject);
		}
	}

	public void updateGlobalObjectOnClient(SGlobalObject sGlobalObject) throws IOException {
		if (sGlobalObject == null) {
			throw new IllegalArgumentException("globalObject is null");
		} else if (sGlobalObject.system != this) {
			throw new IllegalArgumentException("object not in this system");
		} else {
			SGlobalObjectNetwork.updateGlobalObjectOnClient(sGlobalObject);
		}
	}

	private String getFileName() {
		return ZomboidFileSystem.instance.getFileNameInCurrentSave("gos_" + this.name + ".bin");
	}

	public KahluaTable getInitialStateForClient() {
		Object object = this.modData.rawget("getInitialStateForClient");
		if (object == null) {
			throw new IllegalStateException("getInitialStateForClient method undefined for system \'" + this.name + "\'");
		} else {
			Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, object, (Object)this.modData);
			return objectArray != null && objectArray[0].equals(Boolean.TRUE) && objectArray[1] instanceof KahluaTable ? (KahluaTable)objectArray[1] : null;
		}
	}

	public void OnIsoObjectChangedItself(IsoObject object) {
		GlobalObject globalObject = this.getObjectAt(object.getSquare().x, object.getSquare().y, object.getSquare().z);
		if (globalObject != null) {
			Object object2 = this.modData.rawget("OnIsoObjectChangedItself");
			if (object2 == null) {
				throw new IllegalStateException("OnIsoObjectChangedItself method undefined for system \'" + this.name + "\'");
			} else {
				LuaManager.caller.pcall(LuaManager.thread, object2, this.modData, object);
			}
		}
	}

	public int loadedWorldVersion() {
		return this.loadedWorldVersion;
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		boolean boolean1 = byteBuffer.get() == 0;
		if (!boolean1) {
			this.modData.load(byteBuffer, int1);
		}

		int int2 = byteBuffer.getInt();
		for (int int3 = 0; int3 < int2; ++int3) {
			int int4 = byteBuffer.getInt();
			int int5 = byteBuffer.getInt();
			byte byte1 = byteBuffer.get();
			SGlobalObject sGlobalObject = (SGlobalObject)Type.tryCastTo(this.newObject(int4, int5, byte1), SGlobalObject.class);
			sGlobalObject.load(byteBuffer, int1);
		}

		this.loadedWorldVersion = int1;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		if (tempTable == null) {
			tempTable = LuaManager.platform.newTable();
		}

		tempTable.wipe();
		KahluaTableIterator kahluaTableIterator = this.modData.iterator();
		while (kahluaTableIterator.advance()) {
			Object object = kahluaTableIterator.getKey();
			if (this.modDataKeys.contains(object)) {
				tempTable.rawset(object, this.modData.rawget(object));
			}
		}

		if (tempTable.isEmpty()) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			tempTable.save(byteBuffer);
		}

		byteBuffer.putInt(this.objects.size());
		for (int int1 = 0; int1 < this.objects.size(); ++int1) {
			SGlobalObject sGlobalObject = (SGlobalObject)Type.tryCastTo((GlobalObject)this.objects.get(int1), SGlobalObject.class);
			sGlobalObject.save(byteBuffer);
		}
	}

	public void load() {
		File file = new File(this.getFileName());
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
						byte byte1 = byteBuffer.get();
						byte byte2 = byteBuffer.get();
						byte byte3 = byteBuffer.get();
						byte byte4 = byteBuffer.get();
						if (byte1 != 71 || byte2 != 76 || byte3 != 79 || byte4 != 83) {
							throw new IOException("doesn\'t appear to be a GlobalObjectSystem file:" + file.getAbsolutePath());
						}

						int int2 = byteBuffer.getInt();
						if (int2 < 134) {
							throw new IOException("invalid WorldVersion " + int2 + ": " + file.getAbsolutePath());
						}

						if (int2 > 194) {
							throw new IOException("file is from a newer version " + int2 + " of the game: " + file.getAbsolutePath());
						}

						this.load(byteBuffer, int2);
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
		} catch (Throwable throwable5) {
			ExceptionLogger.logException(throwable5);
		}
	}

	public void save() {
		if (!Core.getInstance().isNoSave()) {
			if (!GameClient.bClient) {
				File file = new File(this.getFileName());
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					try {
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
						try {
							synchronized (SliceY.SliceBufferLock) {
								ByteBuffer byteBuffer = SliceY.SliceBuffer;
								byteBuffer.clear();
								byteBuffer.put((byte)71);
								byteBuffer.put((byte)76);
								byteBuffer.put((byte)79);
								byteBuffer.put((byte)83);
								byteBuffer.putInt(194);
								this.save(byteBuffer);
								bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
							}
						} catch (Throwable throwable) {
							try {
								bufferedOutputStream.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}

							throw throwable;
						}

						bufferedOutputStream.close();
					} catch (Throwable throwable3) {
						try {
							fileOutputStream.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}

						throw throwable3;
					}

					fileOutputStream.close();
				} catch (Throwable throwable5) {
					ExceptionLogger.logException(throwable5);
				}
			}
		}
	}

	public void Reset() {
		super.Reset();
		this.modDataKeys.clear();
		this.objectModDataKeys.clear();
		this.objectSyncKeys.clear();
	}
}
