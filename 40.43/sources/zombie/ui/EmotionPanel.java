package zombie.ui;

import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.core.textures.Texture;
import zombie.iso.IsoCamera;


public class EmotionPanel extends UIElement {
	HUDButton a = new HUDButton("angry", 0.0, 0.0, "media/ui/Emotion_Aggressive.png", "media/ui/Emotion_Aggressive_MouseOver.png", this);
	HUDButton b;
	HUDButton c;
	int selected = 1;
	Texture aggSel;
	Texture aggMouseOver;
	Texture neuSel;
	Texture neuMouseOver;
	Texture friSel;
	Texture friMouseOver = null;

	public EmotionPanel(int int1, int int2) {
		this.b = new HUDButton("neutral", this.a.getWidth(), 0.0, "media/ui/Emotion_Neutral.png", "media/ui/Emotion_Neutral_MouseOver.png", this);
		this.c = new HUDButton("happy", this.a.getWidth() + this.b.getWidth(), 0.0, "media/ui/Emotion_Friendly.png", "media/ui/Emotion_Friendly_MouseOver.png", this);
		this.width = this.a.width + this.b.width + this.c.width;
		this.a.notclickedAlpha = this.b.notclickedAlpha = this.c.notclickedAlpha = 0.6F;
		this.a.clickedalpha = this.b.clickedalpha = this.c.clickedalpha = 1.0F;
		this.AddChild(this.a);
		this.AddChild(this.b);
		this.AddChild(this.c);
	}

	public void ButtonClicked(String string) {
		if (string.equals("angry")) {
			IsoPlayer.instance.setDialogMood(0);
		}

		if (IsoCamera.CamCharacter.getMoodles().getMoodleLevel(MoodleType.Angry) <= 2 && string.equals("neutral")) {
			IsoPlayer.instance.setDialogMood(1);
		}

		if (IsoCamera.CamCharacter.getMoodles().getMoodleLevel(MoodleType.Angry) <= 1 && string.equals("happy")) {
			IsoPlayer.instance.setDialogMood(2);
		}
	}

	public void render() {
		if (this.friMouseOver == null) {
			this.aggMouseOver = Texture.getSharedTexture("media/ui/Emotion_Aggressive_MouseOver.png");
			this.aggSel = Texture.getSharedTexture("media/ui/Emotion_Aggressive_Selected.png");
			this.neuMouseOver = Texture.getSharedTexture("media/ui/Emotion_Neutral_MouseOver.png");
			this.neuSel = Texture.getSharedTexture("media/ui/Emotion_Neutral_Selected.png");
			this.friMouseOver = Texture.getSharedTexture("media/ui/Emotion_Friendly_MouseOver.png");
			this.friSel = Texture.getSharedTexture("media/ui/Emotion_Friendly_Selected.png");
		}

		if (IsoPlayer.getInstance().getDialogMood() == 0) {
			this.a.highlight = this.aggSel;
		} else {
			this.a.highlight = this.aggMouseOver;
		}

		if (IsoPlayer.getInstance().getDialogMood() == 1) {
			this.b.highlight = this.neuSel;
		} else {
			this.b.highlight = this.neuMouseOver;
		}

		if (IsoPlayer.getInstance().getDialogMood() == 2) {
			this.c.highlight = this.friSel;
		} else {
			this.c.highlight = this.friMouseOver;
		}

		super.render();
	}

	public void update() {
		super.update();
		if (IsoCamera.CamCharacter != null) {
			if (IsoCamera.CamCharacter == IsoPlayer.getInstance()) {
				this.a.notclickedAlpha = this.b.notclickedAlpha = this.c.notclickedAlpha = 0.6F;
				this.a.clickedalpha = this.b.clickedalpha = this.c.clickedalpha = 1.0F;
				if (IsoCamera.CamCharacter.getMoodles().getMoodleLevel(MoodleType.Angry) > 1) {
					this.c.notclickedAlpha = 0.3F;
					this.c.clickedalpha = 0.3F;
					if (IsoPlayer.getInstance().getDialogMood() >= 2) {
						IsoPlayer.instance.setDialogMood(1);
					}
				}

				if (IsoCamera.CamCharacter.getMoodles().getMoodleLevel(MoodleType.Angry) > 2) {
					this.b.notclickedAlpha = 0.3F;
					this.b.clickedalpha = 0.3F;
					if (IsoPlayer.getInstance().getDialogMood() >= 1) {
						IsoPlayer.instance.setDialogMood(0);
					}
				}
			}
		}
	}

	public String getClickedValue() {
		if (IsoPlayer.getInstance().getDialogMood() == 0) {
			return "angry";
		} else if (IsoPlayer.getInstance().getDialogMood() == 1) {
			return "neutral";
		} else {
			return IsoPlayer.getInstance().getDialogMood() == 2 ? "happy" : "neutral";
		}
	}
}
