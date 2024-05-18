package zombie.gameStates;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionConfig;
import zombie.globalObjects.CGlobalObjects;
import zombie.iso.IsoChunkMap;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.ServerOptions;


public class ConnectToServerState extends GameState {
	public static ConnectToServerState instance;
	private ByteBuffer connectionDetails;
	private ConnectToServerState.State state;
	private ArrayList workshopItems = new ArrayList();
	private ArrayList confirmItems = new ArrayList();
	private ConnectToServerState.ItemQuery query;

	private static void noise(String string) {
		DebugLog.log("ConnectToServerState: " + string);
	}

	public ConnectToServerState(ByteBuffer byteBuffer) {
		this.connectionDetails = ByteBuffer.allocate(byteBuffer.capacity());
		this.connectionDetails.put(byteBuffer);
		this.connectionDetails.rewind();
	}

	public void enter() {
		instance = this;
		this.state = ConnectToServerState.State.Start;
	}

	public GameStateMachine.StateAction update() {
		switch (this.state) {
		case Start: 
			this.Start();
			break;
		
		case TestTCP: 
			this.TestTCP();
			break;
		
		case WorkshopInit: 
			this.WorkshopInit();
			break;
		
		case WorkshopConfirm: 
			this.WorkshopConfirm();
			break;
		
		case WorkshopQuery: 
			this.WorkshopQuery();
			break;
		
		case ServerWorkshopItemScreen: 
			this.ServerWorkshopItemScreen();
			break;
		
		case WorkshopUpdate: 
			this.WorkshopUpdate();
			break;
		
		case CheckMods: 
			this.CheckMods();
			break;
		
		case Finish: 
			this.Finish();
			break;
		
		case Exit: 
			return GameStateMachine.StateAction.Continue;
		
		}
		return GameStateMachine.StateAction.Remain;
	}

	private void Start() {
		noise("Start");
		ByteBuffer byteBuffer = this.connectionDetails;
		if (byteBuffer.get() == 1) {
			long long1 = byteBuffer.getLong();
			String string = GameWindow.ReadStringUTF(byteBuffer);
			Core.GameSaveWorld = long1 + "_" + string + "_player";
		}

		GameClient.instance.ID = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		this.state = ConnectToServerState.State.TestTCP;
	}

	private void TestTCP() {
		noise("TestTCP");
		ByteBuffer byteBuffer = this.connectionDetails;
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			LuaEventManager.triggerEvent("OnConnectionStateChanged", "TestTCP");
			if (SpriteRenderer.instance != null) {
				GameWindow.render();
			}

			if (Core.bDebug) {
				try {
					Thread.sleep(500L);
				} catch (InterruptedException interruptedException) {
				}
			}
		}

		GameClient.accessLevel = GameWindow.ReadStringUTF(byteBuffer);
		if (!SystemDisabler.getAllowDebugConnections() && Core.bDebug && !SystemDisabler.getOverrideServerConnectDebugCheck() && !GameClient.accessLevel.equals("admin") && !CoopMaster.instance.isRunning()) {
			LuaEventManager.triggerEvent("OnConnectFailed");
			GameClient.connection.forceDisconnect();
			this.state = ConnectToServerState.State.Exit;
		} else {
			GameClient.GameMap = GameWindow.ReadStringUTF(byteBuffer);
			if (GameClient.GameMap.contains(";")) {
				String[] stringArray = GameClient.GameMap.split(";");
				Core.GameMap = stringArray[0].trim();
			} else {
				Core.GameMap = GameClient.GameMap.trim();
			}

			if (SteamUtils.isSteamModeEnabled()) {
				this.state = ConnectToServerState.State.WorkshopInit;
			} else {
				this.state = ConnectToServerState.State.CheckMods;
			}
		}
	}

	private void WorkshopInit() {
		ByteBuffer byteBuffer = this.connectionDetails;
		short short1 = byteBuffer.getShort();
		for (int int1 = 0; int1 < short1; ++int1) {
			long long1 = byteBuffer.getLong();
			long long2 = byteBuffer.getLong();
			ConnectToServerState.WorkshopItem workshopItem = new ConnectToServerState.WorkshopItem(long1, long2);
			this.workshopItems.add(workshopItem);
		}

		this.state = ConnectToServerState.State.WorkshopConfirm;
	}

	private void WorkshopConfirm() {
		this.confirmItems.clear();
		for (int int1 = 0; int1 < this.workshopItems.size(); ++int1) {
			ConnectToServerState.WorkshopItem workshopItem = (ConnectToServerState.WorkshopItem)this.workshopItems.get(int1);
			long long1 = SteamWorkshop.instance.GetItemState(workshopItem.ID);
			noise("WorkshopConfirm GetItemState()=" + SteamWorkshopItem.ItemState.toString(long1) + " ID=" + workshopItem.ID);
			if (long1 != (long)(SteamWorkshopItem.ItemState.Subscribed.getValue() | SteamWorkshopItem.ItemState.Installed.getValue())) {
				this.confirmItems.add(workshopItem);
			}
		}

		if (this.confirmItems.isEmpty()) {
			this.state = ConnectToServerState.State.WorkshopUpdate;
		} else {
			long[] longArray = new long[this.workshopItems.size()];
			ArrayList arrayList = new ArrayList();
			for (int int2 = 0; int2 < this.workshopItems.size(); ++int2) {
				ConnectToServerState.WorkshopItem workshopItem2 = (ConnectToServerState.WorkshopItem)this.workshopItems.get(int2);
				longArray[int2] = workshopItem2.ID;
				arrayList.add(SteamUtils.convertSteamIDToString(workshopItem2.ID));
			}

			this.query = new ConnectToServerState.ItemQuery();
			this.query.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longArray, this.query);
			if (this.query.handle != 0L) {
				LuaEventManager.triggerEvent("OnServerWorkshopItems", "Required", arrayList);
				this.state = ConnectToServerState.State.WorkshopQuery;
			} else {
				this.query = null;
				LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_CreateQueryUGCDetailsRequest"));
				GameClient.connection.forceDisconnect();
				this.state = ConnectToServerState.State.Exit;
			}
		}
	}

	private void WorkshopQuery() {
		if (this.query.isCompleted()) {
			ArrayList arrayList = this.query.details;
			this.query = null;
			this.state = ConnectToServerState.State.ServerWorkshopItemScreen;
			LuaEventManager.triggerEvent("OnServerWorkshopItems", "Details", arrayList);
		} else if (this.query.isNotCompleted()) {
			this.query = null;
			this.state = ConnectToServerState.State.ServerWorkshopItemScreen;
			LuaEventManager.triggerEvent("OnServerWorkshopItems", "Error", "ItemQueryNotCompleted");
		}
	}

	private void ServerWorkshopItemScreen() {
	}

	private void WorkshopUpdate() {
		for (int int1 = 0; int1 < this.workshopItems.size(); ++int1) {
			ConnectToServerState.WorkshopItem workshopItem = (ConnectToServerState.WorkshopItem)this.workshopItems.get(int1);
			workshopItem.update();
			if (workshopItem.state == ConnectToServerState.WorkshopItemState.Fail) {
				this.state = ConnectToServerState.State.ServerWorkshopItemScreen;
				LuaEventManager.triggerEvent("OnServerWorkshopItems", "Error", workshopItem.ID, workshopItem.error);
				return;
			}

			if (workshopItem.state != ConnectToServerState.WorkshopItemState.Ready) {
				return;
			}
		}

		ZomboidFileSystem.instance.resetModFolders();
		LuaEventManager.triggerEvent("OnServerWorkshopItems", "Success");
		this.state = ConnectToServerState.State.CheckMods;
	}

	private void CheckMods() {
		ByteBuffer byteBuffer = this.connectionDetails;
		ArrayList arrayList = new ArrayList();
		HashMap hashMap = new HashMap();
		ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			ChooseGameInfo.Mod mod = chooseGameInfo.new Mod(GameWindow.ReadStringUTF(byteBuffer));
			mod.setUrl(GameWindow.ReadStringUTF(byteBuffer));
			mod.setName(GameWindow.ReadStringUTF(byteBuffer));
			arrayList.add(mod.getDir());
			hashMap.put(mod.getDir(), mod);
		}

		GameClient.instance.ServerMods.clear();
		GameClient.instance.ServerMods.addAll(arrayList);
		arrayList.clear();
		String string = ZomboidFileSystem.instance.loadModsAux(GameClient.instance.ServerMods, arrayList);
		if (string != null) {
			String string2 = Translator.getText("UI_OnConnectFailed_ModRequired", string);
			if (hashMap.get(string) != null && !"".equals(((ChooseGameInfo.Mod)hashMap.get(string)).getUrl())) {
				string2 = string2 + " MODURL=" + ((ChooseGameInfo.Mod)hashMap.get(string)).getUrl();
			}

			LuaEventManager.triggerEvent("OnConnectFailed", string2);
			GameClient.connection.forceDisconnect();
			this.state = ConnectToServerState.State.Exit;
		} else {
			this.state = ConnectToServerState.State.Finish;
		}
	}

	private void Finish() {
		ByteBuffer byteBuffer = this.connectionDetails;
		LuaEventManager.triggerEvent("OnConnectionStateChanged", "Connected");
		IsoChunkMap.MPWorldXA = byteBuffer.getInt();
		IsoChunkMap.MPWorldYA = byteBuffer.getInt();
		IsoChunkMap.MPWorldZA = byteBuffer.getInt();
		GameClient.username = GameClient.username.trim();
		Core.GameMode = "Multiplayer";
		LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
		GameClient.instance.bConnected = true;
		int int1 = byteBuffer.getInt();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			ServerOptions.instance.putOption(GameWindow.ReadString(byteBuffer), GameWindow.ReadString(byteBuffer));
		}

		Core.getInstance().ResetLua(false, "ConnectedToServer");
		Core.GameMode = "Multiplayer";
		GameClient.connection.ip = GameClient.ip;
		try {
			SandboxOptions.instance.load(byteBuffer);
			SandboxOptions.instance.applySettings();
			SandboxOptions.instance.toLua();
			GameTime.getInstance().load(byteBuffer);
			GameTime.getInstance().save();
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		}

		GameClient.instance.erosionConfig = new ErosionConfig();
		GameClient.instance.erosionConfig.load(byteBuffer);
		try {
			CGlobalObjects.loadInitialState(byteBuffer);
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
		}

		int2 = byteBuffer.getInt();
		GameClient.instance.setResetID(int2);
		Core.getInstance().setPoisonousBerry(GameWindow.ReadString(byteBuffer));
		GameClient.poisonousBerry = Core.getInstance().getPoisonousBerry();
		Core.getInstance().setPoisonousMushroom(GameWindow.ReadString(byteBuffer));
		GameClient.poisonousMushroom = Core.getInstance().getPoisonousMushroom();
		GameClient.connection.isCoopHost = byteBuffer.get() == 1;
		LuaEventManager.triggerEvent("OnConnected");
		this.state = ConnectToServerState.State.Exit;
	}

	public void FromLua(String string) {
		if (this.state != ConnectToServerState.State.ServerWorkshopItemScreen) {
			throw new IllegalStateException("state != ServerWorkshopItemScreen");
		} else if ("install".equals(string)) {
			this.state = ConnectToServerState.State.WorkshopUpdate;
		} else if ("disconnect".equals(string)) {
			LuaEventManager.triggerEvent("OnConnectFailed", "ServerWorkshopItemsCancelled");
			GameClient.connection.forceDisconnect();
			this.state = ConnectToServerState.State.Exit;
		}
	}

	public void exit() {
		instance = null;
	}

	private class WorkshopItem implements ISteamWorkshopCallback {
		long ID;
		long serverTimeStamp;
		ConnectToServerState.WorkshopItemState state;
		boolean subscribed;
		long downloadStartTime;
		long downloadQueryTime;
		String error;

		WorkshopItem(long long1, long long2) {
			this.state = ConnectToServerState.WorkshopItemState.CheckItemState;
			this.ID = long1;
			this.serverTimeStamp = long2;
		}

		void update() {
			switch (this.state) {
			case CheckItemState: 
				this.CheckItemState();
				break;
			
			case SubscribePending: 
				this.SubscribePending();
				break;
			
			case DownloadPending: 
				this.DownloadPending();
			
			case Ready: 
			
			}
		}

		void setState(ConnectToServerState.WorkshopItemState workshopItemState) {
			ConnectToServerState.noise("item state " + this.state + " -> " + workshopItemState + " ID=" + this.ID);
			this.state = workshopItemState;
		}

		void CheckItemState() {
			long long1 = SteamWorkshop.instance.GetItemState(this.ID);
			ConnectToServerState.noise("GetItemState()=" + SteamWorkshopItem.ItemState.toString(long1) + " ID=" + this.ID);
			if (!SteamWorkshopItem.ItemState.Subscribed.and(long1)) {
				if (SteamWorkshop.instance.SubscribeItem(this.ID, this)) {
					this.setState(ConnectToServerState.WorkshopItemState.SubscribePending);
				} else {
					this.error = "SubscribeItemFalse";
					this.setState(ConnectToServerState.WorkshopItemState.Fail);
				}
			} else if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long1)) {
				if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
					this.setState(ConnectToServerState.WorkshopItemState.DownloadPending);
					this.downloadStartTime = System.currentTimeMillis();
				} else {
					this.error = "DownloadItemFalse";
					this.setState(ConnectToServerState.WorkshopItemState.Fail);
				}
			} else if (SteamWorkshopItem.ItemState.Installed.and(long1)) {
				long long2 = SteamWorkshop.instance.GetItemInstallTimeStamp(this.ID);
				if (long2 == 0L) {
					this.error = "GetItemInstallTimeStamp";
					this.setState(ConnectToServerState.WorkshopItemState.Fail);
				} else if (long2 != this.serverTimeStamp) {
					this.error = "VersionMismatch";
					this.setState(ConnectToServerState.WorkshopItemState.Fail);
				} else {
					this.setState(ConnectToServerState.WorkshopItemState.Ready);
				}
			} else {
				this.error = "UnknownItemState";
				this.setState(ConnectToServerState.WorkshopItemState.Fail);
			}
		}

		void SubscribePending() {
			if (this.subscribed) {
				long long1 = SteamWorkshop.instance.GetItemState(this.ID);
				if (SteamWorkshopItem.ItemState.Subscribed.and(long1)) {
					this.setState(ConnectToServerState.WorkshopItemState.CheckItemState);
				}
			}
		}

		void DownloadPending() {
			long long1 = System.currentTimeMillis();
			if (this.downloadQueryTime + 100L <= long1) {
				this.downloadQueryTime = long1;
				long long2 = SteamWorkshop.instance.GetItemState(this.ID);
				if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long2)) {
					long[] longArray = new long[2];
					if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, longArray)) {
						ConnectToServerState.noise("download " + longArray[0] + "/" + longArray[1] + " ID=" + this.ID);
						LuaEventManager.triggerEvent("OnServerWorkshopItems", "Progress", SteamUtils.convertSteamIDToString(this.ID), longArray[0], Math.max(longArray[1], 1L));
					}
				}
			}
		}

		public void onItemCreated(long long1, boolean boolean1) {
		}

		public void onItemNotCreated(int int1) {
		}

		public void onItemUpdated(boolean boolean1) {
		}

		public void onItemNotUpdated(int int1) {
		}

		public void onItemSubscribed(long long1) {
			ConnectToServerState.noise("onItemSubscribed itemID=" + long1);
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.subscribed = true;
			}
		}

		public void onItemNotSubscribed(long long1, int int1) {
			ConnectToServerState.noise("onItemNotSubscribed itemID=" + long1 + " result=" + int1);
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.error = "ItemNotSubscribed";
				this.setState(ConnectToServerState.WorkshopItemState.Fail);
			}
		}

		public void onItemDownloaded(long long1) {
			ConnectToServerState.noise("onItemDownloaded itemID=" + long1 + " time=" + (System.currentTimeMillis() - this.downloadStartTime) + " ms");
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.setState(ConnectToServerState.WorkshopItemState.CheckItemState);
			}
		}

		public void onItemNotDownloaded(long long1, int int1) {
			ConnectToServerState.noise("onItemNotDownloaded itemID=" + long1 + " result=" + int1);
			if (long1 == this.ID) {
				SteamWorkshop.instance.RemoveCallback(this);
				this.error = "ItemNotDownloaded";
				this.setState(ConnectToServerState.WorkshopItemState.Fail);
			}
		}

		public void onItemQueryCompleted(long long1, int int1) {
		}

		public void onItemQueryNotCompleted(long long1, int int1) {
		}
	}
	private static enum WorkshopItemState {

		CheckItemState,
		SubscribePending,
		DownloadPending,
		Ready,
		Fail;
	}

	private class ItemQuery implements ISteamWorkshopCallback {
		long handle;
		ArrayList details;
		boolean bCompleted;
		boolean bNotCompleted;

		private ItemQuery() {
		}

		public boolean isCompleted() {
			return this.bCompleted;
		}

		public boolean isNotCompleted() {
			return this.bNotCompleted;
		}

		public void onItemCreated(long long1, boolean boolean1) {
		}

		public void onItemNotCreated(int int1) {
		}

		public void onItemUpdated(boolean boolean1) {
		}

		public void onItemNotUpdated(int int1) {
		}

		public void onItemSubscribed(long long1) {
		}

		public void onItemNotSubscribed(long long1, int int1) {
		}

		public void onItemDownloaded(long long1) {
		}

		public void onItemNotDownloaded(long long1, int int1) {
		}

		public void onItemQueryCompleted(long long1, int int1) {
			ConnectToServerState.noise("onItemQueryCompleted handle=" + long1 + " numResult=" + int1);
			if (long1 == this.handle) {
				SteamWorkshop.instance.RemoveCallback(this);
				ArrayList arrayList = new ArrayList();
				for (int int2 = 0; int2 < int1; ++int2) {
					SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long1, int2);
					if (steamUGCDetails != null) {
						arrayList.add(steamUGCDetails);
					}
				}

				this.details = arrayList;
				SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
				this.bCompleted = true;
			}
		}

		public void onItemQueryNotCompleted(long long1, int int1) {
			ConnectToServerState.noise("onItemQueryNotCompleted handle=" + long1 + " result=" + int1);
			if (long1 == this.handle) {
				SteamWorkshop.instance.RemoveCallback(this);
				SteamWorkshop.instance.ReleaseQueryUGCRequest(long1);
				this.bNotCompleted = true;
			}
		}

		ItemQuery(Object object) {
			this();
		}
	}
	private static enum State {

		Start,
		TestTCP,
		WorkshopInit,
		WorkshopConfirm,
		WorkshopQuery,
		ServerWorkshopItemScreen,
		WorkshopUpdate,
		CheckMods,
		Finish,
		Exit;
	}
}
