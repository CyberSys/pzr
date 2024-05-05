package zombie.Lua;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import se.krka.kahlua.vm.Prototype;
import zombie.debug.DebugLog;


public final class LuaEventManager implements JavaFunction {
	public static final ArrayList OnTickCallbacks = new ArrayList();
	static Object[][] a1 = new Object[1][1];
	static Object[][] a2 = new Object[1][2];
	static Object[][] a3 = new Object[1][3];
	static Object[][] a4 = new Object[1][4];
	static Object[][] a5 = new Object[1][5];
	static Object[][] a6 = new Object[1][6];
	static int a1index = 0;
	static int a2index = 0;
	static int a3index = 0;
	static int a4index = 0;
	static int a5index = 0;
	static int a6index = 0;
	private static final ArrayList EventList = new ArrayList();
	private static final HashMap EventMap = new HashMap();

	private static Event checkEvent(String string) {
		Event event = (Event)EventMap.get(string);
		if (event == null) {
			DebugLog.log("LuaEventManager: adding unknown event \"" + string + "\"");
			event = AddEvent(string);
		}

		return event.callbacks.isEmpty() ? null : event;
	}

	public static void triggerEvent(String string) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				event.trigger(LuaManager.env, LuaManager.caller, (Object[])null);
			}
		}
	}

	public static void triggerEvent(String string, Object object) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a1index == a1.length) {
					a1 = (Object[][])Arrays.copyOf(a1, a1.length * 2);
					for (int int1 = a1index; int1 < a1.length; ++int1) {
						a1[int1] = new Object[1];
					}
				}

				Object[] objectArray = a1[a1index];
				objectArray[0] = object;
				++a1index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a1index;
					objectArray[0] = null;
				}
			}
		}
	}

	public static void triggerEventGarbage(String string, Object object) {
		triggerEvent(string, object);
	}

	public static void triggerEventUnique(String string, Object object) {
		triggerEvent(string, object);
	}

	public static void triggerEvent(String string, Object object, Object object2) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a2index == a2.length) {
					a2 = (Object[][])Arrays.copyOf(a2, a2.length * 2);
					for (int int1 = a2index; int1 < a2.length; ++int1) {
						a2[int1] = new Object[2];
					}
				}

				Object[] objectArray = a2[a2index];
				objectArray[0] = object;
				objectArray[1] = object2;
				++a2index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a2index;
					objectArray[0] = null;
					objectArray[1] = null;
				}
			}
		}
	}

	public static void triggerEventGarbage(String string, Object object, Object object2) {
		triggerEvent(string, object, object2);
	}

	public static void triggerEvent(String string, Object object, Object object2, Object object3) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a3index == a3.length) {
					a3 = (Object[][])Arrays.copyOf(a3, a3.length * 2);
					for (int int1 = a3index; int1 < a3.length; ++int1) {
						a3[int1] = new Object[3];
					}
				}

				Object[] objectArray = a3[a3index];
				objectArray[0] = object;
				objectArray[1] = object2;
				objectArray[2] = object3;
				++a3index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a3index;
					objectArray[0] = null;
					objectArray[1] = null;
					objectArray[2] = null;
				}
			}
		}
	}

	public static void triggerEventGarbage(String string, Object object, Object object2, Object object3) {
		triggerEvent(string, object, object2, object3);
	}

	public static void triggerEvent(String string, Object object, Object object2, Object object3, Object object4) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a4index == a4.length) {
					a4 = (Object[][])Arrays.copyOf(a4, a4.length * 2);
					for (int int1 = a4index; int1 < a4.length; ++int1) {
						a4[int1] = new Object[4];
					}
				}

				Object[] objectArray = a4[a4index];
				objectArray[0] = object;
				objectArray[1] = object2;
				objectArray[2] = object3;
				objectArray[3] = object4;
				++a4index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a4index;
					objectArray[0] = null;
					objectArray[1] = null;
					objectArray[2] = null;
					objectArray[3] = null;
				}
			}
		}
	}

	public static void triggerEventGarbage(String string, Object object, Object object2, Object object3, Object object4) {
		triggerEvent(string, object, object2, object3, object4);
	}

	public static void triggerEvent(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a5index == a5.length) {
					a5 = (Object[][])Arrays.copyOf(a5, a5.length * 2);
					for (int int1 = a5index; int1 < a5.length; ++int1) {
						a5[int1] = new Object[5];
					}
				}

				Object[] objectArray = a5[a5index];
				objectArray[0] = object;
				objectArray[1] = object2;
				objectArray[2] = object3;
				objectArray[3] = object4;
				objectArray[4] = object5;
				++a5index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a5index;
					objectArray[0] = null;
					objectArray[1] = null;
					objectArray[2] = null;
					objectArray[3] = null;
					objectArray[4] = null;
				}
			}
		}
	}

	public static void triggerEvent(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		synchronized (EventMap) {
			Event event = checkEvent(string);
			if (event != null) {
				if (a6index == a6.length) {
					a6 = (Object[][])Arrays.copyOf(a6, a6.length * 2);
					for (int int1 = a6index; int1 < a6.length; ++int1) {
						a6[int1] = new Object[6];
					}
				}

				Object[] objectArray = a6[a6index];
				objectArray[0] = object;
				objectArray[1] = object2;
				objectArray[2] = object3;
				objectArray[3] = object4;
				objectArray[4] = object5;
				objectArray[5] = object6;
				++a6index;
				try {
					event.trigger(LuaManager.env, LuaManager.caller, objectArray);
				} finally {
					--a6index;
					objectArray[0] = null;
					objectArray[1] = null;
					objectArray[2] = null;
					objectArray[3] = null;
					objectArray[4] = null;
					objectArray[5] = null;
				}
			}
		}
	}

	public static Event AddEvent(String string) {
		Event event = (Event)EventMap.get(string);
		if (event != null) {
			return event;
		} else {
			event = new Event(string, EventList.size());
			EventList.add(event);
			EventMap.put(string, event);
			Object object = LuaManager.env.rawget("Events");
			if (object instanceof KahluaTable) {
				KahluaTable kahluaTable = (KahluaTable)object;
				event.register(LuaManager.platform, kahluaTable);
			} else {
				DebugLog.log("ERROR: \'Events\' table not found or not a table");
			}

			return event;
		}
	}

	private static void AddEvents() {
		AddEvent("OnGameBoot");
		AddEvent("OnPreGameStart");
		AddEvent("OnTick");
		AddEvent("OnTickEvenPaused");
		AddEvent("OnRenderUpdate");
		AddEvent("OnFETick");
		AddEvent("OnGameStart");
		AddEvent("OnPreUIDraw");
		AddEvent("OnPostUIDraw");
		AddEvent("OnCharacterCollide");
		AddEvent("OnKeyStartPressed");
		AddEvent("OnKeyPressed");
		AddEvent("OnObjectCollide");
		AddEvent("OnNPCSurvivorUpdate");
		AddEvent("OnPlayerUpdate");
		AddEvent("OnZombieUpdate");
		AddEvent("OnTriggerNPCEvent");
		AddEvent("OnMultiTriggerNPCEvent");
		AddEvent("OnLoadMapZones");
		AddEvent("OnAddBuilding");
		AddEvent("OnCreateLivingCharacter");
		AddEvent("OnChallengeQuery");
		AddEvent("OnFillInventoryObjectContextMenu");
		AddEvent("OnPreFillInventoryObjectContextMenu");
		AddEvent("OnFillWorldObjectContextMenu");
		AddEvent("OnPreFillWorldObjectContextMenu");
		AddEvent("OnRefreshInventoryWindowContainers");
		AddEvent("OnGamepadConnect");
		AddEvent("OnGamepadDisconnect");
		AddEvent("OnJoypadActivate");
		AddEvent("OnJoypadActivateUI");
		AddEvent("OnJoypadBeforeDeactivate");
		AddEvent("OnJoypadDeactivate");
		AddEvent("OnJoypadBeforeReactivate");
		AddEvent("OnJoypadReactivate");
		AddEvent("OnJoypadRenderUI");
		AddEvent("OnMakeItem");
		AddEvent("OnWeaponHitCharacter");
		AddEvent("OnWeaponSwing");
		AddEvent("OnWeaponHitTree");
		AddEvent("OnWeaponHitXp");
		AddEvent("OnWeaponSwingHitPoint");
		AddEvent("OnPlayerAttackFinished");
		AddEvent("OnLoginState");
		AddEvent("OnLoginStateSuccess");
		AddEvent("OnCharacterCreateStats");
		AddEvent("OnLoadSoundBanks");
		AddEvent("OnObjectLeftMouseButtonDown");
		AddEvent("OnObjectLeftMouseButtonUp");
		AddEvent("OnObjectRightMouseButtonDown");
		AddEvent("OnObjectRightMouseButtonUp");
		AddEvent("OnDoTileBuilding");
		AddEvent("OnDoTileBuilding2");
		AddEvent("OnDoTileBuilding3");
		AddEvent("OnConnectFailed");
		AddEvent("OnConnected");
		AddEvent("OnDisconnect");
		AddEvent("OnConnectionStateChanged");
		AddEvent("OnScoreboardUpdate");
		AddEvent("OnMouseMove");
		AddEvent("OnMouseDown");
		AddEvent("OnMouseUp");
		AddEvent("OnRightMouseDown");
		AddEvent("OnRightMouseUp");
		AddEvent("OnNewSurvivorGroup");
		AddEvent("OnPlayerSetSafehouse");
		AddEvent("OnLoad");
		AddEvent("AddXP");
		AddEvent("LevelPerk");
		AddEvent("OnSave");
		AddEvent("OnMainMenuEnter");
		AddEvent("OnPreMapLoad");
		AddEvent("OnPostFloorSquareDraw");
		AddEvent("OnPostFloorLayerDraw");
		AddEvent("OnPostTilesSquareDraw");
		AddEvent("OnPostTileDraw");
		AddEvent("OnPostWallSquareDraw");
		AddEvent("OnPostCharactersSquareDraw");
		AddEvent("OnCreateUI");
		AddEvent("OnMapLoadCreateIsoObject");
		AddEvent("OnCreateSurvivor");
		AddEvent("OnCreatePlayer");
		AddEvent("OnPlayerDeath");
		AddEvent("OnZombieDead");
		AddEvent("OnCharacterDeath");
		AddEvent("OnCharacterMeet");
		AddEvent("OnSpawnRegionsLoaded");
		AddEvent("OnPostMapLoad");
		AddEvent("OnAIStateExecute");
		AddEvent("OnAIStateEnter");
		AddEvent("OnAIStateExit");
		AddEvent("OnAIStateChange");
		AddEvent("OnPlayerMove");
		AddEvent("OnInitWorld");
		AddEvent("OnNewGame");
		AddEvent("OnIsoThumpableLoad");
		AddEvent("OnIsoThumpableSave");
		AddEvent("ReuseGridsquare");
		AddEvent("LoadGridsquare");
		AddEvent("EveryOneMinute");
		AddEvent("EveryTenMinutes");
		AddEvent("EveryDays");
		AddEvent("EveryHours");
		AddEvent("OnDusk");
		AddEvent("OnDawn");
		AddEvent("OnEquipPrimary");
		AddEvent("OnEquipSecondary");
		AddEvent("OnClothingUpdated");
		AddEvent("OnWeatherPeriodStart");
		AddEvent("OnWeatherPeriodStage");
		AddEvent("OnWeatherPeriodComplete");
		AddEvent("OnWeatherPeriodStop");
		AddEvent("OnRainStart");
		AddEvent("OnRainStop");
		AddEvent("OnAmbientSound");
		AddEvent("OnWorldSound");
		AddEvent("OnResetLua");
		AddEvent("OnModsModified");
		AddEvent("OnSeeNewRoom");
		AddEvent("OnNewFire");
		AddEvent("OnFillContainer");
		AddEvent("OnChangeWeather");
		AddEvent("OnRenderTick");
		AddEvent("OnDestroyIsoThumpable");
		AddEvent("OnPostSave");
		AddEvent("OnResolutionChange");
		AddEvent("OnWaterAmountChange");
		AddEvent("OnClientCommand");
		AddEvent("OnServerCommand");
		AddEvent("OnContainerUpdate");
		AddEvent("OnObjectAdded");
		AddEvent("OnObjectAboutToBeRemoved");
		AddEvent("onLoadModDataFromServer");
		AddEvent("OnGameTimeLoaded");
		AddEvent("OnCGlobalObjectSystemInit");
		AddEvent("OnSGlobalObjectSystemInit");
		AddEvent("OnWorldMessage");
		AddEvent("OnKeyKeepPressed");
		AddEvent("SendCustomModData");
		AddEvent("ServerPinged");
		AddEvent("OnServerStarted");
		AddEvent("OnLoadedTileDefinitions");
		AddEvent("OnPostRender");
		AddEvent("DoSpecialTooltip");
		AddEvent("OnCoopJoinFailed");
		AddEvent("OnServerWorkshopItems");
		AddEvent("OnVehicleDamageTexture");
		AddEvent("OnCustomUIKey");
		AddEvent("OnCustomUIKeyPressed");
		AddEvent("OnCustomUIKeyReleased");
		AddEvent("OnDeviceText");
		AddEvent("OnRadioInteraction");
		AddEvent("OnLoadRadioScripts");
		AddEvent("OnAcceptInvite");
		AddEvent("OnCoopServerMessage");
		AddEvent("OnReceiveUserlog");
		AddEvent("OnAdminMessage");
		AddEvent("OnGetDBSchema");
		AddEvent("OnGetTableResult");
		AddEvent("ReceiveFactionInvite");
		AddEvent("AcceptedFactionInvite");
		AddEvent("ReceiveSafehouseInvite");
		AddEvent("AcceptedSafehouseInvite");
		AddEvent("ViewTickets");
		AddEvent("SyncFaction");
		AddEvent("OnReceiveItemListNet");
		AddEvent("OnMiniScoreboardUpdate");
		AddEvent("OnSafehousesChanged");
		AddEvent("RequestTrade");
		AddEvent("AcceptedTrade");
		AddEvent("TradingUIAddItem");
		AddEvent("TradingUIRemoveItem");
		AddEvent("TradingUIUpdateState");
		AddEvent("OnGridBurnt");
		AddEvent("OnPreDistributionMerge");
		AddEvent("OnDistributionMerge");
		AddEvent("OnPostDistributionMerge");
		AddEvent("MngInvReceiveItems");
		AddEvent("OnTileRemoved");
		AddEvent("OnServerStartSaving");
		AddEvent("OnServerFinishSaving");
		AddEvent("OnMechanicActionDone");
		AddEvent("OnClimateTick");
		AddEvent("OnThunderEvent");
		AddEvent("OnEnterVehicle");
		AddEvent("OnSteamGameJoin");
		AddEvent("OnTabAdded");
		AddEvent("OnSetDefaultTab");
		AddEvent("OnTabRemoved");
		AddEvent("OnAddMessage");
		AddEvent("SwitchChatStream");
		AddEvent("OnChatWindowInit");
		AddEvent("OnInitSeasons");
		AddEvent("OnClimateTickDebug");
		AddEvent("OnInitModdedWeatherStage");
		AddEvent("OnUpdateModdedWeatherStage");
		AddEvent("OnClimateManagerInit");
		AddEvent("OnPressReloadButton");
		AddEvent("OnPressRackButton");
		AddEvent("OnHitZombie");
		AddEvent("OnBeingHitByZombie");
		AddEvent("OnServerStatisticReceived");
		AddEvent("OnDynamicMovableRecipe");
		AddEvent("OnInitGlobalModData");
		AddEvent("OnReceiveGlobalModData");
		AddEvent("OnInitRecordedMedia");
		AddEvent("onUpdateIcon");
		AddEvent("preAddForageDefs");
		AddEvent("preAddSkillDefs");
		AddEvent("preAddZoneDefs");
		AddEvent("preAddCatDefs");
		AddEvent("preAddItemDefs");
		AddEvent("onAddForageDefs");
		AddEvent("onFillSearchIconContextMenu");
		AddEvent("onItemFall");
	}

	public static void clear() {
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		kahluaTable.rawset("Events", kahluaTable2);
		AddEvents();
	}

	public static void reroute(Prototype prototype, LuaClosure luaClosure) {
		for (int int1 = 0; int1 < EventList.size(); ++int1) {
			Event event = (Event)EventList.get(int1);
			for (int int2 = 0; int2 < event.callbacks.size(); ++int2) {
				LuaClosure luaClosure2 = (LuaClosure)event.callbacks.get(int2);
				if (luaClosure2.prototype.filename.equals(prototype.filename) && luaClosure2.prototype.name.equals(prototype.name)) {
					event.callbacks.set(int2, luaClosure);
				}
			}
		}
	}

	public static void Reset() {
		for (int int1 = 0; int1 < EventList.size(); ++int1) {
			Event event = (Event)EventList.get(int1);
			event.callbacks.clear();
		}

		EventList.clear();
		EventMap.clear();
	}

	public static void ResetCallbacks() {
		for (int int1 = 0; int1 < EventList.size(); ++int1) {
			Event event = (Event)EventList.get(int1);
			event.callbacks.clear();
		}
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		return 0;
	}

	private int OnTick(LuaCallFrame luaCallFrame, int int1) {
		return 0;
	}
}
