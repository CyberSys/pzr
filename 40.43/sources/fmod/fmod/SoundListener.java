package fmod.fmod;

import fmod.javafmod;
import zombie.SoundManager;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector3;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class SoundListener extends BaseSoundListener {
	public float lx;
	public float ly;
	public float lz;
	static Vector3 vec = new Vector3();

	public SoundListener(int int1) {
		super(int1);
	}

	public void tick() {
		if (!GameServer.bServer) {
			if (IsoPlayer.players[this.index] == null || !IsoPlayer.players[this.index].HasTrait("Deaf")) {
				int int1 = 0;
				for (int int2 = 0; int2 < IsoPlayer.numPlayers && int2 != this.index; ++int2) {
					if (IsoPlayer.players[int2] != null && !IsoPlayer.players[int2].HasTrait("Deaf")) {
						++int1;
					}
				}

				vec.x = -1.0F;
				vec.y = -1.0F;
				vec.normalize();
				this.lx = this.x;
				this.ly = this.y;
				this.lz = this.z;
				if (!GameClient.bClient || SoundManager.instance.getSoundVolume() > 0.0F) {
					javafmod.FMOD_Studio_Listener3D(int1, this.x, this.y, this.z * 3.0F, this.x - this.lx, this.y - this.ly, this.z - this.lz, vec.x, vec.y, vec.z, 0.0F, 0.0F, 1.0F);
				}

				this.lx = this.x;
				this.ly = this.y;
				this.lz = this.z;
			}
		}
	}
}
