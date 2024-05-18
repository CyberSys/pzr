package zombie.globalObjects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.spnetwork.SinglePlayerServer;


public final class SGlobalObjectSystem {
	protected final String name;
	protected int loadedWorldVersion = -1;
	protected final ArrayList objects = new ArrayList();
	protected final GlobalObjectLookup lookup = new GlobalObjectLookup(this);
	protected final KahluaTable modData;
	protected final HashSet modDataKeys = new HashSet();
	protected final HashSet objectModDataKeys = new HashSet();
	private static KahluaTable tempTable;
	private static final ArrayDeque objectListPool = new ArrayDeque();

	public SGlobalObjectSystem(String string) {
		this.name = string;
		this.modData = LuaManager.platform.newTable();
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

	public KahluaTable getModData() {
		return this.modData;
	}

	public GlobalObject newObject(int int1, int int2, int int3) {
		if (this.getObjectAt(int1, int2, int3) != null) {
			throw new IllegalStateException("already an object at " + int1 + "," + int2 + "," + int3);
		} else {
			GlobalObject globalObject = new GlobalObject(this, int1, int2, int3);
			this.objects.add(globalObject);
			this.lookup.addObject(globalObject);
			return globalObject;
		}
	}

	public void removeObject(GlobalObject globalObject) throws IllegalArgumentException, IllegalStateException {
		if (globalObject == null) {
			throw new NullPointerException("object is null");
		} else if (globalObject.system != this) {
			throw new IllegalStateException("object not in this system");
		} else {
			this.objects.remove(globalObject);
			this.lookup.removeObject(globalObject);
			globalObject.Reset();
		}
	}

	public GlobalObject getObjectAt(int int1, int int2, int int3) {
		return this.lookup.getObjectAt(int1, int2, int3);
	}

	public boolean hasObjectsInChunk(int int1, int int2) {
		return this.lookup.hasObjectsInChunk(int1, int2);
	}

	public ArrayList getObjectsInChunk(int int1, int int2) {
		return this.lookup.getObjectsInChunk(int1, int2, this.allocList());
	}

	public ArrayList getObjectsAdjacentTo(int int1, int int2, int int3) {
		return this.lookup.getObjectsAdjacentTo(int1, int2, int3, this.allocList());
	}

	public ArrayList allocList() {
		return objectListPool.isEmpty() ? new ArrayList() : (ArrayList)objectListPool.pop();
	}

	public void finishedWithList(ArrayList arrayList) {
		if (arrayList != null && !objectListPool.contains(arrayList)) {
			arrayList.clear();
			objectListPool.add(arrayList);
		}
	}

	public int getObjectCount() {
		return this.objects.size();
	}

	public GlobalObject getObjectByIndex(int int1) {
		return int1 >= 0 && int1 < this.objects.size() ? (GlobalObject)this.objects.get(int1) : null;
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
		if (GameServer.bServer) {
			GameServer.sendServerCommand("gos_" + this.name, string, kahluaTable);
		} else {
			if (GameClient.bClient) {
				throw new IllegalStateException("can\'t call this method on the client");
			}

			SinglePlayerServer.sendServerCommand("gos_" + this.name, string, kahluaTable);
		}
	}

	public void receiveClientCommand(String string, IsoPlayer player, KahluaTable kahluaTable) {
		Object object = this.modData.rawget("OnClientCommand");
		if (object == null) {
			throw new IllegalStateException("OnClientCommand method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcall(LuaManager.thread, object, this.modData, string, player, kahluaTable);
		}
	}

	private String getFileName() {
		return GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "gos_" + this.name + ".bin";
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
			GlobalObject globalObject = this.newObject(int4, int5, byte1);
			globalObject.load(byteBuffer, int1);
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
			GlobalObject globalObject = (GlobalObject)this.objects.get(int1);
			globalObject.save(byteBuffer);
		}
	}

	public void load() {
		File file = new File(this.getFileName());
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Throwable throwable = null;
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				Throwable throwable2 = null;
				try {
					ByteBuffer byteBuffer = SliceY.SliceBuffer;
					byteBuffer.rewind();
					bufferedInputStream.read(byteBuffer.array());
					byte byte1 = byteBuffer.get();
					byte byte2 = byteBuffer.get();
					byte byte3 = byteBuffer.get();
					byte byte4 = byteBuffer.get();
					if (byte1 != 71 || byte2 != 76 || byte3 != 79 || byte4 != 83) {
						throw new IOException("doesn\'t appear to be a GlobalObjectSystem file:" + file.getAbsolutePath());
					}

					int int1 = byteBuffer.getInt();
					if (int1 < 134) {
						throw new IOException("invalid WorldVersion " + int1 + ": " + file.getAbsolutePath());
					}

					if (int1 > 143) {
						throw new IOException("file is from a newer version " + int1 + " of the game: " + file.getAbsolutePath());
					}

					this.load(byteBuffer, int1);
				} catch (Throwable throwable3) {
					throwable2 = throwable3;
					throw throwable3;
				} finally {
					if (bufferedInputStream != null) {
						if (throwable2 != null) {
							try {
								bufferedInputStream.close();
							} catch (Throwable throwable4) {
								throwable2.addSuppressed(throwable4);
							}
						} else {
							bufferedInputStream.close();
						}
					}
				}
			} catch (Throwable throwable5) {
				throwable = throwable5;
				throw throwable5;
			} finally {
				if (fileInputStream != null) {
					if (throwable != null) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable6) {
							throwable.addSuppressed(throwable6);
						}
					} else {
						fileInputStream.close();
					}
				}
			}
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Throwable throwable7) {
			ExceptionLogger.logException(throwable7);
		}
	}

	public void save() {
		if (!Core.getInstance().isNoSave()) {
			if (!GameClient.bClient) {
				File file = new File(this.getFileName());
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					Throwable throwable = null;
					try {
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
						Throwable throwable2 = null;
						try {
							ByteBuffer byteBuffer = SliceY.SliceBuffer;
							byteBuffer.rewind();
							byteBuffer.put((byte)71);
							byteBuffer.put((byte)76);
							byteBuffer.put((byte)79);
							byteBuffer.put((byte)83);
							byteBuffer.putInt(143);
							this.save(byteBuffer);
							bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
						} catch (Throwable throwable3) {
							throwable2 = throwable3;
							throw throwable3;
						} finally {
							if (bufferedOutputStream != null) {
								if (throwable2 != null) {
									try {
										bufferedOutputStream.close();
									} catch (Throwable throwable4) {
										throwable2.addSuppressed(throwable4);
									}
								} else {
									bufferedOutputStream.close();
								}
							}
						}
					} catch (Throwable throwable5) {
						throwable = throwable5;
						throw throwable5;
					} finally {
						if (fileOutputStream != null) {
							if (throwable != null) {
								try {
									fileOutputStream.close();
								} catch (Throwable throwable6) {
									throwable.addSuppressed(throwable6);
								}
							} else {
								fileOutputStream.close();
							}
						}
					}
				} catch (Throwable throwable7) {
					ExceptionLogger.logException(throwable7);
				}
			}
		}
	}

	public void Reset() {
		for (int int1 = 0; int1 < this.objects.size(); ++int1) {
			GlobalObject globalObject = (GlobalObject)this.objects.get(int1);
			globalObject.Reset();
		}

		this.modData.wipe();
		this.modDataKeys.clear();
		this.objectModDataKeys.clear();
		this.objects.clear();
	}
}
