package zombie.network;

import java.nio.ByteBuffer;
import java.util.Stack;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoAnim;


public class PlayerNetState {
	public long time;
	public byte id;
	public byte dir;
	public float x;
	public float y;
	public float z;
	public float mx;
	public float my;
	public byte remoteState;
	public byte anim;
	public byte frame;
	public float adv;
	public float torchDist;
	public float torchStrength;
	public boolean finished;
	public boolean looped;
	public boolean stopOnFrameOneAfterLoop;
	public boolean torchCone;
	private static Stack pool = new Stack();

	public void unpack(ByteBuffer byteBuffer) {
		this.id = byteBuffer.get();
		this.time = byteBuffer.getLong();
		this.dir = byteBuffer.get();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.mx = byteBuffer.getFloat();
		this.my = byteBuffer.getFloat();
		this.remoteState = byteBuffer.get();
		this.anim = byteBuffer.get();
		this.frame = byteBuffer.get();
		this.adv = byteBuffer.getFloat();
		this.torchDist = byteBuffer.getFloat();
		this.torchStrength = byteBuffer.getFloat();
		byte byte1 = byteBuffer.get();
		this.finished = (byte1 & 1) != 0;
		this.looped = (byte1 & 2) != 0;
		this.stopOnFrameOneAfterLoop = (byte1 & 4) != 0;
		this.torchCone = (byte1 & 16) != 0;
	}

	public void update(IsoPlayer player, float float1, float float2, float float3) {
		player.setRemoteState(this.remoteState);
		IsoAnim anim = (IsoAnim)player.legsSprite.AnimStack.get(this.anim);
		player.setDir(this.dir);
		player.setX(float1);
		player.setY(float2);
		player.setZ(float3);
		if (this.remoteState != IsoPlayer.NetRemoteState_Attack) {
			player.setRemoteMoveX(this.mx);
			player.setRemoteMoveY(this.my);
		} else {
			player.setRemoteMoveX(0.0F);
			player.setRemoteMoveY(0.0F);
		}

		player.TimeSinceLastNetData = 0;
		player.PlayAnim(anim.name);
		player.def.Frame = (float)this.frame;
		player.def.Finished = this.finished;
		player.def.Looped = this.looped;
		if (player.legsSprite != null && player.legsSprite.CurrentAnim != null && this.stopOnFrameOneAfterLoop) {
			player.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
		}

		player.def.AnimFrameIncrease = this.adv;
		player.angle.x = player.dir.ToVector().x;
		player.angle.y = player.dir.ToVector().y;
		player.mpTorchDist = this.torchDist;
		player.mpTorchStrength = this.torchStrength;
		player.mpTorchCone = this.torchCone;
		player.bx = player.by = 0.0F;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)float1, (double)float2, (double)float3);
		if (square != null && !IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
			IsoWorld.instance.CurrentCell.getObjectList().add(player);
		}
	}

	public static PlayerNetState get() {
		return pool.isEmpty() ? new PlayerNetState() : (PlayerNetState)pool.pop();
	}

	public static void release(PlayerNetState playerNetState) {
		pool.push(playerNetState);
	}
}
