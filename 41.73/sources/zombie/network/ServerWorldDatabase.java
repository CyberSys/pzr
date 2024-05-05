package zombie.network;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.secure.PZcrypt;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;


public class ServerWorldDatabase {
	public static ServerWorldDatabase instance = new ServerWorldDatabase();
	public String CommandLineAdminUsername = "admin";
	public String CommandLineAdminPassword;
	public boolean doAdmin = true;
	public DBSchema dbSchema = null;
	static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
	Connection conn;
	private static final String nullChar = String.valueOf('\u0000');

	public DBSchema getDBSchema() {
		if (this.dbSchema == null) {
			this.dbSchema = new DBSchema(this.conn);
		}

		return this.dbSchema;
	}

	public void executeQuery(String string, KahluaTable kahluaTable) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement(string);
		KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
		int int1 = 1;
		while (kahluaTableIterator.advance()) {
			preparedStatement.setString(int1++, (String)kahluaTableIterator.getValue());
		}

		preparedStatement.executeUpdate();
	}

	public ArrayList getTableResult(String string) throws SQLException {
		ArrayList arrayList = new ArrayList();
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM " + string);
		ResultSet resultSet = preparedStatement.executeQuery();
		DatabaseMetaData databaseMetaData = this.conn.getMetaData();
		ResultSet resultSet2 = databaseMetaData.getColumns((String)null, (String)null, string, (String)null);
		ArrayList arrayList2 = new ArrayList();
		DBResult dBResult = new DBResult();
		while (resultSet2.next()) {
			String string2 = resultSet2.getString(4);
			if (!string2.equals("world") && !string2.equals("moderator") && !string2.equals("admin") && !string2.equals("password") && !string2.equals("encryptedPwd") && !string2.equals("pwdEncryptType") && !string2.equals("transactionID")) {
				arrayList2.add(string2);
			}
		}

		dBResult.setColumns(arrayList2);
		dBResult.setTableName(string);
		while (resultSet.next()) {
			for (int int1 = 0; int1 < arrayList2.size(); ++int1) {
				String string3 = (String)arrayList2.get(int1);
				String string4 = resultSet.getString(string3);
				if ("\'false\'".equals(string4)) {
					string4 = "false";
				}

				if ("\'true\'".equals(string4)) {
					string4 = "true";
				}

				if (string4 == null) {
					string4 = "";
				}

				dBResult.getValues().put(string3, string4);
			}

			arrayList.add(dBResult);
			dBResult = new DBResult();
			dBResult.setColumns(arrayList2);
			dBResult.setTableName(string);
		}

		preparedStatement.close();
		return arrayList;
	}

	public void saveAllTransactionsID(HashMap hashMap) {
		try {
			Iterator iterator = hashMap.keySet().iterator();
			PreparedStatement preparedStatement = null;
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				Integer integer = (Integer)hashMap.get(string);
				preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET transactionID = ? WHERE username = ?");
				preparedStatement.setString(1, integer.toString());
				preparedStatement.setString(2, string);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void saveTransactionID(String string, Integer integer) {
		try {
			if (!this.containsUser(string)) {
				this.addUser(string, "");
			}

			PreparedStatement preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET transactionID = ? WHERE username = ?");
			preparedStatement.setString(1, integer.toString());
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean containsUser(String string) {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, Core.GameSaveWorld);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				preparedStatement.close();
				return true;
			}

			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return false;
	}

	public boolean containsCaseinsensitiveUser(String string) {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE LOWER(username) = LOWER(?) AND world = ?");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, Core.GameSaveWorld);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				preparedStatement.close();
				return true;
			}

			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return false;
	}

	public String changeUsername(String string, String string2) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			String string3 = resultSet.getString("id");
			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET username = ? WHERE id = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string3);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return "Changed " + string + " user\'s name into " + string2;
		} else {
			return !ServerOptions.instance.getBoolean("Open") ? "User \"" + string + "\" is not in the whitelist, use /adduser first" : "Changed\'s name " + string + " into " + string2;
		}
	}

	public String addUser(String string, String string2) throws SQLException {
		if (this.containsCaseinsensitiveUser(string)) {
			return "A user with this name already exists";
		} else {
			try {
				PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
				preparedStatement.setString(1, string);
				preparedStatement.setString(2, Core.GameSaveWorld);
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					preparedStatement.close();
					return "User " + string + " already exist.";
				}

				preparedStatement.close();
				preparedStatement = this.conn.prepareStatement("INSERT INTO whitelist (world, username, password, encryptedPwd, pwdEncryptType) VALUES (?, ?, ?, \'true\', \'2\')");
				preparedStatement.setString(1, Core.GameSaveWorld);
				preparedStatement.setString(2, string);
				preparedStatement.setString(3, string2);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			} catch (SQLException sQLException) {
				sQLException.printStackTrace();
			}

			return "User " + string + " created with the password " + string2;
		}
	}

	public void updateDisplayName(String string, String string2) {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, Core.GameSaveWorld);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				preparedStatement.close();
				preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET displayName = ? WHERE username = ?");
				preparedStatement.setString(1, string2);
				preparedStatement.setString(2, string);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}

			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	public String getDisplayName(String string) {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, Core.GameSaveWorld);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String string2 = resultSet.getString("displayName");
				preparedStatement.close();
				return string2;
			}

			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return null;
	}

	public String removeUser(String string) throws SQLException {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("DELETE FROM whitelist WHERE world = ? and username = ?");
			preparedStatement.setString(1, Core.GameSaveWorld);
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return "User " + string + " removed from white list";
	}

	public void removeUserLog(String string, String string2, String string3) throws SQLException {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("DELETE FROM userlog WHERE username = ? AND type = ? AND text = ?");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, string2);
			preparedStatement.setString(3, string3);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	public void create() throws SQLException, ClassNotFoundException {
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "db");
		if (!file.exists()) {
			file.mkdirs();
		}

		string = ZomboidFileSystem.instance.getCacheDir();
		File file2 = new File(string + File.separator + "db" + File.separator + GameServer.ServerName + ".db");
		file2.setReadable(true, false);
		file2.setExecutable(true, false);
		file2.setWritable(true, false);
		DebugLog.log("user database \"" + file2.getPath() + "\"");
		if (!file2.exists()) {
			try {
				file2.createNewFile();
				this.conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
				Statement statement = this.conn.createStatement();
				statement.executeUpdate("CREATE TABLE [whitelist] ([id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[world] TEXT DEFAULT \'" + GameServer.ServerName + "\' NULL,[username] TEXT  NULL,[password] TEXT  NULL, [admin] BOOLEAN DEFAULT false NULL, [moderator] BOOLEAN DEFAULT false NULL, [banned] BOOLEAN DEFAULT false NULL, [priority] BOOLEAN DEFAULT false NULL,  [lastConnection] TEXT NULL)");
				statement.executeUpdate("CREATE UNIQUE INDEX [id] ON [whitelist]([id]  ASC)");
				statement.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
				statement.executeUpdate("CREATE TABLE [bannedip] ([ip] TEXT NOT NULL,[username] TEXT NULL, [reason] TEXT NULL)");
				statement.close();
			} catch (Exception exception) {
				exception.printStackTrace();
				DebugLog.log("failed to create user database, server shut down");
				System.exit(1);
			}
		}

		if (this.conn == null) {
			try {
				this.conn = PZSQLUtils.getConnection(file2.getAbsolutePath());
			} catch (Exception exception2) {
				exception2.printStackTrace();
				DebugLog.log("failed to open user database, server shut down");
				System.exit(1);
			}
		}

		DatabaseMetaData databaseMetaData = this.conn.getMetaData();
		ResultSet resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "admin");
		Statement statement2 = this.conn.createStatement();
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'admin\' BOOLEAN NULL DEFAULT false");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "moderator");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'moderator\' BOOLEAN NULL DEFAULT false");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "banned");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'banned\' BOOLEAN NULL DEFAULT false");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "priority");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'priority\' BOOLEAN NULL DEFAULT false");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "lastConnection");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'lastConnection\' TEXT NULL");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "encryptedPwd");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'encryptedPwd\' BOOLEAN NULL DEFAULT false");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "pwdEncryptType");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'pwdEncryptType\' INTEGER NULL DEFAULT 1");
		}

		resultSet.close();
		if (SteamUtils.isSteamModeEnabled()) {
			resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "steamid");
			if (!resultSet.next()) {
				statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'steamid\' TEXT NULL");
			}

			resultSet.close();
			resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "ownerid");
			if (!resultSet.next()) {
				statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'ownerid\' TEXT NULL");
			}

			resultSet.close();
		}

		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "accesslevel");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'accesslevel\' TEXT NULL");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "transactionID");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'transactionID\' INTEGER NULL");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "displayName");
		if (!resultSet.next()) {
			statement2.executeUpdate("ALTER TABLE \'whitelist\' ADD \'displayName\' TEXT NULL");
		}

		resultSet.close();
		resultSet = statement2.executeQuery("SELECT * FROM sqlite_master WHERE type = \'index\' AND sql LIKE \'%UNIQUE%\' and name = \'username\'");
		if (!resultSet.next()) {
			try {
				statement2.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
			} catch (Exception exception3) {
				System.out.println("Can\'t create the username index because some of the username in the database are in double, will drop the double username.");
				statement2.executeUpdate("DELETE FROM whitelist WHERE whitelist.rowid > (SELECT rowid FROM whitelist dbl WHERE whitelist.rowid <> dbl.rowid AND  whitelist.username = dbl.username);");
				statement2.executeUpdate("CREATE UNIQUE INDEX [username] ON [whitelist]([username]  ASC)");
			}
		}

		resultSet = databaseMetaData.getTables((String)null, (String)null, "bannedip", (String[])null);
		if (!resultSet.next()) {
			statement2.executeUpdate("CREATE TABLE [bannedip] ([ip] TEXT NOT NULL,[username] TEXT NULL, [reason] TEXT NULL)");
		}

		resultSet.close();
		resultSet = databaseMetaData.getTables((String)null, (String)null, "bannedid", (String[])null);
		if (!resultSet.next()) {
			statement2.executeUpdate("CREATE TABLE [bannedid] ([steamid] TEXT NOT NULL, [reason] TEXT NULL)");
		}

		resultSet.close();
		resultSet = databaseMetaData.getTables((String)null, (String)null, "userlog", (String[])null);
		if (!resultSet.next()) {
			statement2.executeUpdate("CREATE TABLE [userlog] ([id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,[username] TEXT  NULL,[type] TEXT  NULL, [text] TEXT  NULL, [issuedBy] TEXT  NULL, [amount] INTEGER NULL)");
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "moderator");
		if (resultSet.next()) {
		}

		resultSet.close();
		resultSet = databaseMetaData.getColumns((String)null, (String)null, "whitelist", "admin");
		PreparedStatement preparedStatement;
		PreparedStatement preparedStatement2;
		if (resultSet.next()) {
			resultSet.close();
			preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist where admin = \'true\'");
			ResultSet resultSet2 = preparedStatement.executeQuery();
			while (resultSet2.next()) {
				preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist set accesslevel = \'admin\' where id = ?");
				preparedStatement2.setString(1, resultSet2.getString("id"));
				System.out.println(resultSet2.getString("username"));
				preparedStatement2.executeUpdate();
			}
		}

		resultSet = databaseMetaData.getTables((String)null, (String)null, "tickets", (String[])null);
		if (!resultSet.next()) {
			statement2.executeUpdate("CREATE TABLE [tickets] ([id] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, [message] TEXT NOT NULL, [author] TEXT NOT NULL,[answeredID] INTEGER,[viewed] BOOLEAN NULL DEFAULT false)");
		}

		resultSet.close();
		preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
		preparedStatement.setString(1, this.CommandLineAdminUsername);
		resultSet = preparedStatement.executeQuery();
		String string2;
		if (!resultSet.next()) {
			preparedStatement.close();
			string2 = this.CommandLineAdminPassword;
			if (string2 == null || string2.isEmpty()) {
				Scanner scanner = new Scanner(new InputStreamReader(System.in));
				System.out.println("User \'admin\' not found, creating it ");
				System.out.println("Command line admin password: " + this.CommandLineAdminPassword);
				System.out.println("Enter new administrator password: ");
				string2 = scanner.nextLine();
				label127: while (true) {
					if (string2 != null && !"".equals(string2)) {
						System.out.println("Confirm the password: ");
						String string3 = scanner.nextLine();
						while (true) {
							if (string3 != null && !"".equals(string3) && string2.equals(string3)) {
								break label127;
							}

							System.out.println("Wrong password, confirm the password: ");
							string3 = scanner.nextLine();
						}
					}

					System.out.println("Enter new administrator password: ");
					string2 = scanner.nextLine();
				}
			}

			if (this.doAdmin) {
				preparedStatement = this.conn.prepareStatement("INSERT INTO whitelist (username, password, accesslevel, encryptedPwd, pwdEncryptType) VALUES (?, ?, \'admin\', \'true\', \'2\')");
			} else {
				preparedStatement = this.conn.prepareStatement("INSERT INTO whitelist (username, password, encryptedPwd, pwdEncryptType) VALUES (?, ?, \'true\', \'2\')");
			}

			preparedStatement.setString(1, this.CommandLineAdminUsername);
			preparedStatement.setString(2, PZcrypt.hash(encrypt(string2)));
			preparedStatement.executeUpdate();
			preparedStatement.close();
			System.out.println("Administrator account \'" + this.CommandLineAdminUsername + "\' created.");
		} else {
			preparedStatement.close();
		}

		statement2.close();
		if (this.CommandLineAdminPassword != null && !this.CommandLineAdminPassword.isEmpty()) {
			string2 = PZcrypt.hash(encrypt(this.CommandLineAdminPassword));
			preparedStatement2 = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
			preparedStatement2.setString(1, this.CommandLineAdminUsername);
			resultSet = preparedStatement2.executeQuery();
			if (resultSet.next()) {
				preparedStatement2.close();
				preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist SET password = ? WHERE username = ?");
				preparedStatement2.setString(1, string2);
				preparedStatement2.setString(2, this.CommandLineAdminUsername);
				preparedStatement2.executeUpdate();
				System.out.println("admin password changed via -adminpassword option");
			} else {
				System.out.println("ERROR: -adminpassword ignored, no \'" + this.CommandLineAdminUsername + "\' account in db");
			}

			preparedStatement2.close();
		}
	}

	public void close() {
		try {
			if (this.conn != null) {
				this.conn.close();
			}
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	public static boolean isValidUserName(String string) {
		if (string != null && !string.trim().isEmpty() && !string.contains(";") && !string.contains("@") && !string.contains("$") && !string.contains(",") && !string.contains("/") && !string.contains(".") && !string.contains("\'") && !string.contains("?") && !string.contains("\"") && string.trim().length() >= 3 && string.length() <= 20) {
			if (string.contains(nullChar)) {
				return false;
			} else if (string.trim().equals("admin")) {
				return true;
			} else {
				return !string.trim().toLowerCase().startsWith("admin");
			}
		} else {
			return false;
		}
	}

	public ServerWorldDatabase.LogonResult authClient(String string, String string2, String string3, long long1) {
		System.out.println("User " + string + " is trying to connect.");
		ServerWorldDatabase.LogonResult logonResult = new ServerWorldDatabase.LogonResult();
		if (!ServerOptions.instance.AllowNonAsciiUsername.getValue() && !asciiEncoder.canEncode(string)) {
			logonResult.bAuthorized = false;
			logonResult.dcReason = "NonAsciiCharacters";
			return logonResult;
		} else if (!isValidUserName(string)) {
			logonResult.bAuthorized = false;
			logonResult.dcReason = "InvalidUsername";
			return logonResult;
		} else {
			try {
				PreparedStatement preparedStatement;
				ResultSet resultSet;
				if (!SteamUtils.isSteamModeEnabled() && !string3.equals("127.0.0.1")) {
					preparedStatement = this.conn.prepareStatement("SELECT * FROM bannedip WHERE ip = ?");
					preparedStatement.setString(1, string3);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						logonResult.bAuthorized = false;
						logonResult.bannedReason = resultSet.getString("reason");
						logonResult.banned = true;
						preparedStatement.close();
						return logonResult;
					}

					preparedStatement.close();
				}

				if (isNullOrEmpty(string2) && ServerOptions.instance.Open.getValue() && ServerOptions.instance.AutoCreateUserInWhiteList.getValue()) {
					logonResult.dcReason = "UserPasswordRequired";
					logonResult.bAuthorized = false;
					return logonResult;
				}

				preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE LOWER(username) = LOWER(?) AND world = ?");
				preparedStatement.setString(1, string);
				preparedStatement.setString(2, Core.GameSaveWorld);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					String string4;
					String string5;
					PreparedStatement preparedStatement2;
					if (!isNullOrEmpty(resultSet.getString("password")) && (resultSet.getString("encryptedPwd").equals("false") || resultSet.getString("encryptedPwd").equals("N"))) {
						string4 = resultSet.getString("password");
						string5 = encrypt(string4);
						preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist SET encryptedPwd = \'true\' WHERE username = ? and password = ?");
						preparedStatement2.setString(1, string);
						preparedStatement2.setString(2, string4);
						preparedStatement2.executeUpdate();
						preparedStatement2.close();
						preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist SET password = ? WHERE username = ? AND password = ?");
						preparedStatement2.setString(1, string5);
						preparedStatement2.setString(2, string);
						preparedStatement2.setString(3, string4);
						preparedStatement2.executeUpdate();
						preparedStatement2.close();
						resultSet = preparedStatement.executeQuery();
					}

					if (!isNullOrEmpty(resultSet.getString("password")) && resultSet.getInt("pwdEncryptType") == 1) {
						string4 = resultSet.getString("password");
						string5 = PZcrypt.hash(string4);
						preparedStatement2 = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = \'2\', password = ? WHERE username = ? AND password = ?");
						preparedStatement2.setString(1, string5);
						preparedStatement2.setString(2, string);
						preparedStatement2.setString(3, string4);
						preparedStatement2.executeUpdate();
						preparedStatement2.close();
						resultSet = preparedStatement.executeQuery();
					}

					if (!isNullOrEmpty(resultSet.getString("password")) && !resultSet.getString("password").equals(string2)) {
						logonResult.bAuthorized = false;
						preparedStatement.close();
						if (isNullOrEmpty(string2)) {
							logonResult.dcReason = "DuplicateAccount";
						} else {
							logonResult.dcReason = "InvalidUsernamePassword";
						}

						return logonResult;
					}

					logonResult.bAuthorized = true;
					logonResult.admin = "true".equals(resultSet.getString("admin")) || "Y".equals(resultSet.getString("admin"));
					logonResult.accessLevel = resultSet.getString("accesslevel");
					if (logonResult.accessLevel == null) {
						logonResult.accessLevel = "";
						if (logonResult.admin) {
							logonResult.accessLevel = "admin";
						}

						this.setAccessLevel(string, logonResult.accessLevel);
					}

					logonResult.banned = "true".equals(resultSet.getString("banned")) || "Y".equals(resultSet.getString("banned"));
					if (logonResult.banned) {
						logonResult.bAuthorized = false;
					}

					if (resultSet.getString("transactionID") == null) {
						logonResult.transactionID = 0;
					} else {
						logonResult.transactionID = Integer.parseInt(resultSet.getString("transactionID"));
					}

					logonResult.priority = resultSet.getString("priority").equals("true");
					preparedStatement.close();
					return logonResult;
				}

				if (ServerOptions.instance.Open.getValue()) {
					if (!this.isNewAccountAllowed(string3, long1)) {
						preparedStatement.close();
						logonResult.bAuthorized = false;
						logonResult.dcReason = "MaxAccountsReached";
						return logonResult;
					}

					logonResult.bAuthorized = true;
					preparedStatement.close();
					return logonResult;
				}

				logonResult.bAuthorized = false;
				logonResult.dcReason = "UnknownUsername";
				preparedStatement.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return logonResult;
		}
	}

	public ServerWorldDatabase.LogonResult authClient(long long1) {
		String string = SteamUtils.convertSteamIDToString(long1);
		System.out.println("Steam client " + string + " is initiating a connection.");
		ServerWorldDatabase.LogonResult logonResult = new ServerWorldDatabase.LogonResult();
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM bannedid WHERE steamid = ?");
			preparedStatement.setString(1, string);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				logonResult.bAuthorized = false;
				logonResult.bannedReason = resultSet.getString("reason");
				logonResult.banned = true;
				preparedStatement.close();
				return logonResult;
			}

			preparedStatement.close();
			logonResult.bAuthorized = true;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return logonResult;
	}

	public ServerWorldDatabase.LogonResult authOwner(long long1, long long2) {
		String string = SteamUtils.convertSteamIDToString(long1);
		String string2 = SteamUtils.convertSteamIDToString(long2);
		System.out.println("Steam client " + string + " borrowed the game from " + string2);
		ServerWorldDatabase.LogonResult logonResult = new ServerWorldDatabase.LogonResult();
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM bannedid WHERE steamid = ?");
			preparedStatement.setString(1, string2);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				logonResult.bAuthorized = false;
				logonResult.bannedReason = resultSet.getString("reason");
				logonResult.banned = true;
				preparedStatement.close();
				return logonResult;
			}

			preparedStatement.close();
			logonResult.bAuthorized = true;
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET ownerid = ? where steamid = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return logonResult;
	}

	private boolean isNewAccountAllowed(String string, long long1) {
		int int1 = ServerOptions.instance.MaxAccountsPerUser.getValue();
		if (int1 <= 0) {
			return true;
		} else if (!SteamUtils.isSteamModeEnabled()) {
			return true;
		} else {
			String string2 = SteamUtils.convertSteamIDToString(long1);
			int int2 = 0;
			try {
				PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE steamid = ? AND accessLevel = ?");
				try {
					preparedStatement.setString(1, string2);
					preparedStatement.setString(2, "");
					for (ResultSet resultSet = preparedStatement.executeQuery(); resultSet.next(); ++int2) {
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
				exception.printStackTrace();
				return true;
			}

			return int2 < int1;
		}
	}

	public static String encrypt(String string) {
		if (isNullOrEmpty(string)) {
			return "";
		} else {
			byte[] byteArray = null;
			try {
				byteArray = MessageDigest.getInstance("MD5").digest(string.getBytes());
			} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
				System.out.println("Can\'t encrypt password");
				noSuchAlgorithmException.printStackTrace();
			}

			StringBuilder stringBuilder = new StringBuilder();
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				String string2 = Integer.toHexString(byteArray[int1]);
				if (string2.length() == 1) {
					stringBuilder.append('0');
					stringBuilder.append(string2.charAt(string2.length() - 1));
				} else {
					stringBuilder.append(string2.substring(string2.length() - 2));
				}
			}

			return stringBuilder.toString();
		}
	}

	public String changePwd(String string, String string2, String string3) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND password = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, string2);
		preparedStatement.setString(3, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = \'2\', password = ? WHERE username = ? and password = ?");
			preparedStatement.setString(1, string3);
			preparedStatement.setString(2, string);
			preparedStatement.setString(3, string2);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return "Your new password is " + string3;
		} else {
			preparedStatement.close();
			return "Wrong password for user " + string;
		}
	}

	public String grantAdmin(String string, boolean boolean1) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET admin = ? WHERE username = ?");
			preparedStatement.setString(1, boolean1 ? "true" : "false");
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return boolean1 ? "User " + string + " is now admin" : "User " + string + " is no longer admin";
		} else {
			preparedStatement.close();
			return "User \"" + string + "\" is not in the whitelist, use /adduser first";
		}
	}

	public String setAccessLevel(String string, String string2) throws SQLException {
		string2 = string2.trim();
		if (!this.containsUser(string)) {
			this.addUser(string, "");
		}

		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET accesslevel = ? WHERE username = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			return string2.equals("") ? "User " + string + " no longer has access level" : "User " + string + " is now " + string2;
		} else {
			preparedStatement.close();
			return "User \"" + string + "\" is not in the whitelist, use /adduser first";
		}
	}

	public ArrayList getUserlog(String string) {
		ArrayList arrayList = new ArrayList();
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM userlog WHERE username = ?");
			preparedStatement.setString(1, string);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				arrayList.add(new Userlog(string, resultSet.getString("type"), resultSet.getString("text"), resultSet.getString("issuedBy"), resultSet.getInt("amount")));
			}

			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return arrayList;
	}

	public void addUserlog(String string, Userlog.UserlogType userlogType, String string2, String string3, int int1) {
		try {
			boolean boolean1 = true;
			PreparedStatement preparedStatement;
			if (userlogType == Userlog.UserlogType.LuaChecksum || userlogType == Userlog.UserlogType.DupeItem || userlogType == Userlog.UserlogType.UnauthorizedPacket) {
				preparedStatement = this.conn.prepareStatement("SELECT * FROM userlog WHERE username = ? AND type = ?");
				preparedStatement.setString(1, string);
				preparedStatement.setString(2, userlogType.toString());
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					boolean1 = false;
					int1 = Integer.parseInt(resultSet.getString("amount")) + 1;
					preparedStatement.close();
					PreparedStatement preparedStatement2 = this.conn.prepareStatement("UPDATE userlog set amount = ? WHERE username = ? AND type = ?");
					preparedStatement2.setString(1, (new Integer(int1)).toString());
					preparedStatement2.setString(2, string);
					preparedStatement2.setString(3, userlogType.toString());
					preparedStatement2.executeUpdate();
					preparedStatement2.close();
				}
			}

			if (boolean1) {
				preparedStatement = this.conn.prepareStatement("INSERT INTO userlog (username, type, text, issuedBy, amount) VALUES (?, ?, ?, ?, ?)");
				preparedStatement.setString(1, string);
				preparedStatement.setString(2, userlogType.toString());
				preparedStatement.setString(3, string2);
				preparedStatement.setString(4, string3);
				preparedStatement.setString(5, (new Integer(int1)).toString());
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public String banUser(String string, boolean boolean1) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		boolean boolean2 = resultSet.next();
		if (boolean1 && !boolean2) {
			PreparedStatement preparedStatement2 = this.conn.prepareStatement("INSERT INTO whitelist (world, username, password, encryptedPwd) VALUES (?, ?, \'bogus\', \'false\')");
			preparedStatement2.setString(1, Core.GameSaveWorld);
			preparedStatement2.setString(2, string);
			preparedStatement2.executeUpdate();
			preparedStatement2.close();
			resultSet = preparedStatement.executeQuery();
			boolean2 = true;
		}

		if (boolean2) {
			String string2 = "true";
			if (!boolean1) {
				string2 = "false";
			}

			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET banned = ? WHERE username = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			if (SteamUtils.isSteamModeEnabled()) {
				preparedStatement = this.conn.prepareStatement("SELECT steamid FROM whitelist WHERE username = ? AND world = ?");
				preparedStatement.setString(1, string);
				preparedStatement.setString(2, Core.GameSaveWorld);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					String string3 = resultSet.getString("steamid");
					preparedStatement.close();
					if (string3 != null && !string3.isEmpty()) {
						this.banSteamID(string3, "", boolean1);
					}
				} else {
					preparedStatement.close();
				}
			}

			return boolean1 ? "User " + string + " is now banned" : "User " + string + " is now un-banned";
		} else {
			preparedStatement.close();
			return "User \"" + string + "\" is not in the whitelist, use /adduser first";
		}
	}

	public String banIp(String string, String string2, String string3, boolean boolean1) throws SQLException {
		PreparedStatement preparedStatement;
		if (boolean1) {
			preparedStatement = this.conn.prepareStatement("INSERT INTO bannedip (ip, username, reason) VALUES (?, ?, ?)");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, string2);
			preparedStatement.setString(3, string3);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} else {
			if (string != null) {
				preparedStatement = this.conn.prepareStatement("DELETE FROM bannedip WHERE ip = ?");
				preparedStatement.setString(1, string);
				preparedStatement.executeUpdate();
				preparedStatement.close();
			}

			preparedStatement = this.conn.prepareStatement("DELETE FROM bannedip WHERE username = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}

		return "";
	}

	public String banSteamID(String string, String string2, boolean boolean1) throws SQLException {
		PreparedStatement preparedStatement;
		if (boolean1) {
			preparedStatement = this.conn.prepareStatement("INSERT INTO bannedid (steamid, reason) VALUES (?, ?)");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, string2);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} else {
			preparedStatement = this.conn.prepareStatement("DELETE FROM bannedid WHERE steamid = ?");
			preparedStatement.setString(1, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}

		return "";
	}

	public String setUserSteamID(String string, String string2) {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ?");
			preparedStatement.setString(1, string);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				preparedStatement.close();
				return "User " + string + " not found";
			}

			preparedStatement.close();
			preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET steamid = ? WHERE username = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}

		return "User " + string + " SteamID set to " + string2;
	}

	public void setPassword(String string, String string2) throws SQLException {
		try {
			PreparedStatement preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET pwdEncryptType = \'2\', password = ? WHERE username = ? and world = ?");
			preparedStatement.setString(1, string2);
			preparedStatement.setString(2, string);
			preparedStatement.setString(3, Core.GameSaveWorld);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	public void updateLastConnectionDate(String string, String string2) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			PreparedStatement preparedStatement = this.conn.prepareStatement("UPDATE whitelist SET lastConnection = ? WHERE username = ? AND password = ?");
			preparedStatement.setString(1, simpleDateFormat.format(Calendar.getInstance().getTime()));
			preparedStatement.setString(2, string);
			preparedStatement.setString(3, string2);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
		}
	}

	private static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public String addWarningPoint(String string, String string2, int int1, String string3) throws SQLException {
		PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT * FROM whitelist WHERE username = ? AND world = ?");
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, Core.GameSaveWorld);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			this.addUserlog(string, Userlog.UserlogType.WarningPoint, string2, string3, int1);
			return "Added a warning point on " + string + " reason: " + string2;
		} else {
			return "User " + string + " doesn\'t exist.";
		}
	}

	public void addTicket(String string, String string2, int int1) throws SQLException {
		PreparedStatement preparedStatement;
		if (int1 > -1) {
			preparedStatement = this.conn.prepareStatement("INSERT INTO tickets (author, message, answeredID) VALUES (?, ?, ?)");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, string2);
			preparedStatement.setInt(3, int1);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} else {
			preparedStatement = this.conn.prepareStatement("INSERT INTO tickets (author, message) VALUES (?, ?)");
			preparedStatement.setString(1, string);
			preparedStatement.setString(2, string2);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}
	}

	public ArrayList getTickets(String string) throws SQLException {
		ArrayList arrayList = new ArrayList();
		PreparedStatement preparedStatement = null;
		if (string != null) {
			preparedStatement = this.conn.prepareStatement("SELECT * FROM tickets WHERE author = ? and answeredID is null");
			preparedStatement.setString(1, string);
		} else {
			preparedStatement = this.conn.prepareStatement("SELECT * FROM tickets where answeredID is null");
		}

		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			DBTicket dBTicket = new DBTicket(resultSet.getString("author"), resultSet.getString("message"), resultSet.getInt("id"));
			arrayList.add(dBTicket);
			DBTicket dBTicket2 = this.getAnswer(dBTicket.getTicketID());
			if (dBTicket2 != null) {
				dBTicket.setAnswer(dBTicket2);
			}
		}

		return arrayList;
	}

	private DBTicket getAnswer(int int1) throws SQLException {
		PreparedStatement preparedStatement = null;
		preparedStatement = this.conn.prepareStatement("SELECT * FROM tickets WHERE answeredID = ?");
		preparedStatement.setInt(1, int1);
		ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet.next() ? new DBTicket(resultSet.getString("author"), resultSet.getString("message"), resultSet.getInt("id")) : null;
	}

	public void removeTicket(int int1) throws SQLException {
		DBTicket dBTicket = this.getAnswer(int1);
		PreparedStatement preparedStatement;
		if (dBTicket != null) {
			preparedStatement = this.conn.prepareStatement("DELETE FROM tickets WHERE id = ?");
			preparedStatement.setInt(1, dBTicket.getTicketID());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}

		preparedStatement = this.conn.prepareStatement("DELETE FROM tickets WHERE id = ?");
		preparedStatement.setInt(1, int1);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	public class LogonResult {
		public boolean bAuthorized = false;
		public int x;
		public int y;
		public int z;
		public boolean admin = false;
		public boolean banned = false;
		public boolean priority = false;
		public String bannedReason = null;
		public String dcReason = null;
		public String accessLevel = "";
		public int transactionID = 0;
	}
}
