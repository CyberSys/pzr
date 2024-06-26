package zombie.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.gameStates.IngameState;


public final class ModalDialog extends NewWindow {
	public boolean bYes = false;
	public String Name;
	UIEventHandler handler = null;
	public boolean Clicked = false;

	public ModalDialog(String string, String string2, boolean boolean1) {
		super(Core.getInstance().getOffscreenWidth(0) / 2, Core.getInstance().getOffscreenHeight(0) / 2, 470, 10, false);
		this.Name = string;
		this.ResizeToFitY = false;
		this.IgnoreLossControl = true;
		TextBox textBox = new TextBox(UIFont.Medium, 0, 0, 450, string2);
		textBox.Centred = true;
		textBox.ResizeParent = true;
		textBox.update();
		this.Nest(textBox, 20, 10, 20, 10);
		this.update();
		this.height *= 1.3F;
		if (boolean1) {
			this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 - 40), (float)(this.getHeight().intValue() - 18), "Yes", "Yes"));
			this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2 + 40), (float)(this.getHeight().intValue() - 18), "No", "No"));
		} else {
			this.AddChild(new DialogButton(this, (float)(this.getWidth().intValue() / 2), (float)(this.getHeight().intValue() - 18), "Ok", "Ok"));
		}

		this.x -= (double)(this.width / 2.0F);
		this.y -= (double)(this.height / 2.0F);
	}

	public void ButtonClicked(String string) {
		if (this.handler != null) {
			this.handler.ModalClick(this.Name, string);
			this.setVisible(false);
		} else {
			if (string.equals("Ok")) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(4);
				this.Clicked(string);
				this.Clicked = true;
				this.bYes = true;
				this.setVisible(false);
				IngameState.instance.Paused = false;
			}

			if (string.equals("Yes")) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(4);
				this.Clicked(string);
				this.Clicked = true;
				this.bYes = true;
				this.setVisible(false);
				IngameState.instance.Paused = false;
			}

			if (string.equals("No")) {
				UIManager.getSpeedControls().SetCurrentGameSpeed(4);
				this.Clicked(string);
				this.Clicked = true;
				this.bYes = false;
				this.setVisible(false);
				IngameState.instance.Paused = false;
			}
		}
	}

	public void Clicked(String string) {
		if (this.Name.equals("Sleep") && string.equals("Yes")) {
			float float1 = 12.0F * IsoPlayer.getInstance().getStats().fatigue;
			if (float1 < 7.0F) {
				float1 = 7.0F;
			}

			float1 += GameTime.getInstance().getTimeOfDay();
			if (float1 >= 24.0F) {
				float1 -= 24.0F;
			}

			IsoPlayer.getInstance().setForceWakeUpTime((float)((int)float1));
			IsoPlayer.getInstance().setAsleepTime(0.0F);
			TutorialManager.instance.StealControl = true;
			IsoPlayer.getInstance().setAsleep(true);
			UIManager.setbFadeBeforeUI(true);
			UIManager.FadeOut(4.0);
			UIManager.getSpeedControls().SetCurrentGameSpeed(3);
			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			} catch (IOException ioException) {
				Logger.getLogger(ModalDialog.class.getName()).log(Level.SEVERE, (String)null, ioException);
			}
		}

		UIManager.Modal.setVisible(false);
	}
}
