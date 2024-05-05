package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


class StaticCodeBook {
	static final int VQ_FEXP = 10;
	static final int VQ_FMAN = 21;
	static final int VQ_FEXP_BIAS = 768;
	int dim;
	int entries;
	int[] lengthlist;
	int maptype;
	int q_delta;
	int q_min;
	int q_quant;
	int q_sequencep;
	int[] quantlist;

	static long float32_pack(float float1) {
		int int1 = 0;
		if (float1 < 0.0F) {
			int1 = Integer.MIN_VALUE;
			float1 = -float1;
		}

		int int2 = (int)Math.floor(Math.log((double)float1) / Math.log(2.0));
		int int3 = (int)Math.rint(Math.pow((double)float1, (double)(20 - int2)));
		int2 = int2 + 768 << 21;
		return (long)(int1 | int2 | int3);
	}

	static float float32_unpack(int int1) {
		float float1 = (float)(int1 & 2097151);
		float float2 = (float)((int1 & 2145386496) >>> 21);
		if ((int1 & Integer.MIN_VALUE) != 0) {
			float1 = -float1;
		}

		return ldexp(float1, (int)float2 - 20 - 768);
	}

	static float ldexp(float float1, int int1) {
		return (float)((double)float1 * Math.pow(2.0, (double)int1));
	}

	void clear() {
	}

	int pack(Buffer buffer) {
		boolean boolean1 = false;
		buffer.write(5653314, 24);
		buffer.write(this.dim, 16);
		buffer.write(this.entries, 24);
		int int1;
		for (int1 = 1; int1 < this.entries && this.lengthlist[int1] >= this.lengthlist[int1 - 1]; ++int1) {
		}

		if (int1 == this.entries) {
			boolean1 = true;
		}

		int int2;
		if (boolean1) {
			int2 = 0;
			buffer.write(1, 1);
			buffer.write(this.lengthlist[0] - 1, 5);
			for (int1 = 1; int1 < this.entries; ++int1) {
				int int3 = this.lengthlist[int1];
				int int4 = this.lengthlist[int1 - 1];
				if (int3 > int4) {
					for (int int5 = int4; int5 < int3; ++int5) {
						buffer.write(int1 - int2, Util.ilog(this.entries - int2));
						int2 = int1;
					}
				}
			}

			buffer.write(int1 - int2, Util.ilog(this.entries - int2));
		} else {
			buffer.write(0, 1);
			for (int1 = 0; int1 < this.entries && this.lengthlist[int1] != 0; ++int1) {
			}

			if (int1 == this.entries) {
				buffer.write(0, 1);
				for (int1 = 0; int1 < this.entries; ++int1) {
					buffer.write(this.lengthlist[int1] - 1, 5);
				}
			} else {
				buffer.write(1, 1);
				for (int1 = 0; int1 < this.entries; ++int1) {
					if (this.lengthlist[int1] == 0) {
						buffer.write(0, 1);
					} else {
						buffer.write(1, 1);
						buffer.write(this.lengthlist[int1] - 1, 5);
					}
				}
			}
		}

		buffer.write(this.maptype, 4);
		switch (this.maptype) {
		case 1: 
		
		case 2: 
			if (this.quantlist == null) {
				return -1;
			} else {
				buffer.write(this.q_min, 32);
				buffer.write(this.q_delta, 32);
				buffer.write(this.q_quant - 1, 4);
				buffer.write(this.q_sequencep, 1);
				int2 = 0;
				switch (this.maptype) {
				case 1: 
					int2 = this.maptype1_quantvals();
					break;
				
				case 2: 
					int2 = this.entries * this.dim;
				
				}

				for (int1 = 0; int1 < int2; ++int1) {
					buffer.write(Math.abs(this.quantlist[int1]), this.q_quant);
				}
			}

		
		case 0: 
			return 0;
		
		default: 
			return -1;
		
		}
	}

	int unpack(Buffer buffer) {
		if (buffer.read(24) != 5653314) {
			this.clear();
			return -1;
		} else {
			this.dim = buffer.read(16);
			this.entries = buffer.read(24);
			if (this.entries == -1) {
				this.clear();
				return -1;
			} else {
				int int1;
				int int2;
				label89: switch (buffer.read(1)) {
				case 0: 
					this.lengthlist = new int[this.entries];
					if (buffer.read(1) != 0) {
						int1 = 0;
						while (true) {
							if (int1 >= this.entries) {
								break label89;
							}

							if (buffer.read(1) != 0) {
								int2 = buffer.read(5);
								if (int2 == -1) {
									this.clear();
									return -1;
								}

								this.lengthlist[int1] = int2 + 1;
							} else {
								this.lengthlist[int1] = 0;
							}

							++int1;
						}
					} else {
						int1 = 0;
						while (true) {
							if (int1 >= this.entries) {
								break label89;
							}

							int2 = buffer.read(5);
							if (int2 == -1) {
								this.clear();
								return -1;
							}

							this.lengthlist[int1] = int2 + 1;
							++int1;
						}
					}

				
				case 1: 
					int2 = buffer.read(5) + 1;
					this.lengthlist = new int[this.entries];
					int1 = 0;
					while (true) {
						if (int1 >= this.entries) {
							break label89;
						}

						int int3 = buffer.read(Util.ilog(this.entries - int1));
						if (int3 == -1) {
							this.clear();
							return -1;
						}

						for (int int4 = 0; int4 < int3; ++int1) {
							this.lengthlist[int1] = int2;
							++int4;
						}

						++int2;
					}

				
				default: 
					return -1;
				
				}

				switch (this.maptype = buffer.read(4)) {
				case 1: 
				
				case 2: 
					this.q_min = buffer.read(32);
					this.q_delta = buffer.read(32);
					this.q_quant = buffer.read(4) + 1;
					this.q_sequencep = buffer.read(1);
					int2 = 0;
					switch (this.maptype) {
					case 1: 
						int2 = this.maptype1_quantvals();
						break;
					
					case 2: 
						int2 = this.entries * this.dim;
					
					}

					this.quantlist = new int[int2];
					for (int1 = 0; int1 < int2; ++int1) {
						this.quantlist[int1] = buffer.read(this.q_quant);
					}

					if (this.quantlist[int2 - 1] == -1) {
						this.clear();
						return -1;
					}

				
				case 0: 
					return 0;
				
				default: 
					this.clear();
					return -1;
				
				}
			}
		}
	}

	float[] unquantize() {
		if (this.maptype != 1 && this.maptype != 2) {
			return null;
		} else {
			float float1 = float32_unpack(this.q_min);
			float float2 = float32_unpack(this.q_delta);
			float[] floatArray = new float[this.entries * this.dim];
			int int1;
			float float3;
			int int2;
			switch (this.maptype) {
			case 1: 
				int int3 = this.maptype1_quantvals();
				for (int1 = 0; int1 < this.entries; ++int1) {
					float3 = 0.0F;
					int2 = 1;
					for (int int4 = 0; int4 < this.dim; ++int4) {
						int int5 = int1 / int2 % int3;
						float float4 = (float)this.quantlist[int5];
						float4 = Math.abs(float4) * float2 + float1 + float3;
						if (this.q_sequencep != 0) {
							float3 = float4;
						}

						floatArray[int1 * this.dim + int4] = float4;
						int2 *= int3;
					}
				}

				return floatArray;
			
			case 2: 
				for (int1 = 0; int1 < this.entries; ++int1) {
					float3 = 0.0F;
					for (int2 = 0; int2 < this.dim; ++int2) {
						float float5 = (float)this.quantlist[int1 * this.dim + int2];
						float5 = Math.abs(float5) * float2 + float1 + float3;
						if (this.q_sequencep != 0) {
							float3 = float5;
						}

						floatArray[int1 * this.dim + int2] = float5;
					}
				}

			
			}

			return floatArray;
		}
	}

	private int maptype1_quantvals() {
		int int1 = (int)Math.floor(Math.pow((double)this.entries, 1.0 / (double)this.dim));
		while (true) {
			int int2 = 1;
			int int3 = 1;
			for (int int4 = 0; int4 < this.dim; ++int4) {
				int2 *= int1;
				int3 *= int1 + 1;
			}

			if (int2 <= this.entries && int3 > this.entries) {
				return int1;
			}

			if (int2 > this.entries) {
				--int1;
			} else {
				++int1;
			}
		}
	}
}
