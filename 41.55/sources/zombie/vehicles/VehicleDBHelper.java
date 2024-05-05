package zombie.vehicles;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;


public final class VehicleDBHelper {

	public static boolean isPlayerAlive(String string, int int1) {
		File file = new File(string + File.separator + "map_p.bin");
		if (file.exists()) {
			return true;
		} else if (int1 == -1) {
			return false;
		} else {
			Connection connection = null;
			File file2 = new File(string + File.separator + "vehicles.db");
			file2.setReadable(true, false);
			if (!file2.exists()) {
				return false;
			} else {
				try {
					connection = PZSQLUtils.getConnection(file2.getAbsolutePath());
				} catch (Exception exception) {
					DebugLog.log("failed to create vehicles database");
					System.exit(1);
				}

				boolean boolean1 = false;
				String string2 = "SELECT isDead FROM localPlayers WHERE id=?";
				PreparedStatement preparedStatement = null;
				boolean boolean2;
				try {
					preparedStatement = connection.prepareStatement(string2);
					preparedStatement.setInt(1, int1);
					ResultSet resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						boolean1 = !resultSet.getBoolean(1);
					}

					return boolean1;
				} catch (SQLException sQLException) {
					boolean2 = false;
				} finally {
					try {
						if (preparedStatement != null) {
							preparedStatement.close();
						}

						connection.close();
					} catch (SQLException sQLException2) {
						System.out.println(sQLException2.getMessage());
					}
				}

				return boolean2;
			}
		}
	}
}
