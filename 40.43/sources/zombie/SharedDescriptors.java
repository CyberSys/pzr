package zombie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


public class SharedDescriptors {
	private static SharedDescriptors.Descriptor[] Descriptors;
	private static final int DESCRIPTOR_COUNT = 500;
	private static final int DESCRIPTOR_ID_START = 500;
	private static byte[] DESCRIPTOR_MAGIC = new byte[]{68, 69, 83, 67};
	private static SharedDescriptors.Descriptor[] PlayerZombieDescriptors = new SharedDescriptors.Descriptor[10];
	private static final int FIRST_PLAYER_ZOMBIE_DESCRIPTOR_ID = 1000;

	public static void initSharedDescriptors() {
		if (GameServer.bServer) {
			Descriptors = new SharedDescriptors.Descriptor[500];
			for (int int1 = 0; int1 < Descriptors.length; ++int1) {
				Descriptors[int1] = new SharedDescriptors.Descriptor();
				Descriptors[int1].desc = SurvivorFactory.CreateSurvivor();
				Descriptors[int1].desc.setID(500 + int1);
				Descriptors[int1].palette = Rand.Next(3) + 1;
			}

			if (!loadSharedDescriptors()) {
				saveSharedDescriptors();
			}
		}
	}

	private static boolean loadSharedDescriptors() {
		if (!GameServer.bServer) {
			return false;
		} else {
			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
			if (!file.exists()) {
				return false;
			} else {
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);
				} catch (Exception exception) {
					exception.printStackTrace();
					return false;
				}

				boolean boolean1 = false;
				try {
					if (SliceY.SliceBuffer == null) {
						SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
					}

					synchronized (SliceY.SliceBuffer) {
						SliceY.SliceBuffer.clear();
						fileInputStream.read(SliceY.SliceBuffer.array());
						ByteBuffer byteBuffer = SliceY.SliceBuffer;
						byte[] byteArray = new byte[4];
						byteBuffer.get(byteArray);
						if (!Arrays.equals(byteArray, DESCRIPTOR_MAGIC)) {
							throw new IOException("not magic");
						}

						int int1 = byteBuffer.getInt();
						short short1 = byteBuffer.getShort();
						int int2 = Math.min(short1, 500);
						for (int int3 = 0; int3 < int2; ++int3) {
							Descriptors[int3].desc.loadCompact(byteBuffer);
							Descriptors[int3].desc.setID(500 + int3);
							Descriptors[int3].palette = byteBuffer.get() & 255;
						}

						boolean1 = true;
					}
				} catch (Exception exception2) {
					exception2.printStackTrace();
					boolean1 = false;
				} finally {
					try {
						fileInputStream.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
						boolean1 = false;
					}
				}

				return boolean1;
			}
		}
	}

	private static void saveSharedDescriptors() {
		if (GameServer.bServer) {
			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
			FileOutputStream fileOutputStream = null;
			try {
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				synchronized (SliceY.SliceBuffer) {
					SliceY.SliceBuffer.rewind();
					ByteBuffer byteBuffer = SliceY.SliceBuffer;
					byteBuffer.put(DESCRIPTOR_MAGIC);
					byteBuffer.putInt(1);
					byteBuffer.putShort((short)Descriptors.length);
					SharedDescriptors.Descriptor[] descriptorArray = Descriptors;
					int int1 = descriptorArray.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						SharedDescriptors.Descriptor descriptor = descriptorArray[int2];
						descriptor.desc.saveCompact(byteBuffer);
						byteBuffer.put((byte)descriptor.palette);
					}

					fileOutputStream = new FileOutputStream(file);
					fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
					fileOutputStream.flush();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				fileOutputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public static void setSharedDescriptors(SharedDescriptors.Descriptor[] descriptorArray) {
		for (int int1 = 0; int1 < descriptorArray.length; ++int1) {
			descriptorArray[int1].desc.setID(500 + int1);
		}

		Descriptors = descriptorArray;
	}

	public static SharedDescriptors.Descriptor[] getSharedDescriptors() {
		return Descriptors;
	}

	public static SharedDescriptors.Descriptor getDescriptor(int int1) {
		if (int1 >= 500 && int1 < 500 + Descriptors.length) {
			return Descriptors[int1 - 500];
		} else {
			return int1 >= 1000 && int1 < 1000 + PlayerZombieDescriptors.length ? PlayerZombieDescriptors[int1 - 1000] : null;
		}
	}

	public static SharedDescriptors.Descriptor pickRandomDescriptor() {
		if (Descriptors != null && Descriptors.length != 0) {
			int int1 = Rand.Next(Descriptors.length);
			return Descriptors[int1];
		} else {
			return null;
		}
	}

	public static int pickRandomDescriptorID() {
		return Descriptors != null && Descriptors.length != 0 ? 500 + Rand.Next(Descriptors.length) : 0;
	}

	private static void noise(String string) {
		DebugLog.log("shared-descriptor: " + string);
	}

	public static void createPlayerZombieDescriptor(IsoZombie zombie) {
		if (GameServer.bServer) {
			if (zombie.isReanimatedPlayer()) {
				if (zombie.getDescriptor().getID() == 0) {
					int int1 = -1;
					for (int int2 = 0; int2 < PlayerZombieDescriptors.length; ++int2) {
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
					SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
					descriptor.desc = zombie.getDescriptor();
					descriptor.palette = zombie.palette;
					PlayerZombieDescriptors[int1] = descriptor;
					noise("added id=" + descriptor.desc.getID());
					try {
						for (int int3 = 0; int3 < GameServer.udpEngine.connections.size(); ++int3) {
							UdpConnection udpConnection = (UdpConnection)GameServer.udpEngine.connections.get(int3);
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.doPacket((short)62, byteBufferWriter);
							byteBufferWriter.putShort((short)descriptor.desc.getID());
							descriptor.desc.saveCompact(byteBufferWriter.bb);
							byteBufferWriter.putByte((byte)descriptor.palette);
							udpConnection.endPacketImmediate();
						}
					} catch (IOException ioException) {
						ioException.printStackTrace();
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

	public static void registerPlayerZombieDescriptor(SurvivorDesc survivorDesc, int int1) {
		if (GameClient.bClient) {
			int int2 = survivorDesc.getID() - 1000;
			if (int2 >= 0 && int2 < 32767) {
				if (PlayerZombieDescriptors.length <= int2) {
					int int3 = (int2 + 10) / 10 * 10;
					SharedDescriptors.Descriptor[] descriptorArray = new SharedDescriptors.Descriptor[int3];
					System.arraycopy(PlayerZombieDescriptors, 0, descriptorArray, 0, PlayerZombieDescriptors.length);
					PlayerZombieDescriptors = descriptorArray;
					noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
				}

				SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
				descriptor.desc = survivorDesc;
				descriptor.palette = int1;
				PlayerZombieDescriptors[int2] = descriptor;
				noise("registered id=" + survivorDesc.getID());
			}
		}
	}

	public static class Descriptor {
		public SurvivorDesc desc;
		public int palette;

		public SurvivorDesc getDesc() {
			return this.desc;
		}

		public int getPalette() {
			return this.palette;
		}
	}
}
