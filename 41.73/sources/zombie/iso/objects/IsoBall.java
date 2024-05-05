package zombie.iso.objects;

import zombie.WorldSoundManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoPhysicsObject;
import zombie.network.GameClient;


public class IsoBall extends IsoPhysicsObject {
	private HandWeapon weapon = null;
	private IsoGameCharacter character = null;
	private int lastCheckX = 0;
	private int lastCheckY = 0;

	public String getObjectName() {
		return "MolotovCocktail";
	}

	public IsoBall(IsoCell cell) {
		super(cell);
	}

	public IsoBall(IsoCell cell, float float1, float float2, float float3, float float4, float float5, HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		super(cell);
		this.weapon = handWeapon;
		this.character = gameCharacter;
		this.velX = float4;
		this.velY = float5;
		float float6 = (float)Rand.Next(4000) / 10000.0F;
		float float7 = (float)Rand.Next(4000) / 10000.0F;
		float6 -= 0.2F;
		float7 -= 0.2F;
		this.velX += float6;
		this.velY += float7;
		this.x = float1;
		this.y = float2;
		this.z = float3;
		this.nx = float1;
		this.ny = float2;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		this.terminalVelocity = -0.02F;
		Texture texture = this.sprite.LoadFrameExplicit(handWeapon.getTex().getName());
		if (texture != null) {
			this.sprite.Animate = false;
			int int1 = Core.TileScale;
			this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * int1), (float)(16 * int1));
		}

		this.speedMod = 0.6F;
	}

	public void collideGround() {
		this.Fall();
	}

	public void collideWall() {
		this.Fall();
	}

	public void update() {
		super.update();
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
		if (Core.bDebug) {
		}
	}

	void Fall() {
		this.getCurrentSquare().getMovingObjects().remove(this);
		this.getCell().Remove(this);
		if (!GameClient.bClient) {
			WorldSoundManager.instance.addSound(this, (int)this.x, (int)this.y, 0, 600, 600);
		}

		if (this.character instanceof IsoPlayer) {
			if (((IsoPlayer)this.character).isLocalPlayer()) {
				this.square.AddWorldInventoryItem(this.weapon, Rand.Next(0.2F, 0.8F), Rand.Next(0.2F, 0.8F), 0.0F, true);
			}
		} else {
			DebugLog.General.error("IsoBall: character isn\'t instance of IsoPlayer");
		}
	}
}
