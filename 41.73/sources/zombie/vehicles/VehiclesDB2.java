package zombie.vehicles;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.WorldStreamer;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationRenderer;
import zombie.util.ByteBufferBackedInputStream;
import zombie.util.ByteBufferOutputStream;
import zombie.util.PZSQLUtils;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.Type;


public final class VehiclesDB2 {
	public static final int INVALID_ID = -1;
	private static final int MIN_ID = 1;
	public static final VehiclesDB2 instance = new VehiclesDB2();
	private static final ThreadLocal TL_SliceBuffer = ThreadLocal.withInitial(()->{
    return ByteBuffer.allocate(32768);
});
	private static final ThreadLocal TL_Bytes = ThreadLocal.withInitial(()->{
    return new byte[1024];
});
	private final VehiclesDB2.MainThread m_main = new VehiclesDB2.MainThread();
	private final VehiclesDB2.WorldStreamerThread m_worldStreamer = new VehiclesDB2.WorldStreamerThread();

	public void init() {
		this.m_worldStreamer.m_store.init(this.m_main.m_usedIDs, this.m_main.m_seenChunks);
	}

	public void Reset() {
		assert WorldStreamer.instance.worldStreamer == null;
		this.updateWorldStreamer();
		for (VehiclesDB2.QueueItem queueItem = (VehiclesDB2.QueueItem)this.m_main.m_queue.poll(); queueItem != null; queueItem = (VehiclesDB2.QueueItem)this.m_main.m_queue.poll()) {
			queueItem.release();
		}

		this.m_main.Reset();
		this.m_worldStreamer.Reset();
	}

	public void updateMain() throws IOException {
		this.m_main.update();
	}

	public void updateWorldStreamer() {
		this.m_worldStreamer.update();
	}

	public void setForceSave() {
		this.m_main.m_forceSave = true;
	}

	public void renderDebug(ZombiePopulationRenderer zombiePopulationRenderer) {
	}

	public void setChunkSeen(int int1, int int2) {
		this.m_main.setChunkSeen(int1, int2);
	}

	public boolean isChunkSeen(int int1, int int2) {
		return this.m_main.isChunkSeen(int1, int2);
	}

	public void setVehicleLoaded(BaseVehicle baseVehicle) {
		this.m_main.setVehicleLoaded(baseVehicle);
	}

	public void setVehicleUnloaded(BaseVehicle baseVehicle) {
		this.m_main.setVehicleUnloaded(baseVehicle);
	}

	public boolean isVehicleLoaded(BaseVehicle baseVehicle) {
		return this.m_main.m_loadedIDs.contains(baseVehicle.sqlID);
	}

	public void loadChunkMain(IsoChunk chunk) {
		this.m_main.loadChunk(chunk);
	}

	public void loadChunk(IsoChunk chunk) throws IOException {
		this.m_worldStreamer.loadChunk(chunk);
	}

	public void unloadChunk(IsoChunk chunk) {
		if (Thread.currentThread() != WorldStreamer.instance.worldStreamer) {
			boolean boolean1 = true;
		}

		this.m_worldStreamer.unloadChunk(chunk);
	}

	public void addVehicle(BaseVehicle baseVehicle) {
		try {
			this.m_main.addVehicle(baseVehicle);
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void removeVehicle(BaseVehicle baseVehicle) {
		this.m_main.removeVehicle(baseVehicle);
	}

	public void updateVehicle(BaseVehicle baseVehicle) {
		try {
			this.m_main.updateVehicle(baseVehicle);
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void updateVehicleAndTrailer(BaseVehicle baseVehicle) {
		if (baseVehicle != null) {
			this.updateVehicle(baseVehicle);
			BaseVehicle baseVehicle2 = baseVehicle.getVehicleTowing();
			if (baseVehicle2 != null) {
				this.updateVehicle(baseVehicle2);
			}
		}
	}

	public void importPlayersFromOldDB(VehiclesDB2.IImportPlayerFromOldDB iImportPlayerFromOldDB) {
		VehiclesDB2.SQLStore sQLStore = (VehiclesDB2.SQLStore)Type.tryCastTo(this.m_worldStreamer.m_store, VehiclesDB2.SQLStore.class);
		if (sQLStore != null && sQLStore.m_conn != null) {
			try {
				DatabaseMetaData databaseMetaData = sQLStore.m_conn.getMetaData();
				ResultSet resultSet = databaseMetaData.getTables((String)null, (String)null, "localPlayers", (String[])null);
				label91: {
					try {
						if (resultSet.next()) {
							break label91;
						}
					} catch (Throwable throwable) {
						if (resultSet != null) {
							try {
								resultSet.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}
						}

						throw throwable;
					}

					if (resultSet != null) {
						resultSet.close();
					}

					return;
				}

				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				return;
			}

			String string = "SELECT id, name, wx, wy, x, y, z, worldversion, data, isDead FROM localPlayers";
			try {
				PreparedStatement preparedStatement = sQLStore.m_conn.prepareStatement(string);
				try {
					ResultSet resultSet2 = preparedStatement.executeQuery();
					while (resultSet2.next()) {
						int int1 = resultSet2.getInt(1);
						String string2 = resultSet2.getString(2);
						int int2 = resultSet2.getInt(3);
						int int3 = resultSet2.getInt(4);
						float float1 = resultSet2.getFloat(5);
						float float2 = resultSet2.getFloat(6);
						float float3 = resultSet2.getFloat(7);
						int int4 = resultSet2.getInt(8);
						byte[] byteArray = resultSet2.getBytes(9);
						boolean boolean1 = resultSet2.getBoolean(10);
						iImportPlayerFromOldDB.accept(int1, string2, int2, int3, float1, float2, float3, int4, byteArray, boolean1);
					}
				} catch (Throwable throwable3) {
					if (preparedStatement != null) {
						try {
							preparedStatement.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}
					}

					throw throwable3;
				}

				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception exception2) {
				ExceptionLogger.logException(exception2);
			}

			try {
				Statement statement = sQLStore.m_conn.createStatement();
				statement.executeUpdate("DROP TABLE localPlayers");
				statement.executeUpdate("DROP TABLE networkPlayers");
				sQLStore.m_conn.commit();
			} catch (Exception exception3) {
				ExceptionLogger.logException(exception3);
			}
		}
	}

	private static final class MainThread {
		final TIntHashSet m_seenChunks = new TIntHashSet();
		final TIntHashSet m_usedIDs = new TIntHashSet();
		final TIntHashSet m_loadedIDs = new TIntHashSet();
		boolean m_forceSave = false;
		final ConcurrentLinkedQueue m_queue = new ConcurrentLinkedQueue();

		MainThread() {
			this.m_seenChunks.setAutoCompactionFactor(0.0F);
			this.m_usedIDs.setAutoCompactionFactor(0.0F);
			this.m_loadedIDs.setAutoCompactionFactor(0.0F);
		}

		void Reset() {
			this.m_seenChunks.clear();
			this.m_usedIDs.clear();
			this.m_loadedIDs.clear();
			assert this.m_queue.isEmpty();
			this.m_queue.clear();
			this.m_forceSave = false;
		}

		void update() throws IOException {
			if (!GameClient.bClient && !GameServer.bServer && this.m_forceSave) {
				this.m_forceSave = false;
				for (int int1 = 0; int1 < 4; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && player.getVehicle() != null && player.getVehicle().isEngineRunning()) {
						this.updateVehicle(player.getVehicle());
						BaseVehicle baseVehicle = player.getVehicle().getVehicleTowing();
						if (baseVehicle != null) {
							this.updateVehicle(baseVehicle);
						}
					}
				}
			}

			for (VehiclesDB2.QueueItem queueItem = (VehiclesDB2.QueueItem)this.m_queue.poll(); queueItem != null; queueItem = (VehiclesDB2.QueueItem)this.m_queue.poll()) {
				try {
					queueItem.processMain();
				} finally {
					queueItem.release();
				}
			}
		}

		void setChunkSeen(int int1, int int2) {
			int int3 = int2 << 16 | int1;
			this.m_seenChunks.add(int3);
		}

		boolean isChunkSeen(int int1, int int2) {
			int int3 = int2 << 16 | int1;
			return this.m_seenChunks.contains(int3);
		}

		int allocateID() {
			synchronized (this.m_usedIDs) {
				for (int int1 = 1; int1 < Integer.MAX_VALUE; ++int1) {
					if (!this.m_usedIDs.contains(int1)) {
						this.m_usedIDs.add(int1);
						return int1;
					}
				}

				throw new RuntimeException("ran out of unused vehicle ids");
			}
		}

		void setVehicleLoaded(BaseVehicle baseVehicle) {
			if (baseVehicle.sqlID == -1) {
				baseVehicle.sqlID = this.allocateID();
			}

			assert !this.m_loadedIDs.contains(baseVehicle.sqlID);
			this.m_loadedIDs.add(baseVehicle.sqlID);
		}

		void setVehicleUnloaded(BaseVehicle baseVehicle) {
			if (baseVehicle.sqlID != -1) {
				this.m_loadedIDs.remove(baseVehicle.sqlID);
			}
		}

		void addVehicle(BaseVehicle baseVehicle) throws IOException {
			if (baseVehicle.sqlID == -1) {
				baseVehicle.sqlID = this.allocateID();
			}

			VehiclesDB2.QueueAddVehicle queueAddVehicle = (VehiclesDB2.QueueAddVehicle)VehiclesDB2.QueueAddVehicle.s_pool.alloc();
			queueAddVehicle.init(baseVehicle);
			VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueAddVehicle);
		}

		void removeVehicle(BaseVehicle baseVehicle) {
			VehiclesDB2.QueueRemoveVehicle queueRemoveVehicle = (VehiclesDB2.QueueRemoveVehicle)VehiclesDB2.QueueRemoveVehicle.s_pool.alloc();
			queueRemoveVehicle.init(baseVehicle);
			VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueRemoveVehicle);
		}

		void updateVehicle(BaseVehicle baseVehicle) throws IOException {
			if (baseVehicle.sqlID == -1) {
				baseVehicle.sqlID = this.allocateID();
			}

			VehiclesDB2.QueueUpdateVehicle queueUpdateVehicle = (VehiclesDB2.QueueUpdateVehicle)VehiclesDB2.QueueUpdateVehicle.s_pool.alloc();
			queueUpdateVehicle.init(baseVehicle);
			VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueUpdateVehicle);
		}

		void loadChunk(IsoChunk chunk) {
			VehiclesDB2.QueueLoadChunk queueLoadChunk = (VehiclesDB2.QueueLoadChunk)VehiclesDB2.QueueLoadChunk.s_pool.alloc();
			queueLoadChunk.init(chunk.wx, chunk.wy);
			chunk.m_loadVehiclesObject = queueLoadChunk;
			VehiclesDB2.instance.m_worldStreamer.m_queue.add(queueLoadChunk);
		}
	}

	private static final class WorldStreamerThread {
		final VehiclesDB2.IVehicleStore m_store = new VehiclesDB2.SQLStore();
		final ConcurrentLinkedQueue m_queue = new ConcurrentLinkedQueue();
		final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

		void Reset() {
			this.m_store.Reset();
			assert this.m_queue.isEmpty();
			this.m_queue.clear();
		}

		void update() {
			for (VehiclesDB2.QueueItem queueItem = (VehiclesDB2.QueueItem)this.m_queue.poll(); queueItem != null; queueItem = (VehiclesDB2.QueueItem)this.m_queue.poll()) {
				try {
					queueItem.processWorldStreamer();
				} finally {
					VehiclesDB2.instance.m_main.m_queue.add(queueItem);
				}
			}
		}

		void loadChunk(IsoChunk chunk) throws IOException {
			this.m_store.loadChunk(chunk, this::vehicleLoaded);
		}

		void vehicleLoaded(IsoChunk chunk, VehiclesDB2.VehicleBuffer vehicleBuffer) throws IOException {
			assert vehicleBuffer.m_id >= 1;
			IsoGridSquare square = chunk.getGridSquare((int)(vehicleBuffer.m_x - (float)(chunk.wx * 10)), (int)(vehicleBuffer.m_y - (float)(chunk.wy * 10)), 0);
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.setSquare(square);
			baseVehicle.setCurrent(square);
			try {
				baseVehicle.load(vehicleBuffer.m_bb, vehicleBuffer.m_WorldVersion);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				DebugLog.General.error("vehicle %d is being deleted because an error occurred loading it", vehicleBuffer.m_id);
				this.m_store.removeVehicle(vehicleBuffer.m_id);
				return;
			}

			baseVehicle.sqlID = vehicleBuffer.m_id;
			baseVehicle.chunk = chunk;
			if (chunk.jobType == IsoChunk.JobType.SoftReset) {
				baseVehicle.softReset();
			}

			chunk.vehicles.add(baseVehicle);
		}

		void unloadChunk(IsoChunk chunk) {
			for (int int1 = 0; int1 < chunk.vehicles.size(); ++int1) {
				try {
					BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int1);
					this.m_vehicleBuffer.set(baseVehicle);
					this.m_store.updateVehicle(this.m_vehicleBuffer);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}
	}

	private abstract static class IVehicleStore {

		abstract void init(TIntHashSet tIntHashSet, TIntHashSet tIntHashSet2);

		abstract void Reset();

		abstract void loadChunk(IsoChunk chunk, VehiclesDB2.ThrowingBiConsumer throwingBiConsumer) throws IOException;

		abstract void loadChunk(int int1, int int2, VehiclesDB2.ThrowingConsumer throwingConsumer) throws IOException;

		abstract void updateVehicle(VehiclesDB2.VehicleBuffer vehicleBuffer);

		abstract void removeVehicle(int int1);
	}

	private abstract static class QueueItem extends PooledObject {

		abstract void processMain();

		abstract void processWorldStreamer();
	}

	private static final class SQLStore extends VehiclesDB2.IVehicleStore {
		Connection m_conn = null;
		final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

		void init(TIntHashSet tIntHashSet, TIntHashSet tIntHashSet2) {
			tIntHashSet.clear();
			tIntHashSet2.clear();
			if (!Core.getInstance().isNoSave()) {
				this.create();
				try {
					this.initUsedIDs(tIntHashSet, tIntHashSet2);
				} catch (SQLException sQLException) {
					ExceptionLogger.logException(sQLException);
				}
			}
		}

		void Reset() {
			if (this.m_conn != null) {
				try {
					this.m_conn.close();
				} catch (SQLException sQLException) {
					ExceptionLogger.logException(sQLException);
				}

				this.m_conn = null;
			}
		}

		void loadChunk(IsoChunk chunk, VehiclesDB2.ThrowingBiConsumer throwingBiConsumer) throws IOException {
			if (this.m_conn != null && chunk != null) {
				String string = "SELECT id, x, y, data, worldversion FROM vehicles WHERE wx=? AND wy=?";
				try {
					PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
					try {
						preparedStatement.setInt(1, chunk.wx);
						preparedStatement.setInt(2, chunk.wy);
						ResultSet resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							this.m_vehicleBuffer.m_id = resultSet.getInt(1);
							this.m_vehicleBuffer.m_wx = chunk.wx;
							this.m_vehicleBuffer.m_wy = chunk.wy;
							this.m_vehicleBuffer.m_x = resultSet.getFloat(2);
							this.m_vehicleBuffer.m_y = resultSet.getFloat(3);
							InputStream inputStream = resultSet.getBinaryStream(4);
							this.m_vehicleBuffer.setBytes(inputStream);
							this.m_vehicleBuffer.m_WorldVersion = resultSet.getInt(5);
							boolean boolean1 = this.m_vehicleBuffer.m_bb.get() != 0;
							byte byte1 = this.m_vehicleBuffer.m_bb.get();
							if (byte1 == IsoObject.getFactoryVehicle().getClassID() && boolean1) {
								throwingBiConsumer.accept(chunk, this.m_vehicleBuffer);
							}
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
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		void loadChunk(int int1, int int2, VehiclesDB2.ThrowingConsumer throwingConsumer) throws IOException {
			if (this.m_conn != null) {
				String string = "SELECT id, x, y, data, worldversion FROM vehicles WHERE wx=? AND wy=?";
				try {
					PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
					try {
						preparedStatement.setInt(1, int1);
						preparedStatement.setInt(2, int2);
						ResultSet resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							this.m_vehicleBuffer.m_id = resultSet.getInt(1);
							this.m_vehicleBuffer.m_wx = int1;
							this.m_vehicleBuffer.m_wy = int2;
							this.m_vehicleBuffer.m_x = resultSet.getFloat(2);
							this.m_vehicleBuffer.m_y = resultSet.getFloat(3);
							InputStream inputStream = resultSet.getBinaryStream(4);
							this.m_vehicleBuffer.setBytes(inputStream);
							this.m_vehicleBuffer.m_WorldVersion = resultSet.getInt(5);
							boolean boolean1 = this.m_vehicleBuffer.m_bb.get() != 0;
							byte byte1 = this.m_vehicleBuffer.m_bb.get();
							if (byte1 == IsoObject.getFactoryVehicle().getClassID() && boolean1) {
								throwingConsumer.accept(this.m_vehicleBuffer);
							}
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
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}
			}
		}

		void updateVehicle(VehiclesDB2.VehicleBuffer vehicleBuffer) {
			if (this.m_conn != null) {
				assert vehicleBuffer.m_id >= 1;
				synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
					assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer.m_id);
				}

				try {
					if (this.isInDB(vehicleBuffer.m_id)) {
						this.updateDB(vehicleBuffer);
					} else {
						this.addToDB(vehicleBuffer);
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					this.rollback();
				}
			}
		}

		boolean isInDB(int int1) throws SQLException {
			String string = "SELECT 1 FROM vehicles WHERE id=?";
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

		void addToDB(VehiclesDB2.VehicleBuffer vehicleBuffer) throws SQLException {
			String string = "INSERT INTO vehicles(wx,wy,x,y,worldversion,data,id) VALUES(?,?,?,?,?,?,?)";
			try {
				PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
				try {
					preparedStatement.setInt(1, vehicleBuffer.m_wx);
					preparedStatement.setInt(2, vehicleBuffer.m_wy);
					preparedStatement.setFloat(3, vehicleBuffer.m_x);
					preparedStatement.setFloat(4, vehicleBuffer.m_y);
					preparedStatement.setInt(5, vehicleBuffer.m_WorldVersion);
					ByteBuffer byteBuffer = vehicleBuffer.m_bb;
					byteBuffer.rewind();
					preparedStatement.setBinaryStream(6, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
					preparedStatement.setInt(7, vehicleBuffer.m_id);
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
				this.rollback();
				throw exception;
			}
		}

		void updateDB(VehiclesDB2.VehicleBuffer vehicleBuffer) throws SQLException {
			String string = "UPDATE vehicles SET wx = ?, wy = ?, x = ?, y = ?, worldversion = ?, data = ? WHERE id=?";
			try {
				PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
				try {
					preparedStatement.setInt(1, vehicleBuffer.m_wx);
					preparedStatement.setInt(2, vehicleBuffer.m_wy);
					preparedStatement.setFloat(3, vehicleBuffer.m_x);
					preparedStatement.setFloat(4, vehicleBuffer.m_y);
					preparedStatement.setInt(5, vehicleBuffer.m_WorldVersion);
					ByteBuffer byteBuffer = vehicleBuffer.m_bb;
					byteBuffer.rewind();
					preparedStatement.setBinaryStream(6, new ByteBufferBackedInputStream(byteBuffer), byteBuffer.remaining());
					preparedStatement.setInt(7, vehicleBuffer.m_id);
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
				this.rollback();
				throw exception;
			}
		}

		void removeVehicle(int int1) {
			if (this.m_conn != null && int1 >= 1) {
				String string = "DELETE FROM vehicles WHERE id=?";
				try {
					PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
					try {
						preparedStatement.setInt(1, int1);
						int int2 = preparedStatement.executeUpdate();
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
					ExceptionLogger.logException(exception);
					this.rollback();
				}
			}
		}

		void create() {
			String string = ZomboidFileSystem.instance.getCurrentSaveDir();
			File file = new File(string);
			if (!file.exists()) {
				file.mkdirs();
			}

			File file2 = new File(string + File.separator + "vehicles.db");
			file2.setReadable(true, false);
			file2.setExecutable(true, false);
			file2.setWritable(true, false);
			Statement statement;
			if (!file2.exists()) {
				try {
					file2.createNewFile();
					this.m_conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
					statement = this.m_conn.createStatement();
					statement.executeUpdate("CREATE TABLE vehicles (id   INTEGER PRIMARY KEY NOT NULL,wx	INTEGER,wy	INTEGER,x	FLOAT,y	FLOAT,worldversion	INTEGER,data BLOB);");
					statement.executeUpdate("CREATE INDEX ivwx ON vehicles (wx);");
					statement.executeUpdate("CREATE INDEX ivwy ON vehicles (wy);");
					statement.close();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					DebugLog.log("failed to create vehicles database");
					System.exit(1);
				}
			}

			if (this.m_conn == null) {
				try {
					this.m_conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
				} catch (Exception exception2) {
					DebugLog.log("failed to create vehicles database");
					ExceptionLogger.logException(exception2);
					System.exit(1);
				}
			}

			try {
				statement = this.m_conn.createStatement();
				statement.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
				statement.close();
			} catch (Exception exception3) {
				ExceptionLogger.logException(exception3);
				System.exit(1);
			}

			try {
				this.m_conn.setAutoCommit(false);
			} catch (SQLException sQLException) {
				ExceptionLogger.logException(sQLException);
			}
		}

		private String searchPathForSqliteLib(String string) {
			String[] stringArray = System.getProperty("java.library.path", "").split(File.pathSeparator);
			int int1 = stringArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				String string2 = stringArray[int2];
				File file = new File(string2, string);
				if (file.exists()) {
					return string2;
				}
			}

			return "";
		}

		void initUsedIDs(TIntHashSet tIntHashSet, TIntHashSet tIntHashSet2) throws SQLException {
			String string = "SELECT wx,wy,id FROM vehicles";
			PreparedStatement preparedStatement = this.m_conn.prepareStatement(string);
			try {
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					int int1 = resultSet.getInt(1);
					int int2 = resultSet.getInt(2);
					tIntHashSet2.add(int2 << 16 | int1);
					tIntHashSet.add(resultSet.getInt(3));
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

		private void rollback() {
			if (this.m_conn != null) {
				try {
					this.m_conn.rollback();
				} catch (SQLException sQLException) {
					ExceptionLogger.logException(sQLException);
				}
			}
		}
	}

	public interface IImportPlayerFromOldDB {

		void accept(int int1, String string, int int2, int int3, float float1, float float2, float float3, int int4, byte[] byteArray, boolean boolean1);
	}

	private static final class QueueUpdateVehicle extends VehiclesDB2.QueueItem {
		static final Pool s_pool = new Pool(VehiclesDB2.QueueUpdateVehicle::new);
		final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

		void init(BaseVehicle baseVehicle) throws IOException {
			this.m_vehicleBuffer.set(baseVehicle);
		}

		void processMain() {
		}

		void processWorldStreamer() {
			VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
		}
	}

	private static class QueueRemoveVehicle extends VehiclesDB2.QueueItem {
		static final Pool s_pool = new Pool(VehiclesDB2.QueueRemoveVehicle::new);
		int m_id;

		void init(BaseVehicle baseVehicle) {
			this.m_id = baseVehicle.sqlID;
		}

		void processMain() {
		}

		void processWorldStreamer() {
			VehiclesDB2.instance.m_worldStreamer.m_store.removeVehicle(this.m_id);
		}
	}

	private static class QueueLoadChunk extends VehiclesDB2.QueueItem {
		static final Pool s_pool = new Pool(VehiclesDB2.QueueLoadChunk::new);
		int m_wx;
		int m_wy;
		final ArrayList m_vehicles = new ArrayList();
		IsoGridSquare m_dummySquare;

		void init(int int1, int int2) {
			this.m_wx = int1;
			this.m_wy = int2;
			this.m_vehicles.clear();
			if (this.m_dummySquare == null) {
				this.m_dummySquare = IsoGridSquare.getNew(IsoWorld.instance.CurrentCell, (SliceY)null, 0, 0, 0);
			}
		}

		void processMain() {
			IsoChunk chunk = ServerMap.instance.getChunk(this.m_wx, this.m_wy);
			if (chunk == null) {
				this.m_vehicles.clear();
			} else if (chunk.m_loadVehiclesObject != this) {
				this.m_vehicles.clear();
			} else {
				chunk.m_loadVehiclesObject = null;
				for (int int1 = 0; int1 < this.m_vehicles.size(); ++int1) {
					BaseVehicle baseVehicle = (BaseVehicle)this.m_vehicles.get(int1);
					IsoGridSquare square = chunk.getGridSquare((int)(baseVehicle.x - (float)(chunk.wx * 10)), (int)(baseVehicle.y - (float)(chunk.wy * 10)), 0);
					baseVehicle.setSquare(square);
					baseVehicle.setCurrent(square);
					baseVehicle.chunk = chunk;
					if (chunk.jobType == IsoChunk.JobType.SoftReset) {
						baseVehicle.softReset();
					}

					if (!baseVehicle.addedToWorld && VehiclesDB2.instance.isVehicleLoaded(baseVehicle)) {
						baseVehicle.removeFromSquare();
						this.m_vehicles.remove(int1);
						--int1;
					} else {
						chunk.vehicles.add(baseVehicle);
						if (!baseVehicle.addedToWorld) {
							baseVehicle.addToWorld();
						}
					}
				}

				this.m_vehicles.clear();
			}
		}

		void processWorldStreamer() {
			try {
				VehiclesDB2.instance.m_worldStreamer.m_store.loadChunk(this.m_wx, this.m_wy, this::vehicleLoaded);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		void vehicleLoaded(VehiclesDB2.VehicleBuffer vehicleBuffer) throws IOException {
			assert vehicleBuffer.m_id >= 1;
			int int1 = (int)(vehicleBuffer.m_x - (float)(this.m_wx * 10));
			int int2 = (int)(vehicleBuffer.m_y - (float)(this.m_wy * 10));
			this.m_dummySquare.x = int1;
			this.m_dummySquare.y = int2;
			IsoGridSquare square = this.m_dummySquare;
			BaseVehicle baseVehicle = new BaseVehicle(IsoWorld.instance.CurrentCell);
			baseVehicle.setSquare(square);
			baseVehicle.setCurrent(square);
			try {
				baseVehicle.load(vehicleBuffer.m_bb, vehicleBuffer.m_WorldVersion);
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				DebugLog.General.error("vehicle %d is being deleted because an error occurred loading it", vehicleBuffer.m_id);
				VehiclesDB2.instance.m_worldStreamer.m_store.removeVehicle(vehicleBuffer.m_id);
				return;
			}

			baseVehicle.sqlID = vehicleBuffer.m_id;
			this.m_vehicles.add(baseVehicle);
		}
	}

	private static final class QueueAddVehicle extends VehiclesDB2.QueueItem {
		static final Pool s_pool = new Pool(VehiclesDB2.QueueAddVehicle::new);
		final VehiclesDB2.VehicleBuffer m_vehicleBuffer = new VehiclesDB2.VehicleBuffer();

		void init(BaseVehicle baseVehicle) throws IOException {
			this.m_vehicleBuffer.set(baseVehicle);
		}

		void processMain() {
		}

		void processWorldStreamer() {
			VehiclesDB2.instance.m_worldStreamer.m_store.updateVehicle(this.m_vehicleBuffer);
		}
	}

	private static class MemoryStore extends VehiclesDB2.IVehicleStore {
		final TIntObjectHashMap m_IDToVehicle = new TIntObjectHashMap();
		final TIntObjectHashMap m_ChunkToVehicles = new TIntObjectHashMap();

		void init(TIntHashSet tIntHashSet, TIntHashSet tIntHashSet2) {
			tIntHashSet.clear();
			tIntHashSet2.clear();
		}

		void Reset() {
			this.m_IDToVehicle.clear();
			this.m_ChunkToVehicles.clear();
		}

		void loadChunk(IsoChunk chunk, VehiclesDB2.ThrowingBiConsumer throwingBiConsumer) throws IOException {
			int int1 = chunk.wy << 16 | chunk.wx;
			ArrayList arrayList = (ArrayList)this.m_ChunkToVehicles.get(int1);
			if (arrayList != null) {
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					VehiclesDB2.VehicleBuffer vehicleBuffer = (VehiclesDB2.VehicleBuffer)arrayList.get(int2);
					vehicleBuffer.m_bb.rewind();
					boolean boolean1 = vehicleBuffer.m_bb.get() == 1;
					int int3 = vehicleBuffer.m_bb.getInt();
					throwingBiConsumer.accept(chunk, vehicleBuffer);
				}
			}
		}

		void loadChunk(int int1, int int2, VehiclesDB2.ThrowingConsumer throwingConsumer) throws IOException {
			int int3 = int2 << 16 | int1;
			ArrayList arrayList = (ArrayList)this.m_ChunkToVehicles.get(int3);
			if (arrayList != null) {
				for (int int4 = 0; int4 < arrayList.size(); ++int4) {
					VehiclesDB2.VehicleBuffer vehicleBuffer = (VehiclesDB2.VehicleBuffer)arrayList.get(int4);
					vehicleBuffer.m_bb.rewind();
					boolean boolean1 = vehicleBuffer.m_bb.get() == 1;
					int int5 = vehicleBuffer.m_bb.getInt();
					throwingConsumer.accept(vehicleBuffer);
				}
			}
		}

		void updateVehicle(VehiclesDB2.VehicleBuffer vehicleBuffer) {
			assert vehicleBuffer.m_id >= 1;
			synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
				assert VehiclesDB2.instance.m_main.m_usedIDs.contains(vehicleBuffer.m_id);
			}
			vehicleBuffer.m_bb.rewind();
			VehiclesDB2.VehicleBuffer vehicleBuffer2 = (VehiclesDB2.VehicleBuffer)this.m_IDToVehicle.get(vehicleBuffer.m_id);
			int int1;
			if (vehicleBuffer2 == null) {
				vehicleBuffer2 = new VehiclesDB2.VehicleBuffer();
				vehicleBuffer2.m_id = vehicleBuffer.m_id;
				this.m_IDToVehicle.put(vehicleBuffer.m_id, vehicleBuffer2);
			} else {
				int1 = vehicleBuffer2.m_wy << 16 | vehicleBuffer2.m_wx;
				((ArrayList)this.m_ChunkToVehicles.get(int1)).remove(vehicleBuffer2);
			}

			vehicleBuffer2.m_wx = vehicleBuffer.m_wx;
			vehicleBuffer2.m_wy = vehicleBuffer.m_wy;
			vehicleBuffer2.m_x = vehicleBuffer.m_x;
			vehicleBuffer2.m_y = vehicleBuffer.m_y;
			vehicleBuffer2.m_WorldVersion = vehicleBuffer.m_WorldVersion;
			vehicleBuffer2.setBytes(vehicleBuffer.m_bb);
			int1 = vehicleBuffer2.m_wy << 16 | vehicleBuffer2.m_wx;
			if (this.m_ChunkToVehicles.get(int1) == null) {
				this.m_ChunkToVehicles.put(int1, new ArrayList());
			}

			((ArrayList)this.m_ChunkToVehicles.get(int1)).add(vehicleBuffer2);
		}

		void removeVehicle(int int1) {
			VehiclesDB2.VehicleBuffer vehicleBuffer = (VehiclesDB2.VehicleBuffer)this.m_IDToVehicle.remove(int1);
			if (vehicleBuffer != null) {
				int int2 = vehicleBuffer.m_wy << 16 | vehicleBuffer.m_wx;
				((ArrayList)this.m_ChunkToVehicles.get(int2)).remove(vehicleBuffer);
			}
		}
	}

	@FunctionalInterface
	public interface ThrowingBiConsumer {

		void accept(Object object, Object object2) throws Exception;
	}

	@FunctionalInterface
	public interface ThrowingConsumer {

		void accept(Object object) throws Exception;
	}

	private static final class VehicleBuffer {
		int m_id = -1;
		int m_wx;
		int m_wy;
		float m_x;
		float m_y;
		int m_WorldVersion;
		ByteBuffer m_bb = ByteBuffer.allocate(32768);

		void set(BaseVehicle baseVehicle) throws IOException {
			assert baseVehicle.sqlID >= 1;
			synchronized (VehiclesDB2.instance.m_main.m_usedIDs) {
				assert VehiclesDB2.instance.m_main.m_usedIDs.contains(baseVehicle.sqlID);
			}
			this.m_id = baseVehicle.sqlID;
			this.m_wx = baseVehicle.chunk.wx;
			this.m_wy = baseVehicle.chunk.wy;
			this.m_x = baseVehicle.getX();
			this.m_y = baseVehicle.getY();
			this.m_WorldVersion = IsoWorld.getWorldVersion();
			ByteBuffer byteBuffer = (ByteBuffer)VehiclesDB2.TL_SliceBuffer.get();
			byteBuffer.clear();
			while (true) {
				try {
					baseVehicle.save(byteBuffer);
					break;
				} catch (BufferOverflowException bufferOverflowException) {
					if (byteBuffer.capacity() >= 2097152) {
						DebugLog.General.error("the vehicle %d cannot be saved", baseVehicle.sqlID);
						throw bufferOverflowException;
					}

					byteBuffer = ByteBuffer.allocate(byteBuffer.capacity() + '耀');
					VehiclesDB2.TL_SliceBuffer.set(byteBuffer);
				}
			}

			byteBuffer.flip();
			this.setBytes(byteBuffer);
		}

		void setBytes(ByteBuffer byteBuffer) {
			byteBuffer.rewind();
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
			byteBufferOutputStream.clear();
			byte[] byteArray = (byte[])VehiclesDB2.TL_Bytes.get();
			int int1;
			for (int int2 = byteBuffer.limit(); int2 > 0; int2 -= int1) {
				int1 = Math.min(byteArray.length, int2);
				byteBuffer.get(byteArray, 0, int1);
				byteBufferOutputStream.write(byteArray, 0, int1);
			}

			byteBufferOutputStream.flip();
			this.m_bb = byteBufferOutputStream.getWrappedBuffer();
		}

		void setBytes(byte[] byteArray) {
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
			byteBufferOutputStream.clear();
			byteBufferOutputStream.write(byteArray);
			byteBufferOutputStream.flip();
			this.m_bb = byteBufferOutputStream.getWrappedBuffer();
		}

		void setBytes(InputStream inputStream) throws IOException {
			ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream(this.m_bb, true);
			byteBufferOutputStream.clear();
			byte[] byteArray = (byte[])VehiclesDB2.TL_Bytes.get();
			while (true) {
				int int1 = inputStream.read(byteArray);
				if (int1 < 1) {
					byteBufferOutputStream.flip();
					this.m_bb = byteBufferOutputStream.getWrappedBuffer();
					return;
				}

				byteBufferOutputStream.write(byteArray, 0, int1);
			}
		}
	}
}
