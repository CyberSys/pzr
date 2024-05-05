package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;


public class PlayerVariables implements INetworkPacket {
	byte count = 0;
	PlayerVariables.NetworkPlayerVariable[] variables = new PlayerVariables.NetworkPlayerVariable[2];

	public PlayerVariables() {
		for (byte byte1 = 0; byte1 < this.variables.length; ++byte1) {
			this.variables[byte1] = new PlayerVariables.NetworkPlayerVariable();
		}
	}

	public void set(IsoPlayer player) {
		String string = player.getActionStateName();
		if (string.equals("idle")) {
			this.variables[0].set(player, PlayerVariables.NetworkPlayerVariableIDs.IdleSpeed);
			this.count = 1;
		} else if (string.equals("maskingleft") || string.equals("maskingright") || string.equals("movement") || string.equals("run") || string.equals("sprint")) {
			this.variables[0].set(player, PlayerVariables.NetworkPlayerVariableIDs.WalkInjury);
			this.variables[1].set(player, PlayerVariables.NetworkPlayerVariableIDs.WalkSpeed);
			this.count = 2;
		}
	}

	public void apply(IsoPlayer player) {
		for (byte byte1 = 0; byte1 < this.count; ++byte1) {
			player.setVariable(this.variables[byte1].id.name(), this.variables[byte1].value);
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.count = byteBuffer.get();
		for (byte byte1 = 0; byte1 < this.count; ++byte1) {
			this.variables[byte1].id = PlayerVariables.NetworkPlayerVariableIDs.values()[byteBuffer.get()];
			this.variables[byte1].value = byteBuffer.getFloat();
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte(this.count);
		for (byte byte1 = 0; byte1 < this.count; ++byte1) {
			byteBufferWriter.putByte((byte)this.variables[byte1].id.ordinal());
			byteBufferWriter.putFloat(this.variables[byte1].value);
		}
	}

	public int getPacketSizeBytes() {
		return 1 + this.count * 5;
	}

	public String getDescription() {
		String string = "PlayerVariables: ";
		string = string + "count=" + this.count + " | ";
		for (byte byte1 = 0; byte1 < this.count; ++byte1) {
			string = string + "id=" + this.variables[byte1].id.name() + ", ";
			string = string + "value=" + this.variables[byte1].value + " | ";
		}

		return string;
	}

	public void copy(PlayerVariables playerVariables) {
		this.count = playerVariables.count;
		for (byte byte1 = 0; byte1 < this.count; ++byte1) {
			this.variables[byte1].id = playerVariables.variables[byte1].id;
			this.variables[byte1].value = playerVariables.variables[byte1].value;
		}
	}

	private class NetworkPlayerVariable {
		PlayerVariables.NetworkPlayerVariableIDs id;
		float value;

		public void set(IsoPlayer player, PlayerVariables.NetworkPlayerVariableIDs networkPlayerVariableIDs) {
			this.id = networkPlayerVariableIDs;
			this.value = player.getVariableFloat(networkPlayerVariableIDs.name(), 0.0F);
		}
	}

	private static enum NetworkPlayerVariableIDs {

		IdleSpeed,
		WalkInjury,
		WalkSpeed,
		DeltaX,
		DeltaY,
		AttackVariationX,
		AttackVariationY,
		targetDist,
		autoShootVarX,
		autoShootVarY,
		recoilVarX,
		recoilVarY,
		ShoveAimX,
		ShoveAimY;

		private static PlayerVariables.NetworkPlayerVariableIDs[] $values() {
			return new PlayerVariables.NetworkPlayerVariableIDs[]{IdleSpeed, WalkInjury, WalkSpeed, DeltaX, DeltaY, AttackVariationX, AttackVariationY, targetDist, autoShootVarX, autoShootVarY, recoilVarX, recoilVarY, ShoveAimX, ShoveAimY};
		}
	}
}
