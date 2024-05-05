package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;


public class Info {
	private static final int OV_EBADPACKET = -136;
	private static final int OV_ENOTAUDIO = -135;
	private static byte[] _vorbis = "vorbis".getBytes();
	private static final int VI_TIMEB = 1;
	private static final int VI_FLOORB = 2;
	private static final int VI_RESB = 3;
	private static final int VI_MAPB = 1;
	private static final int VI_WINDOWB = 1;
	public int channels;
	public int rate;
	public int version;
	int bitrate_lower;
	int bitrate_nominal;
	int bitrate_upper;
	int[] blocksizes = new int[2];
	StaticCodeBook[] book_param = null;
	int books;
	int envelopesa;
	Object[] floor_param = null;
	int[] floor_type = null;
	int floors;
	Object[] map_param = null;
	int[] map_type = null;
	int maps;
	InfoMode[] mode_param = null;
	int modes;
	float preecho_clamp;
	float preecho_thresh;
	PsyInfo[] psy_param = new PsyInfo[64];
	int psys;
	Object[] residue_param = null;
	int[] residue_type = null;
	int residues;
	Object[] time_param = null;
	int[] time_type = null;
	int times;

	public int blocksize(Packet packet) {
		Buffer buffer = new Buffer();
		buffer.readinit(packet.packet_base, packet.packet, packet.bytes);
		if (buffer.read(1) != 0) {
			return -135;
		} else {
			int int1 = 0;
			for (int int2 = this.modes; int2 > 1; int2 >>>= 1) {
				++int1;
			}

			int int3 = buffer.read(int1);
			return int3 == -1 ? -136 : this.blocksizes[this.mode_param[int3].blockflag];
		}
	}

	public void clear() {
		int int1;
		for (int1 = 0; int1 < this.modes; ++int1) {
			this.mode_param[int1] = null;
		}

		this.mode_param = null;
		for (int1 = 0; int1 < this.maps; ++int1) {
			FuncMapping.mapping_P[this.map_type[int1]].free_info(this.map_param[int1]);
		}

		this.map_param = null;
		for (int1 = 0; int1 < this.times; ++int1) {
			FuncTime.time_P[this.time_type[int1]].free_info(this.time_param[int1]);
		}

		this.time_param = null;
		for (int1 = 0; int1 < this.floors; ++int1) {
			FuncFloor.floor_P[this.floor_type[int1]].free_info(this.floor_param[int1]);
		}

		this.floor_param = null;
		for (int1 = 0; int1 < this.residues; ++int1) {
			FuncResidue.residue_P[this.residue_type[int1]].free_info(this.residue_param[int1]);
		}

		this.residue_param = null;
		for (int1 = 0; int1 < this.books; ++int1) {
			if (this.book_param[int1] != null) {
				this.book_param[int1].clear();
				this.book_param[int1] = null;
			}
		}

		this.book_param = null;
		for (int1 = 0; int1 < this.psys; ++int1) {
			this.psy_param[int1].free();
		}
	}

	public void init() {
		this.rate = 0;
	}

	public int synthesis_headerin(Comment comment, Packet packet) {
		Buffer buffer = new Buffer();
		if (packet != null) {
			buffer.readinit(packet.packet_base, packet.packet, packet.bytes);
			byte[] byteArray = new byte[6];
			int int1 = buffer.read(8);
			buffer.read(byteArray, 6);
			if (byteArray[0] != 118 || byteArray[1] != 111 || byteArray[2] != 114 || byteArray[3] != 98 || byteArray[4] != 105 || byteArray[5] != 115) {
				return -1;
			}

			switch (int1) {
			case 1: 
				if (packet.b_o_s == 0) {
					return -1;
				}

				if (this.rate != 0) {
					return -1;
				}

				return this.unpack_info(buffer);
			
			case 2: 
			
			case 4: 
			
			default: 
				break;
			
			case 3: 
				if (this.rate == 0) {
					return -1;
				}

				return comment.unpack(buffer);
			
			case 5: 
				if (this.rate != 0 && comment.vendor != null) {
					return this.unpack_books(buffer);
				}

				return -1;
			
			}
		}

		return -1;
	}

	public String toString() {
		Integer integer = new Integer(this.version);
		return "version:" + integer + ", channels:" + new Integer(this.channels) + ", rate:" + new Integer(this.rate) + ", bitrate:" + new Integer(this.bitrate_upper) + "," + new Integer(this.bitrate_nominal) + "," + new Integer(this.bitrate_lower);
	}

	int pack_books(Buffer buffer) {
		buffer.write(5, 8);
		buffer.write(_vorbis);
		buffer.write(this.books - 1, 8);
		int int1;
		for (int1 = 0; int1 < this.books; ++int1) {
			if (this.book_param[int1].pack(buffer) != 0) {
				return -1;
			}
		}

		buffer.write(this.times - 1, 6);
		for (int1 = 0; int1 < this.times; ++int1) {
			buffer.write(this.time_type[int1], 16);
			FuncTime.time_P[this.time_type[int1]].pack(this.time_param[int1], buffer);
		}

		buffer.write(this.floors - 1, 6);
		for (int1 = 0; int1 < this.floors; ++int1) {
			buffer.write(this.floor_type[int1], 16);
			FuncFloor.floor_P[this.floor_type[int1]].pack(this.floor_param[int1], buffer);
		}

		buffer.write(this.residues - 1, 6);
		for (int1 = 0; int1 < this.residues; ++int1) {
			buffer.write(this.residue_type[int1], 16);
			FuncResidue.residue_P[this.residue_type[int1]].pack(this.residue_param[int1], buffer);
		}

		buffer.write(this.maps - 1, 6);
		for (int1 = 0; int1 < this.maps; ++int1) {
			buffer.write(this.map_type[int1], 16);
			FuncMapping.mapping_P[this.map_type[int1]].pack(this, this.map_param[int1], buffer);
		}

		buffer.write(this.modes - 1, 6);
		for (int1 = 0; int1 < this.modes; ++int1) {
			buffer.write(this.mode_param[int1].blockflag, 1);
			buffer.write(this.mode_param[int1].windowtype, 16);
			buffer.write(this.mode_param[int1].transformtype, 16);
			buffer.write(this.mode_param[int1].mapping, 8);
		}

		buffer.write(1, 1);
		return 0;
	}

	int pack_info(Buffer buffer) {
		buffer.write(1, 8);
		buffer.write(_vorbis);
		buffer.write(0, 32);
		buffer.write(this.channels, 8);
		buffer.write(this.rate, 32);
		buffer.write(this.bitrate_upper, 32);
		buffer.write(this.bitrate_nominal, 32);
		buffer.write(this.bitrate_lower, 32);
		buffer.write(Util.ilog2(this.blocksizes[0]), 4);
		buffer.write(Util.ilog2(this.blocksizes[1]), 4);
		buffer.write(1, 1);
		return 0;
	}

	int unpack_books(Buffer buffer) {
		this.books = buffer.read(8) + 1;
		if (this.book_param == null || this.book_param.length != this.books) {
			this.book_param = new StaticCodeBook[this.books];
		}

		int int1;
		for (int1 = 0; int1 < this.books; ++int1) {
			this.book_param[int1] = new StaticCodeBook();
			if (this.book_param[int1].unpack(buffer) != 0) {
				this.clear();
				return -1;
			}
		}

		this.times = buffer.read(6) + 1;
		if (this.time_type == null || this.time_type.length != this.times) {
			this.time_type = new int[this.times];
		}

		if (this.time_param == null || this.time_param.length != this.times) {
			this.time_param = new Object[this.times];
		}

		for (int1 = 0; int1 < this.times; ++int1) {
			this.time_type[int1] = buffer.read(16);
			if (this.time_type[int1] < 0 || this.time_type[int1] >= 1) {
				this.clear();
				return -1;
			}

			this.time_param[int1] = FuncTime.time_P[this.time_type[int1]].unpack(this, buffer);
			if (this.time_param[int1] == null) {
				this.clear();
				return -1;
			}
		}

		this.floors = buffer.read(6) + 1;
		if (this.floor_type == null || this.floor_type.length != this.floors) {
			this.floor_type = new int[this.floors];
		}

		if (this.floor_param == null || this.floor_param.length != this.floors) {
			this.floor_param = new Object[this.floors];
		}

		for (int1 = 0; int1 < this.floors; ++int1) {
			this.floor_type[int1] = buffer.read(16);
			if (this.floor_type[int1] < 0 || this.floor_type[int1] >= 2) {
				this.clear();
				return -1;
			}

			this.floor_param[int1] = FuncFloor.floor_P[this.floor_type[int1]].unpack(this, buffer);
			if (this.floor_param[int1] == null) {
				this.clear();
				return -1;
			}
		}

		this.residues = buffer.read(6) + 1;
		if (this.residue_type == null || this.residue_type.length != this.residues) {
			this.residue_type = new int[this.residues];
		}

		if (this.residue_param == null || this.residue_param.length != this.residues) {
			this.residue_param = new Object[this.residues];
		}

		for (int1 = 0; int1 < this.residues; ++int1) {
			this.residue_type[int1] = buffer.read(16);
			if (this.residue_type[int1] < 0 || this.residue_type[int1] >= 3) {
				this.clear();
				return -1;
			}

			this.residue_param[int1] = FuncResidue.residue_P[this.residue_type[int1]].unpack(this, buffer);
			if (this.residue_param[int1] == null) {
				this.clear();
				return -1;
			}
		}

		this.maps = buffer.read(6) + 1;
		if (this.map_type == null || this.map_type.length != this.maps) {
			this.map_type = new int[this.maps];
		}

		if (this.map_param == null || this.map_param.length != this.maps) {
			this.map_param = new Object[this.maps];
		}

		for (int1 = 0; int1 < this.maps; ++int1) {
			this.map_type[int1] = buffer.read(16);
			if (this.map_type[int1] < 0 || this.map_type[int1] >= 1) {
				this.clear();
				return -1;
			}

			this.map_param[int1] = FuncMapping.mapping_P[this.map_type[int1]].unpack(this, buffer);
			if (this.map_param[int1] == null) {
				this.clear();
				return -1;
			}
		}

		this.modes = buffer.read(6) + 1;
		if (this.mode_param == null || this.mode_param.length != this.modes) {
			this.mode_param = new InfoMode[this.modes];
		}

		for (int1 = 0; int1 < this.modes; ++int1) {
			this.mode_param[int1] = new InfoMode();
			this.mode_param[int1].blockflag = buffer.read(1);
			this.mode_param[int1].windowtype = buffer.read(16);
			this.mode_param[int1].transformtype = buffer.read(16);
			this.mode_param[int1].mapping = buffer.read(8);
			if (this.mode_param[int1].windowtype >= 1 || this.mode_param[int1].transformtype >= 1 || this.mode_param[int1].mapping >= this.maps) {
				this.clear();
				return -1;
			}
		}

		if (buffer.read(1) != 1) {
			this.clear();
			return -1;
		} else {
			return 0;
		}
	}

	int unpack_info(Buffer buffer) {
		this.version = buffer.read(32);
		if (this.version != 0) {
			return -1;
		} else {
			this.channels = buffer.read(8);
			this.rate = buffer.read(32);
			this.bitrate_upper = buffer.read(32);
			this.bitrate_nominal = buffer.read(32);
			this.bitrate_lower = buffer.read(32);
			this.blocksizes[0] = 1 << buffer.read(4);
			this.blocksizes[1] = 1 << buffer.read(4);
			if (this.rate >= 1 && this.channels >= 1 && this.blocksizes[0] >= 8 && this.blocksizes[1] >= this.blocksizes[0] && buffer.read(1) == 1) {
				return 0;
			} else {
				this.clear();
				return -1;
			}
		}
	}
}
