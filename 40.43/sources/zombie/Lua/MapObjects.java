package zombie.Lua;

import gnu.trove.list.array.TShortArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Prototype;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameServer;
import zombie.network.ServerMap;


public final class MapObjects {
	private static final HashMap onNew = new HashMap();
	private static final HashMap onLoad = new HashMap();
	private static final ArrayList tempObjects = new ArrayList();
	private static final Object[] params = new Object[1];

	private static MapObjects.Callback getOnNew(String string) {
		MapObjects.Callback callback = (MapObjects.Callback)onNew.get(string);
		if (callback == null) {
			callback = new MapObjects.Callback(string);
			onNew.put(string, callback);
		}

		return callback;
	}

	public static void OnNewWithSprite(String string, LuaClosure luaClosure, int int1) {
		if (string != null && !string.isEmpty()) {
			if (luaClosure == null) {
				throw new NullPointerException("function is null");
			} else {
				MapObjects.Callback callback = getOnNew(string);
				for (int int2 = 0; int2 < callback.functions.size(); ++int2) {
					if (callback.priority.get(int2) < int1) {
						callback.functions.add(int2, luaClosure);
						callback.priority.insert(int2, (short)int1);
						return;
					}

					if (callback.priority.get(int2) == int1) {
						callback.functions.set(int2, luaClosure);
						callback.priority.set(int2, (short)int1);
						return;
					}
				}

				callback.functions.add(luaClosure);
				callback.priority.add((short)int1);
			}
		} else {
			throw new IllegalArgumentException("invalid sprite name");
		}
	}

	public static void OnNewWithSprite(KahluaTable kahluaTable, LuaClosure luaClosure, int int1) {
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			if (luaClosure == null) {
				throw new NullPointerException("function is null");
			} else {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					Object object = kahluaTableIterator.getValue();
					if (!(object instanceof String)) {
						throw new IllegalArgumentException("expected string but got \"" + object + "\"");
					}

					OnNewWithSprite((String)object, luaClosure, int1);
				}
			}
		} else {
			throw new IllegalArgumentException("invalid sprite-name table");
		}
	}

	public static void newGridSquare(IsoGridSquare square) {
		if (square != null && !square.getObjects().isEmpty()) {
			tempObjects.clear();
			int int1;
			for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
				tempObjects.add(square.getObjects().get(int1));
			}

			for (int1 = 0; int1 < tempObjects.size(); ++int1) {
				IsoObject object = (IsoObject)tempObjects.get(int1);
				if (square.getObjects().contains(object) && !(object instanceof IsoWorldInventoryObject) && object != null && object.sprite != null) {
					String string = object.sprite.name == null ? object.spriteName : object.sprite.name;
					if (string != null && !string.isEmpty()) {
						MapObjects.Callback callback = (MapObjects.Callback)onNew.get(string);
						if (callback != null) {
							params[0] = object;
							for (int int2 = 0; int2 < callback.functions.size(); ++int2) {
								try {
									LuaManager.caller.protectedCallVoid(LuaManager.thread, callback.functions.get(int2), params);
								} catch (Throwable throwable) {
									ExceptionLogger.logException(throwable);
								}

								string = object.sprite != null && object.sprite.name != null ? object.sprite.name : object.spriteName;
								if (!square.getObjects().contains(object) || object.sprite == null || !callback.spriteName.equals(string)) {
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	private static MapObjects.Callback getOnLoad(String string) {
		MapObjects.Callback callback = (MapObjects.Callback)onLoad.get(string);
		if (callback == null) {
			callback = new MapObjects.Callback(string);
			onLoad.put(string, callback);
		}

		return callback;
	}

	public static void OnLoadWithSprite(String string, LuaClosure luaClosure, int int1) {
		if (string != null && !string.isEmpty()) {
			if (luaClosure == null) {
				throw new NullPointerException("function is null");
			} else {
				MapObjects.Callback callback = getOnLoad(string);
				for (int int2 = 0; int2 < callback.functions.size(); ++int2) {
					if (callback.priority.get(int2) < int1) {
						callback.functions.add(int2, luaClosure);
						callback.priority.insert(int2, (short)int1);
						return;
					}

					if (callback.priority.get(int2) == int1) {
						callback.functions.set(int2, luaClosure);
						callback.priority.set(int2, (short)int1);
						return;
					}
				}

				callback.functions.add(luaClosure);
				callback.priority.add((short)int1);
			}
		} else {
			throw new IllegalArgumentException("invalid sprite name");
		}
	}

	public static void OnLoadWithSprite(KahluaTable kahluaTable, LuaClosure luaClosure, int int1) {
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			if (luaClosure == null) {
				throw new NullPointerException("function is null");
			} else {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					Object object = kahluaTableIterator.getValue();
					if (!(object instanceof String)) {
						throw new IllegalArgumentException("expected string but got \"" + object + "\"");
					}

					OnLoadWithSprite((String)object, luaClosure, int1);
				}
			}
		} else {
			throw new IllegalArgumentException("invalid sprite-name table");
		}
	}

	public static void loadGridSquare(IsoGridSquare square) {
		if (square != null && !square.getObjects().isEmpty()) {
			tempObjects.clear();
			int int1;
			for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
				tempObjects.add(square.getObjects().get(int1));
			}

			for (int1 = 0; int1 < tempObjects.size(); ++int1) {
				IsoObject object = (IsoObject)tempObjects.get(int1);
				if (square.getObjects().contains(object) && !(object instanceof IsoWorldInventoryObject) && object != null && object.sprite != null) {
					String string = object.sprite.name == null ? object.spriteName : object.sprite.name;
					if (string != null && !string.isEmpty()) {
						MapObjects.Callback callback = (MapObjects.Callback)onLoad.get(string);
						if (callback != null) {
							params[0] = object;
							for (int int2 = 0; int2 < callback.functions.size(); ++int2) {
								try {
									LuaManager.caller.protectedCallVoid(LuaManager.thread, callback.functions.get(int2), params);
								} catch (Throwable throwable) {
									ExceptionLogger.logException(throwable);
								}

								string = object.sprite != null && object.sprite.name != null ? object.sprite.name : object.spriteName;
								if (!square.getObjects().contains(object) || object.sprite == null || !callback.spriteName.equals(string)) {
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	public static void debugNewSquare(int int1, int int2, int int3) {
		if (Core.bDebug) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null) {
				newGridSquare(square);
			}
		}
	}

	public static void debugLoadSquare(int int1, int int2, int int3) {
		if (Core.bDebug) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null) {
				loadGridSquare(square);
			}
		}
	}

	public static void debugLoadChunk(int int1, int int2) {
		if (Core.bDebug) {
			IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int1, int2) : IsoWorld.instance.CurrentCell.getChunk(int1, int2);
			if (chunk != null) {
				for (int int3 = 0; int3 <= chunk.maxLevel; ++int3) {
					for (int int4 = 0; int4 < 10; ++int4) {
						for (int int5 = 0; int5 < 10; ++int5) {
							IsoGridSquare square = chunk.getGridSquare(int4, int5, int3);
							if (square != null && !square.getObjects().isEmpty()) {
								loadGridSquare(square);
							}
						}
					}
				}
			}
		}
	}

	public static void reroute(Prototype prototype, LuaClosure luaClosure) {
		Iterator iterator = onNew.values().iterator();
		while (iterator.hasNext()) {
			MapObjects.Callback callback = (MapObjects.Callback)iterator.next();
			for (int int1 = 0; int1 < callback.functions.size(); ++int1) {
				LuaClosure luaClosure2 = (LuaClosure)callback.functions.get(int1);
				if (luaClosure2.prototype.filename.equals(prototype.filename) && luaClosure2.prototype.name.equals(prototype.name)) {
					callback.functions.set(int1, luaClosure);
				}
			}
		}
	}

	public static void Reset() {
		onNew.clear();
		onLoad.clear();
	}

	private static final class Callback {
		final String spriteName;
		final ArrayList functions = new ArrayList();
		final TShortArrayList priority = new TShortArrayList();

		Callback(String string) {
			this.spriteName = string;
		}
	}
}
