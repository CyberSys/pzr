package zombie.network;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;


public class DBSchema {
	private HashMap schema = new HashMap();
	private KahluaTable fullTable;

	public DBSchema(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String[] stringArray = new String[]{"TABLE"};
			ResultSet resultSet = databaseMetaData.getTables((String)null, (String)null, (String)null, stringArray);
			while (true) {
				String string;
				do {
					if (!resultSet.next()) {
						return;
					}

					string = resultSet.getString(3);
				}		 while (string.startsWith("SQLITE_"));

				ResultSet resultSet2 = databaseMetaData.getColumns((String)null, (String)null, string, (String)null);
				HashMap hashMap = new HashMap();
				while (resultSet2.next()) {
					String string2 = resultSet2.getString(4);
					if (!string2.equals("world") && !string2.equals("moderator") && !string2.equals("admin") && !string2.equals("password") && !string2.equals("encryptedPwd") && !string2.equals("pwdEncryptType") && !string2.equals("transactionID")) {
						hashMap.put(string2, resultSet2.getString(6));
					}
				}

				this.schema.put(string, hashMap);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public KahluaTable getFullTable() {
		return this.fullTable;
	}

	public void setFullTable(KahluaTable kahluaTable) {
		this.fullTable = kahluaTable;
	}

	public HashMap getSchema() {
		return this.schema;
	}
}
