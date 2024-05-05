package zombie.network;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.codec.binary.Hex;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.scripting.ScriptManager;


public final class NetChecksum {
	public static final NetChecksum.Checksummer checksummer = new NetChecksum.Checksummer();
	public static final NetChecksum.Comparer comparer = new NetChecksum.Comparer();

	private static void noise(String string) {
		if (!Core.bDebug) {
		}

		DebugLog.log("NetChecksum: " + string);
	}

	public static final class Checksummer {
		private MessageDigest md;
		private final byte[] fileBytes = new byte[1024];
		private final byte[] convertBytes = new byte[1024];
		private boolean convertLineEndings;

		public void reset(boolean boolean1) throws NoSuchAlgorithmException {
			if (this.md == null) {
				this.md = MessageDigest.getInstance("MD5");
			}

			this.convertLineEndings = boolean1;
			this.md.reset();
		}

		public void addFile(String string, String string2) throws NoSuchAlgorithmException {
			if (this.md == null) {
				this.md = MessageDigest.getInstance("MD5");
			}

			try {
				FileInputStream fileInputStream = new FileInputStream(string2);
				try {
					NetChecksum.GroupOfFiles.addFile(string, string2);
					while (true) {
						int int1;
						while ((int1 = fileInputStream.read(this.fileBytes)) != -1) {
							if (this.convertLineEndings) {
								boolean boolean1 = false;
								int int2 = 0;
								for (int int3 = 0; int3 < int1 - 1; ++int3) {
									if (this.fileBytes[int3] == 13 && this.fileBytes[int3 + 1] == 10) {
										this.convertBytes[int2++] = 10;
										boolean1 = true;
									} else {
										boolean1 = false;
										this.convertBytes[int2++] = this.fileBytes[int3];
									}
								}

								if (!boolean1) {
									this.convertBytes[int2++] = this.fileBytes[int1 - 1];
								}

								this.md.update(this.convertBytes, 0, int2);
								NetChecksum.GroupOfFiles.updateFile(this.convertBytes, int2);
							} else {
								this.md.update(this.fileBytes, 0, int1);
								NetChecksum.GroupOfFiles.updateFile(this.fileBytes, int1);
							}
						}

						NetChecksum.GroupOfFiles.endFile();
						break;
					}
				} catch (Throwable throwable) {
					try {
						fileInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				fileInputStream.close();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		public String checksumToString() {
			byte[] byteArray = this.md.digest();
			StringBuilder stringBuilder = new StringBuilder();
			for (int int1 = 0; int1 < byteArray.length; ++int1) {
				stringBuilder.append(Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1));
			}

			return stringBuilder.toString();
		}

		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			Iterator iterator = NetChecksum.GroupOfFiles.groups.iterator();
			while (iterator.hasNext()) {
				NetChecksum.GroupOfFiles groupOfFiles = (NetChecksum.GroupOfFiles)iterator.next();
				String string = groupOfFiles.toString();
				stringBuilder.append("\n").append(string);
				if (GameClient.bClient) {
					NetChecksum.comparer.sendError(GameClient.connection, string);
				}
			}

			return stringBuilder.toString();
		}
	}

	public static final class Comparer {
		private static final short PacketTotalChecksum = 1;
		private static final short PacketGroupChecksum = 2;
		private static final short PacketFileChecksums = 3;
		private static final short PacketError = 4;
		private static final byte FileDifferent = 1;
		private static final byte FileNotOnServer = 2;
		private static final byte FileNotOnClient = 3;
		private static final short NUM_GROUPS_TO_SEND = 10;
		private NetChecksum.Comparer.State state;
		private short currentIndex;
		private String error;
		private final byte[] checksum;

		public Comparer() {
			this.state = NetChecksum.Comparer.State.Init;
			this.checksum = new byte[64];
		}

		public void beginCompare() {
			this.error = null;
			this.sendTotalChecksum();
		}

		private void sendTotalChecksum() {
			if (GameClient.bClient) {
				NetChecksum.noise("send total checksum");
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)1);
				byteBufferWriter.putUTF(GameClient.checksum);
				byteBufferWriter.putUTF(ScriptManager.instance.getChecksum());
				PacketTypes.PacketType.Checksum.send(GameClient.connection);
				this.state = NetChecksum.Comparer.State.SentTotalChecksum;
			}
		}

		private void sendGroupChecksum() {
			if (GameClient.bClient) {
				if (this.currentIndex >= NetChecksum.GroupOfFiles.groups.size()) {
					this.state = NetChecksum.Comparer.State.Success;
				} else {
					short short1 = (short)Math.min(this.currentIndex + 10 - 1, NetChecksum.GroupOfFiles.groups.size() - 1);
					NetChecksum.noise("send group checksums " + this.currentIndex + "-" + short1);
					ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
					PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
					byteBufferWriter.putShort((short)2);
					byteBufferWriter.putShort(this.currentIndex);
					byteBufferWriter.putShort(short1);
					for (short short2 = this.currentIndex; short2 <= short1; ++short2) {
						NetChecksum.GroupOfFiles groupOfFiles = (NetChecksum.GroupOfFiles)NetChecksum.GroupOfFiles.groups.get(short2);
						byteBufferWriter.putShort((short)groupOfFiles.totalChecksum.length);
						byteBufferWriter.bb.put(groupOfFiles.totalChecksum);
					}

					PacketTypes.PacketType.Checksum.send(GameClient.connection);
					this.state = NetChecksum.Comparer.State.SentGroupChecksum;
				}
			}
		}

		private void sendFileChecksums() {
			if (GameClient.bClient) {
				NetChecksum.noise("send file checksums " + this.currentIndex);
				NetChecksum.GroupOfFiles groupOfFiles = (NetChecksum.GroupOfFiles)NetChecksum.GroupOfFiles.groups.get(this.currentIndex);
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)3);
				byteBufferWriter.bb.putShort(this.currentIndex);
				byteBufferWriter.putShort(groupOfFiles.fileCount);
				for (int int1 = 0; int1 < groupOfFiles.fileCount; ++int1) {
					byteBufferWriter.putUTF(groupOfFiles.relPaths[int1]);
					byteBufferWriter.putByte((byte)groupOfFiles.checksums[int1].length);
					byteBufferWriter.bb.put(groupOfFiles.checksums[int1]);
				}

				PacketTypes.PacketType.Checksum.send(GameClient.connection);
				this.state = NetChecksum.Comparer.State.SentFileChecksums;
			}
		}

		public String getReason(byte byte1) {
			String string;
			switch (byte1) {
			case 1: 
				string = "File doesn\'t match the one on the server";
				break;
			
			case 2: 
				string = "File doesn\'t exist on the server";
				break;
			
			case 3: 
				string = "File doesn\'t exist on the client";
				break;
			
			default: 
				string = "File status unknown";
			
			}
			return string;
		}

		public void clientPacket(ByteBuffer byteBuffer) {
			if (GameClient.bClient) {
				short short1 = byteBuffer.getShort();
				short short2;
				boolean boolean1;
				switch (short1) {
				case 1: 
					if (this.state != NetChecksum.Comparer.State.SentTotalChecksum) {
						this.error = "NetChecksum: received PacketTotalChecksum in state " + this.state;
						this.state = NetChecksum.Comparer.State.Failed;
					} else {
						boolean boolean2 = byteBuffer.get() == 1;
						boolean1 = byteBuffer.get() == 1;
						NetChecksum.noise("total checksum lua=" + boolean2 + " script=" + boolean1);
						if (boolean2 && boolean1) {
							this.state = NetChecksum.Comparer.State.Success;
						} else {
							this.currentIndex = 0;
							this.sendGroupChecksum();
						}
					}

					break;
				
				case 2: 
					if (this.state != NetChecksum.Comparer.State.SentGroupChecksum) {
						this.error = "NetChecksum: received PacketGroupChecksum in state " + this.state;
						this.state = NetChecksum.Comparer.State.Failed;
					} else {
						short2 = byteBuffer.getShort();
						boolean1 = byteBuffer.get() == 1;
						if (short2 >= this.currentIndex && short2 < this.currentIndex + 10) {
							NetChecksum.noise("group checksum " + short2 + " match=" + boolean1);
							if (boolean1) {
								this.currentIndex = (short)(this.currentIndex + 10);
								this.sendGroupChecksum();
							} else {
								this.currentIndex = short2;
								this.sendFileChecksums();
							}
						} else {
							this.error = "NetChecksum: expected PacketGroupChecksum " + this.currentIndex + " but got " + short2;
							this.state = NetChecksum.Comparer.State.Failed;
						}
					}

					break;
				
				case 3: 
					if (this.state != NetChecksum.Comparer.State.SentFileChecksums) {
						this.error = "NetChecksum: received PacketFileChecksums in state " + this.state;
						this.state = NetChecksum.Comparer.State.Failed;
					} else {
						short2 = byteBuffer.getShort();
						String string = GameWindow.ReadStringUTF(byteBuffer);
						String string2 = GameWindow.ReadStringUTF(byteBuffer);
						byte byte1 = byteBuffer.get();
						if (short2 != this.currentIndex) {
							this.error = "NetChecksum: expected PacketFileChecksums " + this.currentIndex + " but got " + short2;
							this.state = NetChecksum.Comparer.State.Failed;
						} else {
							this.error = this.getReason(byte1);
							if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
								LoggerManager.getLogger("checksum").write(String.format("%s%s", this.error, NetChecksum.checksummer));
							}

							this.error = this.error + ":\n" + string;
							String string3 = ZomboidFileSystem.instance.getString(string);
							if (!string3.equals(string)) {
								this.error = this.error + "\nclient: " + string3;
							}

							if (!string2.equals(string)) {
								this.error = this.error + "\nserver: " + string2;
							}

							this.state = NetChecksum.Comparer.State.Failed;
						}
					}

					break;
				
				case 4: 
					this.error = GameWindow.ReadStringUTF(byteBuffer);
					this.state = NetChecksum.Comparer.State.Failed;
					break;
				
				default: 
					this.error = "NetChecksum: unhandled packet " + short1;
					this.state = NetChecksum.Comparer.State.Failed;
				
				}
			}
		}

		private boolean checksumEquals(byte[] byteArray) {
			if (byteArray == null) {
				return false;
			} else if (this.checksum.length < byteArray.length) {
				return false;
			} else {
				for (int int1 = 0; int1 < byteArray.length; ++int1) {
					if (this.checksum[int1] != byteArray[int1]) {
						return false;
					}
				}

				return true;
			}
		}

		private void sendFileMismatch(UdpConnection udpConnection, short short1, String string, byte byte1) {
			if (GameServer.bServer) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)3);
				byteBufferWriter.putShort(short1);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(ZomboidFileSystem.instance.getString(string));
				byteBufferWriter.putByte(byte1);
				PacketTypes.PacketType.Checksum.send(udpConnection);
				if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
					LoggerManager.getLogger("checksum").write(String.format("%s%s", this.getReason(byte1), NetChecksum.checksummer));
					LoggerManager.getLogger("checksum-" + udpConnection.idStr).write(this.getReason(byte1));
				}
			}
		}

		private void sendError(UdpConnection udpConnection, String string) {
			NetChecksum.noise(string);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
			byteBufferWriter.putShort((short)4);
			byteBufferWriter.putUTF(string);
			PacketTypes.PacketType.Checksum.send(udpConnection);
		}

		public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
			if (GameServer.bServer) {
				short short1 = byteBuffer.getShort();
				String string;
				short short2;
				short short3;
				String string2;
				short short4;
				ByteBufferWriter byteBufferWriter;
				switch (short1) {
				case 1: 
					string = GameWindow.ReadString(byteBuffer);
					String string3 = GameWindow.ReadString(byteBuffer);
					boolean boolean1 = string.equals(GameServer.checksum);
					boolean boolean2 = string3.equals(ScriptManager.instance.getChecksum());
					NetChecksum.noise("PacketTotalChecksum lua=" + boolean1 + " script=" + boolean2);
					if (udpConnection.accessLevel == 32) {
						boolean2 = true;
						boolean1 = true;
					}

					udpConnection.checksumState = boolean1 && boolean2 ? UdpConnection.ChecksumState.Done : UdpConnection.ChecksumState.Different;
					udpConnection.checksumTime = System.currentTimeMillis();
					if (!boolean1 || !boolean2) {
						DebugLog.log("user " + udpConnection.username + " will be kicked because Lua/script checksums do not match");
						string2 = "";
						if (!boolean1) {
							string2 = string2 + "Lua";
						}

						if (!boolean2) {
							string2 = string2 + "Script";
						}

						ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.LuaChecksum, string2, this.getClass().getSimpleName(), 1);
					}

					byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
					byteBufferWriter.putShort((short)1);
					byteBufferWriter.putBoolean(boolean1);
					byteBufferWriter.putBoolean(boolean2);
					PacketTypes.PacketType.Checksum.send(udpConnection);
					break;
				
				case 2: 
					short4 = byteBuffer.getShort();
					short2 = byteBuffer.getShort();
					if (short4 >= 0 && short2 >= short4 && short2 < short4 + 10) {
						short short5 = short4;
						while (true) {
							if (short5 <= short2) {
								short3 = byteBuffer.getShort();
								if (short3 < 0 || short3 > this.checksum.length) {
									this.sendError(udpConnection, "PacketGroupChecksum: numBytes is invalid");
									return;
								}

								byteBuffer.get(this.checksum, 0, short3);
								if (short5 < NetChecksum.GroupOfFiles.groups.size()) {
									NetChecksum.GroupOfFiles groupOfFiles = (NetChecksum.GroupOfFiles)NetChecksum.GroupOfFiles.groups.get(short5);
									if (this.checksumEquals(groupOfFiles.totalChecksum)) {
										++short5;
										continue;
									}
								}

								byteBufferWriter = udpConnection.startPacket();
								PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
								byteBufferWriter.putShort((short)2);
								byteBufferWriter.putShort(short5);
								byteBufferWriter.putBoolean(false);
								PacketTypes.PacketType.Checksum.send(udpConnection);
								return;
							}

							ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
							PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter2);
							byteBufferWriter2.putShort((short)2);
							byteBufferWriter2.putShort(short4);
							byteBufferWriter2.putBoolean(true);
							PacketTypes.PacketType.Checksum.send(udpConnection);
							return;
						}
					} else {
						this.sendError(udpConnection, "PacketGroupChecksum: firstIndex and/or lastIndex are invalid");
						break;
					}

				
				case 3: 
					short4 = byteBuffer.getShort();
					short2 = byteBuffer.getShort();
					if (short4 < 0 || short2 <= 0 || short2 > 20) {
						this.sendError(udpConnection, "PacketFileChecksums: groupIndex and/or fileCount are invalid");
						return;
					}

					if (short4 >= NetChecksum.GroupOfFiles.groups.size()) {
						String string4 = GameWindow.ReadStringUTF(byteBuffer);
						this.sendFileMismatch(udpConnection, short4, string4, (byte)2);
						return;
					}

					NetChecksum.GroupOfFiles groupOfFiles2 = (NetChecksum.GroupOfFiles)NetChecksum.GroupOfFiles.groups.get(short4);
					for (short3 = 0; short3 < short2; ++short3) {
						string2 = GameWindow.ReadStringUTF(byteBuffer);
						byte byte1 = byteBuffer.get();
						if (byte1 < 0 || byte1 > this.checksum.length) {
							this.sendError(udpConnection, "PacketFileChecksums: numBytes is invalid");
							return;
						}

						if (short3 >= groupOfFiles2.fileCount) {
							this.sendFileMismatch(udpConnection, short4, string2, (byte)2);
							return;
						}

						if (!string2.equals(groupOfFiles2.relPaths[short3])) {
							String string5 = ZomboidFileSystem.instance.getString(string2);
							if (string5.equals(string2)) {
								this.sendFileMismatch(udpConnection, short4, string2, (byte)2);
								return;
							}

							this.sendFileMismatch(udpConnection, short4, groupOfFiles2.relPaths[short3], (byte)3);
							return;
						}

						if (byte1 > groupOfFiles2.checksums[short3].length) {
							this.sendFileMismatch(udpConnection, short4, groupOfFiles2.relPaths[short3], (byte)1);
							return;
						}

						byteBuffer.get(this.checksum, 0, byte1);
						if (!this.checksumEquals(groupOfFiles2.checksums[short3])) {
							this.sendFileMismatch(udpConnection, short4, groupOfFiles2.relPaths[short3], (byte)1);
							return;
						}
					}

					if (groupOfFiles2.fileCount > short2) {
						this.sendFileMismatch(udpConnection, short4, groupOfFiles2.relPaths[short2], (byte)3);
						return;
					}

					this.sendError(udpConnection, "PacketFileChecksums: all checks passed when they shouldn\'t");
					break;
				
				case 4: 
					string = GameWindow.ReadStringUTF(byteBuffer);
					if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
						LoggerManager.getLogger("checksum-" + udpConnection.idStr).write(string, (String)null, true);
					}

					break;
				
				default: 
					this.sendError(udpConnection, "Unknown packet " + short1);
				
				}
			}
		}

		private void gc() {
			NetChecksum.GroupOfFiles.gc();
		}

		public void update() {
			switch (this.state) {
			case Init: 
			
			case SentTotalChecksum: 
			
			case SentGroupChecksum: 
			
			case SentFileChecksums: 
			
			default: 
				break;
			
			case Success: 
				this.gc();
				GameClient.checksumValid = true;
				break;
			
			case Failed: 
				this.gc();
				GameClient.connection.forceDisconnect("checksum-" + this.error);
				GameWindow.bServerDisconnected = true;
				GameWindow.kickReason = this.error;
			
			}
		}

		private static enum State {

			Init,
			SentTotalChecksum,
			SentGroupChecksum,
			SentFileChecksums,
			Success,
			Failed;

			private static NetChecksum.Comparer.State[] $values() {
				return new NetChecksum.Comparer.State[]{Init, SentTotalChecksum, SentGroupChecksum, SentFileChecksums, Success, Failed};
			}
		}
	}

	public static final class GroupOfFiles {
		static final int MAX_FILES = 20;
		static MessageDigest mdTotal;
		static MessageDigest mdCurrentFile;
		static final ArrayList groups = new ArrayList();
		static NetChecksum.GroupOfFiles currentGroup;
		byte[] totalChecksum;
		short fileCount;
		final String[] relPaths = new String[20];
		final String[] absPaths = new String[20];
		final byte[][] checksums = new byte[20][];

		private GroupOfFiles() throws NoSuchAlgorithmException {
			if (mdTotal == null) {
				mdTotal = MessageDigest.getInstance("MD5");
				mdCurrentFile = MessageDigest.getInstance("MD5");
			}

			mdTotal.reset();
			groups.add(this);
		}

		public String toString() {
			StringBuilder stringBuilder = (new StringBuilder()).append(this.fileCount).append(" files, ").append(this.absPaths.length).append("/").append(this.relPaths.length).append("/").append(this.checksums.length).append(" \"").append(Hex.encodeHexString(this.totalChecksum)).append("\"");
			for (int int1 = 0; int1 < 20; ++int1) {
				stringBuilder.append("\n");
				if (int1 < this.relPaths.length) {
					stringBuilder.append(" \"").append(this.relPaths[int1]).append("\"");
				}

				if (int1 < this.checksums.length) {
					if (this.checksums[int1] == null) {
						stringBuilder.append(" \"\"");
					} else {
						stringBuilder.append(" \"").append(Hex.encodeHexString(this.checksums[int1])).append("\"");
					}
				}

				if (int1 < this.absPaths.length) {
					stringBuilder.append(" \"").append(this.absPaths[int1]).append("\"");
				}
			}

			return stringBuilder.toString();
		}

		private void gc_() {
			Arrays.fill(this.relPaths, (Object)null);
			Arrays.fill(this.absPaths, (Object)null);
			Arrays.fill(this.checksums, (Object)null);
		}

		public static void initChecksum() {
			groups.clear();
			currentGroup = null;
		}

		public static void finishChecksum() {
			if (currentGroup != null) {
				currentGroup.totalChecksum = mdTotal.digest();
				currentGroup = null;
			}
		}

		private static void addFile(String string, String string2) throws NoSuchAlgorithmException {
			if (currentGroup == null) {
				currentGroup = new NetChecksum.GroupOfFiles();
			}

			currentGroup.relPaths[currentGroup.fileCount] = string;
			currentGroup.absPaths[currentGroup.fileCount] = string2;
			mdCurrentFile.reset();
		}

		private static void updateFile(byte[] byteArray, int int1) {
			mdCurrentFile.update(byteArray, 0, int1);
			mdTotal.update(byteArray, 0, int1);
		}

		private static void endFile() {
			currentGroup.checksums[currentGroup.fileCount] = mdCurrentFile.digest();
			++currentGroup.fileCount;
			if (currentGroup.fileCount >= 20) {
				currentGroup.totalChecksum = mdTotal.digest();
				currentGroup = null;
			}
		}

		public static void gc() {
			Iterator iterator = groups.iterator();
			while (iterator.hasNext()) {
				NetChecksum.GroupOfFiles groupOfFiles = (NetChecksum.GroupOfFiles)iterator.next();
				groupOfFiles.gc_();
			}

			groups.clear();
		}
	}
}
