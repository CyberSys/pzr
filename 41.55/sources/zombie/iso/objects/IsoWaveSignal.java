package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.Talker;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.properties.PropertyContainer;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Radio;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.PresetEntry;
import zombie.radio.devices.WaveSignalDevice;
import zombie.ui.UIFont;


public class IsoWaveSignal extends IsoObject implements WaveSignalDevice,ChatElementOwner,Talker {
	protected IsoLightSource lightSource = null;
	protected boolean lightWasRemoved = false;
	protected int lightSourceRadius = 4;
	protected float nextLightUpdate = 0.0F;
	protected float lightUpdateCnt = 0.0F;
	protected DeviceData deviceData = null;
	protected boolean displayRange = false;
	protected boolean hasPlayerInRange = false;
	protected GameTime gameTime;
	protected ChatElement chatElement;
	protected String talkerType = "device";
	protected static Map deviceDataCache = new HashMap();

	public IsoWaveSignal(IsoCell cell) {
		super(cell);
		this.init(true);
	}

	public IsoWaveSignal(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		this.init(false);
	}

	protected void init(boolean boolean1) {
		this.chatElement = new ChatElement(this, 5, this.talkerType);
		this.gameTime = GameTime.getInstance();
		if (!boolean1) {
			if (this.sprite != null && this.sprite.getProperties() != null) {
				PropertyContainer propertyContainer = this.sprite.getProperties();
				if (propertyContainer.Is("CustomItem") && propertyContainer.Val("CustomItem") != null) {
					this.deviceData = this.cloneDeviceDataFromItem(propertyContainer.Val("CustomItem"));
				}
			}

			if (!GameClient.bClient && this.deviceData != null) {
				this.deviceData.generatePresets();
				this.deviceData.setDeviceVolume(Rand.Next(0.1F, 1.0F));
				this.deviceData.setRandomChannel();
				if (Rand.Next(100) <= 35 && !"Tutorial".equals(Core.GameMode)) {
					this.deviceData.setTurnedOnRaw(true);
				}
			}
		}

		if (this.deviceData == null) {
			this.deviceData = new DeviceData(this);
		}

		this.deviceData.setParent(this);
	}

	public DeviceData cloneDeviceDataFromItem(String string) {
		if (string != null) {
			if (deviceDataCache.containsKey(string) && deviceDataCache.get(string) != null) {
				return ((DeviceData)deviceDataCache.get(string)).getClone();
			}

			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem != null && inventoryItem instanceof Radio) {
				DeviceData deviceData = ((Radio)inventoryItem).getDeviceData();
				if (deviceData != null) {
					deviceDataCache.put(string, deviceData);
					return deviceData.getClone();
				}
			}
		}

		return null;
	}

	public boolean hasChatToDisplay() {
		return this.chatElement.getHasChatToDisplay();
	}

	public boolean HasPlayerInRange() {
		return this.hasPlayerInRange;
	}

	public float getDelta() {
		return this.deviceData != null ? this.deviceData.getPower() : 0.0F;
	}

	public void setDelta(float float1) {
		if (this.deviceData != null) {
			this.deviceData.setPower(float1);
		}
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

	public boolean IsSpeaking() {
		return this.chatElement.IsSpeaking();
	}

	public String getTalkerType() {
		return this.chatElement.getTalkerType();
	}

	public void setTalkerType(String string) {
		this.talkerType = string == null ? "" : string;
		this.chatElement.setTalkerType(this.talkerType);
	}

	public String getSayLine() {
		return this.chatElement.getSayLine();
	}

	public void Say(String string) {
		this.AddDeviceText(string, 1.0F, 1.0F, 1.0F, (String)null, -1, false);
	}

	public void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1) {
		this.AddDeviceText(string, float1, float2, float3, string2, int1, true);
	}

	public void AddDeviceText(String string, int int1, int int2, int int3, String string2, int int4) {
		this.AddDeviceText(string, (float)int1 / 255.0F, (float)int2 / 255.0F, (float)(int3 / 255), string2, int4, true);
	}

	public void AddDeviceText(String string, int int1, int int2, int int3, String string2, int int4, boolean boolean1) {
		this.AddDeviceText(string, (float)int1 / 255.0F, (float)int2 / 255.0F, (float)int3 / 255.0F, string2, int4, boolean1);
	}

	public void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1, boolean boolean1) {
		if (this.deviceData != null && this.deviceData.getIsTurnedOn()) {
			if (!ZomboidRadio.isStaticSound(string)) {
				this.deviceData.doReceiveSignal(int1);
			}

			if (this.deviceData.getDeviceVolume() > 0.0F) {
				this.chatElement.addChatLine(string, float1, float2, float3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), "default", true, true, true, true, true, true);
				if (string2 != null) {
					LuaEventManager.triggerEvent("OnDeviceText", string2, this.getX(), this.getY(), this.getZ(), string, this);
				}
			}
		}
	}

	public void renderlast() {
		if (this.chatElement.getHasChatToDisplay()) {
			if (this.getDeviceData() != null && !this.getDeviceData().getIsTurnedOn()) {
				this.chatElement.clear(IsoCamera.frameState.playerIndex);
				return;
			}

			float float1 = IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0);
			float float2 = IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0);
			float1 = float1 - IsoCamera.getOffX() - this.offsetX;
			float2 = float2 - IsoCamera.getOffY() - this.offsetY;
			float1 += (float)(32 * Core.TileScale);
			float2 += (float)(50 * Core.TileScale);
			float1 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			float2 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float1, (int)float2);
		}
	}

	public void renderlastold2() {
		if (this.chatElement.getHasChatToDisplay()) {
			float float1 = IsoUtils.XToScreen(this.getX(), this.getY(), this.getZ(), 0);
			float float2 = IsoUtils.YToScreen(this.getX(), this.getY(), this.getZ(), 0);
			float1 = float1 - IsoCamera.getOffX() - this.offsetX;
			float2 = float2 - IsoCamera.getOffY() - this.offsetY;
			float1 += 28.0F;
			float2 += 180.0F;
			float1 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			float2 /= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
			float1 += (float)IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
			float2 += (float)IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
			this.chatElement.renderBatched(IsoPlayer.getPlayerIndex(), (int)float1, (int)float2);
		}
	}

	protected boolean playerWithinBounds(IsoPlayer player, float float1) {
		if (player == null) {
			return false;
		} else {
			return (player.getX() > this.getX() - float1 || player.getX() < this.getX() + float1) && (player.getY() > this.getY() - float1 || player.getY() < this.getY() + float1);
		}
	}

	public void update() {
		if (this.deviceData != null) {
			if ((GameServer.bServer || GameClient.bClient) && !GameServer.bServer) {
				this.deviceData.updateSimple();
			} else {
				this.deviceData.update(true, this.hasPlayerInRange);
			}

			if (!GameServer.bServer) {
				this.hasPlayerInRange = false;
				if (this.deviceData.getIsTurnedOn()) {
					IsoPlayer player = IsoPlayer.getInstance();
					if (this.playerWithinBounds(player, (float)this.deviceData.getDeviceVolumeRange() * 0.6F)) {
						this.hasPlayerInRange = true;
					}

					this.updateLightSource();
				} else {
					this.removeLightSourceFromWorld();
				}

				this.chatElement.setHistoryRange((float)this.deviceData.getDeviceVolumeRange() * 0.6F);
				this.chatElement.update();
			} else {
				this.hasPlayerInRange = false;
			}
		}
	}

	protected void updateLightSource() {
	}

	protected void removeLightSourceFromWorld() {
		if (this.lightSource != null) {
			IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
			this.lightSource = null;
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (this.deviceData == null) {
			this.deviceData = new DeviceData(this);
		}

		if (byteBuffer.get() == 1) {
			this.deviceData.load(byteBuffer, int1, true);
		}

		this.deviceData.setParent(this);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (this.deviceData != null) {
			byteBuffer.put((byte)1);
			this.deviceData.save(byteBuffer, true);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void addToWorld() {
		if (!GameServer.bServer) {
			ZomboidRadio.getInstance().RegisterDevice(this);
		}

		if (this.getCell() != null) {
			this.getCell().addToStaticUpdaterObjectList(this);
		}

		super.addToWorld();
	}

	public void removeFromWorld() {
		if (!GameServer.bServer) {
			ZomboidRadio.getInstance().UnRegisterDevice(this);
		}

		this.removeLightSourceFromWorld();
		this.lightSource = null;
		if (this.deviceData != null) {
			this.deviceData.cleanSoundsAndEmitter();
		}

		super.removeFromWorld();
	}

	public void removeFromSquare() {
		super.removeFromSquare();
		this.square = null;
	}

	public void saveState(ByteBuffer byteBuffer) throws IOException {
		if (this.deviceData != null) {
			ArrayList arrayList = this.deviceData.getDevicePresets().getPresets();
			byteBuffer.putInt(arrayList.size());
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				PresetEntry presetEntry = (PresetEntry)arrayList.get(int1);
				GameWindow.WriteString(byteBuffer, presetEntry.getName());
				byteBuffer.putInt(presetEntry.getFrequency());
			}

			byteBuffer.put((byte)(this.deviceData.getIsTurnedOn() ? 1 : 0));
			byteBuffer.putInt(this.deviceData.getChannel());
			byteBuffer.putFloat(this.deviceData.getDeviceVolume());
		}
	}

	public void loadState(ByteBuffer byteBuffer) throws IOException {
		ArrayList arrayList = this.deviceData.getDevicePresets().getPresets();
		int int1 = byteBuffer.getInt();
		for (int int2 = 0; int2 < int1; ++int2) {
			String string = GameWindow.ReadString(byteBuffer);
			int int3 = byteBuffer.getInt();
			if (int2 < arrayList.size()) {
				PresetEntry presetEntry = (PresetEntry)arrayList.get(int2);
				presetEntry.setName(string);
				presetEntry.setFrequency(int3);
			} else {
				this.deviceData.getDevicePresets().addPreset(string, int3);
			}
		}

		while (arrayList.size() > int1) {
			this.deviceData.getDevicePresets().removePreset(int1);
		}

		this.deviceData.setTurnedOnRaw(byteBuffer.get() == 1);
		this.deviceData.setChannelRaw(byteBuffer.getInt());
		this.deviceData.setDeviceVolumeRaw(byteBuffer.getFloat());
	}
}
