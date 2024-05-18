package zombie.ui;

import java.util.ArrayList;
import java.util.Objects;
import zombie.core.Translator;
import zombie.core.znet.SteamUtils;
import zombie.network.CoopMaster;
import zombie.network.ICoopServerMessageListener;


public class UIServerToolbox extends NewWindow implements ICoopServerMessageListener,UIEventHandler {
	public static UIServerToolbox instance;
	ScrollBar ScrollBarV;
	UITextBox2 OutputLog;
	public ArrayList incomingConnections = new ArrayList();
	DialogButton buttonAccept;
	DialogButton buttonReject;
	private String externalAddress = null;
	private String steamID = null;
	public boolean autoAccept = false;

	public UIServerToolbox(int int1, int int2) {
		super(int1, int2, 10, 10, true);
		this.ResizeToFitY = false;
		this.visible = true;
		if (instance != null) {
			instance.shutdown();
		}

		instance = this;
		this.width = 340.0F;
		this.height = 325.0F;
		boolean boolean1 = true;
		boolean boolean2 = true;
		this.OutputLog = new UITextBox2(UIFont.Small, 5, 33, 330, 260, Translator.getText("IGUI_ServerToolBox_Status"), true);
		this.OutputLog.multipleLine = true;
		this.ScrollBarV = new ScrollBar("ServerToolboxScrollbar", this, (int)(this.OutputLog.getX() + this.OutputLog.getWidth()) - 14, this.OutputLog.getY().intValue() + 4, this.OutputLog.getHeight().intValue() - 8, true);
		this.ScrollBarV.SetParentTextBox(this.OutputLog);
		this.AddChild(this.OutputLog);
		this.AddChild(this.ScrollBarV);
		this.buttonAccept = new DialogButton(this, 30, 225, Translator.getText("IGUI_ServerToolBox_acccept"), "accept");
		this.buttonReject = new DialogButton(this, 80, 225, Translator.getText("IGUI_ServerToolBox_reject"), "reject");
		this.AddChild(this.buttonAccept);
		this.AddChild(this.buttonReject);
		this.buttonAccept.setVisible(false);
		this.buttonReject.setVisible(false);
		this.PrintLine("\n");
		if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
			CoopMaster.instance.addListener(this);
			CoopMaster.instance.invokeServer("get-parameter", "external-ip", new ICoopServerMessageListener(){
				
				public void OnCoopServerMessage(String int1, String int2, String boolean1) {
					UIServerToolbox.this.externalAddress = boolean1;
					String boolean2 = "null".equals(UIServerToolbox.this.externalAddress) ? Translator.getText("IGUI_ServerToolBox_IPUnknown") : UIServerToolbox.this.externalAddress;
					UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_ServerAddress", boolean2));
					UIServerToolbox.this.PrintLine("");
					UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_AdminPanel"));
					UIServerToolbox.this.PrintLine("");
				}
			});

			if (SteamUtils.isSteamModeEnabled()) {
				CoopMaster.instance.invokeServer("get-parameter", "steam-id", new ICoopServerMessageListener(){
					
					public void OnCoopServerMessage(String int1, String int2, String boolean1) {
						UIServerToolbox.this.steamID = boolean1;
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_SteamID", UIServerToolbox.this.steamID));
						UIServerToolbox.this.PrintLine("");
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite1"));
						UIServerToolbox.this.PrintLine("");
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite2"));
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite3"));
						UIServerToolbox.this.PrintLine("");
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite4"));
						UIServerToolbox.this.PrintLine("");
						UIServerToolbox.this.PrintLine(Translator.getText("IGUI_ServerToolBox_Invite5"));
					}
				});
			}
		}
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTextCentre(Translator.getText("IGUI_ServerToolBox_Title"), this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
			String string = "null".equals(this.externalAddress) ? Translator.getText("IGUI_ServerToolBox_IPUnknown") : this.externalAddress;
			this.DrawText(Translator.getText("IGUI_ServerToolBox_ExternalIP", string), 7.0, 19.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
			if (!this.incomingConnections.isEmpty()) {
				String string2 = (String)this.incomingConnections.get(0);
				if (string2 != null) {
					this.DrawText(Translator.getText("IGUI_ServerToolBox_UserConnecting", string2), 10.0, 205.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
				}
			}
		}
	}

	public void update() {
		if (this.isVisible()) {
			if (this.incomingConnections.isEmpty()) {
				this.buttonReject.setVisible(false);
				this.buttonAccept.setVisible(false);
			} else {
				this.buttonReject.setVisible(true);
				this.buttonAccept.setVisible(true);
			}

			super.update();
		}
	}

	void UpdateViewPos() {
		this.OutputLog.TopLineIndex = this.OutputLog.Lines.size() - this.OutputLog.NumVisibleLines;
		if (this.OutputLog.TopLineIndex < 0) {
			this.OutputLog.TopLineIndex = 0;
		}

		this.ScrollBarV.scrollToBottom();
	}

	public synchronized void OnCoopServerMessage(String string, String string2, String string3) {
		if (Objects.equals(string, "login-attempt")) {
			this.PrintLine(string3 + " is connecting");
			if (this.autoAccept) {
				this.PrintLine("Accepted connection from " + string3);
				CoopMaster.instance.sendMessage("approve-login-attempt", string3);
			} else {
				this.incomingConnections.add(string3);
				this.setVisible(true);
			}
		}
	}

	void PrintLine(String string) {
		this.OutputLog.SetText(this.OutputLog.Text + string + "\n");
		this.UpdateViewPos();
	}

	public void shutdown() {
		if (CoopMaster.instance != null) {
			CoopMaster.instance.removeListener(this);
		}
	}

	public void DoubleClick(String string, int int1, int int2) {
	}

	public void ModalClick(String string, String string2) {
	}

	public void Selected(String string, int int1, int int2) {
		String string2;
		if (Objects.equals(string, "accept")) {
			string2 = (String)this.incomingConnections.get(0);
			this.incomingConnections.remove(0);
			this.PrintLine("Accepted connection from " + string2);
			CoopMaster.instance.sendMessage("approve-login-attempt", string2);
		}

		if (Objects.equals(string, "reject")) {
			string2 = (String)this.incomingConnections.get(0);
			this.incomingConnections.remove(0);
			this.PrintLine("Rejected connection from " + string2);
			CoopMaster.instance.sendMessage("reject-login-attempt", string2);
		}
	}
}
