package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.gameStates.GameLoadingState;
import zombie.network.packets.RequestDataPacket;


public class RequestDataManager {
	public static final int smallFileSize = 1024;
	public static final int maxLargeFileSize = 52428800;
	public static final int packSize = 204800;
	private final ArrayList requests = new ArrayList();
	private static RequestDataManager instance;

	private RequestDataManager() {
	}

	public static RequestDataManager getInstance() {
		if (instance == null) {
			instance = new RequestDataManager();
		}

		return instance;
	}

	public void ACKWasReceived(RequestDataPacket.RequestID requestID, UdpConnection udpConnection, int int1) {
		RequestDataManager.RequestData requestData = null;
		for (int int2 = 0; int2 <= this.requests.size(); ++int2) {
			if (((RequestDataManager.RequestData)this.requests.get(int2)).connectionGUID == udpConnection.getConnectedGUID()) {
				requestData = (RequestDataManager.RequestData)this.requests.get(int2);
				break;
			}
		}

		if (requestData != null && requestData.id == requestID) {
			this.sendData(requestData);
		}
	}

	public void putDataForTransmit(RequestDataPacket.RequestID requestID, UdpConnection udpConnection, ByteBuffer byteBuffer) {
		RequestDataManager.RequestData requestData = new RequestDataManager.RequestData(requestID, byteBuffer, udpConnection.getConnectedGUID());
		this.requests.add(requestData);
		this.sendData(requestData);
	}

	public void disconnect(UdpConnection udpConnection) {
		long long1 = System.currentTimeMillis();
		this.requests.removeIf((var3)->{
			return long1 - var3.creationTime > 60000L || var3.connectionGUID == udpConnection.getConnectedGUID();
		});
	}

	public void clear() {
		this.requests.clear();
	}

	private void sendData(RequestDataManager.RequestData requestData) {
		requestData.creationTime = System.currentTimeMillis();
		int int1 = requestData.bb.limit();
		requestData.realTransmittedFromLastACK = 0;
		UdpConnection udpConnection = GameServer.udpEngine.getActiveConnection(requestData.connectionGUID);
		RequestDataPacket requestDataPacket = new RequestDataPacket();
		requestDataPacket.setPartData(requestData.id, requestData.bb);
		while (requestData.realTransmittedFromLastACK < 204800) {
			int int2 = Math.min(1024, int1 - requestData.realTransmitted);
			if (int2 == 0) {
				break;
			}

			requestDataPacket.setPartDataParameters(requestData.realTransmitted, int2);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
			requestDataPacket.write(byteBufferWriter);
			PacketTypes.PacketType.RequestData.send(udpConnection);
			requestData.realTransmittedFromLastACK += int2;
			requestData.realTransmitted += int2;
		}

		if (requestData.realTransmitted == int1) {
			this.requests.remove(requestData);
		}
	}

	public ByteBuffer receiveClientData(RequestDataPacket.RequestID requestID, ByteBuffer byteBuffer, int int1, int int2) {
		RequestDataManager.RequestData requestData = null;
		for (int int3 = 0; int3 < this.requests.size(); ++int3) {
			if (((RequestDataManager.RequestData)this.requests.get(int3)).id == requestID) {
				requestData = (RequestDataManager.RequestData)this.requests.get(int3);
				break;
			}
		}

		if (requestData == null) {
			requestData = new RequestDataManager.RequestData(requestID, int1, 0L);
			this.requests.add(requestData);
		}

		requestData.bb.position(int2);
		requestData.bb.put(byteBuffer.array(), 0, byteBuffer.limit());
		requestData.realTransmitted += byteBuffer.limit();
		requestData.realTransmittedFromLastACK += byteBuffer.limit();
		if (requestData.realTransmittedFromLastACK >= 204800) {
			requestData.realTransmittedFromLastACK = 0;
			RequestDataPacket requestDataPacket = new RequestDataPacket();
			requestDataPacket.setACK(requestData.id);
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.PacketType.RequestData.doPacket(byteBufferWriter);
			requestDataPacket.write(byteBufferWriter);
			PacketTypes.PacketType.RequestData.send(GameClient.connection);
		}

		GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_DownloadedLargeFile", requestData.realTransmitted * 100 / int1, requestData.id.getDescriptor());
		if (requestData.realTransmitted == int1) {
			this.requests.remove(requestData);
			requestData.bb.position(0);
			return requestData.bb;
		} else {
			return null;
		}
	}

	static class RequestData {
		private final RequestDataPacket.RequestID id;
		private final ByteBuffer bb;
		private final long connectionGUID;
		private long creationTime = System.currentTimeMillis();
		private int realTransmitted;
		private int realTransmittedFromLastACK;

		public RequestData(RequestDataPacket.RequestID requestID, ByteBuffer byteBuffer, long long1) {
			this.id = requestID;
			this.bb = ByteBuffer.allocate(byteBuffer.position());
			this.bb.put(byteBuffer.array(), 0, this.bb.limit());
			this.connectionGUID = long1;
			this.realTransmitted = 0;
			this.realTransmittedFromLastACK = 0;
		}

		public RequestData(RequestDataPacket.RequestID requestID, int int1, long long1) {
			this.id = requestID;
			this.bb = ByteBuffer.allocate(int1);
			this.bb.clear();
			this.connectionGUID = long1;
			this.realTransmitted = 0;
			this.realTransmittedFromLastACK = 0;
		}
	}
}
