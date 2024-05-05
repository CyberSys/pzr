package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;


public class Block {
	int eofflag;
	int floor_bits;
	int glue_bits;
	long granulepos;
	int lW;
	int mode;
	int nW;
	Buffer opb = new Buffer();
	float[][] pcm = new float[0][];
	int pcmend;
	int res_bits;
	long sequence;
	int time_bits;
	DspState vd;
	int W;

	public static String asdsadsa(String string, byte[] byteArray, int int1) {
		string = string + Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1);
		return string;
	}

	public Block(DspState dspState) {
		this.vd = dspState;
		if (dspState.analysisp != 0) {
			this.opb.writeinit();
		}
	}

	public int clear() {
		if (this.vd != null && this.vd.analysisp != 0) {
			this.opb.writeclear();
		}

		return 0;
	}

	public void init(DspState dspState) {
		this.vd = dspState;
	}

	public int synthesis(Packet packet) {
		Info info = this.vd.vi;
		this.opb.readinit(packet.packet_base, packet.packet, packet.bytes);
		if (this.opb.read(1) != 0) {
			return -1;
		} else {
			int int1 = this.opb.read(this.vd.modebits);
			if (int1 == -1) {
				return -1;
			} else {
				this.mode = int1;
				this.W = info.mode_param[this.mode].blockflag;
				if (this.W != 0) {
					this.lW = this.opb.read(1);
					this.nW = this.opb.read(1);
					if (this.nW == -1) {
						return -1;
					}
				} else {
					this.lW = 0;
					this.nW = 0;
				}

				this.granulepos = packet.granulepos;
				this.sequence = packet.packetno - 3L;
				this.eofflag = packet.e_o_s;
				this.pcmend = info.blocksizes[this.W];
				if (this.pcm.length < info.channels) {
					this.pcm = new float[info.channels][];
				}

				int int2;
				for (int2 = 0; int2 < info.channels; ++int2) {
					if (this.pcm[int2] != null && this.pcm[int2].length >= this.pcmend) {
						for (int int3 = 0; int3 < this.pcmend; ++int3) {
							this.pcm[int2][int3] = 0.0F;
						}
					} else {
						this.pcm[int2] = new float[this.pcmend];
					}
				}

				int2 = info.map_type[info.mode_param[this.mode].mapping];
				return FuncMapping.mapping_P[int2].inverse(this, this.vd.mode[this.mode]);
			}
		}
	}
}
