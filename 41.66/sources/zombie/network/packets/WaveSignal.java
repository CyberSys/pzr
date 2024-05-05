package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;


public class WaveSignal implements INetworkPacket {
	int sourceX;
	int sourceY;
	int channel;
	String msg;
	String codes;
	float r;
	float g;
	float b;
	int signalStrength;
	boolean isTV;

	public void set(int int1, int int2, int int3, String string, String string2, float float1, float float2, float float3, int int4, boolean boolean1) {
		this.sourceX = int1;
		this.sourceY = int2;
		this.channel = int3;
		this.msg = string;
		this.codes = string2;
		this.r = float1;
		this.g = float2;
		this.b = float3;
		this.signalStrength = int4;
		this.isTV = boolean1;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.sourceX = byteBuffer.getInt();
		this.sourceY = byteBuffer.getInt();
		this.channel = byteBuffer.getInt();
		this.msg = null;
		if (byteBuffer.get() == 1) {
			this.msg = GameWindow.ReadString(byteBuffer);
		}

		this.codes = null;
		if (byteBuffer.get() == 1) {
			this.codes = GameWindow.ReadString(byteBuffer);
		}

		this.r = byteBuffer.getFloat();
		this.g = byteBuffer.getFloat();
		this.b = byteBuffer.getFloat();
		this.signalStrength = byteBuffer.getInt();
		this.isTV = byteBuffer.get() == 1;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putInt(this.sourceX);
		byteBufferWriter.putInt(this.sourceY);
		byteBufferWriter.putInt(this.channel);
		byteBufferWriter.putBoolean(this.msg != null);
		if (this.msg != null) {
			GameWindow.WriteString(byteBufferWriter.bb, this.msg);
		}

		byteBufferWriter.putByte((byte)(this.codes != null ? 1 : 0));
		if (this.codes != null) {
			byteBufferWriter.putUTF(this.codes);
		}

		byteBufferWriter.putFloat(this.r);
		byteBufferWriter.putFloat(this.g);
		byteBufferWriter.putFloat(this.b);
		byteBufferWriter.putInt(this.signalStrength);
		byteBufferWriter.putByte((byte)(this.isTV ? 1 : 0));
	}

	public void process(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			ZomboidRadio.getInstance().SendTransmission(udpConnection.getConnectedGUID(), this.sourceX, this.sourceY, this.channel, this.msg, this.codes, this.r, this.g, this.b, this.signalStrength, this.isTV);
		} else {
			ZomboidRadio.getInstance().ReceiveTransmission(this.sourceX, this.sourceY, this.channel, this.msg, this.codes, this.r, this.g, this.b, this.signalStrength, this.isTV);
		}
	}
}
