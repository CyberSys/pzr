package zombie.core.sprite;

import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Styles.TransparentStyle;
import zombie.core.opengl.GLState;
import zombie.core.opengl.RenderSettings;
import zombie.core.textures.TextureFBO;
import zombie.input.Mouse;
import zombie.iso.PlayerCamera;


public final class SpriteRenderState extends GenericSpriteRenderState {
	public TextureFBO fbo = null;
	public long time;
	public final SpriteRenderStateUI stateUI;
	public int playerIndex;
	public final PlayerCamera[] playerCamera = new PlayerCamera[4];
	public final float[] playerAmbient = new float[4];
	public float maxZoomLevel = 0.0F;
	public float minZoomLevel = 0.0F;
	public final float[] zoomLevel = new float[4];

	public SpriteRenderState(int int1) {
		super(int1);
		for (int int2 = 0; int2 < 4; ++int2) {
			this.playerCamera[int2] = new PlayerCamera(int2);
		}

		this.stateUI = new SpriteRenderStateUI(int1);
	}

	public void onRendered() {
		super.onRendered();
		this.stateUI.onRendered();
	}

	public void onReady() {
		super.onReady();
		this.stateUI.onReady();
	}

	public void CheckSpriteSlots() {
		if (this.stateUI.bActive) {
			this.stateUI.CheckSpriteSlots();
		} else {
			super.CheckSpriteSlots();
		}
	}

	public void clear() {
		this.stateUI.clear();
		super.clear();
	}

	public GenericSpriteRenderState getActiveState() {
		return (GenericSpriteRenderState)(this.stateUI.bActive ? this.stateUI : this);
	}

	public void prePopulating() {
		this.clear();
		this.fbo = Core.getInstance().getOffscreenBuffer();
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				this.playerCamera[int1].initFromIsoCamera(int1);
				this.playerAmbient[int1] = RenderSettings.getInstance().getAmbientForPlayer(int1);
				this.zoomLevel[int1] = Core.getInstance().getZoom(int1);
				this.maxZoomLevel = Core.getInstance().getMaxZoom();
				this.minZoomLevel = Core.getInstance().getMinZoom();
			}
		}

		this.defaultStyle = TransparentStyle.instance;
		this.bCursorVisible = Mouse.isCursorVisible();
		GLState.startFrame();
	}
}
