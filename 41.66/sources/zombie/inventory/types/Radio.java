package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.Talker;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.properties.PropertyContainer;
import zombie.interfaces.IUpdater;
import zombie.iso.IsoGridSquare;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.scripting.objects.Item;
import zombie.ui.UIFont;


public final class Radio extends Moveable implements Talker,IUpdater,WaveSignalDevice {
	protected DeviceData deviceData = null;
	protected GameTime gameTime;
	protected int lastMin = 0;
	protected boolean doPowerTick = false;
	protected int listenCnt = 0;

	public Radio(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.deviceData = new DeviceData(this);
		this.gameTime = GameTime.getInstance();
		this.canBeDroppedOnFloor = true;
	}

	public DeviceData getDeviceData() {
		return this.deviceData;
	}

	public void setDeviceData(DeviceData deviceData) {
		if (deviceData == null) {
			deviceData = new DeviceData(this);
		}

		this.deviceData = deviceData;
		this.deviceData.setParent(this);
	}

	public void doReceiveSignal(int int1) {
		if (this.deviceData != null) {
			this.deviceData.doReceiveSignal(int1);
		}
	}

	public void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1) {
		if (!ZomboidRadio.isStaticSound(string)) {
			this.doReceiveSignal(int1);
		}

		IsoPlayer player = this.getPlayer();
		if (player != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0F && !player.Traits.Deaf.isSet()) {
			player.SayRadio(string, float1, float2, float3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), this.deviceData.getChannel(), "radio");
			if (string2 != null) {
				LuaEventManager.triggerEvent("OnDeviceText", string2, -1, -1, -1, string, this);
			}
		}
	}

	public void AddDeviceText(ChatMessage chatMessage, float float1, float float2, float float3, String string, int int1) {
		if (!ZomboidRadio.isStaticSound(chatMessage.getText())) {
			this.doReceiveSignal(int1);
		}

		IsoPlayer player = this.getPlayer();
		if (player != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0F) {
			ChatManager.getInstance().showRadioMessage(chatMessage);
			if (string != null) {
				LuaEventManager.triggerEvent("OnDeviceText", string, -1, -1, -1, chatMessage, this);
			}
		}
	}

	public boolean HasPlayerInRange() {
		return false;
	}

	public boolean ReadFromWorldSprite(String string) {
		if (string == null) {
			return false;
		} else {
			IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
			if (sprite != null) {
				PropertyContainer propertyContainer = sprite.getProperties();
				if (propertyContainer.Is("IsMoveAble")) {
					if (propertyContainer.Is("CustomItem")) {
						this.customItem = propertyContainer.Val("CustomItem");
					}

					this.worldSprite = string;
					return true;
				}
			}

			System.out.println("Warning: Radio worldsprite not valid, sprite = " + (string == null ? "null" : string));
			return false;
		}
	}

	public int getSaveType() {
		return Item.Type.Radio.ordinal();
	}

	public float getDelta() {
		return this.deviceData != null ? this.deviceData.getPower() : 0.0F;
	}

	public void setDelta(float float1) {
		if (this.deviceData != null) {
			this.deviceData.setPower(float1);
		}
	}

	public IsoGridSquare getSquare() {
		return this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer ? this.container.parent.getSquare() : null;
	}

	public float getX() {
		IsoGridSquare square = this.getSquare();
		return square == null ? 0.0F : (float)square.getX();
	}

	public float getY() {
		IsoGridSquare square = this.getSquare();
		return square == null ? 0.0F : (float)square.getY();
	}

	public float getZ() {
		IsoGridSquare square = this.getSquare();
		return square == null ? 0.0F : (float)square.getZ();
	}

	public IsoPlayer getPlayer() {
		return this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer ? (IsoPlayer)this.container.parent : null;
	}

	public void render() {
	}

	public void renderlast() {
	}

	public void update() {
		if (this.deviceData != null) {
			if (!GameServer.bServer && !GameClient.bClient || GameClient.bClient) {
				IsoPlayer player = IsoPlayer.getInstance();
				if (player.getEquipedRadio() == this) {
					this.deviceData.update(false, true);
				} else {
					this.deviceData.cleanSoundsAndEmitter();
				}
			}
		}
	}

	public boolean IsSpeaking() {
		return false;
	}

	public void Say(String string) {
	}

	public String getSayLine() {
		return null;
	}

	public String getTalkerType() {
		return "radio";
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (this.deviceData != null) {
			byteBuffer.put((byte)1);
			this.deviceData.save(byteBuffer, boolean1);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		if (this.deviceData == null) {
			this.deviceData = new DeviceData(this);
		}

		if (byteBuffer.get() == 1) {
			this.deviceData.load(byteBuffer, int1, false);
		}

		this.deviceData.setParent(this);
	}
}
