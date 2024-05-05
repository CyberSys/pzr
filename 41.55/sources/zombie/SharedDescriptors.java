package zombie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;


public final class SharedDescriptors {
	private static final int DESCRIPTOR_COUNT = 500;
	private static final int DESCRIPTOR_ID_START = 500;
	private static final byte[] DESCRIPTOR_MAGIC = new byte[]{68, 69, 83, 67};
	private static final int VERSION_1 = 1;
	private static final int VERSION_2 = 2;
	private static final int VERSION = 2;
	private static SharedDescriptors.Descriptor[] PlayerZombieDescriptors = new SharedDescriptors.Descriptor[10];
	private static final int FIRST_PLAYER_ZOMBIE_DESCRIPTOR_ID = 1000;

	public static void initSharedDescriptors() {
		if (GameServer.bServer) {
			;
		}
	}

	private static void noise(String string) {
		DebugLog.log("shared-descriptor: " + string);
	}

	public static void createPlayerZombieDescriptor(IsoZombie zombie) {
		if (GameServer.bServer) {
			if (zombie.isReanimatedPlayer()) {
				if (zombie.getDescriptor().getID() == 0) {
					int int1 = -1;
					int int2;
					for (int2 = 0; int2 < PlayerZombieDescriptors.length; ++int2) {
						if (PlayerZombieDescriptors[int2] == null) {
							int1 = int2;
							break;
						}
					}

					if (int1 == -1) {
						SharedDescriptors.Descriptor[] descriptorArray = new SharedDescriptors.Descriptor[PlayerZombieDescriptors.length + 10];
						System.arraycopy(PlayerZombieDescriptors, 0, descriptorArray, 0, PlayerZombieDescriptors.length);
						int1 = PlayerZombieDescriptors.length;
						PlayerZombieDescriptors = descriptorArray;
						noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
					}

					zombie.getDescriptor().setID(1000 + int1);
					int2 = PersistentOutfits.instance.pickOutfit("ReanimatedPlayer", zombie.isFemale());
					int2 = int2 & -65536 | int1 + 1;
					zombie.setPersistentOutfitID(int2);
					SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
					descriptor.bFemale = zombie.isFemale();
					descriptor.bZombie = false;
					descriptor.ID = 1000 + int1;
					descriptor.persistentOutfitID = int2;
					descriptor.getHumanVisual().copyFrom(zombie.getHumanVisual());
					ItemVisuals itemVisuals = new ItemVisuals();
					zombie.getItemVisuals(itemVisuals);
					int int3;
					for (int3 = 0; int3 < itemVisuals.size(); ++int3) {
						ItemVisual itemVisual = new ItemVisual((ItemVisual)itemVisuals.get(int3));
						descriptor.itemVisuals.add(itemVisual);
					}

					PlayerZombieDescriptors[int1] = descriptor;
					noise("added id=" + descriptor.getID());
					for (int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
						UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int3);
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						try {
							PacketTypes.doPacket((short)62, byteBufferWriter);
							descriptor.save(byteBufferWriter.bb);
							udpConnection.endPacketImmediate();
						} catch (Exception exception) {
							exception.printStackTrace();
							udpConnection.cancelPacket();
						}
					}
				}
			}
		}
	}

	public static void releasePlayerZombieDescriptor(IsoZombie zombie) {
		if (GameServer.bServer) {
			if (zombie.isReanimatedPlayer()) {
				int int1 = zombie.getDescriptor().getID() - 1000;
				if (int1 >= 0 && int1 < PlayerZombieDescriptors.length) {
					noise("released id=" + zombie.getDescriptor().getID());
					zombie.getDescriptor().setID(0);
					PlayerZombieDescriptors[int1] = null;
				}
			}
		}
	}

	public static SharedDescriptors.Descriptor[] getPlayerZombieDescriptors() {
		return PlayerZombieDescriptors;
	}

	public static void registerPlayerZombieDescriptor(SharedDescriptors.Descriptor descriptor) {
		if (GameClient.bClient) {
			int int1 = descriptor.getID() - 1000;
			if (int1 >= 0 && int1 < 32767) {
				if (PlayerZombieDescriptors.length <= int1) {
					int int2 = (int1 + 10) / 10 * 10;
					SharedDescriptors.Descriptor[] descriptorArray = new SharedDescriptors.Descriptor[int2];
					System.arraycopy(PlayerZombieDescriptors, 0, descriptorArray, 0, PlayerZombieDescriptors.length);
					PlayerZombieDescriptors = descriptorArray;
					noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
				}

				PlayerZombieDescriptors[int1] = descriptor;
				noise("registered id=" + descriptor.getID());
			}
		}
	}

	public static void ApplyReanimatedPlayerOutfit(int int1, String string, IsoGameCharacter gameCharacter) {
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null) {
			short short1 = (short)(int1 & '￿');
			if (short1 >= 1 && short1 <= PlayerZombieDescriptors.length) {
				SharedDescriptors.Descriptor descriptor = PlayerZombieDescriptors[short1 - 1];
				if (descriptor != null) {
					zombie.useDescriptor(descriptor);
				}
			}
		}
	}

	public static final class Descriptor implements IHumanVisual {
		public int ID = 0;
		public int persistentOutfitID = 0;
		public String outfitName;
		public final HumanVisual humanVisual = new HumanVisual(this);
		public final ItemVisuals itemVisuals = new ItemVisuals();
		public boolean bFemale = false;
		public boolean bZombie = false;

		public int getID() {
			return this.ID;
		}

		public int getPersistentOutfitID() {
			return this.persistentOutfitID;
		}

		public HumanVisual getHumanVisual() {
			return this.humanVisual;
		}

		public void getItemVisuals(ItemVisuals itemVisuals) {
			itemVisuals.clear();
			itemVisuals.addAll(this.itemVisuals);
		}

		public boolean isFemale() {
			return this.bFemale;
		}

		public boolean isZombie() {
			return this.bZombie;
		}

		public boolean isSkeleton() {
			return false;
		}

		public void save(ByteBuffer byteBuffer) throws IOException {
			byte byte1 = 0;
			if (this.bFemale) {
				byte1 = (byte)(byte1 | 1);
			}

			if (this.bZombie) {
				byte1 = (byte)(byte1 | 2);
			}

			byteBuffer.put(byte1);
			byteBuffer.putInt(this.ID);
			byteBuffer.putInt(this.persistentOutfitID);
			GameWindow.WriteStringUTF(byteBuffer, this.outfitName);
			this.humanVisual.save(byteBuffer);
			this.itemVisuals.save(byteBuffer);
		}

		public void load(ByteBuffer byteBuffer, int int1) throws IOException {
			this.humanVisual.clear();
			this.itemVisuals.clear();
			byte byte1 = byteBuffer.get();
			this.bFemale = (byte1 & 1) != 0;
			this.bZombie = (byte1 & 2) != 0;
			this.ID = byteBuffer.getInt();
			this.persistentOutfitID = byteBuffer.getInt();
			this.outfitName = GameWindow.ReadStringUTF(byteBuffer);
			this.humanVisual.load(byteBuffer, int1);
			short short1 = byteBuffer.getShort();
			for (int int2 = 0; int2 < short1; ++int2) {
				ItemVisual itemVisual = new ItemVisual();
				itemVisual.load(byteBuffer, int1);
				this.itemVisuals.add(itemVisual);
			}
		}
	}

	private static final class DescriptorList extends ArrayList {
	}
}
