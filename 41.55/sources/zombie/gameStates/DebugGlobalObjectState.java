package zombie.gameStates;

import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.debug.LineDrawer;
import zombie.globalObjects.CGlobalObjectSystem;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.GlobalObject;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.TextDrawObject;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.EditVehicleState;


public final class DebugGlobalObjectState extends GameState {
	public static DebugGlobalObjectState instance;
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

	public DebugGlobalObjectState() {
		this.FONT = UIFont.DebugConsole;
		instance = this;
	}

	public void enter() {
		instance = this;
		if (this.m_luaEnv == null) {
			this.m_luaEnv = new EditVehicleState.LuaEnvironment(LuaManager.platform, LuaManager.converterManager, LuaManager.env);
		}

		this.saveGameUI();
		if (this.m_selfUI.size() == 0) {
			IsoPlayer player = IsoPlayer.players[this.m_playerIndex];
			this.m_z = player == null ? 0 : (int)player.z;
			this.m_luaEnv.caller.pcall(this.m_luaEnv.thread, this.m_luaEnv.env.rawget("DebugGlobalObjectState_InitUI"), (Object)this);
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
		if (!this.bExit && !GameKeyboard.isKeyPressed(60)) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[this.m_playerIndex];
			chunkMap.ProcessChunkPos(IsoPlayer.players[this.m_playerIndex]);
			chunkMap.update();
			return this.updateScene();
		} else {
			return GameStateMachine.StateAction.Continue;
		}
	}

	public void renderScene() {
		IsoCamera.frameState.set(this.m_playerIndex);
		SpriteRenderer.instance.doCoreIntParam(0, IsoCamera.CamCharacter.x);
		SpriteRenderer.instance.doCoreIntParam(1, IsoCamera.CamCharacter.y);
		SpriteRenderer.instance.doCoreIntParam(2, IsoCamera.CamCharacter.z);
		IsoSprite.globalOffsetX = -1.0F;
		IsoWorld.instance.CurrentCell.render();
		IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[this.m_playerIndex];
		int int1 = chunkMap.getWorldXMin();
		int int2 = chunkMap.getWorldYMin();
		int int3 = int1 + IsoChunkMap.ChunkGridWidth;
		int int4 = int2 + IsoChunkMap.ChunkGridWidth;
		int int5 = CGlobalObjects.getSystemCount();
		for (int int6 = 0; int6 < int5; ++int6) {
			CGlobalObjectSystem cGlobalObjectSystem = CGlobalObjects.getSystemByIndex(int6);
			for (int int7 = int2; int7 < int4; ++int7) {
				for (int int8 = int1; int8 < int3; ++int8) {
					ArrayList arrayList = cGlobalObjectSystem.getObjectsInChunk(int8, int7);
					for (int int9 = 0; int9 < arrayList.size(); ++int9) {
						GlobalObject globalObject = (GlobalObject)arrayList.get(int9);
						float float1 = 1.0F;
						float float2 = 1.0F;
						float float3 = 1.0F;
						if (globalObject.getZ() != this.m_z) {
							float3 = 0.5F;
							float2 = 0.5F;
							float1 = 0.5F;
						}

						this.DrawIsoRect((float)globalObject.getX(), (float)globalObject.getY(), (float)globalObject.getZ(), 1.0F, 1.0F, float1, float2, float3, 1.0F, 1);
					}

					cGlobalObjectSystem.finishedWithList(arrayList);
				}
			}
		}

		LineDrawer.render();
		LineDrawer.clear();
	}

	private void renderUI() {
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
		IsoCamera.update();
		this.updateCursor();
		return GameStateMachine.StateAction.Remain;
	}

	private void updateCursor() {
		int int1 = this.m_playerIndex;
		float float1 = (float)Mouse.getXA();
		float float2 = (float)Mouse.getYA();
		float1 -= (float)IsoCamera.getScreenLeft(int1);
		float2 -= (float)IsoCamera.getScreenTop(int1);
		float1 *= Core.getInstance().getZoom(int1);
		float2 *= Core.getInstance().getZoom(int1);
		int int2 = this.m_z;
		this.gridX = (int)IsoUtils.XToIso(float1, float2, (float)int2);
		this.gridY = (int)IsoUtils.YToIso(float1, float2, (float)int2);
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

	private void DrawIsoLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, float float10, int int1) {
		float float11 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
		float float12 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
		float float13 = IsoUtils.XToScreenExact(float4, float5, float6, 0);
		float float14 = IsoUtils.YToScreenExact(float4, float5, float6, 0);
		LineDrawer.drawLine(float11, float12, float13, float14, float7, float8, float9, float10, int1);
	}

	private void DrawIsoRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8, float float9, int int1) {
		this.DrawIsoLine(float1, float2, float3, float1 + float4, float2, float3, float6, float7, float8, float9, int1);
		this.DrawIsoLine(float1 + float4, float2, float3, float1 + float4, float2 + float5, float3, float6, float7, float8, float9, int1);
		this.DrawIsoLine(float1 + float4, float2 + float5, float3, float1, float2 + float5, float3, float6, float7, float8, float9, int1);
		this.DrawIsoLine(float1, float2 + float5, float3, float1, float2, float3, float6, float7, float8, float9, int1);
	}

	public Object fromLua0(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
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
				byte1 = 4;
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
				byte1 = 0;
			}

			break;
		
		case 3526712: 
			if (string.equals("setZ")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.m_playerIndex = PZMath.clamp(((Double)object).intValue(), 0, 3);
			return null;
		
		case 1: 
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
}
