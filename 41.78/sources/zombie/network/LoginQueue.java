package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.gameStates.LoadingQueueState;


public class LoginQueue {
	private static ArrayList LoginQueue = new ArrayList();
	private static ArrayList PreferredLoginQueue = new ArrayList();
	private static UdpConnection currentLoginQueue;
	private static UpdateLimit UpdateLimit = new UpdateLimit(3050L);
	private static UpdateLimit LoginQueueTimeout = new UpdateLimit(15000L);

	public static void receiveClientLoginQueueRequest(ByteBuffer byteBuffer, short short1) {
		byte byte1 = byteBuffer.get();
		if (byte1 == LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal()) {
			LoadingQueueState.onConnectionImmediate();
		} else if (byte1 == LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal()) {
			int int1 = byteBuffer.getInt();
			LoadingQueueState.onPlaceInQueue(int1);
			LuaEventManager.triggerEvent("OnConnectionStateChanged", "FormatMessage", "PlaceInQueue", int1);
		}

		ConnectionManager.log("receive-packet", "login-queue-request", (UdpConnection)null);
	}

	public static void receiveLoginQueueDone(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		long long1 = byteBuffer.getLong();
		LoggerManager.getLogger("user").write("player " + udpConnection.username + " loading time was: " + long1 + " ms");
		synchronized (LoginQueue) {
			if (currentLoginQueue == udpConnection) {
				currentLoginQueue = null;
			}

			loadNextPlayer();
		}
		ConnectionManager.log("receive-packet", "login-queue-done", udpConnection);
		udpConnection.validator.sendChecksum(true, false, false);
	}

	public static void receiveServerLoginQueueRequest(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + udpConnection.username + "\" attempting to join used " + (udpConnection.preferredInQueue ? "preferred " : "") + "queue");
		synchronized (LoginQueue) {
			if (!ServerOptions.getInstance().LoginQueueEnabled.getValue() || !udpConnection.preferredInQueue && currentLoginQueue == null && PreferredLoginQueue.isEmpty() && LoginQueue.isEmpty() || udpConnection.preferredInQueue && currentLoginQueue == null && PreferredLoginQueue.isEmpty()) {
				if (Core.bDebug) {
					DebugLog.log("receiveServerLoginQueueRequest: ConnectionImmediate (ip:" + udpConnection.ip + ")");
				}

				currentLoginQueue = udpConnection;
				currentLoginQueue.wasInLoadingQueue = true;
				LoginQueueTimeout.Reset((long)(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000));
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal());
				PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
			} else {
				if (Core.bDebug) {
					DebugLog.log("receiveServerLoginQueueRequest: PlaceInQueue (ip:" + udpConnection.ip + " preferredInQueue:" + udpConnection.preferredInQueue + ")");
				}

				if (udpConnection.preferredInQueue) {
					if (!PreferredLoginQueue.contains(udpConnection)) {
						PreferredLoginQueue.add(udpConnection);
					}
				} else if (!LoginQueue.contains(udpConnection)) {
					LoginQueue.add(udpConnection);
				}

				sendPlaceInTheQueue();
			}
		}
		ConnectionManager.log("receive-packet", "login-queue-request", udpConnection);
	}

	private static void sendAccessDenied(UdpConnection udpConnection, String string) {
		if (Core.bDebug) {
			DebugLog.log("sendAccessDenied: (ip:" + udpConnection.ip + " message:" + string + ")");
		}

		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.AccessDenied.send(udpConnection);
		ConnectionManager.log("access-denied", "invalid-queue", udpConnection);
		udpConnection.forceDisconnect("queue-" + string);
	}

	private static void sendPlaceInTheQueue() {
		Iterator iterator = PreferredLoginQueue.iterator();
		UdpConnection udpConnection;
		ByteBufferWriter byteBufferWriter;
		while (iterator.hasNext()) {
			udpConnection = (UdpConnection)iterator.next();
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal());
			byteBufferWriter.putInt(PreferredLoginQueue.indexOf(udpConnection) + 1);
			PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
		}

		iterator = LoginQueue.iterator();
		while (iterator.hasNext()) {
			udpConnection = (UdpConnection)iterator.next();
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.PlaceInQueue.ordinal());
			byteBufferWriter.putInt(PreferredLoginQueue.size() + LoginQueue.indexOf(udpConnection) + 1);
			PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
		}
	}

	private static void sendConnectRequest(UdpConnection udpConnection) {
		if (Core.bDebug) {
			DebugLog.log("sendApplyRequest: (ip:" + udpConnection.ip + ")");
		}

		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.LoginQueueRequest2.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)LoginQueue.LoginQueueMessageType.ConnectionImmediate.ordinal());
		PacketTypes.PacketType.LoginQueueRequest2.send(udpConnection);
		ConnectionManager.log("send-packet", "login-queue-request", udpConnection);
	}

	public static boolean receiveLogin(UdpConnection udpConnection) {
		if (!ServerOptions.getInstance().LoginQueueEnabled.getValue()) {
			return true;
		} else {
			if (Core.bDebug) {
				DebugLog.log("receiveLogin: (ip:" + udpConnection.ip + ")");
			}

			if (udpConnection != currentLoginQueue) {
				sendAccessDenied(currentLoginQueue, "QueueNotFound");
				if (Core.bDebug) {
					DebugLog.log("receiveLogin: error");
				}

				return false;
			} else {
				if (Core.bDebug) {
					DebugLog.log("receiveLogin: ok");
				}

				return true;
			}
		}
	}

	public static void disconnect(UdpConnection udpConnection) {
		if (Core.bDebug) {
			DebugLog.log("disconnect: (ip:" + udpConnection.ip + ")");
		}

		synchronized (LoginQueue) {
			if (udpConnection == currentLoginQueue) {
				currentLoginQueue = null;
			} else {
				if (LoginQueue.contains(udpConnection)) {
					LoginQueue.remove(udpConnection);
				}

				if (PreferredLoginQueue.contains(udpConnection)) {
					PreferredLoginQueue.remove(udpConnection);
				}
			}

			sendPlaceInTheQueue();
		}
	}

	public static boolean isInTheQueue(UdpConnection udpConnection) {
		if (!ServerOptions.getInstance().LoginQueueEnabled.getValue()) {
			return false;
		} else {
			synchronized (LoginQueue) {
				return udpConnection == currentLoginQueue || LoginQueue.contains(udpConnection) || PreferredLoginQueue.contains(udpConnection);
			}
		}
	}

	public static void update() {
		if (ServerOptions.getInstance().LoginQueueEnabled.getValue() && UpdateLimit.Check()) {
			synchronized (LoginQueue) {
				if (currentLoginQueue != null) {
					if (currentLoginQueue.isFullyConnected()) {
						if (Core.bDebug) {
							DebugLog.log("update: isFullyConnected (ip:" + currentLoginQueue.ip + ")");
						}

						currentLoginQueue = null;
					} else if (LoginQueueTimeout.Check()) {
						if (Core.bDebug) {
							DebugLog.log("update: timeout (ip:" + currentLoginQueue.ip + ")");
						}

						currentLoginQueue = null;
					}
				}

				loadNextPlayer();
			}
		}
	}

	private static void loadNextPlayer() {
		if (!PreferredLoginQueue.isEmpty() && currentLoginQueue == null) {
			currentLoginQueue = (UdpConnection)PreferredLoginQueue.remove(0);
			currentLoginQueue.wasInLoadingQueue = true;
			if (Core.bDebug) {
				DebugLog.log("update: Next player from the preferred queue to connect (ip:" + currentLoginQueue.ip + ")");
			}

			LoginQueueTimeout.Reset((long)(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000));
			sendConnectRequest(currentLoginQueue);
			sendPlaceInTheQueue();
		}

		if (!LoginQueue.isEmpty() && currentLoginQueue == null) {
			currentLoginQueue = (UdpConnection)LoginQueue.remove(0);
			currentLoginQueue.wasInLoadingQueue = true;
			if (Core.bDebug) {
				DebugLog.log("update: Next player to connect (ip:" + currentLoginQueue.ip + ")");
			}

			LoginQueueTimeout.Reset((long)(ServerOptions.getInstance().LoginQueueConnectTimeout.getValue() * 1000));
			sendConnectRequest(currentLoginQueue);
			sendPlaceInTheQueue();
		}
	}

	public static String getDescription() {
		int int1 = LoginQueue.size();
		return "queue=[" + int1 + "/" + PreferredLoginQueue.size() + "/\"" + (currentLoginQueue == null ? "" : currentLoginQueue.getConnectedGUID()) + "\"]";
	}

	public static enum LoginQueueMessageType {

		ConnectionImmediate,
		PlaceInQueue;

		private static LoginQueue.LoginQueueMessageType[] $values() {
			return new LoginQueue.LoginQueueMessageType[]{ConnectionImmediate, PlaceInQueue};
		}
	}
}
