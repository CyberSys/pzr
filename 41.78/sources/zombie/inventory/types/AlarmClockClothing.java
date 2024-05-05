package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.utils.OnceEvery;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.ItemType;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;


public final class AlarmClockClothing extends Clothing {
	private int alarmHour = -1;
	private int alarmMinutes = -1;
	private boolean alarmSet = false;
	private long ringSound;
	private double ringSince = -1.0;
	private int forceDontRing = -1;
	private String alarmSound = "AlarmClockLoop";
	private int soundRadius = 40;
	private boolean isDigital = true;
	public static short PacketPlayer = 1;
	public static short PacketWorld = 2;
	private static final OnceEvery sendEvery = new OnceEvery(2.0F);

	public AlarmClockClothing(String string, String string2, String string3, String string4, String string5, String string6) {
		super(string, string2, string3, string4, string5, string6);
		this.cat = ItemType.AlarmClockClothing;
		if (this.fullType.contains("Classic")) {
			this.isDigital = false;
		}

		this.randomizeAlarm();
	}

	public AlarmClockClothing(String string, String string2, String string3, Item item, String string4, String string5) {
		super(string, string2, string3, item, string4, string5);
		this.cat = ItemType.AlarmClockClothing;
		if (this.fullType.contains("Classic")) {
			this.isDigital = false;
		}

		this.randomizeAlarm();
	}

	private void randomizeAlarm() {
		if (!Core.bLastStand) {
			if (this.isDigital()) {
				this.alarmHour = Rand.Next(0, 23);
				this.alarmMinutes = (int)Math.floor((double)(Rand.Next(0, 59) / 10)) * 10;
				this.alarmSet = Rand.Next(15) == 1;
			}
		}
	}

	public IsoGridSquare getAlarmSquare() {
		IsoGridSquare square = null;
		ItemContainer itemContainer = this.getOutermostContainer();
		if (itemContainer != null) {
			square = itemContainer.getSourceGrid();
			if (square == null && itemContainer.parent != null) {
				square = itemContainer.parent.square;
			}

			InventoryItem inventoryItem = itemContainer.containingItem;
			if (square == null && inventoryItem != null && inventoryItem.getWorldItem() != null) {
				square = inventoryItem.getWorldItem().getSquare();
			}
		}

		if (square == null && this.getWorldItem() != null && this.getWorldItem().getWorldObjectIndex() != -1) {
			square = this.getWorldItem().square;
		}

		return square;
	}

	public boolean shouldUpdateInWorld() {
		return this.alarmSet;
	}

	public void update() {
		if (this.alarmSet) {
			int int1 = GameTime.instance.getMinutes() / 10 * 10;
			if (!this.isRinging() && this.forceDontRing != int1 && this.alarmHour == GameTime.instance.getHour() && this.alarmMinutes == int1) {
				this.ringSince = GameTime.getInstance().getWorldAgeHours();
			}

			if (this.isRinging()) {
				double double1 = GameTime.getInstance().getWorldAgeHours();
				if (this.ringSince > double1) {
					this.ringSince = double1;
				}

				IsoGridSquare square = this.getAlarmSquare();
				if (square != null && !(this.ringSince + 0.5 < double1)) {
					if (!GameClient.bClient && square != null) {
						WorldSoundManager.instance.addSoundRepeating((Object)null, square.getX(), square.getY(), square.getZ(), this.getSoundRadius(), 3, false);
					}
				} else {
					this.stopRinging();
				}

				if (!GameServer.bServer && this.isRinging()) {
					ItemSoundManager.addItem(this);
				}
			}

			if (this.forceDontRing != int1) {
				this.forceDontRing = -1;
			}
		}
	}

	public void updateSound(BaseSoundEmitter baseSoundEmitter) {
		assert !GameServer.bServer;
		IsoGridSquare square = this.getAlarmSquare();
		if (square != null) {
			baseSoundEmitter.setPos((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z);
			if (!baseSoundEmitter.isPlaying(this.ringSound)) {
				if (this.alarmSound == null || "".equals(this.alarmSound)) {
					this.alarmSound = "AlarmClockLoop";
				}

				this.ringSound = baseSoundEmitter.playSoundImpl(this.alarmSound, square);
			}

			if (GameClient.bClient && sendEvery.Check() && this.isInLocalPlayerInventory()) {
				GameClient.instance.sendWorldSound((Object)null, square.x, square.y, square.z, this.getSoundRadius(), 3, false, 0.0F, 1.0F);
			}

			this.wakeUpPlayers(square);
		}
	}

	private void wakeUpPlayers(IsoGridSquare square) {
		if (!GameServer.bServer) {
			int int1 = this.getSoundRadius();
			int int2 = Math.max(square.getZ() - 3, 0);
			int int3 = Math.min(square.getZ() + 3, 8);
			for (int int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
				IsoPlayer player = IsoPlayer.players[int4];
				if (player != null && !player.isDead() && player.getCurrentSquare() != null && !player.Traits.Deaf.isSet()) {
					IsoGridSquare square2 = player.getCurrentSquare();
					if (square2.z >= int2 && square2.z < int3) {
						float float1 = IsoUtils.DistanceToSquared((float)square.x, (float)square.y, (float)square2.x, (float)square2.y);
						if (player.Traits.HardOfHearing.isSet()) {
							float1 *= 4.5F;
						}

						if (!(float1 > (float)(int1 * int1))) {
							this.wakeUp(player);
						}
					}
				}
			}
		}
	}

	private void wakeUp(IsoPlayer player) {
		if (player.Asleep) {
			SoundManager.instance.setMusicWakeState(player, "WakeNormal");
			SleepingEvent.instance.wakeUp(player);
		}
	}

	public boolean isRinging() {
		return this.ringSince >= 0.0;
	}

	public boolean finishupdate() {
		return !this.alarmSet;
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		ObjectTooltip.LayoutItem layoutItem = layout.addItem();
		layoutItem.setLabel(Translator.getText("IGUI_CurrentTime"), 1.0F, 1.0F, 0.8F, 1.0F);
		int int1 = GameTime.instance.getMinutes() / 10 * 10;
		layoutItem.setValue(GameTime.getInstance().getHour() + ":" + (int1 == 0 ? "00" : int1), 1.0F, 1.0F, 0.8F, 1.0F);
		if (this.alarmSet) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("IGUI_AlarmIsSetFor"), 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValue(this.alarmHour + ":" + (this.alarmMinutes == 0 ? "00" : this.alarmMinutes), 1.0F, 1.0F, 0.8F, 1.0F);
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putInt(this.alarmHour);
		byteBuffer.putInt(this.alarmMinutes);
		byteBuffer.put((byte)(this.alarmSet ? 1 : 0));
		byteBuffer.putFloat((float)this.ringSince);
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.alarmHour = byteBuffer.getInt();
		this.alarmMinutes = byteBuffer.getInt();
		this.alarmSet = byteBuffer.get() == 1;
		this.ringSince = (double)byteBuffer.getFloat();
		this.ringSound = -1L;
	}

	public int getSaveType() {
		return Item.Type.AlarmClock.ordinal();
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "AlarmClock";
	}

	public void setAlarmSet(boolean boolean1) {
		this.stopRinging();
		this.alarmSet = boolean1;
		this.ringSound = -1L;
		if (boolean1) {
			IsoWorld.instance.CurrentCell.addToProcessItems((InventoryItem)this);
			IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
			if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
				IsoCell cell = IsoWorld.instance.getCell();
				if (!cell.getProcessWorldItems().contains(worldInventoryObject)) {
					cell.getProcessWorldItems().add(worldInventoryObject);
				}
			}
		} else {
			IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
		}
	}

	public boolean isAlarmSet() {
		return this.alarmSet;
	}

	public void setHour(int int1) {
		this.alarmHour = int1;
		this.forceDontRing = -1;
	}

	public void setMinute(int int1) {
		this.alarmMinutes = int1;
		this.forceDontRing = -1;
	}

	public int getHour() {
		return this.alarmHour;
	}

	public int getMinute() {
		return this.alarmMinutes;
	}

	public void syncAlarmClock() {
		IsoPlayer player = this.getOwnerPlayer(this.container);
		if (player != null) {
			this.syncAlarmClock_Player(player);
		}

		if (this.worldItem != null) {
			this.syncAlarmClock_World();
		}
	}

	private IsoPlayer getOwnerPlayer(ItemContainer itemContainer) {
		if (itemContainer == null) {
			return null;
		} else {
			IsoObject object = itemContainer.getParent();
			return object instanceof IsoPlayer ? (IsoPlayer)object : null;
		}
	}

	public void syncAlarmClock_Player(IsoPlayer player) {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(PacketPlayer);
			byteBufferWriter.putShort((short)player.getPlayerNum());
			byteBufferWriter.putInt(this.id);
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putInt(this.alarmHour);
			byteBufferWriter.putInt(this.alarmMinutes);
			byteBufferWriter.putByte((byte)(this.alarmSet ? 1 : 0));
			PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
		}
	}

	public void syncAlarmClock_World() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(PacketWorld);
			byteBufferWriter.putInt(this.worldItem.square.getX());
			byteBufferWriter.putInt(this.worldItem.square.getY());
			byteBufferWriter.putInt(this.worldItem.square.getZ());
			byteBufferWriter.putInt(this.id);
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putInt(this.alarmHour);
			byteBufferWriter.putInt(this.alarmMinutes);
			byteBufferWriter.putByte((byte)(this.alarmSet ? 1 : 0));
			PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
		}
	}

	public void syncStopRinging() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
			IsoPlayer player = this.getOwnerPlayer(this.container);
			if (player != null) {
				byteBufferWriter.putShort(PacketPlayer);
				byteBufferWriter.putShort((short)player.getPlayerNum());
			} else if (this.getWorldItem() != null) {
				byteBufferWriter.putShort(PacketWorld);
				byteBufferWriter.putInt(this.worldItem.square.getX());
				byteBufferWriter.putInt(this.worldItem.square.getY());
				byteBufferWriter.putInt(this.worldItem.square.getZ());
			} else {
				assert false;
			}

			byteBufferWriter.putInt(this.id);
			byteBufferWriter.putByte((byte)1);
			PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
		}
	}

	public void stopRinging() {
		if (this.ringSound != -1L) {
			this.ringSound = -1L;
		}

		ItemSoundManager.removeItem(this);
		this.ringSince = -1.0;
		this.forceDontRing = GameTime.instance.getMinutes() / 10 * 10;
	}

	public String getAlarmSound() {
		return this.alarmSound;
	}

	public void setAlarmSound(String string) {
		this.alarmSound = string;
	}

	public int getSoundRadius() {
		return this.soundRadius;
	}

	public void setSoundRadius(int int1) {
		this.soundRadius = int1;
	}

	public boolean isDigital() {
		return this.isDigital;
	}
}
