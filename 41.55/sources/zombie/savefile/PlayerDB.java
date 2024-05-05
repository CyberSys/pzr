package zombie.savefile;

import gnu.trove.set.hash.TIntHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.iso.WorldStreamer;
import zombie.util.ByteBufferBackedInputStream;
import zombie.util.ByteBufferOutputStream;
import zombie.vehicles.VehiclesDB2;


public final class PlayerDB {
	public static final int INVALID_ID = -1;
	private static final int MIN_ID = 1;
	private static PlayerDB instance = null;
	private static final ThreadLocal TL_SliceBuffer = ThreadLocal.withInitial(()->{
    return ByteBuffer.allocate(32768);
});
	private static final ThreadLocal TL_Bytes = ThreadLocal.withInitial(()->{
    return new byte[1024];
});
	private static boolean s_allow = false;
	private final PlayerDB.IPlayerStore m_store = new PlayerDB.SQLPlayerStore();
	private final TIntHashSet m_usedIDs = new TIntHashSet();
	private final ConcurrentLinkedQueue m_toThread = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue m_fromThread = new ConcurrentLinkedQueue();
	private boolean m_forceSavePlayers;
	public boolean m_canSavePlayers = false;
	private final UpdateLimit m_saveToDBPeriod = new UpdateLimit(10000L);

	public static synchronized PlayerDB getInstance() {
		if (instance == null && s_allow) {
			instance = new PlayerDB();
		}

		return instance;
	}

	public static void setAllow(boolean boolean1) {
		s_allow = boolean1;
	}

	public static boolean isAllow() {
		return s_allow;
	}

	public static boolean isAvailable() {
		return instance != null;
	}

	public PlayerDB() {
		if (!Core.getInstance().isNoSave()) {
			this.create();
		}
	}

	private void create() {
		try {
			this.m_store.init(this.m_usedIDs);
			this.m_usedIDs.add(1);
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void close() {
		assert WorldStreamer.instance.worldStreamer == null;
		this.updateWorldStreamer();
		assert this.m_toThread.isEmpty();
		try {
			this.m_store.Reset();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		this.m_fromThread.clear();
		instance = null;
		s_allow = false;
	}

	private int allocateID() {
		synchronized (this.m_usedIDs) {
			for (int int1 = 1; int1 < Integer.MAX_VALUE; ++int1) {
				if (!this.m_usedIDs.contains(int1)) {
					this.m_usedIDs.add(int1);
					return int1;
				}
			}

			throw new RuntimeException("ran out of unused players.db ids");
		}
	}

	private PlayerDB.PlayerData allocPlayerData() {
		PlayerDB.PlayerData playerData = (PlayerDB.PlayerData)this.m_fromThread.poll();
		if (playerData == null) {
			playerData = new PlayerDB.PlayerData();
		}

		assert playerData.m_sqlID == -1;
		return playerData;
	}

	private void releasePlayerData(PlayerDB.PlayerData playerData) {
		playerData.m_sqlID = -1;
		this.m_fromThread.add(playerData);
	}

	public void updateMain() {
		if (this.m_canSavePlayers && (this.m_forceSavePlayers || this.m_saveToDBPeriod.Check())) {
			this.m_forceSavePlayers = false;
			this.savePlayersAsync();
			VehiclesDB2.instance.setForceSave();
		}
	}

	public void updateWorldStreamer() {
		for (PlayerDB.PlayerData playerData = (PlayerDB.PlayerData)this.m_toThread.poll(); playerData != null; playerData = (PlayerDB.PlayerData)this.m_toThread.poll()) {
			try {
				this.m_store.save(playerData);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			} finally {
				this.releasePlayerData(playerData);
			}
		}
	}

	private void savePlayerAsync(IsoPlayer player) throws Exception {
		if (player != null) {
			if (player.sqlID == -1) {
				player.sqlID = this.allocateID();
			}

			PlayerDB.PlayerData playerData = this.allocPlayerData();
			try {
				playerData.set(player);
				this.m_toThread.add(playerData);
			} catch (Exception exception) {
				this.releasePlayerData(playerData);
				throw exception;
			}
		}
	}

	private void savePlayersAsync() {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				try {
					this.savePlayerAsync(player);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}
	}

	public void savePlayers() {
		if (this.m_canSavePlayers) {
			this.m_forceSavePlayers = true;
		}
	}

	public void saveLocalPlayersForce() {
		this.savePlayersAsync();
		if (WorldStreamer.instance.worldStreamer == null) {
			this.updateWorldStreamer();
		}
	}

	public void importPlayersFromVehiclesDB() {
		VehiclesDB2.instance.importPlayersFromOldDB((var1,var2,var3,var4,var5,var6,var7,var8,var9,var10)->{
			PlayerDB.PlayerData playerData = this.allocPlayerData();
			playerData.m_sqlID = this.allocateID();
			playerData.m_x = var5;
			playerData.m_y = var6;
			playerData.m_z = var7;
			playerData.m_isDead = var10;
			playerData.m_name = var2;
			playerData.m_WorldVersion = var8;
			playerData.setBytes(var9);
			try {
				this.m_store.save(playerData);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}

			this.releasePlayerData(playerData);
		});
	}

	public void uploadLocalPlayers2DB() {
		this.savePlayersAsync();
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string2 = string + Core.GameSaveWorld;
		for (int int1 = 1; int1 < 100; ++int1) {
			File file = new File(string2 + File.separator + "map_p" + int1 + ".bin");
			if (file.exists()) {
				try {
					IsoPlayer player = new IsoPlayer(IsoWorld.instance.CurrentCell);
					player.load(file.getAbsolutePath());
					this.savePlayerAsync(player);
					file.delete();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		if (WorldStreamer.instance.worldStreamer == null) {
			this.updateWorldStreamer();
		}
	}

	private boolean loadPlayer(int int1, IsoPlayer player) {
		PlayerDB.PlayerData playerData = this.allocPlayerData();
		boolean boolean1;
		try {
			playerData.m_sqlID = int1;
			if (this.m_store.load(playerData)) {
				player.load(playerData.m_byteBuffer, playerData.m_WorldVersion);
				if (playerData.m_isDead) {
					player.getBodyDamage().setOverallBodyHealth(0.0F);
					player.setHealth(0.0F);
				}

				player.sqlID = int1;
				boolean1 = true;
				return boolean1;
			}

			boolean1 = false;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		} finally {
			this.releasePlayerData(playerData);
		}

		return boolean1;
	}

	public boolean loadLocalPlayer(int int1) {
		try {
			IsoPlayer player = IsoPlayer.getInstance();
			if (player == null) {
				player = new IsoPlayer(IsoCell.getInstance());
				IsoPlayer.setInstance(player);
				IsoPlayer.players[0] = player;
			}

			if (this.loadPlayer(int1, player)) {
				int int2 = (int)(player.x / 10.0F);
				int int3 = (int)(player.y / 10.0F);
				IsoCell.getInstance().ChunkMap[IsoPlayer.getPlayerIndex()].WorldX = int2 + IsoWorld.saveoffsetx * 30;
				IsoCell.getInstance().ChunkMap[IsoPlayer.getPlayerIndex()].WorldY = int3 + IsoWorld.saveoffsety * 30;
				return true;
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		return false;
	}

	public ArrayList getAllLocalPlayers() {
		ArrayList arrayList = new ArrayList();
		this.m_usedIDs.forEach((var2)->{
			if (var2 <= 1) {
				return true;
			} else {
				IsoPlayer player = new IsoPlayer(IsoWorld.instance.CurrentCell);
				if (this.loadPlayer(var2, player)) {
					arrayList.add(player);
				}

				return true;
			}
		});
		return arrayList;
	}

	public boolean loadLocalPlayerInfo(int int1) {
		PlayerDB.PlayerData playerData = this.allocPlayerData();
		boolean boolean1;
		try {
			playerData.m_sqlID = int1;
			if (!this.m_store.loadEverythingExceptBytes(playerData)) {
				return false;
			}

			IsoChunkMap.WorldXA = (int)playerData.m_x;
			IsoChunkMap.WorldYA = (int)playerData.m_y;
			IsoChunkMap.WorldZA = (int)playerData.m_z;
			IsoChunkMap.WorldXA += 300 * IsoWorld.saveoffsetx;
			IsoChunkMap.WorldYA += 300 * IsoWorld.saveoffsety;
			IsoChunkMap.SWorldX[0] = (int)(playerData.m_x / 10.0F);
			IsoChunkMap.SWorldY[0] = (int)(playerData.m_y / 10.0F);
			int[] intArray = IsoChunkMap.SWorldX;
			intArray[0] += 30 * IsoWorld.saveoffsetx;
			intArray = IsoChunkMap.SWorldY;
			intArray[0] += 30 * IsoWorld.saveoffsety;
			boolean1 = true;
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			return false;
		} finally {
			this.releasePlayerData(playerData);
		}

		return boolean1;
	}

	private static final class SQLPlayerStore implements PlayerDB.IPlayerStore {
		Connection m_conn = null;

		public void init(TIntHashSet tIntHashSet) throws Exception {
			tIntHashSet.clear();
			if (!Core.getInstance().isNoSave()) {
				this.m_conn = PlayerDBHelper.create();
				this.initUsedIDs(tIntHashSet);
			}
		}

		public void Reset() {
			if (this.m_conn != null) {
				try {
					this.m_conn.close();
				} catch (SQLException sQLException) {
					ExceptionLogger.logException(sQLException);
				}

				this.m_conn = null;
			}
		}

		public void save(PlayerDB.PlayerData playerData) throws Exception {
			assert playerData.m_sqlID >= 1;
			if (this.m_conn != null) {
				if (this.isInDB(playerData.m_sqlID)) {
					this.update(playerData);
				} else {
					this.add(playerData);
				}
			}
		}

		public boolean load(PlayerDB.PlayerData playerData) throws Exception {
			assert playerData.m_sqlID >= 1;
			if (this.m_conn == null) {
				return false;
			} else {
				String string = "SELECT data,worldversion,x,y,z,isDead,name FROM localPlayers WHERE id=?";
				PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
				boolean boolean1;
				label50: {
					try {
						preparedStatement.setInt(1, playerData.m_sqlID);
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							InputStream inputStream = resultSet.getBinaryStream(1);
							playerData.setBytes(inputStream);
							playerData.m_WorldVersion = resultSet.getInt(2);
							playerData.m_x = (float)resultSet.getInt(3);
							playerData.m_y = (float)resultSet.getInt(4);
							playerData.m_z = (float)resultSet.getInt(5);
							playerData.m_isDead = resultSet.getBoolean(6);
							playerData.m_name = resultSet.getString(7);
							boolean1 = true;
							break label50;
						}
					} catch (Throwable throwable) {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}

					return false;
				}

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				return boolean1;
			}
		}

		public boolean loadEverythingExceptBytes(PlayerDB.PlayerData playerData) throws Exception {
			if (this.m_conn == null) {
				return false;
			} else {
				String string = "SELECT worldversion,x,y,z,isDead,name FROM localPlayers WHERE id=?";
				PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
				boolean boolean1;
				label44: {
					try {
						preparedStatement.setInt(1, playerData.m_sqlID);
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							playerData.m_WorldVersion = resultSet.getInt(1);
							playerData.m_x = (float)resultSet.getInt(2);
							playerData.m_y = (float)resultSet.getInt(3);
							playerData.m_z = (float)resultSet.getInt(4);
							playerData.m_isDead = resultSet.getBoolean(5);
							playerData.m_name = resultSet.getString(6);
							boolean1 = true;
							break label44;
						}
					} catch (Throwable throwable) {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}

					return false;
				}

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				return boolean1;
			}
		}

		void initUsedIDs(TIntHashSet tIntHashSet) throws SQLException {
			String string = "SELECT id FROM localPlayers";
			PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
			try {
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					tIntHashSet.add(resultSet.getInt(1));
				}
			} catch (Throwable throwable) {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}
				}

				throw throwable;
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}

		boolean isInDB(int int1) throws SQLException {
			String string = "SELECT 1 FROM localPlayers WHERE id=?";
			PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
			boolean boolean1;
			try {
				preparedStatement.setInt(1, int1);
				ResultSet resultSet = preparedStatement.executeQuery();
				boolean1 = resultSet.next();
			} catch (Throwable throwable) {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}
				}

				throw throwable;
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			return boolean1;
		}

		void add(PlayerDB.PlayerData playerData) throws Exception {
			if (this.m_conn != null && playerData.m_sqlID >= 1) {
				String string = "INSERT INTO localPlayers(wx,wy,x,y,z,worldversion,data,isDead,name,id) VALUES(?,?,?,?,?,?,?,?,?,?)";
				try {
					PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
					try {
						preparedStatement.setInt(1, (int)(playerData.m_x / 10.0F));
						preparedStatement.setInt(2, (int)(playerData.m_y / 10.0F));
						preparedStatement.setFloat(3, playerData.m_x);
						preparedStatement.setFloat(4, playerData.m_y);
						preparedStatement.setFloat(5, playerData.m_z);
						preparedStatement.setInt(6, playerData.m_WorldVersion);
						ByteBuffer byteBuffer = playerData.m_byteBuffer;
						byteBuffer.rewind();
						preparedStatement.setBinaryStream(7, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
						preparedStatement.setBoolean(8, playerData.m_isDead);
						preparedStatement.setString(9, playerData.m_name);
						preparedStatement.setInt(10, playerData.m_sqlID);
						int int1 = preparedStatement.executeUpdate();
						this.m_conn.commit();
					} catch (Throwable throwable) {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (Exception exception) {
					PlayerDBHelper.rollback(this.m_conn);
					throw exception;
				}
			}
		}

		public void update(PlayerDB.PlayerData playerData) throws Exception {
			if (this.m_conn != null && playerData.m_sqlID >= 1) {
				String string = "UPDATE localPlayers SET wx = ?, wy = ?, x = ?, y = ?, z = ?, worldversion = ?, data = ?, isDead = ?, name = ? WHERE id=?";
				try {
					PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
					try {
						preparedStatement.setInt(1, (int)(playerData.m_x / 10.0F));
						preparedStatement.setInt(2, (int)(playerData.m_y / 10.0F));
						preparedStatement.setFloat(3, playerData.m_x);
						preparedStatement.setFloat(4, playerData.m_y);
						preparedStatement.setFloat(5, playerData.m_z);
						preparedStatement.setInt(6, playerData.m_WorldVersion);
						ByteBuffer byteBuffer = playerData.m_byteBuffer;
						byteBuffer.rewind();
						preparedStatement.setBinaryStream(7, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
						preparedStatement.setBoolean(8, playerData.m_isDead);
						preparedStatement.setString(9, playerData.m_name);
						preparedStatement.setInt(10, playerData.m_sqlID);
						int int1 = preparedStatement.executeUpdate();
						this.m_conn.commit();
					} catch (Throwable throwable) {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (Exception exception) {
					PlayerDBHelper.rollback(this.m_conn);
					throw exception;
				}
			}
		}
	}

	private interface IPlayerStore {

		void init(TIntHashSet tIntHashSet) throws Exception;

		void Reset() throws Exception;

		void save(PlayerDB.PlayerData playerData) throws Exception;

		boolean load(PlayerDB.PlayerData playerData) throws Exception;

		boolean loadEverythingExceptBytes(PlayerDB.PlayerData playerData) throws Exception;
	}

	private static final class PlayerData {
		int m_sqlID = -1;
		float m_x;
		float m_y;
		float m_z;
		boolean m_isDead;
		String m_name;
		int m_WorldVersion;
		ByteBuffer m_byteBuffer = ByteBuffer.allocate(32768);

		PlayerDB.PlayerData set(IsoPlayer player) throws IOException {
			assert player.sqlID >= 1;
			this.m_sqlID = player.sqlID;
			this.m_x = player.getX();
			this.m_y = player.getY();
			this.m_z = player.getZ();
			this.m_isDead = player.isDead();
			String string = player.getDescriptor().getForename();
			this.m_name = string + " " + player.getDescriptor().getSurname();
			this.m_WorldVersion = IsoWorld.getWorldVersion();
			ByteBuffer byteBuffer = (ByteBuffer)PlayerDB.TL_SliceBuffer.get();
			byteBuffer.clear();
			while (true) {
				try {
					player.save(byteBuffer);
					break;
				} catch (BufferOverflowException bufferOverflowException) {
					if (byteBuffer.capacity() >= 2097152) {
						DebugLog.General.error("the player %s cannot be saved", player.getUsername());
						throw bufferOverflowException;
					}

					byteBuffer = ByteBuffer.allocate(byteBuffer.capacity() + '耀');
					PlayerDB.TL_SliceBuffer.set(byteBuffer);
				}
			}

			byteBuffer.flip();
			this.setBytes(byteBuffer);
			return this;
		}

		void setBytes(ByteBuffer byteBuffer) {
			byteBuffer.rewind();
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
			byteBufferOutputStream.clear();
			byte[] byteArray = (byte[])PlayerDB.TL_Bytes.get();
			int int1;
			for (int int2 = byteBuffer.limit(); int2 > 0; int2 -= int1) {
				int1 = Math.min(byteArray.length, int2);
				byteBuffer.get(byteArray, 0, int1);
				byteBufferOutputStream.write(byteArray, 0, int1);
			}

			byteBufferOutputStream.flip();
			this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
		}

		void setBytes(byte[] byteArray) {
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
			byteBufferOutputStream.clear();
			byteBufferOutputStream.write(byteArray);
			byteBufferOutputStream.flip();
			this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
		}

		void setBytes(InputStream inputStream) throws IOException {
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_byteBuffer, true);
			byteBufferOutputStream.clear();
			byte[] byteArray = (byte[])PlayerDB.TL_Bytes.get();
			while (true) {
				int int1 = inputStream.read(byteArray);
				if (int1 < 1) {
					byteBufferOutputStream.flip();
					this.m_byteBuffer = byteBufferOutputStream.getWrappedBuffer();
					return;
				}

				byteBufferOutputStream.write(byteArray, 0, int1);
			}
		}
	}
}
