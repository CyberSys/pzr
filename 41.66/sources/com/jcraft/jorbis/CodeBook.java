package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;


class CodeBook {
	StaticCodeBook c = new StaticCodeBook();
	int[] codelist;
	CodeBook.DecodeAux decode_tree;
	int dim;
	int entries;
	float[] valuelist;
	private int[] t = new int[15];

	static int[] make_words(int[] intArray, int int1) {
		int[] intArray2 = new int[33];
		int[] intArray3 = new int[int1];
		int int2;
		int int3;
		int int4;
		for (int2 = 0; int2 < int1; ++int2) {
			int3 = intArray[int2];
			if (int3 > 0) {
				int4 = intArray2[int3];
				if (int3 < 32 && int4 >>> int3 != 0) {
					return null;
				}

				intArray3[int2] = int4;
				int int5;
				for (int5 = int3; int5 > 0; --int5) {
					int int6;
					if ((intArray2[int5] & 1) != 0) {
						if (int5 == 1) {
							int6 = intArray2[1]++;
						} else {
							intArray2[int5] = intArray2[int5 - 1] << 1;
						}

						break;
					}

					int6 = intArray2[int5]++;
				}

				for (int5 = int3 + 1; int5 < 33 && intArray2[int5] >>> 1 == int4; ++int5) {
					int4 = intArray2[int5];
					intArray2[int5] = intArray2[int5 - 1] << 1;
				}
			}
		}

		for (int2 = 0; int2 < int1; ++int2) {
			int3 = 0;
			for (int4 = 0; int4 < intArray[int2]; ++int4) {
				int3 <<= 1;
				int3 |= intArray3[int2] >>> int4 & 1;
			}

			intArray3[int2] = int3;
		}

		return intArray3;
	}

	private static float dist(int int1, float[] floatArray, int int2, float[] floatArray2, int int3) {
		float float1 = 0.0F;
		for (int int4 = 0; int4 < int1; ++int4) {
			float float2 = floatArray[int2 + int4] - floatArray2[int4 * int3];
			float1 += float2 * float2;
		}

		return float1;
	}

	int best(float[] floatArray, int int1) {
		int int2 = -1;
		float float1 = 0.0F;
		int int3 = 0;
		for (int int4 = 0; int4 < this.entries; ++int4) {
			if (this.c.lengthlist[int4] > 0) {
				float float2 = dist(this.dim, this.valuelist, int3, floatArray, int1);
				if (int2 == -1 || float2 < float1) {
					float1 = float2;
					int2 = int4;
				}
			}

			int3 += this.dim;
		}

		return int2;
	}

	int besterror(float[] floatArray, int int1, int int2) {
		int int3 = this.best(floatArray, int1);
		int int4;
		int int5;
		switch (int2) {
		case 0: 
			int4 = 0;
			for (int5 = 0; int4 < this.dim; int5 += int1) {
				floatArray[int5] -= this.valuelist[int3 * this.dim + int4];
				++int4;
			}

			return int3;
		
		case 1: 
			int4 = 0;
			for (int5 = 0; int4 < this.dim; int5 += int1) {
				float float1 = this.valuelist[int3 * this.dim + int4];
				if (float1 == 0.0F) {
					floatArray[int5] = 0.0F;
				} else {
					floatArray[int5] /= float1;
				}

				++int4;
			}

		
		}
		return int3;
	}

	void clear() {
	}

	int decode(Buffer buffer) {
		int int1 = 0;
		CodeBook.DecodeAux decodeAux = this.decode_tree;
		int int2 = buffer.look(decodeAux.tabn);
		if (int2 >= 0) {
			int1 = decodeAux.tab[int2];
			buffer.adv(decodeAux.tabl[int2]);
			if (int1 <= 0) {
				return -int1;
			}
		}

		do {
			switch (buffer.read1()) {
			case -1: 
			
			default: 
				return -1;
			
			case 0: 
				int1 = decodeAux.ptr0[int1];
				break;
			
			case 1: 
				int1 = decodeAux.ptr1[int1];
			
			}
		} while (int1 > 0);

		return -int1;
	}

	int decodev_add(float[] floatArray, int int1, Buffer buffer, int int2) {
		int int3;
		int int4;
		int int5;
		int int6;
		int int7;
		if (this.dim > 8) {
			int4 = 0;
			while (int4 < int2) {
				int6 = this.decode(buffer);
				if (int6 == -1) {
					return -1;
				}

				int7 = int6 * this.dim;
				for (int5 = 0; int5 < this.dim; floatArray[int1 + int3] += this.valuelist[int7 + int5++]) {
					int3 = int4++;
				}
			}
		} else {
			int4 = 0;
			while (int4 < int2) {
				int6 = this.decode(buffer);
				if (int6 == -1) {
					return -1;
				}

				int7 = int6 * this.dim;
				int5 = 0;
				switch (this.dim) {
				case 0: 
				
				default: 
					break;
				
				case 8: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 7: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 6: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 5: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 4: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 3: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 2: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				case 1: 
					int3 = int4++;
					floatArray[int1 + int3] += this.valuelist[int7 + int5++];
				
				}
			}
		}

		return 0;
	}

	int decodev_set(float[] floatArray, int int1, Buffer buffer, int int2) {
		int int3 = 0;
		while (int3 < int2) {
			int int4 = this.decode(buffer);
			if (int4 == -1) {
				return -1;
			}

			int int5 = int4 * this.dim;
			for (int int6 = 0; int6 < this.dim; floatArray[int1 + int3++] = this.valuelist[int5 + int6++]) {
			}
		}

		return 0;
	}

	int decodevs(float[] floatArray, int int1, Buffer buffer, int int2, int int3) {
		int int4 = this.decode(buffer);
		if (int4 == -1) {
			return -1;
		} else {
			int int5;
			int int6;
			switch (int3) {
			case -1: 
				int5 = 0;
				for (int6 = 0; int5 < this.dim; int6 += int2) {
					floatArray[int1 + int6] = this.valuelist[int4 * this.dim + int5];
					++int5;
				}

				return int4;
			
			case 0: 
				int5 = 0;
				for (int6 = 0; int5 < this.dim; int6 += int2) {
					floatArray[int1 + int6] += this.valuelist[int4 * this.dim + int5];
					++int5;
				}

				return int4;
			
			case 1: 
				int5 = 0;
				for (int6 = 0; int5 < this.dim; int6 += int2) {
					floatArray[int1 + int6] *= this.valuelist[int4 * this.dim + int5];
					++int5;
				}

			
			}

			return int4;
		}
	}

	synchronized int decodevs_add(float[] floatArray, int int1, Buffer buffer, int int2) {
		int int3 = int2 / this.dim;
		if (this.t.length < int3) {
			this.t = new int[int3];
		}

		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			int int5 = this.decode(buffer);
			if (int5 == -1) {
				return -1;
			}

			this.t[int4] = int5 * this.dim;
		}

		int4 = 0;
		for (int int6 = 0; int4 < this.dim; int6 += int3) {
			for (int int7 = 0; int7 < int3; ++int7) {
				floatArray[int1 + int6 + int7] += this.valuelist[this.t[int7] + int4];
			}

			++int4;
		}

		return 0;
	}

	int decodevv_add(float[][] floatArrayArray, int int1, int int2, Buffer buffer, int int3) {
		int int4 = 0;
		int int5 = int1 / int2;
		while (int5 < (int1 + int3) / int2) {
			int int6 = this.decode(buffer);
			if (int6 == -1) {
				return -1;
			}

			int int7 = int6 * this.dim;
			for (int int8 = 0; int8 < this.dim; ++int8) {
				int int9 = int4++;
				floatArrayArray[int9][int5] += this.valuelist[int7 + int8];
				if (int4 == int2) {
					int4 = 0;
					++int5;
				}
			}
		}

		return 0;
	}

	int encode(int int1, Buffer buffer) {
		buffer.write(this.codelist[int1], this.c.lengthlist[int1]);
		return this.c.lengthlist[int1];
	}

	int encodev(int int1, float[] floatArray, Buffer buffer) {
		for (int int2 = 0; int2 < this.dim; ++int2) {
			floatArray[int2] = this.valuelist[int1 * this.dim + int2];
		}

		return this.encode(int1, buffer);
	}

	int encodevs(float[] floatArray, Buffer buffer, int int1, int int2) {
		int int3 = this.besterror(floatArray, int1, int2);
		return this.encode(int3, buffer);
	}

	int errorv(float[] floatArray) {
		int int1 = this.best(floatArray, 1);
		for (int int2 = 0; int2 < this.dim; ++int2) {
			floatArray[int2] = this.valuelist[int1 * this.dim + int2];
		}

		return int1;
	}

	int init_decode(StaticCodeBook staticCodeBook) {
		this.c = staticCodeBook;
		this.entries = staticCodeBook.entries;
		this.dim = staticCodeBook.dim;
		this.valuelist = staticCodeBook.unquantize();
		this.decode_tree = this.make_decode_tree();
		if (this.decode_tree == null) {
			this.clear();
			return -1;
		} else {
			return 0;
		}
	}

	CodeBook.DecodeAux make_decode_tree() {
		int int1 = 0;
		CodeBook.DecodeAux decodeAux = new CodeBook.DecodeAux();
		int[] intArray = decodeAux.ptr0 = new int[this.entries * 2];
		int[] intArray2 = decodeAux.ptr1 = new int[this.entries * 2];
		int[] intArray3 = make_words(this.c.lengthlist, this.c.entries);
		if (intArray3 == null) {
			return null;
		} else {
			decodeAux.aux = this.entries * 2;
			int int2;
			int int3;
			int int4;
			int int5;
			for (int2 = 0; int2 < this.entries; ++int2) {
				if (this.c.lengthlist[int2] > 0) {
					int3 = 0;
					for (int4 = 0; int4 < this.c.lengthlist[int2] - 1; ++int4) {
						int5 = intArray3[int2] >>> int4 & 1;
						if (int5 == 0) {
							if (intArray[int3] == 0) {
								++int1;
								intArray[int3] = int1;
							}

							int3 = intArray[int3];
						} else {
							if (intArray2[int3] == 0) {
								++int1;
								intArray2[int3] = int1;
							}

							int3 = intArray2[int3];
						}
					}

					if ((intArray3[int2] >>> int4 & 1) == 0) {
						intArray[int3] = -int2;
					} else {
						intArray2[int3] = -int2;
					}
				}
			}

			decodeAux.tabn = Util.ilog(this.entries) - 4;
			if (decodeAux.tabn < 5) {
				decodeAux.tabn = 5;
			}

			int2 = 1 << decodeAux.tabn;
			decodeAux.tab = new int[int2];
			decodeAux.tabl = new int[int2];
			for (int3 = 0; int3 < int2; ++int3) {
				int4 = 0;
				boolean boolean1 = false;
				for (int5 = 0; int5 < decodeAux.tabn && (int4 > 0 || int5 == 0); ++int5) {
					if ((int3 & 1 << int5) != 0) {
						int4 = intArray2[int4];
					} else {
						int4 = intArray[int4];
					}
				}

				decodeAux.tab[int3] = int4;
				decodeAux.tabl[int3] = int5;
			}

			return decodeAux;
		}
	}

	class DecodeAux {
		int aux;
		int[] ptr0;
		int[] ptr1;
		int[] tab;
		int[] tabl;
		int tabn;
	}
}
