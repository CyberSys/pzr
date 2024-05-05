package zombie.radio.devices;

import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatManager;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoWaveSignal;
import zombie.radio.ZomboidRadio;
import zombie.ui.UIFont;


public interface WaveSignalDevice {

	DeviceData getDeviceData();

	void setDeviceData(DeviceData deviceData);

	float getDelta();

	void setDelta(float float1);

	IsoGridSquare getSquare();

	float getX();

	float getY();

	float getZ();

	void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1);

	boolean HasPlayerInRange();

	default void AddDeviceText(IsoPlayer player, String string, float float1, float float2, float float3, String string2, int int1) {
		if (this.getDeviceData() != null && this.getDeviceData().getDeviceVolume() > 0.0F) {
			if (!ZomboidRadio.isStaticSound(string)) {
				this.getDeviceData().doReceiveSignal(int1);
			}

			if (player != null && player.isLocalPlayer() && !player.Traits.Deaf.isSet()) {
				if (this.getDeviceData().getParent() instanceof InventoryItem && player.isEquipped((InventoryItem)this.getDeviceData().getParent())) {
					player.getChatElement().addChatLine(string, float1, float2, float3, UIFont.Medium, (float)this.getDeviceData().getDeviceVolumeRange(), "default", true, true, true, false, false, true);
				} else if (this.getDeviceData().getParent() instanceof IsoWaveSignal) {
					((IsoWaveSignal)this.getDeviceData().getParent()).getChatElement().addChatLine(string, float1, float2, float3, UIFont.Medium, (float)this.getDeviceData().getDeviceVolumeRange(), "default", true, true, true, true, true, true);
				}

				if (ZomboidRadio.isStaticSound(string)) {
					ChatManager.getInstance().showStaticRadioSound(string);
				} else {
					ChatManager.getInstance().showRadioMessage(string, this.getDeviceData().getChannel());
				}

				if (string2 != null) {
					LuaEventManager.triggerEvent("OnDeviceText", string2, -1, -1, -1, string, this);
				}
			}
		}
	}
}
