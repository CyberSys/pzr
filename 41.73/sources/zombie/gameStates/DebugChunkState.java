package zombie.gameStates;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import org.joml.Vector2f;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.FliesSound;
import zombie.VirtualZombieManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.Texture;
import zombie.core.utils.BooleanGrid;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionData;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.NearestWalls;
import zombie.iso.ParticlesFire;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.randomizedWorld.randomizedVehicleStory.VehicleStorySpawner;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.ClipperOffset;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PolygonalMap2;


public final class DebugChunkState extends GameState {
	public static DebugChunkState instance;
	private EditVehicleState.LuaEnvironment m_luaEnv;
	private boolean bExit = false;
	private final ArrayList m_gameUI = new ArrayList();
	private final ArrayList m_selfUI = new ArrayList();
	private boolean m_bSuspendUI;
	private KahluaTable m_table = null;
	private int m_playerIndex = 0;
	private int m_z = 0;
	private int gridX = -1;
	private int gridY = -1;
	private UIFont FONT;
	private String m_vehicleStory;
	static boolean keyQpressed = false;
	private static ClipperOffset m_clipperOffset = null;
	private static ByteBuffer m_clipperBuffer;
	private static final int VERSION = 1;
	private final ArrayList options;
	private DebugChunkState.BooleanDebugOption BuildingRect;
	private DebugChunkState.BooleanDebugOption ChunkGrid;
	private DebugChunkState.BooleanDebugOption ClosestRoomSquare;
	private DebugChunkState.BooleanDebugOption EmptySquares;
	private DebugChunkState.BooleanDebugOption FlyBuzzEmitters;
	private DebugChunkState.BooleanDebugOption LightSquares;
	private DebugChunkState.BooleanDebugOption LineClearCollide;
	private DebugChunkState.BooleanDebugOption NearestWallsOpt;
	private DebugChunkState.BooleanDebugOption ObjectPicker;
	private DebugChunkState.BooleanDebugOption RoomLightRects;
	private DebugChunkState.BooleanDebugOption VehicleStory;
	private DebugChunkState.BooleanDebugOption RandomSquareInZone;
	private DebugChunkState.BooleanDebugOption ZoneRect;

	public DebugChunkState() {
		this.FONT = UIFont.DebugConsole;
		this.m_vehicleStory = "Basic Car Crash";
		this.options = new ArrayList();
		this.BuildingRect = new DebugChunkState.BooleanDebugOption("BuildingRect", true);
		this.ChunkGrid = new DebugChunkState.BooleanDebugOption("ChunkGrid", true);
		this.ClosestRoomSquare = new DebugChunkState.BooleanDebugOption("ClosestRoomSquare", true);
		this.EmptySquares = new DebugChunkState.BooleanDebugOption("EmptySquares", true);
		this.FlyBuzzEmitters = new DebugChunkState.BooleanDebugOption("FlyBuzzEmitters", true);
		this.LightSquares = new DebugChunkState.BooleanDebugOption("LightSquares", true);
		this.LineClearCollide = new DebugChunkState.BooleanDebugOption("LineClearCollide", true);
		this.NearestWallsOpt = new DebugChunkState.BooleanDebugOption("NearestWalls", true);
		this.ObjectPicker = new DebugChunkState.BooleanDebugOption("ObjectPicker", true);
		this.RoomLightRects = new DebugChunkState.BooleanDebugOption("RoomLightRects", true);
		this.VehicleStory = new DebugChunkState.BooleanDebugOption("VehicleStory", true);
		this.RandomSquareInZone = new DebugChunkState.BooleanDebugOption("RandomSquareInZone", true);
		this.ZoneRect = new DebugChunkState.BooleanDebugOption("ZoneRect", true);
		instance = this;
	}

	public void enter() {
		instance = this;
		this.load();
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			IsoPlayer player = IsoPlayer.players[this.m_playerIndex];
			this.m_z = player == null ? 0 : (int)player.z;
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("DebugChunkState_InitUI"), (Object)this);
			if (this.m_table != null && this.m_table.getMetatable() != null) {
				this.m_table.getMetatable().rawset("_LUA_RELOADED_CHECK", Boolean.FALSE);
			}
		} else {
			UIManager.UI.addAll(this.m_selfUI);
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_table.rawget("showUI"), (Object)this.m_table);
		}

		this.bExit = false;
	}

	public void yield() {
		this.restoreGameUI();
	}

	public void reenter() {
		this.saveGameUI();
	}

	public void exit() {
		this.save();
		this.restoreGameUI();
		for (int int1 = 0; int1 < IsoCamera.cameras.length; ++int1) {
			IsoCamera.cameras[int1].DeferedX = IsoCamera.cameras[int1].DeferedY = 0.0F;
		}
	}

	public void render() {
		IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
		IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
		boolean boolean1 = true;
		int int1;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (int1 != this.m_playerIndex && IsoPlayer.players[int1] != null) {
				Core.getInstance().StartFrame(int1, boolean1);
				Core.getInstance().EndFrame(int1);
				boolean1 = false;
			}
		}

		Core.getInstance().StartFrame(this.m_playerIndex, boolean1);
		this.renderScene();
		Core.getInstance().EndFrame(this.m_playerIndex);
		Core.getInstance().RenderOffScreenBuffer();
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			TextDrawObject.NoRender(int1);
			ChatElement.NoRender(int1);
		}

		if (Core.getInstance().StartFrameUI()) {
			this.renderUI();
		}

		Core.getInstance().EndFrameUI();
	}

	public GameStateMachine.StateAction update() {
		return !this.bExit && !GameKeyboard.isKeyPressed(60) ? this.updateScene() : GameStateMachine.StateAction.Continue;
	}

	public static DebugChunkState checkInstance() {
		instance = null;
		if (instance != null) {
			if (instance.m_table != null && instance.m_table.getMetatable() != null) {
				if (instance.m_table.getMetatable().rawget("_LUA_RELOADED_CHECK") == null) {
					instance = null;
				}
			} else {
				instance = null;
			}
		}

		return instance == null ? new DebugChunkState() : instance;
	}

	public void renderScene() {
		IsoCamera.frameState.set(this.m_playerIndex);
		SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
		SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
		SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
		IsoSprite.globalOffsetX = -1.0F;
		IsoWorld.instance.CurrentCell.render();
		if (this.ChunkGrid.getValue()) {
			this.drawGrid();
		}

		this.drawCursor();
		int int1;
		if (this.LightSquares.getValue()) {
			Stack stack = IsoWorld.instance.getCell().getLamppostPositions();
			for (int1 = 0; int1 < stack.size(); ++int1) {
				IsoLightSource lightSource = (IsoLightSource)stack.get(int1);
				if (lightSource.z == this.m_z) {
					this.paintSquare(lightSource.x, lightSource.y, lightSource.z, 1.0F, 1.0F, 0.0F, 0.5F);
				}
			}
		}

		if (this.ZoneRect.getValue()) {
			this.drawZones();
		}

		IsoGridSquare square;
		if (this.BuildingRect.getValue()) {
			square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, this.m_z);
			if (square != null && square.getBuilding() != null) {
				BuildingDef buildingDef = square.getBuilding().getDef();
				this.DrawIsoLine((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getX2(), (float)buildingDef.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
				this.DrawIsoLine((float)buildingDef.getX2(), (float)buildingDef.getY(), (float)buildingDef.getX2(), (float)buildingDef.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
				this.DrawIsoLine((float)buildingDef.getX2(), (float)buildingDef.getY2(), (float)buildingDef.getX(), (float)buildingDef.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
				this.DrawIsoLine((float)buildingDef.getX(), (float)buildingDef.getY2(), (float)buildingDef.getX(), (float)buildingDef.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
			}
		}

		if (this.RoomLightRects.getValue()) {
			ArrayList arrayList = IsoWorld.instance.CurrentCell.roomLights;
			for (int1 = 0; int1 < arrayList.size(); ++int1) {
				IsoRoomLight roomLight = (IsoRoomLight)arrayList.get(int1);
				if (roomLight.z == this.m_z) {
					this.DrawIsoRect((float)roomLight.x, (float)roomLight.y, (float)roomLight.width, (float)roomLight.height, 0.0F, 1.0F, 1.0F, 1.0F, 1);
				}
			}
		}

		if (this.FlyBuzzEmitters.getValue()) {
			FliesSound.instance.render();
		}

		if (this.ClosestRoomSquare.getValue()) {
			float float1 = IsoPlayer.players[this.m_playerIndex].getX();
			float float2 = IsoPlayer.players[this.m_playerIndex].getY();
			Vector2f vector2f = new Vector2f();
			BuildingDef buildingDef2 = ((AmbientStreamManager)AmbientStreamManager.getInstance()).getNearestBuilding(float1, float2, vector2f);
			if (buildingDef2 != null) {
				this.DrawIsoLine(float1, float2, vector2f.x, vector2f.y, 1.0F, 1.0F, 1.0F, 1.0F, 1);
			}
		}

		if (this.m_table != null && this.m_table.rawget("selectedSquare") != null) {
			square = (IsoGridSquare)Type.tryCastTo(this.m_table.rawget("selectedSquare"), IsoGridSquare.class);
			if (square != null) {
				this.DrawIsoRect((float)square.x, (float)square.y, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 2);
			}
		}

		LineDrawer.render();
		LineDrawer.clear();
	}

	private void renderUI() {
		int int1 = this.m_playerIndex;
		Stack stack = IsoWorld.instance.getCell().getLamppostPositions();
		int int2 = 0;
		for (int int3 = 0; int3 < stack.size(); ++int3) {
			IsoLightSource lightSource = (IsoLightSource)stack.get(int3);
			if (lightSource.bActive) {
				++int2;
			}
		}

		UIManager.render();
	}

	public void setTable(KahluaTable kahluaTable) {
		this.m_table = kahluaTable;
	}

	public GameStateMachine.StateAction updateScene() {
		IsoPlayer.setInstance(IsoPlayer.players[this.m_playerIndex]);
		IsoCamera.CamCharacter = IsoPlayer.players[this.m_playerIndex];
		UIManager.setPicked(IsoObjectPicker.Instance.ContextPick(Mouse.getXA(), Mouse.getYA()));
		IsoObject object = UIManager.getPicked() == null ? null : UIManager.getPicked().tile;
		UIManager.setLastPicked(object);
		if (GameKeyboard.isKeyDown(16)) {
			if (!keyQpressed) {
				IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, 0);
				if (square != null) {
					GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(square.chunk);
					DebugLog.General.debugln("Requesting sync IsoChunk %s", square.chunk);
				}

				keyQpressed = true;
			}
		} else {
			keyQpressed = false;
		}

		if (GameKeyboard.isKeyDown(19)) {
			if (!keyQpressed) {
				DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(true);
				keyQpressed = true;
				DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
			}
		} else {
			keyQpressed = false;
		}

		if (GameKeyboard.isKeyDown(20)) {
			if (!keyQpressed) {
				DebugOptions.instance.Terrain.RenderTiles.NewRender.setValue(false);
				keyQpressed = true;
				DebugLog.General.debugln("IsoCell.newRender = %s", DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue());
			}
		} else {
			keyQpressed = false;
		}

		if (GameKeyboard.isKeyDown(31)) {
			if (!keyQpressed) {
				ParticlesFire.getInstance().reloadShader();
				keyQpressed = true;
				DebugLog.General.debugln("ParticlesFire.reloadShader");
			}
		} else {
			keyQpressed = false;
		}

		IsoCamera.update();
		this.updateCursor();
		return GameStateMachine.StateAction.Remain;
	}

	private void saveGameUI() {
		this.m_gameUI.clear();
		this.m_gameUI.addAll(UIManager.UI);
		UIManager.UI.clear();
		this.m_bSuspendUI = UIManager.bSuspend;
		UIManager.bSuspend = false;
		UIManager.setShowPausedMessage(false);
		UIManager.defaultthread = this.m_luaEnv.thread;
	}

	private void restoreGameUI() {
		this.m_selfUI.clear();
		this.m_selfUI.addAll(UIManager.UI);
		UIManager.UI.clear();
		UIManager.UI.addAll(this.m_gameUI);
		UIManager.bSuspend = this.m_bSuspendUI;
		UIManager.setShowPausedMessage(true);
		UIManager.defaultthread = LuaManager.thread;
	}

	public Object fromLua0(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -414341217: 
			if (string.equals("getVehicleStory")) {
				byte1 = 4;
			}

			break;
		
		case -103642821: 
			if (string.equals("getPlayerIndex")) {
				byte1 = 3;
			}

			break;
		
		case 3127582: 
			if (string.equals("exit")) {
				byte1 = 0;
			}

			break;
		
		case 3169220: 
			if (string.equals("getZ")) {
				byte1 = 5;
			}

			break;
		
		case 1393900617: 
			if (string.equals("getCameraDragX")) {
				byte1 = 1;
			}

			break;
		
		case 1393900618: 
			if (string.equals("getCameraDragY")) {
				byte1 = 2;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.bExit = true;
			return null;
		
		case 1: 
			return BoxedStaticValues.toDouble((double)(-IsoCamera.cameras[this.m_playerIndex].DeferedX));
		
		case 2: 
			return BoxedStaticValues.toDouble((double)(-IsoCamera.cameras[this.m_playerIndex].DeferedY));
		
		case 3: 
			return BoxedStaticValues.toDouble((double)this.m_playerIndex);
		
		case 4: 
			return this.m_vehicleStory;
		
		case 5: 
			return BoxedStaticValues.toDouble((double)this.m_z);
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\"", string));
		
		}
	}

	public Object fromLua1(String string, Object object) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1875379025: 
			if (string.equals("setPlayerIndex")) {
				byte1 = 2;
			}

			break;
		
		case 3526712: 
			if (string.equals("setZ")) {
				byte1 = 4;
			}

			break;
		
		case 496411307: 
			if (string.equals("setVehicleStory")) {
				byte1 = 3;
			}

			break;
		
		case 1393900617: 
			if (string.equals("getCameraDragX")) {
				byte1 = 0;
			}

			break;
		
		case 1393900618: 
			if (string.equals("getCameraDragY")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
			return BoxedStaticValues.toDouble((double)(-IsoCamera.cameras[this.m_playerIndex].DeferedX));
		
		case 1: 
			return BoxedStaticValues.toDouble((double)(-IsoCamera.cameras[this.m_playerIndex].DeferedY));
		
		case 2: 
			this.m_playerIndex = PZMath.clamp(((Double)object).intValue(), 0, 3);
			return null;
		
		case 3: 
			this.m_vehicleStory = (String)object;
			return null;
		
		case 4: 
			this.m_z = PZMath.clamp(((Double)object).intValue(), 0, 7);
			return null;
		
		default: 
			throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\"", string, object));
		
		}
	}

	public Object fromLua2(String string, Object object, Object object2) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1879300743: 
			if (string.equals("dragCamera")) {
				byte1 = 0;
			}

		
		default: 
			switch (byte1) {
			case 0: 
				float float1 = ((Double)object).floatValue();
				float float2 = ((Double)object2).floatValue();
				IsoCamera.cameras[this.m_playerIndex].DeferedX = -float1;
				IsoCamera.cameras[this.m_playerIndex].DeferedY = -float2;
				return null;
			
			default: 
				throw new IllegalArgumentException(String.format("unhandled \"%s\" \"%s\" \\\"%s\\\"", string, object, object2));
			
			}

		
		}
	}

	private void updateCursor() {
		int int1 = this.m_playerIndex;
		int int2 = Core.TileScale;
		float float1 = (float)Mouse.getXA();
		float float2 = (float)Mouse.getYA();
		float1 -= (float)IsoCamera.getScreenLeft(int1);
		float2 -= (float)IsoCamera.getScreenTop(int1);
		float1 *= Core.getInstance().getZoom(int1);
		float2 *= Core.getInstance().getZoom(int1);
		int int3 = this.m_z;
		this.gridX = (int)IsoUtils.XToIso(float1, float2, (float)int3);
		this.gridY = (int)IsoUtils.YToIso(float1, float2, (float)int3);
	}

	private void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		float float9 = (float)this.m_z;
		float float10 = IsoUtils.XToScreenExact(float1, float2, float9, 0);
		float float11 = IsoUtils.YToScreenExact(float1, float2, float9, 0);
		float float12 = IsoUtils.XToScreenExact(float3, float4, float9, 0);
		float float13 = IsoUtils.YToScreenExact(float3, float4, float9, 0);
		LineDrawer.drawLine(float10, float11, float12, float13, float5, float6, float7, float8, int1);
	}

	private void DrawIsoRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, int int1) {
		this.DrawIsoLine(float1, float2, float1 + float3, float2, float5, float6, float7, float8, int1);
		this.DrawIsoLine(float1 + float3, float2, float1 + float3, float2 + float4, float5, float6, float7, float8, int1);
		this.DrawIsoLine(float1 + float3, float2 + float4, float1, float2 + float4, float5, float6, float7, float8, int1);
		this.DrawIsoLine(float1, float2 + float4, float1, float2, float5, float6, float7, float8, int1);
	}

	private void drawGrid() {
		int int1 = this.m_playerIndex;
		float float1 = IsoUtils.XToIso(-128.0F, -256.0F, 0.0F);
		float float2 = IsoUtils.YToIso((float)(Core.getInstance().getOffscreenWidth(int1) + 128), -256.0F, 0.0F);
		float float3 = IsoUtils.XToIso((float)(Core.getInstance().getOffscreenWidth(int1) + 128), (float)(Core.getInstance().getOffscreenHeight(int1) + 256), 6.0F);
		float float4 = IsoUtils.YToIso(-128.0F, (float)(Core.getInstance().getOffscreenHeight(int1) + 256), 6.0F);
		int int2 = (int)float2;
		int int3 = (int)float4;
		int int4 = (int)float1;
		int int5 = (int)float3;
		int4 -= 2;
		int2 -= 2;
		int int6;
		for (int6 = int2; int6 <= int3; ++int6) {
			if (int6 % 10 == 0) {
				this.DrawIsoLine((float)int4, (float)int6, (float)int5, (float)int6, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			}
		}

		for (int6 = int4; int6 <= int5; ++int6) {
			if (int6 % 10 == 0) {
				this.DrawIsoLine((float)int6, (float)int2, (float)int6, (float)int3, 1.0F, 1.0F, 1.0F, 0.5F, 1);
			}
		}

		for (int6 = int2; int6 <= int3; ++int6) {
			if (int6 % 300 == 0) {
				this.DrawIsoLine((float)int4, (float)int6, (float)int5, (float)int6, 0.0F, 1.0F, 0.0F, 0.5F, 1);
			}
		}

		for (int6 = int4; int6 <= int5; ++int6) {
			if (int6 % 300 == 0) {
				this.DrawIsoLine((float)int6, (float)int2, (float)int6, (float)int3, 0.0F, 1.0F, 0.0F, 0.5F, 1);
			}
		}

		if (GameClient.bClient) {
			for (int6 = int2; int6 <= int3; ++int6) {
				if (int6 % 50 == 0) {
					this.DrawIsoLine((float)int4, (float)int6, (float)int5, (float)int6, 1.0F, 0.0F, 0.0F, 0.5F, 1);
				}
			}

			for (int6 = int4; int6 <= int5; ++int6) {
				if (int6 % 50 == 0) {
					this.DrawIsoLine((float)int6, (float)int2, (float)int6, (float)int3, 1.0F, 0.0F, 0.0F, 0.5F, 1);
				}
			}
		}
	}

	private void drawCursor() {
		int int1 = this.m_playerIndex;
		int int2 = Core.TileScale;
		float float1 = (float)this.m_z;
		int int3 = (int)IsoUtils.XToScreenExact((float)this.gridX, (float)(this.gridY + 1), float1, 0);
		int int4 = (int)IsoUtils.YToScreenExact((float)this.gridX, (float)(this.gridY + 1), float1, 0);
		SpriteRenderer.instance.renderPoly((float)int3, (float)int4, (float)(int3 + 32 * int2), (float)(int4 - 16 * int2), (float)(int3 + 64 * int2), (float)int4, (float)(int3 + 32 * int2), (float)(int4 + 16 * int2), 0.0F, 0.0F, 1.0F, 0.5F);
		IsoChunkMap chunkMap = IsoWorld.instance.getCell().ChunkMap[int1];
		for (int int5 = chunkMap.getWorldYMinTiles(); int5 < chunkMap.getWorldYMaxTiles(); ++int5) {
			for (int int6 = chunkMap.getWorldXMinTiles(); int6 < chunkMap.getWorldXMaxTiles(); ++int6) {
				IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare((double)int6, (double)int5, (double)float1);
				if (square != null) {
					if (square != chunkMap.getGridSquare(int6, int5, (int)float1)) {
						int3 = (int)IsoUtils.XToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						int4 = (int)IsoUtils.YToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly((float)int3, (float)int4, (float)(int3 + 32), (float)(int4 - 16), (float)(int3 + 64), (float)int4, (float)(int3 + 32), (float)(int4 + 16), 1.0F, 0.0F, 0.0F, 0.8F);
					}

					if (square == null || square.getX() != int6 || square.getY() != int5 || (float)square.getZ() != float1 || square.e != null && square.e.w != null && square.e.w != square || square.w != null && square.w.e != null && square.w.e != square || square.n != null && square.n.s != null && square.n.s != square || square.s != null && square.s.n != null && square.s.n != square || square.nw != null && square.nw.se != null && square.nw.se != square || square.se != null && square.se.nw != null && square.se.nw != square) {
						int3 = (int)IsoUtils.XToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						int4 = (int)IsoUtils.YToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly((float)int3, (float)int4, (float)(int3 + 32), (float)(int4 - 16), (float)(int3 + 64), (float)int4, (float)(int3 + 32), (float)(int4 + 16), 1.0F, 0.0F, 0.0F, 0.5F);
					}

					if (square != null) {
						IsoGridSquare square2 = square.testPathFindAdjacent((IsoMovingObject)null, -1, 0, 0) ? null : square.nav[IsoDirections.W.index()];
						IsoGridSquare square3 = square.testPathFindAdjacent((IsoMovingObject)null, 0, -1, 0) ? null : square.nav[IsoDirections.N.index()];
						IsoGridSquare square4 = square.testPathFindAdjacent((IsoMovingObject)null, 1, 0, 0) ? null : square.nav[IsoDirections.E.index()];
						IsoGridSquare square5 = square.testPathFindAdjacent((IsoMovingObject)null, 0, 1, 0) ? null : square.nav[IsoDirections.S.index()];
						IsoGridSquare square6 = square.testPathFindAdjacent((IsoMovingObject)null, -1, -1, 0) ? null : square.nav[IsoDirections.NW.index()];
						IsoGridSquare square7 = square.testPathFindAdjacent((IsoMovingObject)null, 1, -1, 0) ? null : square.nav[IsoDirections.NE.index()];
						IsoGridSquare square8 = square.testPathFindAdjacent((IsoMovingObject)null, -1, 1, 0) ? null : square.nav[IsoDirections.SW.index()];
						IsoGridSquare square9 = square.testPathFindAdjacent((IsoMovingObject)null, 1, 1, 0) ? null : square.nav[IsoDirections.SE.index()];
						if (square2 != square.w || square3 != square.n || square4 != square.e || square5 != square.s || square6 != square.nw || square7 != square.ne || square8 != square.sw || square9 != square.se) {
							this.paintSquare(int6, int5, (int)float1, 1.0F, 0.0F, 0.0F, 0.5F);
						}
					}

					if (square != null && (square.nav[IsoDirections.NW.index()] != null && square.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] != square || square.nav[IsoDirections.NE.index()] != null && square.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] != square || square.nav[IsoDirections.SW.index()] != null && square.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] != square || square.nav[IsoDirections.SE.index()] != null && square.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] != square || square.nav[IsoDirections.N.index()] != null && square.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] != square || square.nav[IsoDirections.S.index()] != null && square.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] != square || square.nav[IsoDirections.W.index()] != null && square.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] != square || square.nav[IsoDirections.E.index()] != null && square.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] != square)) {
						int3 = (int)IsoUtils.XToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						int4 = (int)IsoUtils.YToScreenExact((float)int6, (float)(int5 + 1), float1, 0);
						SpriteRenderer.instance.renderPoly((float)int3, (float)int4, (float)(int3 + 32), (float)(int4 - 16), (float)(int3 + 64), (float)int4, (float)(int3 + 32), (float)(int4 + 16), 1.0F, 0.0F, 0.0F, 0.5F);
					}

					if (this.EmptySquares.getValue() && square.getObjects().isEmpty()) {
						this.paintSquare(int6, int5, (int)float1, 1.0F, 1.0F, 0.0F, 0.5F);
					}

					if (square.getRoom() != null && square.isFree(false) && !VirtualZombieManager.instance.canSpawnAt(int6, int5, (int)float1)) {
						this.paintSquare(int6, int5, (int)float1, 1.0F, 1.0F, 1.0F, 1.0F);
					}

					if (square.roofHideBuilding != null) {
						this.paintSquare(int6, int5, (int)float1, 0.0F, 0.0F, 1.0F, 0.25F);
					}
				}
			}
		}

		if (IsoCamera.CamCharacter.getCurrentSquare() != null && Math.abs(this.gridX - (int)IsoCamera.CamCharacter.x) <= 1 && Math.abs(this.gridY - (int)IsoCamera.CamCharacter.y) <= 1) {
			IsoGridSquare square10 = IsoWorld.instance.CurrentCell.getGridSquare(this.gridX, this.gridY, this.m_z);
			IsoObject object = IsoCamera.CamCharacter.getCurrentSquare().testCollideSpecialObjects(square10);
			if (object != null) {
				object.getSprite().RenderGhostTileRed((int)object.getX(), (int)object.getY(), (int)object.getZ());
			}
		}

		if (this.LineClearCollide.getValue()) {
			this.lineClearCached(IsoWorld.instance.CurrentCell, this.gridX, this.gridY, (int)float1, (int)IsoCamera.CamCharacter.getX(), (int)IsoCamera.CamCharacter.getY(), this.m_z, false);
		}

		if (this.NearestWallsOpt.getValue()) {
			NearestWalls.render(this.gridX, this.gridY, this.m_z);
		}

		if (this.VehicleStory.getValue()) {
			this.drawVehicleStory();
		}
	}

	private void drawZones() {
		ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesAt(this.gridX, this.gridY, this.m_z, new ArrayList());
		IsoMetaGrid.Zone zone = null;
		int int1;
		int int2;
		int int3;
		for (int int4 = 0; int4 < arrayList.size(); ++int4) {
			IsoMetaGrid.Zone zone2 = (IsoMetaGrid.Zone)arrayList.get(int4);
			if (zone2.isPreferredZoneForSquare) {
				zone = zone2;
			}

			if (!zone2.isPolyline()) {
				if (!zone2.points.isEmpty()) {
					for (int int5 = 0; int5 < zone2.points.size(); int5 += 2) {
						int1 = zone2.points.get(int5);
						int int6 = zone2.points.get(int5 + 1);
						int2 = zone2.points.get((int5 + 2) % zone2.points.size());
						int3 = zone2.points.get((int5 + 3) % zone2.points.size());
						this.DrawIsoLine((float)int1, (float)int6, (float)int2, (float)int3, 1.0F, 1.0F, 0.0F, 1.0F, 1);
					}
				} else {
					this.DrawIsoLine((float)zone2.x, (float)zone2.y, (float)(zone2.x + zone2.w), (float)zone2.y, 1.0F, 1.0F, 0.0F, 1.0F, 1);
					this.DrawIsoLine((float)zone2.x, (float)(zone2.y + zone2.h), (float)(zone2.x + zone2.w), (float)(zone2.y + zone2.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
					this.DrawIsoLine((float)zone2.x, (float)zone2.y, (float)zone2.x, (float)(zone2.y + zone2.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
					this.DrawIsoLine((float)(zone2.x + zone2.w), (float)zone2.y, (float)(zone2.x + zone2.w), (float)(zone2.y + zone2.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
				}
			}
		}

		arrayList = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList());
		PolygonalMap2.LiangBarsky liangBarsky = new PolygonalMap2.LiangBarsky();
		double[] doubleArray = new double[2];
		IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);
		int int7;
		int int8;
		float float1;
		float float2;
		float float3;
		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoMetaGrid.Zone zone3 = (IsoMetaGrid.Zone)arrayList.get(int1);
			if (zone3 != null && zone3.isPolyline() && !zone3.points.isEmpty()) {
				for (int2 = 0; int2 < zone3.points.size() - 2; int2 += 2) {
					int3 = zone3.points.get(int2);
					int int9 = zone3.points.get(int2 + 1);
					int7 = zone3.points.get(int2 + 2);
					int8 = zone3.points.get(int2 + 3);
					this.DrawIsoLine((float)int3, (float)int9, (float)int7, (float)int8, 1.0F, 1.0F, 0.0F, 1.0F, 1);
					float1 = (float)(int7 - int3);
					float2 = (float)(int8 - int9);
					if (chunk != null && liangBarsky.lineRectIntersect((float)int3, (float)int9, float1, float2, (float)(chunk.wx * 10), (float)(chunk.wy * 10), (float)(chunk.wx * 10 + 10), (float)(chunk.wy * 10 + 10), doubleArray)) {
						this.DrawIsoLine((float)int3 + (float)doubleArray[0] * float1, (float)int9 + (float)doubleArray[0] * float2, (float)int3 + (float)doubleArray[1] * float1, (float)int9 + (float)doubleArray[1] * float2, 0.0F, 1.0F, 0.0F, 1.0F, 1);
					}
				}

				if (zone3.polylineOutlinePoints != null) {
					float[] floatArray = zone3.polylineOutlinePoints;
					for (int3 = 0; int3 < floatArray.length; int3 += 2) {
						float3 = floatArray[int3];
						float float4 = floatArray[int3 + 1];
						float float5 = floatArray[(int3 + 2) % floatArray.length];
						float1 = floatArray[(int3 + 3) % floatArray.length];
						this.DrawIsoLine(float3, float4, float5, float1, 1.0F, 1.0F, 0.0F, 1.0F, 1);
					}
				}
			}
		}

		IsoMetaGrid.VehicleZone vehicleZone = IsoWorld.instance.MetaGrid.getVehicleZoneAt(this.gridX, this.gridY, this.m_z);
		if (vehicleZone != null) {
			float float6 = 0.5F;
			float float7 = 1.0F;
			float float8 = 0.5F;
			float3 = 1.0F;
			int int10;
			int int11;
			int int12;
			if (vehicleZone.isPolygon()) {
				for (int7 = 0; int7 < vehicleZone.points.size(); int7 += 2) {
					int8 = vehicleZone.points.get(int7);
					int11 = vehicleZone.points.get(int7 + 1);
					int12 = vehicleZone.points.get((int7 + 2) % vehicleZone.points.size());
					int10 = vehicleZone.points.get((int7 + 3) % vehicleZone.points.size());
					this.DrawIsoLine((float)int8, (float)int11, (float)int12, (float)int10, 1.0F, 1.0F, 0.0F, 1.0F, 1);
				}
			} else if (vehicleZone.isPolyline()) {
				for (int7 = 0; int7 < vehicleZone.points.size() - 2; int7 += 2) {
					int8 = vehicleZone.points.get(int7);
					int11 = vehicleZone.points.get(int7 + 1);
					int12 = vehicleZone.points.get(int7 + 2);
					int10 = vehicleZone.points.get(int7 + 3);
					this.DrawIsoLine((float)int8, (float)int11, (float)int12, (float)int10, 1.0F, 1.0F, 0.0F, 1.0F, 1);
				}

				if (vehicleZone.polylineOutlinePoints != null) {
					float[] floatArray2 = vehicleZone.polylineOutlinePoints;
					for (int8 = 0; int8 < floatArray2.length; int8 += 2) {
						float1 = floatArray2[int8];
						float2 = floatArray2[int8 + 1];
						float float9 = floatArray2[(int8 + 2) % floatArray2.length];
						float float10 = floatArray2[(int8 + 3) % floatArray2.length];
						this.DrawIsoLine(float1, float2, float9, float10, 1.0F, 1.0F, 0.0F, 1.0F, 1);
					}
				}
			} else {
				this.DrawIsoLine((float)vehicleZone.x, (float)vehicleZone.y, (float)(vehicleZone.x + vehicleZone.w), (float)vehicleZone.y, float6, float7, float8, float3, 1);
				this.DrawIsoLine((float)vehicleZone.x, (float)(vehicleZone.y + vehicleZone.h), (float)(vehicleZone.x + vehicleZone.w), (float)(vehicleZone.y + vehicleZone.h), float6, float7, float8, float3, 1);
				this.DrawIsoLine((float)vehicleZone.x, (float)vehicleZone.y, (float)vehicleZone.x, (float)(vehicleZone.y + vehicleZone.h), float6, float7, float8, float3, 1);
				this.DrawIsoLine((float)(vehicleZone.x + vehicleZone.w), (float)vehicleZone.y, (float)(vehicleZone.x + vehicleZone.w), (float)(vehicleZone.y + vehicleZone.h), float6, float7, float8, float3, 1);
			}
		}

		if (this.RandomSquareInZone.getValue() && zone != null) {
			IsoGridSquare square = zone.getRandomSquareInZone();
			if (square != null) {
				this.paintSquare(square.x, square.y, square.z, 0.0F, 1.0F, 0.0F, 0.5F);
			}
		}
	}

	private void drawVehicleStory() {
		ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesIntersecting(this.gridX - 1, this.gridY - 1, this.m_z, 3, 3, new ArrayList());
		if (!arrayList.isEmpty()) {
			IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, this.m_z);
			if (chunk != null) {
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int1);
					if ("Nav".equals(zone.type)) {
						VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
						RandomizedVehicleStoryBase randomizedVehicleStoryBase = IsoWorld.instance.getRandomizedVehicleStoryByName(this.m_vehicleStory);
						if (randomizedVehicleStoryBase != null && randomizedVehicleStoryBase.isValid(zone, chunk, true) && randomizedVehicleStoryBase.initVehicleStorySpawner(zone, chunk, true)) {
							int int2 = randomizedVehicleStoryBase.getMinZoneWidth();
							int int3 = randomizedVehicleStoryBase.getMinZoneHeight();
							float[] floatArray = new float[3];
							if (randomizedVehicleStoryBase.getSpawnPoint(zone, chunk, floatArray)) {
								float float1 = floatArray[0];
								float float2 = floatArray[1];
								float float3 = floatArray[2] + 1.5707964F;
								vehicleStorySpawner.spawn(float1, float2, 0.0F, float3, (var0,arrayListx)->{
								});

								vehicleStorySpawner.render(float1, float2, 0.0F, (float)int2, (float)int3, floatArray[2]);
							}
						}
					}
				}
			}
		}
	}

	private void DrawBehindStuff() {
		this.IsBehindStuff(IsoCamera.CamCharacter.getCurrentSquare());
	}

	private boolean IsBehindStuff(IsoGridSquare square) {
		for (int int1 = 1; int1 < 8 && square.getZ() + int1 < 8; ++int1) {
			for (int int2 = -5; int2 <= 6; ++int2) {
				for (int int3 = -5; int3 <= 6; ++int3) {
					if (int3 >= int2 - 5 && int3 <= int2 + 5) {
						this.paintSquare(square.getX() + int3 + int1 * 3, square.getY() + int2 + int1 * 3, square.getZ() + int1, 1.0F, 1.0F, 0.0F, 0.25F);
					}
				}
			}
		}

		return true;
	}

	private boolean IsBehindStuffRecY(int int1, int int2, int int3) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecY(int1, int2 + 1, int3 + 1);
		}
	}

	private boolean IsBehindStuffRecXY(int int1, int int2, int int3, int int4) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecXY(int1 + int4, int2 + int4, int3 + 1, int4);
		}
	}

	private boolean IsBehindStuffRecX(int int1, int int2, int int3) {
		IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (int3 >= 15) {
			return false;
		} else {
			this.paintSquare(int1, int2, int3, 1.0F, 1.0F, 0.0F, 0.25F);
			return this.IsBehindStuffRecX(int1 + 1, int2, int3 + 1);
		}
	}

	private void paintSquare(int int1, int int2, int int3, float float1, float float2, float float3, float float4) {
		int int4 = Core.TileScale;
		int int5 = (int)IsoUtils.XToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		int int6 = (int)IsoUtils.YToScreenExact((float)int1, (float)(int2 + 1), (float)int3, 0);
		SpriteRenderer.instance.renderPoly((float)int5, (float)int6, (float)(int5 + 32 * int4), (float)(int6 - 16 * int4), (float)(int5 + 64 * int4), (float)int6, (float)(int5 + 32 * int4), (float)(int6 + 16 * int4), float1, float2, float3, float4);
	}

	void drawModData() {
		int int1 = this.m_z;
		IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, int1);
		int int2 = Core.getInstance().getScreenWidth() - 250;
		int int3 = 10;
		int int4 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		int int5;
		int int6;
		if (square != null && square.getModData() != null) {
			KahluaTable kahluaTable = square.getModData();
			int5 = int3 += int4;
			int6 = square.getX();
			this.DrawString(int2, int5, "MOD DATA x,y,z=" + int6 + "," + square.getY() + "," + square.getZ());
			KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
			label60: while (true) {
				String string;
				do {
					if (!kahluaTableIterator.advance()) {
						int3 += int4;
						break label60;
					}

					int5 = int3 += int4;
					string = kahluaTableIterator.getKey().toString();
					this.DrawString(int2, int5, string + " = " + kahluaTableIterator.getValue().toString());
				}		 while (!(kahluaTableIterator.getValue() instanceof KahluaTable));

				KahluaTableIterator kahluaTableIterator2 = ((KahluaTable)kahluaTableIterator.getValue()).iterator();
				while (kahluaTableIterator2.advance()) {
					int int7 = int2 + 8;
					int5 = int3 += int4;
					string = kahluaTableIterator2.getKey().toString();
					this.DrawString(int7, int5, string + " = " + kahluaTableIterator2.getValue().toString());
				}
			}
		}

		if (square != null) {
			PropertyContainer propertyContainer = square.getProperties();
			ArrayList arrayList = propertyContainer.getPropertyNames();
			if (!arrayList.isEmpty()) {
				int5 = int3 += int4;
				int6 = square.getX();
				this.DrawString(int2, int5, "PROPERTIES x,y,z=" + int6 + "," + square.getY() + "," + square.getZ());
				Collections.sort(arrayList);
				Iterator iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					String string2 = (String)iterator.next();
					this.DrawString(int2, int3 += int4, string2 + " = \"" + propertyContainer.Val(string2) + "\"");
				}
			}

			IsoFlagType[] flagTypeArray = IsoFlagType.values();
			int int8 = flagTypeArray.length;
			for (int int9 = 0; int9 < int8; ++int9) {
				IsoFlagType flagType = flagTypeArray[int9];
				if (propertyContainer.Is(flagType)) {
					this.DrawString(int2, int3 += int4, flagType.toString());
				}
			}
		}

		if (square != null) {
			ErosionData.Square square2 = square.getErosionData();
			if (square2 != null) {
				int3 += int4;
				int5 = int3 += int4;
				int6 = square.getX();
				this.DrawString(int2, int5, "EROSION x,y,z=" + int6 + "," + square.getY() + "," + square.getZ());
				this.DrawString(int2, int3 += int4, "init=" + square2.init);
				this.DrawString(int2, int3 += int4, "doNothing=" + square2.doNothing);
				this.DrawString(int2, int3 + int4, "chunk.init=" + square.chunk.getErosionData().init);
			}
		}
	}

	void drawPlayerInfo() {
		int int1 = Core.getInstance().getScreenWidth() - 250;
		int int2 = Core.getInstance().getScreenHeight() / 2;
		int int3 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		IsoGameCharacter gameCharacter = IsoCamera.CamCharacter;
		this.DrawString(int1, int2 += int3, "bored = " + gameCharacter.getBodyDamage().getBoredomLevel());
		this.DrawString(int1, int2 += int3, "endurance = " + gameCharacter.getStats().endurance);
		this.DrawString(int1, int2 += int3, "fatigue = " + gameCharacter.getStats().fatigue);
		this.DrawString(int1, int2 += int3, "hunger = " + gameCharacter.getStats().hunger);
		this.DrawString(int1, int2 += int3, "pain = " + gameCharacter.getStats().Pain);
		this.DrawString(int1, int2 += int3, "panic = " + gameCharacter.getStats().Panic);
		this.DrawString(int1, int2 += int3, "stress = " + gameCharacter.getStats().getStress());
		this.DrawString(int1, int2 += int3, "clothingTemp = " + ((IsoPlayer)gameCharacter).getPlayerClothingTemperature());
		this.DrawString(int1, int2 += int3, "temperature = " + gameCharacter.getTemperature());
		this.DrawString(int1, int2 += int3, "thirst = " + gameCharacter.getStats().thirst);
		this.DrawString(int1, int2 += int3, "foodPoison = " + gameCharacter.getBodyDamage().getFoodSicknessLevel());
		this.DrawString(int1, int2 += int3, "poison = " + gameCharacter.getBodyDamage().getPoisonLevel());
		this.DrawString(int1, int2 += int3, "unhappy = " + gameCharacter.getBodyDamage().getUnhappynessLevel());
		this.DrawString(int1, int2 += int3, "infected = " + gameCharacter.getBodyDamage().isInfected());
		this.DrawString(int1, int2 += int3, "InfectionLevel = " + gameCharacter.getBodyDamage().getInfectionLevel());
		this.DrawString(int1, int2 += int3, "FakeInfectionLevel = " + gameCharacter.getBodyDamage().getFakeInfectionLevel());
		int2 += int3;
		this.DrawString(int1, int2 += int3, "WORLD");
		this.DrawString(int1, int2 + int3, "globalTemperature = " + IsoWorld.instance.getGlobalTemperature());
	}

	public LosUtil.TestResults lineClearCached(IsoCell cell, int int1, int int2, int int3, int int4, int int5, int int6, boolean boolean1) {
		int int7 = int2 - int5;
		int int8 = int1 - int4;
		int int9 = int3 - int6;
		int int10 = int8 + 100;
		int int11 = int7 + 100;
		int int12 = int9 + 16;
		if (int10 >= 0 && int11 >= 0 && int12 >= 0 && int10 < 200 && int11 < 200) {
			LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
			byte byte1 = 1;
			float float1 = 0.5F;
			float float2 = 0.5F;
			IsoGridSquare square = cell.getGridSquare(int4, int5, int6);
			int int13;
			int int14;
			float float3;
			float float4;
			IsoGridSquare square2;
			if (Math.abs(int8) > Math.abs(int7) && Math.abs(int8) > Math.abs(int9)) {
				float3 = (float)int7 / (float)int8;
				float4 = (float)int9 / (float)int8;
				float1 += (float)int5;
				float2 += (float)int6;
				int8 = int8 < 0 ? -1 : 1;
				float3 *= (float)int8;
				for (float4 *= (float)int8; int4 != int1; int14 = (int)float2) {
					int4 += int8;
					float1 += float3;
					float2 += float4;
					square2 = cell.getGridSquare(int4, (int)float1, (int)float2);
					this.paintSquare(int4, (int)float1, (int)float2, 1.0F, 1.0F, 1.0F, 0.5F);
					if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
						this.paintSquare(int4, (int)float1, (int)float2, 1.0F, 0.0F, 0.0F, 0.5F);
						this.paintSquare(square.getX(), square.getY(), square.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
						byte1 = 4;
					}

					square = square2;
					int13 = (int)float1;
				}
			} else {
				int int15;
				if (Math.abs(int7) >= Math.abs(int8) && Math.abs(int7) > Math.abs(int9)) {
					float3 = (float)int8 / (float)int7;
					float4 = (float)int9 / (float)int7;
					float1 += (float)int4;
					float2 += (float)int6;
					int7 = int7 < 0 ? -1 : 1;
					float3 *= (float)int7;
					for (float4 *= (float)int7; int5 != int2; int14 = (int)float2) {
						int5 += int7;
						float1 += float3;
						float2 += float4;
						square2 = cell.getGridSquare((int)float1, int5, (int)float2);
						this.paintSquare((int)float1, int5, (int)float2, 1.0F, 1.0F, 1.0F, 0.5F);
						if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
							this.paintSquare((int)float1, int5, (int)float2, 1.0F, 0.0F, 0.0F, 0.5F);
							this.paintSquare(square.getX(), square.getY(), square.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
							byte1 = 4;
						}

						square = square2;
						int15 = (int)float1;
					}
				} else {
					float3 = (float)int8 / (float)int9;
					float4 = (float)int7 / (float)int9;
					float1 += (float)int4;
					float2 += (float)int5;
					int9 = int9 < 0 ? -1 : 1;
					float3 *= (float)int9;
					for (float4 *= (float)int9; int6 != int3; int13 = (int)float2) {
						int6 += int9;
						float1 += float3;
						float2 += float4;
						square2 = cell.getGridSquare((int)float1, (int)float2, int6);
						this.paintSquare((int)float1, (int)float2, int6, 1.0F, 1.0F, 1.0F, 0.5F);
						if (square2 != null && square != null && square2.testVisionAdjacent(square.getX() - square2.getX(), square.getY() - square2.getY(), square.getZ() - square2.getZ(), true, boolean1) == LosUtil.TestResults.Blocked) {
							byte1 = 4;
						}

						square = square2;
						int15 = (int)float1;
					}
				}
			}

			if (byte1 == 1) {
				return LosUtil.TestResults.Clear;
			} else if (byte1 == 2) {
				return LosUtil.TestResults.ClearThroughOpenDoor;
			} else if (byte1 == 3) {
				return LosUtil.TestResults.ClearThroughWindow;
			} else {
				return byte1 == 4 ? LosUtil.TestResults.Blocked : LosUtil.TestResults.Blocked;
			}
		} else {
			return LosUtil.TestResults.Blocked;
		}
	}

	private void DrawString(int int1, int int2, String string) {
		int int3 = TextManager.instance.MeasureStringX(this.FONT, string);
		int int4 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
		SpriteRenderer.instance.renderi((Texture)null, int1 - 1, int2, int3 + 2, int4, 0.0F, 0.0F, 0.0F, 0.8F, (Consumer)null);
		TextManager.instance.DrawString(this.FONT, (double)int1, (double)int2, string, 1.0, 1.0, 1.0, 1.0);
	}

	public ConfigOption getOptionByName(String string) {
		for (int int1 = 0; int1 < this.options.size(); ++int1) {
			ConfigOption configOption = (ConfigOption)this.options.get(int1);
			if (configOption.getName().equals(string)) {
				return configOption;
			}
		}

		return null;
	}

	public int getOptionCount() {
		return this.options.size();
	}

	public ConfigOption getOptionByIndex(int int1) {
		return (ConfigOption)this.options.get(int1);
	}

	public void setBoolean(String string, boolean boolean1) {
		ConfigOption configOption = this.getOptionByName(string);
		if (configOption instanceof BooleanConfigOption) {
			((BooleanConfigOption)configOption).setValue(boolean1);
		}
	}

	public boolean getBoolean(String string) {
		ConfigOption configOption = this.getOptionByName(string);
		return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
	}

	public void save() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "debugChunkState-options.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string2, 1, this.options);
	}

	public void load() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "debugChunkState-options.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string2)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				ConfigOption configOption2 = this.getOptionByName(configOption.getName());
				if (configOption2 != null) {
					configOption2.parse(configOption.getValueAsString());
				}
			}
		}
	}

	public class BooleanDebugOption extends BooleanConfigOption {

		public BooleanDebugOption(String string, boolean boolean1) {
			super(string, boolean1);
			DebugChunkState.this.options.add(this);
		}
	}

	private class FloodFill {
		private IsoGridSquare start = null;
		private final int FLOOD_SIZE = 11;
		private BooleanGrid visited = new BooleanGrid(11, 11);
		private Stack stack = new Stack();
		private IsoBuilding building = null;
		private Mover mover = null;

		void calculate(Mover mover, IsoGridSquare square) {
			this.start = square;
			this.mover = mover;
			if (this.start.getRoom() != null) {
				this.building = this.start.getRoom().getBuilding();
			}

			boolean boolean1 = false;
			boolean boolean2 = false;
			if (this.push(this.start.getX(), this.start.getY())) {
				while ((square = this.pop()) != null) {
					int int1 = square.getX();
					int int2;
					for (int2 = square.getY(); this.shouldVisit(int1, int2, int1, int2 - 1); --int2) {
					}

					boolean2 = false;
					boolean1 = false;
					while (true) {
						this.visited.setValue(this.gridX(int1), this.gridY(int2), true);
						if (!boolean1 && this.shouldVisit(int1, int2, int1 - 1, int2)) {
							if (!this.push(int1 - 1, int2)) {
								return;
							}

							boolean1 = true;
						} else if (boolean1 && !this.shouldVisit(int1, int2, int1 - 1, int2)) {
							boolean1 = false;
						} else if (boolean1 && !this.shouldVisit(int1 - 1, int2, int1 - 1, int2 - 1) && !this.push(int1 - 1, int2)) {
							return;
						}

						if (!boolean2 && this.shouldVisit(int1, int2, int1 + 1, int2)) {
							if (!this.push(int1 + 1, int2)) {
								return;
							}

							boolean2 = true;
						} else if (boolean2 && !this.shouldVisit(int1, int2, int1 + 1, int2)) {
							boolean2 = false;
						} else if (boolean2 && !this.shouldVisit(int1 + 1, int2, int1 + 1, int2 - 1) && !this.push(int1 + 1, int2)) {
							return;
						}

						++int2;
						if (!this.shouldVisit(int1, int2 - 1, int1, int2)) {
							break;
						}
					}
				}
			}
		}

		boolean shouldVisit(int int1, int int2, int int3, int int4) {
			if (this.gridX(int3) < 11 && this.gridX(int3) >= 0) {
				if (this.gridY(int4) < 11 && this.gridY(int4) >= 0) {
					if (this.visited.getValue(this.gridX(int3), this.gridY(int4))) {
						return false;
					} else {
						IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, this.start.getZ());
						if (square == null) {
							return false;
						} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
							if (!square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsMW) && !square.Has(IsoObjectType.stairsTW)) {
								if (square.getRoom() != null && this.building == null) {
									return false;
								} else if (square.getRoom() == null && this.building != null) {
									return false;
								} else {
									return !IsoWorld.instance.CurrentCell.blocked(this.mover, int3, int4, this.start.getZ(), int1, int2, this.start.getZ());
								}
							} else {
								return false;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		boolean push(int int1, int int2) {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.start.getZ());
			this.stack.push(square);
			return true;
		}

		IsoGridSquare pop() {
			return this.stack.isEmpty() ? null : (IsoGridSquare)this.stack.pop();
		}

		int gridX(int int1) {
			return int1 - (this.start.getX() - 5);
		}

		int gridY(int int1) {
			return int1 - (this.start.getY() - 5);
		}

		int gridX(IsoGridSquare square) {
			return square.getX() - (this.start.getX() - 5);
		}

		int gridY(IsoGridSquare square) {
			return square.getY() - (this.start.getY() - 5);
		}

		void draw() {
			int int1 = this.start.getX() - 5;
			int int2 = this.start.getY() - 5;
			for (int int3 = 0; int3 < 11; ++int3) {
				for (int int4 = 0; int4 < 11; ++int4) {
					if (this.visited.getValue(int4, int3)) {
						int int5 = (int)IsoUtils.XToScreenExact((float)(int1 + int4), (float)(int2 + int3 + 1), (float)this.start.getZ(), 0);
						int int6 = (int)IsoUtils.YToScreenExact((float)(int1 + int4), (float)(int2 + int3 + 1), (float)this.start.getZ(), 0);
						SpriteRenderer.instance.renderPoly((float)int5, (float)int6, (float)(int5 + 32), (float)(int6 - 16), (float)(int5 + 64), (float)int6, (float)(int5 + 32), (float)(int6 + 16), 1.0F, 1.0F, 0.0F, 0.5F);
					}
				}
			}
		}
	}
}
