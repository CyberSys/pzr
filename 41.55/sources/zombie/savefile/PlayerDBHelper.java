package zombie.savefile;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;
import zombie.vehicles.VehicleDBHelper;


public final class PlayerDBHelper {

	public static Connection create() {
		Connection connection = null;
		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string2 = string + Core.GameSaveWorld;
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdirs();
		}

		File file2 = new File(string2 + File.separator + "players.db");
		file2.setReadable(true, false);
		file2.setExecutable(true, false);
		file2.setWritable(true, false);
		Statement statement;
		if (!file2.exists()) {
			try {
				file2.createNewFile();
				connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
				statement = connection.createStatement();
				statement.executeUpdate("CREATE TABLE localPlayers (id   INTEGER PRIMARY KEY NOT NULL,name STRING,wx	INTEGER,wy	INTEGER,x	FLOAT,y	FLOAT,z	FLOAT,worldversion	INTEGER,data BLOB,isDead BOOLEAN);");
				statement.executeUpdate("CREATE TABLE networkPlayers (id   INTEGER PRIMARY KEY NOT NULL,world TEXT,username TEXT,playerIndex   INTEGER,name STRING,x	FLOAT,y	FLOAT,z	FLOAT,worldversion	INTEGER,data BLOB,isDead BOOLEAN);");
				statement.executeUpdate("CREATE INDEX inpusername ON networkPlayers (username);");
				statement.close();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
				DebugLog.log("failed to create players database");
				System.exit(1);
			}
		}

		if (connection == null) {
			try {
				connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
			} catch (Exception exception2) {
				ExceptionLogger.logException(exception2);
				DebugLog.log("failed to create players database");
				System.exit(1);
			}
		}

		try {
			statement = connection.createStatement();
			statement.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
			statement.close();
		} catch (Exception exception3) {
			ExceptionLogger.logException(exception3);
			DebugLog.log("failed to config players.db");
			System.exit(1);
		}

		try {
			connection.setAutoCommit(false);
		} catch (SQLException sQLException) {
			DebugLog.log("failed to setAutoCommit for players.db");
		}

		return connection;
	}

	public static void rollback(Connection connection) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException sQLException) {
				ExceptionLogger.logException(sQLException);
			}
		}
	}

	public static boolean isPlayerAlive(String string, int int1) {
		if (Core.getInstance().isNoSave()) {
			return false;
		} else {
			File file = new File(string + File.separator + "map_p.bin");
			if (file.exists()) {
				return true;
			} else if (VehicleDBHelper.isPlayerAlive(string, int1)) {
				return true;
			} else if (int1 == -1) {
				return false;
			} else {
				try {
					File file2 = new File(string + File.separator + "players.db");
					if (!file2.exists()) {
						return false;
					} else {
						file2.setReadable(true, false);
						Connection connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
						boolean boolean1;
						label117: {
							try {
								String string2 = "SELECT isDead FROM localPlayers WHERE id=?";
								PreparedStatement preparedStatement = connection.prepareStatement(string2);
								label104: {
									try {
										preparedStatement.setInt(1, int1);
										ResultSet resultSet = preparedStatement.executeQuery();
										if (!resultSet.next()) {
											break label104;
										}

										boolean1 = !resultSet.getBoolean(1);
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

									break label117;
								}

								if (preparedStatement != null) {
									preparedStatement.close();
								}
							} catch (Throwable throwable3) {
								if (connection != null) {
									try {
										connection.close();
									} catch (Throwable throwable4) {
										throwable3.addSuppressed(throwable4);
									}
								}

								throw throwable3;
							}

							if (connection != null) {
								connection.close();
							}

							return false;
						}

						if (connection != null) {
							connection.close();
						}

						return boolean1;
					}
				} catch (Throwable throwable5) {
					ExceptionLogger.logException(throwable5);
					return false;
				}
			}
		}
	}

	public static ArrayList getPlayers(String string) throws SQLException {
		ArrayList arrayList = new ArrayList();
		if (Core.getInstance().isNoSave()) {
			return arrayList;
		} else {
			File file = new File(string + File.separator + "players.db");
			if (!file.exists()) {
				return arrayList;
			} else {
				file.setReadable(true, false);
				Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath());
				try {
					String string2 = "SELECT id, name, isDead FROM localPlayers";
					PreparedStatement preparedStatement = connection.prepareStatement(string2);
					try {
						ResultSet resultSet = preparedStatement.executeQuery();
						while (resultSet.next()) {
							int int1 = resultSet.getInt(1);
							String string3 = resultSet.getString(2);
							boolean boolean1 = resultSet.getBoolean(3);
							arrayList.add(BoxedStaticValues.toDouble((double)int1));
							arrayList.add(string3);
							arrayList.add(boolean1 ? Boolean.TRUE : Boolean.FALSE);
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
				} catch (Throwable throwable3) {
					if (connection != null) {
						try {
							connection.close();
						} catch (Throwable throwable4) {
							throwable3.addSuppressed(throwable4);
						}
					}

					throw throwable3;
				}

				if (connection != null) {
					connection.close();
				}

				return arrayList;
			}
		}
	}

	public static void setPlayer1(String string, int int1) throws SQLException {
		if (!Core.getInstance().isNoSave()) {
			if (int1 != 1) {
				File file = new File(string + File.separator + "players.db");
				if (file.exists()) {
					file.setReadable(true, false);
					Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath());
					label182: {
						label183: {
							label184: {
								try {
									boolean boolean1 = false;
									boolean boolean2 = false;
									int int2 = -1;
									int int3 = -1;
									String string2 = "SELECT id FROM localPlayers";
									PreparedStatement preparedStatement = connection.prepareStatement(string2);
									int int4;
									try {
										for (ResultSet resultSet = preparedStatement.executeQuery(); resultSet.next(); int3 = Math.max(int3, int4)) {
											int4 = resultSet.getInt(1);
											if (int4 == 1) {
												boolean1 = true;
											} else if (int2 == -1 || int2 > int4) {
												int2 = int4;
											}

											if (int4 == int1) {
												boolean2 = true;
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

									if (int1 <= 0) {
										if (!boolean1) {
											break label182;
										}

										string2 = "UPDATE localPlayers SET id=? WHERE id=?";
										preparedStatement = connection.prepareStatement(string2);
										try {
											preparedStatement.setInt(1, int3 + 1);
											preparedStatement.setInt(2, 1);
											preparedStatement.executeUpdate();
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

										break label183;
									}

									if (!boolean2) {
										break label184;
									}

									if (boolean1) {
										string2 = "UPDATE localPlayers SET id=? WHERE id=?";
										preparedStatement = connection.prepareStatement(string2);
										try {
											preparedStatement.setInt(1, int3 + 1);
											preparedStatement.setInt(2, 1);
											preparedStatement.executeUpdate();
											preparedStatement.setInt(1, 1);
											preparedStatement.setInt(2, int1);
											preparedStatement.executeUpdate();
											preparedStatement.setInt(1, int1);
											preparedStatement.setInt(2, int3 + 1);
											preparedStatement.executeUpdate();
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
									} else {
										string2 = "UPDATE localPlayers SET id=? WHERE id=?";
										preparedStatement = connection.prepareStatement(string2);
										try {
											preparedStatement.setInt(1, 1);
											preparedStatement.setInt(2, int1);
											preparedStatement.executeUpdate();
										} catch (Throwable throwable7) {
											if (preparedStatement != null) {
												try {
													preparedStatement.close();
												} catch (Throwable throwable8) {
													throwable7.addSuppressed(throwable8);
												}
											}

											throw throwable7;
										}

										if (preparedStatement != null) {
											preparedStatement.close();
										}
									}
								} catch (Throwable throwable9) {
									if (connection != null) {
										try {
											connection.close();
										} catch (Throwable throwable10) {
											throwable9.addSuppressed(throwable10);
										}
									}

									throw throwable9;
								}

								if (connection != null) {
									connection.close();
								}

								return;
							}

							if (connection != null) {
								connection.close();
							}

							return;
						}

						if (connection != null) {
							connection.close();
						}

						return;
					}

					if (connection != null) {
						connection.close();
					}
				}
			}
		}
	}
}
