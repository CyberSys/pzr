package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.spnetwork.SinglePlayerClient;


public class CGlobalObjectSystem {
	protected final String name;
	protected final KahluaTable modData;

	public CGlobalObjectSystem(String string) {
		this.name = string;
		this.modData = LuaManager.platform.newTable();
	}

	public KahluaTable getModData() {
		return this.modData;
	}

	public void sendCommand(String string, IsoPlayer player, KahluaTable kahluaTable) {
		if (GameServer.bServer) {
			throw new IllegalStateException("can\'t call this method on the server");
		} else {
			if (GameClient.bClient) {
				GameClient.instance.sendClientCommand(player, "gos_" + this.name, string, kahluaTable);
			} else {
				SinglePlayerClient.sendClientCommand(player, "gos_" + this.name, string, kahluaTable);
			}
		}
	}

	public void receiveServerCommand(String string, KahluaTable kahluaTable) {
		Object object = this.modData.rawget("OnServerCommand");
		if (object == null) {
			throw new IllegalStateException("OnServerCommand method undefined for system \'" + this.name + "\'");
		} else {
			LuaManager.caller.pcall(LuaManager.thread, object, this.modData, string, kahluaTable);
		}
	}

	public void Reset() {
		this.modData.wipe();
	}
}
