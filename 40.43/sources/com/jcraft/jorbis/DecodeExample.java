package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import java.io.FileInputStream;
import java.io.InputStream;


class DecodeExample {
	static int convsize = 8192;
	static byte[] convbuffer;

	public static void main(String[] stringArray) {
		Object object = System.in;
		if (stringArray.length > 0) {
			try {
				object = new FileInputStream(stringArray[0]);
			} catch (Exception exception) {
				System.err.println(exception);
			}
		}

		SyncState syncState = new SyncState();
		StreamState streamState = new StreamState();
		Page page = new Page();
		Packet packet = new Packet();
		Info info = new Info();
		Comment comment = new Comment();
		DspState dspState = new DspState();
		Block block = new Block(dspState);
		int int1 = 0;
		syncState.init();
		while (true) {
			boolean boolean1 = false;
			int int2 = syncState.buffer(4096);
			byte[] byteArray = syncState.data;
			try {
				int1 = ((InputStream)object).read(byteArray, int2, 4096);
			} catch (Exception exception2) {
				System.err.println(exception2);
				System.exit(-1);
			}

			syncState.wrote(int1);
			if (syncState.pageout(page) != 1) {
				if (int1 < 4096) {
					syncState.clear();
					System.err.println("Done.");
					return;
				}

				System.err.println("Input does not appear to be an Ogg bitstream.");
				System.exit(1);
			}

			streamState.init(page.serialno());
			info.init();
			comment.init();
			if (streamState.pagein(page) < 0) {
				System.err.println("Error reading first page of Ogg bitstream data.");
				System.exit(1);
			}

			if (streamState.packetout(packet) != 1) {
				System.err.println("Error reading initial header packet.");
				System.exit(1);
			}

			if (info.synthesis_headerin(comment, packet) < 0) {
				System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
				System.exit(1);
			}

			int int3;
			for (int3 = 0; int3 < 2; syncState.wrote(int1)) {
				label156: while (true) {
					int int4;
					do {
						if (int3 >= 2) {
							break label156;
						}

						int4 = syncState.pageout(page);
						if (int4 == 0) {
							break label156;
						}
					}			 while (int4 != 1);

					streamState.pagein(page);
					while (int3 < 2) {
						int4 = streamState.packetout(packet);
						if (int4 == 0) {
							break;
						}

						if (int4 == -1) {
							System.err.println("Corrupt secondary header.  Exiting.");
							System.exit(1);
						}

						info.synthesis_headerin(comment, packet);
						++int3;
					}
				}

				int2 = syncState.buffer(4096);
				byteArray = syncState.data;
				try {
					int1 = ((InputStream)object).read(byteArray, int2, 4096);
				} catch (Exception exception3) {
					System.err.println(exception3);
					System.exit(1);
				}

				if (int1 == 0 && int3 < 2) {
					System.err.println("End of file before finding all Vorbis headers!");
					System.exit(1);
				}
			}

			byte[][] byteArrayArray = comment.user_comments;
			for (int int5 = 0; int5 < byteArrayArray.length && byteArrayArray[int5] != null; ++int5) {
				System.err.println(new String(byteArrayArray[int5], 0, byteArrayArray[int5].length - 1));
			}

			System.err.println("\nBitstream is " + info.channels + " channel, " + info.rate + "Hz");
			System.err.println("Encoded by: " + new String(comment.vendor, 0, comment.vendor.length - 1) + "\n");
			convsize = 4096 / info.channels;
			dspState.synthesis_init(info);
			block.init(dspState);
			float[][][] floatArrayArrayArray = new float[1][][];
			int[] intArray = new int[info.channels];
			while (!boolean1) {
				label207: while (true) {
					label205: while (true) {
						if (boolean1) {
							break label207;
						}

						int int6 = syncState.pageout(page);
						if (int6 == 0) {
							break label207;
						}

						if (int6 == -1) {
							System.err.println("Corrupt or missing data in bitstream; continuing...");
						} else {
							streamState.pagein(page);
							while (true) {
								do {
									int6 = streamState.packetout(packet);
									if (int6 == 0) {
										if (page.eos() != 0) {
											boolean1 = true;
										}

										continue label205;
									}
								}						 while (int6 == -1);

								if (block.synthesis(packet) == 0) {
									dspState.synthesis_blockin(block);
								}

								int int7;
								while ((int7 = dspState.synthesis_pcmout(floatArrayArrayArray, intArray)) > 0) {
									float[][] floatArrayArray = floatArrayArrayArray[0];
									int int8 = int7 < convsize ? int7 : convsize;
									for (int3 = 0; int3 < info.channels; ++int3) {
										int int9 = int3 * 2;
										int int10 = intArray[int3];
										for (int int11 = 0; int11 < int8; ++int11) {
											int int12 = (int)((double)floatArrayArray[int3][int10 + int11] * 32767.0);
											if (int12 > 32767) {
												int12 = 32767;
											}

											if (int12 < -32768) {
												int12 = -32768;
											}

											if (int12 < 0) {
												int12 |= 32768;
											}

											convbuffer[int9] = (byte)int12;
											convbuffer[int9 + 1] = (byte)(int12 >>> 8);
											int9 += 2 * info.channels;
										}
									}

									System.out.write(convbuffer, 0, 2 * info.channels * int8);
									dspState.synthesis_read(int8);
								}
							}
						}
					}
				}

				if (!boolean1) {
					int2 = syncState.buffer(4096);
					byteArray = syncState.data;
					try {
						int1 = ((InputStream)object).read(byteArray, int2, 4096);
					} catch (Exception exception4) {
						System.err.println(exception4);
						System.exit(1);
					}

					syncState.wrote(int1);
					if (int1 == 0) {
						boolean1 = true;
					}
				}
			}

			streamState.clear();
			block.clear();
			dspState.clear();
			info.clear();
		}
	}

	static  {
		convbuffer = new byte[convsize];
	}
}
