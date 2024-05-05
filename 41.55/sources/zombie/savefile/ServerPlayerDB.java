package zombie.savefile;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoCell;
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
				String string = "SELECT id FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
				String string2 = "INSERT INTO networkPlayers(world,username,playerIndex,name,x,y,z,worldversion,isDead,data) VALUES(?,?,?,?,?,?,?,?,?,?)";
				String string3 = "UPDATE networkPlayers SET x=?, y=?, z=?, worldversion = ?, isDead = ?, data = ? WHERE id=?";
				try {
					PreparedStatement preparedStatement = this.conn.prepareStatement(string);
					PreparedStatement preparedStatement2;
					int int1;
					label99: {
						try {
							preparedStatement.setString(1, networkCharacterData.username);
							preparedStatement.setString(2, Core.GameSaveWorld);
							preparedStatement.setInt(3, networkCharacterData.playerIndex);
							ResultSet resultSet = preparedStatement.executeQuery();
							if (!resultSet.next()) {
								break label99;
							}

							int int2 = resultSet.getInt(1);
							preparedStatement2 = this.conn.prepareStatement(string3);
							try {
								preparedStatement2.setFloat(1, networkCharacterData.x);
								preparedStatement2.setFloat(2, networkCharacterData.y);
								preparedStatement2.setFloat(3, networkCharacterData.z);
								preparedStatement2.setInt(4, networkCharacterData.worldVersion);
								preparedStatement2.setBoolean(5, networkCharacterData.isDead);
								preparedStatement2.setBytes(6, networkCharacterData.buffer);
								preparedStatement2.setInt(7, int2);
								int1 = preparedStatement2.executeUpdate();
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

					ByteBuffer byteBuffer = ByteBuffer.allocate(networkCharacterData.buffer.length);
					byteBuffer.rewind();
					byteBuffer.put(networkCharacterData.buffer);
					byteBuffer.rewind();
					IsoPlayer player = new IsoPlayer(IsoCell.getInstance());
					player.load(byteBuffer, networkCharacterData.worldVersion);
					String string4 = player.getDescriptor().getForename();
					String string5 = string4 + " " + player.getDescriptor().getSurname();
					preparedStatement2 = this.conn.prepareStatement(string2);
					try {
						preparedStatement2.setString(1, Core.GameSaveWorld);
						preparedStatement2.setString(2, networkCharacterData.username);
						preparedStatement2.setInt(3, networkCharacterData.playerIndex);
						preparedStatement2.setString(4, string5);
						preparedStatement2.setFloat(5, networkCharacterData.x);
						preparedStatement2.setFloat(6, networkCharacterData.y);
						preparedStatement2.setFloat(7, networkCharacterData.z);
						preparedStatement2.setInt(8, networkCharacterData.worldVersion);
						preparedStatement2.setBoolean(9, networkCharacterData.isDead);
						preparedStatement2.setBytes(10, networkCharacterData.buffer);
						int1 = preparedStatement2.executeUpdate();
						this.conn.commit();
					} catch (Throwable throwable5) {
						if (preparedStatement2 != null) {
							try {
								preparedStatement2.close();
							} catch (Throwable throwable6) {
								throwable5.addSuppressed(throwable6);
							}
						}

						throw throwable5;
					}

					if (preparedStatement2 != null) {
						preparedStatement2.close();
					}
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					PlayerDBHelper.rollback(this.conn);
				}
			}
		}
	}

	public void serverLoadNetworkCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		if (byte1 >= 0 && byte1 < 4) {
			if (this.conn != null) {
				String string = "SELECT id, x, y, z, data, worldversion, isDead FROM networkPlayers WHERE username=? AND world=? AND playerIndex=?";
				try {
					PreparedStatement preparedStatement = this.conn.prepareStatement(string);
					try {
						preparedStatement.setString(1, udpConnection.username);
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
							PacketTypes.doPacket((short)167, byteBufferWriter);
							byteBufferWriter.putByte((byte)1);
							byteBufferWriter.putInt(byte1);
							byteBufferWriter.putFloat(float1);
							byteBufferWriter.putFloat(float2);
							byteBufferWriter.putFloat(float3);
							byteBufferWriter.putInt(int2);
							byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
							byteBufferWriter.putInt(byteArray.length);
							byteBufferWriter.bb.put(byteArray);
							udpConnection.endPacketImmediate();
						} else {
							ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
							PacketTypes.doPacket((short)167, byteBufferWriter2);
							byteBufferWriter2.putByte((byte)0);
							byteBufferWriter2.putInt(byte1);
							udpConnection.endPacketImmediate();
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
		}
	}

	private static final class NetworkCharacterData {
		byte[] buffer;
		String username;
		int playerIndex;
		float x;
		float y;
		float z;
		boolean isDead;
		int worldVersion;

		public NetworkCharacterData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
			this.playerIndex = byteBuffer.get();
			int int1 = byteBuffer.getInt();
			this.x = byteBuffer.getFloat();
			this.y = byteBuffer.getFloat();
			this.z = byteBuffer.getFloat();
			this.isDead = byteBuffer.get() == 1;
			this.worldVersion = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			this.buffer = new byte[int2];
			byteBuffer.get(this.buffer);
			this.username = udpConnection.username;
		}
	}
}
