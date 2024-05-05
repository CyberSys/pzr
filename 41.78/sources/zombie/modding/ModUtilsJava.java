package zombie.modding;

import java.util.ArrayList;
import java.util.UUID;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class ModUtilsJava {

	public static String getRandomUUID() {
		return UUID.randomUUID().toString();
	}

	public static boolean sendItemListNet(IsoPlayer player, ArrayList arrayList, IsoPlayer player2, String string, String string2) {
		if (arrayList != null) {
			string = string != null ? string : "-1";
			if (GameClient.bClient) {
				if (arrayList.size() > 50) {
					return false;
				}

				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
					if (!player.getInventory().getItems().contains(inventoryItem)) {
						return false;
					}
				}

				GameClient gameClient = GameClient.instance;
				return GameClient.sendItemListNet(player, arrayList, player2, string, string2);
			}

			if (GameServer.bServer) {
				return GameServer.sendItemListNet((UdpConnection)null, player, arrayList, player2, string, string2);
			}
		}

		return false;
	}
}
