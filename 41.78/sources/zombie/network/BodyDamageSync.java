package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;


public class BodyDamageSync {
	public static final byte BD_Health = 1;
	public static final byte BD_bandaged = 2;
	public static final byte BD_bitten = 3;
	public static final byte BD_bleeding = 4;
	public static final byte BD_IsBleedingStemmed = 5;
	public static final byte BD_IsCortorised = 6;
	public static final byte BD_scratched = 7;
	public static final byte BD_stitched = 8;
	public static final byte BD_deepWounded = 9;
	public static final byte BD_IsInfected = 10;
	public static final byte BD_IsFakeInfected = 11;
	public static final byte BD_bandageLife = 12;
	public static final byte BD_scratchTime = 13;
	public static final byte BD_biteTime = 14;
	public static final byte BD_alcoholicBandage = 15;
	public static final byte BD_woundInfectionLevel = 16;
	public static final byte BD_infectedWound = 17;
	public static final byte BD_bleedingTime = 18;
	public static final byte BD_deepWoundTime = 19;
	public static final byte BD_haveGlass = 20;
	public static final byte BD_stitchTime = 21;
	public static final byte BD_alcoholLevel = 22;
	public static final byte BD_additionalPain = 23;
	public static final byte BD_bandageType = 24;
	public static final byte BD_getBandageXp = 25;
	public static final byte BD_getStitchXp = 26;
	public static final byte BD_getSplintXp = 27;
	public static final byte BD_fractureTime = 28;
	public static final byte BD_splint = 29;
	public static final byte BD_splintFactor = 30;
	public static final byte BD_haveBullet = 31;
	public static final byte BD_burnTime = 32;
	public static final byte BD_needBurnWash = 33;
	public static final byte BD_lastTimeBurnWash = 34;
	public static final byte BD_splintItem = 35;
	public static final byte BD_plantainFactor = 36;
	public static final byte BD_comfreyFactor = 37;
	public static final byte BD_garlicFactor = 38;
	public static final byte BD_cut = 39;
	public static final byte BD_cutTime = 40;
	public static final byte BD_stiffness = 41;
	public static final byte BD_BodyDamage = 50;
	private static final byte BD_START = 64;
	private static final byte BD_END = 65;
	private static final byte PKT_START_UPDATING = 1;
	private static final byte PKT_STOP_UPDATING = 2;
	private static final byte PKT_UPDATE = 3;
	public static BodyDamageSync instance = new BodyDamageSync();
	private ArrayList updaters = new ArrayList();

	private static void noise(String string) {
		if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
			DebugLog.log("BodyDamage: " + string);
		}
	}

	public void startSendingUpdates(short short1, short short2) {
		if (GameClient.bClient) {
			noise("start sending updates to " + short2);
			BodyDamageSync.Updater updater;
			for (int int1 = 0; int1 < this.updaters.size(); ++int1) {
				updater = (BodyDamageSync.Updater)this.updaters.get(int1);
				if (updater.localIndex == short1 && updater.remoteID == short2) {
					return;
				}
			}

			IsoPlayer player = IsoPlayer.players[short1];
			updater = new BodyDamageSync.Updater();
			updater.localIndex = short1;
			updater.remoteID = short2;
			updater.bdLocal = player.getBodyDamage();
			updater.bdSent = new BodyDamage((IsoGameCharacter)null);
			this.updaters.add(updater);
		}
	}

	public void stopSendingUpdates(short short1, short short2) {
		if (GameClient.bClient) {
			noise("stop sending updates to " + short2);
			for (int int1 = 0; int1 < this.updaters.size(); ++int1) {
				BodyDamageSync.Updater updater = (BodyDamageSync.Updater)this.updaters.get(int1);
				if (updater.localIndex == short1 && updater.remoteID == short2) {
					this.updaters.remove(int1);
					return;
				}
			}
		}
	}

	public void startReceivingUpdates(short short1) {
		if (GameClient.bClient) {
			noise("start receiving updates from " + short1 + " to " + IsoPlayer.players[0].getOnlineID());
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putShort(IsoPlayer.players[0].getOnlineID());
			byteBufferWriter.putShort(short1);
			PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
		}
	}

	public void stopReceivingUpdates(short short1) {
		if (GameClient.bClient) {
			noise("stop receiving updates from " + short1 + " to " + IsoPlayer.players[0].getOnlineID());
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)2);
			byteBufferWriter.putShort(IsoPlayer.players[0].getOnlineID());
			byteBufferWriter.putShort(short1);
			PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
		}
	}

	public void update() {
		if (GameClient.bClient) {
			for (int int1 = 0; int1 < this.updaters.size(); ++int1) {
				BodyDamageSync.Updater updater = (BodyDamageSync.Updater)this.updaters.get(int1);
				updater.update();
			}
		}
	}

	public void serverPacket(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		short short1;
		short short2;
		Long Long1;
		UdpConnection udpConnection;
		ByteBufferWriter byteBufferWriter;
		if (byte1 == 1) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			Long1 = (Long)GameServer.IDToAddressMap.get(short2);
			if (Long1 != null) {
				udpConnection = GameServer.udpEngine.getActiveConnection(Long1);
				if (udpConnection != null) {
					byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)1);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putShort(short2);
					PacketTypes.PacketType.BodyDamageUpdate.send(udpConnection);
				}
			}
		} else if (byte1 == 2) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			Long1 = (Long)GameServer.IDToAddressMap.get(short2);
			if (Long1 != null) {
				udpConnection = GameServer.udpEngine.getActiveConnection(Long1);
				if (udpConnection != null) {
					byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)2);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putShort(short2);
					PacketTypes.PacketType.BodyDamageUpdate.send(udpConnection);
				}
			}
		} else if (byte1 == 3) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			Long1 = (Long)GameServer.IDToAddressMap.get(short2);
			if (Long1 != null) {
				udpConnection = GameServer.udpEngine.getActiveConnection(Long1);
				if (udpConnection != null) {
					byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)3);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.BodyDamageUpdate.send(udpConnection);
				}
			}
		}
	}

	public void clientPacket(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		short short1;
		short short2;
		short short3;
		IsoPlayer player;
		if (byte1 == 1) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			for (short3 = 0; short3 < IsoPlayer.numPlayers; ++short3) {
				player = IsoPlayer.players[short3];
				noise("looking for " + short2 + " testing player ID=" + player.getOnlineID());
				if (player != null && player.isAlive() && player.getOnlineID() == short2) {
					this.startSendingUpdates(short3, short1);
					break;
				}
			}
		} else if (byte1 == 2) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			for (short3 = 0; short3 < IsoPlayer.numPlayers; ++short3) {
				player = IsoPlayer.players[short3];
				if (player != null && player.getOnlineID() == short2) {
					this.stopSendingUpdates(short3, short1);
					break;
				}
			}
		} else if (byte1 == 3) {
			short1 = byteBuffer.getShort();
			short2 = byteBuffer.getShort();
			GameClient gameClient = GameClient.instance;
			IsoPlayer player2 = (IsoPlayer)GameClient.IDToPlayerMap.get(short1);
			if (player2 != null) {
				BodyDamage bodyDamage = player2.getBodyDamageRemote();
				byte byte2 = byteBuffer.get();
				if (byte2 == 50) {
					bodyDamage.setOverallBodyHealth(byteBuffer.getFloat());
					bodyDamage.setRemotePainLevel(byteBuffer.get());
					bodyDamage.IsFakeInfected = byteBuffer.get() == 1;
					bodyDamage.InfectionLevel = byteBuffer.getFloat();
					byte2 = byteBuffer.get();
				}

				while (byte2 == 64) {
					byte byte3 = byteBuffer.get();
					BodyPart bodyPart = (BodyPart)bodyDamage.BodyParts.get(byte3);
					for (byte byte4 = byteBuffer.get(); byte4 != 65; byte4 = byteBuffer.get()) {
						bodyPart.sync(byteBuffer, byte4);
					}

					byte2 = byteBuffer.get();
				}
			}
		}
	}

	public static final class Updater {
		static ByteBuffer bb = ByteBuffer.allocate(1024);
		short localIndex;
		short remoteID;
		BodyDamage bdLocal;
		BodyDamage bdSent;
		boolean partStarted;
		byte partIndex;
		long sendTime;

		void update() {
			long long1 = System.currentTimeMillis();
			if (long1 - this.sendTime >= 500L) {
				this.sendTime = long1;
				bb.clear();
				int int1 = this.bdLocal.getParentChar().getMoodles().getMoodleLevel(MoodleType.Pain);
				if (this.compareFloats(this.bdLocal.getOverallBodyHealth(), (float)((int)this.bdSent.getOverallBodyHealth())) || int1 != this.bdSent.getRemotePainLevel() || this.bdLocal.IsFakeInfected != this.bdSent.IsFakeInfected || this.compareFloats(this.bdLocal.InfectionLevel, this.bdSent.InfectionLevel)) {
					bb.put((byte)50);
					bb.putFloat(this.bdLocal.getOverallBodyHealth());
					bb.put((byte)int1);
					bb.put((byte)(this.bdLocal.IsFakeInfected ? 1 : 0));
					bb.putFloat(this.bdLocal.InfectionLevel);
					this.bdSent.setOverallBodyHealth(this.bdLocal.getOverallBodyHealth());
					this.bdSent.setRemotePainLevel(int1);
					this.bdSent.IsFakeInfected = this.bdLocal.IsFakeInfected;
					this.bdSent.InfectionLevel = this.bdLocal.InfectionLevel;
				}

				for (int int2 = 0; int2 < this.bdLocal.BodyParts.size(); ++int2) {
					this.updatePart(int2);
				}

				if (bb.position() > 0) {
					bb.put((byte)65);
					ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
					PacketTypes.PacketType.BodyDamageUpdate.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)3);
					byteBufferWriter.putShort(IsoPlayer.players[this.localIndex].getOnlineID());
					byteBufferWriter.putShort(this.remoteID);
					byteBufferWriter.bb.put(bb.array(), 0, bb.position());
					PacketTypes.PacketType.BodyDamageUpdate.send(GameClient.connection);
				}
			}
		}

		void updatePart(int int1) {
			BodyPart bodyPart = (BodyPart)this.bdLocal.BodyParts.get(int1);
			BodyPart bodyPart2 = (BodyPart)this.bdSent.BodyParts.get(int1);
			this.partStarted = false;
			this.partIndex = (byte)int1;
			bodyPart.sync(bodyPart2, this);
			if (this.partStarted) {
				bb.put((byte)65);
			}
		}

		public void updateField(byte byte1, boolean boolean1) {
			if (!this.partStarted) {
				bb.put((byte)64);
				bb.put(this.partIndex);
				this.partStarted = true;
			}

			bb.put(byte1);
			bb.put((byte)(boolean1 ? 1 : 0));
		}

		private boolean compareFloats(float float1, float float2) {
			if (Float.compare(float1, 0.0F) != Float.compare(float2, 0.0F)) {
				return true;
			} else {
				return (int)float1 != (int)float2;
			}
		}

		public boolean updateField(byte byte1, float float1, float float2) {
			if (!this.compareFloats(float1, float2)) {
				return false;
			} else {
				if (!this.partStarted) {
					bb.put((byte)64);
					bb.put(this.partIndex);
					this.partStarted = true;
				}

				bb.put(byte1);
				bb.putFloat(float1);
				return true;
			}
		}

		public void updateField(byte byte1, String string) {
			if (!this.partStarted) {
				bb.put((byte)64);
				bb.put(this.partIndex);
				this.partStarted = true;
			}

			bb.put(byte1);
			GameWindow.WriteStringUTF(bb, string);
		}
	}
}
