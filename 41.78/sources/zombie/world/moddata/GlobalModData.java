package zombie.world.moddata;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.world.WorldDictionary;


public final class GlobalModData {
	public static final String SAVE_EXT = ".bin";
	public static final String SAVE_FILE = "global_mod_data";
	public static GlobalModData instance = new GlobalModData();
	private Map modData = new HashMap();
	private static final int BLOCK_SIZE = 524288;
	private static int LAST_BLOCK_SIZE = -1;

	private KahluaTable createModDataTable() {
		return LuaManager.platform.newTable();
	}

	public GlobalModData() {
		this.reset();
	}

	public void init() throws IOException {
		this.reset();
		this.load();
		LuaEventManager.triggerEvent("OnInitGlobalModData", WorldDictionary.isIsNewGame());
	}

	public void reset() {
		LAST_BLOCK_SIZE = -1;
		this.modData.clear();
	}

	public void collectTableNames(List list) {
		list.clear();
		Iterator iterator = this.modData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			list.add((String)entry.getKey());
		}
	}

	public boolean exists(String string) {
		return this.modData.containsKey(string);
	}

	public KahluaTable getOrCreate(String string) {
		KahluaTable kahluaTable = this.get(string);
		if (kahluaTable == null) {
			kahluaTable = this.create(string);
		}

		return kahluaTable;
	}

	public KahluaTable get(String string) {
		return (KahluaTable)this.modData.get(string);
	}

	public String create() {
		String string = UUID.randomUUID().toString();
		this.create(string);
		return string;
	}

	public KahluaTable create(String string) {
		if (this.exists(string)) {
			DebugLog.log("GlobalModData -> Cannot create table \'" + string + "\', already exists. Returning null.");
			return null;
		} else {
			KahluaTable kahluaTable = this.createModDataTable();
			this.modData.put(string, kahluaTable);
			return kahluaTable;
		}
	}

	public KahluaTable remove(String string) {
		return (KahluaTable)this.modData.remove(string);
	}

	public void add(String string, KahluaTable kahluaTable) {
		this.modData.put(string, kahluaTable);
	}

	public void transmit(String string) {
		KahluaTable kahluaTable = this.get(string);
		if (kahluaTable != null) {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter);
				ByteBuffer byteBuffer = byteBufferWriter.bb;
				try {
					GameWindow.WriteString(byteBuffer, string);
					byteBuffer.put((byte)1);
					kahluaTable.save(byteBuffer);
				} catch (Exception exception) {
					exception.printStackTrace();
					GameClient.connection.cancelPacket();
				} finally {
					PacketTypes.PacketType.GlobalModData.send(GameClient.connection);
				}
			} else if (GameServer.bServer) {
				try {
					for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
						UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int1);
						ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
						PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter2);
						ByteBuffer byteBuffer2 = byteBufferWriter2.bb;
						try {
							GameWindow.WriteString(byteBuffer2, string);
							byteBuffer2.put((byte)1);
							kahluaTable.save(byteBuffer2);
						} catch (Exception exception2) {
							exception2.printStackTrace();
							udpConnection.cancelPacket();
						} finally {
							PacketTypes.PacketType.GlobalModData.send(udpConnection);
						}
					}
				} catch (Exception exception3) {
					DebugLog.log(exception3.getMessage());
				}
			}
		} else {
			DebugLog.log("GlobalModData -> cannot transmit moddata not found: " + string);
		}
	}

	public void receive(ByteBuffer byteBuffer) {
		try {
			String string = GameWindow.ReadString(byteBuffer);
			if (byteBuffer.get() != 1) {
				LuaEventManager.triggerEvent("OnReceiveGlobalModData", string, false);
				return;
			}

			KahluaTable kahluaTable = this.createModDataTable();
			kahluaTable.load((ByteBuffer)byteBuffer, 195);
			LuaEventManager.triggerEvent("OnReceiveGlobalModData", string, kahluaTable);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void request(String string) {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.GlobalModDataRequest.doPacket(byteBufferWriter);
			ByteBuffer byteBuffer = byteBufferWriter.bb;
			try {
				GameWindow.WriteString(byteBuffer, string);
			} catch (Exception exception) {
				exception.printStackTrace();
				GameClient.connection.cancelPacket();
			} finally {
				PacketTypes.PacketType.GlobalModDataRequest.send(GameClient.connection);
			}
		} else {
			DebugLog.log("GlobalModData -> can only request from Client.");
		}
	}

	public void receiveRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		KahluaTable kahluaTable = this.get(string);
		if (kahluaTable == null) {
			DebugLog.log("GlobalModData -> received request for non-existing table, table: " + string);
		}

		if (GameServer.bServer) {
			try {
				for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
					if (udpConnection2 == udpConnection) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.GlobalModData.doPacket(byteBufferWriter);
						ByteBuffer byteBuffer2 = byteBufferWriter.bb;
						try {
							GameWindow.WriteString(byteBuffer2, string);
							byteBuffer2.put((byte)(kahluaTable != null ? 1 : 0));
							if (kahluaTable != null) {
								kahluaTable.save(byteBuffer2);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							udpConnection2.cancelPacket();
						} finally {
							PacketTypes.PacketType.GlobalModData.send(udpConnection2);
						}
					}
				}
			} catch (Exception exception2) {
				DebugLog.log(exception2.getMessage());
			}
		}
	}

	private static ByteBuffer ensureCapacity(ByteBuffer byteBuffer) {
		if (byteBuffer == null) {
			LAST_BLOCK_SIZE = 1048576;
			return ByteBuffer.allocate(LAST_BLOCK_SIZE);
		} else {
			LAST_BLOCK_SIZE = byteBuffer.capacity() + 524288;
			ByteBuffer byteBuffer2 = ByteBuffer.allocate(LAST_BLOCK_SIZE);
			return byteBuffer2.put(byteBuffer.array(), 0, byteBuffer.position());
		}
	}

	public void save() throws IOException {
		if (!Core.getInstance().isNoSave()) {
			try {
				DebugLog.log("Saving GlobalModData");
				ByteBuffer byteBuffer = ByteBuffer.allocate(LAST_BLOCK_SIZE == -1 ? 1048576 : LAST_BLOCK_SIZE);
				byteBuffer.putInt(195);
				byteBuffer.putInt(this.modData.size());
				int int1 = 0;
				Iterator iterator = this.modData.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					if (byteBuffer.capacity() - byteBuffer.position() < 4) {
						int1 = byteBuffer.position();
						ensureCapacity(byteBuffer);
						byteBuffer.position(int1);
					}

					int int2 = byteBuffer.position();
					byteBuffer.putInt(0);
					int int3 = byteBuffer.position();
					while (true) {
						try {
							int1 = byteBuffer.position();
							GameWindow.WriteString(byteBuffer, (String)entry.getKey());
							((KahluaTable)entry.getValue()).save(byteBuffer);
						} catch (BufferOverflowException bufferOverflowException) {
							byteBuffer = ensureCapacity(byteBuffer);
							byteBuffer.position(int1);
							continue;
						}

						int int4 = byteBuffer.position();
						byteBuffer.position(int2);
						byteBuffer.putInt(int4 - int3);
						byteBuffer.position(int4);
						break;
					}
				}

				byteBuffer.flip();
				File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.tmp"));
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.getChannel().truncate(0L);
				fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
				fileOutputStream.flush();
				fileOutputStream.close();
				File file2 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.bin"));
				Files.copy(file, file2);
				file.delete();
			} catch (Exception exception) {
				exception.printStackTrace();
				throw new IOException("Error saving GlobalModData.", exception);
			}
		}
	}

	public void load() throws IOException {
		if (!Core.getInstance().isNoSave()) {
			String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("global_mod_data.bin");
			File file = new File(string);
			if (!file.exists()) {
				if (!WorldDictionary.isIsNewGame()) {
				}
			} else {
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					try {
						DebugLog.log("Loading GlobalModData:" + string);
						this.modData.clear();
						ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
						byteBuffer.clear();
						int int1 = fileInputStream.read(byteBuffer.array());
						byteBuffer.limit(int1);
						int int2 = byteBuffer.getInt();
						int int3 = byteBuffer.getInt();
						for (int int4 = 0; int4 < int3; ++int4) {
							int int5 = byteBuffer.getInt();
							String string2 = GameWindow.ReadString(byteBuffer);
							KahluaTable kahluaTable = this.createModDataTable();
							kahluaTable.load(byteBuffer, int2);
							this.modData.put(string2, kahluaTable);
						}
					} catch (Throwable throwable) {
						try {
							fileInputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					fileInputStream.close();
				} catch (Exception exception) {
					exception.printStackTrace();
					throw new IOException("Error loading GlobalModData.", exception);
				}
			}
		}
	}
}
