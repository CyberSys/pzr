package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Drainable;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIFont;


public final class VehiclePart implements ChatElementOwner,WaveSignalDevice {
	protected BaseVehicle vehicle;
	protected boolean bCreated;
	protected String partId;
	protected VehicleScript.Part scriptPart;
	protected ItemContainer container;
	protected InventoryItem item;
	protected KahluaTable modData;
	protected float lastUpdated = -1.0F;
	protected short updateFlags;
	protected VehiclePart parent;
	protected VehicleDoor door;
	protected VehicleWindow window;
	protected ArrayList children;
	protected String category;
	protected int condition = -1;
	protected boolean specificItem = true;
	protected float wheelFriction = 0.0F;
	protected int mechanicSkillInstaller = 0;
	private float suspensionDamping = 0.0F;
	private float suspensionCompression = 0.0F;
	private float engineLoudness = 0.0F;
	protected VehicleLight light;
	protected DeviceData deviceData;
	protected ChatElement chatElement;
	protected boolean hasPlayerInRange;

	public VehiclePart(BaseVehicle baseVehicle) {
		this.vehicle = baseVehicle;
	}

	public BaseVehicle getVehicle() {
		return this.vehicle;
	}

	public void setScriptPart(VehicleScript.Part part) {
		this.scriptPart = part;
	}

	public VehicleScript.Part getScriptPart() {
		return this.scriptPart;
	}

	public ItemContainer getItemContainer() {
		return this.container;
	}

	public void setItemContainer(ItemContainer itemContainer) {
		if (itemContainer != null) {
			itemContainer.parent = this.getVehicle();
			itemContainer.vehiclePart = this;
		}

		this.container = itemContainer;
	}

	public boolean hasModData() {
		return this.modData != null && !this.modData.isEmpty();
	}

	public KahluaTable getModData() {
		if (this.modData == null) {
			this.modData = LuaManager.platform.newTable();
		}

		return this.modData;
	}

	public float getLastUpdated() {
		return this.lastUpdated;
	}

	public void setLastUpdated(float float1) {
		this.lastUpdated = float1;
	}

	public String getId() {
		return this.scriptPart == null ? this.partId : this.scriptPart.id;
	}

	public int getIndex() {
		return this.vehicle.parts.indexOf(this);
	}

	public String getArea() {
		return this.scriptPart == null ? null : this.scriptPart.area;
	}

	public ArrayList getItemType() {
		return this.scriptPart == null ? null : this.scriptPart.itemType;
	}

	public KahluaTable getTable(String string) {
		if (this.scriptPart != null && this.scriptPart.tables != null) {
			KahluaTable kahluaTable = (KahluaTable)this.scriptPart.tables.get(string);
			return kahluaTable == null ? null : LuaManager.copyTable(kahluaTable);
		} else {
			return null;
		}
	}

	public InventoryItem getInventoryItem() {
		return this.item;
	}

	public void setInventoryItem(InventoryItem inventoryItem, int int1) {
		this.item = inventoryItem;
		this.doInventoryItemStats(inventoryItem, int1);
		this.getVehicle().updateTotalMass();
		this.getVehicle().bDoDamageOverlay = true;
		if (this.isSetAllModelsVisible()) {
			this.setAllModelsVisible(inventoryItem != null);
		}

		this.getVehicle().updatePartStats();
		this.getVehicle().updateBulletStats();
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.setInventoryItem(inventoryItem, 0);
	}

	public boolean isSetAllModelsVisible() {
		return this.scriptPart != null && this.scriptPart.bSetAllModelsVisible;
	}

	public void setAllModelsVisible(boolean boolean1) {
		if (this.scriptPart != null && this.scriptPart.models != null && !this.scriptPart.models.isEmpty()) {
			for (int int1 = 0; int1 < this.scriptPart.models.size(); ++int1) {
				VehicleScript.Model model = (VehicleScript.Model)this.scriptPart.models.get(int1);
				this.vehicle.setModelVisible(this, model, boolean1);
			}
		}
	}

	public void doInventoryItemStats(InventoryItem inventoryItem, int int1) {
		if (inventoryItem != null) {
			if (this.isContainer()) {
				if (inventoryItem.getMaxCapacity() > 0 && this.getScriptPart().container.conditionAffectsCapacity) {
					this.setContainerCapacity((int)getNumberByCondition((float)inventoryItem.getMaxCapacity(), (float)inventoryItem.getCondition(), 5.0F));
				} else if (inventoryItem.getMaxCapacity() > 0) {
					this.setContainerCapacity(inventoryItem.getMaxCapacity());
				}

				this.setContainerContentAmount(inventoryItem.getItemCapacity());
			}

			this.setSuspensionCompression(getNumberByCondition(inventoryItem.getSuspensionCompression(), (float)inventoryItem.getCondition(), 0.6F));
			this.setSuspensionDamping(getNumberByCondition(inventoryItem.getSuspensionDamping(), (float)inventoryItem.getCondition(), 0.6F));
			if (inventoryItem.getEngineLoudness() > 0.0F) {
				this.setEngineLoudness(getNumberByCondition(inventoryItem.getEngineLoudness(), (float)inventoryItem.getCondition(), 10.0F));
			}

			this.setCondition(inventoryItem.getCondition());
			this.setMechanicSkillInstaller(int1);
		} else {
			if (this.scriptPart != null && this.scriptPart.container != null) {
				if (this.scriptPart.container.capacity > 0) {
					this.setContainerCapacity(this.scriptPart.container.capacity);
				} else {
					this.setContainerCapacity(0);
				}
			}

			this.setMechanicSkillInstaller(0);
			this.setContainerContentAmount(0.0F);
			this.setSuspensionCompression(0.0F);
			this.setSuspensionDamping(0.0F);
			this.setWheelFriction(0.0F);
			this.setEngineLoudness(0.0F);
		}
	}

	public void setRandomCondition(InventoryItem inventoryItem) {
		VehicleType vehicleType = VehicleType.getTypeFromName(this.getVehicle().getVehicleType());
		int int1;
		if (this.getVehicle().isGoodCar()) {
			int1 = 100;
			if (inventoryItem != null) {
				int1 = inventoryItem.getConditionMax();
			}

			this.setCondition(Rand.Next(int1 - int1 / 3, int1));
			if (inventoryItem != null) {
				inventoryItem.setCondition(this.getCondition());
			}
		} else {
			int1 = 100;
			if (inventoryItem != null) {
				int1 = inventoryItem.getConditionMax();
			}

			if (vehicleType != null) {
				int1 = (int)((float)int1 * vehicleType.getRandomBaseVehicleQuality());
			}

			float float1 = 100.0F;
			int int2;
			if (inventoryItem != null) {
				int2 = inventoryItem.getChanceToSpawnDamaged();
				if (vehicleType != null) {
					int2 += vehicleType.chanceToPartDamage;
				}

				if (int2 > 0 && Rand.Next(100) < int2) {
					float1 = (float)Rand.Next(int1 - int1 / 2, int1);
				}
			} else {
				int2 = 30;
				if (vehicleType != null) {
					int2 += vehicleType.chanceToPartDamage;
				}

				if (Rand.Next(100) < int2) {
					float1 = Rand.Next((float)int1 * 0.5F, (float)int1);
				}
			}

			switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
			case 1: 
				float1 -= Rand.Next(float1 * 0.3F, Rand.Next(float1 * 0.3F, float1 * 0.9F));
				break;
			
			case 2: 
				float1 -= Rand.Next(float1 * 0.1F, float1 * 0.3F);
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				float1 += Rand.Next(float1 * 0.2F, float1 * 0.4F);
				break;
			
			case 5: 
				float1 += Rand.Next(float1 * 0.5F, float1 * 0.9F);
			
			}

			float1 = Math.max(0.0F, float1);
			float1 = Math.min(100.0F, float1);
			this.setCondition((int)float1);
			if (inventoryItem != null) {
				inventoryItem.setCondition(this.getCondition());
			}
		}
	}

	public void setGeneralCondition(InventoryItem inventoryItem, float float1, float float2) {
		byte byte1 = 100;
		int int1 = (int)((float)byte1 * float1);
		float float3 = 100.0F;
		int int2;
		if (inventoryItem != null) {
			int2 = inventoryItem.getChanceToSpawnDamaged();
			int2 = (int)((float)int2 + float2);
			if (int2 > 0 && Rand.Next(100) < int2) {
				float3 = (float)Rand.Next(int1 - int1 / 2, int1);
			}
		} else {
			byte byte2 = 30;
			int2 = (int)((float)byte2 + float2);
			if (Rand.Next(100) < int2) {
				float3 = Rand.Next((float)int1 * 0.5F, (float)int1);
			}
		}

		switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
		case 1: 
			float3 -= Rand.Next(float3 * 0.3F, Rand.Next(float3 * 0.3F, float3 * 0.9F));
			break;
		
		case 2: 
			float3 -= Rand.Next(float3 * 0.1F, float3 * 0.3F);
		
		case 3: 
		
		default: 
			break;
		
		case 4: 
			float3 += Rand.Next(float3 * 0.2F, float3 * 0.4F);
			break;
		
		case 5: 
			float3 += Rand.Next(float3 * 0.5F, float3 * 0.9F);
		
		}
		float3 = Math.max(0.0F, float3);
		float3 = Math.min(100.0F, float3);
		this.setCondition((int)float3);
		if (inventoryItem != null) {
			inventoryItem.setCondition(this.getCondition());
		}
	}

	public static float getNumberByCondition(float float1, float float2, float float3) {
		float2 += 20.0F * (100.0F - float2) / 100.0F;
		float float4 = float2 / 100.0F;
		return (float)Math.round(Math.max(float3, float1 * float4) * 100.0F) / 100.0F;
	}

	public boolean isContainer() {
		if (this.scriptPart == null) {
			return false;
		} else {
			return this.scriptPart.container != null;
		}
	}

	public int getContainerCapacity() {
		return this.getContainerCapacity((IsoGameCharacter)null);
	}

	public int getContainerCapacity(IsoGameCharacter gameCharacter) {
		if (!this.isContainer()) {
			return 0;
		} else if (this.getItemContainer() != null) {
			return gameCharacter == null ? this.getItemContainer().getCapacity() : this.getItemContainer().getEffectiveCapacity(gameCharacter);
		} else if (this.getInventoryItem() != null) {
			return this.scriptPart.container.conditionAffectsCapacity ? (int)getNumberByCondition((float)this.getInventoryItem().getMaxCapacity(), (float)this.getCondition(), 5.0F) : this.getInventoryItem().getMaxCapacity();
		} else {
			return this.scriptPart.container.capacity;
		}
	}

	public void setContainerCapacity(int int1) {
		if (this.isContainer()) {
			if (this.getItemContainer() != null) {
				this.getItemContainer().Capacity = int1;
			}
		}
	}

	public String getContainerContentType() {
		return !this.isContainer() ? null : this.scriptPart.container.contentType;
	}

	public float getContainerContentAmount() {
		if (!this.isContainer()) {
			return 0.0F;
		} else {
			if (this.hasModData()) {
				Object object = this.getModData().rawget("contentAmount");
				if (object instanceof Double) {
					return ((Double)object).floatValue();
				}
			}

			return 0.0F;
		}
	}

	public void setContainerContentAmount(float float1) {
		this.setContainerContentAmount(float1, false, false);
	}

	public void setContainerContentAmount(float float1, boolean boolean1, boolean boolean2) {
		if (this.isContainer()) {
			int int1 = this.scriptPart.container.capacity;
			if (this.getInventoryItem() != null) {
				int1 = this.getInventoryItem().getMaxCapacity();
			}

			if (!boolean1) {
				float1 = Math.min(float1, (float)int1);
			}

			float1 = Math.max(float1, 0.0F);
			this.getModData().rawset("contentAmount", (double)float1);
			if (this.getInventoryItem() != null) {
				this.getInventoryItem().setItemCapacity(float1);
			}

			if (!boolean2) {
				this.getVehicle().updateTotalMass();
			}
		}
	}

	public int getContainerSeatNumber() {
		return !this.isContainer() ? -1 : this.scriptPart.container.seat;
	}

	public String getLuaFunction(String string) {
		return this.scriptPart != null && this.scriptPart.luaFunctions != null ? (String)this.scriptPart.luaFunctions.get(string) : null;
	}

	protected VehicleScript.Model getScriptModelById(String string) {
		if (this.scriptPart != null && this.scriptPart.models != null) {
			for (int int1 = 0; int1 < this.scriptPart.models.size(); ++int1) {
				VehicleScript.Model model = (VehicleScript.Model)this.scriptPart.models.get(int1);
				if (string.equals(model.id)) {
					return model;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public void setModelVisible(String string, boolean boolean1) {
		VehicleScript.Model model = this.getScriptModelById(string);
		if (model != null) {
			this.vehicle.setModelVisible(this, model, boolean1);
		}
	}

	public VehiclePart getParent() {
		return this.parent;
	}

	public void addChild(VehiclePart vehiclePart) {
		if (this.children == null) {
			this.children = new ArrayList();
		}

		this.children.add(vehiclePart);
	}

	public int getChildCount() {
		return this.children == null ? 0 : this.children.size();
	}

	public VehiclePart getChild(int int1) {
		return this.children != null && int1 >= 0 && int1 < this.children.size() ? (VehiclePart)this.children.get(int1) : null;
	}

	public VehicleDoor getDoor() {
		return this.door;
	}

	public VehicleWindow getWindow() {
		return this.window;
	}

	public VehiclePart getChildWindow() {
		for (int int1 = 0; int1 < this.getChildCount(); ++int1) {
			VehiclePart vehiclePart = this.getChild(int1);
			if (vehiclePart.getWindow() != null) {
				return vehiclePart;
			}
		}

		return null;
	}

	public VehicleWindow findWindow() {
		VehiclePart vehiclePart = this.getChildWindow();
		return vehiclePart == null ? null : vehiclePart.getWindow();
	}

	public VehicleScript.Anim getAnimById(String string) {
		if (this.scriptPart != null && this.scriptPart.anims != null) {
			for (int int1 = 0; int1 < this.scriptPart.anims.size(); ++int1) {
				VehicleScript.Anim anim = (VehicleScript.Anim)this.scriptPart.anims.get(int1);
				if (anim.id.equals(string)) {
					return anim;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		GameWindow.WriteStringUTF(byteBuffer, this.getId());
		byteBuffer.put((byte)(this.bCreated ? 1 : 0));
		byteBuffer.putFloat(this.lastUpdated);
		if (this.getInventoryItem() == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.getInventoryItem().saveWithSize(byteBuffer, false);
		}

		if (this.getItemContainer() == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.getItemContainer().save(byteBuffer);
		}

		if (this.hasModData() && !this.getModData().isEmpty()) {
			byteBuffer.put((byte)1);
			this.getModData().save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		if (this.getDeviceData() == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.getDeviceData().save(byteBuffer, false);
		}

		if (this.light == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.light.save(byteBuffer);
		}

		if (this.door == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.door.save(byteBuffer);
		}

		if (this.window == null) {
			byteBuffer.put((byte)0);
		} else {
			byteBuffer.put((byte)1);
			this.window.save(byteBuffer);
		}

		byteBuffer.putInt(this.condition);
		byteBuffer.putFloat(this.wheelFriction);
		byteBuffer.putInt(this.mechanicSkillInstaller);
		byteBuffer.putFloat(this.suspensionCompression);
		byteBuffer.putFloat(this.suspensionDamping);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.partId = GameWindow.ReadStringUTF(byteBuffer);
		this.bCreated = byteBuffer.get() == 1;
		this.lastUpdated = byteBuffer.getFloat();
		if (byteBuffer.get() == 1) {
			InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, int1);
			this.item = inventoryItem;
		}

		if (byteBuffer.get() == 1) {
			if (this.container == null) {
				this.container = new ItemContainer();
				this.container.parent = this.getVehicle();
				this.container.vehiclePart = this;
			}

			this.container.getItems().clear();
			this.container.ID = 0;
			this.container.load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			this.getModData().load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			if (this.getDeviceData() == null) {
				this.createSignalDevice();
			}

			this.getDeviceData().load(byteBuffer, int1, false);
		}

		if (byteBuffer.get() == 1) {
			if (this.light == null) {
				this.light = new VehicleLight();
			}

			this.light.load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			if (this.door == null) {
				this.door = new VehicleDoor(this);
			}

			this.door.load(byteBuffer, int1);
		}

		if (byteBuffer.get() == 1) {
			if (this.window == null) {
				this.window = new VehicleWindow(this);
			}

			this.window.load(byteBuffer, int1);
		}

		if (int1 >= 116) {
			this.setCondition(byteBuffer.getInt());
		}

		if (int1 >= 118) {
			this.setWheelFriction(byteBuffer.getFloat());
			this.setMechanicSkillInstaller(byteBuffer.getInt());
		}

		if (int1 >= 119) {
			this.setSuspensionCompression(byteBuffer.getFloat());
			this.setSuspensionDamping(byteBuffer.getFloat());
		}
	}

	public int getWheelIndex() {
		if (this.scriptPart != null && this.scriptPart.wheel != null) {
			for (int int1 = 0; int1 < this.vehicle.script.getWheelCount(); ++int1) {
				VehicleScript.Wheel wheel = this.vehicle.script.getWheel(int1);
				if (this.scriptPart.wheel.equals(wheel.id)) {
					return int1;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	public void createSpotLight(float float1, float float2, float float3, float float4, float float5, int int1) {
		this.light = this.light == null ? new VehicleLight() : this.light;
		this.light.offset.set(float1, float2, 0.0F);
		this.light.dist = float3;
		this.light.intensity = float4;
		this.light.dot = float5;
		this.light.focusing = int1;
	}

	public VehicleLight getLight() {
		return this.light;
	}

	public float getLightDistance() {
		return this.light == null ? 0.0F : 8.0F + 16.0F * (float)this.getCondition() / 100.0F;
	}

	public float getLightIntensity() {
		return this.light == null ? 0.0F : 0.5F + 0.25F * (float)this.getCondition() / 100.0F;
	}

	public float getLightFocusing() {
		return this.light == null ? 0.0F : (float)(10 + (int)(90.0F * (1.0F - (float)this.getCondition() / 100.0F)));
	}

	public void setLightActive(boolean boolean1) {
		if (this.light != null && this.light.active != boolean1) {
			this.light.active = boolean1;
			if (GameServer.bServer) {
				BaseVehicle baseVehicle = this.vehicle;
				baseVehicle.updateFlags = (short)(baseVehicle.updateFlags | 8);
			}
		}
	}

	public DeviceData createSignalDevice() {
		if (this.deviceData == null) {
			this.deviceData = new DeviceData(this);
		}

		if (this.chatElement == null) {
			this.chatElement = new ChatElement(this, 5, "device");
		}

		return this.deviceData;
	}

	public boolean hasDevicePower() {
		return this.vehicle.getBatteryCharge() > 0.0F;
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

	public float getDelta() {
		return this.deviceData != null ? this.deviceData.getPower() : 0.0F;
	}

	public void setDelta(float float1) {
		if (this.deviceData != null) {
			this.deviceData.setPower(float1);
		}
	}

	public float getX() {
		return this.vehicle.getX();
	}

	public float getY() {
		return this.vehicle.getY();
	}

	public float getZ() {
		return this.vehicle.getZ();
	}

	public IsoGridSquare getSquare() {
		return this.vehicle.getSquare();
	}

	public void AddDeviceText(String string, float float1, float float2, float float3, String string2, int int1) {
		if (this.deviceData != null && this.deviceData.getIsTurnedOn()) {
			this.deviceData.doReceiveSignal(int1);
			if (this.deviceData.getDeviceVolume() > 0.0F) {
				this.chatElement.addChatLine(string, float1, float2, float3, UIFont.Medium, (float)this.deviceData.getDeviceVolumeRange(), "default", true, true, true, true, true, true);
				if (string2 != null) {
					LuaEventManager.triggerEvent("OnDeviceText", string2, this.getX(), this.getY(), this.getZ(), string, this);
				}
			}
		}
	}

	public boolean HasPlayerInRange() {
		return this.hasPlayerInRange;
	}

	private boolean playerWithinBounds(IsoPlayer player, float float1) {
		if (player != null && !player.isDead()) {
			return (player.getX() > this.getX() - float1 || this.getX() < this.getX() + float1) && (player.getY() > this.getY() - float1 || this.getY() < this.getY() + float1);
		} else {
			return false;
		}
	}

	public void updateSignalDevice() {
		if (this.deviceData != null) {
			if (GameClient.bClient) {
				this.deviceData.updateSimple();
			} else {
				this.deviceData.update(true, this.hasPlayerInRange);
			}

			if (!GameServer.bServer) {
				this.hasPlayerInRange = false;
				if (this.deviceData.getIsTurnedOn()) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						IsoPlayer player = IsoPlayer.players[int1];
						if (this.playerWithinBounds(player, (float)this.deviceData.getDeviceVolumeRange() * 0.6F)) {
							this.hasPlayerInRange = true;
							break;
						}
					}
				}

				this.chatElement.setHistoryRange((float)this.deviceData.getDeviceVolumeRange() * 0.6F);
				this.chatElement.update();
			} else {
				this.hasPlayerInRange = false;
			}
		}
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String string) {
		this.category = string;
	}

	public int getCondition() {
		return this.condition;
	}

	public void setCondition(int int1) {
		int1 = Math.min(100, int1);
		int1 = Math.max(0, int1);
		if (this.getVehicle().getDriver() != null) {
			if (this.condition > 60 && int1 < 60 && int1 > 40) {
				LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
			}

			if (this.condition > 40 && int1 < 40) {
				LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
			}
		}

		this.condition = int1;
		if (this.getInventoryItem() != null) {
			this.getInventoryItem().setCondition(int1);
		}

		this.getVehicle().bDoDamageOverlay = true;
		if ("lightbar".equals(this.getId())) {
			this.getVehicle().lightbarLightsMode.set(0);
			this.getVehicle().setLightbarSirenMode(0);
		}
	}

	public void damage(int int1) {
		if (this.getWindow() != null) {
			this.getWindow().damage(int1);
		} else {
			this.setCondition(this.getCondition() - int1);
			this.getVehicle().transmitPartCondition(this);
		}
	}

	public boolean isSpecificItem() {
		return this.specificItem;
	}

	public void setSpecificItem(boolean boolean1) {
		this.specificItem = boolean1;
	}

	public float getWheelFriction() {
		return this.wheelFriction;
	}

	public void setWheelFriction(float float1) {
		this.wheelFriction = float1;
	}

	public int getMechanicSkillInstaller() {
		return this.mechanicSkillInstaller;
	}

	public void setMechanicSkillInstaller(int int1) {
		this.mechanicSkillInstaller = int1;
	}

	public float getSuspensionDamping() {
		return this.suspensionDamping;
	}

	public void setSuspensionDamping(float float1) {
		this.suspensionDamping = float1;
	}

	public float getSuspensionCompression() {
		return this.suspensionCompression;
	}

	public void setSuspensionCompression(float float1) {
		this.suspensionCompression = float1;
	}

	public float getEngineLoudness() {
		return this.engineLoudness;
	}

	public void setEngineLoudness(float float1) {
		this.engineLoudness = float1;
	}

	public void repair() {
		VehicleScript vehicleScript = this.vehicle.getScript();
		float float1 = this.getContainerContentAmount();
		if (this.getItemType() != null && !this.getItemType().isEmpty() && this.getInventoryItem() == null) {
			String string = (String)this.getItemType().get(Rand.Next(this.getItemType().size()));
			if (string != null && !string.isEmpty()) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
				if (inventoryItem != null) {
					this.setInventoryItem(inventoryItem);
					if (inventoryItem.getMaxCapacity() > 0) {
						inventoryItem.setItemCapacity((float)inventoryItem.getMaxCapacity());
					}

					this.vehicle.transmitPartItem(this);
					this.callLuaVoid(this.getLuaFunction("init"), this.vehicle, this);
				}
			}
		}

		if (this.getDoor() != null && this.getDoor().isLockBroken()) {
			this.getDoor().setLockBroken(false);
			this.vehicle.transmitPartDoor(this);
		}

		if (this.getCondition() != 100) {
			this.setCondition(100);
			if (this.getInventoryItem() != null) {
				this.doInventoryItemStats(this.getInventoryItem(), this.getMechanicSkillInstaller());
			}

			this.vehicle.transmitPartCondition(this);
		}

		if (this.isContainer() && this.getItemContainer() == null && float1 != (float)this.getContainerCapacity()) {
			this.setContainerContentAmount((float)this.getContainerCapacity());
			this.vehicle.transmitPartModData(this);
		}

		if (this.getInventoryItem() instanceof Drainable && ((Drainable)this.getInventoryItem()).getUsedDelta() < 1.0F) {
			((Drainable)this.getInventoryItem()).setUsedDelta(1.0F);
			this.vehicle.transmitPartUsedDelta(this);
		}

		if ("Engine".equalsIgnoreCase(this.getId())) {
			byte byte1 = 100;
			int int1 = (int)((double)vehicleScript.getEngineLoudness() * SandboxOptions.getInstance().ZombieAttractionMultiplier.getValue());
			int int2 = (int)vehicleScript.getEngineForce();
			this.vehicle.setEngineFeature(byte1, int1, int2);
			this.vehicle.transmitEngine();
		}

		this.vehicle.updatePartStats();
		this.vehicle.updateBulletStats();
	}

	private void callLuaVoid(String string, Object object, Object object2) {
		Object object3 = LuaManager.getFunctionObject(string);
		if (object3 != null) {
			LuaManager.caller.protectedCallVoid(LuaManager.thread, object3, object, object2);
		}
	}
}
