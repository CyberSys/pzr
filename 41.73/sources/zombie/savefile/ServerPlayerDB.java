package zombie.savefile;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


public final class ServerPlayerDB {
	private static ServerPlayerDB instance = null;
	private static boolean allow = false;
	private Connection conn = null;
	private ConcurrentLinkedQueue CharactersToSave;

	public static void setAllow(boolean boolean1) {
		allow = boolean1;
	}

	public static boolean isAllow() {
		return allow;
	}

	public static synchronized ServerPlayerDB getInstance() {
		if (instance == null && allow) {
			instance = new ServerPlayerDB();
		}

		return instance;
	}

	public static boolean isAvailable() {
		return instance != null;
	}

	public ServerPlayerDB() {
		if (!Core.getInstance().isNoSave()) {
			this.create();
		}
	}

	public void close() {
		instance = null;
		allow = false;
	}

	private void create() {
		this.conn = PlayerDBHelper.create();
		this.CharactersToSave = new ConcurrentLinkedQueue();
		DatabaseMetaData databaseMetaData = null;
		try {
			databaseMetaData = this.conn.getMetaData();
			Statement statement = this.conn.createStatement();
			ResultSet resultSet = databaseMetaData.getColumns((String)null, (String)null, "networkPlayers", "steamid");
			if (!resultSet.next()) {
				statement.executeUpdate("ALTER TABLE \'networkPlayers\' ADD \'steamid\' STRING NULL");
			}

			resultSet.close();
			statement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	public void process() {
		if (!this.CharactersToSave.isEmpty()) {
			for (ServerPlayerDB.NetworkCharacterData networkCharacterData = (ServerPlayerDB.NetworkCharacterData)this.CharactersToSave.poll(); networkCharacterData != null; networkCharacterData = (ServerPlayerDB.NetworkCharacterData)this.CharactersToSave.poll()) {
				this.serverUpdateNetworkCharacterInt(networkCharacterData);
			}
		}
	}

	public void serverUpdateNetworkCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.CharactersToSave.add(new ServerPlayerDB.NetworkCharacterData(byteBuffer, udpConnection));
	}

	private void serverUpdateNetworkCharacterInt(ServerPlayerDB.NetworkCharacterData networkCharacterData) {
		if (networkCharacterData.playerIndex >= 0 && networkCharacterData.playerIndex < 4) {
			if (this.conn != null) {
				String string;
				if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
					string = "SELECT id FROM networkPlayers WHERE steamid=? AND world=? AND playerIndex=?";
				} else {
					string = "SELECT id FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
				}

				String string2 = "INSERT INTO networkPlayers(world,username,steamid, playerIndex,name,x,y,z,worldversion,isDead,data) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
				String string3 = "UPDATE networkPlayers SET x=?, y=?, z=?, worldversion = ?, isDead = ?, data = ?, name = ? WHERE id=?";
				try {
					PreparedStatement preparedStatement = this.conn.prepareStatement(string);
					label117: {
						try {
							if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
								preparedStatement.setString(1, networkCharacterData.steamid);
							} else {
								preparedStatement.setString(1, networkCharacterData.username);
							}

							preparedStatement.setString(2, Core.GameSaveWorld);
							preparedStatement.setInt(3, networkCharacterData.playerIndex);
							ResultSet resultSet = preparedStatement.executeQuery();
							if (!resultSet.next()) {
								break label117;
							}

							int int1 = resultSet.getInt(1);
							PreparedStatement preparedStatement2 = this.conn.prepareStatement(string3);
							try {
								preparedStatement2.setFloat(1, networkCharacterData.x);
								preparedStatement2.setFloat(2, networkCharacterData.y);
								preparedStatement2.setFloat(3, networkCharacterData.z);
								preparedStatement2.setInt(4, networkCharacterData.worldVersion);
								preparedStatement2.setBoolean(5, networkCharacterData.isDead);
								preparedStatement2.setBytes(6, networkCharacterData.buffer);
								preparedStatement2.setString(7, networkCharacterData.playerName);
								preparedStatement2.setInt(8, int1);
								int int2 = preparedStatement2.executeUpdate();
								this.conn.commit();
							} catch (Throwable throwable) {
								if (preparedStatement2 != null) {
									try {
										preparedStatement2.close();
									} catch (Throwable throwable2) {
										throwable.addSuppressed(throwable2);
									}
								}

								throw throwable;
							}

							if (preparedStatement2 != null) {
								preparedStatement2.close();
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

						return;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}

					preparedStatement = this.conn.prepareStatement(string2);
					try {
						preparedStatement.setString(1, Core.GameSaveWorld);
						preparedStatement.setString(2, networkCharacterData.username);
						preparedStatement.setString(3, networkCharacterData.steamid);
						preparedStatement.setInt(4, networkCharacterData.playerIndex);
						preparedStatement.setString(5, networkCharacterData.playerName);
						preparedStatement.setFloat(6, networkCharacterData.x);
						preparedStatement.setFloat(7, networkCharacterData.y);
						preparedStatement.setFloat(8, networkCharacterData.z);
						preparedStatement.setInt(9, networkCharacterData.worldVersion);
						preparedStatement.setBoolean(10, networkCharacterData.isDead);
						preparedStatement.setBytes(11, networkCharacterData.buffer);
						int int3 = preparedStatement.executeUpdate();
						this.conn.commit();
					} catch (Throwable throwable5) {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (Throwable throwable6) {
								throwable5.addSuppressed(throwable6);
							}
						}

						throw throwable5;
					}

					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					PlayerDBHelper.rollback(this.conn);
				}
			}
		}
	}

	private void serverConvertNetworkCharacter(String string, String string2) {
		try {
			String string3 = "UPDATE networkPlayers SET steamid=? WHERE username=? AND world=? AND (steamid is null or steamid = \'\')";
			PreparedStatement preparedStatement = this.conn.prepareStatement(string3);
			try {
				preparedStatement.setString(1, string2);
				preparedStatement.setString(2, string);
				preparedStatement.setString(3, Core.GameSaveWorld);
				int int1 = preparedStatement.executeUpdate();
				if (int1 > 0) {
					DebugLog.General.warn("serverConvertNetworkCharacter: The steamid was set for the \'" + string + "\' for " + int1 + " players. ");
				}

				this.conn.commit();
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
		} catch (SQLException sQLException) {
			ExceptionLogger.logException(sQLException);
		}
	}

	public void serverLoadNetworkCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		if (byte1 >= 0 && byte1 < 4) {
			if (this.conn != null) {
				if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
					this.serverConvertNetworkCharacter(udpConnection.username, udpConnection.idStr);
				}

				String string;
				if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
					string = "SELECT id, x, y, z, data, worldversion, isDead FROM networkPlayers WHERE steamid=? AND world=? AND playerIndex=?";
				} else {
					string = "SELECT id, x, y, z, data, worldversion, isDead FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
				}

				try {
					PreparedStatement preparedStatement = this.conn.prepareStatement(string);
					try {
						if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
							preparedStatement.setString(1, udpConnection.idStr);
						} else {
							preparedStatement.setString(1, udpConnection.username);
						}

						preparedStatement.setString(2, Core.GameSaveWorld);
						preparedStatement.setInt(3, byte1);
						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							int int1 = resultSet.getInt(1);
							float float1 = resultSet.getFloat(2);
							float float2 = resultSet.getFloat(3);
							float float3 = resultSet.getFloat(4);
							byte[] byteArray = resultSet.getBytes(5);
							int int2 = resultSet.getInt(6);
							boolean boolean1 = resultSet.getBoolean(7);
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.PacketType.LoadPlayerProfile.doPacket(byteBufferWriter);
							byteBufferWriter.putByte((byte)1);
							byteBufferWriter.putInt(byte1);
							byteBufferWriter.putFloat(float1);
							byteBufferWriter.putFloat(float2);
							byteBufferWriter.putFloat(float3);
							byteBufferWriter.putInt(int2);
							byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
							byteBufferWriter.putInt(byteArray.length);
							byteBufferWriter.bb.put(byteArray);
							PacketTypes.PacketType.LoadPlayerProfile.send(udpConnection);
						} else {
							ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
							PacketTypes.PacketType.LoadPlayerProfile.doPacket(byteBufferWriter2);
							byteBufferWriter2.putByte((byte)0);
							byteBufferWriter2.putInt(byte1);
							PacketTypes.PacketType.LoadPlayerProfile.send(udpConnection);
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
				} catch (SQLException sQLException) {
					ExceptionLogger.logException(sQLException);
				}
			}
		} else {
			ByteBufferWriter byteBufferWriter3 = udpConnection.startPacket();
			PacketTypes.PacketType.LoadPlayerProfile.doPacket(byteBufferWriter3);
			byteBufferWriter3.putByte((byte)0);
			byteBufferWriter3.putInt(byte1);
			PacketTypes.PacketType.LoadPlayerProfile.send(udpConnection);
		}
	}

	private static final class NetworkCharacterData {
		byte[] buffer;
		String username;
		String steamid;
		int playerIndex;
		String playerName;
		float x;
		float y;
		float z;
		boolean isDead;
		int worldVersion;

		public NetworkCharacterData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
			this.playerIndex = byteBuffer.get();
			this.playerName = GameWindow.ReadStringUTF(byteBuffer);
			this.x = byteBuffer.getFloat();
			this.y = byteBuffer.getFloat();
			this.z = byteBuffer.getFloat();
			this.isDead = byteBuffer.get() == 1;
			this.worldVersion = byteBuffer.getInt();
			int int1 = byteBuffer.getInt();
			this.buffer = new byte[int1];
			byteBuffer.get(this.buffer);
			if (GameServer.bCoop && SteamUtils.isSteamModeEnabled()) {
				this.steamid = udpConnection.idStr;
			} else {
				this.steamid = "";
			}

			this.username = udpConnection.username;
		}
	}
}
