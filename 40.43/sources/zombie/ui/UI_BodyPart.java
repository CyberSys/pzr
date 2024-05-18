package zombie.ui;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.network.GameClient;


public class UI_BodyPart extends UIElement {
	public float alpha = 1.0F;
	public Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	public BodyPartType BodyPartType;
	public boolean IsFlipped = false;
	public float MaxOscilatorRate = 0.58F;
	public float MinOscilatorRate = 0.025F;
	public float Oscilator = 0.0F;
	public float OscilatorRate = 0.02F;
	public float OscilatorStep = 0.0F;
	IsoGameCharacter chr;
	boolean mouseOver = false;
	Texture scratchTex;
	Texture bandageTex;
	Texture dirtyBandageTex;
	Texture infectionTex;
	Texture deepWoundTex;
	Texture stitchTex;
	Texture biteTex;
	Texture glassTex;
	Texture boneTex;
	Texture splintTex;
	Texture burnTex;
	Texture bulletTex;

	public UI_BodyPart(BodyPartType bodyPartType, int int1, int int2, String string, IsoGameCharacter gameCharacter, boolean boolean1) {
		String string2 = "male";
		if (gameCharacter.isFemale()) {
			string2 = "female";
		}

		this.chr = gameCharacter;
		this.BodyPartType = bodyPartType;
		this.scratchTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_scratch_" + string);
		this.bandageTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_bandage_" + string);
		this.dirtyBandageTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_bandagedirty_" + string);
		this.infectionTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_infection_" + string);
		this.biteTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_bite_" + string);
		this.deepWoundTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_deepwound_" + string);
		this.stitchTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_stitches_" + string);
		this.glassTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_glass_" + string);
		this.boneTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_bones_" + string);
		this.splintTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_splint_" + string);
		this.burnTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_burn_" + string);
		this.bulletTex = Texture.getSharedTexture("media/ui/BodyDamage/" + string2 + "_bullet_" + string);
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)this.scratchTex.getWidth();
		this.height = (float)this.scratchTex.getHeight();
		this.IsFlipped = boolean1;
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.mouseOver = false;
	}

	public void render() {
		BodyDamage bodyDamage = this.chr.getBodyDamage();
		if (GameClient.bClient && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).isLocalPlayer()) {
			bodyDamage = this.chr.getBodyDamageRemote();
		}

		if (this.infectionTex != null && !bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getWoundInfectionLevel() > 0.0F) {
			this.DrawTexture(this.infectionTex, 0.0, 0.0, (double)(bodyDamage.getBodyPart(this.BodyPartType).getWoundInfectionLevel() / 10.0F));
		}

		if (this.scratchTex != null && bodyDamage.IsScratched(this.BodyPartType)) {
			this.DrawTexture(this.scratchTex, 0.0, 0.0, (double)(bodyDamage.getBodyPart(this.BodyPartType).getScratchTime() / 20.0F));
		} else if (this.biteTex != null && !bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.IsBitten(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBiteTime() >= 0.0F) {
			this.DrawTexture(this.biteTex, 0.0, 0.0, 1.0);
		} else if (this.bandageTex != null && bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBandageLife() > 0.0F) {
			this.DrawTexture(this.bandageTex, 0.0, 0.0, 1.0);
		} else if (this.dirtyBandageTex != null && bodyDamage.IsBandaged(this.BodyPartType) && bodyDamage.getBodyPart(this.BodyPartType).getBandageLife() <= 0.0F) {
			this.DrawTexture(this.dirtyBandageTex, 0.0, 0.0, 1.0);
		} else if (this.deepWoundTex != null && bodyDamage.IsDeepWounded(this.BodyPartType)) {
			this.DrawTexture(this.deepWoundTex, 0.0, 0.0, (double)(bodyDamage.getBodyPart(this.BodyPartType).getDeepWoundTime() / 15.0F));
		} else if (this.stitchTex != null && bodyDamage.IsStitched(this.BodyPartType)) {
			this.DrawTexture(this.stitchTex, 0.0, 0.0, 1.0);
		}

		if (this.boneTex != null && bodyDamage.getBodyPart(this.BodyPartType).getFractureTime() > 0.0F && bodyDamage.getBodyPart(this.BodyPartType).getSplintFactor() == 0.0F) {
			this.DrawTexture(this.boneTex, 0.0, 0.0, 1.0);
		} else if (this.splintTex != null && bodyDamage.getBodyPart(this.BodyPartType).getSplintFactor() > 0.0F) {
			this.DrawTexture(this.splintTex, 0.0, 0.0, 1.0);
		}

		if (this.glassTex != null && bodyDamage.getBodyPart(this.BodyPartType).haveGlass() && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
			this.DrawTexture(this.glassTex, 0.0, 0.0, 1.0);
		}

		if (this.bulletTex != null && bodyDamage.getBodyPart(this.BodyPartType).haveBullet() && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
			this.DrawTexture(this.bulletTex, 0.0, 0.0, 1.0);
		}

		if (this.burnTex != null && bodyDamage.getBodyPart(this.BodyPartType).getBurnTime() > 0.0F && !bodyDamage.getBodyPart(this.BodyPartType).bandaged()) {
			this.DrawTexture(this.burnTex, 0.0, 0.0, (double)(bodyDamage.getBodyPart(this.BodyPartType).getBurnTime() / 100.0F));
		}

		super.render();
	}
}
