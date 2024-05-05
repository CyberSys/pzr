package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


public class VorbisFile {
	static final int CHUNKSIZE = 8500;
	static final int SEEK_SET = 0;
	static final int SEEK_CUR = 1;
	static final int SEEK_END = 2;
	static final int OV_FALSE = -1;
	static final int OV_EOF = -2;
	static final int OV_HOLE = -3;
	static final int OV_EREAD = -128;
	static final int OV_EFAULT = -129;
	static final int OV_EIMPL = -130;
	static final int OV_EINVAL = -131;
	static final int OV_ENOTVORBIS = -132;
	static final int OV_EBADHEADER = -133;
	static final int OV_EVERSION = -134;
	static final int OV_ENOTAUDIO = -135;
	static final int OV_EBADPACKET = -136;
	static final int OV_EBADLINK = -137;
	static final int OV_ENOSEEK = -138;
	float bittrack;
	int current_link;
	int current_serialno;
	long[] dataoffsets;
	InputStream datasource;
	boolean decode_ready = false;
	long end;
	int links;
	long offset;
	long[] offsets;
	StreamState os = new StreamState();
	SyncState oy = new SyncState();
	long pcm_offset;
	long[] pcmlengths;
	float samptrack;
	boolean seekable = false;
	int[] serialnos;
	Comment[] vc;
	DspState vd = new DspState();
	Block vb;
	Info[] vi;

	public VorbisFile(String string) throws JOrbisException {
		this.vb = new Block(this.vd);
		VorbisFile.SeekableInputStream seekableInputStream = null;
		try {
			seekableInputStream = new VorbisFile.SeekableInputStream(string);
			int int1 = this.open(seekableInputStream, (byte[])null, 0);
			if (int1 == -1) {
				throw new JOrbisException("VorbisFile: open return -1");
			}
		} catch (Exception exception) {
			throw new JOrbisException("VorbisFile: " + exception.toString());
		} finally {
			if (seekableInputStream != null) {
				try {
					seekableInputStream.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}
	}

	public VorbisFile(InputStream inputStream, byte[] byteArray, int int1) throws JOrbisException {
		this.vb = new Block(this.vd);
		int int2 = this.open(inputStream, byteArray, int1);
		if (int2 == -1) {
		}
	}

	static int fseek(InputStream inputStream, long long1, int int1) {
		if (inputStream instanceof VorbisFile.SeekableInputStream) {
			VorbisFile.SeekableInputStream seekableInputStream = (VorbisFile.SeekableInputStream)inputStream;
			try {
				if (int1 == 0) {
					seekableInputStream.seek(long1);
				} else if (int1 == 2) {
					seekableInputStream.seek(seekableInputStream.getLength() - long1);
				}
			} catch (Exception exception) {
			}

			return 0;
		} else {
			try {
				if (int1 == 0) {
					inputStream.reset();
				}

				inputStream.skip(long1);
				return 0;
			} catch (Exception exception2) {
				return -1;
			}
		}
	}

	static long ftell(InputStream inputStream) {
		try {
			if (inputStream instanceof VorbisFile.SeekableInputStream) {
				VorbisFile.SeekableInputStream seekableInputStream = (VorbisFile.SeekableInputStream)inputStream;
				return seekableInputStream.tell();
			}
		} catch (Exception exception) {
		}

		return 0L;
	}

	public int bitrate(int int1) {
		if (int1 >= this.links) {
			return -1;
		} else if (!this.seekable && int1 != 0) {
			return this.bitrate(0);
		} else if (int1 >= 0) {
			if (this.seekable) {
				return (int)Math.rint((double)((float)((this.offsets[int1 + 1] - this.dataoffsets[int1]) * 8L) / this.time_total(int1)));
			} else if (this.vi[int1].bitrate_nominal > 0) {
				return this.vi[int1].bitrate_nominal;
			} else if (this.vi[int1].bitrate_upper > 0) {
				return this.vi[int1].bitrate_lower > 0 ? (this.vi[int1].bitrate_upper + this.vi[int1].bitrate_lower) / 2 : this.vi[int1].bitrate_upper;
			} else {
				return -1;
			}
		} else {
			long long1 = 0L;
			for (int int2 = 0; int2 < this.links; ++int2) {
				long1 += (this.offsets[int2 + 1] - this.dataoffsets[int2]) * 8L;
			}

			return (int)Math.rint((double)((float)long1 / this.time_total(-1)));
		}
	}

	public int bitrate_instant() {
		int int1 = this.seekable ? this.current_link : 0;
		if (this.samptrack == 0.0F) {
			return -1;
		} else {
			int int2 = (int)((double)(this.bittrack / this.samptrack * (float)this.vi[int1].rate) + 0.5);
			this.bittrack = 0.0F;
			this.samptrack = 0.0F;
			return int2;
		}
	}

	public void close() throws IOException {
		this.datasource.close();
	}

	public Comment[] getComment() {
		return this.vc;
	}

	public Comment getComment(int int1) {
		if (this.seekable) {
			if (int1 < 0) {
				return this.decode_ready ? this.vc[this.current_link] : null;
			} else {
				return int1 >= this.links ? null : this.vc[int1];
			}
		} else {
			return this.decode_ready ? this.vc[0] : null;
		}
	}

	public Info[] getInfo() {
		return this.vi;
	}

	public Info getInfo(int int1) {
		if (this.seekable) {
			if (int1 < 0) {
				return this.decode_ready ? this.vi[this.current_link] : null;
			} else {
				return int1 >= this.links ? null : this.vi[int1];
			}
		} else {
			return this.decode_ready ? this.vi[0] : null;
		}
	}

	public int pcm_seek(long long1) {
		boolean boolean1 = true;
		long long2 = this.pcm_total(-1);
		if (!this.seekable) {
			return -1;
		} else if (long1 >= 0L && long1 <= long2) {
			int int1;
			for (int1 = this.links - 1; int1 >= 0; --int1) {
				long2 -= this.pcmlengths[int1];
				if (long1 >= long2) {
					break;
				}
			}

			long long3 = long1 - long2;
			long long4 = this.offsets[int1 + 1];
			long long5 = this.offsets[int1];
			int int2 = (int)long5;
			Page page = new Page();
			while (long5 < long4) {
				long long6;
				if (long4 - long5 < 8500L) {
					long6 = long5;
				} else {
					long6 = (long4 + long5) / 2L;
				}

				this.seek_helper(long6);
				int int3 = this.get_next_page(page, long4 - long6);
				if (int3 == -1) {
					long4 = long6;
				} else {
					long long7 = page.granulepos();
					if (long7 < long3) {
						int2 = int3;
						long5 = this.offset;
					} else {
						long4 = long6;
					}
				}
			}

			if (this.raw_seek(int2) != 0) {
				this.pcm_offset = -1L;
				this.decode_clear();
				return -1;
			} else if (this.pcm_offset >= long1) {
				this.pcm_offset = -1L;
				this.decode_clear();
				return -1;
			} else if (long1 > this.pcm_total(-1)) {
				this.pcm_offset = -1L;
				this.decode_clear();
				return -1;
			} else {
				while (this.pcm_offset < long1) {
					int int4 = (int)(long1 - this.pcm_offset);
					float[][][] floatArrayArrayArray = new float[1][][];
					int[] intArray = new int[this.getInfo(-1).channels];
					int int5 = this.vd.synthesis_pcmout(floatArrayArrayArray, intArray);
					if (int5 > int4) {
						int5 = int4;
					}

					this.vd.synthesis_read(int5);
					this.pcm_offset += (long)int5;
					if (int5 < int4 && this.process_packet(1) == 0) {
						this.pcm_offset = this.pcm_total(-1);
					}
				}

				return 0;
			}
		} else {
			this.pcm_offset = -1L;
			this.decode_clear();
			return -1;
		}
	}

	public long pcm_tell() {
		return this.pcm_offset;
	}

	public long pcm_total(int int1) {
		if (this.seekable && int1 < this.links) {
			if (int1 >= 0) {
				return this.pcmlengths[int1];
			} else {
				long long1 = 0L;
				for (int int2 = 0; int2 < this.links; ++int2) {
					long1 += this.pcm_total(int2);
				}

				return long1;
			}
		} else {
			return -1L;
		}
	}

	public int raw_seek(int int1) {
		if (!this.seekable) {
			return -1;
		} else if (int1 >= 0 && (long)int1 <= this.offsets[this.links]) {
			this.pcm_offset = -1L;
			this.decode_clear();
			this.seek_helper((long)int1);
			switch (this.process_packet(1)) {
			case -1: 
				this.pcm_offset = -1L;
				this.decode_clear();
				return -1;
			
			case 0: 
				this.pcm_offset = this.pcm_total(-1);
				return 0;
			
			default: 
				while (true) {
					switch (this.process_packet(0)) {
					case -1: 
						this.pcm_offset = -1L;
						this.decode_clear();
						return -1;
					
					case 0: 
						return 0;
					
					}
				}

			
			}
		} else {
			this.pcm_offset = -1L;
			this.decode_clear();
			return -1;
		}
	}

	public long raw_tell() {
		return this.offset;
	}

	public long raw_total(int int1) {
		if (this.seekable && int1 < this.links) {
			if (int1 >= 0) {
				return this.offsets[int1 + 1] - this.offsets[int1];
			} else {
				long long1 = 0L;
				for (int int2 = 0; int2 < this.links; ++int2) {
					long1 += this.raw_total(int2);
				}

				return long1;
			}
		} else {
			return -1L;
		}
	}

	public boolean seekable() {
		return this.seekable;
	}

	public int serialnumber(int int1) {
		if (int1 >= this.links) {
			return -1;
		} else if (!this.seekable && int1 >= 0) {
			return this.serialnumber(-1);
		} else {
			return int1 < 0 ? this.current_serialno : this.serialnos[int1];
		}
	}

	public int streams() {
		return this.links;
	}

	public float time_tell() {
		int int1 = -1;
		long long1 = 0L;
		float float1 = 0.0F;
		if (this.seekable) {
			long1 = this.pcm_total(-1);
			float1 = this.time_total(-1);
			for (int1 = this.links - 1; int1 >= 0; --int1) {
				long1 -= this.pcmlengths[int1];
				float1 -= this.time_total(int1);
				if (this.pcm_offset >= long1) {
					break;
				}
			}
		}

		return float1 + (float)(this.pcm_offset - long1) / (float)this.vi[int1].rate;
	}

	public float time_total(int int1) {
		if (this.seekable && int1 < this.links) {
			if (int1 >= 0) {
				return (float)this.pcmlengths[int1] / (float)this.vi[int1].rate;
			} else {
				float float1 = 0.0F;
				for (int int2 = 0; int2 < this.links; ++int2) {
					float1 += this.time_total(int2);
				}

				return float1;
			}
		} else {
			return -1.0F;
		}
	}

	int bisect_forward_serialno(long long1, long long2, long long3, int int1, int int2) {
		long long4 = long3;
		long long5 = long3;
		Page page = new Page();
		int int3;
		while (long2 < long4) {
			long long6;
			if (long4 - long2 < 8500L) {
				long6 = long2;
			} else {
				long6 = (long2 + long4) / 2L;
			}

			this.seek_helper(long6);
			int3 = this.get_next_page(page, -1L);
			if (int3 == -128) {
				return -128;
			}

			if (int3 >= 0 && page.serialno() == int1) {
				long2 = (long)(int3 + page.header_len + page.body_len);
			} else {
				long4 = long6;
				if (int3 >= 0) {
					long5 = (long)int3;
				}
			}
		}

		this.seek_helper(long5);
		int3 = this.get_next_page(page, -1L);
		if (int3 == -128) {
			return -128;
		} else {
			if (long2 < long3 && int3 != -1) {
				int3 = this.bisect_forward_serialno(long5, this.offset, long3, page.serialno(), int2 + 1);
				if (int3 == -128) {
					return -128;
				}
			} else {
				this.links = int2 + 1;
				this.offsets = new long[int2 + 2];
				this.offsets[int2 + 1] = long2;
			}

			this.offsets[int2] = long1;
			return 0;
		}
	}

	int clear() {
		this.vb.clear();
		this.vd.clear();
		this.os.clear();
		if (this.vi != null && this.links != 0) {
			for (int int1 = 0; int1 < this.links; ++int1) {
				this.vi[int1].clear();
				this.vc[int1].clear();
			}

			this.vi = null;
			this.vc = null;
		}

		if (this.dataoffsets != null) {
			this.dataoffsets = null;
		}

		if (this.pcmlengths != null) {
			this.pcmlengths = null;
		}

		if (this.serialnos != null) {
			this.serialnos = null;
		}

		if (this.offsets != null) {
			this.offsets = null;
		}

		this.oy.clear();
		return 0;
	}

	void decode_clear() {
		this.os.clear();
		this.vd.clear();
		this.vb.clear();
		this.decode_ready = false;
		this.bittrack = 0.0F;
		this.samptrack = 0.0F;
	}

	int fetch_headers(Info info, Comment comment, int[] intArray, Page page) {
		Page page2 = new Page();
		Packet packet = new Packet();
		if (page == null) {
			int int1 = this.get_next_page(page2, 8500L);
			if (int1 == -128) {
				return -128;
			}

			if (int1 < 0) {
				return -132;
			}

			page = page2;
		}

		if (intArray != null) {
			intArray[0] = page.serialno();
		}

		this.os.init(page.serialno());
		info.init();
		comment.init();
		int int2 = 0;
		do {
			if (int2 >= 3) {
				return 0;
			}

			this.os.pagein(page);
			while (int2 < 3) {
				int int3 = this.os.packetout(packet);
				if (int3 == 0) {
					break;
				}

				if (int3 == -1) {
					info.clear();
					comment.clear();
					this.os.clear();
					return -1;
				}

				if (info.synthesis_headerin(comment, packet) != 0) {
					info.clear();
					comment.clear();
					this.os.clear();
					return -1;
				}

				++int2;
			}
		} while (int2 >= 3 || this.get_next_page(page, 1L) >= 0);

		info.clear();
		comment.clear();
		this.os.clear();
		return -1;
	}

	int host_is_big_endian() {
		return 1;
	}

	int open(InputStream inputStream, byte[] byteArray, int int1) throws JOrbisException {
		return this.open_callbacks(inputStream, byteArray, int1);
	}

	int open_callbacks(InputStream inputStream, byte[] byteArray, int int1) throws JOrbisException {
		this.datasource = inputStream;
		this.oy.init();
		if (byteArray != null) {
			int int2 = this.oy.buffer(int1);
			System.arraycopy(byteArray, 0, this.oy.data, int2, int1);
			this.oy.wrote(int1);
		}

		int int3;
		if (inputStream instanceof VorbisFile.SeekableInputStream) {
			int3 = this.open_seekable();
		} else {
			int3 = this.open_nonseekable();
		}

		if (int3 != 0) {
			this.datasource = null;
			this.clear();
		}

		return int3;
	}

	int open_nonseekable() {
		this.links = 1;
		this.vi = new Info[this.links];
		this.vi[0] = new Info();
		this.vc = new Comment[this.links];
		this.vc[0] = new Comment();
		int[] intArray = new int[1];
		if (this.fetch_headers(this.vi[0], this.vc[0], intArray, (Page)null) == -1) {
			return -1;
		} else {
			this.current_serialno = intArray[0];
			this.make_decode_ready();
			return 0;
		}
	}

	int open_seekable() throws JOrbisException {
		Info info = new Info();
		Comment comment = new Comment();
		Page page = new Page();
		int[] intArray = new int[1];
		int int1 = this.fetch_headers(info, comment, intArray, (Page)null);
		int int2 = intArray[0];
		int int3 = (int)this.offset;
		this.os.clear();
		if (int1 == -1) {
			return -1;
		} else if (int1 < 0) {
			return int1;
		} else {
			this.seekable = true;
			fseek(this.datasource, 0L, 2);
			this.offset = ftell(this.datasource);
			long long1 = this.offset;
			long1 = (long)this.get_prev_page(page);
			if (page.serialno() != int2) {
				if (this.bisect_forward_serialno(0L, 0L, long1 + 1L, int2, 0) < 0) {
					this.clear();
					return -128;
				}
			} else if (this.bisect_forward_serialno(0L, long1, long1 + 1L, int2, 0) < 0) {
				this.clear();
				return -128;
			}

			this.prefetch_all_headers(info, comment, int3);
			return 0;
		}
	}

	void prefetch_all_headers(Info info, Comment comment, int int1) throws JOrbisException {
		Page page = new Page();
		this.vi = new Info[this.links];
		this.vc = new Comment[this.links];
		this.dataoffsets = new long[this.links];
		this.pcmlengths = new long[this.links];
		this.serialnos = new int[this.links];
		label38: for (int int2 = 0; int2 < this.links; ++int2) {
			if (info != null && comment != null && int2 == 0) {
				this.vi[int2] = info;
				this.vc[int2] = comment;
				this.dataoffsets[int2] = (long)int1;
			} else {
				this.seek_helper(this.offsets[int2]);
				this.vi[int2] = new Info();
				this.vc[int2] = new Comment();
				if (this.fetch_headers(this.vi[int2], this.vc[int2], (int[])null, (Page)null) == -1) {
					this.dataoffsets[int2] = -1L;
				} else {
					this.dataoffsets[int2] = this.offset;
					this.os.clear();
				}
			}

			long long1 = this.offsets[int2 + 1];
			this.seek_helper(long1);
			do {
				int int3 = this.get_prev_page(page);
				if (int3 == -1) {
					this.vi[int2].clear();
					this.vc[int2].clear();
					continue label38;
				}
			}	 while (page.granulepos() == -1L);

			this.serialnos[int2] = page.serialno();
			this.pcmlengths[int2] = page.granulepos();
		}
	}

	int process_packet(int int1) {
		Page page = new Page();
		while (true) {
			if (this.decode_ready) {
				Packet packet = new Packet();
				int int2 = this.os.packetout(packet);
				if (int2 > 0) {
					long long1 = packet.granulepos;
					if (this.vb.synthesis(packet) == 0) {
						int int3 = this.vd.synthesis_pcmout((float[][][])null, (int[])null);
						this.vd.synthesis_blockin(this.vb);
						this.samptrack += (float)(this.vd.synthesis_pcmout((float[][][])null, (int[])null) - int3);
						this.bittrack += (float)(packet.bytes * 8);
						if (long1 != -1L && packet.e_o_s == 0) {
							int3 = this.seekable ? this.current_link : 0;
							int int4 = this.vd.synthesis_pcmout((float[][][])null, (int[])null);
							long1 -= (long)int4;
							for (int int5 = 0; int5 < int3; ++int5) {
								long1 += this.pcmlengths[int5];
							}

							this.pcm_offset = long1;
						}

						return 1;
					}
				}
			}

			if (int1 == 0) {
				return 0;
			}

			if (this.get_next_page(page, -1L) < 0) {
				return 0;
			}

			this.bittrack += (float)(page.header_len * 8);
			if (this.decode_ready && this.current_serialno != page.serialno()) {
				this.decode_clear();
			}

			if (!this.decode_ready) {
				if (!this.seekable) {
					int[] intArray = new int[1];
					int int6 = this.fetch_headers(this.vi[0], this.vc[0], intArray, page);
					this.current_serialno = intArray[0];
					if (int6 != 0) {
						return int6;
					}

					++this.current_link;
					boolean boolean1 = false;
				} else {
					this.current_serialno = page.serialno();
					int int7;
					for (int7 = 0; int7 < this.links && this.serialnos[int7] != this.current_serialno; ++int7) {
					}

					if (int7 == this.links) {
						return -1;
					}

					this.current_link = int7;
					this.os.init(this.current_serialno);
					this.os.reset();
				}

				this.make_decode_ready();
			}

			this.os.pagein(page);
		}
	}

	int read(byte[] byteArray, int int1, int int2, int int3, int int4, int[] intArray) {
		int int5 = this.host_is_big_endian();
		int int6 = 0;
		while (true) {
			if (this.decode_ready) {
				float[][][] floatArrayArrayArray = new float[1][][];
				int[] intArray2 = new int[this.getInfo(-1).channels];
				int int7 = this.vd.synthesis_pcmout(floatArrayArrayArray, intArray2);
				float[][] floatArrayArray = floatArrayArrayArray[0];
				if (int7 != 0) {
					int int8 = this.getInfo(-1).channels;
					int int9 = int3 * int8;
					if (int7 > int1 / int9) {
						int7 = int1 / int9;
					}

					int int10;
					int int11;
					int int12;
					int int13;
					if (int3 == 1) {
						int11 = int4 != 0 ? 0 : 128;
						for (int12 = 0; int12 < int7; ++int12) {
							for (int13 = 0; int13 < int8; ++int13) {
								int10 = (int)((double)floatArrayArray[int13][intArray2[int13] + int12] * 128.0 + 0.5);
								if (int10 > 127) {
									int10 = 127;
								} else if (int10 < -128) {
									int10 = -128;
								}

								byteArray[int6++] = (byte)(int10 + int11);
							}
						}
					} else {
						int11 = int4 != 0 ? 0 : '耀';
						if (int5 == int2) {
							int int14;
							int int15;
							if (int4 != 0) {
								for (int12 = 0; int12 < int8; ++int12) {
									int13 = intArray2[int12];
									int14 = int12;
									for (int15 = 0; int15 < int7; ++int15) {
										int10 = (int)((double)floatArrayArray[int12][int13 + int15] * 32768.0 + 0.5);
										if (int10 > 32767) {
											int10 = 32767;
										} else if (int10 < -32768) {
											int10 = -32768;
										}

										byteArray[int14] = (byte)(int10 >>> 8);
										byteArray[int14 + 1] = (byte)int10;
										int14 += int8 * 2;
									}
								}
							} else {
								for (int12 = 0; int12 < int8; ++int12) {
									float[] floatArray = floatArrayArray[int12];
									int14 = int12;
									for (int15 = 0; int15 < int7; ++int15) {
										int10 = (int)((double)floatArray[int15] * 32768.0 + 0.5);
										if (int10 > 32767) {
											int10 = 32767;
										} else if (int10 < -32768) {
											int10 = -32768;
										}

										byteArray[int14] = (byte)(int10 + int11 >>> 8);
										byteArray[int14 + 1] = (byte)(int10 + int11);
										int14 += int8 * 2;
									}
								}
							}
						} else if (int2 != 0) {
							for (int12 = 0; int12 < int7; ++int12) {
								for (int13 = 0; int13 < int8; ++int13) {
									int10 = (int)((double)floatArrayArray[int13][int12] * 32768.0 + 0.5);
									if (int10 > 32767) {
										int10 = 32767;
									} else if (int10 < -32768) {
										int10 = -32768;
									}

									int10 += int11;
									byteArray[int6++] = (byte)(int10 >>> 8);
									byteArray[int6++] = (byte)int10;
								}
							}
						} else {
							for (int12 = 0; int12 < int7; ++int12) {
								for (int13 = 0; int13 < int8; ++int13) {
									int10 = (int)((double)floatArrayArray[int13][int12] * 32768.0 + 0.5);
									if (int10 > 32767) {
										int10 = 32767;
									} else if (int10 < -32768) {
										int10 = -32768;
									}

									int10 += int11;
									byteArray[int6++] = (byte)int10;
									byteArray[int6++] = (byte)(int10 >>> 8);
								}
							}
						}
					}

					this.vd.synthesis_read(int7);
					this.pcm_offset += (long)int7;
					if (intArray != null) {
						intArray[0] = this.current_link;
					}

					return int7 * int9;
				}
			}

			switch (this.process_packet(1)) {
			case -1: 
				return -1;
			
			case 0: 
				return 0;
			
			}
		}
	}

	int time_seek(float float1) {
		boolean boolean1 = true;
		long long1 = this.pcm_total(-1);
		float float2 = this.time_total(-1);
		if (!this.seekable) {
			return -1;
		} else if (!(float1 < 0.0F) && !(float1 > float2)) {
			int int1;
			for (int1 = this.links - 1; int1 >= 0; --int1) {
				long1 -= this.pcmlengths[int1];
				float2 -= this.time_total(int1);
				if (float1 >= float2) {
					break;
				}
			}

			long long2 = (long)((float)long1 + (float1 - float2) * (float)this.vi[int1].rate);
			return this.pcm_seek(long2);
		} else {
			this.pcm_offset = -1L;
			this.decode_clear();
			return -1;
		}
	}

	private int get_data() {
		int int1 = this.oy.buffer(8500);
		byte[] byteArray = this.oy.data;
		boolean boolean1 = false;
		int int2;
		try {
			int2 = this.datasource.read(byteArray, int1, 8500);
		} catch (Exception exception) {
			return -128;
		}

		this.oy.wrote(int2);
		if (int2 == -1) {
			int2 = 0;
		}

		return int2;
	}

	private int get_next_page(Page page, long long1) {
		if (long1 > 0L) {
			long1 += this.offset;
		}

		while (long1 <= 0L || this.offset < long1) {
			int int1 = this.oy.pageseek(page);
			if (int1 < 0) {
				this.offset -= (long)int1;
			} else {
				int int2;
				if (int1 != 0) {
					int2 = (int)this.offset;
					this.offset += (long)int1;
					return int2;
				}

				if (long1 == 0L) {
					return -1;
				}

				int2 = this.get_data();
				if (int2 == 0) {
					return -2;
				}

				if (int2 < 0) {
					return -128;
				}
			}
		}

		return -1;
	}

	private int get_prev_page(Page page) throws JOrbisException {
		long long1 = this.offset;
		int int1 = -1;
		label37: do {
			int int2;
			while (int1 == -1) {
				long1 -= 8500L;
				if (long1 < 0L) {
					long1 = 0L;
				}

				this.seek_helper(long1);
				while (this.offset < long1 + 8500L) {
					int2 = this.get_next_page(page, long1 + 8500L - this.offset);
					if (int2 == -128) {
						return -128;
					}

					if (int2 < 0) {
						continue label37;
					}

					int1 = int2;
				}
			}

			this.seek_helper((long)int1);
			int2 = this.get_next_page(page, 8500L);
			if (int2 < 0) {
				return -129;
			}

			return int1;
		} while (int1 != -1);
		throw new JOrbisException();
	}

	private int make_decode_ready() {
		if (this.decode_ready) {
			System.exit(1);
		}

		this.vd.synthesis_init(this.vi[0]);
		this.vb.init(this.vd);
		this.decode_ready = true;
		return 0;
	}

	private void seek_helper(long long1) {
		fseek(this.datasource, long1, 0);
		this.offset = long1;
		this.oy.reset();
	}

	class SeekableInputStream extends InputStream {
		final String mode = "r";
		RandomAccessFile raf = null;

		SeekableInputStream(String string) throws IOException {
			this.raf = new RandomAccessFile(string, "r");
		}

		public int available() throws IOException {
			return this.raf.length() == this.raf.getFilePointer() ? 0 : 1;
		}

		public void close() throws IOException {
			this.raf.close();
		}

		public long getLength() throws IOException {
			return this.raf.length();
		}

		public synchronized void mark(int int1) {
		}

		public boolean markSupported() {
			return false;
		}

		public int read() throws IOException {
			return this.raf.read();
		}

		public int read(byte[] byteArray) throws IOException {
			return this.raf.read(byteArray);
		}

		public int read(byte[] byteArray, int int1, int int2) throws IOException {
			return this.raf.read(byteArray, int1, int2);
		}

		public synchronized void reset() throws IOException {
		}

		public void seek(long long1) throws IOException {
			this.raf.seek(long1);
		}

		public long skip(long long1) throws IOException {
			return (long)this.raf.skipBytes((int)long1);
		}

		public long tell() throws IOException {
			return this.raf.getFilePointer();
		}
	}
}
